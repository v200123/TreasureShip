package com.jzz.treasureship.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CollectCategory

class CollectAdapter(layoutResId: Int = R.layout.item_collect) :
    BaseBindAdapter<CollectCategory>(layoutResId){

    init {
//        setHeaderFooterEmpty(true,true)

        addChildClickViewIds(
            R.id.layout_collects_item,
            R.id.tvAddCollectType,
            R.id.tvCancle
        )

    }

    override fun convert(helper: BaseViewHolder, item: CollectCategory) {
        super.convert(helper, item)

        helper.setText(R.id.tvTitle, item.title)
        helper.setText(R.id.tvNum, "${item.count}个视频")

    }
}