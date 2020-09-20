package com.jzz.treasureship.ui.coupon

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.listener.OnItemChildClickListener
import com.didichuxing.doraemonkit.widget.bravh.listener.OnLoadMoreListener
import com.didichuxing.doraemonkit.widget.bravh.module.LoadMoreModule
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.Data01
import com.jzz.treasureship.model.bean.header
import com.jzz.treasureship.ui.coupon.requestBody.CouponBody
import com.jzz.treasureship.ui.coupon.viewModel.CouponUseViewModel
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.fragment_common_coupon_used.*

/**
 *@author LC
 *@createTime 2020/9/19 22:27
 *@description  描述文件
 */
class CouponUseFragment : BaseVMFragment<CouponUseViewModel>(false) {
    private val mAdapter by lazy { CouponAdapter(arguments?.getInt(mCouponType) ?: 1) }
    private var nowPosition = 1
    override fun getLayoutResId(): Int = R.layout.fragment_common_coupon_used

    companion object {
        const val mCouponType = "com.coupontype"
        fun newInstance(type: Int) = CouponUseFragment().apply {
            val bundle = Bundle().apply { putInt(mCouponType, type) }
            arguments = bundle
        }
    }


    override fun initVM(): CouponUseViewModel = CouponUseViewModel()

    override fun initView() {
        rv_common_card.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(mContext)
        }

        mAdapter.loadMoreModule.apply {
            isEnableLoadMore = true
            isEnableLoadMoreIfNotFullPage = false
            isAutoLoadMore = false
            setOnLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    mViewModel.getCouponList(
                        BaseRequestBody(
                            CouponBody(arguments?.getInt(mCouponType) ?: 1),
                            header(pageNum = nowPosition, pageSize = 20)
                        )
                    )
                }
            })
        }
        mAdapter.setEmptyView(R.layout.rv_empty_01)



    }

    override fun initData() {
        mViewModel.getCouponList(
            BaseRequestBody(
                CouponBody(arguments?.getInt(mCouponType) ?: 1),
                header(pageNum = nowPosition, pageSize = 20)
            )
        )
    }

    override fun startObserve() {
        mViewModel.couponData.observe(this, {
            mAdapter.addData(it.mData)
            if (it.mTotalPages == nowPosition) {
                mAdapter.loadMoreModule.loadMoreEnd()
            } else {
                ++nowPosition
                mAdapter.loadMoreModule.loadMoreComplete()
            }

        })

    }

    override fun initListener() {
        mAdapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val data01 = adapter.data[position] as Data01
                mContext.start<CouponShopActivity> {
                }

            }

        })
    }


    class CouponAdapter(var type: Int) : BaseQuickAdapter<Data01, BaseViewHolder>(R.layout.item_card_unuse),
        LoadMoreModule {
        init {
            addChildClickViewIds(R.id.btn_goto_shop)
        }

        override fun convert(holder: BaseViewHolder, item: Data01) {
            if (type == 1) {
                holder.setBackgroundResource(R.id.imageView, R.drawable.ico_unusecard_bg)
                    .setGone(R.id.btn_goto_shop, false)
                    .setText(R.id.textView7, "${item.mCouponValue}").setText(R.id.textView10, item.mCouponName)
                    .setText(
                        R.id.textView11,item.mCouponStartTime + "-" + item.mCouponEndTime
                    )
            }
            if (type == 2) {
                holder.setBackgroundResource(R.id.imageView, R.drawable.ico_usecard_bg)
                    .setGone(R.id.btn_goto_shop, true)
                    .setText(R.id.textView7, "${item.mCouponValue}").setText(R.id.textView10, item.mCouponName).setText(
                        R.id.textView11,
                        item.mCouponUseTime + "\t\t已使用"
                    )

            }
            if (type == 3) {
                holder.setText(R.id.textView7, "${item.mCouponValue}").setText(R.id.textView10, item.mCouponName)
                    .setText(
                        R.id.textView11, item.mCouponEndTime + "\t\t已过期"
                    )

            }
            holder.getView<TextView>(R.id.tv_card_unuse_content).apply {
              text = item.mCouponRemark
                if(this.lineCount>1)
                {
                    holder.setGone(R.id.iv_card_see_more,false)
                }else{
                    holder.setGone(R.id.iv_card_see_more,true)
                }
            }


        }

    }

}
