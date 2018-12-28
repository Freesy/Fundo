package com.szkct.weloopbtsmartdevice.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.UtilsLX;
import com.szkct.map.dialog.AlertDialog;
import com.szkct.weloopbtsmartdevice.activity.LinkBleActivity;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import static com.szkct.weloopbtsmartdevice.main.MainService.STATE_NOCONNECT;

//import no.nordicsemi.android.dfu.internal.manifest.FileInfo;

/**
 * Created by kct on 2018/5/2.
 */
public class FirmWareUpdateActivityForBK  extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = FirmWareUpdateActivityForBK.class.getSimpleName();
    private Button mButtonToUUID, mButtonOTA, mButtonStartOTA, mButtonStopOTA, mButtonRefresh, mButtonFileList;
    private ListView mOTAListView;
    private int OTAType = 0;
    private final int MaxRetry = 2000;
    private HashMap<String, FileInfo> mOTAFlieList = null;
    private ArrayList<String> mFileList;
//    private int mTimeoutValue = MaxRetry;

    private TextView mProgressInfo;
    private ProgressBar mProgressBar;
    private int mReadyToUpdate = 0;
    private long mAlreadyReadCount = 0;
    private TextView mTextViewDeviceVersion, mTextViewFileVersion, mTextViewFilePath;
    private TextView mTextViewDeviceRomVersion, mTextViewOTARomVersion;
    private boolean isOTADone = false;
    private boolean canGo = false;

    private boolean flagTag = false;
    private boolean ifBlockSend = false;
    private static final int FILE_BUFFER_SIZE = 0x40000;
    private static final int OAD_BLOCK_SIZE = 16;
    private static final int HAL_FLASH_WORD_SIZE = 4;
    private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;     // OAD_BLOCK_SIZE = 16;
    private final byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];
    private byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
    private ImgHdr mFileImgHdr = new ImgHdr();
    //private ImgHdr mfileImgRomHdr = new ImgHdr();
    private Timer mTimer = null;
    private ProgInfo mProgInfo = new ProgInfo();
    private TimerTask mTimerTask = null;
    private long lastBlockReq = 0;
    private static final long TIMER_INTERVAL = 1000;
    private static final int SEND_INTERVAL = 10;
    private final Lock mLock = new ReentrantLock();
    private boolean mProgramming = false;
    private int DelayTimer = 2;
    private Uri mFilePath;
    private int MAXNotify = 10;
    private byte[] mFileIndexBuffer;

    private OTAFileAdapter mOTAFileAdapter;

    private MyBroadcast mbroadcast = null;
//    public static BluetoothGattCharacteristic mOTAUUID2 = null;
    private BluetoothGattCharacteristic mOTAUUID1 = null;
    public  BluetoothGattCharacteristic mOTAUUID2 = null;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private UUID uuid_ota1 = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    private UUID uuid_ota2 = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");

    private int retryNo = 3;
    private boolean flagDiscoverFail = false;

    private int canOTAcount = 0;
//    public static boolean youxiaoCharChange = false;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isGetOldVerSuccess = false;

    private boolean isBindServerSuccess = false;

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
//            EventBus.getDefault().post(new CheckboxEvent(2));   // todo -- 发送   Get Device Version Fail   1处
        }
    };

    private Intent mIntent;
