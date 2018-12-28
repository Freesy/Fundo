package com.kct.bluetooth.callback;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/4/10
 * 描述: ${VERSION}
 * 修订历史：
 */

public interface IDFUProgressCallback {

    void onDeviceConnecting(String deviceAddress);

    void onDeviceConnected(String deviceAddress);

    void onDfuProcessStarting(String deviceAddress);

    void onDfuProcessStarted(String deviceAddress);

    void onEnablingDfuMode(String deviceAddress);

    void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal);

    void onFirmwareValidating(String deviceAddress);

    void onDeviceDisconnecting(String deviceAddress);

    void onDeviceDisconnected(String deviceAddress);

    void onDfuCompleted(String deviceAddress);

    void onDfuAborted(String deviceAddress);

    void onError(String deviceAddress, int error, int errorType, String message);
}
