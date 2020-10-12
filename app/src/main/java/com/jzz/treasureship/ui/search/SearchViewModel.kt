package com.jzz.treasureship.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.Brand
import com.jzz.treasureship.model.bean.HotSearch
import com.jzz.treasureship.model.bean.Illness
import com.jzz.treasureship.model.bean.VideoPageList
import com.jzz.treasureship.model.repository.HomeSearchRepository
import com.jzz.treasureship.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(val repository: HomeSearchRepository, val provider: CoroutinesDispatcherProvider) :
    BaseViewModel() {

    //品牌
    private val _brandUiState = MutableLiveData<BrandModel>()
    val brandUiState: LiveData<BrandModel>
        get() = _brandUiState

    //病症
    private val _illnessUiState = MutableLiveData<IllnewssModel>()
    val illnessUiState: LiveData<IllnewssModel>
        get() = _illnessUiState

    //热搜
    private val _hotSearchUiState = MutableLiveData<HotSearchModel>()
    val hotSearchUiState: LiveData<HotSearchModel>
        get() = _hotSearchUiState

    //获取品牌列表
    fun getBrandList() {
        emitBrandUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getBrandList()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitBrandUiState(false, null, result.result.result)
                } else {
                    emitBrandUiState(false, "失败!${result.result?.message}")
                }
            } else {
                emitBrandUiState(false, "品牌列表请求失败")
            }
        }
    }

    //获取病症列表
    fun getIllnessList() {
        emitBrandUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getIllnessList()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitIllnessUiState(false, null, result.result.result)
                } else {
                    emitIllnessUiState(false, "失败!${result.result?.message}")
                }
            } else {
                emitIllnessUiState(false, "病症列表请求失败")
            }
        }
    }

    //获取热搜列表
    fun getHotSearchList() {
        emitHotSearchUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getHotSearchList()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitHotSearchUiState(false, null, result.result.result)
                } else {
                    emitHotSearchUiState(false, "失败!${result.result?.message}")
                }
            } else {
                emitHotSearchUiState(false, "热搜列表请求失败")
            }
        }
    }


    private val _searchState = MutableLiveData<SearchUiModel>()
    val searchState: LiveData<SearchUiModel>
        get() = _searchState

    fun getSearchPageList(str: String, what: Int, type: Int,pageNum:Int = 1) {
        emitSearchUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getSearchPageList(str, what, type,pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitSearchUiState(false, null, result.result.result)
                } else {
                    emitSearchUiState(false, "失败!${result.result?.message}")
                }
            } else {
                emitSearchUiState(false, "搜索请求失败")
            }
        }
    }

    private fun emitSearchUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: VideoPageList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = SearchUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _searchState.value = uiModel
    }


    //视频列表
    data class SearchUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: VideoPageList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private fun emitBrandUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ArrayList<Brand>? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = BrandModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _brandUiState.value = uiModel
    }

    data class BrandModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: ArrayList<Brand>?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    data class IllnewssModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: ArrayList<Illness>?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private fun emitIllnessUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ArrayList<Illness>? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = IllnewssModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _illnessUiState.value = uiModel
    }

    private fun emitHotSearchUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ArrayList<HotSearch>? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = HotSearchModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _hotSearchUiState.value = uiModel
    }

    data class HotSearchModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: ArrayList<HotSearch>?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}