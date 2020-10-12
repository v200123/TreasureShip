package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class CouponBannerBean(
    @SerializedName("list")
    var mList: List<banner> = listOf()
)

class banner(
    @SerializedName("bannerImg")
    var mBannerImg: String = "", // string
    @SerializedName("bannerModule")
    var mBannerModule: Int = 0, // 0
    @SerializedName("bannerName")
    var mBannerName: String = "", // string
    @SerializedName("bannerNumber")
    var mBannerNumber: String = "", // string
    @SerializedName("bannerType")
    var mBannerType: Int = 0, // 0
    @SerializedName("bannerTypeId")
    var mBannerTypeId: Int = 0, // 0
    @SerializedName("bannerUrl")
    var mBannerUrl: String = "", // string
    @SerializedName("id")
    var mId: Int = 0 // 0
)