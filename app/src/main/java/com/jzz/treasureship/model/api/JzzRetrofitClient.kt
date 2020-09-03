package com.jzz.treasureship.model.api

import com.blankj.utilcode.util.NetworkUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.constants.Constants
import com.jzz.treasureship.utils.PreferenceUtils
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.io.File

object JzzRetrofitClient : BaseRetrofitClient() {
    val service by lazy { getService(JzzApiService::class.java, Constants.BASE_URL) }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
        builder.addInterceptor { chain ->
                var request = chain.request()
                if (!NetworkUtils.isAvailable()) {
                    request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
                }
                val response = chain.proceed(request)
                if (!NetworkUtils.isAvailable()) {
                    val maxAge = 60 * 60
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .build()
                } else {
                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                    response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .build()
                }
                response
            }
    }

}
