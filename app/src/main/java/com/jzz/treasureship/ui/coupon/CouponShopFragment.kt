package com.jzz.treasureship.ui.coupon

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.didichuxing.doraemonkit.widget.bravh.module.LoadMoreModule
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.Data03
import com.jzz.treasureship.model.bean.header
import com.jzz.treasureship.ui.coupon.viewModel.CouponShopViewModel
import com.lc.mybaselibrary.out
import kotlinx.android.synthetic.main.fragment_coupon_shop.*
import kotlinx.android.synthetic.main.include_title.*
import java.math.RoundingMode

/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class CouponShopFragment : BaseVMActivity<CouponShopViewModel>(false) {

    private var nowPosition = 0

    override fun getLayoutResId(): Int  = R.layout.fragment_coupon_shop
    private val mAdapter by lazy { couponAdapter(this) }
    override fun initVM(): CouponShopViewModel  = CouponShopViewModel()

    override fun initView() {
        tv_title.text= "适配商城"
        rv_coupon_shop.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(mContext,2)
        }
        mAdapter.loadMoreModule.apply {
            setOnLoadMoreListener {
                "上啦回调调用了".out(true)
                mViewModel.getList(BaseRequestBody( MyHeader = header(pageNum = nowPosition)))
            }
            isEnableLoadMore = true
        }

    }

    override fun initData() {
            mViewModel.getList(BaseRequestBody( MyHeader = header(pageNum = nowPosition)))

    }

    override fun startObserve() {
        mViewModel.mCouponData.observe(this,{

            if(it.mTotalPages == nowPosition)
            {
                mAdapter.loadMoreModule.loadMoreEnd()
            }else{
                mAdapter.loadMoreModule.loadMoreComplete()
                nowPosition ++
            }
            mAdapter.addData(it.mData)
        })
    }


    class couponAdapter(var fragment: AppCompatActivity) : com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter<Data03,
            BaseViewHolder>(R
        .layout
        .item_coupon_shop),LoadMoreModule{
        override fun convert(holder: BaseViewHolder, item: Data03) {
            Glide.with(fragment).asDrawable().load(item.mPicture).into(holder.getView(R.id.iv_coupon_shop))
            holder.setText(R.id.tv_coupon_shop,item.mGoodsName).setText(R.id.tv_coupon_shop_price,item.mPrice
                .toBigDecimal().setScale(2,RoundingMode.HALF_DOWN).toPlainString())
        }

    }

}