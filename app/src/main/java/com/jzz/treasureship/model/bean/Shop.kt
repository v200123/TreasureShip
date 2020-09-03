package com.jzz.treasureship.model.bean

data class Shop(
    var cartGoodsSkuList: List<CartGoodsSku?>?,
    var isSelected: Int,
    var shopId: Int,
    var shopName: String
)