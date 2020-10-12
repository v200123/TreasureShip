package com.jzz.treasureship.model.repository

import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class GoodsRepository : BaseRepository() {

    suspend fun getGoodsDetail(goodsId: Int): Result<JzzResponse<GoodsDetail>> {
        return safeApiCall(call = { requestGoodsDetail(goodsId) }, errorMessage = "商品详情信息获取失败")
    }

    private suspend fun requestGoodsDetail(goodsId: Int): Result<JzzResponse<GoodsDetail>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("goodsId", goodsId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getGoodsDetail(requestBody)

        return executeResponse(response)
    }

    suspend fun addCart(count: Int, skuId: Int): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestAddCart(count, skuId) }, errorMessage = "添加购物车失败")
    }

    private suspend fun requestAddCart(count: Int, skuId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("count", count)
        body.put("skuId", skuId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.addCart(requestBody)

        return executeResponse(response)
    }


    //获取购物车商品数量
    suspend fun getCartCount(): Result<JzzResponse<Int>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(JzzRetrofitClient.service.getCount(requestBody)) },
            errorMessage = "购物车商品数量获取失败"
        )
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

    //获取用户信息
    suspend fun getUserInfo(): Result<JzzResponse<User>> {
        return safeApiCall(
            call = { requestUserInfo() },
            errorMessage = "获取用户信息请求失败!"
        )
    }

    //请求获取用户信息
    private suspend fun requestUserInfo(): Result<JzzResponse<User>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getUserInfo(requestBody)

        return executeResponse(response)
    }
}