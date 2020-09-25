package com.jzz.treasureship.view

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CommentsAdapter
import com.jzz.treasureship.ui.home.HomeViewModel
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.dialog_home_comments.view.*
import kotlin.math.roundToInt


class CustomCommentBottomPopup(
    context: Context, viewModel: HomeViewModel,
    videoId: Int, adapter: CommentsAdapter
) : BottomPopupView(context) {

    private var mAdapter = adapter
    private val mViewModel = viewModel
    private val mVideoId: Int = videoId
    private var replyId: Int = -1
    private var toWho: String? = null

    override fun getImplLayoutId() = R.layout.dialog_home_comments

    override fun initPopupContent() {
        super.initPopupContent()
        rv_comments.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            mAdapter.run {
                onItemChildClickListener = mOnItemChildClickListener
            }

            adapter = mAdapter
        }

        et_comments.imeOptions = EditorInfo.IME_ACTION_SEND
        et_comments.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND
            ) {
                    if (et_comments.text.toString().isBlank()) {
                        ToastUtils.showShort("先输入点内容再评论吧")
                    } else {
                        mViewModel.addComment(
                            replyId, et_comments.text.toString(), if (replyId == -1) {
                                mVideoId
                            } else {
                                -1
                            }
                        )
                        et_comments.setText("")
                        et_comments.hint = "点击回复评论"
                    }
                    true
            }
            false
        }

        iv_sendComments.setOnClickListener {
            if (et_comments.text.toString().isBlank()) {
                ToastUtils.showShort("先输入点内容再评论吧")
            } else {
                mViewModel.addComment(
                    replyId, et_comments.text.toString(), if (replyId == -1) {
                        mVideoId
                    } else {
                        -1
                    }
                )
                et_comments.setText("")
            }
        }
    }

    private var mOnItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
        when (view.id) {
            R.id.iv_praise -> {
                mViewModel.addPraise(mAdapter.data[position].id, mVideoId)
            }
            R.id.tv_comment_content -> {
                replyId = mAdapter.getItem(position)!!.id
                toWho = "@${mAdapter.getItem(position)!!.nickName}"
                et_comments.hint = toWho
                et_comments.requestFocus()
                val imm: InputMethodManager? =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.showSoftInput(et_comments, InputMethodManager.SHOW_IMPLICIT)
            }
            R.id.layout_comment_item -> {
                replyId = -1
                toWho = "输入内容评论视频"
                et_comments.hint = toWho
                //et_comments.requestFocus()
                et_comments.clearFocus()
                val imm: InputMethodManager? =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(et_comments.windowToken, 0)
            }
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .6f).roundToInt()
    }
}