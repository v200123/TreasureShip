package com.jzz.treasureship.view

import android.content.Context
R
import com.lxj.xpopup.impl.FullScreenPopupView

/**
 *@date: 2020/9/17
 *@describe:
 *@Auth: 29579
 **/
class DialogFullTips(context: Context) : FullScreenPopupView(context) {
    override fun getImplLayoutId(): Int = R.layout.layout_unauth_tips
}