package com.jzz.treasureship.ui.wallet

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.WalletAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.DataXXX
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_wallet_vp.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class WalletVpFragment : BaseVMFragment<WalletViewModel>() {


    companion object {
        fun newInstance(position: Int): WalletVpFragment {
            val f = WalletVpFragment()
            val bundle = Bundle()
            bundle.putInt("walletVpPosition", position)
            f.arguments = bundle
            return f
        }
    }

    private val mAdapter by lazy { WalletAdapter() }
    private var mPosition: Int? = null
    private var pageNum = 1
    private val list: ArrayList<DataXXX> = ArrayList()

    override fun getLayoutResId() = R.layout.fragment_wallet_vp

    override fun initVM(): WalletViewModel = getViewModel()

    override fun initView() {
        arguments?.let {
            mPosition = it.getInt("walletVpPosition")
        }
        srl_wallets.setOnRefreshListener {
            pageNum = 1
            var status = -1
            when (mPosition) {
                //全部
                0 -> {
                    status = -1
                }
                //佣金
                1 -> {
                    status = 0
                }
                //个税扣除
                2 -> {
                    status = 2
                }
                //提现记录
                3 -> {
                    status = 1
                }
                //邀请返利
                4 -> {
                    status = 3
                }
            }
            mViewModel.getBalanceList(status, pageNum)
        }

        srl_wallets.setEnableLoadMore(true)
        srl_wallets.setOnLoadMoreListener {
            var status = -1
            when (mPosition) {
                //全部
                0 -> {
                    status = -1
                }
                //佣金
                1 -> {
                    status = 0
                }
                //个税扣除
                2 -> {
                    status = 2
                }
                //提现记录
                3 -> {
                    status = 1
                }
                //邀请返利
                4 -> {
                    status = 3
                }
            }
            mViewModel.getBalanceList(status, ++pageNum)
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
        val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
        if (isLogin) {
            pageNum = 1
            var status = -1
            when (mPosition) {
                //全部
                0 -> {
                    status = -1
                }
                //佣金
                1 -> {
                    status = 0
                }
                //个税扣除
                2 -> {
                    status = 2
                }
                //提现记录
                3 -> {
                    status = 1
                }
                //邀请返利
                4 -> {
                    status = 3
                }
            }
            mViewModel.getBalanceList(status, pageNum)
        } else {
            ToastUtils.showShort("当前未登录，请先进行登录后再进行操作！")
            //startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(this@WalletVpFragment.context).asLoading()
            balanceListState.observe(this@WalletVpFragment, Observer {

                if (it.showLoading) {
                    xPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@WalletVpFragment.context, LoginActivity::class.java))
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

}