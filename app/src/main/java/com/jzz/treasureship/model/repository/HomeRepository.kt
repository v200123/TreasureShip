package com.jzz.treasureship.model.repository

import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzRetrofitClient.service
import com.jzz.treasureship.model.bean.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class HomeRepository : BaseRepository() {

    //获取视频商品
    suspend fun getPageList(
        type: Int,
        pageNum: Int = 1,
        brandId: String = "",
        illnessName: String = "",
        keyword: String = ""
    ): Result<JzzResponse<VideoPageList>> {
        return safeApiCall(
            call = { requestVideoList(brandId, illnessName, keyword, type, pageNum) },
            errorMessage = "视频列表请求失败!"
        )
    }

    //请求视频列表
    private suspend fun requestVideoList(
        brandId: String,
        illnessName: String,
        keyword: String,
        type: Int,
        pageNum: Int
    ): Result<JzzResponse<VideoPageList>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        body.put("brandId", brandId)
        body.put("illnessName", illnessName)
        body.put("keyword", keyword)
        //tab分类
        body.put("categoryId", type)
        root.put("header", header)
        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getPageList(requestBody)

        return executeResponse(response)
    }

    //用户添加收藏
    suspend fun addCollect(categoryId: Int, remark: String, videoId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("categoryId", categoryId)
        if (remark.isNotBlank()) {
            body.put("remark", remark)
        }
        if (videoId != -1) {
            body.put("videoId", videoId)
        }


        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(call = { executeResponse(service.addCollect(requestBody)) }, errorMessage = "视频收藏失败")
    }

    //用户添加收藏分类
    suspend fun addCollectCategory(title: String): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("title", title)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(service.addCollectCategory(requestBody)) },
            errorMessage = "添加收藏分类失败"
        )
    }

    //用户取消收藏
    suspend fun cancelCollect(videoId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("videoId", videoId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(call = { executeResponse(service.cancelCollect(requestBody)) }, errorMessage = "取消收藏失败")
    }

    //用户删除收藏分类
    suspend fun deleteCollectCategory(id: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("id", id)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(service.deleteCollectCategory(requestBody)) },
            errorMessage = "删除收藏分类失败"
        )
    }

    //获取用户收藏分类列表
    suspend fun getCollectCategory(): Result<JzzResponse<ArrayList<CollectCategory>>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(service.getCollectCategory(requestBody)) },
            errorMessage = "收藏分类获取失败"
        )
    }

    //获取收藏视频列表
    suspend fun getCollectVideoList(
        categoryId: Int,
        remark: String? = null,
        videoId: Int? = -1,
        pageNum: Int = 1
    ): Result<JzzResponse<VideoPageList>> {
        val root = JSONObject()
        val header = JSONObject()
        val body = JSONObject()

        header.put("os", "android")
        header.put("pageNum", pageNum)
        header.put("pageSize", 20)

        body.put("categoryId", categoryId)
        if (remark != null) {
            if (remark.isNotBlank()) {
                body.put("remark", remark)
            }
        }

        if (videoId != null) {
            if (videoId >= 0) {
                body.put("videoId", videoId)
            }
        }

        root.put("header", header)
        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(service.getCollectVideoList(requestBody)) },
            errorMessage = "收藏视频列表获取失败"
        )
    }

    //获取购物车商品数量
    suspend fun getCartCount(): Result<JzzResponse<Int>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(call = { executeResponse(service.getCount(requestBody)) }, errorMessage = "购物车商品数量获取失败")
    }


    //home_comments、评论回复
    suspend fun addComment(commentId: Int, content: String, videoId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        if (commentId != -1) {
            body.put("commentId", commentId)
        }

        body.put("content", content)

        if (videoId != -1) {
            body.put("videoId", videoId)
        }


        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(call = { executeResponse(service.addComment(requestBody)) }, errorMessage = "添加评论失败")
    }

    //评论点赞
    suspend fun addPraise(commentId: Int, videoId: Int): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("commentId", commentId)
        body.put("videoId", videoId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(call = { executeResponse(service.addPraise(requestBody)) }, errorMessage = "评论点赞失败")
    }

    //获取视频评论
    suspend fun getCommentPageList(commentId: Int, videoId: Int): Result<JzzResponse<CommentPageList>> {
        val root = JSONObject()
        val body = JSONObject()

        if (commentId != -1) {
            body.put("commentId", commentId)
        }

        body.put("videoId", videoId)

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(service.getCommentPageList(requestBody)) },
            errorMessage = "评论列表获取失败"
        )
    }

    //移动收藏视频
    suspend fun moveCollect(
        categoryId: Int,
        remark: String? = null,
        videoId: Int? = -1
    ): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("categoryId", categoryId)
        if (!remark.isNullOrBlank()) {
            body.put("remark", remark)
        }

        if (-1 != videoId) {
            body.put("videoId", videoId)
        }

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())

        return safeApiCall(
            call = { executeResponse(service.moveCollect(requestBody)) },
            errorMessage = "移动收藏视频失败"
        )
    }

    //搜索条件分页获取视频列表
    suspend fun getSearchPageList(
        str: String,
        what: Int,
        type: Int,
        pageNum: Int = 1
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

    //获取首页tab
    suspend fun getHomeTabs(): Result<JzzResponse<HomeTabBean>> {
        return safeApiCall(call = { requestHomeTabs() }, errorMessage = "首页分类请求失败!")
    }

    //搜索条件分页获取视频列表
    private suspend fun requestHomeTabs(): Result<JzzResponse<HomeTabBean>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getHomeTabs(requestBody)

        return executeResponse(response)
    }

    suspend fun sendFeedback(content: String): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestFeedback(content) }, errorMessage = "意见反馈请求失败!")
    }

    //搜索条件分页获取视频列表
    private suspend fun requestFeedback(content: String): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("content", content)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.sendSuggestion(requestBody)

        return executeResponse(response)
    }

    //获取未答题目
    suspend fun getQuestionnaire(): Result<JzzResponse<QuestionnaireResponseVo>> {
        return safeApiCall(call = { requestQuestionnaire() }, errorMessage = "获取未答题目失败")
    }

    //获取未答题目
    private suspend fun requestQuestionnaire(): Result<JzzResponse<QuestionnaireResponseVo>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getQuestionnaire(requestBody)
        return executeResponse(response)
    }

    //提交问卷
    suspend fun submitQuestionnaire(answer:String,id:Int): Result<JzzResponse<Reward.RedEnvelopeRecordVo>> {
        return safeApiCall(call = { requestSubmitQuestionnaire(answer,id) }, errorMessage = "提交问卷失败")
    }

    //提交问卷
    private suspend fun requestSubmitQuestionnaire(answer:String,id:Int): Result<JzzResponse<Reward.RedEnvelopeRecordVo>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("answer",answer)
        body.put("id",id)

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.submitQuestionnaire(requestBody)
        return executeResponse(response)
    }

    //领取红包
    suspend fun getReward(): Result<JzzResponse<Reward>> {
        return safeApiCall(call = { requestReward() }, errorMessage = "领取红包失败")
    }

    //领取红包
    private suspend fun requestReward(): Result<JzzResponse<Reward>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.receiveRedEnvelope(requestBody)
        return executeResponse(response)
    }

    //领取活动
    suspend fun getAd(): Result<JzzResponse<Ad>> {
        return safeApiCall(call = { requestAd() }, errorMessage = "领取活动图片失败")
    }

    //领取活动
    private suspend fun requestAd(): Result<JzzResponse<Ad>> {
        val root = JSONObject()
        val body = JSONObject()
        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.getAdList(requestBody)
        return executeResponse(response)
    }
}
