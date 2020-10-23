package com.jzz.treasureship.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.AddressLogisticsBean



class AddressLogisticsAdapter(layoutResId: Int = R.layout.layout_logistics_timeline) :
    BaseBindAdapter<AddressLogisticsBean>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: AddressLogisticsBean) {
        super.convert(holder, item)

        if (item.status == 1) {
            Glide.with(context).load(context.getDrawable(R.drawable.logistics_arrived))
                .into(holder.getView(R.id.image))
        } else if (item.status == 0) {
            Glide.with(context).load(context.getDrawable(R.drawable.logistics_normal))
                .into(holder.getView(R.id.image))
        }

        holder.getView<TextView>(R.id.show_time).text = item.title
    }
}