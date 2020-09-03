package com.jzz.treasureship.model.bean

import android.text.AlteredCharSequence
import com.google.gson.annotations.SerializedName

data class Brand(
    val id: String,
    @SerializedName(value = "name",alternate = ["brandName"])
    val brandName: String
)