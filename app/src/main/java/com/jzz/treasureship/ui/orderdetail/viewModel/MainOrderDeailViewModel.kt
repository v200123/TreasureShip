package com.jzz.treasureship.ui.orderdetail.viewModel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.Order
import com.jzz.treasureship.model.bean.OrderDetailsBean
import com.jzz.treasureship.ui.orderdetail.requestbody.OrderDetailBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 *@PackageName: com.jzz.treasureship.ui.orderdeatil
 *@Auth： 29579
 */
class MainOrderDeailViewModel : BaseViewModel() {

    val mOrderDetailMsg = MutableLiveData<OrderDetailsBean>()
    val addCartResult = MutableLiveData<Boolean>()
    val orderResult = MutableLiveData<Order>()
    fun getOrderDetail(orderId:Int) {
        launchTask("获取详情中") {
            withContext(Dispatchers.IO) {
                HttpHelp.getRetrofit().getOrderDetail(BaseRequestBody(OrderDetailBody(orderId)))
                    .resultCheck {
                        mOrderDetailMsg.postValue(it)
                    }
            }
        }
    }

    fun addShopCart(skuId: Int) {
        launchTask {
            withContext(Dispatchers.IO) {
                val root = JSONObject()
                val body = JSONObject()
                val head = JSONObject()
                head.put("os", 1)
                head.put("pageNum", 1)
                head.put("pageSize", 1)
                body.put("count", 1)
                body.put("skuId", skuId)
                root.put("body", body)
                root.put("header", head)
                val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
                HttpHelp.getRetrofit().addCart(requestBody).resultCheck { addCartResult.postValue(true) }
            }
        }
    }


    fun ensureOrderReceived(orderId:Int){
        launchTask {
            val root = JSONObject()
            val body = JSONObject()

            body.put("id", orderId)

            root.put("body", body)
            val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
           HttpHelp.getRetrofit().sureReceived(requestBody).resultCheck {
               orderResult.value = it
           }
        }

    }

}