package com.jzz.treasureship.model.repository

import com.jzz.treasureship.model.bean.JzzResponse

import com.jzz.treasureship.core.*
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.User

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class UserRepository : BaseRepository() {
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

    //修改用户信息：头像、昵称
    suspend fun modifiedInfo(avatar: String?, nickName: String?): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestModifiedInfo(avatar, nickName) }, errorMessage = "用户信息修改失败！")
    }

    private suspend fun requestModifiedInfo(avatar: String?, nickName: String?): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        if (!avatar.isNullOrBlank()) {
            body.put("avatar", avatar)
        }

        if (!nickName.isNullOrEmpty()) {
            body.put("nickName", nickName)
        }

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.modifinedPersonalInfo(requestBody)

        return executeResponse(response)
    }

    //上传图片
    suspend fun uploadImg(img: File): Result<JzzResponse<UploadImgBean>> {
        return safeApiCall(call = { requestUploadImg(img) }, errorMessage = "图片上传失败！")
    }

    private suspend fun requestUploadImg(img: File): Result<JzzResponse<UploadImgBean>> {

        val requestFile = img.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", img.name, requestFile)
        val response = JzzRetrofitClient.service.uploadFile(filePart)

        return executeResponse(response)
    }

    //上传图片
    suspend fun saveQualification(
        personalImg: String,
        qualificationCertificateImg: String
    ): Result<JzzResponse<ArrayList<Qualification>>> {
        return safeApiCall(
            call = { requestSaveQualification(personalImg, qualificationCertificateImg) },
            errorMessage = "认证资料提交失败！"
        )
    }

    private suspend fun requestSaveQualification(
        personalImg: String,
        qualificationCertificateImg: String
    ): Result<JzzResponse<ArrayList<Qualification>>> {

        val root = JSONObject()
        val body = JSONObject()

        body.put("personalImg", personalImg.replace("119.3.125.1", "bj.jzzchina.com"))
        body.put("qualificationCertificateImg", qualificationCertificateImg.replace("119.3.125.1", "bj.jzzchina.com"))


        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = JzzRetrofitClient.service.savePersonalQualification(requestBody)

        return executeResponse(response)
    }
}