package com.jzz.treasureship.ui.auth

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jzz.treasureship.BuildConfig
import com.jzz.treasureship.ImageLoader
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.image
import com.jzz.treasureship.ui.auth.viewmodel.AuthUpLoadViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import com.jzz.treasureship.utils.FileUtil
import com.jzz.treasureship.utils.RealPathFromUriUtils
import com.jzz.treasureship.utils.out
import com.jzz.treasureship.view.DialogSimpleList
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_auth_upload.*
import kotlinx.android.synthetic.main.item_card_unuse.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthUpLoadFragment : BaseVMFragment<AuthUpLoadViewModel>(false),EasyPermissions.PermissionCallbacks {
//    private lateinit var mSelectPath: ArrayList<String>
    private val activityModel by activityViewModels<CommonDataViewModel>()
    private lateinit var mImageList: List<image>
    private var firstImage: String = ""
    private var secondImage: String = ""
    private var thirdImage: String = ""
    private lateinit var mOccupation: image

    //    当前上传图片的位置为0，1，2
    private var nowPosition = 0

    //    private val IMAGE_CODE = 100
    //调用照相机返回图片文件
    private var tempFile: File? = null

    override fun getLayoutResId(): Int = R.layout.fragment_auth_upload

    override fun initVM(): AuthUpLoadViewModel = AuthUpLoadViewModel()

    override fun initView() {
        tv_first_red.setText(SpannableString("*上传时请确保图片内容清晰、完整、无遮挡").apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.red_cc0814)),
                0,
                1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            setSpan(AbsoluteSizeSpan(12, true), 0, 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        })

        mViewModel.occupationData.observe(this, { occupa ->
            val mList = occupa.mList
            mImageList = occupa.mList
            mOccupation = mList[0]
//            用于取第一个的资质的
            val firstOccupation = mList[0]
            if (!firstOccupation.mRemark2.isNullOrBlank()) {
                tv_auth_upload_remark.setText(SpannableString("*" + firstOccupation.mRemark2).apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.red_cc0814)),
                        0,
                        0,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    setSpan(AbsoluteSizeSpan(12, true), 0, 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                })
            }
            else{
                tv_auth_upload_remark.visibility = View.GONE
            }
            activityModel.mConfirmBody.apply {
                mOccupationConfigId = firstOccupation.mId
                mOccupationId = firstOccupation.mOccupationId
            }
            iv_authupload_image01.setOnClickListener {
                if (firstImage.isBlank())
                    imageChoice()
                else {
                    otherImageChoice(it as ImageView)
                }
                nowPosition = 0
            }

            iv_authupload_image02.setOnClickListener {
                if (secondImage.isBlank())
                    imageChoice()
                else {
                    otherImageChoice(it as ImageView)
                }
                nowPosition = 1
            }
            iv_authupload_idCard_image01.setOnClickListener {
                if (thirdImage.isBlank())
                    imageChoice()
                else {
                    otherImageChoice(it as ImageView)
                }
                nowPosition = 2
            }

            tv_example_01.setOnClickListener {
                XPopup.Builder(context)
                    .asImageViewer(imageView, firstOccupation.mExampleImages, ImageLoader())
                    .show()
            }

            tv_example_02.setOnClickListener {
                XPopup.Builder(context)
                    .asImageViewer(
                        imageView,
                        ContextCompat.getDrawable(mContext, R.drawable.imge_example_card),
                        ImageLoader()
                    )
                    .show()
            }

            Glide.with(this).asDrawable().load(firstOccupation.mBackImage1)
                .into(iv_authupload_image01)
            tv_authUpLoad_title.text = firstOccupation.mTitle
            textView13.text = firstOccupation.mRemark1
