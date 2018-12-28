
package com.mtk.app.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.mediatek.ctrl.notification.NotificationController;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.FullMutualToHalf;
import com.szkct.weloopbtsmartdevice.util.NumberBytes;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.UnsupportedEncodingException;

/**
 * This class will receive and process all new SMS.
 */
public class SmsService extends BroadcastReceiver {
    // Debugging
    private static final String TAG = "AppManager/SmsService";

    private static final String SMS_RECEIVED = "com.mtk.btnotification.SMS_RECEIVED";
//    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    // public static final String SMS_ACTION = "SenderSMSFromeFP";
    private static String preID = null;

    // Received parameters
    private Context mContext = null;

    public SmsService() {
        Log.i(TAG, "SmsReceiver(), SmsReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()");

        mContext = context;
        if (intent.getAction().equals(SMS_RECEIVED)) {
            sendSms();
        }
    }

    void sendSms() {
        String msgbody;
        String address;
        String id;

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Uri.parse("content://sms/inbox"), null,
                    null, null, "_id desc");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msgbody = cursor.getString(cursor.getColumnIndex("body"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    id = cursor.getString(cursor.getColumnIndex("_id"));
                    if (id.equals(preID)) {
                        break;
                    } else {
                        preID = id;
                        if ((msgbody != null) && (address != null)) {
                            String name = Utils.getContactNameFromPhoneBook(mContext,address);
                            Log.i("SmsReceiver", "SmsReceiver(),sendSmsMessage, msgbody = " + msgbody
                                    + ", address = " + address + ",  name = " + ((name == null) ? "" : name));
                            boolean isOpen = (boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, false);
                            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){
                                if(isOpen) {
                                    if (name == null || name.equals("")) {
                                        msgbody = address +": "+FullMutualToHalf.fullToHalf(msgbody);
                                    }else{
                                        msgbody = name + " " + address +": "+FullMutualToHalf.fullToHalf(msgbody);
                                    }

                                    if(!"548".equals(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE))){
                                        if(msgbody.length() > Constants.TICKER_TEXT_MAX_LENGH){
                                            msgbody = msgbody.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
                                        }
                                    }else { // todo --- 序列号为 548 的设备 最大长度
                                        if (msgbody.length() > Constants.TICKER_TEXT_MAX_LENGH_OTHERDEVICE) {//消息内容过长，只截取一部分内容
                                            msgbody = msgbody.substring(0, Constants.TICKER_TEXT_MAX_LENGH_OTHERDEVICE) + Constants.TEXT_POSTFIX;
                                        }
                                    }

//                                    if(msgbody.length() > Constants.TICKER_TEXT_MAX_LENGH){
//                                        msgbody = msgbody.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
//                                    }
                                    String codeType = "Unicode";
                                    if(Build.VERSION.SDK_INT >= 27) {  //Android8.1新特性
                                        codeType = "UnicodeLittleUnmarked";
                                    }
                                    byte[] l2Ticker = null;
                                    try {
                                        l2Ticker = msgbody.getBytes(codeType);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    byte[] l2Value = new byte[l2Ticker.length + 1];
                                    l2Value[0] = BleContants.REMIND_SMS;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    if(l2Ticker.length>1024){
                                        System.arraycopy(l2Ticker, 0, l2Value, 1, 1024);
                                    }else {
                                        System.arraycopy(l2Ticker, 0, l2Value, 1, l2Ticker.length);
                                    }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                    System.arraycopy(l2Ticker, 0, l2Value, 1, l2Ticker.length);
                                    L2Send.sendNotifyMsg(l2Value);
                                }
                            }else if( SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                                if (name == null || name.equals("")) {
                                    msgbody = address +": "+FullMutualToHalf.fullToHalf(msgbody);
                                }else{
                                    msgbody = name + " " + address +": "+FullMutualToHalf.fullToHalf(msgbody);
                                }

                                if(!"548".equals(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE))){
                                    if(msgbody.length() > Constants.TICKER_TEXT_MAX_LENGH){
                                        msgbody = msgbody.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
                                    }
                                }else { // todo --- 序列号为 548 的设备 最大长度
                                    if (msgbody.length() > Constants.TICKER_TEXT_MAX_LENGH_OTHERDEVICE) {//消息内容过长，只截取一部分内容
                                        msgbody = msgbody.substring(0, Constants.TICKER_TEXT_MAX_LENGH_OTHERDEVICE) + Constants.TEXT_POSTFIX;
                                    }
                                }

//                                if(msgbody.length() > Constants.TICKER_TEXT_MAX_LENGH){
//                                    msgbody = msgbody.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
//                                }
                                String codeType = "UTF-8";
                                byte[] l2Ticker = null;
                                try {
                                    l2Ticker = msgbody.getBytes(codeType);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String packName = "com.kct.mms";    // callMes    // com.tencent.mobileqq
                                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                int packNameLength = packName.getBytes().length;  // 20
                                String title = "Mms";   // 来电（分语言 ）
                                int titleLength = title.getBytes().length;    // 啥都变了
                                int ido = 126;
//                long when = notificationData.getWhen(); // 1530350847501
                                long when = System.currentTimeMillis();  // ---- ok  1530351854249
                                int tickerLength = l2Ticker.length;   // 23
                                byte[] l2Value = new byte[1 + 1 + packNameLength + 4 + 2 + titleLength + 2 + tickerLength + 8];
                                //packName
                                l2Value[1] = (byte) packNameLength;   // 20
                                System.arraycopy(packName.getBytes(),0,l2Value,2,packNameLength);
                                //id
                                l2Value[2 + packNameLength] = (byte)(ido >> 24);
                                l2Value[3 + packNameLength] = (byte)(ido >> 16);
                                l2Value[4 + packNameLength] = (byte)(ido >> 8);
                                l2Value[5 + packNameLength] = (byte)(ido & 0xff);
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
                                if(isOpen) {
                                    NotificationController.getInstance(mContext).sendSmsMessage(msgbody, address);
                                }
                            }
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

}
