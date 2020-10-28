package com.lc.liuchanglib.cusotmView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.lc.liuchanglib.R
import com.lc.liuchanglib.customValue.LeftAndRightValue
import com.lc.liuchanglib.ext.Resolve

/**
 *@PackageName: com.lc.liuchanglib.cusotmView
 *@Auth： 29579
 **/
class LeftAndRightView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var maxHeight = 0
    private val mLeftTextView: TextView
    private val mRightTextView: TextView
    val mLeftValue = LeftAndRightValue()
    val mRightValue = LeftAndRightValue()

    init {
        mLeftTextView = TextView(context)
        mRightTextView = TextView(context)
        val leftParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.START)
        val rightParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.END)
        addView(mLeftTextView, leftParams)
        addView(mRightTextView, rightParams)
        initValue(attrs!!)
        mLeftValue.buildText(mLeftTextView)
        mRightValue.buildText(mRightTextView)
    }

    fun initValue(attrs: AttributeSet) {
        attrs.Resolve(context, R.styleable.LeftAndRightView) {
            mLeftValue.mTextMsg = getString(R.styleable.LeftAndRightView_leftText) ?: ""
            mLeftValue.mTextSize= getDimension(R.styleable.LeftAndRightView_leftTextSize, 14f)
            mLeftValue.mTextBold = getBoolean(R.styleable.LeftAndRightView_leftBold, false)
            mLeftValue.mTextColor = getColor(R.styleable.LeftAndRightView_leftTextColor, Color.parseColor("#FFFFFF"))

            mRightValue.mTextMsg = getString(R.styleable.LeftAndRightView_rightText) ?: ""
            mRightValue.mTextSize= getDimension(R.styleable.LeftAndRightView_rightTextSize, 14f)
            mRightValue.mTextBold = getBoolean(R.styleable.LeftAndRightView_rightBold, false)
            mRightValue.mTextColor = getColor(R.styleable.LeftAndRightView_rightTextColor, Color.parseColor("#FFFFFF"))

        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val childCount = childCount
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            maxHeight = maxOf(childAt.measuredHeight + childAt.paddingTop + childAt.paddingBottom, maxHeight)
        }
        setMeasuredDimension(measuredWidth, maxHeight)
    }


}