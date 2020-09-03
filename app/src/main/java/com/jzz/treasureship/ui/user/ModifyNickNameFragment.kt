package com.jzz.treasureship.ui.user

import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_modified_nickname.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ModifyNickNameFragment : BaseVMFragment<UserViewModel>() {

    companion object{
        fun newInstance(): ModifyNickNameFragment {
            return ModifyNickNameFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_modified_nickname

    override fun initVM(): UserViewModel = getViewModel()

    override fun initView() {

        tv_title.text = "修改昵称"

        arguments?.let {
            et_modifiedName.hint = it.getString("nickName")
        }

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        tv_comfirm.setOnClickListener {
            mViewModel.modifiedInfo(null, et_modifiedName.text.toString())
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).asLoading()
            modifiedInfoState.observe(this@ModifyNickNameFragment, Observer {
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    if ("null" == it) {
                        ToastUtils.showShort("修改成功！")
                        activity!!.supportFragmentManager.popBackStack()
                    } else {
                        ToastUtils.showShort(it)
                    }
                }

                it.showError?.let { err ->
                    xPopup.dismiss()
                    ToastUtils.showShort(err)
                }
            })
        }
    }

    override fun initListener() {
    }

}