package com.kct.bluetooth.kctmanager;

import android.util.Log;

import com.kct.bluetooth.KCTBluetoothHelper;
import com.kct.bluetooth.KCTBluetoothManager;
import com.kct.bluetooth.utils.LogUtil;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/3/23
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTConnectRunnable implements Runnable{

    private KCTBluetoothHelper helper;

    public KCTConnectRunnable(KCTBluetoothHelper helper){
        this.helper = helper;
    }

    @Override
    public void run() {
        if (helper.getConnectState() != KCTBluetoothManager.STATE_CONNECTED) {
            LogUtil.d("[KCTConnect]", "connect time over");
            helper.disconnect();
            helper.close();
        }
    }
}
