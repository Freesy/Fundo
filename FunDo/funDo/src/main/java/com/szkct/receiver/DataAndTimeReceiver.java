package com.szkct.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

/**
 * Created by ${wyl} on 2017/8/8.
 * 监听当前 日期及时间的变化 更新手环日期及时间
 */

public class DataAndTimeReceiver extends BroadcastReceiver {
    @Override//修改时间后会进入这个里面
    public void onReceive(Context context, Intent intent) {
        //1 判断当前的手环是否连接（连接的话直接同步修改后的时间到手环）
		 if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")
				 || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
			 if (MainService.getInstance().getState()==3) {
	            L2Send.sendSynTime(BTNotificationApplication.getInstance());
	         }
	        //	Log.e("time","有个家伙在修改时间");
	     }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")){
			 if(MainService.getInstance().getState() == 3){
				 BluetoothMtkChat.getInstance().synTime(BTNotificationApplication.getInstance().getApplicationContext());
			 }
		 }
	}		  
}