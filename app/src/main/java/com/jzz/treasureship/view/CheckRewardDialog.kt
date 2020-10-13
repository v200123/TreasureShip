package com.jzz.treasureship.view

import android.content.Context
R
utils.PreferenceUtils
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_already_reward.view.*

import kotlinx.android.synthetic.main.dialog_questions.view.layout_content

class CheckRewardDialog(context: Context, amount: Double) :
    CenterPopupView(context) {

    private var go2Wallet by PreferenceUtils(PreferenceUtils.goto_wallet, false)
    private val mAmount = amount
    override fun getImplLayoutId() = R.layout.dialog_already_reward

    override fun initPopupContent() {
        super.initPopupContent()

        tv_reward.text = "¥ ${mAmount}"

        layout_content.setOnClickListener {
            go2Wallet = true
            dismiss()
        }

        layout_close.setOnClickListener {
            go2Wallet = false
            dismiss()
        }
    }
}