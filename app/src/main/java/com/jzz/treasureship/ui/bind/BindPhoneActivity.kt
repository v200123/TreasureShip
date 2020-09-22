package com.jzz.treasureship.ui.bind

import android.widget.TextView
import androidx.lifecycle.Observer
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.login.LoginViewModel
import com.jzz.treasureship.utils.CountDownTimerUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_bind_phone.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class BindPhoneActivity : BaseVMActivity<LoginViewModel>() {

    override fun getLayoutResId() = R.layout.activity_bind_phone

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {
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
                    finish()
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }
            })
        }
    }

}