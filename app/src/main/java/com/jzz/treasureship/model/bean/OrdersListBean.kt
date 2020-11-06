package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@PackageName: com.jzz.treasureship.model.bean
 *@Auth： 29579
 **/
class OrdersListBean(
    @SerializedName("data")
    var mData: List<Data> = listOf(),
    @SerializedName("totalElements")
    var mTotalElements: Int? = 0, // 0
    @SerializedName("totalPages")
    var mTotalPages: Int? = 0 // 0
) {
    class Data(
        @SerializedName("commissionAccount")
        var mCommissionAccount: Float = 0f, // 0
        @SerializedName("commissionStatus")
        var mCommissionStatus: String? = "", // string
        @SerializedName("createTime")
        var mCreateTime: String? = "", // string
        @SerializedName("doctorAdvice")
        var mDoctorAdvice: String? = "", // string
        @SerializedName("goodsMoney")
        var mGoodsMoney: Float = 0f, // 0
        @SerializedName("goodsNum")
        var mGoodsNum: Int? = 0, // 0
        @SerializedName("goodsSkuList")
        var mGoodsSkuList: List<GoodsSku>? = listOf(),
        @SerializedName("orderId")
        var mOrderId: Int = 0, // 0
        @SerializedName("orderMoney")
        var mOrderMoney: Float = 0f, // 0
        @SerializedName("orderNo")
        var mOrderNo: String? = "", // string
        @SerializedName("orderRepurchase")
        var mOrderRepurchase: Int? = 0, // 0
        @SerializedName("orderStatus")
        var mOrderStatus: Int = 0, // 0
        @SerializedName("orderType")
        var mOrderType: Int? = 0, // 0
        @SerializedName("payMoney")
        var mPayMoney: Float = 0f, // 0
        @SerializedName("payStatus")
        var mPayStatus: Int? = 0, // 0
        @SerializedName("payTime")
        var mPayTime: Int? = 0, // 0
        @SerializedName("shopName")
        var mShopName: String? = "", // string
        @SerializedName("signTime")
        var mSignTime: String? = "", // string
        @SerializedName("userMoney")
        var mUserMoney: Float = 0f // 0
    ) {
        class GoodsSku(
            @SerializedName("attrValue")
            var mAttrValue: String? = "", // string
            @SerializedName("goodsId")
            var mGoodsId: Int? = 0, // 0
            @SerializedName("goodsMoney")
            var mGoodsMoney: Float = 0f, // 0
            @SerializedName("goodsName")
            var mGoodsName: String? = "", // string
            @SerializedName("goodsType")
            var mGoodsType: Int? = 0, // 0
            @SerializedName("id")
            var mId: Int? = 0, // 0
            @SerializedName("num")
            var mNum: Int? = 0, // 0
            @SerializedName("orderGoodsId")
            var mOrderGoodsId: Int? = 0, // 0
            @SerializedName("orderStatus")
            var mOrderStatus: Int? = 0, // 0
            @SerializedName("price")
            var mPrice: Float = 0f, // 0
            @SerializedName("returnPoint")
            var mReturnPoint: Float = 0f, // 0
            @SerializedName("shopId")
            var mShopId: Int? = 0, // 0
            @SerializedName("skuId")
            var mSkuId: Int? = 0, // 0
            @SerializedName("skuPicture")
            var mSkuPicture: String? = "" // string
        ){
            var shopName:String=""
        }
    }
}