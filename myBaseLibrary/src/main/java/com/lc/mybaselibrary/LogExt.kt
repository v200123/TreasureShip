package com.lc.mybaselibrary

import android.util.Log

/**
 *@date: 2020/9/9
 *@describe: 用于快速输出log
 *@Auth: 29579
 **/
fun  String.out(isDebug:Boolean = false){
    if(isDebug)
    {
        Log.d("LiuChang", this )
    }
    else{
        Log.i("LiuChang", this)
    }
}
