package com.jzz.treasureship.ui.orders

import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.OrderRefundListBean
import com.lc.liuchanglib.adapterUtils.AdapterHelper
import com.lc.liuchanglib.cusotmView.LeftAndRightView
import com.lc.mybaselibrary.ext.getResDrawable
import kotlinx.android.synthetic.main.fragement_ordervp_applyreturn.*

/**
 *@PackageName: com.jzz.treasureship.ui.orders
 *@Auth： 29579
 *@Description: 我的售后/退款界面  **/
class OrderRefundFragment : BaseVMFragment<OrderRefundViewModel>() {

    private val mRefundList: MutableList<OrderRefundListBean.Data> = mutableListOf()
    private var mPagerNum = 1
    private val mAdapter by lazy {
        AdapterHelper.getAdapter(
            R.layout.item_order_refund_list,
            object : AdapterHelper.ViewHolderConverter<OrderRefundListBean.Data> {
                override fun convert(helper: BaseViewHolder, item: OrderRefundListBean.Data) {
                    val goodsSku = item.mGoodsSkuList[0]
                    helper.setText(
                        R.id.tv_item_order_name,
                        if (goodsSku.mGoodsType == 1) "【境外商品】${goodsSku.mGoodsName}" else goodsSku.mGoodsName
                    ).setText(R.id.tv_item_order_list_sku, goodsSku.mAttrValue)
                        .setText(R.id.tv_item_order_list_sku_price, "¥${goodsSku.mSkuPicture}")
                        .setText(R.id.tv_item_order_list_count, "x ${goodsSku.mNum}")

                    helper.getView<LeftAndRightView>(R.id.tv_item_order_list_name).apply {
                        getLeftBuild().mTextMsg = item.mShopName
                        getLeftBuild().mDrawable = mContext.getResDrawable(R.drawable.ico_shop_name)
                        getLeftBuild().buildText()
                        mRightValue.mTextMsg = if (item.mRefundType == 1) "仅退款" else "退货退款"
                        mRightValue.mTextBold = true
                        mRightValue.buildText()
                        mRightTextView.setCompoundDrawablesWithIntrinsicBounds(
                            if (item.mRefundType == 1) mContext.getResDrawable(R.drawable.icon_remoney) else mContext.getResDrawable(
                                R.drawable.icon_refund
                            ), null, null, null
                        )
                    }

                }

            },
            mRefundList,
        ).apply {
            addChildClickViewIds(R.id.tv_item_order_list_seemore, R.id.tv_item_order_list_sudo)

            setOnItemClickListener { adapter, view, position ->

                when (view.id) {
                    R.id.tv_item_order_list_seemore -> {
                        ToastUtils.showShort("跳转到新的界面")
                    }
                }


            }
        }


    }

    override fun getLayoutResId(): Int = R.layout.fragement_ordervp_applyreturn

    override fun initVM(): OrderRefundViewModel = OrderRefundViewModel()

    override fun initView() {
        rv_show_return.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mAdapter
        }
    }

    override fun initData() {
        mViewModel.getRefundList(1)
    }

    override fun startObserve() {
        mViewModel.RefundList.observe(this) {
            if (mPagerNum == 1) {
                mAdapter.setList(it.mData)
            } else {
                mAdapter.addData(it.mData)
            }
        }
    }

    override fun initListener() {

    }


}