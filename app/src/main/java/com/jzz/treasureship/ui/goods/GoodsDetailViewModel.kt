package com.jzz.treasureship.ui.goods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.DirectbuyBean
import com.jzz.treasureship.model.bean.GoodsDetail
import com.jzz.treasureship.model.bean.ShopcarBuyBean
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.model.repository.GoodsRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoodsDetailViewModel(val repository: GoodsRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {
    private var SELECTED_SKU by PreferenceUtils(PreferenceUtils.SELECTED_SKU,"")

    private val _uiState = MutableLiveData<GoodsDetailModel>()
    val uiState: LiveData<GoodsDetailModel>
        get() = _uiState

    private val _addCartState = MutableLiveData<AddCartModel>()
    val addCartState: LiveData<AddCartModel>
        get() = _addCartState

    private val _CartNumUiState = MutableLiveData<CarNumUiModel>()
    val cartNumUiState: LiveData<CarNumUiModel>
        get() = _CartNumUiState

    fun getGoodsDetail(goodsId: Int) {
        emitGoodsDetailState(true)
        viewModelScope.launch(Dispatchers.Main) {
            emitGoodsDetailState(true)
            val result = repository.getGoodsDetail(goodsId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitGoodsDetailState(false, null, result.result.result)
                } else {
                    emitGoodsDetailState(false, "商品详情请求失败!${result.result?.code}")
                }
            } else {
                emitGoodsDetailState(false, "商品详情请求失败")
            }
        }
    }

    private fun emitGoodsDetailState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: GoodsDetail? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = GoodsDetailModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _uiState.value = uiModel
    }


    //商品详情
    data class GoodsDetailModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: GoodsDetail?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    fun addCart(count: Int, skuId: Int) {
        emitAddCartState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.addCart(count, skuId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitAddCartState(false, null, result.result.result.toString())
                    SELECTED_SKU = ""
                    this@GoodsDetailViewModel.getCount()
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
                            emitAddCartState(false, "失败!${result.result.message}", "失败!${result.result.message}", false, false, true)
                        }
                        else -> {
                            emitAddCartState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitAddCartState(false, "添加商品到购物车失败！")
            }
        }
    }

    private fun emitAddCartState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = AddCartModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _addCartState.value = uiModel
    }


    //商品详情
    data class AddCartModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    fun getCount() {
        emitCartNumUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getCartCount()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitCartNumUiState(false, null, result.result.result)
                } else {
                    emitCartNumUiState(false, "失败!${result.result?.message}")
                }
            } else {
                emitCartNumUiState(false, "购物车商品数量请求失败")
            }
        }
    }

    //购物车商品数量
    private fun emitCartNumUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Int? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = CarNumUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _CartNumUiState.value = uiModel
    }

    //购物车商品数量
    data class CarNumUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Int?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _directBuyUiState = MutableLiveData<DirectBuyUiModel>()
    val directBuyUiState: LiveData<DirectBuyUiModel>
        get() = _directBuyUiState

    fun directBuy(count: Int,skuId: Int){
        emitDirectBuyUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getDirectBuy(count,skuId)
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
                            wxCode =""
                            userInfo=""
                            access=""
                            emitDirectBuyUiState(false, "失败!${result.result.message}", null, false, false, true)
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
}
