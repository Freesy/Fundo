
package com.mtk.app.notification;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mediatek.ctrl.notification.NotificationActions;
import com.mediatek.ctrl.notification.NotificationData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

/**
 * This class is used for save notification list sync with watch. 
 */
public final class NotificationSyncList {
    // Debugging
    private static final String TAG = "AppManager/NotificationSyncList";

    private static final String SAVE_FILE_NAME = "SyncList";

    private static final NotificationSyncList mInstance = new NotificationSyncList();

    private ArrayList<NotificationData> mSyncList = null;

    private Context mContext = null;

    private NotificationSyncList() {
        Log.i(TAG, "NotificationSyncList(), NotificationSyncList created!");

        mContext = BTNotificationApplication.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of NotificationSyncList class.
     * 
     * @return the NotificationSyncList instance
     */
    public static NotificationSyncList getInstance() {
        return mInstance;
    }

    /**
     * Return the Notification Sync list.
     * 
     * @return the sync list
     */
    public ArrayList<NotificationData> getSyncList() {
        if (mSyncList == null) {
            loadSyncListFromFile();
        }

        //Log.i(TAG, "getSyncList(), mSyncList = " + mSyncList.toString());
        return mSyncList;
    }

    @SuppressWarnings("unchecked")
    private void loadSyncListFromFile() {
//        Log.i(TAG, "loadSyncListFromFile(),  file_name= " + SAVE_FILE_NAME);  // addlx:这里会报错
        ObjectInputStream inputStream = null;

        if (mSyncList == null) {
            try {
                inputStream = new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME));
                if(inputStream!=null){
                    Object obj = (inputStream.readObject());
                    mSyncList = (ArrayList<NotificationData>) obj;
                }
                inputStream.close();
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (Exception exception) {
                exception.printStackTrace();
            } 
        }

        if (mSyncList == null) {
            mSyncList = new ArrayList<NotificationData>();
        }
    }

    public void removeNotificationData(String msgId) {
        if (mSyncList == null) {
            loadSyncListFromFile();
        }
        for(NotificationData notificationData : mSyncList){
            if(String.valueOf(notificationData.getMsgId()).equals(msgId)){
                mSyncList.remove(notificationData);
                break;
            }
        }
    }

    public void handleNotificationAction(String msgId,String actionId) {
        Log.i(TAG, "handleNotificationAction,  msgId = " + msgId + ", actionId = " + actionId);
        if (mSyncList == null) {
            loadSyncListFromFile();
        }
        for (NotificationData notificationData : mSyncList) {
            if (String.valueOf(notificationData.getMsgId()).equals(msgId)) {
                ArrayList<NotificationActions> notificationActions = notificationData
                        .getActionsList();
                for (NotificationActions action : notificationActions) {
                    if (action.getActionId().equals(actionId)){
                        // send pending intent
                        PendingIntent pendingIntent =  action.getActionIntent();
                        if ( pendingIntent!= null) {
                            try {
                                pendingIntent.send();
                                Log.i(TAG, "send action intent. ");
                            } catch (CanceledException e) {
                                Log.e(TAG, "send action intent canceledException and sync error to watch.");
                                e.printStackTrace();
                                // if the PendingIntent has the flag FLAG_ONE_SHOT, the function of 
                                // send will throw canceldexception if you click "Open" operation from the 
                                // second time.
                                // reserved the API to sync the response to watch if you want to show the
                                // hint to user on watch
                                //NotificationController.getInstance(mContext).sendActionOperateResult(
                                //        notificationData.getMsgId(),false);
                                String error = "(" +action.getActionTitle() + ") " + mContext.getString(R.string.operate_error);
                                String appName = "";
                                PackageManager packagemanager = mContext.getPackageManager();
                                ApplicationInfo appInfo = null;
                                try {
                                    appInfo = packagemanager.getApplicationInfo(notificationData.getPackageName(), 0);
                                    if(appInfo!=null)
                                    appName = mContext.getPackageManager().getApplicationLabel(appInfo).toString();
                                    Log.i(TAG,"appName = " + appName);
                                } catch (NameNotFoundException exception) {
                                    exception.printStackTrace();
                                }
                                if(!TextUtils.isEmpty(appName)){
                                    error = "(" + appName + " : " + action.getActionTitle() + ") " + mContext.getString(R.string.operate_error);
                                    Log.i(TAG,"error = " + error);
                                }
                                
                                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();

                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void addNotificationData(NotificationData notificationData) {
        if (mSyncList == null) {
            loadSyncListFromFile();
        }
        if (!mSyncList.contains(notificationData)) {
            for(NotificationData data: mSyncList){
                if(data.getPackageName().equals(notificationData.getPackageName()) &&
                        data.getMsgId() == notificationData.getMsgId()){
                    // update notification, remove old data
                    Log.i(TAG, "update notification data  ");
                    mSyncList.remove(data);
                    break;
                }
            }
            //add new data
            mSyncList.add(notificationData);
        }
    }




    /**
     * clear sync list file.
     */
    public void clearSyncList(){
        Log.i(TAG, "clear SyncList");
       // saveSyncList(null);
    }
}
