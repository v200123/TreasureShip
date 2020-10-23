package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.DataXXXX


class InvitedAdapter(layoutResId: Int = R.layout.layout_item_invited) :
    BaseBindAdapter<DataXXXX>(layoutResId){

    override fun convert(helper: BaseViewHolder, item: DataXXXX) {
        super.convert(helper, item)

        Glide.with(context).load(item.avatar).into(helper.getView(R.id.iv_avatar))
        helper.setText(R.id.tv_userName, item.nickName)
        helper.setText(R.id.tv_time, item.registerTime)
    }
}