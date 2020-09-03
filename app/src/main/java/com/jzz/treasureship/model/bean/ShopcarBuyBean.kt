package com.jzz.treasureship.model.bean

data class ShopcarBuyBean(
    val dutyPrice: Double?,
    val receiveAddress: Address?,
    val shippingFee: Double?,
    val shops: List<Shop?>?,
    val totalMoney: Double?,
    val totalNum: Double?,
    val orderDiscount:Double?,
    val orderDiscountMoney:Double?
)