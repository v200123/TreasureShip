package com.jzz.treasureship.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Data
import com.jzz.treasureship.utils.MoneyUtil
import java.math.BigDecimal

class OrdersVpAdapter(layoutResId: Int = R.layout.item_order) :
    BaseBindAdapter<Data>(layoutResId, BR.data) {

    override fun convert(helper: BindViewHolder, item: Data) {
        super.convert(helper, item)
        helper.setText(R.id.tv_order_code, "订单号：${item.orderNo}")

        val status: String
        when (item.orderStatus) {
            0 -> {
                status = "待付款"
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.VISIBLE
            }
            1 -> {
                status = "等待卖家发货"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.VISIBLE
            }
            2 -> {
                status = "已发货"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
            }
            3 -> {
                status = "已收货"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
            }
            8 -> {
                status = "已完成"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
            }
            9 -> {
                status = "退款中"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
            }
            10 -> {
                status = "已退款"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
            }
            11 -> {
                status = "已关闭"
                helper.getView<TextView>(R.id.tv_backphone).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_apply_refund).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ckwl).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_sure_goods).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_del_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_cancal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_appeal_order).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_go_pay).visibility = View.GONE
                helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
            }
            else -> status = "${item.orderStatus}"
        }
        helper.setText(R.id.tv_status, status)

        val childAdapter: OrdersChildAdapter = OrdersChildAdapter()
        helper.getView<RecyclerView>(R.id.mrv_order_childlist).run {
            layoutManager = LinearLayoutManager(mContext).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            this.adapter = childAdapter

            childAdapter.setNewData(item.goodsSkuList)
            childAdapter.notifyDataSetChanged()
        }

        var sum = "0.00"
        for (ele in item.goodsSkuList!!) {
            sum = MoneyUtil.moneyAdd(sum, ele!!.goodsMoney.toString())
        }
        val totalMoney = BigDecimal(sum).stripTrailingZeros().toPlainString()
        helper.setText(R.id.tv_order_allprice, "共${item.goodsNum}件商品  合计:¥${totalMoney}")

        helper.addOnClickListener(R.id.tv_go_pay)
        helper.addOnClickListener(R.id.tv_sure_goods)
        helper.addOnClickListener(R.id.tv_ckwl)
        helper.addOnClickListener(R.id.tv_ask_refund)
    }
}