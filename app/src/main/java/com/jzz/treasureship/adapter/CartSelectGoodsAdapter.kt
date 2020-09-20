package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CartGoodsSku

class CartSelectGoodsAdapter(layoutResId: Int = R.layout.item_order_goods) :
    BaseBindAdapter<CartGoodsSku>(layoutResId, BR.goodsSku) {

    override fun convert(helper: BindViewHolder, item: CartGoodsSku) {
        super.convert(helper, item)

        Glide.with(helper.itemView.context).load(item.mImageUrl).into(helper.getView(R.id.iv_goods))
        helper.setText(R.id.tv_goods_name, item.mName)
        helper.setText(R.id.tv_orderGoods_name,item.mName)
//        helper.setText(R.id.tv_goods_num, "${item.stock}")
        helper.setText(R.id.tv_goods_spece, item.mSpecValue)
        helper.setText(R.id.tv_goods_price, "¥${item.mPrice}")
        helper.setText(R.id.tv_goods_buyCount, "x ${item.mCount}")
    }
}