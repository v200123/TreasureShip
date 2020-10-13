package com.jzz.treasureship.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.HomeTabBeanItem
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.model.bean.UserDialogInformationBean
import com.jzz.treasureship.service.RewardService
import com.jzz.treasureship.ui.DialogHelp
import com.jzz.treasureship.ui.questions.QuestionsFragment
import com.jzz.treasureship.ui.search.SearchFragment
import com.jzz.treasureship.ui.wallet.WalletFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CheckRewardDialog
import com.jzz.treasureship.view.NoticeGetRewardDialog
import com.jzz.treasureship.view.StartQuestionsDialog
import com.lc.mybaselibrary.ext.getResColor
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseVMFragment<HomeViewModel>() {
    private var isInviteDialog by PreferenceUtils(PreferenceUtils.everyday_invite_dialog, "")
    private val user by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private val mSearchFragment by lazy { SearchFragment.newInstance() }
    private var curFragment by PreferenceUtils(PreferenceUtils.CUR_FRAGMENT, "")
    val startAnswer by PreferenceUtils(PreferenceUtils.start_answer, false)
    val go2Wallet by PreferenceUtils(PreferenceUtils.goto_wallet, false)
    val getReward by PreferenceUtils(PreferenceUtils.get_reward, false)
    val openReward by PreferenceUtils(PreferenceUtils.open_reward, false)
    private val mWalletFragment by lazy { WalletFragment.newInstance() }

    //    private val tabs = arrayOf("最新", "推荐")
//    private val fragments: ArrayList<HomeVpFragment> = arrayListOf(
//        HomeVpFragment.newInstance(0),
//        HomeVpFragment.newInstance(1)
//    )
    private var tabs: ArrayList<HomeTabBeanItem> = ArrayList()
    private val fragments: ArrayList<HomeVpFragment> = ArrayList()

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
//        App.dialogHelp.showSuccess("eertertet"){}
        if (isLogin) {
            val mUser = GsonUtils.fromJson(user, User::class.java)
            if (mUser.firstPassTip != 1) {
                val mUserDialogShow = MMKV.defaultMMKV()
                    .decodeParcelable(mUser.id.toString(), UserDialogInformationBean::class.java)
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+8"))
                if (mUserDialogShow.showInviteDialogDate.isBlank()) {
                    if (mUser.auditStatus == 1) {
                        App.dialogHelp.showInvite()
                        MMKV.defaultMMKV().encode(
                            mUser.id.toString(),
                            mUserDialogShow.apply {
                                showInviteDialogDate =
                                    "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                            })
                    }
                } else {
                    val split = mUserDialogShow.showInviteDialogDate.split(",")
                    if (split[0].toInt() != calendar.get(Calendar.MONTH) && split[0].toInt() != calendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ) {
                        App.dialogHelp.showInvite()
                    }
                    MMKV.defaultMMKV().encode(
                        mUser.id.toString(),
                        mUserDialogShow.apply {
                            showInviteDialogDate =
                                "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                        })
                }
            }
        }
        GSYVideoManager.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    private var currentPage = 0

    override fun getLayoutResId() = R.layout.fragment_home

    override fun initView() {
//        activity!!.nav_view.visibility = View.VISIBLE
        curFragment = "HomeFragment"
        StateAppBar.setStatusBarLightMode(
            this.activity,
            mContext.getResColor(R.color.white)
        )

        tv_full_sreach.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(HomeFragment.javaClass.name)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, mSearchFragment, mSearchFragment.javaClass.name)
                .commit()
        }
    }


    override fun initData() {
        mViewModel.getHomeTabs()
        mViewModel.getQuestionnaire()
    }

    override fun startObserve() {
        mViewModel.apply {
            homeTabState.observe(this@HomeFragment, Observer {
                it.showSuccess?.let {
                    tabs.clear()
                    fragments.clear()

                    for ((index, item) in it.withIndex()) {
                        val tabBean =
                            HomeTabBeanItem(item.createTime, item.description, item.id, item.name)
                        tabs.add(tabBean)
                        fragments.add(HomeVpFragment.newInstance(tabBean, index))
                    }

                    home_tab.run {
                        setSelectedTabIndicatorColor(this.resources.getColor(R.color.blue_light))
                        setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabReselected(tab: TabLayout.Tab?) {

                            }

                            override fun onTabUnselected(tab: TabLayout.Tab?) {
                                val view = tab!!.customView
                                if (null == view) {
                                    tab.setCustomView(R.layout.custom_tab_layout_text)
                                }
                                val textView: TextView =
                                    tab.customView!!.findViewById(R.id.tab_item_textview)
                                textView.text = tabs[tab.position].name
                                textView.setTextColor(context.resources.getColor(R.color.Home_text_normal))
                                textView.typeface = Typeface.DEFAULT
                            }

                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                val view = tab!!.customView
                                if (null == view) {
                                    tab.setCustomView(R.layout.custom_tab_layout_text)
                                }
                                val textView: TextView =
                                    tab.customView!!.findViewById(R.id.tab_item_textview)
                                textView.text = tabs[tab.position].name
                                textView.setTextColor(context.resources.getColor(R.color.Home_text_bold))
                                textView.typeface = Typeface.DEFAULT_BOLD
                            }

                        })
                    }
                    val mAdapter = object : FragmentStateAdapter(this@HomeFragment) {
                        override fun createFragment(position: Int): Fragment {
                            if (!fragments[position].isAdded) {
                                return fragments[position]
                            }
                            return Fragment()
                        }

                        override fun getItemCount(): Int {
                            return fragments.size
                        }

                    }
                    //home_viewpager2.offscreenPageLimit = fragments.size
                    home_viewpager2.isSaveEnabled = false
                    home_viewpager2.adapter = mAdapter

                    TabLayoutMediator(home_tab, home_viewpager2, true,
                        TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                            //这里需要根据position修改tab的样式和文字等
                            tab.text = tabs[position].name
                            currentPage = position
                        }).attach()
                }
                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            questionnarieState.observe(this@HomeFragment, Observer {
                it.showSuccess?.let {
                    //                    Log.d("caicaicaicai", it.toString())
                    when (it.resultCode) {
                        1 -> {
                            val intent = Intent(mContext, RewardService::class.java)
                            //未答题
                            if (System.currentTimeMillis() !in it.receiveDate!!.startDateTimeInMillis - 10000 until it.receiveDate.startDateTimeInMillis + 10000) {
                                mContext.startService(intent.apply {
                                    putExtra(RewardService.TestQuestions, it.questionnaire)
                                })
                            } else if (System.currentTimeMillis() > it.receiveDate.endDateTimeInMillis) {
                                mContext.stopService(intent)
                            } else
                                XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                                    override fun onDismiss(popupView: BasePopupView) {
                                        super.onDismiss(popupView)
                                        if (startAnswer) {
                                            activity!!.supportFragmentManager.beginTransaction()
                                                .addToBackStack(HomeVpFragment.javaClass.name)
                                                .hide(this@HomeFragment)//隐藏当前Fragment
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
                        3 -> {
                            //红包已领取
                        }
                        4 -> {
                            //抢红包活动未开始
                        }
                        5 -> {
                            //抢红包活动已结束
                        }
                        6 -> {
                            //用户未实名认证
//                            ToastUtils.showShort("未实名认证，请到设置中进行实名认证。")
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
                        8 -> {
                            //手速太慢，红包已抢完
                        }
                        else -> {

                        }
                    }
                }

                it.showError?.let { err ->
                    Log.e("questionnarieState", err)
//
                }
            })

            redEnvState.observe(this@HomeFragment, Observer {

                it.showSuccess?.let {
                    XPopup.Builder(context)
                        .asCustom(NoticeGetRewardDialog(context!!, mViewModel)).show()
                }

                it.showError?.let { err ->
                    Log.e("redEnvState", err)
                }
            })

            rewardState.observe(this@HomeFragment, Observer {

                it.showSuccess?.let {
                    XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView) {
                            super.onDismiss(popupView)
                            if (go2Wallet) {
                                activity!!.supportFragmentManager.beginTransaction()
                                    .addToBackStack(WalletFragment.javaClass.name)
                                    .hide(this@HomeFragment)//隐藏当前Fragment
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
                        .asCustom(CheckRewardDialog(context!!, it.redEnvelopeRecord!!.amount!!))
                        .show()
                }

                it.showError?.let { err ->
                    Log.e("rewardState", err)
                    ToastUtils.showShort(err)
                }
            })
        }
    }

    override fun initVM(): HomeViewModel = getViewModel()


    override fun initListener() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isHidden) {
            mActivity!!.nav_view.visibility = View.VISIBLE
            mActivity!!.nav_view.menu[0].isChecked = true
            StateAppBar.setStatusBarLightMode(this.mActivity, mContext.getResColor(R.color.white))
            curFragment = "HomeFragment"
        } else {
            GSYVideoManager.onPause()
        }
    }
}
