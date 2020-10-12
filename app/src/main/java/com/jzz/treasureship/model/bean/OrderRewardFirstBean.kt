package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


/**
 *@date: 2020/9/17
 *@describe:
 *@Auth: 29579
 **/
class OrderRewardFirstBean(
    @SerializedName("amount")
    var mAmount: Int = 0, // 0
    @SerializedName("count")
    var mCount: Int = 0, // 0
    @SerializedName("inviteRewardCount")
    var mInviteRewardCount: Int = 0, // 0
    @SerializedName("status")
    var mStatus: Int = 0, // 0
    @SerializedName("inviteRewardAmount")
    var mInviteRewardAmount:String = ""
)