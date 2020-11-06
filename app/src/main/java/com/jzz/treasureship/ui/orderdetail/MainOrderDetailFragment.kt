package com.jzz.treasureship.ui.orderdetail

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.util.ClipboardUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.OrderDetailsBean
import com.jzz.treasureship.ui.orderdetail.viewModel.MainOrderDeailViewModel
import com.jzz.treasureship.ui.orderdetail.viewModel.OrderDetailViewModel
import com.jzz.treasureship.ui.trace.TraceFragment
import com.jzz.treasureship.view.DialogSimpleList
import com.jzz.treasureship.view.ItemOrderDetailView
import com.lc.liuchanglib.cusotmView.LeftAndRightView
import com.lc.liuchanglib.ext.click
import com.lc.liuchanglib.ext.toColorSpan
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.ext.getResString
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_main_order_detail.*
import kotlinx.android.synthetic.main.include_title.*
import q.rorbin.badgeview.DisplayUtil

/**
 *@PackageName: com.jzz.treasureship.ui.orderdeatil
 *@Auth： 29579
 *@Description: 用于展示订单详情的主入口  **/


//0、待付款1、待发货2、已发货3、已收货、8、已完成9、退款中10、已退款11、已关闭 12、退款不通过
class MainOrderDetailFragment : BaseVMFragment<MainOrderDeailViewModel>() {

    private val mOrderDetailViewModel by activityViewModels<OrderDetailViewModel>()

    private val _9dp by lazy { DisplayUtil.dp2px(mContext, 9f) }
    private val _1dp by lazy { DisplayUtil.dp2px(mContext, 1f) }
    private val _14dp by lazy { DisplayUtil.dp2px(mContext, 14f) }
    override fun getLayoutResId(): Int = R.layout.fragment_main_order_detail

    override fun initVM(): MainOrderDeailViewModel = MainOrderDeailViewModel()
    override var mStatusColor: Int = R.color.statue_color
    override fun initView() {
        ll_view.setBackgroundColor(mContext.getResColor(R.color.statue_color))
        tv_title.text = "订单详情"
    }

    override fun initData() {
        mViewModel.getOrderDetail(mOrderDetailViewModel.id)
    }
    //0、待付款1、待发货2、已发货3、已收货、8、已完成9、退款中10、已退款11、已关闭 12、退款不通过
    override fun startObserve() {

        mViewModel.orderResult.observe(this){
            ToastUtils.showShort("确认收货")
        mViewModel.getOrderDetail(mOrderDetailViewModel.id)
        }
        mViewModel.mOrderDetailMsg.observe(this) {
            val title = arrayListOf<String>()
            it.mShopServerList?.forEach { title.add(it.mShopName ?: "") }
            iv_order_detail_call_phone click { view ->

                XPopup.Builder(mContext).asCustom(
                    DialogSimpleList(
                        mContext,
                        title
                    ).apply {
                        click = { i ->
                            val data =
                                Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + it.mShopServerList!![i].mShopPhone))
                            mContext.startActivity(data)
                            this.dismiss()
                        }
                    }
                ).show()
            }

            setBottomButton(
                it.mOrderStatus,
                it.mGoodsSkuList?.size ?: 0,
                it.mGoodsSkuList,
                it.mShopServerList?.get(0)?.mShopName ?: "未知",
                it.mId!!
            )
            showOrderSkuList(
                it.mGoodsSkuList,
                it.mShopServerList?.get(0)?.mShopName ?: "未知",
                it.mOrderNo,
                it.mGoodsMoney,
                it.mShippingMoney,
                it.mPayMoney
            )
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

            mViewModel.addCartResult.observe(this) {
                ToastUtils.showShort("加入购物车成功")
            }



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

