package com.mtk.app.notification;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import android.content.Context;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

/**
 * This class is used for save ignored and excluded application list.
 * Their notification will not be pushed to remote device.
 * IgnoreList is a single class.
 */
public final class IgnoreList {
    // Debugging
    private static final String TAG = "AppManager/IgnoreList";

    // EXCLUSION_LIST, will be processed specially.
    private static final String[] EXCLUSION_LIST = { 
        "android",
        //chendalin delete 为了显示彩信
        //"com.android.mms",
        "com.android.phone",
        "com.android.providers.downloads", 
        "com.android.bluetooth",
        "com.mediatek.bluetooth",
        "com.htc.music",
        "com.lge.music",
        "com.sec.android.app.music",
        "com.sonyericsson.music",
        "com.ijinshan.mguard" ,
        "com.android.music",
        "com.android.dialer",
        "com.android.server.telecom",// add for L platform
        "com.google.android.music",
        "com.oppo.music",
        "com.lge.music"
    };

    // The file to save IgnoreList
    private static final String SAVE_FILE_NAME = "IgnoreList";

    private static final IgnoreList INSTANCE = new IgnoreList();

    private HashSet<String> mIgnoreList = null;

    private Context mContext = null;

    private IgnoreList() {
        Log.i(TAG, "IgnoreList(), IgnoreList created!");

        mContext = BTNotificationApplication.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of IgnoreList class.
     * 
     * @return the IgnoreList instance
     */
    public static IgnoreList getInstance() {
        return INSTANCE;
    }

    /**
     * Return the ignored application list.
     * 
     * @return the ignore list
     */
    public HashSet<String> getIgnoreList() {
        if (mIgnoreList == null) {
            loadIgnoreListFromFile();
        }

        Log.i(TAG, "getIgnoreList(), mIgnoreList = " + mIgnoreList.toString());
        return mIgnoreList;
    }

    @SuppressWarnings("unchecked")
    private void loadIgnoreListFromFile() {
        Log.i(TAG, "loadIgnoreListFromFile(),  file_name= " + SAVE_FILE_NAME);

        if (mIgnoreList == null) {
            try {
                Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME)))
                        .readObject();
                mIgnoreList = (HashSet<String>) obj;
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (mIgnoreList == null) {
            mIgnoreList = new HashSet<String>();
        }
    }

    public void addIgnoreItem(String name) {
        if (mIgnoreList == null) {
            loadIgnoreListFromFile();
        }
        if (!mIgnoreList.contains(name)) {
            mIgnoreList.add(name);
        }
    }

    public void removeIgnoreItem(String name) {
        if (mIgnoreList == null) {
            loadIgnoreListFromFile();
        }
        if (mIgnoreList.contains(name)) {
            mIgnoreList.remove(name);
        }

    }

    public void saveIgnoreList() {
        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;

        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(mIgnoreList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
    }

    /**
     * Save ignored applications to file.
     * 
     * @param ignoreList ignored applications list
     */
    public void saveIgnoreList(HashSet<String> ignoreList) {
        Log.i(TAG, "setIgnoreList(),  file_name= " + SAVE_FILE_NAME);

        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;

        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(ignoreList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }

        mIgnoreList = ignoreList;
        Log.e(TAG, "setIgnoreList(),  mIgnoreList= " + mIgnoreList);

        SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.IGNORELIST,true);
    }

    /**
     * Return the exclude application list, these applications will not show to
     * user for selection.
     * 
     * @return the exclude application list
     */
    public HashSet<String> getExclusionList() {
        HashSet<String> exclusionList = new HashSet<String>();
        for (String exclusionPackage : EXCLUSION_LIST) {
            exclusionList.add(exclusionPackage);
        }

        Log.e(TAG, "setIgnoreList(),  exclusionList=" + exclusionList);
        return exclusionList;
    }
}
