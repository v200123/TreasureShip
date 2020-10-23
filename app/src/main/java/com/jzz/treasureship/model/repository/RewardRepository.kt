package com.jzz.treasureship.model.repository

import cn.jpush.android.api.JPushInterface
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.model.api.JzzApiService

import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.bean.JzzResponse
import com.jzz.treasureship.model.bean.Reward

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class RewardRepository(private val service: JzzApiService) : BaseRepository() {


    //领取红包
    suspend fun getReward(): Result<JzzResponse<Reward>> {
        return safeApiCall(
            call = { requestReward() },
            errorMessage = "红包/问卷获取失败!"
        )
    }

    //微信登录
    private suspend fun requestReward(): Result<JzzResponse<Reward>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.receiveRedEnvelope(requestBody)

        return executeResponse(response)
    }
}