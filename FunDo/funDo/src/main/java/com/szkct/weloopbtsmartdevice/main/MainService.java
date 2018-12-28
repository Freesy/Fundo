package com.szkct.weloopbtsmartdevice.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.view.KeyEvent;
import com.google.gson.Gson;
import com.kct.bluetooth.KCTBluetoothManager;
import com.kct.bluetooth.bean.BluetoothLeDevice;
import com.kct.bluetooth.callback.IConnectListener;
import com.kct.bluetooth.callback.IDialogCallback;
import com.kct.fundo.btnotification.R;
import com.mediatek.camera.service.RemoteCameraController;
import com.mediatek.ctrl.fota.common.FotaOperator;
import com.mediatek.ctrl.map.MapController;
import com.mediatek.ctrl.music.RemoteMusicController;
import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.sos.SOSController;
import com.mediatek.ctrl.sync.DataSyncController;
import com.mediatek.ctrl.yahooweather.YahooWeatherController;
import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.leprofiles.bas.BatteryChangeListener;
import com.mediatek.wearable.VxpInstallController;
import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.bluetoothle.KCTDefaultAlerter;
import com.mtk.app.bluetoothle.LocalPxpFmpController;
import com.mtk.app.notification.AppList;
import com.mtk.app.notification.CallService;
import com.mtk.app.notification.IgnoreList;
import com.mtk.app.notification.NotificationReceiver;
import com.mtk.app.notification.NotificationService;
import com.mtk.app.notification.SmsService;
import com.mtk.app.remotecamera.CameraActivity;
import com.mtk.app.remotecamera.RemoteCameraService;
import com.mtk.app.thirdparty.EXCDController;
import com.mtk.app.thirdparty.MREEController;
import com.szkct.bluetoothgyl.BleCmdBean;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L1Bean;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.bluetoothgyl.UtilsLX;
import com.szkct.bluetoothservice.RingService;
import com.szkct.bluetoothtool.AppValueCheckbox;
import com.szkct.bluetoothtool.StepData;
import com.szkct.lock.LockReceiver;
import com.szkct.weloopbtsmartdevice.activity.CalibrationActivity;
import com.szkct.weloopbtsmartdevice.activity.LinkBleActivity;
import com.szkct.weloopbtsmartdevice.activity.WatchPushActivityNew;
import com.szkct.weloopbtsmartdevice.data.DailyForecast;
import com.szkct.weloopbtsmartdevice.data.WeatherCity;
import com.szkct.weloopbtsmartdevice.data.greendao.AlarmClockData;
import com.szkct.weloopbtsmartdevice.data.greendao.Bloodpressure;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Oxygen;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.Temperature;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.AlarmClockDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.EcgDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDetailDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.TemperatureDao;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.DeviceUtils;
import com.szkct.weloopbtsmartdevice.util.HidConncetUtil;
import com.szkct.weloopbtsmartdevice.util.LoadPackageTask;
import com.szkct.weloopbtsmartdevice.util.MediaManager;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.MobileInfoUtils;
import com.szkct.weloopbtsmartdevice.util.NumberBytes;
import com.szkct.weloopbtsmartdevice.util.PhoneUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.MyDevicePolicyManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.dao.query.Query;

import static com.szkct.weloopbtsmartdevice.main.BTNotificationApplication.needReceDataNumber;
import static com.szkct.weloopbtsmartdevice.main.BTNotificationApplication.needSendDataType;


/**
 * This class is the main service, it will process the most logic and interact
 * with other modules.
 */
public final class MainService extends Service {

    public static final String CHANGE_USER = "com.kct.CHANGE_USER"; // 切换账号了


    private AudioManager am = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothManager bluetoothManager;
    private ComponentName componentName;
    private AudioManager myAudioManager = null;
    private DevicePolicyManager policyManager;
    public AppValueCheckbox checkbox;
    private AlertDialog.Builder builder = null;
    private AlertDialog alert = null;
    private boolean findPhoneFlags = true;
    public int ringerMode;
    public boolean ringerFlag = false;
    public int ringerEnd;
    public static Long offtime = (long) 0;
    public static Long syontime = (long) 0;
    public static Long linktime = (long) 0;
    private Timer autoTimer = new Timer();
    int count = 0;
    String matime=null;
    String xieyangmatime=null;
    String xinlvgmatime=null;
    // Debugging
    private static final String TAG = "AppManager/MainService";

    public static final String EXTRA_COMMAND_REQUEST = "KCT_PEDOMETER kct_pedometer 0 0 5 GET,0";

    public static final String ACTION_BLUETOOTH_SEND_EXCD_CMD = "com.mtk.ACTION_BLUETOOTH_SEND_EXCD_CMD";
 	public static final String ACTION_GESTURE_ON = "com.kct.ACTION_GESTURE_ON";      //设备端发送了抬手亮屏开的命令
    public static final String ACTION_GESTURE_OFF = "com.kct.ACTION_GESTURE_OFF";      //设备端发送了抬手亮屏开的命令

    public static final String ACTION_FINDPHONE = "com.kct.ACTION_FINDPHONE";

    public static final String ACTION_MACCHANGE = "com.kct.ACTION_MACCHANGE";

    public static final String ACTION_SYNFINSH_SUCCESS = "com.kct.ACTION_SYNFINSH_SUCCESS";      //实时步数的广播

//    public static final String ACTION_SSHEARTFINSH = "com.kct.ACTION_SSHEARTFINSH";      // 实时心率（ble）

    public static final String ACTION_NO_UNITS = "com.kct.ACTION_NO_UNITS";      // 不支持单位设置

    public static final String ACTION_SYNC_BLECONNECT = "com.kct.ACTION_SYNC_BLECONNECT";      // BLE连接自动同步
    public static final String ACTION_SYNFINSH = "com.kct.ACTION_SYNFINSH";      // 数据同步
    public static final String ACTION_SYNFINSH_SPORTS = "com.kct.ACTION_SPORTSSYNFINSH";
    public static final String ACTION_SYNNOTDATA = "com.kct.ACTION_SYNNOTDATA";   //数据同步成功，但暂无数据
    public static final String ACTION_SYNARTHEART = "com.kct.ACTION_SYNARTHEART";   //实时同步心率
    public static final String ACTION_SYNARTBO = "com.kct.ACTION_SYNARTBO";//实时同步血氧
    public static final String ACTION_SYNARTBP = "com.kct.ACTION_SYNARTBP";  //实时同步血压
    public static final String ACTION_FINDWATCHON = "com.kct.ACTION_FINDWATCHON";   //打开查找手机
    public static final String ACTION_FINDWATCHOFF = "com.kct.ACTION_FINDWATCHOFF";   //关闭查找手机


    public static final String ACTION_BLECONNECTED = "com.kct.ACTION_BLECONNECTED";
    public static final String ACTION_BLEDISCONNECTED = "com.kct.ACTION_BLEDISCONNECTED";
    public static final String ACTION_UNABLECONNECT = "com.kct.ACTION_UNABLECONNECT";
    public static final String ACTION_USERDATACHANGE = "com.kct.ACTION_USERDATACHANGE";
    public static final String ACTION_GETWATCH = "com.kct.ACTION_GETWATCH";
    public static final String ACTION_BLETYPE = "com.kct.ACTION_BLETYPE";
    public static final String ACTION_WEATHER = "android.intent.action.WEATHER";
    public static final String ACTION_WIFIINFO = "com.kct.ACTION_WIFIINFO";
    public static final String ACTION_WIFI_STATE = "com.kct.ACTION_WIFI_STATE";
    public static final String UNABLE_TO_CONNECT_DEVICE = "Unable_to_connect_device";
    public static final String ACTION_AUTOCONNECT_DEVICE = "com.kct.autoconnect_device";
    public static final String ACTION_BLEDISCONNECT = "com.kct.ACTION_BLEDISCONNECT";

    public static final String ACTION_BLECONNECTHID = "com.kct.ACTION_BLECONNECTHID";

    public static final String ACTION_THEME_CHANGE = "com.kct.ACTION_THEME_CHANGE";
    public static final String ACTION_MYINFO_CHANGE = "com.kct.ACTION_MYINFO_CHANGE";

    public static final String ACTION_CLOCK_SKIN_MODEL_CHANGE = "ACTION_CLOCK_SKIN_MODEL_CHANGE";

    public static final String ACTION_MOTION_GOAL_CHANGE = "com.kct.ACTION_MOTION_GOAL_CHANGE";

    public static final String ACTION_LX_GETDATA = "com.mtk.ACTION_LX_GETDATA";

    public static final String ACTION_SPORTMODE_HINT = "com.mtk.ACTION_SPORTMODE_HINT";

    public static final String ACTION_CHANGE_WATCH = "com.mtk.ACTION_CHANGE_WATCH";  // 连接的设备类型更换了

    public static final String ACTION_SPORTMODE_AUTOPAUSE = "com.mtk.ACTION_SPORTMODE_AUTOPAUSE";  // 自动暂停开关的变化
    public static final String ACTION_SPORTMODE_AUTOSTOP = "com.mtk.ACTION_SPORTMODE_AUTOSTOP";  // 自动停止开关的变化

    public static final String ACTION_BKOTASUCCESS_RECON = "com.kct.ACTION_BKOTASUCCESS_RECON";  // BK平台升级成功后发广播自动重连

    public static final String ACTION_GETPUSHPIC_SUCCESS = "com.kct.ACTION_GETPUSHPIC_SUCCESS";

    public static final String ACTION_PUSHPIC_FINISH = "com.kct.ACTION_PUSHPIC_FINISH";

    public static final String ACTION_REMOTE_CAMERA = "com.kct.ble.remote.camera";    // todo --- 远程拍照 BLE  拍照广播
    public static final String ACTION_REMOTE_CAMERA_EXIT = "com.kct.ble.remote.camera.exit";    // todo --- 远程拍照 BLE  退出拍照广播

    public static final String WEATHER_DATA = "WEATHER_DATA";

    public static final String GET_CITY_CODE = "https://api.heweather.com/v5/search?city=";// 和风天气获取 城市/地区编码
    public static final String API_STORE_KEY = "&key=f72da8a8d73d4fdea4603b70944436d5";
// todo  接口全路径：http:// wx.funos.cn:8080 +接口地址，参数部分使用json格式
//todo  --- 天气接口  fundo/weather/requestWeather.do? location=116.376673,39.91737&lang=en&appName=0&systemType=1&appVersion=1&uuid=dasf
//    public static final String GET_WEATHER_URL = "http://wx.funos.cn:8080/fundo/weather/request.do?";
    public static final String GET_WEATHER_URL = "http://wx.funos.cn:8080/fundo/weather/requestWeather.do?";// TODO 20180720 新接口，获取天气

    public static final String GET_SHIPEI_URL = "http://wx.funos.cn:8080/fundo/adaptiveFromApp/request.do?";// TODO 20180720 新接口，获取适配数据       fundo/adaptiveFromAPP/request.do?

    public static final String URL_GET_UV_PRESSURE_TEP_TWO_DAY_NEW = "http://app.fundo.xyz:8001/weardoor/index.php?s=Api/Apistore/weather/cityid/";        //获取 两天 的温度、气压、紫外线数据  注意：有缓存，一直拿到的网址不对路导致获取不到数据，只能新建tag

    public static final int PAGE_INDEX_HOME = 1;
    public static final int PAGE_INDEX_ANALYSIS = 2;
    public static final int PAGE_INDEX_HEALTH = 3;
    public static final int PAGE_INDEX_SPORTMODE = 4;

    public static final int PAGE_INDEX_SPORT_DETAILED_DATA = 5;  // 运动模式详细数据页面
    public static final int PAGE_INDEX_SPORT_SPEED_DETAILS = 6;   // 运动模式配速详情页面
    public static final int PAGE_INDEX_SPORT_MOTION_CHART = 7;    // 运动模式运动图表页面
    public static final int PAGE_INDEX_ECG = 8;    // 运动模式运动图表页面
    private List<Bloodpressure> BloodpressureLIST = new ArrayList<>();
//    public static final String PAGE_INDEX_SPORTMODE= "2";


    public static int warchwifistate = -1;
    // Global instance
    private static MainService sInstance = null;
    public static String dusbingdi = null;
    // Application context
    private static final Context sContext = BTNotificationApplication
            .getInstance().getApplicationContext();

    // Flag to indicate whether main service has been start
    private static boolean mIsMainServiceActive = false;

    private boolean mIsSmsServiceActive = false;

    private boolean mIsCallServiceActive = false;

    // Flag to indicate whether the connection status icon shows
    private boolean mIsConnectionStatusIconShow = false;

    public static boolean mWriteAppConfigDone = false;

    // Register and unregister SMS service dynamically
    private static NotificationReceiver sNotificationReceiver = null;

    // Register and unregister SMS service dynamically
    private SmsService mSmsService = null;

    // Register and unregister call service dynamically
    private CallService mCallService = null;

    //private SystemNotificationService mSystemNotificationService = null;

    // Register and unregister call service dynamically

    private RemoteCameraService mRemoteCameraService = null;

    private NotificationService mNotificationService = null;

    public static final String EXTRA_DATA = "EXTRA_DATA";
    // FOAT add
    //public FotaHelper fotaHelper = new FotaHelper(this);

    private int mBatteryValue = -1;

    private static final String RINGTONE_NAME = "music/Alarm_Beep_03.ogg";
    private MediaPlayer mMediaPlayer = null;
    private Vibrator mVibrator = null;
    private AssetFileDescriptor mRingtoneDescriptor = null;
    private static final long[] VIBRATE_PATTERN = new long[]{
            500, 500
    };


    private HTTPController hc = null;
    private DBHelper db = null;
    private SimpleDateFormat mDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd HH");
    private String requestDate = "";
    /*******
     * 天气相关
     ******/
    private String weatherStr = "";
    private ServiceBroadcast sBroadcast;
    public static boolean Daring = true;
    //public static String bletype = "F";
//	public static int code = 0;

    boolean musicplaying;
    String musicTrack, musicArtistName;
    private BluetoothHeadset mBluetoothHeadset;
    private AlertDialog.Builder inwrbuilder = null;

    private AlertDialog inwralert = null;

    private static final int SENDMESSAGEURL = 16;
    private String message;
    /////////////////////////////////////////////////////////////////////////////////////////////
    private static int sequenctId = 0;    //发送的序列号

    private Queue<BleCmdBean> nSendQueue = new LinkedList<BleCmdBean>();  //发送队列
    private TimerTask timerTask;   //定时器

    private static int type = 0;

    private static boolean burstification = false;  //是否在接收数据

    private byte[] byteArr;    //总包数组

    private static int cmdLength = 0;    //组包长度

    private static int cmdSumLength = 0;    //组包长度

    public static final int RECEIVEMSG = 6;

    private L1Bean l1Bean = null;
    private static boolean burDataBegin = false;  //是否开始运动组包

    private static boolean burDataEnd = false;  //是否结束运动组包
    private List<byte[]> commandList = new ArrayList<>();   //组包集合

    private List<HearData> hlist = new ArrayList<HearData>();        // 心率数据
    private List<SleepData> slist = new ArrayList<SleepData>();            // 睡眠数据据
    private List<StepData> list = new ArrayList<StepData>();  // 计步数据
    private ArrayList<Bloodpressure> BbloodpressureList = new ArrayList<Bloodpressure>();  // 计步数据
    private ArrayList<Oxygen> XIEYANGList = new ArrayList<Oxygen>();  // 计步数据
    ///// 运动模式相关的数据
    String[] baseRun; // TODO -- 基础数据  年月日时分(5)+运动类型(1)+心率(2)+暂停时长(2)+暂停次数(2)+     里程(4)   +最大步幅(4)+  最小步幅(4)+平均步幅(4)+运动时间(4) = 32
    String[] xlsdbp;  // TODO -- 心率，速度，步频数据   心率，速度，步频：年月日时分(5)+心率(2)+步频(2)+速度(4) = 13
    String[] peisu;   // TODO -- 配速数据   配速：年月日时分(5) + 配速(2) = 7
    String[] guiji;    // TODO -- 轨迹数据：经度(8)+纬度(8)+ 年月日时分(5)+海拔(4)  = 25    （经度 + 纬度 + 时间 + 海拔值）

    // todo --- 测试用数据
    String[] baseRunTest;
    String[] xlsdbpTest;
    String[] peisuTest;
    String[] guijiTest;

    String isDataRepetition = "";  // 数据是否重复
    String isDataRepetitionSportData = "";  // 运动数据是否重复

    private String receiveBytes = ""; // 接收到的运动数据字符串
    private String l1_Parse_Sport_t4 = "";   // 接收到的运动数据字符串   l1_Parse_Sport 方法中

    private Timer timer;
    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");

    public static boolean isDeviceSendExitCommand = false;

    public static final int WHAT_URL_ELEVATION = 1;
    public static final int NEW_WEATHER = 2;
    public static final int URL_CITYCODE = 3;  //  = 1,NEW_WEATHER = 2,

    public static final int HEART_DATA_FAILOVER = 4;

    private String code;    //城市code
    private String city;    //城市

    public boolean isCallSend = false;
    public String phoneName = "";
    public String phoneNumber = "";

    private double latitude = 0;
    private double longitude = 0;
    private String altitude;
    public static final String GET_ALTITUDE_OF_GOOGLE_API = "http://ditu.google.cn/maps/api/elevation/json?locations=";//获取谷歌海拔接口地址
    List MYhata = new ArrayList<>();
    ;//接收每分钟心率的值

    List<HearData> heartList = new ArrayList<>();
    int max = 0;
    int min = 0;
    int oxymax = 0;
    int oxymin = 0;

    int avg = 0;

    private List heightOxy = new ArrayList();//添加血氧最高值
    private List minOxy = new ArrayList();//添加血氧最低值

    private List hata_heightOxy = new ArrayList();//添加心率最高值
    private List hata_minOxy = new ArrayList();//添加心率最低值

    private Boolean issave = false;//是否保存当前的心率值
    private Boolean issaveOxy = false;//是否保存当前的血压值
    private Boolean issavexieyang = false;//是否保存当前的血压值
    String mytime=null;
    String myOxytime=null;
    String myOxytimexieyang=null;
    List XINLV = new ArrayList();

    long[] pattern = {0, 500, 1000, 500};   // 停止 开启 停止 开启
    private Vibrator vib;

    private ArrayList<HearData> heartAllList = new ArrayList<>();  //BLE心率总数

    public static final String CONNECT_SUCCESS = "connect_success";
    public static final String CONNECT_FAIL = "connect_fail";
    public static final String AUTO_CONNECT = "auto_connect";
    public static final String CONNECT_STATE = "connect_state";
    public static final int STATE_DISCONNECTEDANDUNBIND = 0;   //断开连接和解绑
    public static final int STATE_DISCONNECTED = 1;            //断开连接
    public static final int STATE_CONNECTING = 2;              //连接中
    public static final int STATE_CONNECTED = 3;               //已连接
    public static final int STATE_NOCONNECT = 4;             //未连接
    public int  deviceState = 0;  //连接状态
    private boolean isScan = false;
	public static boolean ISSYNWATCHINFO = false;    //是否同步了型号适配
    public static boolean FIRMWARE_SUPPORT = false;  //固件升级
    public static boolean AUTO_HEART = false;        //自动检测心率
    public static boolean HEART = false;             //心率
    public static boolean BLOOD_PRESSURE = false;    //血压
    public static boolean BLOOD_OXYGEN = false;      //血氧
    public static boolean SPORT = false;             //运动
    public static boolean PRESSURE = false;          //气压
    public static boolean SLEEP = false;             //睡眠
    public static boolean SOS_CALL = false;          //SOS紧急拨号
    public static boolean ASSISTANT_INPUT = false;   //协助输入
    public static boolean MESSAGE_PUSH = false;      //消息推送
    public static boolean CALL_NOTIFICATION = false; //来电提醒
    public static boolean SMS_NOTIFICATION = false;  //短信提醒
    public static boolean SEDENTARY_CLOCK = false;   //久坐提醒
    public static boolean WATER_CLOCK = false;       //喝水提醒
    public static boolean ALARM_CLOCK = false;       //闹钟提醒
    public static boolean FAZE_MODE = false;         //勿扰模式
    public static boolean BT_CALL = false;           //蓝牙通话
    public static boolean CAMEAR = false;            //智能拍照
    public static boolean GESTURE_CONTROL = false;   //抬手亮屏
    public static boolean DIAL_PUSH = false;         //表盘推送
    public static boolean FIND_DEVICE = false;       //查找设备
    public static boolean ANTI_LOST = false;         //防丢功能
    public static boolean QR_CODE = false;           //二维码推送
    public static boolean WEATHER_PUSH = false;      //天气推送
    public static boolean WECHAT_SPORT = false;      //微信运动
    public static boolean REMIND_MODE = false;       //提醒模式
    public static boolean UNIT = false;              //单位设置
    public static boolean POINTER_CALIBRATION = false;//指针校准
    public static boolean CONSTANTS = false;          //同步联系人

    public static boolean FAPIAO = false;//发票
    public static boolean SHOUKUANEWM = false;//收款二维码

    public static boolean BLEMUSIC = false;//蓝牙音乐
    public static boolean ECG = false;//蓝牙音乐
    public static boolean BODYTEMPERATURE = false;//蓝牙音乐

    public static int getSyncDataNumInService = 0;
    private static boolean issavexeyang=false;//是否保存血氧
    private static boolean issavexeya=false;//是否保存血压
    private static boolean issavexinlv=false;//是否保存心率

    private boolean isGPS = false;   //判断手环是否带有GPS运动数据
    private int gpsNumber = 0;       //手环GPS总包数
    private int gpsIndex = 0;        //手环GPS索引数
    private ArrayList<GpsPointDetailData> gpsList = new ArrayList<>();   //手环运动数据集合
    private GpsPointDetailData gpsPointDetailData;      //手环运动数据类
    private StringBuffer latSb = new StringBuffer();    //纬度
    private StringBuffer lngSb = new StringBuffer();    //经度
    private StringBuffer speedSb = new StringBuffer();  //配速
    private boolean isReceiveSport = false;             //判断是否运动数据无下一个数据包
    private long gpsTime;                               //每公里用时
    private ArrayList<Double> gpsLatList = new ArrayList(); //纬度集合
    private ArrayList<Double> gpsLngList = new ArrayList(); //经度集合
    private ArrayList<Long> gpsTimeList = new ArrayList<>();//每个坐标点相差时间
    private double GPS_PI = 3.14159265358979323846;

    private KCTDefaultAlerter kctDefaultAlerter;

	public static void clearWatchInfo(){
        ISSYNWATCHINFO = false;
        FIRMWARE_SUPPORT = false;
        AUTO_HEART = false;
        HEART = false;
        BLOOD_PRESSURE = false;
        BLOOD_OXYGEN = false;
        SPORT = false;
        PRESSURE = false;
        MESSAGE_PUSH = false;
        CALL_NOTIFICATION = false;
        SMS_NOTIFICATION = false;
        SEDENTARY_CLOCK = false;
        WATER_CLOCK = false;
        ALARM_CLOCK = false;
        FAZE_MODE = false;
        BT_CALL = false;
        CAMEAR = false;
        GESTURE_CONTROL = false;
        DIAL_PUSH = false;
        FIND_DEVICE = false;
        ANTI_LOST = false;
        QR_CODE = false;
        WEATHER_PUSH = false;
        WECHAT_SPORT = false;
        REMIND_MODE = false;
        UNIT = false;
        POINTER_CALIBRATION = false;
        SLEEP = false;
        SOS_CALL = false;
        ASSISTANT_INPUT = false;
        CONSTANTS = false;

        FAPIAO = false;
        SHOUKUANEWM = false;
        BLEMUSIC = false;
    }

    public static boolean isSendFile = false;   //dialog升级发送数据包判断条件

    private AutoConnectThread autoConnectThread;  //蓝牙重连线程

    private HidConncetUtil mHidConncetUtil;
	 private boolean isHandler = false;

    private int index = 0;      // 表盘推送索引（无偏移）
    private  int indexHpy = 0;  // 表盘推送索引（有偏移）
//////////////////////////////////////////////////////////
//    private int packageNum = WatchPushActivityNew.fileByte.length/256;    // 0E00E20006 00 0100 0000 00    ------
//    private int lastpackageNum = WatchPushActivityNew.fileByte.length%256;
    private boolean isEnd = false;
    //                        index = 0;
    private boolean isHasPianYi = false;
    private int ipianYi = 0;

    //                        indexHpy = 0;
    int packageNumHpy = 0;    // 0E00E20006 00 0100 0000 00    ------
    int lastpackageNumHpy = 0;

    /** Key code constant: Play/Pause media key. */
    public static final int KEYCODE_MEDIA_PLAY_PAUSE= 85;
    /** Key code constant: Stop media key. */
    public static final int KEYCODE_MEDIA_STOP      = 86;
    /** Key code constant: Play Next media key. */
    public static final int KEYCODE_MEDIA_NEXT      = 87;
    /** Key code constant: Play Previous media key. */
    public static final int KEYCODE_MEDIA_PREVIOUS  = 88;
    /** Key code constant: Rewind media key. */
    public static final int KEYCODE_MEDIA_REWIND    = 89;
    /** Key code constant: Fast Forward media key. */
    public static final int KEYCODE_MEDIA_FAST_FORWARD = 90;

    private IConnectListener iConnectListener = new IConnectListener() {
        @Override
        public void onConnectState(int i) {
            if(i == 3){
                setState(STATE_CONNECTED);
                EventBus.getDefault().post(new MessageEvent(CONNECT_STATE,2));
                EventBus.getDefault().post(new MessageEvent(CONNECT_SUCCESS, KCTBluetoothManager.getInstance().getConnectDevice()));
            }else if(i == 4){
                Log.e(TAG,"deviceState = " + deviceState + "  ;  " + mBluetoothAdapter.isEnabled());
                if(deviceState != STATE_NOCONNECT && mBluetoothAdapter.isEnabled()) {
                    setState(STATE_DISCONNECTED);
                }
                    EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
                    EventBus.getDefault().post(new MessageEvent(AUTO_CONNECT));

                // todo --- GATT 断开连接了
                if(null != mHidConncetUtil && null != connectDevice ){  // mHidConncetUtil.device    connectDevice
                    mHidConncetUtil.unPair(connectDevice);
                    mHidConncetUtil.disConnect(connectDevice);   // todo --- 绑定后，再连接   ---- 通过反射连接
                }
            }else if(i == 2){
                if(deviceState != STATE_NOCONNECT && mBluetoothAdapter.isEnabled()) {
                    setState(STATE_CONNECTING);
                }
            }
        }

        @Override
        public void onConnectDevice(BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void onScanDevice(BluetoothLeDevice bluetoothLeDevice) {

        }

        @Override
        public void onCommand_d2a(byte[] bytes) {
            byte[] buffer = new byte[bytes.length - 8];
            System.arraycopy(bytes,8,buffer,0,buffer.length);
            if(bytes[1] == (byte)0x70 && ((bytes[8] == (byte)0xA0 && bytes[10] == (byte)0xA5) ||
                    (bytes[8] == (byte)0x07 && bytes[10] == (byte)0x71))){
                isReceiveSport = true;
            }else if(bytes[1] == (byte)0x30 && ((bytes[8] == (byte)0xA0 && bytes[10] == (byte)0xA5) ||
                    (bytes[8] == (byte)0x07 && bytes[10] == (byte)0x71))){
                isReceiveSport = false;
            }
            L2_Parse(buffer);
        }
    };

    private CountTimer countTimer;

    /**
     * 检验MTK数据传输超时处理
     */
    private class CountTimer  extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.e(TAG,"countTimer onTick");
        }

        @Override
        public void onFinish() {
            WearableManager.getInstance().disconnect();
        }
    }


    private Timer reconnectTimer;
    private MyTimerTask myTimerTask;

    class MyTimerTask extends TimerTask {   //todo ---- add 20171122
        @Override
        public void run() {
            if(isScan){
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                isScan = false;
            }else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                isScan = true;
            }
        }
    }

    public int getState() {
       try{
           return deviceState;
       }catch (NullPointerException E){
           return 0;
       }
    }

    public void setState(int state) {
        deviceState = state;
        EventBus.getDefault().post(new MessageEvent("deviceState"));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if (event.getMessage().equals(CONNECT_SUCCESS)) {     //连接成功
            settingNotification();
            BluetoothDevice device = (BluetoothDevice) event.getObject();
            if(null !=  device){
                SharedPreUtil.savePre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC, device.getAddress());// 存储当前连接的蓝牙地址。
                SharedPreUtil.savePre(sContext, SharedPreUtil.USER, SharedPreUtil.MACNAME, device.getName());// 存储当前连接的蓝牙名称。
            }
            SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
            SharedPreUtil.setParam(sContext,SharedPreUtil.USER,SharedPreUtil.UNBOND,false);

            EventBus.getDefault().post(new MessageEvent("deviceState"));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Intent intent = new Intent();
            intent.setAction(MainService.ACTION_BLECONNECTED);
            sContext.sendBroadcast(intent);

            isHandler = false;
            mHandler.removeCallbacks(runnable);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

            if(autoConnectThread != null) {
                autoConnectThread.cancel();
                autoConnectThread = null;
            }

            if((SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")
                    && !device.getName().contains("DfuTarg")) || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //手环

                String uuid = (String) SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.UUID,"");
                L2Send.getSystrmUserData(sContext);  //设置系统设置    todo ---- 先发制式
                L2Send.sendSynTime(sContext);       //时间设置           todo ---- 再发时间
                L2Send.getUserInfoData(sContext);     //设置个人信息
                L2Send.getFirmwareData();            //获取固件信息
                L2Send.sendBraceletSet();           //读取手环设置请求
                L2Send.sendSycnEcg();           //同步心电

                String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE);
                if(WEATHER_PUSH){ // StringUtils.isEmpty()!"601".equals(code) ||
                    L2Send.syncAppWeather();   // 同步天气
                }

//                L2Send.syncAppWeather();   // 同步天气
                if(uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())) {
                    L2Send.getWatchPushData();   //获取表盘数据
                }
                L2Send.sendSyncShishiStep();  // 同步实时步数

                //todo --- F4设备需要发送 ，气压，海拔，紫外线
                BluetoothDevice devices = mBluetoothAdapter.getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));

                if(ISSYNWATCHINFO){
                    if(UNIT){
                        if (!devices.getName().equals("V6")){
                            L2Send.unitSetting(); //todo --- add 1011
                        }
                    }
                }else {
                    /*if (!devices.getName().equals("V6")){
                        L2Send.unitSetting(); //todo --- add 1011
                    }*/
                }


                if(ISSYNWATCHINFO){
                    if(PRESSURE){
                        L2Send.syncWeatherIndex();  // 同步紫外线，气压,海拔
                    }
                }else {
                    if (devices.getName().equals("F4") || devices.getName().equals("Smare Band")
                            || devices.getName().equals("Smart band") || devices.getName().equals("sh321")) {  //
                        L2Send.syncWeatherIndex();  // 同步紫外线，气压，海拔
                    }
                }


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String today = simpleDateFormat.format(new Date());
                if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.TIME).equals("")
                        || !SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.TIME).equals(today)) {
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.TIME, today);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN, "0");
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE, "0");
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE, "0");
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN, "0");
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE, "0");
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE, "0");
                }

                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);
                SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.TEMP_WATCH,tempWatchType);

                // todo ---- 同步健康数据
                Intent intent2 = new Intent();
                intent2.setAction(MainService.ACTION_SYNC_BLECONNECT);    // 发数据同步成功的广播
                sContext.sendBroadcast(intent2);
            }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")){
                //countTimer = new CountTimer(15000,1000);
                BluetoothMtkChat.getInstance().sendApkState();   //前台运行
                BluetoothMtkChat.getInstance().getWathchData();    //获取手表数据
                BluetoothMtkChat.getInstance().syncRun();        //每天计步数据

                BluetoothMtkChat.getInstance().synTime(sContext);  //同步时间
                String unit = ((String) SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))
                        .equals(SharedPreUtil.YES) ? "0" : "1";
                String temp = ((String) SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.UNIT_TEMPERATURE,SharedPreUtil.YES))
                        .equals(SharedPreUtil.YES) ? "0" : "1";
                BluetoothMtkChat.getInstance().synUnit(unit + "," + temp);

                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);
                SharedPreUtil.setParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH, tempWatchType);

                BluetoothMtkChat.getInstance().synWeather();       //天气
                BluetoothMtkChat.getInstance().synMeteorology();  //气象指数

                BTNotificationApplication.isSyncEnd = false; // todo --- 开始同步数据将标志位 置为 false

                // todo ---- 同步健康数据   修改20171121  ---  MTK 平台 连上时同步数据不显示加载框
