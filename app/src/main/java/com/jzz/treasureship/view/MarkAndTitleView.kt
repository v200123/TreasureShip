package com.jzz.treasureship.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SnackbarUtils.addView

/**
 *@date: 2020/9/4
 *@describe:
 *@Auth: 29579
 **/
class MarkAndTitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    val mMark:TextView = TextView(context)
    val mTitle:TextView = TextView(context)

    init {

    }

    fun setMark(mark:String?,ResID:Int){
        if(mark.isNullOrEmpty())
        {

            mMark.apply { visibility = View.VISIBLE
            text = mark
                background = ContextCompat.getDrawable(context,ResID)
            }
        }
        else{
            mMark.visibility = View.GONE
        }
    }

    fun setTitle(title:String){
        mTitle.text = title
    }
}
