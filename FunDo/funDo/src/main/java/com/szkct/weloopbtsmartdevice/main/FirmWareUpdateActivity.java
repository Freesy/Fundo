package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.bluetooth.KCTBluetoothManager;
import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.map.dialog.AlertDialog;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class FirmWareUpdateActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback{

    private ProgressBar progressBar;
    private TextView progressTv, norVersionTv, newVersionTv ,page_gujianshengji_title;
    private ImageView firmwareSuccess, firmwareUpdate, firmwareUpload;
    private Button tryagin;
    private int code;
    private int type;            
    private Context context;
    private final static String TAG = FirmWareUpdateActivity.class.getName();
    private String filepath;    
    private String file_size;   
    private String firmware_version;    
    private String server_firmware_version;   
    private File file;
    private int mFileType;      

    private boolean dfuSucceed = false;
    private int platformCode;
    private boolean isUploading = false;
    private int autoRetry = 0; 


    private static final String PREFS_DEVICE_NAME = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_DEVICE_NAME";
    private static final String PREFS_FILE_NAME = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_FILE_NAME";
    private static final String PREFS_FILE_TYPE = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_FILE_TYPE";
    private static final String PREFS_FILE_SIZE = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_FILE_SIZE";

    private static String myaddress,myNAME;
    private BluetoothAdapter mBluetoothAdapter; 
    private boolean isDfulang;
    private Handler handler;
    private boolean Upgradeornot=false;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fendo/";
    File files;

    private int packNumber = 0;
    private int yuShu = 0;
    private byte[] fileBytes;
    private int index = 0;
    private java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
    if("connect_sussess".equals(event.getMessage())){
        Upgradeornot=true;
    }else if("connect_notsussess".equals(event.getMessage())){
        Upgradeornot=false;
    }else if("firmWare_start".equals(event.getMessage())){
        byte[] bytes = new byte[20];

        System.arraycopy(fileBytes, index * 20, bytes, 0, 20);
        Log.i(TAG,"数据长度 = " + index * 20);
        //BTNotificationApplication.getMainService().write(bytes);
        KCTBluetoothManager.getInstance().sendCommand_a2d(bytes);
        int percent = (int)(((double)index * 20/fileBytes.length) * 100);
        //int percent = (int)(Double.parseDouble(df.format((double)index * 20/fileBytes.length)) * 100);
        if(percent <= 100) {
            progressTv.setText(percent + "%");
            progressBar.setProgress(percent);
        }
        index++;
        isUploading = true;
    }else if("firmWare_sendFile".equals(event.getMessage())){
        if(index == 0){
            return;
        }
        if(packNumber > index) {
            byte[] bytes = new byte[20];
            System.arraycopy(fileBytes, index * 20, bytes, 0, 20);
            //BTNotificationApplication.getMainService().write(bytes);
            KCTBluetoothManager.getInstance().sendCommand_a2d(bytes);
            Log.i(TAG,"数据长度 = " + index * 20);
            int percent = (int)(((double)index * 20/fileBytes.length) * 100);
            //int percent = (int)(Double.parseDouble(df.format((double)index * 20/fileBytes.length)) * 100);
            if(percent <= 100) {
                progressTv.setText(percent + "%");
                progressBar.setProgress(percent);
            }
            index++;
        }else{
            Log.i(TAG,"yushu = " + yuShu);
            byte[] bytes = new byte[yuShu];
            Log.i(TAG,"index = " + index);
            System.arraycopy(fileBytes, index * 20, bytes, 0, yuShu);
            //BTNotificationApplication.getMainService().write(bytes);
            KCTBluetoothManager.getInstance().sendCommand_a2d(bytes);
            Log.i(TAG,"数据长度 = " + (index * 20 + bytes.length));
            int percent = (int)(((double)index * 20/fileBytes.length) * 100);
            //int percent = (int)(Double.parseDouble(df.format((double)index * 20 + yuShu/fileBytes.length)) * 100);
            if(percent <= 100) {
                progressTv.setText(percent + "%");
                progressBar.setProgress(percent);
            }
            MainService.isSendFile = false;
            index = 0;
            isUploading = false;
            EventBus.getDefault().post(new MessageEvent("sendFile_end"));
            finish();
        }

    }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }


        setContentView(R.layout.activity_firmwareupdate);

        EventBus.getDefault().register(this);
