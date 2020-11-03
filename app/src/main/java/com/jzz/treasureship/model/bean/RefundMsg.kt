package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName



/**
 *@PackageName: com.jzz.treasureship.model.bean
 *@Auth： 29579
 *@Description: 订单退货的原因  **/
class RefundMsg(
    @SerializedName("list")
    var mList: List<data> = listOf()
) {
    class data (
        @SerializedName("createTime")
        var mCreateTime: String = "", // string
        @SerializedName("id")
        var mId: Int = 0, // 0
        @SerializedName("isEnable")
        var mIsEnable: Int = 0, // 0
        @SerializedName("reason")
        var mReason: String = "", // string
        @SerializedName("reasonSort")
        var mReasonSort: Int = 0, // 0
        @SerializedName("type")
        var mType: Int = 0 // 0
    )
    {
        var isSelect = false

    }
}