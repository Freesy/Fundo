package com.szkct.weloopbtsmartdevice.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mediatek.wearable.WearableManager;
import com.szkct.weloopbtsmartdevice.net.HttpToService;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DailogUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.CustomProgress;
import android.app.Dialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * 
 * @author chendalin
 * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class AboutActivity extends AppCompatActivity  {

    private static final String TAG = "AboutActivity";
    private TextView version_name;
    private TextView version_tv;
    
    private TextView privacy_tv;
    private TextView mWatch_mall;
    private TextView watch_fota_tvs;
    private ImageView aboutlogo_iv;
    
    private LinearLayout mLinearAbout;
 //   private Toolbar toolbar;
    public static boolean isLatestVersion = true;
    private Context context;

    public Dialog dialog;

    private long syncStartTime = 0;

    public Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 1:   // todo ---  有新版本
                try {

                    if(Utils.isActivityRunning(AboutActivity.this,AboutActivity.class.getName())){
                        if (null != dialog) {
                            if(dialog.isShowing()){
                                dialog.setCancelable(true);
                                dialog.dismiss();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(Utils.isActivityRunning(AboutActivity.this,AboutActivity.class.getName())){
                    DailogUtils.doNewVersionUpdate(AboutActivity.this);
                }
                break;
            case 2:  // todo ---  没有有新版本
                if(Utils.isActivityRunning(AboutActivity.this,AboutActivity.class.getName())){
                    try {
                        if (null != dialog) {
                            if(dialog.isShowing()){
                                dialog.setCancelable(true);
                                dialog.dismiss();
                            }}
                    } catch (Exception e) {e.printStackTrace();}
                    if(!(AboutActivity.this).isFinishing()) {
                        DailogUtils.notNewVersionUpdate(AboutActivity.this);
                    }
                }
                break;

            case 6:
                if (null != dialog) {
                    if(dialog.isShowing()){
                        dialog.setCancelable(true);
                        dialog.dismiss();
                    }
                }
                Toast.makeText(AboutActivity.this, getString(R.string.checking_new_version_fail), Toast.LENGTH_SHORT).show();
                break;

            }
        }
        
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != dialog && syncStartTime!=0) {
                if (System.currentTimeMillis() - syncStartTime > 30 * 1000) {
                    Message msg = myHandler.obtainMessage(6);
                    myHandler.sendMessage(msg);
                    return ;
                }
            }else{
                return ;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dialog){
            dialog.dismiss();
            dialog.setCancelable(true);
            dialog = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		context = this;
		if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			setTheme(R.style.KCTStyleWhite);
		}else{
			setTheme(R.style.KCTStyleBlack);
		}
        setContentView(R.layout.about);
        initControls();
      //状态栏与标题栏一体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,mLinearAbout,R.color.trajectory_bg);
		}

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // The About UI will enable "LogCatcher" button after you clicked the "Version"
        // TextView 5 times quickly.
        // The "LogCatcher" feature only work in the WearableManager "Connected" state.
        // The "LogCatcher" SPP Client will connect the "WearableManager connected" Remote Device Log Server,
        // then received MAUI log from SPP Log server.
        // The remote log device will be SPP connected device if APK mode is SPP and connected successfully.
        // Otherwise user must select a remote log device from scanning UI.
        /***** chendalin *******/
//        TextView versionText = (TextView) findViewById(R.id.version_text);
//        try {
//            versionText
//                    .setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
//            versionText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(LOG_TAG, "versionText Click");
//                    if (mVersionClickTime == 0) {
//                        mVersionClickTime = System.currentTimeMillis();
//                    }
//                    if (System.currentTimeMillis() - mVersionClickTime >= 0
//                            && System.currentTimeMillis() - mVersionClickTime < 600) {
//                        mClickCount++;
//                        Log.d(LOG_TAG, "versionText mClickCount: " + mClickCount);
//                        if (mClickCount == CLICK_COUNT) {
//                            Log.d(LOG_TAG, "showLogButton start");
//                            mClickCount = 0;
//                            updateLogButton();
//                            showLogButton();
//                        }
//                    }
//                    if (System.currentTimeMillis() - mVersionClickTime > 2000) {
//                        mClickCount = 1;
//                        Log.d(LOG_TAG, "showLogButton mClickCount = 1");
//                    }
//                    mVersionClickTime = System.currentTimeMillis();
//                }
//            });
//        } catch (NameNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        Button testButton = (Button) findViewById(R.id.button1);
        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // YahooWeatherCore.LoadCity();
                if (isFastDoubleClick()) {
                    Log.d(TAG, "AboutButton return");
                    return;
                }
                createNotificaction();
            }
        });

        mCatchLog = (Button) findViewById(R.id.log_test);
        mCatchLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // YahooWeatherCore.LoadCity();
                if (isFastDoubleClick()) {
                    Log.d(LOG_TAG, "AboutButton CatchLog return");
                    return;
                }
                if (mConnectState == 0) {
                    startCatchLog();
                } else if (mConnectState == 2) {
                    stopCatchLog();
                }
            }
        });
        showLogButton();
    }

    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private float XPosition = 0;
	private float YPosition = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			XPosition = event.getX();
			YPosition = event.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if(event.getY() - YPosition > 50||YPosition - event.getY() > 50){
				break;
			}
			if(event.getX() - XPosition > 80){
				//finish();
				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}


	private void initControls() {
		// TODO Auto-generated method stub
		mLinearAbout = (LinearLayout)findViewById(R.id.li_about);

        version_name = (TextView)this.findViewById(R.id.version_name);
		version_tv = (TextView) this.findViewById(R.id.versionRenewal_tv);
 
        privacy_tv = (TextView) this.findViewById(R.id.privacy_policy_tv);
        mWatch_mall= (TextView) this.findViewById(R.id.watch_mall_tv);
        watch_fota_tvs = (TextView)this.findViewById(R.id.watch_fota_tv);
        version_name.setText(getVersionShortName());

        dialog= CustomProgress.show(this, getString(R.string.checking_new_version), null);

        //版本更新  TODO ---- 点击检查新版本
        version_tv.setOnClickListener(new OnClickListener() {    
        	@Override
        	public void onClick(View v) {
        		if (NetWorkUtils.isConnect(context)) {
        			//请求网络是否更新apk
                    if(!isFastDoubleClick()){

                        if(!dialog.isShowing()){
                            dialog.setCancelable(false);
                            dialog.show();

                            syncStartTime = System.currentTimeMillis();
                            myHandler.postDelayed(runnable, 1000 * 31);
                        }

                        new HttpToService(context).start();
                    }
        		}else{
        			Toast.makeText(AboutActivity.this, R.string.net_error_tip, Toast.LENGTH_SHORT).show();
        		}
//                Toast.makeText(AboutActivity.this, R.string.check_sucess_message, Toast.LENGTH_SHORT).show();
//                DailogUtils.notNewVersionUpdate(AboutActivity.this);
        	}
        });
        //固件升級
        watch_fota_tvs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*** 公版apk暂不支持FOTA该功能*/
				/*Intent intent  = new Intent(AboutActivity.this, SmartDeviceFirmware.class);
                intent.putExtra(FotaUtils.INTENT_EXTRA_INFO, FotaUtils.FIRMWARE_ROCK);
                startActivity(intent);*/
				Toast.makeText(AboutActivity.this, R.string.no_fota, Toast.LENGTH_LONG).show();
			}
		});
        
       /* //意见反馈
        feedback_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent =new Intent();
				mIntent.setClass(AboutActivity.this,SuggestionFeedBackActivity.class );
				startActivity(mIntent);
				
			}
		});*/
        
        //用户隐私
        privacy_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent =new Intent();
				mIntent.setClass(AboutActivity.this,PrivacyPolicyActivity.class );
				startActivity(mIntent);
                //Toast.makeText(AboutActivity.this, R.string.developed, Toast.LENGTH_LONG).show();
			}
		});
        
        
        //商城
        mWatch_mall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(AboutActivity.this, R.string.no_support, Toast.LENGTH_LONG).show();
			}
		});

         findViewById(R.id.back).setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });

        aboutlogo_iv = (ImageView)this.findViewById(R.id.about_logo);
        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){  // 白色背景
            aboutlogo_iv.setImageResource(R.drawable.about_logo_w);
        }else{
            aboutlogo_iv.setImageResource(R.drawable.about_logo_b);
        }
        aboutlogo_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Display versionShortName
                Intent intent = new Intent(AboutActivity.this, VersionDetail.class);
                startActivity(intent);
                //finish();
                return false;
            }
        });
	}
   
	private void createNotificaction() {
        /*NotificationManager manager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = Utils.genMessageId();
        // Send a notification to show connection status
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_connected_status;
        notification.tickerText = "Ticker Text" + String.valueOf(notificationId);
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, "Title",
                "Content: Hello!" + String.valueOf(notificationId), pendingIntent);
        
        manager.notify(notificationId, notification);*/
    }

    private long mLastClickTime = System.currentTimeMillis();;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;
    }


    /// M: show Log Button
    private int mClickCount = 0;

    private long mVersionClickTime = 0;

    private static final int CLICK_COUNT = 5;

    private void showLogButton() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        boolean isSwitchMode = prefs.getBoolean("log_button", false);
        Log.d(LOG_TAG, "[showLogButton] " + isSwitchMode);
        if (isSwitchMode) {
            mCatchLog.setVisibility(View.VISIBLE);
            mCatchLog.setEnabled(true);
        }
    }

    private void updateLogButton() {
        Log.d(LOG_TAG, "[updateLogButton] start");
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("log_button", true);
        editor.commit();
    }

    /// M: Catch log test @{
    private BluetoothAdapter mBluetoothAdapter;

    private Button mCatchLog;

    private static final String LOG_SPP_UUID = "97a42c60-a826-11e4-8b1c-0002a5d5c51b";

    // 0 = disconnected, 1 = connecting, 2 = connected
    private int mConnectState = 0;

    private static final int READ_BUFFER = 1024 * 5;

    private BluetoothDevice mConnectedDevice;

    private BluetoothSocket mClientSocket;

    private BluetoothSocket mReadSocket;

    private Thread mClientThread;

    private Thread mReadThread;

    private FileOutputStream mRecFile;

    private String mLogFileName;

    private static final int CLIENT_THREAD = 1;

    private static final int READ_THREAD = 2;

    private static final int REQUEST_CODE_SCAN = 0;

    private static final String LOG_DEVICE = "LOG_DEVICE";

    private static final String LOG_TAG = "LogCatcher";

    private void startCatchLog() {
        Log.d(LOG_TAG, "startCatchLog ConnectState: " + mConnectState);
        if (mConnectState == 0) {
            if (mBluetoothAdapter == null || !mBluetoothAdapter.enable()) {
                Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                return;
            }
            if (WearableManager.getInstance().isAvailable()
                    && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
                mConnectedDevice = WearableManager.getInstance().getRemoteDevice();
                if (mConnectedDevice != null) {
                    Log.d(LOG_TAG, "startCatchLog device: " + mConnectedDevice.getAddress());
                }
                mClientThread = new Thread(mSPPClientRunnable);
                mClientThread.start();
            } else {
                Log.d(LOG_TAG, "startCatchLog log_error");
                Toast.makeText(getApplicationContext(), R.string.log_scan, Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent(AboutActivity.this, LogDeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String address = data.getStringExtra(LOG_DEVICE);
                Log.d(LOG_TAG, "[onActivityResult] address: " + address);
                if (BluetoothAdapter.checkBluetoothAddress(address)) {
                    mConnectedDevice = mBluetoothAdapter.getRemoteDevice(address);
                    if (mConnectedDevice != null) {
                        Log.d(LOG_TAG, "[onActivityResult] device: " + mConnectedDevice.getAddress());
                        mClientThread = new Thread(mSPPClientRunnable);
                        mClientThread.start();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void stopCatchLog() {
        Log.d(LOG_TAG, "startCatchLog begin");
        Log.d(TAG, "disconnect begin");
        if (mClientThread != null) {
            cancelThread(CLIENT_THREAD);
            mClientThread = null;
        }
        if (mReadThread != null) {
            cancelThread(READ_THREAD);
            mReadThread = null;
        }
    }

    private void connectFail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.log_fail), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCatchLogButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnectState == 0) {
                    mCatchLog.setEnabled(true);
                    mCatchLog.setText(R.string.log_start);
                } else if (mConnectState == 1) {
                    mCatchLog.setEnabled(false);
                    mCatchLog.setText(R.string.log_connecting);
                } else {
                    mCatchLog.setEnabled(true);
                    mCatchLog.setText(R.string.log_stop);
                }
            }
        });
    }

    private Runnable mSPPClientRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "SPPClientThread begin");
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                UUID LOG_UUID = UUID.fromString(LOG_SPP_UUID);
                tmp = mConnectedDevice.createRfcommSocketToServiceRecord(LOG_UUID);
            } catch (IOException e) {
                Log.e(LOG_TAG, "SPPClientThread create socket IOException" + e.getMessage());
                return;
            }
            mClientSocket = tmp;

            // mAdapter.cancelDiscovery();
            try {
                Log.d(LOG_TAG, "SPPClientThread connect begin");
                mConnectState = 1;
                updateCatchLogButton();
                mClientSocket.connect();
                Log.d(LOG_TAG, "SPPClientThread.connect end");
            } catch (IOException e) {
                mConnectState = 0;
                connectFail();
                updateCatchLogButton();
                Log.e(LOG_TAG, "SPPClientThread.connect fail: " + e.getMessage());
                try {
                    if (mClientSocket != null) {
                        mClientSocket.close();
                    }
                } catch (IOException e2) {
                    Log.e(LOG_TAG, "SPPClientThread.connect close fail: " + e2.getMessage());
                }
                return;
            }

            mClientThread = null;

            // Start the connected thread
            connected(mClientSocket, mConnectedDevice);
            Log.d(LOG_TAG, "SPPClientThread end");
        }
    };

    private Runnable mSPPReadRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "[SPPReadThread] begin");

            InputStream tmpIn = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = mReadSocket.getInputStream();
            } catch (IOException e) {
                Log.e(LOG_TAG, "[SPPReadThread] getInputStream fail: " + e.getMessage());
            }

            // Keep listening to the InputStream while connected
            while (tmpIn != null) {
                try {
                    // Read from the InputStream buffer control
                    byte[] data = new byte[READ_BUFFER];
                    int byteLength = 0;
                    byteLength = tmpIn.read(data);
                    // Send the obtained bytes to the manager
                    Log.d(LOG_TAG, "[SPPReadThread] read length = " + byteLength);
                    if (byteLength > 0) {
                        // write log to file
                        writeLog(data, byteLength);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "[SPPReadThread] read IOException" + e.getMessage());
                    connectionLost();
                    break;
                }
            }
            Log.d(LOG_TAG, "[SPPReadThread] end");
        }
    };

    private void cancelThread(int thread) {
        if (thread == CLIENT_THREAD) {
            try {
                if (mClientSocket != null) {
                    Log.d(LOG_TAG, "[cancelThread] mClientSocket.close");
                    mClientSocket.close();
                    mClientSocket = null;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "[cancelThread] SPPCancelCallback.cancel client fail: " + e.getMessage());
            }
        } else if (thread == READ_THREAD) {
            try {
                synchronized (mReadSocket) {
                    if (mReadSocket != null) {
                        Log.d(LOG_TAG,
                                "[cancelThread] mReadSocket.close begin " + mReadSocket.isConnected());
                        mReadSocket.close();
                        Log.d(LOG_TAG,
                                "[cancelThread] mReadSocket.close end " + mReadSocket.isConnected());
                        mReadSocket = null;
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "[cancelThread] SPPCancelCallback.cancel read failed: " + e.getMessage());
            }
        } else {
            Log.e(LOG_TAG, "[cancelThread] SPPCancelCallback.cancel invaild thread");
        }
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(LOG_TAG, "[connected], socket = " + socket + ", device = " + device);

        if (mClientThread != null) {
            cancelThread(CLIENT_THREAD);
            mClientThread = null;
        }
        if (mReadThread != null) {
            cancelThread(READ_THREAD);
            mReadThread = null;
        }

        mReadSocket = socket;
        mReadThread = new Thread(mSPPReadRunnable);
        mReadThread.start();

        mConnectState = 2;
        updateCatchLogButton();

        // create log file
        createLogFile();
    }

    private void connectionLost() {
        Log.d(LOG_TAG, "[connectionLost] begin");

        if (mClientThread != null) {
            cancelThread(CLIENT_THREAD);
            mClientThread = null;
        }
        if (mReadThread != null) {
            cancelThread(READ_THREAD);
            mReadThread = null;
        }
        mConnectState = 0;
        updateCatchLogButton();

        if (mRecFile != null) {
            try {
                mRecFile.close();
            } catch (IOException e) {
                Log.d(LOG_TAG, "[connectionLost] IOException " + e.getMessage());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(getApplicationContext(),
                          getResources().getString(R.string.log_path) + mLogFileName, Toast.LENGTH_LONG).show();
                  mLogFileName = null;
                }
            });
            mRecFile = null;
        }
    }

    private void createLogFile() {
        String fileRoot = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileRoot = Environment.getExternalStorageDirectory() + "/AsterLog";
        } else {
            fileRoot = Environment.getRootDirectory() + "/AsterLog";
        }

        File dir = new File(fileRoot);
        if (!dir.exists()) {
            dir.mkdir();
        }
        final SimpleDateFormat dateFormat = Utils.setSimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String fileName = fileRoot + "/" + dateFormat.format(System.currentTimeMillis());
        fileName = fileName.replace(".", "_").replace(":", "-") + ".log";
        Log.d(LOG_TAG, "[connected] fileName: " + fileName);

        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.d(LOG_TAG, "[connected] IOException " + e);
            return;
        }
        if (mRecFile == null) {
            try {
                mRecFile = new FileOutputStream(file, false);
                mLogFileName = fileName;
            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "[connected] FileNotFoundException " + e);
            }
        }
    }

    private void writeLog(byte[] data, int length) {
        if (mRecFile != null) {
            try {
                int writed = 0;
                final int write_len = 2 * 1024;
                while (writed != length) {
                    if (length - writed >= write_len) {
                        mRecFile.write(data, writed, write_len);
                        writed += write_len;
                    } else {
                        mRecFile.write(data, writed, length - writed);
                        writed += length - writed;
                    }
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "[writeLog] IOException ", e);
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.write_fail), Toast.LENGTH_LONG).show();
            }
        }
    }
    /// @}
    
	/**
     * 获得当前版本号
     */
    public String getCurrentVersion() {
        String versionName;
        int versionCode;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // Set them by default value
            versionName = Constants.NULL_TEXT_NAME;
            versionCode = 0;
            e.printStackTrace();
        }

        //return (versionName + " (" + getString(R.string.beta_version) + ")");
      return versionName ;
    }

    public String getVersionShortName() {
        String versionShortName;

        try {
            versionShortName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // Set them by default value
            versionShortName = Constants.NULL_TEXT_NAME;
            e.printStackTrace();
        }

        return versionShortName ;
    }

    public String getVersionName() {
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // Set them by default value
            versionName = Constants.NULL_TEXT_NAME;
            e.printStackTrace();
        }

        return versionName ;
    }

    public int getVersionCode() {
        int versionCode;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // Set them by default value
            versionCode = 0;
            e.printStackTrace();
        }

        return versionCode ;
    }
}
