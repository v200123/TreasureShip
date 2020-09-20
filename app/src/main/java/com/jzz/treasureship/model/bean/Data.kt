package com.jzz.treasureship.model.bean

data class Data(
    val commissionAccount: Double?,
    val commissionStatus: String?,
    val createTime: String?,
    val doctorAdvice: String?,
    val goodsNum: Int?,
    val goodsSkuList: List<GoodsSkuX?>?,
    val orderId: Int?,
    val orderMoney: Double?,
    val orderNo: String?,
    var orderStatus: Int?,
    val orderType: Int?,
    val payStatus: Int?,
    val payTime: Int?,
    val shopName: String?,
    val signTime: String?,
    val userMoney: Double?,
   val orderRepurchase:Int
)