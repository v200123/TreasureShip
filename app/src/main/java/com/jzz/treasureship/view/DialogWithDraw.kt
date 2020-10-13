package com.jzz.treasureship.view

import android.content.Context
R
ui.coupon.CouponShopActivity
import com.lc.mybaselibrary.start
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_withdraw_success.view.*

/**
 *@date: 2020/9/17
 *@describe:
 *@Auth: 29579
 **/
class DialogWithDraw(context: Context) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int = R.layout.dialog_withdraw_success

    override fun onCreate() {
        super.onCreate()
        iv_withdraw_close.setOnClickListener { this.dismiss() }
        view_goto_01.setOnClickListener { context.start<CouponShopActivity> {  }
        this.dismiss()}
    }

}