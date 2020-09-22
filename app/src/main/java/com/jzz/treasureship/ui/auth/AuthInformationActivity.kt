package com.jzz.treasureship.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import cn.ycbjie.ycstatusbarlib.utils.StatusBarUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.auth.adapter.AuthInfoAdapter
import com.jzz.treasureship.ui.auth.viewmodel.AuthInforViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import com.lc.mybaselibrary.start
import kotlinx.android.synthetic.main.activity_auth_information.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthInformationActivity : BaseVMActivity<AuthInforViewModel>(false) {
    private val model by viewModels<CommonDataViewModel>()


    companion object {
        const val occuId = "com.jzz.occuId"
    }

    override fun onBackPressed() {
        if(vp_authinfor.currentItem == 0)
        super.onBackPressed()
        else{
            vp_authinfor.setCurrentItem(--vp_authinfor.currentItem)
        }

    }

    override fun initView() {
        StateAppBar.setStatusBarLightMode(this,ContextCompat.getColor(mContext,R.color.white))

        setStatueColor()
        tv_title.text = "认证信息"
        rlback.setOnClickListener { if(vp_authinfor.currentItem == 0)
            finish()
        else{
            vp_authinfor.setCurrentItem(--vp_authinfor.currentItem)
        } }
        model.mConfirmBody.mOccupationId = intent.getIntExtra(occuId, 0)
        vp_authinfor.adapter = AuthInfoAdapter(supportFragmentManager)
        vp_authinfor.isEnabled = false
        btn_baseinfo_next.setOnClickListener {
            vp_authinfor.setCurrentItem(++vp_authinfor.currentItem, true)
        }

        vp_authinfor.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            @SuppressLint("MissingSuperCall")
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }



            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    dash_view.level = 0
                    btn_baseinfo_next.text = "下一步"
                    btn_baseinfo_next.setOnClickListener {
                        vp_authinfor.setCurrentItem(++vp_authinfor.currentItem, true)
                    }
                    tv_authinfor_01.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_now)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
                    tv_authinfor_02.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_unslect)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
                    tv_authinfor_03.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_unslect)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
                }
                if (position == 1) {
                    dash_view.level = 1
                    btn_baseinfo_next.apply {
                        text = "提交审核"
                        setOnClickListener {
                            mViewModel.confirm(model.mConfirmBody)
                        }
                    }

                    tv_authinfor_01.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.icon_withdraw_unselected)!!
                            .apply {
                                setTint(ContextCompat.getColor(mContext, R.color.blue_normal))
                                setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                            },
                        null,
                        null
                    )
                    tv_authinfor_02.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_now)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
                    tv_authinfor_03.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_unslect)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
//                    ico_auth_now
                }
                if (position == 2) {
                    dash_view.level = 2
                    tv_authinfor_01.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.icon_withdraw_unselected)!!
                            .apply {
                                setTint(ContextCompat.getColor(mContext, R.color.blue_normal))
                                setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                            },
                        null,
                        null
                    )
                    tv_authinfor_02.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.icon_withdraw_unselected)!!
                            .apply {
                                setTint(ContextCompat.getColor(mContext, R.color.blue_normal))
                                setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                            },
                        null,
                        null
                    )
                    tv_authinfor_03.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_now)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

    }

    override fun getLayoutResId(): Int = R.layout.activity_auth_information

    override fun initData() {
    }

    override fun startObserve() {

        mViewModel.qualLiveData.observe(this, {
                            lifecycleScope.launch {
                                delay(200)
                                start<AuthConfirmSuccessActivity> {
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                finish()
                            }
        })
    }

    override fun initVM(): AuthInforViewModel = AuthInforViewModel()

    fun setStatueColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
        StatusBarUtils.StatusBarLightMode(this)

    }

}
