/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.szkct.weloopbtsmartdevice.main;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private Queue writeQueue = new LinkedList();
    private boolean isWriting = false;
    private Lock mLock = new ReentrantLock();

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_DATA_BYTE =
            "com.example.bluetooth.le.EXTRA_DATA_BYTE";
    public final static String EXTRA_UUID =
            "com.example.bluetooth.le.EXTRA_UUID";
    //[vers|add]
    public final static String ACTION_DATA_WRITE_FAIL = "com.example.bluetooth.le.ACTION_DATA_WRITE_FAIL";
    public final static String ACTION_DATA_WRITE_SUCCESS = "com.example.bluetooth.le.ACTION_DATA_WRITE_SUCCESS";
    public final static String ACTION_DATA_READ_FAIL = "com.example.bluetooth.le.ACTION_DATA_READ_FAIL";
    public final static String ACTION_QUERY_SUCCESS = "com.example.bluetooth.le.QUERY_SUCCESS";
    public final static String ACTION_NOTIFY_SUCCESS = "com.example.bluetooth.le.ACTION_NOTIFY_SUCCESS";
    public final static String ACTION_NOTIFY_FAIL = "com.example.bluetooth.le.ACTION_NOTIFY_FAIL";
    public final static String ACTION_GATT_SERVICES_DISCOVERED_FAIL =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED_FAIL";
    //[vers|end]

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_bbb = UUID.fromString(SampleGattAttributes.UUID_characteristic);
    public final static UUID UUID_aaa = UUID.fromString(SampleGattAttributes.UUID_service);
    public final static UUID UUID_config = UUID.fromString(SampleGattAttributes.UUID_config);
    public final static UUID UUID_changeAddress = UUID.fromString(SampleGattAttributes.UUID_Address_characteristic);
    public final static UUID UUID_OTA_SERVICE = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
    public final static UUID UUID_IDENTFY = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    public final static UUID UUID_BLOCK = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");

    //add var for beken test
    private static UUID uuidService;
    private static UUID uuidCharacteristic;
    private static int writeType = 0;
    //end

    //[vers| add]
    private BluetoothGattCharacteristic mBluetoothGattCharateristic;
    //[vers| end]

    public int getState() {
        try{
            return mConnectionState;
        }catch (NullPointerException E){
            return 0;
        }
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG, "onConnectionStateChange");      // address not equal mConnectedAddress
            Log.e(TAG, "onConnectionStateChange" + gatt.getDevice().getAddress());
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;     // 仅打印log供调试
                mConnectionState = STATE_CONNECTED;
                Log.e(TAG, "Connected to GATT server");
                broadcastUpdate(intentAction);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;    // 升级成功断开连接，关闭升级页面
                mConnectionState = STATE_DISCONNECTED;

                //[chengYi | make sure isWriting set false]
                isWriting = false;

                Log.e(TAG, "Disconnected from GATT server");
                broadcastUpdate(intentAction);
                close();  // add lx
            } else {
                Log.e(TAG, "other stage");
                close();  // add lx
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);   // 建立GATT服务，开始 通知使能
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED_FAIL);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            final byte[] data = characteristic.getValue();
            if (characteristic.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE && status == BluetoothGatt.GATT_SUCCESS) {
//                if (data != null && data.length > 0) {
//                    final StringBuilder stringBuilder = new StringBuilder(data.length);
//                    for (byte byteChar : data) {
//                        stringBuilder.append(String.format("%02X", byteChar));
//                    }
//                    Log.e(TAG, stringBuilder.toString());
//                }
//                broadcastUpdate(ACTION_DATA_WRITE_SUCCESS, characteristic);
                EventBus.getDefault().post(new CheckboxEvent(11));     // todo  -- 设置进度条的 进度
                mLock.lock();
                isWriting = false;
//                Log.e(TAG, "isWrite reset done");
                mLock.unlock();

//                writeNextValueFromQueue();
            }else if(characteristic.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE && status != BluetoothGatt.GATT_SUCCESS) {
                //Log.e(TAG, "write block fail");
//                broadcastUpdate(ACTION_DATA_WRITE_FAIL, characteristic);
                EventBus.getDefault().post(new CheckboxEvent(10));
                mLock.lock();
                isWriting = false;
                mLock.unlock();

            }else if(characteristic.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT && status == BluetoothGatt.GATT_SUCCESS) {
//                if (data != null && data.length > 0) {
//                    final StringBuilder stringBuilder = new StringBuilder(data.length);
//                    for (byte byteChar : data) {
//                        stringBuilder.append(String.format("%02X", byteChar));
//                    }
//                    Log.e(TAG, stringBuilder.toString());
//                }
            }/*
            else if (new String(data).toString().equals("EF") && status == BluetoothGatt.GATT_SUCCESS) {
                gatt.executeReliableWrite();
                broadcastUpdate(ACTION_DATA_WRITE_SUCCESS, characteristic);
            } else if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.executeReliableWrite();
                broadcastUpdate(ACTION_DATA_WRITE_SUCCESS, characteristic);
            }*/ else {
//                broadcastUpdate(ACTION_DATA_WRITE_FAIL, characteristic);
                EventBus.getDefault().post(new CheckboxEvent(10));
                mLock.lock();
                isWriting = false;
                mLock.unlock();
                //gatt.abortReliableWrite();

            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            mLock.lock();
            isWriting = false;
            mLock.unlock();
            writeNextValueFromQueue();
            broadcastUpdate(ACTION_DATA_WRITE_SUCCESS);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final byte[] data = descriptor.getValue();
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format(Locale.ENGLISH,"%02X ", byteChar));
                    Log.e(TAG, "onDescriptorWrite " + stringBuilder.toString() + " ");
                }
            } else {
                Log.e(TAG, "onDescriptorWrite is not success");
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final byte[] data = descriptor.getValue();
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format(Locale.ENGLISH,"%02X ", byteChar));
                    Log.e(TAG, "onDescriptorRead " + stringBuilder.toString() + " ");
                    Intent intent = new Intent(ACTION_NOTIFY_SUCCESS);
                    intent.putExtra(EXTRA_DATA, stringBuilder.toString());
                    sendBroadcast(intent);
                } else {
                    Log.e(TAG, "onDescriptorRead data length wrong");
                    broadcastUpdate(ACTION_NOTIFY_FAIL);
                }
            } else {
                Log.e(TAG, "onDescriptorRead is not success");
                broadcastUpdate(ACTION_NOTIFY_FAIL);
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format(Locale.ENGLISH,"%02X", byteChar));
//                Log.e(TAG, "broadcastUpdate " + stringBuilder.toString() + " " + action.toString());
                intent.putExtra(EXTRA_DATA, stringBuilder.toString());
                intent.putExtra(EXTRA_DATA_BYTE, characteristic.getValue());
                intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        Log.e(TAG, "onUnBind call");
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public void QueryCommand() {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }
                mBluetoothGattCharateristic.setValue(new byte[]{0x01, (byte) 0xff, (byte) 0xff});
