package com.kct.bluetooth.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * @Description: 连接设备回调
 */
public interface IBluetoothGattCallback {

    void onConnectSuccess(BluetoothGatt gatt);

    void onConnectSuccessDFU(BluetoothGatt gatt);

    void onDisconnect();

    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

    void onMtuChanged(BluetoothGatt gatt, int mtu, int status);
}