//    private final BluetoothManager bluetoothManager;
    private BluetoothManager bluetoothManager;
    private AutoConnectThread autoConnectThread;  //蓝牙重连线程

    private Timer reconnectTimer;
    private MyTimerTask myTimerTask;

    private boolean isScan = false;
    private  String filePath ;  // 下载固件包的路径

    private LoadingDialog loadingDialog = null;   // 加载框

    private final int CHECKVERFAIL = 16;  //检测版本失败
    private long syncStartTime = 0;      // 开始检测版本的时间
    private boolean isStartOta = false;

    private boolean isUploading = false;

    private  boolean isShowFailDialog = false;

    private AlertDialog alertDialog;

    class MyTimerTask extends TimerTask {   //todo ---- add 20171122
        @Override
        public void run() {
            if(isScan){
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                isScan = false;
            }else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                isScan = true;
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 22:
                    if(null != loadingDialog && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                    break;

                case CHECKVERFAIL:  // 同步失败
                    try {
                        if (null != loadingDialog) {
                            if(loadingDialog.isShowing()){
                                loadingDialog.setCancelable(true);
                                loadingDialog.dismiss();
                                loadingDialog = null;
                            }
                        }
                        if(autoConnectThread != null) {     // todo -- 连接成功，取消连接的线程
                            autoConnectThread.cancel();
                            autoConnectThread = null;
                        }
                        if(!isShowFailDialog){
                            alertDialog = new AlertDialog(FirmWareUpdateActivityForBK.this).builder();
                            alertDialog.setCancelable(false);
                            alertDialog.setMsg(getString(R.string.checking_new_version_fail));  // R.string.firmware_is_update   getString(R.string.firmware_is_update)     "版本检测失败,是否重试"
                            alertDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isShowFailDialog = false;
                                    autoConnectDevice();
//                                isUploading = true; //TODO --- 开始升级了
//                                mButtonStartOTA.callOnClick();
                                }
                            });
                            alertDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //////////////////// todo  ---  取消后重连 ////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    Intent mIntent = new Intent();
                                    setResult(8, mIntent);
//                                    Bundle mBundle = new Bundle();
//                                    mBundle.putString("Result OTA", "Success");
//                                    setResult(8, "aa", mBundle);
                                    Log.e("liuxiaodebug", "setResult了");
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    isUploading = false;
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    isShowFailDialog = false;
                                    finish();
                                }
                            });
                            alertDialog.show();
                            isShowFailDialog = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 定时器
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != loadingDialog) {
                if (System.currentTimeMillis() - syncStartTime > 30 * 1000) {  //  90 * 1000
                    Message msg = handler.obtainMessage(CHECKVERFAIL);  // 数据同步失败,稍后重试
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    String address = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                    if (MainService.getInstance().getState() == 3) {
                        if (null != reconnectTimer) {
                            reconnectTimer.cancel();
                            reconnectTimer = null;
                        }

                        if (null != myTimerTask) {
                            myTimerTask.cancel();
                            myTimerTask = null;
                        }
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    List<UUID> uuids = LinkBleActivity.parseFromAdvertisementData(scanRecord);
                    if(uuids == null || uuids.size() <= 0){
                        return;
                    }
                    String findAddress = device.getAddress();
                    for (final UUID mUuid : uuids) {
                        if (mUuid.equals(BleContants.RX_SERVICE_872_UUID)) {
                            String scanRecords = UtilsLX.bytesToHexString(scanRecord);
                            //02011A 05 09 68756E34 0EFF0B00 3650460335F9 0468756E341107B75C49D204A34071A0B535853EB083070000000000000000000000000000000000000000
                            int deviceNameLength = scanRecord[3] & 0xff;
                            int deviceAddressLength = (8 + deviceNameLength) * 2; //13
                            findAddress = scanRecords.substring(deviceAddressLength, deviceAddressLength + 2)
                                    + ":" + scanRecords.substring(deviceAddressLength + 2, deviceAddressLength + 4)
                                    + ":" + scanRecords.substring(deviceAddressLength + 4, deviceAddressLength + 6)
                                    + ":" + scanRecords.substring(deviceAddressLength + 6, deviceAddressLength + 8)
                                    + ":" + scanRecords.substring(deviceAddressLength + 8, deviceAddressLength + 10)
                                    + ":" + scanRecords.substring(deviceAddressLength + 10, deviceAddressLength + 12);
                        }
                    }
                    Log.i(TAG, "Auto Address = " + address + ";   find = " + findAddress);
                    if (findAddress.equals(address)) {
                            if (null != reconnectTimer) {
                                reconnectTimer.cancel();
                                reconnectTimer = null;
                            }

                            if (null != myTimerTask) {
                                myTimerTask.cancel();
                                myTimerTask = null;
                            }
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);

                        mBluetoothLeService.connect(address);   // todo --- 真正开始蓝牙连接
                    }
                }
            };

    private class AutoConnectThread extends Thread{
        private boolean mIsRun;
        private Lock mInnerLock;
        private Condition mInnerCondition;
        private BluetoothDevice device;
        public AutoConnectThread(){
            mIsRun = true;
            mInnerLock = new ReentrantLock();
            mInnerCondition = mInnerLock.newCondition();
            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();
        }

        @Override
        public void run() {
            while(mIsRun){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                    device = null;

                    mInnerLock.lock();
                    try {
                        Log.d("AutoConnectThread", "waiting the connect.");
                        mInnerCondition.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("AutoConnectThread", "connect is fails.");
                    } finally {
                        mInnerLock.unlock();
                    }
            }
        }

        public void update(){
            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();
        }

        public void cancel(){
            mInnerLock.lock();
            mInnerCondition.signalAll();	// UnLock
            mInnerLock.unlock();

            mIsRun = false;
            Log.d("AutoConnectThread","connectThread is stop");
        }
        public void getConnectDevice(BluetoothDevice device){
            this.device = device;
        }
    }

    private synchronized void autoConnectDevice() {
        syncStartTime = System.currentTimeMillis();
        if(null == loadingDialog ){  // && !loadingDialog.isShowing()
            loadingDialog = new LoadingDialog(FirmWareUpdateActivityForBK.this,R.style.Custom_Progress, getString(R.string.ischeck_firmver));  // 正在检测固件版本
            loadingDialog.show();
            handler.postDelayed(runnable, 1000 * 31);// 打开定时器，执行操作   1000 * 91
        }

        Log.i(TAG, "[autoConnectDevice] begin");
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        String address = Utils.exChange2(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
        String uuid = (String) SharedPreUtil.getParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.UUID,"");
        if(TextUtils.isEmpty(address)){
            Log.i(TAG, "[autoConnectDevice] address is null");
            return;
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.i(TAG, "[autoConnectDevice] invalid BT address");
            return;
        }

        Boolean isBTOn = mBluetoothAdapter.isEnabled();
        if (!isBTOn) {
            Log.i(TAG, "[autoConnectDevice] BT is off");
            return;
        } else {
            if(autoConnectThread == null){
                autoConnectThread = new AutoConnectThread();
                autoConnectThread.start();
            }else{
                autoConnectThread.update();
            }
        }
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mIntent = new Intent();
                mIntent.putExtra("Result OTA", "Bluetooth Init fail");
                setResult(3, mIntent);
                finish();
            }
            isBindServerSuccess = true;
            autoConnectDevice(); //  111111111111111111111111111111
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "get disconnect event11");
            mBluetoothLeService.clearQueue();
//            if(mFragmentIndex == 5) {    //TODO --- 服务断开，停止升级
//                mBekenOTAFragment.clickStopBtn();
//            }
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo --- 开始连接蓝牙
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }

        setContentView(R.layout.fragment_beken_ota);
        mOTAListView = (ListView)findViewById(R.id.list_ota_files);
        mProgressInfo = (TextView)findViewById(R.id.tw_info);
        mProgressBar = (ProgressBar)findViewById(R.id.pb_progress);
        mButtonOTA = (Button)findViewById(R.id.button_OtaToOTA);  // OTA按钮 无效  todo --- 现做为 获取 本地固件版本好 操作
        mButtonToUUID = (Button)findViewById(R.id.button_OtaToUUid);   // UUID按钮
        mButtonStartOTA = (Button)findViewById(R.id.button_start_ota); //  START按钮 OTA
        mButtonStopOTA = (Button)findViewById(R.id.button_stop_ota);   //  STOP OTA按钮
        mButtonRefresh = (Button)findViewById(R.id.button_refresh);  //  Refresh List按钮-       TODO --- 重新获取版本号
        mButtonFileList = (Button)findViewById(R.id.button);  //  Other Files按钮
        mTextViewDeviceVersion = (TextView)findViewById(R.id.text_DeviceVersion);  //  Device Ver. 设备版本
        mTextViewFileVersion = (TextView)findViewById(R.id.textview_OTAVersion);  //  OTA File Ver. OTA文件版本
        mTextViewFilePath = (TextView)findViewById(R.id.textview_file_path);
