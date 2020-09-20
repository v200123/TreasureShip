package com.jzz.treasureship.view

import android.content.Context
import com.blankj.utilcode.util.GsonUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.layout_city_picker.view.*

class CustomAddPickerBottomPopup(context: Context, datas: CityPlaces) : BottomPopupView(context) {

    private val mDatas: CityPlaces = datas

    override fun getImplLayoutId() = R.layout.layout_city_picker

    override fun initPopupContent() {
        super.initPopupContent()

        val cities1 = ArrayList<CityPlace>()
        val cities2 = ArrayList<CityPlace>()
        val cities3 = ArrayList<CityPlace>()

        cities1.addAll(mDatas.list)

        address.run {
            setCities(cities1)
            setOnItemClickListener { addressSelector, city, tabPosition ->
                when (tabPosition) {
                    0 -> {
                        cities2.clear()
                        cities2.addAll(city.cityChildren)
                        setCities(cities2)
                        var address by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                        address = GsonUtils.toJson(city)
                    }
                    1 -> {
                        cities3.clear()
                        for (ele in city.cityChildren) {
                            cities3.add(ele)
                        }
                        setCities(cities3)
                        var address by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                        address = GsonUtils.toJson(city)
                    }
                    2 -> {
                        var address by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                        address = GsonUtils.toJson(city)
                        dismiss()
                    }
                }
            }
            setOnTabSelectedListener(object :
                com.jzz.treasureship.view.adressselectorlib.AddressSelector.OnTabSelectedListener {

                override fun onTabReselected(
                    addressSelector: com.jzz.treasureship.view.adressselectorlib.AddressSelector?,
                    tab: com.jzz.treasureship.view.adressselectorlib.AddressSelector.Tab?
                ) {

                }

                override fun onTabSelected(
                    addressSelector: com.jzz.treasureship.view.adressselectorlib.AddressSelector?,
                    tab: com.jzz.treasureship.view.adressselectorlib.AddressSelector.Tab?
                ) {
                    when (tab?.index) {
                        0 -> address.setCities(cities1)
                        1 -> address.setCities(cities2)
                        2 -> address.setCities(cities3)
                    }
                }
            })
        }

    }
}