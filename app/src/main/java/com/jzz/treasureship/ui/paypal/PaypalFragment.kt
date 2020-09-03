package com.jzz.treasureship.ui.paypal

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CartSelectGoodsAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.Address
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.Order
import com.jzz.treasureship.ui.activity.PaySuccessActivity
import com.jzz.treasureship.ui.address.ChooseAddressFragment
import com.jzz.treasureship.ui.license.LicenseActivity
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.otherspay.OthersPayFragment
import com.jzz.treasureship.utils.MoneyUtil
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import com.tencent.mm.opensdk.modelpay.PayReq
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_paypal.*
import kotlinx.android.synthetic.main.include_title.*
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.math.BigDecimal

class PaypalFragment : BaseVMFragment<PaypalViewModel>() {

    private val mChooseAddressFragment by lazy { ChooseAddressFragment.newInstance() }
    private val cartSelectedAdapter by lazy { CartSelectGoodsAdapter() }
    private val isAudit by PreferenceUtils(PreferenceUtils.AUDIT_STATUS, -2)
    var tmpAddress by PreferenceUtils(PreferenceUtils.SELECTED_ADDRESS, "")
    //private val isAudit = 1
    private var mOrderId = -1
    //默认地址
    private var mAddress: Address? = null
    private var selectedAddress: Address? = null
    //支付类型：-1找人代付；0微信支付
    private var mPayType = 0
    private var mOrderSkus = ArrayList<CartGoodsSku>()

    companion object {
        fun newInstance(type: Int, shopsJson: String): PaypalFragment {
            val f = PaypalFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putString("shops", shopsJson)
            f.arguments = bundle
            return f
        }

        fun newInstance(type: Int, count: Int, skuId: Int): PaypalFragment {
            val f = PaypalFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putInt("count", count)
            bundle.putInt("skuId", skuId)
            f.arguments = bundle
            return f
        }
    }

    override fun getLayoutResId() = R.layout.fragment_paypal

    override fun initVM(): PaypalViewModel = getViewModel()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        activity!!.nav_view.visibility = View.GONE

        ll_view.background = context!!.getDrawable(R.drawable.toolbar_bg)
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))

        tv_title.text = "结算"

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

//        val spannableString = SpannableString("点击支付则表示您同意《用户购买合同》")

