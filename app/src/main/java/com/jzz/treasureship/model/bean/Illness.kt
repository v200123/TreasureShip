package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName

data class Illness (
    val id: String,
    @SerializedName(value = "name",alternate = ["illnessName"])
    val name: String
)