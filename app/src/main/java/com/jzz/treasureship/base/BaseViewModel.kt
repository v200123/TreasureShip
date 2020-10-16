package com.jzz.treasureship.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.JzzResponse
import com.lc.mybaselibrary.*
import kotlinx.coroutines.*

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

    fun <T> JzzResponse<T>.resultCheck(
        block: (T?) -> Unit, errorMsg: (msg: String) -> Unit = {
            mStateLiveData.postValue(ErrorState(it))
        }
    ) {
        if (this.success) {
            block(this.result)
        } else {
            if (this.code == 401) {
                mStateLiveData.value = NeedLoginState
            }
            errorMsg(this.message)
        }
    }

    fun launchTask(
        cancel: Cancel? = { mStateLiveData.postValue(ErrorState("请求取消")) },
        block: LaunchBlock
    ) {//使用协程封装统一的网络请求处理
        viewModelScope.launch {
                    //ViewModel自带的viewModelScope.launch,会在页面销毁的时候自动取消请求,有效封装内存泄露
            mStateLiveData.value = LoadState
            runCatching { block() }
                .onSuccess {
                    mStateLiveData.value = SuccessState
                }
                .onFailure { when (it) {
                    is CancellationException -> cancel?.invoke(it)
                    else -> mStateLiveData.value = ErrorState(it.message)
                } }

//            try {
//                block()
//            } catch (e: Exception) {
//                when (e) {
//                    is CancellationException -> cancel?.invoke(e)
//                    else -> mStateLiveData.value = ErrorState(e.message)
//                }
//            }
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
