package com.jzz.treasureship.model.bean

data class HotSearch(
    val commentCount: Int,
    val duration: Int,
    val goodsId: String,
    val haveGoods: Int,
    val height: Int,
    val id: Int,
    val keywords: String,
    val like: Int,
    val likeCount: Int,
    val shareCount: Int,
    val shopName: String,
    val subscriptImgUrl: String,
    val videoCoverUrl: String,
    val videoDesc: String,
    val videoName: String,
    val videoUrl: String,
    val width: Int
)