package com.jzz.treasureship.view

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.license.LicenseActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.layout_comfirm_lic.view.tv_cancel
import kotlinx.android.synthetic.main.view_boot_license_dialog.view.*


class CustomComfirmLicDialog(context: Context) : CenterPopupView(context) {

    private var alreadyNoticeLic by PreferenceUtils(PreferenceUtils.ALREADY_LIC, false)
    private var acceptLic by PreferenceUtils(PreferenceUtils.ACCEPT_LIC, false)
    override fun getImplLayoutId() = R.layout.view_boot_license_dialog
    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context)*0.8).toInt()
    }

    override fun initPopupContent() {
        super.initPopupContent()

        val ssb = SpannableString(
            "感谢您选择宝舰医疗!我们非常重视您的个人信息和隐私保护。\n" +
                    "为了更好地保障您的个人权益，在您使用我们的产品前," +
                    "请务必审慎阅读《隐私政策》和《用户协议》内的所有条款"
        )
        val intent = Intent(App.CONTEXT, LicenseActivity::class.java)

        val start: Int = ssb.indexOf("《") //第一个出现的位置

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                intent.putExtra("title", "隐私政策")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = ContextCompat.getColor(context, R.color.blue_normal)
                //去掉下划线
                ds.isUnderlineText = false
            }
        }
        ssb.setSpan(clickableSpan, start, start + 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val end: Int = ssb.lastIndexOf("《") //最后一个出现的位置
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                intent.putExtra("title", "用户协议")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = ContextCompat.getColor(context, R.color.blue_normal)
                //去掉下划线
                ds.isUnderlineText = false
            }
        }
        ssb.setSpan(clickableSpan2, end, end + 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        tv_content.apply {
            text = ssb
            movementMethod = LinkMovementMethod.getInstance()
        }


        tv_sure.setOnClickListener {
            dismiss()
            alreadyNoticeLic = true
            acceptLic = true
        }

        tv_cancel.setOnClickListener {
            dismiss()
            alreadyNoticeLic = false
            acceptLic = false
        }
    }
}
