package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.OccupationBean
import com.jzz.treasureship.model.bean.UploadImgBean
import com.jzz.treasureship.ui.auth.authRequestBody.getOccupationBody
import com.lc.mybaselibrary.ErrorState
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthUpLoadViewModel : BaseViewModel() {
    val occupationData = MutableLiveData<OccupationBean>()
    val ImageResultData = MutableLiveData<UploadImgBean>()

    /**
     * 获取认证的id
     */
    fun getOccupation( id:Int){
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

    fun uploadImg(image: File) {
            launchTask {
                runBlocking {
                    val requestFile = image.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("file", image.name, requestFile)
                    val uploadFile = HttpHelp.getRetrofit().uploadFile(filePart)
                    uploadFile.resultCheck({
                        mStateLiveData.postValue(ErrorState("上传失败，请重试"))
                    },{
                        ImageResultData.postValue(it)
                    })
                }

            }
    }


}
