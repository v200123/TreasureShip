package com.lc.liuchanglib.ext

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.StyleableRes

/**
 *@PackageName: com.lc.liuchanglib.ext
 *@Auth： 29579
 *@Description: 自定义View的属性解析扩展  **/
 inline fun AttributeSet.Resolve(mContext:Context,@StyleableRes styRes:IntArray, block: TypedArray.()->Unit){
    val obtainStyledAttributes = mContext.obtainStyledAttributes(this, styRes)
    block(obtainStyledAttributes)
    obtainStyledAttributes.recycle()
}