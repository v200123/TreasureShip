package com.jzz.treasureship.view.viewGroup

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView

/**
 *@PackageName: com.jzz.treasureship.view.viewGroup
 *@Auth： 29579
 *@Description: 用于设置只有布局左右有view的  **/
class LeftAndRightView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var maxHeight = 0
    private val mLeftTextView:TextView
    private val mRightTextView:TextView
    init {
        mLeftTextView = TextView(context)
        mRightTextView = TextView(context)
        val leftParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.START)
        val rightParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.END)
        addView(mLeftTextView,leftParams)
        addView(mRightTextView,rightParams)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        for (i in 0 until childCount)
        {
            val childAt = getChildAt(i)
            maxHeight = maxOf( childAt.measuredHeight + childAt.paddingTop + childAt.paddingBottom,maxHeight)
        }

        setMeasuredDimension(measuredWidth,maxHeight)
    }






}