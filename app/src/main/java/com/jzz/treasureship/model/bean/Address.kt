package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

data class Address(
    val address: String? = "",
    var city: Int? = 0,
    val cityName: String?= "",
    val consignee: String?= "",
    val district: Int? = 0,
    var districtName: String?= "",
    val id: Int? = 0,
    val isDefault: Int? = 0,
    val mobile: String?= "",
    val phone: String?= "",
    val province: Int? = 0,
    var provinceName: String?= "",
    val userId: Int? = 0
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeValue(city)
        parcel.writeString(cityName)
        parcel.writeString(consignee)
        parcel.writeValue(district)
        parcel.writeString(districtName)
        parcel.writeValue(id)
        parcel.writeValue(isDefault)
        parcel.writeString(mobile)
        parcel.writeString(phone)
        parcel.writeValue(province)
        parcel.writeString(provinceName)
        parcel.writeValue(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}