package com.jzz.treasureship.ui.user

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jzz.treasureship.App
import com.jzz.treasureship.BuildConfig
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.activity.ClipImageActivity
import com.jzz.treasureship.ui.address.ChooseAddressFragment
import com.jzz.treasureship.ui.auth.AuthenticationActivity
import com.jzz.treasureship.ui.auth.viewmodel.UserViewModel
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.FileUtil
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.RealPathFromUriUtils
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.dialog_upload_header_img.view.*
import kotlinx.android.synthetic.main.fragment_userinfo.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class UserInfoFragment : BaseVMFragment<UserViewModel>(), EasyPermissions.PermissionCallbacks {

    companion object {
        //请求相机
        private const val REQUEST_CAPTURE = 100

        //请求相册
        private val REQUEST_PICK = 101

        //请求截图
        private val REQUEST_CROP_PHOTO = 102

        //请求访问外部存储
        private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 103

        //请求写入外部存储
        private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104
        private var mBindWeChat = false

        fun newInstance(): UserInfoFragment {
            return UserInfoFragment()
        }
    }

    private val mModifyNickNameFragment by lazy { ModifyNickNameFragment.newInstance() }

    //    private val mAuthenticationFragment by lazy { AuthenticationFragment.newInstance() }
    private val mAddressFragment by lazy { ChooseAddressFragment.newInstance() }

    //调用照相机返回图片文件
    private var tempFile: File? = null

    private var userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")

    override fun getLayoutResId() = R.layout.fragment_userinfo

    override fun initVM(): UserViewModel = getViewModel()

    override fun initView() {

        //activity!!.nav_view.visibility = View.GONE

        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))

        tv_title.text = "个人信息"

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

    }

    override fun initData() {
        mViewModel.getUserInfo()
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
        mViewModel.getUserInfo()
        }
    }



    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).asLoading()

            userState.observe(this@UserInfoFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }
                it.needLogin?.let {
                    if (it) {
                        startActivity(Intent(this@UserInfoFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    userJson = GsonUtils.toJson(it)

                    Glide.with(this@UserInfoFragment).load(it.avatar)
                        .apply(RequestOptions.bitmapTransform(CircleCrop())).into(avatar)
                    tv_name.text = it.nickName
                    tv_accountNum.text = it.mobile

                    if (it.wxOpenid.isNullOrBlank()) {
                        tv_bindWx.text = "未绑定"
                        mBindWeChat = true
                        lin_wxBind.setOnClickListener {
                            if (!App.iwxapi.isWXAppInstalled) {
                                ToastUtils.showShort("未安装微信客户端，无法使用微信授权")
                                return@setOnClickListener
                            }
                            val req: SendAuth.Req = SendAuth.Req()
                            req.scope = "snsapi_userinfo"
                            req.state = "treasureship_wx_bind"
                            App.iwxapi.sendReq(req)
                        }
                    } else {
                        tv_bindWx.text = "已绑定"
                        val drawable = ContextCompat.getDrawable(mContext, R.drawable.ico_goto)
                        drawable!!.setBounds(0,0,drawable.minimumWidth,drawable.minimumHeight)
                        drawable.setTint(mContext.getResColor(R.color.transparent))
                        tv_bindWx.setCompoundDrawables(null,null,drawable,null)
                        lin_wxBind.setOnClickListener { null }
                        mBindWeChat = false
                    }

                    when (it.auditStatus) {
                        -1 -> {
                            //未认证
                            tv_auditStatus.text = "未认证"
                            lin_mine_audit.isEnabled = true
                            tv_auditStatus.setTextColor(Color.parseColor("#E60012"))
                            lin_mine_modifyName.setOnClickListener {
                             null
                            }
                            gotoAuth()
                        }
                        0 -> {
                            //审核中
                            tv_auditStatus.text = "审核中"
                            lin_mine_audit.isEnabled = false
                            lin_mine_audit.setOnClickListener { null }
                            tv_auditStatus.setTextColor(Color.parseColor("#E60012"))
//                            lin_mine_modifyName.setOnClickListener {
//                                activity!!.supportFragmentManager.beginTransaction()
//                                    .addToBackStack(UserInfoFragment.javaClass.name)
//                                    .hide(this@UserInfoFragment)//隐藏当前Fragment
//                                    .add(
//                                        R.id.frame_content,
//                                        mModifyNickNameFragment,
//                                        mModifyNickNameFragment.javaClass.name
//                                    )
//                                    .commit()
//                            }
                        }
                        1 -> {

                            //审核通过
                            tv_auditStatus.text = "已认证"
                            lin_mine_audit.isEnabled = false
                            tv_auditStatus.setTextColor(context!!.resources.getColor(R.color.blue_normal))
                            lin_mine_modifyName.setOnClickListener(null)
                            lin_mine_audit.setOnClickListener { null }
                        }
                        2 -> {
                            //审核不通过
                            tv_auditStatus.text = "审核未通过"
                            lin_mine_audit.isEnabled = true
                            tv_auditStatus.setTextColor(Color.parseColor("#E60012"))
//                            lin_mine_modifyName.setOnClickListener {
//                                activity!!.supportFragmentManager.beginTransaction()
//                                    .addToBackStack(UserInfoFragment.javaClass.name)
//                                    .hide(this@UserInfoFragment)//隐藏当前Fragment
//                                    .add(
//                                        R.id.frame_content,
//                                        mModifyNickNameFragment,
//                                        mModifyNickNameFragment.javaClass.name
//                                    )
//                                    .commit()
//                            }
                            gotoAuth()
                        }
                        else -> {
                            tv_auditStatus.text = "未知认证状态${tv_auditStatus}"
                            lin_mine_audit.isEnabled = true
                            tv_auditStatus.setTextColor(Color.parseColor("#E60012"))
                            gotoAuth()
//                            lin_mine_modifyName.setOnClickListener {
//                                activity!!.supportFragmentManager.beginTransaction()
//                                    .addToBackStack(UserInfoFragment.javaClass.name)
//                                    .hide(this@UserInfoFragment)//隐藏当前Fragment
//                                    .add(
//                                        R.id.frame_content,
//                                        mModifyNickNameFragment,
//                                        mModifyNickNameFragment.javaClass.name
//                                    )
//                                    .commit()
//                            }
                        }
                    }
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }

                lin_mine_header.setOnClickListener {
                    //uploadHeadImage()
                    XPopup.Builder(it.context).asCustom(CustomUploadHeaderBottomPopup(it.context)).show()
                }

                lin_receiveAddress.setOnClickListener {
                    activity!!.supportFragmentManager.beginTransaction()
                        .addToBackStack(UserInfoFragment.javaClass.name)
                        .hide(this@UserInfoFragment)//隐藏当前Fragment
                        .add(R.id.frame_content, mAddressFragment, mAddressFragment.javaClass.name)
                        .commit()
                }
            })

            val uploading = XPopup.Builder(context).asLoading()
            uploadImgState.observe(this@UserInfoFragment, Observer { it ->
                if (it.showProgress) {
                    uploading.show()
                }
                it.needLogin.let {
                    if (it) {
                        // startActivity(Intent(this@UserInfoFragment.context, LoginActivity::class.java))
                    }
                }
                it.showError?.let { err ->
                    uploading.dismiss()
                    ToastUtils.showShort(err)
                }
                it.showSuccess?.let {
                    uploading.dismiss()
                    ToastUtils.showShort("头像修改成功")
                    //Glide.with(context!!).load(it.url!!.replace("bj.jzzchina.com", "119.3.125.1")).into(avatar)
//                    Glide.with(context!!).load(it.url!!).into(avatar)

                    mViewModel.modifiedInfo(it.url, null)
                    mViewModel.getUserInfo()
                }
            })
        }
    }


    override fun initListener() {
    }


    /**
     * 跳转到相册
     */
    @AfterPermissionGranted(REQUEST_CAPTURE)
    private fun gotoPhoto() {
        //跳转到调用系统图库
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile))
                }
            }
            REQUEST_PICK -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        gotoClipActivity(data.data)
                    } else {
                        ToastUtils.showShort("图片损坏，请重新选择")
                    }
                }
            }

            REQUEST_CROP_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        val path = RealPathFromUriUtils.getRealPathFromUri(context, data.data)
                        if (path.toLowerCase().endsWith("png") or path.toLowerCase()
                                .endsWith("jpeg") or path.toLowerCase().endsWith(
                                "jpg"
                            )
                        ) {
                            val bmp = BitmapFactory.decodeFile(path)
                            compressImage(bmp)?.let {
                                if (819200 < it.length()) {
                                    ToastUtils.showShort("图片过大，请重新选择")
                                } else {
                                    mViewModel.uploadImg(it)
                                }
                            }
                        } else {
                            ToastUtils.showShort("不支持的照片格式")
                        }
                    } else {
                        ToastUtils.showShort("图片损坏，请重新选择")
                    }
                } else {

                }
            }
        }
    }

    /**
     * 跳转到照相机
     */
    @AfterPermissionGranted(REQUEST_CAPTURE)
    private fun gotoCamera() {
        //创建拍照存储的图片文件
        tempFile = File(
            FileUtil.checkDirPath(Environment.getExternalStorageDirectory().path.toString() + "/image/"),
            System.currentTimeMillis().toString() + ".jpg"
        )
        //跳转到调用系统相机
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val contentUri: Uri = FileProvider.getUriForFile(
                this.activity!!,
                BuildConfig.APPLICATION_ID.toString() + ".fileProvider",
                tempFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        }
        startActivityForResult(intent, REQUEST_CAPTURE)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this@UserInfoFragment.activity!!, perms)) {
            AppSettingsDialog.Builder(this@UserInfoFragment)
                .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置界面以修改应用权限").setTitle("必需权限").build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoCamera()
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE || requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoPhoto()
            }
        }
        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 打开截图界面
     */
    private fun gotoClipActivity(uri: Uri?) {
        if (uri == null) {
            return
        }
        val intent = Intent()
        intent.setClass(this.context!!, ClipImageActivity::class.java)
        intent.putExtra("type", 1)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CROP_PHOTO)
    }

    inner class CustomUploadHeaderBottomPopup(context: Context) :
        BottomPopupView(context) {

        override fun getImplLayoutId() = R.layout.dialog_upload_header_img

        override fun initPopupContent() {
            super.initPopupContent()

            layout_go2takePhoto.setOnClickListener {
                val perms = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if (!EasyPermissions.hasPermissions(it.context, *perms)) {
                    EasyPermissions.requestPermissions(
                        this@UserInfoFragment,
                        "需要权限",
                        REQUEST_CAPTURE,
                        *perms
                    )
                } else {
                    gotoCamera()
                }
                dismiss()
            }

            layout_go2Pics.setOnClickListener {
                gotoPhoto()
                dismiss()
            }

            layout_dissmiss.setOnClickListener {
                dismiss()
            }
        }

        override fun getMaxHeight(): Int {
            return (XPopupUtils.getWindowHeight(context) * .75f).roundToInt()
        }

    }

    override fun onResume() {
        super.onResume()
        val wxCode by PreferenceUtils(PreferenceUtils.WX_CODE_BIND, "")
        if (wxCode.isNotBlank() && mBindWeChat) {
            mViewModel.bindWeChat(wxCode)
            Thread.sleep(200)
        }
        mViewModel.getUserInfo()
    }

    /**
     * 压缩图片（质量压缩）
     * @param bitmap
     */
    private fun compressImage(bitmap: Bitmap): File? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 500) { //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset() //重置baos即清空baos
            options -= 10 //每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos) //这里压缩options%，把压缩后的数据存放到baos中
            val length: Long = baos.toByteArray().size.toLong()
        }
        val format = SimpleDateFormat("yyyyMMddHHmmss")
        val date = Date(System.currentTimeMillis())
        val filename: String = format.format(date)
        val file = File(Environment.getExternalStorageDirectory(), "$filename.png")
        try {
            val fos = FileOutputStream(file)
            try {
                fos.write(baos.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        recycleBitmap(bitmap)
        return file
    }

    private fun recycleBitmap(vararg bitmaps: Bitmap?) {
        for (bm in bitmaps) {
            if (null != bm && !bm.isRecycled) {
                bm.recycle()
            }
        }
    }

    /**
     * 前往认证界面
     */
    fun gotoAuth(){
        lin_mine_audit.setOnClickListener {
            mContext.start<AuthenticationActivity> {
                setFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }


}
