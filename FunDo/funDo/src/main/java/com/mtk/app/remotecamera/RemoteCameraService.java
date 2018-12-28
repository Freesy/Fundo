
package com.mtk.app.remotecamera;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mediatek.camera.service.MtkCameraAPService;
import com.mediatek.camera.service.MtkCameraLocalBinder;
import com.mediatek.camera.service.RemoteCameraController;
import com.mediatek.camera.service.RemoteCameraEventListener;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.MediaManager;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.List;
//import com.mediatek.camera.service.MtkCameraAPServiceListener;

public class RemoteCameraService implements RemoteCameraEventListener {  // 拍照服务类
    private static Context mContext;

    public static final String BT_REMOTECAMERA_EXIT_ACTION = "com.mtk.RemoteCamera.EXIT";

    public static final String BT_REMOTECAMERA_CAPTURE_ACTION = "com.mtk.RemoteCamera.CAPTURE";

    private static final String TAG = "AppManager/Camera/Service";

    public static boolean inLaunchProgress = false;

    public static boolean needPreview = false;

    public static boolean isIntheProgressOfExit = false;

    public static boolean isLaunched = false;

    private MtkCameraAPService mMtkCameraAPService = null;

    private boolean mIsMtkCameraLaunched = false;

    private RemoteCameraController mController = RemoteCameraController.getInstance();

    public RemoteCameraService(Context context) {
        mContext = context;
        
      RemoteBroadcastReceiver remoteBroadcastReceiver = new RemoteBroadcastReceiver();     // TODO ---- 远程拍照的广播
      //实例化过滤器并设置要过滤的广播   
      IntentFilter intentFilter = new IntentFilter("remote_start_activity"); 
//      intentFilter.addAction("remote_exit_activity");
//      intentFilter.addAction("remote_take_picture");
//      intentFilter.addAction("remote_need_preview");
//      intentFilter.addAction("remote_exit_from_cp");
        intentFilter.addAction("0x46");
        intentFilter.addAction("0x47");
        intentFilter.addAction("0x48");
      //注册广播   
      context.registerReceiver(remoteBroadcastReceiver, intentFilter); 
      
    }

    @Override
    public void notifyRemoteCameraEvent(int eventType) {
        needPreview = false;
//        mIsCanLaunchMtkCameraAp = MtkCameraAPService.isCanLaunchMtkCameraAp(mContext);

        switch (eventType) {
            case ACTION_START_ACTIVITY:
                Log.i(TAG, "isIntheProgressOfExit: " + isIntheProgressOfExit + ", isLaunched: " + isLaunched + ", inLaunchProgress: " + inLaunchProgress);

                if (Utils.isScreenLocked(mContext)) {
                    mController.sendOnStart(false);
                } else {
                    if (!Utils.isScreenOn(mContext)) {
                        mController.sendOnStart(false);
                    } else {
                        if (isLaunched && (!isIntheProgressOfExit)) {
                            mController.sendOnStart(true);
                        } else if (isIntheProgressOfExit || inLaunchProgress) {
                            mController.sendOnStart(false);
                        } else {
                            inLaunchProgress = true;
                            Intent launchIntent = new Intent();
                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            launchIntent.setClass(mContext, RemoteCamera.class);
                            mContext.startActivity(launchIntent);
                           /* inLaunchProgress = true;
                            Intent startServiceIntent = new Intent(mContext,
                                    MtkCameraAPService.class);
                            mContext.bindService(startServiceIntent, mCameraConnection,
                                    Context.BIND_AUTO_CREATE);*/
                        }
                    }
                }
                break;
            case ACTION_EXIT_ACTIVITY:
                if (isLaunched) {
                    isIntheProgressOfExit = true;
                }
                if (!mIsMtkCameraLaunched) {
                    if (mListener != null) {
                        mListener.onExitCamera();
                    }
                }
                /*try {
                    mContext.unbindService(mCameraConnection);
                } catch (Exception e) {
                    Log.i(TAG, "unbind service failed, e = " + e);
                }*/
                
                inLaunchProgress = false;
                break;
            case ACTION_TAKE_PICTURE:
                if (mIsMtkCameraLaunched) {
                    if (mMtkCameraAPService != null) {
                        mMtkCameraAPService.takePicture();
                    }
                } else {
                    if (mListener != null) {
                        mListener.onTakePicture();
                    }
                }
                break;
            case ACTION_PREVIEW:
                Log.i(TAG, "needPreview = true");
                needPreview = true;
                break;
            case ACTION_EXIT_FROM_SP:
                if (isLaunched) {
                    isIntheProgressOfExit = true;
                }
                /*try {
                    mContext.unbindService(mCameraConnection);
                } catch (Exception e) {
                    Log.i(TAG, "unbind service failed, e = " + e);
                }*/
                inLaunchProgress = false;
                break;
            default:
                break;
        }
    }

