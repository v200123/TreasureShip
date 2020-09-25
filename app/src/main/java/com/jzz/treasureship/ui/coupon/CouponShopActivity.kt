package com.jzz.treasureship.ui.coupon

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.listener.OnItemClickListener
import com.didichuxing.doraemonkit.widget.bravh.module.LoadMoreModule
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.GlideImageLoader
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.Data03
import com.jzz.treasureship.model.bean.header
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.coupon.viewModel.CouponShopViewModel
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.start
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_coupon_shop.*
import kotlinx.android.synthetic.main.include_title.*
import java.math.RoundingMode

/**
 *@date: 2020/9/20
 *@describe:
 *@Auth: 29579
 **/
class CouponShopActivity : BaseVMActivity<CouponShopViewModel>(false) {
    private var hasBanner = false
    private var nowPosition = 1
    override fun getLayoutResId(): Int = R.layout.fragment_coupon_shop
    private val mAdapter by lazy { couponAdapter(this) }
    private var mTotalOffsetY = 0
    override fun initVM(): CouponShopViewModel = CouponShopViewModel()

    override fun initView() {
        tv_title.text = "商品推荐"
        StateAppBar.setStatusBarLightMode(this, mContext.getResColor(R.color.white))
        rv_coupon_shop.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(mContext, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {

                        if (hasBanner) {
                            if (position == 0) {
                                return 2
                            }
                            if (position == mAdapter.data.size + 1)
                                return 2
                            else
                                return 1

                        } else {
                            return if (position == mAdapter.data.size)
                                2
                            else
                                1
                        }


                    }

                }
            }
        }
        rlback.setOnClickListener { finish() }
        rv_coupon_shop.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                mTotalOffsetY += dy
//                if(mTotalOffsetY>60)
//                {
//                    tv_coupon_shop_content.translationY = -mTotalOffsetY.toFloat()
//                }
            }
        })


        mAdapter.loadMoreModule.apply {
            setOnLoadMoreListener {
                mViewModel.getList(BaseRequestBody(MyHeader = header(pageNum = nowPosition)))
            }
            isEnableLoadMore = true
            isAutoLoadMore = false
            isEnableLoadMoreIfNotFullPage = false
        }


        mAdapter.setOnItemClickListener(object : OnItemClickListener {

            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val data03 = adapter.data[position] as Data03
                mContext.start<MainActivity> { putExtra(MainActivity.GoodsId, "${data03.mGoodsId}") }
            }

        })
    }

    override fun initData() {
        mViewModel.getList(BaseRequestBody(MyHeader = header(pageNum = nowPosition)))
        mViewModel.getBannerList()

    }

    override fun startObserve() {

        mViewModel.mBannerList.observe(this, {
            if (it.mList.isNotEmpty()) {
                hasBanner = true
                val BannerParenter = layoutInflater.inflate(R.layout.common_banner, rv_coupon_shop, false)
                val imageUrl = mutableListOf<String>()
                mAdapter.removeAllHeaderView()
                val mBanner = BannerParenter.findViewById<Banner>(R.id.common_banner).apply {
                    setBannerStyle(BannerConfig.NUM_INDICATOR)
                    setBannerAnimation(Transformer.BackgroundToForeground)
                    isAutoPlay(true)
                    setImageLoader(GlideImageLoader())
                    setDelayTime(2000)
                }

                mBanner.apply {


                    for (banner in it.mList) {
                        imageUrl.add(banner.mBannerImg)
                    }
                    setImages(imageUrl)
                    setOnBannerListener(object : OnBannerListener {
                        override fun OnBannerClick(position: Int) {
                            if ("${it.mList[position].mBannerTypeId}".isNotBlank())
                                mContext.start<MainActivity> {
                                    putExtra(
                                        MainActivity.GoodsId,
                                        "${it.mList[position].mBannerTypeId}"
                                    )
                                }
                        }

                    })
                    start()
                }

                mAdapter.addHeaderView(BannerParenter)
            } else {
                hasBanner = false
            }
        })

        mViewModel.mCouponData.observe(this, {
            mAdapter.addData(it.mData)
            if (it.mTotalPages == nowPosition) {
                mAdapter.loadMoreModule.loadMoreEnd()
            } else {
                mAdapter.loadMoreModule.loadMoreComplete()
                nowPosition++
            }

        })
    }


    class couponAdapter(var fragment: AppCompatActivity) :
        com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter<Data03,
                BaseViewHolder>(
            R.layout
                .item_coupon_shop
        ), LoadMoreModule {
        override fun convert(holder: BaseViewHolder, item: Data03) {
            Glide.with(fragment).asDrawable().apply(
                RequestOptions().placeholder(
                    ContextCompat.getDrawable
                        (
                        fragment, R
                        .drawable.icon_sku_unload
                    )
                ).error(
                    ContextCompat.getDrawable
                        (
                        fragment, R
                        .drawable.icon_sku_unload
                    )
                )
            )
                .load(item.mPicture)
                .into(holder.getView(R.id.iv_coupon_shop))
            holder.setText(R.id.tv_coupon_shop, item.mGoodsName).setText(
                R.id.tv_coupon_shop_price, "￥${
                    item.mPrice
                        .toBigDecimal().setScale(2, RoundingMode.HALF_DOWN).toPlainString()
                }"
            )
        }

    }

}