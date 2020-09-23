package com.jzz.treasureship.ui.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.AddressPageList
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.CityPlace
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.model.repository.AddressRepository
import com.jzz.treasureship.ui.upAddressRequest
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressViewModel(val repository: AddressRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {

    private val _cityPlacesState = MutableLiveData<CityPlacesModel>()
    val cityPlacesState: LiveData<CityPlacesModel>
        get() = _cityPlacesState

    fun getCityPlaces() {
        emitCityPlacesState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getPlaces()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitCityPlacesState(false, null, result.result.result)
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
                            emitCityPlacesState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitCityPlacesState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitCityPlacesState(false, "获取城市列表失败")
            }
        }
    }

    //所有城市
    private fun emitCityPlacesState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: CityPlaces? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = CityPlacesModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _cityPlacesState.value = uiModel
    }


    //所有城市
    data class CityPlacesModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: CityPlaces?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _AddressState = MutableLiveData<Model>()
    val addressState: LiveData<Model>
        get() = _AddressState

    fun addAddress(
        name: String,
        mobile: String,
        detailAddress: String,
        topCityPlace: CityPlace,
        midCityPlace: CityPlace,
        lastCityPlace: CityPlace,
        isDefault: Int,
        phone: String = ""
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            if (name.isBlank() ||
                mobile.isBlank() ||
                detailAddress.isBlank()
            ) {
                ToastUtils.showShort("有必要的值未填写")
                return@launch
            }
            val result =
                repository.addPayPlace(
                    detailAddress,
                    name,
                    mobile,
                    topCityPlace,
                    midCityPlace,
                    lastCityPlace,
                    isDefault,
                    phone
                )
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitState(false, null, "${result.result.result}")
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
                            emitState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitState(false, "收货地址操作失败")
            }
        }
    }
val addressRusult = MutableLiveData<Boolean>()
    fun updateAddress(addressRequest:upAddressRequest) {

        launchTask {
            HttpHelp.getRetrofit().updatePayAddress(BaseRequestBody(addressRequest)).resultCheck({
                addressRusult.postValue(true)
            })
        }

    }

    fun setAddressDefault(id: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val result =
                repository.setAddressDefault(id)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitState(false, null, "${result.result.result}")
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
                            emitState(
                                false,
                                "失败!${result.result.message}",
                                "失败!${result.result.message}",
                                false,
                                false,
                                true
                            )
                        }
                        else -> {
                            emitState(false, "失败!${result.result?.message}", "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitState(false, "操作失败")
            }
        }
    }

    //添加/删除地址状态
    private fun emitState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = Model(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _AddressState.value = uiModel
    }


    //添加/删除地址状态
    data class Model(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )


    private val _addressPageListState = MutableLiveData<AddressModel>()
    val addressPageListState: LiveData<AddressModel>
        get() = _addressPageListState

    fun getAddressPageList(pageNum: Int = 1) {
        emitAddressPageListState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getAddressPageList(pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitAddressPageListState(false, null, result.result.result)
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
                            emitAddressPageListState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitAddressPageListState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitAddressPageListState(false, "分页获取收货地址失败")
            }
        }
    }

    //分页获取收货地址
    private fun emitAddressPageListState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: AddressPageList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = AddressModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _addressPageListState.value = uiModel
    }


    //分页获取收货地址
    data class AddressModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: AddressPageList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _DelAddressState = MutableLiveData<DelAddressModel>()
    val delAddressState: LiveData<DelAddressModel>
        get() = _DelAddressState

    //收货地址删除
    fun delAddress(addressId: Int) {
        emitDelAddressPageListState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.delAddress(addressId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitDelAddressPageListState(false, null, "${result.result.result}")
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
                            emitDelAddressPageListState(
                                false,
                                "失败!${result.result.message}",
                                "失败!${result.result.message}",
                                false,
                                false,
                                true
                            )
                        }
                        else -> {
                            emitDelAddressPageListState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitDelAddressPageListState(false, "分页获取收货地址失败")
            }
        }
    }

    //删除地址
    private fun emitDelAddressPageListState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = DelAddressModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _DelAddressState.value = uiModel
    }


    //删除地址
    data class DelAddressModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}