//        mTextViewDeviceRomVersion = (TextView)findViewById(R.id.textview_DeviceRomVersion); // Dev. Rom Ver. : 当前设备版本-
//        mTextViewOTARomVersion = (TextView)findViewById(R.id.textview_OTARomVersion);   //  File Rom Ver.. 当前OTA文件版本

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading) {
                    Toast.makeText(BTNotificationApplication.getInstance(), R.string.dfu_status_uploading, Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });

        mButtonFileList.setOnClickListener(this);
        mButtonOTA.setOnClickListener(this);
        mButtonToUUID.setOnClickListener(this);
        mButtonStartOTA.setOnClickListener(this);
        mButtonStopOTA.setOnClickListener(this);
        mButtonRefresh.setOnClickListener(this);

        mButtonStopOTA.setVisibility(View.GONE);
        mButtonStartOTA.setEnabled(false);

        mHandler = new Handler();
//        EventBus.getDefault().register(this);
        if (mbroadcast == null) {
            mbroadcast = new MyBroadcast();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_FAIL);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_SUCCESS);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED_FAIL);
            intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_SUCCESS);
            intentFilter.addAction(BluetoothLeService.ACTION_NOTIFY_FAIL);
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mbroadcast, intentFilter);
        }

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            String ddd = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            Log.e(TAG, "当前mac地址------" + ddd);
            final boolean result = mBluetoothLeService.connect(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));   // 连接蓝牙
            Log.e(TAG, "Connect request result =" + result);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
    }

    private String filepath; // 固件包后台获取路径
    private String file_size;  // 固件包后台获取的大小
    private String firmware_version;      // 旧的版本号
    private String server_firmware_version;  // 后台获取的固件包版本号 （开发者版本号）
    private int type;     // 升级平台的类型
    private File file;   // 保存的文件 路径
    File files;   // 下载的固件包存放本地的 路径
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fendo/";   // 存放下载固件包的路径    Environment.getExternalStorageDirectory().getAbsolutePath()+"/fendoBk/"
    private  void loodingfilePath(){       // todo -- 获取 固件
        Intent intent = getIntent();
        filepath = intent.getStringExtra("url_path");   // 文件路径    http://app.fundo.xyz:8001/version/files/180203034834_B22XR_X2_NRF51822_60_V0.2.0_20180202.zip
        if (TextUtils.isEmpty(filepath)) {    // &&!"DfuTarg".equals(myNAME)     &&!myNAME.contains("DfuTarg")
//            setIVVisible(firmwareSuccess);
//            progressTv.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//            progressTv.setText(R.string.is_the_latest_version);
        } else {
            file_size = intent.getStringExtra("file_size");   // 143290
            server_firmware_version = intent.getStringExtra("server_firmware_version");  // V5.2.0   TODO --- 新的版本号
            firmware_version = intent.getStringExtra("firmware_version");   // v5.1.7        TODO --- 旧的版本号
            type = intent.getIntExtra("file_type", 0);  // 0
            if (type == 0 || type == 4) {//Nordic  /storage/emulated/0/fendo/funDo.zip   文件路径
                file = new File(Environment.getExternalStorageDirectory()+"/fendo/", "funDo.zip");
            } else {//dialog
                file = new File(Environment.getExternalStorageDirectory(), type + ".img");
            }
            if (filepath.contains(" ")) {   // http://app.fundo.xyz:8001/version/files/180203034834_B22XR_X2_NRF51822_60_V0.2.0_20180202.zip
                if (filepath.substring(filepath.length() - 1) == " ") {
                    filepath = filepath.substring(0, filepath.length() - 1);
                } else {
                    filepath = filepath.replace(" ", "%20");
                }
            }
            if (filepath.contains("\"")) {
                filepath = filepath.replace("\"", "%22");
            }
            if (filepath.contains("{")) {
                filepath = filepath.replace("{", "%7B");
            }
            if (filepath.contains("}")) {
                filepath = filepath.replace("{", "%7D");
            }

            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(filepath, file.getPath(), new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current, boolean isUploading) {}
                @Override
                public void onStart() {}
                @Override
                public void onSuccess(final ResponseInfo<File> responseInfo) {
                    try {
                        Log.i(TAG, "file.getName = " + file.getName() + "   file.getPath = " + file.getPath());
                        Log.i(TAG, "file.getName =2 " + filepath);
                        files = new File(path);//     /storage/emulated/0/fendo
                        if (!files.exists()) {
                            file.mkdir();
                        }
                        if(type == 0 || type == 4) {   //Nordic
                            filepath = path + "funDo.zip";    // /storage/emulated/0/fendo/funDo.zip    todo --- 最终下载到的 固件 的 路径
                        }else{            //dialog
                            filepath = path + "funDo.img";
                        }
                        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        Utils.Unzip(filepath, path);  // 参数一为源zip文件的完整路径，参数二为解压缩后存放的文件夹。

                        File lFolderFile = new File(path);     // todo  ---  获取 本地固件包       // todo --- getOTAFilePath ---- 获取本地固件包的路径
                        mFileList = new ArrayList<String>();
                        if (lFolderFile.listFiles() != null) {
                            int size = lFolderFile.listFiles().length;
                            for (int forCount = 0; forCount < size; forCount++) {
                                File lFile = lFolderFile.listFiles()[forCount];
                                FileInfo lFileInfo = new FileInfo(lFile.toString(), lFile.getName());
                                if (lFileInfo.getFileName().endsWith(".bin")) {
                                    mFileList.add(lFileInfo.getFileName());
//                                    mOTAFlieList.put(lFile.getName(), lFileInfo);
                                }
                            }
                        }
                        filePath = path + "/" + mFileList.get(0).toString();   // TODO  -- 必须要   只取了第一个文件的 名字
                        mTextViewFileVersion.setText(server_firmware_version);   // 设置 OTA File Ver. OTA文件版本      1120   todo -- 设置新固件的版本号
                        // todo  --- 检测本地版本号和新版本号成功了 ，销毁加载框 ，隐藏重新检测 的按钮
                        if(!StringUtils.isEmpty(mTextViewFileVersion.getText().toString()) && !StringUtils.isEmpty(mTextViewDeviceVersion.getText().toString())){   // && !StringUtils.isEmpty(mTextViewOTARomVersion.getText().toString())
                            if (null != loadingDialog) {
                                if(loadingDialog.isShowing()){
                                    loadingDialog.setCancelable(true);
                                    loadingDialog.dismiss();
                                    loadingDialog = null;
                                }
                            }
                        }else {
                            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.checking_new_version_fail), Toast.LENGTH_SHORT).show();    // 获取固件版本号失败3次后，断开蓝牙重新获取      "获取版本失败了，请重试"
                        }

                        String oldVerStr =  mTextViewDeviceVersion.getText().toString().replace("V", "").replace("v","").replace(".","");    // 旧固件版本号
                        String newVerStr =  mTextViewFileVersion.getText().toString().replace("V","").replace("v","").replace(".","");      // 新固件的版本号
                        int oldVer = Integer.valueOf(oldVerStr);
                        Log.e(TAG, "oldVer------" + oldVer);
                        int newVer = Integer.valueOf(newVerStr);
                        Log.e(TAG, "newVer------" + newVer);
                        if(newVer > oldVer){ //todo  -- 只有新版本号大于 才升级
                                AlertDialog alertDialog = new AlertDialog(FirmWareUpdateActivityForBK.this).builder();
                                alertDialog.setCancelable(false);
                                alertDialog.setMsg(getString(R.string.firmware_is_update));
                                alertDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isUploading = true; //TODO --- 开始升级了
                                        mButtonStartOTA.callOnClick();
                                    }
                                });
                                alertDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //////////////////////////   todo  ---  取消后重连  //////////////////////////////////////////////////////////////////////////////////////////////////
                                        Intent mIntent = new Intent();
                                        setResult(8, mIntent);
