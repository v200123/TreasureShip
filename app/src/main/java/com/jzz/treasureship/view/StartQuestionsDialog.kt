package com.jzz.treasureship.view

import android.content.Context
R
utils.PreferenceUtils
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_start_questions.view.*

class StartQuestionsDialog(context: Context) :
    CenterPopupView(context) {

    private var startAnswer by PreferenceUtils(PreferenceUtils.start_answer, false)
    override fun getImplLayoutId() = R.layout.dialog_start_questions

    override fun initPopupContent() {
        super.initPopupContent()

        tv_answerNow.setOnClickListener {
            startAnswer = true
            dismiss()
        }

        tv_beLater.setOnClickListener {
            startAnswer = false
            dismiss()
        }

        iv_close.setOnClickListener {
            startAnswer = false
            dismiss()
        }
    }
}