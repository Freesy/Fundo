package com.szkct.map.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.kct.fundo.btnotification.R;
import com.szkct.GPSCorrect;
import com.szkct.map.utils.Util;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointData;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.login.Logg;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.main.OutdoorRunActitivy;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.TimerHelper;
import com.szkct.weloopbtsmartdevice.util.TimerProcessor;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 2016/12/19.
 * 版本：v1.0
 */

public class SportService extends Service implements SensorEventListener {    // 运动的服务
    private static final String TAG = SportService.class.getSimpleName();

    public static final String CMD_RECEIVER = "com.szkct.map.SportsService";// 服务收命令
    public static final String SEND_RECEIVER_TIME = "com.szkct.weloopbtsmartdevice.TIME";// 运动时间
    public static final String SEND_RECEIVER_DATA = "com.szkct.weloopbtsmartdevice.main.DATA";// 服务发数据
    public static final String SEND_RECEIVER_GPS = "com.szkct.weloopbtsmartdevice.main.GPS";// 服务发数据（GPS）
    public static final String SEND_RECEIVER_NETWORK = "com.szkct.map.NETWORK";// 网络广播
    public static final String PERMISION_GRANTED_GPS = "com.szkct.map.GRANTED_GPS";// 请求gps权限成功广播

    /**
     * 自动暂停 和自动停止广播
     **/
    public static final String SEND_RECEIVER_AUTO_PAUSE = "com.szkct.map.AUTOPAUSE";// 自动暂停广播
    public static final String SEND_RECEIVER_AUTO_START = "com.szkct.map.AUTOSTART";// 自动启动广播
    public static final String SEND_RECEIVER_AUTO_PFINISH = "com.szkct.map.AUTOFINISH";// 自动结束广播


    //    float speed = 0;
    double altitude = 0;// 海拔   ----  当前的海拔
    static double mMile = 0.00;// 距离  ---- 运动距离

    double addMileTemp = 0.0;

    static int calorie = 0;//卡里路
    long id = 0;
    String mac = "";
    String mid = "";

    private List<String> latList = new ArrayList<String>();//
    private ArrayList<String> lngList = new ArrayList<String>();//
    private ArrayList<String> speedList = new ArrayList<String>();//
    private ArrayList<String> bupingList = new ArrayList<String>();//步频集合
    private ArrayList<String> bufuList = new ArrayList<String>();//步幅集合
    private ArrayList<String> altitudeList = new ArrayList<String>();//


    private boolean isStrat = false;//TODO --- 是否点击了开始运动

    private boolean isPhoneStatic = false;//TODO-- 手机是否静止


    private ArrayList<com.amap.api.maps.model.LatLng> points = new ArrayList<com.amap.api.maps.model.LatLng>();// 定位点画线
    private ArrayList<com.amap.api.maps.model.LatLng> points2 = new ArrayList<com.amap.api.maps.model.LatLng>();// 实时经纬度 用于计算实时配速
    private ArrayList<AMapLocation> pointLocation = new ArrayList<AMapLocation>();  // 定位位置的集合

    private ArrayList<Float> pointSpeedw = new ArrayList<Float>();  // 地图定位速度的集合    float speedw

    private String arrLat = "";//TODO ---  所有纬度拼接
    private String arrLng = "";//TODO ----  所有经度拼接
    private String arrLatDetail = "";// 所有经度拼接
    private String arrLngDetail = "";// 所有经度拼接

    private String arrDistance = "";// 实时距离
    private String mMid;
    private LocationManager locationManager;
    private boolean isWook = true;
    private int Satenum = 0;//gps强度
    public boolean isStop = false;// 是否结束运动
    public boolean isPause = false;// false 表示启动运动  true表示运动暂停
    public boolean isStopStart = false;// false 表示是否暂停后启动运动
    String date = "";//当前跑步日期

    private int pauseNumber = 0;//暂停次数

    private ArrayList<GpsPointData> gpsPointDataList = new ArrayList<GpsPointData>();// 零时记录点集合


    // 运动时间
    private static Timer timer = null;
    private Timer timer2 = null;//运动暂停计时
    private Timer timer3 = null;//运动暂停计时（10分钟）
    private Timer timer4 = null;//运动暂停计时

    private Timer timer5 = null;//运动暂停计时（5分钟）
    private static long count = 0;// 时间变量   ---- 运动的时间累积
    private long count2 = 0;// 时间变量   ----- 暂停的时间累积
    public static String shijian = "00:00:00";  // 时间变量   ---- 运动时的当前时间
    public String shijian2 = "00:00:00";        // 时间变量   ---- 暂停时的当前时间
    String startTime = null;
    private static MyTask mTimerTask;
    private MyTask2 mTimerTask2;//运动暂停计时


    private MyTask3 mTimerTask3;  //倒计时10分钟 停止运动

    private MyTask4 mTimerTask4;  //倒计时15秒

    private MyTask5 mTimerTask5; // 倒计时 5 分钟 停止运动

    private Context mContext;
    private AMapLocationClient mAMapLocationClient = null;
    private MyLocationListenerGD mMyLocationListenerGD;
    private double mLatitude;
    private double mLongitude;
    double tMile = 0.0;

    private long locationTime;

    private DBHelper db = null;
    private String arrSpeed = "";
    private String arrTotalSpeed = "";//总时间 总距离实时配速 数组
    private String arrKmSpeed = "";//每KM的配速 数组
    private PowerManager.WakeLock wl;
    private static String mCurrentSpeed;//当前配速
    private String mSportType = "1";//运动类型
    private String mHeartRate = "0";//心率
    private String deviceType = "1";// 设备类型 代表手机 2 代表手表
    private int mCurrentCount = 0;//当前时间
    private int mKmCurrentCount = 0;//每1km当前时间
    private double mKmMile = 0.0;  // 每千米的距离

    private double mShineiKmMile = 0.0; // TODO --- 室内跑每千米的距离（计算每千米的配速）

    private SharedPreferences preferences;
    private SensorManager mSensorManager;
    private Sensor mStepDetector;//计步传感器
    private Sensor mSensorAccelerometer;//加速度传感器
    private boolean isFirstStep = true;//是否第一次获取步数
    private float firstStep = 0;//第一次获取步数
    private float mStepCount = 0;//计步器计步变量

    private float mAllStepCount = 0;//计步器计步变量

    int time = 600;  //自动暂停和自动停止开关都打开时，倒计时10分钟
    int time3 = 300;//自动暂停没有打开，自动停止开关打开时，倒计时5分钟
    int time2 = 15;

    private int mX, mY, mZ;
    private long lasttimestamp = 0;
    Calendar mCalendar;

    private boolean isAutoPause = true;//控制自动暂停启动的变量 防止重复发送广播   ---- 自动暂停

    private boolean isFirstSport = false;//todo  --- 第一次进来要有运动后才开启自动暂停

    private Thread pauseHread = null;

    private int minBf, maxBf;  // TODO -- 最小步幅，最大步幅

    /**
     * 速度数组。
     */
    private String arrspeed = "";
    /**
     * 步频数组。
     */
    private String arrcadence = "";
    /**
     * 海拔数组。
     */
    private String arraltitude = "";   // TODO --  海拔数组值
    private int pjBf = 0; //步幅

    private int okpjBf = 0; //步幅


    private int sportMode = 1;//运动模式
    private AlarmManager am;
    private PendingIntent pii;
    private GpsPointDetailData gpsDeatils;

    private float isOKValue = 0;
    private boolean isCorrectData = true;

    private int mCurKmMile = 0; // 每千米距离的累加值

