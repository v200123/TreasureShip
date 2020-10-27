package com.jzz.treasureship.model.bean

data class CartList(
    val dutyPrice: Int,
    val isSelected: Int,
    val shops: MutableList<Shop>,
    val vatPrice: Int
)