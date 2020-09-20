package com.jzz.treasureship.ui.usersetting

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.auth.viewmodel.UserViewModel
import com.jzz.treasureship.ui.coupon.CouponActivity
import com.jzz.treasureship.ui.invite.InviteFragment
import com.jzz.treasureship.ui.msg.MsgFragment
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.ranking.RankingFragment
import com.jzz.treasureship.ui.setting.SettingFragment
import com.jzz.treasureship.ui.shopcar.ShopCarFragment
import com.jzz.treasureship.ui.user.AuthenticationFragment
import com.jzz.treasureship.ui.user.UserInfoFragment
import com.jzz.treasureship.ui.wallet.WalletFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_setting.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class UserSettingFragment : BaseVMFragment<UserViewModel>() {
    var isShow by PreferenceUtils(PreferenceUtils.no_auth_show,false)
    companion object {
        fun newInstance(): UserSettingFragment {
            return UserSettingFragment()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            activity!!.nav_view.visibility = View.VISIBLE
            activity!!.nav_view.menu[3].isChecked = true
            StateAppBar.setStatusBarLightMode(this.activity,ContextCompat.getColor(mContext,R.color.blue_normal))
            mViewModel.getUserInfo()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_user_setting

    override fun initVM(): UserViewModel = getViewModel()

    override fun initView() {
        fl_setting_card.setOnClickListener {
            mContext.start<CouponActivity> {  }
        }


        activity!!.nav_view.visibility = View.VISIBLE
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.blue_normal))

        val USER_JSON by PreferenceUtils(PreferenceUtils.USER_GSON, "")

        if (USER_JSON.isNotBlank()) {
            val userJson = GsonUtils.fromJson(USER_JSON, User::class.java)

            userJson?.let {
                if( it.auditStatus == 1)
                {
                    fl_showtips.visibility = View.GONE
                }else{
                    fl_showtips.visibility = View.VISIBLE
                    if(it.auditStatus == 2)
                    {
                        btn_usersetting_auth.text = "重新认证"
                    }
                    btn_usersetting_auth.setOnClickListener {
                        mContext.start<AuthenticationFragment> {  }
                    }
                }

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
                when (it.auditStatus) {
                    -1 -> {
                        val tv = LayoutInflater.from(context).inflate(
                            R.layout.layout_user_title, cf_titles, false
                        ) as TextView
                        tv.text = "未认证"
                        cf_titles.addView(tv)
                      App.dialogHelp.showType()

                    }
                    0 -> {
                        val tv = LayoutInflater.from(context).inflate(
                            R.layout.layout_user_title, cf_titles, false
                        ) as TextView
                        tv.text = "审核中"
                        cf_titles.addView(tv)
                    }
                    1 -> {
                        it.tags?.let {
                            val mTags = it.split(",")
                            if (mTags.size > 3) {
                                for (i in 0..2) {
                                    val tv = LayoutInflater.from(context).inflate(
                                        R.layout.layout_user_title, cf_titles, false
                                    ) as TextView
                                    tv.text = mTags[i]
                                    tv.setTextColor(context!!.resources.getColor(R.color.white))
                                    tv.background = context!!.resources.getDrawable(R.drawable.mine_auditstatus_tag_shape)
                                    cf_titles.addView(tv)
                                }
                            } else {
                                for (tag in mTags) {
                                    val tv = LayoutInflater.from(context).inflate(
                                        R.layout.layout_user_title, cf_titles, false
                                    ) as TextView
                                    tv.text = tag
                                    tv.setTextColor(context!!.resources.getColor(R.color.white))
                                    tv.background = context!!.resources.getDrawable(R.drawable.mine_auditstatus_tag_shape)
                                    cf_titles.addView(tv)
                                }
                            }
                        }

                    }
                    2 -> {
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
        }


        lin_auth.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, UserInfoFragment.newInstance(), UserInfoFragment.javaClass.name)
                .commit()
        }

        lin_shopcar.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, ShopCarFragment.newInstance(), ShopCarFragment.javaClass.name)
                .commit()
        }
        lin_mine_order.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, OrdersFragment.newInstance(), OrdersFragment.javaClass.name)
                .commit()
        }
        lin_income.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, WalletFragment.newInstance(), WalletFragment.javaClass.name)
                .commit()
        }
        lin_ranking.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, RankingFragment.newInstance(), RankingFragment.javaClass.name)
                .commit()
        }
        lin_invite.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, InviteFragment.newInstance(), InviteFragment.javaClass.name)
                .commit()
        }
        lin_setting.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, SettingFragment.newInstance(), SettingFragment.javaClass.name)
                .commit()
        }

        iv_msg.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(UserSettingFragment.javaClass.name)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, MsgFragment.newInstance(), MsgFragment.javaClass.name)
                .commit()

        }
    }

    override fun initData() {
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
                            .load(context!!.resources.getDrawable(R.drawable.icon_default_avatar))
                            .into(iv_user_head)
                    } else {
                        Glide.with(this@UserSettingFragment).load(it.avatar)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(iv_user_head)
                    }
                }
            })
        }
    }

    override fun initListener() {
    }
}
