package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

data class HomeTabBeanItem(
    val createTime: String?,
    val description: String?,
    val id: Int?,
    val name: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createTime)
        parcel.writeString(description)
        parcel.writeValue(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeTabBeanItem> {
        override fun createFromParcel(parcel: Parcel): HomeTabBeanItem {
            return HomeTabBeanItem(parcel)
        }

        override fun newArray(size: Int): Array<HomeTabBeanItem?> {
            return arrayOfNulls(size)
        }
    }
}