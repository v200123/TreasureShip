package com.jzz.treasureship.model.bean

data class QuestionnaireResponseVo(
    val questionnaire: Questionnaire?,
    val receiveDate: ReceiveDate?,
    val resultCode: Int?,
    val resultMsg: String?
)