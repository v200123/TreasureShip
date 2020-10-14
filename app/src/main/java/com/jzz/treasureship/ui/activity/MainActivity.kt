package com.jzz.treasureship.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.jpush.android.api.JPushInterface
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import cn.ycbjie.ycstatusbarlib.utils.StatusBarUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.BuildConfig
import com.jzz.treasureship.R
import com.jzz.treasureship.base.BaseVMActivity
import com.jzz.treasureship.model.api.HttpHelp
import com.jzz.treasureship.model.bean.BaseRequestBody
import com.jzz.treasureship.ui.addressbook.AddressBookFragment
import com.jzz.treasureship.ui.goods.GoodsDetailFragment
import com.jzz.treasureship.ui.invite.InviteFragment
import com.jzz.treasureship.ui.login.LoginViewModel
import com.jzz.treasureship.ui.orders.OrdersFragment
import com.jzz.treasureship.ui.treasurebox.TreasureBoxFragment
import com.jzz.treasureship.ui.usersetting.UserSettingFragment
import com.jzz.treasureship.utils.BackHandlerHelper
import com.jzz.treasureship.utils.HProgressDialogUtils
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.out
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate._XUpdate
import com.xuexiang.xupdate.service.OnFileDownloadListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File
import kotlin.math.roundToInt


class MainActivity : BaseVMActivity<LoginViewModel>() {

    val isLogin by PreferenceUtils(PreferenceUtils.IS_LOGIN, false)
    private var curFragment by PreferenceUtils(PreferenceUtils.CUR_FRAGMENT, "")
    private var mCurrentFragment = Fragment()
    private val mPopStatus by viewModels<DialogStatusViewModel>()
    private var authShowSuccess by PreferenceUtils(PreferenceUtils.authShowSuccess, false)
    val mMainHomeFragemnt = MainHomeFragment()
    //    private val mHomeFragment by lazy { HomeFragment.newInstance() }
    private val mTsbFragment by lazy { TreasureBoxFragment.newInstance() }
    private val mAddressBookFragment by lazy { AddressBookFragment.newInstance() }
    private val mMineFragment by lazy { UserSettingFragment.newInstance() }

    override fun getLayoutResId() = R.layout.activity_main

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extra = intent?.getStringExtra(KEY_EXTRAS)
            Log.d("sendBroadcast", extra)
            val json = JSONObject(extra)
            when (json.get("msgType")) {
                1, "1" -> {
                    //答题抢红包
                }
                2, "2" -> {
                    //刷新用户信息
                    mViewModel.getUserInfo()
                    if (mCurrentFragment is UserSettingFragment) {
                        (mCurrentFragment as UserSettingFragment).refreshUser()
                    }
                }
            }
        }
    }

    /**
     * 上次点击返回键的时间
     */
    private var lastBackPressTime = -1L

    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }

//        if (!BackHandlerHelper.handleBackPress(this)) {
//            super.onBackPressed()
//        }
//        else

        if (BackHandlerHelper.handleBackPress(this)) {
            supportFragmentManager.popBackStack()
        } else
            if (supportFragmentManager.backStackEntryCount == 0) {
                val currentTIme = System.currentTimeMillis()
                if (lastBackPressTime == -1L || currentTIme - lastBackPressTime >= 2000) {
                    // 显示提示信息
                    ToastUtils.showShort("再按一次返回键退出宝舰")
                    // 记录时间
                    lastBackPressTime = currentTIme
                } else {
                    finish()
                }
            } else {
                super.onBackPressed()
            }
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        //super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun initVM(): LoginViewModel = getViewModel()

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(MESSAGE_RECEIVED_ACTION))
//        用于获取当前设置的Alias
        if (JPushInterface.isPushStopped(this)) {
            JPushInterface.resumePush(this)
        }
        JPushInterface.getAlias(mContext, 0x3)
        if (isLogin && !mPopStatus.isOpen) {
            mViewModel.getUserInfo()
        }
    }

    override fun onRestart() {
        super.onRestart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(MESSAGE_RECEIVED_ACTION))
    }

    override fun initView() {

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(MESSAGE_RECEIVED_ACTION))
        isForeground = true

//        //设置状态栏文字颜色及图标为深色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
        StatusBarUtils.StatusBarLightMode(this)

        supportFragmentManager.commit {
            add(R.id.frame_content,mMainHomeFragemnt,"MainHomeFragment")
        }


