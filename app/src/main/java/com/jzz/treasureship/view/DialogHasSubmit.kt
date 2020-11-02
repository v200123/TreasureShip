package com.jzz.treasureship.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jzz.treasureship.R
import com.lc.liuchanglib.adapterUtils.AdapterHelper
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.dialog_has_submit_list.view.*

/**
 *@PackageName: com.jzz.treasureship.view
 *@Auth： 29579
 **/
class  DialogHasSubmit<T>(context: Context , var dataList:List<T>) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int =  R.layout.dialog_has_submit_list

    override fun onCreate() {
        super.onCreate()

        rv_submit_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AdapterHelper.getAdapter(R.layout.item_dialog_submit,object :AdapterHelper.ViewHolderConverter<T>{
                override fun convert(helper: BaseViewHolder, item: T) {

                }

            },dataList.toMutableList())
        }

    }

}