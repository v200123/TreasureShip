package com.jzz.treasureship.ui.otherspay

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.Order
import com.jzz.treasureship.ui.doctordevice.AddDoctorAdvicesFragment
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.orders.OrdersViewModel
import com.king.zxing.util.CodeUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_others_pay.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*


class OthersPayFragment : BaseVMFragment<OrdersViewModel>() {

    private val mOrderFragment by lazy { OrdersFragment.newInstance("OthersPay") }
    private var mOrder: Order? = null
    private var mOrderStatus = 0
    private var mSkus: ArrayList<CartGoodsSku>? = null
    private var mTimer: Timer? = null

    companion object {
        fun newInstance(order: Order, cartGoodsSkus: ArrayList<CartGoodsSku>): OthersPayFragment {
            val f = OthersPayFragment()
            val bundle = Bundle()
            bundle.putSerializable("orderInfo", order)
            bundle.putParcelableArrayList("orderSkus", cartGoodsSkus)
            f.arguments = bundle
            return f
        }
    }

    override fun getLayoutResId() = R.layout.fragment_others_pay

    override fun initVM(): OrdersViewModel = getViewModel()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        //activity!!.nav_view.visibility = View.GONE
        ll_view.background = context!!.getDrawable(R.drawable.toolbar_bg)

        tv_title.text = "支付"

        rlback.setOnClickListener {
            if (mOrderStatus == 0){
                XPopup.Builder(context!!)
                    .asConfirm("温馨提示", "订单还没有支付成功，若此时退出，则无法填写医嘱", "取消", "继续返回", {
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_content, mOrderFragment, mOrderFragment.javaClass.name).commit()
                    }, {

                    }, false).show()
            }
        }

        arguments?.let {
            mOrder = it.getSerializable("orderInfo") as Order
            mSkus = it.getParcelableArrayList("orderSkus")
        }

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                GlobalScope.launch(Dispatchers.Main) {
                    mViewModel.queryOrderStatus(mOrder!!.orderId)
                }
            }
        }

        mTimer = Timer()
        mTimer!!.schedule(task, 0, 10 * 1000)

        mOrder?.let {
            Glide.with(context!!).load(CodeUtils.createQRCode(it.codeUrl, 600)).into(iv_QRcode)
            tv_totalPrice.text = "¥${it.totalFee}"
            tv_lowestPrice.text = "¥${it.totalFee}"
            tv_payPrice.text = "¥${it.totalFee}"


        }

        btn_atSomeone.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().hide(this)
                .add(
                    R.id.frame_content,
                    AddDoctorAdvicesFragment.newInstance(mOrder!!, mSkus!!),
                    AddDoctorAdvicesFragment.javaClass.name
                ).commit()
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel.apply {
            orderStatusState.observe(this@OthersPayFragment, androidx.lifecycle.Observer {

                it.showSuccess?.let { orderStatus ->
                    when (orderStatus.payStatus) {
                        0 -> {
                            //待付款,继续等待下一次轮训
                            mOrderStatus = 0
                        }
                        1 -> {
                            //付款成功
                            mOrderStatus = 1
                            activity!!.supportFragmentManager.beginTransaction().hide(this@OthersPayFragment)
                                .add(
                                    R.id.frame_content,
                                    AddDoctorAdvicesFragment.newInstance(mOrder!!, mSkus!!),
                                    AddDoctorAdvicesFragment.javaClass.name
                                ).commit()
                        }
                        else -> {

                        }
                    }
                }

                it.showError?.let { message ->
                    Log.d("orderStatusState failed", "订单状态查询失败！${message}")
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@OthersPayFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mTimer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancel()
    }
}