package com.jzz.treasureship.ui.activity

import android.content.Intent
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.paypal.PaypalViewModel
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.layout_pay_success.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PaySuccessActivity : BaseVMActivity<PaypalViewModel>() {

    companion object {
        const val isFirst = "com.jzz.isFirst"
    }

    override fun initVM(): PaypalViewModel = getViewModel()

    override fun getLayoutResId() = R.layout.layout_pay_success

    override fun initView() {


        tv_title.text = ""
        rlback.setOnClickListener {
            finish()
        }

        tv_checkOrder.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("goTo", "orders")
            startActivity(intent)
            finish()
        }
    }

    override fun initData() {
        mViewModel.getFirst()
    }

    override fun startObserve() {
        mViewModel.firstBean.observe(this, {
            if (it.mStatus == 1) {
                App.dialogHelp.showRedEnvelopeClose(it.mCount) {
                    mViewModel.getFirstRed()
                }
            }
        })
        mViewModel.redAmount.observe(this,{
            App.dialogHelp.showRedEnvelopeOpen(0,it) {

            }
        })
    }
}
