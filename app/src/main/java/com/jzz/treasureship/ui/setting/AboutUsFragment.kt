package com.jzz.treasureship.ui.setting

import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.jzz.treasureship.BuildConfig
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.ui.license.LicenseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_about_us.*
import kotlinx.android.synthetic.main.fragment_about_us.tv_lic
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class AboutUsFragment : BaseVMFragment<HomeViewModel>() {

    companion object {
        fun newInstance(): AboutUsFragment {
            return AboutUsFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_about_us

    override fun initVM(): HomeViewModel = getViewModel()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE

        tv_title.text = "关于我们"

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        val spannableString = SpannableString("《用户协议》|《隐私政策》")
        val intent = Intent(context, LicenseActivity::class.java)

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                intent.putExtra("title", "用户协议")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = ContextCompat.getColor(context!!, R.color.blue_normal)
                //去掉下划线
                ds.isUnderlineText = true
            }
        }
        spannableString.setSpan(clickableSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                intent.putExtra("title", "隐私政策")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = ContextCompat.getColor(context!!, R.color.blue_normal)
                //去掉下划线
                ds.isUnderlineText = true
            }
        }
        spannableString.setSpan(clickableSpan2, 7, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)


        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.blue_normal))
        spannableString.setSpan(colorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.blue_normal))
        spannableString.setSpan(colorSpan2, 7, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        tv_versionName.text = "V${BuildConfig.VERSION_NAME}"
        textView2.text =
            "宝舰，是由北京金紫竹科技有限公司运营的医疗整合平台，是经过国家食品药品监督局批准的经营性网络平台。宝舰具有强大的信息资源优势、完善的产品追溯能力、完善的物流配送体系，为广大器械、医药界同仁提供优质信息服务和产品教育服务。宝舰立志打造优质的医疗保健信息并提供先进的互联网培育应用模式，全力促进本行业的进步和发展。"

        tv_lic.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun initListener() {
    }


}
