package com.jzz.treasureship.model.repository

import com.jzz.treasureship.model.bean.JzzResponse

import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.WalletBalance

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class WalletRespository : BaseRepository() {

    //余额
    suspend fun getBalance(): Result<JzzResponse<WalletBalance>> {
        return safeApiCall(call = { requestBalance() }, errorMessage = "钱包余额获取失败！")
    }

    private suspend fun requestBalance(): Result<JzzResponse<WalletBalance>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getBalance(requestBody)

        return executeResponse(response)
    }

    //列表
    suspend fun getBalanceList(accountType: Int = -1, pageNum: Int = 1): Result<JzzResponse<DataXXX>> {
        return safeApiCall(call = { requestList(accountType, pageNum) }, errorMessage = "钱包列表获取失败！")
    }

    private suspend fun requestList(accountType: Int = -1, pageNum: Int = 1): Result<JzzResponse<DataXXX>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        if (accountType != -1) {
            body.put("accountType", accountType)
        }

        root.put("header",header)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getBalanceList(requestBody)

        return executeResponse(response)
    }
}