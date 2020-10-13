package com.jzz.treasureship.ui.auth.authRequestBody

import com.google.gson.annotations.SerializedName
import com.jzz.treasureship.model.bean.body



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

    fun checkAllValue():String{
        if(mAreaCity == 0)
            return "请选择城市"
        if(mAreaDistrict==0)
        {
            return "请选择地区"
        }
        if(mAreaProvince == 0)
            return "请选择省份"
        if(mOrganizationName.isBlank())
            return "请填写单位名称"
        if(mIdcardImg.isBlank())
            return "请上传身份证图片"
        if(mRealName.isBlank())
            return "请填写姓名"
        if(mQualificationImages.isBlank())
        {
            return "请上传资质图片"
        }
        if(mIdcardImg.isBlank())
            return "请上传身份证图片"
        return ""
    }
}
