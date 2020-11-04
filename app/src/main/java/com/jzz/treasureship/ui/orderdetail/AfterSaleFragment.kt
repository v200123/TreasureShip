package com.jzz.treasureship.ui.orderdetail

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.ui.orderdetail.viewModel.OrderDetailViewModel
import com.lc.liuchanglib.ext.click
import com.lc.mybaselibrary.ext.getResDrawable
import kotlinx.android.synthetic.main.fragment_after_sale.*
import kotlinx.android.synthetic.main.include_title.*
import q.rorbin.badgeview.DisplayUtil

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 **/
class AfterSaleFragment : BaseVMFragment<BaseViewModel>() {
    override fun getLayoutResId(): Int = R.layout.fragment_after_sale
    val mOrderDetailViewModel by activityViewModels<OrderDetailViewModel>()
    override fun initVM(): BaseViewModel = BaseViewModel()

    override fun initView() {
        showTopMsg()
        tv_title.text = "售后类型"
        layout_refund.getLeftBuild().apply { mDrawable = mContext.getResDrawable(R.drawable.icon_refund) }.buildText()
        layout_refund.mLeftTextView.compoundDrawablePadding = DisplayUtil.dp2px(mContext, 3f)
        val resDrawable = mContext.getResDrawable(R.drawable.icon_goto)
        layout_refund.mRightTextView.apply {
            compoundDrawablePadding = DisplayUtil.dp2px(mContext, 3f)
            setCompoundDrawablesWithIntrinsicBounds(null, null, resDrawable, null)
        }
        layout_refundAndReturnOfGoods.getLeftBuild()
            .apply { mDrawable = mContext.getResDrawable(R.drawable.icon_remoney) }.buildText()
        layout_refundAndReturnOfGoods.mLeftTextView.compoundDrawablePadding = DisplayUtil.dp2px(mContext, 3f)
        layout_refundAndReturnOfGoods.mRightTextView.apply {
            compoundDrawablePadding = DisplayUtil.dp2px(mContext, 3f)
            setCompoundDrawablesWithIntrinsicBounds(null, null, resDrawable, null)
        }

        mOrderDetailViewModel.singleOrderInfo.apply {
            tv_item_order_list_name.text = this?.mGoodsName
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

    }

    override fun initListener() {
        layout_refund click {
            mFragmentManager.commit {
                addToBackStack("2")
                hide(this@AfterSaleFragment)
                add(R.id.frame_content, ApplyRefundFragment.newInstance(1))
            }
        }
        layout_refundAndReturnOfGoods click {
            mFragmentManager.commit {
                addToBackStack("2")
                hide(this@AfterSaleFragment)
                add(R.id.frame_content, ApplyRefundFragment.newInstance(2))
            }

        }
    }

    private fun showTopMsg() {
        mOrderDetailViewModel.singleOrderInfo?.let {
            tv_item_order_list_name.text = it.mShopName
            Glide.with(this).asDrawable().load(it.mSkuPicture)
                .into(tv_item_order_list_image)
            tv_item_order_name.text = it.mGoodsName
            tv_item_order_list_sku.text = it.mAttrValue
            tv_item_order_list_sku_price.text = "￥"+it.mPrice
            tv_item_order_list_count.text = "x ${it.mNum}"
        }
    }
}