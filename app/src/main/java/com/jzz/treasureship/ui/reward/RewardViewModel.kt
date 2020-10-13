package com.jzz.treasureship.ui.reward

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.Rank
import com.jzz.treasureship.model.bean.Reward
import com.jzz.treasureship.model.repository.RankingRepository
import com.jzz.treasureship.model.repository.RewardRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RewardViewModel(val repository: RewardRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {
    private val _rewardState = MutableLiveData<RewardModel>()
    val rewardState: LiveData<RewardModel>
        get() = _rewardState

    //领取红包
    fun getReward() {
        emitRewardUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getReward()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitRewardUiState(false, null, result.result.result)
                } else {
                    when (result.result?.code) {
                        401 -> {
                            emitRewardUiState(false, "失败!${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitRewardUiState(false, "失败!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitRewardUiState(false, "排行榜请求失败")
            }
        }
    }

    private fun emitRewardUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Reward? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = RewardModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _rewardState.value = uiModel
    }

    data class RewardModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Reward?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}