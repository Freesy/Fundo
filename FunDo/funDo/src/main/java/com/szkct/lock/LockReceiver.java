package com.szkct.lock;


import com.szkct.weloopbtsmartdevice.main.MainService;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends DeviceAdminReceiver {
	 @Override 
	    public void onReceive(Context context, Intent intent) { 
	        super.onReceive(context, intent); 
	        System.out.println("onreceiver"); 
	    } 
	   
	    @Override 
	    public void onEnabled(Context context, Intent intent) { 
	        System.out.println("激活使用"); 
	        MainService.getInstance().sendMessage("acti");
	        super.onEnabled(context, intent); 
	    } 
	   
	    @Override 
	    public void onDisabled(Context context, Intent intent) { 
	        System.out.println("取消激活"); 
	        MainService.getInstance().sendMessage("unac");
	        super.onDisabled(context, intent); 
	    } 
}