//                Intent intent2 = new Intent();
//                intent2.setAction(MainService.ACTION_SYNC_BLECONNECT);    // 发数据同步成功的广播
//                sContext.sendBroadcast(intent2);
            }
        } else if (event.getMessage().equals(CONNECT_FAIL)) {
            nSendQueue.clear();
            settingNotification();
            heartAllList.clear();
            /*if(countTimer != null) {
                countTimer.cancel();
                countTimer = null;
            }*/
        } else if (event.getMessage().equals(AUTO_CONNECT)) {     //重连
            autoConnectDevice();

        } else if (event.getMessage().equals(CONNECT_STATE)) {     //连接的设备型号
            int state = 0;
            String uuid = (String) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.UUID,"");
            if(uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())) {
                state = 1;
            }else if(uuid.equals(BleContants.BLE_YDS_UUID.toString()) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING.toString())){
                state = 2;
            }else {
                state = 3;
            }
            //int state = (int) event.getObject();
            SharedPreUtil.setParam(sContext,SharedPreUtil.USER,SharedPreUtil.WATCH,state+"");
        } else if (event.getMessage().equals("unBond")){
            //mHandler.removeCallbacks(runnable);
            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            if (null != reconnectTimer) {
                reconnectTimer.cancel();
                reconnectTimer = null;
            }

            if (null != myTimerTask) {
                myTimerTask.cancel();
                myTimerTask = null;
            }
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }else if(event.getMessage().equals("sendFile_end")){   //dialog升级发送数据包完成
            //isSendFile = false;
            KCTBluetoothManager.getInstance().setDilog(false,iDialogCallback);
        }else if(event.getMessage().equals("mtk_sendData")){
            /*if(countTimer != null) {
                countTimer.start();
            }*/
        }else if(event.getMessage().equals("mkt_pauseData")){
            /*if(countTimer != null) {
                countTimer.cancel();
            }*/
        }

    }

    private IDialogCallback iDialogCallback = new IDialogCallback() {
        @Override
        public void send_file(boolean b) {
            if(b){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new MessageEvent("firmWare_sendFile"));
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_BKOTASUCCESS_RECON.equals(action) ) {       //MTK查找手机 开

                autoConnectDeviceForBk(); //todo  ---   BK平台升级成功后发广播自动重连
            }

            if (action.equals("com.android.music.playstatechanged")) {
                // music.setPlaying(true);
                if (MainService.getInstance().getState() == 3) {
                    try {
                        musicArtistName = intent.getStringExtra("artist");
                        // String album = intent.getStringExtra("album");
                        musicTrack = intent.getStringExtra("track");
                        musicplaying = intent.getBooleanExtra("playing", false);
                        long position = intent.getLongExtra("position", 1000);
//                        MainService.getInstance().sendMessage("play" + musicplaying);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if (action.equals("com.android.music.metachanged")) {

                if (MainService.getInstance().getState() == 3) {
                    try {
                        musicArtistName = intent.getStringExtra("artist");
                        // String album = intent.getStringExtra("album");
                        musicTrack = intent.getStringExtra("track");
                        musicplaying = intent.getBooleanExtra("playing", false);
                        long position = intent.getLongExtra("position", 1000);
//                        MainService.getInstance().sendMessage("name" + musicTrack);
//                        MainService.getInstance().sendMessage("arts" + musicArtistName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // 锁屏
                Log.i("gjt", "手机锁屏l了");
                if (MainService.getInstance().getState() == 3) {
//                    MainService.getInstance().sendMessage("soff");
                }
                return;
            }
            if (ACTION_FINDWATCHON.equals(action) ) {       //MTK查找手机 开
//                try {
//                    AssetManager assetManager = sContext.getAssets();
//                    AssetFileDescriptor mRingtoneDescriptor = assetManager.openFd("music/Alarm_Beep_03.ogg");
//                    MediaManager.getMediaPlayerInstance();
//                    MediaManager.playSound(mRingtoneDescriptor);
//                    MediaManager.replayVib(sContext);//开始震动
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                showDialog();    //todo ----      MTK弹框
            }

            if(ACTION_FINDWATCHOFF.equals(action)){
                final RingService ring = RingService.getRing();
                if (builder != null) {
                    builder = null;
                }
                if (alert != null) {
                    ring.stopRing();
                    if (vib != null) {
                        vib.cancel();
                    }

                    if (alert.isShowing()) {
                        alert.cancel();
                    }

                    /*if (findPhoneFlags) {
                        findPhoneFlags = false;
                    } else {
                        findPhoneFlags = true;
                    }*/
                    findPhoneFlags = false;
                }
            }

//            if (ACTION_FINDWATCHOFF.equals(action)) {     ////MTK查找手机 关
//                //找手机 关
//                MediaManager.stopVib();//停止震动
//                MediaManager.stop();
//                MediaManager.release();
//            }

            if (ACTION_AUTOCONNECT_DEVICE.equals(action)) {           //BLE自动重连
                //断开数据全清空
                byteArr = null;
                cmdSumLength = 0;
                cmdLength = 0;
                burstification = false;
                l1Bean = null;

                if ((boolean) SharedPreUtil.getParam(MainService.this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false)) { //手动断开
                    return;
                }
                if (MainService.getInstance().getState() == 3) {
                    return;
                }
                //antoHandler.sendEmptyMessage(1);
                /*String uuid = (String) SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.UUID,"");
                String address = SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC);
                if(TextUtils.isEmpty(uuid)){
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    return;
                }
                if(uuid.equals(BleContants.BLE_YDS_UUID.toString()) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING.toString())){
                    connectBluetooth(address,true);
                }else{
                    connectBluetooth(address,false);
                }*/
            }
            if (ACTION_BLECONNECTED.equals(action)) {  //BLE自动重连
                SharedPreUtil.setParam(MainService.this, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
                //antoHandler.sendEmptyMessage(0);
            }
            if (ACTION_BLEDISCONNECT.equals(action)) {
                //antoHandler.sendEmptyMessage(0);
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if(blueState == BluetoothAdapter.STATE_ON){
                    setState(STATE_DISCONNECTED);
                    EventBus.getDefault().post(new MessageEvent(AUTO_CONNECT));
                }else if(blueState == BluetoothAdapter.STATE_OFF){
                    EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
                    EventBus.getDefault().post(new MessageEvent(AUTO_CONNECT));
                    setState(STATE_DISCONNECTED);
                }
            }

            if(action.equals(MainService.ACTION_BLECONNECTHID)){
                try {
                    Thread.sleep(500); // 5000
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 17) {
                    if(null == mHidConncetUtil){
                        mHidConncetUtil = new HidConncetUtil(BTNotificationApplication.getInstance());
                    }

                    if(null != connectDevice){  // mHidConncetUtil.device    connectDevice
                        if(!mHidConncetUtil.isBonded(connectDevice)){
                            mHidConncetUtil.pair(connectDevice);
                        }
                        mHidConncetUtil.connect(connectDevice);
                    }
                }else {
                    Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.app_hidconnect_tip),Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    /**
     * BK重连
     */
    private synchronized void autoConnectDeviceForBk() {
        Log.i(TAG, "[autoConnectDevice] begin");
//        String watch = SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        boolean isDisConnect = (boolean) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
        if (isDisConnect) {
            Log.i(TAG, "[autoConnectDevice] is ble_click_stop");
            return;
        }

        String address = Utils.exChange2(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
        String uuid = (String) SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.UUID,"");
//        if (TextUtils.isEmpty(watch)) {
//            Log.i(TAG, "[autoConnectDevice] watch is null");
//            return;
//        }
        if(TextUtils.isEmpty(address)){
            Log.i(TAG, "[autoConnectDevice] address is null");
            return;
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.i(TAG, "[autoConnectDevice] invalid BT address");
            return;
        }
//        if(TextUtils.isEmpty(uuid)){
//            Log.i(TAG, "[autoConnectDevice] uuid is null");
//            return;
//        }
        if(deviceState == MainService.STATE_CONNECTING){
            Log.i(TAG, "[autoConnectDevice] ble is connecting");
            return;
        }

        if(deviceState == MainService.STATE_CONNECTED){
            Log.i(TAG, "[autoConnectDevice] ble is connected");
            return;
        }

        Boolean isBTOn = mBluetoothAdapter.isEnabled();
        if (!isBTOn) {
            Log.i(TAG, "[autoConnectDevice] BT is off");
            return;
        } else {
            if(autoConnectThread == null){
                autoConnectThread = new AutoConnectThread();
                autoConnectThread.start();
            }else{
                autoConnectThread.update();
            }
        }
    }

    /**
     * 重连
     */
    private synchronized void autoConnectDevice() {
        Log.i(TAG, "[autoConnectDevice] begin");
        String watch = SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        boolean isDisConnect = (boolean) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
        if (isDisConnect) {
            Log.i(TAG, "[autoConnectDevice] is ble_click_stop");
            return;
        }
        if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP) {
            WearableManager.getInstance().switchMode();
        }
        String address = Utils.exChange2(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
        String uuid = (String) SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.UUID,"");
        if (TextUtils.isEmpty(watch)) {
            Log.i(TAG, "[autoConnectDevice] watch is null");
            return;
        }
        if(TextUtils.isEmpty(address)){
            Log.i(TAG, "[autoConnectDevice] address is null");
            return;
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Log.i(TAG, "[autoConnectDevice] invalid BT address");
            return;
        }
        if(TextUtils.isEmpty(uuid)){
            Log.i(TAG, "[autoConnectDevice] uuid is null");
            return;
        }
        if(deviceState == MainService.STATE_CONNECTING){
            Log.i(TAG, "[autoConnectDevice] ble is connecting");
            return;
        }

        if(deviceState == MainService.STATE_CONNECTED){
            Log.i(TAG, "[autoConnectDevice] ble is connected");
            return;
        }

        Boolean isBTOn = mBluetoothAdapter.isEnabled();
        if (!isBTOn) {
            Log.i(TAG, "[autoConnectDevice] BT is off");
            return;
        } else {
            if(autoConnectThread == null){
                autoConnectThread = new AutoConnectThread();
                autoConnectThread.start();
            }else{
                autoConnectThread.update();
            }
        }
    }

    /**
     * 蓝牙重连线程
     */
    private class AutoConnectThread extends Thread{

        private boolean mIsRun;

        private Lock mInnerLock;

        private Condition mInnerCondition;

        private BluetoothDevice device;

        public AutoConnectThread(){
            mIsRun = true;
            mInnerLock = new ReentrantLock();
            mInnerCondition = mInnerLock.newCondition();

            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();
        }

        @Override
        public void run() {
            Log.e(TAG,"deviceState = " + deviceState);
            while(mIsRun){
                if(deviceState != 3) {
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    String uuid = (String) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.UUID, "");
                    if(device == null) {
                        String address = Utils.exChange2(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                        device = mBluetoothAdapter.getRemoteDevice(address);
                    }
                   /* int state =  device.getBondState();   // todo --- 重连时不用解绑
                    if(state == BluetoothDevice.BOND_BONDED){
//                        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
                        Method createBondMethod;
                        try {
                            createBondMethod = BluetoothDevice.class.getMethod("removeBond");
                            createBondMethod.invoke(device);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    String uuid = (String) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.UUID, "");
//                    if(device == null) {
//                        String address = Utils.exChange2(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
//                        device = mBluetoothAdapter.getRemoteDevice(address);
//                    }

                    Log.e(TAG,"auto deviceName = " + device.getName() + " ;  deviceAddress = " + device.getAddress());


                    if(deviceState != 3) {
                        if (device.getName() == null) {
                            setState(STATE_CONNECTING);
                            mBluetoothAdapter.startLeScan(mLeScanCallback);
                            device = null;
                        } else {
                            if (uuid.equals(BleContants.BLE_YDS_UUID.toString()) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING.toString())
                                    || uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())) {
                                if(device.getBondState() == device.BOND_NONE && uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())) {
                                    device.createBond();
                                }else {
                                    BTNotificationApplication.getMainService().connectDevice(device);
                                }
                            } else {
                                WearableManager.getInstance().setRemoteDevice(device);
                                WearableManager.getInstance().connect();
                                if(deviceState != STATE_NOCONNECT && mBluetoothAdapter.isEnabled()) {
                                    setState(STATE_CONNECTING);
                                }
                            }
                        }
                        if(!isHandler && deviceState != STATE_NOCONNECT) {
                            mHandler.postDelayed(runnable, 17000);
                            isHandler = true;
                        }
                    }else {
                        Log.e(TAG,"device is Connected");
                    }

                    mInnerLock.lock();
                    try {
                        Log.d("AutoConnectThread", "waiting the connect.");
                        mInnerCondition.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("AutoConnectThread", "connect is fails.");
                    } finally {
                        mInnerLock.unlock();
                    }
                }
            }
        }


        public void update(){
            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();
        }

        public void cancel(){
            mInnerLock.lock();
            mInnerCondition.signalAll();	// UnLock
            mInnerLock.unlock();

            mIsRun = false;
            Log.d("AutoConnectThread","connectThread is stop");
        }


        public void getConnectDevice(BluetoothDevice device){
            this.device = device;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_URL_ELEVATION:
                    String objData = msg.obj.toString();
//                    Log.i(TAG, "mHandler---WHAT_URL_ELEVATION---海拔接口数据返回：" + objData);
                    try {
                        JSONObject json = new JSONObject(objData);
                        JSONArray jsonArray = json.getJSONArray("results");
                        String status = json.getString("status");
                        if (null != jsonArray && jsonArray.length() > 0) {
                            JSONObject jsonObj = (JSONObject) jsonArray.get(0);
                            String elevation = jsonObj.getString("elevation");    // 30.34820747375488
                            if (status.equals("OK")) {
                                double eleValue = Double.parseDouble(elevation) * 10;    // 303.48207473754877
                                altitude = String.valueOf(Math.round(eleValue));    // 303
//                                Log.e(TAG,"mHandler---WHAT_URL_ELEVATION---解析海拔*10的结果是 altitude = "+altitude);
                                String lastTimeAltitude = UTIL.readPre(sContext, "weather", "altitude"); //上一个海拔
                                if (!lastTimeAltitude.equals(altitude)) {//海拔发生变化时发送数据至手环端
                                    UTIL.savePre(sContext, "weather", "altitude", altitude);
                                    if (null != SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC)) {
                                        String myaddress = SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                                        Log.e("myaddress", myaddress);
                                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(myaddress);
                                        if (null != SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH)) {
                                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                                                if (device.getName().equals("F4") || device.getName().equals("Smart band")) {
                                                    if (MainService.getInstance().getState() == 3) {
                                                        L2Send.syncWeatherIndex();
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            } else {
                                try {
                                    Thread.sleep(300);
                                    readSharedPreferences();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case NEW_WEATHER:
                    String response = msg.obj.toString();
                    weatherParse(response);
                    break;
                case URL_CITYCODE:
                    //{"HeWeather5":[{"basic":{"city":"Lambeth","cnty":"United Kingdom","id":"GB6545250","lat":"51.496349","lon":"-0.11152"},"status":"ok"}]}
                    String objDatas = msg.obj.toString();
                    //新天气接口数据返回
                    //{"code":0,"data":{"country":"中国","city":"南山","weatherCode":"101","temperature":"30","cityid":"CN101280604","id":20,"pressure":"1007","updateTimes":"2018-06-04 13:50:00.0","createTimes":"2018-05-21 17:55:00.0"
                    // ,"dailyForecast":[{"weatherDate":"2018-06-04","weatherCode":"305","temperatureMax":"32","temperatureMin":"26","pressure":"1008","uvIndex":"10"},{"weatherDate":"2018-06-05","weatherCode":"300","temperatureMax":"30","temperatureMin":"26","pressure":"1007","uvIndex":"7"},{"weatherDate":"2018-06-06","weatherCode":"310","temperatureMax":"29","temperatureMin":"25","pressure":"1006","uvIndex":"6"}]},"message":"请求成功"}
                    Log.e(TAG,"objDatas = " + objDatas);
                    try {
                        JSONObject json = new JSONObject(objDatas);
                        String data_code = json.getString("code");
                        if(data_code.equals("0")){
                            String data = json.getString("data");
                            WeatherCity weatherCity = new Gson().fromJson(data, WeatherCity.class);
                            Log.e(TAG,"weatherCity = " + weatherCity);
                            if(weatherCity != null){
                                String city = weatherCity.getCity() == null ? "" : weatherCity.getCity();
                                String weatherCode = weatherCity.getWeatherCode();
                                String temperature = weatherCity.getTemperature() == null ? "" : weatherCity.getTemperature();    //温度
                                String cityid = weatherCity.getCityid() == null ? "" : weatherCity.getCityid();      //城市id
                                String pressure = weatherCity.getPressure() == null ? "" : weatherCity.getPressure();   //气压

                                SharedPreUtil.setParam(sContext, SharedPreUtil.WEATHER, SharedPreUtil.WEATHER_UPDATE_TIMES, weatherCity.getUpdateTimes() == null ? "" : weatherCity.getUpdateTimes());  // todo --- 保存天气更新的时间到本地

                                boolean pushStopped = JPushInterface.isPushStopped(MainService.this);
                                if(pushStopped){
                                    JPushInterface.resumePush(MainService.this);
                                }

                                UTIL.savePre(sContext, "weather", "city", city);      //保存城市数据(mtk平台)
                                UTIL.savePre(sContext, "weather", "wendu",temperature);

                                List<DailyForecast> dailyForecastList = weatherCity.getDailyForecast();
                                if(dailyForecastList != null && dailyForecastList.size() > 0){
                                    for (int i = 0; i < dailyForecastList.size(); i++) {
                                        DailyForecast dailyForecast = dailyForecastList.get(i);
                                        String date = dailyForecastList.get(i).getWeatherDate();  //天气日期   2017-07-13

                                        Calendar calendar = Calendar.getInstance();   // 当前天的日期  2017-06-28
                                        calendar.setTime(new Date());
                                        String mcurDate = getDateFormat.format(calendar.getTime());

                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) + 1);
                                        String mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 后一天的数据

                                        Calendar calendar2 = Calendar.getInstance();
                                        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) + 2);
                                        String mcurDate2 = getDateFormat.format(calendar2.getTime());

                                        String code = dailyForecast.getWeatherCode() == null ? "" : dailyForecast.getWeatherCode();  // 天气状况  302 --- 雷阵雨
                                        String min = dailyForecast.getTemperatureMin() == null ? "" : dailyForecast.getTemperatureMin();    // 最低气温  26
                                        String max = dailyForecast.getTemperatureMax() == null ? "" : dailyForecast.getTemperatureMax();
                                        String dayPressure = dailyForecast.getPressure() == null ? "" : dailyForecast.getPressure();    //气压
                                        String dayUv = dailyForecast.getUvIndex() == null ? "" : dailyForecast.getUvIndex();         //紫外线

                                        if (date.equals(mcurDate)) {  //为当前天的日期
                                            UTIL.savePre(sContext, "weather", "date", date);
                                            UTIL.savePre(sContext, "weather", "code", code);
                                            UTIL.savePre(sContext, "weather", "low", min);  // Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8))
                                            UTIL.savePre(sContext, "weather", "high", max);
                                            UTIL.savePre(sContext, "weather", "ziwaixian", dayUv);
                                            UTIL.savePre(sContext, "weather", "qiya", dayPressure);
                                        }

                                        if (date.equals(mcurDate1)) {  //为后一天的日期
                                            UTIL.savePre(sContext, "weather", "nextdate", date);
                                            UTIL.savePre(sContext, "weather", "nextcode", code);
                                            UTIL.savePre(sContext, "weather", "nextlow", min);  // Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8))
                                            UTIL.savePre(sContext, "weather", "nexthigh", max);
                                            UTIL.savePre(sContext, "weather", "nextziwaixian", dayUv);
                                            UTIL.savePre(sContext, "weather", "nextqiya", dayPressure);
                                        }

                                        if (date.equals(mcurDate2)) {  //为第三天的日期
                                            UTIL.savePre(sContext, "weather", "thirddate", date);
                                            UTIL.savePre(sContext, "weather", "thirdcode", code);
                                            UTIL.savePre(sContext, "weather", "thirdlow", min);  // Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8))
                                            UTIL.savePre(sContext, "weather", "thirdhigh", max);
                                            UTIL.savePre(sContext, "weather", "thirdziwaixian", dayUv);
                                            UTIL.savePre(sContext, "weather", "thirdqiya", dayPressure);
                                        }
                                    }
                                }
                                sendReceiver();  //todo --- 发广播给主页，更新天气数据
                            }
                        }else{
                            Log.e(TAG, "weatherParse---json wrong weather");
                            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.no_location),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    /*try {
                        JSONObject json = new JSONObject(objDatas);
                        //JSONObject jsonDatas = json.getJSONObject("HeWeather5");
                        JSONArray jsonArray = json.getJSONArray("HeWeather5");
                        if (null != jsonArray && jsonArray.length() > 0) {
                            JSONObject statusData = jsonArray.getJSONObject(0);
                            String status = statusData.getString("status");
                            if (status.equals("ok")) {
                                JSONObject js = statusData.getJSONObject("basic");
                                code = js.getString("id");    //城市code    ---- CN101280604
                                city = js.getString("city");   //城市       ---- 南山
//                                Log.e(TAG, "code = " + code + ";   city = " + city);
								//todo 设置城市id为极光标签
                                boolean pushStopped = JPushInterface.isPushStopped(MainService.this);
                                if(pushStopped){
                                    JPushInterface.resumePush(MainService.this);
                                }
                                Set<String> set = new HashSet<>();
                                set.add(code);
                                JPushInterface.setTags(MainService.this,0, set);
                                if (!TextUtils.isEmpty(code)) {
                                    setUvPressureValue();
                                }
                            }
                        }else {
                            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.no_location),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    break;

                case HEART_DATA_FAILOVER:
                    Intent intent = new Intent();      // add 0414
                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播      ACTION_SYNFINSH_SUCCESS
                    intent.putExtra("step", "6");
                    sContext.sendBroadcast(intent);     



                    getSyncDataNumInService = 0;
                    BTNotificationApplication.isSyncEnd = true;

                    String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);

                    String isFirstSync = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);
                    if (StringUtils.isEmpty(isFirstSync) || isFirstSync.substring(0,1).equals("0")) {      // TODO--- 没取过7天的数据了
//                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "1#"+curMacaddress);  // todo 同步完成了 --- 第一次同步 7 天的数据，取过7天的数据后 ，将 SYNCED 置1

                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);
                    }

                    nSendQueue.clear();     //todo --- 应该还要清空命令队列    20171121

                    //////////////////////////////////////////////////////////////////////////////////////////////////


                    break;


            }
        }
    };
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG,"STATE_NOCONNECT");
            if(deviceState != MainService.STATE_CONNECTED){
                if(autoConnectThread != null){
                    autoConnectThread.mIsRun = false;
                    autoConnectThread.cancel();
                    autoConnectThread = null;
                }
                setState(MainService.STATE_NOCONNECT);
                isHandler = false;
                mHandler.removeCallbacks(runnable);
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    };


    /**
     * Android5.0以下蓝牙搜索方式
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    String address = SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC);
                    String uuid = (String) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.UUID, "");
                   /* if ((boolean) SharedPreUtil.getParam(MainService.this, SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {  //是否处于固件升级模式
                        return;
                    }*/
                    if (MainService.getInstance().getState() == 3) {
                        //antoHandler.sendEmptyMessage(0);
                        //mHandler.removeCallbacks(runnable);
                        //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        if (null != reconnectTimer) {
                            reconnectTimer.cancel();
                            reconnectTimer = null;
                        }

                        if (null != myTimerTask) {
                            myTimerTask.cancel();
                            myTimerTask = null;
                        }
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    List<UUID> uuids = LinkBleActivity.parseFromAdvertisementData(scanRecord);
                    if(uuids == null || uuids.size() <= 0){
                        return;
                    }
                    String findAddress = device.getAddress();
                    for (final UUID mUuid : uuids) {
                        if (mUuid.equals(BleContants.RX_SERVICE_872_UUID)) {
                            String scanRecords = UtilsLX.bytesToHexString(scanRecord);
                            //02011A 05 09 68756E34 0EFF0B00 3650460335F9 0468756E341107B75C49D204A34071A0B535853EB083070000000000000000000000000000000000000000
                            int deviceNameLength = scanRecord[3] & 0xff;
                            int deviceAddressLength = (8 + deviceNameLength) * 2; //13
                            findAddress = scanRecords.substring(deviceAddressLength, deviceAddressLength + 2)
                                    + ":" + scanRecords.substring(deviceAddressLength + 2, deviceAddressLength + 4)
                                    + ":" + scanRecords.substring(deviceAddressLength + 4, deviceAddressLength + 6)
                                    + ":" + scanRecords.substring(deviceAddressLength + 6, deviceAddressLength + 8)
                                    + ":" + scanRecords.substring(deviceAddressLength + 8, deviceAddressLength + 10)
                                    + ":" + scanRecords.substring(deviceAddressLength + 10, deviceAddressLength + 12);
                        }
                    }
                    Log.i(TAG, "Auto Address = " + address + ";   find = " + findAddress);
                    if (findAddress.equals(address)) {
                        if (!TextUtils.isEmpty(uuid)) {
                            //mHandler.removeCallbacks(runnable);
                            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            if (null != reconnectTimer) {
                                reconnectTimer.cancel();
                                reconnectTimer = null;
                            }

                            if (null != myTimerTask) {
                                myTimerTask.cancel();
                                myTimerTask = null;
                            }
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            if(autoConnectThread == null) {
                                if (uuid.equals(BleContants.BLE_YDS_UUID.toString()) || uuid.equals(BleContants.BLE_YDS_UUID_HUAJING.toString())
                                        || uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())) {
                                    //connectBluetooth(address, true);
                                    if(device.getBondState() == device.BOND_NONE && uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())) {
                                        device.createBond();
                                    }else {
                                        BTNotificationApplication.getMainService().connectDevice(mBluetoothAdapter.getRemoteDevice(findAddress));
                                    }
                                } else {
                                    //connectBluetooth(address, false);
                                    WearableManager.getInstance().setRemoteDevice(mBluetoothAdapter.getRemoteDevice(findAddress));
                                    WearableManager.getInstance().connect();
                                    setState(STATE_CONNECTING);
                                }
                            }else{
                                autoConnectThread.getConnectDevice(device);
                                autoConnectThread.update();
                            }
                        }
                    }
                }
            };


    public void startLock() {
        policyManager = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, LockReceiver.class);
        if (policyManager.isAdminActive(componentName)) {    //判断是否有权限(激活了设备管理器)
            policyManager.lockNow();// 直接锁屏
            Log.i("gjylock", "gjylock");
        } else {
            //激活设备管理器获取权限
//            MainService.getInstance().sendMessage("unac");
            Intent dialogIntent = new Intent(getBaseContext(), MyDevicePolicyManager.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(dialogIntent);
        }
    }

    public void showDialogLost() {
        checkbox = AppValueCheckbox.getInance();
        if (checkbox.isLostPhoneEnabled()) {
            final RingService ring = RingService.getRing();
            if (builder != null) {
                builder = null;
            }
            if (alert != null) {
                ring.stopRing();
                alert.cancel();
            }

            if (offtime != null && System.currentTimeMillis() - offtime < 2000) {
                return;
            }
            ring.ringdisconnect();
            //	Toast.makeText(getApplicationContext(),R.string.bt_disconnected_while_transfer, Toast.LENGTH_SHORT).show();

        }
    }

    private Handler ringHandler = new Handler();
    private Runnable ringRunnable = new Runnable() {
        @Override
        public void run() {
//            mRingService.stopRing();
            findPhoneFlags = true;
            builder = null;
            if (alert != null) {
                alert.dismiss();
            }
            alert = null;
        }
    };

    public void showDialog() {
        final RingService ring = RingService.getRing();
        if (builder != null) {
            builder = null;
        }
        if (alert != null) {
            ring.stopRing();
            if (vib != null) {
                vib.cancel();
            }

            if (alert.isShowing()) {
                alert.cancel();
            }

            if (findPhoneFlags) {
                findPhoneFlags = false;
            } else {
                findPhoneFlags = true;
            }
        }

        if (findPhoneFlags) {
            ring.ring();
            if (vib == null) {
                vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            }
            vib.vibrate(pattern, 0);

            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.cancel_the_bell_ring).setCancelable(false);
            builder.setPositiveButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {   //todo --- 点取消后，不能显示提示框了
                            ring.stopRing();
                            if (vib != null) {
                                vib.cancel();
                            }
                            findPhoneFlags = true;
                            builder = null;
                           L2Send.sendFindPhone();
                            if(null != alert) {
                                alert.dismiss();
                                alert = null;
                            }else{
                                if(null != dialog) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                   if(null!=builder){
                       alert = builder.create();

//                       String dd = android.os.Build.BRAND;  // OPPO
//                       if(android.os.Build.BRAND.contains("vivo")){ // todo --- VIVO 手机单独处理
//                           if (Build.VERSION.SDK_INT >= 24) {
//                                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);   //  TYPE_SYSTEM_ALERT :在有些手机上不行,比如三星,努比亚的,所以使用:TYPE_TOAST
//                           }else{
//                                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//                           }
//                       } else if(android.os.Build.BRAND.contains("OPPO")){  // todo --  OPPO R11(7.1.1)-- OK ,  R9 -- 6.0 ,R11S --- 8.1 ？？？
//                           if (Build.VERSION.SDK_INT >= 24) {
//                               alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);   //  TYPE_SYSTEM_ALERT :在有些手机上不行,比如三星,努比亚的,所以使用:TYPE_TOAST
//                           }else{
//                               alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//                           }
//                       } else {
                           int LAYOUT_FLAG;
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // 26
                               LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY-1;
                           } else {
//                               LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
                               LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                           }
                           alert.getWindow().setType(LAYOUT_FLAG);
//                       }
//                       String mtype = android.os.Build.MODEL;    // PIC-AL00   ----    VTR-AL00      -----   vivo X9i
//                       String mtyb = android.os.Build.BRAND;//手机品牌    HUAWEI   HUAWEI            -----   vivo

                       alert.show(); 
                   }
                    Looper.loop();
                }
            }).start();
        }
    }

    public void showDialogForMtk() {
        try {
            AssetManager assetManager = sContext.getAssets();
            AssetFileDescriptor mRingtoneDescriptor = assetManager.openFd("music/Alarm_Beep_03.ogg");
//            MediaManager.getMediaPlayerInstance();
//            MediaManager.playSound(mRingtoneDescriptor);
//            MediaManager.replayVib(sContext);//开始震动

            //////////////////////////
            //找手机 关
            MediaManager.stopVib();//停止震动
            MediaManager.stop();
            MediaManager.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkbox = AppValueCheckbox.getInance();
        if (checkbox.isFindPhondEnabled()) {
//            final RingService ring = RingService.getRing();   // TODO --- funfit中只有 响铃
            if (builder != null) {
                builder = null;
            }
            if (alert != null) {
//                ring.stopRing();
                MediaManager.stopVib();//停止震动
                MediaManager.stop();
                MediaManager.release();

                alert.cancel();
                findPhoneFlags = !findPhoneFlags;
            }
            if (findPhoneFlags) {
//                ring.ring();
                MediaManager.getMediaPlayerInstance();
                MediaManager.playSound(mRingtoneDescriptor);
                MediaManager.replayVib(sContext);//开始震动

                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.cancel_the_bell_ring).setCancelable(false);    // 取消响铃
                builder.setPositiveButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                ring.stopRing();
                                MediaManager.stopVib();//停止震动
                                MediaManager.stop();
                                MediaManager.release();

//                                BluetoothMtkChat.getInstance().retSyncFindWatchData();  //TODO  ---  反馈查找到手机了
                                BluetoothMtkChat.getInstance().sendFindWatchOff();
                                findPhoneFlags = true;
                                builder = null;
                                alert = null;
                            }
                        });
                alert = builder.create();
                //在Android7.0以上，用Toast会3.5s之后自动消失，故换成Alert，但在小米默认权限禁止，需要权限申请，暂先用型号适配作处理
                if(MobileInfoUtils.getMobileType().endsWith("HUAWEI") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }else {
                    alert.getWindow().setType(
                            //WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);TYPE_TOAST
                            WindowManager.LayoutParams.TYPE_TOAST);
                }
                alert.show();
            }
        }
    }

    public int getAudio() {
        ringerMode = myAudioManager.getRingerMode();
        return ringerMode;
    }

    public void setFlag(boolean flag) {
        ringerFlag = flag;
    }

    public void setringEnd(int ringer) {
        ringerEnd = ringer;
    }

    public void setAudio() {
        myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
        // updateConnectionStatus(false);
        super.onCreate();
      //  NotificationCollectorMonitorService. ensureCollectorRunning();//检查通知服务
        EventBus.getDefault().register(this);
        sInstance = this;

        mIsMainServiceActive = true;
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (applist.size() == 0) {
            applist.put(AppList.MAX_APP, (int) AppList.CREATE_LENTH);

            AppList.getInstance().saveAppList(applist);
        }
        HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
        boolean ignoreBoolean = (boolean) SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.IGNORELIST,false);
        if (ignoreList.size() == 0 && !ignoreBoolean) {
            LoadPackageTask loadPackageTask = new LoadPackageTask(sContext);
            loadPackageTask.execute();
        }

        registerService();


        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Intent intent = new Intent(this, RingService.class);
        this.startService(intent);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);


        sBroadcast = new ServiceBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.WEATHER_DATA); // 只有持有相同的action的接受者才能接收此广播
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(sBroadcast, filter);



        AssetManager assetManager = this.getAssets();
        try {
            mRingtoneDescriptor = assetManager.openFd(RINGTONE_NAME);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        KCTBluetoothManager.getInstance().registerListener(iConnectListener);

        if(!(boolean)SharedPreUtil.getParam(sContext,SharedPreUtil.USER,SharedPreUtil.LOCAL_WATCHINFO,false)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utils.setWatchInfoData(sContext,db);
                }
            }).start();
        }

        //MTK自带的找手机找手表协议
        kctDefaultAlerter = new KCTDefaultAlerter(sContext);
        LocalBluetoothLEManager.getInstance().setCustomizedAlerter(kctDefaultAlerter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        settingNotification();
        autoConnectDevice();
        return Service.START_STICKY;
    }
