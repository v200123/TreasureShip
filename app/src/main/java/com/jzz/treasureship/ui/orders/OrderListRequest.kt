package com.jzz.treasureship.ui.orders

import com.google.gson.annotations.SerializedName
import com.jzz.treasureship.model.bean.body


/**
 *@PackageName: com.jzz.treasureship.ui.orders
 *@Auth： 29579
 *@Description: 订单列表的请求实体类  **/
class OrderListRequest(
                       @SerializedName("orderNo")
                       var mOrderNo: String? = "", // string
                       @SerializedName("orderStatus")
                       var mOrderStatus: String? = "", // 0

 ) : body()