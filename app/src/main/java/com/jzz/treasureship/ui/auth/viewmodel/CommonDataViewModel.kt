package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.jzz.treasureship.ui.auth.authRequestBody.ConfirmBody


/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class CommonDataViewModel : ViewModel() {
    val mConfirmBody: ConfirmBody = ConfirmBody()
    var occuID = ""

}
