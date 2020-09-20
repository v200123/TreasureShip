package com.jzz.treasureship.model.api

import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.jzz.treasureship.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *@date: 2020/9/10
 *@describe:
 *@Auth: 29579
 **/
class HttpHelp {
    companion object{
        fun getRetrofit():JzzApiService{
            val okHttpClient = OkHttpClient.Builder().callTimeout(30L, TimeUnit.SECONDS)
                .connectTimeout(30L, TimeUnit.SECONDS)
                .addInterceptor(addCommonHeadInterceptor())
                .addInterceptor(
                    LoggingInterceptor.Builder().loggable(BuildConfig.DEBUG)
                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Platform.WARN)
                        .request("Request")
                        .response("Response")
                        .build()
                ).build()

         return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BaseUrl)
                .client(okHttpClient)
                .build()
                .create(JzzApiService::class.java)

        }

    }
}
