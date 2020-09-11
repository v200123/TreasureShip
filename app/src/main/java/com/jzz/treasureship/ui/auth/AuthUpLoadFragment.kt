package com.jzz.treasureship.ui.auth

import android.view.View
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.auth.viewmodel.AuthInforViewModel
import com.jzz.treasureship.ui.auth.viewmodel.AuthUpLoadViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import kotlinx.android.synthetic.main.fragment_auth_upload.*

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthUpLoadFragment : BaseVMFragment<AuthUpLoadViewModel>(false) {
    private val activityModel by activityViewModels<CommonDataViewModel>()


    override fun getLayoutResId(): Int  = R.layout.fragment_auth_upload

    override fun initVM(): AuthUpLoadViewModel  = AuthUpLoadViewModel()

    override fun initView() {
        mViewModel.occupationData.observe(this,{
            val mList = it.mList
            Glide.with(this).asDrawable().load(mList[0].mBackImage1).into(iv_authupload_image01)
            if(mList.size==1)
            {
//                Glide.with(this).asDrawable().load(mList[0].mBackImage1).into(iv_authupload_image01)
                tv_authupload_change.visibility = View.GONE
//                iv_authupload_image02.visibility = View.GONE
            }
            else{
//                Glide.with(this).asDrawable().load(mList[0].mBackImage1).into(iv_authupload_image01)
//                Glide.with(this).asDrawable().load(mList[1].mBackImage1).into(iv_authupload_image01)
                tv_authupload_change.visibility = View.VISIBLE
//                iv_authupload_image02.visibility = View.VISIBLE

            }
        })


    }

    override fun initData() {
        mViewModel.getOccupation(activityModel.occuID)
    }

    override fun startObserve() {
    }

    override fun initListener() {
    }
}
