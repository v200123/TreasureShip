package com.jzz.treasureship.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsDetail
import com.jzz.treasureship.model.bean.GoodsSku

class SkuSelectAdapter(layoutResId: Int = R.layout.item_sku) :
    BaseBindAdapter<GoodsDetail.GoodsSku>(layoutResId, BR.goodsSku) {

    private var onItemClickListener: OnItemClickListener? = null

    /*暴露给外部的方法*/
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    override fun convert(helper: BindViewHolder, item: GoodsDetail.GoodsSku) {
        super.convert(helper, item)

        Glide.with(helper.itemView.context).load(item.skuImg).into(helper.getView(R.id.tv_logo))
        helper.setText(R.id.tv_itemName, item.specValue)
        /*设置选中状态*/
//        if (helper.adapterPosition == SelectedNavItem.selectedNavItem) {
//            helper.getView<ImageView>(R.id.tv_logo).background = mContext.resources.getDrawable(R.drawable.sku_select)
//        } else {
//            helper.getView<ImageView>(R.id.tv_logo).background = null
//        }
        helper.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(helper.itemView, position = helper.adapterPosition)
        }

    }
}