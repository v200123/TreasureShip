package com.jzz.treasureship.ui.activity

import android.content.Intent
import android.view.View
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.paypal.PaypalViewModel
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.layout_pay_success.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PaySuccessActivity : BaseVMActivity<PaypalViewModel>() {

    companion object {
        const val orderMoney = "com.jzz.orderMoney"
    }

    override fun initVM(): PaypalViewModel = getViewModel()

    override fun getLayoutResId() = R.layout.layout_pay_success


    override fun initView() {
        tv_title.text = ""
        rlback.visibility = View.GONE

        tv_success_pay_money.text = "订单金额:"+intent.getStringExtra(orderMoney)
        tv_back_money.setOnClickListener { mContext.start<MainActivity> {  } }

        tv_checkOrder.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
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
                App.dialogHelp.showRedEnvelopeClose(it.mCount+it.mInviteRewardCount) {
                    mViewModel.getFirstRed()
                }
            }
        })
        mViewModel.redAmount.observe(this,{
            App.dialogHelp.showRedEnvelopeOpen(0,it) {
                this.finish()
                mContext.start<MainActivity> { putExtra(MainActivity.gotoInvite,true) }
            }
        })
    }

    override fun onResume() {
        super.onResume()
//        App.dialogHelp.showRedEnvelopeOpen(0,10f) {
//            mContext.start<MainActivity> { putExtra(MainActivity.gotoInvite,true) }
//        }
    }
}
