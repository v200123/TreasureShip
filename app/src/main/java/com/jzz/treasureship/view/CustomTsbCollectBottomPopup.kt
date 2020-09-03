package com.jzz.treasureship.view

import android.content.Context
import com.jzz.treasureship.R
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.dialog_move_delete_collect.view.*
import kotlinx.android.synthetic.main.fragment_goods_details.view.*

class CustomTsbCollectBottomPopup(context: Context) : BottomPopupView(context) {
    var moveVideo:Boolean by PreferenceUtils(PreferenceUtils.MOVE_VIDEO, false)
    var delVideo:Boolean by PreferenceUtils(PreferenceUtils.DEL_VIDEO, false)

    override fun getImplLayoutId() = R.layout.dialog_move_delete_collect

    override fun initPopupContent() {
        super.initPopupContent()


        layout_dissmiss.setOnClickListener {

            moveVideo = false
            delVideo = false

            dismiss()
        }

        layout_moveVideo.setOnClickListener {

            moveVideo = true
            delVideo = false

            dismiss()
        }

        layout_delVideo.setOnClickListener {

            moveVideo = false
            delVideo = true

            dismiss()
        }
    }

}