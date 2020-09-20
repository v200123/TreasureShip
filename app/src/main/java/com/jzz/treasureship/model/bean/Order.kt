package com.jzz.treasureship.model.bean

import java.io.Serializable

data class Order(
    val appid: String?,
    val codeUrl: String?,
    val nonceStr: String?,
    val orderId: Int,
    val orderNo: String?,
    val orderType: Int,
    val outTradeNo: String?,
    val packageStr: String?,
    val partnerId: String?,
    val prepayId: String?,
    val sign: String?,
    val timestamp: String?,
    val totalFee: Double,
    val tradeType: String?
):Serializable