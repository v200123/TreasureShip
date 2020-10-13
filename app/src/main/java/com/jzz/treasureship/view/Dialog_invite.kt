package com.jzz.treasureship.view

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
R
ui.invite.InviteFragment
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_invita.view.*

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class Dialog_invite(context: Context) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int = R.layout.dialog_invita

    override fun onCreate() {
        super.onCreate()
        findViewById<ImageView>(R.id.iv_invite_close).setOnClickListener {
            this.dismiss()
        }
        view_invite.setOnClickListener {
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frame_content, InviteFragment.newInstance(), InviteFragment.javaClass.name)
                .commit()
            dismiss()
        }
    }

}