package com.jzz.treasureship.ui.orders

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.OrderRefundListBean
import com.jzz.treasureship.model.bean.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *@PackageName: com.jzz.treasureship.ui.orders
 *@Auth： 29579
 **/
class OrderRefundViewModel : BaseViewModel() {

    val RefundList = MutableLiveData<OrderRefundListBean>()

    fun getRefundList(pagerNum: Int) {
            launchTask{
                withContext(Dispatchers.IO){
                    HttpHelp.getRetrofit().getOrderRefundList(BaseRequestBody(MyHeader = header(pageNum = pagerNum)))
                        .resultCheck{
                            RefundList.value = it
                        }
                }

            }
    }

}