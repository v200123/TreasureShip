package com.jzz.treasureship.ui.bind

import android.widget.TextView
import androidx.lifecycle.Observer
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
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_bind_phone.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class BindPhoneActivity : BaseVMActivity<LoginViewModel>() {
    var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
    var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
    var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)

    override fun getLayoutResId() = R.layout.activity_bind_phone

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {
        App.CURRENT_USER = null
        login = false
        userInfo = ""
        wxCode = ""
        StateAppBar.setStatusBarLightMode(this, this.resources.getColor(R.color.white))
        btn_bind.setOnClickListener {
                if (et_phone.text.isNullOrBlank() or et_code.text.isNullOrBlank()) {
                ToastUtils.showShort("请先输入电话号码/验证码")
                return@setOnClickListener
            }
            mViewModel.bindMobile(et_phone.text.toString(), et_code.text.toString())
        }

        tv_sendSms.setOnClickListener {
            if (!et_phone.text.isNullOrBlank()) {
                CountDownTimerUtils(it as TextView, 60 * 1000, 1000).start()
            }
            mViewModel.sendSmsCode(4, et_phone.text.toString())
        }
    }

    override fun onDestroy() {

        super.onDestroy()


    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(this@BindPhoneActivity).asLoading()
            uiState.observe(this@BindPhoneActivity, Observer {
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    App.CURRENT_USER = it
                    login = true
                    userInfo = GsonUtils.toJson(it)
                    access = it.accessToken!!
                    finish()
                    mContext.start<MainActivity> {  }
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }
            })
        }
    }

}