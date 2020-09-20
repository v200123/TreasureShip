package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class CouponBean(
    @SerializedName("data")
    var mData: List<Data01> = listOf(),
    @SerializedName("totalElements")
    var mTotalElements: Int = 0, // 0
    @SerializedName("totalPages")
    var mTotalPages: Int = 0 // 0
)

class Data01(
    @SerializedName("couponClass")
    var mCouponClass: Int = 0, // 0
    @SerializedName("couponEndTime")
    var mCouponEndTime: String = "", // string
    @SerializedName("couponId")
    var mCouponId: Int = 0, // 0
    @SerializedName("couponName")
    var mCouponName: String = "", // string
    @SerializedName("couponNumber")
    var mCouponNumber: String = "", // string
    @SerializedName("couponRemark")
    var mCouponRemark: String = "", // string
    @SerializedName("couponShopId")
    var mCouponShopId: Int = 0, // 0
    @SerializedName("couponShopName")
    var mCouponShopName: String = "", // string
    @SerializedName("couponStartTime")
    var mCouponStartTime: String = "", // string
    @SerializedName("couponStatus")
    var mCouponStatus: Int = 0, // 0
    @SerializedName("couponType")
    var mCouponType: Int = 0, // 0
    @SerializedName("couponUseTime")
    var mCouponUseTime: String = "", // string
    @SerializedName("couponValue")
    var mCouponValue: Float = 0f // 0
)