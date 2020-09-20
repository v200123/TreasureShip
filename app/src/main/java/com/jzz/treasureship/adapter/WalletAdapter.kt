package com.jzz.treasureship.adapter

import android.widget.TextView
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.DataXXX

class WalletAdapter(layoutResId: Int = R.layout.layout_item_wallet_list) :
    BaseBindAdapter<DataXXX>(layoutResId, BR.walletData) {

    override fun convert(helper: BindViewHolder, item: DataXXX) {
        super.convert(helper, item)

        helper.setText(R.id.tv_title, item.title)
        helper.setText(R.id.tv_createTime, item.createTime)

        when (item.moneyType) {
            0 -> {
                //收入
                if (item.money!! < 0){
                    helper.setText(R.id.tv_money,"${item.money}")
                }else{
                    helper.setText(R.id.tv_money,"+${item.money}")
                }
                helper.getView<TextView>(R.id.tv_money).setTextColor(mContext.resources.getColor(R.color.swipe_red))
            }
            1 -> {
                //支出
                if (item.money!! < 0){
                    helper.setText(R.id.tv_money,"${item.money}")
                }else{
                    helper.setText(R.id.tv_money,"+${item.money}")
                }
                helper.getView<TextView>(R.id.tv_money).setTextColor(mContext.resources.getColor(R.color.black_333333))
            }
        }
    }
}