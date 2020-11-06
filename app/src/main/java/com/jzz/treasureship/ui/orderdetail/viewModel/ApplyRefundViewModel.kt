    package com.jzz.treasureship.ui.orderdetail.viewModel

    import androidx.lifecycle.MutableLiveData
    import com.jzz.treasureship.base.BaseViewModel
    import com.jzz.treasureship.model.api.HttpHelp
    import com.jzz.treasureship.model.bean.BaseRequestBody
    import com.jzz.treasureship.model.bean.RefundMsg
    import com.jzz.treasureship.model.bean.RefundResultBean
    import com.jzz.treasureship.model.bean.UploadImgBean
    import com.jzz.treasureship.ui.orderdetail.requestbody.RefundMsgBody
    import com.jzz.treasureship.ui.orderdetail.requestbody.SubmitRefundBody
    import kotlinx.coroutines.Dispatchers
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

        //    退货原因
        val refundMsg = MutableLiveData<RefundMsg>()
        val refundResultData = MutableLiveData<RefundResultBean>()
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
        fun submitMsg(
            orderId: Int,
            refundType: Int,
            refundResult: Int,
            skuId: String,
            refundExplain: String?,
            photoUrl: String
        ) {
            launchTask {
                withContext(Dispatchers.IO)
                {
                    HttpHelp.getRetrofit().submitRefundMsg(
                        BaseRequestBody(
                            SubmitRefundBody(
                                mRefundReasonId = refundResult,
                                mRefundType = refundType,
                                mOrderId = orderId,
                                mOrderGoodsIds = skuId,
                                mRefundExplain = refundExplain,
                                mRefundImages = photoUrl
                            )
                        )
                    ).resultCheck {
                        refundResultData.value = it
                    }
                }


            }

        }

        fun uploadImg(image: File) {
            launchTask {
                withContext(Dispatchers.IO) {
                    val requestFile = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("file", image.name, requestFile)
                    val uploadFile = HttpHelp.getRetrofit().uploadFile(filePart)
                    uploadFile.resultCheck{
                        ImageResultData.value = it
                    }
                }
            }
        }

    }