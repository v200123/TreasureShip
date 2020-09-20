package com.jzz.treasureship.ui.invite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.InvitedList
import com.jzz.treasureship.model.repository.InviteRespository
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ErrorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InviteViewModel(val repository: InviteRespository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {
    private val _invitedsState = MutableLiveData<InvitedModel>()
    val invitedsState: LiveData<InvitedModel>
        get() = _invitedsState

    //余额
    fun getInvitedList(pageNum: Int = 1) {
        emitInvitedListState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getInvitedList(pageNum)
            if (result is Result.Success) {
                if (result.result!!.code == 200) {
                    emitInvitedListState(false, null, result.result.result)
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
                            emitInvitedListState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitInvitedListState(false, "失败!${result.result.message}")
                        }
                    }
                }
            } else {
                emitInvitedListState(false, "邀请列表获取失败")
            }
        }
    }
    val redEnvelopOpen = MutableLiveData<JsonObject>()
    fun getMoney()
    {
        launchTask { val inviteMoney = HttpHelp.getRetrofit().getInviteMoney()
        inviteMoney.resultCheck({

            it?.let {
                redEnvelopOpen.postValue(it)
            }

        },{ErrorState(it)})}
    }

    //邀请明细
    private fun emitInvitedListState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: InvitedList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = InvitedModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _invitedsState.value = uiModel
    }


    //余额
    data class InvitedModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: InvitedList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    val mCountData = MutableLiveData<Int>()
    fun getCount(){
        launchTask{
            val inviteCount = HttpHelp.getRetrofit().getInviteCount()
            inviteCount.resultCheck({
                mCountData.postValue( it!!.get("inviteRewardCount").asInt)
            },{
                mStateLiveData.postValue(ErrorState(it))
            })
        }
    }

}