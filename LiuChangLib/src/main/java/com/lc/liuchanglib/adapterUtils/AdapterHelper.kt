package com.lc.liuchanglib.adapterUtils

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

object AdapterHelper {

    fun <ITEM> getAdapter(@LayoutRes itemLayout:Int,
                          converter: ViewHolderConverter<ITEM>,
                          data: MutableList<ITEM>): Adapter<ITEM>
            = Adapter(itemLayout, converter, data)

    interface ViewHolderConverter<ITEM> {
        fun convert(helper: BaseViewHolder, item: ITEM)
    }

    class Adapter<ITEM>(
        @LayoutRes private val layout: Int,
        private val converter: ViewHolderConverter<ITEM>,
        val list: MutableList<ITEM>
    ): BaseQuickAdapter<ITEM, BaseViewHolder>(layout, list) {
        override fun convert(helper: BaseViewHolder, item: ITEM) {
            converter.convert(helper, item)
        }
    }
}

// 一个自定义的方法
fun BaseViewHolder.goneIf(@IdRes id: Int, goneIf: Boolean) {
    this.getView<View>(id).visibility = if (goneIf) View.GONE else View.VISIBLE
}
fun BaseViewHolder.setChecked(@IdRes id: Int, isCheck: Boolean) {
    this.getView<CheckBox>(id).isChecked = isCheck
}


