package com.szkct.bluetoothgyl;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szkct.weloopbtsmartdevice.main.MainService;

/**
 * Android  利用广播BroadCast监听网络的变化
 * @author 402-9
 */
public class Bluttoothbroadcast extends BroadcastReceiver {
	/*
	 * State wifiState = null; State mobileState = null;
	 * "com.android.music.metachanged"//播放下一首音乐的时候会发送广播;
	 * "com.android.music.queuechanged" 
	 * "com.android.music.playbackcomplete"
	 * "com.android.music.playstatechanged"
	 */
	

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
	
		if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
			// Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
			BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

			if (btAdapter.isEnabled()) {

				//MainService.getInstance().initchat();

			}
		}

		

	}

}