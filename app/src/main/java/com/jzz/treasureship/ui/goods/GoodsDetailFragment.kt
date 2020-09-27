package com.jzz.treasureship.ui.goods

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.BigImageActivity
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.AddressLogisticsAdapter
import com.jzz.treasureship.adapter.ComparePricesAdapter
import com.jzz.treasureship.adapter.GlideImageLoader
import com.jzz.treasureship.adapter.SkuChooseAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.AddressLogisticsBean
import com.jzz.treasureship.model.bean.GoodsDetail
import com.jzz.treasureship.model.bean.GoodsSku
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.paypal.PaypalFragment
import com.jzz.treasureship.ui.shopcar.ShopCarFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.changeImage
import com.jzz.treasureship.view.CustomComparePricePopup
import com.jzz.treasureship.view.CustomSkuBottomPopup
import com.jzz.treasureship.view.RadiusSpan
import com.lc.mybaselibrary.out
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_goods_details.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import q.rorbin.badgeview.QBadgeView
import java.net.URLEncoder


class  GoodsDetailFragment : BaseVMFragment<GoodsDetailViewModel>(), EasyPermissions.PermissionCallbacks {

    private val mShopCarFragment by lazy { ShopCarFragment.newInstance() }
    private var isAudit by PreferenceUtils(PreferenceUtils.AUDIT_STATUS, -2)
    private var mGoodsDetails: GoodsDetail? = null
    private lateinit var mWindow: Window
    private var goodsId: Int = -1
    private val dialog: Dialog by lazy { Dialog(mContext, R.style.edit_AlertDialog_style) }
    private var JsonString:String = ""
    companion object {
        const val RC_CALL_PERM = 122

        fun newInstance(goodsId: String): GoodsDetailFragment {
            val f = GoodsDetailFragment()
            val bundle = Bundle()
            bundle.putString("goodsId", goodsId)
            f.arguments = bundle
            return f
        }
    }

    private val addressLogisticsList: ArrayList<AddressLogisticsBean> = arrayListOf(
        AddressLogisticsBean("日本发货", "", 1),
        AddressLogisticsBean("内地海关", "", 0),
        AddressLogisticsBean("国内快递", "", 0),
        AddressLogisticsBean("确认收货", "", 0)
    )

    private val addressLogisticsAdapter by lazy { AddressLogisticsAdapter() }
    private val priceCompareAdapter by lazy { ComparePricesAdapter() }
    private val skuListAdapter by lazy { SkuChooseAdapter() }

    override fun getLayoutResId() = R.layout.fragment_goods_details

    override fun initVM(): GoodsDetailViewModel = getViewModel()

    override fun onResume() {
        super.onResume()
        "我${this::class.java.name}\t\tFragment进来了".out()
    }

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE

        gooddetails_shop.setOnClickListener {
            mContext.startActivity(GoodsQualificationActivity.newInstance(mContext,0,JsonString))
        }

        ll_goodsDetails_parameter.setOnClickListener {
            mContext.startActivity(GoodsQualificationActivity.newInstance(mContext,1,JsonString))
        }

        this.mWindow = activity!!.window

        fab_up.hide()

        tv_cart.setOnClickListener {
            mActivity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(GoodsDetailFragment.javaClass.name)
                .hide(this)//隐藏当前Fragment
                .add(
                    R.id.frame_content,
                    mShopCarFragment,
                    mShopCarFragment.javaClass.name
                )
                .commit()
        }



        dialog.setContentView(R.layout.item_img)

