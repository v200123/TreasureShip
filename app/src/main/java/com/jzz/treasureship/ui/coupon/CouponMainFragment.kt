package com.jzz.treasureship.ui.coupon

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.jzz.treasureship.R
import com.lc.mybaselibrary.ext.getResColor
import kotlinx.android.synthetic.main.fragment_main_coupon.*
import kotlinx.android.synthetic.main.include_title.*

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
        StateAppBar.setStatusBarLightMode(this.activity, mContext.getResColor(R.color.white))
        tv_title.text = "我的卡券"
        rlback.setOnClickListener { (mContext as FragmentActivity).onBackPressed() }
        tab_main_coupon.setTabTitles(arrayOf("未使用","已使用","已过期"))
        vp_coupon.offscreenPageLimit = 2
        tab_main_coupon.setViewPager(vp_coupon,object : FragmentStatePagerAdapter(childFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int  = 3
            override fun getItem(position: Int): Fragment = CouponUseFragment.newInstance(position+1)

        })
    }

}
