package com.jzz.treasureship.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.OccupationBean
import com.jzz.treasureship.model.bean.UploadImgBean
import com.jzz.treasureship.ui.auth.authRequestBody.getOccupationBody
import com.lc.mybaselibrary.ErrorState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 *@date: 2020/9/11
 *@describe:
 *@Auth: 29579
 **/
class AuthUpLoadViewModel : BaseViewModel() {
    val occupationData = MutableLiveData<OccupationBean>()
    val ImageResultData = MutableLiveData<UploadImgBean>()
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

                val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), image)
                val filePart = MultipartBody.Part.createFormData("file", image.name, requestFile)
                val uploadFile = HttpHelp.getRetrofit().uploadFile(filePart)
                uploadFile.resultCheck({
                    ImageResultData.postValue(it)
                },{
                    mStateLiveData.postValue(ErrorState("上传失败，请重试"))
                })
            }


    }


}
