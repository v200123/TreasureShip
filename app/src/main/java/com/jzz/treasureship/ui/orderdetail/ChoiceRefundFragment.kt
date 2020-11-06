package com.jzz.treasureship.ui.orderdetail

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.OrderDetailsBean
import com.jzz.treasureship.ui.orderdetail.viewModel.OrderDetailViewModel
import com.jzz.treasureship.utils.FragmentBackHandler
import com.lc.liuchanglib.adapterUtils.AdapterHelper
import com.lc.liuchanglib.adapterUtils.setChecked
import com.lc.liuchanglib.ext.click
import com.lc.mybaselibrary.ext.getResColor
import kotlinx.android.synthetic.main.fragment_choice_refund_orders.*
import kotlinx.android.synthetic.main.include_title.*

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 *@Description: 选择批量退款的商品  **/
class ChoiceRefundFragment : Fragment(R.layout.fragment_choice_refund_orders), FragmentBackHandler {

    companion object {

        const val EXTRA_CHoiceStatus = "com.ChoiceRefund.statue"

        fun newInstance(statue: Int) = ChoiceRefundFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_CHoiceStatus, statue) }
        }

    }


    private lateinit var mContext: Context
    val mOrderDetailViewModel by activityViewModels<OrderDetailViewModel>()
    private val mObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                cb__refund_orders_all_inner.isChecked = mAdapter.data.all { it.isSelect }
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                cb__refund_orders_all_inner.isChecked = mAdapter.data.all { it.isSelect }
            }
        }
    }

    private val mAdapter by lazy {
        AdapterHelper.getAdapter(
            R.layout.item_choice_refund_orders,
            object : AdapterHelper.ViewHolderConverter<OrderDetailsBean.GoodsSku> {
                override fun convert(helper: BaseViewHolder, item: OrderDetailsBean.GoodsSku) {
                    helper.setChecked(R.id.cb_refund_item, item.isSelect)
                        .setText(R.id.tv_item_order_refund_list_name, item.mGoodsName)
                        .setText(R.id.tv_item_order_refund_list_sku, item.mAttrValue)
                        .setText(R.id.tv_item_order_refund_list_sku_price, "￥ ${item.mPayMoney}")
                        .setText(R.id.tv_item_order_refund_list_count, "x ${item.mNum}")
                    Glide.with(this@ChoiceRefundFragment)
                        .asDrawable().load(item.mSkuPicture)
                        .into(helper.getView(R.id.tv_item_order_refund_list_image))
                }
            },
            mOrderDetailViewModel.mingleOrderInfo.toMutableList()
        )

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onBackPressed(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = "退款商品列表"
        rlback click {
            (mContext as AppCompatActivity).supportFragmentManager.popBackStack()
        }
        initView()
        initClickListener()
    }


    private fun initView() {
        setStatusColor()
        rv_refund_orders_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        mAdapter.setOnItemClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view: View, i: Int ->
            mOrderDetailViewModel.mingleOrderInfo[i].isSelect = !mOrderDetailViewModel.mingleOrderInfo[i].isSelect
            baseQuickAdapter.notifyItemChanged(i)
        }

        mAdapter.registerAdapterDataObserver(mObserver)
    }

    private fun initClickListener() {
        cb__refund_orders_all_inner click { view ->
            mAdapter.data.forEach { it.isSelect = (view as CheckBox).isChecked }
            mAdapter.notifyDataSetChanged()
        }

        tv_refund_orders_confirm click {
            mOrderDetailViewModel.mingleOrderInfo.asSequence()
                .filter { it.isSelect }
                .takeIf { it.count() > 0 }?.run {
                    if (this.count() == 1)
                        mOrderDetailViewModel.singleOrderInfo = this.toList()[0]
                    else {
                        mOrderDetailViewModel.mingleOrderInfo = this.toList()
                    }

                    if (arguments?.getInt(EXTRA_CHoiceStatus) == 1) {
                        (mContext as AppCompatActivity).supportFragmentManager.commit {
                            addToBackStack("4")
                            hide(this@ChoiceRefundFragment)
                            add(R.id.frame_content, ApplyRefundFragment())
                        }
                    } else {
                        (mContext as AppCompatActivity).supportFragmentManager.commit {
                            addToBackStack("4")
                            hide(this@ChoiceRefundFragment)
                            add(R.id.frame_content, AfterSaleFragment())
                        }
                    }
                } ?: run {
                ToastUtils.showShort("请选择售后商品")
            }
        }
    }

    private fun setStatusColor() {
        StateAppBar.setStatusBarLightMode(
            context as AppCompatActivity,
            mContext.getResColor(R.color.white)
        )
    }

    override fun onPause() {
        super.onPause()
        mAdapter.unregisterAdapterDataObserver(mObserver)
    }



    override fun onDetach() {
        super.onDetach()

    }

}