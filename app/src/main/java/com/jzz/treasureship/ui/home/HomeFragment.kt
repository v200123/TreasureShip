package com.jzz.treasureship.ui.home

import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.HomeTabBeanItem
import com.jzz.treasureship.ui.questions.QuestionsFragment
import com.jzz.treasureship.ui.search.SearchFragment
import com.jzz.treasureship.ui.wallet.WalletFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CheckRewardDialog
import com.jzz.treasureship.view.NoticeGetRewardDialog
import com.jzz.treasureship.view.StartQuestionsDialog
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_home_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class HomeFragment : BaseVMFragment<HomeViewModel>() {

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
        GSYVideoManager.releaseAllVideos();
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos();
    }

    private var currentPage = 0

    override fun getLayoutResId() = R.layout.fragment_home

    override fun initView() {
        activity!!.nav_view.visibility = View.VISIBLE
        curFragment = "HomeFragment"
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))

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
                        val tabBean = HomeTabBeanItem(item.createTime, item.description, item.id, item.name)
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
                                val textView: TextView = tab.customView!!.findViewById(R.id.tab_item_textview)
                                textView.text = tabs[tab.position].name
                                textView.setTextColor(context.resources.getColor(R.color.Home_text_normal))
                                textView.typeface = Typeface.DEFAULT
                            }

                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                val view = tab!!.customView
                                if (null == view) {
                                    tab.setCustomView(R.layout.custom_tab_layout_text)
                                }
                                val textView: TextView = tab.customView!!.findViewById(R.id.tab_item_textview)
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
                            //未答题
                            XPopup.Builder(context).setPopupCallback(object : SimpleCallback() {
                                override fun onDismiss() {
                                    super.onDismiss()
                                    if (startAnswer) {
//                                        mQuestionsAdapter.run {
//                                            val content = JSONObject(it.questionnaire?.content)
////                                        val content = JSONObject(
////                                            "{\"items\":[{\"item\":\"A\",\"text\":\"非常喜欢\"},{\"item\":\"B\",\"text\":\"有点喜欢\"},{\"item\":\"C\",\"text\":\"既不喜欢也不讨厌\"},{\"item\":\"D\",\"text\":\"有点讨厌\"},{\"item\":\"E\",\"text\":\"非常讨厌\"}]}"
////                                        )
//                                            val questionItems = JSONArray(content.get("items").toString())
//
//                                            val list = ArrayList<QuestionItem>(questionItems.length())
//
//                                            for (i in 0 until questionItems.length()) {
//                                                val questionItem: QuestionItem =
//                                                    QuestionItem(questionItems.get(i).toString())
//                                                list.add(questionItem)
//                                            }
//
//                                            setNewData(list)
//                                            notifyDataSetChanged()
//                                        }
//                                        XPopup.Builder(context!!)
//                                            .asCustom(
//                                                QuestionsDialog(
//                                                    context!!,
//                                                    it.questionnaire!!.iconPath!!,
//                                                    it.questionnaire.id!!,
//                                                    mViewModel,
//                                                    mQuestionsAdapter
//                                                )
//                                            ).show()

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
                        override fun onDismiss() {
                            super.onDismiss()
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
                        .asCustom(CheckRewardDialog(context!!, it.redEnvelopeRecord!!.amount!!)).show()
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
            StateAppBar.setStatusBarLightMode(this.mActivity, context!!.resources.getColor(R.color.white))
            curFragment = "HomeFragment"
        } else {
            GSYVideoManager.onPause()
        }
    }
}