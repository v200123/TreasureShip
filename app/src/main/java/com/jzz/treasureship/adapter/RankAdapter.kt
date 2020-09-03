package com.jzz.treasureship.adapter

import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.XX

class RankAdapter(layoutResId: Int = R.layout.layout_item_rank) :
    BaseBindAdapter<XX>(layoutResId, BR.rankData) {

    override fun convert(helper: BindViewHolder, item: XX) {
        super.convert(helper, item)

        when (helper.layoutPosition) {
            0 -> {
                Glide.with(mContext).load(mContext.resources.getDrawable(R.drawable.icon_number_one))
                    .into(helper.getView(R.id.iv_rankNum))
            }
            1 -> {
                Glide.with(mContext).load(mContext.resources.getDrawable(R.drawable.icon_number_two))
                    .into(helper.getView(R.id.iv_rankNum))
            }
            2 -> {
                Glide.with(mContext).load(mContext.resources.getDrawable(R.drawable.icon_number_three))
                    .into(helper.getView(R.id.iv_rankNum))
            }
            else -> {
                helper.setText(R.id.tv_rankNum, "${helper.layoutPosition + 1}")
                Glide.with(mContext).load(mContext.resources.getDrawable(R.drawable.icon_number_another))
                    .into(helper.getView(R.id.iv_rankNum))
            }
        }
        helper.setText(R.id.tv_rankName, item.nickName)
        helper.setText(R.id.tv_rankIncome, "¥ ${item.account}")
    }
}