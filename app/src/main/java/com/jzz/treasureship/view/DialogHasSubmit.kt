package com.jzz.treasureship.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.jzz.treasureship.R
import com.lc.liuchanglib.adapterUtils.AdapterHelper
import com.lc.liuchanglib.ext.click
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.dialog_has_submit_list.view.*

/**
 *@PackageName: com.jzz.treasureship.view
 *@Auth： 29579
 **/
class DialogHasSubmit<T>(
    context: Context,
    var dataList: List<T>?,
    val dialogCovert: AdapterHelper.ViewHolderConverter<T>,
    val itemDialogClick: OnItemClickListener
) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int = R.layout.dialog_has_submit_list
    override fun onCreate() {
        super.onCreate()
        tv_dialog_list_submit click {
            this.dismiss()
        }
        rv_submit_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AdapterHelper
                .getAdapter(R.layout.item_dialog_submit, dialogCovert, dataList?.toMutableList() ?: arrayListOf())
                .apply { addChildClickViewIds() }
                .apply { setOnItemClickListener(itemDialogClick) }
        }

    }

}