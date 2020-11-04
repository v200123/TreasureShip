package com.lc.liuchanglib.cusotmView

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatCheckBox

/**
 *@PackageName: com.lc.liuchanglib.cusotmView
 *@Auth： 29579
 **/
class noTouchCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr) {


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return performClick()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }
}