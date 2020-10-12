package com.jzz.treasureship.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.bean.*
import com.jzz.treasureship.model.repository.HomeRepository
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(val repository: HomeRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel() {


    private val _uiState = MutableLiveData<HomeUiModel>()
    val uiState: LiveData<HomeUiModel>
        get() = _uiState

    private val _collectUiState = MutableLiveData<CollectUiModel>()
    val collectUiState: LiveData<CollectUiModel>
        get() = _collectUiState

    private val _CartNumUiState = MutableLiveData<CarNumUiModel>()
    val cartNumUiState: LiveData<CarNumUiModel>
        get() = _CartNumUiState

    //获取推荐视频
    fun getRecommendVideoList(pageNum: Int) {
        emitHomeUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getPageList(1, pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitHomeUiState(false, null, result.result.result)
                } else {
                    emitHomeUiState(false, "${result.result?.message}")
                }
            } else {
                emitHomeUiState(false, "推荐列表请求失败")
            }
        }
    }

    //获取最新视频
    fun getNewestVideoList(pageNum: Int) {
        emitHomeUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getPageList(0, pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitHomeUiState(false, null, result.result.result)
                } else {
                    emitHomeUiState(false, "${result.result?.message}")
                }
            } else {
                emitHomeUiState(false, "最新列表请求失败")
            }
        }
    }

    fun getVideoList(tabID: Int, pageNum: Int) {
        emitHomeUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getPageList(tabID, pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitHomeUiState(false, null, result.result.result)
                } else {
                    emitHomeUiState(false, "${result.result?.message}")
                }
            } else {
                emitHomeUiState(false, "视频列表请求失败")
            }
        }
    }

    //获取评论列表
    fun getCommentList(commentId: Int, videoId: Int) {
        emitCommentsUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getCommentPageList(commentId, videoId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitCommentsUiState(false, null, result.result.result)
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
                            emitCommentsUiState(false, null, null, false, false, true)
                        }
                        else -> {
                            emitCommentsUiState(false, "failed!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitCommentsUiState(false, "评论列表请求失败")
            }
        }
    }

    //获取收藏分类
    fun getCollectCategory() {
        emitCollectUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getCollectCategory()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitCollectUiState(false, null, result.result.result)
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
                            emitCollectUiState(false, null, null, false, false, true)
                        }
                        else -> {
                            emitCollectUiState(false, "failed!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitCollectUiState(false, "获取收藏分类失败")
            }
        }
    }

    //用户添加收藏分类
    fun addCollectCategory(categoryName: String) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.addCollectCategory(categoryName)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOperateUiState(false, null, "添加收藏分类成功")
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
                            emitOperateUiState(
                                false,
                                "${result.result.message}",
                                "${result.result.message}",
                                false,
                                false,
                                true
                            )
                        }
                        else -> {
                            emitOperateUiState(false, "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOperateUiState(false, "添加收藏分类失败")
            }
        }
    }

    //将视频添加到收藏标签内
    fun addCollect(categoryId: Int, remark: String, videoId: Int) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.addCollect(categoryId, remark, videoId)

            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    Log.d("getCollectCategory", result.toString())
                    emitOperateUiState(false, null, "视频收藏成功")
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
                            emitOperateUiState(false, "${result.result.message}", "null", false, false, true)
                        }
                        else -> {
                            emitOperateUiState(false, "${result.result?.message}", "null")
                        }
                    }
                }
            } else {
                emitOperateUiState(false, "视频收藏失败", "")
            }
        }
    }

    //根据收藏id获取视频
    fun getCollectVideoList(categoryId: Int, remark: String? = null, videoId: Int? = -1, pageNum: Int) {
        emitHomeUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getCollectVideoList(categoryId, remark, videoId, pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitHomeUiState(false, null, result.result.result)
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
                            emitHomeUiState(false, "${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitHomeUiState(false, "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitHomeUiState(false, "收藏列表视频请求失败")
            }
        }
    }

    //购物车内商品数量
    fun getCount() {
        emitCartNumUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getCartCount()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitCartNumUiState(false, null, result.result.result)
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
                            emitCartNumUiState(false, null, null, false, false, true)
                        }
                        else -> {
                            emitCartNumUiState(false, "failed!${result.result?.message}")
                        }
                    }
                }
            } else {
                emitCartNumUiState(false, "购物车商品数量请求失败")
            }
        }
    }

    //评论点赞
    fun addPraise(commentId: Int, videoId: Int) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.addPraise(commentId, videoId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOperateUiState(false, null, result.result.message)
                } else {
                    emitOperateUiState(false, "${result.result?.message}")
                }
            } else {
                emitOperateUiState(false, "评论点赞请求失败")
            }
        }
    }

    //评论
    fun addComment(commentId: Int, content: String, videoId: Int) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.addComment(commentId, content, videoId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOperateUiState(false, null, "视频评论成功")
                } else {
                    emitOperateUiState(false, "${result.result?.message}")
                }
            } else {
                emitOperateUiState(false, "评论点赞请求失败")
            }
        }
    }

    private fun emitHomeUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: VideoPageList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = HomeUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _uiState.value = uiModel
    }


    //视频列表
    data class HomeUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: VideoPageList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    //收藏标签列表
    private fun emitCollectUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ArrayList<CollectCategory>? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = CollectUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _collectUiState.value = uiModel
    }

    //收藏标签列表
    data class CollectUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: ArrayList<CollectCategory>?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

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

    private val _operateUiState = MutableLiveData<operatetUiModel>()
    val operateUiState: LiveData<operatetUiModel>
        get() = _operateUiState

    //收藏操作（增、删、改）icon_wallet_back
    private fun emitOperateUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = operatetUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _operateUiState.value = uiModel
    }

    //收藏操作（增、删、改）icon_wallet_back
    data class operatetUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    //移动视频
    fun moveCollect(categoryId: Int, remark: String, videoId: Int) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.moveCollect(categoryId, remark, videoId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOperateUiState(false, null, "视频移动成功")
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
                            emitOperateUiState(
                                false,
                                "${result.result.message}",
                                null,
                                false,
                                false,
                                true
                            )
                        }
                        else -> {
                            emitOperateUiState(false, "${result.result?.message}", "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOperateUiState(false, "视频移动请求失败")
            }
        }
    }

    //将视频从收藏夹中删除
    fun delCollect(videoId: Int) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.cancelCollect(videoId)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOperateUiState(false, null, "视频取消收藏成功")
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
                            emitOperateUiState(
                                false,
                                "${result.result.message}",
                                "${result.result.message}",
                                false,
                                false,
                                true
                            )
                        }
                        else -> {
                            emitOperateUiState(false, "${result.result?.message}", "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOperateUiState(false, "删除视频请求失败")
            }
        }
    }

    private val _CommentsUiState = MutableLiveData<CommentsUiModel>()
    val commentsUiState: LiveData<CommentsUiModel>
        get() = _CommentsUiState

    //评论列表
    private fun emitCommentsUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: CommentPageList? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = CommentsUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _CommentsUiState.value = uiModel
    }

    //评论列表
    data class CommentsUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: CommentPageList?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _searchState = MutableLiveData<SearchUiModel>()
    val searchState: LiveData<SearchUiModel>
        get() = _searchState

    fun getSearchPageList(str: String, what: Int, type: Int, pageNum: Int = 1) {
        emitSearchUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getSearchPageList(str, what, type, pageNum)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitSearchUiState(false, null, result.result.result)
                } else {
                    emitSearchUiState(false, "${result.result?.message}")
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

    //意见反馈
    fun sendFeedback(content: String) {
        emitOperateUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.sendFeedback(content)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitOperateUiState(false, null, null)
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
                            emitOperateUiState(
                                false,
                                "${result.result.message}",
                                result.result.message,
                                false,
                                false,
                                true
                            )
                        }
                        else -> {
                            emitOperateUiState(false, "${result.result?.message}", "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitOperateUiState(false, "意见反馈请求失败")
            }
        }
    }

    private val _homeTabState = MutableLiveData<HomeTabUiModel>()
    val homeTabState: LiveData<HomeTabUiModel>
        get() = _homeTabState

    fun getHomeTabs() {
        emitHomeTabsUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getHomeTabs()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitHomeTabsUiState(false, null, result.result.result)
                } else {
                    emitHomeTabsUiState(false, "${result.result?.message}")
                }
            } else {
                emitHomeTabsUiState(false, "首页分类请求失败")
            }
        }
    }

    private fun emitHomeTabsUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: HomeTabBean? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = HomeTabUiModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _homeTabState.value = uiModel
    }


    //视频列表
    data class HomeTabUiModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: HomeTabBean?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _questionnarieState = MutableLiveData<QuestionarieModel>()
    val questionnarieState: LiveData<QuestionarieModel>
        get() = _questionnarieState

    //获取未答题目,同时可以获取到题库已经抢红包开始的时间
    fun getQuestionnaire() {
        emitQuestionsUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result: Result<JzzResponse<QuestionnaireResponseVo>> = repository.getQuestionnaire()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitQuestionsUiState(false, null, result.result.result)
                } else {
                    when (result.result?.code) {
                        401 -> {
                            emitQuestionsUiState(false, "${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitQuestionsUiState(false, "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitQuestionsUiState(false, "获取未答题目失败")
            }
        }
    }

    private fun emitQuestionsUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: QuestionnaireResponseVo? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = QuestionarieModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _questionnarieState.value = uiModel
    }

    data class QuestionarieModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: QuestionnaireResponseVo?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )

    private val _redEnvState = MutableLiveData<RedEnvModel>()
    val redEnvState: LiveData<RedEnvModel>
        get() = _redEnvState

    //提交问卷
    fun submitQuestionnaire(ans: String, id: Int) {
        emitRedEnvUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.submitQuestionnaire(ans, id)
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitRedEnvUiState(false, null, "成功!${result.result.result}")
                } else {
                    when (result.result?.code) {
                        401 -> {
                            emitRedEnvUiState(false, "${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitRedEnvUiState(false, "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitRedEnvUiState(false, "提交问卷失败")
            }
        }
    }

    private fun emitRedEnvUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: String? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = RedEnvModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _redEnvState.value = uiModel
    }

    data class RedEnvModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: String?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )


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
                            var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                            login = false
                            emitRewardUiState(false, "${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitRewardUiState(false, "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitRewardUiState(false, "提交问卷失败")
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

    private val _adState = MutableLiveData<AdBannerModel>()
    val adState: LiveData<AdBannerModel>
        get() = _adState

    //获取广告活动
    fun getAd() {
        emitAdUiState(true)
        viewModelScope.launch(Dispatchers.Main) {
            val result = repository.getAd()
            if (result is Result.Success) {
                if (result.result?.code == 200) {
                    emitAdUiState(false, null, result.result.result)
                } else {
                    when (result.result?.code) {
                        401 -> {
                            emitAdUiState(false, "${result.result.message}", null, false, false, true)
                        }
                        else -> {
                            emitAdUiState(false, "${result.result?.message}")
                        }
                    }
                }
            } else {
                emitAdUiState(false, "活动获取失败")
            }
        }
    }

    private fun emitAdUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: Ad? = null,
        showEnd: Boolean = false,
        isRefresh: Boolean = false,
        needLogin: Boolean? = null
    ) {
        val uiModel = AdBannerModel(showLoading, showError, showSuccess, showEnd, isRefresh, needLogin)
        _adState.value = uiModel
    }

    data class AdBannerModel(
        val showLoading: Boolean,
        val showError: String?,
        val showSuccess: Ad?,
        val showEnd: Boolean, // 加载更多
        val isRefresh: Boolean, // 刷新
        val needLogin: Boolean? = null
    )
}
