package com.jzz.treasureship.view

import android.content.Context
import com.jzz.treasureship.R
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.layout_check_docter_advice_popup.view.*
import kotlin.math.roundToInt

class CustomCheckDoctorAdvicePopup(context: Context, advice: String?) : BottomPopupView(context) {

    private val mAdvice = advice

    override fun getImplLayoutId() = R.layout.layout_check_docter_advice_popup

    override fun initPopupContent() {
        super.initPopupContent()

        iv_close.setOnClickListener {
            dismiss()
        }

        if (mAdvice.isNullOrBlank()) {
            tv_doctorAdvice.text = "无医嘱信息"
        } else {
            tv_doctorAdvice.text = mAdvice
        }

    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getWindowHeight(context) * .75f).roundToInt()
    }
}