package com.szkct.weloopbtsmartdevice.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.kct.fundo.btnotification.R;
import com.mtk.app.notification.AppList;
import com.szkct.weloopbtsmartdevice.data.WatchInfoData;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointData;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.EcgDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDetailDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.greenrobot.dao.query.Query;

import static android.content.Context.TELEPHONY_SERVICE;

//import com.kct.fundobeta.btnotification.R;

public class Utils {

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static boolean  openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        return  true;
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean notificationIsOpen(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//API19+
            return notificationCheckFor19Up(context);
        }
        return false;
    }
    private static boolean notificationCheckFor19Up(Context context){
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = applicationInfo.uid;
        Class appOpsClass;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW,Integer.TYPE,Integer.TYPE,String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int op = (int) opPostNotificationValue.get(Integer.class);
            return ((int)checkOpNoThrowMethod.invoke(appOpsManager,op,uid,pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }











    public static boolean isActivityRunning(Context mContext,String activityClassName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            ComponentName component = info.get(0).topActivity;
            if(activityClassName.equals(component.getClassName())){
                return true;
            }
        }
        return false;
    }


    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        int size = myList.size();
        for (int i = 0; i < size; i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }





    //是否阿里云平台
    public static boolean isYunOS() {
        String version = null;
        String vmName = null;

        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class});
            version = (String) m.invoke((Object) null, new Object[]{"ro.yunos.version"});
            vmName = (String) m.invoke((Object) null, new Object[]{"java.vm.name"});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vmName != null && vmName.toLowerCase().contains("lemur") || version != null && version.trim().length() > 0;
    }


    // 两次点击按钮之间的点击间隔不能少于1000毫秒解决分享多次弹框问题
    private static final int MIN_CLICK_DELAY_TIME = 2000;
    private static long lastClickTime;
    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }












    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    //判断通知栏是否开启
    public static boolean isEnabled(Context Context) {
        String pkgName = Context.getPackageName();
        final String flat = Settings.Secure.getString(Context.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }}}
        }return false;}


    //判断辅助功能开关
    public static boolean isAccessibilitySettingsOn(Context mContext,Class  CCC) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + CCC.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }
        return false;
    }




 /*   *//**
     * 没有连接网络
     *//*
    private static final int NETWORK_NONE = -1;
    *//**
     * 移动网络
     *//*
    private static final int NETWORK_MOBILE = 0;
    *//**
     * 无线网络
     *//*
    private static final int NETWORK_WIFI = 1;

    public static int isMobileConnected(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }*/


  public  static String  mtype,mtyb;
    /**
     * 获取手机信息
     */
    public static void getPhoneInfo() {
        TelephonyManager mTm = (TelephonyManager) BTNotificationApplication.getInstance().getSystemService(TELEPHONY_SERVICE);
//        String imei = mTm.getDeviceId();
//        String imsi = mTm.getSubscriberId();
        // 手机型号
        mtype = android.os.Build.MODEL;
        mtyb = android.os.Build.BRAND;//手机品牌
//        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        Log.d("lq3", "手机品牌：" + mtyb + " mtype:" + mtype);
    }

    //判断当前辅助服务（服务）是否开启
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);   // 100    Integer.MAX_VALUE
        if (serviceList.size() == 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

//    public static boolean isServiceWorked(Context context, String serviceName) {
//        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
//        for (int i = 0; i < runningService.size(); i++) {
//            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
//                return true;
//            }
//        }
//        return false;
//    }



    public static  boolean isZh(Context tent) {
        Locale locale = tent.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public  static void settingFontsize(View view,int size){
        if(null!=view){
            ((TextView) view).setTextSize(size);
        }
    }


    public  static void settingAllFontsize(List<View> LIST,int size){
        if(null!=LIST){
            for(int i=0;i<LIST.size();i++){
                ((TextView) LIST.get(i)).setTextSize(size);
            }
        }
    }






    // Debugging
    static String ss = Utils.date2string(new Date(), Utils.YYYY_MM_DD_HH_MM_SS);
    static String date = ss.replace(" ", "T") + "Z";
    public final static String start = "<?xml version=\"1.0\" encoding=\"gb2312\" standalone=\"no\" ?>"
            + "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"fundo\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">"
            + "<metadata>"
            + "<link href=\"http://www.fundo.cc\"><text>fundo tec</text></link>"
            + "<time>" + date + "</time>" + "</metadata><trk>";
    public final static String end = "</trk></gpx>";
    private static final String TAG = "AppManager/Util";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    // message id, for both notification and SMS
    private static int sMessageId = 0x9000;
    public static final String FILEPATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "funfit"
            + File.separator;
    public static final String FILENAME = "share.png";
    private static int currLCDWidth = 0;

    private static int currLCDHeight = 0;

    /**
     * Return message id, it is unique for all notification or SMS
     *
     * @return message id
     */
    public static int genMessageId() {
        Log.i(TAG, "genMessageId(), messageId=" + sMessageId);

        return sMessageId++;
    }

    // 判断GPS状态
    public static boolean isGpsEnabled(LocationManager locationManager) {
        boolean isOpenGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // boolean isOpenNetwork = locationManager
        // .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isOpenGPS) {
            return true;
        }
        return false;
    }

    /**
     * @param dateString
     * @param formatStr
     * @return
     * @描述 —— 字符串转换成时间对象
     */
    public static Date string2date(String dateString, String formatStr) {
        Date formateDate = null;
        DateFormat format = Utils.setSimpleDateFormat(formatStr);
        try {
            formateDate = (Date) format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return formateDate;
    }

    /**
     * @param date
     * @return
     * @描述 —— 时间对象转换成字符串
     */
    public static String date2string(java.util.Date date, String formatStr) {
        String strDate = "";
        SimpleDateFormat sdf = Utils.setSimpleDateFormat(formatStr);
        strDate = sdf.format(date);
        return strDate;
    }

    public static final String DATE_POINT_1 = "yyyy.MM.dd HH:mm";
    public static final String DATE_POINT_2 = "dd.MM.yyyy HH:mm";
    public static final String DATE_POINT_11 = "yyyy-MM-dd";
    public static final String DATE_POINT_22 = "dd-MM-yyyy";
    /** 字符串转成日在前 传入的格式为DATE_POINT_1*/
    public static String date2De(String data){
        if(!Utils.isDe()){
            return data;
        }
        String useData = "2018.1.1 12:00";
        try {
            Date tempDate = new SimpleDateFormat(DATE_POINT_1,Locale.ENGLISH).parse(data);
            useData = new SimpleDateFormat(DATE_POINT_2,Locale.ENGLISH).format(tempDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return useData;
    }

    public static String date2De2(String data){
        if(StringUtils.isEmpty(data)){
            return data;
        }
        if(!Utils.isDe()){
            return data;
        }
        String useData = "2018.1.1";
        try {
            Date tempDate = new SimpleDateFormat(DATE_POINT_11,Locale.ENGLISH).parse(data);
            useData = new SimpleDateFormat(DATE_POINT_22,Locale.ENGLISH).format(tempDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return useData;
    }

    // 地图截图
//	public static void takeScreenShot(AMap mAMap, final View v) {
//		mAMap.getMapScreenShot(new OnMapScreenShotListener() {
//			@Override
//			public void onMapScreenShot(Bitmap arg0, int arg1) {
//				
//			}
//			
//			@Override
//			public void onMapScreenShot(Bitmap arg0) {
//				Bitmap firstBitmap = takeScreenShot(v);
//				savePic(mergeBitmap(arg0, firstBitmap));
//			}
//		});
////		.snapshot(new SnapshotReadyCallback() {
////			public void onSnapshotReady(Bitmap snapshot) {
////				Bitmap firstBitmap = takeScreenShot(v);
////				savePic(mergeBitmap(snapshot, firstBitmap));
////			}
////		});
//	}

    /**
     * 保存图片
     */
    public static boolean savePic(Bitmap b,String picName) {
        String sdStatus = Environment.getExternalStorageState();
        FileOutputStream stream = null;
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File path = new File(Utils.FILEPATH);
        File file = new File(Utils.FILEPATH + picName);
        try {
            if (!path.exists()) {
                path.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            stream = new FileOutputStream(file);
            if (null != stream) {
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
                if (null != b) {
                    b.recycle();
                    b = null;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件找不到");
            return false;
        } catch (IOException e) {
            System.out.println("文件写出错");
            return false;
        }
        return true;
    }

    public static boolean savePic(Bitmap b) {
        String sdStatus = Environment.getExternalStorageState();
        FileOutputStream stream = null;
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File path = new File(Utils.FILEPATH);
        File file = new File(Utils.FILEPATH + FILENAME);
        try {
            if (!path.exists()) {
                path.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            stream = new FileOutputStream(file);
            if (null != stream) {
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
                if (null != b) {
                    b.recycle();
                    b = null;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件找不到");
            return false;
        } catch (IOException e) {
            System.out.println("文件写出错");
            return false;
        }
        return true;
    }
    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
    public static Bitmap takeScreenShot(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * 合并图片
     *
     * @param firstBitmap
     * @param secondBitmap
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        canvas.save();
        return bitmap;
    }

    /**
     * 保留F位小数
     *
     * @param f
     * @return
     */
    public static double decimalTo2(double f, int weishu) {
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(weishu, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * 得到配速 （分钟除以千米）
     *
     * @return
     */
    public static String[] getPace(String s, String m) {  // 运动时间  距离
        Double Min = Utils.decimalTo2(Double.valueOf(s) / 60, 2);//10.48 min             得到分钟   （运动时间）     --------- 00:10：29    ----- 0.26KM
        Double Km = 0.00;
        if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance().getApplicationContext(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))){
            Km = Utils.decimalTo2(Double.valueOf(m) / 1000, 2);//km    运动距离    0.26
        }else {
            Km = Utils.decimalTo2(Double.valueOf(m) / 1000 * 0.62, 2);//km    运动距离    0.26
        }
        String peisu = "0.0";

        if (Km == 0) {
            peisu = "0.0";
        } else {
            peisu = String.valueOf(Min / Km);// 分钟/距离  得到配速  将配速转换成分秒     // 40.30769230769231
        }

        String arrPs[] = peisu.split("\\.");    //10.48/ 0.26 =  40.30769230769231
        return arrPs;
    }

    public static double getPaceForWatch1(String s, String m) {  // 运动时间  距离   秒/千米

        Double Min = Double.valueOf(s);//10.48 min    ----TODO  ----  秒
        Double Km = 0.00;
        if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance().getApplicationContext(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))){
            Km = Double.valueOf(m) / 1000;//km    运动距离    0.26
        }else {
            Km = Double.valueOf(m)  / 1000 * 0.62;//km    运动距离    0.26
        }

        double mpeisu =  Min / Km;


       /* float countTime = [model.time integerValue]/[model.kilometer floatValue];
        self.paceLabel.text = [NSString stringWithFormat:@"%0d'%02.0f\"",(int)(countTime/60.0),fmod(countTime,60)]; //model.kilometer

        Double Min = Utils.decimalTo2(Double.valueOf(s) / 60, 2);//10.48 min             得到分钟   （运动时间）     --------- 00:10：29    ----- 0.26KM
        Double Km = 0.00;
        if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance().getApplicationContext(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))){
            Km = Utils.decimalTo2(Double.valueOf(m) / 1000, 2);//km    运动距离    0.26
        }else {
            Km = Utils.decimalTo2(Double.valueOf(m) / 1000 * 0.62, 2);//km    运动距离    0.26
        }
        String peisu = "0.0";

        if (Km == 0) {
            peisu = "0.0";
        } else {
            peisu = String.valueOf(Min / Km);// 分钟/距离  得到配速  将配速转换成分秒     // 40.30769230769231
        }

        String arrPs[] = peisu.split("\\.");    //10.48/ 0.26 =  40.30769230769231
        return arrPs;*/

        return mpeisu;
    }


    /**
     * 公里转英里
     * @param k
     * @return
     */
    public static double getUnit_km(double k){
        return k * 0.621;
    }


    /**
     * 英里转公里
     * @param k
     * @return
     */
    public static double getUnit_km_mi(double k){
        return k / 0.621;
    }

    /**
     * 大卡转千焦
     * @param k
     * @return
     */
    public static double getUnit_kal(double k){
        return k * 4.18675;
    }

    /**
     * 千焦转大卡
     * @param k
     * @return
     */
    public static double getUnit_kal_kj(double k){
        return k / 4.18675;
    }

    /**
     * 米转英尺
     */
    public static double getUnit_mile(double m){
        return m * 3.281;
    }

    /**
     * 厘米转英寸
     * @param m
     * @return
     */
    public static double getUnit_cm(double m){
        return m * 0.394;
    }


    public static int getUnit_pace(double p){
        return (int)  (p / 0.621) ;
//        return (int) (3 * p / 2);
    }



    /**
     * 得到配速 （分钟除以千米）
     *
     * @return
     */
    public static String getPace2(String s, String m) {

        Double Min = Utils.decimalTo2(Double.valueOf(s) / 60, 2);//得到分钟
        Double Km = Utils.decimalTo2(Double.valueOf(m) / 1000, 2);//km
        String peisu = "0.0";
        if (Km == 0) {
            peisu = "0.0";
        } else {
            peisu = String.valueOf(Min / Km);//得到配速  将配速转换成分秒
        }


        String peisuok = Utils.setformat(2, peisu);

//        String[] psStr = Utils.setformat(2, peisu).split("\\.");  // 0 --- 43925235
//        int fen  = Integer.valueOf(psStr[0]);
//        int miao = Integer.valueOf(psStr[1].substring(0, 2));
//        int miaook = miao*(60)/100;
//        String peisuok = String.format("%1$02d'%2$02d''", Integer.valueOf(psStr[0]), Integer.valueOf(psStr[1].substring(0, 2)) * (60)/100);
//        String peisuok = String.format("%1$02d%2$02d", Integer.valueOf(psStr[0]), Integer.valueOf(psStr[1].substring(0, 2)) * (60)/100);  // 只能 配置为   ----  3.37  不可配置为 00'24''
        return peisuok;
    }


    public static String getKeyFromValue(CharSequence charSequence) {
        Map<Object, Object> appList = AppList.getInstance().getAppList();
        Set<?> set = appList.entrySet();
        Iterator<?> it = set.iterator();
        String key = null;
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() != null
                    && entry.getValue().equals(charSequence)) {
                key = entry.getKey().toString();
                break;
            }
        }
        return key;
    }

    private static int old_dir = 0;

    /**
     * GPS偏移
     *
     * @param newDir 新角度
     * @return
     */
    public static boolean dirFilter(int newDir) {
        int dir_off = newDir - old_dir;
        if ((dir_off > 90) || (dir_off < -90)) {
            old_dir = newDir;
            return false;
        }
        old_dir = newDir;
        return true;
    }

    /**
     * Returns whether the application is system application.
     *
     * @param appInfo
     * @return Return true, if the application is system application, otherwise,
     * return false.
     */
    public static boolean isSystemApp(ApplicationInfo appInfo) {
        boolean isSystemApp = false;
        if (((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                || ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
            isSystemApp = true;
        }
        return isSystemApp;
    }

    /**
     * Returns whether the mobile phone screen is locked.
     *
     * @param context
     * @return Return true, if screen is locked, otherwise, return false.
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager km = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        Boolean isScreenLocked = km.inKeyguardRestrictedInputMode();

        Log.i(TAG, "isScreenOn(), isScreenOn=" + isScreenLocked);
        return isScreenLocked;
    }

    /**
     * Returns whether the mobile phone screen is currently on.
     *
     * @param context
     * @return Return true, if screen is on, otherwise, return false.
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        Boolean isScreenOn = pm.isScreenOn();

        Log.i(TAG, "isScreenOn(), isScreenOn=" + isScreenOn);
        return isScreenOn;
    }

    /**
     * Lookup contact name from phonebook by phone number.
     *
     * @param context
     * @param phoneNum
     * @return the contact name
     */
    public static String getContactName(Context context, String phoneNum) {
        // Lookup contactName from phonebook by phoneNum
        if (phoneNum == null) {
            return null;
        } else if (phoneNum.equals("")) {
            return null;
        } else {
            String contactName = phoneNum;
            try {
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(contactName));
                Cursor cursor = context.getContentResolver().query(uri,
                        new String[]{"display_name"}, null, null, null);
                if ((cursor != null) && cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                }
                cursor.close();
                Log.i(TAG, "getContactName(), contactName=" + contactName);
                return contactName;
            } catch (Exception e) {
                Log.i(TAG, "getContactName Exception");
                return contactName;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static long getAvailableStore(String filePath) {
        // get sdcard path
        StatFs statFs = new StatFs(filePath);
        // get block SIZE
        long blockSize = statFs.getBlockSize();
        // getBLOCK numbers
        // long totalBlocks = statFs.getBlockCount();
        // get available Blocks
        long availaBlock = statFs.getAvailableBlocks();
        // long total = totalBlocks * blocSize;
        long availableSpace = availaBlock * blockSize;
        return availableSpace / 1024;
    }

    /**
     * Get the current date in "yyyy-MM-dd HH:mm:ss" format. chendalin add
     *
     * @return the formatted date string
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormatedDate() {
        // Date format: 2013-05-24 16:00:00
        SimpleDateFormat dateFormat = Utils.setSimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date(System.currentTimeMillis()));

        // Log.i(LOG_TAG, "getFormatedDate(), date=" + date);
        return date;
    }

    public static boolean isTaskRunning(AsyncTask task) {
        return task != null
                && (task.getStatus() == AsyncTask.Status.PENDING || task
                .getStatus() == AsyncTask.Status.RUNNING);
    }

    public static void setCurrWidth(int width) {
        currLCDWidth = width;
    }

    public static void setCurrHeight(int height) {
        currLCDHeight = height;
    }

    public static int getCurrWidth() {
        return currLCDWidth;
    }

    public static int getCurrHeight() {
        return currLCDHeight;
    }

	/*
	 * public static String date2string(java.util.Date date, String
	 * yyyyMmDdHhMmSs) { // TODO Auto-generated method stub return null; }
	 */

    /**
     * 获取系统默认语言 chendalin add
     *
     * @return
     */
    public static String getLanguage() {
        // 获取系统当前使用的语言
        Locale locale = Locale.getDefault();  // zh_HK  --- zh_TW
        String language = locale.getLanguage(); // zh
        return language;
    }

    /** 判断是不是德语 */
    public static boolean isDe() {
        if (Utils.getLanguage().equals("de") || Utils.getLanguage().equals("fr") || Utils.getLanguage().equals("en") || Utils.getLanguage().equals("es") || Utils.getLanguage().equals("it") || Utils.getLanguage().equals("pl") || Utils.getLanguage().equals("pt")
                || Utils.getLanguage().equals("ru") || Utils.getLanguage().equals("tr") || Utils.getLanguage().equals("ar")) {
            return true;
        } else {
            return false;
        }
    }

    public static String dateInversion(String data) {  // 2018.09.03 17:26    /// ---    03 17:26.09.2018
        if (data == null || data.contains("-") && data.split("-").length < 3) {
            return "2018-1-1";
        }
        if(data.contains("-")){
            String[] dataArray = data.split("-");
            if(dataArray[2].contains(" ")){
                String[]  dayTimeArray = dataArray[2].split(" ");
                return   dayTimeArray[0] + "-" + dataArray[1] + "-" + dataArray[0] + " "+dayTimeArray[1] ;
            }else {
                return   dataArray[2] + "-" + dataArray[1] + "-" + dataArray[0]  ;
            }
        }else if(data.contains(".")){
            String[] dataArray = data.split("\\.");

            if(dataArray[2].contains(" ")){
                String[] dd = dataArray[2].split(" ");
                return dd[0] + "-" + dataArray[1] + "-" + dataArray[0]  + " " +  dd[1] ;  // dataArray[2] = {String@7072} "03 17:26"
            }else {
                return dataArray[2] + "-" + dataArray[1] + "-" + dataArray[0];  // dataArray[2] = {String@7072} "03 17:26"
            }
            //            return dataArray[2] + "." + dataArray[1] + "." + dataArray[0];  // dataArray[2] = {String@7072} "03 17:26"
        }
        //        String[] dataArray = data.split("-");
        //        return dataArray[2] + "-" + dataArray[1] + "-" + dataArray[0];
        return "2018-1-1";
    }

    /**
     * 获取系统默认语言 chendalin add
     *
     * @return
     */
    public static String getCountry() {
        // 获取系统当前使用的语言
        Locale locale = Locale.getDefault();  // zh_HK   ---- zh_TW
        String country = locale.getCountry(); // HK     ---- TW
        return country;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static final double EARTH_RADIUS = 6378137.0;

    // 返回单位是米 2点之间的距离
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static String getDir(double lat_des, double lon_des, double lat_mys,
                                double lon_mys, Context context) {
        String str_dir = "";
        double y = getDistance(lat_des, lon_mys, lat_mys, lon_mys);// 直角边1
        double x = getDistance(lat_des, lon_des, lat_des, lon_mys);// 直角边2
        double dir = (Math.atan(y / x) / Math.PI) * 180;// 角度
        // 转换为两位数的km
        BigDecimal disDecimal = new BigDecimal(dir);
        disDecimal = disDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        if (lat_des > lat_mys) {
            str_dir = (lon_des > lon_mys ? context
                    .getString(R.string.northeast) : context
                    .getString(R.string.northwest));
        } else {
            str_dir = (lon_des > lon_mys ? context
                    .getString(R.string.southeast) : context
                    .getString(R.string.southwest));
        }
        return str_dir + disDecimal + "D";
    }

    public static long DateCompare(String datetime1, String datetime2,
                                   String format) {
        long timecha = -1;
        try {
            // 设定时间的模板
            SimpleDateFormat Simformat = Utils.setSimpleDateFormat(format);
            // 得到指定模范的时间
            Date d1 = Simformat.parse(datetime1);
            Date d2 = Simformat.parse(datetime2);
            timecha = (d1.getTime() - d2.getTime()) / 1000;
            // 比较
            if (timecha > 0) {
                Log.e("date result", "大于0");
            } else {
                Log.e("date result", "小于0");
            }
        } catch (ParseException e) {
            Log.e("时间比较异常了", e.getMessage());
            e.printStackTrace();
        }
        return timecha;
    }

    /**
     * 检测是否是手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNo(String mobiles) {
        Pattern p = Pattern
                .compile("^[1](3[0-9]|5[012356789]|7[678]|8[0-9]|4[57])[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static int dip2pixel(Context paramContext, float paramFloat) {
        return (int) TypedValue.applyDimension(1, paramFloat, paramContext
                .getResources().getDisplayMetrics());
    }

    public static String replace(String strSource, String strFrom, String strTo) {
        // 如果要替换的子串为空，则直接返回源串
        if (strFrom == null || strFrom.equals(""))
            return strSource;
        String strDest = "";
        // 要替换的子串长度
        int intFromLen = strFrom.length();
        int intPos;
        // 循环替换字符串
        while ((intPos = strSource.indexOf(strFrom)) != -1) {
            // 获取匹配字符串的左边子串
            strDest = strDest + strSource.substring(0, intPos);
            // 加上替换后的子串
            strDest = strDest + strTo;
            // 修改源串为匹配子串后的子串
            strSource = strSource.substring(intPos + intFromLen);
        }
        // 加上没有匹配的子串
        strDest = strDest + strSource;
        // 返回
        return strDest;
    }

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float dip2pxfloat(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int toint(String z) {
        int a = 0;
        try {
            a = Integer.parseInt(z);
        } catch (Exception e) {
        }
        return a;

    }

   /* public static int toint(String z, int x) {
        int a = x;
        try {
            a = Integer.parseInt(z);
        } catch (Exception e) {
        }
        return x;
    }*/

    public static int toint(String z, int x) {
        int a = x;
        try {
            a = Integer.parseInt(z);
        } catch (Exception e) {
        }
        return a;
    }

    public static Float tofloat(String z) {
        Float a = (float) 0;
        try {
            a = Float.parseFloat(z);
        } catch (Exception e) {
        }
        return a;

    }

    public static Long tolong(String z) {
        Long a = (long) 0;
        try {
            a = Long.parseLong(z);
        } catch (Exception e) {
        }
        return a;

    }

    public static Double toDouble(String z) {
        Double a = 0.0;
        try {
            a = Double.parseDouble(z);
        } catch (Exception e) {
        }
        return a;

    }

    //获得系统时区
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String createGmtOffsetString(boolean includeGmt,
                                               boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    /*
    public static boolean isTopActivity(Context context,String packagename) {  
        String packageName =packagename;  
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);  
        if (tasksInfo.size() > 0) {  
            System.out.println("---------------包名-----------" + tasksInfo.get(0).topActivity.getPackageName());  
            // 应用程序位于堆栈的顶层  
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {  
                return true;  
            }  
        }  
        return false;  
    } */
    public static boolean isTopActivity(Context context, String packagename) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(20);
        boolean isAppRunning = false;
        String MY_PKG_NAME = packagename;
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                Log.i(TAG, info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                break;
            }
        }
        return isAppRunning;
    }

    public static void doStartApplicationWithPackageName(Context context, String packagename, PendingIntent pendingIntent) {
        if (isTopActivity(context, packagename)) {

            return;
        }

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等  
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent  
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历  
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname  
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]  
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent  
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            Log.e("startActivity", "className" + className + "packageName" + packageName);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径  
            ComponentName cn = new ComponentName(packageName, className);
            // PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
            intent.setComponent(cn);
            //  context.startActivity(intent);
            try {
                pendingIntent.send(context, 1234321, intent);
            } catch (CanceledException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    public static int getMaxLengthForTextView(TextView textView) {
        int maxLength = -1;

        for (InputFilter filter : textView.getFilters()) {
            if (filter instanceof InputFilter.LengthFilter) {
                try {
                    Field maxLengthField = filter.getClass().getDeclaredField("mMax");
                    maxLengthField.setAccessible(true);

                    if (maxLengthField.isAccessible()) {
                        maxLength = maxLengthField.getInt(filter);
                    }
                } catch (IllegalAccessException e) {
                    Log.w(filter.getClass().getName(), e);
                } catch (IllegalArgumentException e) {
                    Log.w(filter.getClass().getName(), e);
                } catch (NoSuchFieldException e) {
                    Log.w(filter.getClass().getName(), e);
                } // if an Exception is thrown, Log it and return -1
            }
        }

        return maxLength;
    }

    public static String setformat(int l, String s) {
        return String.format(Locale.ENGLISH, "%." + l + "f", Utils.tofloat(s));
    }

    public static String setformat(int l, float f) {
        return String.format(Locale.ENGLISH, "%." + l + "f", f);
    }

    public static SimpleDateFormat setSimpleDateFormat(String dataformat) {

        return new SimpleDateFormat(dataformat, Locale.ENGLISH);

    }

    //格式化配速
    public static String getPeisu(String ps) {

        String arrPs[] = ps.split("\\.");
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        String peisu = String.format(Locale.ENGLISH,"%1$02'%2$02d''", Utils.toint(m), (int) sec);
        return peisu;
    }


    //格式化配速
    public static String getPeisu(long ps) {
        String m = ps / 60 + "";//分
        int sec = (int) (ps % 60);//秒数
        String peisu = String.format(Locale.ENGLISH,"%1$02d'%2$02d''",  Utils.toint(m), sec);
        return peisu;
    }


    public static String getcalorie(float distance, Context context) {
//        double distance1 = step * 0.7 * 0.001;
        double okDistance = new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();   // todo --- 手表的数据是按此方法，手机端也必须按此方法（否则卡路里对应不上）
//        double cal1  = 60 * okDistance * 1.036;
        double cal1  = Utils.tofloat(getweight(context)) * okDistance * 1.036;
        double mCal = new BigDecimal(cal1).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        String returnstring=distance*Utils.tofloat(getweight(context))*1.036+"";
        return  mCal + "";

//		String returnstring=distance*Utils.tofloat(getweight(context))*1.036+"";
//		return  returnstring;
	}
	static String getweight(Context context){
		if (SharedPreUtil.readPre(context, SharedPreUtil.USER,
				SharedPreUtil.METRIC, SharedPreUtil.YES).equals(SharedPreUtil.YES)) {

			return  SharedPreUtil.readPre(context, SharedPreUtil.USER,
					SharedPreUtil.WEIGHT, "60");
		} else {
			String weight_en = SharedPreUtil.readPre(context, SharedPreUtil.USER,
					SharedPreUtil.WEIGHT_US, "120");
			int weight_cn = (int) (Utils.tofloat(weight_en) / 2.2);
			return weight_cn + "";
		}
	}
    public static String gethight(Context context){
		if (SharedPreUtil.readPre(context, SharedPreUtil.USER,
				SharedPreUtil.METRIC, SharedPreUtil.YES).equals(SharedPreUtil.YES)) {
			return SharedPreUtil.readPre(context, SharedPreUtil.USER,
					SharedPreUtil.HEIGHT, "170");

		} else {



			String height_in = SharedPreUtil.readPre(context, SharedPreUtil.USER,
					SharedPreUtil.HEIGHT_IN, "0");
			String height_ft = SharedPreUtil.readPre(context, SharedPreUtil.USER,
					SharedPreUtil.HEIGHT_FT, "6");
			int in = Utils.toint(height_ft) * 12 + Utils.toint(height_in);
			int height_cn = (int) (in * 2.54);
			return height_cn + "";


		}
	}
	public static  void josnjps(final String josnstring,final Context context) {   //   TODO --- 保存运动模式的数据
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					JSONObject jsonObject = (JSONObject) new JSONTokener(josnstring.trim()).nextValue();

                    List<GpsPointDetailData> gpspointdetailList = new ArrayList<GpsPointDetailData>();// gps详情数据    ----- 基础数据

                    ArrayList<GpsPointData> gpsPointDataList = new ArrayList<GpsPointData>();// 零时记录点集合   ----- 轨迹数据

                    JSONArray heartRate_velocity_stepSpeedArray = jsonObject.getJSONArray("heartRate_velocity_stepSpeed");   // 心率，速度，步频

                    JSONArray altitudeArray = jsonObject.getJSONArray("trace");    // 轨迹数据

                    Map<String, String> kmpacemap = new HashMap<String, String>();  //   配速的集合

                    Map<String, String> speedmap = new HashMap<String, String>();   // 速度的集合
                    Map<String, String> step_speedmap = new HashMap<String, String>();  // 步频的集合
                    Map<String, String> heart_ratemap = new HashMap<String, String>();   // 心率的集合

                    Map<String, String> altitudemap = new HashMap<String, String>();   // // 海拔值的集合

                    JSONArray step_count_detailArray = jsonObject.getJSONArray("step_count_detail");  //   配速数据
                    ArrayList<Long> sporttime = new ArrayList<Long>();// 运动时间
                    for (int s = 0; s < step_count_detailArray.length(); s++) {
                        JSONObject step_count_detailJsonObject = step_count_detailArray.getJSONObject(s);
                        sporttime.add(Utils.tolong(step_count_detailJsonObject.get("get_time").toString()));

                    }
                    Collections.sort(sporttime);  // 排序运动时间
                    String altitude = "";
                    String latitude = "";
                    String longitude = "";
                    for (int s = 0; s < altitudeArray.length(); s++) {
                        JSONObject altitudeJsonObject = altitudeArray.getJSONObject(s);
                        String get_time = getgettime(sporttime, altitudeJsonObject.get("get_time").toString());  // 轨迹数据的时间
                        // 轨迹数据
                        GpsPointData gpsPointData = new GpsPointData(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MYMAC), "", altitudeJsonObject.get("Latitude").toString(),
                                altitudeJsonObject.get("Longitude").toString(), "", "", altitudeJsonObject.get("altitude").toString(), get_time, "");
                        gpsPointDataList.add(gpsPointData);  // 轨迹数据的集合
                        latitude = altitudeJsonObject.get("Latitude").toString();  // 纬度
                        longitude = altitudeJsonObject.get("Longitude").toString(); //  经度
                        latitude = latitude + "&";
                        longitude = longitude + "&";

                        altitude = altitudeJsonObject.get("altitude").toString();  // 海拔
                        if (altitudemap.get(get_time) == null) {
                            altitudemap.put(get_time, altitudeJsonObject.get("altitude").toString());  // 海拔值的集合
                        } else {
                            altitudemap.put(get_time, altitudemap.get(get_time) + "&" + altitudeJsonObject.get("altitude").toString());
                        }
                    }

                    JSONArray paceArray = jsonObject.getJSONArray("pace");     //   配速数据
                    for (int s = 0; s < paceArray.length(); s++) {
                        JSONObject paceJsonObject = paceArray.getJSONObject(s);
                        String get_time = getgettime(sporttime, paceJsonObject.get("get_time").toString());
                        if (kmpacemap.get(get_time) == null) {   // //   配速的集合
                            kmpacemap.put(get_time, paceJsonObject.get("space").toString());
                        } else {
                            kmpacemap.put(get_time, kmpacemap.get(get_time) + "&" + paceJsonObject.get("space").toString());
                        }
                    }

                    for (int s = 0; s < heartRate_velocity_stepSpeedArray.length(); s++) {
                        JSONObject heartRate_velocity_stepSpeedJsonObject = heartRate_velocity_stepSpeedArray.getJSONObject(s);  //  // 心率，速度，步频
                        String get_time = getgettime(sporttime, heartRate_velocity_stepSpeedJsonObject.get("get_time").toString());  // 心率，速度，步频的时间
                        if (speedmap.get(get_time) == null) {
                            speedmap.put(get_time, heartRate_velocity_stepSpeedJsonObject.get("speed").toString());  // 速度的集合
                            step_speedmap.put(get_time, heartRate_velocity_stepSpeedJsonObject.get("step_speed").toString());// 步频的集合
                            heart_ratemap.put(get_time, heartRate_velocity_stepSpeedJsonObject.get("heart_rate").toString());  // 心率的集合
                        } else {
                            speedmap.put(get_time, speedmap.get(get_time) + "&" + heartRate_velocity_stepSpeedJsonObject.get("speed").toString());
                            step_speedmap.put(get_time, step_speedmap.get(get_time) + "&" + heartRate_velocity_stepSpeedJsonObject.get("step_speed").toString());
                            heart_ratemap.put(get_time, heart_ratemap.get(get_time) + "&" + heartRate_velocity_stepSpeedJsonObject.get("heart_rate").toString());
                        }
                    }
                    for (int s = 0; s < step_count_detailArray.length(); s++) {  // 基础数据
                        JSONObject step_count_detailJsonObject = step_count_detailArray.getJSONObject(s);
                        GpsPointDetailData gpspointdetaildata = new GpsPointDetailData();
                        gpspointdetaildata.setDeviceType("2");   // 设备类型
                        gpspointdetaildata.setMin_step_width(step_count_detailJsonObject.get("min_step_width").toString());  // 最小步幅
                        gpspointdetaildata.setMax_step_width(step_count_detailJsonObject.get("max_step_width").toString());  // 最大步幅
                        gpspointdetaildata.setAve_step_width(step_count_detailJsonObject.get("ave_step_width").toString());  // 平均步幅
                        gpspointdetaildata.setMac(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MYMAC));
                        gpspointdetaildata.setMid("");
                        long time = Utils.tolong(step_count_detailJsonObject.get("time_take").toString());
                        gpspointdetaildata.setSportTime(String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", time / 60 / 60, time / 60 % 60, time % 60));
                        gpspointdetaildata.setArrLat(latitude);
                        gpspointdetaildata.setArrLng(longitude);
                        gpspointdetaildata.setmCurrentSpeed("");
                        gpspointdetaildata.setArrTotalSpeed("");
                        gpspointdetaildata.setCalorie(getcalorie(tolong(step_count_detailJsonObject.get("distance").toString()), context));
                        gpspointdetaildata.setDate(date2string(new Date(tolong(step_count_detailJsonObject.get("get_time").toString())), Utils.YYYY_MM_DD_HH_MM));
                        gpspointdetaildata.setMile(toDouble(step_count_detailJsonObject.get("distance").toString()));
// (时间 + 运动类型 + 心率 + 暂停时长 + 暂停次数 + 里程 + 最大步幅 + 最小步幅 + 平均步幅 + 运动时间)
                        gpspointdetaildata.setTimeMillis(step_count_detailJsonObject.get("get_time").toString());   // 运动时间
                        gpspointdetaildata.setHeartRate(step_count_detailJsonObject.get("heart_rate").toString()); // 心率
                        gpspointdetaildata.setPauseTime(step_count_detailJsonObject.get("pause_time").toString());  // 暂停时间
                        gpspointdetaildata.setPauseNumber(step_count_detailJsonObject.get("pause_times").toString());    // 暂停次数
                        gpspointdetaildata.setsTime(step_count_detailJsonObject.get("time_take").toString());

                        gpspointdetaildata.setSportType(toint(step_count_detailJsonObject.get("item_id").toString())+1+""); // 运动类型

                        gpspointdetaildata.setArrspeed(speedmap.get(step_count_detailJsonObject.get("get_time").toString()));
                        gpspointdetaildata.setArrcadence(step_speedmap.get(step_count_detailJsonObject.get("get_time").toString()));
                        gpspointdetaildata.setArrheartRate(heart_ratemap.get(step_count_detailJsonObject.get("get_time").toString()));
                        Log.e("rq", speedmap.get(step_count_detailJsonObject.get("get_time").toString()) + "===" + step_count_detailJsonObject.get("get_time").toString());
                       if(kmpacemap.get(step_count_detailJsonObject.get("get_time"))==null){
                           gpspointdetaildata.setSpeed("");
                       }else{
                           gpspointdetaildata.setSpeed(kmpacemap.get(step_count_detailJsonObject.get("get_time").toString()));  // 设置配速
                       }
                        if(altitudemap.get(step_count_detailJsonObject.get("get_time"))==null){
                            gpspointdetaildata.setArraltitude("0");
                        } else {
                            gpspointdetaildata.setArraltitude(altitudemap.get(step_count_detailJsonObject.get("get_time").toString()));  // 设置海拔
                        }
                        gpspointdetaildata.setAltitude(altitude);
                        gpspointdetailList.add(gpspointdetaildata);
                    }
                    recordGpsPointForDataBase(gpspointdetailList, gpsPointDataList, context);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

   public static String getgettime(ArrayList<Long> xxx, String get_time) {  // 比较时间戳
       String restring = get_time;
       for (int s = 0; s < xxx.size(); s++) {
           if (Utils.tolong(restring) <= xxx.get(s)) {
               return xxx.get(s) + "";
           }
       }
       return restring;
   }

    /**
     * 记录数据库 gps运动数据
     */

    public static void recordGpsPointForDataBase(List<GpsPointDetailData> gpsDeatils, ArrayList<GpsPointData> gpsPointDataList, Context context) {
        if (gpsDeatils != null)// 写经纬度
        {
            /**写入实时数据到数据库**/
            DBHelper db = DBHelper.getInstance(context);
            for (int i = 0; i < gpsDeatils.size(); i++) {
//                Log.e("rq", "gpsDeatils.size=" + gpsDeatils.size());
                GpsPointDetailData dd = gpsDeatils.get(i);  // gpsDeatils  为 2 时 ， 保存到数据库 出错    id必须为null ，
                db.saveGpsPointDeatilData(gpsDeatils.get(i));  // crash
            }

            if (gpsDeatils.size() != 0) {
                String mid = SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MID);  // 用户id
                for (GpsPointDetailData mDetailData : gpsDeatils) {

                    String date = mDetailData.getTimeMillis();
                    String sportType = mDetailData.getSportType();

                    Query query = null;
                    query = db.getGpsPointDetailDao().queryBuilder().where(GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MAC)))
                            .where(GpsPointDetailDao.Properties.TimeMillis.eq(date))
                            .where(GpsPointDetailDao.Properties.SportType.eq(sportType)).build();
                    List list1 = query.list();
                    if (list1.size() == 0) {
                        db.saveGpsPointDeatilData(mDetailData);
                    } else {
                        db.updataGpsPointDetailData(mDetailData);
                    }
                }


                if (gpsPointDataList != null) {
                    for (int i = 0; i < gpsPointDataList.size(); i++) {
//                    Log.e("rq", "gpsPointDataList.size=" + gpsPointDataList.size());
                    GpsPointData tt = gpsPointDataList.get(i);      // id必须为null ，
                    db.saveGpsPointData(gpsPointDataList.get(i));    //crash
                }
            }
            SharedPreUtil.savePre(context,SharedPreUtil.SPORT_BT,SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MAC),System.currentTimeMillis()+"");  // 运动模式 的数据 保存到本地 数据库 时，保存当前的系统时间，作为下一次 ，获取数据的起点
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // 发广播，运动模式数据 同步成功    ---- 保存3次，发3个广播
            context.sendBroadcast(broadcastIntent);
            //写入数据库成功后 计时结束暂停服
//            stopSelf();// 停止服务
            }
        }
	}
//	/**
//	 *是否支持谷歌地图
//	 */
//	public static boolean getisgooglemap(){
//		try {
//			Class.forName("com.google.android.maps.MapActivity");
//		} catch (Exception e) {
//
//			return false;
//		}
//		return true;
//	}

    public static  boolean getisgooglemap(Context context){
       boolean googleserviceFlag =true;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
//            if(googleApiAvailability.isUserResolvableError(resultCode))
//            {
////                googleApiAvailability.getErrorDialog((Activity) context,
////                        resultCode, 2404).show();
//            }
            googleserviceFlag=false;
        }

        return  googleserviceFlag;
    }

    /**
     * 连接两个byte数组
     */
    public static byte[] arraycat(byte[] buf1, byte[] buf2) {
        byte[] bufret = null;
        int len1 = 0;
        int len2 = 0;
        if (buf1 != null)
            len1 = buf1.length;
        if (buf2 != null)
            len2 = buf2.length;
        if (len1 + len2 > 0)
            bufret = new byte[len1 + len2];
        if (len1 > 0)
            System.arraycopy(buf1, 0, bufret, 0, len1);
        if(len2>0)
            System.arraycopy(buf2, 0, bufret, len1, len2);
        return bufret;
    }

    /**
     * 把字节数组转换成16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        if(bArray == null)
            return null;
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static byte crcSum(byte[] buffer) {
        byte crc = 0;
        for (int i = 0; i < buffer.length; i++) {
            crc += buffer[i];
        }
        return crc;
    }


    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 0] << 24)) |
                (0xff0000 & (arr[index + 1] << 16)) |
                (0xff00 & (arr[index + 2] << 8)) |
                (0xff & arr[index + 3]);
    }


    public static float getFloat(byte[] arr, int index) {
        return Float.intBitsToFloat(getInt(arr, index));
    }

    public static byte getFbyte(String r) {
        int b  = 0;
        char[]c = r.toCharArray();
        for (int i = 0 ;i<c.length;i++){
            if (c[i] == '1'){
                if(i == 0){
                    b +=  1;
                }
                if(i == 1){
                    b +=  2;
                }
                if(i == 2){
                    b +=  4;
                }
                if(i == 3){
                    b +=  8;
                }
                if(i == 4){
                    b +=  16;
                }
                if(i == 5){
                    b +=  32;
                }
                if(i == 6){
                    b +=  64;
                }
//                b += (int) Math.pow(2,i);
            }
        }
        return (byte) b;
    }

    public static String getFrequency(Context context, String f) {  // 00111111
        f = f.substring(0, 7);
        char[] c = f.toCharArray();
        String [] ARRAY_FRUIT=context.getResources().getStringArray(R.array.day_of_week_forx2);  // day_of_week
        StringBuffer sb = new StringBuffer();
        if (f.equals("0000000")) {
            sb.append(context.getString(R.string.one_time_alert));
        } else if (f.equals("1111111")){
            sb.append(context.getString(R.string.every_day));
        } else {
            for (int i = 0; i < f.length(); i++) {
                if (c[i] == '1'){
                    if(sb.toString().length()>1){
                        sb.append("、" + ARRAY_FRUIT[i]);
                    }else {
                        sb.append(ARRAY_FRUIT[i]);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static int getblank(String s) {
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++){
            if (c[i] == '0'){
                return i;
            }
        }
        return 0;
    }

    public static boolean getTBState(String f) {
        return f.equals("1");
    }

    public static List<String> getTimeList(boolean isHalf) {
        List<String> list = new ArrayList();
        for (int i = 0; i < 24; i++) {
            list.add(String.format(Locale.ENGLISH,"%02d" + ":00", i));
            if(isHalf) {
                list.add(String.format(Locale.ENGLISH,"%02d" + ":30", i));
            }
        }
        return list;
    }

    public static List<String> getSitList() {
        List<String> list = new ArrayList();
        for (int i = 30; i <= 360; i += 30) {
            list.add(i + "");
        }
        return list;
    }


    public static List<String> getStepList() {
        List<String> list = new ArrayList();
        for (int i = 100; i <= 1000; i += 100) {
            list.add(i + "");
        }
        return list;
    }

    public static List<String> getHeartList() {
        List<String> list = new ArrayList();
        for (int i = 1; i < 7; i++) {
            list.add(i * 10 + "");
        }
        return list;
    }

    public static List<String> getDrinkList() {
        List<String> list = new ArrayList();
        for (int i = 1; i < 7; i++) {
            list.add(i * 30 + "");
        }
        return list;
    }

    public static byte getByteCycle(String s) {
        byte cycle = 0;
        for (int i = 0; i < s.toCharArray().length; i++) {
            cycle |= (s.toCharArray()[i] << (i));
        }
        return cycle;
    }

    public static String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }

    /**
     * 把byte转化成2进制字符串
     * @param b
     * @return
     */
    public static String getBinaryStrFromByte(byte b){
        String result ="";
        byte a = b; ;
        for (int i = 0; i < 8; i++){
            byte c=a;
            a=(byte)(a>>1);//每移一位如同将10进制数除以2并去掉余数。
            a=(byte)(a<<1);
            if(a==c){
                result="0"+result;
            }else{
                result="1"+result;
            }
            a=(byte)(a>>1);
        }

        String  okStr = reverse(result);   //b=32   result ---  00100000   okStr ==== 00000100    ---- 对应 周6
        return okStr;
//        return reverse(result);   //byte b =  32  ---- 00100000     将8位的字符串翻转
    }


    /**
     * 字符串转换成统时间.
     *
     * @param formatStr 协议中的时间串.
     * @param format    : 字符格式
     *
     * @return ： 时间
     */
    public static Date getDateByFormatStr(String formatStr, String format) {
        Date dt = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            dt = sdf.parse(formatStr);
        } catch (ParseException e) {

        }
        return dt;
    }

    /**
     * 日期格式化为字符串
     *
     * @param date      系统时间
     * @param formatStr 格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     *
     * @return 格式化后的时间串.
     */
    public static String getStringByDate(Date date, String formatStr) {

        DateFormat format = new SimpleDateFormat(formatStr);
        String str = format.format(date);
        return str;
    }

    public static boolean isSameDayDates(Date selectDate, Date currentDate) {
        int sub = checkDateValid(selectDate, currentDate);
        return (sub == 0);
    }

    //比较两个日期的的差距
    private static int checkDateValid(Date selectDate, Date currentDate) {
        int sub = 0;
        try {
            sub = getDaysBetween(selectDate, currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sub;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smallDate 较小的时间
     * @param bigDate   较大的时间
     *
     * @return 相差天数
     *
     * @throws ParseException
     */
    public static int getDaysBetween(Date smallDate, Date bigDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        smallDate = sdf.parse(sdf.format(smallDate));
        bigDate = sdf.parse(sdf.format(bigDate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smallDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bigDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 将字符串转换为十六进制格式
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String ftTometri(int ft,int in){
        return  setformat(0,((ft*12+in)*2.54)+"");
    }


    public static String metricToInchForft(float metric) {    //公制转英制 ft
        double ft;
        float inch = (float) (metric / 2.54);
        ft = (inch / 12);  //英尺
        long zheng = (long) ft;
        long xiao = Math.round((ft - zheng) * 10);
        return zheng+"";
    }

    public static String metricToInchForin(float metric) {    //公制转英制 in
        double ft;
        float inch = (float) (metric / 2.54);
        ft = (inch / 12);  //英尺
        long zheng = (long) ft;
        long xiao = Math.round((ft - zheng) * 10);
        return (xiao * 12) / 10+"";
    }


    public static String kgTolb(float kg) {
        return setformat(0,(2.2 * kg)+"");
    }

    public static String lbTokg(float lb) {
        return setformat(0,(lb / 2.2)+"");
    }

    public static float klToMile(float kl) {
        return (float) (kl * 0.621);
    }

    public static float mileToKl(float mile){
        return (float)(mile/0.621);
    }

    public static float mToft(float m){
        return (float)(m*3.28);
    }

    public static String ftTom(float ft){
        return getDecimal((ft / 3.28), 0);
    }

    public static String getDecimal(Object object, int decNum) {

        StringBuilder builder = new StringBuilder();

        builder.append("#0.");
        for (int i = 0; i < decNum; i++) {
            builder.append("0");
        }
//		LogUtil.e("format--->>"+builder.toString());
        DecimalFormat decimalFormat = new DecimalFormat(builder.toString());
        String formatText = decimalFormat.format(object);
        return formatText;
    }



    //小写转大写
    public static String exChange2(String string){   //蓝牙地址转大写
        StringBuilder sb=new StringBuilder();
        if (string.equals("")||string.length()>0){
            for (int i=0;i<string.length();i++){
                char c=string.charAt(i);
                if (Character.isLowerCase(c)){
                    sb.append(Character.toUpperCase(c));
                }else{
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }


    /**
     * 以下是根据一个已知的电话号码，从通讯录中获取相对应的联系人姓名的代码
     *
     * @param context  上下文
     * @param phoneNum 电话号码
     * @return
     */
    public static String getContactNameFromPhoneBook(Context context, String phoneNum) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER };
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNum));
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        String contactName = "";
        if(null != cursor && cursor.moveToFirst()){//没有获取到手机读取联系人信息时，cursor为空值
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        }
        return contactName;
    }

    public static void onClickShareToQQ(Context context, String detailPath) {
        Bundle shareParams = new Bundle();
        shareParams.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        shareParams.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,
                detailPath);
        shareParams.putString(QQShare.SHARE_TO_QQ_APP_NAME, "LPS CRM");
        shareParams.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
                QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        doShareToQQ(shareParams, context);
    }

    private static class BaseUiListener implements IUiListener {

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onComplete(Object arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(UiError arg0) {
            // TODO Auto-generated method stub

        }

    }

    private static void doShareToQQ(Bundle params, final Context context) {
        Tencent mTencent = Tencent.createInstance("1106047577", context);

        mTencent.shareToQQ((Activity) context, params, new BaseUiListener() {
            protected void doComplete(JSONObject values) {
                Toast.makeText(context, "成功",
                        Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(UiError e) {
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public static boolean isGetPhotoPremission(){
        //先检查相机的权限，如果有权限才进入拍照页面   android.permission.CAMERA
        PackageManager pm = BTNotificationApplication.getInstance().getPackageManager();
        boolean flag = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CAMERA", "com.kct.fundo.btnotification"));

        return flag;
    }



    public static void setWatchInfoData(Context context,DBHelper db) {
        Query query = db.getWatchInfoDataDao().queryBuilder()
                .build();
        List<WatchInfoData> list = query.list();

        XmlResourceParser xrp = context.getResources().getXml(R.xml.watchinfo);
        try {
            // 直到文档的结尾处
            WatchInfoData watch = null;
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                String tagName = xrp.getName();

                if (xrp.getEventType() == XmlResourceParser.START_DOCUMENT){

                }

                if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT){
                    SharedPreUtil.setParam(context,SharedPreUtil.USER,SharedPreUtil.LOCAL_WATCHINFO,true);
                }

                // 如果遇到了开始标签
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    if(tagName.equals("account")){
                        watch = new WatchInfoData();
                    } else if (watch != null) {
                        if (tagName.equals("number")) {
                            watch.setNumber(xrp.nextText());
                        } else if (tagName.equals("qrcodenotice")) {
                            watch.setQrcodenotice(xrp.nextText());
                        } else if (tagName.equals("wechatSport")) {
                            watch.setWechatSport(xrp.nextText());
                        } else if (tagName.equals("autoheart")) {
                            watch.setAutoheart(xrp.nextText());
                        } else if (tagName.equals("appnotice")) {
                            watch.setAppnotice(xrp.nextText());
                        } else if (tagName.equals("callnotice")) {
                            watch.setCallnotice(xrp.nextText());
                        } else if (tagName.equals("platform")) {
                            watch.setPlatform(xrp.nextText());
                        } else if (tagName.equals("smartphoto")) {
                            watch.setSmartphoto(xrp.nextText());
                        } else if (tagName.equals("weathernotice")) {
                            watch.setWeathernotice(xrp.nextText());
                        } else if (tagName.equals("remindMode")) {
                            watch.setRemindMode(xrp.nextText());
                        } else if (tagName.equals("model")) {
                            watch.setModel(xrp.nextText());
                        } else if (tagName.equals("oxygen")) {
                            watch.setOxygen(xrp.nextText());
                        } else if (tagName.equals("smartalarm")) {
                            watch.setSmartalarm(xrp.nextText());
                        } else if (tagName.equals("smsnotice")) {
                            watch.setSmsnotice(xrp.nextText());
                        } else if (tagName.equals("sports")) {
                            watch.setSports(xrp.nextText());
                        } else if (tagName.equals("meteorology")) {
                            watch.setMeteorology(xrp.nextText());
                        } else if (tagName.equals("firware")) {
                            watch.setFirware(xrp.nextText());
                        } else if (tagName.equals("longsit")) {
                            watch.setLongsit(xrp.nextText());
                        } else if (tagName.equals("blood")) {
                            watch.setBlood(xrp.nextText());
                        } else if (tagName.equals("heart")) {
                            watch.setHeart(xrp.nextText());
                        } else if (tagName.equals("watchnotice")) {
                            watch.setWatchnotice(xrp.nextText());
                        } else if (tagName.equals("drinknotice")) {
                            watch.setDrinknotice(xrp.nextText());
                        } else if (tagName.equals("nodisturb")) {
                            watch.setNodisturb(xrp.nextText());
                        } else if (tagName.equals("raisingbright")) {
                            watch.setRaisingbright(xrp.nextText());
                        } else if (tagName.equals("btcall")) {
                            watch.setBtcall(xrp.nextText());
                        } else if (tagName.equals("board")) {
                            watch.setBoard(xrp.nextText());
                        } else if (tagName.equals("createTimes")) {
                            watch.setTimes(xrp.nextText());
                        } else if (tagName.equals("unitSetup")) {
                            watch.setUnitSetup(xrp.nextText());
                        } else if (tagName.equals("pointerCalibration")){
                            watch.setPointerCalibration(xrp.nextText());
                        } else if (tagName.equals("sleep")){
                            watch.setSleep(xrp.nextText());
                        } else if (tagName.equals("sos")){
                            watch.setSos(xrp.nextText());
                        } else if (tagName.equals("assistInput")){
                            watch.setAssistInput(xrp.nextText());
                        }else if (tagName.equals("faPiao")){
                            watch.setFaPiao(xrp.nextText());
                        } else if (tagName.equals("shouKuanewm")){
                            watch.setShouKuanewm(xrp.nextText());
                        } else if (tagName.equals("updateTimes")){
                            watch.setUpdate_time(xrp.nextText());
                        }
                    }
                }
                if (xrp.getEventType() == XmlResourceParser.END_TAG) {
                    if (tagName.equals("account") && watch !=null) {
                        watch = null;
                    }
                }
                xrp.next();// 获取解析下一个事件
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //读取文件到Byte数组
    public static byte[] readFile(String filePath) {
        try {
            FileInputStream in = new FileInputStream(filePath); // 读取文件路径
            byte bs[] = new byte[in.available()];
            in.read(bs);
            in.close();
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断当前日期是星期几
     */
    public static int dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;//星期一是1 星期天是0
        return dayForWeek;
    }
	
 public static void Unzip(String zipFile, String targetDir) {  //todo  -- 解压 zip     参数一为源zip文件的完整路径，参数二为解压缩后存放的文件夹。
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称
        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    Log.i("Unzip: ","="+ entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();

        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断apk是否有安装
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isPkgInstalled(Context context,String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 解析二维码（使用解析RGB编码数据的方式）
     *
     * @param path
     * @return
     */
    public static Result decodeBarcodeRGB(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        Bitmap barcode = BitmapFactory.decodeFile(path, opts);
        Result result = decodeBarcodeRGB(barcode);
        barcode.recycle();
        barcode = null;
        return result;
    }

    /**
     * 解析二维码 （使用解析RGB编码数据的方式）
     *
     * @param barcode
     * @return
     */
    public static Result decodeBarcodeRGB(Bitmap barcode) {
        int width = barcode.getWidth();
        int height = barcode.getHeight();
        int[] data = new int[width * height];
        barcode.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(bitmap1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        barcode.recycle();
        barcode = null;
        return result;
    }

    public static <T> List<T> getObjectList(String jsonString,Class<T> cls){
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 匹配是否为数字
     * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
     * @return
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static List<Ecg> getEcgList(Context context)
    {
        DBHelper db = DBHelper.getInstance(context);
        Query query = null;
        if (SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getEcgDao().queryBuilder().orderAsc(EcgDao.Properties.BinTime).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getEcgDao().queryBuilder().orderAsc(EcgDao.Properties.BinTime).build();  // 根据日期
        }
        List list = query.list();
        return list;
    }

    public static List<Ecg> getEcgList(String date)
    {
        DBHelper db = DBHelper.getInstance(BTNotificationApplication.getInstance());
//        DBHelper db = DBHelper.getInstance(MainService.getInstance());
        Query query = null;
        if (SharedPreUtil.readPre(MainService.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getEcgDao().queryBuilder().where(EcgDao.Properties.Date.eq(date)).orderAsc(EcgDao.Properties.BinTime).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getEcgDao().queryBuilder().where(EcgDao.Properties.Date.eq(date)).orderAsc(EcgDao.Properties.BinTime).build();  // 根据日期
        }
        List list = query.list();
//        ArrayList list = (ArrayList) query.list();
        return list;
    }

    public static int getEcgY(int y,int gain,int dimension,int mmPx)
    {
        int value = dimension>0?y/dimension*gain*mmPx:0;
        return value;
    }

    public static String getHeart(Ecg item)
    {
        String heartStr = "";
        if(item!=null)
        {
            String heartList = item.getHearts();
            if(!TextUtils.isEmpty(heartList))
            {
                String[]hearts =  heartList.split(";");
                int size = hearts.length;
                int sum=0;
                for(int i=0;i<size;i++)
                {
                    if(Utils.isNumeric(hearts[i]))
                        sum = sum+Integer.parseInt(hearts[i]);
                }
                heartStr = (size>0?sum/size:0)+"";
            }
        }
        return heartStr;
    }

}
