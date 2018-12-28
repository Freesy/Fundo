
package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.facebook.stetho.Stetho;
import com.kct.bluetooth.KCTBluetoothManager;
import com.kct.fundo.btnotification.R;
import com.mediatek.wearable.WearableManager;
import com.mob.MobSDK;
import com.mtk.app.bluetoothle.LocalPxpFmpController;
import com.mtk.app.ipc.IPCControllerFactory;
import com.mtk.app.notification.NotificationCollectorMonitorService;
import com.mtk.app.notification.NotificationReceiver19;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.DaoMaster;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.DaoMaster.OpenHelper;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.DaoSession;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.util.AppActivitysLifecycleCallback;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.umeng.analytics.MobclickAgent;
import com.yd.ydsdk.manager.YdConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;


/**
 * This class is the application enter, when it created, begin record logs.
 */
public class BTNotificationApplication extends Application {

    private static Activity activity;
    private LinkedHashMap<String, Integer> globalRoamingMap = new LinkedHashMap<>();
    private ArrayList<Activity> closeActivity = new ArrayList<>();//需要关闭的界面	登陆界面  最后登陆成功把其他界面都关闭


    // Debugging
    private static final String TAG = "AppManager/Application";
    public static int time_count = 8;// 沙漏原理全局变量
    private final List<Activity> activityList = new LinkedList<Activity>();
    public static RequestQueue requestQueue = null;
    private static BTNotificationApplication sInstance = null;

    public static BTNotificationApplication mApp;  //ADDLX

    public Typeface akzidenzGroteskMediumCondAlt = null;
    public Typeface akzidenzGroteskLightCond = null;
    public Typeface lanTingThinBlackTypeface = null;
    public Typeface lanTingBoldBlackTypeface = null;
    public Typeface lanTingBoldestBlackTypeface = null;
    public Typeface dIN1451EF_EngNeuTypeface = null;

    public static ArrayList<Activity> arrActivity;

    public static int needReceiveNum = 0;
    public static boolean isSyncEnd = true;

    public static int needGetSportDayNum = 0; //需要取计步的天数
    public static int needGetSleepDayNum = 0; //需要取睡眠的天数
    public static int needGetHeartDayNum = 0; //需要取心率的天数

    public static int needSendDataType = 0; //(智能表需要同步的类型：计步，睡眠，心率，血压)
    public static int needReceDataNumber = 0;  //(智能表已经收到同步的类型)

//	public static boolean isNeedComment = true;  // 是否需要评论标志

    private String guanggaoJson;//广告或活动解析bean

    public static MainService getMainService() {
        return MainService.getInstance();
    }

    /**
     * Return the instance of our application.
     *
     * @return the application instance
     */
    public static BTNotificationApplication getInstance() {
        return sInstance;
    }

    private String getCurrentProcessName() {
        String currentProcName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            Log.e(TAG, "currentProcName = " + processInfo.processName + " ;  " + processInfo.pid);
            if (processInfo.pid == pid) {
                currentProcName = processInfo.processName;
                break;
            }
        }

        return currentProcName;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate(), BTNoticationApplication create!");
        Log.d(TAG, "onCreate(), SDK level = " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 18) {
            Log.d(TAG, "onCreate(), LE support = "
                    + getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
        }
        super.onCreate();
        sInstance = this;
        YdConfig.getInstance().init(this);
        if (!getPackageName().equals(getCurrentProcessName())) {
            return;
        }

        KCTBluetoothManager.getInstance().init(this);
//		LogUtil.getInstance(getApplicationContext()).start();  //todo ----  启动log日志

        //极光推送  //区分国内外
        if (Utils.getLanguage().contains("zh")) {
            JPushInterface.setDebugMode(false);
            JPushInterface.init(this);
        }

        requestQueue = Volley.newRequestQueue(this);

        IPCControllerFactory.getInstance().init();
        LocalPxpFmpController.initPxpFmpFunctions(this);
        boolean isSuccess = WearableManager.getInstance().init(true, getApplicationContext(), "we had",
                R.xml.wearable_config);
        Log.d(TAG, "WearableManager init " + isSuccess);
        if (!MainService.isMainServiceActive()) {
            Log.i(TAG, "start MainService!");
            getApplicationContext().startService(new Intent(getApplicationContext(), MainService.class));
        }
        startService(new Intent(sInstance, NotificationCollectorMonitorService.class));//监听通知
        if (false == Utils.isServiceWork(this, com.mtk.app.notification.NotificationReceiver19.class.getName())) {
            startService(new Intent(sInstance, NotificationReceiver19.class));//启动通知
        }

        String strListener = Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        Log.i("strListener", "strListener = " + strListener);
        if (strListener != null && strListener.contains("com.kct.fundo.btnotification/com.mtk.app.notification.NotificationReceiver19")) {
            ComponentName localComponentName = new ComponentName(this, NotificationReceiver19.class);
            PackageManager localPackageManager = this.getPackageManager();
            localPackageManager.setComponentEnabledSetting(localComponentName, 2, 1);
            localPackageManager.setComponentEnabledSetting(localComponentName, 1, 1);
            Log.i("strListener", "setComponentEnabledSetting");
        }


        akzidenzGroteskMediumCondAlt = Typeface.createFromAsset(getAssets(), "fonts/AkzidenzGrotesk-MediumCondAlt.otf");
        akzidenzGroteskLightCond = Typeface.createFromAsset(getAssets(), "fonts/AkzidenzGrotesk-LightCond.otf");
        lanTingThinBlackTypeface = Typeface.createFromAsset(getAssets(), "fonts/LanTingThinBlack.TTF");
        lanTingBoldBlackTypeface = Typeface.createFromAsset(getAssets(), "fonts/LanTingBoldBlack.TTF");
        lanTingBoldestBlackTypeface = Typeface.createFromAsset(getAssets(), "fonts/LanTingBoldestBlack.TTF");
        dIN1451EF_EngNeuTypeface = Typeface.createFromAsset(getAssets(), "fonts/DIN1451EF_EngNeu.otf");

        if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
            WearableManager.getInstance().switchMode();
        }

