package com.jzz.treasureship.ui.orderdetail

import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.orderdetail.viewModel.ApplyRefundViewModel
import com.jzz.treasureship.view.DialogHasSubmit
import com.lc.liuchanglib.ext.click
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_apply_refund.*

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 **/
class ApplyRefundFragment : BaseVMFragment<ApplyRefundViewModel>() {
    override fun getLayoutResId(): Int = R.layout.fragment_apply_refund

    override fun initVM(): ApplyRefundViewModel = ApplyRefundViewModel()

    override fun initView() {

    }

    override fun initData() {

    }

    override fun startObserve() {

    }

    override fun initListener() {
        linearLayout2 click {
            XPopup.Builder(mContext).asCustom(DialogHasSubmit(mContext))
                .show()
        }

    }
}