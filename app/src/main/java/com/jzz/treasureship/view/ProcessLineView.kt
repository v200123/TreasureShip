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
 *@author LC
 *@createTime 2020/11/4 22:27
 *@description  描述文件
 */
class ProcessLineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var nowWidth = 0
    private var nowHeight = 0
    private val textWidth
        get() = nowWidth / 4

    private val _5dp = DisplayUtil.dp2px(context,5f)
    private val _11dp = DisplayUtil.dp2px(context,11f)
    private val _28dp = DisplayUtil.dp2px(context,28f)


    private val mFinishPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#26C8D7")
        }
    }
    private val mFinishTextPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.BLACK
            textSize = 60f
        }
    }

    private val mDoingPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#BDEEF3")
        }
    }

    private val mNoFinishPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#D5EDEF")
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {
            drawText("提交申请",0*textWidth+(textWidth-mFinishTextPaint.measureText("提交申请"))/2,_5dp+_28dp+240f,mFinishTextPaint)
            drawText("提交申请",1*textWidth+(textWidth-mFinishTextPaint.measureText("提交申请"))/2,_5dp+_28dp+240f,mFinishTextPaint)
            drawText("提交申请",2*textWidth+(textWidth-mFinishTextPaint.measureText("提交申请"))/2,_5dp+_28dp+240f,mFinishTextPaint)
            drawText("提交申请",3*textWidth+(textWidth-mFinishTextPaint.measureText("提交申请"))/2,_5dp+_28dp+240f,mFinishTextPaint)
            drawCircle((textWidth/2).toFloat(),(_5dp+_28dp).toFloat(), _5dp*2f,mFinishPaint)
            drawCircle(((textWidth/2)*3).toFloat(),(_5dp+_28dp).toFloat(), _11dp*2f,mDoingPaint)
            drawCircle(((textWidth/2)*3).toFloat(),(_5dp+_28dp).toFloat(), _5dp*2f,mFinishPaint)
            drawCircle(((textWidth/2)*5).toFloat(),(_5dp+_28dp).toFloat(), _5dp*2f,mFinishPaint)
            drawCircle(((textWidth/2)*7).toFloat(),(_5dp+_28dp
                    ).toFloat(), _5dp*2f,mFinishPaint)
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        "宽高发生了变化".out()
        nowWidth = w
        nowHeight =h

    }


}