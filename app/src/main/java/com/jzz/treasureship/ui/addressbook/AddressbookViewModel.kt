package com.jzz.treasureship.ui.addressbook

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.Contacter
import com.jzz.treasureship.model.bean.ContacterGoods
import com.jzz.treasureship.model.bean.Data
import com.jzz.treasureship.model.bean.DataXX
import com.jzz.treasureship.model.repository.AddressBookRepository
import com.jzz.treasureship.model.repository.AddressRepository
import com.jzz.treasureship.model.repository.OrdersRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressbookViewModel(val repository: AddressBookRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {

    private val _membersState = MutableLiveData<MembersModel>()
    val membersState: LiveData<MembersModel>
        get() = _membersState

    //获取通讯录列表
    /**
     * buyNumOrder:购买数量排序 1--升序 ， 2--降序
     * buyTimeOrder:购买时间排序 1--升序 ， 2--降序
     * goodsId: 商品ID
     * name: 客户名称
     */
    fun getMemberList(
        buyNumOrder: Int = 0,
        buyTimeOrder: Int = 0,
        goodsID: Int = -1,
        name: String? = null,
        pageNumber: Int = 1
    ) {
        emitMembersStatusState(true)
        viewModelScope.launch(Dispatchers.Main) {
            Log.d("requestMembers", "${buyNumOrder},${buyTimeOrder},${name}")
            val result = repository.getMemberList(buyNumOrder, buyTimeOrder, goodsID, name, pageNumber)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitMembersStatusState(false, null, result.result.result)
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
                            emitMembersStatusState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitMembersStatusState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitMembersStatusState(false, "订单状态获取失败")
            }
        }
    }

    private fun emitMembersStatusState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Contacter? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = MembersModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _membersState.value = uiModel
    }


    data class MembersModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Contacter?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _goodsState = MutableLiveData<GoodsModel>()
    val goodsState: LiveData<GoodsModel>
        get() = _goodsState

    //获取产品列表
    fun getGoodsList(pageNumber: Int = 1) {
        emitGoodsState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getGoodsList()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitGoodsState(false, null, result.result.result)
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
                            emitGoodsState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitGoodsState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitGoodsState(false, "订单状态获取失败")
            }
        }
    }

    private fun emitGoodsState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ContacterGoods? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = GoodsModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _goodsState.value = uiModel
    }


    data class GoodsModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: ContacterGoods?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _noticeState = MutableLiveData<NoticeModel>()
    val noticeState: LiveData<NoticeModel>
        get() = _noticeState

    //设置/取消提醒
    fun setNotice(
        memberId: Int,
        notice: Int,
        noticeTime: String
    ) {
        emitNoticeState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.setNotice(memberId, notice, noticeTime)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitNoticeState(false, null, result.result.result)
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
                            emitNoticeState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitNoticeState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitNoticeState(false, "失败")
            }
        }
    }

    private fun emitNoticeState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Unit? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = NoticeModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _noticeState.value = uiModel
    }


    data class NoticeModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Unit?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}