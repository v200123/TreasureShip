package com.jzz.treasureship.ui.setting

import android.Manifest
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.azhon.appupdate.manager.DownloadManager
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.ToastUtils
App
BuildConfig
R
base.BaseVMFragment
ui.activity.MainActivity
ui.login.LoginActivity
ui.login.LoginViewModel
utils.DataCleanManagerUtils
utils.PreferenceUtils
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mine_setting.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class SettingFragment : BaseVMFragment<LoginViewModel>(), EasyPermissions.PermissionCallbacks {

//    private val mLoginFragment by lazy { LoginFragment.newInstance() }
    private val mAboutUsFragment by lazy { AboutUsFragment.newInstance() }
    private val mFeedbackFragment by lazy { FeedbackFragment.newInstance() }

    private var isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private var userJson by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    private var accessToken by PreferenceUtils(PreferenceUtils.ACCESS_TOKEN,"")
    private var wxCode by PreferenceUtils(PreferenceUtils.WX_CODE,"")
    private var wxCodeBind by PreferenceUtils(PreferenceUtils.WX_CODE_BIND,"")
    private var authShowSuccess by PreferenceUtils(PreferenceUtils.authShowSuccess, false)

    companion object {
        const val RC_CALL_PERM = 122

        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun getLayoutResId() = R.layout.fragment_mine_setting

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {
        activity!!.nav_view.visibility = View.GONE
        StateAppBar.setStatusBarLightMode(this.activity, context!!.resources.getColor(R.color.white))
        rlback.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
    }



        tv_servicePhone.setOnClickListener {

            callService(tv_servicePhone.text.toString())
        }

        tv_logout.setOnClickListener {
            if (isLogin) {
                XPopup.Builder(context).asConfirm("", "确认登出?") {
                    isLogin = false
                    userJson = ""
                    accessToken = ""
                    wxCode = ""
                    wxCodeBind = ""
                    authShowSuccess = false
                    ToastUtils.showShort("已退出系统")
//                    activity!!.supportFragmentManager.popBackStack()
                    (mContext as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .addToBackStack(SettingFragment.javaClass.name)
                        .hide(this).commit()
                    (mContext as MainActivity).switchToHome()
                    if (!JPushInterface.isPushStopped(App.CONTEXT)){
                        JPushInterface.stopPush(App.CONTEXT)
                    }
                }.show()
            } else {
                XPopup.Builder(context).asConfirm("", "当前未登录，需要跳转到登录界面进行登录吗？") {
                    context!!.startActivity(Intent(this.activity!!, LoginActivity::class.java))
                }.show()
            }
        }

        tv_cache.text = DataCleanManagerUtils.getTotalCacheSize(this.context)
        tv_title.text = "设置"
        layout_cleanCache.setOnClickListener {
            XPopup.Builder(it.context).asConfirm("清除缓存", "清除缓存会导致下载的内容删除，是否继续？") {
                DataCleanManagerUtils.clearAllCache(this.context)
                tv_cache.text = DataCleanManagerUtils.getTotalCacheSize(this.context)
            }.show()
        }

        tv_aboutus.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(SettingFragment.javaClass.name)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, mAboutUsFragment, mAboutUsFragment.javaClass.name)
                .commit()
        }

        tv_feedback.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(SettingFragment.javaClass.name)
                .hide(this)//隐藏当前Fragment
                .add(R.id.frame_content, mFeedbackFragment, mFeedbackFragment.javaClass.name)
                .commit()
        }

        tv_update.setOnClickListener {
            mViewModel.checkUpdate()
        }

    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel.apply {
            val xPopup = XPopup.Builder(context).asLoading("正在检测新版本。。。")
            updateState.observe(this@SettingFragment, Observer {
                if (it.showProgress) {
                    xPopup.show()
                }

                it.showSuccess?.let {
                    xPopup.dismiss()

                    if (BuildConfig.VERSION_CODE >= it.versionCode!!) {
                        ToastUtils.showShort("已是最新版本！")
                        return@Observer
                    }

                    if (it.flag == 0) {
                        XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false)
                            .asConfirm("检测到新版本", it.content, "暂不升级", "立即升级", {
                                val manager: DownloadManager = DownloadManager.getInstance(this@SettingFragment.context)
                                manager.setApkName("${BuildConfig.APPLICATION_ID}_${it.versionCode}.apk")
                                    .setApkUrl(it.url)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .download()
                            }, {}, true).show()
                    } else {
                        XPopup.Builder(context).asConfirm("检测到新版本", it.content, "暂不升级", "立即升级", {
                            val manager: DownloadManager = DownloadManager.getInstance(this@SettingFragment.context)
                            manager.setApkName("${BuildConfig.APPLICATION_ID}_${it.versionCode}.apk")
                                .setApkUrl(it.url)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .download()
                        }, {}, false).show()
                    }
                }

                it.showError?.let { err ->
                    ToastUtils.showShort(err)
                }
            })
        }
    }

    override fun initListener() {
    }

    @AfterPermissionGranted(RC_CALL_PERM)
    private fun callService(phoneNum: String) {
        if (EasyPermissions.hasPermissions(context!!, Manifest.permission.CALL_PHONE)) {
            PhoneUtils.call(phoneNum)
        } else {
            EasyPermissions.requestPermissions(
                this, "获取通话权限以拨打联系客服",
                RC_CALL_PERM, Manifest.permission.CALL_PHONE
            )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        ToastUtils.showShort("您拒绝了功能必需的权限申请，可在系统设置中打开后重试")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        callService(tv_servicePhone.text.toString())
    }


}
