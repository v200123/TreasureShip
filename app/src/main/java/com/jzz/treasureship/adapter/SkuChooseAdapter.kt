package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsDetail


class SkuChooseAdapter(layoutResId: Int = R.layout.layout_sku_img) :
    BaseBindAdapter<GoodsDetail.GoodsSku>(layoutResId){

    override fun convert(helper: BaseViewHolder, item: GoodsDetail.GoodsSku) {
        super.convert(helper, item)

        Glide.with(context).load(item.skuImg).into(helper.getView(R.id.iv_sku))
    }
}