package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsSku

class SkuChooseAdapter(layoutResId: Int = R.layout.layout_sku_img) :
    BaseBindAdapter<GoodsSku>(layoutResId, BR.goodSku) {

    override fun convert(helper: BindViewHolder, item: GoodsSku) {
        super.convert(helper, item)

        Glide.with(mContext).load(item.skuImg).into(helper.getView(R.id.iv_sku))
    }
}