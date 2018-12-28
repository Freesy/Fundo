
package com.mtk.app.ipc;

import java.util.concurrent.CopyOnWriteArrayList;

import com.mediatek.wearable.WearableManager;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import mtk.ipc.IControllerCallback;
import mtk.ipc.IControllerInterface;

public class IPCControllerService extends Service {

    private static final String TAG = "[IPC_S][IPCControllerService]";

    private static final int MSG_HANDLE_BYTES_RECEIVED = 100;

    private static final int MSG_HANDLE_CONNECTION_STATE_CHANGE = 101;

    private CopyOnWriteArrayList<ControllerInformation> mControllerInformations;

    private HandlerThread mHandlerThread;

    private Handler mReceiveDataHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "[onCreate] enter");
        mControllerInformations = new CopyOnWriteArrayList<ControllerInformation>();

        mHandlerThread = new HandlerThread("ReceiveDataHandler");
        mHandlerThread.start();
        mReceiveDataHandler = new ReceiveDataHandler(mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "[onDestroy] enter");
        for (ControllerInformation inof : mControllerInformations) {
            inof.mController.tearDown();
        }
        mReceiveDataHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quit();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "[onBind] enter this : " + this);
        ControllerBinder binder = new ControllerBinder(this);
        return binder;
    }

    /**
     * IControllerInterface implement
     */
    private class ControllerBinder extends IControllerInterface.Stub {

        private IPCControllerService mService;

        ControllerBinder(IPCControllerService service) {
            mService = service;
        }

        public int init(int cmd_type, String tagName) {
            if (TextUtils.isEmpty(tagName)) {
                Log.e(TAG, "[Binder][init] tagName is null or empty");
                return -10;
            }
            if (mService == null) {
                return -3;
            } else {
                return mService.init(cmd_type, tagName);
            }
        }

        public long sendBytes(String tagName, String cmd, byte[] dataToSend, int priority) {
            if (TextUtils.isEmpty(tagName)) {
                Log.e(TAG, "[Binder][sendBytes] tagName is null or empty");
                return -10;
            }
            if (dataToSend == null || dataToSend.length == 0) {
                Log.e(TAG, "[Binder][sendBytes] dataToSend is null or empty");
                return -2;
            }
            if (mService == null) {
                Log.e(TAG, "[Binder][sendBytes] mService is null");
                return -3;
            } else {
                return mService.sendBytes(tagName, cmd, dataToSend, priority);
            }
        }

        @Override
        public int getConnectionState() {
            if (mService == null) {
                Log.e(TAG, "[getConnectionState] mService is null");
                return WearableManager.STATE_NONE;
            } else {
                return mService.getConnectionState();
            }
        }

        @Override
        public void registerControllerCallback(String tagName, IControllerCallback callback)
                throws RemoteException {
            Log.d(TAG, "[registerControllerCallback] tagName : " + tagName);
            if (TextUtils.isEmpty(tagName)) {
                Log.e(TAG, "[Binder][registerControllerCallback] tagName is null or empty");
                return;
            }

            ControllerInformation ci = checkExist(tagName);
            if (ci == null) {
                Log.e(TAG, "[Binder][registerControllerCallback] ci is null");
                return;
            }

            Log.d(TAG, "[Binder][registerControllerCallback] call to register");
            boolean b = ci.mOutControllerCallbackList.register(callback);
            Log.d(TAG, "[Binder][registerControllerCallback] register result : " + b);
        }

        @Override
        public void unregisterControllerCallback(String tagName, IControllerCallback callback)
                throws RemoteException {
            if (TextUtils.isEmpty(tagName)) {
                Log.e(TAG, "[Binder][unregisterControllerCallback] tagName is null or empty");
                return;
            }

            Log.d(TAG, "[unregisterControllerCallback] tagName : " + tagName);

            ControllerInformation ci = checkExist(tagName);
            if (ci == null) {
                Log.e(TAG, "[Binder][unregisterControllerCallback] ci is null");
                return;
            }

            Log.d(TAG, "[Binder][unregisterControllerCallback] call to unregister");
            boolean b = ci.mOutControllerCallbackList.unregister(callback);
            Log.d(TAG, "[Binder][unregisterControllerCallback] register result : " + b);
        }

        @Override
        public void close(String tagName) {
            if (TextUtils.isEmpty(tagName)) {
                Log.e(TAG, "[close] tagName is WRONG");
                return;
            }
            ControllerInformation ci = checkExist(tagName);
            if (ci == null) {
                Log.e(TAG, "[Binder][close] ci is null");
                return;
            }
            Log.d(TAG, "[close] tagName : " + tagName);
            ci.mController.tearDown();
            ci.mOutControllerCallbackList.kill();

            mControllerInformations.remove(ci);
        }

        @Override
        public String getRemoteDeviceName() {
            if (mService == null) {
                Log.e(TAG, "[getRemoteDeviceName] mService is null");
                return "";
            } else {
                return mService.getRemoteDeviceName();
            }
        }
    }

    /**
     * 
     *
     */
    private IPCControllerEx.IControllerInternalCallback mInternalCallabck = new IPCControllerEx.IControllerInternalCallback() {
        @Override
        public void onConnectionStateChange(String tagname, int newState) {
            if (TextUtils.isEmpty(tagname)) {
                Log.e(TAG, "[onConnectionStateChange] tagname is null or empty");
                return;
            }

            Log.d(TAG, "[onConnectionStateChange] tagName : " + tagname + ", newState : "
                    + newState);

            ReceivedData data = new ReceivedData(tagname);
            data.mConnectionNewState = newState;

            Message msg = mReceiveDataHandler.obtainMessage(MSG_HANDLE_CONNECTION_STATE_CHANGE);
            msg.obj = data;
            mReceiveDataHandler.sendMessage(msg);
        }

        @Override
        public void onBytesReceived(String tagname, byte[] dataBuffer) {
            if (TextUtils.isEmpty(tagname)) {
                Log.e(TAG, "[onBytesReceived] tagname is null or empty");
                return;
            }
            Log.d(TAG, "[onBytesReceived] tagName : " + tagname + ", dataBuffer Length : "
                    + dataBuffer.length);

            ReceivedData data = new ReceivedData(tagname);
            data.mByteDataBuffer = new byte[dataBuffer.length];
            System.arraycopy(dataBuffer, 0, data.mByteDataBuffer, 0, dataBuffer.length);

            Message msg = mReceiveDataHandler.obtainMessage(MSG_HANDLE_BYTES_RECEIVED);
            msg.obj = data;
            mReceiveDataHandler.sendMessage(msg);
        }
    };

    private class ReceiveDataHandler extends Handler {

        public ReceiveDataHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "[handleMessage] msg.what : " + msg.what);

            Object obj = msg.obj;
            if (!(obj instanceof ReceivedData)) {
                Log.e(TAG, "[handleMessage] obj is not ReceivedData instance");
                return;
            }

            switch (msg.what) {
                case MSG_HANDLE_BYTES_RECEIVED:
                    handleBytesReceived((ReceivedData) msg.obj);
                    break;

                case MSG_HANDLE_CONNECTION_STATE_CHANGE:
                    handleConnectionStateChanged((ReceivedData) msg.obj);
                    break;

                default:
                    break;
            }
        }
    };

    private void handleBytesReceived(ReceivedData value) {
        if (value == null) {
            Log.e(TAG, "[handleBytesReceived] value is null");
            return;
        }

        ControllerInformation ci = checkExist(value.mTag);
        if (ci == null) {
            Log.e(TAG, "[handleBytesReceived] ControllerInformation is null");
            return;
        }

        int n = ci.mOutControllerCallbackList.beginBroadcast();
        int mm = ci.mOutControllerCallbackList.getRegisteredCallbackCount();
        Log.d(TAG, "[handleBytesReceived] beginbroadcast count : " + n + ", registered count : "
                + mm);
        try {
            for (int i = 0; i < n; i++) {
                if (value.mByteDataBuffer != null) {
                    ci.mOutControllerCallbackList.getBroadcastItem(i).onBytesReceived(
                            value.mByteDataBuffer);
                } else {
                    Log.e(TAG, "[handleBytesReceived] mByteDataBuffer in value is null, continue");
                }
            }
        } catch (RemoteException ex) {
            Log.e(TAG,
                    "[handleBytesReceived] RemoteException Happen ex : " + ex.getLocalizedMessage());
        } finally {
            Log.d(TAG, "[handleBytesReceived] finally enter");
            ci.mOutControllerCallbackList.finishBroadcast();
        }

    }

    private void handleConnectionStateChanged(ReceivedData value) {
        if (value == null) {
            Log.e(TAG, "[handleConnectionStateChanged] value is null");
            return;
        }

        ControllerInformation ci = checkExist(value.mTag);
        if (ci == null) {
            Log.e(TAG, "[handleFileReceived] ControllerInformation is null");
            return;
        }

        int n = ci.mOutControllerCallbackList.beginBroadcast();
        int mm = ci.mOutControllerCallbackList.getRegisteredCallbackCount();
        Log.d(TAG, "[handleConnectionStateChanged] beginbroadcast count : " + n
                + ", registered count : " + mm);
        try {
            for (int i = 0; i < n; i++) {
                ci.mOutControllerCallbackList.getBroadcastItem(i).onConnectionStateChange(
                        value.mConnectionNewState);
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "[handleConnectionStateChanged] RemoteException Happen ex : " + ex.getLocalizedMessage());
        } finally {
            Log.d(TAG, "[handleConnectionStateChanged] finally enter");
            ci.mOutControllerCallbackList.finishBroadcast();
        }
    }

    private class ReceivedData {
        private String mTag;

        byte[] mByteDataBuffer;

        int mConnectionNewState;

        ReceivedData(String tag) {
            mTag = tag;
        }
    }

    /**
     * 
     *
     */
    private class ControllerInformation {
        String mTag;

        IPCControllerEx mController;

        // ControllerBinder mControllerBinder;
        RemoteCallbackList<IControllerCallback> mOutControllerCallbackList;

        ControllerInformation(String tag, IPCControllerEx controller) {
            mTag = tag;
            mController = controller;
            mOutControllerCallbackList = new RemoteCallbackList<IControllerCallback>();
        }
    }

    // Call controller API to do something
    private int init(int cmd_type, String tagName) {
        if (TextUtils.isEmpty(tagName)) {
            Log.d(TAG, "[init] tagname is null or empty");
            return -10;
        }
        ControllerInformation ci = checkExist(tagName);
        if (ci == null) {
            Log.d(TAG, "[init] ci is null");
            IPCControllerEx controller = new IPCControllerEx(cmd_type, tagName, mInternalCallabck);
            int result = controller.initController();
            Log.d(TAG, "[init] result = " + result);
            if (result == IPCControllerEx.INIT_OK) {
                ci = new ControllerInformation(tagName, controller);
                mControllerInformations.add(ci);
            }
            return result;
        }
        return IPCControllerEx.INIT_OK;
    }

    private int getConnectionState() {
        if (WearableManager.getInstance().getConnectState() == WearableManager.STATE_CONNECTED) {
            if (WearableManager.getInstance().isAvailable()) {
                return WearableManager.STATE_CONNECTED;
            } else {
                return WearableManager.STATE_CONNECTING;
            }
        }
        return WearableManager.getInstance().getConnectState();
    }

    private String getRemoteDeviceName() {
        String name = "";
        BluetoothDevice remoteDevice = WearableManager.getInstance().getRemoteDevice();
        Log.d(TAG, "[getRemoteDeviceName] remoteDevice: " + remoteDevice);
        if (remoteDevice != null) {
            String address = remoteDevice.getAddress();
            if (remoteDevice.getName() != null) {
                Log.d(TAG, "[getRemoteDeviceName] remoteDevice Name: " + remoteDevice.getName());
                String queryName = queryDeviceName(address);
                if (!TextUtils.isEmpty(queryName) && !queryName.equals(remoteDevice.getName())) {
                    name = queryName;
                } else {
                    name = remoteDevice.getName();
                }
            } else {
                name = queryDeviceName(address);
            }
            return name;
        }
        return name;
    }

    private String queryDeviceName(String address) {
        SharedPreferences prefs = this.getSharedPreferences("device_name", Context.MODE_PRIVATE);
        String name = prefs.getString(address, "");
        Log.d(TAG, "[queryDeviceName] begin " + address + " -> " + name);
        return name;
    }

    /**
     * @param tagname
     * @param dataToSend
     * @param priority
     * @param linkType
     * @return
     */
    private long sendBytes(String tagname, String cmd, byte[] dataToSend, int priority) {
        if (TextUtils.isEmpty(tagname)) {
            Log.e(TAG, "[sendBytes] tagname is null or empty");
            return -10;
        }
        ControllerInformation ci = checkExist(tagname);
        if (ci == null) {
            Log.e(TAG, "[sendBytes] ci is null");
            return -1;
        }
        IPCControllerEx controller = ci.mController;
        controller.sendBytes(cmd, dataToSend, priority);
        return dataToSend.length;
    }

    // private methods
    /**
     * @param tag
     * @return
     */
    private ControllerInformation checkExist(String tag) {
        if (TextUtils.isEmpty(tag)) {
            Log.e(TAG, "[checkExist] tag is null or EMPTY");
            return null;
        }
        ControllerInformation ci = null;

        Log.d(TAG, "[checkExist] mControllerInformations length : " + mControllerInformations.size());
        for (ControllerInformation c : mControllerInformations) {
            if (c.mTag.equals(tag)) {
                ci = c;
            }
        }
        return ci;
    }

}
