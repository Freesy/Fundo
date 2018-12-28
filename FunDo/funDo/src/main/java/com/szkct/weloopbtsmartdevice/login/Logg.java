package com.szkct.weloopbtsmartdevice.login;

import android.util.Log;

public class Logg {

    private static final boolean DEBUG = true;
    private static int i = 0;
    private static final String FLAG = "FunDo->";


    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(FLAG+tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(FLAG+tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(FLAG+tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(FLAG+tag+(++i), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
		 Log.w(FLAG+tag+(++i), msg);
    }
    }
}
