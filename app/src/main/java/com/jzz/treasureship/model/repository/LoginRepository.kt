package com.jzz.treasureship.model.repository

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.JzzApiService
import com.jzz.treasureship.model.api.JzzRetrofitClient
import com.jzz.treasureship.model.bean.JzzResponse
import com.jzz.treasureship.model.bean.QuestionnaireResponseVo
import com.jzz.treasureship.model.bean.UpdateAppBean
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.utils.PreferenceUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginRepository(private val service: JzzApiService) : BaseRepository() {
    private var isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private var userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    private var accessToken by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
    private var isAudit by PreferenceUtils(PreferenceUtils.AUDIT_STATUS, -2)

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

        return executeResponse(response, {
            if (response.code != 200) {
                ToastUtils.showShort("${response.message}")
            } else {
                val user = response.result
                isLogin = true
                userJson = GsonUtils.toJson(user)
                if (user != null) {
                    App.CURRENT_USER = user
                    accessToken = user.accessToken.toString()
                    isAudit = user.auditStatus!!
                }
            }
        })
    }

    //登录
    suspend fun loginByCode(userName: String, activeCode: String): Result<JzzResponse<User>> {
        return safeApiCall(
            call = { requestLogin(userName.replace(" ", ""), activeCode) },
            errorMessage = "登录失败!"
        )
    }

    //绑定手机号
    suspend fun bindMobile(userName: String, activeCode: String): Result<JzzResponse<User>> {
        return safeApiCall(
            call = { requestBindMobile(userName.replace(" ", ""), activeCode) },
            errorMessage = "绑定失败!"
        )
    }

    //注册
    suspend fun register(userName: String, activeCode: String): Result<JzzResponse<User>> {
        return safeApiCall(call = { requestRegister(userName.replace(" ", ""), activeCode) }, errorMessage = "注册失败")
    }

    //获取验证码
    suspend fun sendSmsCode(type: Int, userName: String): Result<JzzResponse<Unit>> {
        return safeApiCall(call = { requestSmsCode(type, userName.replace(" ", "")) }, errorMessage = "验证码请求失败")
    }

    //微信登录
    suspend fun wxLogin(code: String): Result<JzzResponse<User>> {
        return safeApiCall(call = { requestWxLogin(code) }, errorMessage = "微信登录失败")
    }

    //请求注册，注册成功后直接登录
    private suspend fun requestRegister(userName: String, activeCode: String): Result<JzzResponse<User>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("activeCode", activeCode)
        body.put("userName", userName.trim())

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.registerByCode(requestBody)
        return executeResponse(response, {
            if (response.code != 200) {
                ToastUtils.showShort("${response.message}")
            } else {
                val user = response.result
                isLogin = true
                userJson = GsonUtils.toJson(user)
                if (user != null) {
                    App.CURRENT_USER = user
                    accessToken = user.accessToken.toString()
                    isAudit = user.auditStatus!!
                }
            }
        })
    }

    //请求登录
    private suspend fun requestLogin(userName: String, activeCode: String): Result<JzzResponse<User>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("activeCode", activeCode)
        body.put("userName", userName.replace(" ", ""))

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.loginByCode(requestBody)

        return executeResponse(
            response,
            {
                if (response.code != 200) {
                    ToastUtils.showShort("${response.message}")
                } else {
                    val user = response.result
                    isLogin = true
                    userJson = GsonUtils.toJson(user)
                    if (user != null) {
                        App.CURRENT_USER = user
                        accessToken = user.accessToken.toString()
                        isAudit = user.auditStatus!!
                    }
                }
            })
    }

    //请求绑定手机号
    private suspend fun requestBindMobile(mobile: String, activeCode: String): Result<JzzResponse<User>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("activeCode", activeCode)
        body.put("mobile", mobile.trim())

        root.put("body", body)
        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.bindMobile(requestBody)

        return executeResponse(
            response,
            {
                if (response.code != 200) {
                    ToastUtils.showShort("${response.message}")
                } else {
                    val user = response.result
                    isLogin = true
                    userJson = GsonUtils.toJson(user)
                    if (user != null) {
                        App.CURRENT_USER = user
                        accessToken = user.accessToken!!
                        isAudit = user.auditStatus!!
                    }
                }
            })
    }

    //请求验证码
    private suspend
    fun requestSmsCode(type: Int, userName: String): Result<JzzResponse<Unit>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("type", type)
        body.put("userName", userName.trim())

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.sendSmsCode(requestBody)

        return executeResponse(
            response,
            {
                if (response.code != 200) {
                    ToastUtils.showShort("${response.message}")
                } else {
                    ToastUtils.showShort("验证码请求成功")
                }
            },
            { ToastUtils.showShort("验证码请求失败") })
    }

    //微信登录
    private suspend fun requestWxLogin(code: String): Result<JzzResponse<User>> {
        val root = JSONObject()
        val body = JSONObject()

        body.put("code", code)
        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.wxLogin(requestBody)

        return executeResponse(
            response,
            {
                if (response.code != 200) {
                    ToastUtils.showShort("${response.message}")
                } else {
                    val user = response.result
                    isLogin = true
                    userJson = GsonUtils.toJson(user)
                    if (user != null) {
                        App.CURRENT_USER = user
                        accessToken = user.accessToken.toString()
                        isAudit = user.auditStatus!!
                    }
                }
            },
            { ToastUtils.showShort("微信登录失败") })
    }

    //检查更新
    suspend fun checkAppUpdate(): Result<JzzResponse<UpdateAppBean>> {
        return safeApiCall(call = { requestAppUpdate() }, errorMessage = "App更新失败")
    }

    //微信登录
    private suspend fun requestAppUpdate(): Result<JzzResponse<UpdateAppBean>> {
        val root = JSONObject()
        val body = JSONObject()

        root.put("body", body)

        val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val response = service.checkUpdate(requestBody)

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
        val response = JzzRetrofitClient.service.getQuestionnaire(requestBody)
        return executeResponse(response)
    }
}