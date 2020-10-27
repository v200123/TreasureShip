package com.jzz.treasureship.ui.treasurebox

import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.jzz.treasureship.AppInterface.IParentHidden
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.TabBean
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.ui.shopcar.ShopCarFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.out
import com.jzz.treasureship.view.CustomInputDialog
import com.lc.mybaselibrary.ext.getResColor
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.fragment_treasure_box.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import q.rorbin.badgeview.QBadgeView


class TreasureBoxFragment : BaseVMFragment<HomeViewModel>(),IParentHidden{

    private var curFragment by PreferenceUtils(PreferenceUtils.CUR_FRAGMENT, "")
    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)

    private val mShopCarFragment by lazy { ShopCarFragment.newInstance() }

    private val tabs: ArrayList<TabBean> = ArrayList()
    private val fragments: ArrayList<TsbVpFragment> = ArrayList()

    companion object {
        fun newInstance(): TreasureBoxFragment {
            return TreasureBoxFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_treasure_box

    override fun initVM(): HomeViewModel = getViewModel()

    override fun initView() {

        curFragment = "TreasureBoxFragment"
        tv_addCollect.setOnClickListener {
            XPopup.Builder(it.context).asCustom(CustomInputDialog(it.context, mViewModel)).show()
        }

        tv_shopCar_btn.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, mShopCarFragment, mShopCarFragment.javaClass.name)
                .commit()
        }
    }

    override fun initData() {


    }


    override fun startObserve() {
        mViewModel.apply {
            collectUiState.observe(this@TreasureBoxFragment, Observer {
                it.showSuccess?.let { list ->
                    tabs.clear()
                    fragments.clear()
                    for (item in list) {
                        val tabBean = TabBean(item.id, item.title, item.type, item.count, item.deleteShow)
                        tabs.add(tabBean)
                        fragments.add(TsbVpFragment.newInstance(tabBean))
                    }


                    tabLayout.run {
                        setSelectedTabIndicatorColor(this.resources.getColor(R.color.blue_light))
                    }
                    val mAdapter = object : FragmentStateAdapter(this@TreasureBoxFragment) {
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
                    viewpager2.offscreenPageLimit = 1
                    viewpager2.isSaveEnabled = false
                    viewpager2.adapter = mAdapter

                    TabLayoutMediator(tabLayout, viewpager2, true,
                        TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                            //这里需要根据position修改tab的样式和文字等
                            tab.text = tabs[position].title
                        }).attach()
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            cartNumUiState.observe(this@TreasureBoxFragment, Observer {
                it.showSuccess?.let { cartNum ->
                    QBadgeView(context)
                        .bindTarget(tv_shopCar_btn)
                        .setBadgeNumber(cartNum)
                        .setBadgeTextSize(10f, true)
                        .setBadgePadding(1.0f, true)
                        .setBadgeGravity(Gravity.TOP or Gravity.CENTER)
                        .setBadgeBackgroundColor(mContext.getResColor(R.color.blue_light))
                }

                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })

            operateUiState.observe(this@TreasureBoxFragment, Observer {
                it.showSuccess?.let {
                    mViewModel.getCollectCategory()

                }
                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })
        }
    }


    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isLogin) {
            mViewModel.getCount()
            mViewModel.getCollectCategory()
        }
//        activity!!.nav_view.visibility = View.VISIBLE
        GSYVideoManager.releaseAllVideos()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    override fun initListener() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        var tsbUpdate by PreferenceUtils(PreferenceUtils.TSB_UPDATE, false)
        if (!hidden) {
//            mActivity!!.nav_view!!.visibility = View.VISIBLE
//            mActivity!!.nav_view!!.menu[1].isChecked = true
            mViewModel.getCount()
            StateAppBar.setStatusBarLightMode(this.activity, mContext.getResColor(R.color.white))
            if (tsbUpdate) {
                mViewModel.getCollectCategory()
                tsbUpdate = false
            }
            curFragment = "TreasureBoxFragment"
        } else {
            GSYVideoManager.onPause()
        }
    }
    override fun onBackPressed(): Boolean {
        return false
    }

    override fun parentHidden(isHidden: Boolean, Type: Int) {
        "我进入parentHidden01".out()
        setStatusColor()
    }
}