private  void settingNotification(){
    //Notification.Builder builder = null;
    /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel = null;
        String CHANNEL_ONE_ID = "com.kct.fundo.btnotification";
        String CHANNEL_ONE_NAME = "Channel One";
        notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
        builder = new Notification.Builder(getApplicationContext(),CHANNEL_ONE_ID);
    }else {
        builder = new Notification.Builder(getApplicationContext());
    }*/
    Notification.Builder builder = new Notification.Builder(getApplicationContext());
    builder.setContentTitle(getString(R.string.app_name));
    builder.setSmallIcon(R.drawable.speed_logo);
    if(null!=MainService.getInstance()&&MainService.getInstance().getState()==3){
        builder.setContentText(getString(R.string.connected));
        builder.setTicker(getString(R.string.connected));
    }else if(deviceState == STATE_CONNECTING || deviceState == STATE_DISCONNECTED){
        builder.setContentText(getString(R.string.bluetooth_connecting));
        builder.setTicker(getString(R.string.bluetooth_connecting));
    }else{
        builder.setContentText(getString(R.string.ble_not_connected));
        builder.setTicker(getString(R.string.ble_not_connected));
    }
    builder.setAutoCancel(false);
    builder.setWhen(System.currentTimeMillis());
    builder.setOngoing(true);
    Intent i = new Intent();
    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    builder.setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_HIGH);
    Notification notification = builder.build();
    notification.flags |= Notification.FLAG_NO_CLEAR;
    notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
    startForeground(1235, notification);
}

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        EventBus.getDefault().unregister(this);
        this.unregisterReceiver(mReceiver);
         mytime=null;
         myOxytime=null;
         myOxytimexieyang=null;
        issavexeyang=false;//是否保存血氧
        issavexeya=false;//是否保存血压
        issavexinlv=false;//是否保存心率

        issave = false;
        issaveOxy = false;
        issavexieyang = false;
        mIsMainServiceActive = false;

        WearableManager manager = WearableManager.getInstance();
        manager.removeController(YahooWeatherController.getInstance(sContext));
        manager.removeController(RemoteCameraController.getInstance());
        manager.removeController(NotificationController.getInstance(sContext));
        manager.removeController(MapController.getInstance(sContext));
        manager.removeController(VxpInstallController.getInstance());
        manager.removeController(DataSyncController.getInstance(sContext));
        manager.removeController(RemoteMusicController.getInstance(sContext));
        manager.removeController(MREEController.getInstance());
        manager.removeController(EXCDController.getInstance());
        manager.removeController(SOSController.getInstance());
        manager.unregisterWearableListener(mWearableListener);
        LocalPxpFmpController.unregisterBatteryLevelListener();
        FotaOperator.getInstance(sContext).close();

        stopRemoteCameraService();
        stopNotificationService();
        stopSmsService();

        KCTBluetoothManager.getInstance().unregisterListener(iConnectListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    /**
     * Return the instance of main service.
     *
     * @return main service instance
     */
    public static MainService getInstance() {

        if (sInstance == null) {
            Log.e(TAG, "getInstance(), Main service is null.");
            startMainService();
        }

        return sInstance;
    }

    /**
     * Return whether main service is started.
     *
     * @return Return true, if main service start, otherwise, return false.
     */
    public static boolean isMainServiceActive() {
        return mIsMainServiceActive;
    }

    private static void startMainService() {
        Log.e(TAG, "startMainService()");

        Intent startServiceIntent = new Intent(sContext, MainService.class);
        sContext.startService(startServiceIntent);
    }


    private void registerService() {
        // regist battery low
        Log.i(TAG, "registerService()");

        IntentFilter filter = new IntentFilter();

        filter.addAction("com.android.music.metachanged");
        filter.addAction("com.android.music.queuechanged");
        filter.addAction("com.android.music.playbackcomplete");
        filter.addAction("com.android.music.playstatechanged");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(ACTION_FINDWATCHON);
        filter.addAction(ACTION_FINDWATCHOFF);
        filter.addAction(ACTION_BLEDISCONNECTED);
        filter.addAction(ACTION_BLECONNECTED);
        filter.addAction(ACTION_AUTOCONNECT_DEVICE);
        filter.addAction(MainService.ACTION_BLEDISCONNECT);   //点击连接停止搜索
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);//----监听电量去检测通知服务，如果被杀则重新绑定
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        filter.addAction(MainService.ACTION_BKOTASUCCESS_RECON); // BK平台升级成功后发广播自动重连

//        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        filter.addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");

        filter.addAction(MainService.ACTION_BLECONNECTHID);  // HID 连接的广播
        this.registerReceiver(mReceiver, filter);

        startSmsService();      // 短信服务
        // start call service
//        startCallService();   //打电话服务  todo --- 8.0以后不能在这启动

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        int targetSdkVersion;
//        try {
//            final PackageInfo info = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);  // sContext   BTNotificationApplication.getInstance()
//            targetSdkVersion = info.applicationInfo.targetSdkVersion;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int writeSdCardPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            writeSdCardPermission =  sContext.checkSelfPermission(Manifest.permission.WRITE_CALL_LOG); // == PackageManager.PERMISSION_GRANTED;
        }else{
            writeSdCardPermission = PermissionChecker.checkSelfPermission(sContext, Manifest.permission.WRITE_CALL_LOG); //  == PermissionChecker.PERMISSION_GRANTED;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (targetSdkVersion >= Build.VERSION_CODES.M) {
//                result = sContext.checkSelfPermission(permission)  == PackageManager.PERMISSION_GRANTED;
//            } else {
//                result = PermissionChecker.checkSelfPermission(sContext, permission) == PermissionChecker.PERMISSION_GRANTED;
//            }
//        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////

//        int writeSdCardPermission = BTNotificationApplication.getInstance().checkSelfPermission(Manifest.permission.WRITE_CALL_LOG);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || writeSdCardPermission == PackageManager.PERMISSION_GRANTED) {
            startCallService();   //打电话服务
        }

        // showChoiceNotification();
        startRemoteCameraService();  // 远程拍照服务

        startNotificationService();  // 通知服务

        WearableManager manager = WearableManager.getInstance();
        manager.addController(YahooWeatherController.getInstance(sContext));
        manager.addController(RemoteCameraController.getInstance());
        manager.addController(NotificationController.getInstance(sContext));
        manager.addController(MapController.getInstance(sContext));
        manager.addController(VxpInstallController.getInstance());
        manager.addController(DataSyncController.getInstance(sContext));
        //manager.addController(EpoDownloadController.getInstance());
        manager.addController(RemoteMusicController.getInstance(sContext));
        manager.addController(MREEController.getInstance());
        manager.addController(EXCDController.getInstance());
        manager.addController(SOSController.getInstance());
        manager.registerWearableListener(mWearableListener);
        LocalPxpFmpController
                .registerBatteryLevelListener(mBatteryChangeListener);
        FotaOperator.getInstance(sContext);
    }

    private BatteryChangeListener mBatteryChangeListener = new BatteryChangeListener() {

        @Override
        public void onBatteryValueChanged(int currentValue, boolean needNotify) {
            Log.d(TAG, "onBatteryValueChanged() value = " + currentValue);
        }
    };


    private WearableListener mWearableListener = new WearableListener() {

        Intent intent = null;

        @Override
        public void onConnectChange(int oldState, int newState) {
            if (newState == WearableManager.STATE_CONNECTED) {
                Log.e(TAG, "oldState = " + oldState + ";  newState = " + newState);
                setState(STATE_CONNECTED);
                EventBus.getDefault().post(new MessageEvent(CONNECT_STATE,3));
                Intent intent = new Intent(MainService.ACTION_CHANGE_WATCH); //todo --- 发广播更新 主页的计步数据
                BTNotificationApplication.getInstance().sendBroadcast(intent);
                EventBus.getDefault().post(new MessageEvent(CONNECT_SUCCESS,WearableManager.getInstance().getLERemoteDevice()));
            }
            if (oldState == WearableManager.STATE_CONNECTED && newState != WearableManager.STATE_CONNECTED) {     //发送断连广播，发送到我的页面
                if(deviceState != STATE_NOCONNECT && mBluetoothAdapter.isEnabled()) {
                    setState(STATE_DISCONNECTED);
                }
                EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
                EventBus.getDefault().post(new MessageEvent(AUTO_CONNECT));
            }
            if (oldState == WearableManager.STATE_CONNECTING && newState != WearableManager.STATE_CONNECTED) {
                if(deviceState != STATE_NOCONNECT && mBluetoothAdapter.isEnabled()) {
                    setState(STATE_DISCONNECTED);
                }
                EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
                EventBus.getDefault().post(new MessageEvent(AUTO_CONNECT));
            }
        }

        @Override
        public void onDeviceChange(BluetoothDevice device) {
        }

        @Override
        public void onDeviceScan(BluetoothDevice device) {
        }

        @Override
        public void onModeSwitch(int newMode) {
            Log.d(TAG, "onModeSwitch newMode = " + newMode);
        }
    };


    public void startRemoteCameraService() {
        Log.i(TAG, "startRemoteCameraService()");
        mRemoteCameraService = new RemoteCameraService(sContext);
        RemoteCameraController.setListener(mRemoteCameraService);

    }

    public void stopRemoteCameraService() {
        Log.i(TAG, "stopRemoteCameraService()");

        RemoteCameraController.setListener(null);
        mRemoteCameraService = null;
    }

    public void startNotificationService() {
        Log.i(TAG, "startNotificationService()");
        mNotificationService = new NotificationService();
        NotificationController.setListener(mNotificationService);

    }

    public void stopNotificationService() {
        Log.i(TAG, "stopNotificationService()");

        NotificationController.setListener(null);
        mNotificationService = null;
    }


    public boolean getSmsServiceStatus() {
        return mIsSmsServiceActive;
    }

    /**
     * Start SMS service to push new SMS.
     */
    public void startSmsService() {
        Log.i(TAG, "startSmsService()");

        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }

        // Start SMS service
        if (mSmsService == null) {
            mSmsService = new SmsService();
        }
        IntentFilter filter = new IntentFilter(
                "com.mtk.btnotification.SMS_RECEIVED");
        registerReceiver(mSmsService, filter);

        mIsSmsServiceActive = true;
    }

    /**
     * Start call service to push new missed call.
     */
    public void startCallService() {
        Log.i(TAG, "startCallService()");

        // Ensure main service is started
        if (!mIsMainServiceActive) {
            startMainService();
        }

        // Start SMS service
        if (mCallService == null) {
            mCallService = new CallService(sContext);
            TelephonyManager telephony = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(mCallService, PhoneStateListener.LISTEN_CALL_STATE);
        }
		
		/* if (mCallService == null) {
            mCallService = new CallService(sContext);
        }
        TelephonyManager telephony = (TelephonyManager) sContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(mCallService, PhoneStateListener.LISTEN_CALL_STATE);*/

        mIsCallServiceActive = true;
    }


    /**
     * Stop SMS service.
     */
    public void stopSmsService() {
        Log.i(TAG, "stopSmsService()");

        // Stop SMS service
        if (mSmsService != null) {
            unregisterReceiver(mSmsService);
            if (null != XIEYANGList) {
                XIEYANGList.clear();
            }
            mSmsService = null;
        }

        mIsSmsServiceActive = false;
    }

    public boolean getCallServiceStatus() {
        return mIsCallServiceActive;
    }


    /**
     * Stop call service.
     */
    public void stopCallService() {
        Log.i(TAG, "stopCallService()");

        // Stop call service
        if (mCallService != null) {
            TelephonyManager telephony = (TelephonyManager) sContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(mCallService, PhoneStateListener.LISTEN_NONE);
            mCallService.stopCallService();
            mCallService = null;
        }
        mIsCallServiceActive = false;
    }


    public static void setNotificationReceiver(
            NotificationReceiver notificationReceiver) {
        sNotificationReceiver = notificationReceiver;
    }

    /**
     * Clear notification service instance.
     */
    public static void clearNotificationReceiver() {
        sNotificationReceiver = null;
    }

    /**
     * Return whether notification service is started.
     */
    public static boolean isNotificationReceiverActived() {
        return (sNotificationReceiver != null);
    }


    private String getHour(String s) {
        String hourStr = s.split(" ")[1];
        String hour = hourStr.split(":")[0];
        return hour;
    }


    public void updateRunDataArr(List<StepData> runList) {  // TODO  ----- // 更新新同步的运动数据  ---  //todo --- 一天总步数的保存
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }
        if (runList.size() < 0) {
            //Toast.makeText(getApplicationContext(), R.string.now_is_null_syn, Toast.LENGTH_SHORT).show();
            callbackSynchronousDialog();
            isShowToast = false;
            return;
        } else {
            for (int i = 0; i < runList.size(); i++) {
                StepData runData = runList.get(i);
                String date = runData.getTime();
                String year = "";
                String month = "";
                String day = "";
                if (date.indexOf(" ") == -1) {
                    year = date.split("-")[0];
                    month = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(date.split("-")[1]));
                    day = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(date.split("-")[2]));
                } else {
                    year = date.split(" ")[0].split("-")[0];
                    month = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(date.split(" ")[0].split("-")[1]));
                    day = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(date.split(" ")[0].split("-")[2]));
                }
                Query query = null;
                query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Date.eq(year + "-" + month + "-" + day))
                        .where(RunDataDao.Properties.Step.eq("0")).build();
                List list = query.list();  // 本地运动数据的集合
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                String time = simpleDateFormat.format(new Date());
                String hour = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(time.split(":")[0]));
                String minute = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(time.split(":")[1]));
                String second = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(time.split(":")[2]));
                if (list.size() == 0) {
                    RunData runDayData = new RunData();
                    runDayData.setMid(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID));
                    runDayData.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));
                    runDayData.setDate(year + "-" + month + "-" + day);
                    runDayData.setBinTime(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
                    runDayData.setHour(hour + "");
                    runDayData.setData(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second + "|" + runData.getCounts() + "|" + runData.getCalorie() + "|" + runData.getDistance());
                    runDayData.setStep("0");
                    runDayData.setCalorie("0");
                    runDayData.setDistance("0");
                    runDayData.setDayStep(runData.getCounts());
                    runDayData.setDayCalorie(runData.getCalorie());
                    runDayData.setDayDistance(runData.getDistance());
                    runDayData.setTimes("0");
                    runDayData.setUpload("0");
                    db.saveRunData(runDayData);
                } else {
                    for (int j = 0; j < list.size(); j++) {
                        RunData rundata = (RunData) list.get(j);
                        rundata.setDayStep(runData.getCounts());   // 一天的总步数      //todo --- 一天总步数的保存 （保存到 dayStep 中）
                        rundata.setDayCalorie(runData.getCalorie());
                        rundata.setDayDistance(runData.getDistance());
                        rundata.setHour(hour + "");
                        rundata.setBinTime(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
                        db.updataRunData(rundata);
                    }
                }
            }

            if(BTNotificationApplication.isSyncEnd) {     //todo  --- 参考BLE 同步数据成功 才发实时计步数据
                Intent intent = new Intent();
                intent.setAction(ACTION_SYNFINSH_SUCCESS); // TODO  ---MTK 发送同步实时步数的广播
                sContext.sendBroadcast(intent);
            }

          /*  Intent intent = new Intent();
//            intent.setAction(ACTION_SYNFINSH);    // 发数据同步成功的广播    ------ TODO  ---- 发送同步成功的广播（会同步所有的数据） ---  应该只发刷新计步数据的广播
//            intent.putExtra("step", "6");
            intent.setAction(ACTION_SYNFINSH_SUCCESS); // TODO  ---MTK 发送同步实时步数的广播
            sContext.sendBroadcast(intent);   */
            if (isShowToast) {
                callbackSynchronousDialog();  // 关闭同步的提示框
                isShowToast = false;
            }
            //Toast.makeText(getApplicationContext(), R.string.userdata_synfinsh, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveRunDataArr(final ArrayList<RunData> arr) {  // TODO  ----- // 保存新同步的运动数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Query query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Step.notEq("0")).build();   //取数据库分段全部步数
                ArrayList<RunData> slist = (ArrayList<RunData>) query.list();  // 本地运动数据的集合

                for (int i = 0; i < arr.size(); i++) {
                    boolean isFlag = false;
                    for (int j = 0; j < slist.size(); j++) {
                        if(arr.get(i).getBinTime().equals(slist.get(j).getBinTime())){
                            isFlag = true;
                            break;
                        }
                    }
                    if(!isFlag){
                        db.saveRunData(arr.get(i));
                    }
                }
            }
        }).start();

    }

    private void saveRunDataArrForX2(ArrayList<RunData> arr) {  // TODO  ----- // 保存新同步的运动数据
        String mDeviceDate = arr.get(0).getDate();
        Query query = db.getRunDao().queryBuilder()
                .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                .where(RunDataDao.Properties.Date.eq(arr.get(1).getDate())).build();  // TODO --- 根据日期查询数据

        List slist = query.list();  // TODO  先删除本地运动数据的集合
        for (int i = 0; i < slist.size(); i++) {  // TODO --- 是否需要加条件 集合的个数
            db.DeleteRunData((RunData) slist.get(i));
        }

        int step = 0;
        double calorie = 0;
        double distance = 0;
        int dataSize = 0;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);    Locale.ENGLISH
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String today = simpleDateFormat.format(new Date());
        for (int i = 0; i < arr.size(); i++) {  // TODO --- 是否需要加条件 集合的个数
            db.saveRunData(arr.get(i));
            if (arr.get(i).getDate().equals(today)) {
                step += Integer.parseInt(arr.get(i).getStep());
                calorie += Double.parseDouble(arr.get(i).getCalorie());
                distance += Double.parseDouble(arr.get(i).getDistance());
                if (!arr.get(i).getStep().equals("0")) {
                    dataSize++;
                }
            }
        }

        if(mDeviceDate.equals(today)){
            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN, step + "");   // TODO ---- 如果为当天的数据，则保存当天的同步数据
            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE, calorie + "");
            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE, distance + "");
            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDATASIZE, dataSize + "");

//            String dd =  String.format(Locale.ENGLISH, df.format(new Date()) + "");
//            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME,  String.format(Locale.ENGLISH,  df.format(new Date()) + ""));

           // String dd =  String.format(Locale.ENGLISH, today + "");  // yyyy-MM-dd

            //String[] sdss = dd.split("-");

//            String dddd =  String.format(Locale.ENGLISH, "%1$04d-%2$02d-%3$02d", sdss[0], sdss[1], sdss[2]);
//            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME,  String.format(Locale.ENGLISH,  df.format(new Date()) + ""));
//                        String distance = String.format(Locale.ENGLISH, "%.2f", (allStep * 0.7) / 1000.0);   String.format(Locale.ENGLISH, "%1$02d-%2$02d-%3$02d", mai, sec, yunshu) df.format(new Date()) + "");
//                        SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME, df.format(new Date()) + "");

            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHSYNCTIME, today + "");  //存手环同步时间
        }

    }


    public static boolean isShowToast = false;
    public static boolean isExistGET1 = false;


    public synchronized void heartdataWrite(List<HearData> list,boolean isRealTime) {  // TODO ---- 保存心率数据（参数，心率的开始时间，心率的数值） isRealTime:是否实时心率，智能表实时心率时间不做存储
        if (db == null) {
            db = DBHelper.getInstance(sContext);
//            db.deleteRunData(1);    // todo    ???????????????????
        }

        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  //

        if (list.size() != 0) {
            String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);
            for (int i = 0; i < list.size(); i++) {
                // 数据的开始时间不能小于 本地的开始时间   TODO ---- 也是用的 SPORT 计步保存在本地的时间   比较的是时间戳
                if (!watch.equals("2") && !isRealTime) { //实时心率时间不做存储比较
                    if (Utils.tolong(list.get(i).getBinTime()) < Utils.tolong(SharedPreUtil.readPre(this, SharedPreUtil.HEART, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))) {  // 1488441600000   1488441600---- 10位     2017/3/2 16:0:0
                        continue;
                    }
                }

                Date date = new Date(Long.valueOf(list.get(i).getBinTime()));
                SimpleDateFormat formatter = Utils.setSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(date);   // 1970-01-18 13:54:54
                //Log.e("dateString", dateString + SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));

                String beginTime = list.get(i).getBinTime();    // 1502680129
                String dateStringHeart = StringUtils.timestamp2Date(beginTime);   //2017-08-14 11:08:49      2017-03-16 19:00:00     2017-03-21 19:16:21

                HearData hear = new HearData();
                hear.setBinTime(dateStringHeart);  //心率的开始时间 ---  以日期格式保存到数据库

                String fff = mformatter.format(date);   // 1970-01-18
                hear.setData(mformatter.format(date));  //1970-01-18      设置心率的Data --- 数据   设置的 1970-01-18

//                hear.setDate(mformatter.format(date));  //  TODO ---
                hear.setDate(dateStringHeart.substring(0, 10));  //2017-08-14     设置日期 （日期格式）

//                hear.setHour("0");  // 时      c.Hour = list.get(i).getBinTime().split(" ")[1];
                hear.setHour(dateStringHeart.split(" ")[1]);  //TODO ： 保存 时:分 :秒

                hear.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));
                hear.setMid(mid);
                hear.setHeartbeat(list.get(i).getHeartbeat());  // 设置心率值

                hear.setHigt_hata(list.get(i).getHigt_hata());
                hear.setLow_hata(list.get(i).getLow_hata());
                hear.setAvg_hata(list.get(i).getAvg_hata());

//                hear.setTimes("0");         Long.valueOf(list.get(i).getBinTime())
                hear.setTimes(list.get(i).getBinTime());   // todo ---- 保存时间戳
                hear.setUpload("0");
                db.saveHearData(hear);
            }

            if (!watch.equals("2") && !isRealTime) { //实时心率时间不做存储比较
                // 保存最后一条睡眠数据的开始时间
                SharedPreUtil.savePre(this, SharedPreUtil.HEART, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getBinTime());  //
            }

//            Intent intent = new Intent();      // add 0414
//            intent.setAction(MainService.ACTION_SYNFINSH);    //todo ---- ACTION_SYNFINSH：：为刷新数据的广播 发数据同步成功的广播为 ：：ACTION_SYNFINSH_SUCCESS
//            sContext.sendBroadcast(intent);

        }
    }

    public synchronized void sleepdataWrite(List<SleepData> list) {  //TODO --- 保存普通的睡眠数据 （参数，浅睡时间，深睡时间，睡眠开始时间）
        if (db == null) {
            db = DBHelper.getInstance(sContext);
            db.deleteRunData(1);
        }
        int s = 0;
        if (list.size() != 0) {
            String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);  // 用户id
            for (SleepData msleepdata : list) {
                // 同步的睡眠的开始时间 小于 本地睡眠时间 （错误的数据）
                if (Utils.tolong(msleepdata.getStarttimes()) < Utils.tolong(SharedPreUtil.readPre(this, SharedPreUtil.SLEEP, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))) {
                    continue;
                }
                String sleepStartTime = msleepdata.getStarttimes();  // 2017-3-17 20:18:00 --- 已为日期格式（不为时间戳）。不需要转换
//                String dateString = StringUtils.timestamp2Date(sleepStartTime);   // 2017-03-16 19:00:00

                SleepData shear = new SleepData();
                shear.setStarttimes(msleepdata.getStarttimes());  //保存到数据库--- 设置睡眠的开始时间 ----- 日期格式
//                shear.setDate(mformatter.format(date));
                shear.setDate(sleepStartTime.substring(0, 10));//设置日期

                shear.setDeepsleep(msleepdata.getDeepsleep());  // 设置深睡时间   ---- 分钟数
                shear.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));
                shear.setMid(mid);
                shear.setLightsleep(msleepdata.getLightsleep()); // 设置浅睡时间  ---- 分钟数
                shear.setAutosleep("0");

                String deepSrt = msleepdata.getDeepsleep();
                String lightSrt = msleepdata.getDeepsleep();
                int deepSleepTime = Integer.valueOf(msleepdata.getDeepsleep());  // 深睡时间
                int lightSleepTime = Integer.valueOf(msleepdata.getLightsleep()); // 浅睡时间
                if (deepSleepTime > 0) {   // 0- 深睡 1-浅睡 2 -休息 3 等待 4 -运动 5-停止
                    shear.setSleeptype("0");     // 深睡时间大于0 --- 该SleepData 为 深睡数据
                } else if (lightSleepTime > 0) {
                    shear.setSleeptype("1");   // 浅睡时间大于0 --- 该SleepData 为 浅睡数据
                } else {
                    shear.setSleeptype("2");  // 默认设为 2 ： 休息状态 TODO 待验证
                }

                shear.setSleepmillisecond((Utils.tolong(msleepdata.getDeepsleep()) + Utils.tolong(msleepdata.getLightsleep())) * 60 * 1000 + "");// 设置睡眠的总时间 的 豪秒数值   (深睡+浅睡) * 60 *1000 ==== 睡眠的毫秒值

//                shear.setEndTime((Utils.tolong(msleepdata.getDeepsleep()) + Utils.tolong(msleepdata.getLightsleep())) * 60 * 1000 + Utils.tolong(msleepdata.getStarttimes()) + "");
                shear.setEndTime(msleepdata.getEndTime());  // 设置睡眠的结束时间
                db.saveSleepData(shear);
            }
            // 保存最后一条睡眠数据的开始时间
