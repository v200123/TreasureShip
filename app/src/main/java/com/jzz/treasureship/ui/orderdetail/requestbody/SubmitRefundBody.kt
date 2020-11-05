package com.jzz.treasureship.ui.orderdetail.requestbody

import com.google.gson.annotations.SerializedName
import com.jzz.treasureship.model.bean.body


/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail.requestbody
 *@Auth： 29579
 **/
class SubmitRefundBody(
    @SerializedName("id")
    var mId: String? = null, // 0
    @SerializedName("orderGoodsIds")
    var mOrderGoodsIds: String = "", // string
    @SerializedName("orderId")
    var mOrderId: Int = 0, // 0
    @SerializedName("refundExplain")
    var mRefundExplain: String? = "", // string
    @SerializedName("refundImages")
    var mRefundImages: String = "", // string
    @SerializedName("refundReasonId")
    var mRefundReasonId: Int = 0, // 0
    @SerializedName("refundType")
    var mRefundType: Int = 0 // 0
) : body() {

}