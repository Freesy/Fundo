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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.view.SycleSearchView;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 * 说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 * mtk source | chendalin modify
 */
public class DeviceScanActivity extends AppCompatActivity {
    private final static String TAG = "AppManager/DeviceScan";

    public static String REMOTE_DEVICE_INFO = "REMOTE_DEVICE_INFO";

    private DeviceListAdapter mDeviceListAdapter;

    private int mWorkingMode;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;

    private Handler mHandler;
    //chendalin add
    private LinearLayout liLookBlue;
    private Toolbar toolbar;
    private SycleSearchView searchView;
    private TextView tvSesarch;
	private LinearLayout llSesarchAnimation;
	private ListView listEquipment;
	private boolean isSearch = true;
	
	
    
    private static final int REQUEST_ENABLE_BT = 1;

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mStopRunnable begin");
            mScanning = false;
            WearableManager.getInstance().scanDevice(false);
        }
    };

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60 * 1000;
    /** chendalin delete
    private final BroadcastReceiver mDeviceScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionString = intent.getAction();
            if (actionString.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int currState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if (currState == BluetoothAdapter.STATE_OFF) {
                    Log.d(TAG, "mDeviceScanReceiver off begin");
                    mHandler.removeCallbacks(mStopRunnable);
                    scanDevice(false);
                    mDeviceListAdapter.clear();
                }
            }
        }
    };
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "DeviceScanActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_bluetooth);
        // chendalin add
        initContorl();
        mHandler = new Handler();
        mWorkingMode = WearableManager.getInstance().getWorkingMode();

        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (android.os.Build.VERSION.SDK_INT >= 18 && mWorkingMode == WearableManager.MODE_DOGP
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        
        
      	
        WearableManager.getInstance().registerWearableListener(mWearableListener);
        /** chendalin delete
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mDeviceScanReceiver, filter);
        */
    }

    /**
     * 初始化控件 chendalin add
     */
    private void initContorl(){
    	Log.e("寻找设备", "run in initContorl------");
    	liLookBlue = (LinearLayout)findViewById(R.id.li_look_bluetooth);
    	tvSesarch = (TextView)findViewById(R.id.tv_bluetooth_sesarch);
    	llSesarchAnimation = (LinearLayout)findViewById(R.id.ll_bluetooth_sesarch_animation);
    	Log.e("寻找设备", "init tvSesarch1111------");
		searchView = (SycleSearchView)findViewById(R.id.ssv_bluetooth_sesarch);
		Log.e("寻找设备", "init tvSesarch222222222------");
		listEquipment = (ListView)findViewById(R.id.lv_bluetooth_sesarch);
    	toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.action_back_normal);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    	//状态栏与标题栏一体
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
  			ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this,liLookBlue);
  		//搜索图标
		searchView.setImgRes(R.drawable.me);
		searchView.setSize(80, 80);
		//tvSesarch.setOnClickListener(mScanningClickListener);				
		//listEquipment.setOnItemClickListener(mListViewOnClickListenner);
    	}
    	
    	tvSesarch.setOnClickListener(mScanningClickListener);
    	listEquipment.setOnItemClickListener(mListViewOnClickListenner);
    	
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
				finish();
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
    
    /**
	 * 点击扫描的按钮
	 */
	private OnClickListener mScanningClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isSearch){
				//Log.e(TAG, "search button111111!!!!");
				isSearch = false;
				tvSesarch.setText(R.string.bluetooth_search_stop);
				llSesarchAnimation.setVisibility(View.VISIBLE);
				searchView.startsycle(); 
				if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(DeviceScanActivity.this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                    finish();
                }
                mDeviceListAdapter.clear();
                scanDevice(true);
			}else{
				//Log.e(TAG, "search button22222!!!!");
				isSearch = true;
				llSesarchAnimation.setVisibility(View.GONE);
				tvSesarch.setText(R.string.bluetooth_search_start);
				searchView.stopsycle();
				scanDevice(false);
			}
		}
	};
	
	private OnItemClickListener mListViewOnClickListenner  =  new  OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			final BluetoothDevice device = mDeviceListAdapter.getDevice(arg2);
	        if (device == null)
	            return;

	        try {
	        	 MainService.Daring=true;
	        	//MainService.getInstance().connectBluetooth(device.getAddress(), true);
	        	
	            finish();
	        } catch (Exception e) {
	            Toast.makeText(DeviceScanActivity.this, R.string.connect_error, Toast.LENGTH_SHORT).show();
	        }
			
		}
	};
	
	
    @Override
    protected void onDestroy() {
        Log.d(TAG, "DeviceScanActivity onDestroy");
        super.onDestroy();
        WearableManager.getInstance().unregisterWearableListener(mWearableListener);
		//chendalin delete
        //unregisterReceiver(mDeviceScanReceiver);
    }

    /** chendalin delete
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }
	*/
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                /** chendalin delete
                 if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
                mDeviceListAdapter.clear();
                scanDevice(true);
                break;
            case R.id.menu_stop:
                scanDevice(false);
                break;*/
            case android.R.id.home:
    			finish();
    			break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
            return;
        }

        // Initializes list view adapter.
        mDeviceListAdapter = new DeviceListAdapter();
        listEquipment.setAdapter(mDeviceListAdapter);
        Log.d(TAG, "DeviceScanActivity onResume scanDevice(true)");
        //scanDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "DeviceScanActivity onPause");
        super.onPause();
        scanDevice(false);
        mDeviceListAdapter.clear();
    }

    /** chendalin delete
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
        if (device == null)
            return;

        try {
            Log.d(TAG, "DeviceScanActivity onListItemClick");
            WearableManager.getInstance().setRemoteDevice(device);
            WearableManager.getInstance().connect();
            WearableManager.getInstance().unregisterWearableListener(mWearableListener);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.connect_error, Toast.LENGTH_SHORT).show();
        }
    }
	*/
    
    private void scanDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.removeCallbacks(mStopRunnable);
            mHandler.postDelayed(mStopRunnable, SCAN_PERIOD);

            mScanning = true;
            mDeviceListAdapter.addConnectedDevice();
            WearableManager.getInstance().scanDevice(true);
        } else {
            mHandler.removeCallbacks(mStopRunnable);
            mScanning = false;
            WearableManager.getInstance().scanDevice(false);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class DeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mDevices;

        private LayoutInflater mInflator;

        public DeviceListAdapter() {
            super();
            mDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mDevices.get(position);
        }

        public void clear() {
            Log.d(TAG, "clear begin");
            mDevices.clear();
            mDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void addConnectedDevice() {
            if (android.os.Build.VERSION.SDK_INT >= 18) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_DOGP) {
                    List<BluetoothDevice> devices = bluetoothManager
                            .getConnectedDevices(BluetoothProfile.GATT);
                    for (BluetoothDevice device : devices) {
                        if (device != null) {
                            if (android.os.Build.VERSION.SDK_INT < 18) {
                                addDevice(device);
                                Log.d(TAG, "addConnectedDevice GATT < 18 " + device.getAddress());
                                mDeviceListAdapter.notifyDataSetChanged();
                            } else if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                                addDevice(device);
                                Log.d(TAG, "addConnectedDevice GATT " + device.getAddress());
                                mDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (devices != null
                    && WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
                for (BluetoothDevice device : devices) {
                    if (device != null) {
                        if (android.os.Build.VERSION.SDK_INT < 18) {
                            mDeviceListAdapter.addDevice(device);
                            Log.d(TAG, "addConnectedDevice BondedDevices " + device.getAddress());
                            mDeviceListAdapter.notifyDataSetChanged();
                        } else if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
                            Log.d(TAG, "addConnectedDevice BondedDevices " + device.getAddress()
                                    + " " + device.getType());
                            mDeviceListAdapter.addDevice(device);
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            Log.d(TAG, "getView");
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = i < mDevices.size() ? mDevices.get(i) : null;
            if (device != null) {
                String deviceName = device.getName();
                String name = queryDeviceName(device.getAddress());
                if (!TextUtils.isEmpty(name) && !name.equals(deviceName)) {
                    deviceName = name;
                }
                if (deviceName != null && deviceName.length() > 0) {
                    viewHolder.deviceName.setText(deviceName);
                } else {
                    viewHolder.deviceName.setText(R.string.unknown_device);
                }
                viewHolder.deviceAddress.setText(device.getAddress());
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceAddress.setText("");
            }

            return view;
        }
    }

    // register WearableListener
    private WearableListener mWearableListener = new WearableListener() {

        @Override
        public void onDeviceChange(BluetoothDevice device) {
        }

        @Override
        public void onConnectChange(int oldState, int newState) {
            Log.d(TAG, "onConnectChange old = " + oldState + " new = " + newState);
            if (oldState != WearableManager.STATE_CONNECTED
                    && newState == WearableManager.STATE_CONNECTED) {
                finish();
            }
        }

        @Override
        public void onDeviceScan(final BluetoothDevice device) {
            Log.d(TAG, "onDeviceScan " + device.getName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDeviceListAdapter.addDevice(device);
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onModeSwitch(int newMode) {
            Log.d(TAG, "onModeSwitch newMode = " + newMode);
        }
    };

    static class ViewHolder {
        TextView deviceName;

        TextView deviceAddress;

        TextView signalStrength;
    }

    private String queryDeviceName(String address) {
        SharedPreferences prefs = DeviceScanActivity.this.getSharedPreferences("device_name",
                Context.MODE_PRIVATE);
        String name = prefs.getString(address, "");
        Log.d(TAG, "[wearable][queryDeviceName] begin " + address + " " + name);
        return name;
    }
}
