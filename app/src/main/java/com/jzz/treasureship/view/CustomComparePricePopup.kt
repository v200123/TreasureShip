package com.jzz.treasureship.view

import android.content.Context
import android.view.View
import androidx.core.view.marginTop
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CollectAdapter
import com.jzz.treasureship.adapter.ComparePricesAdapter
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.dialog_home_like.view.*
import kotlinx.android.synthetic.main.layout_compare_prices.view.*
import kotlin.math.roundToInt

class CustomComparePricePopup(context: Context, adapter: ComparePricesAdapter) : BottomPopupView(context) {
    private val mAdapter: ComparePricesAdapter = adapter

    override fun getImplLayoutId() = R.layout.layout_compare_prices

    override fun initPopupContent() {
        super.initPopupContent()

        initRecycleView()
    }

    private fun initRecycleView() {
        comparePriceRecycle.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
        }
        mAdapter.run {

            this.removeAllFooterView()

            val dismissFooter: View = View.inflate(context, R.layout.dialog_dissmiss_footer, null)
            dismissFooter.setOnClickListener {
                this@CustomComparePricePopup.dismiss()
            }
            dismissFooter.setPadding(0, 15, 0, 15)
            this.addFooterView(dismissFooter)
        }
        comparePriceRecycle.adapter = mAdapter
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .75f).roundToInt()
    }
}