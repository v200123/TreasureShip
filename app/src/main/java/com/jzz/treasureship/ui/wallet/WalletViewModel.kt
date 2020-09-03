package com.jzz.treasureship.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.Orders
import com.jzz.treasureship.model.bean.WalletBalance
import com.jzz.treasureship.model.bean.WalletList
import com.jzz.treasureship.model.repository.WalletRespository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalletViewModel(val repository: WalletRespository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {

    private val _balanceState = MutableLiveData<WalletBalanceModel>()
    val balanceState: LiveData<WalletBalanceModel>
        get() = _balanceState

    //余额
    fun getBalance() {
        emitBalanceState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getBalance()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitBalanceState(false, null, result.result.result)
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
                            emitBalanceState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitBalanceState(false, "失败!${result.result!!.message}")
                        }
                    }
                }
            } else {
                emitBalanceState(false, "钱包余额获取失败")
            }
        }
    }

    //余额
    private fun emitBalanceState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: WalletBalance? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = WalletBalanceModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _balanceState.value = uiModel
    }


    //余额
    data class WalletBalanceModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: WalletBalance?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )


    private val _balanceListState = MutableLiveData<WalletListModel>()
    val balanceListState: LiveData<WalletListModel>
        get() = _balanceListState

    //余额
    fun getBalanceList(accountType: Int = -1,pageNum:Int = 1) {
        emitWalletListState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getBalanceList(accountType,pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitWalletListState(false, null, result.result.result)
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
                            emitWalletListState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitWalletListState(false, "失败!${result.result!!.message}")
                        }
                    }
                }
            } else {
                emitWalletListState(false, "钱包余额获取失败")
            }
        }
    }

    //余额
    private fun emitWalletListState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: WalletList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = WalletListModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _balanceListState.value = uiModel
    }

    //列表
    data class WalletListModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: WalletList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}