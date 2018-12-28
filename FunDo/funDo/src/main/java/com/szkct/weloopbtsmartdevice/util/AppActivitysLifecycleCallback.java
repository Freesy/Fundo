package com.szkct.weloopbtsmartdevice.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by ${Justin} on 2018/1/19.
 */

/**
 * 监听当前App所有Activity的生命同期接口
 */
public class AppActivitysLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = AppActivitysLifecycleCallback.class.getSimpleName();

    /**
     * 最后一个显示的Activity
     */
    private Activity lastActivity;

    private static AppActivitysLifecycleCallback instance;

    public static AppActivitysLifecycleCallback init(Application application) {
        if (instance == null) {
            instance = new AppActivitysLifecycleCallback();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }


    public static Activity getLastActivity() {
        if (instance != null) {
            return instance.lastActivity;
        } else {
            return null;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        lastActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }


}