//                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false write 1");
                }else
                {
                    Log.e(TAG, "true write");
                }
            } else {
                Log.e(TAG, "get service fail");
            }
        }
    }

    public void writeValue(byte[] strValue) {
//        final StringBuilder stringBuilder = new StringBuilder(strValue.length);
//        for (byte byteChar : strValue) {
//            stringBuilder.append(String.format("%02X ", byteChar));
//        }
        if(writeQueue.size() < 100) {
            writeQueue.add(strValue);
            writeNextValueFromQueue();
        }
        else
        {
            Log.e(TAG, "queue is full");
        }
    }

    public void clearQueue()
    {
        mLock.lock();
        writeQueue.clear();
        isWriting = false;
        mLock.unlock();
        Log.e(TAG, "clean done2");
    }

    public void clearQueue(byte[] byteArray)
    {
        mLock.lock();
        writeQueue.clear();
        mLock.unlock();
        Log.e(TAG, "clean done");
        while(writeQueue.size() != 0)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final StringBuilder stringBuilder = new StringBuilder(byteArray.length);
        for (byte byteChar : byteArray) {
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X ", byteChar));
        }
        Log.e(TAG, stringBuilder.toString());

        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }

                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(byteArray);

                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false write 2");
                }
            } else {
                Log.e(TAG, "get service fail");
            }
        }
    }

    private void writeNextValueFromQueue() {
        if(mBluetoothGatt == null)
        {
            isWriting = false;
            writeQueue.poll();
            //Log.e(TAG, "11 disconnect");
            return;
        }

        if (isWriting) {
            Log.e(TAG, "isBusy");
            return;
        }
        if (writeQueue.size() == 0) {
            return;
        }

        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(uuidService);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(uuidCharacteristic);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }
                mLock.lock();
                isWriting = true;
                Log.e(TAG, "isWrite set true");
                if(writeType == 0) {
                    mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                }else
                {
                    mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//                    mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                }
                mBluetoothGattCharateristic.setValue((byte[]) writeQueue.poll());
                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                mLock.unlock();
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false write 3");
                }
            } else {
                Log.e(TAG, "get service fail");
            }
        }
    }

    public void setAllLightOn() {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }

                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(new byte[]{0x10, (byte) 0xff, (byte) 0xff, 0x01, 0x01});
                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false write 4");
                }
            } else {
                Log.e(TAG, "get service fail");
            }
        }
    }

    public void setAllLightOff() {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }

                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(new byte[]{0x10, (byte) 0xff, (byte) 0xff, 0x01, 0x00});

                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false write 5");
                }

            } else {
                Log.e("TAG", "get service fail");
            }
        }
    }

    public void testRead() {
        if (mBluetoothGatt != null && mConnectionState == STATE_CONNECTED) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                Log.e(TAG, "ready to read");
                mBluetoothGatt.readCharacteristic(service.getCharacteristic(UUID_bbb));
                return;
            }
        }
        Log.e(TAG, "read fail");
        broadcastUpdate(ACTION_DATA_READ_FAIL);
        return;
    }

    public void testDiscoverService() {
        Log.e(TAG, "DiscoverService");
        if (mBluetoothGatt != null) {
            boolean flag = mBluetoothGatt.discoverServices();

            if (flag) {
                Log.e(TAG, "discoverServices success");
            } else {
                Log.e(TAG, "discoverServices fail");
            }
        }
    }

    //query group list
    public void queryGroupList(String nodeName) {
        byte arr[] = new byte[2];
        arr[0] = (byte) Integer.parseInt(nodeName.subSequence(0, 2).toString(), 16);
        arr[1] = (byte) Integer.parseInt(nodeName.subSequence(3, 5).toString(), 16);
        if (mBluetoothGatt != null) {
//            boolean flagRaliableWrite = mBluetoothGatt.beginReliableWrite();

//            if(flagRaliableWrite == true) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                Log.e(TAG, "get service");
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }
//                    mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(new byte[]{0x06, arr[0], arr[1]});

                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false");
                }
            }
