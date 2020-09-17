package com.jzz.treasureship.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.jzz.treasureship.R
import com.lxj.xpopup.core.BottomPopupView
import com.xuexiang.xui.widget.layout.XUILinearLayout

/**
 *@date: 2020/9/13
 *@describe:
 *@Auth: 29579
 **/
class DialogSimpleList(context: Context, var list: Array<String>) : BottomPopupView(context) {
    override fun getImplLayoutId(): Int {
        return  R.layout.dialog_simple_list
    }

    lateinit var click: (pposition: Int) -> Unit

    override fun onCreate() {
        super.onCreate()
        findViewById<XUILinearLayout>(R.id.xll_radius_01).apply {
            setOnClickListener {  this@DialogSimpleList.dismiss()}
            list.forEachIndexed { i: Int, s: String ->
                val commonView =
                    LayoutInflater.from(context).inflate(R.layout.common_simple_text, this, false)
                commonView.findViewById<TextView>(R.id.tv_common_simple_text).text = s
                if(i == list.size -1)
                {
                    commonView.findViewById<View>(R.id.common_simple_view).visibility = View.GONE
                }
                commonView.setOnClickListener { click(i) }
                addView(commonView,i)
            }


        }

        findViewById<Button>(R.id.btn_simple_cancel).setOnClickListener {
            this.dismiss()
        }
//        findViewById<TextView>(R.id.tv_simple_01).apply {
//            text = list[0]
//            setOnClickListener { click(0) }
//        }
//        findViewById<TextView>(R.id.tv_simple_02).apply {
//            text = list[1]
//            setOnClickListener { click(1)  }
//        }
    }

}
