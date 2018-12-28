package com.kct.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/6/8
 * 描述: ${VERSION}
 * 修订历史：
 */

public class KCTBroadcastReceive extends BroadcastReceiver{
    private Context context;
    private KCTBluetoothHelper helper;

    public KCTBroadcastReceive(Context context,KCTBluetoothHelper helper){
        this.context = context;
        this.helper = helper;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if(blueState == BluetoothAdapter.STATE_OFF){
                helper.disconnect();
                helper.close();
                helper.setConnectState(KCTBluetoothManager.STATE_CONNECT_FAIL);
            }
        }
    }
}