//                                        Bundle mBundle = new Bundle();
//                                        mBundle.putString("Result OTA", "Success");
//                                        setResult(8, "aa", mBundle);
                                        Log.e("liuxiaodebug", "setResult了");
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        isUploading = false;
                                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                        finish();
                                    }
                                });
                                alertDialog.show();
                        }else { // 提示当前已是最新版本
                            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.is_the_latest_version), Toast.LENGTH_SHORT).show();    // 获取固件版本号失败3次后，断开蓝牙重新获取
                        }
                        mButtonStartOTA.setEnabled(true);           // V5.2.0      // v5.1.7
                    }catch (Exception  e){}}
                @Override
                public void onFailure(HttpException e, String s) {}});
        }
    }


    private void prepareDate() {
        mOTAFlieList = new HashMap<String, FileInfo>();
        File lFolderFile = new File(FileUnit.getOTAFilePath());    // todo  ---  获取 本地固件包       // todo --- getOTAFilePath ---- 获取本地固件包的路径
        mFileList = new ArrayList<String>();
        if (lFolderFile.listFiles() != null) {
            int size = lFolderFile.listFiles().length;
            for (int forCount = 0; forCount < size; forCount++) {
                File lFile = lFolderFile.listFiles()[forCount];
                FileInfo lFileInfo = new FileInfo(lFile.toString(), lFile.getName());
                if (lFileInfo.getFileName().endsWith(".bin")) {
                    mFileList.add(lFileInfo.getFileName());
                    mOTAFlieList.put(lFile.getName(), lFileInfo);
                }
            }
        }
    }

    public void letGetVersionTrigger() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 5000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_ota:   //TODO  ---- 开始OTA 升级  START按钮 OTA
                isStartOta = true;
                flagTag = false;
                isOTADone = false;
                if(mTextViewDeviceVersion.getText().length()== 0){   // 旧的版本号
                    Toast.makeText(BTNotificationApplication.getInstance(), "check device version fail", Toast.LENGTH_SHORT).show();
                    letGetVersionTrigger();   // 这里会引起 Get Device Version Fail   暂时注释掉
                    getmBluetoothLeService().writeOTAIdentfy(new byte[]{0x00});  // todo -- 暂时注释掉
                    break;
                }else {
                    loadFile(filePath);
                    mProgressBar.setProgress(0);
                    mButtonStartOTA.setEnabled(false);
                    isGetOldVerSuccess = true;
                    startProgramming();        // todo  --- 开始升级
                }
                break;
            case R.id.button_stop_ota:  //  STOP OTA按钮        //TODO  ---- 停止 OTA 升级  按钮 OTA
                stopProgramming();     //todo  --   停止 OTA 升级
                break;
            case R.id.button_refresh: //  Refresh List按钮-    TODO --- 重新获取版本号
                if (!mProgramming) {
                    prepareDate();
                    mTextViewFileVersion.setText("");
                    mTextViewDeviceVersion.setText("");
                    mTextViewFilePath.setText("");
                    mFilePath = null;

                    letGetVersionTrigger();
                    if(getmBluetoothLeService().getState() != 2 ){   // todo --- 不是连接状态
                        autoConnectDevice();   //  33333333333333333333333333333333333
                    }else {
                        isGetOldVerSuccess = getmBluetoothLeService().writeOTAIdentfy(new byte[]{0x00});      // todo --- 发送获取 固件版本号的命令   没有用到了
                    }

                    mOTAFileAdapter = new OTAFileAdapter(BTNotificationApplication.getInstance(), mOTAFlieList, R.layout.listitem_script, mFileList);
                    mOTAListView.setAdapter(mOTAFileAdapter);
                }
                break;
            case R.id.button:   // Other Files按钮
                if(!mProgramming) {
                    VersTest();
                }
                break;
            default:
                break;
        }
    }

    public void VersTest() {
        File aaa = new File(FileUnit.getSDPath());
        Intent lIntent = new Intent(Intent.ACTION_GET_CONTENT);
        lIntent.setType("*/*");
        lIntent.addCategory(Intent.CATEGORY_OPENABLE);
        String [] mimetypes = {"application/octet-stream"};
        lIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(lIntent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if(isIntentSafe) {
            startActivityForResult(lIntent, 2);
        }else {
            Toast.makeText(this, "install a app first", Toast.LENGTH_SHORT).show();
        }
    }

    private class OadTask implements Runnable {
        int x = 0;
        @Override
        public void run() {
            while (mProgramming) {
                try {
                    x = x + SEND_INTERVAL;
                    Thread.sleep(SEND_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; (i < 4) & mProgramming; ) {   // 分 4 块 写数据
                        if (flagTag) {
                            if (mReadyToUpdate > 0) {
//                                flag = programBlock();
                                mLock.lock();
                                programBlock();  // todo  ---  开始升级写数据
                                mLock.unlock();
                                i++;
                                if(DelayTimer > 2) {
                                    DelayTimer--;
                                }
                            }

                            try {
                                x = x + DelayTimer;
                                Thread.sleep(DelayTimer);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
                }
                //one second update progress once
                if (x >= 1000) {
                    x = x % 1000;
                    try {
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                displayStats();  // todo --- 更新持续时间     CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
                            }
                        });
                    } catch (NullPointerException e) {
                        Log.e(TAG, "something wrong 2");
                    }
                }
            }
        }
    }

    private void startProgramming() {
        mProgramming = true;   // todo  --- 开始升级 置换 标志位

        DelayTimer = 2;
        byte[] buf = new byte[16];
        // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度  System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
        System.arraycopy(mFileBuffer, 0, buf, 0, 16); //TODO  将固件包的 第0位开始 copy 16 位到  buf
        StringBuilder stringBuilder = new StringBuilder(buf.length);
        for (byte byteChar : buf) {
            stringBuilder.append(String.format(Locale.ENGLISH,"%02x ", byteChar));
        }
        Log.e(TAG, "first data for ffc1 " + stringBuilder.toString());
        boolean success = getmBluetoothLeService().writeOTAIdentfy(buf);   // todo  -- 通知 开始 升级    00000000000000000000000000000000000000111111111111

        mProgInfo.reset();
        mReadyToUpdate = 1;

        canGo = true;   // ota 升级有效标志

        Thread aaa = new Thread(new OadTask());   // TODO ---  OTA升级 最终代码
        aaa.start();

        mTimer = new Timer();
        mTimerTask = new ProgTimerTask();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, TIMER_INTERVAL);
    }

    private BluetoothLeService mBluetoothLeService;
    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    private void programBlock() {   // todo  -- 在while 循环中多次调用
        if (!mProgramming) {
            return;
        }
        ifBlockSend = true;
        if (mProgInfo.iBlocks < mProgInfo.nBlocks) {    // 数量快 < 总块数
            mOadBuffer = new byte[OAD_BUFFER_SIZE];    // 18
            mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);   // 数据块转换成 16 进制
            mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);   // 数据块转换成 16 进制
            // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
            System.arraycopy(mFileBuffer, (int) mProgInfo.iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE); // mFileBuffer = new byte[0x40000];  -- 程序的字节数   ---- mOadBuffer(目标数组) --- 2 ----  16  todo --- 将 固件包的 第  程序的字节数 位 copy 到 mOadBuffer 的 第2位开始 copy长度为16
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度  System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
//            System.arraycopy(mFileBuffer, 0, buf, 0, 16); //TODO  将固件包的 第0位开始 copy 16 位到  buf
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try {
                    if(canGo) {
                        boolean success = getmBluetoothLeService().writeOTABlock(mOadBuffer);    // todo  --- 写OTA 块数据   将 mOadBuffer 数据 写给设备
                        if (success) {   //todo --- 写块数据成功了
                            canGo = false;
                        }
                    }
            } catch (NullPointerException e) {
                mProgramming = false;
                Log.e(TAG, e.toString());
            }
        } else {  // 数量快 >= 总块数   OTA 升级成功了
            isOTADone = true;
            mProgramming = false;
        }
        ifBlockSend = false;
        if (!mProgramming) { // 升级成功后，或 升级失败
            try {
                runOnUiThread(new Runnable() {
                    public void run() {
                       // 设置预期持续时间        DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
                        mButtonStopOTA.callOnClick();     // 点击停止 OTA 升级
//                    stopProgramming();
                    }
                });
            }catch (NullPointerException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void stopProgramming() {      //todo  --   停止 OTA 升级
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        mTimerTask = null;
        mProgramming = false;
    }

    private void displayStats() {
        String txt;
        int byteRate;
        int sec = mProgInfo.iTimeElapsed / 1000;
        if (sec > 0) {
            byteRate = (int) (mProgInfo.iBytes / sec);
        } else {
            return;
        }
        float timeEstimate;
        timeEstimate = ((float) (mFileImgHdr.len * 4) / (float) mProgInfo.iBytes) * sec;

        txt = String.format(Locale.ENGLISH,"Time: %d / %d sec", sec, (int) timeEstimate);     //todo --- 设置当前升级的 时间进度  Time:18/53 sec
        txt += String.format(Locale.ENGLISH,"    Bytes: %d (%d/sec)", mProgInfo.iBytes, byteRate); //todo --- 设置当前升级的 时间进度 Bytes:28384(1756/sec)
        mProgressInfo.setText(txt);    // 设置预期持续时间
    }     // BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB

    private class ImgHdr {
        long ver = -1;
        long len = -1;
        long rom_ver = -1;
        //Character imgType;
        byte[] uid = new byte[4];
    }

    private class ProgTimerTask extends TimerTask {
        @Override
        public void run() {
            mProgInfo.iTimeElapsed += TIMER_INTERVAL;   // 累加时间
        }
    }

    private class ProgInfo {
        long iBytes = 0; // Number of bytes programmed        程序的字节数
        long iBlocks = 0; // Number of blocks programmed      数量的块编程  程序的数量块
        long nBlocks = 0; // Total number of blocks           总块数
        int iTimeElapsed = 0; // Time elapsed in milliseconds

        void reset() {
            iBytes = 0;
            iBlocks = 0;
            iTimeElapsed = 0;
            nBlocks = (short) (mFileImgHdr.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
            //System.out.println("nBlocks:"+nBlocks);
        }
    }

    public void GETOTANotifyData(byte[] value) {  // TODO  --- 获取OTA 通知数据
        long blockReq = Conversion.buildUint16(value[1], value[0]);
        Log.e(TAG, "get back data blockReq: " + blockReq + ", nBlocks:" + mProgInfo.nBlocks);
        if (((mProgInfo.nBlocks) == blockReq) && mProgramming) {
            Log.e(TAG, "nBlock " + mProgInfo.nBlocks + " iBlock " + mProgInfo.iBlocks + "ready to stop OTA");
            mButtonStopOTA.callOnClick();
            //stopProgramming();
        } else {
            if (blockReq == 0 && !flagTag) {
                flagTag = true;  // todo --- 切换 可以开始 OTA 升级的标志      bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb
                mLock.lock();
                lastBlockReq = blockReq;
                mAlreadyReadCount = blockReq;
                mProgInfo.iBlocks = blockReq;
                mProgInfo.iBytes = (blockReq) * OAD_BLOCK_SIZE;
                mLock.unlock();
            } else {
                mLock.lock();
                lastBlockReq = blockReq;
                mAlreadyReadCount = blockReq;
                mProgInfo.iBlocks = blockReq;
                mProgInfo.iBytes = (blockReq) * OAD_BLOCK_SIZE;
                if(DelayTimer < 50) {
                    DelayTimer = 50;
                }
                mLock.unlock();
            }
        }
    }

    public void GETVersionData(byte[] value) {   // 设备端返回 固件信息
        StringBuilder stringBuilder = new StringBuilder(value.length);
        stringBuilder.append(String.format(Locale.ENGLISH,"%02X", value[1]));
        stringBuilder.append(String.format(Locale.ENGLISH,"%02X", value[0]));
        Log.e(TAG, stringBuilder.toString());
        mTextViewDeviceVersion.setText(stringBuilder.toString());   // 设置 Device Ver. 设备版本

        stringBuilder = new StringBuilder(value.length);
        Log.e(TAG, String.valueOf(value.length));
        if(value.length == 10) {
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", value[9]));
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", value[8]));
        }else {
            stringBuilder.append("FF");
            stringBuilder.append("FF");
        }
        Log.e(TAG, stringBuilder.toString());
//        mTextViewDeviceRomVersion.setText(stringBuilder.toString()); // 设置   Dev. Rom Ver. : 当前设备版本-
        MAXNotify = 10;
    }

    private boolean getFileVerion(String filepath) {
        mFileIndexBuffer = new byte[16];
        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            File f = new File(filepath);
            stream = new FileInputStream(f);
            if(stream.available() > 16) {
                stream.read(mFileIndexBuffer, 0, 16);
                stream.close();
            }else {
                stream.close();
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        }

        if(checkBinCorrect()) {
            StringBuilder stringBuilder = new StringBuilder(5);
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[5]));
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[4]));    // 1120
            Log.e(TAG, stringBuilder.toString());

            mTextViewFileVersion.setText(stringBuilder.toString());   // 设置 OTA File Ver. OTA文件版本      1120

            stringBuilder = new StringBuilder(5);
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[15]));
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[14]));   // 0001
            Log.e(TAG, stringBuilder.toString());

