package com.jzz.treasureship.ui.orderdetail.viewModel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.RefundMsg
import com.jzz.treasureship.ui.orderdetail.requestbody.RefundMsgBody
import com.jzz.treasureship.ui.orderdetail.requestbody.SubmitRefundBody

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail.viewModel
 *@Auth： 29579
 **/
class ApplyRefundViewModel : BaseViewModel() {

    val refundMsg = MutableLiveData<RefundMsg>()
    fun getRefundInformation(type: Int) {

        launchTask {

            HttpHelp.getRetrofit().getRefundMsg(BaseRequestBody(RefundMsgBody(type))).resultCheck {
                refundMsg.postValue(it)
            }
        }
    }

    fun submitMsg(orderId: Int, refundType: Int, refundResult: Int,skuId:String) {

        launchTask {

            HttpHelp.getRetrofit().submitRefundMsg(
                BaseRequestBody(
                    SubmitRefundBody(
                        mRefundReasonId = refundResult,
                        mRefundType = refundType,
                        mOrderId = orderId,
                        mOrderGoodsIds = skuId
                    )
                )
            ).resultCheck {

            }
        }

    }

}