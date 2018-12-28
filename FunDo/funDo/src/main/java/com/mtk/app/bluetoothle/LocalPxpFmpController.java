package com.mtk.app.bluetoothle;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.kct.fundo.btnotification.R;
import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.leprofiles.bas.BatteryChangeListener;
import com.mediatek.leprofiles.fmppxp.CalibrateListener;
import com.mediatek.leprofiles.fmppxp.PxpEventProcessor;



public class LocalPxpFmpController {

    // custimizable values
    private static final int RANGE_ALERT_THRESH_NEAR = 70;
    private static final int RANGE_ALERT_THRESH_FAR = 90;
    private static final int RSSI_TOLERANCE_NEAR = 3;
    private static final int RSSI_TOLERANCE_FAR = 5;
    private static final int READ_RSSI_DEALY = 500;

    // battery level threshold
    public static final int BATTERY_LEVEL_1 = 33;
    public static final int BATTERY_LEVEL_2 = 66;
    public static final int BATTERY_LEVEL_3 = 100;
    public static void initPxpFmpFunctions(final Context context) {
        int profiles = context.getResources().getInteger(R.integer.supported_gatt_profiles);
        LocalBluetoothLEManager.getInstance().init(context, profiles);
        // This is an example for customize the rssi processor for PXP feature
        PxpEventProcessor testProcessor = new PxpEventProcessor() {
            int mTxpower = 0;
            private static final String TAG = "TEST_PROCESSOR";
            @Override
            public void onTxPowerRead(int txPower) {
                Log.d(TAG, "onTxPowerRead: " + txPower);
                mTxpower = txPower;
            }

            @Override
            public void onReadRssi(int rssi) {
                Log.d(TAG, "onReadRssi: " + rssi);
                if (mTxpower - rssi < RANGE_ALERT_THRESH_NEAR) {
                    Log.d(TAG, "normal");
                    LocalBluetoothLEManager.getInstance().notifyPxpAlertChanged(
                            BlePxpFmpConstants.STATE_NO_ALERT);
                } else {
                    Log.d(TAG, "out range");
                    LocalBluetoothLEManager.getInstance().notifyPxpAlertChanged(
                            BlePxpFmpConstants.STATE_OUT_RANGE_ALERT);
                }
            }
        };
        /*
         * This is an example for customize the rssi processor for PXP feature
        LocalBluetoothLEManager.getInstance().setCustomerPxpEventProcessor(testProcessor);
        */
    }


    public static void findTargetDevice(int level) {
        LocalBluetoothLEManager.getInstance().findTargetDevice(level);
    }

    public static void stopRemotePxpAlert(BluetoothDevice device) {
        LocalBluetoothLEManager.getInstance().stopRemotePxpAlert(device);
    }

    public static void calibrateAlertThreshold(CalibrateListener listener, long time) {
        LocalBluetoothLEManager.getInstance().calibrateAlertThreshold(listener, time);
    }

    public static void registerBatteryLevelListener(BatteryChangeListener listener) {
        LocalBluetoothLEManager.getInstance()
                .registerBatteryLevelListener(listener);
    }

    public static void unregisterBatteryLevelListener() {
        LocalBluetoothLEManager.getInstance()
                .unregisterBatteryLevelListener();
    }
}
