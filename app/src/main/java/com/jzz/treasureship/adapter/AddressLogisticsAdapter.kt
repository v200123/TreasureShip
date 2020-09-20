package com.jzz.treasureship.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Address
import com.jzz.treasureship.model.bean.AddressLogisticsBean

class AddressLogisticsAdapter(layoutResId: Int = R.layout.layout_logistics_timeline) :
    BaseBindAdapter<AddressLogisticsBean>(layoutResId, BR.addressLogisticData) {

    override fun convert(helper: BindViewHolder, item: AddressLogisticsBean) {
        super.convert(helper, item)

        if (item.status == 1) {
            Glide.with(mContext).load(mContext.resources.getDrawable(R.drawable.logistics_arrived))
                .into(helper.getView(R.id.image))
        } else if (item.status == 0) {
            Glide.with(mContext).load(mContext.resources.getDrawable(R.drawable.logistics_normal))
                .into(helper.getView(R.id.image))
        }

        helper.getView<TextView>(R.id.show_time).text = item.title
    }
}