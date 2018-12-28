
package com.mtk.app.ipc;

import java.util.ArrayList;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.mtk.app.ipc.IPCControllerEx.IPCController;

public class IPCControllerFactory {

    private static final String TAG = "[IPC_S][IPCControllerFactory]";

    private static final int MSG_NEW = 1;

    private ArrayList<IPCController> controllers = new ArrayList<IPCController>(6);

    // init in main thread
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW:
                    controllers.clear();
                    for (int i = 0; i < 6; i++) {
                        IPCController controller = new IPCControllerEx.IPCController(i);
                        controllers.add(i, controller);
                        Log.d(TAG, "[MSG_NEW] " + controllers.get(i));
                    }
                    break;
                default:
                    return;
            }
        }
    };

    private static IPCControllerFactory sInstance;

    private IPCControllerFactory() {
    }

    public static synchronized IPCControllerFactory getInstance() {
        if (sInstance == null) {
            sInstance = new IPCControllerFactory();
        }
        return sInstance;
    }

    public void init() {
        Log.d(TAG, "init");
        mHandler.sendEmptyMessage(MSG_NEW);
    }

    public synchronized IPCController getIPCController() {
        if (controllers == null) {
            Log.d(TAG, "[getIPCController] null");
            return null;
        }
        Log.d(TAG, "[getIPCController] " + controllers.size() + " = " + controllers);
        mHandler.sendEmptyMessageDelayed(MSG_NEW, 200);
        if (controllers.size() > 0) {
            IPCController controller = controllers.remove(0);
            return controller;
        } else {
            return null;
        }
    }
}
