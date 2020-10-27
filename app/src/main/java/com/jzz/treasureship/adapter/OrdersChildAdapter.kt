package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.OrdersListBean


class OrdersChildAdapter (layoutResId: Int = R.layout.item_order_goods) :
    BaseBindAdapter<OrdersListBean.Data.GoodsSku>(layoutResId){

    override fun convert(holder: BaseViewHolder, item: OrdersListBean.Data.GoodsSku) {

        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.getView(R.id.iv_goods))
        holder.setText(R.id.tv_goods_name, item.name)
        holder.setText(R.id.tv_orderGoods_name, item.shopName)
        holder.setText(R.id.tv_goods_num,item.attrValue)
        holder.setText(R.id.tv_goods_price, "¥${item.price}")
        holder.setText(R.id.tv_goods_buyCount, "x ${item.count}")
    }
}