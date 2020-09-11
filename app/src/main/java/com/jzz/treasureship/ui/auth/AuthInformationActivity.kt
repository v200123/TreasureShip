package com.jzz.treasureship.ui.auth

import androidx.activity.viewModels
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.ui.auth.adapter.AuthInfoAdapter
import com.jzz.treasureship.ui.auth.viewmodel.AuthInforViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import kotlinx.android.synthetic.main.activity_auth_information.*

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthInformationActivity : BaseVMActivity<AuthInforViewModel>(false) {
    private val model by viewModels<CommonDataViewModel>()


    companion object
    {
        const val occuId = "com.jzz.occuId"
    }


    override fun initView() {
        model.occuID= intent.getStringExtra(occuId)?:"1"
        vp_authinfor.adapter =AuthInfoAdapter(supportFragmentManager)
        vp_authinfor.isEnabled = false
        btn_baseinfo_next.setOnClickListener {
            vp_authinfor.setCurrentItem(vp_authinfor.currentItem++,true)
        }

    }

    override fun getLayoutResId(): Int = R.layout.activity_auth_information

    override fun initData() {
    }

    override fun startObserve() {
    }

    override fun initVM(): AuthInforViewModel  = AuthInforViewModel()
}
