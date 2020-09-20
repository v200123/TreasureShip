package com.jzz.treasureship.adapter

import android.util.Log
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsAttribute
import com.jzz.treasureship.model.bean.GoodsDetail

class GoodsPropertiesAdapter(layoutResId: Int = R.layout.item_goods_properties) : BaseBindAdapter<GoodsDetail.GoodsAttribute>(layoutResId, BR.goodsAttr) {

    override fun convert(helper: BindViewHolder, item: GoodsDetail.GoodsAttribute) {
        super.convert(helper, item)

        helper.setText(R.id.tv_attrkey,item.attrValue)
        helper.setText(R.id.tv_attrValue,item.attrValueName)
    }
}