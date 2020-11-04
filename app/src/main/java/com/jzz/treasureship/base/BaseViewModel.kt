package com.jzz.treasureship.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.JzzResponse
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.NeedLoginState
import com.lc.mybaselibrary.StateActionEvent
import com.lc.mybaselibrary.SuccessState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

typealias LaunchBlock = suspend CoroutineScope.() -> Unit
typealias Cancel = (e: Exception) -> Unit

open class BaseViewModel : ViewModel() {
    val mStateLiveData = MutableLiveData<StateActionEvent>()



   suspend fun <T> JzzResponse<T>.resultCheck(
        errorMsg: (msg: String) -> Unit = {
            mStateLiveData.postValue(ErrorState(it))
        },
        block: (T?) -> Unit
    ) {
       withContext(Dispatchers.Main){
           if (this@resultCheck.success) {
               block(this@resultCheck.result)
           } else {
               if (this@resultCheck.code == 401) {
                   mStateLiveData.postValue(NeedLoginState)
               }
               errorMsg(this@resultCheck.message)
           }
       }


    }

    fun launchTask(msg:String = "",
        cancel: Cancel? = { mStateLiveData.postValue(ErrorState("请求取消")) },
        block: LaunchBlock
    ) {//使用协程封装统一的网络请求处理
        viewModelScope.launch {
            //ViewModel自带的viewModelScope.launch,会在页面销毁的时候自动取消请求,有效封装内存泄露
            mStateLiveData.value = LoadState(msg)
            runCatching {
                block()
            }
                .onSuccess {
                    mStateLiveData.postValue(SuccessState)
                }
                .onFailure {
                    getApiException(it, cancel)
                }
        }
    }

    private fun getApiException(e: Throwable, cancel: Cancel?) {
        when (e) {
            is UnknownHostException -> {
                mStateLiveData.value = ErrorState("网络异常", -100)
            }
            is JSONException -> {//|| e is JsonParseException
                mStateLiveData.value = ErrorState("数据异常", -100)
            }
            is SocketTimeoutException -> {
                mStateLiveData.value = ErrorState("连接超时", -100)
            }
            is ConnectException -> {
                mStateLiveData.value = ErrorState("连接错误", -100)
            }
            is HttpException -> {
                mStateLiveData.value = ErrorState("http code ${e.code()}", -100)
            }
            is JsonSyntaxException ->{
                mStateLiveData.value = ErrorState("解析失败 ${e.message}", -100)
            }
            /**
             * 如果协程还在运行，个别机型退出当前界面时，viewModel会通过抛出CancellationException，
             * 强行结束协程，与java中InterruptException类似，所以不必理会,只需将toast隐藏即可
             */
            is CancellationException -> {
                cancel?.invoke(e)
            }
            else -> {
                mStateLiveData.value = ErrorState("未知错误", -100)
            }
        }
    }


    val mException: MutableLiveData<Throwable> = MutableLiveData()


    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch { block() }

    }



    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean = false
    ) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Throwable) {
                if (e !is CancellationException || handleCancellationExceptionManually) {
                    mException.value = e
                    catchBlock(e)
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }

    inline fun <T : Any> checkResult(
        result: Result<T>,
        success: (T) -> Unit,
        error: (String?) -> Unit
    ) {
        if (result is Result.Success) {
            result.result?.let {
                success(it)
            }
        } else if (result is Result.Error) {
            error(result.exception.message)
        }
    }


}
