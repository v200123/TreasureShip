package com.jzz.treasureship.ui.coupon.viewModel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.CouponBean

/**
 *@author LC
 *@createTime 2020/9/19 22:28
 *@description  描述文件
 */
class CouponUseViewModel: BaseViewModel() {

    val couponData = MutableLiveData<CouponBean>()

    fun getCouponList(body: BaseRequestBody){
        launchTask {
            HttpHelp.getRetrofit().getCouponList(body).resultCheck(
                {couponData.postValue(it)}
            )
        }
    }


}