    private final int TEM_MIN = 10 * 60 * 1000;
    /** 每隔10分钟保存一次运动数据 */
    private TimerHelper count_10_min = new TimerHelper(new TimerProcessor() {
        @Override
        public void process() {
            if(mMile > 20){
                saveGps();
            }
        }
    });

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //启用前台服务，主要是startForeground() http://www.kancloud.cn/digest/protectyoureyes/122207
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.speed_logo);
        builder.setTicker(getResources().getString(R.string.app_name));
        builder.setContentTitle(getResources().getString(R.string.app_name) + "!");
        builder.setContentText(getResources().getString(R.string.app_name) + "!");
        Notification notification = builder.getNotification();
        /*Notification notification = new Notification(R.drawable.speed_logo, ""
                , System.currentTimeMillis());
        notification.setLatestEventInfo(this, "FunDo",
                "分动！", null);*/
        //设置通知默认效果
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        startForeground(1, notification);

        mContext = getApplicationContext();
        preferences = getSharedPreferences("userinfo", MODE_MULTI_PROCESS);

        /**获取运动类型  1.健走 2.户外跑 3.登山跑 4.越野跑 5.室内跑 6.半马 7.全马**/
        String sport = SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE);
        if (sport.equals("")) {
            sportMode = 1;
        } else {
            sportMode = Integer.valueOf(sport);
        }


        /**加速度传感器**/
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
        // 参数三，检测的精准度
        mSensorManager.registerListener(this, mSensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME


        /**TYPE_STEP_COUNTER
         API的解释说返回从开机被激活后统计的步数，当重启手机后该数据归零，该传感器是一个硬件传感器所以它是低功耗的。为了能持续的计步，请不要反注册事件，就算手机处于休眠状态它依然会计步。当激活的时候依然会上报步数。该sensor适合在长时间的计步需求。
         TYPE_STEP_DETECTOR
         翻译过来就是走路检测，API文档也确实是这样说的，该sensor只用来监监测走步，每次返回数字1.0。如果需要长事件的计步请使用TYPE_STEP_COUNTER。**/
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this, mStepDetector,
                SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME

        registerBoradcastReceiver();
        initData();

        count_10_min.startPeriodTimer(TEM_MIN,TEM_MIN);
    }

    /**
     * 注册广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(CMD_RECEIVER); //TODO ---  接收命令
        myIntentFilter.addAction("ELITOR_CLOCK");
//        myIntentFilter.addAction(SportService.SEND_RECEIVER_TIME);
//        myIntentFilter.addAction(SportService.SEND_RECEIVER_NETWORK);
        myIntentFilter.addAction(Intent.ACTION_SCREEN_ON);// 监听手机屏幕显示
        myIntentFilter.addAction(Service.ALARM_SERVICE);// 时钟广播

        myIntentFilter.addAction(MainService.ACTION_SPORTMODE_AUTOPAUSE);// 自动暂停的广播
        myIntentFilter.addAction(MainService.ACTION_SPORTMODE_AUTOSTOP);// 自动停止的广播

        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        initLocationGps();// 初始化GPS定位
        initLocationGD();//初始化高德地图
        startSport();  //TODO ---  服务一创建就开始记录运动数据
//        doGpsJob();
        String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);
        if (!"".equals(mid)) {
            mMid = mid;
        } else {
            mMid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MYMAC);
        }

        mac = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MYMAC);

        // 防止休眠
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();// 申请锁这个里面会调用PowerManagerService里面acquireWakeLock()


        /************************防止服务退出********************/
        //也可以写在一行里
        // http://blog.csdn.net/wangxingwu_314/article/details/8060312
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent intent = new Intent("ELITOR_CLOCK");
        intent.putExtra("msg", "消息来了...");
        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"ELITOR_CLOCK"的intent
        pii = PendingIntent.getBroadcast(this, 0, intent, 0);

        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5 * 1000, pii);


    }


    private void initLocationGD() {
        if (sportMode == 3)//室内跑不需要地图   sportMode == 5
            return;

        mAMapLocationClient = new AMapLocationClient(getApplicationContext());
        mMyLocationListenerGD = new MyLocationListenerGD();
        mAMapLocationClient.setLocationListener(mMyLocationListenerGD);

        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setInterval(2000);
        mOption.setGpsFirst(true);
        mOption.setNeedAddress(true);

        //设置是否只定位一次,默认为false
        mOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置   // getProvider
        mOption.setMockEnable(false);
        mAMapLocationClient.setLocationOption(mOption);
        mAMapLocationClient.startLocation();
    }


    private void initLocationGps() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 设置状态监听回调函数。statusListener是监听的回调函数。
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addGpsStatusListener(statusListener);
        Logg.e(TAG, "initLocationGps: 监听Gps状态");

