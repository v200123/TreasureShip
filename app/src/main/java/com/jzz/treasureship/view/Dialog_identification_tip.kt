package com.jzz.treasureship.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.jzz.treasureship.R
import com.lxj.xpopup.core.CenterPopupView
import com.xuexiang.xui.widget.button.roundbutton.RoundButton
import com.xuexiang.xui.widget.dialog.materialdialog.CustomMaterialDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

/**
 *@date: 2020/9/7
 *@describe: 用于未认证的弹窗提示
 *@Auth: 29579
 **/
class Dialog_identification_tip(context: Context) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_no_identification
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<View>(R.id.btn_positive).setOnClickListener {
            Log.d("TAG", "dialog onCreate: 跳转到宁外一个界面 ")
        }
        findViewById<View>(R.id.iv_idenDialog_close).setOnClickListener {
            this.dismiss()
        }
 findViewById<View>(R.id.tv_negative).setOnClickListener {
           this.dismiss()
 }
    }





}
