package com.jzz.treasureship.view

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.ui.home.HomeViewModel
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.dialog_add_category.view.*

class CustomInputDialog(context: Context,viewModel: HomeViewModel) : CenterPopupView(context) {

    private val viewModel:HomeViewModel = viewModel

    override fun getImplLayoutId() = R.layout.dialog_add_category

    override fun initPopupContent() {
        super.initPopupContent()

        btn_addCategory.setOnClickListener {
            val categoryName = et_input.text.toString()


            if (categoryName.isBlank()) {
                ToastUtils.showShort("请输入新增分类名后再创建")
            } else {
                viewModel.addCollectCategory(categoryName)
                dismiss()
            }
        }

        btn_cancle.setOnClickListener {
            dismiss()
        }
    }

}