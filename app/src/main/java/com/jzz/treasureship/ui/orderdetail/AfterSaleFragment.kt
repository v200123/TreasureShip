package com.jzz.treasureship.ui.orderdetail

import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.base.BaseViewModel
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
    }

    override fun initData() {

    }

    override fun startObserve() {

    }

    override fun initListener() {
        layout_refund click {
            ToastUtils.showShort("去退款")
        }
        layout_refundAndReturnOfGoods click {
            ToastUtils.showShort("去退款退货")

        }
    }
}