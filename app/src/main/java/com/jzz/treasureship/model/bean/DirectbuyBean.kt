package com.jzz.treasureship.model.bean

data class DirectbuyBean(
    val dutyPrice: Double?,
    val receiveAddress: Address?,
    val shops: List<Shop?>?,
    val totalMoney: Double,
    val vatPrice: Double
)