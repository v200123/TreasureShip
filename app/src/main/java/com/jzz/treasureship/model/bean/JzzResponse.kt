package com.jzz.treasureship.model.bean

data class JzzResponse<out T>(val code: Int,
                              val message: String,
                              val result: T?,
                              val success:Boolean,
                              val timestamp:Long)