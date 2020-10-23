package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CartGoodsSku


class CartSelectGoodsAdapter(layoutResId: Int = R.layout.item_order_goods) :
    BaseBindAdapter<CartGoodsSku>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: CartGoodsSku) {
        super.convert(holder, item)

        Glide.with(holder.itemView.context).load(item.mImageUrl).into(holder.getView(R.id.iv_goods))
        holder.setText(R.id.tv_goods_name, item.mName)
        holder.setText(R.id.tv_orderGoods_name,item.mName)
//        holder.setText(R.id.tv_goods_num, "${item.stock}")
        holder.setText(R.id.tv_goods_spece, item.mSpecValue)
        holder.setText(R.id.tv_goods_price, "¥${item.mPrice}")
        holder.setText(R.id.tv_goods_buyCount, "x ${item.mCount}")
    }
}