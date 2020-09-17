package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.Qualification
import com.jzz.treasureship.ui.auth.authRequestBody.ConfirmBody
import com.lc.mybaselibrary.ErrorState

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthInforViewModel : BaseViewModel() {
    val qualLiveData = MutableLiveData<Qualification>()
    fun confirm(confirmBody: ConfirmBody){
        launchTask {
            val uploadInformation =
                HttpHelp.getRetrofit().uploadInformation(BaseRequestBody((confirmBody)))
            uploadInformation.resultCheck({
                qualLiveData.postValue(it)
            },{
                mStateLiveData.postValue(ErrorState(it))
            })
        }
    }

}
