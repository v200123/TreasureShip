package com.lc.liuchanglib.customValue

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 *@PackageName: com.lc.liuchanglib.customValue
 *@Auth： 29579
 *@Description: 只有左右布局的属性值  **/
class LeftAndRightValue {
    var mTextMsg = ""
    var mTextSize = 15f
    var mTextBold = false
    @ColorRes
    @ColorInt
    var mTextColor = Color.parseColor("#FFFFFF")


    fun buildText(textView: TextView) {
        textView.apply {
            text = mTextMsg
            setTextColor(mTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize)
            if (mTextBold)
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            else
                typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        }
    }
}