package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


class DataXXX(
    @SerializedName("data")
    var mData: List<Data04> = listOf(),
    @SerializedName("totalElements")
    var mTotalElements: Int = 0, // 0
    @SerializedName("totalPages")
    var mTotalPages: Int = 0 // 0
)

class Data04(
    @SerializedName("balance")
    var mBalance: Float = 0f, // 0
    @SerializedName("createTime")
    var mCreateTime: String = "", // string
    @SerializedName("id")
    var mId: Int = 0, // 0
    @SerializedName("money")
    var mMoney: Float = 0f, // 0
    @SerializedName("moneyType")
    var mMoneyType: Int = 0, // 0
    @SerializedName("remark")
    var mRemark: String = "", // string
    @SerializedName("title")
    var mTitle: String = "", // string
    @SerializedName("type")
    var mType: Int = 0 // 0
)