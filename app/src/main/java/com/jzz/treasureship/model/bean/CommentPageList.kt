package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName

//评论页
data class CommentPageList(
    val `data`: List<CommentData>,
    val totalElements: Int,
    val totalPages: Int
)

data class CommentData(
    val comments: ArrayList<CommentData>,
    val content: String,
    val createTime: String,
    val id: Int,
    val isLike:Int,
    val likeCount: Int,
    @SerializedName(value = "nikeName")
    val nickName: String,
    val status: Int,
    val userAvatar: String,
    val userId: Int,
    val userType: Int
)