package com.jzz.treasureship.ui.search

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.BaseBindAdapter
import com.jzz.treasureship.adapter.CollectAdapter
import com.jzz.treasureship.adapter.CommentsAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.VideoData
import com.jzz.treasureship.ui.goods.GoodsDetailFragment
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search_results.*
import kotlinx.android.synthetic.main.video_layout_normal.view.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class SearchResultsFragment : BaseVMFragment<HomeViewModel>() {

    companion object {
        fun newInstance(searchWords: String, what: Int, type: Int): SearchResultsFragment {
            val f = SearchResultsFragment()
            val bundle = Bundle()
            bundle.putString("searchWords", searchWords)
            bundle.putInt("what", what)
            bundle.putInt("type", type)
            f.arguments = bundle
            return f
        }
    }

    override fun getLayoutResId() = R.layout.fragment_search_results

    override fun initVM(): HomeViewModel = getViewModel()

    private val mAdapter by lazy { SearchResultAdapter() }
    private val collectAdapter by lazy { CollectAdapter() }
    private val commentsAdapter by lazy { CommentsAdapter() }

    private var type: Int = 0
    private var what: Int = 2
    private var searchWords: String? = ""

    private var currentVideoID = -1
    private var currentPosition = -1

    private var pageNum = 1
    private val videoList: ArrayList<VideoData> = ArrayList()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE

        arguments?.let {
            type = it.getInt("type")
            what = it.getInt("what")
            searchWords = it.getString("searchWords")
        }
        iv_back.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        etSearch.setOnTouchListener(OnTouchListener { _, event ->
            // getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
            val drawable: Drawable = etSearch.compoundDrawables[2] ?: return@OnTouchListener false
            //如果右边没有图片，不再处理
            //如果不是按下事件，不再处理
            if (event.action != MotionEvent.ACTION_UP) return@OnTouchListener false
            if (event.x > (etSearch.width
                        - etSearch.paddingRight
                        - drawable.intrinsicWidth)
            ) {
                etSearch.setText("")
            }
            false
        })

        etSearch.setOnEditorActionListener(OnEditorActionListener { arg0, arg1, arg2 ->
            if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg2 != null && arg2.keyCode === KeyEvent.KEYCODE_ENTER && arg2.action === KeyEvent.ACTION_DOWN) {
                // 先隐藏键盘
                (etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    etSearch.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                searchWords = etSearch.text.toString()
                mViewModel.getSearchPageList(searchWords!!, 2, type)
                return@OnEditorActionListener true
            }
            false
        })

        srl_searchResult_Videos.setOnRefreshListener {
            pageNum = 1
            mViewModel.getSearchPageList(str = searchWords!!, what = what, type = type, pageNum = pageNum)
        }
        srl_searchResult_Videos.setEnableLoadMore(true)
        srl_searchResult_Videos.setOnLoadMoreListener {
            mViewModel.getSearchPageList(str = searchWords!!, what = what, type = type, pageNum = ++pageNum)
        }
        initRecycleView()
    }

    private fun initRecycleView() {
        rcv_searchResult_Videos.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            mAdapter.run {
                onItemChildClickListener = this@SearchResultsFragment.onItemChildClickListener
            }

            rcv_searchResult_Videos.adapter = mAdapter

        }
    }

    override fun initData() {
        mViewModel.getSearchPageList(str = searchWords!!, what = what, type = type, pageNum = pageNum)
    }

    override fun startObserve() {
        mViewModel.apply {
            val loadingPopup = XPopup.Builder(context).asLoading()

            searchState.observe(this@SearchResultsFragment, Observer {
                if (it.showLoading) {
                    loadingPopup.show()
                }

                it.showSuccess?.let { searchResults ->
                    loadingPopup.dismiss()
                    if (pageNum > 1) {
                        for (i in searchResults.data.indices) {
                            searchResults.data[i].position = searchResults.data.size + 1
                            videoList.add(searchResults.data[i])
                        }
                        mAdapter.apply {
                            setNewData(videoList)
                            notifyDataSetChanged()
                        }
                        srl_searchResult_Videos.finishLoadMore(0)
                    } else {
                        if (searchResults.data.isNotEmpty()) {
                            videoList.clear()
                            for (i in searchResults.data.indices) {
                                searchResults.data[i].position = i
                                videoList.add(searchResults.data[i])
                            }
                            mAdapter.apply {
                                setNewData(videoList)
                                notifyDataSetChanged()
                            }
                            srl_searchResult_Videos.finishRefresh(0)
                        } else {
                            srl_searchResult_Videos.finishRefresh(0)
                        }
                    }
                }
                it.showError?.let { message ->
                    loadingPopup.dismiss()
                    if (pageNum > 1) {
                        --pageNum
                        srl_searchResult_Videos.finishLoadMore(0)
                    } else if (pageNum == 1) {
                        srl_searchResult_Videos.finishRefresh(0)
                    }
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    loadingPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            val loading = XPopup.Builder(this@SearchResultsFragment.context).asLoading()
            collectUiState.observe(this@SearchResultsFragment, Observer {
                if (it.showLoading) {
                    loading.show()
                }

                it.showSuccess?.let { list ->
                    loading.dismiss()
                    XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {

                        override fun onDismiss() {
                            val id by PreferenceUtils(PreferenceUtils.CLICKED_COLLECT_ID, -1)
                            if (id != -1) {
                                mViewModel.addCollect(categoryId = id, remark = "", videoId = currentVideoID)
                            }
                        }
                    }).asCustom(
                        CustomLikeBottomPopup(
                            context!!,
                            currentVideoID,
                            0,
                            list,
                            mViewModel
                        )
                    ).show()
                }

                it.showError?.let { message ->
                    loading.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    loading.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            operateUiState.observe(this@SearchResultsFragment, Observer {
                if (it.showLoading) {
                    loading.show()
                }

                if (it.showSuccess == null) {
                    loading.dismiss()
                    videoList.clear()

                    mViewModel.getSearchPageList(str = searchWords!!, what = what, type = type)
                } else {
                    ToastUtils.showShort("操作失败！")
                }

                it.showError?.let { message ->
                    loading.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    loading.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            commentsUiState.observe(this@SearchResultsFragment, Observer {
                if (it.showLoading) {
                    loading.show()
                }

                it.showError?.let { message ->
                    loading.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.showSuccess?.let { comments ->
                    commentsAdapter.run {
                        setNewData(comments.data)
                        notifyDataSetChanged()
                    }
                    XPopup.Builder(this@SearchResultsFragment.context).setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss() {
                            super.onDismiss()
                            commentsAdapter.run {
                                setNewData(null)
                                notifyDataSetChanged()
                            }
                        }
                    })
                        .asCustom(
                            CustomCommentBottomPopup(
                                this@SearchResultsFragment.context!!,
                                mViewModel,
                                currentVideoID,
                                commentsAdapter
                            )
                        ).show()
                    loading.dismiss()
                }

                it.needLogin?.let {
                    loading.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    private var onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view.id) {
            R.id.layout_like -> {
                mViewModel.getCollectCategory()
                currentVideoID = mAdapter.getItem(position)!!.id
                currentPosition = position
            }
            R.id.layout_comment -> {
                mViewModel.getCommentList(-1, mAdapter.getItem(position)!!.id)
                currentVideoID = mAdapter.getItem(position)!!.id
                currentPosition = position
            }
//            R.id.layout_share -> {
//                XPopup.Builder(view.context).asCustom(
//                    CustomShareBottomPopup(
//                        view.context,
//                        mAdapter.getItem(position)!!.videoUrl!!,
//                        mAdapter.getItem(position)?.videoName,
//                        mAdapter.getItem(position)?.videoDesc,
//                        mAdapter.getItem(position)!!.videoCoverUrl!!
//                    )
//                ).show()
//            }
        }
    }

    override fun initListener() {

    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause();
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.releaseAllVideos();
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos();
    }

    inner class SearchResultAdapter(layoutResId: Int = R.layout.item_video) :
        BaseBindAdapter<VideoData>(layoutResId, BR.video) {

        override fun convert(helper: BindViewHolder, item: VideoData) {
            super.convert(helper, item)

            if (item.position == 0) {
                helper.getView<LinearLayout>(R.id.layout_top1).visibility = View.GONE
            } else {
                helper.getView<LinearLayout>(R.id.layout_top1).visibility = View.GONE
            }

            helper.getView<ImageView>(R.id.iv_tsbMore).visibility = View.GONE

            GSYVideoType.setRenderType(GSYVideoType.SUFRACE)
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)

            Glide.with(mContext).load(item.videoCoverUrl).into(helper.getView(R.id.thumbImage))

            helper.getView<TextView>(R.id.totalTime).text = formatTimeS(item.duration.toLong())

            GSYVideoOptionBuilder().setIsTouchWiget(false)
                .setUrl(item.videoUrl)
                .setVideoTitle(item.videoName)
                .setCacheWithPlay(false)
                .setRotateViewAuto(true)
                .setCacheWithPlay(true)
                .setLockLand(false)
                .setReleaseWhenLossAudio(true)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setPlayPosition(helper.position)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPrepared(url: String, vararg objects: Any) {
                        super.onPrepared(url, *objects)
                        if (!helper.getView<CustomVideoPlayer>(R.id.video_player).isIfCurrentIsFullscreen) {
                            //静音
                            GSYVideoManager.instance().isNeedMute = false
                        }
                    }

                    override fun onQuitFullscreen(url: String, vararg objects: Any) {
                        super.onQuitFullscreen(url, *objects)
                        //全屏不静音
                        GSYVideoManager.instance().isNeedMute = false
                        helper.getView<LinearLayout>(R.id.layout_checkGoods).visibility = View.VISIBLE
                        helper.getView<TextView>(R.id.totalTime).visibility = View.VISIBLE
                        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)
                    }

                    override fun onEnterFullscreen(url: String, vararg objects: Any) {
                        super.onEnterFullscreen(url, *objects)
                        GSYVideoManager.instance().isNeedMute = false
                        helper.getView<LinearLayout>(R.id.layout_checkGoods).visibility = View.GONE
                        helper.getView<TextView>(R.id.totalTime).visibility = View.INVISIBLE
                        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9)
                    }

                    override fun onClickStartIcon(url: String?, vararg objects: Any?) {
                        super.onClickStartIcon(url, *objects)
                        helper.getView<RelativeLayout>(R.id.layout_unPlay).visibility = View.GONE
                    }

                    override fun onClickResume(url: String?, vararg objects: Any?) {
                        super.onClickResume(url, *objects)
                        helper.getView<RelativeLayout>(R.id.layout_unPlay).visibility = View.GONE
                    }

                    override fun onClickStop(url: String?, vararg objects: Any?) {
                        super.onClickStop(url, *objects)
                        helper.getView<RelativeLayout>(R.id.layout_unPlay).visibility = View.VISIBLE
                    }


                }).build(helper.getView<CustomVideoPlayer>(R.id.video_player))
            helper.getView<CustomVideoPlayer>(R.id.video_player).apply {
                fullscreenButton.setOnClickListener { resolveFullBtn(helper.getView<CustomVideoPlayer>(R.id.video_player) as StandardGSYVideoPlayer) }
                backButton.visibility = View.GONE
            }

            if (item.haveGoods == 0) {
                helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.visibility = View.INVISIBLE
            } else {
                helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.visibility = View.VISIBLE
                helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.setOnClickListener {
                    activity!!.supportFragmentManager.beginTransaction()
                        .addToBackStack(SearchResultsFragment.javaClass.name)
                        .hide(this@SearchResultsFragment)//隐藏当前Fragment
                        .add(
                            R.id.frame_content,
                            GoodsDetailFragment.newInstance("${item.goodsId}"),
                            GoodsDetailFragment.javaClass.name
                        ).commit()

                }
            }


            val keywords = item.keywords?.split(',')
            helper.getView<CustomFlexlayout>(R.id.keywordsFlexlayout).removeAllViews()
            if (!keywords.isNullOrEmpty()) {
                for (ele in keywords) {
                    if (ele.isNotBlank()) {
                        val tv = LayoutInflater.from(mContext).inflate(
                            R.layout.layout_home_video_keywords, helper.getView(R.id.keywordsFlexlayout), false
                        ) as TextView
                        tv.maxEms = 5
                        tv.maxLines = 1
                        tv.ellipsize = TextUtils.TruncateAt.END
                        tv.text = ele.toString()
                        //添加到父View
                        helper.getView<CustomFlexlayout>(R.id.keywordsFlexlayout).addView(tv)
                    }
                }
            }

            if (item.like == 0) {
                helper.setImageDrawable(R.id.iv_like, mContext.resources.getDrawable(R.drawable.home_unfavorite))
            } else {
                helper.setImageDrawable(R.id.iv_like, mContext.resources.getDrawable(R.drawable.home_favorite))
            }

            helper.setText(R.id.tv_likeCount, "${item.likeCount}")
            helper.setText(R.id.tv_commentCount, "${item.commentCount}")
//            helper.setText(R.id.tv_shareCount, "${item.shareCount}")

            helper.addOnClickListener(R.id.layout_like)
            helper.addOnClickListener(R.id.layout_comment)
//            helper.addOnClickListener(R.id.layout_share)
        }

        /**
         * 全屏幕按键处理
         */
        private fun resolveFullBtn(standardGSYVideoPlayer: StandardGSYVideoPlayer) {
            standardGSYVideoPlayer.startWindowFullscreen(mContext, false, true)
        }

        private fun formatTimeS(seconds: Long): String? {
            var temp = 0
            val sb = StringBuffer()
            if (seconds > 3600) {
                temp = (seconds / 3600).toInt()
                sb.append(if (seconds / 3600 < 10) "0$temp:" else "$temp:")
                temp = (seconds % 3600 / 60).toInt()
                changeSeconds(seconds, temp, sb)
            } else {
                temp = (seconds % 3600 / 60).toInt()
                changeSeconds(seconds, temp, sb)
            }
            return sb.toString()
        }

        private fun changeSeconds(seconds: Long, temp: Int, sb: StringBuffer) {
            var temp = temp
            sb.append(if (temp < 10) "0$temp:" else "$temp:")
            temp = (seconds % 3600 % 60).toInt()
            sb.append(if (temp < 10) "0$temp" else "" + temp)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){

        }else{
            GSYVideoManager.onPause()
        }
    }
}
