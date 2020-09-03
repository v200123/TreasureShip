package com.jzz.treasureship.model.repository

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class PaypalRepository : BaseRepository() {

    //获取默认收货地址
    suspend fun getPayAddress(): Result<JzzResponse<Address>> {
        return safeApiCall(
            call = { requestPayAdrress() },
            errorMessage = "获取默认收货地址请求失败!"
        )
    }

    //请求获取默认收货地址
    private suspend fun requestPayAdrress(): Result<JzzResponse<Address>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getPayAddress(requestBody)

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

    suspend fun getDirectBuy(count: Int, skuId: Int): Result<JzzResponse<ShopcarBuyBean>> {
        return safeApiCall(call = { requestGetDirectBuy(count, skuId) }, errorMessage = "直接购买失败")
    }

    private suspend fun requestGetDirectBuy(count: Int, skuId: Int): Result<JzzResponse<ShopcarBuyBean>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("count", count)
        body.put("skuId", skuId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(JzzRetrofitClient.service.getDirectBuy(requestBody)) },
            errorMessage = "直接购买失败"
        )
    }

    suspend fun cartSettlement(shops: String): Result<JzzResponse<ShopcarBuyBean>> {
        return safeApiCall(call = { requestCartSettlement(shops) }, errorMessage = "购物车结算请求失败")
    }

    private suspend fun requestCartSettlement(shops: String): Result<JzzResponse<ShopcarBuyBean>> {
        val root = JSONObject()
        val body = JSONObject()
        val shopsJson = JSONArray(shops)

        body.put("shops", shopsJson)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(JzzRetrofitClient.service.cartSettlement(requestBody)) },
            errorMessage = "购物车结算失败"
        )
    }

    suspend fun addDoctorAdvice(advice: String, orderNo: String): Result<JzzResponse<AddAdviceResBean>> {
        return safeApiCall(call = { requestAddDoctorAdvice(advice, orderNo) }, errorMessage = "添加医嘱请求失败")
    }

    private suspend fun requestAddDoctorAdvice(advice: String, orderNo: String): Result<JzzResponse<AddAdviceResBean>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("advice", advice)
        body.put("orderNo", orderNo)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(JzzRetrofitClient.service.addDoctorAdvice(requestBody)) },
            errorMessage = "添加医嘱失败"
        )
    }

    suspend fun getDocContent(orderChildId: String, key: String = "contract"): Result<JzzResponse<Agreement>> {
        return safeApiCall(call = { requestDocContent(orderChildId, key) }, errorMessage = "合同内容请求失败")
    }

    private suspend fun requestDocContent(
        orderChildId: String,
        key: String = "contract"
    ): Result<JzzResponse<Agreement>> {
        val root = JSONObject()
        val body = JSONObject()

        if (!orderChildId.isNullOrBlank()) {
            body.put("orderChildId", orderChildId)
        }

        body.put("key", key)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(JzzRetrofitClient.service.getDocContent(requestBody)) },
            errorMessage = "合同内容请求失败"
        )
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
}