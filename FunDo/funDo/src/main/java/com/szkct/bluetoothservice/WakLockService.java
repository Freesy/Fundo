package com.szkct.bluetoothservice;



import android.app.KeyguardManager;  
import android.app.KeyguardManager.KeyguardLock;  
import android.app.Service;  
import android.content.Context;  
import android.content.Intent;  
import android.os.IBinder;  
import android.os.PowerManager;  
import android.util.Log;  

public class WakLockService extends Service {  
    
  private static final String TAG="WakLockService";  
    
  // ���̹�����    
  KeyguardManager keyguardManager;  
  // ������    
  private KeyguardLock keyguardLock;  
  // ��Դ������    
  private PowerManager powerManager;  
  // ������    
  private PowerManager.WakeLock wakeLock;  
    

  @Override  
  public IBinder onBind(Intent intent) {  
      // TODO Auto-generated method stub  
      return null;  
  }  
    
  @Override  
  public void onCreate() {  
      // TODO Auto-generated method stub  
      super.onCreate();  
      powerManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
      keyguardManager=(KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
                
  }  
    
  @Override  
  @Deprecated  
  public void onStart(Intent intent, int startId) {  
       Log.e(TAG, "Service start--------------");  
       // ��������    
       wakeLock = powerManager.newWakeLock    
       (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");    
       wakeLock.acquire();    
       /*keyguardLock = keyguardManager.newKeyguardLock("");    
       // ���̽���    
       keyguardLock.disableKeyguard();   */ 
      
  }  
    
  @Override  
  public void onDestroy() {  
      super.onDestroy();  
       Log.e(TAG, "Service onDestroy--------------");  
       if (wakeLock != null) {    
           wakeLock.release();    
           wakeLock = null;    
       }    
       if (keyguardLock!=null) {    
           keyguardLock.reenableKeyguard();    
       }    
        
  }  

}  