package com.kct.bluetooth.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.kct.bluetooth.KCTBluetoothHelper;
import com.kct.bluetooth.bean.BluetoothLeDevice;
import com.kct.bluetooth.utils.HexUtil;
import com.kct.bluetooth.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kct.bluetooth.bean.KCTGattAttributes.BLE_YDS_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.BLE_YDS_UUID_HUAJING;
import static com.kct.bluetooth.bean.KCTGattAttributes.MTK_YDS_2502_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.MTK_YDS_2503_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_SERVICE_872_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_SERVICE_872_UUID_SCAN;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/3/23
 * 描述: ${VERSION}
 * 修订历史：
 */

public class IScanCallback implements BluetoothAdapter.LeScanCallback{

    private ArrayList<IConnectListener> iConnectListeners;
    private KCTBluetoothHelper helper;
    private BluetoothAdapter mBluetoothAdapter;

    public IScanCallback(ArrayList<IConnectListener> iConnectListeners, KCTBluetoothHelper helper,BluetoothAdapter mBluetoothAdapter){
        this.iConnectListeners = iConnectListeners;
        this.helper = helper;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        if(device.getName() != null){
            if(device.getName().contains("DfuTarg")){
                for (int j = 0; j < iConnectListeners.size(); j++) {
                    iConnectListeners.get(j).onScanDevice(new BluetoothLeDevice(device, rssi, scanRecord));
                }
            }
        }
        final List<UUID> uuidList = Utils.parseFromAdvertisementData(scanRecord);
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < uuidList.size(); i++) {
                    if (uuidList.get(i).equals(BLE_YDS_UUID) || uuidList.get(i).equals(BLE_YDS_UUID_HUAJING)
                            || uuidList.get(i).equals(MTK_YDS_2502_UUID) || uuidList.get(i).equals(MTK_YDS_2503_UUID)) {
                        for (int j = 0; j < iConnectListeners.size(); j++) {
                            iConnectListeners.get(j).onScanDevice(new BluetoothLeDevice(device, rssi, scanRecord));
                        }
                        break;
                    }else if(uuidList.get(i).equals(RX_SERVICE_872_UUID_SCAN)){
                        String scanRecords = HexUtil.encodeHexStr(scanRecord);
                        int deviceNameLength = scanRecord[3] & 0xff;
                        int deviceAddressLength = (8 + deviceNameLength) * 2;
                        String address = scanRecords.substring(deviceAddressLength, deviceAddressLength + 2)
                                + ":" + scanRecords.substring(deviceAddressLength + 2, deviceAddressLength + 4)
                                + ":" + scanRecords.substring(deviceAddressLength + 4, deviceAddressLength + 6)
                                + ":" + scanRecords.substring(deviceAddressLength + 6, deviceAddressLength + 8)
                                + ":" + scanRecords.substring(deviceAddressLength + 8, deviceAddressLength + 10)
                                + ":" + scanRecords.substring(deviceAddressLength + 10, deviceAddressLength + 12);
                        BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(address.toUpperCase());
                        for (int j = 0; j < iConnectListeners.size(); j++) {
                            iConnectListeners.get(j).onScanDevice(new BluetoothLeDevice(device1, rssi, scanRecord));
                        }
                    }
                }

            }
        });
    }
}
