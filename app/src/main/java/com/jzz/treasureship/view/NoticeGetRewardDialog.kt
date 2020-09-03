package com.jzz.treasureship.view

import android.content.Context
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_click_to_get_reward.view.*

class NoticeGetRewardDialog(context: Context,viewmodel:HomeViewModel) :
    CenterPopupView(context) {

    private var openAndGetReward by PreferenceUtils(PreferenceUtils.open_reward, false)
    private val mViewModel = viewmodel

    override fun getImplLayoutId() = R.layout.dialog_click_to_get_reward

    override fun initPopupContent() {
        super.initPopupContent()

        iv_clickOpenReward.setOnClickListener {
            openAndGetReward = true
            mViewModel.getReward()
            dismiss()
        }

        layout_close.setOnClickListener {
            openAndGetReward = false
            dismiss()
        }
    }
}