//        locationListener = new LBSServiceListener();
        //通过gps定位 如果gps为null则通过基站定位
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, (float) 10.0, new LocationListener() {  // 似乎无啥作用
            @Override
            public void onLocationChanged(Location location) {
                location.getProvider();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }


    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {// GPS状态变化时的回调，获取当前状态
            GpsStatus status = locationManager.getGpsStatus(null);
            //自己编写的方法，获取卫星状态相关数据
            GetGPSStatus(event, status);
        }
    };

    List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();

    private void GetGPSStatus(int event, GpsStatus status) {
        Log.d(TAG, "enter the updateGpsStatus()");
        if (status == null) {
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            //获取最大的卫星数（这个只是一个预设值）
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            //记录实际的卫星数目
            int gpsCount = 0;
            while (it.hasNext() && gpsCount <= maxSatellites) {
                //保存卫星的数据到一个队列，用于刷新界面
                GpsSatellite s = it.next();
                if (s.getSnr() != 0)//只有信躁比不为0的时候才算搜到了星
                {
                    numSatelliteList.add(s);
                    gpsCount++;
                }
            }
            Satenum = gpsCount;
            Log.i("GPS", Satenum + "");
            int mSatelliteNum = numSatelliteList.size();
            Log.i("GPS", mSatelliteNum + "&&&&&");
        } else if (event == GpsStatus.GPS_EVENT_STARTED) {
            //定位启动
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            //定位结束
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {//计步传感器'  todo ---- 通过计步器计算 室内跑 也要添加 自动暂停和自动停止
            if (isStrat && OutdoorRunActitivy.runstate == 0) {
                if (sportMode == 3) {//室内跑
                    if (isFirstStep) {//第一次将步数置0   todo  是否第一次获取步数
                        isFirstStep = false;
                        firstStep = event.values[0];  // 第一次取的时候的步数 ----  2000
                        mAllStepCount = 0;
//                        Log.e(TAG, "第一次运动步数 = " + mAllStepCount);
//                        Log.e(TAG, "第一次运动计步===== = " + firstStep);
                    } else {
//                        Log.e(TAG, "后续firstStep===== = " + firstStep);
//                        Log.e(TAG, "后续event.values[0]===== = " + event.values[0]);
                        if (event.values[0] != isOKValue) {
                            isOKValue = event.values[0];
                            if ((event.values[0] - firstStep) > 0) {  //  && !isTheSame
                                float temp = event.values[0] - firstStep;
//                                mAllStepCount += temp;  // 测试用
                                if (temp > mAllStepCount) {   // ok
                                    mAllStepCount = temp;
                                    isCorrectData = true;
                                } else {
                                    isCorrectData = false;
                                }
                            }
                        }
//                        Log.e(TAG, "后续实际实时步数 = " + mAllStepCount);
//                        Log.e(TAG, "后续实际实时计步 ====== " + event.values[0]);
                        int height = Integer.valueOf(Utils.gethight(this));//获取用户身高
                        if (mAllStepCount > 0 && isCorrectData && !isPhoneStatic) {  //   todo ---室内跑手机静止时，不能产生运动数据， 室内跑有时无速度和步频
                            mMile = (mAllStepCount * (height * 0.45)) / 100;     //     getSpeed ---获取每千米的配速
//                            Log.e(TAG, "后续实际实时运动距离 ====== " + mMile);
//                            Log.e(TAG, "================================================== ====== " );
                            mCurrentSpeed = getCurrentSpeed(count, mMile);//得到实时配速   -------------  分钟/公里
//                            Log.e(TAG, "实时配速 = " + mCurrentSpeed);

                            String totalPs = Utils.getPace2(String.valueOf(count), String.valueOf(mMile));//记录总距离总时间实时配速(用于计算最大，最小配速) 数组
                            Log.e(TAG, "配速 = " + totalPs);
                            arrTotalSpeed += totalPs + "&";   //todo --- 配速数组（显示于配速详情页面）
                            Log.e(TAG, "arrTotalSpeed = " + arrTotalSpeed);

                            if (addMileTemp != mMile) {
                                // mKmMile = Utils.decimalTo2((mKmMile + mMile), 2);   //km距离
                                mKmMile += Utils.decimalTo2((mMile - addMileTemp), 2);
                                //TODO --- 计算每1km得配速   ---- 手机端除了室内跑,其他运动模式的，每千米的配速都ok,
                                if (mKmMile >= 1000) {
                                    String totalKmPs = Utils.setformat(2, Utils.getPace2(String.valueOf(mKmCurrentCount), String.valueOf(mKmMile)));//记录每km配速 数组
                                    arrKmSpeed += totalKmPs + "&";   // 每千米的 配速 数组
                                    mKmMile = mKmMile % 1000;//将大于1KM的余数保留计算下一个一千米
                                    mKmCurrentCount = 0;
                                }
                                addMileTemp = mMile;
                            }

                            arrspeed = arrspeed + String.valueOf(mMile / count) + "&";//计算实时速度用于画详细图   // todo   mMile/count = 运动距离/运动时间    --- 速度数组   ---- 传感器的速度数组 （显示于运动图表页面）
//                            Log.e(TAG, "arrspeed = " + arrspeed);
                            calorie = getCalories(mMile);//得到卡里路

                            int feetweek = (int) (height * 0.45);// 走路步长 厘米
                            int gps_bushu = (int) mMile * 100 / feetweek;//得到步数 （路程/步长）  tMile * 100 米转换成厘米

                            Long mec = count;//得到秒数
                            if (mec == 0) {
                                mec = 1l;
                            }
                            int sec = Integer.parseInt(mec.toString());
//                            double buping = gps_bushu / sec * 60;//得到步频（步数除以时间 单位分）
                            int sportFen = sec / 60;
                            if (sportFen == 0) {
                                sportFen = 1;
                            }
                            double buping = gps_bushu / sportFen;//得到步频（步数除以时间 单位分）
                            bupingList.add(String.valueOf(buping));
                            arrcadence = arrcadence + String.valueOf(buping) + "&";      //TODO  --  步频数组

                            Log.i("buping", buping + "");
                            /**计算步幅**/
                            if (gps_bushu == 0) {
                                pjBf = 0;
                            } else {
                                pjBf = (int) (mMile * 100) / gps_bushu;
                            }
                            bufuList.add(String.valueOf(pjBf));

                            Intent intent = new Intent();// 创建Intent对象
                            intent.setAction(SEND_RECEIVER_GPS);   //计步传感器-----室内跑
                            Bundle bundle = new Bundle();
                            bundle.putDouble("latitude_gps", mLatitude);
                            bundle.putDouble("longitude_gps", mLongitude);
                            bundle.putSerializable("points", points);
                            bundle.putDouble("altitude", altitude);
                            bundle.putDouble("mile", mMile);
                            bundle.putDouble("calorie", calorie);
                            bundle.putInt("Satenum", Satenum);
                            bundle.putString("peisu", mCurrentSpeed);
                            intent.putExtras(bundle);
                            sendBroadcast(intent);// 发送广播   TODO --- 室内跑 通过计步器产生的数据
                            Log.i(TAG, "发送广播:" + Satenum + " ");
                        }
                    }
                }
            }
        }
        //TODO --- 自动停止开启后，如果用户手动暂停：10分钟内未手动继续运动，则自动停止，保存数据。 如果不是手动暂停，不管用户是否开启自动暂停，5分钟内未能检测到运动，则自动停止运动，保存数据。

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {//加速度传感器
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis() / 1000l;// 1393844912
            Log.i("xyz", "x========" + x + "y======" + y + z + "");

            int second = mCalendar.get(Calendar.SECOND);// 53

            int px = Math.abs(mX - x);
            int py = Math.abs(mY - y);
            int pz = Math.abs(mZ - z);
            int maxvalue = getMaxValue(px, py, pz);
//            Log.e("info", "pX:" + px + "  pY:" + py + "  pZ:" + pz + "    stamp:" + stamp + "  second:" + second + "maxvalue====" + maxvalue + "chazhi=====" + (stamp - lasttimestamp));

            if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOPAUSE).equals(SharedPreUtil.YES)) { //TODO --- 自动暂停  CB_RUNSETTING_AUTOPAUSE
                if (maxvalue > 1 && !isFirstSport) {  // 运动中   maxvalue > 2
                    isAutoPause = true;  // 自动暂停了
                    isFirstSport = true;

                    lasttimestamp = stamp;

                    isPhoneStatic = false;

//                    isStopStart = true;
                    isStrat = true;

                    time = 600;
                    time2 = 15;
                    if (null != timer3) {
                        timer3.cancel();
                        timer3 = null;
                    }
                    if (null != timer4) {
                        timer4.cancel();
                        timer4 = null;
                    }
                    if (null != timer5) {
                        timer5.cancel();
                        timer5 = null;
                    }

                    stopTimer2();//停止 ---- 记录暂停时间
                    sendBroadcast(new Intent(SportService.SEND_RECEIVER_AUTO_START));//   重新开始运动了
                    Log.i("time2", time2 + "");
                } else if (maxvalue == 0 && isAutoPause && isFirstSport) {//TODO   -----手机静止   maxvalue < 2
                    isFirstSport = false;
                    // todo --- add 20170523
                    if ((stamp - lasttimestamp) == 10) {
                        isPhoneStatic = true;
                    }

                    if (null == timer4) {
                        timer4 = new Timer();
                    } else {
                        timer4.cancel();
                        timer4 = new Timer();
                    }

                    if (null == mTimerTask4) {
                        mTimerTask4 = new MyTask4();
                    } else {
                        mTimerTask4.cancel();
                        mTimerTask4 = new MyTask4();
                    }

                    time2 = 15;
                    timer4.schedule(mTimerTask4, 0, 1000); //TODO ---  开启 15 秒 未动进入 自动暂停（开始记录 暂停时间）   timer4.schedule(new MyTask4(), 0, 1000);
                    Log.e(TAG, "开启15秒进入自动暂停倒计时了");

                    //TODO--- （自动停止打开，自动暂停打开） 10 后 停止运动
                    if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOSTOP).equals(SharedPreUtil.YES)) {//TODO --- 自动停止, CB_RUNSETTING_AUTOSTOP  （自动停止开关打开时 5 分钟）
                        if (null == timer3) {
                            timer3 = new Timer();
                        } else {
                            timer3.cancel();
                            timer3 = new Timer();
                        }

                        if (null == mTimerTask3) {
                            mTimerTask3 = new MyTask3();
                        } else {
                            mTimerTask3.cancel();
                            mTimerTask3 = new MyTask3();
                        }

                        time = 600; //
                        timer3.schedule(mTimerTask3, 0, 1000); //   timer3.schedule(new MyTask3(), 0, 1000);
                        Log.e(TAG, "进入自动暂停倒计时了");
                    } else {
                        if (null != timer3) {  // todo --- add 0523
                            timer3.cancel();
                            timer3 = null;
                        }
                        if (null != mTimerTask3) {
                            mTimerTask3.cancel();
                            mTimerTask3 = null;
                        }

                        if (null != timer5) {  // todo --- add 0523
                            timer5.cancel();
                            timer5 = null;
                        }
                        if (null != mTimerTask5) {
                            mTimerTask5.cancel();
                            mTimerTask5 = null;
                        }
                    }
                }
            } else {//TODO ---- 自动暂停关闭时    // ---   } else  {  ---  } else if(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOPAUSE).equals(SharedPreUtil.NO)) {
                if (maxvalue > 1 && !isFirstSport) {  // maxvalue > 2

                    isAutoPause = true;  // 自动暂停了
                    isFirstSport = true;

                    if (null != timer3) {
                        timer3.cancel();
                        timer3 = null;
                    }
                    if (null != timer4) {
                        timer4.cancel();
                        timer4 = null;
                    }
                    if (null != timer5) {
                        timer5.cancel();
                        timer5 = null;
                    }

                    lasttimestamp = stamp;
                    isStrat = true;      //TODO--- 开始运动了
                    isPhoneStatic = false;
//                    Log.e(TAG, "kaishi运动了 ");
                } else if (maxvalue == 0 && isAutoPause && isFirstSport) { // maxvalue < 2
//                    Log.e(TAG, "时间差 = " + (stamp - lasttimestamp));
                    isFirstSport = false;
                    if ((stamp - lasttimestamp) == 10) {  // todo --- 或者改成5  // TODO 手机静止时，只要有GPS数据，即为运动数据
                        isPhoneStatic = true;
                    }
                    //TODO--- （自动停止打开，自动暂停关闭，为自动暂停） 5分钟 后 停止运动
                    if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOSTOP).equals(SharedPreUtil.YES)) {//TODO --- 自动停止, CB_RUNSETTING_AUTOSTOP  （自动停止开关打开时 5 分钟）
                        if (null == timer5) {
                            timer5 = new Timer();
                        } else {
                            timer5.cancel();
                            timer5 = new Timer();
                        }

                        if (null == mTimerTask5) {
                            mTimerTask5 = new MyTask5();
                        } else {
                            mTimerTask5.cancel();
                            mTimerTask5 = new MyTask5();
                        }

                        time3 = 300;
                        timer5.schedule(mTimerTask5, 0, 1000);  //  timer5.schedule(new MyTask5(), 0, 1000);
                        Log.e("jinru", "进入自动停止倒计时了");
                    } else {
                        if (null != timer3) {  // todo --- add 0523
                            timer3.cancel();
                            timer3 = null;
                        }
                        if (null != mTimerTask3) {
                            mTimerTask3.cancel();
                            mTimerTask3 = null;
                        }

                        if (null != timer5) {  // todo --- add 0523
                            timer5.cancel();
                            timer5 = null;
                        }
                        if (null != mTimerTask5) {
                            mTimerTask5.cancel();
                            mTimerTask5 = null;
                        }
                    }
                }
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    /**
     * 获取一个最大值
     *
     * @param px
     * @param py
     * @param pz
     * @return
     */
    public int getMaxValue(int px, int py, int pz) {
        int max = 0;
        if (px > py && px > pz) {
            max = px;
        } else if (py > px && py > pz) {
            max = py;
        } else if (pz > px && pz > py) {
            max = pz;
        }

        return max;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class MyLocationListenerGD implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation location) {
//            location.getProvider();//  定位提供者  lbs:高德网络定位  gps:设备gps模块    getGpsAccuracyStatus()   getLocationType()
//            location.getLocationType();  // 获取定位结果来源

            if (location != null && isStrat && location.getErrorCode() == 0) {
                double mLatitude = location.getLatitude();      //纬度
                double mLongitude = location.getLongitude();    //经度
                altitude = location.getAltitude();              //海拔
                float speedw = location.getSpeed();             //TODO --- 速度
                String city = location.getCity();               //城市
                int errorcode = location.getLocationType();     // 当前为GPS定位
                locationTime = location.getTime();                 //定位时间

                Log.e(TAG, "lat = " + mLatitude + "; --- lng =" + mLongitude + "; --- speedw =" + speedw + "; --- locationTime =" + locationTime);

                if (isStrat && OutdoorRunActitivy.runstate == 0) {   // isStrat    -----  OutdoorRunActitivy.runstate == 0 正在运动中
                    if (points.size() == 0) { //第一个点时
                        com.amap.api.maps.model.LatLng latLng = new com.amap.api.maps.model.LatLng(mLatitude, mLongitude);   //经纬度
                        latLng = GPSCorrect.gcj2wgs(latLng);  //todo ----  高德到标准
                        Log.e(TAG, "lat = " + latLng.latitude + "; lng =" + latLng.longitude);
                        String ss = Utils.date2string(new Date(), Utils.YYYY_MM_DD_HH_MM);
                        date = ss.replace("-", ".");               //日期
                        points.add(latLng);                        //记录第一个点  （添加到经纬度的集合中）
                        points2.add(latLng);                       //记录第一个点
                        arrLat += String.valueOf(mLatitude) + "&";       //所有纬度拼接    //TODO ---  所有纬度拼接
                        arrLng += String.valueOf(mLongitude) + "&";      //TODO   所有经度拼接
                        arrspeed = arrspeed + String.valueOf(speedw) + "&";     //所有速度拼接   // todo --- 速度数组    ---- 地图定位的速度数组
//                        arrspeed = arrspeed + String.valueOf(mMile / count) + "&";//计算实时速度用于画详细图   // todo --- 速度数组   ---- 传感器的速度数组

                        pointLocation.add(location);
                        pointSpeedw.add(speedw);
                    } else {
                        com.amap.api.maps.model.LatLng next = new com.amap.api.maps.model.LatLng(mLatitude, mLongitude);      //经纬度
                        next = GPSCorrect.gcj2wgs(next);  // todo --- 第2个定位点    //todo ----  高德到标准
                        Log.e(TAG, "lat = " + next.latitude + "; lng =" + next.longitude);
                        double distance = 0.0;                    //距离

                        if (isStopStart) {  //isStopStart    todo  ---- 如果是暂停后的启动则第一个点的距离不要计算,并保存下一点
                            isStopStart = false;
                            points.add(next);
                            pointLocation.add(location);
                            pointSpeedw.add(speedw);
                            distance = 0.0;
                        } else {
                            if (pointLocation.size() < 2) {  // TODO -- 需要添加 判断
                                distance = AMapUtils.calculateLineDistance(points.get(points.size() - 1), next);      //获取两点之间的距离 （相邻两个点）
                                if (distance < 20) {  // 500
                                    points.add(next);
                                    pointLocation.add(location);
                                    pointSpeedw.add(speedw);
                                } else {
                                    points.clear();
                                    pointLocation.clear();
                                    pointSpeedw.clear();
//                                    isCorrectData = false;
                                }
                            } else {
                                distance = AMapUtils.calculateLineDistance(points.get(points.size() - 1), next);      //获取两点之间的距离 （相邻两个点）
                                if (distance < 20) {  // 500
                                    isCorrectData = true;
                                    locationTime = location.getTime();
                                    tMile = Utils.decimalTo2((tMile + distance), 2);     //实时总距离
                                    arrDistance += distance + "&";                       //每次进入记录距离
                                    points.add(next);
                                    arrspeed = arrspeed + String.valueOf(speedw) + "&";  //TODO --- ADD 0516   ;//计算实时速度用于画详细图   // todo --- 速度数组 ---- 速度图表数据(数据来源--地图)
//                                  arrspeed = arrspeed + String.valueOf(mMile / count) + "&";
                                    mMile = Utils.decimalTo2((mMile + distance), 2);     //总距离
                                    mKmMile = Utils.decimalTo2((mKmMile + distance), 2);   //km距离
                                    //TODO --- 计算每1km得配速   ---- 手机端除了室内跑,其他运动模式的，每千米的配速都ok,
                                    if (mKmMile >= 1000) {   // Utils.setformat(2, String.valueOf(mins)).split("\\.")   ---- Utils.setformat(2,)
                                        String totalKmPs = Utils.setformat(2, Utils.getPace2(String.valueOf(mKmCurrentCount), String.valueOf(mKmMile)));//记录每km配速 数组 --- 保留两位小数
                                        arrKmSpeed += totalKmPs + "&";   //todo --- 每千米的配速数组

                                        mCurKmMile += (int) (mKmMile / 1000); // todo --- 当前KM 的整数值
                                        mKmMile = mKmMile % 1000;//将大于1KM的余数保留计算下一个一千米  // todo ---- % 后 得到的 整数 呢 ？？？？？？？？？？？？？？？
                                        mKmCurrentCount = 0;
//                                        arrLat = arrLat.substring(0,arrLat.length()-1) + "KM" + mCurKmMile + "&";   // todo ---   当两点的距离大于 1KM 时，在经纬度的集合中添加 KM 的标志       22.554409&KM122.554409&
//                                        arrLng = arrLng.substring(0,arrLng.length()-1) + "KM" + mCurKmMile + "&";
                                        arrLat += String.valueOf(mLatitude) + "KM" + mCurKmMile + "&";   // todo ---   当两点的距离大于 1KM 时，在经纬度的集合中添加 KM 的标志       22.554409&KM122.554409&
                                        arrLng += String.valueOf(mLongitude) + "KM" + mCurKmMile + "&";
                                    } else {
                                        arrLat += String.valueOf(mLatitude) + "&";     //TODO ---  所有纬度拼接
                                        arrLng += String.valueOf(mLongitude) + "&";    //TODO ---  所有经度拼接
                                    }

                                    calorie = getCalories(mMile);//得到卡里路

                                    String totalPs = Utils.getPace2(String.valueOf(count), String.valueOf(mMile));//记录总距离总时间实时配速(用于计算最大，最小配速) 数组
                                    if (totalPs.equals("0")) {
                                        return;
                                    }
                                    arrTotalSpeed += totalPs + "&";     // TODO ---- 配速数组
                                    mCurrentCount = 0;

                                    mCurrentSpeed = getCurrentSpeed(count, mMile);//获取实时配速
                                    //Log.e(TAG,"mCurrentSpeed = " + mCurrentSpeed  +  "; count =" + count + "; mMile =" +mMile + "; totalPs =" +totalPs);
                                    int height = Integer.valueOf(Utils.gethight(getApplication()));//获取用户身高
                                    int feetweek = (int) (height * 0.45);// 走路步长 厘米
                                    int gps_bushu = (int) tMile * 100 / feetweek;//得到步数 （路程/步长）  tMile * 100 米转换成厘米

                                    Long mec = count;//得到秒数
                                    if (mec == 0) {
                                        mec = 1l;
                                    }
                                    int m2 = Integer.parseInt(mec.toString());

                                    int sportFen = m2 / 60;
                                    if (sportFen == 0) {
                                        sportFen = 1;
                                    }
                                    double buping = gps_bushu / sportFen;
//                                    double buping = gps_bushu / m2 * 60;      //得到步频（步数除以时间 单位分）

                                    bupingList.add(String.valueOf(buping));  // 步频数组
                                    arrcadence = arrcadence + String.valueOf(buping) + "&";    //TODO  --  步频数组
                                    Log.i("buping", buping + "");
                                    /**计算步幅**/
                                    if (gps_bushu == 0) {
                                        pjBf = 0;
                                    } else {
                                        pjBf = (int) (tMile * 100) / gps_bushu;
                                    }
                                    bufuList.add(String.valueOf(pjBf));
                                    pointLocation.add(location);
                                    pointSpeedw.add(speedw);
                                } else {
//                                  boolean isFlag = dealDataO(pointLocation.get(pointLocation.size() - 1).getTime(),location.getTime(),distance,speedw);  // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                                    // speedw可用 pointLocation 的数据
                                    boolean isFlag = dealDataO(pointLocation.get(pointLocation.size() - 2).getTime(), pointLocation.get(pointLocation.size() - 1).getTime(), location.getTime(), distance, pointSpeedw.get(pointSpeedw.size() - 1), speedw);
                                    if (!isFlag) {
                                        isCorrectData = false;
//                                        pointLocation.add(location);   // 错误的数据，位置和速度不应该保存
//                                        pointSpeedw.add(speedw);
                                    } else {
                                        isCorrectData = true;
                                        locationTime = location.getTime();
                                        tMile = Utils.decimalTo2((tMile + distance), 2);     //实时总距离
                                        arrDistance += distance + "&";                       //每次进入记录距离
                                        points.add(next);
//                                        arrLat += String.valueOf(mLatitude) + "&";     //TODO ---  所有纬度拼接
//                                        arrLng += String.valueOf(mLongitude) + "&";    //TODO ---  所有经度拼接

                                        arrspeed = arrspeed + String.valueOf(speedw) + "&";  //TODO --- ADD 0516   ;//计算实时速度用于画详细图   // todo --- 速度数组   ----     ---- 速度图表数据
//                                      arrspeed = arrspeed + String.valueOf(mMile / count) + "&";//计算实时速度用于画详细图
                                        mMile = Utils.decimalTo2((mMile + distance), 2);     //总距离
                                        mKmMile = Utils.decimalTo2((mKmMile + distance), 2);   //km距离

                                        //TODO --- 计算每1km得配速   ---- 手机端除了室内跑,其他运动模式的，每千米的配速都ok,
                                        if (mKmMile >= 1000) {
                                            String totalKmPs = Utils.setformat(2, Utils.getPace2(String.valueOf(mKmCurrentCount), String.valueOf(mKmMile)));//记录每km配速 数组
                                            arrKmSpeed += totalKmPs + "&";   //todo --- 每千米的配速数组

                                            mCurKmMile += (int) (mKmMile / 1000);

                                            mKmMile = mKmMile % 1000;//将大于1KM的余数保留计算下一个一千米
                                            mKmCurrentCount = 0;

                                            arrLat += String.valueOf(mLatitude) + "KM" + mCurKmMile + "&";   // todo ---   当两点的距离大于 1KM 时，在经纬度的集合中添加 KM 的标志       22.554409&KM122.554409&
                                            arrLng += String.valueOf(mLongitude) + "KM" + mCurKmMile + "&";
                                        } else {
                                            arrLat += String.valueOf(mLatitude) + "&";     //TODO ---  所有纬度拼接
                                            arrLng += String.valueOf(mLongitude) + "&";    //TODO ---  所有经度拼接
                                        }
                                        calorie = getCalories(mMile);//得到卡里路

                                        String totalPs = Utils.getPace2(String.valueOf(count), String.valueOf(mMile));//记录总距离总时间实时配速(用于计算最大，最小配速) 数组
                                        if (totalPs.equals("0")) {
                                            return;
                                        }
                                        arrTotalSpeed += totalPs + "&";     // TODO ---- 配速数组
                                        mCurrentCount = 0;

                                        mCurrentSpeed = getCurrentSpeed(count, mMile);//获取实时配速
                                        //Log.e(TAG,"mCurrentSpeed = " + mCurrentSpeed  +  "; count =" + count + "; mMile =" +mMile + "; totalPs =" +totalPs);
                                        int height = Integer.valueOf(Utils.gethight(getApplication()));//获取用户身高
                                        int feetweek = (int) (height * 0.45);// 走路步长 厘米
                                        int gps_bushu = (int) tMile * 100 / feetweek;//得到步数 （路程/步长）  tMile * 100 米转换成厘米

                                        Long mec = count;//得到秒数
                                        if (mec == 0) {
                                            mec = 1l;
                                        }
                                        int m2 = Integer.parseInt(mec.toString());

                                        int sportFen = m2 / 60;
                                        if (sportFen == 0) {
                                            sportFen = 1;
                                        }
                                        double buping = gps_bushu / sportFen;
//                                        double buping = gps_bushu / m2 * 60;      //得到步频（步数除以时间 单位分）
                                        bupingList.add(String.valueOf(buping));  // 步频数组
                                        arrcadence = arrcadence + String.valueOf(buping) + "&";    //TODO  --  步频数组
                                        Log.i("buping", buping + "");
                                        //计算步幅
                                        if (gps_bushu == 0) {
                                            pjBf = 0;
                                        } else {
                                            pjBf = (int) (tMile * 100) / gps_bushu;
                                        }
                                        bufuList.add(String.valueOf(pjBf));
                                        pointLocation.add(location);
                                        pointSpeedw.add(speedw);
                                    }
                                }//////
                            }
                        }
                    }
                }

                // TODO -- 条件，     && OutdoorRunActitivy.runstate == 0    -----  OutdoorRunActitivy.runstate == 0 正在运动中
                if (isStrat && sportMode != 3 && isCorrectData && OutdoorRunActitivy.runstate == 0) {   //  && !isPhoneStatic todo ---  除了室内跑，不需要判断手机是否静止，只要有GPS数据即为运动数据
                    Intent intent = new Intent();// 创建Intent对象
                    intent.setAction(SEND_RECEIVER_GPS);  // 高德地图的GPS数据
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude_gps", mLatitude);
                    bundle.putDouble("longitude_gps", mLongitude);
                    bundle.putFloat("speed", speedw);
                    bundle.putSerializable("points", points);
                    bundle.putDouble("altitude", altitude);  // TODO ---- 海拔值
                    bundle.putDouble("mile", mMile);
                    bundle.putDouble("calorie", calorie);
                    bundle.putInt("Satenum", Satenum);
                    bundle.putString("peisu", mCurrentSpeed);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);// 发送广播

                    arraltitude += altitude + "&";   // todo --- 海拔数组  add 0516
                    Log.i(TAG, "发送广播:" + Satenum + " ");
                }
            }
        }
    }


    private boolean dealDataO(long oneTime, long nextTime, double distance, float speed) {   //手机运动数据偏移处理
        long between = (nextTime - oneTime) / 1000;
        double mile = (double) (between * speed);
        Log.e(TAG, "mile =" + mile + "  distance =" + distance + "  speed = " + speed + "   between =" + between);
        if (mile > 200) {  // 500
            return false;
        } else {
            return true;
        }
    }

    //  totalPs==0.0&3.4805517&2.8062656&0.0&0.0&0.0&0.0&0.0&0.0&0.0&0.0&0.0&3.2092931&3.4681983&2.9297988&0.0&0.0&0.0&0.0&0.0&0.0&0.0&
    //  lat = 22.554206; --- lng =113.951553; --- speedw =0.0; --- locationTime = 14967 36268 632
    private boolean dealDataO(long lastTwoTime, long lastoneTime, long curTime, double distance, float lastSpeed, float speed) {   //手机运动数据偏移处理
        long lastInterval = (lastoneTime - lastTwoTime) / 1000; // 上一个定位的时间间隔  单位：秒
        long curInterval = (curTime - lastoneTime) / 1000; // 当前定位的时间间隔   单位：秒

        double lastmile = (double) (lastInterval * lastSpeed); // 上一次定位间隔的距离  单位：米
        double curmile = (double) (curInterval * speed);  // 当前定位间隔的距离

        if (lastInterval < 10) { //  5
            if (curInterval < 10) {
                if (lastmile < 100 && curmile < 100 && distance < 100) { // 为漂点   --- 调整该值调试    100  lastmile < 200 && curmile < 200 && distance < 500
                    return true;
                } else { //为
                    return false;
                }
            } else {
                if (lastmile < 100 && curmile > 200) { // 为漂点   --- 调整该值调试  lastmile < 200 && curmile > 200
                    if (curInterval < 60) {   // ?????????????????  正常定位到一段时间没有定位
                        if (distance > 1500) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } else {
            if (curInterval < 10) {
                if (lastmile > 200 && curmile < 100) { // 为漂点   --- 调整该值调试  lastmile > 200 && curmile < 200
                    return true;
                } else { //为 (进地铁)，长时间无定位，到当前有定位--- 默认为正常的数据
                    return false;
                }
            } else {
                if (curInterval < 60) {
                    if (distance > 1500) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }


     /*  long between = (nextTime - oneTime)/1000;
       double mile = (double)(between * speed);    // todo --- 两次定位时间间隔 （30*60*10 = 18000m = 18km）
       Log.e(TAG,"mile =" + mile  +  "  distance =" + distance + "  speed = " + speed + "   between =" + between);
       if(mile > 200){  // 500
           return false;
       }else{
           return true;
       }*/
    }

    /**
     * 获取实时配速
     *
     * @param time
     * @param mile
     * @return
     */
    private String getCurrentSpeed(long time, double mile) {

        String arrPs[] = Utils.getPace(String.valueOf(count), String.valueOf(mMile));//得到当前实时配速 数组
//                String arrPs[] = Utils.getPace(String.valueOf(mCurrentCount), String.valueOf(ssDistance));//得到当前实时配速 数组
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        String mCurrentSpeed = String.format(Locale.ENGLISH,"%1$01d'%2$01d''", Integer.valueOf(m), (int) sec);
        return mCurrentSpeed;
    }

    /**
     * 获取卡路里
     *
     * @param mMile
     * @return
     */
    private int getCalories(double mMile) {
        /**得到用户体重**/
        String wh = SharedPreUtil.readPre(getApplication(),
                SharedPreUtil.USER, SharedPreUtil.WEIGHT);
        int weight = 60;
        if (wh.equals("")) {
            weight = 60;
        } else {
            weight = Integer.valueOf(wh);
        }
        int calorie = (int) Utils.decimalTo2(weight * mMile * 1.036 / 1000, 2);//消耗  千卡
        return calorie;
    }


    //todo --- 自动停止打开，自动暂停打开 未运动 10 分钟 结束运动
    class MyTask3 extends TimerTask {
        @Override
        public void run() {
            if (time > 0) {   //time = 180
                time--;
                Log.e(TAG, "自动暂停倒计时时间---" + time);
            } else {
                if (null != timer3) {
                    timer3.cancel();
                    timer3 = null;
                }
                if (null != timer4) {
                    timer4.cancel();
                    timer4 = null;
                }
                if (null != timer5) {
                    timer5.cancel();
                    timer5 = null;
                }
                sendBroadcast(new Intent(SportService.SEND_RECEIVER_AUTO_PFINISH));//自动结束广播
//                handler.sendEmptyMessage(10002);
            }
        }
    }

    //todo --- 自动停止打开，自动暂停关闭 未运动 5 分钟 结束运动
    class MyTask5 extends TimerTask {
        @Override
        public void run() {
            if (time3 > 0) {   //time = 180
                time3--;
                Log.e(TAG, "自动停止倒计时时间 ----- " + time3);
            } else {
                if (null != timer3) {
                    timer3.cancel();
                    timer3 = null;
                }
                if (null != timer4) {
                    timer4.cancel();
                    timer4 = null;
                }
                if (null != timer5) {
                    timer5.cancel();
                    timer5 = null;
                }
                sendBroadcast(new Intent(SportService.SEND_RECEIVER_AUTO_PFINISH));//自动结束广播
//                handler.sendEmptyMessage(10002);
            }
        }
    }

    /**
     * 15s倒计时自动暂停
     */
    class MyTask4 extends TimerTask {  //TODO --- 15s倒计时自动暂停
        @Override
        public void run() {
            if (time2 > 0) {  // time2 = 15
                time2--;
                Log.e(TAG, "开启15秒进入自动暂停倒计时了，倒计时时间---" + time2);
            } else {  // 15 秒 倒计时结束
                if (null != timer4) {
                    timer4.cancel();
                    timer4 = null;
                }
                isAutoPause = false;    // 自动暂停
                pauseNumber++;

                pauseTime();//开始记录暂停时间
                isPause = true;//暂停运动
                sendBroadcast(new Intent(SportService.SEND_RECEIVER_AUTO_PAUSE));
//                handler.sendEmptyMessage(10003);
            }
        }
    }


    /**
     * 结束运动
     */
    private void endSport() {
        if (null != timer3) {
            timer3.cancel();
            timer3 = null;
        }
        if (null != timer4) {
            timer4.cancel();
            timer4 = null;
        }
        if (null != timer5) {
            timer5.cancel();
            timer5 = null;
        }

        time = 600;
        time3 = 300;
        time2 = 15;

        isStrat = false;
        isStop = false;

        if (sportMode == 3) {//室内跑  TODO -- sportMode == 5
            isFirstStep = true; // TODO --- 将室内跑开始运动的标志 置为 true，统计下一次数据时 ，从0开始
            mShineiKmMile = 0; //结束运动时将室内跑每千米的配速清0
            mKmCurrentCount = 0;
            mKmMile = 0;
        }


        saveGps();

        System.out.println(gpsDeatils);

        stopTimer();
        finishInitDate();
        stopSelf();//停止服务
    }

    private void saveGps() {
        long timeMillis = System.currentTimeMillis() / 1000;
        /**记录实时数据，用于详情展示**/
        for (int i = 0; i < bufuList.size(); i++) {
            if (latList.size() > 0) {
                GpsPointData gpsPointData = new GpsPointData(mac, mid + "mid", "", "", "", "", "", timeMillis + "", bufuList.get(i));
                gpsPointDataList.add(gpsPointData);   // 所有GPS点的数据
            }
        }
        String ss = Utils.date2string(new Date(), Utils.YYYY_MM_DD_HH_MM);
        date = ss.replace("-", ".");//得到系统时间

        /**得到最大 最小步幅**/
        // todo --- 是否需要过滤掉 步幅为 0 的值，否则，最小步幅会显示为0
        int okBufuSize = 0;
        long okAllBufu = 0;
        if (bufuList.size() > 0) {
            for (int i = 0; i < bufuList.size(); i++) {  // todo  --- 过滤步副为 0 的数据
//                if(Integer.valueOf(bufuList.get(i)) > 30 && Integer.valueOf(bufuList.get(i)) < 150){
                okBufuSize++;
                okAllBufu += Integer.valueOf(bufuList.get(i));
//                }
            }
            okpjBf = (int) (okAllBufu / okBufuSize);
        }

        int maxB = Integer.valueOf(Collections.max(bufuList));
        int minB = Integer.valueOf(Collections.min(bufuList));

        if (maxB < minB) {
            maxBf = minB;
            minBf = maxB;
        } else {
            maxBf = maxB;
            minBf = minB;
        }

        if (okpjBf <= minBf) {
            okpjBf = minBf + 1;
        }

        if (pauseNumber <= 1) {
            shijian2 = "00:00:00";
        }

        boolean tempUpdateFlag;
        long tempId = 0;
        if(gpsDeatils == null){
            tempUpdateFlag = false;
        }else{
            tempUpdateFlag = true;
            tempId = db.getGpsPointDetailDao().getKey(gpsDeatils);
        }

        gpsDeatils = new GpsPointDetailData(mac, mid + "mid", mMile,
                (int) altitude + "", date + "", arrKmSpeed + "", shijian + "",
                (int) calorie + "", count + "", arrLat + "", arrLng + "", mCurrentSpeed + "", arrTotalSpeed + "",
                timeMillis + "", sportMode + "", mHeartRate, deviceType, pauseNumber - 1 + "", shijian2, arrspeed, arrcadence,
                arraltitude, "0", minBf + "", maxBf + "", okpjBf + "","");  // okpjBf  --- pjBf

        if(tempUpdateFlag){
            gpsDeatils.setId(tempId);
        }

        recordGpsPointForDataBase(gpsDeatils, gpsPointDataList,tempUpdateFlag);// 写数据库======     不到这里本地保存了---- 待数据上传成功时，将后台返回的id添加进gpsDeatils中，然后再本地保存
    }


    /**
     * 暂停运动
     */
    private void pauseSprot() {
        isPause = true;//暂停运动
        isStrat = false;
        stopTimer();
    }


    // 运动界面和服务器界面的交互 广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                
                if(PERMISION_GRANTED_GPS.equals(action)){
                    initLocationGps();
                    Logg.e(TAG, "onReceive: ");
                }
                
                if (CMD_RECEIVER.equals(action)) {
                    int cmd = intent.getIntExtra("cmd", -1);// 获取Extra信息
                    switch (cmd) {
                        case OutdoorRunActitivy.CMD_START_SPORTS://TODO 运动 开始   ----- 服务收到广播，开始运动
                            Log.i(TAG, "运动开始");
                            isAutoPause = true;//自动暂停控制变量
                            isFirstSport = true;  // 第一次进来要有运动后才开启自动暂停
                            time = 600;
                            time2 = 15;

                            isStopStart = true;
                            isStrat = true;
                            isPause = false;//false 表示启动运动  true表示运动暂停

                            stopTimer2();//停止记录暂停时间
                            break;
                        case OutdoorRunActitivy.CMD_PAUSE_SPORTS://TODO ---- 运动 暂停
                            Log.i(TAG, "运动暂停");
                            isAutoPause = false;
                            //记录暂停的总时间
                            pauseNumber++;
                            pauseTime();//开始记录暂停时间
                            isPause = true;//暂停运动
                            break;
                        case OutdoorRunActitivy.CMD_STOP_SERVICE://TODO --- 运动停止  停止服务 表示上传成功    ------- 停止运动（结束本次运动）
                            Log.i(TAG, "运动停止");
                            count_10_min.stopTimer();
                            saveSportData();
                            break;
                        case OutdoorRunActitivy.CMD_FINISH_SERVICE:// 停止服务 不上传数据
                            Log.i("11111", "运动停止,小于10M不保存数据");
                            count_10_min.stopTimer();
                            finishInitDate();
                            stopSelf();
                            break;
                    }
                }
                if (action.equals(Intent.ACTION_SCREEN_ON)) {// 手机屏幕显示
//                    mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//                    mSensorManager.registerListener(SportService.this, mStepDetector,
//                            SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME
                }
                if (action.equals("ELITOR_CLOCK")) {
                    Log.i("service111", "时钟服务...........");
                }

                if (action.equals(MainService.ACTION_SPORTMODE_AUTOPAUSE) || action.equals(MainService.ACTION_SPORTMODE_AUTOSTOP)) {
                    if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOSTOP).equals(SharedPreUtil.YES)) {  // 自动停止
                        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOPAUSE).equals(SharedPreUtil.YES)) { // 自动暂停
                            if (null != timer5) {  // todo --- add 0523
                                timer5.cancel();
                                timer5 = null;
                            }
                            if (null != mTimerTask5) {
                                mTimerTask5.cancel();
                                mTimerTask5 = null;
                            }


                            if (null == timer4) {
                                timer4 = new Timer();
                            } else {
                                timer4.cancel();
                                timer4 = new Timer();
                            }

                            if (null == mTimerTask4) {
                                mTimerTask4 = new MyTask4();
                            } else {
                                mTimerTask4.cancel();
                                mTimerTask4 = new MyTask4();
                            }

                            time2 = 15;
                            timer4.schedule(mTimerTask4, 0, 1000); //TODO ---  开启 15 秒 未动进入 自动暂停（开始记录 暂停时间）   timer4.schedule(new MyTask4(), 0, 1000);
                            Log.e(TAG, "开启15秒进入自动暂停倒计时了");

                            if (null == timer3) {
                                timer3 = new Timer();
                            } else {
                                timer3.cancel();
                                timer3 = new Timer();
                            }

                            if (null == mTimerTask3) {
                                mTimerTask3 = new MyTask3();
                            } else {
                                mTimerTask3.cancel();
                                mTimerTask3 = new MyTask3();
                            }
                            time = 600; //
                            timer3.schedule(mTimerTask3, 0, 1000); //   timer3.schedule(new MyTask3(), 0, 1000);
                            Log.e(TAG, "进入自动暂停倒计时了");
                        } else {
                            if (null != timer3) {  // todo --- add 0523
                                timer3.cancel();
                                timer3 = null;
                            }
                            if (null != mTimerTask3) {
                                mTimerTask3.cancel();
                                mTimerTask3 = null;
                            }

                            if (null != timer4) {  // todo --- 取消 15 秒 倒计时
                                timer4.cancel();
                                timer4 = null;
                            }
                            if (null != mTimerTask4) {
                                mTimerTask4.cancel();
                                mTimerTask4 = null;
                            }

                            if (null == timer5) {
                                timer5 = new Timer();
                            } else {
                                timer5.cancel();
                                timer5 = new Timer();
                            }

                            if (null == mTimerTask5) {
                                mTimerTask5 = new MyTask5();
                            } else {
                                mTimerTask5.cancel();
                                mTimerTask5 = new MyTask5();
                            }

                            time3 = 300;
                            timer5.schedule(mTimerTask5, 0, 1000);  //  timer5.schedule(new MyTask5(), 0, 1000);
                            Log.e(TAG, "进入自动停止倒计时了");
                        }
                    } else { // 自动停止关闭了
                        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_AUTOPAUSE).equals(SharedPreUtil.YES)) { // 自动暂停

                            if (null != timer3) {  // todo --- add 0523
                                timer3.cancel();
                                timer3 = null;
                            }
                            if (null != mTimerTask3) {
                                mTimerTask3.cancel();
                                mTimerTask3 = null;
                            }

                            if (null != timer5) {  // todo --- add 0523
                                timer5.cancel();
                                timer5 = null;
                            }
                            if (null != mTimerTask5) {
                                mTimerTask5.cancel();
                                mTimerTask5 = null;
                            }

                            if (null == timer4) {
                                timer4 = new Timer();
                            } else {
                                timer4.cancel();
                                timer4 = new Timer();
                            }

                            if (null == mTimerTask4) {
                                mTimerTask4 = new MyTask4();
                            } else {
                                mTimerTask4.cancel();
                                mTimerTask4 = new MyTask4();
                            }

                            time2 = 15;
                            timer4.schedule(mTimerTask4, 0, 1000); //TODO ---  开启 15 秒 未动进入 自动暂停（开始记录 暂停时间）   timer4.schedule(new MyTask4(), 0, 1000);
                            Log.e("jinru", "开启15秒进入自动暂停倒计时了");

                        } else { // 自动暂停关闭了
                            if (null != timer4) {  // todo --- 取消 15 秒 倒计时
                                timer4.cancel();
                                timer4 = null;
                            }
                            if (null != mTimerTask4) {
                                mTimerTask4.cancel();
                                mTimerTask4 = null;
                            }

                            if (null != timer3) {  // todo --- add 0523
                                timer3.cancel();
                                timer3 = null;
                            }
                            if (null != mTimerTask3) {
                                mTimerTask3.cancel();
                                mTimerTask3 = null;
                            }

                            if (null != timer5) {  // todo --- add 0523
                                timer5.cancel();
                                timer5 = null;
                            }
                            if (null != mTimerTask5) {
                                mTimerTask5.cancel();
                                mTimerTask5 = null;
                            }
                        }

                    }
                }
            }
        }
    };

    private void saveSportData() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                endSport();   // 停止运动时，保存数据到本地数据库
            }
        });
        t.start();
    }


    /**
     * 记录数据库 gps运动数据
     */
    private void recordGpsPointForDataBase(GpsPointDetailData gpsDeatils, ArrayList<GpsPointData> gpsPointDataList,boolean isUpdate) {
        if (gpsDeatils != null)// 写经纬度
        {
            /**写入实时数据到数据库**/
            if (db == null) {
                db = DBHelper.getInstance(getApplicationContext());
            }

            if(isUpdate){
                db.updataGpsPointDetailData(gpsDeatils);
            }else{
                db.saveGpsPointDeatilData(gpsDeatils);
            }

            for (int i = 0; i < gpsPointDataList.size(); i++) {
                db.saveGpsPointData(gpsPointDataList.get(i));
            }
            //写入数据库成功后 计时结束暂停服
//            stopSelf();// 停止服务
        }
    }


    /**
     * 记录暂停时间
     */
    public void pauseTime() {    // 开始记录暂停的时间
        if (null == timer2) {
            timer2 = new Timer();
        } else {
            timer2.cancel();
            timer2 = new Timer();
        }
        if (null == mTimerTask2) {
            mTimerTask2 = new MyTask2();
        } else {
            mTimerTask2.cancel();
            mTimerTask2 = new MyTask2();
        }
        timer2.schedule(mTimerTask2, 1000, 1000);
    }

    /**
     * 开始运动
     */
    public void startSport() {  // todo -- 服务一创建 即开始记录 运动数据
        isStrat = true;  // todo --- 开始运动了
        isPause = false;//false 表示启动运动  true表示运动暂停    false 表示启动运动  true表示运动暂停
        if (null == timer) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }
        if (null == mTimerTask) {
            mTimerTask = new MyTask();
        } else {
            mTimerTask.cancel();
            mTimerTask = new MyTask();
        }
        timer.schedule(mTimerTask, 1000, 1000);  // todo --- 需要延时 1秒  ？？？？？

    }

    class MyTask extends TimerTask {   //todo ----  服务一创建就开始记录数据
        @Override
        public void run() {
            if (OutdoorRunActitivy.runstate == 0) {
                if (!isAutoPause)//TODO ---- 为ture启动  为false自动暂停
                    return;   // todo -- 自动暂停了 ， 不记录数据

                mCurrentCount++;   //当前时间 --- 开始记录当前运动用时
                mKmCurrentCount++;   //每1km当前时间 --- 开始记录当前每千米运动用时
                count++;     // 时间变量    ---- 该定时器定时频率为 1000ms (每隔一秒 ，时间变量 加 1)

                Intent intent = new Intent();
                intent.setAction(SEND_RECEIVER_TIME); // todo  --- 发送 当前运动用时
                Bundle bundle = new Bundle();
                bundle.putLong("count", count);
                bundle.putInt("Satenum", Satenum);   //gps强度
                Log.i("count", "秒数 count = " + count);

                if (null == startTime) {
                    startTime = Utils.date2string(new Date(), Utils.YYYY_MM_DD_HH_MM_SS);  // 运动开始时间赋值
                }
                bundle.putString("startTime", startTime);  // 开始时间
                intent.putExtras(bundle);
                mContext.sendBroadcast(intent);// 发送广播    ------ 运动开始的时间


                int totalSec = 0;
                int yunshu = 0;
                totalSec = (int) (count / 60); // 总分钟数
                yunshu = (int) (count % 60);   // 有效秒数
                int mai = totalSec / 60;   // 有效小时数
                int sec = totalSec % 60;   // 有效分钟数
                try {
                    shijian = String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai, sec, yunshu);   // 运动中的当前时间
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            handler.sendEmptyMessage(1);
            }
        }
    }

    /**
     * 暂停计时
     */
    class MyTask2 extends TimerTask {    // todo ----   暂停计时  （记录暂停的时间）
        @Override
        public void run() {
            count2++;
            int totalSec2 = 0;
            int yunshu2 = 0;
            totalSec2 = (int) (count2 / 60);
            yunshu2 = (int) (count2 % 60);
            int mai2 = totalSec2 / 60;
            int sec2 = totalSec2 % 60;
            try {
                shijian2 = String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai2, sec2, yunshu2);   // 暂停时的时间长度
                Log.e(TAG, "暂停时长 = " + shijian2);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            handler.sendEmptyMessage(2);
        }

    }

    private void stopTimer() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }

