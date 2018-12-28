package com.kct.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;

import com.kct.bluetooth.bean.BluetoothLeDevice;
import com.kct.bluetooth.bean.KCTBLE_L1Bean;
import com.kct.bluetooth.bean.KCTGattAttributes;
import com.kct.bluetooth.callback.IBluetoothGattCallback;
import com.kct.bluetooth.callback.IConnectListener;
import com.kct.bluetooth.callback.IDFUProgressCallback;
import com.kct.bluetooth.callback.IDialogCallback;
import com.kct.bluetooth.callback.IScanCallback;
import com.kct.bluetooth.kctmanager.DFUProgressListener;
import com.kct.bluetooth.kctmanager.KCTConnectRunnable;
import com.kct.bluetooth.kctmanager.KCTSendDataRunnable;
import com.kct.bluetooth.utils.HTTPUtil;
import com.kct.bluetooth.utils.HexUtil;
import com.kct.bluetooth.utils.LogUtil;
import com.kct.bluetooth.utils.ThreadPoolProxy;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_CHAR_872_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_CHAR_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_SERVICE_872_UUID;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_SERVICE_872_UUID_SCAN;
import static com.kct.bluetooth.bean.KCTGattAttributes.RX_SERVICE_UUID;

/**
 * 版权：深圳金康特智能科技有限公司 作者：ZGH 版本： 创建日期：2017/10/10 描述: ${VERSION} 修订历史：
 */

public class KCTBluetoothHelper implements IBluetoothGattCallback {

	private final static String TAG = "[LogHelper]";
	private static final String LIB_VERSION = "1.2.2"; //SDK版本

	private Context context;
	private BluetoothManager mBluetoothManager;        //蓝牙管理
	private BluetoothAdapter mBluetoothAdapter;        //蓝牙适配器
	private BluetoothDevice mConnectDevice;            //连接设备
	private String mConnectAddress;                    //连接蓝牙地址
	private BluetoothGatt mBluetoothGatt;              //蓝牙gatt
	private KCTBluetoothGattCallback mGattCallback;    //蓝牙回调
	private int state;                                 //连接状态 0：未连接；2：连接中；3：已连接；4：连接断开

	private KCTSendCommand kctSendCommand;             //发送线程
	private KCTReceiveCommand kctReceiveCommand;       //接收线程
	private ArrayList<IConnectListener> iConnectListeners;   //状态回调
	private BluetoothGattCharacteristic RxChar;        //特征值
	private int mtuSize = 20;                          //mtu传输大小
	private BluetoothGattService RxService;            //GattService
	private UUID serviceUuid;                          //蓝牙UUID

	private boolean isSendFile = false;

	private Queue<byte[]> nextByte = new LinkedList<byte[]>();

	private ThreadPoolProxy threadPoolProxy = new ThreadPoolProxy(8,64,10000);

	private IDialogCallback iDialogCallback;
	private IScanCallback iScanCallback;

	private KCTSendDataRunnable mKctSendDataRunnable;
	private KCTConnectRunnable mKCTConnectRunnable;
	private DFUProgressListener mDFUProgressListener;
	private KCTBroadcastReceive mKCTBroadcastReceive;
	private IntentFilter intentFilter;

	public KCTBluetoothHelper() {
	}

