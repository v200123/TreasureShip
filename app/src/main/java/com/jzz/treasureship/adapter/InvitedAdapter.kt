package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.DataXXXX

class InvitedAdapter(layoutResId: Int = R.layout.layout_item_invited) :
    BaseBindAdapter<DataXXXX>(layoutResId, BR.invitedBean) {

    override fun convert(helper: BindViewHolder, item: DataXXXX) {
        super.convert(helper, item)

        Glide.with(mContext).load(item.avatar).into(helper.getView(R.id.iv_avatar))
        helper.setText(R.id.tv_userName, item.nickName)
        helper.setText(R.id.tv_time, item.registerTime)
    }
}