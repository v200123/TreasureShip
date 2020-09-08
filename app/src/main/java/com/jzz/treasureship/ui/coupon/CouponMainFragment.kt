package com.jzz.treasureship.ui.coupon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.jzz.treasureship.R
import kotlinx.android.synthetic.main.fragment_main_coupon.*

/**
 *@date: 2020/9/8
 *@describe:
 *@Auth: 29579
 **/
class CouponMainFragment : Fragment(R.layout.fragment_main_coupon) {
    private lateinit var mContext:Context
    private val mViewPager by lazy { ViewPager(mContext) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tab_main_coupon.setTabTitles(arrayOf("未使用","已使用","已过期"))
    }

}
