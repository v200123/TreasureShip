package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

data class CartGoodsSku(
    var cartId: Int,
    var count: Int,
    var freightPrice: Double,
    var goodsId: Int,
    var goodsType: Int,
    var imageUrl: String?,
    var isSelected: Int,
    var name: String?,
    var price: Double,
    var skuId: Int,
    var specValue: String?,
    var stock: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(cartId)
        parcel.writeInt(count)
        parcel.writeDouble(freightPrice)
        parcel.writeInt(goodsId)
        parcel.writeInt(goodsType)
        parcel.writeString(imageUrl)
        parcel.writeInt(isSelected)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeInt(skuId)
        parcel.writeString(specValue)
        parcel.writeInt(stock)
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