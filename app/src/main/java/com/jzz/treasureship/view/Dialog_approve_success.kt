package com.jzz.treasureship.view

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.withdraw.WithdrawActivity
import com.lc.mybaselibrary.start
import com.lxj.xpopup.core.CenterPopupView

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class Dialog_approve_success(context: Context,var userName:String) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int =  R.layout.dialog_approve_success

    override fun onCreate() {
        super.onCreate()
        findViewById<Button>(R.id.btn_approve_money).setOnClickListener {
            this.dismiss()
            context.start<WithdrawActivity> {  }
        }
        findViewById<Button>(R.id.btn_approve_buy).setOnClickListener {
            ToastUtils.showShort("還沒設置")
        }
        findViewById<ImageView>(R.id.iv_approve_success).setOnClickListener {
                this.dismiss()
        }
        setName()
    }

   private fun setName(){
        findViewById<TextView>(R.id.tv_approve_name).text = "恭喜您：$userName\n您的资质已认证成功"


    }
}