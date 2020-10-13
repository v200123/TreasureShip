package com.jzz.treasureship.ui.customer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CustomerDetailsAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.DataX
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.orders.OrdersViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.view.CustomCheckCommissionPopup
import com.jzz.treasureship.view.CustomCheckDoctorAdvicePopup
import com.jzz.treasureship.view.CustomSetNoticePopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.layout_customer_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class CustomerDetailFragment : BaseVMFragment<OrdersViewModel>() {
    private var mMemberInfo: DataX? = null

    companion object {
        fun newInstance(contacter: DataX): CustomerDetailFragment {
            val f = CustomerDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("memberInfo", contacter)
            f.arguments = bundle
            return f
        }
    }

    private val mAdapter by lazy { CustomerDetailsAdapter() }

    override fun getLayoutResId() = R.layout.layout_customer_detail

    override fun initVM(): OrdersViewModel = getViewModel()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        tv_title.text = "客户详情"

        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        arguments?.let {
            mMemberInfo = it.getParcelable("memberInfo")
        }

        mMemberInfo?.let {
            Glide.with(context!!).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(iv_customer_head)
            tv_customer_name.text = it.nikeName

            when (it.sex) {
                0 -> {
                    Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.icon_male))
                        .into(iv_sex)
                    tv_sex.text = "男"
                }
                1 -> {
                    Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.icon_female))
                        .into(iv_sex)
                    tv_sex.text = "女"
                }
                else -> {
                    Glide.with(context!!).load(context!!.resources.getDrawable(R.drawable.icon_male))
                        .into(iv_sex)
                    tv_sex.text = "其它"
                }
            }


        }

        rcv_orders.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            mAdapter.run {
                setOnItemChildClickListener() { adapter, view, position ->
                    when (view.id) {
                        R.id.tv_checkDoctorAdvice -> {
                            XPopup.Builder(view.context)
                                .asCustom(CustomCheckDoctorAdvicePopup(view.context, mAdapter.getItem(position)!!.doctorAdvice))
                                .show()
                        }
                        R.id.tv_check_commission -> {
                            XPopup.Builder(view.context)
                                .asCustom(CustomCheckCommissionPopup(view.context, mAdapter.getItem(position)!!))
                                .show()
                        }
                    }
                }
            }

            adapter = mAdapter
        }

        iv_remind.setOnClickListener {
            XPopup.Builder(it.context).setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView) {
                    super.onDismiss(popupView)
                    val noticeTime by PreferenceUtils(PreferenceUtils.NOTICE_TIME, "")

                    var noticeType by PreferenceUtils(PreferenceUtils.NOTICE_TYPE, -1)


                    if (!noticeTime.isBlank()) {
                        mViewModel.setNotice(mMemberInfo!!.id!!, noticeType, noticeTime)
                    }

                    noticeType = 1
                }


            }).asCustom(CustomSetNoticePopup(it.context)).show()
        }
    }


    override fun initData() {
        arguments?.let {
            mViewModel.getOrderList(mMemberInfo!!.id, null, 8, 0)
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            ordersState.observe(this@CustomerDetailFragment, Observer {
                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.showSuccess?.let {
                    mAdapter.setList(it.data)
                    mAdapter.notifyDataSetChanged()
                }
            })

            noticeState.observe(this@CustomerDetailFragment, Observer {
                it.showError?.let { message ->
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@CustomerDetailFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess.let {
                    var noticeType by PreferenceUtils(PreferenceUtils.NOTICE_TYPE, -1)
                    if (noticeType == 1) {
                        ToastUtils.showShort("设置提醒成功！")
                        noticeType = -1
                    }

                }
            })
        }
    }

    override fun initListener() {
    }

}