//            }
        }
    }

    //for listViewAdapter
    public void RemoveAddGroup(byte[] arr) {
        if (mBluetoothGatt != null) {
//            boolean flagRaliableWrite = mBluetoothGatt.beginReliableWrite();
//            if(flagRaliableWrite == true) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                }
//                    mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(arr);

                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) Log.e(TAG, "false");
            }
//            }
        }
    }

    public void rssiTest() {
        mBluetoothGatt.readRemoteRssi();
    }

    public void oneNodeLightOnOff(String nodeName, int mode) {
        byte arr[] = new byte[2];
        arr[0] = (byte) Integer.parseInt(nodeName.subSequence(0, 2).toString(), 16);
        arr[1] = (byte) Integer.parseInt(nodeName.subSequence(3, 5).toString(), 16);
        if (mBluetoothGatt != null) {
//            boolean flagRaliableWrite = mBluetoothGatt.beginReliableWrite();
//            if(flagRaliableWrite == true) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
            if (service != null) {
                Log.e(TAG, "get service");
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return;
                } else {
                    Log.e(TAG, "not null");
                }
//                    mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                if (mode == 0) {
                    mBluetoothGattCharateristic.setValue(new byte[]{0x10, arr[0], arr[1], 0x01, 0x01});
                } else {
                    mBluetoothGattCharateristic.setValue(new byte[]{0x10, arr[0], arr[1], 0x01, 0x00});
                }
                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) Log.e(TAG, "false");
            }
