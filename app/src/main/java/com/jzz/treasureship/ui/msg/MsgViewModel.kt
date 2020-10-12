package com.jzz.treasureship.ui.msg

import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.base.BaseViewModel
import com.jzz.treasureship.model.repository.MsgRepository

class MsgViewModel(val repository: MsgRepository, val provider: CoroutinesDispatcherProvider) : BaseViewModel()