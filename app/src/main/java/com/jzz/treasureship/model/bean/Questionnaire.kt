package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

data class Questionnaire(
    val content: String?,
    val iconPath: String?,
    val id: Int?,
    val title: String?,
    val isVisible:Int?,
    val question:String?,
    val subtitle:String?,
    val todayDefault:String?,
    val type: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeString(iconPath)
        parcel.writeValue(id)
        parcel.writeString(title)
        parcel.writeValue(isVisible)
        parcel.writeString(question)
        parcel.writeString(subtitle)
        parcel.writeString(todayDefault)
        parcel.writeValue(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Questionnaire> {
        override fun createFromParcel(parcel: Parcel): Questionnaire {
            return Questionnaire(parcel)
        }

        override fun newArray(size: Int): Array<Questionnaire?> {
            return arrayOfNulls(size)
        }
    }
}