//        L2Send.sendFirmUpdate(null);//  TODO --- 调试 固件升级用 ，临时打开
        context = this;
        tryagin = (Button) findViewById(R.id.user_Updata_button);
        progressBar = (ProgressBar) findViewById(R.id.firmware_pb_progressbar);   
        progressTv = (TextView) findViewById(R.id.firmeware_progressTv);          
        norVersionTv = (TextView) findViewById(R.id.firmeware_normalVersionTv);   
        newVersionTv = (TextView) findViewById(R.id.firmeware_newVersionTV);      
        firmwareSuccess = (ImageView) findViewById(R.id.firmware_success_iv);
        firmwareUpdate = (ImageView) findViewById(R.id.firmware_update_iv);
        firmwareUpload = (ImageView) findViewById(R.id.firmware_upload_iv);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading) {
                    Toast.makeText(context, R.string.dfu_status_uploading, Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });

        page_gujianshengji_title = (TextView) findViewById(R.id.page_gujianshengji_title);
        String languageLx = Utils.getLanguage();
        if(languageLx.equals("ja")){
            page_gujianshengji_title.setTextSize(12);
        }



        if(null!=SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MAC)){
                myaddress = SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                myNAME= SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MACNAME).toString();
                Toast.makeText(FirmWareUpdateActivity.this,myaddress,Toast.LENGTH_SHORT).show();
                if(myNAME.contains("DfuTarg")){    // "DfuTarg".equals(myNAME)
                    isDfulang=true;
                    SharedPreferences mySharedPre= FirmWareUpdateActivity.this.getSharedPreferences("filepath", Activity.MODE_PRIVATE);
                   if(null!=mySharedPre.getString("filepath","")){filepath=mySharedPre.getString("filepath","");}
                   if(fileIsExists(filepath)){
                       Eond(1);
                       progressBar.setVisibility(View.VISIBLE);
                       progressTv.setVisibility(View.VISIBLE);
                       norVersionTv.setVisibility(View.VISIBLE);
                       newVersionTv.setVisibility(View.VISIBLE);
                   }else{
                       loodingfilePath();
                   }
                }else{
                    isDfulang=false;
                    loodingfilePath();
                }

            }


        tryagin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(FirmWareUpdateActivity.this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                    return;
                }
                tryagin.setText(R.string.bluetooth_connecting);
                tryagin.setClickable(false);
                    
                    if(isDfulang){
                        BTNotificationApplication.getMainService().connectDevice(bluetoothAdapter.getRemoteDevice(myaddress));
                        //MainService.getInstance().connectBluetooth(myaddress, true);
                        if(Upgradeornot){Eond(1);}
                    }else{
                        BTNotificationApplication.getMainService().connectDevice(bluetoothAdapter.getRemoteDevice(getNewMac(myaddress)));
                        //MainService.getInstance().connectBluetooth(getNewMac(myaddress), true);
                        if(Upgradeornot){Eond(0);}
                    }

            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(null!=msg&&null!=msg.getData()){
                    progressBar.setVisibility(View.VISIBLE);
                    Bundle B=msg.getData();
                    int str = (int) B.get("num");
                    Log.e("num", str + "");
                    progressBar.setProgress(str);
                }
            }
        };
    }

    /**
     * 
     */
    private  void loodingfilePath(){
        Intent intent = getIntent();
        filepath = intent.getStringExtra("url_path");
        if (TextUtils.isEmpty(filepath)&&!myNAME.contains("DfuTarg")) {    // &&!"DfuTarg".equals(myNAME)
            setIVVisible(firmwareSuccess);
            progressTv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            progressTv.setText(R.string.is_the_latest_version);
        } else {
            file_size = intent.getStringExtra("file_size");
            server_firmware_version = intent.getStringExtra("server_firmware_version");
            firmware_version = intent.getStringExtra("firmware_version");
            type = intent.getIntExtra("file_type", 0);
            if (type == 0) {//Nordic
                file = new File(
                        Environment.getExternalStorageDirectory()+"/fendo/",
                        "funDo.zip");
            } else {
                file = new File( //dialog
                        Environment.getExternalStorageDirectory(),
                        type + ".img");
            }
            if (filepath.contains(" ")) {
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
                        files = new File(path);// 
                        if (!files.exists()) {
                            file.mkdir();
                        }
                        if(type == 0) {   //Nordic
                            filepath = path + "funDo.zip";
                        }else{            //dialog
                            filepath = path + "funDo.img";
                        }
                        
                        SharedPreferences mySharedPre= FirmWareUpdateActivity.this.getSharedPreferences("filepath", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editorc = mySharedPre.edit();
                        editorc.putString("filepath",filepath);editorc.commit();
                        AlertDialog alertDialog = new AlertDialog(context).builder();
                        alertDialog.setCancelable(false);
                        alertDialog.setMsg(getString(R.string.firmware_is_update));
                        alertDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setIVVisible(firmwareUpdate);
                                progressBar.setVisibility(View.VISIBLE);
                                progressTv.setVisibility(View.VISIBLE);
                                norVersionTv.setVisibility(View.VISIBLE);
                                newVersionTv.setVisibility(View.VISIBLE);
                                progressTv.setText("0%");
                                if(Utils.getLanguage().equals("de") || Utils.getLanguage().equals("tr")){
                                    newVersionTv.setTextSize(14);
                                    norVersionTv.setTextSize(14);
                                }
                                norVersionTv.setText(context.getString(R.string.firmware_upload_nor) + firmware_version);
                                newVersionTv.setText(context.getString(R.string.firmware_update) + server_firmware_version);
                                if(type == 0) {   //Nordic
                                    if (isDfulang) {
                                        Eond(2);
                                    } else {
                                        L2Send.sendFirmUpdate(null);

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                scanDevice();
                                            }
                                        }, 3000);
                                    }
                                }else{    //dialog
                                    byte []buffer = new byte[64];
                                    byte[] fileByte = Utils.readFile(file.getPath());
                                    if(fileByte != null){
                                        byte crc = 0;
                                        for(int i = 64; i < fileByte.length; i++){
                                            crc ^= fileByte[i];
                                        }
                                        //file的CRC校验固定33索引
                                        fileByte[33] = crc;
                                        Log.e(TAG,"crc " + crc);
                                        System.arraycopy(fileByte, 0, buffer, 0, 64);

                                    }
                                    Log.e(TAG,"crc = " + Utils.bytesToHexString(fileByte));
                                    fileBytes = new byte[fileByte.length - 64];
                                    System.arraycopy(fileByte,64,fileBytes,0,fileByte.length - 64);
                                    Log.e(TAG,"fileBytes.length " + fileBytes.length);
                                    packNumber = fileBytes.length/20;
                                    yuShu = fileBytes.length % 20;
                                    L2Send.sendFirmUpdate(buffer);

                                }
                            }
                        });
                        alertDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {finish();}
                        });
                        alertDialog.show();
                    }catch (Exception  e){}}
                @Override
                public void onFailure(HttpException e, String s) {}});
        }
    }







    public boolean fileIsExists(String FIE){
        try{
            File f=new File(FIE);
            if(!f.exists()){
                return false;
            }

        }catch (Exception e) {
            return false;
        }return true;
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



    /**
     * mac
     */
    public void scanDevice() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothAdapter.startLeScan(this);
    }

    /**
     * 
     * @param
     */
    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        if(isDfulang==false){
            try{
                //mDeviceAddress
                String address = bluetoothDevice.getAddress();
                System.out.println("address" + address.toString());
                if (TextUtils.equals(address, getNewMac(myaddress))) {//判断收到的蓝牙mac是否与之前相等
                    System.out.println("扫描到地址" );

                    String devicename = bluetoothDevice.getName();
                    SharedPreUtil.savePre(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC, address);// 存储当前连接的蓝牙地址。
                    SharedPreUtil.savePre(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.MACNAME, devicename);// 存储当前连接的蓝牙ming。


                    if (null != mBluetoothAdapter) {
                        mBluetoothAdapter.stopLeScan(FirmWareUpdateActivity.this);//
                        BTNotificationApplication.getMainService().connectDevice(mBluetoothAdapter.getRemoteDevice(getNewMac(myaddress)));
               //MainService.getInstance().connectBluetooth(getNewMac(myaddress), true);  //
                        Eond(0);
                    }

                }
            }catch (Exception E){E.printStackTrace();}
        }else{
           
            String address = bluetoothDevice.getAddress();
            System.out.println("address" +address.toString());
            if (TextUtils.equals(address, myaddress)) {
                BTNotificationApplication.getMainService().connectDevice(bluetoothDevice);
                //MainService.getInstance().connectBluetooth(myaddress, true);  //
                Eond(1);
            }

        }

    }





    /**
     * 
     * @param switchs
     */
    private  void Eond(int switchs){
    try {
        
        final DfuServiceInitiator starter;
        String dfuName = SharedPreUtil.readPre(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.MACNAME); // todo ---- 需加判断
        if(0==switchs){
            starter = new DfuServiceInitiator(getNewMac(myaddress)).setDeviceName(dfuName).setKeepBond(false);
        }else{
            starter = new DfuServiceInitiator(myaddress).setDeviceName(dfuName).setKeepBond(false);
//            starter = new DfuServiceInitiator(myaddress).setDeviceName("DfuTarg").setKeepBond(false);
        }
        File filed = new File(filepath);
        Uri fileUri = Uri.fromFile(filed);
        starter.setZip(fileUri, filepath);
        starter.start(FirmWareUpdateActivity.this, DfuService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNewMac(String address) {
        String front = address.substring(0,address.length()-2);  // D1:F9:E7:31:C5:     ---- C0:51:69:0A:9A:
        String back = address.substring(address.length()-2);    // FF                   ---  C4
        int next = Integer.parseInt(back, 16)+1;   // 256
        back = Integer.toHexString(next).toUpperCase();  // 100

        if(back.length() == 3){
            back = back.substring(1,3);
        }else {
            back = back.length() == 1?"0"+back:back;   // 100
        }
//        String last = front + back;
        return front + back;
    }

    private String getOldMac(String address){
        String front = address.substring(0,address.length()-2);  // D1:F9:E7:31:C5:     ---- C0:51:69:0A:9A:
        String back = address.substring(address.length()-2);    // FF                   ---  C4
        int next = Integer.parseInt(back, 16);   // 256
        if(next == 0){
            back = "FF";
        }else {
            next = Integer.parseInt(back, 16) - 1;   // 256
            back = Integer.toHexString(next).toUpperCase();  // 100
            back = back.length() == 1?"0"+back:back;
        }
        return front + back;
    }

    public void setIVVisible(View v) {
        firmwareSuccess.setVisibility(View.GONE);
        firmwareUpload.setVisibility(View.GONE);
        firmwareUpdate.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    PowerManager.WakeLock mWakeLock;

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        PowerManager pManager = ((PowerManager) getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
//        if (null != mWakeLock) {
//            mWakeLock.release();
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        if (null != mWakeLock) {
            mWakeLock.release();
        }

        EventBus.getDefault().unregister(this);
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
            /**
             * ota
             * @param deviceAddress
             */
            @Override
        public void onDeviceConnecting(final String deviceAddress) {
            setIVVisible(firmwareUpdate);
            progressTv.setText("0%");
                if(null!=firmware_version){
                    norVersionTv.setText(context.getString(R.string.firmware_upload_nor) + firmware_version);
                }
                if(null!=server_firmware_version){
                    newVersionTv.setText(context.getString(R.string.firmware_update) + server_firmware_version);
                }
            Log.i(TAG, "onDeviceConnecting = " + deviceAddress);
        }

        /**
         * ota
         * @param deviceAddress
         */
        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            setIVVisible(firmwareUpdate);
            isUploading = true;
            progressTv.setText("0%");

            if(null!=firmware_version){
                norVersionTv.setText(context.getString(R.string.firmware_upload_nor) + firmware_version);
            }
            if(null!=server_firmware_version){
                newVersionTv.setText(context.getString(R.string.firmware_update) + server_firmware_version);
            }
            tryagin.setVisibility(View.GONE);

            SharedPreUtil.setParam(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, true);
            Log.i(TAG, "onDfuProcessStarting = " + deviceAddress);

            SharedPreUtil.setParam(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
            SharedPreUtil.setParam(FirmWareUpdateActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH,"2");
        }


        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            Log.i(TAG, "onEnablingDfuMode = " + deviceAddress);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            Log.i(TAG, "onFirmwareValidating = " + deviceAddress);
        }

        /**
         * ota
         * @param deviceAddress
         */
        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            setIVVisible(firmwareUpdate);
            if(progressBar.getProgress()>0 && progressBar.getProgress()<100){
                tryagin.setVisibility(View.VISIBLE);

            }
        }

        /**
         * ota
         * @param deviceAddress
         */
        @Override
        public void onDfuCompleted(final String deviceAddress) {
            setIVVisible(firmwareSuccess);
            isUploading = false;
            norVersionTv.setText(context.getString(R.string.firmware_upload_nor) + server_firmware_version);
            newVersionTv.setText(context.getString(R.string.firmware_update_finish) + server_firmware_version);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dfuSucceed = true;
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
            Log.i(TAG, "onDfuCompleted = " + deviceAddress);
            SharedPreUtil.setParam(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false);
            SharedPreUtil.savePre(FirmWareUpdateActivity.this, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION, server_firmware_version);
            SharedPreUtil.savePre(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC, getOldMac(deviceAddress));
            SharedPreUtil.savePre(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.MACNAME, myNAME);
            SharedPreUtil.setParam(FirmWareUpdateActivity.this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
            SharedPreUtil.setParam(FirmWareUpdateActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH,"2");
            finish();
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            Log.i(TAG, "onDfuAborted = " + deviceAddress);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                    updateFirmware(file);
                }
            }, 200);*/
        }

        /**
         * 
         * @param deviceAddress
         */
        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            Log.i(TAG, "onProgressChanged = " + deviceAddress);
            Message message =new Message();
            message.what = 0;
            message.obj = percent;
            Bundle  bundle = new Bundle();
            bundle.putInt("num",percent);
            message.setData(bundle);
            handler.sendMessage(message);

            progressTv.setText(percent + "%");
        }


        /**
         * ota
         * @param deviceAddress
         */
        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
    
           if(isDfulang){
               Eond(1);
           }else{
               Eond(0);
           }
        }
    };


 
    private boolean isDfuServiceRunning() {
        Log.e("DfuActivity", "isDfuServiceRunning()");
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     */
    private void abortUpload() {
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
        manager.sendBroadcast(pauseAction);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isUploading) {
                Toast.makeText(context, R.string.dfu_status_uploading, Toast.LENGTH_SHORT).show();
                return true;
            }
            finish();
        }
        return true;
    }

}
