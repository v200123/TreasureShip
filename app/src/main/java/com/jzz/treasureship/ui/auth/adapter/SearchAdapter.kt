package com.jzz.treasureship.ui.auth.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jzz.treasureship.R
import com.jzz.treasureship.model.bean.DepartmentBean

/**
 *@date: 2020/9/14
 *@describe:
 *@Auth: 29579
 **/
class SearchAdapter : BaseQuickAdapter<DepartmentBean.DepartmentType, BaseViewHolder>(R.layout.item_simple_text) {
    override fun convert(holder: BaseViewHolder, item: DepartmentBean.DepartmentType) {
        holder.setText(R.id.tv_simple_name,item.mName)
    }
}
