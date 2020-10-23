package com.jzz.treasureship.ui.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.observe
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.paypal.PaypalViewModel

import com.lc.mybaselibrary.start
import com.lxj.xpopup.core.BasePopupView
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.layout_pay_success.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PaySuccessActivity : BaseVMActivity<PaypalViewModel>() {

    companion object {
        const val orderMoney = "com.jzz.orderMoney"
    }

    private var showRedEnvelopeOpen: BasePopupView? = null
    override fun initVM(): PaypalViewModel = getViewModel()

    override fun getLayoutResId() = R.layout.layout_pay_success


    override fun initView() {
        tv_title.text = ""
        rlback.visibility = View.GONE
        tv_success_pay_money.text = "订单金额:\t" + intent.getFloatExtra(orderMoney, 0.0f)
        tv_back_money.setOnClickListener {
            mContext.start<MainActivity> {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
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
        mViewModel.redEnvelopOpen.observe(owner = this, onChanged = {
            showRedEnvelopeOpen?.dismiss()
            showRedEnvelopeOpen =
                App.dialogHelp.showRedEnvelopeOpen(it.mInviteRewardCount, it.mInviteRewardAmount, {
//                    mViewModel.getMoney()
                }, unChange = {
                    finish()
                    mContext.start<MainActivity> {
                        putExtra(MainActivity.gotoInvite, true)
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                }
                )
                {
                    mViewModel.getMoney()
                }
        })

        mViewModel.firstBean.observe(owner = this) {
            if (it.mStatus == 1) {
                App.dialogHelp.showRedEnvelopeClose(it.mInviteRewardCount + 1) {
                    mViewModel.getFirstRed()
                }
            }
        }
        mViewModel.redAmount.observe(owner = this) {
            showRedEnvelopeOpen = App.dialogHelp.showRedEnvelopeOpen(
                it.mInviteRewardCount,
                it.mInviteRewardAmount,
                {
//                    mViewModel.getMoney()
                }, unChange = {
                    finish()
                    mContext.start<MainActivity> {
                        putExtra(MainActivity.gotoInvite, true)
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                }
            ) {
                mViewModel.getMoney()

            }
        }
    }
}



