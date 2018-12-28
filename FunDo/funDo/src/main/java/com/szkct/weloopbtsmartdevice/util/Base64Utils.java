package com.szkct.weloopbtsmartdevice.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/19
 * 描述: ${VERSION}
 * 修订历史：
 */

public class Base64Utils {

    // 加密
    public static String getBase64(String str) {
        String result = "";
        if( str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 解密
    public static String getFromBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.decode(str, Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
