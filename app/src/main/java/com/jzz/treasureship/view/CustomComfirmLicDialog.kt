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
import kotlinx.android.synthetic.main.layout_comfirm_lic.view.tv_cancel
import kotlinx.android.synthetic.main.view_boot_license_dialog.view.*


class CustomComfirmLicDialog(context: Context) : CenterPopupView(context) {

    private var alreadyNoticeLic by PreferenceUtils(PreferenceUtils.ALREADY_LIC, false)
    private var acceptLic by PreferenceUtils(PreferenceUtils.ACCEPT_LIC, false)
    override fun getImplLayoutId() = R.layout.view_boot_license_dialog

    override fun initPopupContent() {
        super.initPopupContent()

        val ssb = SpannableString(
            "感谢您选择宝舰医疗!我们非常重视您的个人信息和隐私保护。\n" +
                    "为了更好地保障您的个人权益，在您使用我们的产品前," +
                    "请务必审慎阅读《隐私政策》和《用户协议》内的所有条款," +
                    "尤其是:\n1.我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，以及您的用户权利等条款;" +
                    "\n2. 约定我们的限制责任、免责条款; \n3.其他以颜色或加粗进行标识的重要条款。\n如您对以上协议有任何疑问," +
                    "可通过客服与我们联系。您点击\"同意并继续”的行为即表示您已阅读完毕并同意以上协议的全部内容。" +
                    "如您同意以上协议内容，请点击\"同意并继续”，开始使用我们的产品和服务!"
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