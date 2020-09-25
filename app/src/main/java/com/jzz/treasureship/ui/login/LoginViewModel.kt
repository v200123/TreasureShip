package com.jzz.treasureship.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jpush.android.api.JPushInterface
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.UpdateAppBean
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.model.repository.LoginRepository
import com.jzz.treasureship.utils.CountDownTimerUtils
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(val repository: LoginRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {


    private val _UserState = MutableLiveData<UserModel>()
    val userState: LiveData<UserModel>
        get() = _UserState

    //获取用户信v
    fun getUserInfo() {
        emitUserUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getUserInfo()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    JPushInterface.setAlias(App.CONTEXT,1001,result.result.result!!.id.toString())
                    emitUserUiState(false, null, result.result.result)
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
                            emitUserUiState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitUserUiState(false, "失败!${result.result!!.message}")
                        }
                    }
                }
            } else {
                emitUserUiState(false, "用户信息请求失败")
            }
        }
    }

    private fun emitUserUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: User? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = UserModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _UserState.value = uiModel
    }

    data class UserModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: User?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _uiState = MutableLiveData<LoginUiModel>()
    val uiState: LiveData<LoginUiModel>
        get() = _uiState

    val mLoginUser: MutableLiveData<User> = MutableLiveData()

    // ViewModel 只处理视图逻辑，数据仓库 Repository 负责业务逻辑
    fun loginByCode(phoneNum: String, codeNum: String) {
        emitUiState(true)
        viewModelScope.launch(provider.computation) {

            val result = repository.loginByCode(phoneNum, codeNum)

            withContext(provider.main) {
                checkResult(result, {
                    if (it.result == null && it.code != 200) {

                        emitUiState(showSuccess = null, enableLoginButton = true)
                    } else {
                        emitUiState(showSuccess = it.result, enableLoginButton = true)
                    }

                }, {
                    emitUiState(showError = it, enableLoginButton = true)
                })
            }
        }
    }

    fun bindMobile(phoneNum: String, codeNum: String) {
        emitUiState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.bindMobile(phoneNum, codeNum)

            withContext(provider.main) {
                checkResult(result, {
                    emitUiState(showSuccess = it.result, enableLoginButton = true)
                }, {
                    emitUiState(showError = it, enableLoginButton = true)
                })
            }
        }
    }

    fun sendSmsCode(type: Int, userName: String?, countDown: CountDownTimerUtils) {
        viewModelScope.launch(provider.computation) {
            if (userName.isNullOrBlank()) {
                ToastUtils.showShort("请先输入电话号码")
            } else {
                userName.let {
                    repository.sendSmsCode(type, it,countDown)
                }
            }

        }
    }

    fun register(phoneNum: String, codeNum: String) {
        viewModelScope.launch(provider.computation) {

            withContext(provider.main) { showLoading() }

            val result = repository.register(phoneNum, codeNum)
            withContext(provider.main) {
                if (result is Result.Success) {
                    emitUiState(showSuccess = result.result?.result, enableLoginButton = true)
                } else if (result is Result.Error) {
                    emitUiState(showError = result.exception.message, enableLoginButton = true)
                }
            }
        }
    }

    fun wxLogin(code: String) {
        viewModelScope.launch(provider.computation) {
            if (code.isBlank()) {
                ToastUtils.showShort("wechat code is blank!")
                return@launch
            }

            withContext(provider.main) { showLoading() }

            val result = repository.wxLogin(code)
            withContext(provider.main) {
                if (result is Result.Success) {
                    var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                    wxCode = ""
                    emitUiState(showSuccess = result.result?.result, enableLoginButton = true)
                } else if (result is Result.Error) {
                    emitUiState(showError = result.exception.message, enableLoginButton = true)
                }
            }
        }
    }

    private fun showLoading() {
        emitUiState(true)
    }

    private fun emitUiState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: User? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = LoginUiModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _uiState.value = uiModel
    }

    data class LoginUiModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: User?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )


    private val _updateState = MutableLiveData<UpdateUiModel>()
    val updateState: LiveData<UpdateUiModel>
        get() = _updateState

    fun checkUpdate() {
        emitUpdateState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.checkAppUpdate()

            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitUpdateState(false, null, result.result.result)
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
                                emitUpdateState(false, "失败!${result.result.message}", null, false, true)
                            }
                            else -> {
                                emitUpdateState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitUpdateState(false, "App更新请求失败")
                }
            }
        }
    }

    private fun emitUpdateState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: UpdateAppBean? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = UpdateUiModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _updateState.value = uiModel
    }

    data class UpdateUiModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: UpdateAppBean?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )
}
