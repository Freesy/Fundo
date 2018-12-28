package com.mtk.app.notification;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.util.List;

/**
 * Created by ${wyl} on 2017/8/29.
 * 乐视手机，及部分手机开启通知无反应
 *
 */

public class NotificationCollectorMonitorService extends Service {
    boolean isServiceRunning = false;
    @Override
    public void onCreate() {
        super.onCreate();


        Log.e("isServiceRunning","isServiceRunningbbb");

        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE))
        {
            if("com.szkct.weloopbtsmartdevice.main.MainService".equals(service.service.getClassName())){
                isServiceRunning = true;
                Log.e("isServiceRunning","isServiceRunning");
            }			     }
        if (!isServiceRunning ) {
            Log.e("isServiceRunning","notisServiceRunning");
            Intent intent= new Intent(BTNotificationApplication.getInstance(), com.szkct.weloopbtsmartdevice.main.MainService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BTNotificationApplication.getInstance().startService(intent);
        }
       // ensureCollectorRunning();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}