package com.jzz.treasureship.ui.auth.authRequestBody

import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.body
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/14
 *@describe:
 *@Auth: 29579
 **/
class ConfirmBody
    : body(){
    @SerializedName("areaCity")
    var mAreaCity = 0// 0
    @SerializedName("areaDistrict")
    var mAreaDistrict = 0 // 0
    @SerializedName("areaProvince")
    var mAreaProvince = 0 // 0
    @SerializedName("departmentId")
    var mDepartmentId: Int = 0 // 0
    @SerializedName("idcardImg")
    var mIdcardImg: String = "" // string
    @SerializedName("occupationConfigId")
    var mOccupationConfigId: Int = 0 // 0
    @SerializedName("occupationId")
    var mOccupationId: Int = 0// 0
    @SerializedName("organizationName")
    var mOrganizationName: String = "" // string
    @SerializedName("qualificationImages")
    var mQualificationImages: String = ""
    @SerializedName("realName")
    var mRealName: String = ""
}
