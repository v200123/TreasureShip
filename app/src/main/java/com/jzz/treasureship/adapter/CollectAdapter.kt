package com.jzz.treasureship.adapter

import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CollectCategory

class CollectAdapter(layoutResId: Int = R.layout.item_collect) : BaseBindAdapter<CollectCategory>(layoutResId,BR.collects) {

    init {
        setHeaderFooterEmpty(true,true)
    }

    override fun convert(helper: BindViewHolder, item: CollectCategory) {
        super.convert(helper, item)

        helper.setText(R.id.tvTitle,item.title)
        helper.setText(R.id.tvNum,"${item.count}个视频")

        helper.addOnClickListener(R.id.layout_collects_item)
        helper.addOnClickListener(R.id.tvAddCollectType)
        helper.addOnClickListener(R.id.tvCancle)
    }
}