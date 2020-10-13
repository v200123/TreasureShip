package com.jzz.treasureship.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.DataX


class ContacterAdapter(layoutResId: Int = R.layout.item_address_book) :
    BaseBindAdapter<DataX>(layoutResId) {

    init {
        addChildClickViewIds(R.id.layout_middle_info,
        R.id.iv_userIco,
        R.id.layout_times)
    }

    override fun convert(helper: BaseViewHolder, item: DataX) {
        super.convert(helper, item)

        Glide.with(context).load(item.avatar).apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(helper.getView(R.id.iv_userIco))

        helper.setText(R.id.tv_userName, item.nikeName)

        var sex = "未知"
        when (item.sex) {
            0 -> {
                sex = "男"
                Glide.with(context).load(context.resources.getDrawable(R.drawable.icon_male))
                    .into(helper.getView(R.id.iv_sex))
            }

            1 -> {
                sex = "女"
                Glide.with(context).load(context.resources.getDrawable(R.drawable.icon_female))
                    .into(helper.getView(R.id.iv_sex))
            }
        }
        helper.setText(R.id.tv_sex, sex)
        helper.setText(R.id.tv_time, item.lastedBuyTime)
        helper.setText(R.id.tv_count, "${item.buyNum}")

        when (item.notice) {
            0 -> {
                //未设置提醒
                Glide.with(context).load(context.resources.getDrawable(R.drawable.icon_clock_normal))
                    .into(helper.getView(R.id.iv_clock))
            }
            1 -> {
                //设置提醒
                Glide.with(context).load(context.resources.getDrawable(R.drawable.ic_clock_clocked))
                    .into(helper.getView(R.id.iv_clock))
                this.addChildClickViewIds(R.id.iv_clock)
            }
        }

    }
}