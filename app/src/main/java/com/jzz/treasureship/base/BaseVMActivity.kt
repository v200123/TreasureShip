package com.jzz.treasureship.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.ui.login.LoginActivity
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.NeedLoginState
import com.lc.mybaselibrary.SuccessState
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.coroutines.launch
import java.util.*

abstract class BaseVMActivity<VM : BaseViewModel>(useDataBinding: Boolean = true) : AppCompatActivity() {
    private val mLoading by lazy {
        XPopup.Builder(mContext).dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asLoading()
    }
    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private val userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")

    //    每日弹窗的记录
    private var isInviteDialog by PreferenceUtils(PreferenceUtils.everyday_invite_dialog, "")
    private var isAuthDialog by PreferenceUtils(PreferenceUtils.auth_is_show, "")
    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
    lateinit var mViewModel: VM
    var mContext: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = initVM()

        try {
            rlback.setOnClickListener { finish() }
        } catch (e: Exception) {

        }
        mViewModel.mStateLiveData.observe(this, {
            if (it is LoadState) {
                mLoading.show()
            }
            if (it is SuccessState) {
                mLoading.delayDismiss(100)
            }
            if (it is ErrorState) {
                ToastUtils.showShort(it.message)
            }
            if (it is NeedLoginState) {
                var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                login = false
                wxCode = ""
                userInfo = ""
                access = ""
                startActivity(Intent(this, LoginActivity::class.java))
            }
        })
        startObserve()
        if (_useBinding) {
            mBinding = DataBindingUtil.setContentView<ViewDataBinding>(this, getLayoutResId())
            mBinding.lifecycleOwner = this
        } else {
            setContentView(getLayoutResId())
        }

        initView()
        initData()
    }

    override fun onResume() {
        super.onResume()
        showAuthDialog()
    }

    open fun getLayoutResId(): Int = 0
    abstract fun initVM(): VM
    abstract fun initView()
    abstract fun initData()
    abstract fun startObserve()

    //显示认证的dialog
    private fun showAuthDialog() {
        if (isLogin) {
//未认证
            val user = GsonUtils.fromJson(userInfo, User::class.java)
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+8"))
            if (!PreferenceUtils("", "").contains(PreferenceUtils.everyday_invite_dialog)) {
                if (isInviteDialog.isBlank()) {
                    App.dialogHelp.showInvite()
                    isInviteDialog = "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                } else {

                    val split = isInviteDialog.split(",")
                    if (split[0].toInt() != calendar.get(Calendar.MONTH) && split[0].toInt() != calendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ) {
                        App.dialogHelp.showInvite()
                    }
                    isInviteDialog = "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                }
            }

            if (user.auditStatus == 1 && user.firstPassTip == 1) {
                App.dialogHelp.showSuccess(user.nickName)
                lifecycleScope.launch {
                    HttpHelp.getRetrofit().notificationServerPass(BaseRequestBody())
                }
            }

            if(user.auditStatus == -1)
            {
                if(!PreferenceUtils("", "").contains(PreferenceUtils.auth_is_show))
                {
                    App.dialogHelp.showType()
                    isAuthDialog = "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                }
                else{
                    val split = isInviteDialog.split(",")
                    if (split[0].toInt() != calendar.get(Calendar.MONTH) && split[0].toInt() != calendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ) {
                        App.dialogHelp.showType()
                    }
                    isAuthDialog = "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                }

            }

        }
    }
}
