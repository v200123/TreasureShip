package com.lc.mybaselibrary.ext

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 *@date: 2020/9/21
 *@describe:
 *@Auth: 29579
 **/
fun Context.getResColor(@ColorRes colorResId:Int):Int = ContextCompat.getColor(this,colorResId)

fun Context.getResDrawable(@DrawableRes drawableResId:Int)= ContextCompat.getDrawable(this,drawableResId)