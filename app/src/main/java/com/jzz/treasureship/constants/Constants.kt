package com.jzz.treasureship.constants


object Constants {
    //测试环境
    const val BASE_URL = BuildConfig.BaseUrl

    //生产环境
//    const val BASE_URL = "http://bj.jzzchina.com/"
    //微信登录、分享的app id
    const val WX_APP_ID: String = "wxd21c22338d2a2151"
    //微信登录、分享的app secret
    const val WX_APP_SECRET: String = "96d4451a6464ad132557e09b8b46bef8"

    // Name of Notification Channel for verbose notifications of background work
    @JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose WorkManager Notifications"
    const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
    @JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val NOTIFICATION_ID = 1

    // The name of the image manipulation work
    const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

    // Other keys
    const val OUTPUT_PATH = "blur_filter_outputs"
    const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
    const val TAG_OUTPUT = "OUTPUT"

    const val DELAY_TIME_MILLIS: Long = 3000
}
