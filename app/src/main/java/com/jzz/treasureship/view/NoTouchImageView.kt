package com.jzz.treasureship.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView


/**
 *@date: 2020/9/21
 *@describe:
 *@Auth: 29579
 **/
class NoTouchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}