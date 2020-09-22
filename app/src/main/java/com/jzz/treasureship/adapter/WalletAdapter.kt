package com.jzz.treasureship.adapter

import android.content.Context
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Data04
import com.lc.mybaselibrary.ShapeConstraintLayout
import q.rorbin.badgeview.DisplayUtil


class WalletAdapter( mContext: Context) :
    BaseQuickAdapter<Data04, BaseViewHolder>(R.layout.layout_item_wallet_list) {
    private val _8dp = DisplayUtil.dp2px(mContext, 8f).toFloat()
    override fun convert(holder: BaseViewHolder, item: Data04) {
        holder.getView<ShapeConstraintLayout>(R.id.shape_constraint_wallet)
            .apply {
                if (holder.adapterPosition == 0) {
                    shapeBuilder!!.setShapeCornersTopLeftRadius(_8dp)
                        .setShapeCornersTopRightRadius(_8dp)
                    holder.setGone(R.id.view_wallet,false)
                }

                if (holder.adapterPosition != 0 && holder.adapterPosition < data.size - 1) {
                    shapeBuilder!!.setShapeCornersTopLeftRadius(0f)
                        .setShapeCornersTopRightRadius(0f)
                    holder.setGone(R.id.view_wallet,false)

                }
                if (holder.adapterPosition == data.size - 1) {
                    shapeBuilder!!.setShapeCornersBottomLeftRadius(_8dp)
                        .setShapeCornersBottomRightRadius(_8dp)
                    holder.setGone(R.id.view_wallet,true)
                }
<<<<<<< HEAD
            }
=======
            }p
>>>>>>> 274fdd01a1084d68a02a4c6364e6c6bcbf9e18da
        holder.setText(R.id.tv_title, item.mTitle)
            .setText(R.id.tv_createTime, item.mCreateTime)
            .setText(R.id.tv_wallet_balance, "余额：" + item.mBalance)
            .setText(R.id.tv_money, if (item.mMoney < 0) "${item.mMoney}" else "+${item.mMoney}")
    }


    override fun convert(holder: BaseViewHolder, item: Data04, payloads: List<Any>) {
        super.convert(holder, item, payloads)
    }

//
//    override fun convert(helper: BaseBindAdapter.BindViewHolder, item: Data04) {
//        super.convert(helper, item)
//
//        helper.getView<ShapeConstraintLayout>(R.id.shape_constraint_wallet)
//            .apply {
//                if (helper.adapterPosition == 0) {
//                    shapeBuilder!!.setShapeCornersTopLeftRadius(_8dp)
//                        .setShapeCornersTopRightRadius(_8dp)
//                }
//
//            }
//
//        helper.setText(R.id.tv_title, item.title)
//        helper.setText(R.id.tv_createTime, item.createTime)
//
//        when (item.moneyType) {
//            0 -> {
//                //收入
//                if (item.money!! < 0) {
//                    helper.setText(R.id.tv_money, "${item.money}")
//                } else {
//                    helper.setText(R.id.tv_money, "+${item.money}")
//                }
//                helper.getView<TextView>(R.id.tv_money).setTextColor(mContext.resources.getColor(R.color.swipe_red))
//            }
//            1 -> {
//                //支出
//                if (item.money!! < 0) {
//                    helper.setText(R.id.tv_money, "${item.money}")
//                } else {
//                    helper.setText(R.id.tv_money, "+${item.money}")
//                }
//                helper.getView<TextView>(R.id.tv_money).setTextColor(mContext.resources.getColor(R.color.black_333333))
//            }
//        }
//    }
}