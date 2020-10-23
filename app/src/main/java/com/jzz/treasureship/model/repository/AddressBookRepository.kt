package com.jzz.treasureship.model.repository

import android.util.Log
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.Contacter
import com.jzz.treasureship.model.bean.ContacterGoods
import com.jzz.treasureship.model.bean.JzzResponse

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AddressBookRepository : BaseRepository() {

    //获取通讯录所有人员
    suspend fun getMemberList(
        buyNumOrder: Int = 0,
        buyTimeOrder: Int = 0,
        goodsID: Int = -1,
        name: String? = null,
        pageNumber: Int = 1
    ): com.jzz.treasureship.core.Result<JzzResponse<Contacter>> {
        return safeApiCall(
            call = { requestMembers(buyNumOrder, buyTimeOrder, goodsID, name, pageNumber) },
            errorMessage = "通讯录人员列表请求失败!"
        )
    }

    //获取通讯录所有人员
    private suspend fun requestMembers(
        buyNumOrder: Int = 0,
        buyTimeOrder: Int = 0,
        goodsID: Int = -1,
        name: String? = null,
        pageNumber: Int = 1
    ): com.jzz.treasureship.core.Result<JzzResponse<Contacter>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNumber)
        header.put("pageSize", 20)

        Log.d("requestMembers2", "${buyNumOrder},${buyTimeOrder},${name}")
        if (buyNumOrder != -1) {
            body.put("buyNumOrder", buyNumOrder)
        }

        if (buyTimeOrder != -1) {
            body.put("buyTimeOrder", buyTimeOrder)
        }

        if (goodsID != -1) {
            body.put("goodsId", goodsID)
        }

        if (!name.isNullOrBlank()) {
            body.put("name", name)
        }

        root.put("header", header)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getMemberList(requestBody)

        return executeResponse(response)
    }

    //获取通讯录所有人员
    suspend fun getGoodsList(pageNumber: Int = 1): com.jzz.treasureship.core.Result<JzzResponse<ContacterGoods>> {
        return safeApiCall(
            call = { requestGoods(pageNumber) },
            errorMessage = "产品列表请求失败!"
        )
    }

    //获取通讯录所有人员
    private suspend fun requestGoods(pageNumber: Int = 1): com.jzz.treasureship.core.Result<JzzResponse<ContacterGoods>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNumber)
        header.put("pageSize", 20)


        root.put("header", header)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.getGoodsList(requestBody)

        return executeResponse(response)
    }

    //设置/取消提醒
    suspend fun setNotice(
        memberId: Int,
        notice: Int,
        noticeTime: String
    ): com.jzz.treasureship.core.Result<JzzResponse<Unit>> {
        return safeApiCall(
            call = { requestSetNotice(memberId, notice, noticeTime) },
            errorMessage = "提醒请求失败!"
        )
    }

    //设置/取消提醒
    private suspend fun requestSetNotice(
        memberId: Int,
        notice: Int,
        noticeTime: String
    ): com.jzz.treasureship.core.Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("memberId", memberId)
        body.put("notice", notice)

        if (noticeTime.isNotBlank()) {
            body.put("memberId", memberId)
        }

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.setNotice(requestBody)

        return executeResponse(response)
    }
}