package com.jzz.treasureship.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.jzz.treasureship.R


class CustomDialog : Dialog {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, theme: Int) : super(context!!, theme)

    class Builder(context: Context) {
        private val context //上下文对象
                : Context
        private var title //对话框标题
                : String? = null
        private var message //对话框内容
                : String? = null
        private var confirm_btnText //按钮名称“确定”
                : String? = null
        private var cancel_btnText //按钮名称“取消”
                : String? = null
        private var contentView //对话框中间加载的其他布局界面
                : View? = null

        /*按钮确认事件*/
        private var confirm_btnClickListener: DialogInterface.OnClickListener? = null
//        private var cancel_btnClickListener: DialogInterface.OnClickListener? = null

        /*设置对话框信息*/
        fun setMessage(message: String?): Builder {
            this.message = message
            return this
        }

        /**
         * Set the Dialog message from resource
         *
         * @param title
         * @return
         */
        fun setMessage(message: Int): Builder {
            this.message = context.getText(message).toString()
            return this
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        fun setTitle(title: Int): Builder {
            this.title = context.getText(title).toString()
            return this
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        /**
         * 设置对话框界面
         * @param v View
         * @return
         */
        fun setContentView(v: View?): Builder {
            contentView = v
            return this
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        fun setPositiveButton(
            confirm_btnText: Int,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            this.confirm_btnText = context
                .getText(confirm_btnText).toString()
            confirm_btnClickListener = listener
            return this
        }

        /**
         * Set the positive button and it's listener
         *
         * @param confirm_btnText
         * @return
         */
        fun setPositiveButton(
            confirm_btnText: String?,
            listener: DialogInterface.OnClickListener?
        ): Builder {
            this.confirm_btnText = confirm_btnText
            confirm_btnClickListener = listener
            return this
        }

//        /**
//         * Set the negative button resource and it's listener
//         *
//         * @param confirm_btnText
//         * @return
//         */
//        fun setNegativeButton(
//            cancel_btnText: Int,
//            listener: DialogInterface.OnClickListener?
//        ): Builder {
//            this.cancel_btnText = context
//                .getText(cancel_btnText).toString()
//            cancel_btnClickListener = listener
//            return this
//        }
//
//        /**
//         * Set the negative button and it's listener
//         *
//         * @param confirm_btnText
//         * @return
//         */
//        fun setNegativeButton(
//            cancel_btnText: String?,
//            listener: DialogInterface.OnClickListener?
//        ): Builder {
//            this.cancel_btnText = cancel_btnText
//            cancel_btnClickListener = listener
//            return this
//        }

        @SuppressLint("WrongViewCast")
        fun create(): CustomDialog {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            // instantiate the dialog with the custom Theme
            val dialog = CustomDialog(context, R.style.ios_dialog_style)
            val layout: View = inflater.inflate(R.layout.dialog_ios_ensure, null)
            dialog.addContentView(
                layout, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            // set the dialog title
            val mDialogTitle = layout.findViewById<TextView>(R.id.title)
            mDialogTitle.text = title
            (mDialogTitle as TextView).paint.isFakeBoldText = true
            // set the confirm button
            if (confirm_btnText != null) {
                (layout.findViewById(R.id.confirm_btn) as Button).text = confirm_btnText
                if (confirm_btnClickListener != null) {
                    (layout.findViewById(R.id.confirm_btn) as Button)
                        .setOnClickListener {
                            confirm_btnClickListener!!.onClick(
                                dialog,
                                DialogInterface.BUTTON_POSITIVE
                            )
                        }
                }
            } else {
// if no confirm button just set the visibility to GONE
                (layout.findViewById(R.id.confirm_btn) as Button).visibility = View.GONE
            }
            // set the cancel button
//            if (cancel_btnText != null) {
//                (layout.findViewById(R.id.cancel_btn) as Button).text = cancel_btnText
//                if (cancel_btnClickListener != null) {
//                    (layout.findViewById(R.id.cancel_btn) as Button)
//                        .setOnClickListener {
//                            cancel_btnClickListener!!.onClick(
//                                dialog,
//                                DialogInterface.BUTTON_NEGATIVE
//                            )
//                        }
//                }
//            } else {
//// if no confirm button just set the visibility to GONE
//                (layout.findViewById(R.id.cancel_btn) as Button).visibility = View.GONE
//            }
            // set the content message
            if (message != null) {
                (layout.findViewById(R.id.message) as TextView).text = message
            } else if (contentView != null) {
// if no message set
// add the contentView to the dialog body
                (layout.findViewById(R.id.message) as LinearLayout)
                    .removeAllViews()
                (layout.findViewById(R.id.message) as LinearLayout).addView(
                    contentView, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
            dialog.setContentView(layout)
            return dialog
        }

        init {
            this.context = context
        }
    }
}

