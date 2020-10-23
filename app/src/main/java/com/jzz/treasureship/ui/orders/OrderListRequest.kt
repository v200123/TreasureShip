package com.jzz.treasureship.ui.orders

import com.google.gson.annotations.SerializedName
import com.jzz.treasureship.model.bean.body


/**
 *@PackageName: com.jzz.treasureship.ui.orders
 *@Auth： 29579
 *@Description: 订单列表的请求实体类  **/
class OrderListRequest(@SerializedName("memberId")
                       var mMemberId: Int = 0, // 0
                       @SerializedName("orderNo")
                       var mOrderNo: String? = "", // string
                       @SerializedName("orderStatus")
                       var mOrderStatus: Int = 0, // 0
                       @SerializedName("orderType")
                       var mOrderType: Int = 0 // 0
 ) : body()