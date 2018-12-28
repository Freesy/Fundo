package com.kct.bluetooth.kctmanager;

import com.kct.bluetooth.KCTBluetoothHelper;
import com.kct.bluetooth.callback.IDFUProgressCallback;

import no.nordicsemi.android.dfu.DfuProgressListener;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/4/10
 * 描述: ${VERSION}
 * 修订历史：
 */

public class DFUProgressListener implements DfuProgressListener{

    private IDFUProgressCallback mIDFUProgressListener;
    private KCTBluetoothHelper helper;

    public DFUProgressListener(KCTBluetoothHelper helper,IDFUProgressCallback mIDFUProgressListener){
        this.mIDFUProgressListener = mIDFUProgressListener;
        this.helper = helper;
    }


    @Override
    public void onDeviceConnecting(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDeviceConnecting(deviceAddress);
            }
        });
    }

    @Override
    public void onDeviceConnected(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDeviceConnected(deviceAddress);
            }
        });
    }

    @Override
    public void onDfuProcessStarting(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDfuProcessStarting(deviceAddress);
            }
        });
    }

    @Override
    public void onDfuProcessStarted(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDfuProcessStarted(deviceAddress);
            }
        });
    }

    @Override
    public void onEnablingDfuMode(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onEnablingDfuMode(deviceAddress);
            }
        });
    }

    @Override
    public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onProgressChanged(deviceAddress,percent,speed,avgSpeed,currentPart,partsTotal);
            }
        });
    }

    @Override
    public void onFirmwareValidating(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onFirmwareValidating(deviceAddress);
            }
        });
    }

    @Override
    public void onDeviceDisconnecting(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDeviceDisconnecting(deviceAddress);
            }
        });
    }

    @Override
    public void onDeviceDisconnected(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDeviceDisconnected(deviceAddress);
            }
        });
    }

    @Override
    public void onDfuCompleted(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDfuCompleted(deviceAddress);
            }
        });
    }

    @Override
    public void onDfuAborted(final String deviceAddress) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onDfuAborted(deviceAddress);
            }
        });
    }

    @Override
    public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
        helper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mIDFUProgressListener.onError(deviceAddress,error,errorType,message);
            }
        });
    }
}