	public KCTBluetoothHelper(Context context) {
		this.context = context;
		Log.d(TAG,"KCT Bluetooth version is " + LIB_VERSION);
		initialize();
	}


	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (null != iConnectListeners && iConnectListeners.size() > 0) {
					for (int i = 0; i < iConnectListeners.size(); i++) {
						if (null != iConnectListeners.get(i)) {
							iConnectListeners.get(i).onCommand_d2a((byte[]) msg.obj);
						}
					}
				}
				break;
			case 2:
				byte[] bytes = (byte[]) msg.obj;
				writeToDevice(bytes);
				break;
			case 3:
				if(kctReceiveCommand != null) {
					kctReceiveCommand.dataClear();
				}
				break;
			case 4:
				if (null != mKctSendDataRunnable){
					handler.removeCallbacks(mKctSendDataRunnable);
				}
				break;
			case 5:
				if(null != mKctSendDataRunnable){
					if(RxService.getUuid().equals(RX_SERVICE_872_UUID)){
						handler.postDelayed(mKctSendDataRunnable,6000);
					}else {
						handler.postDelayed(mKctSendDataRunnable,5000);
					}
				}
				LogUtil.d(TAG,"timer is start");
				break;
			}
		}
	};


	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	private boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) context
					.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * 连接蓝牙
	 * @param address
	 * @param listeners
	 * @return
	 */
	public boolean connect(String address, ArrayList<IConnectListener> listeners) {
		if (!mBluetoothAdapter.isEnabled()) {
			Log.d(TAG, "BluetoothAdapter is not enable");
			return false;
		}

		if (mBluetoothAdapter == null || address == null) {
			Log.d(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		if (!BluetoothAdapter.checkBluetoothAddress(address)) {
			Log.d(TAG, "invalid BT address");
			return false;
		}

		if (state == KCTBluetoothManager.STATE_CONNECTING){
			Log.d(TAG, "Bluetooth is connecting");
			return false;
		}
		final BluetoothDevice remoteDevice = mBluetoothAdapter
				.getRemoteDevice(address.toUpperCase().trim());

		if (mConnectAddress != null && address.equals(mConnectAddress) && mBluetoothGatt != null) {
			Log.d(TAG,"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				if(mKCTConnectRunnable == null) {
					mKCTConnectRunnable = new KCTConnectRunnable(this);
				}
				handler.postDelayed(mKCTConnectRunnable,20000);
				LogUtil.d(TAG,"connect time start");
				mConnectAddress = address;
				mConnectDevice = remoteDevice;
				state = KCTBluetoothManager.STATE_CONNECTING;
				for (int i = 0; i < iConnectListeners.size(); i++) {
					iConnectListeners.get(i).onConnectState(state);
				}
				return true;
			} else {
				return false;
			}
		}
		if (null == remoteDevice) {
			Log.d(TAG, "Device not found.  Unable to connect.");
			return false;
		}

		if (null == mGattCallback) {
			Log.d(TAG, "BluetoothGattCallback is null");
			mGattCallback = new KCTBluetoothGattCallback(this, KCTBluetoothHelper.this);
		}

		this.iConnectListeners = listeners;
		handler.post(new Runnable() {
			@Override
			public void run() {
				mBluetoothGatt = remoteDevice.connectGatt(context, false, mGattCallback);
			}
		});

		if(mKCTConnectRunnable == null) {
			mKCTConnectRunnable = new KCTConnectRunnable(this);
		}
		handler.postDelayed(mKCTConnectRunnable,20000);
		LogUtil.d(TAG,"connect time start");
		mConnectAddress = address;
		mConnectDevice = remoteDevice;
		state = KCTBluetoothManager.STATE_CONNECTING;
		setConnectState(KCTBluetoothManager.STATE_CONNECTING);
		return true;
	}

	private BluetoothGattServerCallback serverCallback = new BluetoothGattServerCallback() {
		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
			super.onConnectionStateChange(device, status, newState);
			Log.e(TAG,"onConnectionStateChange");
		}

		@Override
		public void onServiceAdded(int status, BluetoothGattService service) {
			super.onServiceAdded(status, service);
			Log.e(TAG,"onServiceAdded");
		}
	};


	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public synchronized void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.d(TAG, "BluetoothAdapter not initialized");
			return;
		}
		if(mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		}
		if (null != kctReceiveCommand) {
			kctReceiveCommand.cancel();
			kctReceiveCommand = null;
		}
		if (null != kctSendCommand) {
			kctSendCommand.cancel();
			kctSendCommand = null;
		}

		if (mKctSendDataRunnable != null){
			handler.removeCallbacks(mKctSendDataRunnable);
			mKctSendDataRunnable = null;
		}

		if(mKCTConnectRunnable != null) {
			handler.removeCallbacks(mKCTConnectRunnable);
			mKCTConnectRunnable = null;
		}
		if(mGattCallback != null) {
			mGattCallback = null;
		}

		if(state != KCTBluetoothManager.STATE_CONNECT_FAIL) {
			setConnectState(KCTBluetoothManager.STATE_CONNECT_FAIL);
		}
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			Log.d(TAG,"mBluetoothGatt is null");
			return;
		}
		refreshDeviceCache(mBluetoothGatt);
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * 清理本地的BluetoothGatt 的缓存，以保证在蓝牙连接设备的时候，设备的服务、特征是最新的
	 * 
	 * @param gatt
	 * @return
	 */
	public boolean refreshDeviceCache(BluetoothGatt gatt) {
		if (null != gatt) {
			try {
				BluetoothGatt localBluetoothGatt = gatt;
				Method localMethod = localBluetoothGatt.getClass().getMethod(
						"refresh", new Class[0]);
				if (localMethod != null) {
					boolean bool = ((Boolean) localMethod.invoke(
							localBluetoothGatt, new Object[0])).booleanValue();
					return bool;
				}
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
		return false;
	}

	public synchronized void write(byte[] bytes) {
		if (state == KCTBluetoothManager.STATE_CONNECTED) {
			if (isSendFile) {
				writeToDevice(bytes);
			} else {
				if (null != kctSendCommand) {
					kctSendCommand.addCommand(bytes);
				} else {
					Log.d(TAG, "KCTSend is null");
				}
			}
		} else {
			Log.d(TAG, "bluetooth is not connected");
		}
	}


	/**
	 * 蓝牙发送数据
	 */
	public void writeToDevice(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return;
		}
		if (mBluetoothGatt != null) {
			BluetoothGattService RxService = null;
			List<BluetoothGattService> services = mBluetoothGatt.getServices();
			if(services != null && services.size() > 0) {
				for (int i = 0; i < services.size(); i++) {
					if(services.get(i).getUuid() != null){
						if(services.get(i).getUuid().toString().equals(RX_SERVICE_UUID.toString())
								|| services.get(i).getUuid().toString().equals(RX_SERVICE_872_UUID.toString())
								|| services.get(i).getUuid().toString().equals(RX_SERVICE_872_UUID_SCAN.toString())){
							RxService = mBluetoothGatt.getService(services.get(i).getUuid());
							break;
						}
					}
				}
			}
			if (null == RxService) {
				Log.d(TAG, "service is null");
				disconnect();
				return;
			}
			List<BluetoothGattCharacteristic> characteristics = RxService.getCharacteristics();
			if(characteristics != null && characteristics.size() > 0){
				for (int i = 0; i < characteristics.size(); i++) {
					if(characteristics.get(i).getUuid() != null){
						Log.d(TAG,"characteristics = " + characteristics.get(i).getUuid());
						if(characteristics.get(i).getUuid().toString().equals(RX_CHAR_UUID.toString())
								|| characteristics.get(i).getUuid().toString().equals(RX_CHAR_872_UUID.toString())){
							RxChar = RxService.getCharacteristic(characteristics.get(i).getUuid());
							break;
						}
					}
				}
			}
			if (null == RxChar) {
				Log.d(TAG, "Characteristic is null");
				disconnect();
				return;
			}
		}

		int length = bytes.length;
		if (length <= mtuSize) { // 每次最多写入20字节
			if (RxChar == null) {
				return;
			}
			RxChar.setValue(bytes);
			RxChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			if(mBluetoothGatt != null) {
				boolean flag = mBluetoothGatt.writeCharacteristic(RxChar);
				if (!flag) {
					Log.d(TAG,"write not characteristic");
					if(mKctSendDataRunnable != null) {
						handler.removeCallbacks(mKctSendDataRunnable);
					}
					if(kctSendCommand != null) {
						kctSendCommand.reCancel(false);
					}
				}
			}
		} else {
			int count = length / mtuSize;
			int remainder = length % mtuSize;
			for (int i = 0; i < count; ++i) {
				byte[] subCmd = new byte[mtuSize];
				System.arraycopy(bytes, i * mtuSize, subCmd, 0, mtuSize);
				nextByte.offer(subCmd);
			}
			if (remainder != 0) {
				byte[] remainCmd = new byte[remainder];
				System.arraycopy(bytes, count * mtuSize, remainCmd, 0, remainder);
				nextByte.offer(remainCmd);
			}
			RxChar.setValue(nextByte.peek());
			RxChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			if(mBluetoothGatt != null) {
				boolean flag = mBluetoothGatt.writeCharacteristic(RxChar);
				if (!flag) {
					Log.d(TAG,"write not characteristic");
					if(mKctSendDataRunnable != null) {
						handler.removeCallbacks(mKctSendDataRunnable);
					}
					if (kctSendCommand != null) {
						kctSendCommand.reCancel(false);
					}
				}
			}
		}
	}

	/**
	 * 获取蓝牙连接状态值
	 */
	public int getConnectState() {
		return state;
	}

	/**
	 * 获取蓝牙连接设备
	 */
	public BluetoothDevice getConnectDevice() {
		return mConnectDevice;
	}

	/**
	 * 搜索设备
	 */
	public void scanDevice(boolean scan) {
		if(iScanCallback == null){
			iScanCallback = new IScanCallback(iConnectListeners,this,mBluetoothAdapter);
		}
		if (scan) {
			mBluetoothAdapter.startLeScan(iScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(iScanCallback);
		}
	}

	/**
	 * 外部接口回调集合
	 * @param listeners
	 */
	public void updateIConnectListener(ArrayList<IConnectListener> listeners) {
		if(iConnectListeners == null){
			if(mKCTBroadcastReceive == null){
				mKCTBroadcastReceive = new KCTBroadcastReceive(context,this);
				intentFilter = new IntentFilter();
				intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			}
			context.registerReceiver(mKCTBroadcastReceive,intentFilter);
		}else if(listeners.size() == 0){
			if(mKCTBroadcastReceive != null) {
				context.unregisterReceiver(mKCTBroadcastReceive);
				mKCTBroadcastReceive = null;
			}
		}
		this.iConnectListeners = listeners;
	}


	/**
	 * 设置蓝牙连接状态
	 */
	public synchronized void setConnectState(int state) {
		this.state = state;
		if (state == KCTBluetoothManager.STATE_CONNECTED) {
			if (null == kctSendCommand) {
				kctSendCommand = new KCTSendCommand(handler);
				kctSendCommand.start();
			}
			if (null == kctReceiveCommand) {
				kctReceiveCommand = new KCTReceiveCommand(handler);
				kctReceiveCommand.start();
			}
		} else if (state == KCTBluetoothManager.STATE_CONNECT_FAIL) {
			if (null != kctReceiveCommand) {
				kctReceiveCommand.cancel();
				kctReceiveCommand = null;
			}
			if (null != kctSendCommand) {
				kctSendCommand.cancel();
				kctSendCommand = null;
			}
		}
		if(iConnectListeners != null && iConnectListeners.size() > 0) {
			LogUtil.d(TAG, "iConnectListeners num = " + iConnectListeners.size());
			for (int i = 0; i < iConnectListeners.size(); i++) {
				iConnectListeners.get(i).onConnectState(state);
			}
		}
	}

	public boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	public void runOnMainThread(Runnable runnable) {
		if (isMainThread()) {
			runnable.run();
		} else {
			if (handler != null) {
				handler.post(runnable);
			}
		}
	}

	/**
	 * Dialog升级
	 */
	public void setDialog(boolean isSendFile,IDialogCallback dialogCallback){
		this.isSendFile = isSendFile;
		this.iDialogCallback = dialogCallback;
	}


	public String checkDFU_upgrade(final int versionCode){
		String version = "";
		String url = KCTLoadJNICommand.getInstance().getDFUCommand() + versionCode;
		try {
			String result = HTTPUtil.sendGet(url);
			if(!TextUtils.isEmpty(result)){
				JSONObject jsonObject = new JSONObject(result);
				version = jsonObject.getString(KCTLoadJNICommand.getInstance().getDFUVersion());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}


	public byte[] getDFU_data(final int versionCode){
		byte[] data = null;
		String url = KCTLoadJNICommand.getInstance().getDFUCommand() + versionCode;
		try {
			String result = HTTPUtil.sendGet(url);
			if(!TextUtils.isEmpty(result)){
				JSONObject jsonObject = new JSONObject(result);
				String file = jsonObject.getString(KCTLoadJNICommand.getInstance().getDFUData());
				if(!TextUtils.isEmpty(file)){
					data = HTTPUtil.downFile(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}


	public void upgrade_DFU(String filePath,String connectAddress){
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(connectAddress);
		if(device == null || TextUtils.isEmpty(device.getName())){
			Log.d(TAG,"device is null");
			return;
		}
		DfuServiceInitiator starter;
		starter = new DfuServiceInitiator(connectAddress).setDeviceName(device.getName()).setKeepBond(false);
		File filed = new File(filePath);
		Uri fileUri = Uri.fromFile(filed);
		starter.setZip(fileUri, filePath);
		starter.start(context, KCTDFUService.class);
	}


	@Override
	public void onConnectSuccess(BluetoothGatt gatt) {
		if (null == kctSendCommand) {
			kctSendCommand = new KCTSendCommand(handler);
			kctSendCommand.start();
		}
		if (null == kctReceiveCommand) {
			kctReceiveCommand = new KCTReceiveCommand(handler);
			kctReceiveCommand.start();
		}

		if(mKCTConnectRunnable != null) {
			handler.removeCallbacks(mKCTConnectRunnable);
			mKCTConnectRunnable = null;
		}

		if(!mBluetoothGatt.getDevice().getName().contains("DfuTarg")) {
			List<BluetoothGattService> gattServiceList = gatt.getServices();
			if (gattServiceList != null && gattServiceList.size() > 0) {
				for (int i = 0; i < gattServiceList.size(); i++) {
					if (gattServiceList.get(i).getUuid() != null) {
						if (gattServiceList.get(i).getUuid().equals(RX_SERVICE_UUID)
								|| gattServiceList.get(i).getUuid().equals(RX_SERVICE_872_UUID)
								|| gattServiceList.get(i).getUuid().equals(RX_SERVICE_872_UUID_SCAN)) {
							serviceUuid = gattServiceList.get(i).getUuid();
							RxService = mBluetoothGatt.getService(gattServiceList.get(i).getUuid());
							break;
						}
					}
				}
			}

			if(mKctSendDataRunnable == null) {
				mKctSendDataRunnable = new KCTSendDataRunnable(kctSendCommand
						,kctReceiveCommand,handler,RxService);
			}
		}


		mConnectDevice = gatt.getDevice();
		for (int i = 0; i < iConnectListeners.size(); i++) {
			iConnectListeners.get(i).onConnectDevice(mConnectDevice);
		}

		setConnectState(KCTBluetoothManager.STATE_CONNECTED);
	}

	@Override
	public void onConnectSuccessDFU(BluetoothGatt gatt) {
		if (null == kctSendCommand) {
			kctSendCommand = new KCTSendCommand(handler);
			kctSendCommand.start();
		}
		if (null == kctReceiveCommand) {
			kctReceiveCommand = new KCTReceiveCommand(handler);
			kctReceiveCommand.start();
		}

		if(mKCTConnectRunnable != null) {
			handler.removeCallbacks(mKCTConnectRunnable);
			mKCTConnectRunnable = null;
		}
	}

	@Override
	public void onDisconnect() {
		disconnect();
		close();
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		if(status == BluetoothGatt.GATT_SUCCESS){
			if(nextByte.peek() != null) {
				nextByte.poll();
			}
			if (null != nextByte.peek()) {
				writeToDevice(nextByte.peek());
			}
			if(isSendFile){
				iDialogCallback.send_file(true);
			}
		}else{
			if(isSendFile){
				iDialogCallback.send_file(false);
			}
		}
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] buffer = characteristic.getValue();
		if (null == kctReceiveCommand) {
			kctReceiveCommand = new KCTReceiveCommand(handler);
			kctReceiveCommand.start();
		}
		if (kctReceiveCommand.addDataBuffer(buffer,serviceUuid)) {
			if (null != kctSendCommand) {
				if(mKctSendDataRunnable != null) {
					handler.removeCallbacks(mKctSendDataRunnable);
				}
				if(kctSendCommand != null) {
					kctSendCommand.reCancel(true);
				}
			} else {
				Log.d(TAG, "KCTSend is null");
			}
		}
	}

	@Override
	public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

	}

	@Override
	public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
		mtuSize = mtu;
	}


	public void registerDFUProgressListener(IDFUProgressCallback mIDFUProgressCallback){
		if(mDFUProgressListener == null){
			mDFUProgressListener = new DFUProgressListener(this,mIDFUProgressCallback);
		}
		DfuServiceListenerHelper.registerProgressListener(context, mDFUProgressListener);
	}

	public void unregisterDFUProgressListener(){
		if(mDFUProgressListener != null) {
			DfuServiceListenerHelper.unregisterProgressListener(context, mDFUProgressListener);
			mDFUProgressListener = null;
		}
	}
}
