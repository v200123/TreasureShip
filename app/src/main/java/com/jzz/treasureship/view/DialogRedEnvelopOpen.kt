package com.jzz.treasureship.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.widget.TextView
import com.jzz.treasureship.R
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_red_envelope_open.view.*

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class DialogRedEnvelopOpen(context: Context,var count: Int, var money: Float) : CenterPopupView(context) {
    lateinit var gotoLottery:()->Unit
    override fun getImplLayoutId(): Int {
        if(count >0)
            return R.layout.dialog_red_envelope_open
        else
            return R.layout.dialog_red_envelope_open_nochance

    }


    override fun onCreate() {
        super.onCreate()

        if(count == 0)
        {
            view_click.setOnClickListener {
                this.dismiss()
                gotoLottery()
            }
        }else{
            view_click.setOnClickListener {
               this.dismiss()
            }
        }

        val spannableString = SpannableString("${money}元")
        spannableString.setSpan(
            AbsoluteSizeSpan(32, true),
            spannableString.length - 1,
            spannableString.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        tv_redEnvelope_count.setText(spannableString)
        findViewById<TextView>(R.id.tv_envelope_lose).text = "已存入余额 可直接提现至微信\n您还有${count}次抽奖机会"
        iv_envelop_open_close.setOnClickListener { this.dismiss() }

    }


}