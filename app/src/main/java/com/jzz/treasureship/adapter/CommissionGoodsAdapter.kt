package com.jzz.treasureship.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsSkuX


class CommissionGoodsAdapter(layoutResId: Int = R.layout.layout_item_commission_goods) :
    BaseBindAdapter<GoodsSkuX>(layoutResId){

    override fun convert(helper: BaseViewHolder, item: GoodsSkuX) {
        super.convert(helper, item)

        helper.setText(R.id.tv_goodsName, item.name)
        helper.setText(R.id.tv_price, "¥${item.price}")
        helper.setText(R.id.tv_returnPoint, "${item.returnPoint}%")
        helper.setText(R.id.tv_count, "${item.count}")
        helper.setText(R.id.tv_commission, "¥${item.commissionAccount}")
    }
}