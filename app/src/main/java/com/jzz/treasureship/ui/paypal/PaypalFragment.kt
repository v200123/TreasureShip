package com.jzz.treasureship.ui.paypal

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CartSelectGoodsAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.Coupon
import com.jzz.treasureship.model.bean.Order
import com.jzz.treasureship.model.bean.ReceiveAddress
import com.jzz.treasureship.ui.activity.PaySuccessActivity
import com.jzz.treasureship.ui.address.ChooseAddressFragment
import com.jzz.treasureship.ui.license.LicenseActivity
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.otherspay.OthersPayFragment
import com.jzz.treasureship.utils.MoneyUtil
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.DialogChoiceCoupon
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.ext.getResDrawable
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.tencent.mm.opensdk.modelpay.PayReq
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
    private lateinit var mCoupon: MutableList<Coupon>

    //    private  var mCouponId = 0
    private var mPayDialog : BasePopupView? =null
    //private val isAudit = 1
    private var mOrderId = -1
    override var mStatusColor: Int = R.color.blue_normal

    //默认地址
    private var mAddress: ReceiveAddress? = null
    private var selectedAddress: ReceiveAddress? = null
    private lateinit var mOccupationBean: Coupon

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
//            bundle.putString("shopId", shopId)
//            bundle.putString("shopName", shopName)
            f.arguments = bundle
            return f
        }
    }

    override fun getLayoutResId() = R.layout.fragment_paypal

    override fun initVM(): PaypalViewModel = getViewModel()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        //activity!!.nav_view.visibility = View.GONE
        ll_view.background = mContext.getResDrawable(R.drawable.toolbar_bg)
        StateAppBar.setStatusBarLightMode(
            this.activity,
            mContext.getResColor(R.color.blue_normal)
        )
        tv_title.text = "订单支付"
        rlback.setOnClickListener {
            mFragmentManager.popBackStack()
        }


//        tv_agreement.setOnClickListener {
//            mViewModel.getDocContent("")
//        }
    }

    override fun initData() {
        if (!tmpAddress.isBlank()) {
            selectedAddress = GsonUtils.fromJson(tmpAddress, ReceiveAddress::class.java)
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
//            if(!it.getString("shopName").isNullOrBlank())
//            {
//
//            }
        }
    }

    override fun startObserve() {
        mViewModel.apply {

            agreementUiState.observe(this@PaypalFragment, Observer {

                if (it.showLoading)
                {
                    showLoading("请稍后...")
                }else{
                    hideLoading()
                }

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
                        startActivity(
                            Intent(
                                this@PaypalFragment.context,
                                LoginActivity::class.java
                            )
                        )
                    }
                }
            })

            payAddressUiState.observe(this@PaypalFragment, Observer {
                if (it.showLoading)
                {
                    showLoading("请稍后...")
                }else{
                    hideLoading()
                }
                it.showSuccess?.let { address ->
                    if (selectedAddress != null) {
                        layout_noAddress.visibility = View.GONE
                        layout_Address.visibility = View.VISIBLE

                        tv_name.text =
                            "${selectedAddress!!.mConsignee}\t\t${selectedAddress!!.mMobile}"

                        tv_address_province.text = selectedAddress!!.mProvinceName
                        tv_address_city.text = selectedAddress!!.mCityName
                        tv_address_districtName.text = selectedAddress!!.mDistrictName
                        tv_address_detail.text = selectedAddress!!.mAddress
                        mAddress = selectedAddress
                    } else {
                        layout_noAddress.visibility = View.GONE

                        layout_Address.visibility = View.VISIBLE

                        tv_name.text =
                            "${selectedAddress?.mConsignee ?: ""}\t\t${selectedAddress?.mMobile ?: ""}"

                        tv_address_province.text = address.mProvinceName
                        tv_address_city.text = address.mCityName
                        tv_address_districtName.text = address.mDistrictName
                        tv_address_detail.text = address.mAddress
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
                        startActivity(
                            Intent(
                                this@PaypalFragment.context,
                                LoginActivity::class.java
                            )
                        )
                    }
                }
            })
