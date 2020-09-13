package com.jzz.treasureship.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lc.mybaselibrary.LoadState
import com.lxj.xpopup.XPopup

abstract class BaseVMActivity<VM : BaseViewModel>(useDataBinding: Boolean = true) : AppCompatActivity() {

    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
    lateinit var mViewModel: VM
     var mContext: Context  = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = initVM()

        mViewModel.mStateLiveData.observe(this,{
            if(it is LoadState)
            {
                XPopup.Builder(mContext).dismissOnBackPressed(false).dismissOnTouchOutside(false)
                    .asLoading()
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

    open fun getLayoutResId(): Int = 0
    abstract fun initVM(): VM
    abstract fun initView()
    abstract fun initData()
    abstract fun startObserve()

}
