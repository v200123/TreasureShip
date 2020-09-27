package com.jzz.treasureship.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.App
import com.jzz.treasureship.model.bean.User
import com.jzz.treasureship.utils.FragmentBackHandler
import com.jzz.treasureship.utils.PreferenceUtils
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.SuccessState
import com.lc.mybaselibrary.out
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.include_title.*
import java.util.*


abstract class BaseVMFragment<VM : BaseViewModel>(useDataBinding: Boolean = true) : Fragment(), FragmentBackHandler {
    private val mLoading by lazy {
        XPopup.Builder(mContext).dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asLoading()
    }
    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
    private val userInfo by PreferenceUtils(PreferenceUtils.USER_GSON, "")
    private var isAuthDialog by PreferenceUtils(PreferenceUtils.auth_is_show, "")

    protected lateinit var mViewModel: VM
    var mActivity: AppCompatActivity? = null
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        this.mActivity = context as AppCompatActivity
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
        "我${this::class.java.name}Fragment进入了".out()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+8"))
        if(userInfo != "") {
            val user = GsonUtils.fromJson(userInfo, User::class.java)
            if (user.auditStatus == -1) {
                if (isAuthDialog.isBlank()) {
                    App.dialogHelp.showType()
                    isAuthDialog = "${calendar.get(Calendar.MONTH)},${calendar.get(Calendar.DAY_OF_MONTH)}"
                } else {
                    val split = isAuthDialog.split(",")
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

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onBackPressed(): Boolean {
        return true
    }


}
