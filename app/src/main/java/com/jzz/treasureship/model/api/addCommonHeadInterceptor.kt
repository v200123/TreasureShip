package com.jzz.treasureship.model.api

import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.out
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response

/**
 *@date: 2020/9/10
 *@describe: 添加请求的统一header
 *@Auth: 29579
 **/
class addCommonHeadInterceptor
    : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
        access.out(true)
        if(access.isNotBlank() or ("null" != access) or !request.url.host.contains("/api/v1/smsCode/sendCode"))
            {
                val build = request.newBuilder().addHeader("accessToken", access)
                    .addHeader("Accept","*/*")
                    .build()
              return  chain.proceed(build)
            }
        return  chain.proceed(request)
    }
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val body: RequestBody? = request.body
//        if(body.toString().contentEquals("body"))
//        if(!body.toString().contentEquals("header"))
//        {
//            val trimIndent = """
//                    ,"header": {
//                    "os": "android",
//                    "pageNum": 1,
//                    "pageSize": 20
//  }
//            """.trimIndent()
//
//        }
//        if(request.headers.size == 0)
//        {
//
//        }
//        request.url
//
//
//    }
}
