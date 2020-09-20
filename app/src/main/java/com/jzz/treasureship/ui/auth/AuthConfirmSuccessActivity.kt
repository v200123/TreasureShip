package com.jzz.treasureship.ui.auth

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import cn.ycbjie.ycstatusbarlib.utils.StatusBarUtils
import com.jzz.treasureship.R
import kotlinx.android.synthetic.main.include_title.*

/**
 *@date: 2020/9/15
 *@describe:
 *@Auth: 29579
 **/
class AuthConfirmSuccessActivity : AppCompatActivity(R.layout.activity_confirm_success) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_title.text = "审核中"
        iv_leftBack.setOnClickListener { finish() }
        rl_title.setBackgroundColor(Color.parseColor("#FFD92A20"))

        setStatueColor()

    }

    fun setStatueColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.red_db32223))
        StatusBarUtils.StatusBarLightMode(this)

    }

}
