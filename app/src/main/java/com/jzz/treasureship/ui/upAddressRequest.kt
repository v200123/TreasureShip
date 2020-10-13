package com.jzz.treasureship.ui

import com.google.gson.annotations.SerializedName
model.bean.body


/**
 *@date: 2020/9/23
 *@describe:
 *@Auth: 29579
 **/
class upAddressRequest(
    @SerializedName("address")
    var mAddress: String = "", // string
    @SerializedName("city")
    var mCity: Int = 0, // 0
    @SerializedName("cityName")
    var mCityName: String = "", // string
    @SerializedName("consignee")
    var mConsignee: String = "", // string
    @SerializedName("district")
    var mDistrict: Int = 0, // 0
    @SerializedName("districtName")
    var mDistrictName: String = "", // string
    @SerializedName("id")
    var mId: Int = 0, // 0
    @SerializedName("isDefault")
    var mIsDefault: Int = 0, // 0
    @SerializedName("mobile")
    var mMobile: String = "", // string
    @SerializedName("phone")
    var mPhone: String = "", // string
    @SerializedName("province")
    var mProvince: Int = 0, // 0
    @SerializedName("provinceName")
    var mProvinceName: String = "" // string
):body() {

}