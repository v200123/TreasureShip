package com.jzz.treasureship.adapter

import android.widget.TextView
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.CartGoodsSku


class DoctorAdviceSkuAdapter(layoutResId: Int = R.layout.layout_add_doctor_advice_sku) :
    BaseBindAdapter<CartGoodsSku>(layoutResId, BR.sku) {

    //隐藏
    private var isHide = false
    //展开
    private var isOpen = false


    override fun convert(helper: BindViewHolder, item: CartGoodsSku) {
        super.convert(helper, item)

        helper.getView<TextView>(R.id.tv_name).text = item.mName
        helper.getView<TextView>(R.id.tv_count).text = item.mCount.toString()
    }
}