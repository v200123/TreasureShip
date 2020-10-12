package com.jzz.treasureship.view

import android.Manifest
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jzz.treasureship.CoroutinesDispatcherProvider
import com.jzz.treasureship.R
import com.jzz.treasureship.adapter.CollectAdapter
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.model.repository.HomeRepository
import com.jzz.treasureship.ui.home.HomeViewModel
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.viewModelModule
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.dialog_home_like.view.*
import kotlinx.android.synthetic.main.dialog_upload_header_img.view.*
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.roundToInt

//修改头像
