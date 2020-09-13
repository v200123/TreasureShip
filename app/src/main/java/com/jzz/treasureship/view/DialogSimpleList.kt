package com.jzz.treasureship.view

import android.content.Context
import android.widget.TextView
import com.jzz.treasureship.R
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.impl.BottomListPopupView
import com.xuexiang.xui.widget.button.roundbutton.RoundButton
import com.xuexiang.xui.widget.layout.XUILinearLayout

/**
 *@date: 2020/9/13
 *@describe:
 *@Auth: 29579
 **/
class DialogSimpleList(context: Context,var list:Array<String>) : BottomPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_simple_list
    }

    lateinit var click:(pposition:Int) -> Unit

    override fun onCreate() {
        super.onCreate()
        findViewById<XUILinearLayout>(R.id.xll_radius_01).apply {
            onDismiss()
        }
        findViewById<TextView>(R.id.tv_simple_01).apply {
            text = list[0]
            setOnClickListener { click(0) }
        }
        findViewById<TextView>(R.id.tv_simple_02).apply {
            text = list[1]
            setOnClickListener { click(1)  }
        }
    }

}
