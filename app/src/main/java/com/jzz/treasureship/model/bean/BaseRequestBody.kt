package com.jzz.treasureship.model.bean

import com.google.gson.annotations.SerializedName

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
open class body
open class header(val os:String = "android"
             ,val pageNum : Int= 1
             ,val pageSize:Int = 20)

