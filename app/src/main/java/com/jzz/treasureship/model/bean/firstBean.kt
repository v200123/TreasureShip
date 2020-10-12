package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class firstBean(
    @SerializedName("couponName")
    var mCouponName: String = "", // string
    @SerializedName("couponNumber")
    var mCouponNumber: String = "", // string
    @SerializedName("couponStatus")
    var mCouponStatus: Int = 0, // 0
    @SerializedName("couponValue")
    var mCouponValue: Int = 0, // 0
    @SerializedName("id")
    var mId: Int = 0 // 0
)