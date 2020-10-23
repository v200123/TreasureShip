package com.jzz.treasureship.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.EditText
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.SkuSelectAdapter
import com.jzz.treasureship.model.bean.GoodsDetail
import com.jzz.treasureship.utils.SelectedNavItem
import com.jzz.treasureship.utils.out
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.layout_sku.view.*

/**
 * 商品的SKU选择
 */
@SuppressLint("ViewConstructor")
class CustomSkuBottomPopup(context: Context, goodsSku: GoodsDetail, selectPosition: Int = 0) :
    BottomPopupView(context) {
    private val mCountTips by lazy {
        XPopup.Builder(context).hasShadowBg(false)
            .atView(et_sku_quantity_input)
            .isCenterHorizontal(true)
            .asCustom(DialogOrderCount(context))
    }
    private var mInitSelect = selectPosition
    private val skuListAdapter by lazy { SkuSelectAdapter() }
//    private var SELECTED_SKU by PreferenceUtils(PreferenceUtils.SELECTED_SKU,"")
//    val s by PreferenceUtils(PreferenceUtils.SELECTED_SKU, "")

    private val mGoods: GoodsDetail = goodsSku
    var mCount: Int = 0

    //0:加入购物车，1：立即支付
    var mWhere: Int = -1

    override fun getImplLayoutId() = R.layout.layout_sku

    override fun onCreate() {
        super.onCreate()
        mCount = et_sku_quantity_input.text.toString().toInt()
        et_sku_quantity_input.setSelection(et_sku_quantity_input.text.length)

        mGoods.goodsSku?.get(mInitSelect)?.let {
            Glide.with(context).load(it.skuImg).into(iv_sku_logo)
            tv_sku_selling_price.text = "¥ ${it.price}"
            tv_sku_selling_price_unit.text = "已选：${it.specValue}"
            tv_sku_info.text = "库存：${it.stock}"
        }
        SelectedNavItem.setSectedNavItem(mInitSelect)
        rv_skuList.run {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
            }
            skuListAdapter.setList(mGoods.goodsSku)
            skuListAdapter.notifyDataSetChanged()
            adapter = skuListAdapter
        }

        ib_sku_close.setOnClickListener {
            dismiss()
        }
        skuListAdapter.run {
            setOnItemClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view: View, i: Int ->
                SelectedNavItem.setSectedNavItem(i)
                mGoods.goodsSku?.get(i)?.let {
                    Glide.with(context).load(it.skuImg).into(iv_sku_logo)
                    tv_sku_selling_price.text = "¥ ${it.price}"
                    tv_sku_selling_price_unit.text = "已选：${it.specValue}"
                    tv_sku_info.text = "库存：${it.stock}"
                }
                notifyDataSetChanged()
            }

        }

        tv_addCart.setOnClickListener {
            mWhere = 0
            dismiss()
        }

        tv_Now.setOnClickListener {
            mWhere = 1
            dismiss()
        }

        iv_addCount.setOnClickListener {
            if (mCount < 3) {
                mCount += 1
            } else {
                mCountTips.show()
            }
            et_sku_quantity_input.setText("${mCount}")
            et_sku_quantity_input.setSelection(et_sku_quantity_input.text.length)

        }
        et_sku_quantity_input.setOnClickListener {
            (it as EditText).isCursorVisible = true
        }

        iv_subCount.setOnClickListener {
            if (mCount > 1) {
                mCount -= 1
            }

            et_sku_quantity_input.setText("${mCount}")

        }
    }

    override fun onDismiss() {
        super.onDismiss()
        "我被退出来了".out(true)
    }

}