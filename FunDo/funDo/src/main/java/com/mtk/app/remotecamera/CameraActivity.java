package com.mtk.app.remotecamera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.MediaManager;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

//import com.szkct.fundobracelet.BluetoothUartService;
//import com.szkct.fundobracelet.R;
//import com.szkct.utils.Constants;
//import com.szkct.utils.CustomCameraView;

/**
 * 远程拍照 页面
 *
 * @author zhaixiang$.
 * @explain
 * @time 2016/11/3$ 9:37$.
 */
public class CameraActivity extends Activity {

    public static final String TAG = CameraActivity.class.getName();
    private ServiceRecevier serviceRecevier1;
    private ImageButton btnToggle;
    CustomCameraView customCameraView ;
    private boolean isBackground = true;

    private boolean isDeviceSendExitCommand =  false; // 设备端主动退出拍照
    public static boolean isPhoneExitTakephoto = false;  // 手机端主动退出拍照


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cameras);

        customCameraView = (CustomCameraView) findViewById(R.id.cc_camera);
        serviceRecevier1 = new ServiceRecevier();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainService.ACTION_REMOTE_CAMERA);  //  拍照
        intentFilter.addAction(MainService.ACTION_REMOTE_CAMERA_EXIT); // 退出拍照
        registerReceiver(serviceRecevier1, intentFilter);
        btnToggle = (ImageButton) findViewById(R.id.toggle);
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCameraView.changeCamere = true;
                isBackground=!isBackground;
                customCameraView.setBackground(isBackground);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public class ServiceRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MainService.ACTION_REMOTE_CAMERA)) {
                //CustomCameraView customCameraView = new CustomCameraView(getApplicationContext());

//                if (camera != null) {
                // 控制摄像头自动对焦后才拍摄
                try {
                    customCameraView.takePicture();

                    try {
                        AssetManager assetManager = BTNotificationApplication.getInstance().getAssets();
                        AssetFileDescriptor mRingtoneDescriptor = assetManager.openFd("music/paizhaook.wav");
                        MediaManager.getMediaPlayerInstance();
                        MediaManager.playSound(mRingtoneDescriptor);
                        new Thread().sleep(500);  // 500    1000
                        MediaManager.stop();
                        MediaManager.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                }
            }else if(action.equals(MainService.ACTION_REMOTE_CAMERA_EXIT)){   // ACTION_REMOTE_CAMERA_EXIT
                isDeviceSendExitCommand = true;
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceRecevier1);

        //关闭相机指令
      /*  BluetoothUartService uartService = BluetoothUartService.instance;
        boolean state = false;
        if(uartService != null){
            state = uartService.uart_data_send(Constants.REMOTE_CAMERA_SWITCH, new byte[]{0}, 1);
        }
        Log.e(TAG,"关闭手环端相机："+state);
        CustomCameraView.changeCamere = false;*/

/*//        if(MainService.isDeviceSendExitCommand){
//            MainService.isDeviceSendExitCommand = false;
//        }else {
            String protocolCode =  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.PROTOCOLCODE);
            if(DateUtil.versionCompare("V1.1.36", protocolCode)
                    && SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){   //现根据协议版本号，大于1.1.37
                L2Send.sendNewExitTakephoto();
//                isSendExitTakephoto = true;
            }else {
                // 退出拍照时，发送退出拍照的命令
                L2Send.sendExitTakephoto();
//                isSendExitTakephoto = true;
            }
//        }*/

        if(!isDeviceSendExitCommand){
            String protocolCode =  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.PROTOCOLCODE);
            if(DateUtil.versionCompare("V1.1.36", protocolCode)
                    && SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){   //现根据协议版本号，大于1.1.37
                L2Send.sendNewExitTakephoto();
                isPhoneExitTakephoto = true;
            }else {
                // 退出拍照时，发送退出拍照的命令
                L2Send.sendExitTakephoto();
                isPhoneExitTakephoto = true;
            }
        }

        CustomCameraView.changeCamere = false;
    }
}
