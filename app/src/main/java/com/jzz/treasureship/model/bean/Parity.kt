package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

data class Parity(
    val imageUrl: String?,
    val name: String?,
    val price: Double?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeDouble(price!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Parity> {
        override fun createFromParcel(parcel: Parcel): Parity {
            return Parity(parcel)
        }

        override fun newArray(size: Int): Array<Parity?> {
            return arrayOfNulls(size)
        }
    }
}