package com.jzz.treasureship.model.repository

import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.CartList
import com.jzz.treasureship.model.bean.JzzResponse
import com.jzz.treasureship.model.bean.OrderExpress
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ExpressRepository : BaseRepository() {
    suspend fun getOrderExpress(id: Int): Result<JzzResponse<OrderExpress>> {
        return safeApiCall(call = { requestExpress(id) }, errorMessage = "物流信息获取失败！")
    }

    private suspend fun requestExpress(id: Int): Result<JzzResponse<OrderExpress>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("id", id)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getOrderExpress(requestBody)

        return executeResponse(response)
    }
}