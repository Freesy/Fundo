package com.kct.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kct.bluetooth.callback.IBluetoothGattCallback;
import com.kct.bluetooth.callback.IConnectListener;
import com.kct.bluetooth.callback.IDFUProgressCallback;
import com.kct.bluetooth.callback.IDialogCallback;

import java.io.File;
import java.util.ArrayList;

import no.nordicsemi.android.dfu.DfuProgressListener;

import static android.R.attr.versionCode;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/10
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTBluetoothManager {

    private static KCTBluetoothManager kctBluetoothManager;
    private ArrayList<IConnectListener> iConnectListeners = new ArrayList<IConnectListener>();
    private KCTBluetoothHelper kctBluetoothHelper;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECT_FAIL = 4;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    
    public static synchronized KCTBluetoothManager getInstance() {
        if(null == kctBluetoothManager) {
            synchronized (KCTBluetoothManager.class){
                if(null == kctBluetoothManager){
                    kctBluetoothManager = new KCTBluetoothManager();
                }
            }

        }
        return kctBluetoothManager;
    }

    public void init(Context context){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","init KctBluetooth");
            kctBluetoothHelper = new KCTBluetoothHelper(context);
        }
    }


    public void connect(BluetoothDevice bluetoothDevice){
        if(null == bluetoothDevice){
            Log.d("KCTBluetoothManager","BluetoothDevice is null!");
            return;
        }
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.connect(bluetoothDevice.getAddress(),iConnectListeners);
    }

    public void scanDevice(boolean scan){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.scanDevice(scan);
    }


    public synchronized void sendCommand_a2d(byte[] buffer){
        if(buffer.length < 0 || null == buffer){
            Log.d("KCTBluetoothManager","the data is null");
            return;
        }
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.write(buffer);
    }

    public synchronized void writeCommand_a2d(byte[] buffer){
        if(buffer.length < 0 || null == buffer){
            Log.d("KCTBluetoothManager","the data is null");
            return;
        }
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.writeToDevice(buffer);
    }


    public void disConnect_a2d(){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.disconnect();
    }


    public int getConnectState(){
        if(kctBluetoothHelper == null){
            return 0;
        }
        return kctBluetoothHelper.getConnectState();
    }

    public  void registerListener(IConnectListener iConnectListener){
        if(!iConnectListeners.contains(iConnectListener)) {
            iConnectListeners.add(iConnectListener);
            if (null != kctBluetoothHelper) {
                kctBluetoothHelper.updateIConnectListener(iConnectListeners);
            }
        }
    }


    public  void unregisterListener(IConnectListener iConnectListener){
        iConnectListeners.remove(iConnectListener);
        if(null != kctBluetoothHelper){
            kctBluetoothHelper.updateIConnectListener(iConnectListeners);
        }
    }

    public BluetoothDevice getConnectDevice(){
        if(null != kctBluetoothHelper){
            return kctBluetoothHelper.getConnectDevice();
        }
        return null;
    }


    public void close(){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.close();
    }

    public void setDilog(boolean isSendFile,IDialogCallback dialogCallback){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.setDialog(isSendFile,dialogCallback);
    }

    public String checkDFU_upgrade(int versionCode){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return null;
        }
        return kctBluetoothHelper.checkDFU_upgrade(versionCode);
    }

    public byte[] getDFU_data(int versionCode){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return null;
        }
        return kctBluetoothHelper.getDFU_data(versionCode);
    }

    public void upgrade_DFU(String filePath,String connectAddress){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        File file = new File(filePath);
        if(!file.exists()){
            Log.d("KCTBluetoothManager","file is null");
            return;
        }
        if(TextUtils.isEmpty(connectAddress)){
            Log.d("KCTBluetoothManager","address is null");
            return;
        }
        kctBluetoothHelper.upgrade_DFU(filePath,connectAddress);
    }

    public void registerDFUProgressListener(IDFUProgressCallback mIDFUProgressCallback){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.registerDFUProgressListener(mIDFUProgressCallback);
    }

    public void unregisterDFUProgressListener(){
        if(null == kctBluetoothHelper){
            Log.d("KCTBluetoothManager","kctBluetooth is null");
            return;
        }
        kctBluetoothHelper.unregisterDFUProgressListener();
    }
}