//            SharedPreUtil.savePre(this, SharedPreUtil.SLEEP, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getStarttimes());  // 睡眠时间 精确 到秒
            SharedPreUtil.savePre(this, SharedPreUtil.SLEEP, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getEndTime());
        }
    }

    public synchronized void autosleepdataWrite(List<SleepData> list) {  //TODO ---- 保存自动睡眠的数据 （参数：睡眠开始时间，睡眠结束时间，睡眠的类型）
        if (db == null) {
            db = DBHelper.getInstance(sContext);
            db.deleteRunData(1);
        }
        int s = 0;
        if (list.size() != 0) {
            ArrayList<RunData> arrRun = new ArrayList<RunData>();
            String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);
            for (SleepData mHeartData : list) {
                // 睡眠的开始时间 小于  本地的手表的睡眠时间时  （不需要保存）
                if (Utils.tolong(mHeartData.getStarttimes()) < Utils.tolong(SharedPreUtil.readPre(this, SharedPreUtil.SLEEP, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))) {
                    continue;
                }
                Date date = new Date(Long.valueOf(mHeartData.getStarttimes())); // 根据睡眠的开始时间获取到对应的日期
                SimpleDateFormat formatter = Utils.setSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(date);  // 日期时间值，精确到秒
                Log.e("sleep_time", dateString);
//  保存自动睡眠的数据 （参数：睡眠开始时间，睡眠结束时间，睡眠的类型）
                SleepData shear = new SleepData();
                shear.setStarttimes(mHeartData.getStarttimes());  // 设置开始时间
                shear.setDate(mformatter.format(date));            // 设置日期
                shear.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));  // 手表的mac地址
                shear.setMid(mid);   // 保存用户id
                shear.setEndTime(mHeartData.getEndTime());     // 结束时间
                shear.setSleeptype(mHeartData.getSleeptype());   // 睡眠的类型
                // 分钟值
                long min = (Utils.tolong(mHeartData.getEndTime()) - Utils.tolong(mHeartData.getStarttimes())) / 1000 / 60;  // (结束时间 - 开始时间)/1000/60  ----
                // 结束时间 - 开始时间 对应的值为ms,除1000，得到秒，再除60 ，得到分钟
                if (mHeartData.getSleeptype().equals("0")) {  // 0---为深睡
                    shear.setDeepsleep(min + ""); // 设置深睡的时间
                    shear.setLightsleep("0");   // 浅睡时间为0（深睡时浅睡为0）
                } else {   // 不为0 ---- 为浅睡
                    shear.setDeepsleep("0");
                    shear.setLightsleep(min + "");  // 设置浅睡时间
                }
                shear.setSleepmillisecond(Utils.tolong(mHeartData.getStarttimes()) - Utils.tolong(mHeartData.getEndTime()) + "");  // 设置睡眠的总毫秒数 （应该是 结束时间 - 开始时间）
                shear.setAutosleep("1");

            }
            //根据手表的MAC地址 保存睡眠的开始时间 （取收到的睡眠数据的最后一条）
            SharedPreUtil.savePre(this, SharedPreUtil.SLEEP, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getStarttimes());
        }
    }

    public synchronized void BTdataWrite(List<StepData> list) {
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }
        String bluetoothAddress = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC);   // 15:48:51:35:64:65
        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  //

        if (list.size() != 0) {
            ArrayList<RunData> arrRun = new ArrayList<RunData>();
            String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);  // ""  用户id
            if (watch.equals("2")) {
                int fenduanAllSteps = 0;
                for (int i = 0; i < list.size(); i++) {  //   StepData mStepData : list
                    RunData run = new RunData();
                    if (i == list.size() - 1) {
                        fenduanAllSteps += Integer.valueOf(list.get(i).getCounts());
                        String ssTime = list.get(i).getTime();
//                        SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");
//                String dateString = formatter.format(date2);  // 1970-01-18 13:47:42  1970-01-18 13:47:42
                        String dateString = StringUtils.timestamp2Date(ssTime);
                        run.setBinTime(dateString);
                        run.setCalorie(list.get(i).getCalorie());
                        run.setDayCalorie(list.get(i).getCalorie());
                        run.setData(dateString + "|" + list.get(i).getCounts() + "|" + list.get(i).getDistance() + "|" + list.get(i).getCalorie());  // 设置运动数据  开始时间|步数|距离|卡路里

                        String dda = dateString.substring(0, 10);
                        run.setDate(dateString.substring(0, 10));

                        run.setDistance(list.get(i).getDistance());
                        run.setDayDistance(list.get(i).getDistance());
                        run.setHour(getHour(dateString));
                        run.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));
                        run.setMid(mid);
                        run.setStep(list.get(i).getCounts());
                        run.setDayStep(fenduanAllSteps + "");
                        run.setTimes("0");
                        run.setUpload("0");
                        arrRun.add(run);
                        Log.e(dateString, list.get(i).getCounts() + "");

                    } else {
                        String ssTime = list.get(i).getTime();  // 1489662000   ---- 2017/3/16 19:0:0   1489662000
                        SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");
//                String dateString = formatter.format(date2);  // 1970-01-18 13:47:42  1970-01-18 13:47:42
                        String dateString = StringUtils.timestamp2Date(ssTime);
                        run.setBinTime(dateString);
                        run.setCalorie(list.get(i).getCalorie());
                        run.setDayCalorie(list.get(i).getCalorie());
                        run.setData(dateString + "|" + list.get(i).getCounts() + "|" + list.get(i).getDistance() + "|" + list.get(i).getCalorie());

                        String dda = dateString.substring(0, 10);
                        run.setDate(dateString.substring(0, 10));

                        run.setDistance(list.get(i).getDistance());
                        run.setDayDistance(list.get(i).getDistance());
                        run.setHour(getHour(dateString));  //
                        run.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));
                        run.setMid(mid);
                        run.setStep(list.get(i).getCounts());

                        run.setDayStep("0");
                        run.setTimes("0");
                        run.setUpload("0");
                        arrRun.add(run);
                        Log.e(dateString, list.get(i).getCounts() + "");

                        fenduanAllSteps += Integer.valueOf(list.get(i).getCounts());
                    }
                }

            } else {
                for (StepData mStepData : list) {
                    long curTime = Utils.tolong(mStepData.getTime());   //
                    long saveSportTime = Utils.tolong(SharedPreUtil.readPre(this, SharedPreUtil.SPORT, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)));
                    /*if (!watch.equals("2")) {
                        if (Utils.tolong(mStepData.getTime()) <= Utils.tolong(SharedPreUtil.readPre(this, SharedPreUtil.SPORT, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))) {    // 1488441600000   1488441600---- 10位     2017/3/2 16:0:0
                            continue;
                        }
                    }*/
                    RunData run = new RunData();
                    String ssTime = mStepData.getTime();
                    SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");

                    String dateString = StringUtils.timestamp2Date(ssTime);

                    run.setBinTime(dateString);
                    run.setCalorie(mStepData.getCalorie());
                    run.setDayCalorie(mStepData.getCalorie());
                    run.setData(dateString + "|" + mStepData.getCounts() + "|" + mStepData.getDistance() + "|" + mStepData.getCalorie());  // 设置运动数据  开始时间|步数|距离|卡路里

                    String dda = dateString.substring(0, 10);  // 2017-03-16
                    run.setDate(dateString.substring(0, 10));

                    run.setDistance(mStepData.getDistance());  //
                    run.setDayDistance(mStepData.getDistance());//
                    run.setHour(getHour(dateString));  //
                    run.setMac(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC));
                    run.setMid(mid);
                    run.setStep(mStepData.getCounts());
//                run.setDayStep(mStepData.getCounts());  //
                    run.setDayStep("0"); //
                    run.setTimes("0");
                    run.setUpload("0");
                    arrRun.add(run);
                    Log.e(dateString, mStepData.getCounts() + "");
                    if(Integer.parseInt(run.getStep()) != 0){
                        SharedPreUtil.savePre(this, SharedPreUtil.SPORT, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), mStepData.getTime());
                    }
                }
            }

            if (arrRun != null && arrRun.size() > 0) {
                if (watch.equals("3")) {
                    saveRunDataArr(arrRun);   //MTK
                    //SharedPreUtil.savePre(this, SharedPreUtil.SPORT, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getTime());    // TODO 保存 新同步的运动数据的 最后一条 的 时间(10位时间戳) --- 计步精确到时
                } else {
                    saveRunDataArrForX2(arrRun);   //BLE
                }
            }

            /*if (isShowToast) {
                callbackSynchronousDialog();
                isShowToast = false;
            }*/
        } else {
//            Intent intent = new Intent();
//            intent.setAction(ACTION_SYNFINSH);
//            sContext.sendBroadcast(intent);

//            if (isShowToast) {
//                Toast.makeText(getApplicationContext(), R.string.now_is_null_syn, Toast.LENGTH_SHORT).show();
//                callbackSynchronousDialog();
//                isShowToast = false;
//            }
        }
    }



    private ReturnData hd;

    // private String strHeartrateJson = "";

    public void setHeartrateData(ReturnData hd) {
        this.hd = hd;
    }

    public interface ReturnData {
        public void heartrateData(String strHeartrateJson);

        public void runData(String strRunJson);

        public void sleepData(String strSleepJson);
    }

    public void callback(String strHeartrateJson, String strRunJson,
                         String strSleepJson) {
        if (hd != null) {
            if (!"".equals(strHeartrateJson)) {
                hd.heartrateData(strHeartrateJson);
            }
            if (!"".equals(strRunJson)) {
                hd.runData(strRunJson);
            }
            if (!"".equals(strSleepJson)) {
                hd.sleepData(strSleepJson);
            }
        }
    }




    private CloseDialog cd;

    public void setSynchronousDialog(CloseDialog cd) {
        this.cd = cd;
    }

    public interface CloseDialog {
        public void closeDialog();
    }

    public void callbackSynchronousDialog() {
        if (cd != null) {
            cd.closeDialog();
        }
    }



    public void sendMessage(String value) {   // 发送字符串的方法
    }






    /**
     * 内部类广播
     *
     * @author chendalin
     */
    private class ServiceBroadcast extends BroadcastReceiver {     // TODO --- 从HomeFragment发送广播，接收到广播后，后台获取天气
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainService.WEATHER_DATA)) {
                readSharedPreferences();
            }else if(intent.getAction() != null && intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String uuid = (String) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.UUID, "");
                if(device.getBondState() == BluetoothDevice.BOND_BONDED &&
                        !TextUtils.isEmpty(uuid) && uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())){
                    BTNotificationApplication.getMainService().connectDevice(device);
                }else if(device.getBondState() == BluetoothDevice.BOND_NONE &&
                        !TextUtils.isEmpty(uuid) && uuid.equals(BleContants.RX_SERVICE_872_UUID.toString())){
                    EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
                }
            }
        }
    }

    /**
     * 读取已经保存的经纬度，调用请求服务器方法
     */
    public void readSharedPreferences() {
        String la = UTIL.readPre(sContext, "weather", "Latitude");
//        double latitude = 0;
        if (!StringUtils.isEmpty(la)) {
            latitude = Double.valueOf(la);
        }

        String lo = UTIL.readPre(sContext, "weather", "Longitude");   // 这里是从主页的定位获取到的数据
//        double longitude = 0;
        if (!StringUtils.isEmpty(lo)) {
            longitude = Double.valueOf(lo);
        }

        if (!StringUtils.isEmpty(la) && !StringUtils.isEmpty(lo)) {    // TODO ---- 获取天气   && !"".equals(city)
            try {
                ///////////////////////////////////////////////////////////////   通过经纬度获取城市code
                if (!NetWorkUtils.isConnect(BTNotificationApplication.getInstance())) return;
                if (0 != latitude && 0 != longitude ) {   // && !"4.9E-324".equals(String.valueOf(latitude))
                    HTTPController hc = HTTPController.getInstance();
                    hc.open(BTNotificationApplication.getInstance());

                    //获取城市code 新接口city换code
                    String latAndlng = longitude + "," + latitude;
                    String language = Utils.getLanguage();
                    String cn = language.contains("zh") ? "cn" : "en";  // //todo  --- 天气接口  fundo/weather/requestWeather.do? location=116.376673,39.91737&lang=en&appName=0&systemType=1&appVersion=1&uuid=dasf
                    String uid = DeviceUtils.getUniqueId(this);//设备id
//                    String urlCityCode = MainService.GET_WEATHER_URL + "location=" + latAndlng + "&lang=" + cn;  //新接口
                    //appName ---  0:分动；1：分动手环，2：分动穿戴,3:funfit,4:funrun；5分动圈；6：手表
                    //systemType  --- 安卓：1；ios：2；手表：3
                    //appVersion----  App版本或手表版本
                    String urlCityCode = MainService.GET_WEATHER_URL + "location=" + latAndlng + "&lang=" + cn + "&appName=" + 0 + "&systemType=" + 1 + "&appVersion=" + getVersionName() + "&uuid=" + uid;  //todo  ---- 20180720 新接口
// http://wx.funos.cn:8080/fundo/weather/requestWeather.do?location=113.951739,22.554364&lang=cn&appName=0&systemType=1&appVersion=V1.3.4&uuid=cf1af82fcb1281480a99c69a7b93d17b


                    //String urlCityCode = MainService.GET_CITY_CODE + latAndlng + MainService.API_STORE_KEY;  //旧街口
                    Log.e(TAG, "getElevation---获取和风城市code接口地址：" + urlCityCode);
                    hc.getNetworkStringData(urlCityCode, mHandler, URL_CITYCODE);

                    getDatas();   //获得海拔
                }
                //////////////////////////////////////////////////////////////

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getVersionName() {
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Set them by default value
            versionName = Constants.NULL_TEXT_NAME;
            e.printStackTrace();
        }

        return versionName ;
    }

    private void getDatas() {      //获取谷歌海拔
        if (!NetWorkUtils.isConnect(BTNotificationApplication.getInstance())) return;
        if (0 != latitude && 0 != longitude && !"4.9E-324".equals(String.valueOf(latitude))) {
            HTTPController hc = HTTPController.getInstance();
            hc.open(BTNotificationApplication.getInstance());
            String urlEle = MainService.GET_ALTITUDE_OF_GOOGLE_API + latitude + "," + longitude + "&sensor=false";
            Log.e(TAG, "getElevation---获取谷歌海拔接口地址：" + urlEle);
            hc.getNetworkStringData(urlEle, mHandler, WHAT_URL_ELEVATION);
        }
    }

    public void setUvPressureValue() {      //从后台获取天气
        if (StringUtils.isEmpty(code)) {
            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.no_location),Toast.LENGTH_SHORT).show();
            return;
        }else{
            HTTPController hc = HTTPController.getInstance();
            hc.open(BTNotificationApplication.getInstance());

           /* String newurl = "";
            String language = Utils.getLanguage();
            if (language.equals("zh")) {
                newurl = MainService.URL_GET_UV_PRESSURE_TEP_TWO_DAY_NEW + code + "/lang/" + language;
            }else {
                newurl = MainService.URL_GET_UV_PRESSURE_TEP_TWO_DAY_NEW + code;
            }*/
//                                                                                           http://app.fundo.xyz:8001/weardoor/index.php?s=Api/Apistore/weather/cityid/CN101280604/lang/zh
//                                                                                            http://app.fundo.xyz:8001/weardoor/index.php?s=Api/Apistore/weather/cityid/CN101280604langzh
           //                                                                                http://119.23.15.199:8001/weardoor/index.php?s=Api/Apistore/weather/cityid/TR751575/lang/zh
           String newurl = MainService.URL_GET_UV_PRESSURE_TEP_TWO_DAY_NEW + code;  // http://app.fundo.xyz:8001/weardoor/index.php?s=Api/Apistore/weather/cityid/CN101280604
            Log.e(TAG, "setUvPressureValue---获取两天的气压 紫外线 温度：" + newurl);
            hc.getNetworkStringData(newurl, mHandler, NEW_WEATHER);
        }
    }


    /**
     * 添加天气json数据到shared中
     *
     * @author xujianbo
     * @time 2017/3/4 11:11
     */
    public void weatherParse(String response) {
//        Log.e(TAG,"weatherParse---请求返回数据为:"+response);
        if (response.equals("-1")) {
            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.no_location),Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject src = new JSONObject(response);   // new
            if (src.getString("flag").equals("Success")) {
                if (src.getString("data").contains("null")) {
                    Log.e(TAG, "weatherParse----天气数据包含null");
                    return;
                }
                JSONObject data = src.getJSONObject("data");

                JSONObject base = data.getJSONObject("basic");
                String ziwaixian = base.getString("ziwaixian");  // 2
                String qiya = base.getString("qiya");   // 1004
                String city = base.getString("city");
                String wendu = base.getString("wendu");
                UTIL.savePre(sContext, "weather", "ziwaixian", ziwaixian);  //TODO --- 保存紫外线数据
                UTIL.savePre(sContext, "weather", "qiya", qiya);            //todo ---- 保存气压数据
                UTIL.savePre(sContext, "weather", "city", city);      //保存城市数据(mtk平台)
                UTIL.savePre(sContext, "weather", "wendu",wendu);      //当前温度(mtk平台)

                String list = data.getString("list"); // [{"date":"2017-07-13","code":"302","txt":"雷阵雨","min":"26","max":"33"},{"date":"2017-07-14","code":"302","txt":"雷阵雨","min":"26","max":"33"},{"date":"2017-07-15","code":"306","txt":"中雨","min":"26","max":"32"}]
                JSONArray temperatureList = new JSONArray(list);
                for (int i = 0; i < temperatureList.length(); i++) {
                    JSONObject day = temperatureList.getJSONObject(i);
                    String date = day.getString("date");  //天气日期   2017-07-13

                    Calendar calendar = Calendar.getInstance();   // 当前天的日期  2017-06-28
                    calendar.setTime(new Date());
                    String mcurDate = getDateFormat.format(calendar.getTime());

                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) + 1);
                    String mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 后一天的数据

                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) + 2);
                    String mcurDate2 = getDateFormat.format(calendar2.getTime());

                    if (date.equals(mcurDate)) {  //为当前天的日期
                        String code = day.getString("code");  // 天气状况  302 --- 雷阵雨
                        String min = day.getString("min");    // 最低气温  26
                        String max = day.getString("max");    // 33
                        UTIL.savePre(sContext, "weather", "date", date);
                        UTIL.savePre(sContext, "weather", "code", code);
                        UTIL.savePre(sContext, "weather", "low", min);  // Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8))
                        UTIL.savePre(sContext, "weather", "high", max);

                    }

                    if (date.equals(mcurDate1)) {  //为后一天的日期
                        String code = day.getString("code");  // 天气状况  302 --- 雷阵雨
                        String min = day.getString("min");    // 最低气温  26
                        String max = day.getString("max");    // 33
                        UTIL.savePre(sContext, "weather", "nextdate", date);
                        UTIL.savePre(sContext, "weather", "nextcode", code);
                        UTIL.savePre(sContext, "weather", "nextlow", min);  // Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8))
                        UTIL.savePre(sContext, "weather", "nexthigh", max);
                    }

                    if (date.equals(mcurDate2)) {  //为第三天的日期
                        String code = day.getString("code");  // 天气状况  302 --- 雷阵雨
                        String min = day.getString("min");    // 最低气温  26
                        String max = day.getString("max");    // 33
                        UTIL.savePre(sContext, "weather", "thirddate", date);
                        UTIL.savePre(sContext, "weather", "thirdcode", code);
                        UTIL.savePre(sContext, "weather", "thirdlow", min);  // Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8))
                        UTIL.savePre(sContext, "weather", "thirdhigh", max);
                    }
//                    Log.e(TAG,"weatherParse---for() "+code+"的紫外线:"+ziwaixian+",气压:"+qiya+"当前温度:"+wendu+'\n'+date+"的温度信息为："+min+"~"+max+",天气情况:"+code);
                }
                sendReceiver();  //todo --- 发广播给主页，更新天气数据
            } else {
                Log.e(TAG, "weatherParse---json wrong weather");
                Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.no_location),Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析服务器返回的json 数据
     *
     * @param s
     */
    private void resolve(String s) {   // todo ---- 读取 天气
        if (!s.equals("FAILURE")) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject forecast = jsonObject.getJSONObject("query").getJSONObject("results")
                        .getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(0);
                Log.e("requestData", "queryObject == " + forecast);
                UTIL.savePre(sContext, "weather", "day", forecast.getString("day"));
                UTIL.savePre(sContext, "weather", "date", forecast.getString("date"));
                UTIL.savePre(sContext, "weather", "low", Integer.toString((int) ((forecast.getInt("low") - 32) / 1.8)));
                UTIL.savePre(sContext, "weather", "high",
                        Integer.toString((int) ((forecast.getInt("high") - 32) / 1.8)));
                UTIL.savePre(sContext, "weather", "code", forecast.getString("code"));
                sendReceiver();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送广播给MainService端
     */
    private void sendReceiver() {
        Log.e("WeatherService ", " 服务 发送广播");
        Intent intent = new Intent();
        intent.setAction(ACTION_WEATHER);
        sendBroadcast(intent);
    }


    /**
     * 接听电话.
     */
    public void startcall() {
        TelephonyManager telephony = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneUtils.autoAnswerPhone(sContext, telephony);
    }

    /**
     * 挂断电话.
     */
    public void endcall() {
        TelephonyManager telephony = (TelephonyManager) sContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        try {
            PhoneUtils.getITelephony(telephony);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PhoneUtils.endPhone(sContext, telephony);
    }


    private final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};
    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    public boolean phonelingk = false;
    private String tellname = "";

    public void getcall(String number) {
        if (number == null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("upordown", "down");
                jsonObject.put("calllinking", "no");
//                sendMessage("call" + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        tellname = "";

        getPhoneContacts(number);
        if (tellname.equals("")) {
            getSIMContacts(number);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("number", number);
            jsonObject.put("name", tellname);
            jsonObject.put("upordown", "up");
//            sendMessage("call" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void callend() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("upordown", "down");
            jsonObject.put("calllinking", "yes");
//            sendMessage("call" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 得到手机通讯录联系人信息
     **/
    private void getPhoneContacts(String number) {
        ContentResolver resolver = sInstance.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                Log.e("phoneNumber", "" + phoneNumber.replace("-", ""));
                if (phoneNumber.replace("-", "").replace(" ", "").equals(number)) {
                    tellname = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    break;
                }
            }
            phoneCursor.close();
        }
    }

    /**
     * 得到手机SIM卡联系人人信息
     **/
    private void getSIMContacts(String number) {
        ContentResolver resolver = sInstance.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                Log.e("phoneNumber", "" + phoneNumber.replace("-", ""));
                if (phoneNumber.replace("-", "").replace(" ", "").equals(number)) {
                    tellname = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    break;
                }
            }
            phoneCursor.close();
        }
    }

    /**
     * 写入数据并处理数据
     */
    public synchronized void writeToDevice(byte[] l2, boolean isAck) {   //写入数据
       if(null!=l2) {
           byte[] l1 = new L1Bean().MessagePack(sequenctId, l2);
           Log.e("1-----------", Arrays.toString(l2));

           byte[] command = UtilsLX.arraycat(l1, l2);
           Log.e(TAG, "send = " + UtilsLX.bytesToHexString(command));  // 刷新 运动模式 数据后，再发送命令 会卡在这里，其他的命令发送不了了
           if(isAck) {
               KCTBluetoothManager.getInstance().sendCommand_a2d(command);
           }else{
               KCTBluetoothManager.getInstance().writeCommand_a2d(command);
           }
       }
    }

    private CountDownTimer receiveCountDownTimer = new CountDownTimer((8 - BTNotificationApplication.needReceDataNumber) * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Intent intent = new Intent();
            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
            intent.putExtra("step", needReceDataNumber + "");
            sContext.sendBroadcast(intent);
            if(needReceDataNumber == 6){
                BTNotificationApplication.isSyncEnd = true;
                cancel();
            }
            needReceDataNumber ++;
        }

        @Override
        public void onFinish() {
            needReceDataNumber = 0;
            needSendDataType = 0;
        }
    };

    private int hasStepTemp = 0;
    private int hasRunTemp = 0;
    private float hasCalorieTemp = 0;
    private float hasDistanceTemp = 0;

    /**
     * 处理L2数据
     */
    @NonNull
    private void L2_Parse(byte[] bytes) {  //  L2_Parse   getL2Command
        if (bytes != null && bytes.length > 0) {
            String resModebyteslx = UtilsLX.bytesToHexString(bytes);
            //TODO  单包数据请求
            if (bytes.length == 5) {  //todo --- 手表主动发送的命令   bytes.length == 13     ----- 单包去掉前8byte
                // BA30 0005 0043 0002 0500510000  --- 找手机    // BA30 0005 00BE 0007  0400460000
                int byte1 = bytes[0];
                int byte3 = bytes[2];
                if (byte1 == BleContants.FIRMWARE_UPGRADE_COMMAND) {          //TODO -- 固件升级命令   0x01   byte9 == BleContants.FIRMWARE_UPGRADE_COMMAND
                    if (byte3 == BleContants.FIRMWARE_UPGRADE_ECHO) {
                        //chat.stop();
                        disConnect();
                        Intent intent = new Intent(ACTION_BLEDISCONNECTED);
                        sContext.sendBroadcast(intent);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);    // false
                        EventBus.getDefault().post(new MessageEvent("goto_updata"));//进入固件升级模式
                    }
                }
                else if (byte1 == BleContants.INSTALL_COMMAND) {              //TODO -- 设置命令        0x02

                }
                else if (byte1 == BleContants.WEATHER_PROPELLING) {           //TODO -- 天气推送命令     0x03

                } else if (byte1 == BleContants.DEVICE_COMMADN) {                    //TODO -- 设备命令         0x04
                    switch (byte3) {   // -------  key  byte11
                        case BleContants.CRAMER_OPEN: //  打开拍照
//                            if (!SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_CAMERA).equals("0")) {   //判断是否开启照片按钮
                            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                                if ((Boolean) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true)){
                                    Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
                                    sendBroadcast(intent);
                                }
                            } else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
//                                if (RemoteCamera.isSendExitTakephoto) {
////                                    RemoteCamera.isSendExitTakephoto = false;
//                                    return;
//                                } else {
//                                    Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
//                                    sendBroadcast(intent);
//                                }

                                if(!CameraActivity.isPhoneExitTakephoto){  // 手机端主动退出
                                    Intent intentCamera = new Intent(getApplicationContext(), CameraActivity.class);     // todo  --- 打开 拍照              BTNotificationApplication.getInstance()    getApplicationContext()  sContext   getBaseContext
                                    intentCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intentCamera);

//                                    Intent launchIntent = new Intent();
//                                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    launchIntent.setClass(mContext, RemoteCamera.class);
//                                    mContext.startActivity(launchIntent);
                                }else{
                                    CameraActivity.isPhoneExitTakephoto = false;
                                }

                            } else {
                                Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
                                sendBroadcast(intent);
                            }

//                                Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
//                                sendBroadcast(intent);
//                            }
                            break;

                        case BleContants.CRAMER_TAKE: // 拍照
                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                                sendBroadcast(new Intent(ACTION_REMOTE_CAMERA));
                            }
                            else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1"))
                            {
                                boolean b = (boolean) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true);
                                if(b)
                                {
                                    Intent intent2 = new Intent("0x47");   // 发送远程拍照 --- 对应的广播
                                    sendBroadcast(intent2);
                                }
                            }
                            else {
                                    Intent intent2 = new Intent("0x47");   // 发送远程拍照 --- 对应的广播
                                    sendBroadcast(intent2);
                            }
                            break;

                        case BleContants.CRAMER_CLOSE: // 退出拍照
//                            if (!SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_CAMERA).equals("0")) {   //判断是否开启照片按钮
//                            }

                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                                sendBroadcast(new Intent(ACTION_REMOTE_CAMERA_EXIT));
                            }else {
                                Intent intent3 = new Intent("0x48");   // 发送远程拍照 --- 对应的广播
                                sendBroadcast(intent3);
                                isDeviceSendExitCommand = true;
                            }
                            break;

                        case BleContants.KEY_WATCH_LOCK_SCREEN: // 手表发送锁屏功能
                            startLock();
                            break;

                        case BleContants.DIAL_RETURN: // 发送绑定设备后，手表返回  表盘数据返回
                            // 04004F00003032233037233136233137233138233139233230233231233232233233233234233235233236233237233238233239233331233332233333233334233335233336233337233338233339233430233431233432233433233434
                            // 04 00 4F0000 3032233037233136233137233138233139233230233231233232233233233234233235233236233237233238233239233331233332233333233334233335233336233337233338233339233430233431233432233433233434

                            byte[] newByte = new byte[bytes.length - 5];   // 去掉前面5byte 第6byte开始为 手表实际返回数据
                            System.arraycopy(bytes, 5, newByte, 0, bytes.length - 5);
                            String resnewbytes = new String(newByte);
                            Log.e(TAG, "newbytes.toString----" + resnewbytes);
                            // 02#07#16#17#18#19#20#21#22#23#24#25#26#27#28#29#31#32#33#34#35#36#37#38#39#40#41#42#43#44   ---- 表盘序号数据
//                            String[] dialData = resnewbytes.split("#");

                            String ss = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL);
                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL).equals(resnewbytes)) {
                                return;
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                Log.e("readMessagereadcontant", resnewbytes);
                                Intent intentNew = new Intent();
                                intentNew.setAction(MainService.ACTION_CLOCK_SKIN_MODEL_CHANGE);

                                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL, resnewbytes);
                                // intent.putExtra("type", readMessagereadcontant);
                                sendBroadcast(intentNew);
                            }
                            break;

                        case BleContants.WATCH_BLUETOOTH_DISCONNECT: // 手表发送蓝牙断开连接
//                            MainService.Daring = true;
//                            stopChat();     //TODO -----  APP端蓝牙断开连接
                            break;

                        case BleContants.KEY_NOTIFICATION_PUSH: // 手机通知推送（app端推送手机短信等）

                            break;

                        case BleContants.SYNC_USER_WEIGHT: // 手机端同步用户体重--- 手表端回复 （同步成功时）

                            break;
                        case BleContants.KEY_INPUTASSIT_SEND:        //协助输入
                            EventBus.getDefault().post(new MessageEvent("assistInput_success"));
                            break;
                        case BleContants.SYN_ADDREST_LIST:        //联系人
                            EventBus.getDefault().post(new MessageEvent("constants"));  //发送到
                            break;

                    }
                } else if (byte1 == BleContants.FIND_COMMAND) {                        //TODO -- 查找命令   0x05
                    switch (byte3) {   // -------  key
                        case BleContants.FIND_PHONE: // 找手机
//                            if(!SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_FIND_PHONE).equals("0")){  // 0 --- ???
                            showDialog();      //todo ----    单包找手机

//                            Intent intent2 = new Intent();
//                            intent2.setAction(MainService.ACTION_FINDPHONE);   // 发找手机广播，
//                            sendBroadcast(intent2);
//                            }
                            break;

                        case BleContants.FIND_DEVICE: // 找手表  ---- 手表端回复命令
                            Intent intent = new Intent();
                            intent.setAction(MainService.ACTION_GETWATCH);   // 发广播，销毁加载框
                            sendBroadcast(intent);
                            break;

                    }
                } else if (byte1 == BleContants.REMIND_COMMAND) {                      //TODO -- 提醒命令   0x06

                } else if (byte1 == BleContants.RUN_MODE_COMMADN) {                     //TODO -- 运动模式命令   0x07    单包
                    if(bytes[2] == BleContants.RUN_BASE_RETURN){
                        int l2ValueLength = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));
                        if (l2ValueLength == 0) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // todo  --- 当没有运动模式数据时，发广播，销毁加载的同步框
                            getApplicationContext().sendBroadcast(broadcastIntent);
                            return;
                        }
                    }
                } else if (byte1 == BleContants.SLEEP_COMMAND) {                        //TODO -- 睡眠命令     0x08

                } else if (byte1 == BleContants.HEART_COMMAND) {                           //TODO -- 心率命令   0x09

                } else if (byte1 == BleContants.SYN_COMMAND) {                      //TODO -- 同步命令   0x0A    ---- 单包
//                    closeDialogNoData();  // TODO  注释 0616
                    if (bytes[2] == BleContants.BRACELREALSPORT) {                         //TODO 手环运动模式数据返回 (0xA5)
                        int l2ValueLength = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));
                        if (l2ValueLength == 0) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // todo  --- 当没有运动模式数据时，发广播，销毁加载的同步框
                            getApplicationContext().sendBroadcast(broadcastIntent);
                            return;
                        }
                    }

                    if (bytes[2] == BleContants.BRACELET_RUN_DATA_RETURN) {
                        if(!BTNotificationApplication.isSyncEnd) {
                            if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                                Log.e(TAG,"needSendDataType = " + needSendDataType + " ; needReceDataNumber = " + needReceDataNumber);
                                needReceDataNumber = 1;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "1");
                                sContext.sendBroadcast(intent);
                            } else {

                                getSyncDataNumInService++;

                                Log.e("liuxiaodata", "需要收到的数据条目为----" + BTNotificationApplication.needReceiveNum);
                                if (BTNotificationApplication.needReceiveNum == 21) {  // 1/5,2/5
                                    if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 5) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 13) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 16) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 20) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送5广播");
                                    }

                                } else if (BTNotificationApplication.needReceiveNum == 6) {
                                    if (getSyncDataNumInService == 1) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 3) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 4) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 5) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送5广播");
                                    }
                                }//////////////////////////////////////////////////////////////////

                                Log.e("liuxiaodata", "已收到的数据条数为--" + getSyncDataNumInService);
                            }
                        }

                    } else if (bytes[2] == BleContants.BRACELET_SLEEP_DATA_RETURN) {
                        if(!BTNotificationApplication.isSyncEnd) {
                            if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                                if (needReceDataNumber == 1) {
                                    needReceDataNumber = 2;
                                    Intent intent = new Intent();
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "2");
                                    sContext.sendBroadcast(intent);
                                }
                                if (BTNotificationApplication.needSendDataType == needReceDataNumber) {
                                    receiveCountDownTimer.cancel();
                                    receiveCountDownTimer.start();
                                }


                            } else {
                                getSyncDataNumInService++;

                                Log.e("liuxiaodata", "需要收到的数据条目为----" + BTNotificationApplication.needReceiveNum);
                                if (BTNotificationApplication.needReceiveNum == 21) {  // 1/5,2/5
                                    if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 5) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 13) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 16) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 20) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送5广播");
                                    }

                                } else if (BTNotificationApplication.needReceiveNum == 6) {
                                    if (getSyncDataNumInService == 1) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 3) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 4) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 5) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送5广播");
                                    }
                                }//////////////////////////////////////////////////////////////////

                                Log.e("liuxiaodata", "已收到的数据条数为--" + getSyncDataNumInService);
                            }
                        }
                    } else if (bytes[2] == BleContants.BRACELET_HEART_DATA_RETURN) {

                        if (!BTNotificationApplication.isSyncEnd) {
                            if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                                Log.e(TAG,"needSendDataType = " + needSendDataType + " ; needReceDataNumber = " + needReceDataNumber);
                                if (needReceDataNumber == 1) {
                                    needReceDataNumber = 2;
                                    Intent intent = new Intent();
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "2");
                                    sContext.sendBroadcast(intent);
                                } else if (needReceDataNumber == 2) {
                                    needReceDataNumber = 3;
                                    Intent intent = new Intent();
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "3");
                                    sContext.sendBroadcast(intent);
                                }
                                if (BTNotificationApplication.needSendDataType == needReceDataNumber) {
                                    receiveCountDownTimer.cancel();
                                    receiveCountDownTimer.start();
                                }
                            } else {
                                getSyncDataNumInService++;

                                Log.e("liuxiaodata", "需要收到的数据条目为----" + BTNotificationApplication.needReceiveNum);
                                if (BTNotificationApplication.needReceiveNum == 21) {  // 1/5,2/5
                                    if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 5) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 13) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 16) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 20) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送5广播");
                                    }

                                } else if (BTNotificationApplication.needReceiveNum == 6) {
                                    if (getSyncDataNumInService == 1) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 3) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 4) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 5) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送5广播");
                                    }
                                }//////////////////////////////////////////////////////////////////

                                Log.e("liuxiaodata", "已收到的数据条数为--" + getSyncDataNumInService);
                                if (getSyncDataNumInService == BTNotificationApplication.needReceiveNum) {  //   BTNotificationApplication.bleSyncDataDays = 7;    HomeFragment.getHistoryDataDays
//                                Intent intent = new Intent();      // add 0414
//                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播      ACTION_SYNFINSH_SUCCESS
//                                sContext.sendBroadcast(intent);

                                    Intent intent = new Intent();      // add 0414
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "6");
                                    sContext.sendBroadcast(intent);
                                    Log.e("liuxiaodata", "发送6广播");

