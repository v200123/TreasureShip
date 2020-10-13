package com.jzz.treasureship.model.repository

import com.jzz.treasureship.model.bean.JzzResponse

import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.InvitedList

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class InviteRespository : BaseRepository() {

    suspend fun getInvitedList(pageNum: Int = 1): Result<JzzResponse<InvitedList>> {
        return safeApiCall(call = { requestInvitedList(pageNum) }, errorMessage = "邀请列表获取失败！")
    }

    private suspend fun requestInvitedList(pageNum: Int = 1): Result<JzzResponse<InvitedList>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        root.put("header", header)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getInvitedList(requestBody)

        return executeResponse(response)
    }
}