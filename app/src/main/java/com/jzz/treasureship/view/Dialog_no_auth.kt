package com.jzz.treasureship.view

import android.content.Context
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.auth.AuthenticationActivity
import com.lc.mybaselibrary.start
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_no_identification.view.*

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class Dialog_no_auth(context: Context,val type:Int) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int = R.layout.dialog_no_identification
    override fun onCreate() {
        super.onCreate()
        if(type == 2){
            textView8.text = "平台认证后可使用完整功能"
        }

        btn_positive.setOnClickListener {
            context.start<AuthenticationActivity> {  }
            dismiss()
        }
        tv_negative.setOnClickListener { this.dismiss() }
        iv_idenDialog_close.setOnClickListener { this.dismiss() }
    }
}