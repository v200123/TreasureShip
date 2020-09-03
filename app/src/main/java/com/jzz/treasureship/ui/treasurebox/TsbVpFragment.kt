package com.jzz.treasureship.ui.treasurebox

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
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
import com.jzz.treasureship.model.bean.TabBean
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
import kotlinx.android.synthetic.main.fragment_tsb_vp.*
import kotlinx.android.synthetic.main.video_layout_normal.view.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.math.BigDecimal

class TsbVpFragment : BaseVMFragment<HomeViewModel>() {

    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private val mAdapter by lazy { HomeVpAdapter(1, mViewModel, mTabBean!!.id) }
    private val commentsAdapter by lazy { CommentsAdapter() }
    private val videoList: ArrayList<VideoData> = ArrayList()

    private var mTabBean: TabBean? = null
    private var pageNum = 1

    private var currentVideoID = -1
    private var currentPosition = -1

    override fun getLayoutResId() = R.layout.fragment_tsb_vp

    override fun initVM(): HomeViewModel = getViewModel()

    override fun initView() {

        mTabBean = arguments!!.getParcelable("tabBean")

        srl_collectVideos.setOnRefreshListener {
            pageNum = 1
            mViewModel.getCollectVideoList(mTabBean!!.id, null, -1, pageNum)
        }
        srl_collectVideos.setEnableLoadMore(true)
        srl_collectVideos.setOnLoadMoreListener {
            mViewModel.getCollectVideoList(mTabBean!!.id, null, -1, ++pageNum)
        }

        initRecycleView()
    }

    companion object {
        fun newInstance(tabBean: TabBean): TsbVpFragment {
            val f = TsbVpFragment()
            val bundle = Bundle()
            bundle.putParcelable("tabBean", tabBean)
            f.arguments = bundle
            return f
        }
    }

