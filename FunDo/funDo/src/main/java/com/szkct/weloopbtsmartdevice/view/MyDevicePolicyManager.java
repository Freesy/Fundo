package com.szkct.weloopbtsmartdevice.view;


import com.kct.fundo.btnotification.R;
import com.szkct.lock.LockReceiver;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class MyDevicePolicyManager extends Activity{


	private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}
        setContentView(R.layout.activity_main);
        
  
        componentName = new ComponentName(this, LockReceiver.class);
            activeManager();//激活设备管理器获取权限 
        
    }
    
    private void activeManager() { 
        //使用隐式意图调用系统方法来激活指定的设备管理器 
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName); 
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏"); 
       startActivity(intent);  
       finish();
    } 
}