//                                HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize5));

                                    getSyncDataNumInService = 0;
                                    BTNotificationApplication.isSyncEnd = true;

                                    String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);

                                    String isFirstSync = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);
                                    if (StringUtils.isEmpty(isFirstSync) || isFirstSync.substring(0, 1).equals("0")) {      // TODO--- 没取过7天的数据了
                                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "1#" + curMacaddress);  // todo 同步完成了 --- 第一次同步 7 天的数据，取过7天的数据后 ，将 SYNCED 置1
                                    }
                                } else { //todo --- 没有达到相应的接收数据的条数
                                    mHandler.sendEmptyMessageDelayed(HEART_DATA_FAILOVER, 20000); //TODO --- 延时30秒发送，广播
                                }
                            }
                        }
                    }else if(bytes[2] == BleContants.BLOOD_PRESSURE_HIS) {                  //TODO -- 历史血压数据返回 -- 0xAD
                        if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                            Log.e(TAG,"needSendDataType = " + needSendDataType + " ; needReceDataNumber = " + needReceDataNumber);
                            if (needReceDataNumber == 1) {
                                needReceDataNumber = 2;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "2");
                                sContext.sendBroadcast(intent);
                            } else if (needReceDataNumber == 2) {
                                needReceDataNumber = 3;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "3");
                                sContext.sendBroadcast(intent);
                            } else if(needReceDataNumber == 3){
                                needReceDataNumber = 4;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "4");
                                sContext.sendBroadcast(intent);
                            }
                            if (BTNotificationApplication.needSendDataType == needReceDataNumber) {
                                receiveCountDownTimer.cancel();
                                receiveCountDownTimer.start();
                            }
                        }

                    }

                } else if (byte1 == BleContants.CALIBRATION_COMMAND) {                       //TODO -- 校准命令    0x0B

                } else if (byte1 == BleContants.FACTORY_COMMAND) {                             //TODO -- 工厂命令   0x0C

                } else if (byte1 == BleContants.PUSH_DATA_TO_PHONE_COMMAND) {                        //TODO -- 查找命令   0x0D   推送数据到手机 (接挂电话相关)
                    //    0A00A300 64110909000000004C00000059000000A6000000B0000000E1000000F3000001360000028E00000C1000000EDA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
                    //    0A00A300 641109080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000063300000C2600000E3E00000F1D
                    switch (byte3) {   // -------  key                                      BA300 0080 04F000A0D00010003000101      0A00 AD00 08 1109080101000000
//                        case BleContants.GESTURE_PUSH_COMMAND: // 手势智控推送    ----    0D0001 00  03 00 0000    0D0001 00  0300 0101
//                            Log.e("phone", "智控开关发命令了");  // TAG     05005000 0100
//
//                            //todo  --- 当开关为开时，将抬手亮屏的开关打开 ----发广播
//
//
//                            break;

                        case BleContants.REJECT_DIAL_COMMAND: // 拒接电话
                            uart_data_end_call(sContext);
                            Log.e("phone", "拒接电话了");  // TAG
                            break;

                        case BleContants.ANSWER_DIAL_COMMAND: // 接电话
                            startcall();
                            Log.e("phone", "接电话了");
                            break;

                     



                        case BleContants.PLAY_MUSIC_COMMAND: // 播放音乐推送
                            controlMusic(KEYCODE_MEDIA_PLAY_PAUSE);
                            break;

                        case BleContants.PAUSE_MUSIC_COMMAND: // 暂停音乐推送
                            controlMusic(KEYCODE_MEDIA_PLAY_PAUSE);
                            break;

                        case BleContants.LAST_MUSIC_COMMAND: // 上一首推送
                            controlMusic(KEYCODE_MEDIA_PREVIOUS);
                            break;

                        case BleContants.NEXT_MUSIC_COMMAND: // 下一首推送
                            controlMusic(KEYCODE_MEDIA_NEXT);
                            break;

                    }
                } else if (byte1 == (byte) 0x10){              //TODO   ---时钟机芯校准命令
                    switch (byte3){
                        case (byte)0x02:
                            EventBus.getDefault().post(new MessageEvent(CalibrationActivity.REFUSE_CALIBRATION));
                            break;
                        case (byte)0x03:
                            EventBus.getDefault().post(new MessageEvent(CalibrationActivity.REFUSE_CALIBRATION));
                            break;
                        case (byte)0x04:
                            EventBus.getDefault().post(new MessageEvent(CalibrationActivity.CONFIRM_CALIBRATION));
                            break;
                        case (byte)0x05:
                            if(bytes.length > 5){
                                int code = bytes[5];
                                if(code == 0){
                                    EventBus.getDefault().post(new MessageEvent(CalibrationActivity.SEND_CALIBRATION));
                                }else{
                                    EventBus.getDefault().post(new MessageEvent(CalibrationActivity.CANCEL_CALIBRATION));
                                }
                            }
                            break;
                    }
                } else if (byte1 == BleContants.COMMAND_WEATHER_INDEX) {//TODO -- 表盘推送    0x0E
                    if (bytes[2] == BleContants.DIAL_PUSH) {   // 表盘推送
                        int ddd = 666;
                    }
                }
            } else {    //todo ---- 手机端主动 发送命令      //TODO  多包数据请求
                int byte1 = bytes[0];   // 命令号    0x11,0x03,0x15,0x13,0x10,0x28,0x3D,
                int byte3 = bytes[2];
                if (byte1 == BleContants.FIRMWARE_UPGRADE_COMMAND) {              //TODO -- 固件升级命令   0x01
                    switch (bytes[2]) {
                        case BleContants.FIRMWARE_UPGRADE_REQURN:                 //TODO -- 固件信息返回
                            String version = "v" + bytes[5] + "." + bytes[6] + "." + bytes[7];          //固件版本
                            Log.i(TAG, "固件信息版本: " + version);
                            SharedPreUtil.savePre(sContext, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION, version);
                            int braceletType = bytes[8];
                            Log.i(TAG, "升级平台: " + braceletType);         //升级平台  0:nordic  1:dialog  2：MTK  3：智能表
                            SharedPreUtil.savePre(sContext, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE, braceletType + "");
                            int platformCode = (((bytes[9] << 16) & 0xff0000) | ((bytes[10] << 8) & 0xff00) | (bytes[11] & 0xff));                   //手环序列号
                            SharedPreUtil.savePre(sContext, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE, platformCode + "");
                            Log.i(TAG, "手环序列号：" + platformCode);
                            EventBus.getDefault().post(new MessageEvent("firmeware_version"));
                            if(bytes.length == 16){
                                String protocolCode = "V" + bytes[12] + "." + bytes[13] + "." + bytes[14] + bytes[15];
                                Log.i(TAG,"协议版本：" + protocolCode);
                                if(DateUtil.versionCompare("V1.1.39",protocolCode) && braceletType == 3){   //现根据协议版本号，大于1.1.40
                                    CONSTANTS = true;
                                }else {
                                    CONSTANTS = false;
                                }
                                SharedPreUtil.savePre(sContext, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.PROTOCOLCODE, protocolCode + "");
                            }
                            if(db == null) {
                                db = DBHelper.getInstance(sContext);
                            }
                            HTTPController.SynWatchInfo(sContext,db,platformCode);   //请求型号适配功能
                            break;
                        case BleContants.FIRMWARE_UPGRADE_ECHO:                   //TODO -- 固件升级回应
                            if("1".equals(SharedPreUtil.readPre(sContext, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE))) {   //dialog升级
                                KCTBluetoothManager.getInstance().setDilog(true,iDialogCallback);
                                EventBus.getDefault().post(new MessageEvent("firmWare_start"));
                                //isSendFile = true;

                            }
                            break;
                    }
                }
                else if (byte1 == BleContants.TEMPERATURE_COMMAND) {
                    if(byte3 == BleContants.TEMPERATURE_RETURN)
                    {
                        if(bytes.length<12){
                            return;
                        }
                        String year = String.format(Locale.ENGLISH,"20" + "%02d", (bytes[5] & 0xff));   // 第5位 年
                        Log.e(TAG, "sleep year =" + year);
                        String month = String.format(Locale.ENGLISH,"%02d", bytes[6] & 0xff);
                        Log.e(TAG, "sleep mouth =" + month);
                        String day = String.format(Locale.ENGLISH,"%02d", bytes[7] & 0xff);
                        Log.e(TAG, "sleep day =" + day);
                        String hour = (bytes[9] & 0xff)+":"+(bytes[10] & 0xff)+":00";
                        String temperatureValue = (bytes[11] & 0xff)+"."+(bytes[12] & 0xff);
                        String date = year+"-"+month+"-"+day;
                        String binTime = null;
                        try {
                            binTime = mSimpleDateFormat.parse(date + " " + hour).getTime()+"";
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Temperature temperature = new Temperature();
                        temperature.setBinTime(binTime);
                        temperature.setDate(date);
                        temperature.setTemperatureValue(temperatureValue);
                        String mac = "";
                        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
                            mac = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC);
                        } else {
                            mac = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC);
                        }
                        temperature.setMac(mac);
                        try {
                            saveTemperature(temperature);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post("updateTemperature");
                    }
                }
                else if (byte1 == BleContants.ECG_COMMAND) {              //TODO -- 心电命令

                    switch (byte3)
                    {
                        case BleContants.ECG_HISTORY_CONTENT:
                            parseEcg(bytes);
                            break;
                        case BleContants.ECG_CONFIGURATION:
                            if (bytes.length >= 8) {
                                int speed = bytes[5];
                                int gain = bytes[6];
                                short rate = (short) ((bytes[7]&0xff)<<8|(bytes[8]&0xff));
                                int dimension = 350;
                                SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_SPEED,speed);
                                SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_GAIN,gain);
                                SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_RATE,rate);
                                SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_DIMENSION,dimension);
                            }
                            break;
                        case BleContants.ECG_START:
                        case BleContants.ECG_FINISH:
                        case BleContants.ECG_CONTENT: {
                            EventBus.getDefault().post(bytes);
                            break;
                        }
                    }
                }
                else if (byte1 == BleContants.INSTALL_COMMAND) {              //TODO -- 设置命令        0x02
                    if (bytes[2] == BleContants.INSTALL_SETTING_RETURN) {            //TODO -- 读取手环设置响应   0x2F
// 02002F 004A
// 000000 0000
// 000000 0000
// 000000 0000
// 000000 0000
// 000000 0000   --- 25 bytes	闹钟（全部5组
// 0109 0B7F 001E 0032   --- 8 bytes		久坐提醒
// 0012 AA3C 8813 0000   --- 8 bytes		用户信息
// 03                   1 byte		提醒模式
// 0000 0000 00             5 bytes		免打扰设置
// 0000 0000 0000       6 bytes		心率检测   （6bytes）
// 0018 3C0E            4 bytes		系统设置   --- 没有用到
// 0000 0000 0000 0000  8 bytes		喝水提醒
// 0000                  2 bytes		消息推送
// 8813 0000            4 bytes		运动目标
// 000000               3 bytes		手势亮屏
                        byte[] clockByte = new byte[25];       //闹钟
                        byte[] sedentaryByte = new byte[8];    //久坐提醒
                        byte[] userInfoByte = new byte[8];     //用户信息
                        byte[] notifyByte = new byte[1];       //提醒模式
                        byte[] disturbByte = new byte[5];      //免打扰设置
                        byte[] heartByte = new byte[6];        //心率检测     byte[] heartByte = new byte[7];  TODO--- 修改为6 0703 17:41
                        byte[] systemByte = new byte[4];       //系统设置
                        byte[] waterByte = new byte[8];        //喝水提醒
                        byte[] notificationByte = new byte[2]; //消息推送
                        byte[] goalByte = new byte[4];         //运动目标
                        byte[] gestureByte = new byte[3];        //手势亮屏

                        System.arraycopy(bytes, 5, clockByte, 0, clockByte.length);  // 闹钟从 5开始 25
                        System.arraycopy(bytes, 5 + clockByte.length, sedentaryByte, 0, sedentaryByte.length);  // 久坐
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length, userInfoByte, 0, userInfoByte.length); // 用户信息
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length, notifyByte, 0, notifyByte.length); // 提醒模式
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length + notifyByte.length, disturbByte, 0, disturbByte.length); // 免打扰设置
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length + notifyByte.length + disturbByte.length, heartByte, 0, heartByte.length); // 心率检测
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length + notifyByte.length + disturbByte.length + heartByte.length, systemByte, 0, systemByte.length); // 系统设置
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length + notifyByte.length + disturbByte.length + heartByte.length + systemByte.length, waterByte, 0, waterByte.length); // 喝水提醒
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length + notifyByte.length + disturbByte.length + heartByte.length + systemByte.length + 2, goalByte, 0, goalByte.length); // 运动目标
                        System.arraycopy(bytes, 5 + clockByte.length + sedentaryByte.length + userInfoByte.length + notifyByte.length + disturbByte.length + heartByte.length + systemByte.length + waterByte.length + 2 + 4, gestureByte, 0, gestureByte.length); // 手势亮屏

                        ArrayList<AlarmClockData> alarmList = new ArrayList<>();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        for (int i = 0; i < (clockByte.length / 5); i++) {
                            Log.i(TAG, "闹钟" + (i + 1) + "  小时:" + clockByte[i * 5]);
                            Log.i(TAG, "闹钟" + (i + 1) + "  分钟:" + clockByte[(i * 5) + 1]);
                            Log.i(TAG, "闹钟" + (i + 1) + "  重复:" + Utils.getBinaryStrFromByte(clockByte[(i * 5) + 2]));
                            Log.i(TAG, "闹钟" + (i + 1) + "  标签:" + clockByte[(i * 5) + 3]);
                            Log.i(TAG, "闹钟" + (i + 1) + "  开关:" + clockByte[(i * 5) + 4]);
//                            if(clockByte[i*5+4] ==1){ //todo -- 不用管开关
                            AlarmClockData alarmClock = new AlarmClockData();
                            if (clockByte[(i * 5) + 3] != 1) { // 标签位为1 说明用户设置过
                                continue;
                            }
                            int hour = clockByte[i * 5];
                            int minute = clockByte[i * 5 + 1];
                            if (hour > 23 || hour < 0){    //小时是否大于23小时或小于0小时，统一作为0小时
                                hour = 0;
                            }
                            if (minute > 59 || minute < 0){ //分钟是否大于59分钟或小于0分钟，统一作为0分钟
                                minute = 0;
                            }

                            alarmClock.setRemind(clockByte[(i * 5) + 3] + "");  // 标签
                            alarmClock.setTime(String.format(Locale.ENGLISH,"%02d", hour) + ":" + String.format(Locale.ENGLISH,"%02d", minute));
                            alarmClock.setCycle(Utils.getBinaryStrFromByte(clockByte[(i * 5) + 2]));
                            alarmClock.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                            alarmClock.setMid(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MID));
                            alarmClock.setType(clockByte[i * 5 + 4] + "");
                            alarmClock.setAlarm_time(simpleDateFormat.format(new Date()) + " " + String.format(Locale.ENGLISH,"%02d", hour) + ":" + String.format(Locale.ENGLISH,"%02d", minute));
                            alarmClock.setUpload("0");
                            alarmList.add(alarmClock);
//                            }
                        }
                        if (db == null) {
                            db = DBHelper.getInstance(sContext);
                        }
                        Query query = db.getAlarmClockDataDao().queryBuilder()
                                .where(AlarmClockDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                                .build();   //  .where(AlarmClockDataDao.Properties.Type.eq("1")) 不用管开关

//                        List ddd =  query.list();
                        while (query.list().size() != 0) {
                            for (int i = 0; i < query.list().size(); i++) {
                                db.DeleteAlarmClockData((AlarmClockData) query.list().get(i));
                            }
                        }

                        if (alarmList.size() > 0) {
                            for (int i = 0; i < alarmList.size(); i++) {
                                db.saveAlarmClockData(alarmList.get(i));
                            }
                        }
//                        List ddd2 =  query.list();

                        Log.e(TAG, "久坐开关:" + sedentaryByte[0]);
                        Log.e(TAG, "久坐开始时间:" + sedentaryByte[1]);
                        Log.e(TAG, "久坐结束时间:" + sedentaryByte[2]);
                        Log.e(TAG, "久坐重复:" + Utils.getBinaryStrFromByte(sedentaryByte[3]).substring(0, 7));
                        Log.e(TAG, "久坐时间:" + (((sedentaryByte[4] << 8) & 0xff00) | (sedentaryByte[5] & 0xff)));
                        Log.e(TAG, "久坐阈值:" + (((sedentaryByte[6] << 8) & 0xff00) | (sedentaryByte[7] & 0xff)));
                        int sedentaryStart = sedentaryByte[1];   //久坐开始时间
                        int sedentaryEnd = sedentaryByte[2];     //久坐结束时间
                        int sedentaryTime = ((sedentaryByte[4] << 8) & 0xff00) | (sedentaryByte[5] & 0xff);  //久坐时间
                        int sedentaryStep = ((sedentaryByte[6] << 8) & 0xff00) | (sedentaryByte[7] & 0xff);  //久坐阈值
                        if (sedentaryStart > 23 || sedentaryStart < 0){   //小时是否大于23小时或小于0小时，统一作为0小时
                            sedentaryStart = 0;
                        }
                        if (sedentaryEnd > 23 || sedentaryEnd < 0){    //小时是否大于23小时或小于0小时，统一作为0小时
                            sedentaryEnd = 0;
                        }
                        List<String> sedentaryHourList = Utils.getSitList();
                        List<String> sedentaryStepList = Utils.getStepList();
                        int sedentaryTimes = 0;
                        int sedentarySteps = 0;
                        for (int i = 0; i < sedentaryHourList.size(); i++) {    //久坐时间判断是否在30,60,90,120,150,180,210,240,270,300，330,360
                            if(sedentaryTime == Integer.parseInt(sedentaryHourList.get(i))){
                                sedentaryTimes = sedentaryTime;
                                break;
                            }
                        }
                        if(sedentaryTimes == 0){
                            sedentaryTimes = 30;
                        }
                        for (int i = 0; i < sedentaryStepList.size(); i++) {    //久坐阈值判断是否在100,200,300,400,500,600,700,800,900,1000
                            if(sedentaryStep == Integer.parseInt(sedentaryStepList.get(i))){
                                sedentarySteps = sedentaryStep;
                                break;
                            }
                        }
                        if(sedentarySteps == 0){
                            sedentarySteps = 100;
                        }
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.SIT_REPEAT_WEEK, Utils.getBinaryStrFromByte(sedentaryByte[3]).substring(0, 7));
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.SIT_SWITCH, sedentaryByte[0] == 1 ? true : false);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.SIT_START_TIME, sedentaryStart);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.SIT_STOP_TIME, sedentaryEnd);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.SIT_TIME, sedentaryTimes);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.SIT_STEP, sedentarySteps);


                        Log.i(TAG, "用户性别:" + userInfoByte[0]);
                        Log.i(TAG, "用户年龄:" + userInfoByte[1]);
                        Log.i(TAG, "用户身高:" + userInfoByte[2]);
                        Log.i(TAG, "用户体重:" + userInfoByte[3]);
                        Log.i(TAG, "用户步数:" + Utils.getInt(userInfoByte, 4));


                        Log.i(TAG, "提醒模式:" + notifyByte[0]);
                        int notifyMode = notifyByte[0];
                        if(notifyMode > 3 || notifyMode < 1){    //提醒模式大于3或小于1，统一作为1模式:1：亮屏；2：震动；3：亮屏+震动
                            notifyMode = 3;
                        }
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.ALARM_MODE, notifyMode);


                        Log.i(TAG, "免打扰开关:" + disturbByte[0]);
                        Log.i(TAG, "免打扰开始小时:" + disturbByte[1]);
                        Log.i(TAG, "免打扰开始分钟:" + disturbByte[2]);
                        Log.i(TAG, "免打扰结束小时:" + disturbByte[3]);
                        Log.i(TAG, "免打扰结束分钟:" + disturbByte[4]);
                        int disturbStartHour = disturbByte[1];
                        int disturbStartMin = disturbByte[2];
                        int disturbEndHour = disturbByte[3];
                        int disturbEndMin = disturbByte[4];

                        if(disturbStartHour > 23 || disturbStartHour < 0){
                            disturbStartHour = 0;
                        }

                        if(disturbStartMin != 0 && disturbStartMin != 30){
                            disturbStartMin = 0;
                        }

                        if(disturbEndHour > 23 || disturbEndHour < 0){
                            disturbEndHour = 0;
                        }

                        if(disturbEndMin != 0 && disturbEndMin != 30){
                            disturbEndMin = 0;
                        }

                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.NO_START_TIME, disturbStartHour);//勿扰开始小时
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.NO_START_TIME_MIN, disturbStartMin);//勿扰开始分钟
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.NO_STOP_TIME, disturbEndHour);//勿扰结束小时
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.NO_STOP_TIME_MIN, disturbEndMin);//勿扰结束分钟
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.NO_SWITCH, disturbByte[0] == 1 ? true : false);


                        Log.i(TAG, "心率检测开关:" + heartByte[0]);  // 心率检测现在改为 6
                        Log.i(TAG, "心率检测开始小时:" + heartByte[1]);
                        Log.i(TAG, "心率检测开始分钟:" + heartByte[2]);
                        Log.i(TAG, "心率检测结束小时:" + heartByte[3]);
                        Log.i(TAG, "心率检测结束分钟:" + heartByte[4]);
                        Log.i(TAG, "心率检测结束间隔:" + heartByte[5]);

                        int heartStartHour = heartByte[1];
                        int heartStartMin = heartByte[2];
                        int heartEndHour = heartByte[3];
                        int heartEndMin = heartByte[4];
                        int heartFrequency = heartByte[5];

                        if(heartStartHour > 23 || heartStartHour < 0){
                            heartStartHour = 0;
                        }
                        if(heartStartMin != 0 && heartStartMin != 30){
                            heartStartMin = 0;
                        }
                        if(heartEndHour > 23 || heartEndHour < 0){
                            heartEndHour = 0;
                        }
                        if(heartEndMin != 0 && heartEndMin != 30){
                            heartEndMin = 0;
                        }
                        int frequency = 0;
                        List<String> frequencyList = Utils.getHeartList();
                        for (int i = 0; i < frequencyList.size(); i++) {
                            if(heartFrequency == Integer.parseInt(frequencyList.get(i))){
                                frequency = heartFrequency;
                                break;
                            }
                        }
                        if(frequency == 0){
                            frequency = 10;
                        }
//                        Log.i(TAG,"心率检测结束预留:" + heartByte[6]);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.HEART_SWITCH, heartByte[0] == 1 ? true : false);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.HEART_START_TIME, heartStartHour); // 开始时间小时
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.HEART_START_TIME_MIN, heartStartMin); // 开始时间分钟
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.HEART_STOP_TIME, heartEndHour); // 结束时间小时
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.HEART_STOP_TIME_MIN, heartEndMin); // 结束时间分钟
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.HEART_FREQUENCY, frequency);  // 心率检测结束间隔


                        Log.i(TAG, "系统设置语言:" + systemByte[0]);
                        Log.i(TAG, "系统设置小时制:" + systemByte[1]);
                        Log.i(TAG, "系统设置亮屏:" + systemByte[2]);
                        Log.i(TAG, "系统设置手机配对:" + systemByte[3]);

                        Log.i(TAG, "喝水提醒开关:" + waterByte[0]);
                        Log.i(TAG, "喝水提醒开始小时:" + waterByte[1]);
                        Log.i(TAG, "喝水提醒开始分钟:" + waterByte[2]);
                        Log.i(TAG, "喝水提醒结束小时:" + waterByte[3]);
                        Log.i(TAG, "喝水提醒结束分钟:" + waterByte[4]);
                        Log.i(TAG, "喝水提醒重复:" + Utils.getBinaryStrFromByte(waterByte[5]).substring(0, 7));
                        Log.i(TAG, "喝水提醒间隔:" + (((waterByte[6] << 8) & 0xff00) | (waterByte[7] & 0xff)));

                        int waterStartHour = waterByte[1];
                        int waterStartMin = waterByte[2];
                        int waterEndHour = waterByte[3];
                        int waterEndMin = waterByte[4];
                        int waterFrequency = ((waterByte[6] << 8) & 0xff00) | (waterByte[7] & 0xff);

                        if(waterStartHour > 23 || waterStartHour < 0){
                            waterStartHour = 0;
                        }
                        if(waterStartMin != 0 && waterStartMin != 30){
                            waterStartMin = 0;
                        }
                        if(waterEndHour > 23 || waterEndHour < 0){
                            waterEndHour = 0;
                        }
                        if(waterEndMin != 0 && waterEndMin != 30){
                            waterEndMin = 0;
                        }
                        List<String> waterList = Utils.getDrinkList();
                        int waters = 0;
                        for (int i = 0; i < waterList.size(); i++) {
                            if(waterFrequency == Integer.parseInt(waterList.get(i))){
                                waters = waterFrequency;
                                break;
                            }
                        }
                        if(waters == 0){
                            waters = 30;
                        }
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_SWITCH, waterByte[0] == 1 ? true : false);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_REPEAT_WEEK, Utils.getBinaryStrFromByte(waterByte[5]).substring(0, 7));
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_START_TIME, waterStartHour);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_START_TIME_MIN, waterStartMin);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_STOP_TIME, waterEndHour);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_STOP_TIME_MIN, waterEndMin);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.DRINK_FREQUENCY, waters);

                        Log.i(TAG, "手势智控--左右手----" + gestureByte[0]);
                        Log.i(TAG, "手势智控--抬手亮屏----:" + gestureByte[1]);
                        Log.i(TAG, "手势智控--翻腕亮屏----:" + gestureByte[2]);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.GESTURE_HAND, (int)gestureByte[0]);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.RAISE_BRIGHT, gestureByte[1] == 1 ? true : false);
                        SharedPreUtil.setParam(sContext, SharedPreUtil.USER, SharedPreUtil.FANWAN_BRIGHT, gestureByte[2] == 1 ? true : false);
                    }
                } else if (byte1 == BleContants.WEATHER_PROPELLING) {           //TODO -- 天气推送命令     0x03

                } else if (byte1 == BleContants.DEVICE_COMMADN) {               //TODO -- 设备命令         0x04
                    switch (bytes[2]) {   //todo -----  key类型
                        case BleContants.CRAMER_OPEN: //  打开拍照
                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                                if ((Boolean) SharedPreUtil.getParam(sContext, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true)){
                                    Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
                                    sendBroadcast(intent);
                                }
                            }else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                              /*  if (RemoteCamera.isSendExitTakephoto) {
                                    return;
                                } else {
                                    Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
                                    sendBroadcast(intent);
                                }*/
                                ///////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                if (state) {  // 是否打开了相机
//                                    Intent intentCamera = new Intent(BTNotificationApplication.getInstance(), CameraActivity.class);
//                                    startActivity(intentCamera);
//                                }

                                if(!CameraActivity.isPhoneExitTakephoto){  // 手机端主动退出
//                                    Intent intentCamera = new Intent(BTNotificationApplication.getInstance(), CameraActivity.class);     // todo  --- 打开 拍照
//                                    startActivity(intentCamera);

                                    Intent intentCamera = new Intent(getApplicationContext(), CameraActivity.class);     // todo  --- 打开 拍照              BTNotificationApplication.getInstance()    getApplicationContext()  sContext   getBaseContext
                                    intentCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intentCamera);
                                }else{
                                    CameraActivity.isPhoneExitTakephoto = false;
                                }


                            } else {
                                Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
                                sendBroadcast(intent);
                            }
                            break;

                        case BleContants.CRAMER_TAKE: // 拍照
//                            Intent intent2 = new Intent("0x47");   // 发送远程拍照 --- 对应的广播
//                            sendBroadcast(intent2);

                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                                sendBroadcast(new Intent(ACTION_REMOTE_CAMERA));
                            }
                            else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1"))
                            {
                                boolean b = (boolean) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true);
                                if(b)
                                {
                                    Intent intent2 = new Intent("0x47");   // 发送远程拍照 --- 对应的广播
                                    sendBroadcast(intent2);
                                }
                            }
                            else {
                                Intent intent2 = new Intent("0x47");   // 发送远程拍照 --- 对应的广播
                                sendBroadcast(intent2);
                            }


                            break;

                        case BleContants.CRAMER_CLOSE: // 退出拍照
//                            Intent intent3 = new Intent("0x48");   // 发送远程拍照 --- 对应的广播
//                            sendBroadcast(intent3);
//                            isDeviceSendExitCommand = true;

