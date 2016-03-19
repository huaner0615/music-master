package com.buaa.utils;

import android.util.Log;

/**
 * Created by Window10 on 2016/3/17.
 */
public class L {
    private static final boolean DEBUG = true;
    private static final String TAG = "info";

    public static void i(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }

    public static void e(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }

    public static void d(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

}
