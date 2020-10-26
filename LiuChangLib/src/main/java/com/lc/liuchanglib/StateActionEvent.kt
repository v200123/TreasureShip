package com.lc.mybaselibrary

/**
 *@date: 2020/9/10
 *@describe:
 *@Auth: 29579
 **/
//定义网络请求状态(密封类扩展性更好)
sealed class StateActionEvent()

class LoadState(val type:Int = 0) : StateActionEvent()

object SuccessState : StateActionEvent()

object NeedLoginState : StateActionEvent()

class ErrorState(val message: String?,val errorCode:Int = -1) : StateActionEvent()
