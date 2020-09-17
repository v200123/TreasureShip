package com.jzz.treasureship.view

import android.content.Context
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.auth.AuthInformationActivity
import com.lc.mybaselibrary.start
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_no_identification.view.*

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class Dialog_no_auth(context: Context) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int = R.layout.dialog_no_identification
    override fun onCreate() {
        super.onCreate()
        btn_positive.setOnClickListener {
            context.start<AuthInformationActivity> {  }
        }
        tv_negative.setOnClickListener { this.dismiss() }
    }
}