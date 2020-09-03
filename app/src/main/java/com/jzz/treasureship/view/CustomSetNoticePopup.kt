package com.jzz.treasureship.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TimePicker
import com.blankj.utilcode.util.ToastUtils
import com.jzz.treasureship.R
import com.jzz.treasureship.utils.PreferenceUtils
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.layout_customer_set_time.view.*
import java.lang.StringBuilder
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*


class CustomSetNoticePopup(context: Context) : CenterPopupView(context) {

    override fun getImplLayoutId() = R.layout.layout_customer_set_time

    override fun initPopupContent() {
        super.initPopupContent()

        var dateString: String? = null
        var timeString: String? = null

        setDatePickerDividerColor(date)
        val year: Int = Calendar.getInstance().get(Calendar.YEAR)
        val monthOfYear: Int = Calendar.getInstance().get(Calendar.MONTH)
        val dayofMonth: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)
        date.init(year, monthOfYear, dayofMonth, object : DatePicker.OnDateChangedListener {

            @SuppressLint("SimpleDateFormat")
            override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)

                val format = SimpleDateFormat("yyyy年MM月dd日")
                val formatTime = format.format(calendar.getTime())

                dateString = "$formatTime"
            }

        })

        setTimePickerDividerColor(time)
        time.setIs24HourView(true)
        time.setOnTimeChangedListener { view, hourOfDay, minute ->
            timeString = "${hourOfDay}:${minute}"
        }
        layout_dissmiss.setOnClickListener {
            var noticeTime by PreferenceUtils(PreferenceUtils.NOTICE_TIME, "")
            noticeTime = ""
            this.dismiss()
        }

        layout_confirm.setOnClickListener {
            var noticeTime by PreferenceUtils(PreferenceUtils.NOTICE_TIME, "")
            var mYear: String? = null
            var mTime: String? = null
            if (!dateString.isNullOrBlank()) {
                mYear = "${year}年${monthOfYear}月${dayofMonth}日"
            }
            if (!timeString.isNullOrBlank()) {
                mTime = "${hour}:${minute}"
            }
            noticeTime = "$mYear  $mTime"
            dismiss()
        }
    }

    /**
     * 设置时间选择器的分割线颜色
     *
     * @param datePicker
     */
    private fun setDatePickerDividerColor(datePicker: DatePicker) { // Divider changing:
// 获取 mSpinners
        val llFirst = datePicker.getChildAt(0) as LinearLayout
        // 获取 NumberPicker
        val mSpinners = llFirst.getChildAt(0) as LinearLayout
        for (i in 0 until mSpinners.childCount) {
            val picker = mSpinners.getChildAt(i) as NumberPicker
            val pickerFields = NumberPicker::class.java.declaredFields
            for (pf in pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true)
                    try {
                        pf.set(picker, ColorDrawable(Color.parseColor("#26C8D7"))) //设置分割线颜色
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                    break
                }
            }
        }
    }

    fun setTimePickerDividerColor(timePicker: TimePicker) { // 获取 mSpinners
        val llFirst = timePicker.getChildAt(0) as LinearLayout
        // 获取 NumberPicker
        val mSpinners = llFirst.getChildAt(1) as LinearLayout
        for (i in 0 until mSpinners.getChildCount()) {
            if (mSpinners.getChildAt(i) is NumberPicker) {
                val pickerFields: Array<Field> = NumberPicker::class.java.declaredFields
                for (pf in pickerFields) {
                    if (pf.getName().equals("mSelectionDivider")) {
                        pf.setAccessible(true)
                        try {
                            pf.set(mSpinners.getChildAt(i), ColorDrawable(Color.parseColor("#26C8D7")))
                        } catch (e: java.lang.IllegalArgumentException) {
                            e.printStackTrace()
                        } catch (e: Resources.NotFoundException) {
                            e.printStackTrace()
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        }
                        break
                    }
                }
            }
        }
    }
}