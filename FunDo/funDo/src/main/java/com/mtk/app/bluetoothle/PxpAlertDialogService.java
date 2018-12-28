/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 * 
 * MediaTek Inc. (C) 2010. All rights reserved.
 * 
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */

package com.mtk.app.bluetoothle;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.PxpFmStatusRegister;
import com.szkct.bluetoothtool.AppValueCheckbox;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;

import java.io.IOException;

public class PxpAlertDialogService extends Service {

    private static final String TAG = "[PxpAlertDialogService]";
    private static final boolean DBG = true;

    private static final int UPDATE_ALERT = 0;
    private static final int MOVE_ALERT_TO_NOTIFICATION = 1;
    private static final int INTENT_START_ACTIVITY = Integer.MAX_VALUE;
    private static final String INTENT_ADDRESS = BlePxpFmpConstants.PXP_INTENT_ADDRESS;
    private static final String INTENT_STATE = BlePxpFmpConstants.PXP_INTENT_STATE;
    public static final String ACTION_LAUNCH_BLE_MANAGER = "android.intent.action.MAIN";

    private static final String RINGTONE_NAME = "music/Alarm_Beep_03.ogg";
    private static final long[] VIBRATE_PATTERN = new long[] {
            500, 500
    };
    private static final long DIALOG_TIMEOUT_DURATION = 30000;
    
    private static final int RING_TONE_VOLUMN = 10;

    private int mDeviceStatus;
    private String mDeviceAddress;
    private AlertDialog mAlertDialog = null;
    private View mContentView = null;
    private TextView mDeviceNameView = null;
    private TextView mDeviceStatusView = null;
    private TelephonyManager mTM = null;
    private MediaPlayer mMediaPlayer = null;
    private Vibrator mVibrator = null;
    private AssetFileDescriptor mRingtoneDescriptor = null;

    private boolean mflags = false;

