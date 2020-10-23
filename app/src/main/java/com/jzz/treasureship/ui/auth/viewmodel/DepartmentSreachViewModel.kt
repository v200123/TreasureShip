package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.DepartmentBean
import com.lc.mybaselibrary.ErrorState

/**
 *@date: 2020/9/14
 *@describe:
 *@Auth: 29579
 **/
class DepartmentSreachViewModel : BaseViewModel() {
    val hospitalData = MutableLiveData<DepartmentBean>()
    fun getHospital() {
        launchTask {
            val hospitalType = HttpHelp.getRetrofit().getHospitalType(BaseRequestBody())
            hospitalType.resultCheck({
                mStateLiveData.postValue(ErrorState(it))
            }) {
                hospitalData.postValue(it)
            }
        }
    }


}
