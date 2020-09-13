package com.jzz.treasureship.ui.auth

import android.view.View
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.image
import com.jzz.treasureship.ui.auth.viewmodel.AuthUpLoadViewModel
import com.jzz.treasureship.ui.auth.viewmodel.CommonDataViewModel
import com.jzz.treasureship.view.DialogSimpleList
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_auth_upload.*

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthUpLoadFragment : BaseVMFragment<AuthUpLoadViewModel>(false) {
    private val activityModel by activityViewModels<CommonDataViewModel>()
    private lateinit var mImageList: List<image>

    override fun getLayoutResId(): Int = R.layout.fragment_auth_upload

    override fun initVM(): AuthUpLoadViewModel = AuthUpLoadViewModel()

    override fun initView() {
        mViewModel.occupationData.observe(this, {
            val mList = it.mList
            mImageList = it.mList
//            用于取第一个的资质的
            val firstOccupation = mList[0]
            Glide.with(this).asDrawable().load(firstOccupation.mBackImage1)
                .into(iv_authupload_image01)
            tv_authUpLoad_title.text = firstOccupation.mTitle
//            if(firstOccupation.mImageCount == 1)
//            {
//                iv_authupload_image02.visibility = View.GONE
//            }

            if (firstOccupation.mBackImage2 != null) {
                Glide.with(this).asDrawable().load(firstOccupation.mBackImage2)
                    .into(iv_authupload_image01)
            }
            if (mList.size == 1) {
//                Glide.with(this).asDrawable().load(mList[0].mBackImage1).into(iv_authupload_image01)
                tv_authupload_change.visibility = View.GONE
//                iv_authupload_image02.visibility = View.GONE
            } else {
//                Glide.with(this).asDrawable().load(mList[0].mBackImage1).into(iv_authupload_image01)
//                Glide.with(this).asDrawable().load(mList[1].mBackImage1).into(iv_authupload_image01)
                tv_authupload_change.visibility = View.VISIBLE
                tv_authupload_change.setOnClickListener {
                    XPopup.Builder(mContext).asCustom(DialogSimpleList(mContext, arrayOf(mList[0].mTitle,mList[1].mTitle)))
                        .show()
                }
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
