
package com.mtk.app.notification;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.support.v4.content.PermissionChecker;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.kct.fundo.btnotification.R;
import com.mediatek.ctrl.notification.NotificationController;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.FullMutualToHalf;
import com.szkct.weloopbtsmartdevice.util.NumberBytes;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Timer;



/**
 * This class will receive and process phone information, when phone state
 * changes.
 */
public class CallService extends PhoneStateListener {
    // Debugging
    private static final String TAG = "AppManager/CallService";

    private static final int MSG_NEED_UPDATE_MISSED_CALL = 100;

    private Context mContext = null;

    private int mLastState = TelephonyManager.CALL_STATE_IDLE; // the last phone
                                                               // state

    private String mIncomingNumber = null;

    private Timer mTimer = null;

    private MissedCallContentOberserver mMCOberserver = null;

    private ContentResolver mContentResolver = null;

    private MainService mainService;

    private boolean isHasCommingPhone = false;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEED_UPDATE_MISSED_CALL: {
                    // sendCallMessage();
                    String phoneNum = mIncomingNumber;
                    String sender = Utils.getContactName(mContext, phoneNum);
                    String content = getMessageContent(sender);
                    int missedCallCount = getMissedCallCount();
                    boolean isOpen = (boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, false);
                    if(isOpen) {
                            NotificationController.getInstance(mContext).sendCallMessage(mIncomingNumber,
                                    sender, content, missedCallCount);   // todo --- 推送来电
                    }
                    break;
                }
            }
        }
    };

    public CallService(Context context) {
        Log.i(TAG, "CallService(), CallService created!");
        mContext = context;
        mContentResolver = context.getContentResolver();

        mMCOberserver = new MissedCallContentOberserver(mHandler);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /////////////////////////////////////////////////////////////////////////////////////////////////////
            int writeSdCardPermission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                writeSdCardPermission =  mContext.checkSelfPermission(Manifest.permission.WRITE_CALL_LOG); // == PackageManager.PERMISSION_GRANTED;
            }else{
                writeSdCardPermission = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.WRITE_CALL_LOG); //  == PermissionChecker.PERMISSION_GRANTED;
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//            int writeSdCardPermission = BTNotificationApplication.getInstance().checkSelfPermission(Manifest.permission.WRITE_CALL_LOG);
            if (writeSdCardPermission == PackageManager.PERMISSION_GRANTED) {
                mContentResolver.registerContentObserver(Calls.CONTENT_URI, false, mMCOberserver);  // todo --- 有权限才开启
//                Logg.e(TAG, "CallService: 权限申请成功------------------");
            }else{
//                Logg.e(TAG, "CallService: 权限未申请失败------------------");
            }
        }else{
            mContentResolver.registerContentObserver(Calls.CONTENT_URI, false, mMCOberserver);
        }



        mainService = MainService.getInstance();
    }

    public void stopCallService() {
        Log.i(TAG, "StopCallService(), CallService stoped!");

        mContentResolver.unregisterContentObserver(mMCOberserver);
        mMCOberserver = null;
        mContentResolver = null;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        Log.i(TAG, "onCallStateChanged(), incomingNumber" + incomingNumber);
        Log.i(TAG, "state = " + state);
        if ((state == TelephonyManager.CALL_STATE_RINGING) && (incomingNumber != null)) {    //todo   ----
            isHasCommingPhone = true;
            mIncomingNumber = incomingNumber;
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){
                boolean isOpen = (boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, false);
                if(isOpen) {
                    String name = Utils.getContactNameFromPhoneBook(BTNotificationApplication.getInstance().getApplicationContext()
                            , mIncomingNumber);//通过电话获取名字
                    if (name == null || name.equals("")) {
                        name = BTNotificationApplication.getInstance().getApplicationContext().getString(R.string.unknown);
                    }
                    String callStr = name + " " + mIncomingNumber;
                    String codeType = "Unicode";
                    if(Build.VERSION.SDK_INT >= 27) {  //Android8.1新特性
                        codeType = "UnicodeLittleUnmarked";
                    }
                    byte[] l2Ticker = null;
                    try {
                        l2Ticker = callStr.getBytes(codeType);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    final byte[] l2Value = new byte[l2Ticker.length + 1];
                    l2Value[0] = BleContants.REMIND_INCALL;
                    System.arraycopy(l2Ticker, 0, l2Value, 1, l2Ticker.length);
                    L2Send.sendNotifyMsg(l2Value); //不延迟发送手环会有乱码       // todo --- 推送来电
                }
            }
            else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                ///////###########################################################################################################################
                String name = Utils.getContactNameFromPhoneBook(BTNotificationApplication.getInstance().getApplicationContext()
                        , mIncomingNumber);//通过电话获取名字
                if (name == null || name.equals("")) {
                    name = BTNotificationApplication.getInstance().getApplicationContext().getString(R.string.unknown);
                }
                String callStr = name + " " + mIncomingNumber;
                String codeType = "UTF-8";
                byte[] l2Ticker = null;
                try {
                    l2Ticker = callStr.getBytes(codeType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ///////###########################################################################################################################
                String packName = "com.kct.call";    // callMes    // com.tencent.mobileqq
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                int packNameLength = packName.getBytes().length;  // 20
                String title = "Call";   // 来电（分语言 ）
                int titleLength = title.getBytes().length;    // 啥都变了
                int id = 121;
//                long when = notificationData.getWhen(); // 1530350847501        long time = System.currentTimeMillis();
                long when = System.currentTimeMillis();  // ---- ok  1530351854249
                int tickerLength = l2Ticker.length;   // 23
                byte[] l2Value = new byte[1 + 1 + packNameLength + 4 + 2 + titleLength + 2 + tickerLength + 8];
                //packName
                l2Value[1] = (byte) packNameLength;   // 20
                System.arraycopy(packName.getBytes(),0,l2Value,2,packNameLength);
                //id
                l2Value[2 + packNameLength] = (byte)(id >> 24);
                l2Value[3 + packNameLength] = (byte)(id >> 16);
                l2Value[4 + packNameLength] = (byte)(id >> 8);
                l2Value[5 + packNameLength] = (byte)(id & 0xff);
                //title
                l2Value[6 + packNameLength] = (byte)(titleLength >> 8);
                l2Value[7 + packNameLength] = (byte)(titleLength & 0xff);
                System.arraycopy(title.getBytes(),0,l2Value,8 + packNameLength,titleLength);
                //ticker
                l2Value[8 + packNameLength + titleLength] = (byte)(tickerLength >> 8);
                l2Value[9 + packNameLength + titleLength] = (byte)(tickerLength & 0xff);
                System.arraycopy(l2Ticker,0,l2Value,10 + packNameLength + titleLength,tickerLength);
                //when
                byte[] bytes = NumberBytes.longToBytes(when);
                System.arraycopy(bytes,0,l2Value,10 + packNameLength + titleLength + tickerLength,bytes.length);
                L2Send.sendNotifyMsg(l2Value);
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }

            else {
                mainService.phoneName = Utils.getContactNameFromPhoneBook(BTNotificationApplication.getInstance().getApplicationContext()
                        , mIncomingNumber);//通过电话获取名字
                mainService.phoneNumber = incomingNumber;
            }
        }else if((state == TelephonyManager.CALL_STATE_IDLE) && (incomingNumber != null)){   //空闲状态
            mainService.isCallSend = false;
            mainService.phoneName = "";
            mainService.phoneNumber = "";
            
            if(BTNotificationApplication.isSyncEnd && MainService.getInstance().getState() == 3 && isHasCommingPhone) {  //
                String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //
                BluetoothDevice device =  BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));     // TODO ---- 同步数据完成
                if("QW11".equals(device.getName()) || "156".equals(code) || "150".equals(code) || "198".equals(code) || "172".equals(code) || "427"
                        .equals(code) || "415".equals(code) || "540".equals(code) || "548".equals(code) || "570".equals(code)|| "530".equals(code) ) {// todo ---    此设备挂电话不回命令
                    byte[] l2Value2 = new byte[1];
                    l2Value2[0] = BleContants.REMIND_HUNGUP; // 挂断电话提醒  命令 TODO --- 已占用 ，需要用另外的命令 0x0B
                    L2Send.sendNotifyMsg(l2Value2);
                }

                if("435".equals(code)){
                    String tickerText = BTNotificationApplication.getInstance().getString(R.string.missed_callsble) + ":" + mIncomingNumber;  // todo --- 需要  添加 多语言 字段    ？？？？？？？？？？？？？？？？？
                    byte[] l2Ticker = null;
                    String tickerTextok = FullMutualToHalf.fullToHalf(tickerText);
                    String codeType = "Unicode";
                    if(Build.VERSION.SDK_INT >= 27) {  //Android8.1新特性
                        codeType = "UnicodeLittleUnmarked";
                    }
                    try {
                        l2Ticker = tickerTextok.getBytes(codeType);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    byte[] l2Value2 = new byte[l2Ticker.length + 1];
                    l2Value2[0] = BleContants.REMIND_OTHER; // 挂断电话提醒  命令 TODO --- 已占用 ，需要用另外的命令 0x0B
                    System.arraycopy(l2Ticker, 0, l2Value2, 1, l2Ticker.length);
                    L2Send.sendNotifyMsg(l2Value2);
                }

//                Toast.makeText(BTNotificationApplication.getInstance(),"发送了挂断电话的命令",Toast.LENGTH_LONG).show();
            }
        }else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
            mainService.phoneName = Utils.getContactNameFromPhoneBook(BTNotificationApplication.getInstance().getApplicationContext()
                    , mIncomingNumber);//通过电话获取名字
            mainService.phoneNumber = incomingNumber;

            if(BTNotificationApplication.isSyncEnd && MainService.getInstance().getState() == 3) {  // 同步数据成功 才发实时心率数据
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //
                BluetoothDevice device =  BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));     // TODO ---- 同步数据完成
                if("QW11".equals(device.getName()) || "156".equals(code) || "150".equals(code) || "198".equals(code) || "172".equals(code) || "427".equals(code) || "548".equals(code) || "570".equals(code)|| "530".equals(code) ) {// todo ---    此设备挂电话不回命令   || "540".equals(code)
                    byte[] l2Value2 = new byte[1];
                    l2Value2[0] = BleContants.REMIND_RING; // 接电话提醒
                    L2Send.sendNotifyMsg(l2Value2);     
                }

                if("435".equals(code)){

                }

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }

        }
        mLastState = state;
    }

    private String getMessageContent(String sender) {
        StringBuilder content = new StringBuilder();
        content.append(mContext.getText(R.string.missed_call));
        content.append(": ");
        content.append(sender);

        // TODO: Only for test
        content.append("\r\n");
        content.append("Missed Call Count:");
        content.append(getMissedCallCount());

        Log.i(TAG, "getMessageContent(), content=" + content);
        return content.toString();
    }

    private int getMissedCallCount() {
        // setup query spec, look for all Missed calls that are new.
        StringBuilder queryStr = new StringBuilder("type = ");
        queryStr.append(Calls.MISSED_TYPE);
        queryStr.append(" AND new = 1");
        Log.i(TAG, "getMissedCallCount(), query string=" + queryStr);

        // start the query
        int missedCallCount = 0;
        Cursor cur = null;
        cur = mContext.getContentResolver().query(Calls.CONTENT_URI, new String[] {
            Calls._ID
        }, queryStr.toString(), null, Calls.DEFAULT_SORT_ORDER);
        if (cur != null) {
            missedCallCount = cur.getCount();
            cur.close();
        }

        Log.i(TAG, "getMissedCallCount(), missed call count=" + missedCallCount);
        return missedCallCount;
    }

    private class MissedCallContentOberserver extends ContentObserver {

        private int mPreviousMissedCallCount;

        private Handler mHandler;

        public MissedCallContentOberserver(Handler handler) {
            super(handler);
            mPreviousMissedCallCount = 0;
            mHandler = handler;
        }

        public void onChange(boolean onSelf) {
            super.onChange(onSelf);
            Log.i(TAG, "DataBase State Changed");
            int missedCallCount = getMissedCallCount();
            if (missedCallCount == 0) {
                NotificationController.getInstance(mContext).sendReadMissedCallData();
            } else if (mPreviousMissedCallCount < missedCallCount) {
                Message msg = new Message();
                msg.what = MSG_NEED_UPDATE_MISSED_CALL;
                mHandler.sendMessage(msg);
            }

            mPreviousMissedCallCount = missedCallCount;
        }
    }

}
