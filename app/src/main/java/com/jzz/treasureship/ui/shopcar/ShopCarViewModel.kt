package com.jzz.treasureship.ui.shopcar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.CartList
import com.jzz.treasureship.model.bean.HotSearch
import com.jzz.treasureship.model.repository.GoodsCartRepository
import com.jzz.treasureship.ui.search.SearchViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopCarViewModel(val repository: GoodsCartRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {


    private val _CartsUiState = MutableLiveData<CartModel>()
    val cartsUiState: LiveData<CartModel>
        get() = _CartsUiState

    //获取购物车列表
    fun getCartList() {
        emitCartUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getCartList()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitCartUiState(false, null, result.result.result)
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
                            emitCartUiState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitCartUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitCartUiState(false, "购物车商品列表请求失败")
            }
        }
    }

    fun deleteGoods(cartId: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.delGoods(cartId)
            if (result is Result.Success) {
                getCartList()
            } else {
                ToastUtils.showShort("商品删除失败")
            }

        }
    }

    private fun emitCartUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: CartList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = CartModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _CartsUiState.value = uiModel
    }

    data class CartModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: CartList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}
