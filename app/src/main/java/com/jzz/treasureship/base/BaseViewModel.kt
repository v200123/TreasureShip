package com.jzz.treasureship.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.core.Result
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.StateActionEvent
import com.lc.mybaselibrary.SuccessState
import kotlinx.coroutines.*
import okhttp3.internal.ignoreIoExceptions

typealias LaunchBlock = suspend CoroutineScope.() -> Unit
typealias Cancel = (e: Exception) -> Unit
open class BaseViewModel : ViewModel() {

    val mStateLiveData = MutableLiveData<StateActionEvent>()

    open class BaseUiModel<T>(
        var showLoading: Boolean = false,
        var showError: String? = null,
        var showSuccess: T? = null,
        var showEnd: Boolean = false, // 加载更多
        var isRefresh: Boolean = false // 刷新

    )


    fun launchTask(cancel: Cancel? = null, block: LaunchBlock) {//使用协程封装统一的网络请求处理
        viewModelScope.launch {
            //ViewModel自带的viewModelScope.launch,会在页面销毁的时候自动取消请求,有效封装内存泄露
            try {
                mStateLiveData.value = LoadState
                block()
                mStateLiveData.value = SuccessState
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> cancel?.invoke(e)
                    else -> mStateLiveData.value = ErrorState(e.message)
                }
            }
        }
    }


    val mException: MutableLiveData<Throwable> = MutableLiveData()


    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        viewModelScope.launch { block() }

    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    fun launc(tryBlock: suspend CoroutineScope.() -> Unit) {
        launchOnUI {
            tryCatch(tryBlock, {}, {}, true)
        }
    }


    fun launchOnUITryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean
    ) {
        launchOnUI {
            tryCatch(tryBlock, catchBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }

    fun launchOnUITryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean = false
    ) {
        launchOnUI {
            tryCatch(tryBlock, {}, {}, handleCancellationExceptionManually)
        }
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

    inline fun <T : Any> checkResult(result: Result<T>, success: (T) -> Unit, error: (String?) -> Unit) {
        if (result is Result.Success) {
            result.result?.let {
                success(it)
            }
        } else if (result is Result.Error) {
            error(result.exception.message)
        }
    }
}
