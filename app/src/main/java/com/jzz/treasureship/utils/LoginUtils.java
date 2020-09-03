package com.jzz.treasureship.utils;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * @author : martin
 * @date : 2018/9/6
 */
public class LoginUtils {

    private Context context;

    @SuppressLint("StaticFieldLeak")
    private static LoginUtils instance;

    /**
     * 是否登录
     */
    public boolean isLogin = false;

    private LoginUtils(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        instance = new LoginUtils(context);
    }

    public static LoginUtils getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }
}