        mGoodsDetails?.let {
            initUi(it)
        }
    }

    override fun initData() {
        mViewModel.getUserInfo()
        arguments?.let {
            goodsId = it.getString("goodsId")!!.toInt()
            mViewModel.getGoodsDetail(goodsId)
            val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
            if (isLogin) {
                mViewModel.getCount()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun startObserve() {
        mViewModel.apply {
            val addCartPopup = XPopup.Builder(this@GoodsDetailFragment.context).asLoading()
            uiState.observe(this@GoodsDetailFragment, Observer { viewModel ->

                if (viewModel.showLoading) {
                    addCartPopup.show()
                }

                viewModel.showSuccess?.let { goodsDetail ->
                    addCartPopup.dismiss()
                    JsonString =  GsonUtils.toJson(goodsDetail)
                    initUi(goodsDetail)
                }

                viewModel.showError?.let { message ->
                    addCartPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            val xPopup = XPopup.Builder(this@GoodsDetailFragment.context).asLoading("正在加入购物车。。。")
            addCartState.observe(this@GoodsDetailFragment, Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    if ((it == null) or (it.isNullOrBlank() or "null".equals(it))) {
                        ToastUtils.showShort("添加成功")
                    } else {
                        ToastUtils.showShort(it)
                    }
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    xPopup.dismiss()
                    if (needLogin) {
                        startActivity(Intent(this@GoodsDetailFragment.context, LoginActivity::class.java))
                    }
                }
            })


            cartNumUiState.observe(this@GoodsDetailFragment, Observer {

                if (it.showLoading) {
                    addCartPopup.show()
                }

                it.showSuccess?.let {
                    addCartPopup.dismiss()
                    QBadgeView(context)
                        .bindTarget(tv_cart)
                        .setBadgeNumber(it)
                        .setBadgeTextSize(10f, true)
                        .setBadgePadding(1.0f, true)
                        .setBadgeGravity(Gravity.CENTER or Gravity.TOP)
                        .setBadgeBackgroundColor(context!!.resources.getColor(R.color.blue_light))

                }

                it.showError?.let { message ->
                    addCartPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    xPopup.dismiss()
                    if (needLogin) {
                        startActivity(Intent(this@GoodsDetailFragment.context, LoginActivity::class.java))
                    }
                }
            })

            directBuyUiState.observe(this@GoodsDetailFragment, Observer {
                if (it.showLoading) {
                    addCartPopup.show()
                }

                it.showSuccess?.let {

                    addCartPopup.dismiss()
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    xPopup.dismiss()
                    if (needLogin) {
                        startActivity(Intent(this@GoodsDetailFragment.context, LoginActivity::class.java))
                    }
                }
            })

            userState.observe(this@GoodsDetailFragment, Observer {
                it.showSuccess?.let {
                    isAudit = it.auditStatus!!
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        startActivity(Intent(this@GoodsDetailFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {
        fab_up.setOnClickListener {

        }

        iv_back.setOnClickListener {
           (mContext as AppCompatActivity).supportFragmentManager.popBackStack()
        }


    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            //重置webview中img标签的图片大小
            imgReset()
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListener()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     */
    private fun imgReset() {
        slidedetails_behind?.loadUrl(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                    "}" +
                    "})()"
        )
    }

    // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
    private fun addImageClickListener() {
        slidedetails_behind?.loadUrl(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "    objs[i].οnclick=function()  " +
                    "    {  "
                    + "        window.imagelistner.openImage(this.src);  " +
                    "    }  " +
                    "}" +
                    "})()"
        )
    }

    class JavaScriptInterface(context: Context) {
        private val context: Context = context

        //点击图片回调方法
        //必须添加注解,否则无法响应
        @JavascriptInterface
        fun openImage(img: String?) {
            Log.i("TAG", "响应点击事件!")
            val intent = Intent()
            intent.putExtra("image", img)
            intent.setClass(context, BigImageActivity::class.java) //BigImageActivity查看大图的类，自己定义就好
            context.startActivity(intent)
        }
    }

    @AfterPermissionGranted(RC_CALL_PERM)
    private fun callService(phoneNum: String) {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.CALL_PHONE)) {
            PhoneUtils.call(phoneNum)
        } else {
            EasyPermissions.requestPermissions(
                this, "获取通话权限以拨打联系客服",
                RC_CALL_PERM, Manifest.permission.CALL_PHONE
            )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        ToastUtils.showShort("您拒绝了功能必需的权限申请，可在系统设置中打开后重试")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    class ImgAdapter(context: Context, ivPicList: ArrayList<ImageView>) : PagerAdapter() {

        private val mContext = context
        private val mImgList = ivPicList

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return mImgList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imgView = mImgList[position]
            container.addView(imgView)
            return imgView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val imgView = `object` as ImageView
            container.removeView(imgView)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        }
    }

    // TODO: 2020/9/17 如何判断是境外商品
    private fun initUi(goodsDetail: GoodsDetail) {

        tv_good_details_name.text = goodsDetail.shopName
        tv_goods_fare.text = if (goodsDetail.shippingFee == 0)
            "包邮"
        else
            goodsDetail.shippingFee.toString()
        if (goodsDetail.goodsType == 1) {
            ll_showAndHide.visibility = View.VISIBLE
            view_1.visibility = View.VISIBLE
            val apply = SpannableString("境外商品\t" + goodsDetail.goodsName).apply {
                setSpan(RadiusSpan(Color.parseColor("#FFF0A823"), 9, context), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            tv_goodsName.setText(apply)
        } else
            tv_goodsName.text = goodsDetail.goodsName
//        tv_compony.text = goodsDetail.shopName
        tv_price.text = "¥${goodsDetail.goodsSku?.get(0)?.price?:"未知"}"

        if (goodsDetail.goodsSku?.get(0)?.isParity ?: 0 == 0) {
            tv_goCompare.visibility = View.GONE
        } else {
            tv_goCompare.visibility = View.VISIBLE
            priceCompareAdapter.setNewData(goodsDetail.goodsSku?.get(0)?.parityList)
            priceCompareAdapter.notifyDataSetChanged()

            tv_goCompare.setOnClickListener {
                XPopup.Builder(it.context).asCustom(CustomComparePricePopup(mContext, priceCompareAdapter))
                    .show()
            }
        }

//        rcv_skus.run {
//            layoutManager = GridLayoutManager(context!!, goodsDetail.goodsSku?.size?:1)
//            adapter = skuListAdapter
//            suppressLayout(true)
//        }
        skuListAdapter.setNewData(goodsDetail.goodsSku)
        skuListAdapter.notifyDataSetChanged()


        goodsDetail.shippingFee.let {
            if (goodsDetail.shippingFee.toString().isBlank()) {
                tv_freight.text = "免运费"
            } else {
                tv_freight.text = "¥ ${goodsDetail.shippingFee}(商品售价已含)"
            }
        }

        detail_banner.apply {
            setBannerStyle(BannerConfig.NUM_INDICATOR)
            setBannerAnimation(Transformer.BackgroundToForeground)
            isAutoPlay(true)
            setDelayTime(2000)
            setImages(goodsDetail.goodsPictureList)
            setIndicatorGravity(BannerConfig.RIGHT)
            setImageLoader(GlideImageLoader())
            start()
        }

        ll_choice_norm.setOnClickListener {
            val skuPopup = CustomSkuBottomPopup(mContext, goodsDetail)
            XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {
                override fun onShow(popupView: BasePopupView?) {
                    super.onShow(popupView)
                }

                override fun onDismiss(popupView: BasePopupView) {
                    super.onDismiss(popupView)
                    val s by PreferenceUtils(PreferenceUtils.SELECTED_SKU, "")
                    val selectedGoods = GsonUtils.fromJson(s, GoodsSku::class.java)
                    tv_price.text = "¥${selectedGoods.price}"
                    selectedGoods.let {
                        when (skuPopup.mWhere) {
                            0 -> mViewModel.addCart(skuPopup.mCount, selectedGoods.skuId)
                            1 -> {
                                activity!!.supportFragmentManager.beginTransaction()
                                    .addToBackStack(GoodsDetailFragment.javaClass.name)
                                    .hide(
                                        this@GoodsDetailFragment
                                    ).add(
                                        R.id.frame_content,
                                        PaypalFragment.newInstance(1, skuPopup.mCount, selectedGoods.skuId),
                                        PaypalFragment.javaClass.name
                                    ).commit()
                            }
                            else -> {

                            }
                        }
                    }
                }
            }).asCustom(skuPopup)
                .show()
        }

//        layout_chooseGoods.setOnClickListener {
//            val skuPopup = CustomSkuBottomPopup(mContext, goodsDetail)
//            XPopup.Builder(mContext).setPopupCallback(object : SimpleCallback() {
//                override fun onDismiss(popupView: BasePopupView) {
//                    super.onDismiss(popupView)
//                    val s by PreferenceUtils(PreferenceUtils.SELECTED_SKU, "")
//                    val selectedGoods = GsonUtils.fromJson(s, GoodsSku::class.java)
//
//                    selectedGoods.let {
//                        when (skuPopup.mWhere) {
//                            0 -> {
//                                mViewModel.addCart(skuPopup.mCount, selectedGoods.skuId)
//                            }
//                            1 -> {
//                                when (isAudit) {
//                                    -1 -> {
//                                        ToastUtils.showLong("您还未进行资质认证，请先在设置中提交资质认证并通过认证后再进行购买")
//                                    }
//                                    0 -> {
//                                        ToastUtils.showLong("资质审核中，请联系相关人员通过审核后再进行购买")
//                                    }
//                                    1 -> {
//                                        activity!!.supportFragmentManager.beginTransaction()
//                                            .addToBackStack(GoodsDetailFragment.javaClass.name)
//                                            .hide(
//                                                this@GoodsDetailFragment
//                                            ).add(
//                                                R.id.frame_content,
//                                                PaypalFragment.newInstance(
//                                                    1,
//                                                    skuPopup.mCount,
//                                                    selectedGoods.skuId
//                                                ),
//                                                PaypalFragment.javaClass.name
//                                            ).commit()
//                                    }
//                                    2 -> {
//                                        ToastUtils.showLong("资质审核未通过，请联系相关人员/重新提交资质认证资料，资质审核通过后再进行购买")
//                                    }
//                                    else -> {
//
//                                    }
//                                }
//                            }
//                            else -> {
//
//                            }
//                        }
//                    }
//                }
//            }).asCustom(skuPopup)
//                .show()
//        }
//
//        layout_properties.setOnClickListener {
//            XPopup.Builder(it.context)
//                .asCustom(CustomPropertyPopup(it.context, goodsDetail.goodsAttributeList)).show()
//        }

        tv_callService.setOnClickListener {
            if (goodsDetail.companyPhone.isNullOrBlank()) {
                ToastUtils.showShort("客服电话为空")
            } else {
                callService(goodsDetail.companyPhone)
            }
        }

//        iv_useNote.setOnClickListener {
//            dialog.setCanceledOnTouchOutside(true)
//            //val test = arrayListOf<String>("http://119.3.125.1/upload/default/20200225/1582606192758.jpg","http://119.3.125.1/upload/default/20200225/1582606192779.jpg")
//            val list = ArrayList<ImageView>(goodsDetail.instructionsPictureList.size)
//            for (ele in goodsDetail.instructionsPictureList) {
//                val img = ImageView(context)
//                Glide.with(context!!).load(ele).into(img)
//                list.add(img)
//            }
//            if (list.size > 0) {
//                val mAdapter = ImgAdapter(context!!, list)
//                val vp = dialog.findViewById<ViewPager>(R.id.img_vp)
//                vp.adapter = mAdapter
//
//                val w: Window = dialog.window!!
//                val lp: WindowManager.LayoutParams = w.attributes
//                lp.x = 0
//                lp.y = 40
//
//                dialog.findViewById<ImageView>(R.id.close).setOnClickListener {
//                    dialog.dismiss()
//                }
//                dialog.onWindowAttributesChanged(lp)
//                dialog.show()
//            } else {
//                ToastUtils.showShort("无使用说明，请联系商家！")
//            }
//
//        }

//        layout_qualification.setOnClickListener {
//            val list = ArrayList<ImageView>(goodsDetail.qualificationPictureList.size)
//            for (ele in goodsDetail.qualificationPictureList) {
//                val img = ImageView(context)
//                Glide.with(context!!).load(ele).into(img)
//                list.add(img)
//            }
//            if (list.size > 0) {
//                val mAdapter = ImgAdapter(context!!, list)
//                val vp = dialog.findViewById<ViewPager>(R.id.img_vp)
//                vp.adapter = mAdapter
//
//                val w: Window = dialog.window!!
//                val lp: WindowManager.LayoutParams = w.attributes
//                lp.x = 0
//                lp.y = 40
//                dialog.findViewById<ImageView>(R.id.close).setOnClickListener {
//                    dialog.dismiss()
//                }
//                dialog.onWindowAttributesChanged(lp)
//                dialog.show()
//            } else {
//                ToastUtils.showShort("无资质说明，请联系商家！")
//            }
//        }
//
//        layout_secureNote.setOnClickListener {
//            val list = ArrayList<ImageView>(goodsDetail.specialDescriptionPictureList.size)
//            for (ele in goodsDetail.specialDescriptionPictureList) {
//                val img = ImageView(context)
//                Glide.with(this).load(ele).into(img)
//                list.add(img)
//            }
//            if (list.size > 0) {
//                val mAdapter = ImgAdapter(mContext, list)
//                val vp = dialog.findViewById<ViewPager>(R.id.img_vp)
//                vp.adapter = mAdapter
//
//                val w: Window = dialog.window!!
//                val lp: WindowManager.LayoutParams = w.attributes
//                lp.x = 0
//                lp.y = 40
//
//                dialog.findViewById<ImageView>(R.id.close).setOnClickListener {
//                    dialog.dismiss()
//                }
//                dialog.onWindowAttributesChanged(lp)
//                dialog.show()
//            } else {
//                ToastUtils.showShort("无安全说明，请联系商家！")
//            }
//        }

        slidedetails_behind?.settings?.javaScriptEnabled = true
        slidedetails_behind?.webViewClient = MyWebViewClient()
        slidedetails_behind?.addJavascriptInterface(
            JavaScriptInterface(mContext),
            "imagelistner"
        ) //这个是给图片设置点击监听的，如果你项目需要webview中图片，点击查看大图功能，可以这么添加


        goodsDetail.goodsDetailHtml?.let {
            slidedetails_behind?.loadDataWithBaseURL(
                null, goodsDetail.goodsDetailHtml,
                "text/html", "UTF-8", null
            )
        }

        tv_buyNow.setOnClickListener { view ->
            Log.d("selectGoods", goodsDetail.toString())
            val skuPopup = CustomSkuBottomPopup(view.context, goodsDetail)
            XPopup.Builder(view.context).setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView) {
                    super.onDismiss(popupView)
                    val s by PreferenceUtils(PreferenceUtils.SELECTED_SKU, "")
                    val selectedGoods = GsonUtils.fromJson(s, GoodsSku::class.java)
                    tv_price.text = "¥${selectedGoods.price}"

                    selectedGoods?.let {
                        when (skuPopup.mWhere) {
                            0 -> {
                                mViewModel.addCart(skuPopup.mCount, selectedGoods.skuId)
                            }
                            1 -> {
                                when (isAudit) {
                                    -1 -> {
                                        ToastUtils.showLong("您还未进行资质认证，请先在设置中提交资质认证并通过认证后再进行购买")
                                    }
                                    0 -> {
                                        ToastUtils.showLong("资质审核中，请联系相关人员通过审核后再进行购买")
                                    }
                                    1 -> {
                                        activity!!.supportFragmentManager.beginTransaction().addToBackStack(
                                            GoodsDetailFragment.javaClass.name
                                        )
                                            .hide(
                                                this@GoodsDetailFragment
                                            ).add(
                                                R.id.frame_content,
                                                PaypalFragment.newInstance(
                                                    1,
                                                    skuPopup.mCount,
                                                    selectedGoods.skuId
                                                ),
                                                PaypalFragment.javaClass.name
                                            ).commit()
                                    }
                                    2 -> {
                                        ToastUtils.showLong("资质审核未通过，请联系相关人员/重新提交资质认证资料，资质审核通过后再进行购买")
                                    }
                                    else -> {

                                    }
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
            }).asCustom(skuPopup)
                .show()
        }
        tv_addCart.setOnClickListener { view ->
            val skuPopup = CustomSkuBottomPopup(view.context, goodsDetail)
            XPopup.Builder(view.context).setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView) {
                    super.onDismiss(popupView)
                    val s by PreferenceUtils(PreferenceUtils.SELECTED_SKU, "")
                    val selectedGoods = GsonUtils.fromJson(s, GoodsSku::class.java)
                    tv_price.text = "¥${selectedGoods.price}"

                    selectedGoods?.let {
                        when (skuPopup.mWhere) {
                            0 -> mViewModel.addCart(skuPopup.mCount, selectedGoods.skuId)
                            1 -> {
                                when (isAudit) {
                                    -1 -> {
                                        ToastUtils.showLong("您还未进行资质认证，请先在设置中提交资质认证并通过认证后再进行购买")
                                    }
                                    0 -> {
                                        ToastUtils.showLong("资质审核中，请联系相关人员通过审核后再进行购买")
                                    }
                                    1 -> {
                                        activity!!.supportFragmentManager.beginTransaction()
                                            .addToBackStack(GoodsDetailFragment.javaClass.name)
                                            .hide(
                                                this@GoodsDetailFragment
                                            ).add(
                                                R.id.frame_content,
                                                PaypalFragment.newInstance(
                                                    1,
                                                    skuPopup.mCount,
                                                    selectedGoods.skuId
                                                ),
                                                PaypalFragment.javaClass.name
                                            ).commit()
                                    }
                                    2 -> {
                                        ToastUtils.showLong("资质审核未通过，请联系相关人员/重新提交资质认证资料，资质审核通过后再进行购买")
                                    }
                                    else -> {

                                    }
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
            }).asCustom(skuPopup)
                .show()
        }

        iv_share.setOnClickListener {
            if (!App.iwxapi.isWXAppInstalled) {
                ToastUtils.showShort("未安装微信客户端，请先安装微信！")
                return@setOnClickListener
            }
            val userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
            val userObj = GsonUtils.fromJson(userJson, User::class.java)

            val shareGoodsUrl = "http://bj.jzzchina.com/goods/detail?id=${goodsDetail.goodsId}&uid=${userObj.id}"
            val webpage = WXWebpageObject()
            val shareUrl =
                "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe5dee58a24f8a244&redirect_uri=${
                    URLEncoder.encode(
                        "$shareGoodsUrl", "utf-8"
                    )
                }&response_type=code&scope=snsapi_userinfo#wechat_redirect"
            webpage.webpageUrl = shareUrl

            val shareMsg = WXMediaMessage(webpage)
            shareMsg.title = goodsDetail.goodsName
            shareMsg.description = "我在宝舰上发现了一个不错的商品，快来看看吧。"
            val thumbBmp = BitmapFactory.decodeResource(context!!.resources, R.mipmap.ic_launcher)
            shareMsg.thumbData = thumbBmp.changeImage()

            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("webpage")
            req.message = shareMsg

            req.userOpenId = userObj.wxOpenid
            req.scene = SendMessageToWX.Req.WXSceneSession

            val share = App.iwxapi.sendReq(req)
            if (share) {

            } else {
                Log.d("wx share", "微信分享失败")
            }


            //构造一个Req
//            val req = SendMessageToWX.Req()
//            req.transaction = buildTransaction("webpage")
//            req.message = msg
//
//            req.userOpenId = userObj.wxOpenid
//            req.scene = SendMessageToWX.Req.WXSceneSession;
//
//            val isShare = App.iwxapi.sendReq(req)
//
//            if (isShare) {
//                //ToastUtils.showLong("分享成功！")
//            } else {
//                //ToastUtils.showLong("分享失败！")
//            }
        }
    }

    override fun onDestroy() {
        var s by PreferenceUtils(PreferenceUtils.SELECTED_SKU, "")
        s = ""
        super.onDestroy()

    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
}
