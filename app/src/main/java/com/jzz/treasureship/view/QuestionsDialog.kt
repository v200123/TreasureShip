package com.jzz.treasureship.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
R
adapter.AnswersAdapter
ui.home.HomeViewModel
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_questions.view.*

class QuestionsDialog(
    context: Context,
    iconPath: String,
    id: Int,
    viewModel: HomeViewModel,
    adapter: AnswersAdapter
) :
    CenterPopupView(context) {

    private val viewModel: HomeViewModel = viewModel
    private val mAdapter: AnswersAdapter = adapter
    private val mIconPath: String = iconPath

    override fun getImplLayoutId() = R.layout.dialog_questions

    override fun initPopupContent() {
        super.initPopupContent()
        Glide.with(context).load(mIconPath).into(iv_icon)
        initRecycleView()
    }

    private fun initRecycleView() {

        rcv_questions.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = mAdapter
        }
    }

}