package com.jzz.treasureship.utils

import android.content.Context
import android.util.TypedValue

/**
 * 像素转换工具类.
 */
object DensityUtil {
    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpVal,
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spVal,
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    fun px2dp(context: Context, pxVal: Float): Float {
        val scale: Float = context.getResources().getDisplayMetrics().density
        return pxVal / scale
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @param pxVal
     * @return
     */
    fun px2sp(context: Context, pxVal: Float): Float {
        return pxVal / context.getResources().getDisplayMetrics().scaledDensity
    }
}