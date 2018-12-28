
package com.mtk.app.notification;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

/**
 * This class is used for save application list. Their notification will 
 * be pushed to remote device. AppList is a single class.
 */
public final class AppList {
    // Debugging
    private static final String TAG = "AppManager/AppList";

    private static final String SAVE_FILE_NAME = "AppList";

    public static final String MAX_APP = "MaxApp";

  

    public static final int CREATE_LENTH = 3;

    private static final AppList mInstance = new AppList();

    private Map<Object, Object> mAppList = null;

    private Context mContext = null;

    private AppList() {
        Log.i(TAG, "AppList(), AppList created!");

        mContext = BTNotificationApplication.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of AppList class.
     * 
     * @return the AppList instance
     */
    public static AppList getInstance() {
        return mInstance;
    }

    /**
     * Return the passed application list.
     * 
     * @return the AppList list
     */
    public Map<Object, Object> getAppList() {
        if (mAppList == null) {
            loadAppListFromFile();
        }

        Log.i(TAG, "getAppList(), mAppList = " + mAppList.toString());
        return new HashMap<>(mAppList);   //   return  mAppList
    }

    @SuppressWarnings("unchecked")
    private void loadAppListFromFile() {
        Log.i(TAG, "loadIgnoreListFromFile(),  file_name= " + SAVE_FILE_NAME);
        ObjectInputStream inputStream = null;
        
        if (mAppList == null) {
            try {
                inputStream = new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME));
                Object obj = (inputStream.readObject());
                mAppList = (Map<Object, Object>) obj;
                inputStream.close();
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (mAppList == null) {
            mAppList = new HashMap<Object, Object>();
        }
    }

    /**
     * Save passed applications to file.
     * 
     * @param appList passed applications list
     */
    public void saveAppList(Map<Object, Object> appList) {
        Log.i(TAG, "saveAppList(),  file_name= " + SAVE_FILE_NAME);

        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;

        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(mAppList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }

        mAppList = appList;
        Log.i(TAG, "saveAppList(),  mAppList= " + mAppList);
    }
}
