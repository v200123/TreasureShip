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
import com.jzz.treasureship.utils.out
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.NeedLoginState
import com.lc.mybaselibrary.SuccessState
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.include_title.*
import kotlinx.coroutines.launch

abstract class BaseVMActivity<VM : BaseViewModel>(useDataBinding: Boolean = false) : AppCompatActivity() {
    private val mLoading by lazy {
        XPopup.Builder(mContext)
            .hasShadowBg(false)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asLoading()
    }

    private val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private val userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
//    认证的弹窗是否显示过
    private var authShowSuccess by PreferenceUtils(PreferenceUtils.authShowSuccess, false)

    //    每日弹窗的记录
    private var isInviteDialog by PreferenceUtils(PreferenceUtils.everyday_invite_dialog, "")
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
                mLoading.delayDismiss(100)
                ToastUtils.showShort(it.message)
            }
            if (it is NeedLoginState) {
                var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE, "")
                var userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
                var access by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN, "")
                var login by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
                isInviteDialog = ""
                authShowSuccess = false
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
        "我${this::class.java.name}\t\t进来了\n".out()

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
            if (user.auditStatus == 1 && user.firstPassTip == 1 &&!authShowSuccess) {
                authShowSuccess = true
                App.dialogHelp.showSuccess(user.nickName){

                }
                lifecycleScope.launch {
                    HttpHelp.getRetrofit().notificationServerPass(BaseRequestBody())
                }
            }



        }
    }

}
