
package com.mtk.app.notification;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import android.content.Context;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

/**
 * This class is used for save blocked application list. Their
 * notification will not be pushed to remote device. IgnoreList is a single
 * class.
 */
public final class BlockList {
    // Debugging
    private static final String TAG = "AppManager/BlockList";

    private static final String SAVE_FILE_NAME = "BlockList";

    private static final BlockList mInstance = new BlockList();

    private HashSet<CharSequence> mBlockList = null;

    private Context mContext = null;

    private BlockList() {
        Log.i(TAG, "BlockList(), BlockList created!");

        mContext = BTNotificationApplication.getInstance().getApplicationContext();
    }

    /**
     * Return the instance of BlockList class.
     * 
     * @return the BlockList instance
     */
    public static BlockList getInstance() {
        return mInstance;
    }

    /**
     * Return the block application list.
     * 
     * @return the block list
     */
    public HashSet<CharSequence> getBlockList() {
        if ( null==  mBlockList) {
            loadBlockListFromFile();
        }

        Log.i(TAG, "getBlockList(), mBlockList = " + mBlockList.toString());
        return mBlockList;
    }

    @SuppressWarnings("unchecked")
    private void loadBlockListFromFile() {
        Log.i(TAG, "loadBlockListFromFile(),  file_name= " + SAVE_FILE_NAME);

        if ( null==  mBlockList) {
            try {
                Object obj = (new ObjectInputStream(mContext.openFileInput(SAVE_FILE_NAME))).readObject();
                mBlockList = (HashSet<CharSequence>) obj;
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if ( null==  mBlockList) {
            mBlockList = new HashSet<CharSequence>();
        }
    }

    public void removeBlockItem(CharSequence name) {
        if (mBlockList == null) {
            loadBlockListFromFile();
        }
        if (mBlockList.contains(name)) {
            mBlockList.remove(name);
        }

    }

    public void addBlockItem(CharSequence name) {
        if (mBlockList == null) {
            loadBlockListFromFile();
        }
        if (!mBlockList.contains(name)) {
            mBlockList.add(name);
        }
    }

    public void saveBlockList() {
        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;

        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(mBlockList);
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
     * @param blockList ignored applications list
     */
    public void saveBlockList(HashSet<CharSequence> blockList) {
        Log.i(TAG, "saveBlockList(),  file_name= " + SAVE_FILE_NAME);

        FileOutputStream fileoutputstream;
        ObjectOutputStream objectoutputstream;

        try {
            fileoutputstream = mContext.openFileOutput(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(blockList);
            objectoutputstream.close();
            fileoutputstream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }

        mBlockList = blockList;
        Log.i(TAG, "saveBlockList(),  mBlockList= " + mBlockList);
    }
}
