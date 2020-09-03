package com.jzz.treasureship.ui.home

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.AnswersAdapter
import com.jzz.treasureship.adapter.BaseBindAdapter
import com.jzz.treasureship.adapter.CommentsAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.HomeTabBeanItem
import com.jzz.treasureship.model.bean.VideoData
import com.jzz.treasureship.ui.goods.GoodsDetailFragment
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.questions.QuestionsFragment
import com.jzz.treasureship.ui.wallet.WalletFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.fragment_home_vp.*
import kotlinx.android.synthetic.main.layout_home_header.*
import kotlinx.android.synthetic.main.video_layout_normal.view.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.math.BigDecimal

class HomeVpFragment : BaseVMFragment<HomeViewModel>() {

    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private var tsbUpdate by PreferenceUtils(PreferenceUtils.TSB_UPDATE, false)
    private val mAdapter by lazy { HomeVpAdapter(0) }
    private val commentsAdapter by lazy { CommentsAdapter() }
    private val videoList: ArrayList<VideoData> = ArrayList()
    val startAnswer by PreferenceUtils(PreferenceUtils.start_answer, false)
    val go2Wallet by PreferenceUtils(PreferenceUtils.goto_wallet, false)
    val getReward by PreferenceUtils(PreferenceUtils.get_reward, false)
    val openReward by PreferenceUtils(PreferenceUtils.open_reward, false)
    private val mWalletFragment by lazy { WalletFragment.newInstance() }
    private val mQuestionsAdapter by lazy { AnswersAdapter() }

    private var mTab: HomeTabBeanItem? = null
    private var pageNum = 1

    private var currentVideoID = -1
    private var currentPosition = -1

    private var mTabPosition = -1

    companion object {
        fun newInstance(tabItem: HomeTabBeanItem, index: Int): HomeVpFragment {
            val f = HomeVpFragment()
            val bundle = Bundle()
            bundle.putParcelable("tabItem", tabItem)
            bundle.putInt("tabPosition", index)
            f.arguments = bundle
            return f
        }
    }

    override fun getLayoutResId() = R.layout.fragment_home_vp

    override fun initVM(): HomeViewModel = getViewModel()

    override fun initView() {

        arguments!!.let {
            mTab = it.getParcelable<HomeTabBeanItem>("tabItem")
            mTabPosition = it.getInt("tabPosition")
        }
        if (mTabPosition == 0) {
            mViewModel.getAd()
        }
        srl_Videos.setOnRefreshListener {
            pageNum = 1
//            if (mTab == 0) {
//                mViewModel.getNewestVideoList(pageNum)
//            } else if (mTab == 1) {
//                mViewModel.getRecommendVideoList(pageNum)
//            }
            mViewModel.getVideoList(mTab!!.id!!, pageNum)
            mViewModel.getAd()
            mViewModel.getQuestionnaire()
        }
        srl_Videos.setEnableLoadMore(true)
        srl_Videos.setOnLoadMoreListener {
            //            if (mTab == 0) {
//                mViewModel.getNewestVideoList(++pageNum)
//            } else if (mTab == 1) {
//                mViewModel.getRecommendVideoList(++pageNum)
//            }
            mViewModel.getVideoList(mTab!!.id!!, ++pageNum)
        }

        initRecycleView()
    }

