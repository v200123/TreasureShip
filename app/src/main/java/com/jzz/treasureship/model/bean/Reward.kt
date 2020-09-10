package com.jzz.treasureship.model.bean

data class Reward(
    val questionnaire: Questionnaire?,
    val receiveDate: ReceiveDate?,
    val redEnvelopeRecord: RedEnvelopeRecordVo?,
    val resultCode: Int?,
    val resultMsg: String?
) {
    data class RedEnvelopeRecordVo(
        val amount: Double?,
        val amountFen: Double?,
        val id: Int?,
        val redEnvelopeId: Int?
    )
    data class ReceiveDate(
        val endDateTimeInMillis: Long,
        val startDateTimeInMillis: Long
    )
}




