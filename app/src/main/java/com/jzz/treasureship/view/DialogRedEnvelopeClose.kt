package com.jzz.treasureship.view

import android.content.Context
import android.view.View
import com.jzz.treasureship.R
import com.jzz.treasureship.view.viewGroup.BaseDialog
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_red_envelope_close.view.*

/**
 *@date: 2020/9/16
 *@describe:注意这里的
 *@Auth: 29579
 **/
class DialogRedEnvelopeClose(context: Context,var count:Int,var block:() -> Unit) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int  = R.layout.dialog_red_envelope_close
    override fun onCreate() {
        super.onCreate()
        tv_redEnvelopeClose_count.text = "您还有${count}次抽奖机会"
        findViewById<BaseDialog>(R.id.dialog_open_red_close).setOnClickListener {
            this.dismiss()
        }
        findViewById<View>(R.id.view_close).setOnClickListener {
            this.dismiss()
            block()
        }

    }

}