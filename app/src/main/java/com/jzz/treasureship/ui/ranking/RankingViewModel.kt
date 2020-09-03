package com.jzz.treasureship.ui.ranking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.Rank
import com.jzz.treasureship.model.repository.RankingRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RankingViewModel(val repository: RankingRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {
    private val _rankState = MutableLiveData<RankModel>()
    val rankState: LiveData<RankModel>
        get() = _rankState

    //获取排行榜
    fun getRankList(time: String) {
        emitRankUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getRankingList(time)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitRankUiState(false, null, result.result.result)
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
                            emitRankUiState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitRankUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitRankUiState(false, "排行榜请求失败")
            }
        }
    }

    private fun emitRankUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Rank? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = RankModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _rankState.value = uiModel
    }

    data class RankModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Rank?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}