package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

/**
 *@date: 2020/9/10
 *@describe: 该类是为了封装所有的body中的请求
 *@Auth: 29579
 **/
class BaseRequestBody(MyBody:body = body(),MyHeader:header = header())  {
    @SerializedName("body")
 var mBody:body = MyBody
    @SerializedName("header")
var mHead = MyHeader
}
class body()
class header(val os:String = "android"
             ,val pageNum : Int= 1
             ,val pageSize:Int = 20)

