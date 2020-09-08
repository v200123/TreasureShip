package com.jzz.treasureship.ui.doctordevice

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.DoctorAdviceSkuAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.Order
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.paypal.PaypalViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.changeImage
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_doctor_advices.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.net.URLEncoder


class AddDoctorAdvicesFragment : BaseVMFragment<PaypalViewModel>() {

    private var skuList: ArrayList<CartGoodsSku>? = null
    private var mAdvice: String? = null
    private var mOrder: Order? = null
    private val skuAdapter by lazy { DoctorAdviceSkuAdapter() }

    companion object {
        fun newInstance(order: Order, cartGoodsSkus: ArrayList<CartGoodsSku>): AddDoctorAdvicesFragment {
            val f = AddDoctorAdvicesFragment()
            val bundle = Bundle()
            bundle.putSerializable("orderInfo", order)
            bundle.putParcelableArrayList("orderSkus", cartGoodsSkus)
            f.arguments = bundle
            return f
        }
    }

    internal enum class SHARE_TYPE {
        Type_WXSceneSession, Type_WXSceneTimeline
    }

    override fun getLayoutResId() = R.layout.fragment_doctor_advices

    override fun initVM(): PaypalViewModel = getViewModel()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        ll_view.background = context!!.getDrawable(R.drawable.toolbar_bg)

        tv_title.text = "医嘱"

        rlback.visibility = View.GONE

        Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.bjwechat)).into(iv_QRcode)

        arguments?.let {
            mOrder = it.getSerializable("orderInfo") as Order?
            skuList = it.getParcelableArrayList("orderSkus")

            rcv_skuList.run {
                layoutManager = LinearLayoutManager(context).also {
                    it.orientation = LinearLayoutManager.VERTICAL
                }

                adapter = skuAdapter
            }

            skuAdapter.setNewData(skuList)
            skuAdapter.notifyDataSetChanged()
        }

        tv_wx_send.setOnClickListener {
            if (et_advice.text.toString().isBlank()) {
                ToastUtils.showShort("医嘱不能为空，请先填写医嘱")
            } else {
                mAdvice = et_advice.text.toString()
                mViewModel.addAdvice(mAdvice!!, mOrder!!.orderNo!!)
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

        mViewModel.apply {
            addAdviceUiState.observe(this@AddDoctorAdvicesFragment, Observer {
                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@AddDoctorAdvicesFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let { adviceResBean ->
                    //订单未支付(0)才分享，订单已支付(1)不分享
                    when (adviceResBean.orderStatus) {
                        0 -> {
                            if (!App.iwxapi.isWXAppInstalled) {
                                ToastUtils.showShort("未安装微信客户端，请先安装微信！")
                                return@Observer
                            }

                            val userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                            val userObj = GsonUtils.fromJson(userJson, User::class.java)


                            val orderUrl = "http://bj.jzzchina.com/official/orderSettle?orderNo=${mOrder!!.orderNo}"
                            Log.d("shareUrl", orderUrl)
                            val webpage = WXWebpageObject()
                            val shareUrl =
                                "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe5dee58a24f8a244&redirect_uri=${URLEncoder.encode(
                                    "$orderUrl", "utf-8"
                                )}&response_type=code&scope=snsapi_userinfo#wechat_redirect"
                            webpage.webpageUrl = shareUrl

                            Log.d("shareUrl", shareUrl)
                            val msg = WXMediaMessage(webpage)
                            msg.title = "支付"
                            msg.description = "前往微信支付吧"
                            val thumbBmp = BitmapFactory.decodeResource(context!!.resources, R.mipmap.ic_launcher);
                            msg.thumbData = thumbBmp.changeImage()
                            //构造一个Req
                            val req = SendMessageToWX.Req()
                            req.transaction = buildTransaction("webpage")
                            req.message = msg

                            req.userOpenId = userObj.wxOpenid
                            req.scene = SendMessageToWX.Req.WXSceneSession;

                            val share = App.iwxapi.sendReq(req)
                            if (share) {
                                activity!!.supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.frame_content,
                                        OrdersFragment.newInstance("AddAdvice")
                                    ).commit()
                            } else {
                                Log.d("wx share", "微信分享失败")
                            }


                        }
                        1 -> {
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.frame_content,
                                    OrdersFragment.newInstance("AddAdvice")
                                ).commit()
                        }
                        else -> {
                            ToastUtils.showShort("订单支付状态异常${adviceResBean.orderStatus}")
                        }
                    }
                }
            })
        }
    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

    override fun initListener() {
    }
}
