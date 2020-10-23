package com.jzz.treasureship.model.repository

import com.jzz.treasureship.model.bean.CartList
import com.jzz.treasureship.model.bean.JzzResponse

import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.api.JzzRetrofitClient

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class GoodsCartRepository : BaseRepository() {

    suspend fun getCartList(): Result<JzzResponse<CartList>> {
        return safeApiCall(call = { requestCartList() }, errorMessage = "购物车列表请求失败!")
    }

    suspend fun delGoods(cartId: Int): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestDelGoods(cartId) }, errorMessage = "移除商品失败！")
    }

    private suspend fun requestCartList(): Result<JzzResponse<CartList>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getCartList(requestBody)

        return executeResponse(response)
    }

    private suspend fun requestDelGoods(cartId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("cartId", cartId)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.deleteCart(requestBody)

        return executeResponse(response)
    }
}