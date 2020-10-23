package com.jzz.treasureship.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.model.bean.firstBean
import com.jzz.treasureship.model.repository.WithdrawRepository
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ErrorState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WithdrawViewModel(val repository: WithdrawRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {

    private val _userState = MutableLiveData<UserModel>()
    val userState: LiveData<UserModel>
        get() = _userState

    // ViewModel 只处理视图逻辑，数据仓库 Repository 负责业务逻辑
    fun bindWeChat(code: String) {
        emitUiState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.bindWeChat(code)
            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitUiState(false, null, result.result.result)
                    } else {
                        when (result.result?.code) {
                            401 -> {
                                var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                                var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                                var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                                var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                                login = false
                                wxCode = ""
                                userInfo = ""
                                access = ""
                                emitUiState(false, "失败!${result.result.message}", null, false, false)
                            }
                            else -> {
                                emitUiState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitUiState(false, "默认地址请求失败")
                }
            }
        }
    }

    private fun emitUiState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: User? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = UserModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _userState.value = uiModel
    }

    data class UserModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: User?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )

    private val _withdrawState = MutableLiveData<withDrawModel>()
    val withDrawState: LiveData<withDrawModel>
        get() = _withdrawState

    // ViewModel 只处理视图逻辑，数据仓库 Repository 负责业务逻辑
    fun askWithdraw(amt: String) {
        emitWithDrawState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.askWithdraw(amt)
            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitWithDrawState(false, null, "${result.result.message}")
                    } else {
                        when (result.result?.code) {
                            401 -> {
                                var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                                var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                                var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                                var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                                login = false
                                wxCode = ""
                                userInfo = ""
                                access = ""
                                emitWithDrawState(false, "失败!${result.result.message}", null, false, false)
                            }
                            else -> {
                                emitWithDrawState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitWithDrawState(false, "默认地址请求失败")
                }
            }
        }
    }

    private fun emitWithDrawState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = withDrawModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _withdrawState.value = uiModel
    }

    data class withDrawModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )

    val isUse = MutableLiveData<firstBean>()
    fun getCouponUse() {
        launchTask {
            HttpHelp.getRetrofit().couponIsUse().resultCheck{
                isUse.postValue(it)
            }
        }
    }

    val canWithDraw = MutableLiveData<String>()
    fun getWallet() {
        launchTask({
            mStateLiveData.postValue(ErrorState("请求取消了"))
        }) {
            HttpHelp.getRetrofit().getBalance02().resultCheck{
                canWithDraw.postValue(it!!.balance)
            }
        }
    }




}