package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class OccupationBean(
    @SerializedName("list")
    var mList: List<image> = listOf()
)

class image(
    @SerializedName("backImage1")
    var mBackImage1: String = "", // http://oss.jzzchina.com/identity/bg/gp_bg.png
    @SerializedName("backImage2")
    var mBackImage2: String? = "", // null
    @SerializedName("createTime")
    var mCreateTime: Any = Any(), // null
    @SerializedName("exampleImages")
    var mExampleImages: String = "", // http://oss.jzzchina.com/identity/example/gp_e.jpg
    @SerializedName("id")
    var mId: Int = 0, // 3
    @SerializedName("imageCount")
    var mImageCount: Int = 0, // 1
    @SerializedName("isEnable")
    var mIsEnable: Int = 0, // 1
    @SerializedName("occupationId")
    var mOccupationId: Int = 0, // 2
    @SerializedName("remark1")
    var mRemark1: String = "", // 可选择护士执业资格证或工牌
    @SerializedName("remark2")
    var mRemark2: String = "", // 工牌
    @SerializedName("title")
    var mTitle: String = "" // 工牌
)
