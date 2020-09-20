package com.jzz.treasureship.ui.orders

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.OrdersVpAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.Data
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.trace.TraceFragment

import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import com.tencent.mm.opensdk.modelpay.PayReq
import kotlinx.android.synthetic.main.fragment_orders_vp.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

//position:-1"全部", 0"待付款", 1"待发货", 2"已发货", 3"已收货", 8"已完成", 9"退款中",10"已退款",11"已关闭"
class OrdersVpFragment(position: Int) : BaseVMFragment<OrdersViewModel>() {

    private val mAdapter by lazy { OrdersVpAdapter() }
    private val mPosition: Int = position
    private var pageNum = 1
    private var itemPosition = -1
    private val list: ArrayList<Data> = ArrayList()

    override fun getLayoutResId() = R.layout.fragment_orders_vp

    override fun initVM(): OrdersViewModel = getViewModel()

    override fun initView() {

        srl_orders.setOnRefreshListener {
            pageNum = 1
            var status = -1
            when (mPosition) {
                //全部
                0 -> {
                    status = -1
                }
                //待付款
                1 -> {
                    status = 0
                }
                //待发货
                2 -> {
                    status = 1
                }
                //已发货
                3 -> {
                    status = 2
                }
                //已收货
//                4 -> {
//                    status = 3
//                }
                //已完成
                4 -> {
                    status = 8
                }
                //退款中
//                6 -> {
//                    status = 9
//                }
                //已退款
                7 -> {
                    status = 10
                }
                //已关闭
                8 -> {
                    status = 11
                }
            }
            mViewModel.getOrderList(-1, null, status, -1, pageNum)
        }
        srl_orders.setEnableLoadMore(true)
        srl_orders.setOnLoadMoreListener {
            var status = -1
            when (mPosition) {
                //全部
                0 -> {
                    status = -1
                }
                //待付款
                1 -> {
                    status = 0
                }
                //待发货
                2 -> {
                    status = 1
                }
                //已发货
                3 -> {
                    status = 2
                }
                //已收货
//                4 -> {
//                    status = 3
//                }
                //已完成
                4 -> {
                    status = 8
                }
                //退款中
                6 -> {
                    status = 9
                }
                //已退款
                7 -> {
                    status = 10
                }
                //已关闭
                8 -> {
                    status = 11
                }
            }
            mViewModel.getOrderList(-1, null, status, -1, ++pageNum)
        }

        initRecycleView()
    }

    private fun initRecycleView() {
        rcv_orders.run {
            layoutManager = LinearLayoutManager(activity).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            mAdapter.run {
                onItemChildClickListener = this@OrdersVpFragment.onItemChildClickListener
            }

            rcv_orders.adapter = mAdapter
        }
    }

