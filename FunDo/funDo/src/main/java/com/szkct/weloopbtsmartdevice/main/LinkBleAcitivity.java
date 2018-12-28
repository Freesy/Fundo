///*
// * Copyright (C) 2013 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.szkct.weloopbtsmartdevice.main;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Looper;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.LinearInterpolator;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.kct.fundo.btnotification.R;
//import com.kct.fundo.zxing.android.CaptureActivity;
//import com.mediatek.wearable.WearableListener;
//import com.mediatek.wearable.WearableManager;
//import com.szkct.bluetoothgyl.BleContants;
//import com.szkct.bluetoothgyl.BluetoothMtkChat;
//import com.szkct.bluetoothgyl.L2Bean;
//import com.szkct.bluetoothgyl.L2Send;
//import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
//import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
//import com.szkct.weloopbtsmartdevice.util.MessageEvent;
//import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
//import com.szkct.weloopbtsmartdevice.util.StringUtils;
//import com.szkct.weloopbtsmartdevice.util.Utils;
//import com.umeng.analytics.MobclickAgent;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static com.szkct.bluetoothgyl.BleContants.BLE_YDS_UUID;
//import static com.szkct.bluetoothgyl.BleContants.BLE_YDS_UUID_HUAJING;
//import static com.szkct.bluetoothgyl.BleContants.MTK_YDS_2502_UUID;
//import static com.szkct.bluetoothgyl.BleContants.MTK_YDS_2503_UUID;
//
///**
// * Activity for scanning and displaying available Bluetooth LE devices.
// * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity mtk
// * source | chendalin modify
// */
//public class LinkBleAcitivity extends AppCompatActivity implements OnClickListener {   //  手表蓝牙连接页面
//    private final static String TAG = "LinkBleAcitivity";
//
//    public static String REMOTE_DEVICE_INFO = "REMOTE_DEVICE_INFO";
//
//    private DeviceListAdapter mDeviceListAdapter;
//
//    private int mWorkingMode;
//    private int linking = 0;// 未连接
//    private BluetoothAdapter mBluetoothAdapter;
//    private LinearLayout bangdeview;
//    private boolean mScanning;
//
//    private Handler mHandler;
//    private LinearLayout liLookBlue;
//    private TextView tvSesarch;
//    private ImageView img_bluetooth_sesarch, iv_hasdevice;
//    private ListView listEquipment;
//    private boolean isSearch = true;
//    private TextView link_blename_txt;
//    private static final int REQUEST_ENABLE_BT = 1;
//    private static final int REQUEST_CODE_SCAN = 0x0000;
//    private static final String DECODED_CONTENT_KEY = "codedContent";
//    private static final String DECODED_BITMAP_KEY = "codedBitmap";
//    private Runnable mStopRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.d(TAG, "mStopRunnable begin");
//            scanDevice(false);
//        }
//    };
//    Set<BluetoothDevice> devices;
//    private static final long SCAN_PERIOD = 60 * 1000;
//    Animation operatingAnim, aoperatingAnim, boperatingAnim;
//    Bluttoothbroadcast blutbroadcast;
//    private LoadingDialog loadingDialog;
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onServiceEventMainThread(MessageEvent event) {
//        if(event.getMessage().equals("connect_Dfu")){
//            BluetoothDevice device = (BluetoothDevice) event.getObject();
//            SharedPreUtil.savePre(this, SharedPreUtil.USER, SharedPreUtil.MAC, device.getAddress());// 存储当前连接的蓝牙地址。
//            SharedPreUtil.savePre(this, SharedPreUtil.NAME, device.getAddress(), device.getName());// 存储当前连接的蓝牙名称。
//            if (mDeviceListAdapter != null) {
//                mDeviceListAdapter.notifyDataSetChanged();
//            }
//            if (loadingDialog != null) {
//                loadingDialog.dismiss();
//                loadingDialog = null;
//            }
//        }else if(event.getMessage().equals("connect_success")){
//            BluetoothDevice device = (BluetoothDevice) event.getObject();
//            SharedPreUtil.savePre(this, SharedPreUtil.USER, SharedPreUtil.MAC, device.getAddress());// 存储当前连接的蓝牙地址。
//            SharedPreUtil.savePre(this, SharedPreUtil.NAME, device.getAddress(), device.getName());// 存储当前连接的蓝牙名称。
//            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP,false);
//            //link_blename_txt.setText(device.getName());
//            iv_hasdevice.setVisibility(View.VISIBLE);
//            if (mDeviceListAdapter != null) {
//                mDeviceListAdapter.notifyDataSetChanged();
//            }
//            if (loadingDialog != null) {
//                loadingDialog.dismiss();
//                loadingDialog = null;
//            }
//            //Toast.makeText(this, device.getName(), Toast.LENGTH_SHORT).show();
//            finish();
//        }else if(event.getMessage().equals("connect_fail")){
//            if (mDeviceListAdapter != null) {
//                mDeviceListAdapter.notifyDataSetChanged();
//
//            }
//            if (loadingDialog != null) {
//                loadingDialog.dismiss();
//                loadingDialog = null;
//            }
//            //link_blename_txt.setText(getString(R.string.no_ble));
//            Toast.makeText(this,R.string.disconnected,Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private Handler handler = new Handler(new Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            switch (msg.what) {
//                case 1:
//                    if (loadingDialog != null) {
//                        loadingDialog.dismiss();
//                        loadingDialog = null;
//                    }
//
//                    break;
//            }
//            return false;
//        }
//    });
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (loadingDialog != null) {
//                if (System.currentTimeMillis() - MainService.syontime > 5 * 1000) {
//                    Message msg = handler.obtainMessage(1);
//                    handler.sendMessage(msg);
//                }
//            }
//        }
//    };
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.e(TAG, "DeviceScanActivity onCreate");
//        super.onCreate(savedInstanceState);
//        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
//            setTheme(R.style.KCTStyleWhite);
//        } else {
//            setTheme(R.style.KCTStyleBlack);
//        }
//        EventBus.getDefault().register(this);
//        setContentView(R.layout.linkble);
//        initContorl();
//        mHandler = new Handler();
//        mWorkingMode = WearableManager.getInstance().getWorkingMode();
//
//        // Use this check to determine whether BLE is supported on the device.
//        // Then you can selectively disable BLE-related features.
//        // TODO --- 不用 WearableManager.MODE_DOGP  用的 SPP
//        if (android.os.Build.VERSION.SDK_INT >= 18 && mWorkingMode == WearableManager.MODE_DOGP && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show(); // 该手机不支持蓝牙BLE功能
//            finish();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
//                    liLookBlue, R.color.trajectory_bg);
//        }
//        // Initializes a Bluetooth adapter. For API level 18 and above, get a
//        // reference to
//        // BluetoothAdapter through BluetoothManager.
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported on the device.
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        //WearableManager.getInstance().registerWearableListener(mWearableListener);  // MTK 蓝牙连接jar 包
//        init();
//        blutbroadcast = new Bluttoothbroadcast();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MainService.ACTION_BLEDISCONNECTED);
//        intentFilter.addAction(MainService.ACTION_BLECONNECTED);
//        intentFilter.addAction(MainService.ACTION_UNABLECONNECT);
//        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        registerReceiver(blutbroadcast, intentFilter);
//
//        scanDevice(true);
//    }
//
//    private void init() {
//        if (!mBluetoothAdapter.isEnabled()) {
//            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        findViewById(R.id.link_ble).setOnClickListener(new OnClickListener() {  //TODO -- 当前连接 条目点击 --- 进入断开连接页面
//            @Override
//            public void onClick(View v) {
//                if (MainService.getInstance().getState() == 3) {
//                    startActivity(new Intent(LinkBleAcitivity.this, DisconbleActivity.class));
//                }
//            }
//        });
//        mDeviceListAdapter = new DeviceListAdapter();
//        listEquipment.setAdapter(mDeviceListAdapter);
//        Log.d(TAG, "DeviceScanActivity onResume scanDevice(true)");
//        initbangdinview();
//
//    }
//
//    /**
//     * 初始化控件 chendalin add
//     */
//    private void initContorl() {
//        liLookBlue = (LinearLayout) findViewById(R.id.li_look_bluetooth);
//        tvSesarch = (TextView) findViewById(R.id.tv_bluetooth_sesarch);
//        img_bluetooth_sesarch = (ImageView) findViewById(R.id.img_bluetooth_sesarch);
//        //bangdeview = (LinearLayout) findViewById(R.id.bangdeview);
//        //findViewById(R.id.zxing_tv).setOnClickListener(this);
//
//        //iv_hasdevice = (ImageView) findViewById(R.id.iv_hasdevice);
//
//        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate359);
//        aoperatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate359);
//        boperatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate359);
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);
//        aoperatingAnim.setInterpolator(lin);
//        boperatingAnim.setInterpolator(lin);
//        listEquipment = (ListView) findViewById(R.id.lv_bluetooth_sesarch);
//        //link_blename_txt = (TextView) findViewById(R.id.link_blename_txt);
//
//        /*if (MainService.getInstance().getState() == 3) {
//            link_blename_txt.setText(SharedPreUtil.readPre(this, SharedPreUtil.NAME, SharedPreUtil.readPre(this,
//                    SharedPreUtil.USER, SharedPreUtil.MAC)));
//            iv_hasdevice.setVisibility(View.VISIBLE);
//        } else {
//            link_blename_txt.setText(getString(R.string.no_ble));
//            iv_hasdevice.setVisibility(View.GONE);
//            Intent intent = new Intent();
//            intent.setAction(MainService.ACTION_BLEDISCONNECTED);
//            sendBroadcast(intent);
//        }*/
//        // 状态栏与标题栏一体
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
//                    liLookBlue);
//        }
//        img_bluetooth_sesarch.setOnClickListener(mScanningClickListener);
//        tvSesarch.setOnClickListener(mScanningClickListener);
//        listEquipment.setOnItemClickListener(mListViewOnClickListenner);
//
//    }
//
//    private void initblename() {
//        // TODO Auto-generated method stub
//        if (MainService.getInstance().getState() == 3) {
//            link_blename_txt.setText(SharedPreUtil.readPre(this, SharedPreUtil.NAME, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)));
//            iv_hasdevice.setVisibility(View.VISIBLE);
//        } else {
//            link_blename_txt.setText(getString(R.string.no_ble));
//            iv_hasdevice.setVisibility(View.GONE);
//        }
//    }
//
//    private float XPosition = 0;
//    private float YPosition = 0;
//    /**
//     * 点击扫描的按钮
//     */
//    private OnClickListener mScanningClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            /*if (MainService.getInstance().getState() == 2){
//                Toast.makeText(LinkBleAcitivity.this, getString(R.string.bluetooth_connecting), Toast.LENGTH_SHORT).show();
//                return;
//            }*/
//            if(MainService.getInstance().getState() == 3){
//                Toast.makeText(LinkBleAcitivity.this,
//                        getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (isSearch) {
//                isSearch = false;
//                if (!mBluetoothAdapter.isEnabled()) {
//                    Toast.makeText(LinkBleAcitivity.this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                mDeviceListAdapter.clear();
//                scanDevice(true);
//            } else {
//                isSearch = true;
//                tvSesarch.setText(R.string.menu_scan);  // 搜索
//                scanDevice(false);
//            }
//        }
//    };
//
//    private OnItemClickListener mListViewOnClickListenner = new OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                long arg3) {
//            if (MainService.getInstance().getState() == 2) {
//                scanDevice(false);
////                return;
//            }
//            if (MainService.getInstance().getState() == 3) {  // 连接成功时，点击条目，断开蓝牙
//                 if(SharedPreUtil.readPre(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1")) {
//                                byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
//                                MainService.getInstance().writeToDevice(l2, true);
//                  }
//            }
//
//            final BluetoothDevice device = mDeviceListAdapter.getDevice(arg2);
//            final UUID uuid  = mDeviceListAdapter.getUuid(arg2);
//            if (device == null)
//                return;
//            try {
//                ViewHolder holder = (ViewHolder) arg0.getChildAt(arg2).getTag();
//                scanDevice(false);      //todo ---- 停止扫描
//                if(MainService.getInstance().getState() != 3) {
//                    Intent intent = new Intent();
//                    intent.setAction(MainService.ACTION_BLEDISCONNECT);
//                    sendBroadcast(intent);
//
//                    if(loadingDialog != null){
//                        loadingDialog.dismiss();
//                        loadingDialog = null;
//                    }
//                    loadingDialog = new LoadingDialog(LinkBleAcitivity.this);
//                    loadingDialog.show();
//                    SharedPreUtil.setParam(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.UUID, uuid.toString());
//
//                    SharedPreUtil.savePre(LinkBleAcitivity.this, SharedPreUtil.NAME, SharedPreUtil.MAC, device.getName());//存地址
//
//                    if(uuid.equals(BleContants.BLE_YDS_UUID) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING) ){
//                        //MainService.getInstance().connectBluetooth(device.getAddress(), true);  // 蓝牙没有连接时，连接蓝牙
//                        BTNotificationApplication.getMainService().connectDevice(device);
//                    }else{
//                        WearableManager.getInstance().setRemoteDevice(device);
//                        WearableManager.getInstance().connect();
//                        //MainService.getInstance().connectBluetooth(device.getAddress(), false);  // 蓝牙没有连接时，连接蓝牙
//                    }
//                    Log.e(TAG,"address = " + device.getAddress() + "; uuid = " + uuid);
//                }else{
//                    Toast.makeText(LinkBleAcitivity.this,
//                            getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                Toast.makeText(LinkBleAcitivity.this, R.string.connect_error, Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        Log.d(TAG, "DeviceScanActivity onDestroy");
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        try {
//            unregisterReceiver(blutbroadcast);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//
//        WearableManager.getInstance().unregisterWearableListener(mWearableListener);
//        if (MainService.getInstance().getState() < 3) {
//            if(SharedPreUtil.readPre(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1")) {
//                byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
//                MainService.getInstance().writeToDevice(l2, true);
//            }
//        }
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        MobclickAgent.onResume(this);
//        MobclickAgent.onPageStart("LinkBleAcitivity");
//
//        if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") ||
//                SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")){
//            findViewById(R.id.zxing_tv).setVisibility(View.GONE);
//        }
//    }
//
//    private void initbangdinview() {
//        devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
//        if (bangdeview == null) {
//            bangdeview = (LinearLayout) findViewById(R.id.bangdeview);
//        }
//        bangdeview.removeAllViews();
//        int i = 0;
//        if (devices != null && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {  // TODO --- 走SPP
//            for (BluetoothDevice device : devices) {
//                if (device != null) {
//                    if (MainService.dusbingdi != null && device.getAddress().equals(MainService.dusbingdi)) {
//                        MainService.dusbingdi = null;
//                        continue;
//                    }
//                    View view = LayoutInflater.from(this).inflate(R.layout.listblebangde, null);
//                    aViewHolder holder = new aViewHolder();
//
//                    holder.devive_imag = (ImageView) view.findViewById(R.id.devive_imag);
//                    holder.device_name = (TextView) view.findViewById(R.id.device_name);  //
//                    holder.device_mac_name = (TextView) view.findViewById(R.id.device_mac_name);
//
////                    holder.device_name.setText(device.getName() + " " + device.getAddress());
//                    holder.device_name.setText(device.getName());
//                    holder.device_mac_name.setText(device.getAddress());
//                    holder.device_name.setTag(device.getAddress() + "nozuomi" + i++);
//                    holder.device_name.setOnClickListener(this);
//                    view.setTag(holder);
//                    bangdeview.addView(view);
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // User chose not to enable Bluetooth.
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
//            return;
//        }
//        // 扫描二维码/条码回传
//        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {   // 扫描成功
//            if (data != null) {
//                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                // Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
//                Log.e("aa", "解码结果： \n" + content);
//                // qrCoded.setText("解码结果： \n" + content);
//                // qrCodeImage.setImageBitmap(bitmap);
//                LinkBle(content);
//            }
//        }
//
//    }
//
//    private void LinkBle(String scontent) {  // 15:48:51:35:64:65#JM01
//        String name = scontent.substring(scontent.indexOf("#") + 1, scontent.length());
//        String address;
//        try {
//            address = scontent.substring(0, scontent.indexOf("#"));
//            if (!isbleaddress(address)) {
//                address = name.substring(0, name.indexOf("#"));  // bao
//                if (!isbleaddress(address)) {
//                    Toast.makeText(LinkBleAcitivity.this, getString(R.string.codeerror), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                name = name.substring(name.indexOf("#") + 1, name.length());
//            }
//            if (MainService.getInstance().getState() == 3) {  // 连接成功了
//                if (address.equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC))) {
//                    Toast.makeText(LinkBleAcitivity.this, getString(R.string.ble_connected), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // TODO ---  发送断开蓝牙的命令
//                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                if(SharedPreUtil.readPre(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1")) {
//                    byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
//                    MainService.getInstance().writeToDevice(l2, true);
//                }
//                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MainService.offtime = System.currentTimeMillis();
//                MainService.Daring = true;
//                MainService.getInstance().stopChat();
//            }
//            MainService.Daring = true;
//            MainService.getInstance().connectBluetooth(address, true);     // 这里是用的系统API 连接蓝牙
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(LinkBleAcitivity.this,
//                    getString(R.string.codeerror), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (null == loadingDialog) {   // TODO ---- 接着会弹出一个系统通知（配对蓝牙对话框）（MTK jar包中的 ？？？）
//            loadingDialog = new LoadingDialog(LinkBleAcitivity.this, name + getString(R.string.connecting));
//            loadingDialog.show();  // 正在连接的提示框
//            handler.postDelayed(runnable, 1000 * 10);// 打开定时器，执行操作
//        }
//
//    }
//
//    public static boolean isbleaddress(String mobiles) {
//        Pattern p = Pattern.compile("([a-fA-F0-9]{2}:){5}[a-fA-F0-9]{2}");
//        Matcher m = p.matcher(mobiles);
//        return m.matches();
//    }
//
//
//
//    @Override
//    protected void onPause() {
//        Log.d(TAG, "DeviceScanActivity onPause");
//        super.onPause();
//
//        MobclickAgent.onPause(this);
//        MobclickAgent.onPageEnd("LinkBleAcitivity");
//        scanDevice(false);   // TODO --- 离开页面，停止扫描    add 0517（之前注释了）
//    }
//
//
//    private void scanDevice(final boolean enable) {     //TODO --- 使用的mtk
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            mHandler.removeCallbacks(mStopRunnable);
//            mHandler.postDelayed(mStopRunnable, SCAN_PERIOD);
//            mScanning = true;
//            tvSesarch.setVisibility(View.GONE);
//            img_bluetooth_sesarch.setVisibility(View.VISIBLE);
//            if (operatingAnim != null) {
//                img_bluetooth_sesarch.startAnimation(operatingAnim);
//            }
//            //mDeviceListAdapter.addConnectedDevice();
//            //mBluetoothAdapter.startDiscovery();
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//            //WearableManager.getInstance().scanDevice(true);  //TODO --- 使用的mtk
//        } else {
//            mHandler.removeCallbacks(mStopRunnable);
//            mScanning = false;
//            img_bluetooth_sesarch.clearAnimation();
//            tvSesarch.setVisibility(View.VISIBLE);
//            img_bluetooth_sesarch.setVisibility(View.GONE);
//            //mBluetoothAdapter.cancelDiscovery();
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            //WearableManager.getInstance().scanDevice(false);
//        }
//        invalidateOptionsMenu();
//    }
//
//    // Adapter for holding devices found through scanning.
//    private class DeviceListAdapter extends BaseAdapter {
//        private ArrayList<BluetoothDevice> mDevices;
//        private ArrayList<UUID> mDeviceUuid;
//
//        private LayoutInflater mInflator;
//
//        public DeviceListAdapter() {
//            super();
//            mDevices = new ArrayList<BluetoothDevice>();
//            mInflator = LinkBleAcitivity.this.getLayoutInflater();
//            mDeviceUuid = new ArrayList<>();
//        }
//
//        public void addDevice(BluetoothDevice device,UUID uuid) {
//            if (!mDevices.contains(device)) {
//                mDevices.add(device);
//                mDeviceUuid.add(uuid);
//                setListViewHeightBasedOnChildren(listEquipment);
//            }
//        }
//
//        public UUID getUuid(int position){
//            return mDeviceUuid.get(position);
//        }
//
//        public BluetoothDevice getDevice(int position) {
//            return mDevices.get(position);
//        }
//
//        public void clear() {
//            Log.d(TAG, "clear begin");
//            mDevices.clear();
//            mDeviceUuid.clear();
//            mDeviceListAdapter.notifyDataSetChanged();
//            setListViewHeightBasedOnChildren(listEquipment);
//        }
//
//        @Override
//        public int getCount() {
//            return mDevices.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mDevices.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            ViewHolder viewHolder;
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.listitem_ble, null);
//                viewHolder = new ViewHolder();
//                viewHolder.deviceimg = (ImageView) view.findViewById(R.id.devive_imag);
//                viewHolder.deviceMacName = (TextView) view.findViewById(R.id.device_mac_name);
//                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            BluetoothDevice device = i < mDevices.size() ? mDevices.get(i)
//                    : null;
//            if (device != null) {
//                String deviceName = device.getName();
//                String name = queryDeviceName(device.getAddress());
//                if (!TextUtils.isEmpty(name) && !name.equals(deviceName)) {
//                    deviceName = name;
//                }
//                if (deviceName != null && deviceName.length() > 0) {
////                    viewHolder.deviceName.setText(deviceName + "  " + device.getAddress());
//                    viewHolder.deviceName.setText(deviceName);
//                    viewHolder.deviceMacName.setText(device.getAddress());
//                } else {
//                    viewHolder.deviceName.setText(R.string.unknown_device);
//                    viewHolder.deviceMacName.setText(device.getAddress());
//                }
//                viewHolder.deviceimg.setVisibility(View.GONE);
//                viewHolder.deviceimg.clearAnimation();
//            } else {
//                viewHolder.deviceName.setText(R.string.unknown_device);
//
//            }
//
//            return view;
//        }
//    }
//
//    // register WearableListener
//    private WearableListener mWearableListener = new WearableListener() {  // TODO ---- MTK
//
//        @Override
//        public void onDeviceChange(BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onConnectChange(int oldState, int newState) {
//            Log.e(TAG, "onConnectChange old = " + oldState + " new = " + newState);
//            if (newState == WearableManager.STATE_CONNECTED) {
//                EventBus.getDefault().post(new MessageEvent("connect_success",WearableManager.getInstance().getLERemoteDevice()));
//                SharedPreUtil.savePre(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH, 3 + ""); // 保存 MTK 对应的设备类型
//                BluetoothMtkChat.getInstance().sendApkState();  //前台运行
//            }
//            if (oldState == WearableManager.STATE_CONNECTED && newState != WearableManager.STATE_CONNECTED) {     //发送断连广播，发送到我的页面
//                EventBus.getDefault().post(new MessageEvent("connect_fail"));
//                EventBus.getDefault().post(new MessageEvent("autoConnect"));
//            }
//            if (oldState == WearableManager.STATE_CONNECTING && newState != WearableManager.STATE_CONNECTED){
//                EventBus.getDefault().post(new MessageEvent("connect_fail"));
//                EventBus.getDefault().post(new MessageEvent("autoConnect"));
//            }
//        }
//
//        @Override
//        public void onDeviceScan(final BluetoothDevice device) {
//            Log.d(TAG, "onDeviceScan " + device.getName());
//        }
//
//        @Override
//        public void onModeSwitch(int newMode) {
//            Log.d(TAG, "onModeSwitch newMode = " + newMode);
//        }
//    };
//
//    static class ViewHolder {
//        TextView deviceName;
//        TextView deviceMacName;
//        ImageView deviceimg;
//    }
//
//    private String queryDeviceName(String address) {
//        SharedPreferences prefs = LinkBleAcitivity.this.getSharedPreferences("device_name", Context.MODE_PRIVATE);
//        String name = prefs.getString(address, "");
//        Log.d(TAG, "[wearable][queryDeviceName] begin " + address + " " + name);
//        return name;
//    }
//
//    public void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight
//                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.device_name:
//                if (MainService.getInstance().getState() == 2) {
////                    return;
//                }
//                if (MainService.getInstance().getState() == 3) {
//                    if(SharedPreUtil.readPre(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1")) {
//                        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
//                        MainService.getInstance().writeToDevice(l2, true);
//                    }
//                    SharedPreUtil.setParam(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.BLE_CLICK_STOP,true);
//                    return;
//                }
//
//                String newStr = v.getTag().toString().substring(v.getTag().toString().indexOf("nozuomi"),
//                                v.getTag().toString().length());
//                newStr = newStr.substring(7, newStr.length());
//                String address = v.getTag().toString()
//                        .substring(0, v.getTag().toString().indexOf("nozuomi"));
//                scanDevice(false);   //todo ---- 开始连接之前，停止扫描
//                MainService.Daring = true;
//
//                if(MainService.getInstance().getState() != 3) {
//                    MainService.getInstance().connectBluetooth(address, true);      // 点击设备名字时，连接蓝牙
//                }else{
//                    Toast.makeText(LinkBleAcitivity.this,
//                            getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show();
//                }
//
//                for (int s = 0; s < bangdeview.getChildCount(); s++) {
//                    aViewHolder holder = (aViewHolder) bangdeview.getChildAt(s).getTag();
//                    if (Utils.toint(newStr) == s) {
//                        if (aoperatingAnim != null) {
//                            holder.devive_imag.startAnimation(aoperatingAnim);
//                        }
//                        holder.devive_imag.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.devive_imag.setVisibility(View.GONE);
//                    }
//
//                }
//                break;
//            case R.id.zxing_tv:    // 扫一扫 点击
//                isSearch = true;
//                tvSesarch.setText(R.string.menu_scan);
//                scanDevice(false);   // 扫码配对时，关闭扫描方法  //TODO --- 使用的mtk
//                Intent intent = new Intent(LinkBleAcitivity.this, CaptureActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_SCAN);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private class aViewHolder {
//        TextView device_mac_name;
//        TextView device_name;
//        ImageView devive_imag;
//    }
//
//    public class Bluttoothbroadcast extends BroadcastReceiver {  // 通过广播监听设备的连接状态
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            //initblename();
//            if (MainService.ACTION_BLECONNECTED.equals(action)) {   // TODO ---  连接成功
//                initbangdinview();
//                if (mDeviceListAdapter != null) {
//                    mDeviceListAdapter.notifyDataSetChanged();
//                }
//                if (loadingDialog != null) {
//                    loadingDialog.dismiss();
//                    loadingDialog = null;
//                }
//                if(SharedPreUtil.readPre(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") ||            //MTK,手环连接上之后，把扫一扫屏蔽掉
//                        SharedPreUtil.readPre(LinkBleAcitivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")){
//                    findViewById(R.id.zxing_tv).setVisibility(View.GONE);
//                }
//                finish();
//            }
//            if (MainService.ACTION_BLEDISCONNECTED.equals(action)) {    // TODO ---  断开连接
//                if (System.currentTimeMillis() - MainService.offtime > 2000) {
//                    initbangdinview();
//                    if (mDeviceListAdapter != null) {
//                        mDeviceListAdapter.notifyDataSetChanged();
//
//                    }
//                    if (loadingDialog != null) {
//                        loadingDialog.dismiss();
//                        loadingDialog = null;
//                    }
//                }
//            }
//            if (MainService.ACTION_UNABLECONNECT.equals(action)) {
//                initbangdinview();
//                if (mDeviceListAdapter != null) {
//                    mDeviceListAdapter.notifyDataSetChanged();
//                }
//                if (loadingDialog != null) {
//                    loadingDialog.dismiss();
//                    loadingDialog = null;
//                }
//            }
//        }
//    }
//
//
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//                    if (Looper.myLooper() == Looper.getMainLooper()) {
//                        if(null  != device.getName()  && !StringUtils.isEmpty(SharedPreUtil.readPre(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))) {
////                            Log.e(TAG, "deviceName ===== " + device.getName());
////                            Log.e(TAG, "deviceAddress ===== " + device.getAddress());
////                            Log.e(TAG, "spMac ===== " + SharedPreUtil.readPre(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.MAC));
//                            if (device.getName().contains("DfuTarg") && device.getAddress().equals(SharedPreUtil.readPre(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))) {  // device.getName().equals("DfuTarg") &&
//                                mDeviceListAdapter.addDevice(device, BleContants.BLE_YDS_UUID);
//                                mDeviceListAdapter.notifyDataSetChanged();
//                                if(MainService.getInstance().getState() != 2  && MainService.getInstance().getState() != 3){
//                                    MainService.getInstance().connectBluetooth(device.getAddress(), true);  // 蓝牙没有连接时，连接蓝牙
//                                }
//                            }
//                        }
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(null  != device.getName() && !StringUtils.isEmpty(SharedPreUtil.readPre(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))) {
//                                    if (device.getName().contains("DfuTarg") && device.getAddress().equals(SharedPreUtil.readPre(LinkBleAcitivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))) {  // device.getName().equals("DfuTarg") &&
//                                        mDeviceListAdapter.addDevice(device, BleContants.BLE_YDS_UUID);
//                                        mDeviceListAdapter.notifyDataSetChanged();
//                                        if(MainService.getInstance().getState() != 2  && MainService.getInstance().getState() != 3){
//                                            MainService.getInstance().connectBluetooth(device.getAddress(), true);  // 蓝牙没有连接时，连接蓝牙
//                                        }
//                                    }
//                                }
//                            }
//                        });
//                    }
//
//                    List<UUID> uuids = parseFromAdvertisementData(scanRecord);
//                    for (final UUID uuid : uuids) {
//                      //  Log.e(TAG,"uuid = " + uuid);
//                        if(uuid.equals(BleContants.BLE_YDS_UUID) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING) || uuid.equals(BleContants.MTK_YDS_2502_UUID) || uuid.equals(BleContants.MTK_YDS_2503_UUID)){
//                            if (Looper.myLooper() == Looper.getMainLooper()) {
//                                // Android 5.0 及以上
//                                mDeviceListAdapter.addDevice(device,uuid);
//                                mDeviceListAdapter.notifyDataSetChanged();
//                            } else {
//                                // Android 5.0 以下
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mDeviceListAdapter.addDevice(device,uuid);
//                                        mDeviceListAdapter.notifyDataSetChanged();
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
//
//            };
//
//    public static List<UUID> parseFromAdvertisementData(byte[] advertisedData) {
//        List<UUID> uuids = new ArrayList<UUID>();
//
//        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
//        while (buffer.remaining() > 2) {
//            byte length = buffer.get();
//            if (length == 0) break;
//            byte type = buffer.get();
//            switch (type) {
//                case 0x02: // Partial list of 16-bit UUIDs
//                case 0x03: // Complete list of 16-bit UUIDs
//                    while (length >= 2) {
//                        uuids.add(UUID.fromString(String.format(
//                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
//                        length -= 2;
//                    }
//                    break;
//                case 0x06: // Partial list of 128-bit UUIDs
//                case 0x07: // Complete list of 128-bit UUIDs
//                    while (length >= 16) {
//                        long lsb = buffer.getLong();
//                        long msb = buffer.getLong();
//                        uuids.add(new UUID(msb, lsb));
//                        length -= 16;
//                    }
//                    break;
//
//                default:
//                    buffer.position(buffer.position() + length - 1);
//                    break;
//            }
//        }
//        return uuids;
//    }
//}
