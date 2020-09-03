package com.jzz.treasureship.model.bean

data class AddressPageList(
    val `data`: List<Address>,
    val totalElements: Int,
    val totalPages: Int
)