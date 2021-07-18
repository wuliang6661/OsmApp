package com.heloo.android.osmapp.utils;

import android.content.Context;
import android.util.Log;


public class LogUtils {

    private static String Tag = "wuliang";

    private static Context mcontext;

    public static void init(Context context) {
        mcontext = context;
    }


    public static void I(String message) {
        Log.i(Tag, message);
    }

    public static void D(String message) {
        Log.d(Tag, message);
    }

    public static void E(String message) {
        Log.e(Tag, message);
    }

    public static void log(String message) {
        Log.i(Tag, message);
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }

    public static void showToast(String message) {
        ToastUtils.showShortToast(message);
    }

}
