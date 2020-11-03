package com.jzz.treasureship.ui.orderdetail

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.ui.orderdetail.viewModel.OrderDetailViewModel
import com.lc.liuchanglib.ext.click
import com.lc.mybaselibrary.ext.getResDrawable
import kotlinx.android.synthetic.main.fragment_after_sale.*
import q.rorbin.badgeview.DisplayUtil

/**
 *@PackageName: com.jzz.treasureship.ui.orderdetail
 *@Auth： 29579
 **/
class AfterSaleFragment : BaseVMFragment<BaseViewModel>() {
    override fun getLayoutResId(): Int  = R.layout.fragment_after_sale
    val mOrderDetailViewModel by activityViewModels<OrderDetailViewModel>()

    override fun initVM(): BaseViewModel  = BaseViewModel()

    override fun initView() {
        layout_refund.getLeftBuild().apply { mDrawable = mContext.getResDrawable(R.drawable.icon_refund) }.buildText()
        layout_refund.mLeftTextView.compoundDrawablePadding = DisplayUtil.dp2px(mContext,3f)
        val resDrawable = mContext.getResDrawable(R.drawable.icon_goto)
        layout_refund.mRightTextView.apply {
            compoundDrawablePadding = DisplayUtil.dp2px(mContext,3f)
            setCompoundDrawablesWithIntrinsicBounds(null,null,resDrawable,null) }

        layout_refundAndReturnOfGoods.getLeftBuild().apply { mDrawable = mContext.getResDrawable(R.drawable.icon_remoney) }.buildText()
        layout_refundAndReturnOfGoods.mLeftTextView.compoundDrawablePadding = DisplayUtil.dp2px(mContext,3f)
        layout_refundAndReturnOfGoods.mRightTextView.apply {
            compoundDrawablePadding = DisplayUtil.dp2px(mContext,3f)
            setCompoundDrawablesWithIntrinsicBounds(null,null,resDrawable,null) }

        mOrderDetailViewModel.singleOrderInfo.apply {
            tv_item_detail_shoop_name.text = this.mGoodsName
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
                add(R.id.frame_content,ApplyRefundFragment.newInstance(1))
            }
        }
        layout_refundAndReturnOfGoods click {
           mFragmentManager.commit {
                addToBackStack("2")
                hide(this@AfterSaleFragment)
                add(R.id.frame_content,ApplyRefundFragment.newInstance(2))
            }

        }
    }
}