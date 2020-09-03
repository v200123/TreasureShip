package com.jzz.treasureship.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.*
import com.jzz.treasureship.model.repository.OrdersRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrdersViewModel(val repository: OrdersRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {

    private val _ordersState = MutableLiveData<OrdersModel>()
    val ordersState: LiveData<OrdersModel>
        get() = _ordersState

    //获取订单列表或客户详情
    fun getOrderList(
        memberId: Int?,
        orderNo: String?,
        orderStatus: Int?,
        orderType: Int?, pageNum: Int = 1
    ) {
        emitOrdersState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getOrderList(memberId, orderNo, orderStatus, orderType, pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOrdersState(false, null, result.result.result)
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
                            emitOrdersGoPayState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitOrdersGoPayState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOrdersState(false, "订单列表获取失败")
            }
        }
    }

    //订单列表或客户详情
    private fun emitOrdersState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Orders? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = OrdersModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _ordersState.value = uiModel
    }


    //订单列表或客户详情
    data class OrdersModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Orders?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _ordersGoPayState = MutableLiveData<OrdersGoPayModel>()
    val ordersGoPayState: LiveData<OrdersGoPayModel>
        get() = _ordersGoPayState

    //订单支付（微信支付）
    fun payByOrder(ordersId: Int) {
        emitOrdersGoPayState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.payByOrder(ordersId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOrdersGoPayState(false, null, result.result.result)
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
                            emitOrdersGoPayState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitOrdersGoPayState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOrdersGoPayState(false, "订单列表获取失败")
            }
        }
    }

    private fun emitOrdersGoPayState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: OrdersGoPayBean? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = OrdersGoPayModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _ordersGoPayState.value = uiModel
    }


    data class OrdersGoPayModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: OrdersGoPayBean?,
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


    //确认收货
    private val _confirmReceiveState = MutableLiveData<ComfirmReceiveModel>()
    val comfirmReceiveState: LiveData<ComfirmReceiveModel>
        get() = _confirmReceiveState

    //查询订单状态
    fun sureReceice(orderId: Int) {
        emitComfirmReceiveState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.sureReceive(orderId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitComfirmReceiveState(false, null, result.result.result)
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
                            emitComfirmReceiveState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitComfirmReceiveState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitComfirmReceiveState(false, "订单状态获取失败")
            }
        }
    }

    private fun emitComfirmReceiveState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Order? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = ComfirmReceiveModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _confirmReceiveState.value = uiModel
    }


    data class ComfirmReceiveModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Order?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    //确认收货
    private val _noticeState = MutableLiveData<NoticeReceiveModel>()
    val noticeState: LiveData<NoticeReceiveModel>
        get() = _noticeState

    //设置或取消提醒
    fun setNotice(memberId: Int, notice: Int, noticeTime: String) {
        emitNoticeReceiveState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.setNotice(memberId, notice, noticeTime)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitNoticeReceiveState(false, null, result.result.result)
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
                            emitNoticeReceiveState(false, "失败!${result.result?.message}", null, false, false, true)
                        }
                        else -> {
                            emitNoticeReceiveState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitNoticeReceiveState(false, "订单状态获取失败")
            }
        }
    }

    private fun emitNoticeReceiveState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Unit? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = NoticeReceiveModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _noticeState.value = uiModel
    }


    data class NoticeReceiveModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Unit?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    //确认收货
    private val _refundState = MutableLiveData<RefundModel>()
    val refundState: LiveData<RefundModel>
        get() = _refundState

    //设置或取消提醒
    fun askRefund(orderNo: String,refundReason:String = "") {
        emitRefundState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.askRefund(orderNo,refundReason)
            if (result is Result.Success) {
                if (result.result!!.code == 200) {
                    emitRefundState(false, null, result.result.result)
                } else {
                    when (result.result.code) {
                        401 -> {
                            var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                            var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                            var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                            var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                            login = false
                            wxCode =""
                            userInfo=""
                            access=""
                            emitRefundState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitRefundState(false, "失败!${result.result.message}")
                        }
                    }
                }
            } else {
                emitRefundState(false, "订单状态获取失败")
            }
        }
    }

    private fun emitRefundState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Refund? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = RefundModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _refundState.value = uiModel
    }


    data class RefundModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Refund?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}
