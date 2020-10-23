package com.jzz.treasureship.ui.auth

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import cn.ycbjie.ycstatusbarlib.utils.StatusBarUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.model.api.HttpHelp
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.activity_confirm_success.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 *@date: 2020/9/15
 *@describe:
 *@Auth: 29579
 **/
class AuthConfirmSuccessActivity : AppCompatActivity(R.layout.activity_confirm_success) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_title.text = "审核中"
        rlback.setOnClickListener {
            this.start<AuthenticationActivity> {
                putExtra(
                    AuthenticationActivity.NeedFinish,
                    "exit"
                )
            }
        }
        rl_title.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        setStatueColor()

        lifecycleScope.launchWhenStarted {
            val root = JSONObject()
            val body = JSONObject()

            root.put("body", body)

            val requestBody = root.toString().toRequestBody("application/json".toMediaTypeOrNull())
            launch {
                val userInfo = HttpHelp.getRetrofit().getUserInfo(requestBody)
                if(userInfo.success)
                {
                    if(userInfo.result!!.mAppCampaignType == 1)
                    {
                        imageView8.visibility = View.VISIBLE
                        imageView6.visibility = View.VISIBLE
                        imageView7.visibility = View.VISIBLE
                        imageView3.visibility = View.VISIBLE
                        tv_tips_01.visibility = View.VISIBLE
                    }
                }
            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.start<AuthenticationActivity> { putExtra(AuthenticationActivity.NeedFinish,"exit") }
    }


    fun setStatueColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
        StatusBarUtils.StatusBarLightMode(this)

    }

}
