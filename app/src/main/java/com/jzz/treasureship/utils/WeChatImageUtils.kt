package com.jzz.treasureship.utils

import android.graphics.Bitmap
import com.blankj.utilcode.util.ImageUtils

/**
 *@date: 2020/9/8
 *@describe:
 *@Auth: 29579
 **/
fun Bitmap.changeImage() = ImageUtils.bitmap2Bytes(this, Bitmap.CompressFormat.PNG)
