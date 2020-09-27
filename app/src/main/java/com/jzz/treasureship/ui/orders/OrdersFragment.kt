package com.jzz.treasureship.ui.orders

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.addressbook.AddressBookFragment
import com.jzz.treasureship.ui.home.HomeFragment
import com.jzz.treasureship.ui.treasurebox.TreasureBoxFragment
import com.jzz.treasureship.ui.usersetting.UserSettingFragment
import com.jzz.treasureship.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mine_orders.*
import kotlinx.android.synthetic.main.fragment_treasure_box.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class OrdersFragment : BaseVMFragment<OrdersViewModel>() {

    private var mComeFrom: String? = null
    private val mRootFragment by PreferenceUtils(PreferenceUtils.CUR_FRAGMENT, "")
    //private val titles = arrayOf("全部", "待付款", "待发货", "已发货", "已收货", "已完成", "退款中", "已退款", "已关闭")

    companion object {
        fun newInstance(from: String? = null): OrdersFragment {
            val f = OrdersFragment()
            val bundle = Bundle()
            bundle.putString("from", from)
            f.arguments = bundle
            return f
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            activity!!.nav_view.visibility = View.GONE
            StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        }
    }

    override fun getLayoutResId() = R.layout.fragment_mine_orders

    override fun initVM(): OrdersViewModel = getViewModel()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        tv_title.text = "我的订单"

        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))

        val titles = arrayOf("全部", "待付款", "待发货", "已发货", "已完成")
        val fragments: ArrayList<OrdersVpFragment> = ArrayList(titles.size)


        arguments?.let {
            mComeFrom = it.getString("from")
        }

        rlback.setOnClickListener {
            if (mRootFragment.isBlank()) {
                activity!!.supportFragmentManager.popBackStack()
            } else {
                Log.d("asd", mRootFragment)
                when (mRootFragment) {
                    "HomeFragment" -> {
//                        activity!!.supportFragmentManager.popBackStackImmediate(
//                            HomeFragment.javaClass.name,
//                            FragmentManager.POP_BACK_STACK_INCLUSIVE
//                        )
                        activity!!.supportFragmentManager.beginTransaction().hide(this)
                            .add(R.id.frame_content, HomeFragment.newInstance(), HomeFragment.javaClass.name)
                            .commit()
                        Log.d("asd", "hoem")
                    }
                    "TreasureBoxFragment" -> {
//                        activity!!.supportFragmentManager.popBackStackImmediate(
//                            TreasureBoxFragment.javaClass.name,
//                            FragmentManager.POP_BACK_STACK_INCLUSIVE
//                        )
                        activity!!.supportFragmentManager.beginTransaction().hide(this)
                            .add(
                                R.id.frame_content,
                                TreasureBoxFragment.newInstance(),
                                TreasureBoxFragment.javaClass.name
                            )
                            .commit()
                        Log.d("asd", "hoem2")
                    }
                    else -> {
                        activity!!.supportFragmentManager.popBackStack()
                        Log.d("asd", "hoem3")
                    }
                }
            }

//            when (mComeFrom) {
//                "paypal",
//                "OthersPay",
//                "AddAdvice" -> {
//                    StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))
//                    switchFragment(mHomeFragment)
//                }
//                else -> {
//                    activity!!.supportFragmentManager.popBackStack()
//                }
//            }

        }

        for ((index) in titles.withIndex()) {
            fragments.add(OrdersVpFragment(index))
        }
        ordersTablayout.run {
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
                    textView.setTextColor(context.resources.getColor(R.color.gray999))
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
                    textView.typeface = Typeface.DEFAULT_BOLD
                }

            })
        }

        val mAdapter = object : FragmentStateAdapter(this@OrdersFragment) {
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
        ordersViewpager.offscreenPageLimit = fragments.size
        ordersViewpager.isSaveEnabled = false
        ordersViewpager.adapter = mAdapter

        TabLayoutMediator(ordersTablayout, ordersViewpager, true,
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

    override fun onBackPressed(): Boolean {
        if (mRootFragment.isBlank()) {
            activity!!.supportFragmentManager.popBackStack()
        } else {
            when (mRootFragment) {
                "HomeFragment" -> {
                    Log.d("orderback","HomeFragment")
                    activity!!.supportFragmentManager.beginTransaction().hide(this)
                        .add(
                            R.id.frame_content,
                            HomeFragment.newInstance(),
                            HomeFragment.javaClass.name
                        )
                        .commit()
                }
                "TreasureBoxFragment" -> {
                    Log.d("orderback","TreasureBoxFragment")
                    activity!!.supportFragmentManager.beginTransaction().hide(this)
                        .add(
                            R.id.frame_content,
                            TreasureBoxFragment.newInstance(),
                            TreasureBoxFragment.javaClass.name
                        )
                        .commit()
                }
                else -> {
                    Log.d("orderback","popBackStack")
                    activity!!.supportFragmentManager.popBackStack()
                }
            }
        }
        return true
    }


}