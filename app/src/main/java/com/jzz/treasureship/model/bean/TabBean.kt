package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

data class TabBean(val id: Int, val title: String?, val type: Int, val count: Int, val deleteShow: Int):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeInt(type)
        parcel.writeInt(count)
        parcel.writeInt(deleteShow)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TabBean> {
        override fun createFromParcel(parcel: Parcel): TabBean {
            return TabBean(parcel)
        }

        override fun newArray(size: Int): Array<TabBean?> {
            return arrayOfNulls(size)
        }
    }
}