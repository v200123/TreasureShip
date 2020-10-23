package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName

data class User(
    val accessToken: String?,
    val address: String?,
    val auditRemark: String?,
    val auditStatus: Int?,
    val avatar: String?,
    val departmentId: String?,
    val description: String?,
    val email: String?,
    val hospitalName: String?,
    val id: Int?,
    val mobile: String?,
    val nickName: String,
    val sex: String?,
    val street: String?,
    val tags: String?,
    val username: String?,
    val wxOpenid: String?,
    val wxUnionid: String?,
    val firstPassTip: Int,
    //微信头像
    val wxAvatar: String,
    //微信名称
    val wxNickName: String,
//    判断是否开启营销流程
    @SerializedName("appCampaignType")
    val mAppCampaignType:Int = 1
)