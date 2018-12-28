package com.szkct.bluetoothtool;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.view.KeyEvent;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

//Created by edman xie
public class PhoneSet  extends InstrumentationTestCase {
	final static String TAG = "PhoneSet";
	public static PhoneSet mPhone = new PhoneSet();
    final static String   MSG_KEY_ID = "KEY:" ;
    private Context mContext = null;
   // private static Context mContext;

	public static PhoneSet getInstance() { 
		return mPhone;
	}
	public  void simulateKeyStroke(final int KeyCode) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyCode);
				} catch (Exception e) {
					Log.e(TAG, "Instrumentation:"+e);
				}
			}
		}).start(); 
	}
	public  void messageHandle(String msg) {
		Log.d(TAG, "<<<Receive.msg:"+msg);
		String keyEvent = msg.substring(0, MSG_KEY_ID.indexOf(":")+1);
		String keyCode = msg.substring(MSG_KEY_ID.indexOf(":")+1);
		if (keyEvent.equals(MSG_KEY_ID)){
            Log.d(TAG, "ReceiveWatch:KeyCode=" + keyCode);  
			simulateKeyStrokeExt(Integer.valueOf(keyCode));
		}
	}
	public  void simulateKeyStrokeExt(final int KeyCode) {
		Intent it = new Intent();   
		mContext  = BTNotificationApplication.getInstance().getApplicationContext();
		AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        switch (KeyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI); 
 			break;  
		case KeyEvent.KEYCODE_VOLUME_UP:
			am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI); 
			break;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			it.setAction("com.android.music.musicservicecommand.previous");
			mContext.sendBroadcast(it); 	
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			it.setAction("com.android.music.musicservicecommand.togglepause");
			mContext.sendBroadcast(it); 	
			break;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			it.setAction("com.android.music.musicservicecommand.next");
			mContext.sendBroadcast(it); 	
			break;
		case KeyEvent.KEYCODE_MEDIA_STOP:
			it.setAction("com.android.music.musicservicecommand.stop");
			mContext.sendBroadcast(it); 	
			break;
		case KeyEvent.KEYCODE_BACK:
			 Log.d(TAG, ">>>Watch:KeyEvent.KEYCODE_CAMERA... ..."); 
			 /*
			 String currentActivity = mainservice.getComponent().toString();
			if (currentActivity.contains("com.android.music"))
			{
				it.setAction("com.android.music.musicservicecommand.stop");
				mainservice.sendBroadcast(it); 	
			}
			else if (currentActivity.contains("com.example.bluetoothactivity.MyCameraActivity"))
		    {
			    simulateKeyStroke(KeyEvent.KEYCODE_BACK);
			}*/
			simulateKeyStroke(KeyEvent.KEYCODE_BACK);
			//personal_app,com.android.gallery3d---com.android.camera.CameraLauncher
			break;
		
            
		default:
			break;
		}
	}
    private void takePicture() throws Exception {
    	getInstrumentation().sendKeySync(
                new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_FOCUS));
        getInstrumentation().sendCharacterSync(KeyEvent.KEYCODE_CAMERA);
        //Thread.sleep(4000);
    }
}
