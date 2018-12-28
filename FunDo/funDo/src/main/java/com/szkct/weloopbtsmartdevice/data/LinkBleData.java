package com.szkct.weloopbtsmartdevice.data;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class LinkBleData {

    private BluetoothDevice bluetoothDevice;
    private UUID uuid;
    private String deviceName;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return "LinkBleData{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", uuid=" + uuid +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }

    public LinkBleData(BluetoothDevice bluetoothDevice, UUID uuid, String deviceName) {
        this.bluetoothDevice = bluetoothDevice;
        this.uuid = uuid;
        this.deviceName = deviceName;
    }
}
