package com.kct.bluetooth.utils;

import android.util.Log;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/11/13
 * 描述: ${VERSION}
 * 修订历史：
 */

public class LogUtil {

    public static final  String tag = "LogUtil";

    public static final  boolean isDebug = true;

    public static void  d(String tag,String msg)
    {
        if(isDebug)
        {
            Log.d(tag,msg);
        }
    }

    public static void  d(String msg)
    {
        if(isDebug)
        {
            Log.d(tag,msg);
        }
    }

    public static void  e(String tag,String msg)
    {
        if(isDebug)
        {
            Log.e(tag, msg);
        }
    }

    public static void  e(String msg)
    {
        if(isDebug)
        {
            Log.e(tag, msg);
        }
    }


    public static void  w(String tag,String msg)
    {
        if(isDebug)
        {
            Log.w(tag, msg);
        }
    }

    public static void  w(String msg)
    {
        if(isDebug)
        {
            Log.w(tag, msg);
        }
    }

    public static void  i(String tag,String msg)
    {
        if(isDebug)
        {
            Log.i(tag, msg);
        }
    }

    public static void  i(String msg)
    {
        if(isDebug)
        {
            Log.i(tag, msg);
        }
    }

}
