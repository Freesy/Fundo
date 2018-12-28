package com.kct.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import com.kct.bluetooth.bean.KCTGattAttributes;
import com.kct.bluetooth.callback.IBluetoothGattCallback;
import com.kct.bluetooth.utils.HexUtil;
import com.kct.bluetooth.utils.LogUtil;

import java.util.List;
import java.util.UUID;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/10
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTBluetoothGattCallback extends BluetoothGattCallback {

    private IBluetoothGattCallback iBluetoothGattCallback;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic RxChar;
    private BluetoothGattCharacteristic TxChar;
    private BluetoothGattService RxService;

    private KCTBluetoothHelper helper;

    private UUID TX_uuid;

    private UUID RX_uuid;

    private UUID Service_uuid;


    public KCTBluetoothGattCallback() {}

    public KCTBluetoothGattCallback(IBluetoothGattCallback iBluetoothGattCallback
            , KCTBluetoothHelper helper) {
        this.iBluetoothGattCallback = iBluetoothGattCallback;
        this.helper = helper;
    }


    @Override
    public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
        Log.d("[KCTBluetoothGattCallback]","status = " + status + " ; newState = " + newState);
        if(BluetoothProfile.STATE_CONNECTED == newState){
            Log.i("[KCTBluetoothGattCallback]", "Connected to GATT server.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(iBluetoothGattCallback != null){
                helper.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        iBluetoothGattCallback.onConnectSuccessDFU(gatt);
                    }
                });
            }
            Log.i("[KCTBluetoothGattCallback]", "Attempting to start service discovery:" +
                    gatt.discoverServices());
        } else if(BluetoothProfile.STATE_DISCONNECTED == newState){
            if(iBluetoothGattCallback != null){
                helper.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        iBluetoothGattCallback.onDisconnect();
                    }
                });
            }
            //helper.setConnectState(KCTBluetoothManager.STATE_CONNECT_FAIL);
            Log.i("[KCTBluetoothGattCallback]", "Disconnected from GATT server.");
        } else if(BluetoothProfile.STATE_CONNECTING == newState){
            helper.setConnectState(KCTBluetoothManager.STATE_CONNECTING);
            Log.i("[KCTBluetoothGattCallback]", "Connecting to GATT server.");
        }
    }

    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
        Log.d("[KCTBluetoothGattCallback]","onServicesDiscovered = " + status);
        if(status == BluetoothGatt.GATT_SUCCESS){

            if (gatt == null) {
                return;
            }
            mBluetoothGatt = gatt;

            if(mBluetoothGatt.getDevice().getName().contains("DfuTarg")){
                helper.setConnectState(KCTBluetoothManager.STATE_CONNECTED);
                if(iBluetoothGattCallback != null){
                    helper.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            iBluetoothGattCallback.onConnectSuccess(gatt);
                        }
                    });
                }
                return;
            }
            List<BluetoothGattService> services = gatt.getServices();
            if(services != null && services.size() > 0) {
                for (int i = 0; i < services.size(); i++) {
                    if (services.get(i).getUuid() != null) {
                        if (services.get(i).getUuid().toString().equals(KCTGattAttributes.RX_SERVICE_UUID.toString()) ||
                                services.get(i).getUuid().toString().equals(KCTGattAttributes.RX_SERVICE_872_UUID.toString())
                                || services.get(i).getUuid().toString().equals(KCTGattAttributes.RX_SERVICE_872_UUID_SCAN.toString())) {
                            Service_uuid = services.get(i).getUuid();
                            RxService = gatt.getService(Service_uuid);
                            break;
                        }
                    }
                }
            }else{
                Log.d("[KCTBluetoothGattCallback]","gatt is not services");
                if (iBluetoothGattCallback != null) {
                    helper.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            iBluetoothGattCallback.onDisconnect();
                        }
                    });
                }
            }
            if (RxService != null) {
                List<BluetoothGattCharacteristic> characteristics = RxService.getCharacteristics();
                if (characteristics != null && characteristics.size() > 0) {
                    for (int i = 0; i < characteristics.size(); i++) {
                        if (characteristics.get(i).getUuid().toString().equals(KCTGattAttributes.TX_CHAR_UUID.toString())
                                || characteristics.get(i).getUuid().toString().equals(KCTGattAttributes.TX_CHAR_872_UUID.toString())) {
                            TX_uuid = characteristics.get(i).getUuid();
                            TxChar = RxService.getCharacteristic(TX_uuid);
                        } else if (characteristics.get(i).getUuid().toString().equals(KCTGattAttributes.RX_CHAR_UUID.toString())
                                || characteristics.get(i).getUuid().toString().equals(KCTGattAttributes.RX_CHAR_872_UUID.toString())) {
                            RX_uuid = characteristics.get(i).getUuid();
                            RxChar = RxService.getCharacteristic(RX_uuid);
                        }

                    }
                }
                mBluetoothGatt.setCharacteristicNotification(TxChar, true);
                mBluetoothGatt.setCharacteristicNotification(RxChar, true);


                List<BluetoothGattDescriptor> descriptors = TxChar.getDescriptors();
                if (descriptors != null && descriptors.size() > 0) {
                    for (int i = 0; i < descriptors.size(); i++) {
                        if (descriptors.get(i).getUuid() != null &&
                                descriptors.get(i).getUuid().equals(KCTGattAttributes.DESC)) {
                            BluetoothGattDescriptor descriptor = TxChar.getDescriptor(KCTGattAttributes.DESC);
                            boolean isSetValue = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            if (isSetValue) {
                                mBluetoothGatt.writeDescriptor(descriptor);
                            } else {
                                mBluetoothGatt.disconnect();
                                Log.i("[KCTBluetoothGattCallback]", "GATT is disConnected and getDescriptor is false");
                                if (iBluetoothGattCallback != null) {
                                    helper.runOnMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            iBluetoothGattCallback.onDisconnect();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }else{
                Log.d("[KCTBluetoothGattCallback]","gatt is not service");
                if (iBluetoothGattCallback != null) {
                    helper.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            iBluetoothGattCallback.onDisconnect();
                        }
                    });
                }
            }
            if(RxChar != null) {
                mBluetoothGatt.readRemoteRssi();

                Log.i("[KCTBluetoothGattCallback]", "GATT is connected.");
                if (iBluetoothGattCallback != null) {
                    helper.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            iBluetoothGattCallback.onConnectSuccess(gatt);
                        }
                    });
                }
            }else{
                Log.d("[KCTBluetoothGattCallback]","gatt is not characteristic");
                if (iBluetoothGattCallback != null) {
                    helper.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            iBluetoothGattCallback.onDisconnect();
                        }
                    });
                }

            }
        }else{
            Log.i("[KCTBluetoothGattCallback]", "GATT is disConnected.");
            if(iBluetoothGattCallback != null) {
                helper.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        iBluetoothGattCallback.onDisconnect();
                    }
                });
            }
        }
    }

    @Override
    public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
        LogUtil.d("[KCTBluetoothGattCallback]","onCharacteristicRead = " + status);
        if(iBluetoothGattCallback != null) {
            iBluetoothGattCallback.onCharacteristicRead(gatt, characteristic, status);
        }

    }

    @Override
    public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
        LogUtil.d("[KCTBluetoothGattCallback]","onCharacteristicWrite = " + HexUtil.encodeHexStr(characteristic.getValue()));
        if(iBluetoothGattCallback != null){
            helper.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    iBluetoothGattCallback.onCharacteristicWrite(gatt,characteristic,status);
                }
            });

        }
    }

    @Override
    public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        LogUtil.d("[KCTBluetoothGattCallback]","onCharacteristicChanged = " + HexUtil.encodeHexStr(characteristic.getValue()));
        if(iBluetoothGattCallback != null){
            iBluetoothGattCallback.onCharacteristicChanged(gatt,characteristic);
        }
    }


    @Override
    public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
        Log.d("[KCTBluetoothGattCallback]","onReadRemoteRssi : " + "rssi = " + rssi);
        if(iBluetoothGattCallback != null){
            helper.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    iBluetoothGattCallback.onReadRemoteRssi(gatt,rssi,status);
                }
            });

        }
    }

    @Override
    public void onMtuChanged(final BluetoothGatt gatt, final int mtu, final int status) {
        super.onMtuChanged(gatt, mtu, status);
        Log.d("[KCTBluetoothGattCallback]","support mtu size = " + mtu);
        if(iBluetoothGattCallback != null){
            helper.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    iBluetoothGattCallback.onMtuChanged(gatt,mtu,status);
                }
            });

        }
    }

    @Override
    public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Log.d("[KCTBluetoothGattCallback]","onDescriptorWrite status = " + status + " ; gatt = " + gatt.getDevice().getAddress());
    }
}
