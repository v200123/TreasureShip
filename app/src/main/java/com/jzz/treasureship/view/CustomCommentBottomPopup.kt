package com.jzz.treasureship.view

import android.content.Context
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CommentsAdapter
import com.jzz.treasureship.ui.home.HomeViewModel
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
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
        val msgInput = findViewById<TextView>(R.id.et_comments)
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
                            replyId = mAdapter.getItem(position).id
                            toWho = "@${mAdapter.getItem(position).nickName}"
                            msgInput.hint = toWho
                            showEditextDialog(msgInput.hint.toString())

                        }
                        R.id.layout_comment_item -> {
                            replyId = -1
                            toWho = "输入内容评论视频"
                            msgInput.hint = toWho
                            showEditextDialog(msgInput.hint.toString())
                        }
                    }
                }
                adapter = mAdapter
            }
        }
        msgInput.setOnClickListener {
            showEditextDialog(msgInput.hint.toString())
        }
//        msgInput.imeOptions = EditorInfo.IME_ACTION_SEND
//        msgInput.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEND
//            ) {
//                if (msgInput.text.toString().isBlank()) {
//                    ToastUtils.showShort("先输入点内容再评论吧")
//                } else {
//                    mViewModel.addComment(
//                        replyId, msgInput.text.toString(), if (replyId == -1) {
//                            mVideoId
//                        } else {
//                            -1
//                        }
//                    )
//                    msgInput.setText("")
//                    msgInput.hint = "点击回复评论"
//                    msgInput.requestFocus()
//                    KeyboardUtils.showSoftInput(this)
//                }
//            }
//            false
//        }

    }


    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .8f).roundToInt()
    }

    fun showEditextDialog(msg: String) {
        XPopup.Builder(context)
            .hasShadowBg(false)
            .autoFocusEditText(true)
            .setPopupCallback(object :SimpleCallback(){
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)

                }
            })
            .asCustom(object : BottomPopupView(context) {
                override fun getImplLayoutId(): Int = R.layout.dialog_editext

                override fun dismiss() {
                    super.dismiss()
                    val comments = findViewById<EditText>(R.id.et_dialog_comments)
                    comments.clearFocus()
                    KeyboardUtils.hideSoftInput(comments)

                }

                override fun onCreate() {
                    super.onCreate()
                    val comments = findViewById<EditText>(R.id.et_dialog_comments)
                    val sendComments = findViewById<ImageView>(R.id.iv_dialog_sendComments)
                    comments.requestFocus()
                    KeyboardUtils.showSoftInput(comments)
                    comments.hint = msg
                    sendComments.setOnClickListener {
                        if (comments.text.isNotEmpty()) {
                            mViewModel.addComment(
                                replyId, comments.text.toString(), if (replyId == -1) {
                                    mVideoId
                                } else {
                                    -1
                                }
                            )
                        } else {
                            ToastUtils.showShort("请输入回复")
                        }
                        this.dismiss()
                    }
                }
            }).show()
    }

    override fun getPopupHeight(): Int =
        if (mAdapter.data.size == 0)
            0
        else
            (XPopupUtils.getWindowHeight(context) * .8f).roundToInt()
}






