package com.jzz.treasureship.ui.msg

import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment

import org.koin.androidx.viewmodel.ext.android.getViewModel

class MsgVpFragment(position: Int) : BaseVMFragment<MsgViewModel>() {
    override fun getLayoutResId() = R.layout.layout_msgvp

    override fun initVM(): MsgViewModel = getViewModel()

    override fun initView() {
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun initListener() {
    }

}