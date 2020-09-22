package com.lc.mybaselibrary.ext

import android.content.Context
import androidx.core.content.ContextCompat

/**
 *@date: 2020/9/21
 *@describe:
 *@Auth: 29579
 **/
fun Context.getResColor(colorResId:Int):Int = ContextCompat.getColor(this,colorResId)