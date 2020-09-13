package com.jzz.treasureship.ui.auth

import android.R.anim
import android.content.Context
import android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE
import android.widget.Toast
import com.blankj.utilcode.util.GsonUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.ui.auth.viewmodel.AuthBaseInfoViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomAddPickerBottomPopup
import com.lc.mybaselibrary.out
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xuexiang.citypicker.CityPicker
import com.xuexiang.citypicker.adapter.OnLocationListener
import com.xuexiang.citypicker.adapter.OnPickListener
import com.xuexiang.citypicker.model.City
import com.xuexiang.citypicker.model.LocateState
import com.xuexiang.citypicker.model.LocatedCity
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.fragment_base_information.*


/**
 *@date: 2020/9/11
 *@describe: 医护认证的基本页面，包含了个人信息，地址的选择
 *@Auth: 29579
 **/
class AuthBaseInformationFragment : BaseVMFragment<AuthBaseInfoViewModel>(false) {
    private var allPlace by PreferenceUtils(PreferenceUtils.ALL_PLACES, "")

    var topAddressObj: CityPlace? = null
    var midAddressObj: CityPlace? = null
    var lastAddressObj: CityPlace? = null

    override fun getLayoutResId(): Int = R.layout.fragment_base_information

    override fun initVM(): AuthBaseInfoViewModel = AuthBaseInfoViewModel()

    override fun initView() {
        xll_raduis.apply {
            radius = 24
            showDividers = SHOW_DIVIDER_MIDDLE
        }

        fl_baseinfo_address.setOnClickListener {
            if (allPlace.isBlank()) {
                mViewModel.getCityPlaces()
            } else {
                val places = GsonUtils.fromJson(allPlace, CityPlaces::class.java)

                XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {

                    override fun onDismiss() {
                        val lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                        if (lastAddress.isNotBlank()) {
                            lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
                        }

                        val topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                        if (topAddress.isNotBlank()) {
                            topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
                        }

                        val midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                        if (midAddress.isNotBlank()) {
                            midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
                        }

                        tv_adress.text =
                            "${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}"
                    }

                }).asCustom(CustomAddPickerBottomPopup(mContext, places)).show()
            }
        }


    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.CityPlacesLiveData.observe(this,{
            allPlace = GsonUtils.toJson(it.list)
            XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {

                override fun onDismiss() {
                    val lastAddress by PreferenceUtils(PreferenceUtils.LAST_ADDRESS, "")
                    if (lastAddress.isNotBlank()) {
                        lastAddressObj = GsonUtils.fromJson(lastAddress, CityPlace::class.java)
                    }

                    val topAddress by PreferenceUtils(PreferenceUtils.TOP_ADDRESS, "")
                    if (topAddress.isNotBlank()) {
                        topAddressObj = GsonUtils.fromJson(topAddress, CityPlace::class.java)
                    }

                    val midAddress by PreferenceUtils(PreferenceUtils.MID_ADDRESS, "")
                    if (midAddress.isNotBlank()) {
                        midAddressObj = GsonUtils.fromJson(midAddress, CityPlace::class.java)
                    }

                    tv_adress.text =
                        "${topAddressObj!!.areaName} ${midAddressObj!!.areaName} ${lastAddressObj!!.areaName}"
                }

            }).asCustom(CustomAddPickerBottomPopup(mContext, it)).show()
        })

    }

    override fun initListener() {
    }

}
