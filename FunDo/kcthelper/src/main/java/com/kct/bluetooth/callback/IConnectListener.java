package com.kct.bluetooth.callback;

import android.bluetooth.BluetoothDevice;

import com.kct.bluetooth.bean.BluetoothLeDevice;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/10/21
 * 描述: ${VERSION}
 * 修订历史：
 */

public interface IConnectListener {

    void onConnectState(int state);

    void onConnectDevice(BluetoothDevice device);

    void onScanDevice(BluetoothLeDevice device);

    void onCommand_d2a(byte[] bytes);
}