//                            sendBroadcast(new Intent(ACTION_REMOTE_CAMERA_EXIT));

                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                                sendBroadcast(new Intent(ACTION_REMOTE_CAMERA_EXIT));
                            }else {
                                Intent intent3 = new Intent("0x48");   // 发送远程拍照 --- 对应的广播
                                sendBroadcast(intent3);
                                isDeviceSendExitCommand = true;
                            }
                            break;

                        case BleContants.KEY_WIFI_PASSWORD:  // wifi 需要密码  ---- 还未用到

                            break;

                        case BleContants.KEY_WIFI_LINK:  // 已保存的 wifi
                            break;

                        case BleContants.KEY_WIFI_NOPASSWORD:  //  wifi 无需密码

                            break;

                        case BleContants.KEY_WIFI_LIST:  // -- 手表WiFi     // BleContants.SYN_WIFI TODO --- 手表返回错误 （0x19 --- 25）   KEY_WIFI_LIST  ---- 手表端返回WiFi列表
                            String resbytes = new String(bytes);
                            Log.e(TAG, "bytes.toString----" + resbytes);

                            byte[] newByte = new byte[bytes.length - 5];
                            System.arraycopy(bytes, 5, newByte, 0, bytes.length - 5);
                            String resnewbytes = new String(newByte);
                            Log.e(TAG, "newbytes.toString----" + resnewbytes);

                            Intent intent = new Intent();
                            intent.setAction(MainService.ACTION_WIFIINFO);  // TODO --- 收到 WiFi 数据后 发广播 更新 WiFi 列表
                            intent.putExtra("wifidata", resnewbytes);
                            sendBroadcast(intent);

                            break;

                        case BleContants.DIAL_RETURN: // 发送绑定设备后，手表返回  表盘数据返回

                            byte[] newByte2 = new byte[bytes.length - 5];   // 去掉前面5byte 第6byte开始为 手表实际返回数据
                            System.arraycopy(bytes, 5, newByte2, 0, bytes.length - 5);
                            String resnewbytes2 = new String(newByte2);
                            Log.e(TAG, "newbytes.toString----" + resnewbytes2);

                            String ss = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL);
                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL).equals(resnewbytes2)) {
                                return;
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                Log.e("readMessagereadcontant", resnewbytes2);
                                Intent intentNew = new Intent();
                                intentNew.setAction(MainService.ACTION_CLOCK_SKIN_MODEL_CHANGE);

                                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.CLOCK_SKIN_MODEL, resnewbytes2);
                                // intent.putExtra("type", readMessagereadcontant);
                                sendBroadcast(intentNew);
                            }
                            break;
                        case (byte)0x50:
                            byte[] bytes1 = new byte[bytes.length - 5];
                            System.arraycopy(bytes,5,bytes1,0,bytes.length - 5);
                            try {
                                String watchPushData = new String(bytes1,"utf-8");
                                Log.e(TAG,"watchPushData = " + watchPushData);
                                SharedPreUtil.setParam(sContext,SharedPreUtil.USER,SharedPreUtil.CLOCK_SKIN_MODEL,watchPushData);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        case BleContants.KEY_INPUTASSIT_SEND:        //协助输入
                            EventBus.getDefault().post("assistInput_success");
                            break;
                    }
                    if (bytes[2] == BleContants.ELECTRIC_RETURN) {
                        int battary = bytes[5];
                        Log.i(TAG, "设备电量： " + battary);
                        int battaryType = bytes[6];
                        Log.i(TAG, "设备电量状态: " + battaryType);
                    }
                } else if (byte1 == BleContants.FIND_COMMAND) {                        //TODO -- 查找命令   0x05
//                    if (bytes[2] == BleContants.FIND_PHONE) {   // -------  key
//                        showDialog();        //todo ----     多包找手机
//                    }
                    if (bytes[2] == BleContants.FIND_DEVICE) { // 找手表  ---- 手表端回复命令
                        Intent intent = new Intent();
                        intent.setAction(MainService.ACTION_GETWATCH);   // 发广播，销毁加载框
                        sendBroadcast(intent);
                    }
                } else if (byte1 == BleContants.REMIND_COMMAND) {                      //TODO -- 提醒命令   0x06

                } else if (byte1 == BleContants.RUN_MODE_COMMADN) {                     //TODO -- 运动模式命令   0x07   多包
                    if(bytes[2] == BleContants.RUN_BASE_RETURN){
                        GpsPointDetailData gpsPointDetailData = new GpsPointDetailData();
                        String year = String.format(Locale.ENGLISH,"20" + "%02d", bytes[5]);
                        String month = String.format(Locale.ENGLISH,"%02d", bytes[6]);
                        String day = String.format(Locale.ENGLISH,"%02d", bytes[7]);
                        String hour = String.format(Locale.ENGLISH,"%02d", bytes[8]);
                        String minute = String.format(Locale.ENGLISH,"%02d", bytes[9]);
                        String second = String.format(Locale.ENGLISH,"%02d", bytes[10]);
                        int mode = bytes[11];
                        int time = Utils.getInt(bytes,12);
                        float distance = Utils.byte2float(bytes,16);
                        int step = Utils.getInt(bytes,20);
                        float calorie = Utils.byte2float(bytes,24);
                        int pauseTime = (((bytes[28] << 8) & 0xff00) | (bytes[29] & 0xff));
                        int pauseNumber = (((bytes[30] << 8) & 0xff00) | (bytes[31] & 0xff));
                        int maxWidth = bytes[32] & 0xff;
                        int avgWidth = bytes[33] & 0xff;
                        int minWidth = bytes[34] & 0xff;
                        int heartNumber = (((bytes[35] << 8) & 0xff00) | (bytes[36] & 0xff));
                        gpsPointDetailData.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                        gpsPointDetailData.setMid(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MID));
                        gpsPointDetailData.setCalorie(calorie + "");
                        gpsPointDetailData.setSpeed("0");
                        gpsPointDetailData.setAve_step_width(avgWidth + "");
                        gpsPointDetailData.setMax_step_width(maxWidth + "");
                        gpsPointDetailData.setMin_step_width(minWidth + "");
                        gpsPointDetailData.setSportType(mode + "");
                        gpsPointDetailData.setMile(distance);
                        gpsPointDetailData.setDate(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                        gpsPointDetailData.setDeviceType("2");
                        gpsPointDetailData.setPauseNumber(pauseNumber + "");
                        gpsPointDetailData.setPauseTime(pauseTime + "");
                        gpsPointDetailData.setSportTime(String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", time / 60 / 60, time / 60 % 60, time % 60));
                        gpsPointDetailData.setAltitude("0");
                        gpsPointDetailData.setmCurrentSpeed("0");
                        gpsPointDetailData.setHeartRate("0");
                        gpsPointDetailData.setStep(step + "");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        try {
                            gpsPointDetailData.setsTime(time + "");
                            gpsPointDetailData.setTimeMillis(simpleDateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second).getTime() /1000 + "");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(heartNumber > 0) {
                            String heartBuffer = "";
                            String frequencyBuffer = "";
                            String speedBuffer = "";
                            for (int i = 0; i < heartNumber; i++) {
                                int heart = bytes[i * 7 + 37] & 0xFF;
                                float frequency = (((bytes[(i * 7) + 1 + 37] << 8) & 0xff00) | (bytes[(i * 7) + 2 + 37] & 0xff));
                                float speed = Utils.byte2float(bytes,(i * 7) + 3 + 37);
                                Log.e(TAG,"heart = " + heart + " ; frequency = " + frequency + " ; speed = " + speed);
                                if(heart > 200 || frequency < 0 || heart < 0 || speed < 0){
                                    continue;
                                }
                                heartBuffer += (heart + "&");
                                frequencyBuffer += (frequency + "&");
                                speedBuffer += (speed + "&");
                        }
                            if(TextUtils.isEmpty(heartBuffer)){
                                gpsPointDetailData.setArrheartRate("0");
                            }else{
                                gpsPointDetailData.setArrheartRate(heartBuffer);
                            }
                            if(TextUtils.isEmpty(frequencyBuffer)){
                                gpsPointDetailData.setArrcadence("0");
                            }else{
                                gpsPointDetailData.setArrcadence(frequencyBuffer);
                            }
                            if(TextUtils.isEmpty(speedBuffer)){
                                gpsPointDetailData.setArrspeed("0");
                            }else{
                                gpsPointDetailData.setArrspeed(speedBuffer);
                            }
                        }else{
                            gpsPointDetailData.setArrheartRate("0");
                            gpsPointDetailData.setArrcadence("0");
                            gpsPointDetailData.setArrspeed("0");
                        }
                        int paceNumber = (((bytes[heartNumber * 7 + 37] << 8) & 0xff00) | (bytes[heartNumber * 7 + 38] & 0xff));
                        if(paceNumber > 0){
                            String paceBuffer = "";
                            for (int i = 0; i < paceNumber; i++) {
                                int pace = (((bytes[(i * 2) + heartNumber * 7 + 39] << 8) & 0xff00) | (bytes[(i * 2) + heartNumber * 7 + 1 + 39] & 0xff));
                                if(pace > 0) {
                                    paceBuffer += (String.format(Locale.ENGLISH,"%1$02d'%2$02d''", pace / 60, pace % 60) + "&");
                                }
                                Log.e(TAG,"paceBuffer = " + paceBuffer);
                            }
                            if(TextUtils.isEmpty(paceBuffer)){
                                gpsPointDetailData.setArrTotalSpeed("0");
                            }else{
                                gpsPointDetailData.setArrTotalSpeed(paceBuffer);
                            }
                        }else{
                            gpsPointDetailData.setArrTotalSpeed("0");
                        }
                        int latlngNumber = (((bytes[paceNumber * 2 + heartNumber * 7 + 39] << 8) & 0xff00) | (bytes[paceNumber * 2 + heartNumber * 7 + 40] & 0xff));
                        if(latlngNumber > 0){
                            String latBuffer = "";
                            String lngBuffer = "";
                            String altitudeBuffer = "";
                            for (int i = 0; i < latlngNumber; i++) {
                                double lng = (double)Utils.getInt(bytes,i * 10 + paceNumber * 2 + heartNumber * 7 + 41) / 1000000;
                                double lat = (double)Utils.getInt(bytes,i * 10 + paceNumber * 2 + heartNumber * 7 + 4 + 41) / 1000000;
                                float altitude = ((bytes[i * 10 + paceNumber * 2 + heartNumber * 7 + 4 + 4 + 41] << 8) & 0xff00) | (bytes[i * 10 + 4 + 4 + 1 + 41] & 0xff);
                                Log.e(TAG,"lng = " + lng + " ; lat = " + lat + " ; altitude = " + altitude);
                                if(lng == -1  && lat == -1){
                                    continue;
                                }
                                lngBuffer += (lng + "&");
                                latBuffer += (lat + "&");
                                altitudeBuffer += (altitude + "&");
                            }
                            if(TextUtils.isEmpty(latBuffer)){
                                gpsPointDetailData.setArrLat("0");
                            }else{
                                gpsPointDetailData.setArrLat(latBuffer);
                            }
                            if(TextUtils.isEmpty(lngBuffer)){
                                gpsPointDetailData.setArrLng("0");
                            }else{
                                gpsPointDetailData.setArrLng(lngBuffer);
                            }
                            if(TextUtils.isEmpty(altitudeBuffer)){
                                gpsPointDetailData.setArraltitude("0");
                            }else{
                                gpsPointDetailData.setArraltitude(altitudeBuffer);
                            }
                        }else{
                            gpsPointDetailData.setArrLat("0");
                            gpsPointDetailData.setArrLng("0");
                            gpsPointDetailData.setArraltitude("0");
                        }
                        Log.e(TAG,"year = " + year + " ; month = " + month + " ; day = " + day + " ; hour = " + hour + " ; minute = " + minute + " ; second = " + second + " ; mode = " + mode + " ; time = " + time + " ; distance = " + distance + " ; step = " + step +
                                " ; calorie = " + calorie + " ; pauseTime = " + pauseTime + " ; pauseNumber = " + pauseNumber + " ; maxWidth = " + maxWidth + " ; avgWidth = " + avgWidth + " ; minWidth = " + minWidth);
                        gpsList.add(gpsPointDetailData);
                        saveSpoetData(gpsList);
                        gpsList.clear();
                    }

                } else if (byte1 == BleContants.SLEEP_COMMAND) {                        //TODO -- 睡眠命令     0x08

                } else if (byte1 == BleContants.HEART_COMMAND) {                           //TODO -- 心率命令   0x09

                } else if (byte1 == BleContants.SYN_COMMAND) {                           //TODO -- 同步命令      0x0A
                    if (bytes[2] == BleContants.BRACELET_RUN_DATA_RETURN) {               //TODO 手环运动数据返回  ---历史  (0xA3)
                        // 0A00A3006411081D00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004B4000004B400000AC000000B2000000CEA000014180000197500001F1400001F48
                        // 0A00A30000
                        if(!BTNotificationApplication.isSyncEnd) {
                            if(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                                needReceDataNumber = 1;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "1");
                                sContext.sendBroadcast(intent);
                            }else{        //(手环)
                                getSyncDataNumInService++;  //todo ---- getSyncDataNumInService 值对应设备端有几天的数据

                                Log.e("liuxiaodata", "需要收到的数据条目为----" + BTNotificationApplication.needReceiveNum);
                                if (BTNotificationApplication.needReceiveNum == 21) {  // 1/5,2/5
                                    if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 5) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 13) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 16) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 20) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送5广播");
                                    }

                                } else if (BTNotificationApplication.needReceiveNum == 6) {
                                    if (getSyncDataNumInService == 1) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 3) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 4) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 5) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送5广播");
                                    }
                                }//////////////////////////////////////////////////////////////////

                                Log.e("liuxiaodata", "已收到的数据条数为--" + getSyncDataNumInService);
                            }
                        }

                        int l2ValueLength = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));
                        if (l2ValueLength == 0) {
                            return;
                        }

                        if (l2ValueLength == 100) {   //旧协议
//                            if(Locale.getDefault().getLanguage().equalsIgnoreCase("ar")){ //todo ---  阿拉伯语
//                                String ttd = String.format(Locale.ENGLISH,"20" + "%02d", bytes[5] & 0xff);  // 2018
//                                String tt = String.format(Locale.getDefault(),"20" + "%02d", bytes[5] & 0xff);  // 20١٨
//                                String year2 = String.format("20" + "%02d", bytes[5] & 0xff);   // 20١٨
//                                String ddd  = "554";
//                            }else {
//                                String year = String.format("20" + "%02d", bytes[5] & 0xff);   // 2018
//                                String dd  = "554";
//                            }

                            String year = String.format(Locale.ENGLISH,"20" + "%02d", bytes[5] & 0xff);
                            Log.i(TAG, "run year =" + year);
                            String mouth = String.format(Locale.ENGLISH,"%02d", bytes[6] & 0xff);
                            Log.i(TAG, "run mouth =" + mouth);
                            String day = String.format(Locale.ENGLISH,"%02d", bytes[7] & 0xff);
                            Log.i(TAG, "run day =" + day);
                            String hour = String.format(Locale.ENGLISH,"%02d", bytes[8] & 0xff);
                            Log.i(TAG, "run hour =" + hour);
                            int[] oneDayStep = new int[24];   //对应1-24点之间的时间段 步数增量值
                            int j = 0;
                            for (int i = 9; i < 105; i++) {   //
                                int pp = i % 4;
                                if (pp == 0) { // 16 --- 20   //
                                    byte[] bb = new byte[4];
                                    bb[3] = bytes[i];   //
                                    bb[2] = bytes[i - 1];
                                    bb[1] = bytes[i - 2];  //
                                    bb[0] = bytes[i - 3];
                                    int mindex = ++j;
                                    int dddd = NumberBytes.byteArrayToInt(bb);
                                    oneDayStep[mindex - 1] = NumberBytes.byteArrayToInt(bb);
                                }
                            }

//                        int runCount =  oneDayStep[23]; // 步数  --- 一天的总步数值
                            for (int p = 0; p < 24; p++) {
                                Log.i(TAG, "oneDayStep--" + p + "----" + oneDayStep[p]);
                            }

                            List<StepData> dataList = new ArrayList<>();

                            int hasStepTemp = 0;
                            for (int i = 0; i < oneDayStep.length; i++) {
                                int goalSum = oneDayStep[i];
                                int realStep = 0;  // 时间段实际步数值
                                StepData stepData = new StepData();
                                String mcurTime = year + "-" + mouth + "-" + day + " " + String.format(Locale.ENGLISH,"%02d", i) + ":" + "00:00";  // 20١٨-٠٧-٢٨ ٠٠:00:00
                                Date date = StringUtils.parseStrToDate(mcurTime, StringUtils.SIMPLE_DATE_FORMAT);
                                if (date != null) {
                                    stepData.setTime(date.getTime() / 1000 + "");
                                    if (hasStepTemp == 0 || SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //智能表传入的是增量数据
                                        stepData.setCounts(goalSum + "");
                                        realStep = goalSum;
                                    } else {

                                        int zhengliang = goalSum - hasStepTemp;
                                        if (zhengliang < 0) {
                                            zhengliang = 0;
                                            stepData.setCounts(zhengliang + "");
                                            realStep = zhengliang;
                                        } else {
                                            stepData.setCounts(zhengliang + "");
                                            realStep = zhengliang;
                                        }
                                    }
                                    Log.i(TAG, "oneDayStep--实际步数增量 ---- " + realStep);

                                    if (hasStepTemp != goalSum && goalSum > hasStepTemp) {   // hasStepTemp != goalSum
                                        hasStepTemp = goalSum;
                                    }

                                    int userWeightI = 60;
                                    String userWeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WEIGHT, "60");
                                    if (StringUtils.isEmpty(userWeight) || userWeight.equals("0")) {
                                        userWeightI = 60;
                                    } else {
                                        userWeightI = Integer.valueOf(userWeight);
                                    }

                                    int userHeightI = 170;
                                    String userHeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.HEIGHT, "170");
                                    if (StringUtils.isEmpty(userHeight) || userHeight.equals("0")) {
                                        userHeightI = 170;
                                    } else {
                                        userHeightI = Integer.valueOf(userHeight);
                                    }

                                    double calorie = 0.00;
                                    if (realStep > 0) {
                                        calorie = Double.valueOf(String.format(Locale.ENGLISH, "%1$.3f", realStep * (((float) (userWeightI) - 15) * 0.000693 + 0.005895))); //TODO  BLE手环给的计算公式
                                        //calorie = Double.valueOf(String.format(Locale.ENGLISH,"%1$.2f", ((float)(userWeightI) * (float) ((realStep * 0.7) / 1000.0) * 1.036)));  // 卡路里     0,00    Locale.ENGLISH
                                    }
                                    String distance = String.format(Locale.ENGLISH, "%.3f", (realStep * (0.415 * (float) userHeightI) / 100000));  //TODO BLE手环给的计算公式
                                    //String distance = String.format(Locale.ENGLISH,"%.2f", (realStep * 0.7) / 1000.0);     // 运动距离
//                                String distance = String.format("%.2f", (realStep * 0.7) / 1000.0);     // 运动距离
                                    stepData.setCalorie(calorie + "");
                                    stepData.setDistance(distance + "");
                                    dataList.add(stepData);
                                }
                                Log.e(TAG, "goalSum = " + goalSum + " ; hasStepTemp = " + hasStepTemp + " ; time = " + mcurTime + " ;  binTime = " + stepData.getTime() + " ; goal = " + realStep);
                                Log.i(TAG, "run goal = " + realStep + "  time = " + i);
                            }

                            BTdataWrite(dataList);
                        }else {      //新协议(388字节)
                            String year = String.format(Locale.ENGLISH,"20" + "%02d", bytes[5] & 0xff);
                            Log.i(TAG, "run year =" + year);
                            String mouth = String.format(Locale.ENGLISH,"%02d", bytes[6] & 0xff);
                            Log.i(TAG, "run mouth =" + mouth);
                            String day = String.format(Locale.ENGLISH,"%02d", bytes[7] & 0xff);
                            Log.i(TAG, "run day =" + day);
                            String hour = String.format(Locale.ENGLISH,"%02d", bytes[8] & 0xff);
                            Log.i(TAG, "run hour =" + hour);
                            List<StepData> dataList = new ArrayList<>();
                            int j = 0;
                            for (int i = 9; i < bytes.length; i+=16) {
                                int dayStep = Utils.getInt(bytes,i);
                                int runStep = Utils.getInt(bytes,i+4);
                                float calorie = Utils.byte2float(bytes,i+8);
                                float distance = Utils.byte2float(bytes,i+12);
                                Log.i(TAG,"oneDayStep--实际步数增量 ---- " + dayStep + "; oneRunStep--实际跑步增量 ---- " + runStep + "; calorie = " + calorie + "; distance = " + distance);
                                StepData stepData = new StepData();
                                String mcurTime = year + "-" + mouth + "-" + day + " " + String.format(Locale.ENGLISH,"%02d", j) + ":" + "00:00";
                                Date date = StringUtils.parseStrToDate(mcurTime, StringUtils.SIMPLE_DATE_FORMAT);
                                if (date != null) {
                                    stepData.setTime(date.getTime() / 1000 + "");
                                    if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //智能表传入的是增量数据
                                        stepData.setCounts(dayStep + "");
                                        stepData.setCalorie(calorie + "");
                                        stepData.setDistance(distance + "");
                                    } else {
                                        stepData.setCounts(dayStep - hasStepTemp + "");
                                        stepData.setDistance(calorie - hasCalorieTemp + "");
                                        stepData.setCalorie(distance - hasDistanceTemp + "");
                                    }
                                    Log.i(TAG, "oneDayStep--实际步数增量 ---- " + stepData.getCounts() + "; oneDayCalorie--实际卡路里增量 ---- " + stepData.getCalorie() + "; oneDayDistance--实际距离增量 ---- " + stepData.getDistance());

                                    if (hasStepTemp != dayStep && dayStep > hasStepTemp) {   // hasStepTemp != goalSum
                                        hasStepTemp = dayStep;
                                        hasCalorieTemp = calorie;
                                        hasDistanceTemp = distance;
                                    }

                                    dataList.add(stepData);
                                    j++;
                                }
                                /*Log.e(TAG, "goalSum = " + goalSum + " ; hasStepTemp = " + hasStepTemp + " ; time = " + mcurTime + " ;  binTime = " + stepData.getTime() + " ; goal = " + realStep);
                                Log.i(TAG, "run goal = " + realStep + "  time = " + i);*/
                            }
                            hasStepTemp = 0;
                            hasRunTemp = 0;
                            hasDistanceTemp = 0;
                            hasCalorieTemp = 0;
                            BTdataWrite(dataList);
                        }
                    } else if (bytes[2] == BleContants.BRACELET_SLEEP_DATA_RETURN) {             //TODO 手环睡眠数据返回  ---历史  (0xA2)
//                        String resModebyteslx2 = UtilsLX.bytesToHexString(bytes);   // 0A00A2000C110610011600021700000200
                        //         3     5       8     11
                        // 0A00 A2 00 4B 110617 011637 02172101173402173901001400002D01003800010901011A02012401020900020E01022400031201031602032401033102041801042602043701050802050C01051700062F    23号下午到
                        // 0A00A2003C11061901173A02003A01010902010E01012A02013401021202021801022A02032501033302042C010438020521010530020629010639020702000713

                        if(!BTNotificationApplication.isSyncEnd) {
                            if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                                if(needReceDataNumber == 1){
                                    needReceDataNumber = 2;
                                    Intent intent = new Intent();
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "2");
                                    sContext.sendBroadcast(intent);
                                }
                                if(BTNotificationApplication.needSendDataType == needReceDataNumber){
                                    receiveCountDownTimer.cancel();
                                    receiveCountDownTimer.start();
                                }


                            } else {
                                getSyncDataNumInService++;

                                Log.e("liuxiaodata", "需要收到的数据条目为----" + BTNotificationApplication.needReceiveNum);
                                if (BTNotificationApplication.needReceiveNum == 21) {  // 1/5,2/5
                                    if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 5) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 13) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 16) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 20) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送5广播");
                                    }

                                } else if (BTNotificationApplication.needReceiveNum == 6) {
                                    if (getSyncDataNumInService == 1) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 3) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 4) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 5) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送5广播");
                                    }
                                }//////////////////////////////////////////////////////////////////

                                Log.e("liuxiaodata", "已收到的数据条数为--" + getSyncDataNumInService);
                            }
                        }

                        byte[] sleep = new byte[3];
                        byte[] sleepNext = new byte[3];
                        int l2Length = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));   // 75    --- 60
                        String year = String.format(Locale.ENGLISH,"20" + "%02d", (bytes[5] & 0xff));   // 第5位 年
                        Log.e(TAG, "sleep year =" + year);
                        String month = String.format(Locale.ENGLISH,"%02d", bytes[6] & 0xff);
                        Log.e(TAG, "sleep mouth =" + month);
                        String day = String.format(Locale.ENGLISH,"%02d", bytes[7] & 0xff);
                        Log.e(TAG, "sleep day =" + day);

                        String sleepBeginDay = year + "-" + month + "-" + day;
                        String sleepEndDay = "";

                        int sleepLength = (l2Length - 3) / 3;   // 所有的睡眠数据的组数    ---- 17  应该为 19组
                        List<SleepData> sleepList = new ArrayList<>();
                        boolean hasOtherDay = false;  // 是否跨天
                        for (int i = 0; i < sleepLength; i++) {  // 3
                            System.arraycopy(bytes, (i * 3) + 8, sleep, 0, 3);
                            if ((i + 1) == sleepLength) {
                                break;
                            }
                            System.arraycopy(bytes, ((i + 1) * 3) + 8, sleepNext, 0, 3);
                            int sleepBeginMode = sleep[0];  // 睡眠模式
                            int sleepBeginHour = sleep[1];  // 睡眠的开始 小时  10进制
                            int sleepBeginMinute = sleep[2]; // 睡眠的开始 分钟
                            Log.e(TAG, "sleepBeginMode =" + sleepBeginMode);
                            Log.e(TAG, "sleepBeginHour = " + sleepBeginHour);
                            Log.e(TAG, "sleepBeginMinute = " + sleepBeginMinute);
                            int sleepEngMode = sleepNext[0];  // 睡眠模式
                            int sleepEndHour = sleepNext[1];   // 睡眠的开始 小时   10进制
                            int sleepEndMinute = sleepNext[2];  // 睡眠的开始 分钟
                            Log.e(TAG, "sleepEngMode =" + sleepEngMode);
                            Log.e(TAG, "sleepEndHour = " + sleepEndHour);
                            Log.e(TAG, "sleepEndMinute = " + sleepEndMinute);
                            long deepSleepTime = 0;   //深睡
                            long lightSleepTime = 0;   //浅睡
                            long notSleepTime = 0; //未睡眠
                            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            DBHelper db = DBHelper.getInstance(sContext);
                            //         3     5       8     11
                            // 0A00 A2 00 4B 110617 011637 02172101173402173901001400002D01003800010901011A02012401020900020E01022400031201031602032401033102041801042602043701050802050C01051700062F
//                            01 1637    --- 小时分钟
                            String sleepBeginTime = String.format(Locale.ENGLISH,"%02d", sleepBeginHour) + ":" + String.format(Locale.ENGLISH,"%02d", sleepBeginMinute);   //开始时间   小时:分钟
                            String sleepEndTime = String.format(Locale.ENGLISH,"%02d", sleepEndHour) + ":" + String.format(Locale.ENGLISH,"%02d", sleepEndMinute);   //结束时间
                            Log.e(TAG, "sleepBeginTime = " + sleepBeginTime);
                            Log.e(TAG, "sleepEndTime = " + sleepEndTime);
                            SleepData sleepData = new SleepData();

                            if (hasOtherDay) {
                                sleepBeginDay = sleepEndDay;
                            } else {
                                if (sleepBeginHour > sleepEndHour) {
                                    hasOtherDay = true;
                                    Date date = new Date();
                                    try {
                                        Date newDay = new DateTime(format.parse(sleepBeginDay)).plusDays(1).toDate();  // 日期
                                        sleepEndDay = dfs.format(newDay).split(" ")[0];  // 结束的日期 加1
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else { //开始时间一直小于结束时间（没有跨天的睡眠数据）
                                    if (i == 0) { // 取当天睡眠数据的第一条
                                        if (sleepEndHour <= 12) {   // 全为第2天 0点以后的数据
                                            try {
                                                Date newDay = new DateTime(format.parse(sleepBeginDay)).plusDays(1).toDate();  // 日期
                                                sleepEndDay = dfs.format(newDay).split(" ")[0];  // 结束的日期 加1
                                                sleepBeginDay = sleepEndDay;
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else {  // 当天的24点之前有睡眠数据
                                            sleepEndDay = sleepBeginDay;
                                        }
                                    }
                                }
                            }

                            Log.e(TAG, "sleepEndDay = " + sleepEndDay);
                            Log.e(TAG, "sleepBeginDay = " + sleepBeginDay); //
                            try {
                                Date begin = dfs.parse(sleepBeginDay + " " + sleepBeginTime);   // "yyyy-MM-dd HH:mm
                                Date end = dfs.parse(sleepEndDay + " " + sleepEndTime);        // "yyyy-MM-dd HH:mm
                                long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒 (睡眠开始时间和结束时间之间的 总秒数)
                                if (sleepBeginMode == 0) {
                                    notSleepTime = between / 60;    // 0 : 未睡
                                    continue;
                                } else if (sleepBeginMode == 1) {   // 1: 浅睡 ：分钟数
                                    lightSleepTime = between / 60;
                                } else {                            // 2: 深睡 ：分钟数
                                    deepSleepTime = between / 60;
                                }
                                sleepData.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                                sleepData.setMid(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MID));
                                sleepData.setDeepsleep(deepSleepTime + "");   // 深睡时间  --- 分钟数
                                sleepData.setLightsleep(lightSleepTime + ""); // 浅睡时间  --- 分钟数
                                sleepData.setDate(sleepBeginDay);   // 睡眠的日期  -- yyyy-MM-dd
                                sleepData.setSleepmillisecond((deepSleepTime + lightSleepTime) * 60 * 1000 + "");  // 睡眠总时间的毫秒数
                                sleepData.setStarttimes(sleepBeginDay + " " + sleepBeginTime + ":00");
                                sleepData.setEndTime(sleepEndDay + " " + sleepEndTime + ":00");
                                sleepData.setAutosleep(deepSleepTime + lightSleepTime + ":00");
                                sleepData.setSleeptype(sleepBeginMode + "");
                                sleepList.add(sleepData);
                                Log.e(TAG, "sleepList----" + i + "------" + deepSleepTime + "---" + lightSleepTime + "---" + sleepBeginDay + " " + sleepBeginTime + ":00" + "---" + sleepEndDay + " " + sleepEndTime + ":00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        String tempDate = "";
                        StringBuffer sbData = new StringBuffer();

                        if (sleepList.size() > 0) {
                            for (int i = 0; i < sleepList.size(); i++) {
                                if (!sleepList.get(i).getDate().equals(tempDate)) {
                                    tempDate = sleepList.get(i).getDate();
                                    sbData.append(tempDate + "#");
                                }
                            }
                        }

                        String mData = sbData.toString();
                        if (!StringUtils.isEmpty(mData) && mData.contains("#")) {
                            String[] allData = mData.split("#");
                            if (allData.length == 2) {
                                Query query1 = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                                        .where(SleepDataDao.Properties.Date.eq(allData[0])).build();
                                List<SleepData> list1 = query1.list();

                                for (int j = 0; j < list1.size(); j++) {
                                    for (int i = 0; i < sleepList.size(); i++) {
                                        if (list1.get(j).getStarttimes().equals(sleepList.get(i).getStarttimes())) {  //todo --- 查询数据库中的睡眠开始时间，是否与设备传过来的睡眠开始是否相等，若相等，说明传过,删除，没穿过不删除
                                            db.deleteSleepData(list1.get(j).getId());
                                        }
                                    }
                                }

                                Query query2 = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                                        .where(SleepDataDao.Properties.Date.eq(allData[1])).build();
                                List<SleepData> list2 = query2.list();
                                for (int j = 0; j < list2.size(); j++) {
                                    for (int i = 0; i < sleepList.size(); i++) {
                                        if (list2.get(j).getStarttimes().equals(sleepList.get(i).getStarttimes())) {  //todo --- 查询数据库中的睡眠开始时间，是否与设备传过来的睡眠开始是否相等，若相等，说明传过,删除，没穿过不删除
                                            db.deleteSleepData(list2.get(j).getId());
                                        }
                                    }
                                }
                            } else {
                                Query query1 = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                                        .where(SleepDataDao.Properties.Date.eq(allData[0])).build();
                                List<SleepData> list1 = query1.list();
                                for (int j = 0; j < list1.size(); j++) {
                                    for (int i = 0; i < sleepList.size(); i++) {
                                        if (list1.get(j).getStarttimes().equals(sleepList.get(i).getStarttimes())) {  //todo --- 查询数据库中的睡眠开始时间，是否与设备传过来的睡眠开始是否相等，若相等，说明传过,删除，没穿过不删除
                                            db.deleteSleepData(list1.get(j).getId());
                                        }
                                    }
                                }
                            }
                        }
                        if(sleepList.size() > 0) {
                            SharedPreUtil.savePre(sContext, SharedPreUtil.SLEEP, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC), sleepList.get(sleepList.size() - 1).getEndTime());
                        }
                        for (int i = 0; i < sleepList.size(); i++) {
                            db.saveSleepData(sleepList.get(i));      // 重新保存对应日期的睡眠数据
                        }
                    } else if (bytes[2] == BleContants.BRACELET_HEART_DATA_RETURN) {            //TODO 手环心率数据返回  ----历史(0xA4)

                        if(!BTNotificationApplication.isSyncEnd) {
                            if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                                if (needReceDataNumber == 1) {
                                    needReceDataNumber = 2;
                                    Intent intent = new Intent();
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "2");
                                    sContext.sendBroadcast(intent);
                                }else if(needReceDataNumber == 2){
                                    needReceDataNumber = 3;
                                    Intent intent = new Intent();
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "3");
                                    sContext.sendBroadcast(intent);
                                }
                                if(BTNotificationApplication.needSendDataType == needReceDataNumber) {
                                    receiveCountDownTimer.cancel();
                                    receiveCountDownTimer.start();
                                }
                            } else {
                                getSyncDataNumInService++;

                                Log.e("liuxiaodata", "需要收到的数据条目为----" + BTNotificationApplication.needReceiveNum);
                                if (BTNotificationApplication.needReceiveNum == 21) {  // 1/5,2/5
                                    if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 5) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 13) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 16) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 20) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize4));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);

                                        Log.e("liuxiaodata", "发送5广播");
                                    }

                                } else if (BTNotificationApplication.needReceiveNum == 6) {
                                    if (getSyncDataNumInService == 1) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize1));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播     ACTION_SYNFINSH_STEP
                                        intent.putExtra("step", "1");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送1广播");
                                    } else if (getSyncDataNumInService == 2) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize2));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "2");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送2广播");
                                    } else if (getSyncDataNumInService == 3) {
//                                    HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize3));

                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "3");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送3广播");
                                    } else if (getSyncDataNumInService == 4) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "4");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送4广播");
                                    } else if (getSyncDataNumInService == 5) {
                                        Intent intent = new Intent();      // add 0414
                                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                        intent.putExtra("step", "5");
                                        sContext.sendBroadcast(intent);
                                        Log.e("liuxiaodata", "发送5广播");
                                    }
                                }

                                Log.e("liuxiaodata", "已收到的数据条数为--" + getSyncDataNumInService);
                                if (getSyncDataNumInService == BTNotificationApplication.needReceiveNum) {  //   BTNotificationApplication.bleSyncDataDays = 7;    HomeFragment.getHistoryDataDays
//                                Intent intent = new Intent();      // add 0414
//                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
//                                sContext.sendBroadcast(intent);

                                    Intent intent = new Intent();      // add 0414
                                    intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                    intent.putExtra("step", "6");
                                    sContext.sendBroadcast(intent);
                                    Log.e("liuxiaodata", "发送6广播");

//                                HelperFragment.loadingDialog.setText(getString(R.string.userdata_synchronize5));

                                    getSyncDataNumInService = 0;
                                    BTNotificationApplication.isSyncEnd = true;

                                    String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);

                                    String isFirstSync = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);
                                    if (StringUtils.isEmpty(isFirstSync) || isFirstSync.substring(0, 1).equals("0")) {      // TODO--- 没取过7天的数据了
                                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "1#" + curMacaddress);  // todo 同步完成了 --- 第一次同步 7 天的数据，取过7天的数据后 ，将 SYNCED 置1
                                    }
                                } else { //todo --- 没有达到相应的接收数据的条数
                                    mHandler.sendEmptyMessageDelayed(HEART_DATA_FAILOVER, 20000); //TODO --- 延时30秒发送，广播
                                }
                            }
                        }

                        List<HearData> hearDataList = new ArrayList<>();
                        byte[] heart = new byte[7]; // ????? 7    byte[] heart = new byte[10]
                        int l2Length = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));
                        int runCount = l2Length / 7;
                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.DEFAULT_HEART_RATE, "1");  // 显示心率页面的 标识

                        Query query = null;
                        List<HearData> list = null;
                        if(bytes.length >= 8) {
                            String year = String.format(Locale.ENGLISH,"20" + "%02d", bytes[5]);
                            String mouth = String.format(Locale.ENGLISH,"%02d", bytes[6]);
                            String day = String.format(Locale.ENGLISH,"%02d", bytes[7]);
                            String beginDate = year + "-" + mouth + "-" + day;
                            query = db.getHearDao().queryBuilder()
                                    .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                                    .where(HearDataDao.Properties.Date.eq(beginDate)).build();
                        }
                        if(null != query) {
                            list = query.list();
                        }
                        Log.e(TAG, "heart l2Length =" + l2Length);
                        for (int i = 0; i < runCount; i++) {
                            HearData hearData = new HearData();
                            System.arraycopy(bytes, (i * 7) + 5, heart, 0, 7);
                            String year = String.format(Locale.ENGLISH,"20" + "%02d", heart[0]);
                            Log.e(TAG, "heart year =" + year);
                            String mouth = String.format(Locale.ENGLISH,"%02d", heart[1]);
                            Log.e(TAG, "heart mouth =" + mouth);
                            String day = String.format(Locale.ENGLISH,"%02d", heart[2]);
                            Log.e(TAG, "heart day =" + day);
                            String hour = String.format(Locale.ENGLISH,"%02d", heart[3]);
                            Log.e(TAG, "heart hour =" + hour);
                            String minute = String.format(Locale.ENGLISH,"%02d", heart[4]);
                            Log.e(TAG, "heart minute =" + minute);
                            String second = String.format(Locale.ENGLISH,"%02d", heart[5]);
                            Log.e(TAG, "heart second =" + second);
                            int hearts = heart[6] & 0xff;
                            Log.e(TAG, "heart hearts =" + hearts);
                            if(hearts <= 0){
                                continue;
                            }
                            String beginTime = year + "-" + mouth + "-" + day + " " + hour + ":" + minute + ":" + second;
                            Date date = null;
                            if(StringUtils.isEmpty(beginTime)){
                                continue;
                            }
                            boolean isFlag = false;  //判断是否有相同数据
                            try {
                                date = new SimpleDateFormat(Utils.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH).parse(beginTime);
                                if (null == date) {
                                    break;
                                }
                                if(heartAllList.size() > 0) {
                                    for (int j = 0; j < heartAllList.size(); j++) {
                                        if((date.getTime()/1000 + "").equals(heartAllList.get(j).getBinTime())){
                                            isFlag = true;
                                            break;
                                        }
                                    }
                                }else if(null != list && list.size() > 0){
                                    for (int j = 0; j < list.size(); j++) {
                                        if((date.getTime()/1000 + "").equals(list.get(j).getTimes())){
                                            isFlag = true;
                                            break;
                                        }
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(!isFlag) {
                                if(null == date){
                                    try {
                                        date = new SimpleDateFormat(Utils.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH).parse(beginTime);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (null == date) {
                                    break;
                                }
                                hearData.setBinTime(date.getTime() / 1000 + "");    // 1502680129
                                hearData.setHeartbeat(hearts + "");      // 107
                                hearData.setHigt_hata(hearts + "");
                                hearData.setLow_hata(hearts + "");
                                hearData.setAvg_hata(hearts + "");//平均的心率
                                hearDataList.add(hearData);
                            }
                        }
                        Log.e("UPDTA", "3");
                        heartAllList.addAll(hearDataList);
                        heartdataWrite(hearDataList,false);
                    } else if (bytes[2] == BleContants.BRACELREALRUN) {                      //TODO 手环实时计步数据返回 (0xAC)
                        int run = Utils.getInt(bytes, 5);
                        Log.e(TAG, "bracel run =" + run);
                        float calorie = Utils.getFloat(bytes, 9);  // 16.079199
                        Log.e(TAG, "bracel calorie =" + calorie);
                        float distance = Utils.getFloat(bytes, 13);  // 0.27472
                        Log.e(TAG, "bracel distance =" + distance);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);//设置日期格式
                        SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN, run + "");
                        SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE, calorie + "");
                        SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE, distance + "");

//                        String dd =  String.format(Locale.ENGLISH, df.format(new Date()) + "");
                        SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME,  String.format(Locale.ENGLISH,  df.format(new Date()) + ""));
