package com.lc.liuchanglib.customValue

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 *@PackageName: com.lc.liuchanglib.customValue
 *@Auth： 29579
 *@Description: 只有左右布局的属性值  **/
class LeftAndRightValue(var mTextView: TextView) {
    var mTextMsg: CharSequence = ""
    var mTextSize = 15f
    var mTextBold = false
    var mDrawable: Drawable? = null

    @ColorRes
    @ColorInt
    var mTextColor = Color.parseColor("#FFFFFF")

    fun buildText() {
        mTextView.apply {
            gravity = Gravity.CENTER_VERTICAL
            text = mTextMsg
            setTextColor(mTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
            if (mTextBold)
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            else
                typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            mDrawable?.let {
                setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null)
            }
        }
    }
}