package com.jzz.treasureship.ui.ranking

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.RankAdapter
import com.jzz.treasureship.base.BaseVMFragment
import com.jzz.treasureship.model.bean.XX
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.view.MyDatePickerDialog
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_mine_rank.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*


class RankingFragment : BaseVMFragment<RankingViewModel>() {

    private val calendar = Calendar.getInstance()
    private var mYear = calendar[Calendar.YEAR]
    private var mMonth = calendar[Calendar.MONTH]

    companion object {
        fun newInstance(): RankingFragment {
            return RankingFragment()
        }
    }

    private val mAdapter by lazy { RankAdapter() }

    override fun getLayoutResId() = R.layout.layout_mine_rank

    override fun initVM(): RankingViewModel = getViewModel()

    override fun initView() {

        activity!!.nav_view.visibility = View.GONE

        iv_back.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        tv_chooseMonth.setOnClickListener {

            val c = Calendar.getInstance()
            var year = mYear
            if (mMonth != c[Calendar.MONTH]){
                --mMonth
            }
            var monthOfYear = mMonth
            var dayOfMonth = c[Calendar.DAY_OF_MONTH]

            val datePicker = MyDatePickerDialog(
                it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                MyDatePickerDialog.OnDateSetListener { _, year, _monthOfYear, _ ->
                    val month = _monthOfYear + 1

                    if ((month) < 10) {
                        mViewModel.getRankList("${year}-0${month}")
                    } else {
                        mViewModel.getRankList("${year}-${month}")
                    }

                    mYear = year
                    mMonth = month

                    if ((mMonth) < 10) {
                        tv_chooseMonth.text = "${mYear}-0${mMonth}"
                    } else {
                        tv_chooseMonth.text = "${mYear}-${mMonth}"
                    }
                }, year, monthOfYear, dayOfMonth
            )
            datePicker.myShow()


            // 将对话框的大小按屏幕大小的百分比设置
            val windowManager: WindowManager = activity!!.windowManager
            val display = windowManager.defaultDisplay
            val lp: WindowManager.LayoutParams = datePicker.window!!.attributes
            lp.width = (display.width * 0.8).toInt() //设置宽度

            datePicker.window!!.attributes = lp

            // 去掉显示日  只显示年月
            ((datePicker.datePicker.getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(2)
                .visibility = View.GONE
        }

        rcv_ranking.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            adapter = mAdapter
        }


    }

    override fun initData() {
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            mViewModel.getRankList("${calendar.get(Calendar.YEAR)}-0${calendar.get(Calendar.MONTH) + 1}")
        } else {
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(this@RankingFragment.context).asLoading()
            rankState.observe(this@RankingFragment, androidx.lifecycle.Observer {
                if (it.showLoading) {
                    xPopup.show()
                }

                it.needLogin?.let { needLogin ->
                    if (needLogin) {
                        ToastUtils.showShort("未登录，请登录后再操作！")
                        startActivity(Intent(this@RankingFragment.context, LoginActivity::class.java))
                    }
                }

                it.showSuccess?.let {
                    xPopup.dismiss()

                    it.avatar?.let { avatar ->
                        Glide.with(this@RankingFragment).load(avatar)
                            .apply(RequestOptions.bitmapTransform(CircleCrop())).into(iv_avatar)
                    }
                    tv_name.text = it.nickName
                    tv_rankNum.text = "${it.rankNum}"
                    tv_income.text = "收入:${it.account}元"
                    tv_totalRankNum.text = "${it.num}人"

                    mAdapter.setNewData(it.list as MutableList<XX>?)
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