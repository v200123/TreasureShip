package com.jzz.treasureship.ui.auth

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.getMinimumHeight
import androidx.viewpager.widget.ViewPager
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.auth.adapter.AuthInfoAdapter
import com.jzz.treasureship.ui.auth.viewmodel.AuthInforViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import com.lc.mybaselibrary.out
import kotlinx.android.synthetic.main.activity_auth_information.*
import kotlinx.android.synthetic.main.include_title.*

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


    override fun initView() {
        tv_title.text = "认证信息"
        model.occuID = intent.getStringExtra(occuId) ?: "1"
        vp_authinfor.adapter = AuthInfoAdapter(supportFragmentManager)
        vp_authinfor.isEnabled = false
        btn_baseinfo_next.setOnClickListener {
            vp_authinfor.setCurrentItem(vp_authinfor.currentItem++, true)
        }

        vp_authinfor.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            @SuppressLint("MissingSuperCall")
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                "我进来了".out()
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
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
                    tv_authinfor_02.setCompoundDrawables(
                        null,
                        ContextCompat.getDrawable(mContext, R.drawable.ico_auth_now)!!.apply {
                            setBounds(0, 0, this.minimumWidth, this.minimumHeight)
                        },
                        null,
                        null
                    )
//                    ico_auth_now
                }

                "当前这是第$position 个流程了".out(true)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

    }

    override fun getLayoutResId(): Int = R.layout.activity_auth_information

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun initVM(): AuthInforViewModel = AuthInforViewModel()
}
