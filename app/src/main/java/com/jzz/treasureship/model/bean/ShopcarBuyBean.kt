package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class ShopcarBuyBean(
    @SerializedName("couponId")
    var mCouponId: Int = 0, // 0
    @SerializedName("couponList")
    var mCouponList: List<Coupon> = listOf(),
    @SerializedName("couponValue")
    var mCouponValue:Float = 0f, // 0
    @SerializedName("dutyPrice")
    var mDutyPrice: Float = 0f, // 0
    @SerializedName("goodsMoney")
    var mGoodsMoney: Float = 0f, // 0
    @SerializedName("orderDiscount")
    var mOrderDiscount: Float = 0f, // 0
    @SerializedName("orderDiscountMoney")
    var mOrderDiscountMoney: Float = 0f, // 0
    @SerializedName("orderRepurchase")
    var mOrderRepurchase: Float = 0f, // 0
    @SerializedName("payMoney")
    var mPayMoney: Float = 0f, // 0
    @SerializedName("receiveAddress")
    var mReceiveAddress: ReceiveAddress?,
    @SerializedName("repurchaseChildOrderId")
    var mRepurchaseChildOrderId: Int = 0, // 0
    @SerializedName("repurchaseDiscount")
    var mRepurchaseDiscount:Float = 0f, // 0
    @SerializedName("shippingFee")
    var mShippingFee: Float = 0f, // 0
    @SerializedName("shops")
    var mShops: List<Shop> = listOf(),
    @SerializedName("totalMoney")
    var mTotalMoney: Float = 0f, // 0
    @SerializedName("totalNum")
    var mTotalNum: Int = 0, // 0
    @SerializedName("usedCouponValue")
    var mUsedCouponValue: Float = 0f, // 0
    @SerializedName("wxShareUserId")
    var mWxShareUserId: Int = 0 // 0
)

class Coupon(
    @SerializedName("couponClass")
    var mCouponClass: Int = 0, // 0
    @SerializedName("couponEndTime")
    var mCouponEndTime: String = "", // string
    @SerializedName("couponId")
    var mCouponId: Int? = 0, // 0
    @SerializedName("couponName")
    var mCouponName: String = "", // string
    @SerializedName("couponNumber")
    var mCouponNumber: String = "", // string
    @SerializedName("couponRemark")
    var mCouponRemark: String = "", // string
    @SerializedName("couponShopId")
    var mCouponShopId: Int = 0, // 0
    @SerializedName("couponShopName")
    var mCouponShopName: String = "", // string
    @SerializedName("couponStartTime")
    var mCouponStartTime: String = "", // string
    @SerializedName("couponStatus")
    var mCouponStatus: Int = 0, // 0
    @SerializedName("couponType")
    var mCouponType: Int = 0, // 0
    @SerializedName("couponUseTime")
    var mCouponUseTime: String = "", // string
    @SerializedName("couponValue")
    var mCouponValue: Int = 0 ,// 0
    var isSelector : Boolean= false
)

class ReceiveAddress(
    @SerializedName("address")
    var mAddress: String = "", // string
    @SerializedName("city")
    var mCity: Int = 0, // 0
    @SerializedName("cityName")
    var mCityName: String = "", // string
    @SerializedName("consignee")
    var mConsignee: String = "", // string
    @SerializedName("district")
    var mDistrict: Int = 0, // 0
    @SerializedName("districtName")
    var mDistrictName: String = "", // string
    @SerializedName("id")
    var mId: Int = 0, // 0
    @SerializedName("isDefault")
    var mIsDefault: Int = 0, // 0
    @SerializedName("mobile")
    var mMobile: String = "", // string
    @SerializedName("phone")
    var mPhone: String = "", // string
    @SerializedName("province")
    var mProvince: Int = 0, // 0
    @SerializedName("provinceName")
    var mProvinceName: String = "", // string
    @SerializedName("userId")
    var mUserId: Int = 0 // 0
)

class Shop(
    @SerializedName("cartGoodsSkuList")
    var mCartGoodsSkuList: List<CartGoodsSku> = listOf(),
    @SerializedName("shopId")
    var mShopId: Int = 0, // 0
    @SerializedName("shopName")
    var mShopName: String = "",// string

    var isSelected: Int = 0
)

class CartGoodsSku(
    @SerializedName("cartId")
    var mCartId: Int = 0, // 0
    @SerializedName("count")
    var mCount: Int = 0, // 0
    @SerializedName("freightPrice")
    var mFreightPrice: Float = 0f, // 0
    @SerializedName("goodsId")
    var mGoodsId: Int = 0, // 0
    @SerializedName("goodsType")
    var mGoodsType: Int = 0, // 0
    @SerializedName("imageUrl")
    var mImageUrl: String? = "", // string
    @SerializedName("name")
    var mName: String? = "", // string
    @SerializedName("price")
    var mPrice: Float = 0f, // 0
    @SerializedName("skuId")
    var mSkuId: Int = 0, // 0
    @SerializedName("specValue")
    var mSpecValue: String = "", // string

    var isSelected: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mCartId)
        parcel.writeInt(mCount)
        parcel.writeFloat(mFreightPrice)
        parcel.writeInt(mGoodsId)
        parcel.writeInt(mGoodsType)
        parcel.writeString(mImageUrl)
        parcel.writeString(mName)
        parcel.writeFloat(mPrice)
        parcel.writeInt(mSkuId)
        parcel.writeString(mSpecValue)
        parcel.writeInt(isSelected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartGoodsSku> {
        override fun createFromParcel(parcel: Parcel): CartGoodsSku {
            return CartGoodsSku(parcel)
        }

        override fun newArray(size: Int): Array<CartGoodsSku?> {
            return arrayOfNulls(size)
        }
    }
}