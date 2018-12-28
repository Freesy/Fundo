package com.szkct.bluetoothservice;


import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.PhoneUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
 
public class PhoneReceiver extends BroadcastReceiver {
    String TAG = "PhoneReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);
        if(MainService.getInstance().getState()!=3){
        	Log.i("onCallStateChanged", "ble未连接");
        	return ;
        }
        switch (tm.getCallState()) {
        case TelephonyManager.CALL_STATE_OFFHOOK:// 电话打进来接通状态；电话打出时首先监听到的状态。
            Log.i("onCallStateChanged", "CALL_STATE_OFFHOOK");
            MainService.getInstance(). phonelingk=true;
            if(intent.getStringExtra("incoming_number")==null){
            	 MainService.getInstance().getcall(null);
            /*	 MainService.getInstance().initOrCloseBtCheck(false);
            	  MainService.getInstance().disconnectHFP();*/
            }
           
            break;
        case TelephonyManager.CALL_STATE_RINGING:// 电话打进来状态
            Log.i("onCallStateChanged", "CALL_STATE_RINGING"+intent.getStringExtra("incoming_number"));
            //mIncomingNumber就是来电号码
            MainService.getInstance(). phonelingk=false;
           MainService.getInstance().getcall(intent.getStringExtra("incoming_number"));
           //MainService.getInstance().initOrCloseBtCheck(true);
           
      /*     MainService.getInstance().connectHFP();*/
            break;
        case TelephonyManager.CALL_STATE_IDLE:// 不管是电话打出去还是电话打进来都会监听到的状态。
            Log.i("onCallStateChanged", "CALL_STATE_IDLE");
            if(!MainService.getInstance(). phonelingk){
            	MainService.getInstance().callend();
            }
            
            break;
        }
    }
 
     
}