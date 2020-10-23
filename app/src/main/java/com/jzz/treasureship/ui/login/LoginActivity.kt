package com.jzz.treasureship.ui.login

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.TagAliasCallback
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.model.bean.UserDialogInformationBean
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.bind.BindPhoneActivity
import com.jzz.treasureship.ui.register.RegisterActivity
import com.jzz.treasureship.utils.CountDownTimerUtils
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class LoginActivity : BaseVMActivity<LoginViewModel>() {
    val xPopup by lazy { XPopup.Builder(this@LoginActivity).asLoading() }
    private var mobile: String? = null
    var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)

    override fun getLayoutResId() = R.layout.activity_login
    private val countDownTimerUtils by lazy { CountDownTimerUtils(iv_getCode, 60 * 1000, 1000) }
    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {

        tv_title.text = "手机号登录"
        rlback.setOnClickListener {
            finish()
        }

        StateAppBar.setStatusBarLightMode(this, this.resources.getColor(R.color.white))

        ib_login.setOnClickListener {
            if (et_phoneNum.text.isNullOrBlank() or et_codeNum.text.isNullOrBlank()) {
                ToastUtils.showShort("请先输入电话号码/验证码")
                return@setOnClickListener
            }
            mViewModel.loginByCode(et_phoneNum.text.toString(), et_codeNum.text.toString())
        }

        iv_getCode.setOnClickListener {
            if (!et_phoneNum.text.isNullOrBlank()) {
                mViewModel.sendSmsCode(2, et_phoneNum.text.toString(), countDownTimerUtils, xPopup)
            } else {
                ToastUtils.showShort("请输入验证码")
            }
        }

        tv_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        ib_wxLogin.setOnClickListener {
            if (!App.iwxapi.isWXAppInstalled) {
                ToastUtils.showShort("未安装微信客户端，无法使用微信登录")
                return@setOnClickListener
            }
            val req: SendAuth.Req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "treasureship_wx_login"
            App.iwxapi.sendReq(req)
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel.apply {


            uiState.observe(this@LoginActivity, Observer {
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    if (JPushInterface.isPushStopped(this@LoginActivity)) {
                        JPushInterface.resumePush(this@LoginActivity)
                    }
                    setAlias(it.id!!)
                    mobile = it.mobile
                    finish()
                    if (mobile.isNullOrBlank()) {
                        MMKV.defaultMMKV().encode(it.id.toString(), UserDialogInformationBean())
                        startActivity(Intent(this@LoginActivity, BindPhoneActivity::class.java))
                    } else {
                        MMKV.defaultMMKV().encode(it.id.toString(), UserDialogInformationBean())
                        mContext.start<MainActivity> {
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                    }

                } ?: run {
                    xPopup.dismiss()
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }

                it.enableLoginButton
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
        if (wxCode.isNotBlank()) {
            mViewModel.wxLogin(wxCode)
        }
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private fun setAlias(id: Int) {
        // 调用 Handler 来异步设置别名
        Log.d("JIGUANG", "Prepare to setAlias")
        JPushInterface.setAlias(applicationContext, MSG_SET_ALIAS, id.toString())
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, id))
    }

    private val mAliasCallback = TagAliasCallback { code, alias, tags ->
        val logs: String
        when (code) {
            0 -> {
                logs = "Set tag and alias success"
                Log.i("JIGUANG", logs)

            }
            6002 -> {
                logs = "Failed to set alias and tags due to timeout. Try again after 60s."
                Log.i("JIGUANG", logs)
                // 延迟 60 秒来调用 Handler 设置别名
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60)
            }
            else -> {
                logs = "Failed with errorCode = $code"
                Log.e("JIGUANG", logs)
            }
        }
    }
    private val MSG_SET_ALIAS = 1001
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_SET_ALIAS -> {
                    Log.d("JIGUANG", "Set alias in handler.")
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(
                        applicationContext,
                        msg.obj.toString(),
                        null,
                        mAliasCallback
                    )
                }
                else -> Log.i("JIGUANG", "Unhandled msg - " + msg.what)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!login)
            mContext.start<MainActivity>()
    }

}
