package com.jzz.treasureship.model.bean

data class OrderExpress(
    val courier: String?,
    val courierPhone: String?,
    val list: List<X>?,
    val logo: String?,
    val msg: String?,
    val name: String?,
    val no: String?,
    val phone: String?,
    val state: Int?,
    val takeTime: String?,
    val type: String?,
    val updateTime: String?
)