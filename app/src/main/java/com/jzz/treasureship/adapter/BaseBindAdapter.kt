package com.jzz.treasureship.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * Created by luyao
 * on 2019/10/25 13:16
 */
open class BaseBindAdapter<T>(layoutResId: Int, br: Int? = null) : BaseQuickAdapter<T, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: T) {

    }

}