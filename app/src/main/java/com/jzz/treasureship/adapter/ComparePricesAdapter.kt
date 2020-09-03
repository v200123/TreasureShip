package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Parity

class ComparePricesAdapter(layoutResId: Int = R.layout.item_compare_prices) : BaseBindAdapter<Parity>(
    layoutResId,
    BR.parity
) {

    override fun convert(helper: BindViewHolder, item: Parity) {
        super.convert(helper, item)

        Glide.with(mContext).load(item.imageUrl).into(helper.getView(R.id.iv_mallIcon))
        helper.setText(R.id.tvDesc, item.name)
        helper.setText(R.id.tvPrice, "¥${item.price}")
    }
}
