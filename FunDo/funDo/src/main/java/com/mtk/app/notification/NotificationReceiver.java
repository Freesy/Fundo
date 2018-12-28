/**
 * 
 */

package com.mtk.app.notification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.notification.NotificationData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;


/**
 * This class will receive and process all notifications.
 */
public class NotificationReceiver extends AccessibilityService {
    // Debugging
    private static final String TAG = "AppManager/Noti/Receiver";

    // Avoid propagating events to the client too frequently
    private static final long EVENT_NOTIFICATION_TIMEOUT_MILLIS = 0L;

    // Received event
    private AccessibilityEvent mAccessibilityEvent = null;
    private NotificationDataManager notificationDataManager = null;
    private Context mContext = BTNotificationApplication.getInstance().getApplicationContext();

    public NotificationReceiver() {
        notificationDataManager = new NotificationDataManager(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Only concern TYPE_NOTIFICATION_STATE_CHANGED
        Log.i(TAG, "onAccessibilityEvent(), eventType=" + event.getEventType());
        if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return;
        }

        Log.d(TAG, "sdk version is " + android.os.Build.VERSION.SDK_INT);
        if(android.os.Build.VERSION.SDK_INT  >= 18){
            Log.i(TAG,"Android platform version is higher than 18.");
            return;
        }

        // If notification is null, will not forward it
        mAccessibilityEvent = event;
        Notification notification = (Notification) mAccessibilityEvent.getParcelableData();
        if (notification == null) {
            return;
        }
        if(event.getPackageName().toString().contains(".music")){
            return;
        }
        if (!(boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, true)) {
            if (event.getPackageName().toString().contains(".incallui")) {
                return;
            }
        }
        if (!(boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, true)) {
            if (event.getPackageName().toString().contains(".mms") || event.getPackageName().toString().contains(".contacts")) {
                return;
            }
        }

        if (event.getPackageName().toString().equals("com.miui.securitycenter")||event.getPackageName().toString().equals("com.kct.fundo.btnotification")||event.getPackageName().toString().equals("com.android.mms")) {    //todo add 20180202   com.kct.fundo.btnotification
            return;
        }

        NotificationData notificationData = notificationDataManager.getNotificationData(
                notification, event.getPackageName().toString(),"",NotificationController.genMessageId());
        notificationDataManager.sendNotificationData(notificationData,2);

    }

    @Override
    public void onServiceConnected() {
        Log.i(TAG, "onServiceConnected()");

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 14) {
            setAccessibilityServiceInfo();
        }

       // BTNotificationApplication.getBluetoothBleService().setNotificationReceiver(this);
    }

    private void setAccessibilityServiceInfo() {
        Log.i(TAG, "setAccessibilityServiceInfo()");

        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        accessibilityServiceInfo.notificationTimeout = EVENT_NOTIFICATION_TIMEOUT_MILLIS;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind()");
        //BTNotificationApplication.getBluetoothBleService().clearNotificationReceiver();

        return false;
    }

}