//                        String distance = String.format(Locale.ENGLISH, "%.2f", (allStep * 0.7) / 1000.0);   String.format(Locale.ENGLISH, "%1$02d-%2$02d-%3$02d", mai, sec, yunshu) df.format(new Date()) + "");
//                        SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME, df.format(new Date()) + "");
                        //todo ---- 添加判断 当 run 步数为0 时， 说明是 0点清0数据 或 固件升级了 --- 应该将同步历史的步数也清0
                        if(run == 0){
                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN, "0");
                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE,  "0");
                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE, "0");
//                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME, df.format(new Date()) + "");  //存手环实时时间

                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN, "0");
                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE,  "0");
                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE, "0");
                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDATASIZE,  "0");
//                            SharedPreUtil.savePre(sContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHSYNCTIME, df.format(new Date()) + "");  //存手环同步时间
                        }

                        if(BTNotificationApplication.isSyncEnd) {  //todo --- 同步数据完成才发送 同步实时计步的广播
                            Intent intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH_SUCCESS);    //todo ----BLE 实时步数的广播 发数据同步成功的广播       ACTION_SYNFINSH
                            sendBroadcast(intent);
                        }

//                        Intent intent = new Intent();
//                        intent.setAction(MainService.ACTION_SYNFINSH_SUCCESS);    //todo ----BLE 实时步数的广播 发数据同步成功的广播       ACTION_SYNFINSH
//                        sendBroadcast(intent);
                    } else if (bytes[2] == BleContants.BRACELREALHEART) {
                        //TODO 手环实时心率数据返回 (0xAB)

                        int heart = bytes[5] & 0xff;
                        Log.e(TAG,"实时心率 ： " + heart);
                        if(heart > 0){
                            SendHate(heart);
                        }
                        //判断当前手环类型 cf006
                        /*if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MACNAME).contains("006")){
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            String str = new SimpleDateFormat("HH:mm").format(curDate);
                            if(issavexinlv==false){
                                issavexinlv=true;
                                if(null==xinlvgmatime){xinlvgmatime= str;SendHate(heart);}
                            }else{
                                if(null!=xinlvgmatime&&!xinlvgmatime.equals(str)){xinlvgmatime= str;SendHate(heart);}
                            }
                        }else{
                            SendHate(heart);
                        }*/


                    } else if (bytes[2] == BleContants.BRACELREALSPORT) {                         //TODO 手环运动模式数据返回 (0xA5)
                        int l2ValueLength = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));
                        if (l2ValueLength == 0) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // todo  --- 当没有运动模式数据时，发广播，销毁加载的同步框
                            getApplicationContext().sendBroadcast(broadcastIntent);
                            return;
                        }
                        if(l2ValueLength % 16 == 0) {
                            if(!isGPS) {
                                byte[] sports = new byte[16];   //运动模式数组
                                int sportCount = (l2ValueLength) / 16;   //运动模式数据组个数
                                List<GpsPointDetailData> listGps = new ArrayList<>();
                                for (int i = 0; i < sportCount; i++) {
                                    GpsPointDetailData gpsPointDetailData = new GpsPointDetailData();
                                    System.arraycopy(bytes, (i * 16) + 5, sports, 0, 16);
                                    String year = String.format(Locale.ENGLISH,"20" + "%02d", sports[0]);
                                    Log.e(TAG, "sport year = " + year);
                                    String month = String.format(Locale.ENGLISH,"%02d", sports[1]);
                                    Log.e(TAG, "sport month = " + month);
                                    String day = String.format(Locale.ENGLISH,"%02d", sports[2]);
                                    Log.e(TAG, "sport day = " + day);
                                    String startHour = String.format(Locale.ENGLISH,"%02d", sports[3]);
                                    Log.e(TAG, "sport startHour = " + startHour);
                                    String startMin = String.format(Locale.ENGLISH,"%02d", sports[4]);
                                    Log.e(TAG, "sport startMin = " + startMin);
                                    String endHour = String.format(Locale.ENGLISH,"%02d", sports[5]);
                                    Log.e(TAG, "sport endHour = " + endHour);
                                    String endMin = String.format(Locale.ENGLISH,"%02d", sports[6]);
                                    Log.e(TAG, "sport endMin = " + endMin);
                                    int sportType = sports[7];
                                    Log.e(TAG, "sport sportType = " + sportType);
                                    int step = Utils.getInt(sports, 8);
                                    Log.e(TAG, "sport step = " + step);
                                    float calories = Utils.byte2float(sports, 12);
                                    float calorie = Math.round(calories * 100) / 100;
                                    Log.e(TAG, "sport calorie = " + calorie);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH);
                                    SimpleDateFormat sportSimpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

                                    String startTime = "";
                                    String endTime = "";
                                    int sHourI = Integer.valueOf(startHour);
                                    int eHourI = Integer.valueOf(endHour);

                                    if (eHourI < sHourI) {
                                        startTime = year + "-" + month + "-" + day + " " + startHour + ":" + startMin + ":00";  // 2017-07-28 21:04   --- 2017-07-28 21:04:00   -----   2017-11-08 20:04:00
//                                endTime = year + "-" + month + "-" + day + " " + endHour + ":" + endMin + ":00";
                                        try {
                                            Date startDate = simpleDateFormat.parse(startTime);
                                            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
                                            calendar.setTime(startDate);
//                                    String  mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-11-08
                                            calendar.add(Calendar.DAY_OF_MONTH, 1);  //设置为后1天
                                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                            String lastOneDay = sdf2.format(calendar.getTime());//todo 后一天的日期  --- 2017-11-09       2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                                            Log.e(TAG, "当前日期的后1天日期为 ---- " + lastOneDay);

                                            endTime = lastOneDay + " " + endHour + ":" + endMin + ":00";    // 2017-11-09 09:26:00
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        startTime = year + "-" + month + "-" + day + " " + startHour + ":" + startMin + ":00";  // 2017-07-28 21:04   --- 2017-07-28 21:04:00
                                        endTime = year + "-" + month + "-" + day + " " + endHour + ":" + endMin + ":00";
                                    }
//                            String startTime = year + "-" + month + "-" + day + " " + startHour + ":" + startMin + ":00";  // 2017-07-28 21:04   --- 2017-07-28 21:04:00
//                            String endTime = year + "-" + month + "-" + day + " " + endHour + ":" + endMin + ":00";      // 2017-07-28 08:46:00
                                    try {
                                        Date startDate = simpleDateFormat.parse(startTime);
                                        Date endDate = simpleDateFormat.parse(endTime);
                                        long sportTime = 0;
                                        /*if (endDate.getTime() > startDate.getTime()) { //TODO --- 运动模式数据跨天时，日期是前一天的日期
                                            sportTime = endDate.getTime() - startDate.getTime();  //todo --- 秒数
                                        } else {
                                            sportTime = startDate.getTime() - endDate.getTime();  //todo --- 秒数
                                        }*/
                                        sportTime = Math.abs(endDate.getTime() - startDate.getTime());


                                        long sportMiaos = sportTime / 1000;

                                        int day1 = (int) (sportTime / (24 * 60 * 60 * 1000));
//                                int hour = (int) (sportTime / (60 * 60 * 1000) - day1 * 24);
//                                int min = (int) ((sportTime / (60 * 1000)) - day1 * 24 * 60 - hour * 60);
//                                int s = (int) (sportTime / 1000 - day1 * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

                                        int hour = (int) (sportTime / (60 * 60 * 1000));   // 13
                                        int min = (int) ((sportTime / (60 * 1000)) - hour * 60);  // 22    ---- 802分钟
                                        int s = (int) (sportTime / 1000 - hour * 60 * 60 - min * 60);

                                        Date sportDate = new Date();
                                        sportDate.setTime(sportTime);
                                        gpsPointDetailData.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC)); //mac地址
                                        gpsPointDetailData.setMid(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MID)); //用户mid
                                        gpsPointDetailData.setCalorie(calorie + "");   //卡路里
                                        gpsPointDetailData.setSpeed("0");
                                        gpsPointDetailData.setAve_step_width("0");
                                        gpsPointDetailData.setAltitude("0");
                                        gpsPointDetailData.setArraltitude("0");
                                        gpsPointDetailData.setArrLat("0");
                                        gpsPointDetailData.setArrheartRate("0");
                                        gpsPointDetailData.setArrLng("0");
                                        gpsPointDetailData.setArrspeed("0");
                                        gpsPointDetailData.setArrTotalSpeed("0");
                                        String hour1 = hour == 0 ? "00" : String.valueOf(hour);
                                        String min1 = min == 0 ? "00" : String.valueOf(min);
                                        String ss = s == 0 ? "00" : String.valueOf(s);
                                        String s1 = hour1 + ":" + min1 + ":" + ss;
                                        gpsPointDetailData.setSportTime(s1);

//                                long ddd = startDate.getTime();   // 1498721520000  --- 1498721940000 ---
                                        gpsPointDetailData.setTimeMillis(startDate.getTime() / 1000 + "");     // 运动数据开始时间 --- 该字段必须设置，否则，运动模式数据列表错乱
                                        gpsPointDetailData.setSportType(sportType + "");

                                        String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //
                                        if("473".equals(code) || "193".equals(code) || "199".equals(code) || "496".equals(code)) {// todo  --- AB227-X2+ 跑步模式距离算法改为90CM,序列号193,473
//                                            gpsPointDetailData.setMile(step * 0.9);
                                            int userHeightI = 170;
                                            String userHeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.HEIGHT, "170");
                                            if (StringUtils.isEmpty(userHeight) || userHeight.equals("0")) {
                                                userHeightI = 170;
                                            } else {
                                                userHeightI = Integer.valueOf(userHeight);
                                            }

                                            int sex = 1;
                                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("")) {
                                                sex = 1;//默认为男
                                            } else {
                                                int mspSex = Integer.parseInt(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SEX));
                                                if(mspSex == 0){
                                                    sex = 1;
                                                }else {
                                                    sex = 0;
                                                }
                                            }

                                            if(sportType == 1){  // todo --- 健走
                                                if(sex == 1){ // 男
                                                    gpsPointDetailData.setMile(step * 0.320*userHeightI/100);  // 0.415
                                                }else { // 女
                                                    gpsPointDetailData.setMile(step * 0.313*userHeightI/100);  // 0.413
                                                }
                                            }else if(sportType == 2){  // todo --- 跑步
                                                if(sex == 1){ // 男
                                                    gpsPointDetailData.setMile(step * 0.415*userHeightI/100);  // 0.516
                                                }else { // 女
                                                    gpsPointDetailData.setMile(step * 0.413*userHeightI/100);    // 0.5
                                                }
                                            }else if(sportType == 4){   // todo --- 爬山
                                                if(sex == 1){ // 男
                                                    gpsPointDetailData.setMile(step * 0.320*userHeightI/100); // 0.415
                                                }else { // 女
                                                    gpsPointDetailData.setMile(step * 0.313*userHeightI/100); // 0.413
                                                }
                                            }
