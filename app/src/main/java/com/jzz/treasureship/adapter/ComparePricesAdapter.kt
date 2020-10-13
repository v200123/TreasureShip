package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsDetail


class ComparePricesAdapter(layoutResId: Int = R.layout.item_compare_prices) : BaseBindAdapter<GoodsDetail.GoodsSku.Parity>(
    layoutResId
) {

    override fun convert(helper: BaseViewHolder, item: GoodsDetail.GoodsSku.Parity) {
        super.convert(helper, item)

        Glide.with(context).load(item.imageUrl).into(helper.getView(R.id.iv_mallIcon))
        helper.setText(R.id.tvDesc, item.name)
        helper.setText(R.id.tvPrice, "¥${item.price}")
    }
}