//        if (mTimerTask != null) {
//            mTimerTask.cancel();
//            mTimerTask = null;
//        }
    }


    /**
     * 暂停运动时间
     */
    private void stopTimer2() {
//        isStop = false;
        if (null != timer2) {
            timer2.cancel();
            timer2 = null;
        }

        if (null != mTimerTask2) {
            mTimerTask2.cancel();
            mTimerTask2 = null;
        }
    }


//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:// 时间
//                    mCurrentCount++;
//                    mKmCurrentCount++;
//                    count++;
//                    Intent intent = new Intent();// 创建Intent对象
//                    intent.setAction(SEND_RECEIVER_TIME);
//                    Bundle bundle = new Bundle();
//                    bundle.putLong("count", count);
//                    bundle.putInt("Satenum", Satenum);
//                     Log.i("count", "秒数 count = "+count);
//                    if (null == startTime) {
//                        startTime = Utils.date2string(new Date(), Utils.YYYY_MM_DD_HH_MM_SS);
//                    }
//                    bundle.putString("startTime", startTime);
//                    intent.putExtras(bundle);
//                    mContext.sendBroadcast(intent);// 发送广播
//                    int totalSec = 0;
//                    int yunshu = 0;
//                    totalSec = (int) (count / 60);
//                    yunshu = (int) (count % 60);
//                    int mai = totalSec / 60;
//                    int sec = totalSec % 60;
//                    try {
//                        shijian = String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai, sec, yunshu);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case 2://暂停计时
//                    count2++;
//                    int totalSec2 = 0;
//                    int yunshu2 = 0;
//                    totalSec2 = (int) (count2 / 60);
//                    yunshu2 = (int) (count2 % 60);
//                    int mai2 = totalSec2 / 60;
//                    int sec2 = totalSec2 % 60;
//                    try {
//                        shijian2 = String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", mai2, sec2, yunshu2);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case 10002://自动结束运动时间结束
//                    sendBroadcast(new Intent(SportService.SEND_RECEIVER_AUTO_PFINISH));
//                    break;
//                case 10003://自动暂停
//                    pauseNumber++;
//                    pauseTime();//开始记录暂停时间
//                    pauseSprot();
//                    sendBroadcast(new Intent(SportService.SEND_RECEIVER_AUTO_PAUSE));
//                    break;
//            }
//        }
//    };

    /**
     * 结束服务初始化数据
     */
    public void finishInitDate() {
        stopTimer();
        stopTimer2();
        if (null != timer3) {
            timer3.cancel();
            timer3 = null;
        }
        if (null != timer4) {
            timer4.cancel();
            timer4 = null;
        }
        if (null != timer5) {
            timer5.cancel();
            timer5 = null;
        }
        if (mStepDetector != null || mSensorAccelerometer != null) {//注销传感器监听
            mSensorManager.unregisterListener(this);
        }

//        handler.removeCallbacksAndMessages(null);
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();// 结束地图定位
        }
        this.unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
        calorie = 0;
        mMile = 0.00;
        isWook = false;
        count = 0;
        time = 600;
        time3 = 300;
        time2 = 15;
        arrLat = "";
        arrLng = "";
        arrspeed = "";
        arrcadence = "";
        arraltitude = "";
        points.clear();
        points2.clear();

        pointLocation.clear();
        pointSpeedw.clear();

        latList.clear();
        lngList.clear();
        speedList.clear();
        bupingList.clear();
        bufuList.clear();
        altitudeList.clear();
        gpsPointDataList.clear();
        Util.SPORT_STATUS = 0;//表示运动启动标识
        if (locationManager != null) {
            locationManager.removeGpsStatusListener(statusListener);
            Log.i(TAG, "locationManager.removeGpsStatusListener.");
        }
        am.cancel(pii);
        wl.release(); // 释放锁，显示的释放，如果申请的锁不在此释放系统就不会进入休眠。
        Log.i("Map", "服务销毁......00000000000000");
    }

    @Override
    public void onDestroy() {
        Log.i("Map", "服务销毁......");
//        finishInitDate();
        super.onDestroy();
    }



//    /**
//     * 结束运动，将数据保存到数据库
//     */
//    public void finshSport() {
//        isSave = true;
////        stopSelf();
////        if (locationManager != null && locationListener != null && gpsSatelliteListener != null) {
////            locationManager.removeUpdates(locationListener);
////            locationManager.removeGpsStatusListener(gpsSatelliteListener);
////            Log.i(TAG, "locationManager.removeGpsStatusListener.");
////        }
////        saveGpsData(mLatitude, mLongitude, altitude, speed, Satenum);//保存当前gps所有数据
//    }
}