//                                            gpsPointDetailData.setMile(step * 0.516*170/100);
                                        }else{
                                            gpsPointDetailData.setMile(step * 0.7);
                                        }

                                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                       /* int userHeightI = 170;
                                        String userHeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.HEIGHT, "170");
                                        if (StringUtils.isEmpty(userHeight) || userHeight.equals("0")) {
                                            userHeightI = 170;
                                        } else {
                                            userHeightI = Integer.valueOf(userHeight);
                                        }

                                        String distance = String.format(Locale.ENGLISH, "%.3f", (realStep * (0.415 * (float) userHeightI) / 100000));  //TODO BLE手环给的计算公式*/
                                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                                        gpsPointDetailData.setMile(step * 0.7);  // todo  --- AB227-X2+ 跑步模式距离算法改为90CM,序列号193,473
                                        gpsPointDetailData.setArrcadence("0");
                                        gpsPointDetailData.setDate(startTime.substring(0, 16));  //2017-06-29 15:39:00
                                        gpsPointDetailData.setDeviceType("2");
                                        gpsPointDetailData.setHeartRate("0");
                                        gpsPointDetailData.setMin_step_width("0");
                                        gpsPointDetailData.setPauseNumber("0");
                                        gpsPointDetailData.setPauseTime("0");
                                        gpsPointDetailData.setsTime(sportMiaos + "");
                                        gpsPointDetailData.setMax_step_width("0");
                                        gpsPointDetailData.setmCurrentSpeed("0");
                                        gpsPointDetailData.setStep("0");
                                        listGps.add(gpsPointDetailData);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                saveSpoetData(listGps);
                            }else{
                                byte[] b = new byte[4];
                                int sportLength = l2ValueLength / 16;
                                if(sportLength > 0 ) {
                                    for (int j = 0; j < sportLength; j++) {
                                        String gpsYear = String.format(Locale.ENGLISH,"20" + "%02d", bytes[j * 16 + 5]);
                                        String gpsMonth = String.format(Locale.ENGLISH,"%02d", bytes[j * 16 + 6]);
                                        String gpsDay = String.format(Locale.ENGLISH,"%02d", bytes[j * 16 + 7]);
                                        String gpsHour = String.format(Locale.ENGLISH,"%02d", bytes[j * 16 + 8]);
                                        String gpsMinute = String.format(Locale.ENGLISH,"%02d", bytes[j * 16 + 9]);
                                        String gpsSecond = String.format(Locale.ENGLISH,"%02d", bytes[j * 16 + 10]);
                                        System.arraycopy(bytes, j * 16 + 13, b, 0, 4);
                                        double lat = (double) NumberBytes.byteArrayToInt(b) / 1000000;
                                        latSb.append(lat + "&");
                                        gpsLatList.add(lat);
                                        System.arraycopy(bytes, j * 16 + 17, b, 0, 4);
                                        double lng = (double) NumberBytes.byteArrayToInt(b) / 1000000;
                                        lngSb.append(lng + "&");
                                        gpsLngList.add(lat);
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                        try {
                                            gpsTimeList.add(simpleDateFormat.parse(gpsYear + "-" + gpsMonth + "-" + gpsDay + " " + gpsHour + ":" + gpsMinute + ":" + gpsSecond).getTime() / 1000);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        gpsIndex++;
                                        if (gpsIndex == gpsNumber) {
                                            if(gpsPointDetailData != null){
                                                gpsPointDetailData.setArrLng(lngSb.toString());
                                                gpsPointDetailData.setArrLat(latSb.toString());

                                                float distance2 = 0;

                                                for (int i = 0; i < gpsTimeList.size(); i++) {
                                                    if(i >= gpsTimeList.size() -1){
                                                        break;
                                                    }
                                                    gpsTime += gpsTimeList.get(i + 1) - gpsTimeList.get(i);
                                                    double radLat1 = gpsLatList.get(i)*GPS_PI/180.0;  // 纬度
                                                    double radLat2 = gpsLatList.get(i+1)*GPS_PI/180.0;  // 纬度
                                                    double radLng1=  gpsLngList.get(i)*GPS_PI /180.0;    // 经度
                                                    double radLng2=  gpsLngList.get(i+1)*GPS_PI /180.0;    // 经度
                                                    double a = Math.abs(radLat1 - radLat2);
                                                    double c = Math.abs(radLng1 - radLng2);
                                                    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(c / 2), 2)));
                                                    s=s*6378137.0;
                                                    distance2 += s;
                                                    if(distance2 > 1000){

                                                        if(Locale.getDefault().getLanguage().equalsIgnoreCase("ar")){ //todo ---  阿拉伯语
                                                            speedSb.append(String.format(Locale.ENGLISH,"%1$02s'%2$02''",gpsTime/60,gpsTime%60) + "&");
                                                        }else {
                                                            speedSb.append(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",gpsTime/60,gpsTime%60) + "&");
                                                        }
//                                                        speedSb.append(String.format("%1$02d'%2$02d''",gpsTime/60,gpsTime%60) + "&");
                                                        gpsTime = 0;
                                                        distance2 = distance2 % 1000;
                                                    }else{
                                                        if(i == gpsTimeList.size() - 2){
                                                            speedSb.append(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",gpsTime/60,gpsTime%60) + "&");
                                                        }
                                                    }

                                                }
                                                if(!TextUtils.isEmpty(speedSb.toString())) {
                                                    gpsPointDetailData.setArrTotalSpeed(speedSb.toString());
                                                }else{
                                                    gpsPointDetailData.setArrTotalSpeed("0");
                                                }
                                                gpsList.add(gpsPointDetailData);
                                                if(!isReceiveSport) {
                                                    saveSpoetData(gpsList);
                                                }
                                                gpsList.clear();
                                                latSb.delete(0,latSb.length());
                                                lngSb.delete(0,lngSb.length());
                                                speedSb.delete(0,speedSb.length());
                                                gpsLatList.clear();
                                                gpsLngList.clear();
                                                gpsTimeList.clear();
                                                gpsNumber = 0;
                                                gpsIndex = 0;
                                                isGPS = false;
                                            }
                                        }
                                    }
                                }
                            }
                        }else {
                            int type = bytes[5] & 0xff;
                            int year = Integer.parseInt(String.format(Locale.ENGLISH,"20" + "%02d", bytes[6]));
                            String month = String.format(Locale.ENGLISH,"%02d", bytes[7]);
                            String day = String.format(Locale.ENGLISH,"%02d", bytes[8]);
                            String hour = String.format(Locale.ENGLISH,"%02d", bytes[9]);
                            String minute = String.format(Locale.ENGLISH,"%02d", bytes[10]);
                            String second = String.format(Locale.ENGLISH,"%02d", bytes[11]);
                            int sportTime = Utils.getInt(bytes, 12);
                            int sportDistance = Utils.getInt(bytes, 16);
                            int sportCalorie = Utils.getInt(bytes, 20);
                            int sportStep = Utils.getInt(bytes, 24);
                            int maxHeart = bytes[28] & 0xff;
                            int avgHeart = bytes[29] & 0xff;
                            int minHeart = bytes[30] & 0xff;
                            int maxFrequency = ((bytes[31] << 8) & 0xff00 | bytes[32] & 0xff);
                            int avgFrequency = ((bytes[33] << 8) & 0xff00 | bytes[34] & 0xff);
                            int minFrequency = ((bytes[35] << 8) & 0xff00 | bytes[36] & 0xff);
                            int maxPace = ((bytes[37] << 8) & 0xff00 | bytes[38] & 0xff);
                            int avgPace = ((bytes[39] << 8) & 0xff00 | bytes[40] & 0xff);
                            int minPace = ((bytes[41] << 8) & 0xff00 | bytes[42] & 0xff);
                            gpsNumber = Utils.getInt(bytes, 43);
                            gpsPointDetailData = new GpsPointDetailData();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            gpsPointDetailData.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                            gpsPointDetailData.setMid(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MID));
                            gpsPointDetailData.setmCurrentSpeed(avgPace + "");
                            try {
                                gpsPointDetailData.setTimeMillis(format.parse(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second).getTime() / 1000 + "");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            gpsPointDetailData.setAltitude("0");
                            gpsPointDetailData.setArraltitude("0");
                            gpsPointDetailData.setArrcadence(minFrequency + "&" + avgFrequency + "&" + maxFrequency);
                            gpsPointDetailData.setArrheartRate(minHeart + "&" + avgHeart + "&" + maxHeart);
                            gpsPointDetailData.setArrTotalSpeed(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",minPace/60,minPace%60) + "&"
                                    + String.format(Locale.ENGLISH,"%1$02d'%2$02d''",avgPace/60,avgPace%60) + "&" + String.format(Locale.ENGLISH,"%1$02d'%2$02d''",maxPace/60,maxPace%60));
                            gpsPointDetailData.setAve_step_width("0");
                            gpsPointDetailData.setMax_step_width("0");
                            gpsPointDetailData.setMin_step_width("0");
                            gpsPointDetailData.setDeviceType("2");
                            gpsPointDetailData.setPauseTime("0");
                            gpsPointDetailData.setPauseNumber("0");
                            gpsPointDetailData.setSportType(type + "");
                            gpsPointDetailData.setDate(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                            gpsPointDetailData.setMile(sportDistance);
                            gpsPointDetailData.setHeartRate(avgHeart + "");
                            gpsPointDetailData.setsTime(sportTime + "");
                            gpsPointDetailData.setSportTime(String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", sportTime / 60 / 60, sportTime / 60 % 60, sportTime % 60));
                            gpsPointDetailData.setCalorie((double)sportCalorie/1000 + "");
                            gpsPointDetailData.setSpeed((sportDistance/(double)sportTime) + "");
                            gpsPointDetailData.setArrspeed("0");
                            gpsPointDetailData.setStep(sportStep + "");
                            if(gpsNumber <= 0){
                                gpsPointDetailData.setArrLat("0");
                                gpsPointDetailData.setArrLng("0");
                                gpsList.add(gpsPointDetailData);
                                if(!isReceiveSport){
                                    saveSpoetData(gpsList);
                                }
                                gpsIndex = 0;
                                gpsNumber = 0;
                                isGPS = false;
                                gpsList.clear();
                            }else{
                                isGPS = true;
                            }
                        }
//                        Utils.saveSpoetData(listGps, null, sContext);
                    } else if (bytes[2] == BleContants.BLOOD_OXYGEN_HIS) {                         //TODO -- 血氧数据返回    0xAE

                    } else if (bytes[2] == BleContants.BLOOD_PRESSURE_HIS) {                 //TODO -- 历史血压数据返回    0xAD
                        if (SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {    //(智能表)
                            Log.e(TAG,"needSendDataType = " + needSendDataType + " ; needReceDataNumber = " + needReceDataNumber);
                            if (needReceDataNumber == 1) {
                                needReceDataNumber = 2;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "2");
                                sContext.sendBroadcast(intent);
                            } else if (needReceDataNumber == 2) {
                                needReceDataNumber = 3;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "3");
                                sContext.sendBroadcast(intent);
                            } else if(needReceDataNumber == 3){
                                needReceDataNumber = 4;
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "4");
                                sContext.sendBroadcast(intent);
                            }
                            if (BTNotificationApplication.needSendDataType == needReceDataNumber) {
                                receiveCountDownTimer.cancel();
                                receiveCountDownTimer.start();
                            }
                        }

                        int l2ValueLength = (((bytes[3] << 8) & 0xff00) | (bytes[4] & 0xff));
                        if (l2ValueLength == 0) {
                            return;
                        }
                        int pressureCount = l2ValueLength / 8;
                        if(pressureCount > 0) {
                            String myaddress = SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC);
                            if(!TextUtils.isEmpty(myaddress)) {
                                for (int j = 0; j < pressureCount; j++) {
                                    Bloodpressure bloodpressure = new Bloodpressure();
                                    count++;
                                    bloodpressure.setData(String.format(Locale.ENGLISH,"20" + "%02d",bytes[(j * 8) + 5])
                                            + "-" + String.format(Locale.ENGLISH,"%02d",bytes[(j * 8) + 6]) + "-" + String.format(Locale.ENGLISH,"%02d",bytes[(j * 8) + 7]));
                                    bloodpressure.setHour(String.format(Locale.ENGLISH,"%02d",bytes[(j * 8) + 8]) + ":" + String.format(Locale.ENGLISH,"%02d",bytes[(j * 8) + 9])
                                            + ":" + String.format(Locale.ENGLISH,"%02d",bytes[(j * 8) + 10]));
                                    bloodpressure.setConunt(count + "");
                                    bloodpressure.setMac(myaddress);
                                    bloodpressure.setHeightBlood((bytes[(j * 8) + 11] & 0xff) + "");
                                    bloodpressure.setMinBlood((bytes[(j * 8) + 12] & 0xff) + "");
                                    Log.e(TAG,"Blood date = " + bloodpressure.getData() + " " + bloodpressure.getHour());
                                    Log.e(TAG,"MinBlood = " + bloodpressure.getMinBlood() + " ;  MaxBlood = " + bloodpressure.getHeightBlood());
                                    if (BbloodpressureList != null) {
                                        BbloodpressureList.add(bloodpressure);
                                    }
                                }
                                if (BbloodpressureList.size() > 0 && BbloodpressureList != null) {
                                    saveBloodpressure(BbloodpressureList);
                                    BbloodpressureList.clear();
                                }
                            }
                        }


                    } else if (bytes[2] == BleContants.BLOOD_PRESSURE) {                 //TODO --  实时血压数据返回    0xAD
                        int bp_max = bytes[5] & 0xFF;
                        int bp_min = bytes[6] & 0xFF;
                        //判断当前手环类型 cf006

                        if(BTNotificationApplication.isSyncEnd) {  //todo --- 同步数据完成才发送 同步实时计步的广播
                            SendXieya(bp_min,bp_max);   //todo   ---  BLE实时血压
                        }

                    } else if (bytes[2] == BleContants.BLOOD_OXYGEN) {                 //TODO --  实时血氧数据返回    0xAD
                        int oxygen = bytes[5] & 0xFF;             //血氧值
                        Log.e(TAG, "实时血氧 ： " + oxygen);  //    

                        if(BTNotificationApplication.isSyncEnd) {  //todo --- 同步数据完成才发送 同步实时计步的广播
                            SenDXieyang(oxygen);      //todo   ---  BLE实时血氧
                        }
                    }
                } else if (byte1 == BleContants.CALIBRATION_COMMAND) {                       //TODO -- 校准命令    0x0B

                } else if (byte1 == BleContants.FACTORY_COMMAND) {                             //TODO -- 工厂命令   0x0C

                }else if (byte1 == BleContants.PUSH_DATA_TO_PHONE_COMMAND) {                        //TODO -- 查找命令   0x0D   推送数据到手机 (接挂电话相关)
                    //    0A00A300 64110909000000004C00000059000000A6000000B0000000E1000000F3000001360000028E00000C1000000EDA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
                    //    0A00A300 641109080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000063300000C2600000E3E00000F1D
                    switch (byte3) {   // -------  key                                      BA300 0080 04F000A0D00010003000101      0A00 AD00 08 1109080101000000
                        case BleContants.GESTURE_PUSH_COMMAND: // 手势智控推送    ----    0D0001 00  03 00 0000    0D0001 00  0300 0101
                            Log.e("phone", "智控开关发命令了");  // TAG     05005000 0100
                            //todo  --- 当开关为开时，将抬手亮屏的开关打开 ----发广播
                            int gesNum = bytes[6];
                            if(gesNum == 1){
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_GESTURE_ON);    // 发数据同步成功的广播
                                sendBroadcast(intent);
                            }else{
                                Intent intent = new Intent();
                                intent.setAction(MainService.ACTION_GESTURE_OFF);    // 发数据同步成功的广播
                                sendBroadcast(intent);
                            }

                            break;

//                        case BleContants.REJECT_DIAL_COMMAND: // 拒接电话
//                            Log.e("phone", "拒接电话了");  // TAG
//                            break;
//
//                        case BleContants.ANSWER_DIAL_COMMAND: // 接电话
//                            Log.e("phone", "接电话了");
//                            break;

                    }
                }else if (byte1 == (byte) 0x10){              //TODO   ---时钟机芯校准命令
                    switch (byte3){
                        case (byte)0x02:
                            EventBus.getDefault().post(new MessageEvent(CalibrationActivity.REFUSE_CALIBRATION));
                            break;
                        case (byte)0x03:
                            EventBus.getDefault().post(new MessageEvent(CalibrationActivity.REFUSE_CALIBRATION));
                            break;
                        case (byte)0x04:
                            EventBus.getDefault().post(new MessageEvent(CalibrationActivity.CONFIRM_CALIBRATION));
                            break;
                        case (byte)0x05:
                            if(bytes.length > 5){
                                int code = bytes[5];
                                if(code == 0){
                                    EventBus.getDefault().post(new MessageEvent(CalibrationActivity.SEND_CALIBRATION));
                                }else{
                                    EventBus.getDefault().post(new MessageEvent(CalibrationActivity.CANCEL_CALIBRATION));
                                }
                            }

                            break;
                    }
                }else if (byte1 == BleContants.COMMAND_WEATHER_INDEX) {//TODO -- 表盘推送    0x0E 多包
                    if (bytes[2] == BleContants.DIAL_PUSH) {   // 表盘推送       0E 00 E2 00 06  00 00 00 00 00 00   ------     0E 00 E2 00 02 01 00
                        int packageNum = WatchPushActivityNew.fileByte.length/256;    // 0E00E20006 00 0100 0000 00    ------
                        int lastpackageNum = WatchPushActivityNew.fileByte.length%256;
                       /* int packageNum = WatchPushActivityNew.fileByte.length/256;    // 0E00E20006 00 0100 0000 00    ------
                        int lastpackageNum = WatchPushActivityNew.fileByte.length%256;
                        boolean isEnd = false;
//                        index = 0;
                        boolean isHasPianYi = false;
                        int ipianYi = 0;

//                        indexHpy = 0;
                        int packageNumHpy = 0;    // 0E00E20006 00 0100 0000 00    ------
                        int lastpackageNumHpy = 0;*/
                        if(bytes[6] == 0 && bytes.length>7){  // TODO -- 没有表盘   --- 根据返回Byte数组的长度 加判断
                            byte[] value = new byte[262];
                            value[0] = (byte)0x01; // 固定1字节
                            value[1] = (byte)00; // 4字节偏移地址
                            value[2] = (byte)00;
                            value[3] = (byte)00;
                            value[4] = (byte)00;
                            value[5] = (byte)00; // 固定1字节
                            // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
                            System.arraycopy(WatchPushActivityNew.fileByte, 0, value, 6, 256);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
                            L2Send.sendPushDialPicData(value);
                        }else if(bytes[6] == 1&& bytes.length>7){  // TODO -- 表盘未推送完     0E 00 E2 00 01 01
                            isHasPianYi = true;

                            byte[] valueLast = new byte[4]; //  todo  有推送偏移地址    0E 00 E2 00 06  00 00   00 00 00 00
                            valueLast[0] = bytes[7];
                            valueLast[1] = bytes[8];
                            valueLast[2] = bytes[9];
                            valueLast[3] = bytes[10]; // 保存偏移地址
                            ipianYi = NumberBytes.byteArrayToInt(valueLast);   // todo --- 已经发送过的图片 数据

                            packageNumHpy = (WatchPushActivityNew.fileByte.length - ipianYi)/256;    // 0E00E20006 00 0100 0000 00    ------
                            lastpackageNumHpy = (WatchPushActivityNew.fileByte.length - ipianYi)%256;

                            byte[] value = new byte[262];
                            value[0] = (byte)0x01; // 固定1字节
                            value[1] = bytes[7]; // 4字节偏移地址
                            value[2] = bytes[8];
                            value[3] = bytes[9];        //     int dddd = NumberBytes.byteArrayToInt(bb);
                            value[4] = bytes[10];
                            value[5] = (byte)00; // 固定1字节
                            // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
//                            System.arraycopy(WatchPushActivityNew.fileByte, 0, value, 6, 256);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid

                            if(WatchPushActivityNew.fileByte.length - ipianYi >= 256){
                                System.arraycopy(WatchPushActivityNew.fileByte,ipianYi, value, 6, 256);
                            }else {
                                System.arraycopy(WatchPushActivityNew.fileByte,ipianYi, value, 6, WatchPushActivityNew.fileByte.length - ipianYi);
                            }
//                            System.arraycopy(WatchPushActivityNew.fileByte,ipianYi, value, 6, 256);  // todo ---- 还需要考虑 是否 够 256 字节
                            L2Send.sendPushDialPicData(value);
                        }else if(bytes[6] == 2 && bytes.length>7){  // TODO -- 表盘已推送完
                            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.dialpush_ed),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(MainService.ACTION_PUSHPIC_FINISH);   //TODO  ---  发广播，
                            sendBroadcast(intent);
                        }

                        if(bytes[5] == 1 && bytes.length == 7){   // 0E 00 E2 00 02 0100  ---- 发送升级包 应该为 6个字节 （第2条命令）
//                            int indexHpy = 0;
                            if(isHasPianYi){ // 有偏移，有表盘未推送完   ----    ipianYi 偏移
                                indexHpy++;
//                                int packageNumHpy = (WatchPushActivityNew.fileByte.length - ipianYi)/256;    // 0E00E20006 00 0100 0000 00    ------
//                                int lastpackageNumHpy = (WatchPushActivityNew.fileByte.length - ipianYi)%256;

                                if(isEnd){
                                    indexHpy = 0;
                                    byte[] value = new byte[1];
                                    value[0] = (byte)0x02; // 固定1字节
                                    L2Send.sendPushDialPicData(value);
                                    isHasPianYi = false;
                                    return;
                                }

                                if(indexHpy + 1 <= packageNumHpy){ //indexHpy +1 <= packageNumHpy  最后一个完整包       0E 00 E2 00 02 02 00  ----  0E 00 E2 00 02 02 01 （最后一条命令）
                                    isEnd = false;
                                    int pianyiAdd = indexHpy*256;
                                    byte[] value = new byte[262];
                                    value[0] = (byte)0x01; // 固定1字节

//                                    value[1] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
//                                    value[2] = (byte)(pianyiAdd >> 8);
//                                    value[3] = (byte)(pianyiAdd >> 16);
//                                    value[4] = (byte)(pianyiAdd >> 24);
                                    value[4] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
                                    value[3] = (byte)(pianyiAdd >> 8);
                                    value[2] = (byte)(pianyiAdd >> 16);
                                    value[1] = (byte)(pianyiAdd >> 24);

                                    value[5] = (byte)00; // 固定1字节
                                    // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
                                    System.arraycopy(WatchPushActivityNew.fileByte, pianyiAdd, value, 6, 256);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
                                    L2Send.sendPushDialPicData(value);
                                }else {
                                    int pianyiAdd = packageNumHpy*256;
                                    byte[] value = new byte[6+ lastpackageNumHpy];
                                    value[0] = (byte)0x01; // 固定1字节

//                                    value[1] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
//                                    value[2] = (byte)(pianyiAdd >> 8);
//                                    value[3] = (byte)(pianyiAdd >> 16);
//                                    value[4] = (byte)(pianyiAdd >> 24);
                                    value[4] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
                                    value[3] = (byte)(pianyiAdd >> 8);
                                    value[2] = (byte)(pianyiAdd >> 16);
                                    value[1] = (byte)(pianyiAdd >> 24);

                                    value[5] = (byte)00; // 固定1字节
                                    // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
                                    System.arraycopy(WatchPushActivityNew.fileByte, pianyiAdd, value, 6, lastpackageNum);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
                                    L2Send.sendPushDialPicData(value);
                                    isEnd = true;
                                }

//                                if(isEnd){
//                                    indexHpy = 0;
//                                    byte[] value = new byte[1];
//                                    value[0] = (byte)0x02; // 固定1字节
//                                    L2Send.sendPushDialPicData(value);
//                                }

                            }else{
                                index++;

                                if(isEnd){
                                    index = 0;
                                    byte[] value = new byte[1];
                                    value[0] = (byte)0x02; // 固定1字节
                                    L2Send.sendPushDialPicData(value);
                                    return;
                                }

                                if(index +1 <= packageNum){ // 最后一个完整包       0E 00 E2 00 02 02 00  ----  0E 00 E2 00 02 02 01 （最后一条命令）
                                    isEnd = false;
                                    int pianyiAdd = index*256;
                                    byte[] value = new byte[262];
                                    value[0] = (byte)0x01; // 固定1字节

//                                    value[1] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
//                                    value[2] = (byte)(pianyiAdd >> 8);
//                                    value[3] = (byte)(pianyiAdd >> 16);
//                                    value[4] = (byte)(pianyiAdd >> 24);
                                    value[4] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
                                    value[3] = (byte)(pianyiAdd >> 8);
                                    value[2] = (byte)(pianyiAdd >> 16);
                                    value[1] = (byte)(pianyiAdd >> 24);

                                    value[5] = (byte)00; // 固定1字节
                                    // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
                                    System.arraycopy(WatchPushActivityNew.fileByte, pianyiAdd, value, 6, 256);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
                                    L2Send.sendPushDialPicData(value);
                                }else {
                                    int pianyiAdd = packageNum*256;
                                    byte[] value = new byte[6+ lastpackageNum];
                                    value[0] = (byte)0x01; // 固定1字节

//                                    value[1] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
//                                    value[2] = (byte)(pianyiAdd >> 8);
//                                    value[3] = (byte)(pianyiAdd >> 16);
//                                    value[4] = (byte)(pianyiAdd >> 24);
                                    value[4] = (byte)(pianyiAdd & 0xff); // 4字节偏移地址
                                    value[3] = (byte)(pianyiAdd >> 8);
                                    value[2] = (byte)(pianyiAdd >> 16);
                                    value[1] = (byte)(pianyiAdd >> 24);


                                    value[5] = (byte)00; // 固定1字节
                                    // Object src : 原数组 int srcPos : 元数据的起始  Object dest : 目标数组  int destPos : 目标数组起始  int length  : 要copy的长度
                                    System.arraycopy(WatchPushActivityNew.fileByte, pianyiAdd, value, 6, lastpackageNum);   // 将 固件包 的 起始 第8位 copy 4位 到  mFileImgHdr.uid
                                    L2Send.sendPushDialPicData(value);
                                    isEnd = true;
                                }

//                                if(isEnd){
//                                    index = 0;
//                                    byte[] value = new byte[1];
//                                    value[0] = (byte)0x02; // 固定1字节
//                                    L2Send.sendPushDialPicData(value);
//                                }
                            }
                        }else if(bytes[5] == 2  && bytes.length == 7){      //TODO -- 此处应该为7个字节  0E 00 E2 00 02 02  00
                            ipianYi = 0;
                            packageNumHpy = 0;    // 0E00E20006 00 0100 0000 00    ------
                            lastpackageNumHpy = 0;
                            isEnd = false;
                            if(bytes[6] == 1){
                                Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.dialpush_success),Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.dialpush_fail),Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent();
                            intent.setAction(MainService.ACTION_PUSHPIC_FINISH);   //TODO  ---  发广播，
                            sendBroadcast(intent);
                        }
                    }
                }
            }
        }
    }


    /**
     * 发送心率数据
     */
private void SendHate(int heart){
    if (heart == 0) {
        return;
    }
    HearData hearData = new HearData();
    hearData.setBinTime(System.currentTimeMillis() / 1000 + "");
    hearData.setHeartbeat(heart + "");
    hearData.setHigt_hata(heart + "");
    hearData.setLow_hata(heart + "");
    hearData.setAvg_hata(heart + "");//平均的心率
    List<HearData> heartList = new ArrayList<>();
    heartList.add(hearData);
    Log.e("UPDTA", "4");
    heartAllList.add(hearData);
    heartdataWrite(heartList,true);
    Intent intent = new Intent();      // add 0414
    //todo    ACTION_SYNARTHEART  ---- BLE 平台同步数据的时候，不让刷新实时心率的数据

    if(BTNotificationApplication.isSyncEnd){  // 同步数据成功 才发实时心率数据
        intent.setAction(MainService.ACTION_SYNARTHEART);
        intent.putExtra("heart", heart+"");
//        intent.putExtra("time", hearData.getBinTime());
        sendBroadcast(intent);
}

//    intent.setAction(MainService.ACTION_SYNARTHEART);
//    intent.putExtra("heart", heart+"");
//    intent.putExtra("time", hearData.getBinTime());
//    sendBroadcast(intent);
}



    /**
     * 发送血压
     * @param
     */
   private void SendXieya(int bp_min,int bp_max){   //      

                      /*  if(BbloodpressureList.size()>0){
                            BbloodpressureList.clear();
                        }*/
                        if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
                            count++;
                            String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();


                            Bloodpressure bloodpressure = new Bloodpressure();
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            String str = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(curDate);
                            long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Date d1 = new Date(time);
                            String t1 = format.format(d1);
                            bloodpressure.setData(t1);
                            bloodpressure.setHour(str);
                            bloodpressure.setHeightBlood(bp_max + "");
                            bloodpressure.setMinBlood(bp_min + "");
                            bloodpressure.setMac(myaddress);
                            bloodpressure.setId(Long.valueOf("0"));
                            bloodpressure.setConunt(count + "");
                            BbloodpressureList.add(bloodpressure);
                            saveBloodpressure(BbloodpressureList);//保存到数据库
                            BbloodpressureList.clear();
       }
   }


    /**
     * 发送血氧
     * @param
     */
    private  void SenDXieyang(int oxygen){
        // EventBus.getDefault().post(new MessageEvent("updata_XIEYANGpressurev",oxygen+""));
        if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
            String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();

            long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d1 = new Date(time);
            String t1 = format.format(d1);
            String str = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(d1);
            Oxygen oxygens = new Oxygen();
            heightOxy.add(oxygen);
            if (heightOxy.size() > 2||heightOxy.size()==2) {
                oxygens.setHour(str);
                oxygens.setData(t1);
                oxygens.setOxygen(oxygen + "");
                oxygens.setHeightOxygen(Integer.valueOf(Collections.max(heightOxy) + "") + "");
                oxygens.setMinOxygen(Integer.valueOf(Collections.min(heightOxy) + "") + "");
                oxygens.setMac(myaddress);
                XIEYANGList.add(oxygens);
                saveOxygen(XIEYANGList);
                XIEYANGList.clear();
            } else {
                oxygens.setHour(str);
                oxygens.setData(t1);
                oxygens.setOxygen(oxygen + "");
                oxygens.setHeightOxygen(oxygen + "");
                oxygens.setMinOxygen(oxygen + "");
                oxygens.setMac(myaddress);
                XIEYANGList.add(oxygens);
                saveOxygen(XIEYANGList);
                XIEYANGList.clear();
            }
        }
    }

    /**
     * 保存血氧 数据
     *
     * @param list
     */

    public synchronized void saveOxygen(List<Oxygen> list) {  // TODO ---- 保存心率数据（参数，心率的开始时间，心率的数值）
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }

        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  //

        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");
                Oxygen oxygen = new Oxygen();
                oxygen.setOxygen(list.get(i).getOxygen());
                oxygen.setHour(list.get(i).getHour());//测试时间
                oxygen.setData(list.get(i).getData() + "");
                if (null == list.get(i).getHeightOxygen()) {
                    oxygen.setHeightOxygen(list.get(i).getOxygen());
                } else {
                    oxygen.setHeightOxygen(list.get(i).getHeightOxygen());
                }

                if (null == list.get(i).getMinOxygen()) {
                    oxygen.setMinOxygen(list.get(i).getOxygen());
                } else {
                    oxygen.setMinOxygen(list.get(i).getMinOxygen());
                }
                oxygen.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                db.saveOxygen(oxygen);
            }

            Intent intent = new Intent();
            intent.setAction(MainService.ACTION_SYNARTBO);    //发送血压血氧
//            intent.putExtra("bo", oxygen + "");
            sContext.sendBroadcast(intent);

//            EventBus.getDefault().post(new MessageEvent("updata_XIEYANGpressure"));
        }
    }


    public synchronized void saveTemperature(List<Temperature> list) throws ParseException {  // TODO ---- 保存心率数据（参数，心率的开始时间，心率的数值）
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                Temperature temperature = (Temperature) list.get(i);
                temperature.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                deleteSameTemperatureData(temperature);
                db.saveTemperature(temperature);
            }
            Intent intent = new Intent();
            intent.setAction(MainService.ACTION_SYNARTBO);    //发送血压血氧
            sContext.sendBroadcast(intent);
        }
    }

    public synchronized void saveTemperature(Temperature temperature) throws ParseException {  // TODO ---- 保存心率数据（参数，心率的开始时间，心率的数值）
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }
        deleteSameTemperatureData(temperature);
        db.saveTemperature(temperature);
    }

    public synchronized void deleteSameTemperatureData(Temperature temperature) throws ParseException {
        String binTime = temperature.getBinTime();
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }
        Query query = db.getTemperatureDao().queryBuilder()
                .where(TemperatureDao.Properties.BinTime.eq(binTime)).build();
        List list = query.list();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Temperature temperature_ = (Temperature) list.get(i);
            if(temperature_!=null&&!TextUtils.isEmpty(temperature_.getBinTime())&&temperature_.getBinTime().equals(temperature.getBinTime()))
            {
                db.getTemperatureDao().delete(temperature_);
                break;
            }
        }
    }

    public synchronized void deleteEcgData(Ecg ecg) throws ParseException {
        String binTime = ecg.getBinTime();
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }
        Query query = db.getEcgDao().queryBuilder()
                .where(EcgDao.Properties.BinTime.eq(binTime)).build();
        List list = query.list();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Ecg ecg_ = (Ecg) list.get(i);
            if(ecg_!=null&&!TextUtils.isEmpty(ecg_.getBinTime())&&ecg_.getBinTime().equals(ecg.getBinTime()))
            {
                db.getEcgDao().delete(ecg_);
                break;
            }
        }
    }

    /**
     * 保存血压 数据
     *
     * @param list
     */

    public synchronized void saveBloodpressure(List<Bloodpressure> list) {  // TODO ----  isRealTime:是否实时血压
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }

        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  //

        if (list.size() != 0) {
            String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);
            for (int i = 0; i < list.size(); i++) {
                if(watch.equals("1")){
                    if(!StringUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLOOD_PRESSURE, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))) {
                        if ((StringUtils.parseStrToDate(list.get(i).getData() + " " + list.get(i).getHour())).getTime() < StringUtils.parseStrToDate(SharedPreUtil.readPre(this, SharedPreUtil.BLOOD_PRESSURE, SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC))).getTime()) {
                            continue;
                        }
                    }
                }

                SimpleDateFormat mformatter = Utils.setSimpleDateFormat("yyyy-MM-dd");
                Bloodpressure bloodpressure = new Bloodpressure();
                bloodpressure.setHeightBlood(list.get(i).getHeightBlood());//高压
                bloodpressure.setMinBlood(list.get(i).getMinBlood());//低压
                bloodpressure.setHour(list.get(i).getHour());//测试时间
                bloodpressure.setData(list.get(i).getData() + "");
                //bloodpressure.setId(Long.valueOf("0"));
                bloodpressure.setConunt(list.get(i).getConunt());
                bloodpressure.setMac(SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                db.saveBloodpressure(bloodpressure);
            }
            if(watch.equals("1")) {
                SharedPreUtil.savePre(this, SharedPreUtil.BLOOD_PRESSURE, SharedPreUtil.readPre(sContext, SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getData() + " " + list.get(list.size() - 1).getHour() + "");  //存最后个血压数据的时间 2018-03-23 09:00:00
            }
            Intent intent = new Intent();
            intent.setAction(MainService.ACTION_SYNARTBP);    //发送血压
//            intent.putExtra("bp_min", bp_min + "");
//            intent.putExtra("bp_max", bp_max + "");
            sContext.sendBroadcast(intent);

//            EventBus.getDefault().post(new MessageEvent("updata_Bloodpressureone"));
        }
    }


    public synchronized void saveSpoetData(List<GpsPointDetailData> list) {
        if (db == null) {
            db = DBHelper.getInstance(sContext);
        }

        if (list.size() != 0) {
            String mid = SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MID);  // 用户id
            Query query = null;
            query = db.getGpsPointDetailDao().queryBuilder().where(GpsPointDetailDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            List<GpsPointDetailData> list1 = query.list();
            boolean b = list1.size() == 0 ? true : false;

            if (list1.size() != 0) {
                for (int j = 0; j < list1.size(); j++) {   // 只要数据库有运动模式数据，先清除
                    for (GpsPointDetailData mDetailData : list) {      // todo --- 新产生的运动模式的数据
                        if(mDetailData.getDate().equals(list1.get(j).getDate())){
                            db.DeleteGpsPointData(list1.get(j));
                        }
                    }
                }

                for (GpsPointDetailData mDetailData : list) {      // todo --- 新产生的运动模式的数据
                    db.saveGpsPointDeatilData(mDetailData);
                }

            }else {
                for (GpsPointDetailData mDetailData : list) {      // todo --- 新产生的运动模式的数据
                    db.saveGpsPointDeatilData(mDetailData);
                }
            }
            SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.SPORT_BT, SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC), list.get(list.size() - 1).getTimeMillis() + "");  // 运动模式 的数据 保存到本地 数据库 时，保存当前的系统时间，作为下一次 ，获取数据的起点
        }

        if(!isReceiveSport){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // 发广播，运动模式数据 同步成功    ---- 保存3次，发3个广播
            getApplicationContext().sendBroadcast(broadcastIntent);
        }

    }



    //***********************************************************************************************************************//
    private BluetoothDevice connectDevice;

    public boolean connectDevice(BluetoothDevice device) {
        connectDevice = device;
        KCTBluetoothManager.getInstance().connect(device);
        return true;
    }


    public void disConnect() {
        if(null != mHidConncetUtil && null != connectDevice){  // null != connectDevice
            mHidConncetUtil.unPair(connectDevice);
            mHidConncetUtil.disConnect(connectDevice);   // todo --- 绑定后，再连接   ---- 通过反射连接
        }
        
        KCTBluetoothManager.getInstance().disConnect_a2d();
        /*if (null ==  mBluetoothAdapter ||  null == mBluetoothGatt) {
            return;
        }
        mBluetoothGatt.disconnect();  // 系统api ,断开蓝牙
        connectDevice = null;*/

        EventBus.getDefault().post(new MessageEvent(CONNECT_FAIL));
        setState(STATE_DISCONNECTED);
    }

    private void uart_data_end_call(Context context) {  // Context context, int i
        Object telephonyObject = getTelephonyObject(context);
        if (telephonyObject!=null){
            Class telephonyClass = telephonyObject.getClass();
            try {
                Log.e(TAG,"------------------------");
                Method endClassMethod= telephonyClass.getMethod("endCall");
                endClassMethod.setAccessible(true);    // public boolean com.android.internal.telephony.ITelephony$Stub$Proxy.endCall() throws android.os.RemoteException
                endClassMethod.invoke(telephonyObject);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object getTelephonyObject(Context context) {
        Object telephonyObject=null;
        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class telManager=telephonyManager.getClass();
        try {
            Method getITelephony=telManager.getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            telephonyObject=getITelephony.invoke(telephonyManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return telephonyObject;
    }

    public synchronized void saveEcgData(final Ecg ecg)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sContext) {
                    if (ecg != null) {
                        try {
                            if (db == null) {
                                db = DBHelper.getInstance(sContext);
                            }
                            deleteEcgData(ecg);
                            db.saveEcg(ecg);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    int mEcgPackageIndex =0;
    String mEcgTime = "";
    Ecg mEcg = new Ecg();
    boolean isNormal = false;
    private  synchronized void parseEcg(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        int length = bytes.length;
        if (length < 5) {
            return;
        }
        int start = 5;
        int testCount = (bytes[start] & 0xff);
        int testIndex = (bytes[start+1] & 0xff);
        int packageCount = (bytes[start+2] & 0xff);
        int packageIndex = (bytes[start+3] & 0xff);
        if(testCount<=0||testIndex>testCount)
        {
            return;
        }
        if(packageCount<=0||packageIndex>packageCount)
        {
            return;
        }
        Log.e(TAG, "parseEcg packageIndex =" + packageIndex+"--packageCount="+packageCount);
        int startIndex = start+4;
        String year = String.format(Locale.ENGLISH, "20" + "%02d", bytes[startIndex + 0]);
        String mouth = String.format(Locale.ENGLISH, "%02d", bytes[startIndex + 1]);
        String day = String.format(Locale.ENGLISH, "%02d", bytes[startIndex + 2]);
        String hour = String.format(Locale.ENGLISH, "%02d", bytes[startIndex + 3]);
        String minute = String.format(Locale.ENGLISH, "%02d", bytes[startIndex + 4]);
        String second = String.format(Locale.ENGLISH, "%02d", bytes[startIndex + 5]);
        String ecgTime = year + "-" + mouth + "-" + day + " " + hour + ":" + minute + ":" + second;
        String date = year + "-" + mouth + "-" + day ;
        String hourStr = hour + ":" + minute + ":" + second;
        if (packageIndex == 0) {
            isNormal = true;
            mEcgPackageIndex = packageIndex;
            mEcgTime = ecgTime;
            mEcg = new Ecg();
        } else {
            if (!ecgTime.equals(mEcgTime)) {
                isNormal = false;
                answerEcg(testCount,testIndex,packageCount,packageIndex);
                return;
            }
            if (mEcgPackageIndex + 1 != packageIndex) {
                isNormal = false;
                answerEcg(testCount,testIndex,packageCount,packageIndex);
                return;
            }
            mEcgPackageIndex = packageIndex;
        }
        mEcg.setDate(date);
        String binTime = null;
        try {
            binTime = mSimpleDateFormat.parse(date + " " + hourStr).getTime() + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mEcg.setBinTime(binTime);
        String hearts = mEcg.getHearts();
        if(!TextUtils.isEmpty(hearts)&&!hearts.endsWith(";"))
        {
            hearts = hearts+";";
        }
        String ecgStr = mEcg.getEcgs();
        if(!TextUtils.isEmpty(ecgStr)&&!ecgStr.endsWith(";"))
        {
            ecgStr = ecgStr+";";
        }

        int ecgStartIndex = startIndex + 5+1;
        for (int i=ecgStartIndex;i<length;i++)
        {
            if(i+1<=length-1)
            {
                final short value = (short) ((bytes[i]&0xff)<<8|(bytes[i+1]&0xff));
                if(i == ecgStartIndex)
                {
                    hearts = hearts+value+";";
                }
                else
                {
                    ecgStr = ecgStr+value+";";
                }
                i++;
            }
        }
        mEcg.setHearts(hearts);
        mEcg.setEcgs(ecgStr);
        Log.e(TAG, "parseEcg packageIndex111 =" + packageIndex+"--packageCount="+packageCount);
        answerEcg(testCount,testIndex,packageCount,packageIndex);
        if (packageIndex+1 == packageCount&&isNormal) {
            String mac = "";
            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
                mac = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            } else {
                mac = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC);
            }
            mEcg.setMac(mac);
            saveEcgData(mEcg);
        }
    }


    private void answerEcg(int testCount,int testIndex,int packageCount,int packageIndex)
    {
        byte[] key = new byte[4];
        key[0] = (byte) testCount;
        key[1] = (byte) testIndex;
        key[2] = (byte) packageCount;
        key[3] = (byte) packageIndex;
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.ANSWER_ECG_SYCN_REQUEST, key);
        MainService.getInstance().writeToDevice(l2, true);
    }
	
	private void controlMusic(int keyCode) {      
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
        dispatchMediaKeyToAudioService(key);
        dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, KeyEvent.ACTION_UP));
    }

    private void dispatchMediaKeyToAudioService(KeyEvent event) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            try {
                audioManager.dispatchMediaKeyEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}	

}
