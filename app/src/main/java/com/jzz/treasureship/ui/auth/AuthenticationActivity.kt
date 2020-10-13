package com.jzz.treasureship.ui.auth

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.BuildConfig
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.UploadImgBean
import com.jzz.treasureship.model.bean.UserAuthTypeBean
import com.jzz.treasureship.ui.auth.viewmodel.UserViewModel
import com.jzz.treasureship.utils.FileUtil
import com.jzz.treasureship.utils.RealPathFromUriUtils
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_mine_authentication.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import q.rorbin.badgeview.DisplayUtil
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class AuthenticationActivity : BaseVMActivity<UserViewModel>() {
    companion object {
        //请求相机,半身照
        const val REQUEST_CAPTURE_HALF_BODY = 101
        const val NeedFinish = "finish"

        //请求相机,证书
        const val REQUEST_CAPTURE_CERT = 100
        const val REQUEST_PERMISSION_WRITE_STORAGE = 102
        const val REQUEST_PERMISSION_READ_STORAGE = 103
        const val REQUEST_PICK = 104//请求相册
        const val REQUEST_PERMISSION_CAMERA = 105//请求相机
    }

    private val mAdapter by lazy { AuthAdapter(this) }

    //调用照相机返回图片文件
    private var tempFile: File? = null

    //0:资质证书，1：半身照
    private var whichImg = -1

    private var certBean: UploadImgBean? = null
    private var halfBodyBean: UploadImgBean? = null
    private var nowSelectPosition = 0

    override fun getLayoutResId() = R.layout.fragment_mine_authentication

    override fun initVM(): UserViewModel = getViewModel()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getStringExtra(NeedFinish) == "exit") {
            finish()
        }
    }

    override fun initView() {

        if (intent.getStringExtra(NeedFinish) == "exit") {
            finish()
        }
        StateAppBar.setStatusBarLightMode(this, ContextCompat.getColor(mContext, R.color.white))
        btn_auth_next.setOnClickListener {
            mContext.start<AuthInformationActivity> {
                putExtra(
                    AuthInformationActivity.occuId,
                    mViewModel.userType.value?.mList?.get(nowSelectPosition)?.mId
                )
            }

        }

        rv_type.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(mContext, 3)
        }
        mAdapter.setOnItemChildClickListener() { adapter, view, position ->
            if (nowSelectPosition != position) {
                mAdapter.notifyItemChanged(nowSelectPosition, "unSelect")
                mAdapter.notifyItemChanged(position, "select")
                nowSelectPosition = position
            }
        }
        tv_title.text = ""
        rlback.setOnClickListener {
            (mContext as AppCompatActivity).finish()
        }
    }

    override fun initData() {
        mViewModel.getType()
    }

    override fun startObserve() {
        mViewModel.userType.observe(this, {
            mAdapter.setList(it.mList)

        })

        mViewModel.apply {
            val uploading = XPopup.Builder(mContext).asLoading()
            uploadImgState.observe(this@AuthenticationActivity, Observer {
                if (it.showProgress) {
                    uploading.show()
                }
                it.needLogin.let {
                    if (it) {
                        //startActivity(Intent(this@AuthenticationFragment.context, LoginActivity::class.java))
                    }
                }
                it.showError?.let { err ->
                    uploading.dismiss()
                    ToastUtils.showShort(err)
                }
                it.showSuccess?.let {
                    uploading.dismiss()
                    ToastUtils.showShort("上传成功！稍等片刻图片将在界面上显示")
                    when (whichImg) {
                        0 -> {
                            certBean = it

                        }
                        1 -> {
                            halfBodyBean = it
//                            tv_halfBody.visibility = View.INVISIBLE
//                            iv_imgHalfBody.visibility = View.VISIBLE
//                            Glide.with(context!!).asBitmap()
//                                .load(it.url!!).into(object : SimpleTarget<Bitmap>() {
//                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                        iv_imgHalfBody.setImageBitmap(resource)
//                                    }
//                                })
                        }
                        else -> {

                        }
                    }
                }
            })

            qualificationState.observe(this@AuthenticationActivity, Observer {
                if (it.showProgress) {
                    uploading.show()
                }
                it.needLogin.let {
                    if (it) {
                        // startActivity(Intent(this@AuthenticationFragment.context, LoginActivity::class.java))
                    }
                }
                it.showError?.let { err ->
                    uploading.dismiss()
                    ToastUtils.showShort(err)
                }
                it.showSuccess?.let {
                    uploading.dismiss()
                    if (it.isBlank() or ("null" == it)) {
                        ToastUtils.showShort("认证资料提交成功")
                        (mContext as AppCompatActivity).supportFragmentManager.popBackStack()
                    } else {
                        ToastUtils.showShort(it)
                    }
                }
            })
        }
    }


    /**
     * 跳转到照相机
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_CAMERA)
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
                mContext,
                BuildConfig.APPLICATION_ID + ".fileProvider",
                tempFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        }
        when (whichImg) {
            0 -> {
                startActivityForResult(intent, REQUEST_CAPTURE_CERT)
            }
            1 -> {
                startActivityForResult(intent, REQUEST_CAPTURE_HALF_BODY)
            }
        }

    }

    @AfterPermissionGranted(REQUEST_PERMISSION_READ_STORAGE)
    private fun gotoPhoto() {
        //跳转到调用系统图库
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CAPTURE_CERT -> {
                if (resultCode == RESULT_OK) {
                    val img = Uri.fromFile(tempFile)
                    var bmp = BitmapFactory.decodeFile(img.path)
                    if (bmp == null) {
                        bmp = getBitmapFromUri(
                            mContext,
                            this.getImageContentUri(mContext, img.path!!)!!
                        )
                    }
                    compressImage(bmp)?.let { mViewModel.uploadImg(it) }
                }
            }

            REQUEST_CAPTURE_HALF_BODY -> {
                if (resultCode == RESULT_OK) {
                    val img = Uri.fromFile(tempFile)
                    var bmp = BitmapFactory.decodeFile(img.path)
                    if (bmp == null) {
                        bmp = getBitmapFromUri(
                            mContext,
                            this.getImageContentUri(mContext, img.path!!)!!
                        )
                    }
                    compressImage(bmp)?.let { mViewModel.uploadImg(it) }
                }
            }

            REQUEST_PICK -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        val path = RealPathFromUriUtils.getRealPathFromUri(mContext, data.data)
                        var bmp = BitmapFactory.decodeFile(path)
                        if (bmp == null) {
                            bmp = getBitmapFromUri(
                                mContext,
                                this.getImageContentUri(mContext, path!!)!!
                            )
                        }
                        compressImage(bmp)?.let {
                            mViewModel.uploadImg(it)
                        }
                    } else {
                        ToastUtils.showShort("图片损坏，请重新选择")
                    }
                }
            }
        }
    }

    /**
     * 压缩图片（质量压缩）
     * @param bitmap
     */
    fun compressImage(bitmap: Bitmap): File? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 500) { //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset() //重置baos即清空baos
            options -= 10 //每次都减少10
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            ) //这里压缩options%，把压缩后的数据存放到baos中
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

    fun recycleBitmap(vararg bitmaps: Bitmap?) {
        for (bm in bitmaps) {
            if (null != bm && !bm.isRecycled) {
                bm.recycle()
            }
        }
    }


