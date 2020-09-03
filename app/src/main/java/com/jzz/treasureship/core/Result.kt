package com.jzz.treasureship.core

sealed class Result<out T : Any> {

    data class Success<out T : Any>(val result: T?) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$result]"
            is Error -> "Error[exception=$exception]"
        }
    }
}