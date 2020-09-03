package com.jzz.treasureship.ui.paypal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.*
import com.jzz.treasureship.model.repository.PaypalRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PaypalViewModel(val repository: PaypalRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {

    private val _payAddressUiState = MutableLiveData<PayAddressUiModel>()
    val payAddressUiState: LiveData<PayAddressUiModel>
        get() = _payAddressUiState

    //获取默认地址
    fun getPayAddress() {
        emitAddressUiState(true)
        viewModelScope.launch(provider.computation) {
            val result = repository.getPayAddress()

            withContext(provider.main) {
                if (result is Result.Success) {
                    if (result.result?.code == 200) {
                        emitAddressUiState(false, null, result.result.result)
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
                                emitAddressUiState(false, "失败!${result.result?.message}", null, false, false, true)
                            }
                            else -> {
                                emitAddressUiState(false, "失败!${result.result?.message}")
                            }
                        }
                    }
                } else {
                    emitAddressUiState(false, "默认地址请求失败")
                }
            }
        }
    }

    //收货地址
    private fun emitAddressUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Address? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = PayAddressUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _payAddressUiState.value = uiModel
    }

    //收货地址
    data class PayAddressUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Address?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _orderState = MutableLiveData<OrderModel>()
    val orderState: LiveData<OrderModel>
        get() = _orderState

    fun createOrder(body: JSONObject) {
        emitOrderState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.createOrder(body)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOrderState(false, null, result.result.result)
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
                            emitOrderState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitOrderState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOrderState(false, "订单创建失败")
            }
        }
    }

    //订单
    private fun emitOrderState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Order? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = OrderModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _orderState.value = uiModel
    }


    //订单
    data class OrderModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Order?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    fun directBuy(count: Int, skuId: Int) {
        emitDirectBuyUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getDirectBuy(count, skuId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitDirectBuyUiState(false, null, result.result.result)
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
                            emitDirectBuyUiState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitDirectBuyUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitDirectBuyUiState(false, "立即购买请求失败")
            }
        }
    }

    private val _directBuyUiState = MutableLiveData<DirectBuyUiModel>()
    val directBuyUiState: LiveData<DirectBuyUiModel>
        get() = _directBuyUiState

    //直接购
    private fun emitDirectBuyUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ShopcarBuyBean? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = DirectBuyUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _directBuyUiState.value = uiModel
    }

    //直接购
    data class DirectBuyUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: ShopcarBuyBean?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    //购物车结算
    fun cartSettlement(body: String) {
        emitDirectBuyUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.cartSettlement(body)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitDirectBuyUiState(false, null, result.result.result)
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
                            emitDirectBuyUiState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitDirectBuyUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitDirectBuyUiState(false, "购物车购买请求失败")
            }
        }
    }


    private val _addAdviceUiState = MutableLiveData<AdviceUiModel>()
    val addAdviceUiState: LiveData<AdviceUiModel>
        get() = _addAdviceUiState

    fun addAdvice(advice: String, orderNo: String) {
        emitAdviceUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.addDoctorAdvice(advice, orderNo)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitAdviceUiState(false, null, result.result.result)
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
                            emitAdviceUiState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitAdviceUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitDirectBuyUiState(false, "购物车购买请求失败")
            }
        }
    }

    //添加医嘱
    private fun emitAdviceUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: AddAdviceResBean? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = AdviceUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _addAdviceUiState.value = uiModel
    }

    //添加医嘱
    data class AdviceUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: AddAdviceResBean?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _agreementUiState = MutableLiveData<AgreementUiModel>()
    val agreementUiState: LiveData<AgreementUiModel>
        get() = _agreementUiState


    fun getDocContent(orderChildId: String, key: String = "contract") {
        emitAgreementUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getDocContent(orderChildId, key)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitAgreementUiState(false, null, result.result.result)
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
                            emitAgreementUiState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitAgreementUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitAgreementUiState(false, "合同内容请求失败")
            }
        }
    }

    //合同内容
    private fun emitAgreementUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Agreement? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = AgreementUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _agreementUiState.value = uiModel
    }

    //合同内容
    data class AgreementUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Agreement?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _orderStatusState = MutableLiveData<OrderStatusModel>()
    val orderStatusState: LiveData<OrderStatusModel>
        get() = _orderStatusState

    //查询订单状态
    fun queryOrderStatus(orderId: Int) {
        emitOrderStatusState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.queryOrderStatus(orderId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOrderStatusState(false, null, result.result.result)
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
                            emitOrderStatusState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitOrderStatusState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOrderStatusState(false, "订单状态获取失败")
            }
        }
    }

    private fun emitOrderStatusState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Data? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = OrderStatusModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _orderStatusState.value = uiModel
    }


    data class OrderStatusModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Data?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}
