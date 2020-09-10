package com.jzz.treasureship.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.*
import com.jzz.treasureship.model.repository.UserRepository
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.out
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class UserViewModel(val repository: UserRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {
    private val _UserState = MutableLiveData<UserModel>()
    val userState: LiveData<UserModel>
        get() = _UserState

    //获取用户信息
    fun getUserInfo() {
        emitUserUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getUserInfo()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitUserUiState(false, null, result.result.result)
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
    val userType = MutableLiveData<UserAuthTypeBean>()
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

    private val _userBndState = MutableLiveData<UserModel>()
    val userBindState: LiveData<UserModel>
        get() = _userBndState

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
                                wxCode =""
                                userInfo=""
                                access=""
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
        _userBndState.value = uiModel
    }

    private val _modifiedInfoState = MutableLiveData<ModifiedInfoModel>()
    val modifiedInfoState: LiveData<ModifiedInfoModel>
        get() = _modifiedInfoState

    fun modifiedInfo(avatar: String?, nickName: String?) {
        emitModifiedInfoState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.modifiedInfo(avatar, nickName)
            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitModifiedInfoState(false, null, "${result.result.result}")
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
                                emitModifiedInfoState(false, "失败!${result.result.message}", null, false, false)
                            }
                            else -> {
                                emitModifiedInfoState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitModifiedInfoState(false, "修改昵称请求失败")
                }
            }
        }
    }

    private fun emitModifiedInfoState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = ModifiedInfoModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _modifiedInfoState.value = uiModel
    }

    data class ModifiedInfoModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )

    //上传图片
    private val _uploadImgState = MutableLiveData<uploadImgModel>()
    val uploadImgState: LiveData<uploadImgModel>
        get() = _uploadImgState

    fun uploadImg(img: File) {
        emitUploadImgState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.uploadImg(img)
            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitUploadImgState(false, null, result.result.result)
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
                                emitUploadImgState(false, "失败!${result.result.message}", null, false, false)
                            }
                            else -> {
                                emitUploadImgState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitUploadImgState(false, "图片上传请求失败")
                }
            }
        }
    }

    private fun emitUploadImgState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: UploadImgBean? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = uploadImgModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _uploadImgState.value = uiModel
    }

    data class uploadImgModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: UploadImgBean?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )

    //上传图片
    private val _qualificationState = MutableLiveData<saveQualificationModel>()
    val qualificationState: LiveData<saveQualificationModel>
        get() = _qualificationState

    fun saveQualification(
        personalImg: String,
        qualificationCertificateImg: String
    ) {
        emitQualificationState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.saveQualification(personalImg, qualificationCertificateImg)
            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitQualificationState(false, null, "${result.result.result}")
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
                                emitQualificationState(false, "失败!${result.result.message}", "失败!${result.result.message}", false, false)
                            }
                            else -> {
                                emitQualificationState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitQualificationState(false, "图片上传请求失败")
                }
            }
        }
    }

    fun getType(){
        "开始请求了".out(true)
        launchTask {

             HttpHelp.getRetrofit().getAuthType(BaseRequestBody())
                 .apply {
                     if(success)
                     {
                         userType.value = this.result
                     }
                     else{
                         mStateLiveData.value = ErrorState("请求错误")
                     }
                 }


        }


    }

    private fun emitQualificationState(
        showProgress: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        enableLoginButton: Boolean = false,
        needLogin: Boolean = false
    ) {
        val uiModel = saveQualificationModel(showProgress, showError, showSuccess, enableLoginButton, needLogin)
        _qualificationState.value = uiModel
    }

    data class saveQualificationModel(
        val showProgress: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val enableLoginButton: Boolean,
        val needLogin: Boolean
    )
}
