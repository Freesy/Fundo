package com.szkct.bluetoothtool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import android.content.Context;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

/**
 * This class is used for save ignored and excluded application list.
 * Their notification will not be pushed to remote device.
 * IgnoreList is a single class.
 */
public final class IgnoreList {
    // Debugging
    private static final String LOG_TAG = "IgnoreList";

    // EXCLUSION_LIST, will be processed specially.
    private static final String[] EXCLUSION_LIST = { 
        "android",
        "com.android.mms",
        "com.android.phone",
        "com.android.providers.downloads", 
        "com.android.bluetooth",
        "com.mediatek.bluetooth",
        "com.htc.music",
        "com.lge.music",
        "com.sec.android.app.music",
        "com.sonyericsson.music",
        "com.tencent.mm", //add by edman xie
        "com.ijinshan.mguard" 
    };

    // The file to save IgnoreList
    private static final String SAVE_FILE_NAME = "IgnoreList";

    private static final IgnoreList INSTANCE = new IgnoreList();

    private HashSet<String> mIgnoreList = null;
    private Context mContext = null;

    private IgnoreList() {
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
        return mIgnoreList;
    }

    @SuppressWarnings("unchecked")
    private void loadIgnoreListFromFile() {

        if (mIgnoreList == null) {
            try {
                Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME))).readObject();
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

    /**
     * Save ignored applications to file.
     * 
     * @param ignoreList ignored applications list
     */
    public void saveIgnoreList(HashSet<String> ignoreList) {

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

        mIgnoreList =ignoreList;
    }

    /**
     * Return the exclude application list, these applications will not show to user for selection.
     * 
     * @return the exclude application list
     */
    public HashSet<String> getExclusionList() {
        HashSet<String> exclusionList = new HashSet<String>();
        for (String exclusionPackage : EXCLUSION_LIST) {
            exclusionList.add(exclusionPackage);
        }
        return exclusionList;
    }
}
