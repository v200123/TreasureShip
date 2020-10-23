package com.jzz.treasureship.ui.license

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.jzz.treasureship.R

import kotlinx.android.synthetic.main.activity_license.*
import kotlinx.android.synthetic.main.include_title.*

class LicenseActivity : AppCompatActivity() {

    private val REGIS_AGREEMENT = "http://bj.jzzchina.com/agreement/registration.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        StateAppBar.setStatusBarLightMode(this, this.resources.getColor(R.color.white))
        rlback.setOnClickListener {
            finish()
        }

        webview.settings.defaultTextEncodingName = "UTF-8"
        when (intent.getStringExtra("title")) {
            "用户协议" -> {
                tv_title.text = "注册协议"
//                webview.loadDataWithBaseURL(
//                    null, this.resources.getString(R.string.user_agreement),
//                    "text/html", "UTF-8", null
//                )
                webview.loadUrl(REGIS_AGREEMENT)
            }
            "隐私政策" -> {
                tv_title.text = "隐私政策"

//                webview.loadDataWithBaseURL(
//                    null, this.resources.getString(R.string.privacy_policy),
//                    "text/html", "UTF-8", null
//                )
                webview.loadUrl(REGIS_AGREEMENT)
            }
            "用户购买合同" -> {
                tv_title.text = "用户购买合同"
                val agreement = intent.getStringExtra("BuyAgreement")
                when (agreement) {
                    "http://",
                    "https://",
                    "HTTP://",
                    "HTTPS://" -> {
                        webview.loadUrl(agreement)
                    }
                    else -> {
                        webview.loadDataWithBaseURL(
                            null, agreement,
                            "text/html", "UTF-8", null
                        )
                    }
                }
            }
        }
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url)
                return true
            }
        }

    }
}
