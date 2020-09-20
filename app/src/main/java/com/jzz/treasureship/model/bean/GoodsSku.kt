package com.jzz.treasureship.model.bean

import java.io.Serializable
import java.util.*

data class GoodsSku(
    val isParity: Int,
    val oldPrice: Double,
    val parityList: ArrayList<Parity>?,
    val price: Double,
    val skuId: Int,
    val skuImg: String,
    val specValue: String?,
    val stock: Int
) : Serializable