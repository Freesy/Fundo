package com.kct.bluetooth.kctmanager;


import android.bluetooth.BluetoothGattService;
import android.os.Handler;

import com.kct.bluetooth.KCTReceiveCommand;
import com.kct.bluetooth.KCTSendCommand;

import static com.kct.bluetooth.bean.KCTGattAttributes.RX_SERVICE_872_UUID;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/3/23
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTSendDataRunnable implements Runnable {

    private KCTSendCommand kctSendCommand;
    private KCTReceiveCommand kctReceiveCommand;
    private Handler handler;
    private BluetoothGattService RxService;

    public KCTSendDataRunnable(KCTSendCommand kctSendCommand, KCTReceiveCommand kctReceiveCommand
            , Handler handler, BluetoothGattService RxService){
        this.kctSendCommand = kctSendCommand;
        this.kctReceiveCommand = kctReceiveCommand;
        this.handler = handler;
        this.RxService = RxService;
    }

    @Override
    public void run() {
        if (null != kctSendCommand && !kctReceiveCommand.getDataBuffer().burDataBegin) {
            kctSendCommand.reCancel(false);
            if(RxService.getUuid().equals(RX_SERVICE_872_UUID)){
                handler.postDelayed(this,6000);
            }else {
                handler.postDelayed(this,5000);
            }
        }

    }
}
