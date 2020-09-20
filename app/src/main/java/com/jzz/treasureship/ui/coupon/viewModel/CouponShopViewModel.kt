package com.jzz.treasureship.ui.coupon.viewModel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.CouponShopBean

/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class CouponShopViewModel : BaseViewModel() {
     val mCouponData = MutableLiveData<CouponShopBean>()
fun getList(body:BaseRequestBody){
    launchTask {
        HttpHelp.getRetrofit().getRecommendGoods(body).resultCheck({
            mCouponData.postValue(it)
        })
    }
}
}