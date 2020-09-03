package com.jzz.treasureship.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.jzz.treasureship.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import kotlinx.android.synthetic.main.video_layout_normal.view.*


class CustomVideoPlayer : StandardGSYVideoPlayer {
    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag) {}
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun getLayoutId(): Int {
        return R.layout.video_layout_normal
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView: ImageView = mStartButton as ImageView
            if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.custom_video_pause_selector)
            } else if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.custom_video_play_selector)
            } else {
                imageView.setImageResource(R.drawable.custom_video_play_selector)
                layout_bottom.visibility = View.GONE
            }
        }
    }


}