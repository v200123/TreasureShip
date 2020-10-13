package com.jzz.treasureship.ui.register

import android.app.ProgressDialog
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
R
base.BaseVMActivity
databinding.ActivityRegisterBinding
model.bean.UserDialogInformationBean
ui.activity.MainActivity
ui.license.LicenseActivity
ui.login.LoginViewModel
utils.CountDownTimerUtils
import com.lxj.xpopup.XPopup
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class RegisterActivity : BaseVMActivity<LoginViewModel>() {
    override fun getLayoutResId() = R.layout.activity_register
    private val countDown by lazy { CountDownTimerUtils(iv_getCode, 60 * 1000, 1000) }
    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {

        tv_title.text = "注册账号"
        rlback.setOnClickListener {
            finish()
        }

        (mBinding as ActivityRegisterBinding).viewModel = mViewModel

        StateAppBar.setStatusBarLightMode(this, this.resources.getColor(R.color.white))
        iv_getCode.setOnClickListener {
            if (!et_phoneNum.text.isNullOrBlank()) {
                mViewModel.sendSmsCode(
                    1,
                    et_phoneNum.text.toString(),
                    countDown,
                    XPopup.Builder(mContext).asLoading()
                )
            } else {
                ToastUtils.showShort("请输入验证码")
            }

        }

        tv_alreadyRegis.setOnClickListener {
            et_phoneNum.text!!.clear()
            et_codeNum.text.clear()
            cb_lic.isChecked = false
            finish()
        }

        ib_regis.setOnClickListener {
            if (!cb_lic.isChecked) {
                ToastUtils.showLong("请仔细阅读并同意下方的《用户协议》和《隐私政策》后再进行操作")
                return@setOnClickListener
            }
            if (et_phoneNum.text.isNullOrBlank() or et_codeNum.text.isNullOrBlank()) {
                ToastUtils.showLong("请输入手机号码和验证码后注册")
                return@setOnClickListener
            }
            mViewModel.register(et_phoneNum.text.toString(), et_codeNum.text.toString())
        }

        val spannableString = SpannableString("我已阅读并同意《用户协议》和《隐私政策》")
        val intent = Intent(this, LicenseActivity::class.java)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                intent.putExtra("title", "用户协议")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.blue_normal)
                //去掉下划线
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 7, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                intent.putExtra("title", "隐私政策")
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                //设置颜色
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.blue_normal)
                //去掉下划线
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan2, 14, 20, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)


        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(this@RegisterActivity, R.color.blue_normal))
        spannableString.setSpan(colorSpan, 7, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        val colorSpan2 =
            ForegroundColorSpan(ContextCompat.getColor(this@RegisterActivity, R.color.blue_normal))
        spannableString.setSpan(colorSpan2, 14, 20, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        tv_lic.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }

    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel.apply {

            mLoginUser.observe(this@RegisterActivity, Observer {
                it?.run {
                    mViewModel.register(et_phoneNum.text.toString(), et_codeNum.text.toString())
                }
            })

            uiState.observe(this@RegisterActivity, Observer {
                if (it.showProgress) showProgressDialog()
                it.showSuccess?.let {
                    countDown.start()
                    dismissProgressDialog()
                    JPushInterface.setAlias(applicationContext, 1001, it.id.toString())
                    finish()
                    MMKV.defaultMMKV().encode(it.id.toString(), UserDialogInformationBean())

                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                }

                it.showError?.let { err ->
                    dismissProgressDialog()
                    ToastUtils.showShort(err)
                }
            })
        }
    }

    private var progressDialog: ProgressDialog? = null
    private fun showProgressDialog() {
        if (progressDialog == null)
            progressDialog = ProgressDialog(this@RegisterActivity)
        progressDialog!!.setMessage("正在注册，注册成功后将为您自动登录")
        progressDialog!!.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

}