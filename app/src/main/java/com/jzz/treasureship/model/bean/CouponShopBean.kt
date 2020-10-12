package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class CouponShopBean(
    @SerializedName("data")
    var mData: List<Data03> = listOf(),
    @SerializedName("totalElements")
    var mTotalElements: Int = 0, // 5
    @SerializedName("totalPages")
    var mTotalPages: Int = 0 // 1
)

class Data03(
    @SerializedName("goodsId")
    var mGoodsId: Int = 0, // 33
    @SerializedName("goodsName")
    var mGoodsName: String = "", // 施巴婴儿润肤乳
    @SerializedName("goodsType")
    var mGoodsType: Int = 0, // 0
    @SerializedName("oldPrice")
    var mOldPrice: Double = 0.0, // 8.9
    @SerializedName("picture")
    var mPicture: String = "", // http://bj.jzzchina.com/upload/default/20200509/1589013623823.jpg
    @SerializedName("price")
    var mPrice: Double = 0.0, // 7.57
    @SerializedName("shopId")
    var mShopId: Int = 0, // 20006
    @SerializedName("shopName")
    var mShopName: String = "" // 北京隆盛泰健康科技股份有限公司
)