    private static CustomCameraListener mListener;

    public static void setListener(CustomCameraListener l) {
        mListener = l;
    }

    public interface CustomCameraListener {
        public void onExitCamera();

        public void onTakePicture();
    }

    private ServiceConnection mCameraConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "CameraConnection, onServiceDisconnected()");
//            mMtkCameraAPService.release();
            mMtkCameraAPService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "CameraConnection, onServiceConnected()");
            mMtkCameraAPService = ((MtkCameraLocalBinder) service).getService();
//            mMtkCameraAPService.start();
            
            mIsMtkCameraLaunched = mMtkCameraAPService.isMTKCameraLaunched();
            if (!mIsMtkCameraLaunched) {
                Intent launchIntent = new Intent();
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.setClass(mContext, RemoteCamera.class); 
                mContext.startActivity(launchIntent);
            }
        }
    };

	public static void intinotification(Uri uri) {
		NotificationManager mNotificationManager = (NotificationManager) 
				mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder;
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentTitle(mContext.getString(R.string.click_to_view_photo)).setContentText(mContext.getString(R.string.picture_saved_in)+Environment.getExternalStorageDirectory().getAbsolutePath() + "/Photo")
		// .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
		// .setNumber(number)//显示数量
			//	.setTicker("测试通知来啦")// 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setAutoCancel(true)// 设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);
		Intent picIntent = new Intent();
	//	picIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		picIntent.setAction(android.content.Intent.ACTION_VIEW);
		//注意：这里的这个APK是放在assets文件夹下，获取路径不能直接读取的，要通过COYP出去在读或者直接读取自己本地的PATH，这边只是做一个跳转APK，实际打不开的
		
//		Uri uri = Uri.parse(apk_path);
	
		picIntent.setDataAndType(uri, "image/*");
		// context.startActivity(intent);
		PendingIntent contextIntent = PendingIntent.getActivity(mContext, 0,picIntent, 0);
		mBuilder.setContentIntent(contextIntent);
		mNotificationManager.notify(1, mBuilder.build());
	}

    /**
     * 作用：用户是否同意打开相机权限
     *
     * @return true 同意 false 拒绝
     */
    public boolean isCameraPermission() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }
	
    public class RemoteBroadcastReceiver extends BroadcastReceiver    
    {
        @Override  
        public void onReceive(Context context, Intent intent)   
        {
        	String action = intent.getAction();
            needPreview = false;
//            mIsCanLaunchMtkCameraAp = MtkCameraAPService.isCanLaunchMtkCameraAp(mContext);
            switch (action) {
                case "0x46":    // remote_start_activity  开始拍照
                    boolean flag = Utils.isGetPhotoPremission();
                    if (flag) {
                        if (isCameraPermission()) {
//                        L.i("myPermission", "当前开启了相机权限");
                            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {  // TODO --- ble 拍照
                                if(RemoteCamera.isSendExitTakephoto){
                                    return;
                                }else{
                                    Log.i(TAG, "isIntheProgressOfExit: " + isIntheProgressOfExit + ", isLaunched: " + isLaunched + ", inLaunchProgress: " + inLaunchProgress);
                                    if (Utils.isScreenLocked(mContext)) {
                                        mController.sendOnStart(false);
                                    } else {
                                        if (!Utils.isScreenOn(mContext)) {
                                            mController.sendOnStart(false);
                                        } else {
                                            if (isLaunched && (!isIntheProgressOfExit)) {
                                                mController.sendOnStart(true);
                                            } else if (isIntheProgressOfExit || inLaunchProgress) {
                                                mController.sendOnStart(false);
                                            }
//                                    else {
                                    inLaunchProgress = true;
                                    Intent launchIntent = new Intent();
                                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchIntent.setClass(mContext, RemoteCamera.class);
                                    mContext.startActivity(launchIntent);
//                                    }
                                        }
                                    }////
                                }
                            }else {
                                Log.i(TAG, "isIntheProgressOfExit: " + isIntheProgressOfExit + ", isLaunched: " + isLaunched + ", inLaunchProgress: " + inLaunchProgress);
                                if (Utils.isScreenLocked(mContext)) {
                                    mController.sendOnStart(false);
                                } else {
                                    if (!Utils.isScreenOn(mContext)) {
                                        mController.sendOnStart(false);
                                    } else {
                                        if (isLaunched && (!isIntheProgressOfExit)) {
                                            mController.sendOnStart(true);
                                        } else if (isIntheProgressOfExit || inLaunchProgress) {
                                            mController.sendOnStart(false);
                                        } else {
                                            inLaunchProgress = true;
                                            Intent launchIntent = new Intent();
                                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            launchIntent.setClass(mContext, RemoteCamera.class);
                                            mContext.startActivity(launchIntent);
                                        }
                                    }
                                }
                            }
                            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        } else {
                            Log.e(TAG, "当前没有开启相机权限");
                            Toast.makeText(BTNotificationApplication.getInstance(), R.string.camera_permission, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case "0x48":   // remote_exit_activity   退出拍照
                    boolean flag2 = Utils.isGetPhotoPremission();
//                    if (flag2) {
//                        if (isCameraPermission()) {
//                        L.i("myPermission", "当前开启了相机权限");
                            if (isLaunched) {
                                isIntheProgressOfExit = true;
                            }
                            if (!mIsMtkCameraLaunched) {
                                if (mListener != null) {
                                    mListener.onExitCamera();
                                }
                            }
                            inLaunchProgress = false;
                    break;
                case "0x47":    // remote_take_picture   todo   ------    拍照
//                	System.out.println("----------------==============================");
                    boolean flag3 = Utils.isGetPhotoPremission();
//                    if (flag3) {
//                        if (isCameraPermission()) {
//                        L.i("myPermission", "当前开启了相机权限");
                            try {
                                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    if (mIsMtkCameraLaunched) {
                                        if (mMtkCameraAPService != null) {
                                            mMtkCameraAPService.takePicture();   //  todo   ------    拍照
                                        }
                                    } else {
                                        if (mListener != null) {
                                            mListener.onTakePicture();      //  todo   ------    拍照
                                        }
                                    }
                                    try {
                                        AssetManager assetManager = BTNotificationApplication.getInstance().getAssets();
                                        AssetFileDescriptor mRingtoneDescriptor = assetManager.openFd("music/paizhaook.wav");
                                        MediaManager.getMediaPlayerInstance();
                                        MediaManager.playSound(mRingtoneDescriptor);
                                        new Thread().sleep(500);
                                        MediaManager.stop();
                                        MediaManager.release();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            } catch (Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
//                        } else {
//                            Log.e(TAG, "当前没有开启相机权限");
//                            Toast.makeText(BTNotificationApplication.getInstance(), R.string.camera_permission, Toast.LENGTH_SHORT).show();
//                        }
//                    }
                    break;
                case "remote_need_preview":   // 是否需要预览
                    Log.i(TAG, "needPreview = true");
                    needPreview = true;
                    break;
                case "remote_exit_from_cp":   // ????  退出拍照的服务
                    if (isLaunched) {
                        isIntheProgressOfExit = true;
                    }
                   /* try {
                        mContext.unbindService(mCameraConnection);
                    } catch (Exception e) {
                        Log.i(TAG, "unbind service failed, e = " + e);
                    }*/
                    inLaunchProgress = false;
                    break;
                default:
                    break;
            }
        
        	
        	
        }   
           
    } 
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {  
        // Retrieve all services that can match the given intent  
        PackageManager pm = context.getPackageManager();  
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);  
        // Make sure only one match was found  
        if (resolveInfo == null || resolveInfo.size() != 1) {  
            return null;  
        }  
        // Get component info and create ComponentName  
        ResolveInfo serviceInfo = resolveInfo.get(0);  
        String packageName = serviceInfo.serviceInfo.packageName;  
        String className = serviceInfo.serviceInfo.name;  
        ComponentName component = new ComponentName(packageName, className);  
        // Create a new intent. Use the old one for extras and such reuse  
        Intent explicitIntent = new Intent(implicitIntent);  
        // Set the component to be explicit  
        explicitIntent.setComponent(component);  
        return explicitIntent;  
    } 
    
}
