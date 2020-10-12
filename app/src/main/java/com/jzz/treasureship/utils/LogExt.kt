package com.jzz.treasureship.utils

import android.util.Log
import com.orhanobut.logger.Logger


fun <T> T.out(isDebug: Boolean = false) {
    if (isDebug) {
        Logger.d(this.toString())
    } else {
        Logger.i(this.toString())
    }
}

fun <T> T.normalOut(isDebug: Boolean = false) {
    if (isDebug) {
        Log.d("LiuChang", this.toString())
    } else {
        Log.i("LiuChang", this.toString())
    }
}