package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName

data class AnswerItem(
    @SerializedName("item",alternate = ["items"])
    val item:String,
    var isClicked:Boolean)