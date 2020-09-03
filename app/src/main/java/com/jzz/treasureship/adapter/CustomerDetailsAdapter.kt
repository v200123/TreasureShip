package com.jzz.treasureship.adapter

import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jzz.treasureship.BR
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Data

class CustomerDetailsAdapter(layoutResId: Int = R.layout.layout_customer_detail_item) :
    BaseBindAdapter<Data>(layoutResId, BR.customerOrder) {

    override fun convert(helper: BindViewHolder, item: Data) {
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

        helper.addOnClickListener(R.id.tv_checkDoctorAdvice)
        helper.addOnClickListener(R.id.tv_check_commission)

        val mAdapter: OrdersChildAdapter = OrdersChildAdapter()
        mAdapter.setNewData(item.goodsSkuList)
        helper.getView<RecyclerView>(R.id.mrv_prolist).run {
            layoutManager = LinearLayoutManager(mContext).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            this.adapter = mAdapter

            mAdapter.setNewData(item.goodsSkuList)
            mAdapter.notifyDataSetChanged()
        }
    }
}