    private fun initRecycleView() {
        rcv_collectVideos.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            mAdapter.run {
                onItemChildClickListener = this@TsbVpFragment.onItemChildClickListener
            }

            rcv_collectVideos.adapter = mAdapter
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
                XPopup.Builder(this@TsbVpFragment.context)
                    .asCustom(
                        CustomCommentBottomPopup(
                            this@TsbVpFragment.context!!,
                            mViewModel,
                            mAdapter.getItem(position)!!.id,
                            commentsAdapter
                        )
                    ).show()
                currentVideoID = mAdapter.getItem(position)!!.id
                currentPosition = position
            }
//            R.id.layout_share -> {
//                XPopup.Builder(view.context).asCustom(
//                    CustomShareBottomPopup(
//                        view.context,
//                        mAdapter.getItem(position)!!.videoUrl!!,
//                        mAdapter.getItem(position)!!.videoName,
//                        mAdapter.getItem(position)!!.videoDesc,
//                        mAdapter.getItem(position)!!.videoCoverUrl!!
//                    )
//                ).show()
//            }
        }
    }

    override fun initData() {
        mViewModel.getCollectVideoList(mTabBean!!.id, null, -1, pageNum)
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).asLoading()
            uiState.observe(this@TsbVpFragment, Observer {
                it.showLoading.let {
                    //xPopup.show()
                }
                it.showSuccess?.let { list ->
                    //xPopup.dismiss()
                    if (pageNum > 1) {
                        if (list.data.isNotEmpty()) {
                            videoList.addAll(list.data)
                            mAdapter.run {
                                setNewData(videoList)
                                notifyDataSetChanged()
                            }
                        } else {
                            //第二页没视频，页码不增加
                            --pageNum
                        }
                        srl_collectVideos.finishLoadMore(0)
                    } else {
                        videoList.clear()
                        for (i in list.data.indices) {
                            list.data[i].position = i
                            videoList.add(list.data[i])
                        }
                        mAdapter.run {
                            setNewData(videoList)
                            notifyDataSetChanged()
                        }
                        srl_collectVideos.finishRefresh(0)
                    }
                }

                it.showError?.let { message ->
                    //xPopup.dismiss()
                    if (pageNum > 1) {
                        --pageNum
                        srl_collectVideos.finishLoadMore(0)
                    } else if (pageNum == 1) {
                        srl_collectVideos.finishRefresh(0)
                    }
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@TsbVpFragment.context, LoginActivity::class.java))
                    }
                }
            })

            collectUiState.observe(this@TsbVpFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.showSuccess?.let { list ->
                    xPopup.dismiss()
                    XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {

                        override fun onDismiss() {
                            val id by PreferenceUtils(PreferenceUtils.CLICKED_COLLECT_ID, -1)
                            if (id != -1) {
//                                mViewModel.addCollect(categoryId = id, remark = "", videoId = currentVideoID)
                                mViewModel.moveCollect(categoryId = id, remark = "", videoId = currentVideoID)
                            }
                        }
                    }).asCustom(
                        CustomLikeBottomPopup(
                            context!!,
                            currentVideoID,
                            1,
                            list,
                            mViewModel
                        )
                    ).show()
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@TsbVpFragment.context, LoginActivity::class.java))
                    }
                }
            })

            operateUiState.observe(this@TsbVpFragment, Observer {

                it.showLoading.let {
                    xPopup.show()
                }
                it.showSuccess?.let { success ->
                    xPopup.dismiss()
                    when (success) {
                        "视频评论成功" -> {
                            mViewModel.getCommentList(-1, currentVideoID)
                        }
                        "视频取消收藏成功","视频移动成功" -> {
                            pageNum = 1
                            mViewModel.getCollectVideoList(mTabBean!!.id, null, -1, pageNum)
                        }
                        else -> {
//                            pageNum = 1
//                            mViewModel.getCollectVideoList(mTabBean!!.id, null, -1, pageNum)
                        }
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    xPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            commentsUiState.observe(this@TsbVpFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.showSuccess?.let { comments ->
                    commentsAdapter.run {
                        setNewData(comments.data)
                        notifyDataSetChanged()
                    }
                    xPopup.dismiss()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@TsbVpFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    //from:0首页 1宝舰箱
    inner class HomeVpAdapter(
        from: Int,
        viewModel: HomeViewModel? = null,
        tabId: Int = -1,
        layoutResId: Int = R.layout.item_video
    ) :
        BaseBindAdapter<VideoData>(layoutResId, BR.video) {
        private val mFrom = from
        private val mViewModel = viewModel
        private val mTabId = tabId

        override fun convert(helper: BindViewHolder, item: VideoData) {
            super.convert(helper, item)

            if (mFrom == 0) {
                if (item.position == 0) {
                    helper.getView<LinearLayout>(R.id.layout_top1).visibility = View.GONE
                } else {
                    helper.getView<LinearLayout>(R.id.layout_top1).visibility = View.GONE
                }
            }

            if (mFrom == 1) {
                helper.getView<ImageView>(R.id.iv_tsbMore).visibility = View.VISIBLE
            }
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
                        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)
                    }

                    override fun onEnterFullscreen(url: String, vararg objects: Any) {
                        super.onEnterFullscreen(url, *objects)
                        GSYVideoManager.instance().isNeedMute = false
                        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
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

            when (mFrom) {
                1 -> {
                    if (item.haveGoods == 0) {
                        helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.visibility =
                            View.INVISIBLE
                    } else {
                        helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.apply {
                            visibility = View.VISIBLE
                            setOnClickListener {
                                if (!isLogin) {
                                    startActivity(Intent(activity!!, LoginActivity::class.java))
                                    return@setOnClickListener
                                }
                                activity!!.supportFragmentManager.beginTransaction()
                                    .addToBackStack(TsbVpFragment.javaClass.name)
                                    .hide(this@TsbVpFragment.parentFragment!!)//隐藏当前Fragment
                                    .add(
                                        R.id.frame_content,
                                        GoodsDetailFragment.newInstance("${item.goodsId}"),
                                        GoodsDetailFragment.javaClass.name
                                    )
                                    .commit()
                            }
                        }
                    }
                }
            }

            helper.getView<CustomVideoPlayer>(R.id.video_player).iv_tsbMore.setOnClickListener {
                XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {
                    override fun onDismiss() {
                        super.onDismiss()
                        var moveVideo by PreferenceUtils(PreferenceUtils.MOVE_VIDEO, false)
                        var delVideo by PreferenceUtils(PreferenceUtils.DEL_VIDEO, false)

                        if (moveVideo and (!delVideo)) {
                            //移动视频
//                            XPopup.Builder(mContext).asCustom(CustomLikeBottomPopup(mContext,)).show()
                            moveVideo = false
                            currentVideoID = item.id
//                            mViewModel!!.moveCollect(mTabId, "", item.id)
                            mViewModel!!.getCollectCategory()
                        }

                        if (!moveVideo and (delVideo)) {
                            //删除视频
                            delVideo = false
                            currentVideoID = item.id
                            mViewModel!!.delCollect(item.id)
                        }
                    }
                }).asCustom(CustomTsbCollectBottomPopup(mContext)).show()
            }

            val keywords = item.keywords?.split(',')
            helper.getView<CustomFlexlayout>(R.id.keywordsFlexlayout).removeAllViews()
            if (!keywords.isNullOrEmpty()) {
                for (ele in keywords) {
                    if (ele.isNotBlank()) {
                        val tv = LayoutInflater.from(mContext).inflate(
                            R.layout.layout_home_video_keywords, helper.getView(R.id.keywordsFlexlayout), false
                        ) as TextView
                        tv.maxEms = 6
                        tv.maxLines = 1
                        tv.ellipsize = TextUtils.TruncateAt.END
                        tv.text = "$ele"
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
//            helper.setText(R.id.tv_likeCount, "${item.likeCount}")
            //helper.getView<AppCompatTextView>(R.id.tv_likeCount).visibility = View.GONE
            helper.getView<LinearLayout>(R.id.layout_like).visibility = View.GONE

            helper.setText(R.id.tv_commentCount, "${toNum(item.commentCount)}")
//            helper.setText(R.id.tv_shareCount, "${toNum(item.shareCount)}")

            //helper.addOnClickListener(R.id.layout_like)
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

    fun toNum(num: Int): String {
        var str = ""
        if (num <= 0) {
            str = "0"
        } else if (num < 10000) {
            str = "$num"
        } else {
            val d = num.toDouble()
            val b = BigDecimal(d / 10000)
            val f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
            str = "${f1}W+"
        }
        return str
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mViewModel.getCount()
        mViewModel.getCollectCategory()
    }
}