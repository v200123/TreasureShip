package com.jzz.treasureship.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.blankj.utilcode.util.GsonUtils
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.SkuSelectAdapter
import com.jzz.treasureship.model.bean.GoodsDetail
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.SelectedNavItem
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.layout_sku.view.*


class CustomSkuBottomPopup(context: Context, goodsSku: GoodsDetail) : BottomPopupView(context) {

    private val skuListAdapter by lazy { SkuSelectAdapter() }
    private var SELECTED_SKU by PreferenceUtils(PreferenceUtils.SELECTED_SKU,"")

    private val mGoods: GoodsDetail = goodsSku
    var mCount: Int = 0
    //0:加入购物车，1：立即支付
    var mWhere:Int = -1

    override fun getImplLayoutId() = R.layout.layout_sku

    override fun initPopupContent() {
        super.initPopupContent()
        mCount = et_sku_quantity_input.text.toString().toInt()
        et_sku_quantity_input.setSelection(et_sku_quantity_input.text.length)
        et_sku_quantity_input.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                et_sku_quantity_input.setSelection(s?.length?:0)
            }
        })


        mGoods.goodsSku?.get(0)?.skuImg.let {
            Glide.with(context).load(it).into(iv_sku_logo)
        }

        tv_sku_selling_price.text = "¥ ${mGoods.goodsSku?.get(0)?.price}"
        tv_sku_selling_price_unit.text = "已选：${mGoods.goodsSku?.get(0)?.specValue}"

        tv_sku_info.text = "库存：${mGoods.goodsSku?.get(0)?.stock}"

        rv_skuList.run {
            layoutManager = FlexboxLayoutManager(context).apply {
                flexWrap = FlexWrap.WRAP
            }

            skuListAdapter.setNewData(mGoods.goodsSku)
            skuListAdapter.notifyDataSetChanged()
            adapter = skuListAdapter
        }

        ib_sku_close.setOnClickListener {
            dismiss()
        }
        skuListAdapter.run {
//            if (SELECTED_SKU.isBlank()){
                SELECTED_SKU  = GsonUtils.toJson(mGoods.goodsSku?.get(0))
//            }
            this.setOnItemClickListener(object : SkuSelectAdapter.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    SelectedNavItem.setSectedNavItem(position)
                    notifyDataSetChanged()
                    SELECTED_SKU  = GsonUtils.toJson(mGoods.goodsSku?.get(position))
                    tv_sku_info.text = "库存：${mGoods.goodsSku?.get(position)?.stock}"
                    Log.d("selectGoods",SELECTED_SKU)
                    Glide.with(context).load(mGoods.goodsSku?.get(position)?.skuImg).into(iv_sku_logo)
                    tv_sku_selling_price.text = "¥ ${mGoods.goodsSku?.get(position)?.price}"
                    tv_sku_selling_price_unit.text = "已选：${mGoods.goodsSku?.get(position)?.specValue}"
                }
            })
            Log.d("selectGoods",SELECTED_SKU)
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
            mCount += 1
            et_sku_quantity_input.setText("${mCount}")

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
}