package com.jzz.treasureship.model.bean

data class Reward(
    val questionnaire: Questionnaire?,
    val receiveDate: ReceiveDate?,
    val redEnvelopeRecord: RedEnvelopeRecordVo?,
    val resultCode: Int?,
    val resultMsg: String?
)