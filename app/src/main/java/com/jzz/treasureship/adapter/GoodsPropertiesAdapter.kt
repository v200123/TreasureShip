package com.jzz.treasureship.adapter

import android.util.Log
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsDetail


class GoodsPropertiesAdapter(layoutResId: Int = R.layout.item_goods_properties) :
    BaseBindAdapter<GoodsDetail.GoodsAttribute>(layoutResId) {

    override fun convert(helper: BaseViewHolder, item: GoodsDetail.GoodsAttribute) {
        super.convert(helper, item)

        helper.setText(R.id.tv_attrkey, item.attrValue)
        helper.setText(R.id.tv_attrValue, item.attrValueName)
    }
}