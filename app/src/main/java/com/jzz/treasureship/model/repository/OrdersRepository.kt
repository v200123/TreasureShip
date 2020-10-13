package com.jzz.treasureship.model.repository

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.jzz.treasureship.model.bean.JzzResponse
import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.Orders

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class OrdersRepository : BaseRepository() {

    //获取默认收货地址
    suspend fun getOrderList(
        memberId: Int?,
        orderNo: String?,
        orderStatus: Int?,
        orderType: Int?,
        pageNum: Int = 1
    ): Result<JzzResponse<Orders>> {
        return safeApiCall(
            call = { requestOrderList(memberId, orderNo, orderStatus, orderType, pageNum) },
            errorMessage = "获取订单列表请求失败!"
        )
    }

    //请求获取订单详情/客户列表
    //客户详情必传，值为8】订单状态（不传表示全部 0、待付款1、待发货2、已发货3、已收货、8、已完成9、退款中10、已退款11、已关闭）
    private suspend fun requestOrderList(
        memberId: Int?,
        orderNo: String?,
        orderStatus: Int?,
        orderType: Int?,
        pageNum: Int = 1
    ): Result<JzzResponse<Orders>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        if (memberId != -1) {
            body.put("memberId", memberId)
        }

        if (!orderNo.isNullOrBlank()) {
            body.put("orderNo", orderNo)
        }

        if (orderStatus != -1) {
            body.put("orderStatus", orderStatus)
        }

        if (orderType != -1) {
            body.put("orderType", orderType)
        }

        root.put("header", header)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getOrderList(requestBody)

        return executeResponse(response)
    }

    suspend fun createOrder(body: JSONObject): Result<JzzResponse<Order>> {
        return safeApiCall(call = { requestCreateOrder(body) }, errorMessage = "订单生成失败")
    }

    private suspend fun requestCreateOrder(body: JSONObject): Result<JzzResponse<Order>> {
        val root = JSONObject()

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.createOrder(requestBody)

        return executeResponse(response)
    }

    //订单支付
    suspend fun payByOrder(orderId: Int): Result<JzzResponse<OrdersGoPayBean>> {
        return safeApiCall(call = { requestPayByOrder(orderId) }, errorMessage = "订单支付失败")
    }

    private suspend fun requestPayByOrder(orderId: Int): Result<JzzResponse<OrdersGoPayBean>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("id", orderId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.payByOrder(requestBody)

        return executeResponse(response)
    }

    //查询订单状态
    suspend fun queryOrderStatus(orderId: Int): Result<JzzResponse<Data>> {
        return safeApiCall(call = { requestOrderStatus(orderId) }, errorMessage = "订单状态获取失败")
    }

    private suspend fun requestOrderStatus(orderId: Int): Result<JzzResponse<Data>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("id", orderId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.queryOrderStatus(requestBody)

        return executeResponse(response)
    }

    //确认收货
    suspend fun sureReceive(orderId: Int): Result<JzzResponse<Order>> {
        return safeApiCall(call = { requestSureReceive(orderId) }, errorMessage = "确认收货失败")
    }

    private suspend fun requestSureReceive(orderId: Int): Result<JzzResponse<Order>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("id", orderId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.sureReceived(requestBody)

        return executeResponse(response)
    }

    //确认收货
    suspend fun setNotice(memberId: Int, notice: Int, noticeTime: String): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestNotice(memberId, notice, noticeTime) }, errorMessage = "提醒失败")
    }

    private suspend fun requestNotice(memberId: Int, notice: Int, noticeTime: String): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("memberId", memberId)
        body.put("notice", notice)
        body.put("noticeTime", noticeTime)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.setNotice(requestBody)

        return executeResponse(response)
    }

    //确认收货
    suspend fun askRefund(orderNo: String,refundReason:String = ""): Result<JzzResponse<Refund>> {
        return safeApiCall(call = { requestRefund(orderNo,refundReason) }, errorMessage = "申请退款失败")
    }

    private suspend fun requestRefund(orderNo: String,refundReason:String = ""): Result<JzzResponse<Refund>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("orderNo",orderNo)
        if (refundReason.isNotEmpty()){
            body.put("refundReason",refundReason)
        }
        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.refundQuery(requestBody)

        return executeResponse(response)
    }

}