package com.jzz.treasureship.ui.orderdetail

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.get
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.util.ClipboardUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.OrderDetailsBean
import com.jzz.treasureship.view.ItemOrderDetailView
import com.lc.liuchanglib.cusotmView.LeftAndRightView
import com.lc.liuchanglib.ext.click
import com.lc.liuchanglib.ext.toColorSpan
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.ext.getResString
import com.lc.mybaselibrary.ext.getResStringFormatter
import kotlinx.android.synthetic.main.fragment_main_order_detail.*
import kotlinx.android.synthetic.main.include_title.*
import q.rorbin.badgeview.DisplayUtil

/**
 *@PackageName: com.jzz.treasureship.ui.orderdeatil
 *@Auth： 29579
 *@Description: 用于展示订单详情的主入口  **/


//0、待付款1、待发货2、已发货3、已收货、8、已完成9、退款中10、已退款11、已关闭 12、退款不通过
class MainOrderDetailFragment : BaseVMFragment<MainOrderDeailViewModel>() {
    private val _9dp by lazy { DisplayUtil.dp2px(mContext,9f) }
    private val _1dp by lazy { DisplayUtil.dp2px(mContext,1f) }
    private val _14dp by lazy { DisplayUtil.dp2px(mContext,14f) }
    override fun getLayoutResId(): Int = R.layout.fragment_main_order_detail

    override fun initVM(): MainOrderDeailViewModel = MainOrderDeailViewModel()
    override var mStatusColor: Int = R.color.statue_color
    override fun initView() {
        ll_view.setBackgroundColor(mContext.getResColor(R.color.statue_color))
        tv_title.text = "订单详情"
    }

    override fun initData() {
        mViewModel.getOrderDetail()
    }

    override fun startObserve() {
        mViewModel.mOrderDetailMsg.observe(this) {
            setBottomButton(it.mOrderStatus, it.mGoodsSkuList?.size ?: 0)
            showOrderSkuList(it.mGoodsSkuList, it.mShopName, it.mOrderNo, it.mGoodsMoney, it.mShippingMoney,it.mPayMoney)
            textView21.text = it.getStatusMsg(mContext, iv_order_detail_img)
            tv_order_detail_address_name.setText(it.mReceiverName + "\t\t" + it.mReceiverMobile)
            tv_order_detail_address.setText(it.mReceiverAddress + "\t\t" + it.mReceiverDistrict)
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

    private fun showOrderSkuList(
        skuList: List<OrderDetailsBean.GoodsSku>?,
        shopName: String?,
        orderNo: String,
        totalPrice: String,
        postPrice: String,
        realPrice:String
    ) {
        var isFirst = true
        skuList?.forEach {
            ll_order_deatils_goods.addView(ItemOrderDetailView(it, shopName, orderNo, isFirst, mContext))
            isFirst = false
        }
        val totalParamars = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        totalParamars.setMargins(0,_9dp,0,0)
        //商品总额
        LeftAndRightView(mContext).apply {
            getLeftBuild()
                .run {
                    mTextMsg = mContext.getResString(R.string.order_price)
                    mTextColor = mContext.getResColor(R.color.black_666666)
                    mTextSize = 13f
                    buildText()
                }
            mRightValue.run {
                mTextMsg = "¥ ${totalPrice}"
                mTextColor = mContext.getResColor(R.color.black)
                mTextSize = 13f
                buildText()
            }
            ll_order_deatils_goods.addView(this,totalParamars)
        }
        //运费
        LeftAndRightView(mContext).apply {
            getLeftBuild()
                .run {
                    mTextMsg = mContext.getResString(R.string.post_price)
                    mTextColor = mContext.getResColor(R.color.black_666666)
                    mTextSize = 13f
                    buildText()
                }
            mRightValue.run {
                mTextMsg = "免运费"
                mTextBold = true
                mTextColor = mContext.getResColor(R.color.black)
                mTextSize = 13f
                buildText()
            }
            ll_order_deatils_goods.addView(this,totalParamars)
        }

        LeftAndRightView(mContext).apply {
            getLeftBuild()
                .run {
                    mTextMsg = mContext.getResString(R.string.taxes_price)
                    mTextColor = mContext.getResColor(R.color.black_666666)
                    mTextSize = 13f
                    buildText()
                }
            mRightValue.run {
                mTextMsg = "¥ ${postPrice}"
                mTextBold = true
                mTextColor = mContext.getResColor(R.color.black)
                mTextSize = 13f
                buildText()
            }
            ll_order_deatils_goods.addView(this,totalParamars)
        }

        View(mContext).apply {

            val ViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,_1dp)
            ViewParams.setMargins(0,_14dp,0,0)
            layoutParams = ViewParams
            setBackgroundColor(mContext.getResColor(R.color.red))
            ll_order_deatils_goods.addView(this)
        }

        LeftAndRightView(mContext).apply {

            mRightValue.run {
                mTextMsg =  "实付款：¥ ${realPrice}".toColorSpan(0..3,Color.GREEN)
                mTextBold = true
                mTextColor = mContext.getResColor(R.color.red_cc0814)
                mTextSize = 13f
                buildText()
            }
            ll_order_deatils_goods.addView(this,totalParamars)
        }





    }

    private fun setBottomButton(statue: Int, count: Int) {
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
                tv_lookup_logistics.layoutParams =
                    (tv_lookup_logistics.layoutParams as FrameLayout.LayoutParams).apply {
                        marginEnd = DisplayUtil.dp2px(mContext, 100f)
                    }
                tv_lookup_logistics click { ToastUtils.showShort("我应该查看物流") }
            }

        }

    }

}