//        val clickableSpan: ClickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                mViewModel.getDocContent("")
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                //设置颜色
//                ds.color = ContextCompat.getColor(this@PaypalFragment.activity!!, R.color.garyf3)
//                //去掉下划线
//                ds.isUnderlineText = false
//            }
//        }
//
//        spannableString.setSpan(
//            clickableSpan,
//            1,
//            spannableString.length,
//            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
//        )
        tv_agreement.setOnClickListener {
            mViewModel.getDocContent("")
        }
    }

    override fun initData() {
        if (!tmpAddress.isBlank()) {
            selectedAddress = GsonUtils.fromJson(tmpAddress, Address::class.java)
        }
        arguments?.let {
            //获取默认地址
            mViewModel.getPayAddress()
            when (it.get("type")) {
                //直接购
                1 -> mViewModel.directBuy(it.getInt("count"), it.getInt("skuId"))
                //购物车结算
                2 -> it.getString("shops")?.let { shops ->
                    mViewModel.cartSettlement(shops)
                }
                else -> {
                    ToastUtils.showShort("未知结算类型")
                }
            }
        }
    }

    override fun startObserve() {
        mViewModel.apply {

            agreementUiState.observe(this@PaypalFragment, Observer {
                it.showSuccess?.let {
                    val intent = Intent(this@PaypalFragment.activity, LicenseActivity::class.java)
                    intent.putExtra("title", "用户购买合同")
                    intent.putExtra("BuyAgreement", it.content)
                    startActivity(intent)
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@PaypalFragment.context, LoginActivity::class.java))
                    }
                }
            })

            payAddressUiState.observe(this@PaypalFragment, Observer {
                it.showSuccess?.let { address ->
                    if (selectedAddress != null) {
                        layout_noAddress.visibility = View.GONE
                        layout_Address.visibility = View.VISIBLE

                        tv_name.text = selectedAddress!!.consignee
                        tv_phone.text = selectedAddress!!.mobile

                        tv_address_province.text = selectedAddress!!.provinceName
                        tv_address_city.text = selectedAddress!!.cityName
                        tv_address_districtName.text = selectedAddress!!.districtName
                        tv_address_detail.text = selectedAddress!!.address
                        mAddress = selectedAddress
                    } else {
                        layout_noAddress.visibility = View.GONE

                        layout_Address.visibility = View.VISIBLE

                        tv_name.text = address.consignee
                        tv_phone.text = address.mobile

                        tv_address_province.text = address.provinceName
                        tv_address_city.text = address.cityName
                        tv_address_districtName.text = address.districtName
                        tv_address_detail.text = address.address
                        mAddress = address
                        selectedAddress = address
                    }
                }

                if (it.showSuccess == null) {
                    layout_noAddress.visibility = View.VISIBLE

                    layout_Address.visibility = View.GONE
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@PaypalFragment.context, LoginActivity::class.java))
                    }
                }
            })

            directBuyUiState.observe(this@PaypalFragment, Observer { directBuyUiModel ->

                directBuyUiModel.showSuccess?.let {
                    //返回的收货地址不为空，显示收货地址
                    if (selectedAddress != null) {
                        layout_noAddress.visibility = View.GONE
                        layout_Address.visibility = View.VISIBLE

                        tv_name.text = selectedAddress!!.consignee
                        tv_phone.text = selectedAddress!!.mobile

                        tv_address_province.text = selectedAddress!!.provinceName
                        tv_address_city.text = selectedAddress!!.cityName
                        tv_address_districtName.text = selectedAddress!!.districtName
                        tv_address_detail.text = selectedAddress!!.address
                    } else {
                        if (it.receiveAddress != null) {

                            layout_noAddress.visibility = View.GONE
                            layout_Address.visibility = View.VISIBLE

                            tv_name.text = it.receiveAddress.consignee
                            tv_phone.text = it.receiveAddress.mobile

                            tv_address_province.text = it.receiveAddress.provinceName
                            tv_address_city.text = it.receiveAddress.cityName
                            tv_address_districtName.text = it.receiveAddress.districtName
                            tv_address_detail.text = it.receiveAddress.address

                            selectedAddress = it.receiveAddress

                        } else {
                            // //返回的收货地址为空，显示收货地址
                            layout_noAddress.visibility = View.GONE
                            layout_Address.visibility = View.VISIBLE

                            if (mAddress == null) {
                                layout_noAddress.visibility = View.VISIBLE
                                layout_Address.visibility = View.GONE
                            } else {
                                tv_name.text = mAddress!!.consignee
                                tv_phone.text = mAddress!!.mobile

                                tv_address_province.text = mAddress!!.provinceName
                                tv_address_city.text = mAddress!!.cityName
                                tv_address_districtName.text = mAddress!!.districtName
                                tv_address_detail.text = mAddress!!.address

                                selectedAddress = mAddress
                            }
                        }
                    }

                    rv_goodsList.run {
                        layoutManager = LinearLayoutManager(activity).also {
                            it.orientation = LinearLayoutManager.VERTICAL
                        }

                        val list: ArrayList<CartGoodsSku?> = ArrayList()
                        for (shop in it.shops!!) {
                            for (sku in shop?.cartGoodsSkuList!!) {
                                sku?.let {
                                    list.add(it)
                                }
                            }
                        }
                        adapter = cartSelectedAdapter

                        cartSelectedAdapter.setNewData(list)
                        cartSelectedAdapter.notifyDataSetChanged()

                    }

                    val tmpTotal = BigDecimal(it.totalMoney.toString())
                    if (tmpTotal.compareTo(BigDecimal.ZERO) == 0) {
                        tv_totalPrice.text = "¥0.00"
                    } else {
                        tv_totalPrice.text =
                            "¥${tmpTotal.stripTrailingZeros().toPlainString()}"
                    }

//                    tv_lowestPrice.text = tv_totalPrice.text
                    tv_selfPurchaseOffer.text = "-¥${it.orderDiscountMoney.toString()}"
                    tv_selfPurchaseDiscount.text = "自购优惠（${it.orderDiscount}%)"

                    val tmpTax = BigDecimal(MoneyUtil.moneyAdd(it.dutyPrice.toString(), "0"))
                    if (tmpTax.compareTo(BigDecimal.ZERO) == 0) {
                        tv_tax.text = "¥0.00"
                    } else {
                        tv_tax.text = "¥${tmpTax.stripTrailingZeros().toPlainString()}"
                    }

                    val payPrice = MoneyUtil.moneyAdd(it.totalMoney.toString(), "0")
                    val tmpPayPrice = BigDecimal(payPrice)
                    if (tmpPayPrice.compareTo(BigDecimal.ZERO) == 0) {
                        tv_payPrice.text = "¥0.00"
                    } else {
                        tv_payPrice.text = "¥${MoneyUtil.moneySub(tmpPayPrice, BigDecimal(it.orderDiscountMoney.toString())).stripTrailingZeros().toPlainString()}"
//                        tv_payPrice.text = "¥${tmpPayPrice.stripTrailingZeros().toPlainString()}"
                    }

                    //立即购买（微信支付）
                    btn_wechatPay.setOnClickListener { _ ->
                        if (selectedAddress == null) {
                            ToastUtils.showShort("请先新增/选择收货地址")
                            return@setOnClickListener
                        }
                        when (isAudit) {
                            -1 -> {
                                ToastUtils.showShort("您还未进行资质认证，请现在设置中提交资质认证并通过认证后再进行购买")
                            }
                            0 -> {
                                ToastUtils.showShort("资质审核中，请联系相关人员通过审核后再进行购买")
                            }
                            1 -> {

                                mPayType = 1
                                val body: JSONObject = JSONObject()
                                val orderParams: JSONArray = JSONArray()

                                it.shops?.let { shops ->
                                    for (shop in shops) {
                                        shop?.let { shop ->
                                            val orderParamItem: JSONObject = JSONObject()
                                            shop.cartGoodsSkuList?.let { cartGoodsSkus ->
                                                val details: JSONArray = JSONArray()
                                                for (cartGoodsSku in cartGoodsSkus) {
                                                    cartGoodsSku?.let {

                                                        val detail: JSONObject = JSONObject()

                                                        detail.put("cartId", cartGoodsSku.cartId)
                                                        detail.put("skuId", cartGoodsSku.skuId)
                                                        detail.put("count", cartGoodsSku.count)

                                                        details.put(detail)
                                                    }
                                                }
                                                orderParamItem.put("details", details)
                                                orderParamItem.put("shopId", shop.shopId)
                                            }
                                            orderParams.put(orderParamItem)
                                        }
                                        body.put("orderParams", orderParams)
                                    }
                                    body.put("orderType", 1)
                                    body.put("receiveAddressId", selectedAddress?.id ?: -1)
                                }
                                mViewModel.createOrder(body)
                            }
                            2 -> {
                                ToastUtils.showShort("资质审核未通过，请联系相关人员/重新提交资质认证资料，资质审核通过后再进行购买")
                            }
                        }
                    }

                    //推荐Ta
//                    btn_atSomeone.setOnClickListener { view ->
//                        when (isAudit) {
//                            -1 -> {
//                                ToastUtils.showShort("您还未进行资质认证，请现在设置中提交资质认证并通过认证后再进行购买")
//                            }
//                            0 -> {
//                                ToastUtils.showShort("资质审核中，请联系相关人员通过审核后再进行购买")
//                            }
//                            1 -> {
//                                mPayType = -1
//                                val body: JSONObject = JSONObject()
//                                val orderParams: JSONArray = JSONArray()
//
//                                val orderParamItem: JSONObject = JSONObject()
//                                val details: JSONArray = JSONArray()
//                                it.shops?.let { shops ->
//                                    for (shop in shops) {
//                                        shop?.let { shop ->
//                                            shop.cartGoodsSkuList?.let { cartGoodsSkus ->
//                                                for (cartGoodsSku in cartGoodsSkus) {
//                                                    cartGoodsSku?.let {
//
//                                                        mOrderSkus.add(cartGoodsSku)
//
//                                                        val detail: JSONObject = JSONObject()
//
//                                                        detail.put("cartId", cartGoodsSku.cartId)
//                                                        detail.put("skuId", cartGoodsSku.skuId)
//                                                        detail.put("count", cartGoodsSku.count)
//
//                                                        details.put(detail)
//                                                    }
//                                                }
//                                                orderParamItem.put("details", details)
//                                                orderParamItem.put("shopId", shop.shopId)
//                                            }
//                                            orderParams.put(orderParamItem)
//                                        }
//                                        body.put("orderParams", orderParams)
//                                    }
//
//                                    body.put("orderType", 0)
//                                    body.put("receiveAddressId", selectedAddress?.id ?: -1)
//                                }
//
//                                mViewModel.createOrder(body)
//                            }
//                            2 -> {
//                                ToastUtils.showShort("资质审核未通过，请联系相关人员/重新提交资质认证资料，资质审核通过后再进行购买")
//                            }
//                        }
//                    }
                }

                directBuyUiModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                directBuyUiModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@PaypalFragment.context, LoginActivity::class.java))
                    }
                }
            })

            //订单状态
            orderStatusState.observe(this@PaypalFragment, Observer { orderStatusModel ->
                orderStatusModel.showSuccess?.let {
                    if (it.payStatus != 1) {
                        XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false)
                            .asConfirm("订单提示", "您的订单还没有支付成功", "订单列表", "继续支付", {
                                //继续支付
                                val body: JSONObject = JSONObject()

                                body.put("orderId", mOrderId)
                                body.put("orderType", 1)
                                body.put("receiveAddressId", selectedAddress?.id ?: -1)
                                mViewModel.createOrder(body)
                            }, {
                                //订单列表
                                activity!!.supportFragmentManager.beginTransaction().hide(this@PaypalFragment)
                                    .add(
                                        R.id.frame_content,
                                        OrdersFragment.newInstance("paypal"),
                                        OrdersFragment.javaClass.name
                                    )
                                    .commit()
                            }, false).show()
                    } else {
                        mOrderId = -1
                        startActivity(Intent(context, PaySuccessActivity::class.java))
                    }
                }

                orderStatusModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                orderStatusModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        startActivity(Intent(this@PaypalFragment.context, LoginActivity::class.java))
                    }
                }
            })

            //订单信息返回
            orderState.observe(this@PaypalFragment, Observer { orderModel ->
                orderModel.showSuccess?.let {
                    mOrderId = it.orderId
                    when (mPayType) {
                        //推荐Ta，代付
                        -1 -> {
                            activity!!.supportFragmentManager.beginTransaction()
                                .addToBackStack(PaypalFragment.javaClass.name)
                                .hide(this@PaypalFragment)//隐藏当前Fragment
                                .add(
                                    R.id.frame_content,
                                    OthersPayFragment.newInstance(it, mOrderSkus),
                                    OthersPayFragment.javaClass.name
                                )
                                .commit()
                        }
                        //立即微信支付
                        1 -> {
                            weChatPay(it)
                        }
                        else -> Log.e("wtf", "unknow pay type:${mPayType}")
                    }
                }

                orderModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                orderModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        startActivity(Intent(this@PaypalFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {

        layout_noAddress.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(PaypalFragment.javaClass.name)
                .hide(this@PaypalFragment)//隐藏当前Fragment
                .add(
                    R.id.frame_content,
                    mChooseAddressFragment,
                    mChooseAddressFragment.javaClass.name
                )
                .commit()
        }

        layout_Address.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(PaypalFragment.javaClass.name)
                .hide(this@PaypalFragment)//隐藏当前Fragment
                .add(
                    R.id.frame_content,
                    mChooseAddressFragment,
                    mChooseAddressFragment.javaClass.name
                )
                .commit()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val tmpAddress2 by PreferenceUtils(PreferenceUtils.SELECTED_ADDRESS, "")
            tmpAddress = tmpAddress2
            activity!!.nav_view.visibility = View.GONE

            StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))

            if (!tmpAddress.isBlank()) {
                selectedAddress = GsonUtils.fromJson(tmpAddress, Address::class.java)
            } else {
                selectedAddress = null
            }
            mViewModel.getPayAddress()
        }
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))
    }

    private fun weChatPay(order: Order) {
        //继续支付
        if (!App.iwxapi.isWXAppInstalled) {
            ToastUtils.showShort("未安装微信客户端，无法使用微信支付")
        } else {
            val req = PayReq()
            req.appId = order.appid
            req.partnerId = order.partnerId
            req.prepayId = order.prepayId
            req.nonceStr = order.nonceStr
            req.timeStamp = order.timestamp
            req.packageValue = order.packageStr
            req.sign = order.sign
            //这里就发起调用微信支付了
            App.iwxapi.sendReq(req)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mOrderId != -1) {
            mViewModel.queryOrderStatus(mOrderId)
        }
    }
}