    AudioManager.OnAudioFocusChangeListener mAudioListener =
            new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (DBG) {
                Log.d(TAG, "onAudioFocusChange:" + focusChange);
            }
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    pauseRingAndVib();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    replayRingAndVib();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    stopRingAndVib();
                    break;
                default:
                    break;
            }
        }
    };

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.i(TAG, "PhoneStateListener, new state=" + state);
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                    if (mProximityHandler.hasMessages(MOVE_ALERT_TO_NOTIFICATION)) {
                        mProximityHandler.removeMessages(MOVE_ALERT_TO_NOTIFICATION);
                        mProximityHandler.sendMessageAtFrontOfQueue(mProximityHandler
                                .obtainMessage(MOVE_ALERT_TO_NOTIFICATION));
                    }
                }
            }
        }
    };

    private Handler mProximityHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (DBG) {
                Log.d(TAG, "handleMessage: " + msg.what);
            }
            switch (msg.what) {
                case UPDATE_ALERT:
                    updateInfo((String) msg.obj, msg.arg1);
                    break;
                case MOVE_ALERT_TO_NOTIFICATION:
                    moveAlertToNotification();
                    break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        if (DBG) {
            Log.d(TAG, "onCreate()");
        }
        AssetManager assetManager = this.getAssets();
        try {
            mRingtoneDescriptor = assetManager.openFd(RINGTONE_NAME);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mTM = (TelephonyManager) (getSystemService(Context.TELEPHONY_SERVICE));
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        initDialog();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String address = intent.getStringExtra(INTENT_ADDRESS);
            int state = intent.getIntExtra(INTENT_STATE, 0);
            updateInfo(address, state);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            mRingtoneDescriptor.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void checkAndFinishService() {
        if ((!mProximityHandler.hasMessages(UPDATE_ALERT)) && mDeviceAddress == null) {
            try {
                finalize();
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initDialog() {
        if (DBG) {
            Log.d(TAG, "initDialog");
        }
        mContentView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_text_view, null);
        mDeviceNameView = (TextView) mContentView.findViewById(R.id.device_name);
        mDeviceStatusView = (TextView) mContentView.findViewById(R.id.device_status);

        OnClickListener buttonListener = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DBG) {
                    Log.d(TAG, "Check clicked");
                }
                // to do
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        startMainActivity();
                        break;
                    default:
                        break;
                }
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                if (mDeviceStatus == BlePxpFmpConstants.STATE_IN_RANGE_ALERT
                        || mDeviceStatus == BlePxpFmpConstants.STATE_OUT_RANGE_ALERT) {
                    stopRemoteAlert();
                }
                mDeviceStatus = BlePxpFmpConstants.STATE_NO_ALERT;
                mDeviceAddress = null;
                mProximityHandler.sendMessageAtFrontOfQueue(mProximityHandler
                        .obtainMessage(UPDATE_ALERT));
            }
        };
        /****** zhangxiong modify  begin  ******/
        try {
			mAlertDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.pxp_dialog_title)
			.setCancelable(false)
			.setView(mContentView)
			.setPositiveButton(R.string.pxp_dialog_check, buttonListener)
			.setNegativeButton(R.string.pxp_dialog_dismiss, buttonListener)
			.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					if (DBG) {
						Log.d(TAG, "onDismiss");
					}
					stopRingAndVib();
					if (mProximityHandler.hasMessages(MOVE_ALERT_TO_NOTIFICATION)) {
						mProximityHandler.removeMessages(MOVE_ALERT_TO_NOTIFICATION);
					}
				}
			})
			.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
							|| event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
						return true;
					}
					return false;
				}
			}).create();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
        /****** zhangxiong modify  end ******/

        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        mAlertDialog.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    private void startMainActivity() {
        Intent intent = new Intent(ACTION_LAUNCH_BLE_MANAGER);
        intent.setClassName(getApplicationContext(), MainActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PxpAlertDialogService.this.startActivity(intent);
    }

    private void stopRemoteAlert() {
        BluetoothManager bluetoothManager = null;
        BluetoothAdapter adapter = null;
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return;
            }
            adapter = bluetoothManager.getAdapter();
        }
        if (adapter == null || mDeviceAddress == null) {
            Log.e(TAG, "stopRemoteAlert(), Adapter is null or mDeviceAddress is null");
            return;
        }
        BluetoothDevice device = adapter.getRemoteDevice(mDeviceAddress);
        if (device != null) {
            LocalPxpFmpController.stopRemotePxpAlert(device);
        } else {
            Log.e(TAG, "stopRemoteRing() device is null: " + mDeviceAddress);
        }
    }

    private void updateInfo(String address, int state) {
        Log.d(TAG, "updateInfo address=" + address + " state=" + state
                + " mDeviceAddress=" + mDeviceAddress + " mDeviceStatus=" + mDeviceStatus);
        boolean createAlert = false;
        if (address != null) {
            if (state == BlePxpFmpConstants.STATE_NO_ALERT) {
                mDeviceAddress = null;
            } else {
                if (!address.equals(mDeviceAddress) || state != mDeviceStatus) {
                    if (!address.equals(mDeviceAddress)
                            || (mDeviceStatus == BlePxpFmpConstants.STATE_NO_ALERT)) {
                        createAlert = true;
                    }
                    mDeviceAddress = address;
                    mDeviceStatus = state;
                } else {
                    // no need do anything
                    Log.d(TAG, "The same status and address, return");
                    return;
                }
            }
            PxpFmStatusRegister register = PxpFmStatusRegister.getInstance();
            register.setPxpAlertStatus(state);
        } else {
            if (state == INTENT_START_ACTIVITY) {
                startMainActivity();
            }
        }
        updateAlert(createAlert, address);
        checkAndFinishService();
    }

    private void moveAlertToNotification() {
        Log.d(TAG, "moveAlertToNotification ");
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            Log.d(TAG, "moveAlertToNotification dismiss");
            mAlertDialog.dismiss();
        }
        updateNotification(false, false);
    }

    private void updateNotification(boolean isDialogShowing, boolean silentUpdate) {

        removeNotification();
        if (DBG) {
            Log.d(TAG, "updateNotification : ");
        }

        if (mDeviceAddress != null && (!isDialogShowing)) {
            Resources r = this.getResources();
            String infoString = updateNotificationString();
            Intent intent = new Intent(PxpStatusChangeReceiver.STATE_CHANGE_ACTION);
            intent.putExtra(INTENT_STATE, INTENT_START_ACTIVITY);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Notification alertNotification = new Notification.Builder(this)
                    .setContentTitle(r.getString(R.string.ble_manager_alert))
                    .setContentText(infoString).setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentIntent(pendingIntent).setAutoCancel(true).build();

            if (silentUpdate) {
                alertNotification.defaults |= Notification.DEFAULT_LIGHTS;
            } else {
                alertNotification.defaults |= Notification.DEFAULT_ALL;
            }
            alertNotification.flags |= Notification.FLAG_ONGOING_EVENT;

            NotificationManager nM = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nM.notify(R.string.ble_manager_alert, alertNotification);
        }
    }

    private void removeNotification() {
        if (DBG) {
            Log.d(TAG, "removeNotification");
        }
        NotificationManager nM = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nM.cancel(R.string.ble_manager_alert);
    }

    private void updateAlert(boolean createAlert, String address) {
        Log.d(TAG, "updateAlert address=" + address + " createAlert=" + createAlert);
        /*boolean isDialogShowing = updateDialog(createAlert, address);
        updateNotification(isDialogShowing, false);*/
    }

    /*private boolean updateDialog(boolean createAlert, String address) {
        boolean isInCalling = (mTM.getCallState() == TelephonyManager.CALL_STATE_RINGING);
        Log.d(TAG, "updateDialog isInCalling=" + isInCalling);
        if (mDeviceAddress != null) {
            if (mAlertDialog.isShowing() && !isInCalling) {
                //mAlertDialogManager.notifyDataSetChanged();
                updateDialogText();
                *//*
                 * If address != null, the triggering device is in non-normal
                 * status. If address == null, the triggering device is in
                 * normal status
                 *//*
                if (address != null) {
                    sendDelayMessage();
                    applyRingAndVib(address);
                }
                return true;
            } else if (createAlert && !isInCalling) {
                updateDialogText();
                removeNotification();
                mflags = AlertSettingReadWriter.getSwtichPreferenceEnabled(
                        getApplicationContext(),AlertSettingPreference.SYSTEM_ALERT_WINDOW,false);
                if(mflags) {
                    mAlertDialog.show();
                }
                sendDelayMessage();
                // Normally, createAlert == true, address must be null
                if (address != null) {
                    applyRingAndVib(address);
                }
                return true;
            } else {
                if (mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                return false;
            }
        } else {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
            return false;
        }
    }*/

    private void sendDelayMessage() {
        if (mProximityHandler.hasMessages(MOVE_ALERT_TO_NOTIFICATION)) {
            mProximityHandler.removeMessages(MOVE_ALERT_TO_NOTIFICATION);
        }
        mProximityHandler.sendMessageDelayed(
                mProximityHandler.obtainMessage(MOVE_ALERT_TO_NOTIFICATION),
                DIALOG_TIMEOUT_DURATION);
    }

    private String updateNotificationString() {
        StringBuilder infoStringBuilder = new StringBuilder();
        Resources r = getResources();
        int status;
        String address = null;
        String deviceName = null;
        synchronized (mDeviceAddress) {
            if (mDeviceAddress != null) {
                address = mDeviceAddress;
            } else {
                return "";
            }
        }
        deviceName = readDeviceName(address);
        if (deviceName == null) {
            deviceName = address;
        }

        status = mDeviceStatus;

        if (deviceName == null) {
            return "";
        }
        switch (status) {
            case BlePxpFmpConstants.STATE_DISCONNECTED_ALERT:
                infoStringBuilder.append(r.getString(R.string.pxp_dialog_device_name, deviceName));
                infoStringBuilder.append(r.getString(R.string.device_status_text_disconnected));
                break;

            case BlePxpFmpConstants.STATE_IN_RANGE_ALERT:
                infoStringBuilder.append(r.getString(R.string.pxp_dialog_device_name, deviceName));
                infoStringBuilder.append(r.getString(R.string.device_status_text_in_range));
                break;

            case BlePxpFmpConstants.STATE_OUT_RANGE_ALERT:
                infoStringBuilder.append(r.getString(R.string.pxp_dialog_device_name, deviceName));
                infoStringBuilder.append(r.getString(R.string.device_status_text_out_range));
                break;
            default:
                break;
        }
        return infoStringBuilder.toString();
    }

    private void stopRingAndVib() {
        if (DBG) {
            Log.d(TAG, "stopRingAndVib");
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(mAudioListener);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }

    private void pauseRingAndVib() {
        if (DBG) {
            Log.d(TAG, "pauseRingAndVib");
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }

    private void replayRingAndVib() {
        if (DBG) {
            Log.d(TAG, "replayRingAndVib");
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBRATE_PATTERN, 0);
    }

    private void updateDialogText() {
        String name = null;
        Resources res = getApplicationContext().getResources();
        String address = null;
        synchronized (mDeviceAddress) {
            if (mDeviceAddress != null) {
                address = mDeviceAddress;
            } else {
                return;
            }
        }
        name = readDeviceName(address);
        if (name == null) {
            name = address;
        }
        mDeviceNameView.setText(res.getString(R.string.pxp_dialog_device_name, name));
        switch (mDeviceStatus) {
            case BlePxpFmpConstants.STATE_DISCONNECTED_ALERT:
                mDeviceStatusView.setText(R.string.device_status_text_disconnected);
                break;

            case BlePxpFmpConstants.STATE_IN_RANGE_ALERT:
                mDeviceStatusView.setText(R.string.device_status_text_in_range);
                break;

            case BlePxpFmpConstants.STATE_OUT_RANGE_ALERT:
                mDeviceStatusView.setText(R.string.device_status_text_out_range);
                break;
            default:
                mDeviceStatusView.setText("");
                break;
        }
    }

    /*private void applyRingAndVib(String address) {

        if (DBG) {
            Log.d(TAG, "applyRingAndVib: " + address);
        }
        stopRingAndVib();

        if (address == null) {
            return;
        }

        boolean ringToneEnabler = AlertSettingReadWriter.getSwtichPreferenceEnabled(
                getApplicationContext(),
                AlertSettingPreference.RINGTONE_PREFERENCE,
                AlertSettingPreference.DEFAULT_RANGE_ALERT_ENABLE);
        boolean vibEnabler = AlertSettingReadWriter.getSwtichPreferenceEnabled(
                getApplicationContext(),
                AlertSettingPreference.VIBRATION_PREFERENCE,
                AlertSettingPreference.DEFAULT_VIBRATION_ENABLE);
        //int ringtoneVolume = 0;

        if (DBG) {
            Log.d(TAG, "applyRingAndVib: ringToneEnabler:" + ringToneEnabler + ", vibEnabler:"
                    + vibEnabler);
        }
        if (ringToneEnabler) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mRingtoneDescriptor.getFileDescriptor(),
                        mRingtoneDescriptor.getStartOffset(), mRingtoneDescriptor.getLength());
                // mMediaPlayer.setDataSource("file:///android_asset/ + RINGTONE_NAME");
                mMediaPlayer.setLooping(true);

                AudioManager aM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                aM.setStreamVolume(AudioManager.STREAM_ALARM, RING_TONE_VOLUMN, 0);

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer player, int what, int extra) {
                        if (DBG) {
                            Log.d(TAG, "Media Player onError:" + what);
                        }
                        stopRingAndVib();
                        return false;
                    }
                });
                mMediaPlayer.prepare();
                aM.requestAudioFocus(mAudioListener, AudioManager.STREAM_ALARM,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                mMediaPlayer.start();

            } catch (IllegalStateException e) {
                Log.e(TAG, "Media Player IllegalStateException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "Media Player IOException");
                e.printStackTrace();
            }
        }
        if (vibEnabler) {
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(VIBRATE_PATTERN, 0);
        }
    }*/

    private String readDeviceName(String address) {
        SharedPreferences prefs = this.getSharedPreferences("device_name", Context.MODE_PRIVATE);
        String name = prefs.getString(address, "");
        Log.d(TAG, "[readDeviceName] begin " + address + " " + name);
        return name;
    }

}
