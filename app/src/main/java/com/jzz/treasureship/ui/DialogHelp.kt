package com.jzz.treasureship.ui

import android.content.Context
import com.jzz.treasureship.view.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class DialogHelp(var context: Context) {
    /**
     * 未认证的界面
     */
    fun showType() {
        XPopup.Builder(context).hasShadowBg(true).asCustom(Dialog_no_auth(context))
            .show()
    }

    //显示认证成功的界面
    fun showSuccess(name: String,block: () -> Unit) {
        XPopup.Builder(context).hasShadowBg(true).setPopupCallback(object : SimpleCallback() {
            override fun onDismiss(popupView: BasePopupView?) {
                super.onDismiss(popupView)
                block()
            }
        }).asCustom(Dialog_approve_success
            (context,name))
            .show()
    }

    //显示邀请人的界面
    fun showInvite() {
        XPopup.Builder(context).hasShadowBg(true).asCustom(Dialog_invite(context)).show()
    }

    /**
     * 红包关闭的界面
     */
    fun showRedEnvelopeClose(count: Int,text:()->Unit) {
        if (count > 0)
            XPopup.Builder(context).hasShadowBg(true).asCustom(DialogRedEnvelopeClose(context, count) { text() }).show()
    }

    fun showRedEnvelopeOpen(count: Int, money: String, dismiss: () -> Unit,unChange:(view:BasePopupView)->Unit, block:()->Unit):BasePopupView {
        val show = XPopup.Builder(context).hasShadowBg(true)
            .setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    dismiss()
                }
            })
            .asCustom(DialogRedEnvelopOpen(context, count, money).apply {
                gotoLottery = { block() }
                noChange = {unChange(it)}
            })
            .show()
        return show
    }

    fun showWithdrawSuccess(){
        XPopup.Builder(context).hasShadowBg(true).asCustom(DialogWithDraw(context)).show()
    }

}