package com.szkct.weloopbtsmartdevice.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/8/14
 * 描述: ${VERSION}
 * 修订历史：
 */

public class LocalChangeReceiver extends BroadcastReceiver{    //监听语言变化情况
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            Log.e("LocalChangeReceiver","Language change");
            if(MainService.getInstance().getState() == 3){
                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){
                    L2Send.getSystrmUserData(context);  //设置系统设置
                }
            }
        }
    }
}