    private val onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        itemPosition = position
        when (view.id) {
            R.id.tv_go_pay -> {
                mViewModel.payByOrder(list[position].orderId!!)
            }
            R.id.tv_sure_goods -> {
                mViewModel.sureReceice(list[position].orderId!!)
            }
            R.id.tv_ckwl -> {
                activity!!.supportFragmentManager.beginTransaction().addToBackStack(OrdersFragment.javaClass.name).hide(this.parentFragment!!).add(
                    R.id.frame_content,
                    TraceFragment.newInstance(list[position].orderId!!),
                    TraceFragment.javaClass.name
                ).commit()
            }
            R.id.tv_ask_refund -> {
                XPopup.Builder(view.context).asConfirm("确认退款","确认要对该订单进行退货吗？","取消","退货",{
                    mViewModel.askRefund(list[position].orderNo!!)
                },{},false).show()
            }
        }
    }

    override fun initData() {
        val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
        if (isLogin) {
            pageNum = 1
            var status = -1
            when (mPosition) {
                //全部
                0 -> {
                    status = -1
                }
                //待付款
                1 -> {
                    status = 0
                }
                //待发货
                2 -> {
                    status = 1
                }
                //已发货
                3 -> {
                    status = 2
                }
                //已收货
//            4 -> {
//                status = 3
//            }
                //已完成
                4 -> {
                    status = 8
                }
                //退款中
                6 -> {
                    status = 9
                }
                //已退款
                7 -> {
                    status = 10
                }
                //已关闭
                8 -> {
                    status = 11
                }
            }
            mViewModel.getOrderList(-1, null, status, -1, pageNum)
        } else {
            ToastUtils.showShort("当前未登录，请先进行登录后再进行操作！")
            //startActivity(Intent(context, LoginActivity::class.java))
        }

    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(this@OrdersVpFragment.context).asLoading("订单列表获取中")
            ordersState.observe(this@OrdersVpFragment, Observer {

                if (it.showLoading) {
                    xPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@OrdersVpFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let { orders ->
                    xPopup.dismiss()
                    if (pageNum > 1) {
                        if (orders.data!!.isNotEmpty()) {
                            for (ele in orders.data) {
                                list.add(ele!!)
                            }
                            mAdapter.run {
                                setNewData(list)
                                notifyDataSetChanged()
                            }
                            srl_orders.finishLoadMore(0)
                        } else {
                            --pageNum
                            srl_orders.finishLoadMore(0)
                        }

                    } else {
                        list.clear()
                        for (ele in orders.data!!) {
                            list.add(ele!!)
                        }
                        mAdapter.run {
                            setNewData(list)
                            notifyDataSetChanged()
                        }
                        srl_orders.finishRefresh(0)
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    if (pageNum > 1) {
                        --pageNum
                        srl_orders.finishLoadMore(0)
                    } else if (pageNum == 1) {
                        srl_orders.finishRefresh(0)
                    }
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })


            val goPayPopup = XPopup.Builder(this@OrdersVpFragment.context).asLoading()
            ordersGoPayState.observe(this@OrdersVpFragment, Observer {

                if (it.showLoading) {
                    goPayPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@OrdersVpFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let { ordersGoPayBean ->
                    goPayPopup.dismiss()
                    if (!App.iwxapi.isWXAppInstalled) {
                        ToastUtils.showShort("未安装微信客户端，无法使用微信支付")
                    } else {
                        val req = PayReq()
                        req.appId = ordersGoPayBean.appid
                        req.partnerId = ordersGoPayBean.partnerId
                        req.prepayId = ordersGoPayBean.prepayId
                        req.nonceStr = ordersGoPayBean.nonceStr
                        req.timeStamp = ordersGoPayBean.timestamp
                        req.packageValue = ordersGoPayBean.packageStr
                        req.sign = ordersGoPayBean.sign
                        //这里就发起调用微信支付了
                        App.iwxapi.sendReq(req)
                    }
                }

                it.showError?.let { message ->
                    goPayPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            val sureReceivePopup = XPopup.Builder(this@OrdersVpFragment.context).asLoading()
            comfirmReceiveState.observe(this@OrdersVpFragment, Observer {

                if (it.showLoading) {
                    sureReceivePopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@OrdersVpFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess.let { order ->
                    sureReceivePopup.dismiss()
                    ToastUtils.showShort("已收货")
                    //mViewModel.getOrderList(-1, null, 2, -1, pageNum)
                }

                it.showError?.let { message ->
                    sureReceivePopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            refundState.observe(this@OrdersVpFragment, Observer {
                if (it.showLoading) {
                    sureReceivePopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@OrdersVpFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess.let { it ->
                    sureReceivePopup.dismiss()
                    if(it == null){
                        ToastUtils.showShort("已申请退款！")
                        list[itemPosition].orderStatus = 9
                        mAdapter.notifyDataSetChanged()
                    }else{
                        ToastUtils.showShort("退款申请失败！")
                    }

//                    pageNum = 1
//                    var status = -1
//                    when (mPosition) {
//                        //全部
//                        0 -> {
//                            status = -1
//                        }
//                        //待付款
//                        1 -> {
//                            status = 0
//                        }
//                        //待发货
//                        2 -> {
//                            status = 1
//                        }
//                        //已发货
//                        3 -> {
//                            status = 2
//                        }
//                        //已收货
////            4 -> {
////                status = 3
////            }
//                        //已完成
//                        4 -> {
//                            status = 8
//                        }
//                        //退款中
//                        6 -> {
//                            status = 9
//                        }
//                        //已退款
//                        7 -> {
//                            status = 10
//                        }
//                        //已关闭
//                        8 -> {
//                            status = 11
//                        }
//                    }
//                    mViewModel.getOrderList(-1, null, status, -1,pageNum)
                }

                it.showError?.let { message ->
                    sureReceivePopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })
        }
    }

    override fun initListener() {

    }
}