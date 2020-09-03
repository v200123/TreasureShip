package com.jzz.treasureship.adapter

import android.util.Log
import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.PayGoodsItem

class CartSelectGoodsAdapter(layoutResId: Int = R.layout.item_order_goods) :
    BaseBindAdapter<CartGoodsSku>(layoutResId, BR.goodsSku) {

    override fun convert(helper: BindViewHolder, item: CartGoodsSku) {
        super.convert(helper, item)

        Glide.with(helper.itemView.context).load(item.imageUrl).into(helper.getView(R.id.iv_goods))
        helper.setText(R.id.tv_goods_name, item.name)
//        helper.setText(R.id.tv_goods_num, "${item.stock}")
        helper.setText(R.id.tv_goods_spece, item.specValue)
        helper.setText(R.id.tv_goods_price, "¥${item.price}")
        helper.setText(R.id.tv_goods_buyCount, "x ${item.count}")
    }
}