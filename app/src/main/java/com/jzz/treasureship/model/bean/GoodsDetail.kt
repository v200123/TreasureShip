package com.jzz.treasureship.model.bean

import java.io.Serializable

data class GoodsDetail(
    val companyPhone:String?,
    val goodsAttributeList: List<GoodsAttribute>,
    val goodsDetailHtml: String?,
    val goodsId: Int,
    val goodsName: String,
    val goodsPictureList: List<String>,
    val goodsSku: List<GoodsSku>,
    val instructionsPictureList: List<String>,
    val qualificationPictureList: List<String>,
    val shippingFee: Int,
    val shopId: Int,
    val shopName: String,
    val goodsType:Int,
    val specialDescriptionPictureList: List<String>
) : Serializable