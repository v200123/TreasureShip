package com.jzz.treasureship.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.Coupon
import com.lc.mybaselibrary.ShapeLinearLayout
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.dialog_coupon.view.*

/**
 *@date: 2020/9/19
 *@describe:
 *@Auth: 29579
 **/
class DialogChoiceCoupon(context: Context, var mCoupon: MutableList<Coupon>) : BottomPopupView(context) {

    override fun getImplLayoutId() = R.layout.dialog_coupon

    override fun onCreate() {
        super.onCreate()
        val checkList = arrayListOf<CheckBox>()


        btn_coupon_confirm.setOnClickListener {
            dismiss()
        }
        findViewById<ShapeLinearLayout>(R.id.ll_addView_01).apply {
            mCoupon.forEachIndexed { i: Int, coupon: Coupon ->
                val inflate = LayoutInflater.from(context).inflate(R.layout.item_coupon, this, false)
                inflate.findViewById<TextView>(R.id.textView18).text =if( coupon.mCouponName == "不使用优惠券")
                    "不使用优惠券" else "${coupon.mCouponValue}元优惠券"
                inflate.findViewById<CheckBox>(R.id.cb_coupon).apply {
                    isChecked = coupon.isSelector
                    checkList.add(this)
                }.setOnClickListener {
                    for (mCou in mCoupon) {
                        mCou.isSelector = false
                    }
                    coupon.isSelector = true
                    checkList.forEachIndexed { position: Int, checkBox: CheckBox ->
                        checkBox.isChecked = position == i

                    }
//                    it.isSelected = (it as CheckBox).isChecked
                }
                this.addView(inflate, i)
            }

        }

    }
}