package com.jzz.treasureship.view.viewGroup

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.jzz.treasureship.R
import q.rorbin.badgeview.DisplayUtil

/**
 *@date: 2020/9/16
 *@describe:
 *@Auth: 29579
 **/
class BaseDialog @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var imageView:ImageView = ImageView(context)
    var bottomImageView:ImageView = ImageView(context)
    var mWidth:Int = 0
    var mHight:Int = 0
    init {
        getValues(attrs)
        addView(imageView)
        orientation = VERTICAL
        imageView.apply {

            adjustViewBounds = true
        }
       bottomImageView.apply {
            adjustViewBounds = true
        }
        val layoutParams = LinearLayout.LayoutParams(context,attrs)
        layoutParams.topMargin = DisplayUtil.dp2px(context,19f)
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        layoutParams.width = mWidth
        if(mHight != 0)
        {
            layoutParams.height = mHight
        }
        addView(bottomImageView,layoutParams)

        if(isInEditMode)
        {}
    }

        private fun getValues(attrs: AttributeSet?){
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseDialog)
            val topImage = typedArray.getResourceId(R.styleable.BaseDialog_setTopImage,0)
            val bottomImage = typedArray.getResourceId(R.styleable.BaseDialog_setBottomImage,0)
            mWidth = typedArray.getDimension(R.styleable.BaseDialog_bottomWidth,0f).toInt()
            mHight = typedArray.getDimension(R.styleable.BaseDialog_bottomHight,0f).toInt()
            imageView.setImageDrawable(ContextCompat.getDrawable(context,topImage))
            bottomImageView.setImageDrawable(ContextCompat.getDrawable(context,bottomImage))
            typedArray.recycle()
        }



}
