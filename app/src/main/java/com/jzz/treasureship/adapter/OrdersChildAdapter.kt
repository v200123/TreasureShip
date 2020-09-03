package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CartGoodsSku
import com.jzz.treasureship.model.bean.GoodsSkuX

class OrdersChildAdapter (layoutResId: Int = R.layout.item_order_goods) :
    BaseBindAdapter<GoodsSkuX>(layoutResId, BR.goodsSkuX){

    override fun convert(helper: BindViewHolder, item: GoodsSkuX) {

        Glide.with(helper.itemView.context).load(item.imageUrl).into(helper.getView(R.id.iv_goods))
        helper.setText(R.id.tv_goods_name, item.name)
        helper.setText(R.id.tv_goods_num,item.attrValue)
        helper.setText(R.id.tv_goods_price, "¥${item.price}")
        helper.setText(R.id.tv_goods_buyCount, "x ${item.count}")
    }
}