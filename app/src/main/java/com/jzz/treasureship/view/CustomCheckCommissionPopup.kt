package com.jzz.treasureship.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
R
adapter.CommissionGoodsAdapter
model.bean.Data
model.bean.DataXX
model.bean.GoodsSku
model.bean.GoodsSkuX
utils.MoneyUtil
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.layout_check_commision.view.*
import kotlinx.android.synthetic.main.layout_check_docter_advice_popup.view.*
import kotlinx.android.synthetic.main.layout_check_docter_advice_popup.view.iv_close
import java.math.BigDecimal
import kotlin.math.roundToInt

class CustomCheckCommissionPopup(context: Context, data: Data) : BottomPopupView(context) {

    private val mData = data
    private val mAdapter by lazy { CommissionGoodsAdapter() }

    override fun getImplLayoutId() = R.layout.layout_check_commision

    @SuppressLint("SetTextI18n")
    override fun initPopupContent() {
        super.initPopupContent()

        iv_close.setOnClickListener {
            dismiss()
        }

        tv_commisionStatus.text = mData.commissionStatus

        mData.goodsSkuList?.let {
            var allCommission = "0.000"
            for (ele in it) {
                allCommission = MoneyUtil.moneyAdd(
                    BigDecimal(allCommission).toString(),
                    BigDecimal(ele!!.commissionAccount!!).toString()
                )
            }
            tv_commisionPrice.text = "¥${BigDecimal(allCommission).setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()}"

            rcv_commision.run {
                layoutManager = LinearLayoutManager(context).also {
                    it.orientation = LinearLayoutManager.VERTICAL
                }

                adapter = mAdapter
            }

            mAdapter.setList(it)
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .75f).roundToInt()
    }
}