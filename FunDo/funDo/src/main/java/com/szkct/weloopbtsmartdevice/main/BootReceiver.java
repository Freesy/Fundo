/**
 * 
 */

package com.szkct.weloopbtsmartdevice.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class receives BOOT_COMPLETED action to start NotificationWatcher main
 * service.
 */
public class BootReceiver extends BroadcastReceiver {
    // Debugging
    private static final String TAG = "AppManager/BootReceiver";

    public BootReceiver() {
        Log.i(TAG, "BootReceiver(), BootReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive(), action=" + intent.getAction());
        // Start main service
        Log.i(TAG, "start MainService!");
        context.startService(new Intent(context, MainService.class));
    }
}
