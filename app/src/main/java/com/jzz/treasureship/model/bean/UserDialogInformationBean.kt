package com.jzz.treasureship.model.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 储存了用户的弹窗时间信息，
 */

class UserDialogInformationBean(
    var ShowAuthDialogDate:String  ="",
    var ShowNoAuthDialogDate:String ="",
    var showInviteDialogDate:String = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ShowAuthDialogDate)
        parcel.writeString(ShowNoAuthDialogDate)
        parcel.writeString(showInviteDialogDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDialogInformationBean> {
        override fun createFromParcel(parcel: Parcel): UserDialogInformationBean {
            return UserDialogInformationBean(parcel)
        }

        override fun newArray(size: Int): Array<UserDialogInformationBean?> {
            return arrayOfNulls(size)
        }
    }
}