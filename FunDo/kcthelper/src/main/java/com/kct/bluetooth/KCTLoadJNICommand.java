package com.kct.bluetooth;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/4/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTLoadJNICommand {

    public static final String TAG = "KCTLoadJNICommand";
    public static KCTLoadJNICommand mKCTLoadJNICommand;

    static {
        System.loadLibrary("KCTCommand");
    }

    public static KCTLoadJNICommand getInstance(){
        if(mKCTLoadJNICommand == null){
            synchronized (KCTLoadJNICommand.class){
                mKCTLoadJNICommand = new KCTLoadJNICommand();
            }
        }
        return mKCTLoadJNICommand;
    }

    public native String getDFUCommand();

    public native String getDFUVersion();

    public native String getDFUData();
}
