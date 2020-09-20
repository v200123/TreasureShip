package com.jzz.treasureship.model.bean

data class OrdersGoPayBean(
    val appid: String?,
    val codeUrl: String?,
    val nonceStr: String?,
    val orderId: Int?,
    val orderNo: String?,
    val orderType: Int?,
    val outTradeNo: String?,
    val packageStr: String?,
    val partnerId: String?,
    val prepayId: String?,
    val sign: String?,
    val signType: String?,
    val timestamp: String?,
    val totalFee: Double?,
    val tradeType: String?
)