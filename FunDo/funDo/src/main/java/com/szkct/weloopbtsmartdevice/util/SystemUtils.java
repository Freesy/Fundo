package com.szkct.weloopbtsmartdevice.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.kct.bluetooth.utils.*;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.util.List;

/**工具类
 *
 */

public class SystemUtils {

    /**
     * 判断本应用是否存活
     * 如果需要判断本应用是否在后台还是前台用getRunningTask
     * */
    public static boolean isAPPALive(Context mContext,String packageName){
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for(ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList){
            if(packageName.equals(appInfo.processName)){
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }

    public static String getVersionName() {
        String localVersion = "";
        try {
            PackageInfo packageInfo = BTNotificationApplication.getInstance().getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(BTNotificationApplication.getInstance().getPackageName(), 0);
            localVersion = packageInfo.versionName;
            com.kct.bluetooth.utils.LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
