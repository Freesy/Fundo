/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.szkct.weloopbtsmartdevice.main;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mediatek.wearable.WearableManager;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.Log;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity mtk
 * source | chendalin modify
 */
public class DisconbleActivity extends AppCompatActivity implements OnClickListener {   // 断开蓝牙页面
    LinearLayout liLookBlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.linkble_discon);
        // chendalin add
        initContorl();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,
                    liLookBlue, R.color.trajectory_bg);
        }
    }

    private void initContorl() {
        // TODO Auto-generated method stub
        findViewById(R.id.discon_ble_txt).setOnClickListener(this);  // 与当前设备断开连接

       /* if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){
            findViewById(R.id.ignores_ble_txt).setVisibility(View.GONE);
        }else {
            findViewById(R.id.ignores_ble_txt).setVisibility(View.VISIBLE);
        }*/

        findViewById(R.id.ignores_ble_txt).setOnClickListener(this); // 忽略该设备
        findViewById(R.id.back).setOnClickListener(this);
        liLookBlue = (LinearLayout) findViewById(R.id.li_look_bluetooth);
    }

    private void unpairDevice() {
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (devices != null && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
            for (BluetoothDevice device : devices) {
                if (device != null) {
                    if (device.getAddress().equals(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC))) {
                        if (SharedPreUtil.readPre(DisconbleActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {
                            byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
                            MainService.getInstance().writeToDevice(l2, true);
                            byte[] l2UnBond = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.KEY_RELIEVE_BLUETOOTH, null);
                            MainService.getInstance().writeToDevice(l2UnBond, true);
                        }
                        try {
                            Method m = device.getClass()
                                    .getMethod("removeBond", (Class[]) null);
                            m.invoke(device, (Object[]) null);
                            MainService.Daring = true;
                            //MainService.getInstance().stopChat();
                            MainService.dusbingdi = device.getAddress();
                            Toast.makeText(DisconbleActivity.this, getString(R.string.unpair_success), Toast.LENGTH_LONG).show();


                        } catch (Exception e) {
                            Log.e("unpairDevice", e.getMessage());
                            Toast.makeText(DisconbleActivity.this, getString(R.string.unpair_fail), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.discon_ble_txt:  // 与当前设备断开连接
                if (SharedPreUtil.readPre(DisconbleActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {
                    byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
                    MainService.getInstance().writeToDevice(l2, true);
                }
                if(SharedPreUtil.readPre(DisconbleActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {     //BLE手动断开
                    SharedPreUtil.setParam(DisconbleActivity.this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);
                    BTNotificationApplication.getMainService().disConnect();
                }
                if(SharedPreUtil.readPre(DisconbleActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {     //BLE手动断开
                    SharedPreUtil.setParam(DisconbleActivity.this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);
                    WearableManager.getInstance().disconnect();
                }
               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainService.Daring = true;
                        MainService.getInstance().stopChat();
                    }
                },500);*/

                break;
            case R.id.ignores_ble_txt:   // 忽略该设备
                unpairDevice();
                break;
            case R.id.back:
                finish();
                break;

            default:
                break;
        }
    }

}
