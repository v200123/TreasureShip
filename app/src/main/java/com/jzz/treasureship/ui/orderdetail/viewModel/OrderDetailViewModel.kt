package com.jzz.treasureship.ui.orderdetail.viewModel

import androidx.lifecycle.ViewModel
import com.jzz.treasureship.model.bean.OrderDetailsBean

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail.viewModel
 *@Auth： 29579
 **/
class OrderDetailViewModel : ViewModel() {

    var id:Int = 644

    var singleOrderInfo: OrderDetailsBean.GoodsSku? = null


    var mingleOrderInfo = listOf<OrderDetailsBean.GoodsSku>()

    set(value) {
        singleOrderInfo = null
        field = value
    }
}