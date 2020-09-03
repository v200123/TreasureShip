package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

//通讯录人员信息
data class DataX(
    val avatar: String?,
    val birthYear: Int?,
    val buyNum: Int?,
    val id: Int?,
    val illness: String?,
    val lastedBuyTime: String?,
    val memo: String?,
    val nikeName: String?,
    val notice: Int?,
    val noticeTime: String?,
    val sex: Int?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatar)
        parcel.writeValue(birthYear)
        parcel.writeValue(buyNum)
        parcel.writeValue(id)
        parcel.writeString(illness)
        parcel.writeString(lastedBuyTime)
        parcel.writeString(memo)
        parcel.writeString(nikeName)
        parcel.writeValue(notice)
        parcel.writeString(noticeTime)
        parcel.writeValue(sex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataX> {
        override fun createFromParcel(parcel: Parcel): DataX {
            return DataX(parcel)
        }

        override fun newArray(size: Int): Array<DataX?> {
            return arrayOfNulls(size)
        }
    }
}