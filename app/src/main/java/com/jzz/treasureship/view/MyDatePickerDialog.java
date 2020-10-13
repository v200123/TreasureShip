package com.jzz.treasureship.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

R;

import java.util.Calendar;

public class MyDatePickerDialog extends AlertDialog implements DatePicker.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final DatePicker mDatePicker;
    private final OnDateSetListener mDateSetListener;

    private View view;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    public MyDatePickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear,
                              int dayOfMonth) {
        super(context, theme);

        mDateSetListener = listener;

        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        view = inflater.inflate(R.layout.dialog_date_picker, null);
        view.setBackgroundColor(Color.WHITE);
        // setView(view);

        // setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok),
        // this);
        // setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel),
        // this);
        // setButtonPanelLayoutHint(LAYOUT_HINT_SIDE);

        mDatePicker = view.findViewById(R.id.datePicker);
        // TODO 设置显示的最大值范围
        mDatePicker.setMaxDate(System.currentTimeMillis() - 1000L);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);

        // mDatePicker.setValidationCallback(mValidationCallback);
        // 实现自己的标题和ok按钮
        // setTitle("选择日期:");
        setButton();
    }

    // private void setTitle(String title) {
    // //获取自己定义的title布局并赋值。
    // ((TextView) view.findViewById(R.id.date_picker_title)).setText(title);
    // }
    private void setButton() {
        // 获取自己定义的响应按钮并设置监听，直接调用构造时传进来的CallBack接口（为了省劲，没有自己写接口，直接用之前本类定义好的）同时关闭对话框。
        view.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateSetListener != null) {
                    // Clearing focus forces the dialog to commit any
                    // pending
                    // changes, e.g. typed text in a NumberPicker.
                    mDatePicker.clearFocus();
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(),
                            mDatePicker.getDayOfMonth());
                    dismiss();
                }
            }
        });
        view.findViewById(R.id.layout_dissmiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public void myShow() {
        // 自己实现show方法，主要是为了把setContentView方法放到show方法后面，否则会报错。
        show();
        setContentView(view);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (month < 10) {
            mDatePicker.init(year, Integer.parseInt("0" + month), day, this);
        } else {
            mDatePicker.init(year, month, day, this);
        }


    }

    /**
     * Gets the {@link DatePicker} contained in this dialog.
     *
     * @return The calendar view.
     */
    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        if (mDatePicker.getMonth() < 10) {
            state.putInt(MONTH, Integer.parseInt("0" + mDatePicker.getMonth()));
        } else {
            state.putInt(MONTH, mDatePicker.getMonth());
        }
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int year = savedInstanceState.getInt(YEAR);
        if (mDatePicker.getMonth() < 10) {
            final int month = savedInstanceState.getInt("0" + mDatePicker.getMonth());
            final int day = savedInstanceState.getInt(DAY);
            mDatePicker.init(year, month, day, this);
        } else {
            final int month = savedInstanceState.getInt(String.valueOf(mDatePicker.getMonth()));
            final int day = savedInstanceState.getInt(DAY);
            mDatePicker.init(year, month, day, this);
        }
    }

}
