package com.jzz.treasureship.ui.trace

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.TraceAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.android.synthetic.main.layout_view_logistics.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class TraceFragment : BaseVMFragment<TraceViewModel>() {
    private val mOrdersFragment by lazy { OrdersFragment.newInstance() }

    companion object {
        fun newInstance(orderId: Int): TraceFragment {
            val f = TraceFragment()
            val bundle = Bundle()
            bundle.putInt("orderId", orderId)
            f.arguments = bundle
            return f
        }
    }

    private val mAdapter by lazy { TraceAdapter() }

    override fun getLayoutResId() = R.layout.layout_view_logistics

    override fun initVM(): TraceViewModel = getViewModel()

    override fun initView() {

        tv_title.text = "查看物流"
        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
//            activity!!.supportFragmentManager.beginTransaction().hide(this)
//                .add(
//                    R.id.frame_content,
//                    mOrdersFragment,
//                    mOrdersFragment.javaClass.name
//                ).commit()
        }

        tv_cpoy.setOnClickListener {
            val cm = activity!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", tv_traceNo.text)
            cm.setPrimaryClip(mClipData)

            cm.addPrimaryClipChangedListener(OnPrimaryClipChangedListener {
                if (cm.hasPrimaryClip() && cm.primaryClip!!.itemCount > 0) {
                    val addedText: CharSequence = cm.primaryClip!!.getItemAt(0).text
                    if (addedText.isNotBlank()) {
                        ToastUtils.showShort("已复制单号：${tv_traceNo.text}")
                    }
                }
            })
        }

        rvTrace.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = mAdapter
        }
    }

    override fun initData() {
        val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
        if (isLogin) {
            arguments?.let {
                val orderId = it.getInt("orderId")
                mViewModel.getTrace(orderId)
            }
        } else {
            ToastUtils.showShort("当前未登录，请登录后再重新操作！")
        }
    }

    override fun startObserve() {

        mViewModel.apply {
            val xPopup = XPopup.Builder(this@TraceFragment.context).asLoading()
            expressState.observe(this@TraceFragment, Observer {

                if (it.showLoading) {
                    xPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@TraceFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let {
                    xPopup.dismiss()
                    tv_ExpressCompany.text = it.name
                    tv_traceNo.text = it.no

                    mAdapter.setList(it.list)
                    mAdapter.notifyDataSetChanged()
                }

                it.showError?.let { message ->
                    xPopup.dismiss()
                    ToastUtils.showShort(if (message.isBlank()) "网络异常" else message)
                }
            })
        }
    }

    override fun initListener() {
    }

}