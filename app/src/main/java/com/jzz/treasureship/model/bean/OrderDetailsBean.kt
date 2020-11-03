package com.jzz.treasureship.model.bean

import android.content.Context
import android.widget.ImageView
import com.google.gson.annotations.SerializedName
import com.jzz.treasureship.R
import com.lc.mybaselibrary.ext.getResDrawable


/**
 *@PackageName: com.jzz.treasureship.model.bean
 *@Auth： 29579
 **/
class OrderDetailsBean(
    @SerializedName("consignTime")
    var mConsignTime: String = "", // string
    @SerializedName("createTime")
    var mCreateTime: String = "", // string
    @SerializedName("finishTime")
    var mFinishTime: String? = "", // string
    @SerializedName("goodsMoney")
    var mGoodsMoney: String = "", // 0
    @SerializedName("goodsNum")
    var mGoodsNum: Int? = 0, // 0
    @SerializedName("goodsSkuList")
    var mGoodsSkuList: List<GoodsSku>? = listOf(),
    @SerializedName("id")
    var mId: Int? = 0, // 0
    @SerializedName("orderDiscount")
    var mOrderDiscount: Float? = 0f, // 0
    @SerializedName("orderDiscountMoney")
    var mOrderDiscountMoney: Float? = 0f, // 0
    @SerializedName("orderMoney")
    var mOrderMoney: Float? = 0f, // 0
    @SerializedName("orderNo")
    var mOrderNo: String = "", // string
    @SerializedName("orderStatus")
    var mOrderStatus: Int = 0, // 0
    @SerializedName("payMoney")
    var mPayMoney: String = "", // 0
    @SerializedName("payStatus")
    var mPayStatus: Int? = 0, // 0
    @SerializedName("payTime")
    var mPayTime: String? = "", // string
    @SerializedName("paymentType")
    var mPaymentType: Int = 0, // 0
    @SerializedName("receiverAddress")
    var mReceiverAddress: String? = "", // string
    @SerializedName("receiverDistrict")
    var mReceiverDistrict: Int? = 0, // 0
    @SerializedName("receiverMobile")
    var mReceiverMobile: String? = "", // string
    @SerializedName("receiverName")
    var mReceiverName: String? = "", // string
    @SerializedName("shippingMoney")
    var mShippingMoney: String = "", // 0
    @SerializedName("shopName")
    var mShopName: String? = "", // string
    @SerializedName("shopServerList")
    var mShopServerList: List<ShopServer?>? = listOf(),
    @SerializedName("signTime")
    var mSignTime: String = "", // string
    @SerializedName("taxMoney")
    var mTaxMoney: Float? = 0f, // 0
    @SerializedName("useCouponMoney")
    var mUseCouponMoney: Int? = 0, // 0
    @SerializedName("userMoney")
    var mUserMoney: Float? = 0f // 0
) {

    fun getPayType() = if(mPaymentType == 1) "微信支付" else "支付宝支付"

    fun getStatusMsg(context: Context? = null, imageview: ImageView? = null): String =
        when (mOrderStatus) {
            0 -> {
                imageview?.setImageDrawable(context?.getResDrawable(R.drawable.icon_order_before_pay))
                "等待买家付款"
            }
            1 -> {
                "等待卖家发货"
            }
            2 -> {
                "商家已发货"
            }
            3 -> {
                "已收货"
            }
            9 -> {
                imageview?.setImageDrawable(context?.getResDrawable(R.drawable.icon_order_sent))

                "交易成功"
            }
            10 -> {
                "已退款"
            }
            11 -> {
                imageview?.setImageDrawable(context?.getResDrawable(R.drawable.icon_order_close))

                "订单已关闭"
            }
            12 -> {
                "退款不通过"
            }
            8 ->{
                "已完成"
            }
            else -> {
                "未知状态"
            }
        }


    class GoodsSku(
        @SerializedName("attrValue")
        var mAttrValue: String? = "", // string
        @SerializedName("goodsId")
        var mGoodsId: Int? = 0, // 0
        @SerializedName("goodsMoney")
        var mGoodsMoney: Float? = 0f, // 0
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
        @SerializedName("payMoney")
        var mPayMoney: Float? = 0f, // 0
        @SerializedName("price")
        var mPrice: Float? = 0f, // 0
        @SerializedName("returnPoint")
        var mReturnPoint: Float? = 0f, // 0
        @SerializedName("shopId")
        var mShopId: Int? = 0, // 0
        @SerializedName("skuId")
        var mSkuId: Int = 0, // 0
        @SerializedName("skuPicture")
        var mSkuPicture: String? = "" // string
    )

    class ShopServer(
        @SerializedName("shopName")
        var mShopName: String? = "", // string
        @SerializedName("shopPhone")
        var mShopPhone: String? = "" // string
    )
}