//            mTextViewOTARomVersion.setText(stringBuilder.toString());    // 设置 File Rom Ver.. 当前OTA文件版本
            mButtonStartOTA.setEnabled(true);
            return true;
        }else {
            Toast.makeText(BTNotificationApplication.getInstance(), "not a correct bin", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean getFileVerion() {
        mFileIndexBuffer = new byte[16];
        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            stream = getContentResolver().openInputStream(mFilePath);
            if(stream.available() > 16) {
                stream.read(mFileIndexBuffer, 0, 16);
                stream.close();
            }else
            {
                stream.close();
                return false;
            }
        } catch (IOException e) {
            // Handle exceptions here
            Log.e(TAG, e.toString());
            return false;
        }

        if(checkBinCorrect()) {
            StringBuilder stringBuilder = new StringBuilder(5);
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[5]));
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[4]));
            Log.e(TAG, stringBuilder.toString());

            mTextViewFileVersion.setText(stringBuilder.toString());  // 设置 OTA File Ver. OTA文件版本

            stringBuilder = new StringBuilder(5);
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[15]));
            stringBuilder.append(String.format(Locale.ENGLISH,"%02X", mFileIndexBuffer[14]));
            Log.e(TAG, stringBuilder.toString());

//            mTextViewOTARomVersion.setText(stringBuilder.toString());   // 设置 File Rom Ver.. 当前OTA文件版本
            return true;
        }else {
            Toast.makeText(BTNotificationApplication.getInstance(), "not a correct bin", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean loadFile(String filepath) {
        boolean fSuccess = false;
        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            File f = new File(filepath);    // 根据文件路径，获取到升级的文件
            stream = new FileInputStream(f);
            stream.read(mFileBuffer, 0, mFileBuffer.length);
            stream.close();
        } catch (IOException e) {
            // Handle exceptions here
            return false;
        }
        mFileImgHdr.ver = Conversion.buildUint16(mFileBuffer[5], mFileBuffer[4]);
        mFileImgHdr.len = Conversion.buildUint16(mFileBuffer[7], mFileBuffer[6]);
        mFileImgHdr.rom_ver = Conversion.buildUint16(mFileBuffer[15], mFileBuffer[14]);
// Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
        System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
        return fSuccess;
    }

    public void clickStopBtn() {
        if (mProgramming) {
            mButtonStopOTA.callOnClick();
        }
    }
    public void clickRefreshBtn() {
        mButtonRefresh.callOnClick();
    }

    public boolean ismProgramming()
    {
        return mProgramming;
    }

    public boolean isOTADone()
    {
        return isOTADone;   //todo
    }

    public void setOutSideFileData(String filename,Uri selectFilePath) {
        mFilePath = selectFilePath;
        mOTAFileAdapter.init();
        mOTAFileAdapter.notifyDataSetChanged();
        if(getFileVerion() /*&& filename.endsWith(".bin")*/) {
            mTextViewFilePath.setText(filename);
            if(!mButtonStartOTA.isEnabled()) {
                mButtonStartOTA.setEnabled(true);
            }
        }else {
            mFilePath = null;
            Toast.makeText(BTNotificationApplication.getInstance(), "not a correct bin", Toast.LENGTH_SHORT).show();
            mTextViewFileVersion.setText("");
//            mTextViewOTARomVersion.setText("");     // 设置 File Rom Ver.. 当前OTA文件版本
            mTextViewFilePath.setText("");
            if(mButtonStartOTA.isEnabled()) {
                mButtonStartOTA.setEnabled(false);
            }
        }
    }

    public boolean checkBinCorrect() {
        byte[] buf = new byte[4];
        System.arraycopy(mFileIndexBuffer, 8, buf, 0, 4);

        final StringBuilder stringBuilder = new StringBuilder(5);
        stringBuilder.append(String.format(Locale.ENGLISH,"%02X ", buf[0]));
        stringBuilder.append(String.format(Locale.ENGLISH,"%02X ", buf[1]));
        stringBuilder.append(String.format(Locale.ENGLISH,"%02X ", buf[2]));
        stringBuilder.append(String.format(Locale.ENGLISH,"%02X", buf[3]));

        Log.e(TAG, stringBuilder.toString());

        if((buf[0] == 0x42) && (buf[1] == 0x42) && (buf[2] == 0x42) && (buf[3] == 0x42)) {
            OTAType = 1;
            mButtonStartOTA.setText("开始升级");   // Partial OTA
            return true;
        }else  if((buf[0] == 0x53) && (buf[1] == 0x53) && (buf[2] == 0x53) && (buf[3] == 0x53)) {
            OTAType = 2;
            mButtonStartOTA.setText("开始升级");     // Full OTA
            return true;
        }else {
            OTAType = 0;
            mButtonStartOTA.setText("开始升级");   // Start OTA
            return false;
        }
    }

    public void setBlockIndex(int x) {
        if(mProgramming) {
            try {
                if (x == 1) {     // todo -- 设置进度条的状态
                    mLock.lock();
                    canGo = true;
                    mAlreadyReadCount = mProgInfo.iBlocks + 1;
                    mProgInfo.iBlocks++;
                    mProgInfo.iBytes = mProgInfo.iBytes + OAD_BLOCK_SIZE;
                    mLock.unlock();
                    mProgressBar.setProgress((short) ((mProgInfo.iBlocks * 100) / mProgInfo.nBlocks));     //TODO  ---  升级过程中设置 进度条

                    mProgressInfo.setText((short) ((mProgInfo.iBlocks * 100) / mProgInfo.nBlocks) + "%");       // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

                    Log.e(TAG, String.valueOf(mProgInfo.iBlocks));
                } else {
                    mLock.lock();
                    canGo = true;
                    mLock.unlock();
                }
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(autoConnectThread != null) {     // todo -- 连接成功，取消连接的线程
            autoConnectThread.cancel();
            autoConnectThread = null;
        }

        EventBus.getDefault().unregister(this);
        if(null != mbroadcast){
            unregisterReceiver(mbroadcast);
        }
        unbindService(mServiceConnection);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckboxEvent event) {
         if(event.getmDisconnect() == 11) {
             setBlockIndex(1);    // todo -- 设置进度条的状态
         }else if(event.getmDisconnect() == 10) {
             setBlockIndex(2);
         }

        if(event.getmDisconnect() == 1) {
                Toast.makeText(this, "Max Retry", Toast.LENGTH_SHORT).show();
                finish();
        }else if(event.getmDisconnect() == 2) {
                Toast.makeText(this, "Get Device Version Fail", Toast.LENGTH_SHORT).show();
        }
    };

    private boolean canChangeToOTA() {
        if(canOTAcount == 2)
            return true;
        else
            return false;
    }

    ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
    ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
    ArrayList<UUIDCheckList> checkLists = new ArrayList<UUIDCheckList>();
    public void displayGattServices(List<BluetoothGattService> gattServices) {
        int charSize = 0;
        canOTAcount = 0;
        mOTAUUID1 = null;
        mOTAUUID2 = null;
        if (gattServices == null) {
            Log.e(TAG, "gattService is null");
            return;
        }
//        clearUUIDList();
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
//         Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            int tempCharSize = 0;
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);

            if (!gattServiceData.contains(currentServiceData)) {
                gattServiceData.add(currentServiceData);
                ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
                List<Boolean> serviceList_1 = new ArrayList<>();
                List<Boolean> serviceList_2 = new ArrayList<>();
                List<Boolean> serviceList_3 = new ArrayList<>();

//             Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    serviceList_1.add(false);
                    serviceList_2.add(false);
                    serviceList_3.add(false);
                    UUIDCheckList lUUIDCheckList = new UUIDCheckList();
                    tempCharSize++;
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    if(uuid.equals(uuid_ota1.toString())) {
                        canOTAcount = canOTAcount + 1;
                        mOTAUUID1 = gattCharacteristic;
                        Log.e(TAG, "find one");
                       getmBluetoothLeService().setCharacteristicNotification(mOTAUUID1, true);    // todo  ---  使能服务通知
                    }else if(uuid.equals(uuid_ota2.toString())) {
                        canOTAcount = canOTAcount + 1;
                        mOTAUUID2 = gattCharacteristic;    // todo --- UUID 赋值
                        Log.e(TAG, "find two");
                    }
                    final int charaProp = gattCharacteristic.getProperties();
                    StringBuilder a = new StringBuilder(" ");
                    currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                    currentCharaData.put(LIST_UUID, uuid);
                    if((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) == 2) {
                        a.append("[Read] ");
                    }
                    if((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 4) {
                        lUUIDCheckList.setWriteFlag(true);
                        a.append("[Write No Response] ");
                    }
                    if((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE)== 8) {
                        lUUIDCheckList.setWriteFlag(true);
                        a.append("[Write] ");
                    }
                    if((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 16) {
                        lUUIDCheckList.setNotifyFlag(true);
                        a.append("[Notify] ");
                    }
                    if((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 32) {
                        lUUIDCheckList.setIndicateFlag(true);
                        a.append("[Indicate] ");
                    }
                }
                mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }
    }

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
//            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Bluetooth adapter state change
                switch (mBluetoothAdapter.getState()) {
                    case BluetoothAdapter.STATE_OFF:
                        mIntent = new Intent();
                        mIntent.putExtra("Result OTA", "Bluetooth function disable");
//                        DeviceControlActivity.this.setResult(4, mIntent);
                        finish();
                        break;
                    default:
                        break;
                }
            }

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 仅打印log供调试   todo  --- 连接成功
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.testDiscoverService();
                }

                if(autoConnectThread != null) {     // todo -- 连接成功，取消连接的线程
                    autoConnectThread.cancel();
                    autoConnectThread = null;
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {    // 升级成功断开连接，关闭升级页面     todo  --- 断开连接
                Log.e(TAG, "get disconnect event22");
                if(isBindServerSuccess){
                    if(autoConnectThread != null) {     // todo -- 连接成功，取消连接的线程
                        autoConnectThread.cancel();
                        autoConnectThread = null;
                    }

                    if(!isGetOldVerSuccess){
                        if(!isShowFailDialog){
                            alertDialog = new AlertDialog(FirmWareUpdateActivityForBK.this).builder();
                            alertDialog.setCancelable(false);
                            alertDialog.setMsg(getString(R.string.checking_new_version_fail));  // R.string.firmware_is_update   getString(R.string.firmware_is_update)   checking_new_version_fail    "版本检测失败,是否重试"
                            alertDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isShowFailDialog = false;
                                    autoConnectDevice();
//                                isUploading = true; //TODO --- 开始升级了
//                                mButtonStartOTA.callOnClick();
                                }
                            });
                            alertDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //////////////////// todo  ---  取消后重连 ////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    Intent mIntent = new Intent();
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("Result OTA", "Success");
                                    setResult(8, "aa", mBundle);
                                    Log.e("liuxiaodebug", "setResult了");
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    isUploading = false;
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    isShowFailDialog = false;
                                    finish();
                                }
                            });
                            alertDialog.show();
                            isShowFailDialog = true;
                        }
                    }
                }

                if(isStartOta){  // todo --- 升级成功了
                    Intent mIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putString("Result OTA", "Success");
                    setResult(8, "aa", mBundle);
                    Log.e("liuxiaodebug", "setResult了");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isUploading = false;
                    finish();
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {     // 建立GATT服务，开始 通知使能
                retryNo = 3;
                flagDiscoverFail = false;
                displayGattServices(mBluetoothLeService.getSupportedGattServices());   // todo -- 使能服务通知
                mTextViewDeviceVersion.setText(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION));   // 设置 Device Ver. 设备版本  TODO -- 设置旧固件版本号
                if(null != mBluetoothLeService && null != mOTAUUID2){
                    boolean bbb =  getmBluetoothLeService().setCharacteristicNotification(mOTAUUID2, true);  //  todo  --- 重要的步骤       // todo  ---  使能服务通知 2
                }

                loodingfilePath();   // todo ---获取新的版本号       获取设备固件版本号成功后，再获取后台固件版本号
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_BYTE);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                StringBuilder stringBuilder = new StringBuilder(value.length);
                for (byte byteChar : value) {
                    stringBuilder.append(String.format(Locale.ENGLISH,"%02x ", byteChar));
                }
                Log.e(TAG, "first data for ffc1 " + stringBuilder.toString());
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                if(uuidStr.equals(BluetoothLeService.UUID_BLOCK.toString())) {
                    GETOTANotifyData(value);        // todo --- 开始升级了，才会进来  000000000000000000000000000000000000000000002222222222222222222222222222222222222222
                }else if(uuidStr.equals(BluetoothLeService.UUID_IDENTFY.toString())) {
                    mHandler.removeCallbacks(mRunnable);
                    Toast.makeText(BTNotificationApplication.getInstance(), "Get Device Version Success", Toast.LENGTH_SHORT).show();    // 获取固件版本号失败3次后，断开蓝牙重新获取
                }
            } else if (BluetoothLeService.ACTION_DATA_WRITE_FAIL.equals(action)) {

            } else if (BluetoothLeService.ACTION_DATA_WRITE_SUCCESS.equals(action)) {

            } else if (BluetoothLeService.ACTION_DATA_READ_FAIL.equals(action)) {
                Toast.makeText(context, "Fail READ", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_QUERY_SUCCESS.equals(action)) {
                Toast.makeText(context, "Query", Toast.LENGTH_SHORT).show();
            }else if(BluetoothLeService.ACTION_NOTIFY_SUCCESS.equals(action)) {
                String a = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Toast.makeText(context, "Notify change " + a, Toast.LENGTH_SHORT).show();
            }else if(BluetoothLeService.ACTION_NOTIFY_FAIL.equals(action)) {
                Toast.makeText(context, "Notify fail", Toast.LENGTH_SHORT).show();
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED_FAIL.equals(action)) {
                Log.e(TAG, "get service fail");
                Toast.makeText(context, "Get Service Fail", Toast.LENGTH_SHORT).show();
                if(retryNo > 0) {
                    flagDiscoverFail = true;
                    mBluetoothLeService.disconnect();
                    invalidateOptionsMenu();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException a){
                        Log.e(TAG, a.toString());
                    }
                    retryNo--;
                }else {
                    mBluetoothLeService.disconnect();
                }
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice aaa = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (aaa.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.e(TAG, "bonded");
                } else if (aaa.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.e(TAG, "bonding");
                } else {
                    Log.e(TAG, "unbound");
                }
            }else if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isUploading) {
                Toast.makeText(BTNotificationApplication.getInstance(), R.string.dfu_status_uploading, Toast.LENGTH_SHORT).show();
                return true;
            }
            finish();
        }
        return true;
    }
}
