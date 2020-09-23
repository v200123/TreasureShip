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
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.utils.BackHandlerHelper
import com.jzz.treasureship.utils.FragmentBackHandler
import com.lc.mybaselibrary.ErrorState
import com.lc.mybaselibrary.LoadState
import com.lc.mybaselibrary.SuccessState
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.include_title.*


abstract class BaseVMFragment<VM : BaseViewModel>(useDataBinding: Boolean = true) : Fragment(), FragmentBackHandler {
    private val mLoading by lazy {
        XPopup.Builder(mContext).dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .asLoading()
    }
    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
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
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    override fun onBackPressed(): Boolean {
        return BackHandlerHelper.handleBackPress(this)
    }


}