//            }
        }
    }

    public void testChangeAddress() {
        BluetoothGattService service = mBluetoothGatt.getService(UUID_config);
        if (service != null) {
            mBluetoothGattCharateristic = service.getCharacteristic(UUID_changeAddress);
            if (mBluetoothGattCharateristic == null) {
                Log.e(TAG, "mBluetoothGattCharateristic is null");
                return;
            }
            mBluetoothGattCharateristic.setValue(new byte[]{0x02, 0x02});
            mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
        }
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(String address) { // final String address
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            mBluetoothGatt.disconnect();

            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                //mBluetoothGatt.connect();
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.

        //chengyi to false //2017|10|25|1509|
        //set true will cause app can not get services
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
//        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);

        if (mBluetoothGatt != null) {
            Log.e(TAG, "Trying to create a new connection.");
            mBluetoothDeviceAddress = address;
//            mBluetoothGatt.
            if (mBluetoothGatt.connect()) {
                //mBluetoothGatt.connect();
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        } else return false;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.e(TAG, "disconnect");
        mBluetoothGatt.disconnect();
//        close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        Log.e(TAG, "close");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                 boolean enabled) {

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        boolean returnValue = false;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        Log.e(TAG, characteristic.getUuid().toString());
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        UUID uuida = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        BluetoothGattDescriptor desc = characteristic.getDescriptor(uuida);
//        BluetoothGattDescriptor desc = characteristic.getDescriptor(characteristic.getUuid());
        if (enabled) {
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            returnValue = mBluetoothGatt.writeDescriptor(desc);
            Log.e(TAG, "enabled true 成功了-----BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE");
        } else {
            desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            returnValue = mBluetoothGatt.writeDescriptor(desc);
            Log.e(TAG, "enabled false 成功了-----BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE");
        }
        // This is specific to Heart Rate Measurement.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
        return returnValue;
    }

    public boolean setCharacteristicIndication(BluetoothGattCharacteristic characteristic,
                                                 boolean enabled) {
        boolean returnValue = false;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        Log.e(TAG, characteristic.getUuid().toString());
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        UUID uuida = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        BluetoothGattDescriptor desc = characteristic.getDescriptor(uuida);
//        BluetoothGattDescriptor desc = characteristic.getDescriptor(characteristic.getUuid());
        if (enabled) {
            desc.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            returnValue = mBluetoothGatt.writeDescriptor(desc);
        } else {
            desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            returnValue = mBluetoothGatt.writeDescriptor(desc);
        }
        return returnValue;
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    public void sendCommand(String cmd, int length) {

        byte arr[] = new byte[length / 2];
        int commandLength = 0;
        for (int forCount = 0; forCount < length - 1 && commandLength < length; forCount = forCount + 2) {
            arr[commandLength] = (byte) Integer.parseInt(cmd.subSequence(forCount, forCount + 2).toString(), 16);
            commandLength++;
        }

        writeQueue.add(arr);
        writeNextValueFromQueue();
//        if (mBluetoothGatt != null) {
//            BluetoothGattService service = mBluetoothGatt.getService(UUID_aaa);
//            if (service != null) {
//                mBluetoothGattCharateristic = service.getCharacteristic(UUID_bbb);
//                if (mBluetoothGattCharateristic == null) {
//                    Log.e(TAG, "mBluetoothGattCharateristic is null");
//                    return;
//                }
//                byte arr[] = new byte[length / 2];
//                int commandLength = 0;
//                for (int forCount = 0; forCount < length - 1 && commandLength < length; forCount = forCount + 2) {
//                    arr[commandLength] = (byte) Integer.parseInt(cmd.subSequence(forCount, forCount + 2).toString(), 16);
//                    commandLength++;
//                }
//
//                mBluetoothGattCharateristic.setValue(arr);
//
//                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
//                Log.e(TAG, cmd.toString());
//                if (flagWriteCharacteristic == false) {
//                    Log.e(TAG, "false write");
//                }
//            } else {
//                Log.e(TAG, "get service fail");
//            }
//        }
    }

    public void readDescriptor(BluetoothGattDescriptor descriptor) {
        mBluetoothGatt.readDescriptor(descriptor);
    }

    public void setServiceandCharacteristic(UUID service , UUID characteristic, int mode)
    {
        uuidService = service;
        uuidCharacteristic = characteristic;
        writeType = mode;
    }

    public boolean writeOTAIdentfy(byte[] byteArray) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_OTA_SERVICE);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_IDENTFY);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return false;
                }

                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(byteArray);
                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
                    Log.e(TAG, "false write 6");
                    return false;
                }else {
                    return true;
                }
            } else {
                Log.e(TAG, "get service fail");
                return false;
            }
        }
        return false;
    }


    public boolean writeOTABlock(byte[] byteArray) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service = mBluetoothGatt.getService(UUID_OTA_SERVICE);
            if (service != null) {
                mBluetoothGattCharateristic = service.getCharacteristic(UUID_BLOCK);
                if (mBluetoothGattCharateristic == null) {
                    Log.e(TAG, "mBluetoothGattCharateristic is null");
                    return false;
                }

                mBluetoothGattCharateristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothGattCharateristic.setValue(byteArray);
                boolean flagWriteCharacteristic = mBluetoothGatt.writeCharacteristic(mBluetoothGattCharateristic);
                if (!flagWriteCharacteristic) {
//                    Log.e(TAG, "false write");
                    return false;
                }else
                {
                    return true;
                }
            } else {
                Log.e(TAG, "get service fail");
                return false;
            }
        }
        return false;
    }

    public String getCharacteristicUUID()
    {
        return uuidCharacteristic.toString();
    }

}
