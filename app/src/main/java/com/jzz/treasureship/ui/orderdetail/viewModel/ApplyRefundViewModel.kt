package com.jzz.treasureship.ui.orderdetail.viewModel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.RefundMsg
import com.jzz.treasureship.model.bean.UploadImgBean
import com.jzz.treasureship.ui.orderdetail.requestbody.RefundMsgBody
import com.jzz.treasureship.utils.out
import com.lc.mybaselibrary.ErrorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail.viewModel
 *@Auth： 29579
 **/
class ApplyRefundViewModel : BaseViewModel() {
    var mUrlList = mutableListOf<String>()
    val refundMsg = MutableLiveData<RefundMsg>()
    val ImageResultData = MutableLiveData<UploadImgBean>()
    fun getRefundInformation(type: Int) {

        launchTask {

            HttpHelp.getRetrofit().getRefundMsg(BaseRequestBody(RefundMsgBody(type))).resultCheck {
                refundMsg.postValue(it)
            }
        }
    }

    /**
     * @param refundResult:退货原因的ID
     * @param refundExplain:退货原因的补充文字
     */
    fun submitMsg(orderId: Int, refundType: Int, refundResult: Int, skuId: String, refundExplain: String?,photoMap:Map<View,String>) {
        launchTask {
            val async = async { photoMap.values.forEach { uploadImg(File(it)) } }

            var mPhotoString = ""
            val async1 = async {
                mUrlList.forEachIndexed { i: Int, s: String ->
                    if (i == mUrlList.size - 1) {
                        mPhotoString += s
                    } else {
                        mPhotoString += "$s,"
                    }
                }
                mUrlList = arrayListOf()
                return@async mPhotoString
            }
            async.await()
            async1.await().out()
//            HttpHelp.getRetrofit().submitRefundMsg(
//                BaseRequestBody(
//                    SubmitRefundBody(
//                        mRefundReasonId = refundResult,
//                        mRefundType = refundType,
//                        mOrderId = orderId,
//                        mOrderGoodsIds = skuId,
//                        mRefundExplain = refundExplain,
//                        mRefundImages = mPhotoString
//                    )
//                )
//            ).resultCheck {
//
//            }
        }

    }

   suspend fun uploadImg(image: File) {
        launchTask {
            withContext(Dispatchers.IO) {
                val requestFile = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", image.name, requestFile)
                val uploadFile = HttpHelp.getRetrofit().uploadFile(filePart)
                uploadFile.resultCheck({
                    mStateLiveData.postValue(ErrorState("上传失败，请重试"))
                }, {
                    mUrlList.add(it!!.url)
                })
            }
        }
    }

}