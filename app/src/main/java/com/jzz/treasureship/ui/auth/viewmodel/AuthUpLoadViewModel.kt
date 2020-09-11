package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.OccupationBean
import com.jzz.treasureship.ui.auth.authRequestBody.getOccupationBody
import com.lc.mybaselibrary.ErrorState

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthUpLoadViewModel : BaseViewModel() {
    val occupationData = MutableLiveData<OccupationBean>()
    fun getOccupation( id:String){
        launchTask {
            val occupationType = HttpHelp.getRetrofit().getOccupationType(BaseRequestBody(getOccupationBody(id)))
            if(occupationType.success)
            {
                occupationData.postValue(occupationType.result)
            }else{
                mStateLiveData.postValue(ErrorState(occupationType.message))
            }
        }
    }



}
