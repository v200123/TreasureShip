package com.jzz.treasureship.ui.trace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.OrderExpress
import com.jzz.treasureship.model.repository.ExpressRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TraceViewModel(val repository: ExpressRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {

    private val _ExpressState = MutableLiveData<TraceModel>()
    val expressState: LiveData<TraceModel>
        get() = _ExpressState

    //获取物流信息
    fun getTrace(id: Int) {
        emitExpressUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getOrderExpress(id)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitExpressUiState(false, null, result.result.result)
                } else {
                    when (result.result?.code) {
                        401 -> {
                            var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                            var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                            var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                            var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                            login = false
                            wxCode =""
                            userInfo=""
                            access=""
                            emitExpressUiState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitExpressUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitExpressUiState(false, "物流信息请求失败")
            }
        }
    }

    private fun emitExpressUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: OrderExpress? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = TraceModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _ExpressState.value = uiModel
    }

    data class TraceModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: OrderExpress?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}