    private fun initRecycleView() {
        rcv_Videos.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            mAdapter.run {
                onItemChildClickListener = this@HomeVpFragment.onItemChildClickListener
                if (mTabPosition == 0) {
                    val view = View.inflate(context, R.layout.layout_home_header, null)
                    view.findViewById<ImageView>(R.id.iv_ad).setOnClickListener {
                        mViewModel.getQuestionnaire()
                    }
                    addHeaderView(view)
                }
            }


            adapter = mAdapter
        }
    }

    private var onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view.id) {
            R.id.layout_like -> {
                if (isLogin) {
                    mViewModel.getCollectCategory()
                    currentVideoID = mAdapter.getItem(position)!!.id
                    currentPosition = position
                } else {
                    switchLogin()
                }
            }
            R.id.layout_comment -> {
                if (isLogin) {
                    mViewModel.getCommentList(-1, mAdapter.getItem(position)!!.id)
                    XPopup.Builder(this@HomeVpFragment.context)
                        .asCustom(
                            CustomCommentBottomPopup(
                                this@HomeVpFragment.context!!,
                                mViewModel,
                                mAdapter.getItem(position)!!.id,
                                commentsAdapter
                            )
                        ).show()
                    currentVideoID = mAdapter.getItem(position)!!.id
                    currentPosition = position
                } else {
                    switchLogin()
                }

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

    override fun initData() {
//        if (mTab == 0) {
//            mViewModel.getNewestVideoList(1)
//        } else if (mTab == 1) {
//            mViewModel.getRecommendVideoList(1)
//        }
        mViewModel.getVideoList(mTab!!.id!!, 1)
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).asLoading()

            adState.observe(this@HomeVpFragment, Observer {

                it.showSuccess?.let { ad ->
                    ad.list?.let { list ->
                        if (list.isNotEmpty()) {
                            list[0]?.let { item ->
                                Glide.with(context!!).load(item.adCover).into(iv_ad)
                            }
                        }

                    }
                }

                it.showError?.let { message ->
                    //xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            uiState.observe(this@HomeVpFragment, Observer {
                it.showLoading.let {
                    //xPopup.show()
                }

                it.showSuccess?.let { list ->
                    //xPopup.dismiss()
                    if (pageNum > 1) {
                        if (list.data.isNotEmpty()) {
                            for (i in list.data.indices) {
                                list.data[i].position = list.data.size + 1
                                videoList.add(list.data[i])
                            }

                            mAdapter.run {
                                setNewData(videoList)
                                notifyDataSetChanged()
                            }
                        } else {
                            //第二页没视频，不增加
                            --pageNum
                        }
                        srl_Videos.finishLoadMore(0)
                    } else {
//                        if (list.data.isNotEmpty()) {
//                            srl_Videos.visibility = View.VISIBLE
//                            layout_noVideos.visibility = View.GONE
                        videoList.clear()
                        for (i in list.data.indices) {
                            list.data[i].position = i
                            videoList.add(list.data[i])
                        }
                        mAdapter.run {
                            setNewData(videoList)

                            notifyDataSetChanged()
                        }
                        srl_Videos.finishRefresh(0)
//                        } else {
//                            srl_Videos.visibility = View.GONE
//                            layout_noVideos.visibility = View.VISIBLE
//
//                            tv_getVideos.setOnClickListener {
//                                mViewModel.getVideoList(mTab!!.id!!, 1)
//                            }
//                        }
                    }
                }

                it.showError?.let { message ->
                    //xPopup.dismiss()
                    if (pageNum > 1) {
                        srl_Videos.finishLoadMore(0)
                    } else if (pageNum == 1) {
                        srl_Videos.finishRefresh(0)
                    }
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let {
                    xPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            collectUiState.observe(this@HomeVpFragment, Observer {
                it.showLoading.let {
                    if (it) {
                        xPopup.show()
                    }
                }

                it.showSuccess?.let { list ->
                    xPopup.dismiss()
                    XPopup.Builder(context).asCustom(
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

            operateUiState.observe(this@HomeVpFragment, Observer {
                //                Log.d("operateUiState", it.showSuccess)
                it.showLoading.let {
                    xPopup.show()
                }
                it.showSuccess?.let { success ->
                    xPopup.dismiss()
                    when (success) {
                        "添加收藏分类成功" -> {
                            mViewModel.getCollectCategory()
                            var tsbUpdate by PreferenceUtils(PreferenceUtils.TSB_UPDATE, false)
                            tsbUpdate = true
                        }
                        "视频收藏成功" -> {
//                            if (mTab == 0) {
//                                tsbUpdate = true
//                                mViewModel.getNewestVideoList(1)
//                            } else if (mTab == 1) {
//                                tsbUpdate = true
//                                mViewModel.getRecommendVideoList(1)
//                            }
                            tsbUpdate = true
                            mViewModel.getVideoList(mTab!!.id!!, 1)
                        }
                        "视频评论成功" -> {
                            mViewModel.getCommentList(-1, currentVideoID)
                        }
                        "点赞成功" -> {
                            mViewModel.getCommentList(-1, currentVideoID)
                        }
                        else -> {
                            ToastUtils.showShort("操作失败")
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

            commentsUiState.observe(this@HomeVpFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.showSuccess?.let { comments ->
                    commentsAdapter.run {
                        setNewData(comments.data)
                        notifyDataSetChanged()
                    }
                    xPopup.dismiss()
                }

                it.needLogin?.let {
                    xPopup.dismiss()
                    if (it) {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            })

            questionnarieState.observe(this@HomeVpFragment, Observer {

                it.showSuccess?.let {
                    //                    Log.d("caicaicaicai", it.toString())
                    when (it.resultCode) {
                        1 -> {
                            //未答题
                            XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                                override fun onDismiss() {
                                    super.onDismiss()
                                    if (startAnswer) {
                                        activity!!.supportFragmentManager.beginTransaction()
                                            .addToBackStack(HomeVpFragment.javaClass.name)
                                            .hide(this@HomeVpFragment.parentFragment!!)//隐藏当前Fragment
                                            .add(
                                                R.id.frame_content,
                                                QuestionsFragment.newInstance(it.questionnaire!!),
                                                QuestionsFragment.javaClass.name
                                            )
                                            .commit()
                                    }
                                }
                            })
                                .asCustom(StartQuestionsDialog(context!!)).show()
                        }
                        2 -> {
                            //未领取红包
                            XPopup.Builder(context)
                                .asCustom(NoticeGetRewardDialog(context!!, mViewModel)).show()
                        }
                        3, 4, 5, 6, 8 -> {
                            //红包已领取
                            val customDialog = CustomDialog.Builder(this@HomeVpFragment.context!!)
                            customDialog.setTitle("提示")
                            customDialog.setMessage("${it.resultMsg}")
                            customDialog.setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })
                            customDialog.create().show()
//                            ToastUtils.showShort("${it.resultMsg}")
                        }

                        7 -> {
                            //抢红包成功
//                            XPopup.Builder(this@MainActivity).setPopupCallback(object : SimpleCallback() {
//                                override fun onDismiss() {
//                                    super.onDismiss()
//                                    if (go2Wallet) {
//                                        switchToWallet()
//                                    }
//                                }
//                            })
//                                .asCustom(CheckRewardDialog(this@MainActivity)).show()

                        }
                        else -> {
//                            ToastUtils.showShort("${it.resultMsg}")
                            val customDialog = CustomDialog.Builder(this@HomeVpFragment.context!!)
                            customDialog.setTitle("提示")
                            customDialog.setMessage("${it.resultMsg}")
                            customDialog.setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })
                            customDialog.create().show()
                        }
                    }
                }

                it.showError?.let { err ->
                    Log.e("questionnarieState", err)
//
                }
            })

            redEnvState.observe(this@HomeVpFragment, Observer {

                it.showSuccess?.let {
                    XPopup.Builder(context)
                        .asCustom(NoticeGetRewardDialog(context!!, mViewModel)).show()
                }

                it.showError?.let { err ->
                    Log.e("redEnvState", err)
                }
            })

            rewardState.observe(this@HomeVpFragment, Observer {

                it.showSuccess?.let {reward ->
                    if (reward.redEnvelopeRecord == null){
                        ToastUtils.showShort("来晚一步，红包没有啦！")
                        return@Observer
                    }
                    reward.redEnvelopeRecord.let { record ->
                        record.amount?.let {amount ->
                            XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                                override fun onDismiss() {
                                    super.onDismiss()
                                    if (go2Wallet) {
                                        activity!!.supportFragmentManager.beginTransaction()
                                            .addToBackStack(WalletFragment.javaClass.name)
                                            .hide(this@HomeVpFragment)//隐藏当前Fragment
                                            .add(
                                                R.id.frame_content,
                                                WalletFragment.newInstance(),
                                                WalletFragment.javaClass.name
                                            )
                                            .commit()
                                    }
                                }
                            }
                            )
                                .asCustom(CheckRewardDialog(context!!, amount)).show()
                        }
                    }

                }

                it.showError?.let { err ->
                    Log.e("rewardState", err)
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
            helper.getView<ImageView>(R.id.iv_tsbMore).visibility = View.GONE

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

            when (mFrom) {
                0 -> {
                    if (item.haveGoods == 0) {
                        helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.visibility =
                            View.INVISIBLE
                    } else {
                        helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.visibility = View.VISIBLE
                        helper.getView<CustomVideoPlayer>(R.id.video_player).layout_checkGoods.setOnClickListener {
                            if (!isLogin) {
                                switchLogin()
                                return@setOnClickListener
                            }
                            activity!!.supportFragmentManager.beginTransaction()
                                .addToBackStack(HomeVpFragment.javaClass.name)
                                .hide(this@HomeVpFragment.parentFragment!!)//隐藏当前Fragment
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

            helper.getView<CustomVideoPlayer>(R.id.video_player).iv_tsbMore.setOnClickListener {
                XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {
                    override fun onDismiss() {
                        super.onDismiss()
                        var moveVideo by PreferenceUtils(PreferenceUtils.MOVE_VIDEO, false)
                        var delVideo by PreferenceUtils(PreferenceUtils.DEL_VIDEO, false)

                        if (moveVideo and (!delVideo)) {
                            //移动视频
                            moveVideo = false
                            mViewModel!!.moveCollect(mTabId, "", item.id)
                        }

                        if (!moveVideo and (delVideo)) {
                            //删除视频
                            delVideo = false
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
            helper.setText(R.id.tv_likeCount, "${toNum(item.likeCount)}")
            helper.setText(R.id.tv_commentCount, "${toNum(item.commentCount)}")
//            helper.setText(R.id.tv_shareCount, "${toNum(item.shareCount)}")

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

    private fun switchLogin() {
        startActivity(Intent(this.activity!!, LoginActivity::class.java))
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
            val f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
            str = "${f1}W+"
        }
        return str
    }

}