package com.jzz.treasureship.ui.orderdetail

import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.util.ClipboardUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.lc.liuchanglib.ext.click
import com.lc.liuchanglib.ext.toColorSpan
import kotlinx.android.synthetic.main.fragment_main_order_detail.*
import kotlinx.android.synthetic.main.include_title.*
import q.rorbin.badgeview.DisplayUtil

/**
 *@PackageName: com.jzz.treasureship.ui.orderdeatil
 *@Auth： 29579
 *@Description: 用于展示订单详情的主入口  **/
class MainOrderDetailFragment : BaseVMFragment<MainOrderDeailViewModel>() {
    override fun getLayoutResId(): Int = R.layout.fragment_main_order_detail

    override fun initVM(): MainOrderDeailViewModel = MainOrderDeailViewModel()

    override fun initView() {
        tv_title.text = "订单详情"
    }

    override fun initData() {
        mViewModel.getOrderDetail()
    }

    override fun startObserve() {

        mViewModel.mOrderDetailMsg.observe(this) {
            setBottomButton(it.mOrderStatus,it.mGoodsSkuList?.size?:0)

            textView21.text = it.getStatusMsg(mContext, iv_order_detail_img)
            tv_order_detail_address_name.setText(it.mReceiverName + "\t\t\t" + it.mReceiverMobile)
            tv_order_detail_address.setText(it.mReceiverAddress + "\t\t\t" + it.mReceiverDistrict)
            tv_order_detail_number.text = ("订单编号: " + it.mOrderNo).toColorSpan(0 .. 5, R.color.black_666666)
            tv_order_detail_create_time.getLeftBuild()
                .apply { mTextMsg = ("下单时间: " + it.mCreateTime).toColorSpan(0 .. 5, R.color.black_666666) }.buildText()


            if (it.mPayTime != null) {
                tv_order_detail_pay_time.apply {
                    visibility = View.VISIBLE
                    val leftBuild = getLeftBuild()
                    leftBuild.mTextMsg = ("支付时间: " + it.mCreateTime).toColorSpan(0 .. 5, R.color.black_666666)
                    leftBuild.buildText()
                }
                tv_order_detail_pay_style.apply {
                    visibility = View.VISIBLE
                    val leftBuild = getLeftBuild()
                    leftBuild.mTextMsg = ("支付方式: " + it.mCreateTime).toColorSpan(0 .. 5, R.color.black_666666)
                    leftBuild.buildText()
                }
            }

//            if (it.mFinishTime != null) {
//                tv_order_detail_finish_time.apply {
//                    visibility = View.VISIBLE
//                    getLeftBuild().mTextMsg = ("成交时间: " + it.mFinishTime).toColorSpan(0 .. 5, R.color.black_666666)
//                    getLeftBuild().buildText()
//                }
//            }

            if (it.mOrderStatus == 3) {
                tv_order_detail_send_time.getLeftBuild()
                    .apply { mTextMsg = ("发货时间: " + it.mConsignTime).toColorSpan(0 .. 5, R.color.black_666666) }
                    .buildText()
            } else {
                tv_order_detail_send_time.visibility = View.GONE
            }
            if (it.mOrderStatus == 8) {
                tv_order_detail_finish_time.apply {
                    visibility = View.VISIBLE
                    getLeftBuild().mTextMsg = ("成交时间: " + it.mFinishTime).toColorSpan(0 .. 5, R.color.black_666666)
                    getLeftBuild().buildText()
                }
            }

            if (it.mOrderStatus == 11) {
                tv_order_detail_pay_time.visibility = View.VISIBLE
                tv_order_detail_pay_time.getLeftBuild().mTextMsg =
                    ("关闭时间: " + it.mFinishTime ?: "").toColorSpan(0 .. 5, R.color.black_666666)
                tv_order_detail_pay_time.getLeftBuild().buildText()
            }
        }
    }

    override fun initListener() {
        tv_order_detail_copy.setOnClickListener {
            ClipboardUtils.copyText(tv_order_detail_number.text.removePrefix("订单编号: "))
            ToastUtils.showShort("粘贴成功")
        }
    }


    fun setBottomButton(statue: Int, count: Int) {
        when (statue) {

            0 -> {
                tv_imme_pay.visibility = View.VISIBLE
                tv_cancel_order.visibility = View.VISIBLE

            }

            8 -> {
                tv_lookup_logistics.visibility = View.VISIBLE
            }

            11 -> {
                tv_restart_buy.visibility = View.VISIBLE

            }
            1 -> {
                if (count > 1)
                    tv_order_detail_BatchRefund.visibility = View.VISIBLE
                tv_order_detail_BatchRefund click { ToastUtils.showShort("我应该批量退货") }
            }

            2 -> {
                tv_ensureGet.visibility = View.VISIBLE
                tv_lookup_logistics.visibility = View.VISIBLE
                (tv_lookup_logistics.layoutParams as FrameLayout.LayoutParams).apply {
                    marginEnd = DisplayUtil.dp2px(mContext, 200f)
                }
                tv_lookup_logistics click { ToastUtils.showShort("我应该查看物流") }
            }

        }

    }

}