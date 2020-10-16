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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.CartList
import com.jzz.treasureship.model.bean.Shop
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.paypal.PaypalFragment
import com.jzz.treasureship.utils.MoneyUtil
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.start
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.include_title.*
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
    override var mStatusColor: Int = R.color.blue_normal
    override fun getLayoutResId() = R.layout.shop_car_fragment

    override fun initVM(): ShopCarViewModel = getViewModel()

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        //activity!!.nav_view.visibility = View.GONE
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
//            activity!!.nav_view.visibility = View.VISIBLE
//            activity!!.nav_view.selectedItemId = R.id.navigation_home
           mContext.start<MainActivity>{
               setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
           }
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
            mViewModel.getCartList()
            //activity!!.nav_view.visibility = View.GONE
            StateAppBar.setStatusBarLightMode(this.activity, mContext.getResColor(R.color.blue_normal))
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


                        cartAdapter.setNewInstance(spcs.shops.toMutableList())
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

                            for (j in spcs.shops[i].mCartGoodsSkuList?.indices!!) {
                                if (selected) {
                                    spcs.shops[i].mCartGoodsSkuList?.get(j)?.isSelected = 0
                                } else {
                                    spcs.shops[i].mCartGoodsSkuList?.get(j)?.isSelected = 1
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
                                shop.mCartGoodsSkuList?.let { cartGoodsSkus ->
                                    for (cartGoodsSku in cartGoodsSkus) {
                                        cartGoodsSku?.let {
                                            val detail: JSONObject = JSONObject()

                                            detail.put("cartId", cartGoodsSku.mCartId)
                                            detail.put("skuId", cartGoodsSku.mSkuId)
                                            detail.put("count", cartGoodsSku.mCount)

                                            details.put(detail)
                                        }
                                    }
                                    if (details.length() > 0) {
                                        shopItem.put("details", details)
                                        shopItem.put("shopId", shop.mShopId)
                                    }
                                }
                            } else {
                                //未选中店铺，遍历商品列表，只有选中的商品才加入
                                shop.mCartGoodsSkuList?.let { goodsSkuList ->
                                    val details: JSONArray = JSONArray()
                                    for (goods in goodsSkuList) {
                                        if (goods?.isSelected == 1) {
                                            val detail: JSONObject = JSONObject()
                                            detail.put("cartId", goods.mCartId)
                                            detail.put("SkuId", goods.mSkuId)
                                            detail.put("count", goods.mCount)
                                            details.put(detail)
                                        }
                                    }
                                    if (details.length() > 0) {
                                        shopItem.put("details", details)
                                        shopItem.put("shopId", shop.mShopId)
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

                        for (item in ele.mCartGoodsSkuList!!) {
                            if (item!!.isSelected == 1) {
                                tmp = MoneyUtil.moneyAdd(
                                    tmp,
                                    MoneyUtil.moneyMul(item.mPrice.toString(), item.mCount.toString())
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

        override fun convert(helper: BaseViewHolder, item: Shop) {
            item?.let {
                helper.setText(R.id.cb_shop_car_all, item.mShopName)
                val cbShop: CheckBox = helper.getView(R.id.cb_shop_car_all)
                cbShop.isChecked = item.isSelected == 1

                cbShop.setOnClickListener {
                    spcs.shops[helper.adapterPosition].isSelected = if (cbShop.isChecked) {
                        1
                    } else {
                        0
                    }

                    for (element in spcs.shops[helper.adapterPosition].mCartGoodsSkuList!!) {
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
                    goodsAdapter.setNewData(item.mCartGoodsSkuList.toMutableList())
                    adapter = goodsAdapter

                    goodsAdapter.notifyDataSetChanged()
                }
                var tmp: String? = "0.00"
                for (ele in cartAdapter.data) {

                    for (item in ele.mCartGoodsSkuList!!) {
                        if (item!!.isSelected == 1) {
                            tmp = MoneyUtil.moneyAdd(
                                tmp,
                                MoneyUtil.moneyMul(item.mPrice.toString(), item.mCount.toString())
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
        var isInit = false

        init {
            this.positionShop = positionShop
        }

        override fun convert(helper: BaseViewHolder, item: CartGoodsSku) {
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

                                mViewModel.deleteGoods(goodsSku.mCartId)
                            }
                        }

                        override fun onClose(layout: SwipeLayout?) {

                        }

                    })
                }

                val cbGoods: CheckBox = helper.getView(R.id.cb_shopcar_good)
                cbGoods.isChecked = item.isSelected == 1
                cbGoods.setOnClickListener {
                    spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.adapterPosition)?.isSelected =
                        if (cbGoods.isChecked) {
                            1
                        } else {
                            0
                        }

                    //改变这个商品的选中状态后，遍历看是否全部选中，若全部选中则改变店铺的选中状态
                    val goodsList = ArrayList<Boolean>()
                    for (element in spcs.shops[positionShop].mCartGoodsSkuList!!) {
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

                helper.setText(R.id.tv_shopcar_good_name, goodsSku.mName)

                val icon: ImageView = helper.getView(R.id.iv_shopcar_good_cover)
                Glide.with(mContext).load(goodsSku.mImageUrl).into(icon)

                helper.setText(R.id.et_shopcar_good_num, goodsSku.mCount.toString())
                val etNum: EditText = helper.getView(R.id.et_shopcar_good_num)
                if(!isInit) {
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
                                        mViewModel.deleteGoods(
                                            spcs.shops[positionShop].mCartGoodsSkuList?.get(
                                                helper.adapterPosition
                                            ).mCartId
                                        )
                                    }, {}, false).show()
                                }
                                in 1..3 -> {
                                    spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.adapterPosition).mCount =
                                        number
                                    var tmp: String? = "0.00"
                                    for (ele in cartAdapter.data) {
                                        for (item in ele.mCartGoodsSkuList!!) {
                                            if (item!!.isSelected == 1) {
                                                tmp = MoneyUtil.moneyAdd(
                                                    tmp,
                                                    MoneyUtil.moneyMul(item.mPrice.toString(), item.mCount.toString())
                                                )
                                            }
                                        }
                                    }
                                    val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                                    tv_totalPrice.text = "¥${totalPrice}"
                                }
                                else -> ToastUtils.showShort("限购3件")
                            }
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, mCount: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, mCount: Int) {
                        }

                    })
                    isInit = true
                }
//                helper.setText(R.id.tv_specsv_num, goodsSku.stock.toString())
                helper.setText(R.id.tv_specsv_name, goodsSku.mSpecValue)

                helper.setText(R.id.tv_goods_price, "¥${goodsSku.mPrice}")

                val add: TextView = helper.getView(R.id.tv_shopcar_good_add)
                add.setOnClickListener {
                    spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.layoutPosition)?.mCount!!.plus(1)
                    Log.d("caicaicai", "click")
                    etNum.setText(
                        "${spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.layoutPosition)?.mCount!!.plus(
                            1
                        )}"
                    )
                    notifyDataSetChanged()
                    var tmp: String? = "0.00"
                    for (ele in cartAdapter.data) {

                        for (item in ele.mCartGoodsSkuList!!) {
                            if (item!!.isSelected == 1) {
                                tmp = MoneyUtil.moneyAdd(
                                    tmp,
                                    MoneyUtil.moneyMul(item.mPrice.toString(), item.mCount.toString())
                                )
                            }
                        }
                    }
                    val totalPrice = BigDecimal(tmp).stripTrailingZeros().toPlainString()
                    tv_totalPrice.text = "¥${totalPrice}"
                }

                val sub: TextView = helper.getView(R.id.tv_shopcar_good_sub)
                sub.setOnClickListener {
                    if (spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.layoutPosition)?.mCount!! > 0) {
                        spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.layoutPosition)?.mCount!!.minus(1)
                        etNum.setText(
                            "${spcs.shops[positionShop].mCartGoodsSkuList?.get(helper.layoutPosition)?.mCount!!.minus(
                                1
                            )}"
                        )
                        notifyDataSetChanged()
                        var tmp: String? = "0.00"
                        for (ele in cartAdapter.data) {

                            for (item in ele.mCartGoodsSkuList!!) {
                                if (item!!.isSelected == 1) {
                                    tmp = MoneyUtil.moneyAdd(
                                        tmp,
                                        MoneyUtil.moneyMul(item.mPrice.toString(), item.mCount.toString())
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
