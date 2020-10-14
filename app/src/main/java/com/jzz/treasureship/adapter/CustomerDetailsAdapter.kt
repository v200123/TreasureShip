package com.jzz.treasureship.adapter

import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Data


class CustomerDetailsAdapter(layoutResId: Int = R.layout.layout_customer_detail_item) :
    BaseBindAdapter<Data>(layoutResId){

    init {
        addChildClickViewIds(
            R.id.tv_checkDoctorAdvice,
            R.id.tv_check_commission
        )
    }

    override fun convert(helper: BaseViewHolder, item: Data) {
        super.convert(helper, item)

        helper.setText(R.id.tv_shop_name, item.shopName)
        var status = ""
        when (item.orderStatus) {
            0 -> {
                status = "待付款"
            }
            1 -> {
                status = "等待卖家发货"
            }
            2 -> {
                status = "已发货"
            }
            3 -> {
                status = "已收货"
            }
            8 -> {
                status = "交易完成"
            }
            9 -> {
                status = "退款中"
            }
            10 -> {
                status = "已退款"
            }
            11 -> {
                status = "已关闭"
            }
            else -> status = "${item.orderStatus}"
        }
        helper.setText(R.id.tv_status, status)
        helper.setText(R.id.tv_receiveTime, "收货时间 ${item.signTime}")

        val mAdapter: OrdersChildAdapter = OrdersChildAdapter()
        mAdapter.setList(item.goodsSkuList)
        helper.getView<RecyclerView>(R.id.mrv_prolist).run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            this.adapter = mAdapter

            mAdapter.setList(item.goodsSkuList)
            mAdapter.notifyDataSetChanged()
        }
    }
}