package com.jzz.treasureship.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.OrdersListBean

class OrdersVpAdapter(layoutResId: Int = R.layout.item_order)
    : BaseBindAdapter<OrdersListBean.Data>(layoutResId){
    init {
        addChildClickViewIds(
            R.id.tv_go_pay,
            R.id.tv_sure_goods,
            R.id.tv_ckwl,
            R.id.tv_ask_refund
        )
    }

    override fun convert(helper: BaseViewHolder, item: OrdersListBean.Data) {
        super.convert(helper, item)
        helper.setText(R.id.tv_order_code, "订单号：${item.mOrderNo}")

        val status: String
        when (item.mOrderStatus) {
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
            else -> status = "${item.mOrderStatus}"
        }
        helper.setText(R.id.tv_status, status)

        val childAdapter: OrdersChildAdapter = OrdersChildAdapter()
        helper.getView<RecyclerView>(R.id.mrv_order_childlist).run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            this.adapter = childAdapter
            childAdapter.setList(item.mGoodsSkuList)
            childAdapter.notifyDataSetChanged()
        }

        if (item.mPayMoney == 0.0f) {
            helper.getView<TextView>(R.id.tv_ask_refund).visibility = View.GONE
        }

//        var sum = "0.00"
//        for (ele in item.goodsSkuList!!) {
//            sum = MoneyUtil.moneyAdd(sum, ele!!.goodsMoney.toString())
//        }
//        val totalMoney = BigDecimal(sum).stripTrailingZeros().toPlainString()
        helper.setText(R.id.tv_order_allprice, "共${item.mGoodsNum}件商品  合计:¥${item.mPayMoney}")

    }
}