        // 设置未捕获异常的处理器
        Thread.setDefaultUncaughtExceptionHandler(new GlobarCatchException());

        arrActivity = new ArrayList<Activity>();
        Stetho.initializeWithDefaults(this);//调试的时候开启，发布版本时注释掉

        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(getApplicationContext(), "59487e56f43e48400600046b", "FunDo"));  // 59487e56f43e48400600046b
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.enableEncrypt(true);

        // 通过代码注册你的mob AppKey和AppSecret    TODO --- add 20180105
        MobSDK.init(getApplicationContext(), "1ce3b16f6b0a0", "321bdeb3b5375ebe564bc78ddc1105ca");
        //获取国际区号
//        initGlobalRoamingMap();
        //30分钟定时上传数据
//		NewUploadDataUtil.start30MinUploadAll(this);
        AppActivitysLifecycleCallback.init(this);

        parsePlist();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //因为引用的包过多，实现多包问题
        MultiDex.install(this);
    }

    class GlobarCatchException implements Thread.UncaughtExceptionHandler {
        // 出现未被捕获的异常就会走入该方法中
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            if (SharedPreUtil.readPre(getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {
                byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.APP_BLUETOOTH_DISCONNECT, null);  //  验证 OK
                MainService.getInstance().writeToDevice(l2, true);
            }

            MobclickAgent.reportError(getApplicationContext(), ex);

            File errLog = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FunDoError.log");

            PrintWriter writer;
            try {
                writer = new PrintWriter(errLog);
                ex.printStackTrace(writer);

                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ex.printStackTrace();

            // 根据当前进程id,杀死进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//		LogUtil.getInstance(getApplicationContext()).stop();
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }


    /****** zhangxiong add begin greendao相关添加 ******/
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    public static SQLiteDatabase db;
    // 数据库名，表名是自动被创建的
    public static final String DB_NAME = "dbnametwo.db";

    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static SQLiteDatabase getSQLDatebase(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            db = daoMaster.getDatabase();
        }
        return db;
    }

    /****** zhangxiong add end ******/

    public static void setActivity(Activity activity) {
        arrActivity.add(activity);
    }

    @SuppressWarnings("unchecked")
    public static void toFinishAllActivity() {
        if (arrActivity != null) {
            Log.i("activity-->size", arrActivity.size() + "");
            for (int i = 0; i < arrActivity.size(); i++) {
                ((Activity) arrActivity.get(i)).finish();
            }

        }
    }

    public String getGuangGaoBean() {
        return guanggaoJson;
    }

    public void setGuangGaoBean(String guanggaoJson) {
        this.guanggaoJson = guanggaoJson;
    }

    /** ----------登录注册新加的代码----------------------------------------- */
    private void initGlobalRoamingMap() {
        SMSSDK.unregisterAllEventHandler();
        SMSSDK.registerEventHandler(new cn.smssdk.EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                super.afterEvent(event, result, data);
                if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    String ct = data.toString();
                    HashMap<Character, ArrayList<String[]>> first = SMSSDK.getGroupedCountryList();
                    //System.out.println("第一层数组："+first);
                    if (ct == null) {
                        Log.e(TAG, "afterEvent: data.toString()");
                    } else {
                        Log.e(TAG, "afterEvent: " + ct);
                    }
                    if (first == null) {
                        Log.e(TAG, "afterEvent: 异常");
                        return;
                    }

                    Set set = first.keySet();
                    for (Object obj : set) {
                        ArrayList<String[]> second = first.get(obj);
                        //System.out.println("第二层数组："+second);
                        for (int i = 0; i < second.toArray().length; i++) {
                            String[] thirst = second.get(i);
                            //System.out.println("第三层数组："+thirst);
                            System.out.println("国家区号:" + thirst[0] + " " + thirst[1]);
                            try {
                                globalRoamingMap.put(thirst[0], Integer.parseInt(thirst[1]));
                            } catch (Exception e) {
                            }
                        }

                    }
                }
            }
        });
        SMSSDK.getSupportedCountries();
    }

    /**
     * 获得国家对应区号
     *
     * @param country_name
     * @return
     */
    public Integer getGlobalRoaming(String country_name) {
        return globalRoamingMap.get(country_name);
    }

    public void addCloseActivity(Activity mActivity) {
        closeActivity.add(mActivity);
    }

    public void removeAllCloseActivity() {
        for (Activity mActivity : closeActivity)
            mActivity.finish();
    }

    public void parsePlist() {
        try {
            NSDictionary dic = (NSDictionary) PropertyListParser.parse(getAssets().open("country.plist"));
            String[] temp = dic.allKeys();
            for (int i = 0; i < temp.length; i++) {
                NSArray tmpAry = (NSArray) dic.objectForKey(temp[i]);
                for (int index = 0; index < tmpAry.count(); index++) {
                    NSObject nso = tmpAry.objectAtIndex(index);
                    String countryAndCode[] = nso.toJavaObject().toString().split("\\+");
                    globalRoamingMap.put(countryAndCode[0], Integer.parseInt(countryAndCode[1]));
                }
            }
        } catch (Exception e) {
            Logg.e(TAG, "parsePlist: 异常");
            e.printStackTrace();
        }
    }

    /**
     * 获取短信支持的国家区号
     *
     * @return
     */
    public LinkedHashMap<String, Integer> getGlobalRoamingMap() {
        return globalRoamingMap;
    }
}
