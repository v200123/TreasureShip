package com.jzz.treasureship.view

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.license.LicenseActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.layout_comfirm_lic.view.*
import kotlin.system.exitProcess


class CustomReComfirmLicDialog(context: Context) : CenterPopupView(context) {

    private var alreadyNoticeLic by PreferenceUtils(PreferenceUtils.ALREADY_LIC, false)
    private var acceptLic by PreferenceUtils(PreferenceUtils.ACCEPT_LIC, false)
    override fun getImplLayoutId() = R.layout.layout_comfirm_lic

    override fun initPopupContent() {
        super.initPopupContent()

        tv_title.text = "提示"

        tv_1.text = "不同意将无法使用我们的产品和服务，并会退出APP!"

        tv_agree.text = "同意并使用"
        tv_agree.setOnClickListener {
            dismiss()
            alreadyNoticeLic = true
            acceptLic = true
        }

        tv_cancel.text = "不同意并退出"
        tv_cancel.setOnClickListener {
            dismiss()
            alreadyNoticeLic = false
            acceptLic = false
        }
    }
}