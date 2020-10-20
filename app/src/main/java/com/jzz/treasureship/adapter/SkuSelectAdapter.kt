package com.jzz.treasureship.adapter

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.GoodsDetail
import com.jzz.treasureship.utils.SelectedNavItem
import com.lc.mybaselibrary.ShapeTextView

class SkuSelectAdapter(layoutResId: Int = R.layout.item_sku) :
    BaseBindAdapter<GoodsDetail.GoodsSku>(layoutResId){

    private var onItemClickListener: OnItemClickListener? = null

    /*暴露给外部的方法*/
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    override fun convert(holder: BaseViewHolder, item: GoodsDetail.GoodsSku) {
        super.convert(holder, item)

        Glide.with(holder.itemView.context).load(item.skuImg).into(holder.getView(R.id.tv_logo))
        holder.setText(R.id.tv_itemName, item.specValue)
        /*设置选中状态*/
        if (holder.adapterPosition == SelectedNavItem.selectedNavItem) {
            holder.getView<ShapeTextView>(R.id.tv_itemName).apply {
                shapeBuilder!!.setShapeSolidColor(Color.parseColor("#FFFBE9EA"))
                    .setShapeStrokeColor(Color.parseColor("#FFFBE9EA")).into(this)
                setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        } else {
            holder.getView<ShapeTextView>(R.id.tv_itemName).apply {
                shapeBuilder!!.setShapeSolidColor(Color.parseColor("#FFF5F5F5"))
                    .setShapeStrokeColor(Color.parseColor("#FFF5F5F5")).into(this)
                setTextColor(ContextCompat.getColor(context, R.color.black_121212))
            }
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(holder.itemView, position = holder.adapterPosition)
            }

        }
    }
}