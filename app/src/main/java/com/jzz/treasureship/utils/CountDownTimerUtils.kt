package com.jzz.treasureship.utils

import android.R
import android.graphics.Color
import android.os.CountDownTimer
import android.widget.TextView


class CountDownTimerUtils
/**
 * 传入参数为：倒计时控件，倒计时总时间（一般为六十秒，传：60000），倒计时单位（一般为一秒，传：1000）
 */(
    private val mTextView: TextView,
    millisInFuture: Long,
    countDownInterval: Long
) :
    CountDownTimer(millisInFuture, countDownInterval) {
    /**
     * 进入倒计时，设置样式
     */
    override fun onTick(millisUntilFinished: Long) {
        val text = millisUntilFinished / 1000
        mTextView.isClickable = false //设置不可点击
        mTextView.text = "$text s 后重新获取" //设置倒计时显示时间和文字
        mTextView.setTextColor(Color.parseColor("#FFA3A3A3")) //设置字体的颜色
    }

    /**
     * 倒计时完毕，设置样式
     */
    override fun onFinish() {
        mTextView.text = "获取验证码"
        mTextView.isClickable = true //重新获得点击
    }

}