package com.jzz.treasureship.adapter

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.HotSearch


class HotSearchAdapter(layoutResId: Int = R.layout.item_hot_search) :
    BaseBindAdapter<HotSearch>(layoutResId){
    init {
        addChildClickViewIds(R.id.layout_hot_search)
    }
    override fun convert(helper: BaseViewHolder, item: HotSearch) {
        super.convert(helper, item)
        helper.setText(R.id.iv_hot, "${helper.adapterPosition + 1}")
        helper.setText(R.id.tv_hot_content, item.videoName)

    }
}

