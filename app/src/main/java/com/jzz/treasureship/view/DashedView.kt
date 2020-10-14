package com.jzz.treasureship.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jzz.treasureship.utils.out
import q.rorbin.badgeview.DisplayUtil

/**
 *@date: 2020/9/15
 *@describe: 用于绘制虚线的
 *@Auth: 29579
 **/
class DashedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private  var mWidth:Int = 0
    private  var mHeight:Int = 0
    private  var canDrawCount = 0
     var level = 0
    set(value) {
        field = value
        invalidate()
    }
    private val mPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FF26C8D7")
    }

    private val unSelect = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFD5EDEF")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(DisplayUtil.dp2px(context,252f),DisplayUtil.dp2px(context,2f))
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        canDrawCount = w /65
        "可以绘制$canDrawCount".out(true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        for(i in 0 until canDrawCount) {
            canvas.drawRect(0f, 0f, 55f, 10f, unSelect)
            canvas.translate(65f, 0f)
        }
        canvas.restore()
       if(level == 1)
       {
           canvas.drawRect(0f, 0f, mWidth /2f, 10f, mPaint)
       }
        if(level ==2)
        {
            canvas.drawRect(0f, 0f, mWidth /2f+175, 10f, mPaint)
        }
    }
}