package com.jzz.treasureship.model.bean
import com.google.gson.annotations.SerializedName


class FirOrderRedBean(
    @SerializedName("amount")
    var mAmount: Float = 0f, // 0
    @SerializedName("count")
    var mCount: Int = 0, // 0
    @SerializedName("inviteRewardCount")
    var mInviteRewardCount: Int = 0, // 0
    @SerializedName("status")
    var mStatus: Int = 0, // 0
@SerializedName("inviteRewardAmount")
var mInviteRewardAmount :String = ""
)