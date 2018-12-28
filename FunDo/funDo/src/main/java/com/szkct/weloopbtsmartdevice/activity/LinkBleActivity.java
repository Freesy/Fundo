package com.szkct.weloopbtsmartdevice.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.mediatek.wearable.WearableManager;
import com.szkct.adapter.LinkBleAdapter;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.UtilsLX;
import com.szkct.weloopbtsmartdevice.data.LinkBleData;
import com.szkct.weloopbtsmartdevice.data.advertisingBean;
import com.szkct.weloopbtsmartdevice.login.WxGuildeActivity;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DividerItemDecoration;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.CustomProgress;
import com.thefinestartist.finestwebview.FinestWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static com.szkct.weloopbtsmartdevice.main.MainService.CONNECT_FAIL;
import static com.szkct.weloopbtsmartdevice.main.MainService.CONNECT_SUCCESS;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class LinkBleActivity extends AppCompatActivity{

    public static final String TAG = LinkBleActivity.class.getSimpleName();
    public Context mContext;
    private int mWorkingMode;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView tvSesarch;
    private ImageView img_bluetooth_sesarch, iv_hasdevice;
    ;
    private Animation operatingAnim, aoperatingAnim, boperatingAnim;
    private RecyclerView recyclerView;
    public LinkBleAdapter deviceAdapter;
    private List<LinkBleData> deviceList;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 60 * 1000;
    private boolean mScanning = false;
    private boolean isSearch = false;
   // private LoadingDialog loadingDialog;
    public Dialog dialog;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isConnect = false;
    private BondBroadcastReceiver bondBroadcastReceiver;
    private UUID mUUID;
    private boolean isFirstConnect;
    private  SharedPreferences preferences;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    private HTTPController hc;
    public final int AD = 10086, CHECK = 10;
    public String murl = "https://mp.weixin.qq.com/s/KgcZYWvDIYa6N0rA-NsJQQ";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if(event.getMessage().equals(CONNECT_SUCCESS)){
            if (null != dialog&&dialog.isShowing()) {
                dialog.dismiss();
            }

            scanDevice(false,true);

            isConnect = false;

            ///////////////////////////////////////////////////////////////////////////////////////////
            if(null != mHandler){
                mHandler.removeCallbacks(dialogRunnable);    // dialogRunnable
            }
            //////////////////////////////////////////////////////////////////////////////////////

            preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
            isFirstConnect = preferences.getBoolean("isFirstConnect", true);
            if (isFirstConnect){
                String url = Constants.CHECK_Advertising + "1";
                hc = HTTPController.getInstance();
                hc.open(LinkBleActivity.this);
                hc.getNetworkStringData(url, mmHandler, AD);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isFirstConnect", false); //保存是否第一次连接设备
                editor.commit();
            }
            finish();
        }else if(event.getMessage().equals(CONNECT_FAIL)){
            if (null != dialog&&dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(this,R.string.disconnected,Toast.LENGTH_SHORT).show();
            isConnect = false;

            ///////////////////////////////////////////////////////////////////////////////////////////
            if(null != mHandler){
                mHandler.removeCallbacks(dialogRunnable);
            }
            //////////////////////////////////////////////////////////////////////////////////////
        }
    }
    private Handler mmHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {

                case AD:  // 是否弹出广告
                    if (!StringUtils.isEmpty(msg.obj.toString())){
                        Log.e("msg:", ""+msg.obj.toString());
                        if (!("-1").equals(msg.obj.toString())){
                            advertisingBean advertisingBean = new Gson().fromJson(msg.obj.toString(), advertisingBean.class);
                            Log.e("advertisingBean:", ""+advertisingBean.getData().getGuideUrl());
                            String urls = advertisingBean.getData().getGuideUrl();
                            if (!StringUtils.isEmpty(urls)){
                                new FinestWebView.Builder(LinkBleActivity.this).showIconMenu(false).show(urls);
                            }else{
                                new FinestWebView.Builder(LinkBleActivity.this).showIconMenu(false).show(murl);
                            }
                            finish();
                        }else{
                            finish();
                        }

                    }else{
                        finish();
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        EventBus.getDefault().register(this);
        setContentView(R.layout.linkble);
        dialog= CustomProgress.show(LinkBleActivity.this  , getString(R.string.bluetooth_connecting), null);
        mContext = this;
        mHandler = new Handler();
        mWorkingMode = WearableManager.getInstance().getWorkingMode();

        if (android.os.Build.VERSION.SDK_INT >= 18 && mWorkingMode == WearableManager.MODE_DOGP
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show(); // 该手机不支持蓝牙BLE功能
            finish();
        }
        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvSesarch = (TextView) findViewById(R.id.tv_bluetooth_sesarch);
        img_bluetooth_sesarch = (ImageView) findViewById(R.id.img_bluetooth_sesarch);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate359);
        aoperatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate359);
        boperatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate359);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        aoperatingAnim.setInterpolator(lin);
        boperatingAnim.setInterpolator(lin);
        recyclerView = (RecyclerView) findViewById(R.id.lv_bluetooth_sesarch);


        deviceList = new ArrayList<>();
        deviceAdapter = new LinkBleAdapter(this,deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(deviceAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                mContext, DividerItemDecoration.VERTICAL_LIST,15));

        img_bluetooth_sesarch.setOnClickListener(mScanningClickListener);
        tvSesarch.setOnClickListener(mScanningClickListener);

        deviceAdapter.setOnItemClickListener(new LinkBleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, LinkBleData linkBleData) {
                BluetoothDevice device = linkBleData.getBluetoothDevice();
                UUID uuid = linkBleData.getUuid();
                if (null ==  device || null  == uuid) return;
                if (isConnect){
                    Toast.makeText(mContext,getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(MainService.getInstance().getState() != 3) {

                    if (null != dialog&&!dialog.isShowing()) {
                        dialog.setCancelable(false);
                        dialog.show();
 						mHandler.postDelayed(dialogRunnable,20 * 1000);
                    }
                   
                    scanDevice(false,true);
                    SharedPreUtil.setParam(mContext, SharedPreUtil.USER, SharedPreUtil.UUID, uuid.toString());

                    SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.MACNAME, device.getName());//存地址

                    if(uuid.equals(BleContants.BLE_YDS_UUID) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING) ||
                            uuid.equals(BleContants.RX_SERVICE_872_UUID)){
                        if(device.getBondState() != BluetoothDevice.BOND_BONDED  && uuid.equals(BleContants.RX_SERVICE_872_UUID)) {
                            mUUID = BleContants.RX_CHAR_872_UUID;
                            device.createBond();
                        }else{
                            BTNotificationApplication.getMainService().connectDevice(device);
                        }
                    }else{
                        WearableManager.getInstance().setRemoteDevice(device);
                        WearableManager.getInstance().connect();
                    }
                    Log.e(TAG,"address = " + device.getAddress() + "; uuid = " + uuid);
                    isConnect = true;
                }else{
                    Toast.makeText(mContext,
                            getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bondBroadcastReceiver = new BondBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondBroadcastReceiver,intentFilter);
        scanDevice(true,false);
    }

    /**
     * 点击扫描的按钮
     */
    private View.OnClickListener mScanningClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(BTNotificationApplication.getMainService().getState() == 3){
                Toast.makeText(mContext,
                        getString(R.string.connectSuccess), Toast.LENGTH_SHORT).show();
                return;
            }
            if (isSearch) {
                isSearch = false;
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(mContext, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                    finish();
                }
                deviceList.clear();
                deviceAdapter.notifyDataSetChanged();
                scanDevice(true,false);
                if(null != timer){
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
                if(null != timerTask){
                    timerTask.cancel();
                    timerTask = null;
                }
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,R.string.cannot_scanble,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                };
                timer.schedule(timerTask,1000);
            } else {
                isSearch = true;
                tvSesarch.setText(R.string.menu_scan);  // 搜索
                scanDevice(false,false);
            }
        }
    };




    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        deviceList.clear();
        scanDevice(false,true);
        isConnect = false;
        if(bondBroadcastReceiver != null){
            unregisterReceiver(bondBroadcastReceiver);
        }
        super.onDestroy();
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mStopRunnable begin");
           scanDevice(false,true);
        }
    };

    private Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != dialog&&dialog.isShowing()) {
                if(isValidContext(LinkBleActivity.this)) {
                    dialog.dismiss();
                }

                Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                isConnect = false;
            }
        }
    };

    private boolean isValidContext (Context c){

        Activity a = (Activity)c;

        if (a.isDestroyed() || a.isFinishing()){
            Log.i(TAG, "Activity is Destroyed or isFinishing-->");
            return false;
        }else{
            return true;
        }
    }

    private void scanDevice(final boolean enable,boolean isDestroy) {     //TODO --- 使用的mtk
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.removeCallbacks(mStopRunnable);
            mHandler.postDelayed(mStopRunnable, SCAN_PERIOD);
            mScanning = true;
            tvSesarch.setVisibility(View.GONE);
            img_bluetooth_sesarch.setVisibility(View.VISIBLE);
            if (null  != operatingAnim) {
                img_bluetooth_sesarch.startAnimation(operatingAnim);
            }

            if(MainService.getInstance().getState() != 3){  //todo --- 未连接成功
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }

//            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mHandler.removeCallbacks(mStopRunnable);
            mScanning = false;
            img_bluetooth_sesarch.clearAnimation();
            tvSesarch.setVisibility(View.VISIBLE);
            img_bluetooth_sesarch.setVisibility(View.GONE);

            if(isDestroy) {    //当退出界面取消搜索
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            /*if(MainService.getInstance().getState() == 3){  //todo --- 连接成功
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }*/
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    //Log.i(TAG, "address = " + device.getAddress());
                    if (mScanning) {       //当取消搜索就不再加入设备
                        if (null != timer) {
                            timer.cancel();
                            timer.purge();
                            timer = null;
                        }
                        if (null != timerTask) {
                            timerTask.cancel();
                            timerTask = null;
                        }
                        boolean deviceFound = false;
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            //判断是否已有相同设备deviceFound
                            for (int i = 0; i < deviceList.size(); i++) {
                                if (deviceList.get(i).getBluetoothDevice().getAddress().equals(device.getAddress())) {
                                    deviceFound = true;
                                    break;
                                }
                            }
                            if (deviceFound) {
                                return;
                            }
                            if (!StringUtils.isEmpty(device.getName())  && !StringUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC))) {
                                if (!StringUtils.isEmpty(device.getName()) && device.getName().contains("DfuTarg") && device.getAddress().equals(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC))) {  // device.getName().equals("DfuTarg") &&
                                    LinkBleData linkBleData = new LinkBleData(device, BleContants.BLE_YDS_UUID,device.getName());
                                    deviceList.add(linkBleData);
                                    deviceAdapter.notifyDataSetChanged();
                                    if (MainService.getInstance().getState() != 2 && MainService.getInstance().getState() != 3) {
                                        //MainService.getInstance().connectBluetooth(device.getAddress(), true);  // 蓝牙没有连接时，连接蓝牙
                                        BTNotificationApplication.getMainService().connectDevice(device);
                                    }
                                }
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean isFound = false;
                                    //判断是否已有相同设备deviceFound
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        if (deviceList.get(i).getBluetoothDevice().getAddress().equals(device.getAddress())) {
                                            isFound = true;
                                            break;
                                        }
                                    }
                                    if (isFound) {
                                        return;
                                    }
                                    if (!StringUtils.isEmpty(device.getName()) && !StringUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC))) {
                                        if (!StringUtils.isEmpty(device.getName()) && device.getName().contains("DfuTarg") && device.getAddress().equals(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC))) {  // device.getName().equals("DfuTarg") &&
                                            LinkBleData linkBleData = new LinkBleData(device, BleContants.BLE_YDS_UUID,device.getName());
                                            deviceList.add(linkBleData);
                                            deviceAdapter.notifyDataSetChanged();
                                            if (MainService.getInstance().getState() != 2 && MainService.getInstance().getState() != 3) {
                                                //MainService.getInstance().connectBluetooth(device.getAddress(), true);  // 蓝牙没有连接时，连接蓝牙
                                                BTNotificationApplication.getMainService().connectDevice(device);
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        List<UUID> uuids = parseFromAdvertisementData(scanRecord);
                        if(uuids == null || uuids.size() <= 0){
                            return;
                        }
                        for (final UUID uuid : uuids) {
                            if (uuid.equals(BleContants.BLE_YDS_UUID) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING)
                                    || uuid.equals(BleContants.MTK_YDS_2502_UUID) || uuid.equals(BleContants.MTK_YDS_2503_UUID) || uuid.equals(BleContants.MTK_YDS_3802_UUID)) {
                                //判断是否已有相同设备deviceFound
                                for (int i = 0; i < deviceList.size(); i++) {
                                    if (deviceList.get(i).getBluetoothDevice().getAddress().equals(device.getAddress())) {
                                        deviceFound = true;
                                        break;
                                    }
                                }
                                if (deviceFound) {
                                    return;
                                }
                                if (Looper.myLooper() == Looper.getMainLooper()) {
                                    // Android 5.0 及以上
                                    LinkBleData linkBleData = new LinkBleData(device, uuid,device.getName());
                                    deviceList.add(linkBleData);
                                    deviceAdapter.notifyDataSetChanged();
                                } else {
                                    // Android 5.0 以下
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LinkBleData linkBleData = new LinkBleData(device, uuid,device.getName());
                                            deviceList.add(linkBleData);
                                            deviceAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                                break;
                            }else if(uuid.equals(BleContants.RX_SERVICE_872_UUID)){
                                try {
                                    String scanRecords = UtilsLX.bytesToHexString(scanRecord);
                                    //02011A 05 09 68756E34 0EFF0B00 3650460335F9 0468756E341107B75C49D204A34071A0B535853EB083070000000000000000000000000000000000000000
                                    int deviceNameLength = scanRecord[3] & 0xff;
                                    String deviceName = "";
                                    try {
                                        deviceName = new String(UtilsLX.hexStringToBytes(scanRecords.substring(10, 10 + (deviceNameLength * 2))), "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    int deviceAddressLength = (8 + deviceNameLength) * 2; //13
                                    String address = scanRecords.substring(deviceAddressLength, deviceAddressLength + 2)
                                            + ":" + scanRecords.substring(deviceAddressLength + 2, deviceAddressLength + 4)
                                            + ":" + scanRecords.substring(deviceAddressLength + 4, deviceAddressLength + 6)
                                            + ":" + scanRecords.substring(deviceAddressLength + 6, deviceAddressLength + 8)
                                            + ":" + scanRecords.substring(deviceAddressLength + 8, deviceAddressLength + 10)
                                            + ":" + scanRecords.substring(deviceAddressLength + 10, deviceAddressLength + 12);
                                    final BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(address);

                                    //判断是否已有相同设备deviceFound
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        if (deviceList.get(i).getBluetoothDevice().getAddress().equals(device1.getAddress())) {
                                            deviceFound = true;
                                            break;
                                        }
                                    }
                                    if (deviceFound) {
                                        return;
                                    }
                                    if (Looper.myLooper() == Looper.getMainLooper()) {
                                        // Android 5.0 及以上
                                        LinkBleData linkBleData = new LinkBleData(device1, uuid, deviceName);
                                        deviceList.add(linkBleData);
                                        deviceAdapter.notifyDataSetChanged();
                                    } else {
                                        final String name = deviceName;
                                        // Android 5.0 以下
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                LinkBleData linkBleData = new LinkBleData(device1, uuid, name);
                                                deviceList.add(linkBleData);
                                                deviceAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
            };

    public static List<UUID> parseFromAdvertisementData(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        try {
            ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
            while (buffer.remaining() > 2) {
                byte length = buffer.get();
                if (length == 0) break;
                byte type = buffer.get();
                switch (type) {
                    case 0x02: // Partial list of 16-bit UUIDs
                    case 0x03: // Complete list of 16-bit UUIDs
                        while (length >= 2) {
                            uuids.add(UUID.fromString(String.format(Locale.ENGLISH,
                                    "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                            length -= 2;
                        }
                        break;
                    case 0x06: // Partial list of 128-bit UUIDs
                    case 0x07: // Complete list of 128-bit UUIDs
                        while (length >= 16) {
                            long lsb = buffer.getLong();
                            long msb = buffer.getLong();
                            uuids.add(new UUID(msb, lsb));
                            length -= 16;
                        }
                        break;

                    default:
                        buffer.position(buffer.position() + length - 1);
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return uuids;
    }

    private class BondBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null && action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mUUID != null) {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED && mUUID.equals(BleContants.RX_SERVICE_872_UUID)) {
                        BTNotificationApplication.getMainService().connectDevice(device);
                    } else if (device.getBondState() == BOND_NONE && mUUID.equals(BleContants.RX_SERVICE_872_UUID)) {
                        EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
                    }
                }
            }
        }
    }
}