// TODO: 2020/9/20 可能有问题
            directBuyUiState.observe(this@PaypalFragment, Observer { directBuyUiModel ->
                if (directBuyUiModel.showLoading)
                {
                    showLoading("请稍后...")
                }else{
                    hideLoading()
                }
                directBuyUiModel.showSuccess?.let {
                    mCoupon = it.mCouponList.toMutableList()
                    if (mCoupon.isNullOrEmpty()) {
                        mOccupationBean = Coupon(mCouponId = null)
                        tv_paypal_coupon.text = "暂无可用优惠券"
                        tv_paypal_coupon.setTextColor(Color.parseColor("#FF999999"))
                    } else {
                        mCoupon.add(0, Coupon().apply {
                            mCouponName = "不使用优惠券"
                        })
                        tv_paypal_coupon.text = "选择优惠券"
                        tv_paypal_coupon.setTextColor(Color.parseColor("#FF999999"))
                    }

                    if (it.mCouponId > 0) {
                        mOccupationBean = Coupon(mCouponId = it.mCouponId)
                        tv_paypal_coupon.text = "-¥${it.mCouponValue}"
                        tv_paypal_coupon.setTextColor(Color.parseColor("#FF999999"))
                    } else {
                        mOccupationBean = Coupon(mCouponId = 0)
                    }

                    //返回的收货地址不为空，显示收货地址
                    if (selectedAddress != null) {
                        layout_noAddress.visibility = View.GONE
                        layout_Address.visibility = View.VISIBLE
                        tv_name.text =
                            "${selectedAddress!!.mConsignee}\t\t${selectedAddress!!.mMobile}"

                        tv_address_province.text = selectedAddress!!.mProvinceName
                        tv_address_city.text = selectedAddress!!.mCityName
                        tv_address_districtName.text = selectedAddress!!.mDistrictName
                        tv_address_detail.text = selectedAddress!!.mAddress
                    } else {
                        if (it.mReceiveAddress != null) {

                            layout_noAddress.visibility = View.GONE
                            layout_Address.visibility = View.VISIBLE
                            iv_paypal_bottom.visibility = View.VISIBLE

                            tv_name.text =
                                "${selectedAddress!!.mConsignee}\t\t${selectedAddress!!.mMobile}"

                            tv_address_province.text = it.mReceiveAddress!!.mProvinceName
                            tv_address_city.text = it.mReceiveAddress!!.mCityName
                            tv_address_districtName.text = it.mReceiveAddress!!.mDistrictName
                            tv_address_detail.text = it.mReceiveAddress!!.mAddress

                            selectedAddress = it.mReceiveAddress

                        } else {
                            // //返回的收货地址为空，显示收货地址
                            layout_noAddress.visibility = View.GONE
                            layout_Address.visibility = View.VISIBLE
                            iv_paypal_bottom.visibility = View.VISIBLE

                            if (mAddress == null) {
                                layout_noAddress.visibility = View.VISIBLE
                                layout_Address.visibility = View.GONE
                                iv_paypal_bottom.visibility = View.GONE
                            } else {
                                tv_name.text =
                                    "${selectedAddress!!.mConsignee}\t\t${selectedAddress!!.mMobile}"

                                tv_address_province.text = mAddress!!.mProvinceName
                                tv_address_city.text = mAddress!!.mCityName
                                tv_address_districtName.text = mAddress!!.mDistrictName
                                tv_address_detail.text = mAddress!!.mAddress

                                selectedAddress = mAddress
                            }
                        }
                    }

                    rv_goodsList.run {
                        layoutManager = LinearLayoutManager(activity).also {
                            it.orientation = LinearLayoutManager.VERTICAL
                        }

                        val list: ArrayList<CartGoodsSku> = ArrayList()
                        for (shop in it.mShops!!) {
                            for (sku in shop.mCartGoodsSkuList!!) {
                                sku.let {
                                    list.add(it)
                                }
                            }
                        }
                        adapter = cartSelectedAdapter

                        cartSelectedAdapter.setNewInstance(list.toMutableList())
                        cartSelectedAdapter.notifyDataSetChanged()

                    }

                    val tmpTotal = BigDecimal(it.mTotalMoney.toString())
                    if (tmpTotal.compareTo(BigDecimal.ZERO) == 0) {
                        tv_totalPrice.text = "¥0.00"
                    } else {
                        tv_totalPrice.text =
                            "¥${tmpTotal.stripTrailingZeros().toPlainString()}"
                    }

//                    tv_lowestPrice.text = tv_totalPrice.text
                    tv_selfPurchaseOffer.text = "-¥${it.mOrderDiscountMoney.toString()}"
                    tv_selfPurchaseDiscount.text = "自购优惠（${it.mOrderDiscount}%)"

                    val tmpTax = BigDecimal(MoneyUtil.moneyAdd(it.mDutyPrice.toString(), "0"))
                    if (tmpTax.compareTo(BigDecimal.ZERO) == 0) {
                        tv_tax.text = "¥0.00"
                    } else {
                        tv_tax.text = "¥${tmpTax.stripTrailingZeros().toPlainString()}"
                    }

                    val payPrice = MoneyUtil.moneyAdd(it.mPayMoney.toString(), "0")
                    val tmpPayPrice = BigDecimal(payPrice)
                    if (tmpPayPrice.compareTo(BigDecimal.ZERO) == 0) {
                        val span = SpannableString("应付:¥0.00").apply {
                            setSpan(
                                AbsoluteSizeSpan(12, true),
                                0,
                                2,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            )
                        }
                        tv_payPrice.setText(span)
                    } else {
                        //有疑问
                        val span02 = SpannableString(
                            "应付:¥${tmpPayPrice.stripTrailingZeros().toPlainString()}"
                        ).apply {
                            setSpan(
                                AbsoluteSizeSpan(12, true),
                                0,
                                2,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            )
                        }
                        tv_payPrice.setText(span02)
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

                                it.mShops.let { shops ->
                                    for (shop in shops) {
                                        shop.let { shop ->
                                            val orderParamItem: JSONObject = JSONObject()
                                            shop.mCartGoodsSkuList.let { cartGoodsSkus ->
                                                val details: JSONArray = JSONArray()
                                                for (cartGoodsSku in cartGoodsSkus) {
                                                    cartGoodsSku?.let {

                                                        val detail: JSONObject = JSONObject()
                                                        detail.put("cartId", cartGoodsSku.mCartId)
                                                        detail.put("skuId", cartGoodsSku.mSkuId)
                                                        detail.put("count", cartGoodsSku.mCount)
                                                        details.put(detail)
                                                    }
                                                }

                                                orderParamItem.put("details", details)
                                                orderParamItem.put("shopId", shop.mShopId)
                                            }
                                            orderParams.put(orderParamItem)
                                        }
                                        body.put("orderParams", orderParams)
                                    }
                                    body.put(
                                        "couponId",
                                        "${if (mOccupationBean.mCouponId!! <= 0) null else mOccupationBean.mCouponId}"
                                    )
                                    body.put("orderType", 1)
                                    body.put("receiveAddressId", selectedAddress?.mId ?: -1)
                                }
                                mViewModel.createOrder(body)
                            }
                            2 -> {
                                ToastUtils.showShort("资质审核未通过，请联系相关人员/重新提交资质认证资料，资质审核通过后再进行购买")
                            }
                        }
                    }


                }

                directBuyUiModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                directBuyUiModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(
                            Intent(
                                this@PaypalFragment.context,
                                LoginActivity::class.java
                            )
                        )
                    }
                }
            })

            //订单状态
            orderStatusState.observe(this@PaypalFragment, Observer { orderStatusModel ->

                if (orderStatusModel.showLoading)
                {
                    showLoading("请稍后...")
                }else{
                    hideLoading()
                }

                orderStatusModel.showSuccess?.let {
                    if (it.payStatus != 1) {
                            mPayDialog = XPopup.Builder(mContext).dismissOnBackPressed(true)
                                .dismissOnTouchOutside(false)
                                .asConfirm("订单提示", "您的订单还没有支付成功", "订单列表", "继续支付", {
                                    //继续支付
                                    val body: JSONObject = JSONObject()
                                    body.put("orderId", mOrderId)
                                    body.put("orderType", 1)
                                    body.put("receiveAddressId", selectedAddress?.mId ?: -1)
                                    mViewModel.createOrder(body)
                                }, {
                                    //订单列表
                                    (mContext as AppCompatActivity).supportFragmentManager.commit {
                                        remove(this@PaypalFragment)
                                        add(R.id.frame_content,
                                            OrdersFragment.newInstance("paypal"),
                                            "OrdersFragment"
                                        )
                                    }

                                }, false).show()
                    } else {
                        mOrderId = -1
                        (mContext as AppCompatActivity).supportFragmentManager.popBackStack()
                        startActivity(
                            Intent(context, PaySuccessActivity::class.java).putExtra(
                                PaySuccessActivity
                                    .orderMoney, it.payMoney
                            )
                        )
                    }
                }

                orderStatusModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                orderStatusModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        startActivity(
                            Intent(
                                this@PaypalFragment.context,
                                LoginActivity::class.java
                            )
                        )
                    }
                }
            })

            //订单信息返回
            orderState.observe(this@PaypalFragment, Observer { orderModel ->
                if (orderModel.showLoading)
                {
                    showLoading("请稍后...")
                }else{
                    hideLoading()
                }

                orderModel.showSuccess?.let {
                    mOrderId = it.orderId
                    when (mPayType) {
                        //推荐Ta，代付
                        -1 -> {
                            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
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
                            if (it.totalFee == 0.00) {
                                mOrderId = -1
                                (mContext as AppCompatActivity).supportFragmentManager.popBackStack()
                                startActivity(
                                    Intent(context, PaySuccessActivity::class.java).putExtra(
                                        PaySuccessActivity
                                            .orderMoney, "${it.totalFee}"
                                    )
                                )
                            } else {
                                weChatPay(it)
                            }
                        }
                        else -> Log.e("wtf", "unknow pay type:${mPayType}")
                    }
                }

                orderModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                orderModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        startActivity(
                            Intent(
                                this@PaypalFragment.context,
                                LoginActivity::class.java
                            )
                        )
                    }
                }
            })
        }
    }

    override fun initListener() {

        ll_paypal_voucher.setOnClickListener {
            if (!mCoupon.isNullOrEmpty()) {
                tv_paypal_coupon.setTextColor(Color.parseColor("#FFCC0814"))
                if (mOccupationBean.mCouponId != null && mOccupationBean.mCouponId != 0) {
                    mCoupon.forEach {
                        if (mOccupationBean.mCouponId == it.mCouponId) {
                            it.isSelector = true
                        }
                    }
                }
                XPopup.Builder(mContext)
                    .setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView?) {
                            super.onDismiss(popupView)
                            val filter = mCoupon.filter { it.isSelector }
                            if (filter.isEmpty()) {
                                AreadyPprice(null)
                                tv_paypal_coupon.text = "不使用优惠券"
                                tv_paypal_coupon.setTextColor(Color.parseColor("#FF999999"))
                            } else {
                                val coupon = filter[0]
                                mOccupationBean = coupon
                                if (coupon.mCouponName == "不使用优惠券") {
                                    tv_paypal_coupon.text = "不使用优惠劵"
                                    tv_paypal_coupon.setTextColor(Color.parseColor("#FF999999"))
                                    AreadyPprice(null)
                                } else {
                                    if (coupon.mCouponId != mOccupationBean.mCouponId ?: -1) {
                                        mOccupationBean = coupon
                                        AreadyPprice("${mOccupationBean.mCouponId}")
                                    }

//                                    mViewModel.directBuy(it.getInt("count"), it.getInt("skuId"),)
                                    tv_paypal_coupon.setTextColor(Color.parseColor("#FFCC0814"))
                                    tv_paypal_coupon.text = "-¥${filter[0].mCouponValue}"
                                }
                            }
                        }
                    })
                    .asCustom(DialogChoiceCoupon(mContext, mCoupon)).show()
            } else {
                tv_paypal_coupon.text = "暂无可用优惠券"
                tv_paypal_coupon.setTextColor(Color.parseColor("#FF999999"))
            }
        }

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

    private fun AreadyPprice(couponId: String?) {
        arguments?.let {
            when (it.get("type")) {
                //直接购
                1 -> mViewModel.directBuy(
                    it.getInt("count"), it.getInt("skuId"),
                    mOccupationBean.mCouponId
                )
                //购物车结算
                2 -> it.getString("shops")?.let { shops ->
                    mViewModel.cartSettlement(shops, mOccupationBean.mCouponId)
                }
                else -> {
                    ToastUtils.showShort("未知结算类型")
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val tmpAddress2 by PreferenceUtils(PreferenceUtils.SELECTED_ADDRESS, "")
            tmpAddress = tmpAddress2
            //activity!!.nav_view.visibility = View.GONE

            StateAppBar.setStatusBarLightMode(
                this.activity,
                mContext.getResColor(R.color.blue_normal)
            )

            if (!tmpAddress.isBlank()) {
                selectedAddress = GsonUtils.fromJson(tmpAddress, ReceiveAddress::class.java)
            } else {
                selectedAddress = null
            }
            mViewModel.getPayAddress()
        }
        StateAppBar.setStatusBarLightMode(
            this.activity,
            mContext.getResColor(R.color.blue_normal)
        )
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

    override fun onPause() {
        super.onPause()
        mPayDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        if (mOrderId != -1) {
            mViewModel.queryOrderStatus(mOrderId)
        }
    }
}
