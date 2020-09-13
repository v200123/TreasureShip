package com.jzz.treasureship.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.jzz.treasureship.utils.BackHandlerHelper
import com.jzz.treasureship.utils.FragmentBackHandler
import com.lc.mybaselibrary.LoadState
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager


abstract class BaseVMFragment<VM : BaseViewModel>(useDataBinding: Boolean = true) : Fragment(), FragmentBackHandler {

    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
    protected lateinit var mViewModel: VM
    var mActivity: Activity? = null
    lateinit var mContext:Context

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
        initView()
        initData()
        startObserve()
        initListener()

        mViewModel.mStateLiveData.observe(this,{
            if(it is LoadState)
            {
                XPopup.Builder(mContext).dismissOnBackPressed(false).dismissOnTouchOutside(false)
                    .asLoading().show()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onBackPressed(): Boolean {
        return BackHandlerHelper.handleBackPress(this)
    }
}
