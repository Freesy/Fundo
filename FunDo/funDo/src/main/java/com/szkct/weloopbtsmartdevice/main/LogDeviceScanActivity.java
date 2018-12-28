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
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;

public class LogDeviceScanActivity extends ListActivity {
    private final static String TAG = "AppManager/LogCatcher";

    private DeviceListAdapter mDeviceListAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;

    private Handler mHandler;

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mStopRunnable begin");
            mScanning = false;
            mBluetoothAdapter.cancelDiscovery();
            invalidateOptionsMenu();
        }
    };

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60 * 1000;

    private final BroadcastReceiver mDeviceScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionString = intent.getAction();
            if (actionString.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int currState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if (currState == BluetoothAdapter.STATE_OFF) {
                    /*Log.d(TAG, "[mDeviceScanReceiver] off begin");
                    mHandler.removeCallbacks(mStopRunnable);
                    scanDevice(false);
                    mDeviceListAdapter.clear();*/
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(actionString)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (android.os.Build.VERSION.SDK_INT < 18) {
                        Log.d(TAG, "[mDeviceScanReceiver] android.os.Build.VERSION.SDK_INT < 18 = "
                                + device.getName());
                        notifyList(device);
                        return;
                    } else {
                        Log.d(TAG,
                                "[mDeviceScanReceiver] found BluetootDevice = "
                                        + device.getAddress() + " " + device.getName() + " type = "
                                        + device.getType());
                        if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
                            notifyList(device);
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(actionString)) {
                Log.d(TAG, "[mDeviceScanReceiver] found finish");
            }
        }
    };

    private void notifyList(final BluetoothDevice device) {
        Log.d(TAG, "notifyList " + device.getAddress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceListAdapter.addDevice(device);
                mDeviceListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

        // Initializes a Bluetooth adapter. For API level 18 and above, get a reference to
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

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mDeviceScanReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        unregisterReceiver(mDeviceScanReceiver);
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
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
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
            return;
        }

        // Initializes list view adapter.
        mDeviceListAdapter = new DeviceListAdapter();
        setListAdapter(mDeviceListAdapter);
        Log.d(TAG, "onResume scanDevice(true)");
        scanDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        scanDevice(false);
        mDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
        if (device == null)
            return;

        try {
            Log.d(TAG, "DeviceScanActivity onListItemClick");
            Intent data = new Intent();
            data.putExtra("LOG_DEVICE", device.getAddress());
            setResult(RESULT_OK, data);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.connect_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void scanDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.removeCallbacks(mStopRunnable);
            mHandler.postDelayed(mStopRunnable, SCAN_PERIOD);

            mScanning = true;
            mDeviceListAdapter.addConnectedDevice();
            mBluetoothAdapter.startDiscovery();
        } else {
            mHandler.removeCallbacks(mStopRunnable);
            mScanning = false;
            mBluetoothAdapter.cancelDiscovery();
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
            mInflator = LogDeviceScanActivity.this.getLayoutInflater();
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
            Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (devices != null) {
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

    static class ViewHolder {
        TextView deviceName;

        TextView deviceAddress;

        TextView signalStrength;
    }

    private String queryDeviceName(String address) {
        SharedPreferences prefs = LogDeviceScanActivity.this.getSharedPreferences("device_name",
                Context.MODE_PRIVATE);
        String name = prefs.getString(address, "");
        Log.d(TAG, "[wearable][queryDeviceName] begin " + address + " " + name);
        return name;
    }
}
