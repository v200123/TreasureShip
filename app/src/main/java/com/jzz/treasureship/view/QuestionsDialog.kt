package com.jzz.treasureship.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.AnswersAdapter
import com.jzz.treasureship.ui.home.HomeViewModel
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
        mAdapter.apply {
            onItemChildClickListener = this@QuestionsDialog.onItemChildClickListener
        }

        rcv_questions.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = mAdapter
        }
    }

    private val onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->

        when (view.id) {
//            R.id.layout_questionItem -> {
//                Log.d("QuestionsDialog", "提交答案")
//                val json = JSONObject(mAdapter.getItem(position)!!.item)
//                val ans = json.get("item").toString()
//                viewModel.submitQuestionnaire(ans, id)
//            }
        }

        dismiss()
    }
}