package com.jzz.treasureship.model.repository
import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.bean.Brand
import com.jzz.treasureship.model.bean.JzzResponse

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class HomeSearchRepository : BaseRepository() {

    //获取品牌列表
    suspend fun getBrandList(): Result<JzzResponse<ArrayList<Brand>>> {
        return safeApiCall(call = { requestBrandList() }, errorMessage = "视频列表请求失败!")
    }

    //获取品牌列表
    private suspend fun requestBrandList(): Result<JzzResponse<ArrayList<Brand>>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getBrandList(requestBody)

        return executeResponse(response)
    }

    //获取病症列表
    suspend fun getIllnessList(): Result<JzzResponse<ArrayList<Illness>>> {
        return safeApiCall(call = { requestIllnessList() }, errorMessage = "病症列表请求失败!")
    }

    //获取病症列表
    private suspend fun requestIllnessList(): Result<JzzResponse<ArrayList<Illness>>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getIllnessList(requestBody)

        return executeResponse(response)
    }

    //获取热搜列表
    suspend fun getHotSearchList(): Result<JzzResponse<ArrayList<HotSearch>>> {
        return safeApiCall(call = { requestHotSearchList() }, errorMessage = "热搜列表请求失败!")
    }

    //获取热搜列表
    private suspend fun requestHotSearchList(): Result<JzzResponse<ArrayList<HotSearch>>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getHotSearchList(requestBody)

        return executeResponse(response)
    }

    //搜索条件分页获取视频列表
    suspend fun getSearchPageList(
        str: String,
        what: Int,
        type: Int,
        pageNum: Int = -1
    ): Result<JzzResponse<VideoPageList>> {
        return safeApiCall(call = { requestSearchPageList(str, what, type, pageNum) }, errorMessage = "搜索请求失败!")
    }

    //搜索条件分页获取视频列表
    private suspend fun requestSearchPageList(
        str: String,
        what: Int,
        type: Int,
        pageNum: Int = 1
    ): Result<JzzResponse<VideoPageList>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        when (what) {
            0 -> body.put("brandId", str)
            1 -> body.put("illnessName", str)
            2 -> body.put("keyword", str)
        }

        body.put("type", type)

        root.put("header", header)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getSearchPageList(requestBody)

        return executeResponse(response)
    }
}