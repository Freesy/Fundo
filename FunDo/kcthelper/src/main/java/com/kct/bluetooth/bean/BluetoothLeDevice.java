package com.kct.bluetooth.bean;

import android.bluetooth.BluetoothDevice;

/**
 * @Description: 设备信息
 */
public class BluetoothLeDevice {

    private BluetoothDevice device;
    private int rssi;
    private byte[] scanRecord;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public BluetoothLeDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public BluetoothLeDevice() {
    }


}
