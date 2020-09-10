package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/10
 *@describe:
 *@Auth: 29579
 **/
class UserAuthTypeBean(
    @SerializedName("code")
    var mCode: Int = 0, // 0
    @SerializedName("message")
    var mMessage: String = "", // string
    @SerializedName("result")
    var mResult: Result = Result(),
    @SerializedName("success")
    var mSuccess: Boolean = false, // true
    @SerializedName("timestamp")
    var mTimestamp: Int = 0 // 0
)

class Result(
    @SerializedName("list")
    var mList: List<Type> = listOf()
)

class Type(
    @SerializedName("createTime")
    var mCreateTime: String = "", // string
    @SerializedName("iconPath")
    var mIconPath: String = "", // string
    @SerializedName("iconSelectedPath")
    var mIconSelectedPath: String = "", // string
    @SerializedName("id")
    var mId: Int = 0, // 0
    @SerializedName("isEnable")
    var mIsEnable: Int = 0, // 0
    @SerializedName("occupationName")
    var mOccupationName: String = "", // string
    @SerializedName("occupationSort")
    var mOccupationSort: Int = 0, // 0
    @SerializedName("remark")
    var mRemark: String = "" // string
)
