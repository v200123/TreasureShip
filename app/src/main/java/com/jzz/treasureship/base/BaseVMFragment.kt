package com.jzz.treasureship.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import cn.ycbjie.ycstatusbarlib.bar.StateAppBar
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.model.bean.UserDialogInformationBean
import com.jzz.treasureship.utils.FragmentBackHandler
import com.jzz.treasureship.utils.PreferenceUtils
import com.jzz.treasureship.utils.normalOut
import com.jzz.treasureship.utils.out
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.SuccessState
import com.lc.mybaselibrary.ext.getResColor

import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.include_title.*
import java.util.*


abstract class BaseVMFragment<VM : BaseViewModel>(useDataBinding: Boolean = true) : Fragment(),
    FragmentBackHandler {
    private val mLoading by lazy {
        XPopup.Builder(mContext)
            .hasShadowBg(false)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asLoading()
    }
    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
    private val userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
   @ColorRes
   open var mStatusColor:Int = R.color.white

//    var user = GsonUtils.fromJson(userInfo, User::class.java)
//    private var isAuthDialog by PreferenceUtils(PreferenceUtils.auth_is_show, "")

    protected lateinit var mViewModel: VM
    var mActivity: AppCompatActivity? = null
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        this.mActivity = context as AppCompatActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (_useBinding) {
            mBinding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
            mBinding.root
        } else {
            inflater.inflate(getLayoutResId(), container, false)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = initVM()
        try {
            rlback.setOnClickListener { mActivity!!.onBackPressed() }
        } catch (e: Exception) {

        }
        initView()
        initData()
        startObserve()
        initListener()
        mViewModel.mStateLiveData.observe(viewLifecycleOwner, {
            if (it is LoadState) {
                mLoading.show()
            }
            if (it is SuccessState) {
                mLoading.dismiss()
            }
            if (it is ErrorState) {
                mLoading.delayDismiss(100)
                ToastUtils.showShort(it.message)
            }
        })

    }

    abstract fun getLayoutResId(): Int
    abstract fun initVM(): VM
    abstract fun initView()
    abstract fun initData()
    abstract fun startObserve()
    abstract fun initListener()

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        "我${this::class.java.name}\t\tFragment进入到onHiddenChanged了\n ".out()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
        setStatusColor(mStatusColor)
        "我${this::class.java.name}\t进入到onResume了\n".out()
        "当前的FragmentManager中有以下Fragment：｛".normalOut(true)
        (mContext as AppCompatActivity).supportFragmentManager.fragments.forEach {

            it.javaClass.simpleName.normalOut(true)

        }
        "}".normalOut(true)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+8"))
//        判断当天是否显示了未认证的弹窗
        if (userInfo != "") {
           val user = GsonUtils.fromJson(userInfo, User::class.java)
            val mUserDialogShow =
                MMKV.defaultMMKV()
                    .decodeParcelable(user.id.toString(), UserDialogInformationBean::class.java)

            if (user.auditStatus == -1) {
                if (mUserDialogShow.ShowNoAuthDialogDate.isBlank()) {
                    App.dialogHelp.showNoAuth()
                    MMKV.defaultMMKV().encode(
                        user.id.toString(),
                        mUserDialogShow.apply {
                            ShowNoAuthDialogDate =
                                "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                        })
                } else {
                    val split = mUserDialogShow.ShowNoAuthDialogDate.split(",")
                    if (split[0].toInt() != calendar.get(Calendar.MONTH) && split[0].toInt() != calendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ) {
                        App.dialogHelp.showNoAuth()
                    }
                    MMKV.defaultMMKV().encode(
                        user.id.toString(),
                        mUserDialogShow.apply {
                            ShowNoAuthDialogDate =
                                "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                        })
                }

            }
        }

    }

    fun showLoading(msg:String? = null){
        mLoading.setTitle(msg?:"").show()
    }

    fun hideLoading(){
        mLoading.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    private fun setStatusColor(@ColorRes colorRes:Int){
        StateAppBar.setStatusBarLightMode(mContext as AppCompatActivity, mContext.getResColor(colorRes))
    }

}
