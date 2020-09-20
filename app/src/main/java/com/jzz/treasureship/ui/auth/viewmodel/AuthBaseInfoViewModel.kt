package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.core.Result
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.CityPlaces
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ErrorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthBaseInfoViewModel : BaseViewModel() {
    val CityPlacesLiveData = MutableLiveData<CityPlaces>()

    fun getCityPlaces() {
        launchTask {
            val result = HttpHelp.getRetrofit().getCityPlaces02(BaseRequestBody())
            if(result.success)
            {
                CityPlacesLiveData.value = result.result
            }
            else{
                when (result.code) {
                    401 -> {
                        var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                        var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                        var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                        var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                        login = false
                        wxCode = ""
                        userInfo = ""
                        access = ""
                        mStateLiveData.postValue(ErrorState(result.message))
//                        emitCityPlacesState(false, "失败!${result.result?.message}", null, false, false, true)
                    }
                    else -> {
                        mStateLiveData.postValue(ErrorState(result.message))
                    }
                }
            }
//            if (result is Result.Success) {
//                if (result.result?.code == 200) {
//                    emitCityPlacesState(false, null, result.result.result)
//                } else {
//                    when (result.result?.code) {
//                        401 -> {
//                            var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
//                            var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
//                            var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
//                            var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
//                            login = false
//                            wxCode = ""
//                            userInfo = ""
//                            access = ""
//                            emitCityPlacesState(false, "失败!${result.result?.message}", null, false, false, true)
//                        }
//                        else -> {
//                            emitCityPlacesState(false, "失败!${result.result?.message}")
//                        }
//                    }
//                }
//            } else {
//                emitCityPlacesState(false, "获取城市列表失败")
//            }
        }
    }

}
