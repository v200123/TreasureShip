package com.jzz.treasureship.model.bean

data class VideoPageList(
    val `data`: List<VideoData>,
    val totalElements: Int,
    val totalPages: Int
)