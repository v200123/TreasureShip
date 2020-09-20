package com.jzz.treasureship.ui.invite

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.InvitedAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.DataXXXX
import com.jzz.treasureship.ui.login.LoginActivity
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_invited_detail.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class InviteDetailsFragment : BaseVMFragment<InviteViewModel>() {
    companion object {
        fun newInstance(): InviteDetailsFragment {
            return InviteDetailsFragment()
        }
    }

    private val mAdapter by lazy { InvitedAdapter() }
    private var pageNum = 1
    private val list: ArrayList<DataXXXX> = ArrayList()

    override fun getLayoutResId() = R.layout.fragment_invited_detail

    override fun initVM(): InviteViewModel = getViewModel()

    override fun initView() {
        tv_title.text = "邀请好友明细"
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        srl_users.setOnRefreshListener {
            pageNum = 1
            mViewModel.getInvitedList(pageNum)
        }
        srl_users.setEnableLoadMore(true)
        srl_users.setOnLoadMoreListener {
            mViewModel.getInvitedList(++pageNum)
        }

        rcv_users.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            adapter = mAdapter
        }
    }

    override fun initData() {
        mViewModel.getInvitedList()
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).asLoading()
            invitedsState.observe(this@InviteDetailsFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    if (pageNum > 1) {
                        if (it.data!!.isNotEmpty()) {
                            for (ele in it.data) {
                                list.add(ele!!)
                            }
                            mAdapter.run {
                                setNewData(list)
                                notifyDataSetChanged()
                            }
                        } else {
                            //第二页没内容，不增加
                            --pageNum
                        }
                        srl_users.finishLoadMore(0)
                    } else {
                        list.clear()
                        for (ele in it.data!!) {
                            list.add(ele!!)
                        }
                        mAdapter.run {
                            setNewData(list)
                            notifyDataSetChanged()
                        }
                        srl_users.finishRefresh(0)
                    }
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    if (pageNum > 1) {
                        srl_users.finishLoadMore(0)
                    } else if (pageNum == 1) {
                        srl_users.finishRefresh(0)
                    }
                    ToastUtils.showShort(err)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@InviteDetailsFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
    }

}