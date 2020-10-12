package com.jzz.treasureship.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import cn.ycbjie.ycstatusbarlib.utils.StatusBarUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomComfirmLicDialog
import com.jzz.treasureship.view.CustomReComfirmLicDialog
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlin.system.exitProcess


class SplashActivity : AppCompatActivity() {
    private val FIRST_BOOT by PreferenceUtils(PreferenceUtils.FIRST_BOOT, true)
    private var alreadyNoticeLic by PreferenceUtils(PreferenceUtils.ALREADY_LIC, false)
    private var acceptLic by PreferenceUtils(PreferenceUtils.ACCEPT_LIC, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
        StatusBarUtils.StatusBarLightMode(this)
        setContentView(R.layout.activity_splash)
        if (FIRST_BOOT) {
            //首次启动，进入引导页
            if (!alreadyNoticeLic) {
                XPopup.Builder(this).dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView) {
                            super.onDismiss(popupView)
                            //如果通过了用户的隐私权限协议
                            if (acceptLic) {
                                startActivity(Intent(this@SplashActivity, WelcomeGuideActivity::class.java))
                                finish()
                                return
                            } else {
                                XPopup.Builder(this@SplashActivity).dismissOnTouchOutside(false)
                                    .dismissOnBackPressed(false)
                                    .setPopupCallback(object : SimpleCallback() {
                                        override fun onDismiss(popupView: BasePopupView) {
                                            super.onDismiss(popupView)
                                            if (acceptLic) { startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    WelcomeGuideActivity::class.java
                                                )
                                            )
                                                finish()
                                                return
                                            } else {
                                                exitProcess(0)
                                            }
                                        }
                                    })
                                    .asCustom(CustomReComfirmLicDialog(this@SplashActivity)).show()
                            }
                        }
                    })
                    .asCustom(CustomComfirmLicDialog(this)).show()
            } else {
                startActivity(Intent(this@SplashActivity, WelcomeGuideActivity::class.java))
                finish()
                return
            }
        } else {
            Handler().postDelayed(
                {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2 * 1000
            )
        }
    }


}
