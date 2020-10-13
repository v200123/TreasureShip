package com.jzz.treasureship.base

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jzz.treasureship.R


open class BaseBottomDialogFragment : BottomSheetDialogFragment() {
    override fun onStart() {
        super.onStart()
        val dialogHeight = getDialogHeight(context!!)
        // 点击外面允许取消
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.window!!.setGravity(Gravity.BOTTOM)
        val bottomSheetDialog = dialog as BottomSheetDialog
        val view = bottomSheetDialog.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = view.layoutParams
        layoutParams.height = dialogHeight
        view.layoutParams = layoutParams
        view.setBackgroundResource(R.color.transparent)
        BottomSheetBehavior.from(view).peekHeight = getPeekHeight()
    }

    /**
     * 得到屏幕的高
     *
     * @param context
     * @return
     */
    protected fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return (wm.defaultDisplay.height)
    }

    /**
     * 底部弹窗的高度
     * 子类可以自己实现，定义高度
     */
    open fun getDialogHeight(context: Context) = (context.resources.displayMetrics.heightPixels * 0.75).toInt()

    /**
     * 底部弹窗弹出时的高度，需要注意的是如果PeekHeight大于tDialogHeight时，弹窗会显示不全
     */
    open fun getPeekHeight() = (getScreenHeight(context!!) * 0.75).toInt()
}