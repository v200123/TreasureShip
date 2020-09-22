package com.jzz.treasureship.ui.msg

import android.app.AppComponentFactory
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.usersetting.UserSettingFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_msg_center.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class MsgFragment : BaseVMFragment<MsgViewModel>() {
    override fun getLayoutResId() = R.layout.fragment_msg_center

    override fun initVM(): MsgViewModel = getViewModel()

    companion object {
        fun newInstance(): MsgFragment {
            return MsgFragment()
        }
    }

    override fun initView() {
        rlback.setOnClickListener {
            mActivity?.onBackPressed()
        }

        activity!!.nav_view.visibility = View.GONE
        tv_title.text = "消息中心"

        val titles = arrayOf("通知消息", "评论消息")
        val fragments: ArrayList<MsgVpFragment> = ArrayList(titles.size)

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
        for ((index) in titles.withIndex()) {
            fragments.add(MsgVpFragment(index))
        }
        msg_tab.run {
            setSelectedTabIndicatorColor(this.resources.getColor(R.color.blue_light))
            setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val view = tab!!.customView
                    if (null == view) {
                        tab.setCustomView(R.layout.custom_tab_layout_text)
                    }
                    val textView: TextView = tab.customView!!.findViewById(R.id.tab_item_textview)
                    textView.text = titles[tab.position]
                    textView.setTextColor(context.resources.getColor(R.color.garyf3))
                    textView.typeface = Typeface.DEFAULT
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val view = tab!!.customView
                    if (null == view) {
                        tab.setCustomView(R.layout.custom_tab_layout_text)
                    }
                    val textView: TextView = tab.customView!!.findViewById(R.id.tab_item_textview)
                    textView.text = titles[tab.position]
                    textView.setTextColor(context.resources.getColor(R.color.blue_light))
                    textView.typeface = Typeface.DEFAULT
                }

            })
        }

        val mAdapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                if (!fragments[position].isAdded) {
                    return fragments[position]
                }
                return Fragment()
            }

            override fun getItemCount(): Int {
                return fragments.size
            }
        }

        msgcenter_viewpager.adapter = mAdapter

        TabLayoutMediator(msg_tab, msgcenter_viewpager, true,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                if (position >= titles.size) {
                    return@TabConfigurationStrategy
                }
                tab.text = titles[position]
            }).attach()
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun initListener() {
    }

}
