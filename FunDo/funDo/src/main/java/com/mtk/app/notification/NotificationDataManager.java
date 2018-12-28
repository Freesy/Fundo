package com.mtk.app.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.kct.fundo.btnotification.R;
import com.mediatek.ctrl.notification.NotificationActions;
import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.notification.NotificationData;
import com.mediatek.wearable.WearableManager;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.FullMutualToHalf;
import com.szkct.weloopbtsmartdevice.util.NumberBytes;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class NotificationDataManager {  // 通知处理类
    private static final String TAG = "AppManager/Noti/Manager";
    // For get tile and content of notification
    private static final int NOTIFICATION_TITLE_TYPE = 9;
    private static final int NOTIFICATION_CONTENT_TYPE = 10;
    private Handler mHandler;
    private SendNotficationDataThread mSendThread = null;
    private Context mContext;
  public   static boolean isSendmsg=false;//是否发消息

    private int messAgeTypeOne = 0;
    private int messAgeTypeTwo = 0;

    public static boolean isSendmsg() {
        return isSendmsg;
    }

    public static void setSendmsg(boolean sendmsg) {
        isSendmsg = sendmsg;
    }

    public NotificationDataManager(Context context) {
        Log.d(TAG, "NotificationDataManager created!");
        mContext = context;
        mSendThread = new SendNotficationDataThread();
        mSendThread.start();
        mHandler = mSendThread.getHandler();
    }
public static NotificationData myNotificationData;
public static String Pakagename;

    public static String getPakagename() {
        return Pakagename;
    }

    public static void setPakagename(String pakagename) {
        Pakagename = pakagename;
    }

    private long mLastDiaoyongTime = 0L;    //    短时间重发

    private static long time1 = 0;
    private static long time2 = 0;
    private static String messTemp = "";
    public void sendNotificationData(NotificationData notificationData, int type) {
        if(null==notificationData.getPackageName()){
           return;
        }
            // Filter notification according to ignore list and exclusion list
       try{
           HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
           HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
           HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
           if (!blockList.contains(notificationData.getPackageName()) && !ignoreList.contains(notificationData.getPackageName())
                   && !exclusionList.contains(notificationData.getPackageName())) {
               Log.i("ttggggg", "Notice: notification need send, package name=" + notificationData.getTickerText());
               /*mSendThread = new SendNotficationDataThread(notificationData);  /TODO--   new导致APP卡顿
               mSendThread.start();*/
               Message message = new Message();
               message.what = SendNotficationDataThread.MESSAGE_SEND_NOTIFICATION;
               message.obj = (Object) notificationData;
               message.arg1 = type;

               mHandler = mSendThread.getHandler();
               if (mHandler != null) {
                   mHandler.sendMessage(message);
               }
           } else {
               Log.i(TAG, "Notice: notification don't need send, package name=" + notificationData.getPackageName());
           }
       }catch (Exception E){E.printStackTrace();}
    }

    public NotificationData getNotificationData(Notification notification, String packageName, String tag, int id) {
        setPakagename(packageName);
        int watchVersion = WearableManager.getInstance().getRemoteDeviceVersion();
        Log.d(TAG, "watch version is " + watchVersion);

        NotificationData notificationData = new NotificationData();
        String[] textArray = null;
        if (android.os.Build.VERSION.SDK_INT >= 24 || packageName.contains("whatsapp")  || packageName.contains("linkedin") || packageName.contains("xiaomi.xmsf")) {      //todo ---  add 20180314   xiaomi.xmsf --- 为小米系统推送
            textArray = getNotidicationTextForN(notification,packageName); //该方法会直接从extra中获取title和content
        } else {
            textArray = getNotificationText(notification);    //7.0以下兼容
        }
        String[] pageTextArray = getNotificationPageText(notification); //android 4.4w.2 support
        MainService mainService = MainService.getInstance();

        if (!TextUtils.isEmpty(notification.tickerText)) {
            notificationData.setTickerText(notification.tickerText.toString());
            if(packageName.contains("whatsapp") || (android.os.Build.VERSION.SDK_INT >= 24)){
                if(textArray != null && !TextUtils.isEmpty(textArray[1]) && !TextUtils.isEmpty(textArray[0])) {
                    notification.tickerText = textArray[0] + " : " + textArray[1];
                    notificationData.setTickerText(textArray[0] + " : " + textArray[1]);
                }
            }
            if (packageName.contains(".incallui")) {
                if (TextUtils.isEmpty(mainService.phoneName)) {
                    textArray = new String[]{mContext.getString(R.string.inCalling), mainService.phoneNumber};
                } else {
                    textArray = new String[]{mContext.getString(R.string.inCalling), mainService.phoneNumber + "," + mainService.phoneName};
                }
            }
            if (null != pageTextArray && null != textArray && null != textArray[0]) {
                textArray = concat(textArray, pageTextArray);
            } else {
                if (null != notification && null != notification.tickerText) {
                    textArray = new String[2];
                    if (notification.tickerText.toString().contains(":")) {
                        textArray[0] = notification.tickerText.toString().split(":")[0];
                        textArray[1] = notification.tickerText.toString();
                    } else {
                        textArray[0] = notification.tickerText.toString();
                        textArray[1] = notification.tickerText.toString();
                    }
                }
            }
            if (null != textArray && null != textArray[0]) {
                notificationData.setTextList(textArray);
            }
            try {
                Log.d(TAG, "textlist = " + Arrays.toString(textArray));
            } catch (Exception e) {
                Log.d(TAG, "get textlist error");
            }

        }else{
            if(packageName.contains("linkedin.android") || packageName.contains("xiaomi.xmsf")){    // todo ----   packageName.contains("xiaomi.xmsf") 主要是对于小米手机
                if(textArray != null){
                    notificationData.setTickerText(textArray[0] + ": " + textArray[1]);
                }
                notificationData.setTextList(textArray);
            }
        }
        if(null!=notification&&null!=packageName){
            // notificationData.setGroupKey(getGroupKey(notification));
            notificationData.setActionsList(null);
            notificationData.setPackageName(packageName);
            notificationData.setAppID(Utils.getKeyFromValue(notificationData.getPackageName()));
            notificationData.setWhen(notification.when);
        }

        if (id == 0) { //Maybe some app's id is 0. like: hangouts(com.google.android.talk)
            id = 1 + (int) (Math.random() * 1000000);
            Log.d(TAG, "the id is 0 and need create a random number : " + id);
        }
        notificationData.setMsgId(id);
        if(null!=tag){notificationData.setTag(tag);}

        if(null==notificationData.getPackageName()){
            if(null!=getPakagename()){
                notificationData.setPackageName(getPakagename());
            }
        }
        if(null==notificationData.getTextList()&&null==notificationData.getPackageName()&&null==notificationData.getTickerText()){
         return  null;
        }

        Log.e(TAG,"notificationData = " + notificationData.toString());
        return notificationData;
    }

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("unchecked")
    public String[] getNotificationText(Notification notification) {
        String[] textArray = null;
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            textArray = new String[]{"", ""};
            Log.i(TAG, "remoteViews is null, set title and content to be empty. ");
        } else {
            HashMap<Integer, String> text = new HashMap<Integer, String>();
            try {
                Class<?> remoteViewsClass = Class.forName(RemoteViews.class.getName());
                Field[] outerFields = remoteViewsClass.getDeclaredFields();
                Log.i(TAG, "outerFields.length = " + outerFields.length);
                Field actionField = null;
                for (Field outerField : outerFields) {
                    if (outerField.getName().equals("mActions")) {
                        actionField = outerField;
                        break;
                    }
                }
                if (null==actionField) {
                    Log.e(TAG, "actionField is null, return null");
                    return null;
                }
                actionField.setAccessible(true);
                ArrayList<Object> actions = (ArrayList<Object>) actionField.get(remoteViews);
                int viewId = 0;
                for (Object action : actions) {
                    /*
                     * Get notification tile and content
                     */
                    Field[] innerFields = action.getClass().getDeclaredFields();

                    // RemoteViews curr_action = (RemoteViews)action;
                    Object value = null;
                    Integer type = null;
                    for (Field field : innerFields) {
                        field.setAccessible(true);
                        if (field.getName().equals("value")) {
                            value = field.get(action);
                        } else if (field.getName().equals("type")) {
                            type = field.getInt(action);
                        } else if (field.getName().equals("methodName")) {
                            String method = (String) field.get(action);
                            if (method.equals("setProgress")) {
                                return null;
                            }
                        }
                    }
                    if (( null!= type)
                            && ((type == NOTIFICATION_TITLE_TYPE) || (type == NOTIFICATION_CONTENT_TYPE))) {
                        if ( null!=value) {
                            viewId++;
                            text.put(viewId, value.toString());
                            if (viewId == 2) {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "getText ERROR");
            }

            textArray = text.values().toArray(new String[0]);
            if (null  == textArray) {
                Log.i(TAG, "get title and content from notification is null.Set it to be empty string.");
                textArray = new String[]{"", ""};
            } else {
                Log.i(TAG, "textArray is " + Arrays.toString(textArray));
            }
        }
        String[] bigTextArray = new String[2];
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT >= 19) {//android 4.4
            //get bigtextstyle title and content
            String EXTRA_TITLE = "android.title";
            String EXTRA_TITLE_BIG = EXTRA_TITLE + ".big";
            String EXTRA_BIG_TEXT = "android.bigText";
            CharSequence mBigTitle = notification.extras.getCharSequence(EXTRA_TITLE_BIG);
            CharSequence mBigText = notification.extras.getCharSequence(EXTRA_BIG_TEXT);
            if (!TextUtils.isEmpty(mBigTitle)) {
                bigTextArray[0] = mBigTitle.toString();
            } else if (null!=textArray && textArray.length > 0 && !TextUtils.isEmpty(textArray[0])) {
                bigTextArray[0] = textArray[0];
            } else {
                bigTextArray[0] = "";
            }

            if (!TextUtils.isEmpty(mBigText)) {
                bigTextArray[1] = mBigText.toString();
            } else if ( null!=textArray&& textArray.length > 1 && !TextUtils.isEmpty(textArray[1])) {
                bigTextArray[1] = textArray[1];
            } else {
                bigTextArray[1] = "";
            }

        } else {
            bigTextArray = textArray;
            Log.i(TAG, "Android platform is lower than android 4.4 and does not support bigtextstyle attribute.");
        }
        try {
            Log.d(TAG, "getNotificationText(), text list = " + Arrays.toString(bigTextArray));
        } catch (Exception e) {
            Log.d(TAG, "getNotificationText Exception");
        }
        return bigTextArray;
    }

    public String[] getNotificationPageText(Notification notification) {
        String[] textArray = null;
        // get title and content of Pages
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT >= 20) {//android 4.4w.2
            String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
            String KEY_PAGES = "pages";
            Bundle wearableBundle = notification.extras.getBundle(EXTRA_WEARABLE_EXTENSIONS);
            if ( null!=wearableBundle) {
                Notification[] pages = getNotificationArrayFromBundle(wearableBundle, KEY_PAGES);
                if (null!=pages) {
                    Log.i(TAG, "pages num = " + pages.length);
                    for (int i = 0; i < pages.length; i++) {
                        String[] pageTextArray = getNotificationText(pages[i]);
                        if ( null!= pageTextArray) {
                            if (i == 0) {
                                textArray = pageTextArray;
                            } else {
                                textArray = concat(textArray, pageTextArray);
                            }
                        }
                    }
                }
            }
        } else {
            Log.i(TAG, "Android platform is lower than android 4.4w.2 and does not support page attribute.");
        }
        try {
            Log.d(TAG, "getNotificationPageText(), text list = " + Arrays.toString(textArray));
        } catch (Exception e) {
            Log.d(TAG, "getNotificationPageText Exception");
        }
        return textArray;
    }

    public Notification[] getNotificationArrayFromBundle(Bundle bundle, String key) {
        Parcelable[] array = bundle.getParcelableArray(key);
        if (array instanceof Notification[] || array == null) {
            return (Notification[]) array;
        }
        Notification[] typedArray = Arrays.copyOf(array, array.length,
                Notification[].class);
        bundle.putParcelableArray(key, typedArray);
        return typedArray;
    }

    public String[] concat(String[] first, String[] second) {
        String[] result = new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public ArrayList<NotificationActions> getNotificationActions(Notification notification) {
        ArrayList<NotificationActions> actionsList = new ArrayList<NotificationActions>();
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340) {
            try {
                // get contentIntent field(The intent to execute when the expanded status entry is clicked.)
                Field mContentIntentField = Notification.class.getDeclaredField("contentIntent");
                if ( null!=  mContentIntentField) {
                    mContentIntentField.setAccessible(true);
                    PendingIntent contentIntent = (PendingIntent) mContentIntentField.get(notification);
                    // the contentIntent maybe is null, if the contentIntent is null do not add it to actionsList
                    if ( null!=contentIntent) {
                        NotificationActions notificationAction = new NotificationActions();
                        notificationAction.setActionId(String.valueOf(0)); // always is 0
                        notificationAction.setActionTitle(mContext.getString(R.string.notification_action_open));
                        notificationAction.setActionIntent(contentIntent);
                        actionsList.add(notificationAction);
                    } else {
                        Log.i(TAG, "contentIntent is null.");
                    }
                } else {
                    Log.i(TAG, "get contentIntent field failed.");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return actionsList;
    }


    Map<Object, Object> applist = AppList.getInstance().getAppList();
    private class SendNotficationDataThread extends Thread {
        public static final int MESSAGE_SEND_NOTIFICATION = 1;
        private NotificationData notificationData = null;

        @SuppressLint("HandlerLeak")
        private Handler mHandler;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MESSAGE_SEND_NOTIFICATION:   // arg1
                            int messType = msg.arg1;
                            notificationData = (NotificationData) msg.obj;
                            if (notificationData != null) {
                                if (MainService.getInstance().getState() == 3) {    //已经连接上了
                                    //////////////////////////////////////////////////////////////////////////////////////////////////
                                    String tickerText = null;
                                    if(!StringUtils.isEmpty(notificationData.getTickerText())) {
                                        tickerText = notificationData.getTickerText().toString();

                                        if(messType == 2){   //TODO NeNotificationService 发送
                                            time2 = System.currentTimeMillis();   // 1517555810015   todo --- 进此方法的时间
                                            Log.e(TAG, "time2的值为----" + time2);
                                            Log.e(TAG, "Math.abs(time2 - time1) 的值为--" + Math.abs(time2 - time1));
                                            Log.e(TAG, "当前消息为--" + tickerText + "旧的消息为--" + messTemp);
                                            if(Math.abs(time2 - time1) <1000  && messTemp.equals(tickerText) ){  // && messTemp.equals(tickerText)
                                                Log.e(TAG, "短时间内有重复消息，已过滤掉了--- Math.abs(time2 - time1) 的值为--" + Math.abs(time2 - time1));
//                                            mHandler.removeCallbacksAndMessages(null);
                                                return ;
                                            }else {
                                                messTemp = tickerText;
                                            }
                                        }else if(messType == 1){   //TODO NotificationReceiver19 发送
                                            time1 = System.currentTimeMillis();   // 1517555810015   todo --- 进此方法的时间
                                            Log.e(TAG, "time1的值为----" + time1);
                                            Log.e(TAG, "Math.abs(time2 - time1) 的值为--" + Math.abs(time2 - time1));
                                            Log.e(TAG, "当前消息为--" + tickerText + "旧的消息为--" + messTemp);
                                            if(Math.abs(time2 - time1) <1000  && messTemp.equals(tickerText) ){ //  && messTemp.equals(tickerText)
                                                Log.e(TAG, "短时间内有重复消息，已过滤掉了--- Math.abs(time2 - time1) 的值为--" + Math.abs(time2 - time1));
//                                            mHandler.removeCallbacksAndMessages(null);
                                                return ;
                                            }else {
                                                messTemp = tickerText;
                                            }
                                        }
                                    }
                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {  //mtk
                                        try {
                                            if (!applist.containsValue(notificationData.getPackageName())) {
                                                int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
                                                if (!applist.equals("[]")) {
                                                    applist.remove(AppList.MAX_APP);
                                                    max = max + 1;
                                                } else {
                                                    max = 1;
                                                }
                                                applist.put(AppList.MAX_APP, max);
                                                applist.put(max, notificationData.getPackageName());
                                                notificationData.setAppID(max + "");
                                                AppList.getInstance().saveAppList(applist);
                                            }
                                        } catch (Exception E) {
                                            E.printStackTrace();
                                        }
                                        if (null != notificationData && null != notificationData.getPackageName()) {
                                            if (notificationData.getPackageName().contains("incallui")) {
                                                MainService mainService = MainService.getInstance();
                                                if (!mainService.isCallSend) {
                                                    NotificationController.getInstance(mContext).sendNotfications(notificationData);
                                                    mainService.isCallSend = true;
                                                    notificationData = null;
                                                }
                                            } else {
                                                if (null != notificationData && null != notificationData.getTextList()) {
                                                    NotificationController.getInstance(mContext).sendNotfications(notificationData);
                                                    notificationData = null;
                                                }

                                            }
                                        }
                                    } /*else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) { //72
                                        String syncDatas = notificationData.getPackageName() + " " + notificationData.getAppID() + " " + notificationData.getWhen() + " " + notificationData.getTickerText().trim();   // notificationData.getTickerText()
                                        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.KEY_NOTIFICATION_PUSH, syncDatas.getBytes());  // 手机消息推送
                                        MainService.getInstance().writeToDevice(l2, true);
                                        if (null != notificationData) {
                                            notificationData = null;
                                        }
                                    }*/ else {
                                        dealNotification(notificationData);
                                        if (null != notificationData) {
                                            notificationData = null;
                                        }
                                    }
                                }
                            }
                    }
                }
            };
            Looper.loop();
        }

        public Handler getHandler() {
            return mHandler;
        }
    }


    private  void dealNotification(NotificationData notificationData) {   // synchronized

//        try {
//            new Thread().sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // com.tencent.mobileqq     com.kugou.android（酷狗不要）    com.qihoo360.mobilesafe（360）   com.tencent.android.qqdownloader（应用包）
        String appPackageNameChar = notificationData.getPackageName();//消息来源包名 新消息推送
        //获取应用报名失败
        if (null != appPackageNameChar) {
            Log.e(TAG, "新消息,app包名" + appPackageNameChar);

            // 过滤的应用的消息
            if(appPackageNameChar.equals("com.kugou.android") || appPackageNameChar.equals("com.qihoo360.mobilesafe") || appPackageNameChar.equals("com.tencent.android.qqdownloader")
                    ||appPackageNameChar.contains(".mms")||appPackageNameChar.contains("com.kct.fundo")){  //酷狗,360,应用宝    ||appPackageNameChar.contains(".incallui")  TODO --- 注释 20180806
                return;
            }
           /* //过滤部分可获取来电通知的来电提醒
            if(appPackageNameChar.equals(Contants.APP_PACKAGE_NAME_TEL))return;*/

            String tickerText = null;
            if(!StringUtils.isEmpty(notificationData.getTickerText())){

                String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //

                tickerText = notificationData.getTickerText().toString();

                if(!"548".equals(code)){
                    if (tickerText.length() > Constants.TICKER_TEXT_MAX_LENGH) {//消息内容过长，只截取一部分内容
                        tickerText = tickerText.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
                    }
                }else { // todo --- 序列号为 548 的设备 最大长度
                    if (tickerText.length() > Constants.TICKER_TEXT_MAX_LENGH_OTHERDEVICE) {//消息内容过长，只截取一部分内容
                        tickerText = tickerText.substring(0, Constants.TICKER_TEXT_MAX_LENGH_OTHERDEVICE) + Constants.TEXT_POSTFIX;
                    }
                }
//                if (tickerText.length() > Constants.TICKER_TEXT_MAX_LENGH) {//消息内容过长，只截取一部分内容
//                    tickerText = tickerText.substring(0, Constants.TICKER_TEXT_MAX_LENGH) + Constants.TEXT_POSTFIX;
//                }
                byte[] l2Ticker = null;
                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                    tickerText = FullMutualToHalf.fullToHalf(tickerText);
                    String codeType = "Unicode";
                    if(Build.VERSION.SDK_INT >= 27) {  //Android8.1新特性
                        codeType = "UnicodeLittleUnmarked";
                    }
                    try {
                        l2Ticker = tickerText.getBytes(codeType);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {
                    String codeType = "UTF-8";
                    try {
                        l2Ticker = tickerText.getBytes(codeType);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "处理后的tickerText =" + tickerText);
                byte[] l2Value = null;
                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                    String packName = notificationData.getPackageName();
                    int packNameLength = packName.getBytes().length;
                    String title = notificationData.getTextList()[0];
                    int titleLength = title.getBytes().length;
                    int id = notificationData.getMsgId();
                    long when = notificationData.getWhen();
                    int tickerLength = l2Ticker.length;
                    l2Value = new byte[1 + 1 + packNameLength + 4 + 2 + titleLength + 2 + tickerLength + 8];
                    //packName
                    l2Value[1] = (byte) packNameLength;
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
                }else {
                    l2Value = new byte[l2Ticker.length + 1];
                }
                if (Constants.APP_PACKAGE_NAME_SMS_COMMON.equals(appPackageNameChar) || Constants.APP_PACKAGE_NAME_SMS_NUBIA.equals(appPackageNameChar) || Constants.APP_PACKAGE_NAME_SMS_NUBIA.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_SMS;
                } else if (Constants.APP_PACKAGE_NAME_TEL.equals(appPackageNameChar) || Constants.APP_PACKAGE_NAME_TEL_OTHER.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_INCALL;
                } else if (Constants.APP_PACKAGE_NAME_QQ.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_QQ;   // todo ---  2
                } else if (Constants.APP_PACKAGE_NAME_WECHAT.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_WX;
                } else if (Constants.APP_PACKAGE_NAME_FACEBOOK.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_FACEBOOK;
                } else if (Constants.APP_PACKAGE_NAME_GOOGLE_MESSENGER.equals(appPackageNameChar)||Constants.APP_PACKAGE_NAME_MESSENGER.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_MESSENGER;
                } else if (Constants.APP_PACKAGE_NAME_TWITTER.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_TWITTER;
                } else if (Constants.APP_PACKAGE_NAME_WHATSAPP.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_WHATSAPP;
                } else if (Constants.APP_PACKAGE_NAME_INSTAGRAM.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_INSTAGRAM;
                } else if (Constants.APP_PACKAGE_NAME_LINKEDIN.equals(appPackageNameChar)) {
                    l2Value[0] = BleContants.REMIND_LINKEDIN;
                }else if (appPackageNameChar.contains("line") && "503".equals(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE))){     // TODO --- LINE
                    l2Value[0] = BleContants.REMIND_LINE;
                } else {
                    l2Value[0] = BleContants.REMIND_OTHER;
                }
                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                    L2Send.sendNotifyMsg(l2Value);
                }else {
                    if(l2Ticker.length>1024){
                        System.arraycopy(l2Ticker, 0, l2Value, 1, 1024);
                    }else {
                        System.arraycopy(l2Ticker, 0, l2Value, 1, l2Ticker.length);
                    }
//                    System.arraycopy(l2Ticker, 0, l2Value, 1, l2Ticker.length);
                    L2Send.sendNotifyMsg(l2Value);
                }
            }
        }




    }

    private String[] getNotidicationTextForN(Notification notification,String packName) {
        if (notification == null) {
            Log.e(TAG, "Notification is null to get text");
            return null;
        }
        String[] retArray = null;
        retArray = new String[2];
        if(packName.contains("whatsapp") || packName.contains("linkedin") ){  //todo ---  add 20180314    ||  packName.contains("xiaomi.xmsf")
            retArray[0] = notification.extras.getString("android.title");
            CharSequence[] charSequenceArray = notification.extras.getCharSequenceArray("android.textLines");
            if(charSequenceArray != null && charSequenceArray.length > 0){
                retArray[1] = charSequenceArray[charSequenceArray.length - 1].toString();
            }else{
                retArray[1] = notification.extras.getString("android.text");
            }
        }else {
            retArray[0] = notification.extras.getString("android.title");
            retArray[1] = notification.extras.getString("android.text");
        }
        Log.i(TAG, "[getNotidicationTextForN] Title = " + retArray[0] + ", Content = " + retArray[1]);
        return retArray;
    }
}