//        nav_view.setOnNavigationItemSelectedListener { item: MenuItem ->
//            if (!isLogin) {
//                switchToLogin()
//                return@setOnNavigationItemSelectedListener false
//            } else {
//                return@setOnNavigationItemSelectedListener when (item.itemId) {
//                    R.id.navigation_home -> {
//                        item.setIcon(R.drawable.home_nav_home_icon_selected)
//                        switchToHome()
//                        curFragment = "HomeFragment"
//                        true
//                    }
//                    R.id.navigation_treasure_box -> {
//                        item.setIcon(R.drawable.icon_treasure_box_selected)
//                        switchToTsb()
//                        curFragment = "TreasureBoxFragment"
//                        true
//                    }
//                    R.id.navigation_address_book -> {
//                        curFragment = "AddressBookFragment"
//                        item.setIcon(R.drawable.icon_addressbook_selected)
//                        switchToAddressBook()
//                        true
//                    }
//                    R.id.navigation_user_settings -> {
//                        curFragment = "UserSettingFragment"
//                        item.setIcon(R.drawable.icon_mine_selected)
//                        switchToMine()
//                        true
//                    }
//                    else -> {
//                        false
//                    }
//                }
//            }
//        }

        intent?.let {
            val goodId = it.getStringExtra(GoodsId)
            val gotoInvite = it.getBooleanExtra(gotoInvite, false)
            val gotoWhere = intent.getStringExtra("goTo")
//            val stringExtra = intent.getStringExtra(GoodsId)

            if (goodId != null) {
                supportFragmentManager.beginTransaction()
                    .addToBackStack("")
                    .replace(
                        R.id.frame_content, GoodsDetailFragment.newInstance
                            (goodId)
                    ).commit()
            }
            if (gotoInvite) {
                supportFragmentManager.beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.frame_content, InviteFragment.newInstance(true))
                    .commit()
            }

            if (gotoWhere == "orders") {
                switchFragment(
                    OrdersFragment.newInstance("HomeFragment"),
                    OrdersFragment.javaClass.name
                )
            }

        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        switchToHome()
        intent?.let {
            val stringExtra = it.getStringExtra(GoodsId)
            val gotoInvite = it.getBooleanExtra(gotoInvite, false)
            val gotoWhere = intent.getStringExtra("goTo")
            if (stringExtra != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.frame_content, GoodsDetailFragment.newInstance
                        (stringExtra), GoodsDetailFragment.javaClass.name
                ).commit()
            }
            if (gotoInvite) {
                supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(
                        R.id.frame_content,
                        InviteFragment.newInstance(true),
                        InviteFragment.javaClass.name
                    )
                    .commit()
            }
            if (gotoWhere == "orders") {
                switchFragment(
                    OrdersFragment.newInstance("HomeFragment"),
                    OrdersFragment.javaClass.name
                )
            }
        }


    }

//    private fun switchToMine() {
//        resetToDefaultIcon()
//        val mineItem: MenuItem = nav_view.menu.findItem(R.id.navigation_user_settings)
//        mineItem.setIcon(R.drawable.icon_mine_selected)
//        mineItem.setChecked(true)
//        switchFragment(mMineFragment, UserSettingFragment.javaClass.name)
//    }

//    private fun switchToLogin() {
//        startActivity(Intent(this, LoginActivity::class.java))
//    }

//    private fun switchToAddressBook() {
//        resetToDefaultIcon()
//        val addressBookItem: MenuItem = nav_view.menu.findItem(R.id.navigation_address_book)
//        addressBookItem.setIcon(R.drawable.icon_addressbook_selected)
//        addressBookItem.setChecked(true)
//        switchFragment(mAddressBookFragment, AddressBookFragment.javaClass.name)
//    }

//    private fun switchToTsb() {
//        resetToDefaultIcon()
//        val tsbItem: MenuItem = nav_view.menu.findItem(R.id.navigation_treasure_box)
//        tsbItem.setIcon(R.drawable.icon_treasure_box_selected)
//        tsbItem.setChecked(true)
//        switchFragment(mTsbFragment, TreasureBoxFragment.javaClass.name)
//    }

//    public fun switchToHome() {
//        val homeItem: MenuItem = nav_view.menu.findItem(R.id.navigation_home)
//        homeItem.setIcon(R.drawable.home_nav_home_icon_selected)
//        homeItem.setChecked(true)
//        switchFragment(HomeFragment.newInstance(), HomeFragment.javaClass.name)
//    }

    private fun switchFragment(targetFragment: Fragment, tag: String) {
        val currentFragmentStr = mCurrentFragment.javaClass.name
        val targetFragmentStr = targetFragment.javaClass.name
        Log.e("currentFragmentStr", currentFragmentStr)
        Log.e("targetFragmentStr", targetFragmentStr)
        val result = currentFragmentStr.equals(targetFragmentStr)

        if (result) {
            return
        }

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        //如果要显示的targetFragment没有添加过
//        if (!targetFragment.isAdded and (null == supportFragmentManager.findFragmentByTag(tag))) {
        supportFragmentManager.executePendingTransactions()
        if ((!targetFragment.isAdded)) {
            transaction
                .hide(mCurrentFragment)//隐藏当前Fragment
                .add(R.id.frame_content, targetFragment, targetFragment.javaClass.name)
                .commit()
        } else {
            transaction.hide(mCurrentFragment).show(targetFragment).commit()
        }
        mCurrentFragment = targetFragment
    }