    /**
     * 展示该订单的所有列表
     */
    private fun showOrderSkuList(
        skuList: List<OrderDetailsBean.GoodsSku>?,
        shopName: String?,
        orderNo: String,
        totalPrice: String,
        postPrice: String,
        realPrice: String
    ) {
        var isFirst = true
        skuList?.forEach {

            ll_order_deatils_goods.addView(ItemOrderDetailView(it, shopName, orderNo, isFirst, mContext).apply {
                setViewClickListener(R.id.tv_item_order_add_shop_car) {
                    mViewModel.addShopCart(this.mSkuId)
                }
                setViewClickListener(R.id.tv_item_order_apply_afterSale) {
                    mOrderDetailViewModel.singleOrderInfo = this
                    mOrderDetailViewModel.singleOrderInfo!!.mShopName = shopName
                    mFragmentManager.commit {
                        addToBackStack("1")
                        hide(this@MainOrderDetailFragment)
                        add(R.id.frame_content, AfterSaleFragment(), "AfterSaleFragment")
                    }
                }
                setViewClickListener(R.id.tv_item_order_apply_refund) {
                    mOrderDetailViewModel.singleOrderInfo = this
                    mOrderDetailViewModel.singleOrderInfo!!.mShopName = shopName
                    mFragmentManager.commit {
                        addToBackStack("1")
                        hide(this@MainOrderDetailFragment)
                        add(R.id.frame_content, ApplyRefundFragment(), "ApplyRefundFragment")
                    }
                }
            })
            isFirst = false
        }
        val totalParamars =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        totalParamars.setMargins(0, _9dp, 0, 0)
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
            ll_order_deatils_goods.addView(this, totalParamars)
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
            ll_order_deatils_goods.addView(this, totalParamars)
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
            ll_order_deatils_goods.addView(this, totalParamars)
        }

        View(mContext).apply {
            val ViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, _1dp)
            ViewParams.setMargins(0, _14dp, 0, 0)
            layoutParams = ViewParams
            setBackgroundColor(mContext.getResColor(R.color.background_grey))
            ll_order_deatils_goods.addView(this)
        }

        LeftAndRightView(mContext).apply {

            mRightValue.run {
                mTextMsg = "实付款：¥ ${realPrice}".toColorSpan(0 .. 3, mContext.getResColor(R.color.Home_text_normal))
                mTextBold = true
                mTextColor = mContext.getResColor(R.color.red_cc0814)
                mTextSize = 13f
                buildText()
            }
            ll_order_deatils_goods.addView(this, totalParamars)
        }


    }
//0、待付款1、待发货2、已发货3、已收货、8、已完成 9、退款中 10、已退款 11、已关闭 12、退款不通过
    /**
     * 用于控制底部按钮的显隐
     */
    private fun setBottomButton(statue: Int, count: Int, skuList: List<OrderDetailsBean.GoodsSku>?, shopName: String,orderId:Int) {
        when (statue) {

            0 -> {
                tv_imme_pay.visibility = View.VISIBLE
                tv_cancel_order.visibility = View.VISIBLE

            }

            8 -> {
                tv_lookup_logistics.visibility = View.VISIBLE
                tv_lookup_logistics click { mFragmentManager.commit {
                    addToBackStack("6")
                    hide(this@MainOrderDetailFragment)
                    add(R.id.frame_content, TraceFragment.newInstance(orderId), "TraceFragment")
                } }
            }

            11 -> {
                tv_restart_buy.visibility = View.VISIBLE
            }
            1 -> {
                if (count > 1)
                    tv_order_detail_BatchRefund.visibility = View.VISIBLE
                tv_order_detail_BatchRefund click {
                    mOrderDetailViewModel.mingleOrderInfo = skuList ?: arrayListOf()
                    mOrderDetailViewModel.mingleOrderInfo.forEach { it.mShopName = shopName }
                    mFragmentManager.commit {
                        addToBackStack("4")
                        replace(R.id.frame_content, ChoiceRefundFragment.newInstance(1))
                    }
                }
            }

            2 -> {
                if (count > 1)
                    tv_order_detail_BatchRefund.visibility = View.GONE
//                tv_order_detail_BatchRefund click {
//                    mOrderDetailViewModel.mingleOrderInfo = skuList ?: arrayListOf()
//                    mOrderDetailViewModel.mingleOrderInfo.forEach { it.mShopName = shopName }
//                    mFragmentManager.commit {
//                        addToBackStack("4")
//                        replace(R.id.frame_content, ChoiceRefundFragment.newInstance(2))
//                    }
//                }
                tv_ensureGet.visibility = View.VISIBLE

                tv_ensureGet click {
                    mViewModel.ensureOrderReceived(orderId)
                }

                tv_lookup_logistics.visibility = View.VISIBLE
                tv_lookup_logistics.layoutParams =
                    (tv_lookup_logistics.layoutParams as FrameLayout.LayoutParams).apply {
                        marginEnd = DisplayUtil.dp2px(mContext, 100f)
                    }
                tv_lookup_logistics click { mFragmentManager.commit {
                    addToBackStack("6")
                    hide(this@MainOrderDetailFragment)
                    add(R.id.frame_content, TraceFragment.newInstance(orderId), "TraceFragment")
                } }
            }

        }
    }


    private fun showEnsureDialog() {


    }

    enum class DialogType {


    }
}