//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            AppSettingsDialog.Builder(this@AuthenticationActivity)
//                .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置界面以修改应用权限").setTitle("必需权限").build().show()
//        }
//    }
//
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//    }


    private fun getImageContentUri(context: Context, path: String): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
            arrayOf(path), null
        )
        if ((cursor != null) and (cursor!!.moveToFirst())) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            return Uri.withAppendedPath(baseUri, "${id}")
        } else {
            if (File(path).exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, path)
                return context.contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            } else {
                return null
            }
        }
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }


    class AuthAdapter(var activity: AppCompatActivity) :
        BaseQuickAdapter<UserAuthTypeBean.Type, BaseViewHolder>(R.layout.item_user_type) {
        private val _72dp = DisplayUtil.dp2px(activity, 72f)
        private val _65dp = DisplayUtil.dp2px(activity, 65f)
        override fun convert(helper: BaseViewHolder, item: UserAuthTypeBean.Type) {
            if (helper.layoutPosition == 0) {
                Glide.with(activity).asDrawable().load(item.mIconSelectedPath)
                    .override(
                        _72dp, _65dp
                    ).into(helper.getView(R.id.iv_item_type))
            } else {
                Glide.with(activity).asDrawable().load(item.mIconPath).override(
                    _72dp, _65dp
                ).into(helper.getView(R.id.iv_item_type))
            }

            helper.setText(R.id.tv_item_type, item.mOccupationName)
        }

        override fun convert(
            holder: BaseViewHolder,
            item: UserAuthTypeBean.Type,
            payloads: List<Any>
        ) {
            super.convert(holder, item, payloads)
            if (payloads.isEmpty())
                convert(holder, item)
            else {
                val mType = payloads[0] as String
                if (mType == "select") {
                    Glide.with(activity).asDrawable().load(item.mIconSelectedPath)
                        .override(
                            _72dp, _65dp
                        ).into(holder.getView(R.id.iv_item_type))
                } else {
                    Glide.with(activity).asDrawable().load(item.mIconPath)
                        .override(
                            _72dp, _65dp
                        ).into(holder.getView(R.id.iv_item_type))
                }
            }
        }

    }

}
