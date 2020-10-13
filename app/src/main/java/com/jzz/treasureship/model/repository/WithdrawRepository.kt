package com.jzz.treasureship.model.repository

import com.jzz.treasureship.model.bean.JzzResponse

import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.User

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.math.BigDecimal


class WithdrawRepository : BaseRepository() {

    //绑定微信
    suspend fun bindWeChat(code: String): Result<JzzResponse<User>> {
        return safeApiCall(call = { requestBindWeChat(code) }, errorMessage = "微信绑定失败！")
    }

    private suspend fun requestBindWeChat(code: String): Result<JzzResponse<User>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("code", code)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.bindWeChat(requestBody)

        return executeResponse(response)
    }


    //icon_withdraw_comfirm
    suspend fun askWithdraw(amt: String): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestWithdraw(amt) }, errorMessage = "提现申请失败！")
    }

    private suspend fun requestWithdraw(amt: String): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        //与后台确认固定传0
        body.put("accountType", 0)
        body.put("amt", BigDecimal(amt))

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.askWithdraw(requestBody)

        return executeResponse(response)
    }
}