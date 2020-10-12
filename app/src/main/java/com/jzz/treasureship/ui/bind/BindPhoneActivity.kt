package com.jzz.treasureship.ui.bind

import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.login.LoginViewModel
import com.jzz.treasureship.utils.CountDownTimerUtils
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_bind_phone.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class BindPhoneActivity : BaseVMActivity<LoginViewModel>() {
    var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
    var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
    var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    val xPopup  by lazy { XPopup.Builder(mContext).asLoading()}


    private val countDown by lazy { CountDownTimerUtils(tv_sendSms, 60 * 1000, 1000) }
    override fun getLayoutResId() = R.layout.activity_bind_phone

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {
        App.CURRENT_USER = null
        login = false
        userInfo = ""
        wxCode = ""
        StateAppBar.setStatusBarLightMode(this, getResColor(R.color.white))
        btn_bind.setOnClickListener {
                if (et_phone.text.isNullOrBlank() or et_code.text.isNullOrBlank()) {
                ToastUtils.showShort("请先输入电话号码/验证码")
                return@setOnClickListener
            }
            mViewModel.bindMobile(et_phone.text.toString(), et_code.text.toString())
        }

        tv_sendSms.setOnClickListener {
            if (!et_phone.text.isNullOrBlank()) {
                mViewModel.sendSmsCode(4, et_phone.text.toString(), countDown,xPopup)
            }
            else{
                ToastUtils.showShort("请输入验证码")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.apply {
            uiState.observe(this@BindPhoneActivity, Observer { it ->
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {user->
                    xPopup.dismiss()
                    JPushInterface.setAlias(mContext,1001,user.id.toString())
                    App.CURRENT_USER = user
                    login = true
                    userInfo = GsonUtils.toJson(user)
                    access = user.accessToken!!
                    finish()
                    mContext.start<MainActivity> {  }
                }

                if(it is LoginViewModel.LoginUiModel)
                {
                    xPopup.dismiss()

                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }
            })
        }
    }

}