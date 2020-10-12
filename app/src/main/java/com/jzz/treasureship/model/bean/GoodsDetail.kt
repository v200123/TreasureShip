package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName


class GoodsDetail(
    @SerializedName("companyPhone")
    val companyPhone: String = "", // string
    @SerializedName("goodsAttributeList")
    val goodsAttributeList: List<GoodsAttribute> = listOf(),
    @SerializedName("goodsDetailHtml")
    val goodsDetailHtml: String = "", // string
    @SerializedName("goodsId")
    val goodsId: Int = 0, // 0
    @SerializedName("goodsName")
    val goodsName: String = "", // string
    @SerializedName("goodsPictureList")
    val goodsPictureList: List<String> = listOf(),
    @SerializedName("goodsSku")
    val goodsSku: List<GoodsSku>? = listOf(),
    @SerializedName("goodsType")
    val goodsType: Int = 0, // 0
    @SerializedName("instructionsPictureList")
    val instructionsPictureList: List<String> = listOf(),
    @SerializedName("qualificationPictureList")
    val qualificationPictureList: List<String> = listOf(),
    @SerializedName("shippingFee")
    val shippingFee: Int = 0, // 0
    @SerializedName("shopId")
    val shopId: Int = 0, // 0
    @SerializedName("shopName")
    val shopName: String = "", // string
    @SerializedName("shopQualification")
    val shopQualification: ShopQualification = ShopQualification(),
    @SerializedName("specialDescriptionPictureList")
    val specialDescriptionPictureList: List<String> = listOf()
) {
    class GoodsAttribute(
        @SerializedName("attrValue")
        val attrValue: String = "", // string
        @SerializedName("attrValueName")
        val attrValueName: String = "" // string
    )

    class GoodsSku(
        @SerializedName("isParity")
        val isParity: Int = 0, // 0
        @SerializedName("oldPrice")
        val oldPrice: Float = 0f, // 0
        @SerializedName("parityList")
        val parityList: List<Parity> = listOf(),
        @SerializedName("price")
        val price: Float = 0f, // 0
        @SerializedName("skuId")
        val skuId: Int = 0, // 0
        @SerializedName("skuImg")
        val skuImg: String = "", // string
        @SerializedName("specValue")
        val specValue: String = "", // string
        @SerializedName("stock")
        val stock: Int = 0 // 0
    ) {
        class Parity(
            @SerializedName("goodsParityList")
            val goodsParityList: List<GoodsParity> = listOf(),
            @SerializedName("imageUrl")
            val imageUrl: String = "", // string
            @SerializedName("name")
            val name: String = "", // string
            @SerializedName("price")
            val price: Float = 0f // 0
        ) {
            class GoodsParity(
                @SerializedName("createTime")
                val createTime: String = "", // string
                @SerializedName("id")
                val id: Int = 0, // 0
                @SerializedName("parityId")
                val parityId: Int = 0, // 0
                @SerializedName("price")
                val price: Float = 0f, // 0
                @SerializedName("skuId")
                val skuId: Int = 0 // 0
            )
        }
    }

    class ShopQualification(
        @SerializedName("companyPhone")
        val companyPhone: String = "", // string
        @SerializedName("imageList")
        val imageList: List<Image> = listOf(),
        @SerializedName("shopName")
        val shopName: String = "" // string
    ) {
        class Image(
            @SerializedName("path")
            val path: String = "", // string
            @SerializedName("title")
            val title: String = "" // string
        )
    }
}