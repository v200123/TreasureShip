package com.jzz.treasureship.model.repository

import android.util.Log
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.CartList
import com.jzz.treasureship.model.bean.JzzResponse
import com.jzz.treasureship.model.bean.OrderExpress
import com.jzz.treasureship.model.bean.Rank
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class RankingRepository : BaseRepository() {
    suspend fun getRankingList(month: String): Result<JzzResponse<Rank>> {
        return safeApiCall(call = { requestRank(month) }, errorMessage = "排名获取失败！")
    }

    private suspend fun requestRank(month: String): Result<JzzResponse<Rank>> {
        val root = JSONObject()
        val body = JSONObject()

        //month (string, optional): 月份 2020-02
        body.put("month", month)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.rank(requestBody)

        return executeResponse(response)
    }
}