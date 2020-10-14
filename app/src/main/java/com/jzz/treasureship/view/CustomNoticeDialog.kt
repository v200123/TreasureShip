package com.jzz.treasureship.view

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.viewModelModule
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.dialog_add_category.view.*
import kotlinx.android.synthetic.main.layout_cancle_alarm.view.*

class CustomNoticeDialog(context: Context) : CenterPopupView(context) {

    private var cancleNotice by PreferenceUtils(PreferenceUtils.CANCLE_NOTICE, false)

    override fun getImplLayoutId() = R.layout.layout_cancle_alarm

    override fun initPopupContent() {
        super.initPopupContent()

        layout_dissmiss.setOnClickListener {
            cancleNotice = false
            this.dismiss()
        }

        layout_confirm.setOnClickListener {
            cancleNotice = true
            dismiss()
        }
    }

}