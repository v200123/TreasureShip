package com.jzz.treasureship.ui.usersetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jzz.treasureship.AppInterface.IParentHidden
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.activity.DialogStatusViewModel
import com.jzz.treasureship.ui.activity.MainActivity
import com.jzz.treasureship.ui.auth.AuthenticationActivity
import com.jzz.treasureship.ui.auth.viewmodel.UserViewModel
import com.jzz.treasureship.ui.coupon.CouponMainFragment
import com.jzz.treasureship.ui.invite.InviteFragment
import com.jzz.treasureship.ui.msg.MsgFragment
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.ranking.RankingFragment
import com.jzz.treasureship.ui.setting.SettingFragment
import com.jzz.treasureship.ui.shopcar.ShopCarFragment
import com.jzz.treasureship.ui.user.UserInfoFragment
import com.jzz.treasureship.ui.wallet.WalletFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.out
import com.lc.mybaselibrary.ext.getResColor
import com.lc.mybaselibrary.ext.getResDrawable
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.fragment_user_setting.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class UserSettingFragment : BaseVMFragment<UserViewModel>() , IParentHidden {
    private val mPopStatus by activityViewModels<DialogStatusViewModel> ()
    override var  mStatusColor = R.color.blue_normal
    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)

    var isShow by PreferenceUtils(PreferenceUtils.no_auth_show,false)
    companion object {
        fun newInstance(): UserSettingFragment {
            return UserSettingFragment()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if(isLogin)
            mViewModel.getUserInfo()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_user_setting

    override fun initVM(): UserViewModel = getViewModel()

    override fun onStart() {
        super.onStart()
        "我userSetting当前在onStart".out(true)
    }

    override fun onStop() {
        super.onStop()
        "我userSetting当前在onStop".out(true)

    }
    override fun onPause() {
        super.onPause()
        "我userSetting当前在onPause".out(true)
    }

    override fun onResume() {
        super.onResume()
        "我userSetting当前在onResume".out(true)
        if(isLogin)
        mViewModel.getUserInfo()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        "我userSetting当前在onViewStateRestored".out(true)
    }

    override fun initView() {
        fl_setting_card.setOnClickListener {

            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, CouponMainFragment(), "CouponMainFragment")
                .commit()

        }
        StateAppBar.setStatusBarLightMode(this.activity, mContext.getResColor(R.color.blue_normal))

        val USER_JSON by PreferenceUtils(PreferenceUtils.USER_GSON, "")

        if (USER_JSON.isNotBlank()) {
            val userJson = GsonUtils.fromJson(USER_JSON, User::class.java)

            userJson?.let {
                if (userJson.nickName.isNullOrBlank()) {
                    tv_name.text = userJson.username
                } else {
                    tv_name.text = userJson.nickName
                }
                tv_hospital.text = userJson.hospitalName

                if (userJson.avatar.isNullOrBlank()) {
                    Glide.with(this).load(this.resources.getDrawable(R.drawable.icon_default_avatar))
                        .into(iv_user_head)
                } else {
                    Glide.with(this).load(userJson.avatar).apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(iv_user_head)
                }
                showUserStatus(it)
            }
        }


        lin_auth.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, UserInfoFragment.newInstance(), "UserInfoFragment")
                .commit()
        }

        lin_shopcar.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, ShopCarFragment.newInstance(), ShopCarFragment.javaClass.name)
                .commit()
        }
        lin_mine_order.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, OrdersFragment.newInstance(), OrdersFragment.javaClass.name)
                .commit()
        }
        lin_income.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, WalletFragment.newInstance(), WalletFragment.javaClass.name)
                .commit()
        }
        lin_ranking.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, RankingFragment.newInstance(), RankingFragment.javaClass.name)
                .commit()
        }
        lin_invite.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, InviteFragment.newInstance(), InviteFragment.javaClass.name)
                .commit()
        }
        lin_setting.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, SettingFragment.newInstance(), SettingFragment.javaClass.name)
                .commit()
        }

        iv_msg.setOnClickListener {
            (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide((mContext as MainActivity).mMainHomeFragemnt)
                .add(R.id.frame_content, MsgFragment.newInstance(), MsgFragment.javaClass.name)
                .commit()

        }
    }

    private fun showUserStatus(it: User): Unit? {
        cf_titles.removeAllViews()
        return when (it.auditStatus) {
            -1 -> {
                btn_usersetting_auth.setOnClickListener { mContext.start<AuthenticationActivity> {  } }
                iv_msg.visibility = View.INVISIBLE
                fl_showtips.visibility = View.VISIBLE
                val tv = LayoutInflater.from(context).inflate(
                    R.layout.layout_user_title, cf_titles, false
                ) as TextView
                tv.text = "未认证"
                cf_titles.addView(tv)
//                App.dialogHelp.showType()

            }
            0 -> {
                iv_msg.visibility = View.VISIBLE
                fl_showtips.visibility = View.GONE
                val tv = LayoutInflater.from(context).inflate(
                    R.layout.layout_user_title, cf_titles, false
                ) as TextView
                tv.text = "审核中"
                cf_titles.addView(tv)
            }
            1 -> {
                iv_msg.visibility = View.VISIBLE
                fl_showtips.visibility = View.GONE
                it.tags?.let {
                    val mTags = it.split(",")
                    if (mTags.size > 3) {
                        for (i in 0..2) {
                            val tv = LayoutInflater.from(context).inflate(
                                R.layout.layout_user_title, cf_titles, false
                            ) as TextView
                            tv.text = mTags[i]
                            tv.setTextColor(context!!.resources.getColor(R.color.white))
                            tv.background = mContext.getResDrawable(R.drawable.mine_auditstatus_tag_shape)
                            cf_titles.addView(tv)
                        }
                    } else {
                        for (tag in mTags) {
                            val tv = LayoutInflater.from(context).inflate(
                                R.layout.layout_user_title, cf_titles, false
                            ) as TextView
                            tv.text = tag
                            tv.setTextColor(context!!.resources.getColor(R.color.white))
                            tv.background = mContext.getResDrawable(R.drawable.mine_auditstatus_tag_shape)
                            cf_titles.addView(tv)
                        }
                    }
                }

            }
            2 -> {
                iv_msg.visibility = View.INVISIBLE
                fl_showtips.visibility = View.VISIBLE
                btn_usersetting_auth.text = "重新认证"
                btn_usersetting_auth.setOnClickListener { mContext.start<AuthenticationActivity> {  } }
                val tv = LayoutInflater.from(context).inflate(
                    R.layout.layout_user_title, cf_titles, false
                ) as TextView
                tv.text = "审核不通过"
                cf_titles.addView(tv)
            }
            else -> {
                val tv = LayoutInflater.from(context).inflate(
                    R.layout.layout_user_title, cf_titles, false
                ) as TextView
                tv.text = "未认证"
                cf_titles.addView(tv)
            }
        }
    }

    override fun initData() {
        if(isLogin)
        mViewModel.getUserInfo()
    }

    override fun startObserve() {
        mViewModel.apply {
            userState.observe(this@UserSettingFragment, Observer {
                it.showError?.let {
                    ToastUtils.showShort(if (it.isBlank()) "网络异常" else it)
                }
                it.showSuccess?.let {
                    var userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                    userJson = GsonUtils.toJson(it)
                    if (it.nickName.isNullOrBlank()) {
                        tv_name.text = it.username
                    } else {
                        tv_name.text = it.nickName
                    }
                    tv_hospital.text = it.hospitalName

                    if (it.avatar.isNullOrBlank()) {
                        Glide.with(this@UserSettingFragment)
                            .load(mContext.getResDrawable(R.drawable.icon_default_avatar))
                            .into(iv_user_head)
                    } else {
                        Glide.with(this@UserSettingFragment).load(it.avatar)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(iv_user_head)
                    }
                    showUserStatus(it)
                }
            })
        }
    }
    fun refreshUser(){
        mViewModel.getUserInfo()
    }
    override fun initListener() {
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun parentHidden(isHidden: Boolean, Type: Int) {

        "我进入parentHidden02".out()

        if(!isHidden)
        {
            setStatusColor()
        }
    }
}
