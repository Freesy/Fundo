
package com.mtk.app.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mediatek.wearable.Controller;
import com.mediatek.wearable.WearableManager;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.util.HashSet;


public class MREEController extends Controller {
    private static final String sControllerTag = "MREEController";

    private static final String TAG = "AppManager/MREEController";

    private static MREEController mInstance;

    private Context mContext = BTNotificationApplication.getInstance().getApplicationContext();

    public static final String EXTRA_DATA = "EXTRA_DATA";

    public static final String ACTION_QUERY_MTK_BLUETOOTH_SYNC_DATA_SUCCESS = "com.mtk.QUERY_MTK_BLUETOOTH_SYNC_DATA_SUCCESS";

    public static final String ACTION_QUERY_MTK_BLUETOOTH_SYNC_DATA_FIALED = "com.mtk.QUERY_MTK_BLUETOOTH_SYNC_DATA_FIALED";

    public static final String EXTRA_QUERY_MTK_BLUETOOTH_SYNC_DATA_BUFFER = "com.mtk.QUERY_MTK_BLUETOOTH_SYNC_DATA_BUFFER";

    public static final String PEDOMETER_SENDER = "MTK_PEDOMETER";

    public static final String PEDOMETER_RECEIVER = "mtk_pedometer";

    private MREEController() {
        super(sControllerTag, CMD_8);
//        HashSet<String> receivers = new HashSet<String>();
//        receivers.add(PEDOMETER_SENDER);
//        super.setReceiverTags(receivers);
    }

    public static MREEController getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new MREEController();
        }
        return mInstance;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Override
    public void onConnectionStateChange(int state) {
        super.onConnectionStateChange(state);
    }

    @Override
     public void send(String cmd, byte[] dataBuffer, boolean response, boolean progress, int priority) {
        try {
            super.send(cmd, dataBuffer, response, progress, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(byte[] dataBuffer) {
        super.onReceive(dataBuffer);
        String command = new String(dataBuffer);
        String[] commands = command.split(" ");
        for (Controller c : (HashSet<Controller>) WearableManager.getInstance()
                .getControllers()) {
              if (c.getCmdType() == 8) {
                  HashSet<String> receivers = c.getReceiverTags();
                  if (receivers != null && receivers.size() > 0 && receivers.contains(commands[0])) {
                      return;
                  }
              }
        }
        Log.e(TAG, "MREEController onReceive(), command :" + command);
//        HashSet<String> receivers = getReceiverTags();
        Intent broadcastIntent = new Intent();
        if (commands[1].equals(PEDOMETER_SENDER)) {
            broadcastIntent.setAction(ACTION_QUERY_MTK_BLUETOOTH_SYNC_DATA_SUCCESS);
            // Fill extra data, it is optional
            if (dataBuffer != null) {
                broadcastIntent.putExtra(EXTRA_QUERY_MTK_BLUETOOTH_SYNC_DATA_BUFFER, dataBuffer);
            } else {
                Intent syncDataIntent = new Intent();
                broadcastIntent.setAction(ACTION_QUERY_MTK_BLUETOOTH_SYNC_DATA_FIALED);
                mContext.sendBroadcast(syncDataIntent);
            }
        } else {
            broadcastIntent.setAction(commands[1]);
            broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            // Fill extra data, it is optional
            if (dataBuffer != null) {
                broadcastIntent.putExtra(EXTRA_DATA, dataBuffer);
            }
        }
        mContext.sendBroadcast(broadcastIntent);

    }

}
