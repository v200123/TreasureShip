package com.jzz.treasureship.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.jzz.treasureship.AppInterface.IParentHidden
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.home.HomeFragment
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.treasurebox.TreasureBoxFragment
import com.jzz.treasureship.ui.usersetting.UserSettingFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.normalOut
import com.jzz.treasureship.view.TabSegment
import com.lc.mybaselibrary.ext.getResDrawable
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.fragment_main_home.*


class MainHomeFragment : Fragment(R.layout.fragment_main_home) {

    private lateinit var mContext: Context


    val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_container.addTab(
            TabSegment.Tab(
                mContext.getResDrawable(R.drawable.home_nav_home_icon_normal),
                mContext.getResDrawable(R.drawable.home_nav_home_icon_selected),
                "首页", false, true
            )
        )
        tab_container.addTab(
            TabSegment.Tab(
                mContext.getResDrawable(R.drawable.icon_treasure_box_normal),
                mContext.getResDrawable(R.drawable.icon_treasure_box_selected),
                "宝舰箱", false, true
            )
        )
        tab_container.addTab(
            TabSegment.Tab(
                mContext.getResDrawable(R.drawable.icon_mine_normal),
                mContext.getResDrawable(R.drawable.icon_mine_selected),
                "我的", false, true
            )
        )
        vp_container_home.offscreenPageLimit = 2
        tab_container.setupWithViewPager(vp_container_home, false)

        vp_container_home.adapter = object : FragmentPagerAdapter(
            childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount(): Int = 3

            override fun getItem(position: Int): Fragment = when (position) {
                0 -> HomeFragment.newInstance()
                1 -> TreasureBoxFragment.newInstance()
                2 -> UserSettingFragment.newInstance()
                else -> Fragment()
            }
        }


        tab_container.setOnTabClickListener(object : TabSegment.OnTabClickListener{
            override fun onTabClick(index: Int) {
                if (index != 0) {
                    if (!isLogin) {
                        mContext.start<LoginActivity> { }
                        tab_container.selectTab(0)
                    }
                }
            }

        })



    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            childFragmentManager.fragments.forEach {
                if (it is IParentHidden && it.isResumed) {
                    it.parentHidden(hidden, 0)
                }
            }
        }
        "我MainFragment进入到onHiddenChanged了".normalOut(true)
    }

}