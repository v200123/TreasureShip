package com.jzz.treasureship

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.tencent.mmkv.MMKV

/**
 *@PackageName: com.jzz.treasureship
 *@Auth： 29579
 *@Description: 哆啦A梦工具箱的增加功能---切换正式和测试环境  **/
class MyDoraemoKit : AbstractKit() {
    companion object {

        const val Kit_Name = "com.lc.doraemo.kit"
        const val Kit_Key = "com.lc.doraemo.IsTest"

    }

    private val mDeposit by lazy { MMKV.mmkvWithID(Kit_Name) }

    override val icon: Int
        get() = R.mipmap.ic_launcher
    override val name: Int
        get() = R.string.app_name

    override fun onAppInit(context: Context?) {

    }

    override fun onClick(context: Context?) {
        val decodeBool = mDeposit.decodeBool(Kit_Key)
        if (decodeBool) {
           ToastUtils.showShort("切换到测试环境")
        } else {
            ToastUtils.showShort("切换到正式环境")
        }
        mDeposit.encode(Kit_Key, !decodeBool)
    }


}