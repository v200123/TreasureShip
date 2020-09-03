package com.jzz.treasureship.ui.wallet

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.WalletAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.DataXXX
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.withdraw.WithdrawFragment
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mine_wallet.*
import kotlinx.android.synthetic.main.fragment_mine_wallet.rcv_wallets
import kotlinx.android.synthetic.main.fragment_mine_wallet.srl_wallets
import kotlinx.android.synthetic.main.fragment_wallet_vp.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class WalletFragment : BaseVMFragment<WalletViewModel>() {

    private val mAdapter by lazy { WalletAdapter() }
    private var pageNum = 1
    private val list: ArrayList<DataXXX> = ArrayList()

    companion object {
        fun newInstance(): WalletFragment {
            return WalletFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_mine_wallet

    override fun initVM(): WalletViewModel = getViewModel()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
//        val titles = arrayOf("全部", "佣金", "个税", "提现记录", "返利")
//        val fragments: ArrayList<WalletVpFragment> = ArrayList(titles.size)
        iv_walletBack.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
//
//        for ((index) in titles.withIndex()) {
//            fragments.add(WalletVpFragment.newInstance(index))
//        }
//        walletTablayout.run {
//            setSelectedTabIndicatorColor(this.resources.getColor(R.color.blue_light))
//            setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                    val view = tab!!.customView
//                    if (null == view) {
//                        tab.setCustomView(R.layout.custom_tab_layout_text)
//                    }
//                    val textView: TextView = tab.customView!!.findViewById(R.id.tab_item_textview)
//                    textView.text = titles[tab.position]
//                    textView.setTextColor(context.resources.getColor(R.color.gray999))
//                    textView.typeface = Typeface.DEFAULT
//                }
//
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    val view = tab!!.customView
//                    if (null == view) {
//                        tab.setCustomView(R.layout.custom_tab_layout_text)
//                    }
//                    val textView: TextView = tab.customView!!.findViewById(R.id.tab_item_textview)
//                    textView.text = titles[tab.position]
//                    textView.setTextColor(context.resources.getColor(R.color.blue_light))
//                    textView.typeface = Typeface.DEFAULT_BOLD
//                }
//
//            })
//        }

//        val mAdapter = object : FragmentStateAdapter(this) {
//            override fun createFragment(position: Int): Fragment {
//                if (!fragments[position].isAdded){
//                    return fragments[position]
//                }
//                return Fragment()
//            }
//
//            override fun getItemCount(): Int {
//                return fragments.size
//            }
//        }
//        walletViewpager.offscreenPageLimit = fragments.size
//        walletViewpager.isSaveEnabled = false
//        walletViewpager.adapter = mAdapter
//
//        TabLayoutMediator(walletTablayout, walletViewpager, true,
//            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
//                if (position >= titles.size) {
//                    return@TabConfigurationStrategy
//                }
//                tab.text = titles[position]
//            }).attach()

        srl_wallets.setOnRefreshListener {
            pageNum = 1
            mViewModel.getBalanceList(-1, pageNum)
        }

        srl_wallets.setEnableLoadMore(true)
        srl_wallets.setOnLoadMoreListener {
            mViewModel.getBalanceList(-1, ++pageNum)
        }

        initRecycleView()
    }

    private fun initRecycleView() {
        rcv_wallets.run {
            layoutManager = LinearLayoutManager(activity).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = mAdapter
        }
    }

    override fun initData() {
        mViewModel.getBalance()
        mViewModel.getBalanceList(-1, pageNum)
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(this@WalletFragment.context).asLoading()
            balanceState.observe(this@WalletFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@WalletFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let { balance ->
                    xPopup.dismiss()
                    tv_money.text = balance.balance
                    tv_totalRevenue.text = balance.totalMoney

                    tv_withDraw.setOnClickListener {
                        activity!!.supportFragmentManager.beginTransaction()
                            .addToBackStack(this@WalletFragment.javaClass.name)
                            .hide(this@WalletFragment)//隐藏当前Fragment
                            .add(
                                R.id.frame_content,
                                WithdrawFragment.newInstance(balance.balance!!),
                                WithdrawFragment.javaClass.name
                            )
                            .commit()
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            balanceListState.observe(this@WalletFragment, Observer {

                if (it.showLoading) {
                    xPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@WalletFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let { walletList ->
                    xPopup.dismiss()
                    if (pageNum > 1) {
                        if (walletList.data!!.isNotEmpty()) {
                            for (ele in walletList.data) {
                                list.add(ele!!)
                            }
                            mAdapter.run {
                                setNewData(list)
                                notifyDataSetChanged()
                            }
                        } else {
                            --pageNum
                        }
                        srl_wallets.finishLoadMore(0)

                    } else {
                        list.clear()
                        for (ele in walletList.data!!) {
                            list.add(ele!!)
                        }
                        mAdapter.run {
                            setNewData(list)
                            notifyDataSetChanged()
                        }
                        srl_wallets.finishRefresh(0)
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    if (pageNum > 1) {
                        --pageNum
                        srl_wallets.finishLoadMore(0)
                    } else if (pageNum == 1) {
                        srl_wallets.finishRefresh(0)
                    }
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            activity!!.nav_view.visibility = View.GONE
            StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))
            mViewModel.getBalance()
            pageNum = 1
            mViewModel.getBalanceList(-1, pageNum)
        }
    }

}