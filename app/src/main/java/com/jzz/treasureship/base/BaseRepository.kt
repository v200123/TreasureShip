package com.jzz.treasureship.base

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.model.bean.JzzResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.exception.NotLoginException
import com.jzz.treasureship.utils.PreferenceUtils
import java.io.IOException

open class BaseRepository {
    suspend fun <T : Any> apiCall(call: suspend () -> JzzResponse<T>): JzzResponse<T> {
        return call.invoke()
    }

    suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            // An exception was thrown when calling the API so we're converting this to an IOException
            Log.d("Jzz", e.toString())
            Result.Error(IOException(errorMessage, e))
        }
//        val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
//        return if (isLogin) {
//            try {
//                call()
//            } catch (e: Exception) {
//                // An exception was thrown when calling the API so we're converting this to an IOException
//                Log.d("Jzz", e.toString())
//                Result.Error(IOException(errorMessage, e))
//            }
//        } else {
//            val intent = Intent(App.CONTEXT, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            App.CONTEXT.startActivity(intent)
//            Result.Error(NotLoginException("未登录"))
//        }
    }

    suspend fun <T : Any> executeResponse(
        response: JzzResponse<T>, successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): Result<JzzResponse<T>> {
        return coroutineScope {
            if (response.code == -1) {
                errorBlock?.let { it() }
                Result.Error(IOException(response.message))
            } else {
                successBlock?.let { it() }
                Result.Success(response)
            }
        }
    }

}
