package com.jzz.treasureship.model.bean

data class CartList(
    val dutyPrice: Int,
    val isSelected: Int,
    val shops: List<Shop>,
    val vatPrice: Int
)