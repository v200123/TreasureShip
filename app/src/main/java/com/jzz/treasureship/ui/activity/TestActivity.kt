package com.jzz.treasureship.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.home.HomeFragment
import com.jzz.treasureship.ui.treasurebox.TreasureBoxFragment
import com.jzz.treasureship.ui.usersetting.UserSettingFragment
import com.jzz.treasureship.view.TabSegment
import com.lc.mybaselibrary.ext.getResDrawable
import kotlinx.android.synthetic.main.fragment_main_home.*


class TestActivity : AppCompatActivity(R.layout.fragment_main_home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tab_container.setupWithViewPager(vp_container_home,false)
        tab_container.addTab(
            TabSegment.Tab(
                getResDrawable(R.drawable.home_nav_home_icon_normal),
                getResDrawable(R.drawable.home_nav_home_icon_selected),
                "首页", false,true
            )
        )
        tab_container.addTab(
            TabSegment.Tab(
                getResDrawable(R.drawable.icon_treasure_box_normal),
                getResDrawable(R.drawable.icon_treasure_box_selected),
                "保健箱", false,true
            )
        )
        tab_container.addTab(
            TabSegment.Tab(
                getResDrawable(R.drawable.icon_mine_normal),
                getResDrawable(R.drawable.icon_mine_selected),
                "我的", false,true
            )
        )


        vp_container_home.adapter = object :FragmentPagerAdapter(
            supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ){
            override fun getCount(): Int  =3

            override fun getItem(position: Int): Fragment =when(position){
                0 -> HomeFragment.newInstance()
                1 -> TreasureBoxFragment.newInstance()
                2 -> UserSettingFragment.newInstance()
                else -> Fragment()
            }
        }
    }
}