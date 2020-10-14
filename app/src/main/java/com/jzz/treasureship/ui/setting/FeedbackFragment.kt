package com.jzz.treasureship.ui.setting

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class FeedbackFragment : BaseVMFragment<HomeViewModel>() {

    companion object {
        fun newInstance(): FeedbackFragment {
            return FeedbackFragment()
        }
    }

    override fun getLayoutResId() = R.layout.fragment_feedback

    override fun initVM(): HomeViewModel = getViewModel()

    override fun initView() {
//        //activity!!.nav_view.visibility = View.GONE

        tv_title.text = "意见反馈"

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        tv_submit.setOnClickListener {
            if (edit_content.text.toString().isBlank()) {
                ToastUtils.showShort("请先输入您的宝贵意见哦")
            } else {
                mViewModel.sendFeedback(edit_content.text.toString())
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.apply {
            operateUiState.observe(this@FeedbackFragment, Observer {
                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@FeedbackFragment.context, LoginActivity::class.java))
                    }
                }

                if (it.showSuccess == null) {
                    edit_content.setText("")
                    ToastUtils.showShort("已收到您的意见，我们会努力改进！")

                } else {
                    ToastUtils.showShort("操作失败")
                }
            })

        }
    }

    override fun initListener() {
    }


}
