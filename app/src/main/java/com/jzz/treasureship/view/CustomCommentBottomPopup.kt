package com.jzz.treasureship.view

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CommentsAdapter
import com.jzz.treasureship.ui.home.HomeViewModel
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.KeyboardUtils
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

    override fun onCreate() {
        super.onCreate()
        val msgInput = findViewById<EditText>(R.id.et_comments)
        rv_comments.run {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            mAdapter.run {
                setOnItemChildClickListener { adapter, view, position ->
                    when (view.id) {
                        R.id.iv_praise -> {
                            mViewModel.addPraise(mAdapter.data[position].id, mVideoId)
                        }
                        R.id.tv_comment_content_parent -> {
                            replyId = mAdapter.getItem(position)!!.id
                            toWho = "@${mAdapter.getItem(position)!!.nickName}"
                            msgInput.hint = toWho
                            msgInput.requestFocus()
                            val imm: InputMethodManager? =
                                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                            imm?.showSoftInput(msgInput, InputMethodManager.SHOW_IMPLICIT)
                        }
                        R.id.layout_comment_item -> {
                            replyId = -1
                            toWho = "输入内容评论视频"
                            msgInput.hint = toWho
                            msgInput.requestFocus()
                            val imm: InputMethodManager? =
                                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                            imm?.hideSoftInputFromWindow(msgInput.windowToken, 0)
                        }
                    }
                }
                adapter = mAdapter
            }
        }
        msgInput.imeOptions = EditorInfo.IME_ACTION_SEND
        msgInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND
            ) {
                if (msgInput.text.toString().isBlank()) {
                    ToastUtils.showShort("先输入点内容再评论吧")
                } else {
                    mViewModel.addComment(
                        replyId, msgInput.text.toString(), if (replyId == -1) {
                            mVideoId
                        } else {
                            -1
                        }
                    )
                    msgInput.setText("")
                    msgInput.hint = "点击回复评论"
                    msgInput.requestFocus()
                    KeyboardUtils.showSoftInput(this)
                }

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
                msgInput.requestFocus()

                KeyboardUtils.showSoftInput(msgInput)

                et_comments.setText("")
                et_comments.hint = "点击回复评论"

            }
        }

    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .65f).roundToInt()
    }
}






