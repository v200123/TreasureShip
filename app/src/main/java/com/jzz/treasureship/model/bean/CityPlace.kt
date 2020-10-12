package com.jzz.treasureship.model.bean

import com.jzz.treasureship.view.adressselectorlib.CityInterface
import java.util.ArrayList


data class CityPlace(
    val allName: String,
    val areaName: String,
    val children: List<CityPlace>,
    val id: Int,
    val initial: String,
    val isTopCity: Int,
    val parentId: Int,
    val spell: String
) : CityInterface {
    override fun getChildIsTop(): kotlin.Int {
        return isTopCity
    }

    override fun getCityAllName(): kotlin.String {
        return allName
    }

    override fun getCityId(): Int {
        return id
    }

    override fun getCityAreaName(): String {
        return areaName
    }

    override fun getCityParentId(): Int {
        return parentId
    }

    override fun getCityChildren(): ArrayList<CityPlace> {
        return children as ArrayList<CityPlace>
    }

    override fun getCityInital(): kotlin.String {
        return initial
    }

    override fun getChildSpell(): kotlin.String {
        return spell
    }
}



