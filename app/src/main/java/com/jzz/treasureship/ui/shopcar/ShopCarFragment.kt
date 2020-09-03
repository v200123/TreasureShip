package com.jzz.treasureship.ui.shopcar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.math.MathUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.CartList
import com.jzz.treasureship.model.bean.Shop
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.home.HomeFragment
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.paypal.PaypalFragment
import com.jzz.treasureship.utils.MoneyUtil
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.item_shopcar_goods.*
import kotlinx.android.synthetic.main.shop_car_fragment.*
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.math.BigDecimal

class ShopCarFragment : BaseVMFragment<ShopCarViewModel>() {

    companion object {
        fun newInstance(): ShopCarFragment {
            return ShopCarFragment()
        }
    }

    lateinit var spcs: CartList

    private val cartAdapter by lazy { CartOrderAdapter() }

    override fun getLayoutResId() = R.layout.shop_car_fragment

    override fun initVM(): ShopCarViewModel = getViewModel()

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        ll_view.background = context!!.getDrawable(R.drawable.toolbar_bg)
        tv_title.text = context!!.resources.getText(R.string.title_shop_car)

        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))
        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        rv_shopcar_list.run {
            layoutManager = LinearLayoutManager(activity).also { layoutManager ->
                layoutManager.orientation = LinearLayoutManager.VERTICAL
            }

            adapter = cartAdapter
        }

        tv_addGoods2Cart.setOnClickListener {
            activity!!.nav_view.visibility = View.VISIBLE
            activity!!.nav_view.selectedItemId = R.id.navigation_home
            startActivity(Intent(this.context,MainActivity::class.java))
//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.frame_content,
//                    HomeFragment.newInstance(),
//                    HomeFragment.javaClass.name
//                )
//                .commit()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isHidden) {
            activity!!.nav_view.visibility = View.GONE
            StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))
        }
    }

    override fun initData() {
        mViewModel.getCartList()
    }

    override fun startObserve() {
        mViewModel.apply {
            cartsUiState.observe(this@ShopCarFragment, Observer { cartModel ->
                cartModel.showSuccess?.let {
                    if (it.shops.isEmpty()) {
                        layout_noGoods.visibility = View.VISIBLE
                        layout_goods.visibility = View.GONE
                        ll_shop_car_confirm.visibility = View.GONE
                    } else {
                        layout_noGoods.visibility = View.GONE
                        layout_goods.visibility = View.VISIBLE
                        ll_shop_car_confirm.visibility = View.VISIBLE

                        spcs = CartList(it.dutyPrice, it.isSelected, it.shops, it.vatPrice)

                        for (ele in spcs.shops) {
                            ele.isSelected = it.isSelected
                        }

                        cb_shop_car_all.isChecked = it.isSelected == 1


                        cartAdapter.setNewData(spcs.shops)
                        cartAdapter.notifyDataSetChanged()
                    }


                    cb_shop_car_all.setOnClickListener {
                        val selected: Boolean = it.isSelected
                        for (i in spcs.shops.indices) {
                            if (selected) {
                                spcs.shops[i].isSelected = 0
                            } else {
                                spcs.shops[i].isSelected = 1
                            }

                            for (j in spcs.shops[i].cartGoodsSkuList?.indices!!) {
                                if (selected) {
                                    spcs.shops[i].cartGoodsSkuList?.get(j)?.isSelected = 0
                                } else {
                                    spcs.shops[i].cartGoodsSkuList?.get(j)?.isSelected = 1
                                }
                            }
                        }

                    }

                    tv_shop_car_confirm.setOnClickListener {
                        val shopsJson: JSONArray = JSONArray()

                        for (shop in spcs.shops) {
                            val shopItem: JSONObject = JSONObject()
                            val details: JSONArray = JSONArray()
                            if (shop.isSelected == 1) {
                                //选中店铺，则该店铺下方的商品全选
                                shop.cartGoodsSkuList?.let { cartGoodsSkus ->
                                    for (cartGoodsSku in cartGoodsSkus) {
                                        cartGoodsSku?.let {
                                            val detail: JSONObject = JSONObject()

                                            detail.put("cartId", cartGoodsSku.cartId)
                                            detail.put("skuId", cartGoodsSku.skuId)
                                            detail.put("count", cartGoodsSku.count)

                                            details.put(detail)
                                        }
                                    }
                                    if (details.length() > 0) {
                                        shopItem.put("details", details)
                                        shopItem.put("shopId", shop.shopId)
                                    }
                                }
                            } else {
                                //未选中店铺，遍历商品列表，只有选中的商品才加入
                                shop.cartGoodsSkuList?.let { goodsSkuList ->
                                    val details: JSONArray = JSONArray()
                                    for (goods in goodsSkuList) {
                                        if (goods?.isSelected == 1) {
                                            val detail: JSONObject = JSONObject()

                                            detail.put("cartId", goods.cartId)
                                            detail.put("skuId", goods.skuId)
                                            detail.put("count", goods.count)

                                            details.put(detail)
                                        }
                                    }
                                    if (details.length() > 0) {
                                        shopItem.put("details", details)
                                        shopItem.put("shopId", shop.shopId)
                                    }
                                }
                            }
                            shopsJson.put(shopItem)
                        }
                        activity!!.supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .hide(this@ShopCarFragment)//隐藏当前Fragment
                            .add(
                                R.id.frame_content,
                                PaypalFragment.newInstance(2, shopsJson.toString()),
                                PaypalFragment.javaClass.name
                            )
                            .commit()

                    }
                    var tmp: String? = "0.00"
                    for (ele in cartAdapter.data) {

                        for (item in ele.cartGoodsSkuList!!) {
                            if (item!!.isSelected == 1) {
                                tmp = MoneyUtil.moneyAdd(
                                    tmp,
                                    MoneyUtil.moneyMul(item.price.toString(), item.count.toString())
                                )
                            }
                        }
                    }
                    val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                    tv_totalPrice.text = "¥${totalPrice}"
                }

                cartModel.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                cartModel.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@ShopCarFragment.context, LoginActivity::class.java))
                    }
                }
            })
        }
    }

    override fun initListener() {

    }

    //外层商家
    inner class CartOrderAdapter(layoutResId: Int = R.layout.item_car_shop) :
        BaseQuickAdapter<Shop, BaseViewHolder>(layoutResId) {

        override fun convert(helper: BaseViewHolder, item: Shop?) {
            item?.let {
                helper.setText(R.id.cb_shop_car_all, item.shopName)

                val cbShop: CheckBox = helper.getView(R.id.cb_shop_car_all)
                cbShop.isChecked = item.isSelected == 1

                cbShop.setOnClickListener {
                    spcs.shops[helper.adapterPosition].isSelected = if (cbShop.isChecked) {
                        1
                    } else {
                        0
                    }

                    for (element in spcs.shops[helper.adapterPosition].cartGoodsSkuList!!) {
                        element!!.isSelected = if (cbShop.isChecked) {
                            1
                        } else {
                            0
                        }
                    }

                    val shopList = ArrayList<Boolean>()
                    for (element in spcs.shops) {
                        shopList.add(element.isSelected == 1)
                    }

                    cb_shop_car_all.isChecked = !shopList.contains(false)

                    cartAdapter.notifyDataSetChanged()
                }

                val rvGoods: RecyclerView = helper.getView(R.id.sl_good)

                rvGoods.run {
                    layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)

                    isNestedScrollingEnabled = false

                    val goodsAdapter = CartOrderChildAdapter(helper.adapterPosition, R.layout.item_shopcar_goods)
                    goodsAdapter.setNewData(item.cartGoodsSkuList)
                    adapter = goodsAdapter

                    goodsAdapter.notifyDataSetChanged()
                }
                var tmp: String? = "0.00"
                for (ele in cartAdapter.data) {

                    for (item in ele.cartGoodsSkuList!!) {
                        if (item!!.isSelected == 1) {
                            tmp = MoneyUtil.moneyAdd(
                                tmp,
                                MoneyUtil.moneyMul(item.price.toString(), item.count.toString())
                            )
                        }
                    }
                }
                val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                tv_totalPrice.text = "¥${totalPrice}"
            }

        }
    }

    //内层商品
    inner class CartOrderChildAdapter(positionShop: Int, layoutResId: Int = R.layout.item_shopcar_goods) :
        BaseQuickAdapter<CartGoodsSku, BaseViewHolder>(layoutResId) {
        private var positionShop: Int = -1

        init {
            this.positionShop = positionShop
        }

        override fun convert(helper: BaseViewHolder, item: CartGoodsSku?) {

            item?.let { goodsSku ->

                val swipeLayout: SwipeLayout = helper.getView(R.id.swipeLayout)
                swipeLayout.apply {
                    addDrag(SwipeLayout.DragEdge.Right, helper.getView(R.id.layout_delGoods))

                    addSwipeListener(object : SwipeLayout.SwipeListener {
                        override fun onOpen(layout: SwipeLayout?) {
                        }

                        override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {

                        }

                        override fun onStartOpen(layout: SwipeLayout?) {
                        }

                        override fun onStartClose(layout: SwipeLayout?) {

                        }

                        override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                            helper.getView<LinearLayout>(R.id.layout_delGoods).setOnClickListener {
                                layout!!.toggle()

                                mViewModel.deleteGoods(goodsSku.cartId)
                            }
                        }

                        override fun onClose(layout: SwipeLayout?) {

                        }

                    })
                }

                val cbGoods: CheckBox = helper.getView(R.id.cb_shopcar_good)
                cbGoods.isChecked = item.isSelected == 1
                cbGoods.setOnClickListener {
                    spcs.shops[positionShop].cartGoodsSkuList?.get(helper.adapterPosition)?.isSelected =
                        if (cbGoods.isChecked) {
                            1
                        } else {
                            0
                        }

                    //改变这个商品的选中状态后，遍历看是否全部选中，若全部选中则改变店铺的选中状态
                    val goodsList = ArrayList<Boolean>()
                    for (element in spcs.shops[positionShop].cartGoodsSkuList!!) {
                        element?.let {
                            if (it.isSelected == 1) {
                                goodsList.add(true)
                            } else {
                                goodsList.add(false)
                            }
                        }
                    }

                    if (goodsList.contains(false)) {
                        spcs.shops[positionShop].isSelected = 0
                    } else {
                        spcs.shops[positionShop].isSelected = 1
                    }

                    //改变这个店铺的选中状态后，遍历看是否全部选中，若全部选中则改变全选的选中状态
                    val shopList = ArrayList<Boolean>()
                    for (element in spcs.shops) {
                        shopList.add(element.isSelected == 1)
                    }
                    cb_shop_car_all.isChecked = !shopList.contains(false)

                    cartAdapter.notifyDataSetChanged()
                    notifyDataSetChanged()
                }

                helper.setText(R.id.tv_shopcar_good_name, goodsSku.name)

                val icon: ImageView = helper.getView(R.id.iv_shopcar_good_cover)
                Glide.with(mContext).load(goodsSku.imageUrl).into(icon)

                helper.setText(R.id.et_shopcar_good_num, goodsSku.count.toString())
                val etNum: EditText = helper.getView(R.id.et_shopcar_good_num)
                etNum.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        var number: Int = -1
                        if (!etNum.text.toString().isBlank()) {
                            number = etNum.text.toString().toInt()
                        }

                        when (number) {
                            -1 -> {
                            }
                            0 -> {
                                XPopup.Builder(context).asConfirm("删除商品", "确认将物品从购物车删除？", "取消", "删除", {
                                    mViewModel.deleteGoods(spcs.shops[positionShop].cartGoodsSkuList?.get(helper.adapterPosition)?.cartId!!)
                                }, {}, false).show()
                            }
                            in 1..99 -> {
                                spcs.shops[positionShop].cartGoodsSkuList?.get(helper.adapterPosition)?.count = number
                                var tmp: String? = "0.00"
                                for (ele in cartAdapter.data) {

                                    for (item in ele.cartGoodsSkuList!!) {
                                        if (item!!.isSelected == 1) {
                                            tmp = MoneyUtil.moneyAdd(
                                                tmp,
                                                MoneyUtil.moneyMul(item.price.toString(), item.count.toString())
                                            )
                                        }
                                    }
                                }
                                val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                                tv_totalPrice.text = "¥${totalPrice}"
                            }
                            else -> ToastUtils.showShort("不是合理的数量")
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })

                helper.setText(R.id.tv_specsv_num, goodsSku.stock.toString())
                helper.setText(R.id.tv_specsv_name, goodsSku.specValue)

                helper.setText(R.id.tv_goods_price, "¥${goodsSku.price}")

                val add: TextView = helper.getView(R.id.tv_shopcar_good_add)
                add.setOnClickListener {

                    spcs.shops[positionShop].cartGoodsSkuList?.get(helper.layoutPosition)?.count!!.plus(1)
                    Log.d("caicaicai", "click")
                    etNum.setText(
                        "${spcs.shops[positionShop].cartGoodsSkuList?.get(helper.layoutPosition)?.count!!.plus(
                            1
                        )}"
                    )
                    notifyDataSetChanged()

                    var tmp: String? = "0.00"
                    for (ele in cartAdapter.data) {

                        for (item in ele.cartGoodsSkuList!!) {
                            if (item!!.isSelected == 1) {
                                tmp = MoneyUtil.moneyAdd(
                                    tmp,
                                    MoneyUtil.moneyMul(item.price.toString(), item.count.toString())
                                )
                            }
                        }
                    }
                    val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                    tv_totalPrice.text = "¥${totalPrice}"
                }

                val sub: TextView = helper.getView(R.id.tv_shopcar_good_sub)
                sub.setOnClickListener {
                    if (spcs.shops[positionShop].cartGoodsSkuList?.get(helper.layoutPosition)?.count!! > 0) {
                        spcs.shops[positionShop].cartGoodsSkuList?.get(helper.layoutPosition)?.count!!.minus(1)
                        etNum.setText(
                            "${spcs.shops[positionShop].cartGoodsSkuList?.get(helper.layoutPosition)?.count!!.minus(
                                1
                            )}"
                        )
                        notifyDataSetChanged()
                        var tmp: String? = "0.00"
                        for (ele in cartAdapter.data) {

                            for (item in ele.cartGoodsSkuList!!) {
                                if (item!!.isSelected == 1) {
                                    tmp = MoneyUtil.moneyAdd(
                                        tmp,
                                        MoneyUtil.moneyMul(item.price.toString(), item.count.toString())
                                    )
                                }
                            }
                        }
                        val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                        tv_totalPrice.text = "¥${totalPrice}"
                    } else {
                        ToastUtils.showShort("已经不能再减咯！")
                    }
                }
            }
        }
    }
}
