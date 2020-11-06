package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@PackageName: com.jzz.treasureship.model.bean
 *@Auth： 29579
 *@Description: 申请售后的结果  **/
class RefundResultBean(
    @SerializedName("id")
    var mId: Int = 0, // 0
    @SerializedName("orderId")
    var mOrderId: Int = 0, // 0
    @SerializedName("shopId")
    var mShopId: Int = 0 // 0
)