//    private fun resetToDefaultIcon() {
//        val homeItem: MenuItem = nav_view.menu.findItem(R.id.navigation_home)
//        val tsbItem: MenuItem = nav_view.menu.findItem(R.id.navigation_treasure_box)
//        val addressBookItem: MenuItem = nav_view.menu.findItem(R.id.navigation_address_book)
//        val mineItem: MenuItem = nav_view.menu.findItem(R.id.navigation_user_settings)
//        homeItem.setIcon(R.drawable.home_nav_home_icon_normal)
//        homeItem.setChecked(false)
//        tsbItem.setIcon(R.drawable.icon_treasure_box_normal)
//        tsbItem.setChecked(false)
//        addressBookItem.setIcon(R.drawable.icon_addressbook_normal)
//        addressBookItem.setChecked(false)
//        mineItem.setIcon(R.drawable.icon_mine_normal)
//        mineItem.setChecked(false)
//    }


    override fun initData() {
        Handler().postDelayed(
            {
                mViewModel.checkUpdate()
            }, 3 * 1000
        )
    }

    override fun startObserve() {
        mViewModel.apply {
            userState.observe(this@MainActivity, Observer {
                it.showSuccess?.let {
                    JPushInterface.setAlias(applicationContext, 1001, it.id.toString())

                    if (it.auditStatus == 1 && it.firstPassTip == 1 && !authShowSuccess) {
                        authShowSuccess = true
                        App.dialogHelp.showSuccess(it.nickName) {

                        }
                        lifecycleScope.launch {
                            HttpHelp.getRetrofit().notificationServerPass(BaseRequestBody())
                        }
                    }

                    Log.e("userInfo", "refresh user info success:$it")
                    "当前的用户登录的accessToken为:${it.accessToken}".out()
                }

                it.showError?.let { err ->
                    Log.e("userInfo", err)
                }
            })

            updateState.observe(this@MainActivity, Observer {
                it.showSuccess?.let {
                    if (BuildConfig.VERSION_CODE >= it.versionCode!!) {
                        return@Observer
                    }

                    if (it.flag == 0) {
                        XPopup.Builder(this@MainActivity).dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false)
                            .asConfirm("检测到新版本", it.content, "暂不升级", "立即升级", {
                                XUpdate.newBuild(this@MainActivity)
                                    .build()
                                    .download(it.url, object : OnFileDownloadListener {
                                        //设置下载的地址和下载的监听
                                        override fun onStart() {
                                            HProgressDialogUtils.showHorizontalProgressDialog(
                                                this@MainActivity,
                                                "下载进度",
                                                false
                                            )
                                        }

                                        override fun onProgress(progress: Float, total: Long) {
                                            HProgressDialogUtils.setProgress((progress * 100).roundToInt())
                                        }

                                        override fun onCompleted(file: File): Boolean {
                                            HProgressDialogUtils.cancel()
                                            _XUpdate.startInstallApk(this@MainActivity, file)
                                            return false
                                        }

                                        override fun onError(throwable: Throwable?) {
                                            HProgressDialogUtils.cancel()
                                        }
                                    })
                            }, {}, true).show()
                    } else {
                        XPopup.Builder(this@MainActivity)
                            .asConfirm("检测到新版本", it.content, "暂不升级", "立即升级", {
                                XUpdate.newBuild(this@MainActivity)
                                    .build()
                                    .download(it.url, object : OnFileDownloadListener {
                                        //设置下载的地址和下载的监听
                                        override fun onStart() {
                                            HProgressDialogUtils.showHorizontalProgressDialog(
                                                this@MainActivity,
                                                "下载进度",
                                                false
                                            )
                                        }

                                        override fun onProgress(progress: Float, total: Long) {
                                            HProgressDialogUtils.setProgress((progress * 100).roundToInt())
                                        }

                                        override fun onCompleted(file: File): Boolean {
                                            HProgressDialogUtils.cancel()
                                            _XUpdate.startInstallApk(
                                                this@MainActivity,
                                                file
                                            ) //填写文件所在的路径
                                            return false
                                        }

                                        override fun onError(throwable: Throwable?) {
                                            HProgressDialogUtils.cancel()
                                        }
                                    })
                            }, {}, false).show()
                    }
                }

                it.showError?.let { err ->
                    Log.e("updateError", err)
                }
            })
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        var isForeground: Boolean = false
        const val GoodsId = "com.goodsId"
        const val gotoInvite = "com.invite"
        const val MESSAGE_RECEIVED_ACTION = "com.jzz.treasureship.MESSAGE_RECEIVED_ACTION"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_EXTRAS = "extras"
    }

    override fun onStop() {
        super.onStop()
        isForeground = false
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }


}