//            if(firstOccupation.mBackImage2 == null) {tv_auth_upload_remark.visibility = View.GONE}
//            else{
//                tv_auth_upload_remark.visibility = View.VISIBLE
//                tv_auth_upload_remark.text = firstOccupation.mBackImage2
//            }

            if (firstOccupation.mBackImage2 != null) {
                Glide.with(this).asDrawable().load(firstOccupation.mBackImage2)
                    .into(iv_authupload_image02)
            }else{
                iv_authupload_image02.visibility = View.GONE
            }


            if (mList.size == 1) {
                tv_authupload_change.visibility = View.GONE
            } else {
                tv_authupload_change.visibility = View.VISIBLE
                tv_authupload_change.setOnClickListener {
                    XPopup.Builder(mContext).asCustom(
                        DialogSimpleList(
                            mContext,
                            mutableListOf(mList[0].mTitle, mList[1].mTitle)
                        ).apply {
                            click = {
                                if (it == 0) {
                                    activityModel.mConfirmBody.mOccupationConfigId =
                                        mList[0].mOccupationId
                                    cleanInformation(mList[0])
                                } else {
                                    activityModel.mConfirmBody.mOccupationConfigId =
                                        mList[1].mOccupationId
                                    cleanInformation(mList[1])
                                }
                                this.dismiss()
                            }
                        }
                    )
                        .show()
                }
            }
        })


    }

    /**
     * 切换资质后的图片重置
     */
    private fun cleanInformation(image: image) {
        tv_authUpLoad_title.text = image.mTitle
        textView13.text = image.mRemark1
        if(image.mRemark2.isNullOrEmpty()) {tv_auth_upload_remark.visibility = View.GONE}
        else{
            tv_auth_upload_remark.visibility = View.VISIBLE
            tv_auth_upload_remark.text = SpannableString("*" + image.mRemark2).apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.red_cc0814)),
                    0,
                    1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(AbsoluteSizeSpan(12, true), 0, 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        mOccupation = image
        activityModel.mConfirmBody.mQualificationImages = ""
        firstImage = ""
        secondImage = ""
        Glide.with(this).asDrawable().load(image.mBackImage1).into(iv_authupload_image01)
        activityModel.mConfirmBody.apply {
            mOccupationConfigId = image.mId
            mOccupationId = image.mOccupationId
        }
        tv_example_01.setOnClickListener {
            XPopup.Builder(context)
                .asImageViewer(imageView, image.mExampleImages, ImageLoader())
                .show()
        }
        if (image.mBackImage2 != null) {
            iv_authupload_image02.visibility = View.VISIBLE
            Glide.with(this).asDrawable().load(image.mBackImage2).into(iv_authupload_image02)
        } else {
            iv_authupload_image02.visibility = View.GONE
        }
    }

    fun imageChoice() {
        XPopup.Builder(mContext).asCustom(
            DialogSimpleList(
                mContext,
                mutableListOf("从手机相册选择", "拍摄")
            ).apply {
                click = {
                    if (it == 0) {
                        getFileAuth()
                    } else {
                        getCameraAuth()
                    }
                    this.dismiss()
                }
            }
        ).show()

    }


    fun otherImageChoice(view: ImageView) {
        XPopup.Builder(mContext).asCustom(
            DialogSimpleList(
                mContext,
                mutableListOf("删除", "查看大图", "从手机相册选择", "拍摄")
            ).apply {
                click = {
                    if (it == 0) {
                        when (nowPosition) {
                            0 -> {
                                firstImage = ""
                                Glide.with(this).asDrawable().load(mOccupation.mBackImage1)
                                    .into(view)
                            }
                            1 -> {
                                secondImage = ""
                                Glide.with(this).asDrawable().load(mOccupation.mBackImage2)
                                    .into(view)
                            }
                            2 -> {
                                thirdImage = ""
                                Glide.with(this).asDrawable()
                                    .load(
                                        ContextCompat.getDrawable(
                                            mContext,
                                            R.drawable.image_id_card
                                        )
                                    ).into(view)
                            }
                        }

                    }
                    if (it == 1) {
                        XPopup.Builder(mContext).asImageViewer(view, view.drawable, ImageLoader())
                            .show()
                    }
                    if (it == 2) {
                        getFileAuth()
                    }
                    if (it == 3)
                        getCameraAuth()
                    this.dismiss()
                }
            }
        ).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthenticationActivity.REQUEST_CAPTURE_CERT) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
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

        if (requestCode == AuthenticationActivity.REQUEST_PICK) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
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

    override fun initData() {
        mViewModel.getOccupation(activityModel.mConfirmBody.mOccupationId)
    }

    override fun startObserve() {
        mViewModel.ImageResultData.observe(this, {
            "上传成功了".out()
            if (nowPosition == 0) {
                firstImage = it.url
                Glide.with(this).asDrawable().load(it.url).into(iv_authupload_image01)
                activityModel.mConfirmBody.mQualificationImages = "$firstImage,$secondImage"

            }
            if (nowPosition == 1) {
                Glide.with(this).asDrawable().load(it.url).into(iv_authupload_image02)
                secondImage = it.url
                activityModel.mConfirmBody.mQualificationImages = "$firstImage,$secondImage"


            }
            if (nowPosition == 2) {
                Glide.with(this).asDrawable().load(it.url).into(iv_authupload_idCard_image01)
                thirdImage = it.url
                activityModel.mConfirmBody.mIdcardImg = it.url
            }
        })
    }

    override fun initListener() {
    }


    private fun getCameraAuth(){
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(mContext, *perms)) {
            EasyPermissions.requestPermissions(
                this,
                "需要权限",
                AuthenticationActivity.REQUEST_PERMISSION_CAMERA,
                *perms
            )
        } else {
            gotoCamera()
        }
    }

    private fun getFileAuth(){
        if (!EasyPermissions.hasPermissions(
                mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            EasyPermissions.requestPermissions(
                this,
                "需要读写本地文件权限",
                AuthenticationActivity.REQUEST_PERMISSION_WRITE_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            gotoPhoto()
        }

    }

    /**
     * 跳转到照相机
     */
    @AfterPermissionGranted(AuthenticationActivity.REQUEST_PERMISSION_CAMERA)
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
//            0 -> {
        startActivityForResult(intent, AuthenticationActivity.REQUEST_CAPTURE_CERT)

    }


    @AfterPermissionGranted(AuthenticationActivity.REQUEST_PERMISSION_READ_STORAGE)
    private fun gotoPhoto() {
        //跳转到调用系统图库
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            Intent.createChooser(intent, "请选择图片"),
            AuthenticationActivity.REQUEST_PICK
        )
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setPositiveButton("确定")
                .setNegativeButton("取消")
                .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置界面以修改应用权限").setTitle("必需权限").build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == AuthenticationActivity.REQUEST_PERMISSION_WRITE_STORAGE)
        {
            gotoPhoto()
        }

        "我权限授予成功了".out()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}
