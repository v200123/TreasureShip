package com.jzz.treasureship.ui.auth

import android.R.anim
import android.content.Context
import android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE
import android.widget.Toast
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.auth.viewmodel.AuthBaseInfoViewModel
import com.lc.mybaselibrary.out
import com.xuexiang.citypicker.CityPicker
import com.xuexiang.citypicker.adapter.OnLocationListener
import com.xuexiang.citypicker.adapter.OnPickListener
import com.xuexiang.citypicker.model.City
import com.xuexiang.citypicker.model.LocateState
import com.xuexiang.citypicker.model.LocatedCity
import kotlinx.android.synthetic.main.fragment_base_information.*


/**
 *@date: 2020/9/11
 *@describe: 医护认证的基本页面，包含了个人信息，地址的选择
 *@Auth: 29579
 **/
class AuthBaseInformationFragment : BaseVMFragment<AuthBaseInfoViewModel>(false) {

    override fun getLayoutResId(): Int = R.layout.fragment_base_information

    override fun initVM(): AuthBaseInfoViewModel = AuthBaseInfoViewModel()

    override fun initView() {
        xll_raduis.apply {
            radius = 24
            showDividers = SHOW_DIVIDER_MIDDLE
        }

        fl_baseinfo_address.setOnClickListener {
            CityPicker.from(this)
                .enableAnimation(true)
                .setOnPickListener(object : OnPickListener {
                    override fun onPick(position: Int, data: City) {
                        et_base_address.setText( "${data.province} \t${data.name} ")
                    }

                    override fun onCancel() {

                    }

                    override fun onLocate(locationListener: OnLocationListener) {

                        //开始定位，这里模拟一下定位
//                        Handler().postDelayed(Runnable {
//                            locationListener.onLocationChanged(
//                                LocatedCity("深圳", "广东", "101280601"),
//                                LocateState.SUCCESS
//                            )
//                        }, 3000)
                    }
                })
                .show()
        }


    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun initListener() {
    }

}
