package com.jzz.treasureship

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.lxj.xpopup.interfaces.XPopupImageLoader
import java.io.File


/**
 *@date: 2020/9/14
 *@describe:
 *@Auth: 29579
 **/
 class ImageLoader : XPopupImageLoader {

    override fun loadImage(position: Int, uri: Any, imageView: ImageView) {
        Glide.with(imageView).load(uri).into(imageView)
    }

    override fun getImageFile(context: Context, uri: Any): File? {
        return null
    }
}
