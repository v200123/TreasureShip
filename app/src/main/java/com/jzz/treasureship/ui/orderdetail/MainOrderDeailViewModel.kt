package com.jzz.treasureship.ui.orderdetail

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.OrderDetailsBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *@PackageName: com.jzz.treasureship.ui.orderdeatil
 *@Auth： 29579
*/
class MainOrderDeailViewModel : BaseViewModel() {

    val mOrderDetailMsg = MutableLiveData<OrderDetailsBean>()

    fun getOrderDetail(){
        launchTask("获取详情中") {
            withContext(Dispatchers.IO){
           HttpHelp.getRetrofit().getOrderDetail(BaseRequestBody(OrderDetailBody(644)))
               .resultCheck{
                   mOrderDetailMsg.postValue(it)
               }
            }
        }
    }

}