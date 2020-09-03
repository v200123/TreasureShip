package com.jzz.treasureship.adapter

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CollectCategory
import com.jzz.treasureship.model.bean.HotSearch
import com.jzz.treasureship.model.bean.VideoData


class HotSearchAdapter(layoutResId: Int = R.layout.item_hot_search) :
    BaseBindAdapter<HotSearch>(layoutResId, BR.hotSearch) {

    override fun convert(helper: BindViewHolder, item: HotSearch) {
        super.convert(helper, item)
        helper.setText(R.id.iv_hot, "${helper.adapterPosition + 1}")
        helper.setText(R.id.tv_hot_content, item.videoName)

        helper.addOnClickListener(R.id.layout_hot_search)
    }
}

