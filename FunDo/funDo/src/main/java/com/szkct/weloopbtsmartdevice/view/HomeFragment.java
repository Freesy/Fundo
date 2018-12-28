package com.szkct.weloopbtsmartdevice.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.szkct.adapter.ViewPagerAdapter;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.bluetoothgyl.UtilsLX;
import com.szkct.map.SportsHistoryActivity;
import com.szkct.map.bean.GuangGaoBean;
import com.szkct.map.popu.SportModePopu;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.data.greendao.Bloodpressure;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Oxygen;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.BloodpressureDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDetailDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.OxyDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.CalendarAcitity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.main.MainService.ReturnData;
import com.szkct.weloopbtsmartdevice.main.MotionSettingActivity;
import com.szkct.weloopbtsmartdevice.main.OutdoorRunActitivy;
import com.szkct.weloopbtsmartdevice.main.PresentationActivity;
import com.szkct.weloopbtsmartdevice.main.SportsTheCountdownActivity;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.trajectory.MyAlertDialog;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.DeviceUtils;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.MarketUtils;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.PullToRefreshBase;
import com.szkct.weloopbtsmartdevice.util.PullToRefreshBase.OnRefreshListener;
import com.szkct.weloopbtsmartdevice.util.PullToRefreshScrollView;
import com.szkct.weloopbtsmartdevice.util.Resolve;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreferencesUtils;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import de.greenrobot.dao.query.Query;

import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_OXYGEN;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.CONNECT_FAIL;
import static com.szkct.weloopbtsmartdevice.main.MainService.HEART;
import static com.szkct.weloopbtsmartdevice.main.MainService.ISSYNWATCHINFO;
import static com.szkct.weloopbtsmartdevice.main.MainService.SLEEP;
import static com.szkct.weloopbtsmartdevice.util.StringUtils.SIMPLE_DATE_FORMAT;
import static com.szkct.weloopbtsmartdevice.util.Utils.dateInversion;

import static com.szkct.weloopbtsmartdevice.main.MainService.WEATHER_PUSH;

/*import cn.sharesdk.demo.wxapi.onekeyshare.OnekeyShare;
import cn.sharesdk.demo.wxapi.onekeyshare.OnekeyShareTheme;*/
/*import cn.sharesdk.instagram.Instagram;*/
/*import cn.sharesdk.twitter.Twitter;*/

@SuppressLint("SimpleDateFormat")
public class HomeFragment extends Fragment implements AMapLocationListener, OnPageChangeListener, OnClickListener {
    private static final String TAG = "HomeFragment";
    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_TITLE = "home_title";
    private ViewPagerAdapter vpAdapter;
    private View v_vp, v_sleep, v_newsport, v_heart_rate, v_Oxygen, v_Bloodpressure;

    private TextView bloodpressureheart_textone, bloodpressureheart_texttwo, bloodpressureheart_textthere;//血氧
    private TextView bloodpressurebaohe_show, bloodpressurebaohe_showtwo, bloodpressurebaohe_showthere, xieya_number;//血氧高低显示
    private TextView counts, showtitle;//测量次数血压
    private TextView bloodpressurebaoheconts;//测量次数血氧
    private TextView bloodpressure_tv_weather;//血氧温度
    private ImageView bloodpressure_weather_icon;//血氧天气图标
    private TextView bloodpressure_curdatetv;//日期显示
    private ImageView bloodpressureiv_bo, bloodpressureiv_bp;

    List AAA = new ArrayList();

    private TextView oxyheart_textone, oxyheart_texttwo, oxyheart_textthere;//血ya
    private TextView baohe_show, baohe_showtwo, baohe_showthere;//血氧高低显示
    private TextView Oxy_tv_weather;//血氧温度
    private ImageView Oxy_weather_icon;//血氧天气图标
    private TextView OXY_curdatetv;//日期显示
    private ImageView OXYiv_bo, OXYiv_bp;

    private ImageView ivGuanggao;
    private ImageView ivCenter;
    private FrameLayout fl_guanggao;
    private ImageView iv_x;
    private String btnUrl;
    private String centerUrl;
    private int btnPageId = -1;
    private int centerPageId = -1;
    public static final int TJ = 1010;


    // private RadioGroup transactionRadio;
    private List<View> views;
    private ViewPager vp;
    private TextView cb_navigation_sport, cb_navigation_sleep, cb_navigation_heart;
    private ImageButton ib_navigation_share;
    private TextView tv_navigation_synchronization;
    private Typeface tfCondensed, fzlt, tfMediumCondAlt, ltqh;
    private RelativeLayout mSportLayout;
    // private HomepageCircleView sport_process_wheel;
    private TestProgressView sport_process_view;
    private Sleepprogreessview sleep_process_view;
    private Heartprogressview heart_process_view;
    private Heartprogressview xieya_process_view;
    private Heartprogressview xieyang_process_view;

    private RelativeLayout mSleepLayout;
    private TextView active_time, sport_step;
    private TextView mTv_weather, sleep_tv_weather, heart_tv_weather;
    private TextView mean_heart_text, highest_heart_text, minimum_heart_text;
    private LinearLayout heart_time_ll;
    private TextView heart_num, measuringtime_tv, measuringtime;
    private TextView sleep_curdatetv, heart_curdatetv;
    private ImageView mWeather_icon, sleep_weather_icon, heart_weather_icon;
    private ImageView sleep_data_bt_downturning, sleep_data_bt_upturning;
    private ImageView heart_data_bt_downturning, heart_data_bt_upturning;
    private ImageButton sport_settarget;
    // 日期处理相关****************************
    private TextView curdatetv;
    private String curtime_str, change_date, uptime_str, select_monthstr, select_daystr;
    private int adayallstep = 0;// 当前这次同步的步数
    private final int SETCURTIME = 1;
    private final int UPDATEDATE = 2;
    private final int TIMEUP = 3;
    private final int TIMEUPDAY = 4;
    private final int MESSAGE_UPDATE = 5;
    private final int GETADAYDATA = 6;
    private final int SETNONETWORK = 7;
    private final int UPUERRUNINFO = 8;
    private final int UPUERSLEEPINFO = 9;
    private final int GETUERGOALINFO = 10;
    private final int UPUERHEARTINFO = 11;
    private final int SNYBTDATAOK = 12;
    private final int CLEARSPORT = 13;
    private final int CLEARSLEEP = 14;
    private final int CLEARHEART = 15;
    private final int SNYBTDATAFAIL = 16;
    private final int BBBSNYBTDATAFAIL = 17;
    private final int UPBloodpressure_INFO = 18;
    private final int UPOxygen_INFO = 19;
    private final int CLEARHEARTUPOxygen_INFO = 20;
    private final int CLEARHEARTUPBloodpressure_INFO = 21;

    private final int SETCURTIMEFORHEART = 23;


    private SharedPreferences datePreferences;
    private double getstep_double;
    // *********圆环运动数据/睡眠数据同步显示相关
    boolean running;
    private TextView allstepnumber, sport_percent, mileage, calorie, sleepqaulity, sleep_qaulity, sleeptime_tv,
            lightsleeptime, deepsleeptime, allsleeptime, setgoalsteptv;
    private TextView calorie_up, active_time_up, mileage_up, heart_text1, heart_text2, heart_text3, sleep_text1,
            sleep_text2, sleep_text3, tv_bo, tv_bp_min, tv_bp_max;
    private ArrayList<SleepData> arrSleepDataDay = null;
    private ArrayList<SleepData> arrNetworkSleepMb;
    TextView Bloodpressure_num, show_blood;
    private PullToRefreshScrollView mPullScrollView;
    private ScrollView mScrollView;
    // private WheelIndicatorView wheelSportView, wheelSleepView;
    private View homeView, convertView;
    private ImageView data_bt_downturning, data_bt_upturning, iv_bo, iv_bp;

    private SimpleDateFormat mDateFormat = Utils.setSimpleDateFormat("MM-dd HH:mm");
    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format = Utils.setSimpleDateFormat("yyyy-MM-dd HH");
    //    private SimpleDateFormat mSimpleDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
    private LinearLayout mLinearLayoutSport;
    private LinearLayout mLinearLayoutSleep;
    // 悬浮菜单导航
    private FloatingActionButton fab;
    private FloatingActionButton fab_look;
    private FloatingActionButton fab_blue_device;
    private FloatingActionButton fab_modify;
    // 标识FloatingActionBar的打开状态
    private boolean FAB_STATE = true;
    private BluetoothDevice mRemoteDevice;
    private TextView deviceName;

    //经纬度、城市 定位类
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mClientOption = null;

    private MyBroadcast mbroadcast = null;
    //    private static final int UPDATE_TIME = 2 * 3600 * 1000;
    private static final int UPDATE_TIME = 600 * 1000;  // todo  --- 1800 * 1000;   300
    private boolean isInit = true;
    // private MyBroadcast mbroadcast = null;
    private Context mContext = null;

    public static boolean isService = false;
    private String mid = "";
    private MainService service = null;

    private TextView home_distance_number, home_pace_tv, home_time_tv, home_kal_tv, home_altitude_tv, home_run_text, home_distance_number_up, home_pace_tv_up, home_kal_tv_up, home_altitude_tv_up, home_run_text_up;

    //运动模式新增
    private ImageButton bt_fragment_home_run_start, ibt_motionsetting, ibt_sporthistory;


    private double shallowSleep = 0;
    private double deepSleep = 0;
    private double sleepOverallTime = 0;
    private double lightTime = 0;

    public static boolean isReceiveWhichData = false;
    public static boolean isNetwork = false;
    private static boolean isExecuteRefresh = true;
    private int goalstepcount;

    boolean isFirstReadHelp = false;
    int mycount = 0;
    int mycount_ERROR = 0;
    int mycount_xieyang = 0;
    private String hourString = "";
    private String spotString = "";
    private String riceString = "";
    private String calorieString = "";

    private DBHelper db = null;
    private PopupWindow mPopupWindow;

//  public    Dialog dialog;
    // private boolean isStepInterface = true;
   /* private static int[] dengjiimages = new int[]{R.drawable.w0, R.drawable.w1, R.drawable.w2, R.drawable.w3,
            R.drawable.w4, R.drawable.w5, R.drawable.w6, R.drawable.w7, R.drawable.w8, R.drawable.w9, R.drawable.w10,
            R.drawable.w11, R.drawable.w12, R.drawable.w13, R.drawable.w14, R.drawable.w15, R.drawable.w16,
            R.drawable.w17, R.drawable.w18, R.drawable.w19, R.drawable.w20, R.drawable.w21, R.drawable.w22,
            R.drawable.w23, R.drawable.w24, R.drawable.w25, R.drawable.w26, R.drawable.w27, R.drawable.w28,
            R.drawable.w29, R.drawable.w30, R.drawable.w31, R.drawable.w32, R.drawable.w33, R.drawable.w34,
            R.drawable.w35, R.drawable.w36, R.drawable.w37, R.drawable.w38, R.drawable.w39, R.drawable.w40,
            R.drawable.w41, R.drawable.w42, R.drawable.w43, R.drawable.w44, R.drawable.w45, R.drawable.w46,
            R.drawable.w47};*/

  /*  private static int[] dengjiimages = new int[]{R.drawable.p0, R.drawable.p1, R.drawable.p2, R.drawable.p3,
            R.drawable.p4, R.drawable.p5, R.drawable.p6, R.drawable.p7, R.drawable.p8, R.drawable.p9, R.drawable.p10,
            R.drawable.p11, R.drawable.p12, R.drawable.p13, R.drawable.p14, R.drawable.p15, R.drawable.p16,
            R.drawable.p17, R.drawable.p18, R.drawable.p19, R.drawable.p20, R.drawable.p21, R.drawable.p22,
            R.drawable.p23, R.drawable.p24, R.drawable.p25, R.drawable.p26, R.drawable.p27, R.drawable.p28,
            R.drawable.p29, R.drawable.p30, R.drawable.p31, R.drawable.p32, R.drawable.p33, R.drawable.p34,
            R.drawable.p35, R.drawable.p36, R.drawable.p37, R.drawable.p38, R.drawable.p39, R.drawable.p40,
            R.drawable.p41, R.drawable.p42, R.drawable.p43, R.drawable.p44, R.drawable.p45, R.drawable.p46,
            R.drawable.p47, R.drawable.p48, R.drawable.p49, R.drawable.p50};*/

    private static int[] dengjiimages = new int[]{ R.drawable.newp1,R.drawable.newp0, R.drawable.newp2, R.drawable.newp3,
           };

    private Toast toast = null;
    private ArrayList<HearData> heartDataList;

//    private int needGetSportDayNum = 0; //需要取计步的天数
//    private int needGetSleepDayNum = 0; //需要取睡眠的天数
//    private int needGetHeartDayNum = 0; //需要取心率的天数

//    public static boolean isSyncEnd = false;

    private long syncStartTime = 0;

    private RelativeLayout llWeatherStep, llWeatherSleep, llWeatherHeart, llWeatherXueya, llWeatherXueyang; // 天气的总体布局  // LinearLayout    RelativeLayout

    private LoadingDialog loadingDialog = null;

    private boolean isStartBaidu = false;

    private boolean isRunning = false;
    //  // http://imtt.dd.qq.com/16891/97BDD85FF66FDD9E47A9ECF21904BD15.apk?fsname=com.kct.fundo.btnotification_V1.1.7_123.apk&amp;csr=1bbd
    public static String commentUrl = "https://play.google.com/store/apps/details?id=com.kct.fundo.btnotification&rdid=com.kct.fundo.btnotification";

    private Handler handler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case TJ://todo 页面统计
                    String st = msg.obj.toString();
                    Log.e("a", "----------统计" + st + "-----------");
                    break;
                case 22:
                    if (null != loadingDialog && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                    break;

                case SETCURTIME:  // 设置当前的日期
                    curtime_str = (String) msg.obj;  // 当前的日期
                    setCurDate();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentRunDB();
                            judgmentSleepDB();
                            judgmentHealthDB();
                            judgmentBloodpressureDB();// 从本地数据库获取血压数据
                            judgmentOxygenDB();
                        }/////
                    }).start();

//                    judgmentRunDB();
//                    judgmentSleepDB();
//                    judgmentHealthDB();
//                    judgmentBloodpressureDB();// 从本地数据库获取血压数据
//                    judgmentOxygenDB();
                    break;

                case SETCURTIMEFORHEART:  // TODO --- 实时心率跳转到当前的日期
                    curtime_str = (String) msg.obj;  // 当前的日期
                    setCurDate();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentHealthDB();
                        }/////
                    }).start();


                    break;

                case UPDATEDATE: {
                    String select_date = (String) msg.obj;
                    setCurDate2(select_date);

                    setSportData(0, 0, 0, 0);
                    setHeartData("0", "");
                    ArrayList<SleepData> arrS = new ArrayList<SleepData>();
//                    updateSleep(arrS);     //TODO  ---- 注释  20170717 1550

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentRunDB();
                            judgmentSleepDB();
                            judgmentHealthDB();
                            judgmentBloodpressureDB();// 从本地数据库获取血压数据
                            judgmentOxygenDB();
                        }/////
                    }).start();

//                    judgmentRunDB();
//                    judgmentSleepDB();
//                    judgmentHealthDB();
//                    judgmentBloodpressureDB();// 从本地数据库获取血压数据
//                    judgmentOxygenDB();
                }
                break;
                case TIMEUP:
                    Toast.makeText(getActivity(), R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
                    break;
                case TIMEUPDAY:
                    String up_date = (String) msg.obj;
                    setCurDate2(up_date);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentRunDB();
                            judgmentSleepDB();
                            judgmentHealthDB();
                            judgmentBloodpressureDB();// 从本地数据库获取血压数据
                            judgmentOxygenDB();
                        }/////
                    }).start();

//                    judgmentRunDB();
//                    judgmentSleepDB();
//                    judgmentHealthDB();
//                    judgmentBloodpressureDB();// 从本地数据库获取血压数据
//                    judgmentOxygenDB();
                    break;

                case GETADAYDATA:
                    String RunReturnStr = (String) msg.obj;
                    if (RunReturnStr != null) {
                        dealDayDatas(RunReturnStr);
                        // Log.e("", "接收到的数据 = "+RunReturnStr);
                    }
                    break;
                case GETUERGOALINFO:
                    String returnInfo = (String) msg.obj;
                    // Log.e(TAG, "获取步数的返回returnInfo ="+returnInfo);
                    try {
                        JSONObject myjObject = new JSONObject(returnInfo);
                        if ("0".equals(myjObject.getString("result"))) {
                            String goalstepcountstr = myjObject.getString("goal");
                            if (!goalstepcountstr.equals("")) {
                                goalstepcount = Integer.valueOf(goalstepcountstr).intValue();
                            }
                            // Log.e(TAG, "获取的目标步数：="+goalstepcount);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case SETNONETWORK: {
                    setSportData(0, 0, 0, 0);
                    setHeartData("0", "");
                    ArrayList<SleepData> arrS = new ArrayList<SleepData>();
                    updateSleep(arrS);
                }
                break;

                case UPUERRUNINFO:   // 更新计步数据
                    float[] f = (float[]) msg.obj;
                    setSportData((int) f[0], f[1], f[2], f[3]);
                    break;

                case UPUERSLEEPINFO:    // 手表睡眠数据同步成功时更新本地的睡眠数据
                    ArrayList<SleepData> s = (ArrayList<SleepData>) msg.obj;
                    updateSleep(s);
                    break;
                case UPUERHEARTINFO:// 更新心率数据
                    mean_heart_text.setText("0");
                    highest_heart_text.setText("0");
                    minimum_heart_text.setText("0");   // todo --- 切换日期时，应该将平均心率，最快，最慢都清0  -
                    setHeartData("0", "");
                    ArrayList<HearData> hd = (ArrayList<HearData>) msg.obj;
                    heartDataList = hd;
                    /*String watch = SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.WATCH);
                    try {
                        String string = hd.get(hd.size() - 1).getBinTime().split(" ")[1];
                        setHeartData(hd.get(hd.size() - 1).getHeartbeat(), string.split(":")[0] + ":" + string.split(":")[1]);
                    } catch (Exception e) {
                        setHeartData(hd.get(hd.size() -1).getHeartbeat(), "");
                    }*/
                    int mean_heart = 0;
                    int highest_heart = 0;
                    int minimum_heart = 11110;
                    long lastTime = 0;
                    String setHeartData = "";
                    String binTime = "";
                    for (int v = 0; v < hd.size(); v++) {
                        highest_heart = Math.max(Integer.parseInt(hd.get(v).getHeartbeat()), highest_heart);
                        minimum_heart = Math.min(Integer.parseInt(hd.get(v).getHeartbeat()), minimum_heart);
                        mean_heart += Integer.parseInt(hd.get(v).getHeartbeat());
                        try {
                            long time = StringUtils.SIMPLE_DATE_FORMAT.parse(hd.get(v).getBinTime()).getTime();
                            if (time > lastTime) {
                                lastTime = time;
                                setHeartData = hd.get(v).getHeartbeat();
                                binTime = hd.get(v).getBinTime();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        String string = binTime.split(" ")[1];
                        setHeartData(setHeartData, string.split(":")[0] + ":" + string.split(":")[1]);
                    } catch (Exception e) {
                        setHeartData(setHeartData, "");
                    }
                    mean_heart = mean_heart / hd.size();
                    mean_heart_text.setText(mean_heart + "");
                    highest_heart_text.setText(highest_heart + "");
                    minimum_heart_text.setText(minimum_heart + "");
                    break;
                case SNYBTDATAOK:   // 同步数据成功
                    try {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.setCancelable(true);
                            loadingDialog.dismiss();
                            loadingDialog = null;
                        }
                        if (handler != null) {
//                                handler.removeCallbacks(runnable); // 
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String mac = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC);
                    if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC).contains(mac)) {
                        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC).equals("")) {
                            SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC, mac);
                        } else {
                            SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC,
                                    SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC) + "nozuomi" + mac);
                        }
                    }
                    //Log.e("ALLMAC", mac + "222");
                    reloadHeartPage(); //todo --- 同步数据成功后，重新加载页面

                    break;
                case SNYBTDATAFAIL:  // 同步失败
                    try {
                        if (null != loadingDialog) {
                            if (loadingDialog.isShowing()) {
                                loadingDialog.setCancelable(true);
                                loadingDialog.dismiss();
                                loadingDialog = null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.userdata_synerror), Toast.LENGTH_SHORT).show();
                    }
                    MainService.getSyncDataNumInService = 0;
                    BTNotificationApplication.isSyncEnd = true;
                    break;

                case CLEARSPORT:  // 清除
                    setSportData(0, 0, 0, 0);
                    break;

                case UPBloodpressure_INFO: //更新血压数据
                    if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
                        String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                        ArrayList<Bloodpressure> aa = (ArrayList<Bloodpressure>) msg.obj;
                        Collections.sort(aa);
                        baohe_show.setText(aa.get(aa.size() - 1).getHeightBlood());
                        baohe_showtwo.setText(aa.get(aa.size() - 1).getMinBlood());
                        baohe_showthere.setText(((Integer.valueOf(aa.get(aa.size() - 1).getHeightBlood()) - Integer.valueOf(aa.get(aa.size() - 1).getMinBlood()))) / 3 + Integer.valueOf(aa.get(aa.size() - 1).getMinBlood()) + "");
                        /*if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya")) {
                            int    bmycount_xieyang = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya");
                            counts.setText(getResources().getString(R.string.Measurement_times)+bmycount_xieyang+"");
                            xieya_process_view.setMaxCount(200);
                            if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya_err")) {
                           int      dmycount_ERROR = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya_err");
                                Log.e("mycount_ERROR",dmycount_ERROR+"");
                                Log.e("mycount_ERROR",bmycount_xieyang+"");
                                xieya_number.setText((int)((1-(double)(dmycount_ERROR)/(double)(bmycount_xieyang))*100)+"");//正确率=1-错误率
                                if(bmycount_xieyang==1&&dmycount_ERROR==1){
                                    xieya_process_view.setCurrentCount(200);
                                }else{
                                    xieya_process_view.setCurrentCount((float)(((1-(double)(dmycount_ERROR)/(double)(bmycount_xieyang))*200)));
                                }

                            }
                            else{
                                xieya_number.setText(100 + "");
                                xieya_process_view.setCurrentCount(200);
                            }
                        }else{
                            counts.setText(getResources().getString(R.string.Measurement_times));
                            xieya_number.setText(100 + "");
                            xieya_process_view.setCurrentCount(200);
                        }*/
                        int bmycount_xieyang = aa.size();
                        if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya_err")) {
                            int dmycount_ERROR = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya_err");
                            Log.e("mycount_ERROR", dmycount_ERROR + "");
                            Log.e("mycount_ERROR", bmycount_xieyang + "");

                            int mzcl = (int) ((1 - (double) (dmycount_ERROR) / (double) (bmycount_xieyang)) * 100);
                            if (mzcl > 0) {
                                xieya_number.setText((int) ((1 - (double) (dmycount_ERROR) / (double) (bmycount_xieyang)) * 100) + "");//正确率=1-错误率   TODO（预防正确率为0）
                            } else {
                                xieya_number.setText("100");//正确率=1-错误率
                            }

//                            xieya_number.setText((int)((1-(double)(dmycount_ERROR)/(double)(bmycount_xieyang))*100)+"");//正确率=1-错误率
                            if (bmycount_xieyang == 1 && dmycount_ERROR == 1) {
                                xieya_process_view.setCurrentCount(200);
                            } else {
                                xieya_process_view.setCurrentCount((float) (((1 - (double) (dmycount_ERROR) / (double) (bmycount_xieyang)) * 200)));
                            }

                        } else {
                            xieya_number.setText(100 + "");
                            xieya_process_view.setCurrentCount(200);
                        }
                        counts.setText(getResources().getString(R.string.Measurement_times) + bmycount_xieyang);
//                        EventBus.getDefault().post(new MessageEvent("updata_Bloodpressure",aa));    99999999999999999999999999999999999    1111111111111111   死循环
                    }
                    break;
                case UPOxygen_INFO: //更新血氧数据
                    if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
                        String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                        ArrayList<Oxygen> aa = (ArrayList<Oxygen>) msg.obj;
                        Collections.sort(aa);
                        xieyang_process_view.setMaxCount(200);
                        xieyang_process_view.setCurrentCount(100 + Float.valueOf(aa.get(aa.size() - 1).getOxygen()));
                        Bloodpressure_num.setText(aa.get(aa.size() - 1).getOxygen());
                        bloodpressurebaohe_showtwo.setText(aa.get(aa.size() - 1).getHeightOxygen());//高
                        bloodpressurebaohe_showthere.setText(aa.get(aa.size() - 1).getMinOxygen());//低
                        bloodpressurebaohe_show.setText((Integer.valueOf(aa.get(aa.size() - 1).getHeightOxygen()) + Integer.valueOf(aa.get(aa.size() - 1).getMinOxygen())) / 2 + "");//平均
                        bloodpressurebaoheconts.setText(getResources().getString(R.string.Measurement_times) + aa.size());
                        /*if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieyang")) {
                         int    bmycount_xieyang = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieyang");
                            bloodpressurebaoheconts.setText(getResources().getString(R.string.Measurement_times)+bmycount_xieyang+"");
                        }else{
                            bloodpressurebaoheconts.setText(getResources().getString(R.string.Measurement_times));
                        }*/
//                        EventBus.getDefault().post(new MessageEvent("updata_XIEYANGpressuretwo",aa));      999999999999999999999999999999999999999999999999
                    }

                    break;
                //设置血氧的默认值
                case CLEARHEARTUPOxygen_INFO:
                    xieyang_process_view.setMaxCount(200);
                    xieyang_process_view.setCurrentCount(0);
                    Bloodpressure_num.setText("0");
                    bloodpressurebaohe_showtwo.setText("0");//高
                    bloodpressurebaohe_showthere.setText("0");//低
                    bloodpressurebaohe_show.setText("0");//平均
                    bloodpressurebaoheconts.setText(getResources().getString(R.string.Measurement_times));//平均
                    break;
                //设置血压默认值
                case CLEARHEARTUPBloodpressure_INFO:
                    baohe_show.setText("0");
                    baohe_showtwo.setText("0");
                    baohe_showthere.setText("0");
                    counts.setText(getResources().getString(R.string.Measurement_times));
                    //设置百分比
                    xieya_number.setText(0 + "");
                    xieya_process_view.setMaxCount(200);
                    xieya_process_view.setCurrentCount(0);
                    break;


                case CLEARSLEEP:
                    ArrayList<SleepData> arrS = new ArrayList<SleepData>();
                    updateSleep(arrS);
                    break;
                case CLEARHEART:
                    mean_heart_text.setText("0");
                    highest_heart_text.setText("0");
                    minimum_heart_text.setText("0");
                    setHeartData("0", "");
                    break;
                case 1007:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.quanma_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.quanma));
                    }
                    home_run_text.setText(getResources().getString(R.string.sporttext_quanma));
                    updateSportDate("7");
                    break;
                case 1006:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.banma_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.banma));
                    }
                    home_run_text.setText(getResources().getString(R.string.sporttext_banma));
                    updateSportDate("6");
                    break;

                case 1005:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.yuyepao_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.yuyepao));
                    }
                    home_run_text.setText(getResources().getString(R.string.sporttext_yueyepao));
                    updateSportDate("5");
                    break;
                case 1004:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.dengshan_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.dengshan));
                    }

                    home_run_text.setText(getResources().getString(R.string.sporttext_dengshan));
                    updateSportDate("4");
                    break;

                case 1003:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.shineipao_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.shineipao));
                    }

                    home_run_text.setText(getResources().getString(R.string.sporttext_shineipao));
                    updateSportDate("3");
                    break;

                case 1002:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.huwaipaoyundong_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.huwaipaoyundong));
                    }
                    home_run_text.setText(getResources().getString(R.string.sporttext_huwaipao));
                    updateSportDate("2");
                    break;
                case 1001:
                    if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.jianzou_white));
                    } else {//黑色主题
                        tv_sport_mode.setBackground(mContext.getResources().getDrawable(
                                R.drawable.jianzou));
                    }
                    home_run_text.setText(getResources().getString(R.string.sporttext_jianzou));
                    updateSportDate("1");
                    break;

                // TODO ---- 用Volley发请求时，在此添加回调
                default:
                    break;
            }
            return false;
        }
    });



    /**-----------------------------------------------------------------------**/
    @NonNull
    private String getCurDate() {
        if (Utils.isDe()) {
            return dateInversion(curdatetv.getText().toString());
        } else {
            return curdatetv.getText().toString();
        }
    }

    private void setCurDate2(String downtime_str) {
        if (Utils.isDe()) {
            downtime_str = Utils.dateInversion(downtime_str);
        }
        curdatetv.setText(downtime_str);
        sleep_curdatetv.setText(downtime_str);
        heart_curdatetv.setText(downtime_str);
        OXY_curdatetv.setText(downtime_str);
        bloodpressure_curdatetv.setText(downtime_str);
    }

    private void setCurDate() {
        setCurDate2(curtime_str);
    }

    /**-----------------------------------------------------------------------**/

    // 定时器
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != loadingDialog) {
                if (System.currentTimeMillis() - syncStartTime > 60 * 1000) {  //  90 * 1000
                    Message msg = handler.obtainMessage(SNYBTDATAFAIL);  // 数据同步失败,稍后重试
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    };

    private ImageButton ibt_history;
    private GpsPointDetailData gpsPoint;  // todo --- 运动轨迹数据
    private TextView tv_sport_mode;
    private HTTPController hc;

    /**
     * 返回根据title参数创建的fragment
     */
    public static HomeFragment newInstance(String title) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    private void showDialog() {  //todo ---- 还需要考虑 重新设置 目标后，应该将  isShowAlertDialog 置为 false
        final android.app.AlertDialog myDialog;
//        if(!isShowAlertDialog) {//是否显示过
        myDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        myDialog.show();
//            isShowAlertDialog = true;
        myDialog.getWindow().setContentView(R.layout.user_comment_dialog);
        myDialog.setCancelable(false);

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("de")) {  // 德语改小字体
            TextView comment1 = (TextView) myDialog.getWindow().findViewById(R.id.tv_comment1);
            comment1.setTextSize(12);
            TextView comment2 = (TextView) myDialog.getWindow().findViewById(R.id.tv_comment2);
            comment2.setTextSize(12);
            TextView comment3 = (TextView) myDialog.getWindow().findViewById(R.id.tv_comment3);
            comment3.setTextSize(12);
        }

        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        myDialog.getWindow().setBackgroundDrawable(dw);
        myDialog.getWindow()
                .findViewById(R.id.makesure_btn)  //           makesure_btn    cancel_btn
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> mList = MarketUtils.queryInstalledMarketPkgs(BTNotificationApplication.getInstance());
                        // com.android.vending  com.tencent.android.qqdownloader
                        if (Utils.getLanguage().equals("zh")) { //中文环境跳转到应用宝
                            boolean isInstalled = false;
                            for (String mPkg : mList) {
                                if (mPkg.equals("com.tencent.android.qqdownloader")) {
                                    isInstalled = true;
                                    break;
                                }
                            }
                            if (isInstalled) { // 安装了应用宝    @string/user_comment_1
                                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "1");//     0：未评论 1：已评论
                                MarketUtils.launchAppDetail("com.kct.fundo.btnotification", "com.tencent.android.qqdownloader");
                            } else { // 未安装引导用户安装
                                Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.user_comment_5), Toast.LENGTH_SHORT).show();  // 请先安装应用宝
                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse(commentUrl));
                                intent.setData(Uri.parse("http://sj.qq.com/"));
                                startActivity(intent);
                            }
                        } else { // 非中文显示 Google Play
                            boolean isInstalled = false;
                            for (String mPkg : mList) {
                                if (mPkg.equals("com.android.vending")) {
                                    isInstalled = true;
                                    break;
                                }
                            }
                            if (isInstalled) { // 安装了google
                                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "1");//     0：未评论 1：已评论
                                MarketUtils.launchAppDetail("com.kct.fundo.btnotification", "com.android.vending");
                            } else { // 未安装引导用户安装      https://play.google.com/store/apps/details?id=com.kct.fundo.btnotification&rdid=com.kct.fundo.btnotification
                                Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.user_comment_6), Toast.LENGTH_SHORT).show();  // "请先安装Google Play"
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
////                                    intent.setData(Uri.parse(commentUrl));
//                                    intent.setData(Uri.parse("https://play.google.com/"));
//                                    startActivity(intent);
                            }
                        }
                        myDialog.dismiss();
                    }
                });
        myDialog.getWindow()
                .findViewById(R.id.cancel_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
//        dialog= CustomProgress.show(getActivity(), getString(R.string.userdata_synchronize), null);
        mContext = this.getActivity();
        //Log.e("home", "onCreateView");
        service = MainService.getInstance();
        homeView = inflater.inflate(R.layout.fragment_pull_down_itme, container, false);
        hourString = getActivity().getString(R.string.sleephour); // h
        hourString = "h";

        mid = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID);
        init(); // 初始化下拉刷新及ScrollLayout中各个控件的初始化

        String isUserComment = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT);  //     0：未评论 1：已评论
        String appStartNumStr = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.PAGE_WELCOME_STARTNUM);  //  记录APP启动次数
        if (!StringUtils.isEmpty(appStartNumStr)) {
            int appStartNum = Integer.valueOf(appStartNumStr);
            Log.e("WelcomeTimes", "Times:--Home--" + appStartNum);  //  SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.IS_USER_COMMENT, "1");//     0：未评论 1：已评论
            if ((appStartNum >= 15 && StringUtils.isEmpty(isUserComment)) || (appStartNum >= 15 && !StringUtils.isEmpty(isUserComment) && isUserComment.equals("0"))) {        // appStartNum >= 15 &&  BTNotificationApplication.isNeedComment
                showDialog();
            }
        }

        DateInit();// 日期的处理及显示选中当天的运动及睡眠数据

        OnekeyShare.isShowShare = true;   // todo --- 页面初始化时

        return homeView;
    }

    /**
     * 广告
     */
    private void initGuangGao() {
        try {

            String json = BTNotificationApplication.getInstance().getGuangGaoBean();
            JSONObject jo = new JSONObject(json);
            int code = jo.getInt("code");
            if(code == 0) {
                JSONArray ja = jo.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jsonObject = ja.getJSONObject(i);
                    if (jsonObject.has("location1")) {
                        JSONObject location1 = jsonObject.getJSONObject("location1");
                        GuangGaoBean locationBean1 = new Gson().fromJson(location1.toString(), GuangGaoBean.class);
                        int operationPosition = locationBean1.getOperationPosition();//todo 页面位置：0：开屏，1：主页位置1；2：主页位置2
                        String status = locationBean1.getStatus();
                        String detailPageUrl = locationBean1.getDetailPageUrl();
                        String pageEntry = locationBean1.getPageEntry();
                        int id = locationBean1.getId();
                        if (status.equals("1")) {//todo 状态开启时去判断
                            if (operationPosition == 1) {  //todo 设置图标
                                //todo mobileSystem投放手机系统 0:所有，1：安卓， 2：ios
                                int mobileSystem = locationBean1.getMobileSystem();
                                //todo country投放国家：0：所有，1：国内，2：国外
                                int country = locationBean1.getCountry();
                                if ((mobileSystem == 0 || mobileSystem == 1) && (country == 0 || country == 1)) {
                                    if (pageEntry != null) {
                                        RequestOptions options = new RequestOptions().dontAnimate();
                                        Glide.with(this).load(pageEntry).apply(options).into(ivCenter);

//                                        Glide.with(this).load(pageEntry).dontAnimate().into(ivCenter);
                                        fl_guanggao.setVisibility(View.VISIBLE);
                                    }
									 centerUrl = detailPageUrl;
                                    centerPageId = id;
                                   
                                }
                            }
                        }

                    }else if(jsonObject.has("location2")){
                        JSONObject location2 = jsonObject.getJSONObject("location2");
                        GuangGaoBean locationBean2 = new Gson().fromJson(location2.toString(), GuangGaoBean.class);
                        int operationPosition = locationBean2.getOperationPosition();//todo 页面位置：0：开屏，1：主页位置1；2：主页位置2
                        String status = locationBean2.getStatus();
                        String detailPageUrl = locationBean2.getDetailPageUrl();
                        String pageEntry = locationBean2.getPageEntry();
                        int id = locationBean2.getId();
                        if(status.equals("1")) {
                            if (operationPosition == 2) {
                                //todo mobileSystem投放手机系统 0:所有，1：安卓， 2：ios
                                int mobileSystem = locationBean2.getMobileSystem();
                                //todo country投放国家：0：所有，1：国内，2：国外
                                int country = locationBean2.getCountry();
                                if ((mobileSystem == 0 || mobileSystem == 1) && (country == 0 || country == 1)) {
                                    btnUrl = detailPageUrl;
                                    btnPageId = id;
                                    if (pageEntry != null) {
                                        RequestOptions options = new RequestOptions().dontAnimate().placeholder(R.drawable.pop_img_default);
                                        Glide.with(this).load(pageEntry).apply(options).into(ivGuanggao);

//                                        Glide.with(this).load(pageEntry).dontAnimate().placeholder(R.drawable.pop_img_default).into(ivGuanggao);
                                        ivGuanggao.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    private void reloadHeartPage() {
        /*if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.DEFAULT_HEART_RATE, "0").equals("1")) {   //todo ---  条件判断 是否显示心率页面   现改为默认显示心率页面
            views.clear();
            views.add(v_newsport);
            views.add(convertView);
            views.add(v_sleep);
            views.add(v_heart_rate);  //之前注释掉了
            views.add(v_Oxygen);  //血氧
            views.add(v_Bloodpressure);  //
        }*/
        views.clear();
        views.add(v_newsport);
        views.add(convertView);
        //views.add(v_sleep);
        /*views.add(v_heart_rate);
        views.add(v_Oxygen);
        views.add(v_Bloodpressure);*/
        if (ISSYNWATCHINFO) {
            if (SLEEP) {
                views.add(v_sleep);
            }
            if (HEART) {
                views.add(v_heart_rate);
            }

            if (BLOOD_PRESSURE) {
                views.add(v_Oxygen);    // TODO  ----血压界面？
            }

            if (BLOOD_OXYGEN) {
                views.add(v_Bloodpressure);   // TODO  ----血氧界面？
            }
        } else {
            views.add(v_sleep);
            views.add(v_heart_rate);
            views.add(v_Oxygen);
            views.add(v_Bloodpressure);
        }
        int curItem = vp.getCurrentItem();  // 2
        if (curItem >= views.size()) {
            curItem = 1;
        }
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        vp.setCurrentItem(curItem);
    }

    private void init() {
        if (db == null) {
            db = DBHelper.getInstance(mContext);
        }

       /* if(MainService.getInstance().getState() != 3){
            getActivity().sendBroadcast(new Intent().setAction(MainService.ACTION_BLEDISCONNECTED));
        }*/
        Query query = null;
        List<GpsPointDetailData> gpsList = db.getGpsPointDetailDao().queryBuilder().orderDesc(GpsPointDetailDao.Properties.Mile).list();
        if (gpsList != null && gpsList.size() > 0) {
            gpsPoint = gpsList.get(0);  //TODO--- 将本地数据库中的 第一条数据 赋值给 gpsPoint
        }

        mPullScrollView = (PullToRefreshScrollView) homeView.findViewById(R.id.prsv_home_refresh_head);
        // mPullScrollView.doPullRefreshing(true, 300);
        mPullScrollView.setOnRefreshListener(new RefreshListener());
        mPullScrollView.setPullRefreshEnabled(false);
        // Log.e("homepgage", "prosess running here!4");
        mScrollView = mPullScrollView.getRefreshableView();
        mScrollView.setVerticalScrollBarEnabled(false);

        tv_sport_mode = (TextView) homeView.findViewById(R.id.tv_sport_mode);  // 默认隐藏的 ，不知道干啥的 ？？？
        tv_sport_mode.setOnClickListener(this);

        cb_navigation_sport = (TextView) homeView.findViewById(R.id.cb_navigation_sport);    // 计步
        cb_navigation_sleep = (TextView) homeView.findViewById(R.id.cb_navigation_sleep);    // 睡眠  ---- 顶部导航标题
        cb_navigation_heart = (TextView) homeView.findViewById(R.id.cb_navigation_heart_rate); // 运动 （手机运动模式）

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("zh")) {  // en
            cb_navigation_sport.setTextSize(16); // R.dimen.home_title_text_n
            cb_navigation_sleep.setTextSize(16);
            cb_navigation_heart.setTextSize(16);
        } else {
            cb_navigation_sport.setTextSize(12); // R.dimen.menu_my_item_small
            cb_navigation_sleep.setTextSize(12);
            cb_navigation_heart.setTextSize(12);
        }

        cb_navigation_heart.setOnClickListener(this);
        cb_navigation_sleep.setOnClickListener(this);
        cb_navigation_heart.setOnClickListener(this);

        ib_navigation_share = (ImageButton) homeView.findViewById(R.id.ib_navigation_share);  // 分享
        tv_navigation_synchronization = (TextView) homeView.findViewById(R.id.tv_navigation_synchronization);   // 同步按钮（同步蓝牙数据）

        ib_navigation_share.setOnClickListener(this);
        tv_navigation_synchronization.setOnClickListener(this);

        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.homesport_sleep_fragment, null);  //计步页面的布局
        v_sleep = LayoutInflater.from(getActivity()).inflate(R.layout.home_sleep_fragment, null);           //睡眠页面的布局

        tfCondensed = BTNotificationApplication.getInstance().akzidenzGroteskLightCond;
        tfMediumCondAlt = BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt;
        fzlt = BTNotificationApplication.getInstance().lanTingBoldBlackTypeface;
        ltqh = BTNotificationApplication.getInstance().lanTingThinBlackTypeface;
//        cb_navigation_sport.setTypeface(fzlt);
//        cb_navigation_sleep.setTypeface(fzlt);
//        cb_navigation_heart.setTypeface(fzlt);
        // 计步界面
        mSportLayout = (RelativeLayout) convertView.findViewById(R.id.sport_fragment);  //TODO -- 通过convertView 找（convertView中还包含运动模式页面）

       /* mSportLayout.setOnClickListener(new OnClickListener() {   // 在 计步页面点击时，进入大数据分析页面   ---- 屏蔽掉
            @Override
            public void onClick(View v) {
                *//*Intent intent = new Intent(getActivity(), EverydayDataActivity.class);
                intent.putExtra("type", "movement");
				// intent.putParcelableArrayListExtra("runArr", arrRunDataDay);
				// intent.putParcelableArrayListExtra("sleepArr",
				// arrSleepDataDay);
				intent.putExtra("date", curdatetv.getText().toString());
				getActivity().startActivity(intent);*//*
                Message message = new Message();
                message.what = 7;
                message.obj = 0;
                MainActivity.mMainActivity.myHandler.sendMessage(message);
            }
        });*/

        sport_settarget = (ImageButton) convertView.findViewById(R.id.sport_settarget);  //TODO ---- 设置计步目标
        sport_settarget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initTarget();
                initAlertDialog(pickers);
            }
        });
        // sport_process_wheel = (HomepageCircleView)
        // convertView.findViewById(R.id.sport_process_circle);
        sport_process_view = (TestProgressView) convertView.findViewById(R.id.test_progress); // 计步圆环

        curdatetv = (TextView) convertView.findViewById(R.id.curdate_tv);   // 当前日期
        curdatetv.setTypeface(tfCondensed);

        data_bt_downturning = (ImageView) convertView.findViewById(R.id.data_bt_downturning);  // 减
        data_bt_downturning.setOnClickListener(this);
        data_bt_upturning = (ImageView) convertView.findViewById(R.id.data_bt_upturning);       // 加
        data_bt_upturning.setOnClickListener(this);
        sport_percent = (TextView) convertView.findViewById(R.id.complete_percent_tv);
        sport_step = (TextView) convertView.findViewById(R.id.sport_step_tv);  // 步数
        sport_step.setTypeface(ltqh);

        calorie = (TextView) convertView.findViewById(R.id.calorie_tv);  // 卡路里
        active_time = (TextView) convertView.findViewById(R.id.active_time_tv); // 活跃时间
        mileage = (TextView) convertView.findViewById(R.id.mileage_tv);

        calorie_up = (TextView) convertView.findViewById(R.id.calorie_tv_up);
        active_time_up = (TextView) convertView.findViewById(R.id.active_time_tv_up);
        mileage_up = (TextView) convertView.findViewById(R.id.mileage_tv_up);

        if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
            calorie_up.setText(getActivity().getString(R.string.unit_kj));
            mileage_up.setText(getActivity().getString(R.string.unit_mi));
        }

        calorie.setTypeface(tfCondensed);
        active_time.setTypeface(tfCondensed);
        mileage.setTypeface(tfCondensed);
//        sport_percent.setTypeface(fzlt);
//        calorie_up.setTypeface(fzlt);
//        active_time_up.setTypeface(fzlt);
//        mileage_up.setTypeface(fzlt);
//        calorie.setTypeface(tfCondensed);
//        active_time.setTypeface(tfCondensed);
//        mileage.setTypeface(tfCondensed);

        mTv_weather = (TextView) convertView.findViewById(R.id.weather_tv);  // 天气相关
//        mTv_weather.setTypeface(fzlt);
        mWeather_icon = (ImageView) convertView.findViewById(R.id.weather_icon);

        // 睡眠界面
        mSleepLayout = (RelativeLayout) v_sleep.findViewById(R.id.sleep_fragment);

       /* mSleepLayout.setOnClickListener(new OnClickListener() {   // 点击圆环不可进入分析页面
            @Override
            public void onClick(View v) {
                *//*Intent intent = new Intent(getActivity(), EverydayDataActivity.class);
                intent.putExtra("type", "sleep");
				// intent.putParcelableArrayListExtra("runArr", arrRunDataDay);
				// intent.putParcelableArrayListExtra("sleepArr",
				// arrSleepDataDay);
				intent.putExtra("date", curdatetv.getText().toString());
				getActivity().startActivity(intent);*//*
                Message message = new Message();
                message.what = 7;
                message.obj = 1;
                MainActivity.mMainActivity.myHandler.sendMessage(message);
            }
        });*/

        sleep_text1 = (TextView) v_sleep.findViewById(R.id.sleep_text1);
        sleep_text2 = (TextView) v_sleep.findViewById(R.id.sleep_text2);
        sleep_text3 = (TextView) v_sleep.findViewById(R.id.sleep_text3);
//        sleep_text1.setTypeface(fzlt);
//        sleep_text2.setTypeface(fzlt);
//        sleep_text3.setTypeface(fzlt);

        sleep_process_view = (Sleepprogreessview) v_sleep.findViewById(R.id.sleep_progress);  // 睡眠圆环赋值
        sleep_curdatetv = (TextView) v_sleep.findViewById(R.id.curdate_tv);
        sleep_data_bt_downturning = (ImageView) v_sleep.findViewById(R.id.data_bt_downturning);
        sleep_data_bt_downturning.setOnClickListener(this);
        sleep_data_bt_upturning = (ImageView) v_sleep.findViewById(R.id.data_bt_upturning);
        sleep_data_bt_upturning.setOnClickListener(this);
        sleepqaulity = (TextView) v_sleep.findViewById(R.id.sleepqaulity_tv);  //sleepqaulity_tv  睡眠质量赋值
        sleep_qaulity = (TextView) v_sleep.findViewById(R.id.sleep_qaulity_tv);
        sleeptime_tv = (TextView) v_sleep.findViewById(R.id.sleeptime_tv);    // 圆环内---睡眠时间值赋值
//        sleepqaulity.setTypeface(fzlt);
//        sleep_qaulity.setTypeface(fzlt);
        sleeptime_tv.setTypeface(ltqh);

        lightsleeptime = (TextView) v_sleep.findViewById(R.id.lightsleeptime_tv);  // 浅睡赋值
        deepsleeptime = (TextView) v_sleep.findViewById(R.id.deepsleeptime_tv);    // 深睡赋值
        allsleeptime = (TextView) v_sleep.findViewById(R.id.allsleeptime_tv);      // 睡眠总时长


        if (languageLx.equals("ru") || languageLx.equals("pt") || languageLx.contains("it") || languageLx.contains("fr") || languageLx.contains("th")) {  // en   tv_stop_time
            sleep_qaulity.setTextSize(10);
            sleepqaulity.setTextSize(10);
            lightsleeptime.setTextSize(20);
            deepsleeptime.setTextSize(20);
            allsleeptime.setTextSize(20);
        }

        sleep_curdatetv.setTypeface(tfCondensed);
        lightsleeptime.setTypeface(tfCondensed);
        deepsleeptime.setTypeface(tfCondensed);
        allsleeptime.setTypeface(tfCondensed);

        sleep_tv_weather = (TextView) v_sleep.findViewById(R.id.weather_tv);
//        sleep_tv_weather.setTypeface(fzlt);
        sleep_weather_icon = (ImageView) v_sleep.findViewById(R.id.weather_icon);

        // 接收HomeFragment广播处理天气
        // if (mbroadcast == null) {
        // mbroadcast = new MyBroadcast();
        // IntentFilter filter = new IntentFilter();
        // filter.addAction("android.intent.action.WEATHER"); //
        // 只有持有相同的action的接受者才能接收此广播
        // mContext.registerReceiver(mbroadcast, filter);
        // }
        // weatherInit();


        // 心率界面 *
        v_heart_rate = LayoutInflater.from(getActivity()).inflate(R.layout.home_heart_rate_fragment, null);


        heart_text1 = (TextView) v_heart_rate.findViewById(R.id.heart_text1);
        heart_text2 = (TextView) v_heart_rate.findViewById(R.id.heart_text2);
        heart_text3 = (TextView) v_heart_rate.findViewById(R.id.heart_text3);
//        heart_text1.setTypeface(fzlt);
//        heart_text2.setTypeface(fzlt);
//        heart_text3.setTypeface(fzlt);

        heart_time_ll = (LinearLayout) v_heart_rate.findViewById(R.id.heart_time_ll);
        heart_process_view = (Heartprogressview) v_heart_rate.findViewById(R.id.heart_progress);
        heart_num = (TextView) v_heart_rate.findViewById(R.id.heart_num);
        measuringtime_tv = (TextView) v_heart_rate.findViewById(R.id.measuringtime_tv);
        measuringtime = (TextView) v_heart_rate.findViewById(R.id.measuring_time_tv);
        heart_curdatetv = (TextView) v_heart_rate.findViewById(R.id.curdate_tv);
        heart_tv_weather = (TextView) v_heart_rate.findViewById(R.id.weather_tv);
//        heart_tv_weather.setTypeface(fzlt);
        mean_heart_text = (TextView) v_heart_rate.findViewById(R.id.mean_heart_text);
        highest_heart_text = (TextView) v_heart_rate.findViewById(R.id.highest_heart_text);
        minimum_heart_text = (TextView) v_heart_rate.findViewById(R.id.minimum_heart_text);

        tv_bo = (TextView) v_heart_rate.findViewById(R.id.tv_bo);
        tv_bp_min = (TextView) v_heart_rate.findViewById(R.id.tv_bp_min);
        tv_bp_max = (TextView) v_heart_rate.findViewById(R.id.tv_bp_max);
        String bo = (String) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.LAST_BO, "");
        String bp_min = (String) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.LAST_BP_MIN, "");
        String bp_max = (String) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.LAST_BP_MAX, "");

        if (!TextUtils.isEmpty(bo)) {
            tv_bo.setText(bo + "%");
        }
        if (!TextUtils.isEmpty(bp_min)) {
            tv_bp_min.setText(bp_min + " mmHg");
        }
        if (!TextUtils.isEmpty(bp_max)) {
            tv_bp_max.setText(bp_max + " mmHg");
        }
        mean_heart_text.setTypeface(tfCondensed);
        highest_heart_text.setTypeface(tfCondensed);
        minimum_heart_text.setTypeface(tfCondensed);

        heart_weather_icon = (ImageView) v_heart_rate.findViewById(R.id.weather_icon);
        heart_data_bt_downturning = (ImageView) v_heart_rate.findViewById(R.id.data_bt_downturning);
        heart_data_bt_upturning = (ImageView) v_heart_rate.findViewById(R.id.data_bt_upturning);
        iv_bo = (ImageView) v_heart_rate.findViewById(R.id.iv_bo);
        iv_bp = (ImageView) v_heart_rate.findViewById(R.id.iv_bp);
        heart_data_bt_downturning.setOnClickListener(this);
        heart_data_bt_upturning.setOnClickListener(this);
        heart_curdatetv.setTypeface(tfCondensed);
        heart_num.setTypeface(ltqh);

        views = new ArrayList<View>();


        /**********************运动主界面**********************/
        v_newsport = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_run_main, null);   // TODO  ------ 运动（模式）页面的布局
        home_distance_number = (TextView) v_newsport.findViewById(R.id.home_distance_number);   // 所跑距离取值
        home_pace_tv = (TextView) v_newsport.findViewById(R.id.home_pace_tv);               // 配速取值
        home_time_tv = (TextView) v_newsport.findViewById(R.id.home_time_tv);               // 运动时长取值
        home_kal_tv = (TextView) v_newsport.findViewById(R.id.home_kal_tv);                 // 卡路里取值
        home_altitude_tv = (TextView) v_newsport.findViewById(R.id.home_altitude_tv);   // 海拔取值
        home_run_text = (TextView) v_newsport.findViewById(R.id.home_run_text);

        home_distance_number_up = (TextView) v_newsport.findViewById(R.id.home_distance_number_up);   // 所跑距离单位
        home_pace_tv_up = (TextView) v_newsport.findViewById(R.id.home_pace_text_tv_up);               // 配速单位
        home_kal_tv_up = (TextView) v_newsport.findViewById(R.id.home_kal_text_tv_up);                 // 卡路里单位
        home_altitude_tv_up = (TextView) v_newsport.findViewById(R.id.home_altitude_text_tv_up);       // 海拔单位


        bt_fragment_home_run_start = (ImageButton) v_newsport.findViewById(R.id.bt_fragment_home_run_start);
        if (Utils.getScreenHeight(getActivity()) < 900) {
            ViewGroup.LayoutParams layoutParams = bt_fragment_home_run_start.getLayoutParams();
            layoutParams.height = 140;
            layoutParams.width = 140;
            bt_fragment_home_run_start.setLayoutParams(layoutParams);
        }
        bt_fragment_home_run_start.setOnClickListener(this);
        ibt_motionsetting = (ImageButton) v_newsport.findViewById(R.id.ibt_motionsetting);
        ibt_motionsetting.setOnClickListener(this);    //运动设置
        ibt_history = (ImageButton) v_newsport.findViewById(R.id.ibt_history);
        ibt_history.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//查看历史记录
                Intent intent = new Intent();
                intent.setClass(mContext, SportsHistoryActivity.class);   // 运动历史数据条目页面
                startActivity(intent);
            }
        });


        /***********************血压************************************/

        v_Oxygen = LayoutInflater.from(getActivity()).inflate(R.layout.home_oxygen_fragment, null);
        oxyheart_textone = (TextView) v_Oxygen.findViewById(R.id.oxyheart_text1);
        oxyheart_texttwo = (TextView) v_Oxygen.findViewById(R.id.oxy_text2);
        oxyheart_textthere = (TextView) v_Oxygen.findViewById(R.id.oxy_text3);
        xieya_number = (TextView) v_Oxygen.findViewById(R.id.Oxy_numxieya);
        counts = (TextView) v_Oxygen.findViewById(R.id.oxymeasuring_time_tv);
        showtitle = (TextView) v_Oxygen.findViewById(R.id.Oxy_numxieya_showtitle);
//        oxyheart_textone.setTypeface(fzlt);
//        oxyheart_texttwo.setTypeface(fzlt);
//        oxyheart_textthere.setTypeface(fzlt);
        if (!Utils.isZh(getActivity())) {
            Utils.settingFontsize(oxyheart_textone, 10);
            Utils.settingFontsize(oxyheart_texttwo, 10);
            Utils.settingFontsize(oxyheart_textthere, 10);
        }
        //血氧高低值显示
        baohe_show = (TextView) v_Oxygen.findViewById(R.id.mean_oxy_text);
        xieya_process_view = (Heartprogressview) v_Oxygen.findViewById(R.id.Oxygen_progress);
        baohe_showtwo = (TextView) v_Oxygen.findViewById(R.id.highest_oxy_text);
        baohe_showthere = (TextView) v_Oxygen.findViewById(R.id.minimum_hoxy_text);
        baohe_show.setTypeface(tfCondensed);
        baohe_showtwo.setTypeface(tfCondensed);
        baohe_showthere.setTypeface(tfCondensed);

        //温度
        Oxy_tv_weather = (TextView) v_Oxygen.findViewById(R.id.weather_tv);
//        Oxy_tv_weather.setTypeface(fzlt);
        Oxy_weather_icon = (ImageView) v_Oxygen.findViewById(R.id.weather_icon);

        OXY_curdatetv = (TextView) v_Oxygen.findViewById(R.id.curdate_tv);//日期显示
        OXY_curdatetv.setTypeface(tfCondensed);
        OXYiv_bo = (ImageView) v_Oxygen.findViewById(R.id.data_bt_downturning);//日期点击左边
        OXYiv_bp = (ImageView) v_Oxygen.findViewById(R.id.data_bt_upturning);//日期点击右边
        OXYiv_bo.setOnClickListener(this);
        OXYiv_bp.setOnClickListener(this);

        /***********************血氧************************************/

        v_Bloodpressure = LayoutInflater.from(getActivity()).inflate(R.layout.home_bloodpressure_fragment, null);
        bloodpressureheart_textone = (TextView) v_Bloodpressure.findViewById(R.id.Bloodpressureheart_text1);
        bloodpressureheart_texttwo = (TextView) v_Bloodpressure.findViewById(R.id.Bloodpressure_text2);
        bloodpressureheart_textthere = (TextView) v_Bloodpressure.findViewById(R.id.Bloodpressure_text3);
//        bloodpressureheart_textone.setTypeface(fzlt);
//        bloodpressureheart_texttwo.setTypeface(fzlt);
//        bloodpressureheart_textthere.setTypeface(fzlt);
        Bloodpressure_num = (TextView) v_Bloodpressure.findViewById(R.id.Bloodpressure_num);
        show_blood = (TextView) v_Bloodpressure.findViewById(R.id.show_blood_oxygen_staurstion);
        if (!Utils.isZh(getActivity())) {
            Utils.settingFontsize(bloodpressureheart_textone, 10);
            Utils.settingFontsize(bloodpressureheart_texttwo, 10);
            Utils.settingFontsize(bloodpressureheart_textthere, 10);
        }


        bloodpressurebaohe_show = (TextView) v_Bloodpressure.findViewById(R.id.mean_Bloodpressure_text);
        bloodpressurebaoheconts = (TextView) v_Bloodpressure.findViewById(R.id.Bloodpressuremeasuring_time_tv);
        bloodpressurebaohe_showtwo = (TextView) v_Bloodpressure.findViewById(R.id.highest_Bloodpressure_text);
        bloodpressurebaohe_showthere = (TextView) v_Bloodpressure.findViewById(R.id.minimum_Bloodpressure_text);
        xieyang_process_view = (Heartprogressview) v_Bloodpressure.findViewById(R.id.Bloodpressure_progress);
        bloodpressurebaohe_show.setTypeface(tfCondensed);
        bloodpressurebaohe_showtwo.setTypeface(tfCondensed);
        bloodpressurebaohe_showthere.setTypeface(tfCondensed);

        bloodpressure_tv_weather = (TextView) v_Bloodpressure.findViewById(R.id.weather_tv);
//        bloodpressure_tv_weather.setTypeface(fzlt);
        bloodpressure_weather_icon = (ImageView) v_Bloodpressure.findViewById(R.id.weather_icon);
        bloodpressure_curdatetv = (TextView) v_Bloodpressure.findViewById(R.id.curdate_tv);//日期显示
        bloodpressure_curdatetv.setTypeface(tfCondensed);
        bloodpressureiv_bo = (ImageView) v_Bloodpressure.findViewById(R.id.data_bt_downturning);//日期点击左边
        bloodpressureiv_bp = (ImageView) v_Bloodpressure.findViewById(R.id.data_bt_upturning);//日期点击右边
        String language = (getResources().getConfiguration().locale).getLanguage();

        if (language.endsWith("zh")) {
            bloodpressurebaoheconts.setTextSize(16);
            show_blood.setTextSize(16);
            counts.setTextSize(16);
            showtitle.setTextSize(16);
            measuringtime.setTextSize(16);
        } else if (language.endsWith("en")) {
            bloodpressurebaoheconts.setTextSize(12);
            show_blood.setTextSize(12);
            counts.setTextSize(12);
            showtitle.setTextSize(12);
            measuringtime.setTextSize(12);
        } else if (language.endsWith("ru")) {//俄文
            bloodpressurebaoheconts.setTextSize(10);
            show_blood.setTextSize(10);
            counts.setTextSize(10);
            showtitle.setTextSize(10);
            measuringtime.setTextSize(10);
        } else if (language.endsWith("pt")) {//葡萄牙语 (巴西)
            bloodpressurebaoheconts.setTextSize(10);
            show_blood.setTextSize(10);
            counts.setTextSize(10);
            showtitle.setTextSize(10);
            measuringtime.setTextSize(10);
        } else if (language.endsWith("fr")) {//法语
            bloodpressurebaoheconts.setTextSize(9);
            show_blood.setTextSize(9);
            counts.setTextSize(9);
            showtitle.setTextSize(9);
            measuringtime.setTextSize(10);
        } else if (language.endsWith("de")) {//德语
            bloodpressurebaoheconts.setTextSize(8);
            show_blood.setTextSize(9);
            counts.setTextSize(8);
            showtitle.setTextSize(9);
            measuringtime.setTextSize(10);
        } else if (language.endsWith("es")) {//西班牙
            bloodpressurebaoheconts.setTextSize(8);
            show_blood.setTextSize(10);
            counts.setTextSize(8);
            showtitle.setTextSize(10);
        } else if (language.endsWith("it")) {//意大利
            bloodpressurebaoheconts.setTextSize(8);
            show_blood.setTextSize(10);
            counts.setTextSize(8);
            showtitle.setTextSize(10);
        } else if (language.endsWith("pl")) {//波兰
            bloodpressurebaoheconts.setTextSize(8);
            show_blood.setTextSize(10);
            counts.setTextSize(8);
            showtitle.setTextSize(10);
        } else if (language.endsWith("tr")) { // 土耳其
            show_blood.setTextSize(10);
            bloodpressurebaoheconts.setTextSize(10);
            counts.setTextSize(10);
        }

        /** LG G3 手机是540的,文字压到边了 */
        if (Utils.getScreenWidth(getActivity()) <= 540) {
//        if(Utils.getScreenWidth(getActivity()) < 500){
            bloodpressurebaoheconts.setTextSize(10);
            show_blood.setTextSize(10);
            counts.setTextSize(10);
            showtitle.setTextSize(10);
            measuringtime.setTextSize(10);
        }

        bloodpressureiv_bo.setOnClickListener(this);
        bloodpressureiv_bp.setOnClickListener(this);

        llWeatherStep = (RelativeLayout) convertView.findViewById(R.id.weather_linerlayout);  //todo --   计步页面天气的总体布局   RelativeLayout
        llWeatherStep.setOnClickListener(this);
        llWeatherSleep = (RelativeLayout) v_sleep.findViewById(R.id.sleep_weather_linerlayout);  //todo --   睡眠  LinearLayout
        llWeatherSleep.setOnClickListener(this);
        llWeatherHeart = (RelativeLayout) v_heart_rate.findViewById(R.id.heart_weather_linerlayout);  //todo --   心率
        llWeatherHeart.setOnClickListener(this);
        llWeatherXueya = (RelativeLayout) v_Oxygen.findViewById(R.id.Oxygen_weather_linerlayout);  //todo --   血压
        llWeatherXueya.setOnClickListener(this);
        llWeatherXueyang = (RelativeLayout) v_Bloodpressure.findViewById(R.id.Bloodpressure_weather_linerlayout);  //todo --   血氧
        llWeatherXueyang.setOnClickListener(this);

        views.add(v_newsport);  // 运动模式
        views.add(convertView); // 计步
        //views.add(v_sleep);     // 睡眠
        /*views.add(v_heart_rate); // 心率
        views.add(v_Oxygen); // 血氧
        views.add(v_Bloodpressure);*/
        if (ISSYNWATCHINFO) {
            if (SLEEP) {
                views.add(v_sleep);     // 睡眠
            }
            if (HEART) {
                views.add(v_heart_rate);
            }

            if (BLOOD_PRESSURE) {
                views.add(v_Oxygen);
            }

            if (BLOOD_OXYGEN) {
                views.add(v_Bloodpressure);
            }
        } else {
            views.add(v_sleep);     // 睡眠
            views.add(v_heart_rate); // 心率
            views.add(v_Oxygen); // 血氧
            views.add(v_Bloodpressure);
        }

        v_vp = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_viewpager, null);
        vp = (ViewPager) v_vp.findViewById(R.id.test_vp);
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);

        vp.setCurrentItem(1);
        sport_settarget.setVisibility(View.VISIBLE);
        vp.setOnPageChangeListener(this);

        mScrollView.setFillViewport(true);
        mScrollView.addView(v_vp);

        // 接收HomeFragment广播处理天气
        if (mbroadcast == null) {
            mbroadcast = new MyBroadcast();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.ACTION_WEATHER);
        filter.addAction(MainService.ACTION_MACCHANGE);//
        filter.addAction(MainService.ACTION_SYNFINSH);// 主页面 注册 手表数据同步 成功的 广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(MainService.ACTION_SYNNOTDATA);
        filter.addAction(MainService.ACTION_SYNARTHEART);  //TODO --   实时心率 所有平台
        filter.addAction(MainService.ACTION_SYNARTBP);
        filter.addAction(MainService.ACTION_SYNARTBO);
        filter.addAction(MainService.ACTION_CHANGE_WATCH);

        filter.addAction("android.intent.action.DATE_CHANGED");
        filter.addAction(MainService.ACTION_SYNFINSH_SUCCESS);


        filter.addAction(MainService.ACTION_NO_UNITS);  // 不支持单位设置

        mContext.registerReceiver(mbroadcast, filter);

        weatherInit();     //TODO--- 初始化天气

        //添加圆环点击跳转事件
        sport_process_view.setClickable(true);
        sport_process_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }

                Log.e(TAG, "onClick: 计步圆环");
                startPresentationActivity(0);
            }
        });
        sleep_process_view.setClickable(true);
        sleep_process_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }

                Log.e(TAG, "onClick: 睡眠圆环");
                startPresentationActivity(1);
            }
        });
        heart_process_view.setClickable(true);
        heart_process_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }
                Log.e(TAG, "onClick: 心率圆环");
                startPresentationActivity(2);
            }
        });
        xieya_process_view.setClickable(true);
        xieya_process_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }
                Log.e(TAG, "onClick: 血压圆环");
                startPresentationActivity(3);
            }
        });
        xieyang_process_view.setClickable(true);
        xieyang_process_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }
                Log.e(TAG, "onClick: 血氧圆环");
                startPresentationActivity(4);
            }
        });

        //todo 悬浮图标
        ivGuanggao = homeView.findViewById(R.id.iv_guanggao);
        iv_x = homeView.findViewById(R.id.iv_x);
        ivCenter = homeView.findViewById(R.id.iv_center);
        fl_guanggao = homeView.findViewById(R.id.fl_guanggao);

        //todo 悬浮图标的点击
        ivGuanggao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGuangGao(btnUrl, 1);
            }
        });
        //todo 主页中间广告的点击
        ivCenter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGuangGao(centerUrl, 2);
            }
        });
        //todo 主页中间广告关闭
        iv_x.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fl_guanggao.setVisibility(View.GONE);
            }
        });
        initGuangGao();

    }

    private void clickGuangGao(String url, int i) {
        hc = HTTPController.getInstance();
        hc.open(getActivity());
        int pageId = -1;
        if(i == 1){
            pageId = btnPageId;
        }else if(i == 2){
            pageId  = centerPageId;
            fl_guanggao.setVisibility(View.GONE);
        }
        if (pageId == -1 || TextUtils.isEmpty(url)) return;
        String uid = DeviceUtils.getUniqueId(getActivity());//设备id
        String tjUrl = Constants.FUNDO_UNIFIED_DOMAIN_test + Constants.TJ + "pageId=" + pageId + "&uuid=" + uid + "&mobileSystem=1";
        hc.getNetworkStringData(tjUrl, handler, TJ);
        Uri uri = Uri.parse(url);
        Intent in = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(in);
    }

    private void sethearttitileText(int x) {   //TODO ---  显示心率页面
        switch (x) {
            case 0:
                tv_navigation_synchronization.setVisibility(View.GONE);
                tv_sport_mode.setVisibility(View.VISIBLE);
                tv_navigation_synchronization.setFocusable(false);
                tv_sport_mode.setFocusable(true);

                cb_navigation_sport.setText(getActivity().getString(R.string.sport_text));  // 运动模式页面 --- 运动
                cb_navigation_sleep.setText(getActivity().getString(R.string.steps_text));  // 计步页面   --- 计步
                cb_navigation_heart.setText("");       // 空

                break;
            case 1:
                sport_settarget.setVisibility(View.VISIBLE);
                tv_navigation_synchronization.setVisibility(View.VISIBLE);
                tv_sport_mode.setVisibility(View.GONE);
                tv_navigation_synchronization.setFocusable(true);
                tv_sport_mode.setFocusable(false);

                cb_navigation_sport.setText(getActivity().getString(R.string.steps_text));  // 计步
                if (ISSYNWATCHINFO) {
                    if (SLEEP) {
                        cb_navigation_sleep.setText(getActivity().getString(R.string.data_sleep));  // 睡眠
                    } else {
                        if (HEART) {
                            cb_navigation_sleep.setText(getActivity().getString(R.string.heart_rate));
                        } else {
                            if (BLOOD_PRESSURE) {
                                cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                            } else {
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                                } else {
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        }
                    }
                } else {
                    cb_navigation_sleep.setText(getActivity().getString(R.string.data_sleep));  // 睡眠
                }
                //cb_navigation_sleep.setText(getActivity().getString(R.string.data_sleep));  // 睡眠
                cb_navigation_heart.setText(getActivity().getString(R.string.sport_text)); // 运动
                break;
            case 2:
                sport_settarget.setVisibility(View.INVISIBLE);
                tv_navigation_synchronization.setVisibility(View.VISIBLE);
                tv_sport_mode.setVisibility(View.GONE);
                tv_navigation_synchronization.setFocusable(true);
                tv_sport_mode.setFocusable(false);
                //cb_navigation_sport.setText(getActivity().getString(R.string.data_sleep));
                //cb_navigation_sleep.setText(getActivity().getString(R.string.heart_rate));  // 默认显示心率页面
                if (ISSYNWATCHINFO) {
                    if (SLEEP) {
                        cb_navigation_sport.setText(getActivity().getString(R.string.data_sleep));
                        if (HEART) {
                            cb_navigation_sleep.setText(getActivity().getString(R.string.heart_rate));
                        } else {
                            if (BLOOD_PRESSURE) {
                                cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                            } else {
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                                } else {
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        }
                    } else {
                        if (HEART) {
                            cb_navigation_sport.setText(getActivity().getString(R.string.heart_rate));
                            if (BLOOD_PRESSURE) { //血压
                                cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                            } else {
                                if (BLOOD_OXYGEN) { //血氧
                                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                                } else {
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        } else {
                            if (BLOOD_PRESSURE) { //血压
                                cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                                if (BLOOD_OXYGEN) { //血氧
                                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                                } else {
                                    cb_navigation_sleep.setText("");
                                }
                            } else {
                                if (BLOOD_OXYGEN) { //血氧
                                    cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                                    cb_navigation_sleep.setText("");
                                } else {
                                    cb_navigation_sport.setText("");
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        }
                    }
                } else {
                    cb_navigation_sport.setText(getActivity().getString(R.string.data_sleep));
                    cb_navigation_sleep.setText(getActivity().getString(R.string.heart_rate));  // 心率
                }
                /*if(ISSYNWATCHINFO) {
                    if (HEART) {
                        cb_navigation_sleep.setText(getActivity().getString(R.string.heart_rate));  // 默认显示心率页面
                    } else {
                        if(BLOOD_PRESSURE){  //血压
                            cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                        }else{
                            if(BLOOD_OXYGEN) { //血氧
                                cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                            }else{
                                cb_navigation_sleep.setText("");
                            }
                        }
                    }
                }else {
                    cb_navigation_sleep.setText(getActivity().getString(R.string.heart_rate));  // 心率
                }*/

                cb_navigation_heart.setText(getActivity().getString(R.string.steps_text));   //计步

                break;
            case 3:
                sport_settarget.setVisibility(View.INVISIBLE);
                tv_navigation_synchronization.setVisibility(View.VISIBLE);
                tv_sport_mode.setVisibility(View.GONE);
                tv_navigation_synchronization.setFocusable(true);
                tv_sport_mode.setFocusable(false);
                if (ISSYNWATCHINFO) {
                    if (SLEEP) {
                        cb_navigation_heart.setText(getActivity().getString(R.string.data_sleep));
                        if (HEART) {
                            cb_navigation_sport.setText(getActivity().getString(R.string.heart_rate));
                            if (BLOOD_PRESSURE) {
                                cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                            } else {
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                                } else {
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        } else {
                            if (BLOOD_PRESSURE) {
                                cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                            } else {
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                                    cb_navigation_sleep.setText("");
                                } else {
                                    cb_navigation_sport.setText("");
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        }
                    } else {
                        if (HEART) {
                            cb_navigation_heart.setText(getActivity().getString(R.string.heart_rate));
                            if (BLOOD_PRESSURE) {
                                cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                                } else {
                                    cb_navigation_sleep.setText("");
                                }
                            } else {
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                                    cb_navigation_sleep.setText("");
                                } else {
                                    cb_navigation_sport.setText("");
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        } else {
                            if (BLOOD_PRESSURE) {
                                cb_navigation_heart.setText(getActivity().getString(R.string.blood_pressure));
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                                    cb_navigation_sleep.setText("");
                                } else {
                                    cb_navigation_sport.setText("");
                                    cb_navigation_sleep.setText("");
                                }
                            } else {
                                if (BLOOD_OXYGEN) {
                                    cb_navigation_heart.setText(getActivity().getString(R.string.Blood_oxygen));
                                    cb_navigation_sport.setText("");
                                    cb_navigation_sleep.setText("");
                                } else {
                                    cb_navigation_heart.setText("");
                                    cb_navigation_sport.setText("");
                                    cb_navigation_sleep.setText("");
                                }
                            }
                        }
                    }
                } else {
                    cb_navigation_sport.setText(getActivity().getString(R.string.heart_rate));
                    cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                    cb_navigation_heart.setText(getActivity().getString(R.string.data_sleep));
                }

               /* if(ISSYNWATCHINFO) {
                    if (HEART) {
                        cb_navigation_sport.setText(getActivity().getString(R.string.heart_rate));
                        cb_navigation_heart.setText(getActivity().getString(R.string.data_sleep));
                        if (BLOOD_PRESSURE) {
                            cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                        } else {
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                            } else {
                                cb_navigation_sleep.setText("");
                            }
                        }
                    } else {
                        if (BLOOD_PRESSURE) {
                            cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                            cb_navigation_heart.setText(getActivity().getString(R.string.data_sleep));
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                            } else {
                                cb_navigation_sleep.setText("");
                            }
                        } else {
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                                cb_navigation_heart.setText(getActivity().getString(R.string.data_sleep));
                                cb_navigation_sleep.setText("");
                            } else {
                                cb_navigation_sport.setText(getActivity().getString(R.string.data_sleep));
                                cb_navigation_heart.setText(getActivity().getString(R.string.steps_text));
                                cb_navigation_sleep.setText("");
                            }
                        }
                    }
                }else{
                    cb_navigation_sport.setText(getActivity().getString(R.string.heart_rate));
                    cb_navigation_sleep.setText(getActivity().getString(R.string.blood_pressure));
                    cb_navigation_heart.setText(getActivity().getString(R.string.data_sleep));
                }*/

                break;
            case 4:
                sport_settarget.setVisibility(View.INVISIBLE);
                tv_navigation_synchronization.setVisibility(View.VISIBLE);
                tv_sport_mode.setVisibility(View.GONE);
                tv_navigation_synchronization.setFocusable(true);
                tv_sport_mode.setFocusable(false);
                //cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                //cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                if (ISSYNWATCHINFO) {
                    if (HEART) {
                        cb_navigation_heart.setText(getActivity().getString(R.string.heart_rate));
                        if (BLOOD_PRESSURE) {
                            cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                            } else {
                                cb_navigation_sleep.setText("");
                            }
                        } else {
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                            } else {
                                cb_navigation_sport.setText("");
                            }
                            cb_navigation_sleep.setText("");
                        }
                    } else {
                        if (BLOOD_PRESSURE) {
                            cb_navigation_heart.setText(getActivity().getString(R.string.blood_pressure));
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                            } else {
                                cb_navigation_sport.setText("");
                            }
                            cb_navigation_sleep.setText("");
                        } else {
                            cb_navigation_heart.setText("");
                            if (BLOOD_OXYGEN) {
                                cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                            } else {
                                cb_navigation_sport.setText("");
                            }
                            cb_navigation_sleep.setText("");
                        }
                    }
                } else {
                    cb_navigation_heart.setText(getActivity().getString(R.string.heart_rate));
                    cb_navigation_sport.setText(getActivity().getString(R.string.blood_pressure));
                    cb_navigation_sleep.setText(getActivity().getString(R.string.Blood_oxygen));
                }
                /*if(!Utils.isZh(getActivity())){
                    Utils.settingFontsize(cb_navigation_sport,10);
				Utils.settingFontsize(cb_navigation_sleep,10);}*/
                break;
            case 5:
                sport_settarget.setVisibility(View.INVISIBLE);
                tv_navigation_synchronization.setVisibility(View.VISIBLE);
                tv_sport_mode.setVisibility(View.GONE);
                tv_navigation_synchronization.setFocusable(true);
                tv_sport_mode.setFocusable(false);
                cb_navigation_sport.setText(getActivity().getString(R.string.Blood_oxygen));
                cb_navigation_sleep.setText("");  // 运动
                cb_navigation_heart.setText(getActivity().getString(R.string.blood_pressure)); //
                /*if(!Utils.isZh(getActivity())){
                    Utils.settingFontsize(cb_navigation_sport,10);
                    Utils.settingFontsize(cb_navigation_heart,10);
                }*/
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if (null != event.getMessage()) {
            if (event.getMessage().equals("updata_Bloodpressureone")) {  // todo   --- s实时血压

               /* Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = new SimpleDateFormat("yyyy-MM-dd").format(curDate);
                if(str.equals(curdatetv.getText().toString())){
                if(null!=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)){
                    String myaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                    if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya")) {
                        mycount = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya");
                        mycount++;
                    }else{mycount++;}

                    if(null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya_err")){
                        mycount_ERROR = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya_err");
                            //90<收缩压<140  60<舒张压<90  当前日期对比下
                        if(!baohe_show.getText().toString().equals("0")&&!baohe_showtwo.getText().toString().equals("0")){
                            if(Integer.valueOf(baohe_show.getText().toString())<90||Integer.valueOf(baohe_show.getText().toString())>140
                                    ||Integer.valueOf(baohe_showtwo.getText().toString())<60||Integer.valueOf(baohe_showtwo.getText().toString())>90){
                                mycount_ERROR++;
                                SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya_err", mycount_ERROR);
                            }
                        }
                    }else{
                        if(!baohe_show.getText().toString().equals("0")&&!baohe_showtwo.getText().toString().equals("0")){
                            //90<收缩压<140  60<舒张压<90  当前日期对比下
                            if(Integer.valueOf(baohe_show.getText().toString())<90||Integer.valueOf(baohe_show.getText().toString())>140
                                    ||Integer.valueOf(baohe_showtwo.getText().toString())<60||Integer.valueOf(baohe_showtwo.getText().toString())>90){
                                mycount_ERROR++;
                                SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya_err", mycount_ERROR);
                            }
                        }

                    }


                    SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieya", mycount);
                    judgmentBloodpressureDB();// 从本地数据库获取血压数据
                    //清空下次数 ，避免手环切换发生的次数问题
                     mycount=0;
                     mycount_ERROR=0;
                }
               }*/
            } else if (event.getMessage().equals("updata_XIEYANGpressure")) {
               /* if(null!=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
                    String myaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                    //如果日期一样保存数据
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = new SimpleDateFormat("yyyy-MM-dd").format(curDate);
                    if(str.equals(curdatetv.getText().toString())){
                        if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieyang")) {
                            mycount_xieyang = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), curdatetv.getText().toString() + myaddress + "mycount_xieyang");
                            mycount_xieyang++;
                        }else{
                            mycount_xieyang++;
                        }

                    }
                    if(str.equals(curdatetv.getText().toString())){
                        SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(),curdatetv.getText().toString() + myaddress + "mycount_xieyang",mycount_xieyang);
                    }
                    judgmentOxygenDB();
                    //清空下次数 ，避免手环切换发生的次数问题
                    mycount_xieyang=0;
                }
*/
            } else if (event.getMessage().equals("updata_myhata")) {
                //查询下数据库历史数据
                // judgmentHealthDB();
                // EventBus.getDefault().post(new MessageEvent("updata_xinlv"));//更新下数据报告
            } else if ("update_view".equals(event.getMessage())) {
                reloadHeartPage();
            } else if ("unBond".equals(event.getMessage())) {
                reloadHeartPage();
            } else if ("update_userInfo".equals(event.getMessage())) {
                setgoalstep();
            } else if ("update_unit".equals(event.getMessage())) {
                weatherShow();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                    }/////
                }).start();


                if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                    calorie_up.setText(getActivity().getString(R.string.unit_kj));
                    mileage_up.setText(getActivity().getString(R.string.unit_mi));
                } else {
                    calorie_up.setText(getActivity().getString(R.string.everyday_calorie));
                    mileage_up.setText(getActivity().getString(R.string.kilometer));
                }
                String sport = SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE);
                if (sport.equals("")) {
                    sport = "1";
                }
                updateSportDate(sport);//更新数据
            } else if (CONNECT_FAIL.equals(event.getMessage())) {
                if (null != loadingDialog && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                    loadingDialog = null;
                }
            }

        }
    }

    int max = 0;
    int min = 0;

    private void settitileText(int x) {
        switch (x) {
            case 0:
                cb_navigation_heart.setText("");
                cb_navigation_sport.setText(getActivity().getString(R.string.sport_text));
                cb_navigation_sleep.setText(getActivity().getString(R.string.steps_text));
                vp.setCurrentItem(0);
                break;
            case 1:
                cb_navigation_heart.setText(getActivity().getString(R.string.sport_text));
                cb_navigation_sport.setText(getActivity().getString(R.string.steps_text));
                cb_navigation_sleep.setText(getActivity().getString(R.string.data_sleep));
                vp.setCurrentItem(1);
                break;
            case 2:
                cb_navigation_heart.setText(getActivity().getString(R.string.steps_text));
                cb_navigation_sport.setText(getActivity().getString(R.string.data_sleep));
                cb_navigation_sleep.setText("");
                vp.setCurrentItem(2);
                break;
            default:
                break;
        }
    }


    private class RefreshListener implements OnRefreshListener<ScrollView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
//            new GetDataTask().execute();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

        }
    }

    // 设置目标步数
    private void setgoalstep() {
        // TODO Auto-generated method stub
        setgoalsteptv = (TextView) convertView.findViewById(R.id.setgoalstep_tv);
//        setgoalsteptv.setTypeface(fzlt);
        SharedPreferences goalPreferences = getActivity().getSharedPreferences("goalstepfiles",
                Context.MODE_PRIVATE);
        goalstepcount = goalPreferences.getInt("setgoalstepcount", 5000); // 默认步数为5000步
        String goalString = getActivity().getString(R.string.the_goalstepsetting) + " " + goalstepcount;

        setgoalsteptv.setText(goalString);
        setsteppercent();
    }

    private void DateInit() {
        // *************************获取系统时间************************************
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                    if (StringUtils.isEmpty(curtime_str) || !curtime_str.equals(getDateFormat.format(curDate))) {  // yyyy-MM-dd
                        Thread.sleep(10);
                        curtime_str = getDateFormat.format(curDate);   // 更新当前页面的 本地日期
                        System.out.println("HomeFragment  get system date" + curtime_str);
                        Message msg = new Message();
                        msg.what = SETCURTIME;  // 设置当前的日期
                        msg.obj = curtime_str;
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 点击日期选择要查看的日期************************************************
        curdatetv.setOnClickListener(new OnClickListener() {   // 计步页面选择日期
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), CalendarAcitity.class);
                startActivity(mIntent);
                // AnimCommon.set(R.anim.in_from_top, R.anim.out_to_top);
            }
        });
        sleep_curdatetv.setOnClickListener(new OnClickListener() { //  // 睡眠页面选择日期
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), CalendarAcitity.class);
                startActivity(mIntent);
                // AnimCommon.set(R.anim.in_from_top, R.anim.out_to_top);
            }
        });

        heart_curdatetv.setOnClickListener(new OnClickListener() {   // 心率页面选择日期
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), CalendarAcitity.class);
                startActivity(mIntent);
            }
        });
        OXY_curdatetv.setOnClickListener(new OnClickListener() {   // 心率页面选择日期
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), CalendarAcitity.class);
                startActivity(mIntent);
            }
        });
        bloodpressure_curdatetv.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), CalendarAcitity.class);
                startActivity(mIntent);
            }
        });


    }

    private void setSportData(int allStep, float allCalorie, float allDistance, float activetime) {   // 步数，卡路里，距离，集合的个数(作为活跃时间activetime)
        // DecimalFormat df = new DecimalFormat("0.00"); // 保留小数点后两位
        if (allStep > 200000) {
            allStep = 52007;

            String distance = String.format(Locale.ENGLISH, "%.2f", (allStep * 0.7) / 1000.0);     // 运动距离
            allDistance = Float.valueOf(distance);

            int userWeightI = 0;
            String userWeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WEIGHT);
            if (StringUtils.isEmpty(userWeight)) {
                userWeightI = 60;
            } else {
                userWeightI = Integer.valueOf(userWeight);
            }
            allCalorie = Float.valueOf(String.format(Locale.ENGLISH, "%1$.2f", (float) (userWeightI) * (float) ((allStep * 0.7) / 1000.0) * 1.036));  // 卡路里

        } else if (allStep < 0) {
            allStep = 0;
            allDistance = 0;
            allCalorie = 0;
        }
        sport_step.setText(allStep + "");   // 步数值

        float dd = allDistance;
        String dd2 = Utils.setformat(1, allDistance);
        if (SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {  //公制
            mileage.setText(Utils.setformat(1, allDistance) + riceString);  // 运动距离
            calorie.setText(Utils.setformat(1, allCalorie) + calorieString); // 卡路里值
        } else {
            mileage_up.setText(getActivity().getString(R.string.unit_mi));
            calorie_up.setText(getActivity().getString(R.string.unit_kj));
            mileage.setText(Utils.setformat(1, Utils.getUnit_km(allDistance) + riceString));  // 运动距离(英制)
            calorie.setText(Utils.setformat(1, Utils.getUnit_kal(allCalorie) + calorieString)); // 卡路里值(英制)
        }
        /*mileage.setText(Utils.setformat(1, allDistance) + riceString);  // 运动距离

        float dd3 = allCalorie;
        String dd24 = Utils.setformat(1, allCalorie);
        calorie.setText(Utils.setformat(1, allCalorie) + calorieString); // 卡路里值*/

        //活跃时间值设置    1km = 15min  4km = 60min   1/15km = 1min   20170328 修改
        String distanceStr = String.format(Locale.ENGLISH, "%.2f", (allStep * 0.7) / 1000.0);  //处理活跃时间时除10 便于计算活跃时间值  curStep * 0.7) / 10.0
        //不同语言下会出现格式化错误
        distanceStr = distanceStr.replace(",", ".");
        Float distanceF = Float.valueOf(distanceStr);
        if (distanceF <= 0) {
            activetime = 0;  //active_time ,活跃时间值
            active_time.setText((int) activetime + "h");
        } else {
            activetime = (int) ((distanceF) / (0.067));  //总的分钟数   TODO --- 根据运动距离 计算 活跃时间
            int minsNew = (int) ((distanceF) / (0.067));  //总的分钟数  // 1/15 0.066667
            if (minsNew < 0) {
                active_time.setText("0h 0m");  //active_time ,活跃时间值
            } else {
                active_time.setText((minsNew / 60) + "h " + (minsNew % 60) + "m");
            }
        }
        getstep_double = (double) allStep; // 测试数据获得一天的总步数
        setsteppercent();
    }

    private void setsteppercent() {
        int percent = (int) ((getstep_double / goalstepcount) * 100);
        String percentStr = percent + "";
        String strDate = getCurDate();    //获取当前时间
        if (percent >= 100) { //判断是否是计步界面和当天时间   && getDateFormat.format(new Date()).equals(strDate)&& vp.getCurrentItem() == 1
            sport_percent.setText(getActivity().getString(R.string.kctfinish) + " " + "100%");
            //TODO 改成好看一点的对话框弹出 达成目标 提示   --- 计步目标弹框
//            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.GOALSET).equals("")) {
              /*  new AlertDialog(mContext).builder()
                        .setMsg(getString(R.string.goal_again_set))
                        .setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initTarget();
                                initAlertDialog(pickers);
                            }
                        }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();*/
//                SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.GOALSET, "1");
//                SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.GOALSET, "5000");
//            }
            //Toast.makeText(mContext, "您已达成目标!", Toast.LENGTH_SHORT).show();
            sport_process_view.setCurrentCount(100);
        } else if (percent <= 0) {
            sport_percent.setText(getActivity().getString(R.string.kctfinish) + " " + "0%");
            sport_process_view.setCurrentCount(0);
        } else {
            sport_percent.setText(getActivity().getString(R.string.kctfinish) + " " + percentStr + "%");// 步数完成百分比
            sport_process_view.setCurrentCount((int) ((getstep_double / goalstepcount) * 100));
        }
    }

    private void setHeartData(String heart_rate, String time) {
        if (!Utils.getLanguage().equals("zh")) {        //外文情况下心率界面
            if (!Utils.getLanguage().contains("ja")) {
                heart_time_ll.setOrientation(LinearLayout.VERTICAL);
            }
        }
        heart_num.setText(heart_rate);
        measuringtime_tv.setText(time);
        heart_process_view.setCurrentCount(Utils.toint(heart_rate));
    }

    private MyAlertDialog myDialog = null;

    // 睡眠相关数据数据处理
    private void getSleepTimeData(String s, ArrayList<SleepData> arr) {
        String mid = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID);
        ArrayList<SleepData> arrSleep = null;
        if (arr != null) {
            arrSleep = arr;    // 当前天和前一天的 睡眠数据
        }

        if (arrSleep != null) {
            int lights = 0;
            int deepS = 0;
            // 当前天的 21点 的时间戳  前一天 9 点的时间戳
            String strDate = getCurDate();  // TODO 开始日期   2017-04-09
            Date startTimeDates;
            Calendar calendar = Calendar.getInstance();
            try {
                startTimeDates = getDateFormat.parse(strDate);  // 当前天的 日期格式
                calendar.setTime(startTimeDates);
                calendar.add(Calendar.DATE, -1);
                strDate = getDateFormat.format(calendar.getTime()).toString();  // 2017-04-06     2017-04-11   // TODO 结束日期
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String choiceDate = arrangeDate(strDate);

            strDate = strDate + " 21";
            Date endTimeDate;
            long endTime = 0;    // todo  当前日期的睡眠的结束时间
            Calendar calendar3 = Calendar.getInstance();
            Date startTimeDate;
            long startTime = 0;   // todo  当前日期的睡眠开始时间
            try {
                startTimeDate = format.parse(strDate);
                startTime = startTimeDate.getTime() / 1000;     // todo --- 当前日期的睡眠开始时间  1491742800
                calendar3.setTime(startTimeDate);
                calendar3.add(Calendar.DATE, +1);
                String start = getDateFormat.format(calendar3.getTime()).toString();  //TODO 这里通过getDateFormat 将日期转成了 年月日格式，下面 + 09 还是第2天的 9点 2017-04-06
                start = start + " 09";
                endTimeDate = format.parse(start);
                endTime = endTimeDate.getTime() / 1000;   //TODO ---当前日期的睡眠结束时间   前一天日期的 9 点的时间戳    当前日期的结束时间 1491786000000 13位转为10位 1491786000
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < arrSleep.size(); i++) {
                String endTimeStr = arrSleep.get(i).getEndTime(); // s数据库中结束时间  日期格式   2017-04-09 21:00:00
                Date date = StringUtils.parseStrToDate(endTimeStr, SIMPLE_DATE_FORMAT);
                calendar.setTime(date);
                long sleepDataEndTime = calendar.getTimeInMillis() / 1000;  //将日期格式转为时间戳 1489816800   TODO --- 每一条睡眠数据的 结束时间  10位时间戳    1491742800     ---- 2017/4/9 21:0:0    1491742800     1491656400--2017/4/8 21:0:0

                String startTimeStr = arrSleep.get(i).getStarttimes(); // s数据库中结束时间  日期格式   2017-04-09 21:00:00
                Date date2 = StringUtils.parseStrToDate(startTimeStr, SIMPLE_DATE_FORMAT);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(date2);
                long sleepDataStartTime = calendar2.getTimeInMillis() / 1000;  //TODO  s数据库睡眠数据的 开始时间   1491707220   2017/4/9 11:7:0
                //todo  1： 结束时间点必须大于 21 点     这里取的是一天的有效数据
                if (sleepDataEndTime > startTime) {  // TODO 当天睡眠数据的有效数据 应该 结束时间 >= 当天的21点 <= 后一天的 9点
                    // 结束时间 >= 21 点 但可以 超过 9 点 （超过9点时 --- 以9点分割点， 9点之前的为当天的有效睡眠数据 ， 9点之后的为无效的睡眠数据）   用结束时间 -   20170409 00:08:50
                    if (sleepDataEndTime <= endTime) {  // 全为有效数据   结束时间在 21点到9点之间
                        if (sleepDataStartTime >= startTime) { // 开始时间 >= 21 点   即睡眠数据在 21:00 到 09:00 之间  ---- 全为有效数据
                            deepS += Utils.toint(arrSleep.get(i).getDeepsleep());   // 深睡时长
                            lights += Utils.toint(arrSleep.get(i).getLightsleep()); // 浅睡时长
                        } else {  // 开始时间 在 21 点之前    ----统计一天的 睡眠
                            long okSleeptime = sleepDataEndTime - startTime;  //21点之后的有效 秒数值     1491753600 -- 2017/4/10 0:0:0      1491753601  ---- 2017/4/10 0:0:1
                            int okFenTime = (int) okSleeptime / 60; // 有效的睡眠 分钟数
                            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //72
                                if (arrSleep.get(i).getSleeptype().equals("0")) {  // 深睡
                                    deepS += okFenTime;
                                } else if (arrSleep.get(i).getSleeptype().equals("1")) { // 浅睡
                                    lights += okFenTime;
                                }
                            } else if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")
                                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { // 2：BLE 3：MTK
                                if (arrSleep.get(i).getSleeptype().equals("2")) {  //TODO  --- 2 深睡
                                    deepS += okFenTime;
                                } else if (arrSleep.get(i).getSleeptype().equals("1")) { // 浅睡
                                    lights += okFenTime;
                                }
                            }
                        }
                    } else {    // 结束时间 > 9点    9点之后的为无效的睡眠数据）   用结束时间 -   20170409 00:08:50     20170409 00:10:50   120
                        arrSleep.get(i).getStarttimes();
                        if (sleepDataStartTime >= endTime) {   // 数据库的开始时间 比 当前睡眠的截止 还大  --- 当天无效睡眠数据
                            continue;
                        } else {
                            long okSleeptime = endTime - sleepDataStartTime;
                            int okFenTime = (int) okSleeptime / 60; // 有效的睡眠 分钟数
                            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //72
                                if (arrSleep.get(i).getSleeptype().equals("0")) {  // 深睡
                                    deepS += okFenTime;
                                } else if (arrSleep.get(i).getSleeptype().equals("1")) { // 浅睡
                                    lights += okFenTime;
                                }
                            } else if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")
                                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { // 2：BLE 3：MTK
                                if (arrSleep.get(i).getSleeptype().equals("2")) {  // 深睡
                                    deepS += okFenTime;
                                } else if (arrSleep.get(i).getSleeptype().equals("1")) { // 浅睡
                                    lights += okFenTime;
                                }
                            }

                        }
                    }
                }
            }
            //Log.e(lights + "===lights", deepS + "===deepS");
            lightTime = lights % 60;
            lightTime = lightTime / 60 + lights / 60;    // 获取最终的睡眠时间值      2.4166666666666665
            deepSleep = deepS % 60;  // 720
            deepSleep = deepSleep / 60 + deepS / 60;   // 12.0                        0.016666666666666666
            //Log.e(lightTime + "===", deepSleep + "===");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        setgoalstep();
//        if (service != null) {
//            service.setHeartrateData(new getNetworkDatas());
//        }
//        if (isExecuteRefresh) {
        // mPullScrollView.doPullRefreshing(true, 300);
//        }
    }


    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullScrollView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // ((MainActivity) activity).onSectionAttached(getArguments().getString(
        // ARG_SECTION_TITLE));
    }

    public void weatherInit() {
        isInit = false;
        // 检查网络是否可用
        if (NetWorkUtils.isConnect(mContext)) {
            mLocationClient = new AMapLocationClient(mContext.getApplicationContext());
            mLocationClient.setLocationListener(HomeFragment.this);
            weatherShow();
            InitLocation();
            if (!mLocationClient.isStarted()) {
                Log.e("开始定位", "..........");
                mLocationClient.startLocation();
            }
        } else {
            weatherShow();    //todo --- 获取数据失败，也显示天气 ？？？
            Toast.makeText(mContext, getString(R.string.no_net_noweather), Toast.LENGTH_LONG).show();  // 网络未连接,获取数据失败
        }
    }

    /**
     * 开始定位
     */
    private void InitLocation() {
        mClientOption = new AMapLocationClientOption();
        mClientOption.setLocationMode(AMapLocationMode.Battery_Saving);
        mClientOption.setGpsFirst(true);
        mClientOption.setInterval(UPDATE_TIME);
        mClientOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mClientOption);
    }


    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("HomeFragment");

        StatService.onResume(this);

        // 接受日期选着Activity 穿过来的数据
        dealDateShow();
        /**获取运动类型  1.健走 2.户外跑 3.登山跑 4.越野跑 5.室内跑 6.半马 7.全马**/
        String sport = SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE);
        if (StringUtils.isEmpty(sport)) {
            sport = "1";
        }
        updateSportDate(sport);//更新数据
        updateSportView(sport);//更新UI
        if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {
           /* iv_bo.setVisibility(View.VISIBLE);
            iv_bp.setVisibility(View.VISIBLE);
            tv_bo.setVisibility(View.VISIBLE);  // 血氧值
            tv_bp_min.setVisibility(View.VISIBLE);  // 血压值
            tv_bp_max.setVisibility(View.VISIBLE);   //血压值*/
        } else {
            iv_bp.setVisibility(View.GONE);
            iv_bo.setVisibility(View.GONE);
            tv_bo.setVisibility(View.GONE);  // 血氧值
            tv_bp_min.setVisibility(View.GONE);  // 血压值
            tv_bp_max.setVisibility(View.GONE);   //血压值
        }

        DateInit();// 日期的处理及显示选中当天的运动及睡眠数据

        String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
        if (!StringUtils.isEmpty(curMacaddress)) {
            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  //当前系统的日期
            String Last7DayDate = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE);
            if (!StringUtils.isEmpty(Last7DayDate) && Last7DayDate.equals(mcurDate) && !StringUtils.isEmpty(curMacaddress)) {
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0:" + curMacaddress);//TODO ---将取7取过7天数据的标志重置为0--- 没有取过 7 天的数据
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeFragment");

        StatService.onPause(this);   // todo --- 百度
    }

    private void dealDateShow() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        datePreferences = getActivity().getSharedPreferences("datepreferences", Context.MODE_PRIVATE);
        final int select_day = datePreferences.getInt("select_day", 0);
        final int select_month = datePreferences.getInt("select_month", 0);
        final int select_year = datePreferences.getInt("select_year", 0);
        if ((select_day != 0) && (select_month != 0) && (select_year != 0)) {
            if (select_month < 10) {
                select_monthstr = "0" + select_month;
            } else {
                select_monthstr = String.valueOf(select_month);
            }
            if (select_day < 10) {
                select_daystr = "0" + select_day;
            } else {
                select_daystr = String.valueOf(select_day);
            }
            String select_date = select_year + "-" + select_monthstr + "-" + select_daystr;
            Message msg = new Message();
            msg.what = UPDATEDATE;
            msg.obj = select_date;
            handler.sendMessage(msg);
            // 清除缓存。
            Editor editor = datePreferences.edit();
            editor.remove("select_day");
            editor.remove("select_month");
            editor.remove("select_year");
            editor.commit();
        }
    }

    private long mLastClickTime = 0L;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 1000) {
            return true;
        }
        return false;
    }

    protected void dealDayDatas(String datas) {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(datas);
            String result = jsonObj.getString("result");
            if ("0".equals(result)) {
                JSONArray jsonArr = jsonObj.getJSONArray("datas");
                for (int i = 0; i < jsonArr.length(); i++) {
                    adayallstep = jsonArr.getInt(i);
                }
                // setSportData(adayallstep);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String filePath = Environment.getExternalStorageDirectory() + "/appmanager/fundoShare/";  // fundoShare    --- funfit
    private String fileName = "screenshot_analysis.png";
    private String detailPath = filePath + File.separator + fileName;

    private void showShare(int pageIndex) {  // 分享
        ScreenshotsShare.savePicture(ScreenshotsShare.takeScreenShot(getActivity(), pageIndex), filePath, fileName);
        //  ShareSDK.initSDK(getActivity());  // ShareSDK
        mapPackageName = setImage(getActivity());
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");
        oks.setTitleUrl("http://www.fundo.cc");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getString(R.string.welcome_funrun));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(getString(R.string.welcome_funrun));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        if (android.os.Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }

        // 启动分享GUI
        oks.show(getActivity());
        dismissLoadingDialog();
    }

    OnClickListener facebookclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToFacebook();
        }
    };
    OnClickListener Instagramclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToInstagram();
        }
    };
    OnClickListener twitterclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTotwitter();
        }
    };
    OnClickListener whatsappclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTowhatsapp();
        }
    };
    OnClickListener Linkedinclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToLinkedin();
        }
    };
    OnClickListener mobileqqclick = new OnClickListener() {   // 点击分享到QQ
        @Override
        public void onClick(View v) {
//            shareTomobileqq();
            Utils.onClickShareToQQ(getActivity(), detailPath);
        }
    };
    OnClickListener stravaclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToStrava();
        }
    };

    /**
     * 分享至Facebook
     */
    public void shareToFacebook() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_FACEBOOK_KATANA);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, getActivity(), "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 分享至Instagram
     */
    public void shareToInstagram() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_INSTAGRAM_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, getActivity(), "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Twitter
     */
    public void shareTotwitter() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TWITTER_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, getActivity(), "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至whatsapp
     */
    public void shareTowhatsapp() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_WHATSAPP);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, getActivity(), "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();  // 手机上没有安装Whatsapp
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Linkedin
     */
    public void shareToLinkedin() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_LINKEDIN_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, getActivity(), "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至mobileqq
     */
    public void shareTomobileqq() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TENCENT_MOBILEQQ);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, getActivity(), "");  // 分享到QQ
            } else {   // 包名为空
                Toast.makeText(getActivity(), getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();  // 手机上没有安装QQ
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();  // 手机上没有安装QQ
        }
    }


    /**
     * 分享至strava
     */
    public void shareToStrava() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_STRAVA);
            if (packageName != null) {
                PackageManager pm = getActivity().getPackageManager();
                boolean isAdd = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", packageName));
                if (!isAdd) {
                    Toast.makeText(getActivity(), getString(R.string.strava_need_open_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
                actionShare_sms_email_facebook(packageName, getActivity(), "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    public static final String COM_FACEBOOK = "com.facebook";
    public static final String COM_FACEBOOK_KATANA = "com.facebook.katana";

    public static final String COM_INSTAGRAM = "com.instagram";
    public static final String COM_INSTAGRAM_ANDROID = "com.instagram.android";

    public static final String COM_TWITTER = "com.twitter";
    public static final String COM_TWITTER_ANDROID = "com.twitter.android";

    public static final String COM_WHATSAPP = "com.whatsapp";

    public static final String COM_LINKEDIN = "com.linkedin";
    public static final String COM_LINKEDIN_ANDROID = "com.linkedin.android";

    public static final String COM_STRAVA = "com.strava";

    public static final String COM_TENCENT_MOBILEQQ = "com.tencent.mobileqq";  //qq包名
    private Map<String, String> mapPackageName;

    public Map<String, String> setImage(Activity activity) {
        Map<String, String> mapPackageName = new LinkedHashMap<String, String>();
        // PackageManager pManager = activity.getPackageManager();
        ArrayList<ResolveInfo> resolveInfos = getShareApp(activity);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            // String packageName = resolveInfo.activityInfo.name;
            if (packageName.startsWith(COM_FACEBOOK_KATANA)) {
                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_FACEBOOK_KATANA, packageName);
            }
            if (packageName.startsWith(COM_INSTAGRAM_ANDROID)) {

                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_INSTAGRAM_ANDROID, packageName);

            }
            if (packageName.startsWith(COM_TWITTER_ANDROID)) {
                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_TWITTER_ANDROID, packageName);
            }
            if (packageName.startsWith(COM_WHATSAPP)) {
                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_WHATSAPP, packageName);
            }
            if (packageName.startsWith(COM_LINKEDIN_ANDROID)) {
                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_LINKEDIN_ANDROID, packageName);
            }
            if (packageName.startsWith(COM_TENCENT_MOBILEQQ)) {
                System.out.println("**************" + packageName);
                // image_facebook.setImageDrawable(resolveInfo.loadIcon(pManager));
                mapPackageName.put(COM_TENCENT_MOBILEQQ, packageName);
            }
            if (packageName.startsWith(COM_STRAVA)) {
                mapPackageName.put(COM_STRAVA, packageName);
            }
        }
        return mapPackageName;
    }

    public ArrayList<ResolveInfo> getShareApp(Context context) {
        ArrayList<ResolveInfo> WECHAT_FACEBOOK = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("image/png");
        PackageManager pManager = context.getPackageManager();
        List<ResolveInfo> mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        for (ResolveInfo resolveInfo : mApps) {
            String packageName = resolveInfo.activityInfo.packageName;
            Log.i(TAG, resolveInfo.activityInfo.name + ", " + resolveInfo.loadLabel(pManager).toString());
            if (packageName.startsWith(COM_FACEBOOK)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_INSTAGRAM)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_TWITTER)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }

            if (packageName.startsWith(COM_WHATSAPP)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_LINKEDIN)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_TENCENT_MOBILEQQ)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
            if (packageName.startsWith(COM_STRAVA)) {
                WECHAT_FACEBOOK.add(resolveInfo);
            }
        }
        return WECHAT_FACEBOOK;
    }

    public void actionShare_sms_email_facebook(String packageName, Activity activity, String shareText) {  //所有分享目标共享此方法
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_SUBJECT, "SchwinnCycleNav Ride Share");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        File file = new File(detailPath);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(Intent.EXTRA_STREAM, TUriParse.getUriForFile(activity, file));
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//		intent.putExtra(Intent.EXTRA_STREAM, DatabaseProvider.queryScreenshot(activity, datetime.getTime()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(packageName);
        activity.startActivity(Intent.createChooser(intent, getString(R.string.app_name)));  //根据QQ包名分享到QQ
        System.out.println("****3");
    }


    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // 整理日期数据
    private String arrangeDate(String dateStr) {
        String[] dates = dateStr.split("-");
        String year = dates[0];
        String month = dates[1];
        String day = dates[2];
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        return year + "-" + month + "-" + day;
    }

    /**
     * 接受从服务器请求到的数据
     */
    class getNetworkDatas implements ReturnData {
        @Override
        public void heartrateData(String strHeartrateJson) {
            Log.e(TAG, "Interface 接口中获取的heartrateData：" + strHeartrateJson);
        }

        @Override
        public void runData(String strRunJson) {
            // Log.e("", "strRunJson h = "+strRunJson);
            Log.e(TAG, "Interface 接口中获取的runData：" + strRunJson);
            ArrayList<RunData> mdArrRun = Resolve.resolveRunData(strRunJson);
            Log.e(TAG, "Interface mdArrRun ：" + mdArrRun);
            // Log.e(TAG, "Interface mdArrRun ：" + mdArrRun.size());
            updateRun(mdArrRun);   // 从网络获取的数据 -----
        }

        @Override
        public void sleepData(String strSleepJson) {
            // Log.e(TAG, "Interface 接口中获取的睡眠数据 ：" + strSleepJson);
            arrNetworkSleepMb = Resolve.resolveSleepData(strSleepJson);
            Log.e(TAG, "Interface 接口中获取的sleepData ：" + strSleepJson);
            if (arrNetworkSleepMb != null) {
                arrSleepDataDay = arrNetworkSleepMb;
                getSleepTimeData(null, arrSleepDataDay);
                // Log.e(TAG, "Interface 睡眠时长 ：" + sleepOverallTime);
                // Log.e(TAG, "Interface 深睡时长 ：" + deepSleep);
                sleepOverallTime = lightTime + deepSleep;

                if (sleepOverallTime > 24) {
                    sleepOverallTime = 24;
                    lightTime = 10;
                    deepSleep = 14;
                } else if (sleepOverallTime < 0) {
                    sleepOverallTime = 0;
                    lightTime = 0;
                    deepSleep = 0;
                }

                lightsleeptime.setText(Utils.setformat(2, lightTime + "") + hourString);             // 浅睡赋值
                deepsleeptime.setText(Utils.setformat(2, deepSleep + "") + hourString);               // 深睡赋值
                allsleeptime.setText(Utils.setformat(2, sleepOverallTime + "") + hourString);        // 睡眠总时长
                // WheelIndicatorView.setsleepProgressBar(wheelSleepView,deepSleep,sleepOverallTime);
                if (sleepOverallTime == 0) {
                    sleepqaulity.setText(R.string.sleep_no);   // 睡眠质量赋值
                    sleep_process_view.setCurrentCount(0);     // 睡眠圆环赋值
                    sleeptime_tv.setText("0");     // 圆环内---睡眠时间值赋值
                    updateSleep(arrNetworkSleepMb);
                }
            }
        }
    }

    private void updateRun(ArrayList<RunData> mdArrRun) {  //更新运动数据(计步)
        if (mdArrRun != null) {
            int step = 0;
            float claorie = 0;
            float distance = 0;
            for (int i = 0; i < mdArrRun.size(); i++) {
                RunData mb = mdArrRun.get(i);
                step += Integer.parseInt(mb.getStep());
                claorie += Float.parseFloat(mb.getCalorie());
                distance += Float.parseFloat(mb.getDistance());
            }
            setSportData(step, claorie, distance, mdArrRun.size());
        } else {
            setSportData(0, 0, 0, 0);
        }
    }

    private void updateSleep(ArrayList<SleepData> arrNetworkSleepMb) {   // 当天的睡眠 数组  --- 这里有两天 的 数据
        if (arrNetworkSleepMb != null) {
            arrSleepDataDay = arrNetworkSleepMb;
            getSleepTimeData(null, arrSleepDataDay);
            // Log.e(TAG, "Interface 睡眠时长 ：" + sleepOverallTime);
            // Log.e(TAG, "Interface 深睡时长 ：" + deepSleep);
            sleepOverallTime = lightTime + deepSleep;  // 12.0    2.433333333333333

            if (lightTime < 0) {
                lightTime = 0;
            }
            if (deepSleep < 0) {
                deepSleep = 0;
            }
            if (sleepOverallTime < 0) {
                sleepOverallTime = 0;
            } else if (sleepOverallTime > 12) {
                sleepOverallTime = 12;
            }

            lightsleeptime.setText(Utils.setformat(2, lightTime + "") + hourString);                // 浅睡赋值
            deepsleeptime.setText(Utils.setformat(2, deepSleep + "") + hourString);                  // 深睡赋值
            allsleeptime.setText(Utils.setformat(2, sleepOverallTime + "") + hourString);           // 睡眠总时长   12.00h
            // WheelIndicatorView.setsleepProgressBar(wheelSleepView,deepSleep,sleepOverallTime);
            if (sleepOverallTime == 0) {
                sleepqaulity.setText(R.string.sleep_no);   // 睡眠质量赋值
                sleep_process_view.setCurrentCount(0);    // 睡眠圆环赋值
                sleeptime_tv.setText("0");     // 圆环内---睡眠时间值赋值
            } else {
                int i = (int) sleepOverallTime;
                if (i <= 9 && i > 0) {
                    sleeptime_tv.setText("0" + i);    // 圆环内---睡眠时间值赋值
                } else if (i > 12) {
                    sleeptime_tv.setText(12 + "");    // 圆环内---睡眠时间值赋值    // 12
                } else if (i >= 10 && i <= 12) {
                    sleeptime_tv.setText(i + "");
                } else if (i <= 0) {
                    sleeptime_tv.setText("00");
                }
                int s = (int) (sleepOverallTime * 100);    // 1200
                sleep_process_view.setCurrentCount((s % 100) * 60 / 100);   // 睡眠圆环赋值
                /*	if ((deepSleep / sleepOverallTime) < 0.6) {
					sleepqaulity.setText(R.string.sleep_zhiliang_bad);
				} else if ((deepSleep / sleepOverallTime) < 0.8) {
					sleepqaulity.setText(R.string.sleep_zhiliang_good);
				} else {
					sleepqaulity.setText(R.string.sleep_zhiliang_nice);
				}*/
                if (sleepOverallTime > 7 && deepSleep > (deepSleep + lightTime) / 4) {
                    sleepqaulity.setText(R.string.sleep_good_text);     // 睡眠质量赋值
                } else if (sleepOverallTime > 5.5 && deepSleep > (deepSleep + lightTime) / 5) {
                    sleepqaulity.setText(R.string.sleep_normal_text);    // 睡眠质量赋值
                } else if (deepSleep == 0 && lightTime == 0) {
                    sleepqaulity.setText(R.string.sleep_no);
                } else {
                    sleepqaulity.setText(R.string.sleep_bad_text);
                }
            }
            sleepOverallTime = 0;
            deepSleep = 0;
        }
    }

    private void judgmentRunDB() {  // 从数据库中获取 运动数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        String strDate = getCurDate();  // 2017-03-28   // 每次切换日期都会进入此 方法（根据对应的日期获取对应的计步数据）  TODO --- 当前的日期
        String choiceDate = arrangeDate(strDate);  //2017-03-28

        String watch = SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        // todo ---- 根据 日期 查询 计步数据
        if (watch.equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {   // 72,手环   // || (watch.equals("2") && !sdf.format(new Date()).toString().equals(choiceDate))
            Query query = null;
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
                query = db.getRunDao().queryBuilder()
                        .where(RunDataDao.Properties.Mac
                                .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
            } else {
                query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
            }
            List list = query.list();   // 当天的所有 计步 数组
            if (list != null && list.size() > 0) {
                int step = 0;  // 步数
                float claorie = 0; // 卡路里
                float distance = 0; // 距离
                for (int j = 0; j < list.size(); j++) {
                    RunData runDB = (RunData) list.get(j);  // RunData ---- 计步数据
                    int s = Integer.parseInt(runDB.getStep());
                    float c = Float.parseFloat(runDB.getCalorie());
                    float d = Float.parseFloat(runDB.getDistance());
                    //Log.e("getStep", runDB.getStep() + "============");
                    step += s;
                    claorie += c;
                    distance += d;
                }
                // Log.e("step", "step"+step+"claorie"+claorie+"distance"+distance);
                float[] f = {step, claorie, distance, list.size()}; // 步数，卡路里，距离，集合大小
                Message msg = handler.obtainMessage();
                msg.what = UPUERRUNINFO;  // 更新计步数据
                msg.obj = f;
                handler.sendMessage(msg);
            } else if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {
                if (mContext == null) {
                    mContext = getActivity();
                }
            } else {
                Message msg = handler.obtainMessage();    //TODO --- 注释 0525
                msg.what = CLEARSPORT;    // TODO ----- 只要同步时 数据库中 当天没有数据时
                handler.sendMessage(msg);
            }
        } else if (watch.equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {  //mtk
            Query querySum = db.getRunDao().queryBuilder()
                    .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(RunDataDao.Properties.Date.eq(choiceDate)) //  .build();
                    .where(RunDataDao.Properties.Step.eq("0")).build();  //   todo --- Step.eq("0")  --- 取全天的计步数据

            List list = querySum.list();
            if (list != null && list.size() >= 1) {
                int step = 0;  // 步数
                float claorie = 0; // 卡路里
                float distance = 0; // 距离
                for (int j = 0; j < list.size(); j++) {
                    RunData runDB = (RunData) list.get(j);  // RunData ---- 计步数据
                    int s = Integer.parseInt(runDB.getDayStep());
                    float c = Float.parseFloat(runDB.getDayCalorie());
                    float d = Float.parseFloat(runDB.getDayDistance());
                    // Log.e("getStep", runDB.getStep() + "============");
                    step += s;
                    claorie += c;
                    distance += d;
                }
                // Log.e("step", "step"+step+"claorie"+claorie+"distance"+distance);
                float[] f = {step, claorie, distance, querySum.list().size()}; // 步数，卡路里，距离，集合大小
                Message msg = handler.obtainMessage();
                msg.what = UPUERRUNINFO;  // 更新计步数据
                msg.obj = f;
                handler.sendMessage(msg);
                Log.e(TAG, "step = " + step);
            } else if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {
                if (mContext == null) {
                    mContext = getActivity();
                }
            } else {
                   /* Message msg = handler.obtainMessage();
                    msg.what = CLEARSPORT;    // TODO ----- 只要同步时 数据库中 当天没有数据时
                    handler.sendMessage(msg);*/
            }
        } else if (watch.equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {   //手环
            int step = 0;
            float calorie = 0;
            float distance = 0;
            int dataSize = 1;
            String realTime = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME);  // 实时的日期   //todo  --- ٢٠١٨-٠٧-٢٨
            String syncTime = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHSYNCTIME); // 同步的日期   // todo  --- ٢٠١٨-٠٧-٢٨

           // String add = sdf.format(new Date());   // todo --- ٢٠١٨-٠٧-٢٨    choiceDate = 2018-07-28

            if (choiceDate.equals(sdf.format(new Date())) && !StringUtils.isEmpty(realTime) && realTime.equals(choiceDate)) {    //当前日期   ---- sp中实时的日期为当前日期    && realTime.equals(choiceDate)
                int synStep = 0;
                int realStep = 0;
                if (!StringUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {   // 同步的步数
                    synStep = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
                }
                if (!StringUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {      // 实时的步数
                    realStep = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
                }

                if (synStep <= realStep) {  // 同步的小于实时，用实时的数据
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {    // 实时的卡路里
                        calorie = Float.parseFloat(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                    }
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {   // 实时的距离
                        distance = Float.parseFloat(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                    }
                    step = realStep;
                    if (synStep == realStep) {
                        if (!StringUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDATASIZE))) {
                            dataSize = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDATASIZE));
                        }
                    }
                } else {  // 同步的大于 实时 用同步的数据
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {  // 对于X2端计步页面上 卡路里和距离 一直用的是实时的数据
                        calorie = Float.parseFloat(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                    }
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {
                        distance = Float.parseFloat(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                    }
                    step = synStep;
                    if (synStep == realStep) {
                        if (!StringUtils.isEmpty(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDATASIZE))) {
                            dataSize = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDATASIZE));
                        }
                    }
                }
            } else {   // 不是当前日期
                Query query = null;
                String mac = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC);  // MAC  SHOWMAC
                if (TextUtils.isEmpty(mac)) {
                    return;
                } else {
                    query = db.getRunDao().queryBuilder()
                            .where(RunDataDao.Properties.Mac.eq(mac))
                            .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
                }
                List<RunData> list = query.list();   // 当天的所有 计步 数组
                if (list != null && list.size() > 0) {
                    String mrunBinTime = "";
                    List<RunData> listok = new ArrayList<>();
                    for (int j = 0; j < list.size(); j++) {
                        if (!list.get(j).getBinTime().equals(mrunBinTime)) {
                            mrunBinTime = list.get(j).getBinTime();
                            if (listok.size() > 0) {
                                boolean isExist = false;
                                for (RunData data : listok) {
                                    if (data.getBinTime().equals(list.get(j).getBinTime())) {
                                        isExist = true;
                                        break;
                                    }
                                }
                                if (!isExist) {
                                    listok.add(list.get(j));
                                }
                            } else {
                                listok.add(list.get(j));
                            }
                        }
                    }

                    if (listok.size() > 0) {
                        for (int j = 0; j < listok.size(); j++) {
                            RunData runDB = (RunData) listok.get(j);  // RunData ---- 计步数据
                            int s = Integer.parseInt(runDB.getStep());
                            float c = Float.parseFloat(runDB.getCalorie());
                            float d = Float.parseFloat(runDB.getDistance());
                            step += s;
                            calorie += c;
                            distance += d;
                        }
                    }
                    dataSize = list.size();
                } else if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {
                    if (mContext == null) {
                        mContext = getActivity();
                    }
                } else {
//                    Message msg = handler.obtainMessage();    //TODO --- 应该去掉 ，引起瞬间清0
//                    msg.what = CLEARSPORT;    // TODO ----- 只要同步时 数据库中 当天没有数据时
//                    handler.sendMessage(msg);
                }
            }
            float[] f = {step, calorie, distance, dataSize}; // 步数，卡路里，距离，集合大小
            Message msg = handler.obtainMessage();
            msg.what = UPUERRUNINFO;  // 更新计步数据
            msg.obj = f;
            handler.sendMessage(msg);
        }
//            }/////
//        }).start();
    }

    private void judgmentSleepDB() {  // 主页面的睡眠数据 也 取 当天21点 到 第2天 9点的 数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        String strDate = getCurDate();  // 2017-04-09   2017-04-10    // TODO 开始日期
        String choiceDate = arrangeDate(strDate);  // 当前日期   // 获取当天的睡眠数据 ，必须往前 再取一天的睡眠数据     2017-04-10
        String beginStrDate = "";
//        strDate = strDate + " 00";  //   2017-04-07 08
        Date startTimeDate;
        Calendar calendar = Calendar.getInstance();
        try {
            startTimeDate = getDateFormat.parse(strDate);  // 当前天的 日期格式
            calendar.setTime(startTimeDate);
            calendar.add(Calendar.DATE, -1);
            beginStrDate = getDateFormat.format(calendar.getTime()).toString();  // 2017-04-06     2017-04-11   // TODO 结束日期
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String choiceDateBegin = arrangeDate(beginStrDate); // 前一天的日期

        Query query = null;
        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
            query = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDateBegin)).build();
        } else {
            query = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDateBegin)).build();
        }

        Query queryEnd = null;
        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
            queryEnd = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
        } else {
            queryEnd = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
        }

        List listCur = query.list();    //按日期查询到当前天的 睡眠 数据
        List listPre = queryEnd.list();  // 查询前一天的 睡眠 数据
        List<SleepData> list = new ArrayList();
        list.addAll(listCur);
        list.addAll(listPre); // 将当前天和上一天的睡眠数据都添加

        // Log.e("judgmentSleepDB", list.size() + "==");
        if (list != null && list.size() >= 1) {   // 如果有本地的当天数据，取本地的数据
            ArrayList<SleepData> arrSleep = new ArrayList<SleepData>();
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String msleepBinTime = "";
            for (int j = 0; j < list.size(); j++) {
                if (!list.get(j).getStarttimes().equals(msleepBinTime)) {
                    msleepBinTime = list.get(j).getStarttimes();
                    if (arrSleep.size() > 0) {
                        boolean isExist = false;
                        for (SleepData data : arrSleep) {
                            if (data.getStarttimes().equals(list.get(j).getStarttimes())) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            arrSleep.add(list.get(j));
                        }
                    } else {
                        arrSleep.add(list.get(j));
                    }
                }
            }
            Message msg = handler.obtainMessage();
            msg.what = UPUERSLEEPINFO; // 更新睡眠 数据
            msg.obj = arrSleep;
            handler.sendMessage(msg);
        } else if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {   // 如果没有本地的当天数据，从后台取当天的睡眠数据
            // 从后台获取当天的睡眠数据

        } else {
            Message msg = handler.obtainMessage();
            msg.what = CLEARSLEEP;
            handler.sendMessage(msg);
        }
//            }/////
//        }).start();
    }

    /**
     * 查询血氧值
     */

    private void judgmentOxygenDB() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        String strDate = getCurDate();
        String choiceDate = arrangeDate(strDate);     // 整理日期数据

        Query query = null;
        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getOxygenDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))).where(OxyDao.Properties.Date.eq(choiceDate)).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getOxygenDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).where(OxyDao.Properties.Date.eq(choiceDate)).build();  // 根据日期
        }

        List list = query.list();
        if (list != null && list.size() >= 1) {
            ArrayList<Oxygen> arrHear = new ArrayList<Oxygen>();
            for (int j = 0; j < list.size(); j++) {
                Oxygen hearDB = (Oxygen) list.get(j);
                arrHear.add(hearDB);
            }
            Message msg = handler.obtainMessage();
            msg.what = UPOxygen_INFO;
            msg.obj = arrHear;
            handler.sendMessage(msg);
        } else {
            Message msg = handler.obtainMessage();
            msg.what = CLEARHEARTUPOxygen_INFO;
            handler.sendMessage(msg);
        }
//            }/////
//        }).start();
    }


    /**
     * 查询血压值
     */

    private void judgmentBloodpressureDB() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        String strDate = getCurDate();
        String choiceDate = arrangeDate(strDate);     // 整理日期数据

        Query query = null;
        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getBloodpressureDao().queryBuilder()
                    // .where(HearDataDao.Properties.Mid.eq(mid))
                    .where(BloodpressureDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))).where(BloodpressureDao.Properties.Date.eq(choiceDate)).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getBloodpressureDao().queryBuilder().where(BloodpressureDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                    .where(BloodpressureDao.Properties.Date.eq(choiceDate)).build();  // 根据日期
        }

        List list = query.list();
        if (list != null && list.size() >= 1) {
            ArrayList<Bloodpressure> arrHear = new ArrayList<Bloodpressure>();
            for (int j = 0; j < list.size(); j++) {
                Bloodpressure hearDB = (Bloodpressure) list.get(j);
                arrHear.add(hearDB);
            }
            Message msg = handler.obtainMessage();
            msg.what = UPBloodpressure_INFO;
            msg.obj = arrHear;
            handler.sendMessage(msg);
        } else {
            Message msg = handler.obtainMessage();
            msg.what = CLEARHEARTUPBloodpressure_INFO;
            handler.sendMessage(msg);
        }
//            }/////
//        }).start();
    }

    private void judgmentHealthDB() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        String strDate = getCurDate();
        String choiceDate = arrangeDate(strDate);     // 整理日期数据
        Query query = null;
        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getHearDao().queryBuilder()
                    // .where(HearDataDao.Properties.Mid.eq(mid))
                    .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))).where(HearDataDao.Properties.Date.eq(choiceDate)).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                    .where(HearDataDao.Properties.Date.eq(choiceDate)).build();  // 根据日期 查询 运动数据
        }

        List list = query.list();
        if (list != null && list.size() >= 1) {
            ArrayList<HearData> arrHear = new ArrayList<HearData>();
            for (int j = 0; j < list.size(); j++) {
                HearData hearDB = (HearData) list.get(j);
                arrHear.add(hearDB);
            }
            Message msg = handler.obtainMessage();
            msg.what = UPUERHEARTINFO;
            msg.obj = arrHear;
            handler.sendMessage(msg);
        } else if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {

        } else {
            Message msg = handler.obtainMessage();
            msg.what = CLEARHEART;
            handler.sendMessage(msg);
        }
//            }/////
//        }).start();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        sethearttitileText(arg0);
        DateInit();
    }


    /**
     * 开始运动按钮
     */
    private void startSport() {   //TODO--- 点击开始运动按钮后 开始运动
        //todo --- 室内跑 不需要GPS
        int sportMode = 1;
        String sport = SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE);
        if (StringUtils.isEmpty(sport)) {
            sportMode = 1;   //默认选择健走
        } else {
            sportMode = Integer.valueOf(sport);
        }

        if (sportMode != 3) { // TODO --- 不是室内跑
            // 提示打开GPS
            if (!Utils.isGpsEnabled((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE))) {    // 提示
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.sweet_warn);
                builder.setMessage(R.string.gps_open);
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
                builder.create().show();
                return;
            } else {   // gps打开了
                Intent as = new Intent(getActivity(), SportsTheCountdownActivity.class);  // 倒计时页面
//                startActivityForResult(as, 55);
                startActivity(as);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        } else {
            Intent as = new Intent(getActivity(), SportsTheCountdownActivity.class);  // 倒计时页面
//            startActivityForResult(as, 55);
            startActivity(as);
            getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
//        if (resultCode == -1) {
        switch (requestCode) {
            case 55://TODO ---- 启动运动   (倒计时页面完成后，进入OutdoorRunActitivy页面)
                if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_VOICE).equals(SharedPreUtil.YES)) {  // 振动开关打开了
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);  //开始运动时，振动500ms
                    vib.vibrate(500);
                }

                intent = new Intent(getActivity(), OutdoorRunActitivy.class);   //  TODO ---- 倒计时完成后，进入OutdoorRunActitivy 页面
                Bundle bundle = new Bundle();
                bundle.putSerializable("gpsPoint", gpsPoint);  // todo --- 将 gpsPoint 传给  OutdoorRunActitivy
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
//            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_linerlayout:    // 手动刷新天气
            case R.id.sleep_weather_linerlayout:
            case R.id.heart_weather_linerlayout:
            case R.id.Oxygen_weather_linerlayout:
            case R.id.Bloodpressure_weather_linerlayout:
                if (mTv_weather.getText().toString().equals(getString(R.string.temperature)) || sleep_tv_weather.getText().toString().equals(getString(R.string.temperature))
                        || heart_tv_weather.getText().toString().equals(getString(R.string.temperature)) || Oxy_tv_weather.getText().toString().equals(getString(R.string.temperature))
                        || bloodpressure_tv_weather.getText().toString().equals(getString(R.string.temperature))) {
                    weatherInit();
//                    Toast.makeText(getActivity(), "点击获取天气了", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.bt_fragment_home_run_start://TODO --- 点击开始运动按钮 启动跑步---- 开始跑步
                if (isFastDoubleClick()) {
                    return;
                }
                startSport();
                break;
            case R.id.ibt_motionsetting:    // 运动设置按钮
                Intent intent = new Intent(getActivity(), MotionSettingActivity.class);   //进入运动设置页面
                startActivity(intent);
                break;
            case R.id.cb_navigation_heart_rate:   // 点击顶部心率按钮
                //settitileText(0);
                vp.setCurrentItem(vp.getCurrentItem() - 1);
                break;
            case R.id.cb_navigation_sport:
                //settitileText(1);
                break;

            case R.id.cb_navigation_sleep:
                vp.setCurrentItem(vp.getCurrentItem() + 1);
                break;
            case R.id.ib_navigation_share:  // 分享
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                showLoadingDialogNew(getString(R.string.progress_dialog_title));
                if (isRunning) {
                return;
            }
                isRunning = true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        if (!NetWorkUtils.isConnect(getActivity())) {
                            Toast.makeText(getActivity(), R.string.my_network_disconnected, Toast.LENGTH_SHORT).show();
                        } else {
                            if (Utils.isFastClick()) {
                                if (OnekeyShare.isShowShare) { // todo ---- 弹出分享框了
                                    OnekeyShare.isShowShare = false;
                                    showShare(MainService.PAGE_INDEX_HOME);// todo  --- 添加页面 标识
                                }
                            }
                        }
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        isRunning = false;
                    }
                }, 1000);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                break;
            case R.id.tv_navigation_synchronization:    // 同步按钮（同步蓝牙数据）------ 所有页面的同步按钮
                if (isFastDoubleClick()) {
                    return;
                }
                MainService service = MainService.getInstance();
                if (service.getState() != 3) {   // 没有连接手表时
                    Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();  //您没有连接到设备
                    return;
                }

//                service.setSynchronousDialog(new CloseDialog() {
//                    @Override
//                    public void closeDialog() {
//                        Message msg = handler.obtainMessage(SNYBTDATAOK);  // 发送同步蓝牙数据成功的消息
//                        handler.sendMessage(msg);     // 自动关闭的对话框，对话框关闭后，发送数据同步成功的指令
//                    }
//                });
//                MainService.isShowToast = true;


                String bluetoothAdress = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC);  // 蓝牙地址  72:D9:46:65:72:3A
                String sporttime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.SPORT, bluetoothAdress);   //1488441600000   ----- 上次保存的运动时间   时间戳   1491994800
                String sleeptime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.SLEEP, bluetoothAdress);   // 睡眠时间        ---- 上次保存的睡眠时间   日期     2017-04-13 09:00:00
                String hearttime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.HEART, bluetoothAdress);   // 心率时间        ---- 上次保存的心率时间   时间戳    1490095000
                String pressuretime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLOOD_PRESSURE, bluetoothAdress);   // 血压时间        ---- 上次保存的血压时间  日期     2018-03-26 09:00:00

                byte[] sportBytes = new byte[7];
                byte[] sleepBytes = new byte[7];
                byte[] heartBytes = new byte[7];
                byte[] pressureBytes = new byte[7];
                if (!StringUtils.isEmpty(sporttime)) {
                    String lastGetSportData = StringUtils.timestamp2Date(sporttime);   //由时间戳格式转为日期格式  2017-03-16 19:00:00     2017-04-12 19:00:00

                    int sportYear = Integer.valueOf(lastGetSportData.substring(2, 4));  // 11
                    int sportMonth = Integer.valueOf(lastGetSportData.substring(5, 7));  // 3
                    int sportRi = Integer.valueOf(lastGetSportData.substring(8, 10));  // 10
                    int sportShi = Integer.valueOf(lastGetSportData.substring(11, 13));

                    sportBytes[0] = (byte) 3;
                    sportBytes[1] = (byte) sportYear;
                    sportBytes[2] = (byte) sportMonth;
                    sportBytes[3] = (byte) sportRi;
                    sportBytes[4] = (byte) sportShi;
                    sportBytes[5] = (byte) 0;
                    sportBytes[6] = (byte) 0;
                }

                if (!StringUtils.isEmpty(sleeptime)) {
                    String lastGetSleepData = sleeptime;

                    int sleepYear = Integer.valueOf(lastGetSleepData.substring(2, 4));  // 11
                    int sleepMonth = Integer.valueOf(lastGetSleepData.substring(5, 7));  // 3
                    int sleepRi = Integer.valueOf(lastGetSleepData.substring(8, 10));  // 10
                    int sleepShi = Integer.valueOf(lastGetSleepData.substring(11, 13));
                    int sleepFen = Integer.valueOf(lastGetSleepData.substring(14, 16));

                    sleepBytes[0] = (byte) 1;
                    sleepBytes[1] = (byte) sleepYear;
                    sleepBytes[2] = (byte) sleepMonth;
                    sleepBytes[3] = (byte) sleepRi;
                    sleepBytes[4] = (byte) sleepShi;
                    sleepBytes[5] = (byte) sleepFen;
                    sportBytes[6] = (byte) 0;
                }


                if (!StringUtils.isEmpty(hearttime)) {
                    String lastGetHeartData = StringUtils.timestamp2Date(hearttime);

                    int heartYear = Integer.valueOf(lastGetHeartData.substring(2, 4));  // 11
                    int heartMonth = Integer.valueOf(lastGetHeartData.substring(5, 7));  // 3
                    int heartRi = Integer.valueOf(lastGetHeartData.substring(8, 10));  // 10
                    int heartShi = Integer.valueOf(lastGetHeartData.substring(11, 13));
                    int heartFen = Integer.valueOf(lastGetHeartData.substring(14, 16));
                    int heartMiao = Integer.valueOf(lastGetHeartData.substring(17, 19));

                    heartBytes[0] = (byte) 2;
                    heartBytes[1] = (byte) heartYear;
                    heartBytes[2] = (byte) heartMonth;
                    heartBytes[3] = (byte) heartRi;
                    heartBytes[4] = (byte) heartShi;
                    heartBytes[5] = (byte) heartFen;
                    heartBytes[6] = (byte) heartMiao;

                }

                if (!StringUtils.isEmpty(pressuretime)) {
                    String lastGetPressureData = pressuretime;

                    int pressureYear = Integer.valueOf(lastGetPressureData.substring(2, 4));
                    int pressureMonth = Integer.valueOf(lastGetPressureData.substring(5, 7));
                    int pressureRi = Integer.valueOf(lastGetPressureData.substring(8, 10));
                    int pressureShi = Integer.valueOf(lastGetPressureData.substring(11, 13));
                    int pressureFen = Integer.valueOf(lastGetPressureData.substring(14, 16));
                    int pressureSecond = Integer.valueOf(lastGetPressureData.substring(17, 18));

                    pressureBytes[0] = (byte) 5;
                    pressureBytes[1] = (byte) pressureYear;
                    pressureBytes[2] = (byte) pressureMonth;
                    pressureBytes[3] = (byte) pressureRi;
                    pressureBytes[4] = (byte) pressureShi;
                    pressureBytes[5] = (byte) pressureFen;
                    pressureBytes[6] = (byte) pressureSecond;
                }

                try {
                    // TODO --- 点击同步时，同步的是所有的数据（计步，睡眠，运动）  ----- 旧协议
                    if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {
                        BTNotificationApplication.needSendDataType = 0;
                        BTNotificationApplication.needReceDataNumber = 0;
                        //同步计步
                        if (StringUtils.isEmpty(sporttime)) {
                            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{3, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                            MainService.getInstance().writeToDevice(l2, true);
                            BTNotificationApplication.needSendDataType += 1;

                        } else {
                            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sportBytes);  // 传最后个日期的时间
                            MainService.getInstance().writeToDevice(l2, true);
                            BTNotificationApplication.needSendDataType += 1;
                        }

                        //同步睡眠
                        if (ISSYNWATCHINFO) {
                            if (SLEEP) {
                                if (StringUtils.isEmpty(sleeptime)) {
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{1, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                } else {
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sleepBytes);  // 传最后个日期的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                }
                            }
                        } else {
                            if (StringUtils.isEmpty(sleeptime)) {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{1, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            } else {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sleepBytes);  // 传最后个日期的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }
                        }


                        //同步心率
                        if (ISSYNWATCHINFO) {
                            if (HEART) {
                                if (StringUtils.isEmpty(hearttime)) {
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{2, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                } else {
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, heartBytes);  // 传最后个日期的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                }
                            }
                        } else {
                            if (StringUtils.isEmpty(hearttime)) {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{2, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            } else {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, heartBytes);  // 传最后个日期的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }
                        }

                        if (ISSYNWATCHINFO) {
                            if (BLOOD_PRESSURE) {
                                if (StringUtils.isEmpty(pressuretime)) {
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{5, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                } else {
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, pressureBytes);  // 传最后个日期的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                }
                            }
                        } else {
                            if (StringUtils.isEmpty(pressuretime)) {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{5, 0, 0, 0, 0, 0, 0});  // keyValue --- 可以传上次获取同步数据的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            } else {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, pressureBytes);  // 传最后个日期的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }
                        }

                        BTNotificationApplication.isSyncEnd = false;
                    } else if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {    //手环实时同步运动数据
                        syncStartTime = System.currentTimeMillis();

                        sendSyncData(3);  // 计步
                        sendSyncData(1);  // 睡眠
                        sendSyncData(2);  // 心率
                        sendSyncData(5);  // 血氧血压
//                        sendSyncData(6);  // 血氧血压

                        // todo ----
                        BTNotificationApplication.needReceiveNum = BTNotificationApplication.needGetSportDayNum + BTNotificationApplication.needGetSleepDayNum + BTNotificationApplication.needGetHeartDayNum; //todo ---- 需要获取的数据条数
                        Log.e("liuxiaodata", "需要收到的数据条数为--" + BTNotificationApplication.needReceiveNum);
                        Log.e("liuxiaodata", "需要收到的计步数据条数为--" + BTNotificationApplication.needGetSportDayNum);
                        Log.e("liuxiaodata", "需要收到的睡眠数据条数为--" + BTNotificationApplication.needGetSleepDayNum);
                        Log.e("liuxiaodata", "需要收到的心率数据条数为--" + BTNotificationApplication.needGetHeartDayNum);

                        BTNotificationApplication.isSyncEnd = false;   //TODO ---  开始同步数据将标志位 置为 false
                    } else if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {  //TODO  ---  mtk  同步数据
                        BluetoothMtkChat.getInstance().getWathchData();    //获取手表数据
                        BluetoothMtkChat.getInstance().syncRun();        //每天计步数据
                        BluetoothMtkChat.getInstance().sendApkState();  //前台运行
//                        BluetoothMtkChat.getInstance().syncEcg();
                        BluetoothMtkChat.getInstance().synTime(getActivity()); //同步时间
                        BTNotificationApplication.isSyncEnd = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //当前不是处于固件升级模式可同步数据
//                if(!(boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {
                if (null == loadingDialog) {  // && !loadingDialog.isShowing()
                    loadingDialog = new LoadingDialog(getActivity(), R.style.Custom_Progress, getString(R.string.userdata_synchronize));
                    loadingDialog.show();
                    handler.postDelayed(runnable, 1000 * 61);// 打开定时器，执行操作   1000 * 91
                }
//                }
                break;


            case R.id.data_bt_downturning: {  // 切换日期减
                Message msg = handler.obtainMessage();
                msg.what = SETNONETWORK;
                handler.sendMessage(msg);
                change_date = getCurDate();
                // 日期处理方法返回的日期
                String downtime_str = UTIL.getSubtractDay(change_date);

                setCurDate2(downtime_str);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                        judgmentSleepDB();
                        judgmentHealthDB();
                        judgmentBloodpressureDB();// 从本地数据库获取血压数据
                        judgmentOxygenDB();
                    }/////
                }).start();

//                judgmentRunDB();
//                judgmentSleepDB();
//                judgmentHealthDB();
//                judgmentBloodpressureDB();// 从本地数据库获取血压数据
//                judgmentOxygenDB();
            }

            break;
            case R.id.data_bt_upturning: {
                // TODO Auto-generated method stub
                change_date = getCurDate();
                Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                curtime_str = getDateFormat.format(curDate);
                if (change_date.equals(curtime_str)) {
                    Toast.makeText(getActivity(), R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = SETNONETWORK;
                    handler.sendMessage(msg);
                    uptime_str = UTIL.getAddDay(change_date);
                    setCurDate2(uptime_str);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                        judgmentSleepDB();
                        judgmentHealthDB();
                        judgmentBloodpressureDB();// 从本地数据库获取血压数据
                        judgmentOxygenDB();
                    }/////
                }).start();

//                judgmentRunDB();
//                judgmentSleepDB();
//                judgmentHealthDB();
//                judgmentBloodpressureDB();// 从本地数据库获取血压数据
//                judgmentOxygenDB();
            }
            break;
            case R.id.tv_sport_mode://左上角切换运动模式
                SportModePopu popu = new SportModePopu(mContext, handler, (RelativeLayout) homeView.findViewById(R.id.ll_homefragment));
                break;

            default:
                break;
        }

    }


    /**
     * 更新当前的血压及血氧数据
     */
    private void updataOxyAndBloodpressure(String data) {
        if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
            String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();
            if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "Bloodpressure")) {
                Bloodpressure aa = (Bloodpressure) (SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "Bloodpressure"));
                baohe_show.setText(aa.getHeightBlood());
                baohe_showtwo.setText(aa.getMinBlood());
                //
                baohe_showthere.setText((Integer.valueOf(aa.getHeightBlood() + "") + Integer.valueOf(aa.getMinBlood() + "")) / 2 + "");
                xieya_number.setText(100 + "");
                xieya_process_view.setMaxCount(200);
                xieya_process_view.setCurrentCount(200);
                if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "mycount")) {
                    int mycountb = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "mycount");
                    counts.setText(getResources().getString(R.string.Measurement_times) + mycountb);
                }
            } else {
                counts.setText(getResources().getString(R.string.Measurement_times));
                baohe_show.setText("0");
                baohe_showtwo.setText("0");
                baohe_showthere.setText("0");
                xieya_number.setText(0 + "");
                xieya_process_view.setMaxCount(200);
                xieya_process_view.setCurrentCount(200);
            }

            if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "AAA")) {
                List BBB = (List) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "AAA");
                bloodpressurebaohe_showtwo.setText(Collections.max(BBB) + "");//最高血氧
                bloodpressurebaohe_showthere.setText(Collections.min(BBB) + "");
                bloodpressurebaohe_show.setText((Integer.valueOf(bloodpressurebaohe_showthere.getText().toString()) + Integer.valueOf(bloodpressurebaohe_showtwo.getText().toString())) / 2 + "");
                xieyang_process_view.setMaxCount(200);
                xieyang_process_view.setCurrentCount(100 + Float.valueOf(BBB.get(BBB.size() - 1) + ""));
                Bloodpressure_num.setText(BBB.get(BBB.size() - 1) + "");

                if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "mycount_xieyang")) {
                    int mycount_xieyangb = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), data + myaddress + "mycount_xieyang");
                    bloodpressurebaoheconts.setText(getResources().getString(R.string.Measurement_times) + mycount_xieyangb);
                }

            } else {
                bloodpressurebaoheconts.setText(getResources().getString(R.string.Measurement_times));
                bloodpressurebaohe_showtwo.setText("0");//最高血氧
                bloodpressurebaohe_showthere.setText("0");
                bloodpressurebaohe_show.setText("0");
                xieyang_process_view.setMaxCount(200);
                xieyang_process_view.setCurrentCount(200);
                Bloodpressure_num.setText("0");
            }

            // EventBus.getDefault().post(new MessageEvent("updata_Bloodpressureer",data));//更新下数据报告

        }


    }

    private boolean isValidCorrect(double nLon, double nLat) {
        if (nLon < 72.004 || nLon > 137.8347 || nLat < 0.8293 || nLat > 55.8271) {
            return false;
        }
        return true;
    }

    /**
     * 发送广播给给MainService ---- 获取天气
     */
    private void sendReceiver() {
        Intent intent = new Intent();
        intent.setAction(MainService.WEATHER_DATA);
        mContext.sendBroadcast(intent);
    }

    /**
     * 内部内广播
     *
     * @author chendalin
     */
    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MainService.ACTION_NO_UNITS)) {
                weatherShow();
            }

            if ("android.intent.action.DATE_CHANGED".equals(intent.getAction())) {   // todo --- 切换日期

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                        judgmentSleepDB();
                        judgmentHealthDB();
                        judgmentBloodpressureDB();// 从本地数据库获取血压数据
                        judgmentOxygenDB();
                    }/////
                }).start();

                //Toast.makeText(BTNotificationApplication.getInstance(),"收到系统日期切换的广播了",Toast.LENGTH_SHORT).show();             
//                judgmentRunDB();
//                judgmentSleepDB();
//                judgmentHealthDB();
//                judgmentBloodpressureDB();// 从本地数据库获取血压数据
//                judgmentOxygenDB();


                return;
            }

            if (intent.getAction().equals(MainService.ACTION_SYNFINSH_SUCCESS)) {   //实时步数 || 实时心率   || intent.getAction().equals(MainService.ACTION_SSHEARTFINSH)
                setCurDate();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                    }/////
                }).start();


                return;
            }

            if (intent.getAction().equals(MainService.ACTION_SYNFINSH)) {

                String stepNum = intent.getStringExtra("step");
                if (null == loadingDialog) {   //todo ---  蓝牙连上的时候，同步数据（TMK平台）    && SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")
                    if (stepNum.equals("6")) {
                        Log.e("liuxiaodata", "收到6广播");
                        setCurDate();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                judgmentRunDB();
                                judgmentSleepDB();
                                judgmentHealthDB();
                                judgmentBloodpressureDB();// 从本地数据库获取血压数据
                                judgmentOxygenDB();
                            }/////
                        }).start();

                    }//////////////////////////////
                } else if (null != loadingDialog && !StringUtils.isEmpty(stepNum)) {

                    if (stepNum.equals("1")) {
                        Log.e("liuxiaodata", "收到1广播");
                        loadingDialog.setText(getString(R.string.userdata_synchronize1));
                    } else if (stepNum.equals("2")) {
                        Log.e("liuxiaodata", "收到2广播");
                        loadingDialog.setText(getString(R.string.userdata_synchronize2));
                    } else if (stepNum.equals("3")) {
                        Log.e("liuxiaodata", "收到3广播");
                        loadingDialog.setText(getString(R.string.userdata_synchronize3));
                    } else if (stepNum.equals("4")) {
                        Log.e("liuxiaodata", "收到4广播");
                        loadingDialog.setText(getString(R.string.userdata_synchronize4));
                    } else if (stepNum.equals("5")) {
                        Log.e("liuxiaodata", "收到5广播");
                        loadingDialog.setText(getString(R.string.userdata_synchronize5));
                    } else if (stepNum.equals("6")) {
                        Log.e("liuxiaodata", "收到6广播");

                        loadingDialog.setText(getString(R.string.userdata_synchronize_success));
                        Message msg = new Message();
                        msg.what = 22;
                        handler.sendMessageDelayed(msg, 1000);

                        if (handler != null) {
                            handler.removeCallbacks(runnable);
                        }
                        setCurDate();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                judgmentRunDB();
                                judgmentSleepDB();
                                judgmentHealthDB();
                                judgmentBloodpressureDB();// 从本地数据库获取血压数据
                                judgmentOxygenDB();
                            }/////
                        }).start();
                    }//////////////////////////////

                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                  /*  if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){
                        if(stepNum.equals("1")){
                            Log.e("liuxiaodata", "收到1广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize1));
                        }else  if(stepNum.equals("2")){
                            Log.e("liuxiaodata", "收到2广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize2));
                        }else if(stepNum.equals("3")){
                            Log.e("liuxiaodata", "收到3广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize3));
                        }else if(stepNum.equals("4")){
                            Log.e("liuxiaodata", "收到4广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize4));
                        }else if(stepNum.equals("5")){
                            Log.e("liuxiaodata", "收到5广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize5));
                        }else if(stepNum.equals("6")){
                            Log.e("liuxiaodata", "收到6广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize_success));
                            Message msg = new Message();
                            msg.what = 22;
                            handler.sendMessageDelayed(msg, 1000);
                            if(handler!=null){
                                handler.removeCallbacks(runnable);
                            }

                            curdatetv.setText(curtime_str);
                            sleep_curdatetv.setText(curtime_str);
                            heart_curdatetv.setText(curtime_str);
                            OXY_curdatetv.setText(curtime_str);
                            bloodpressure_curdatetv.setText(curtime_str);

                            judgmentRunDB();  // 更新计步数据
                            judgmentSleepDB();   // 手表数据同步完成
                            judgmentHealthDB(); // 更新心率数据
                            judgmentBloodpressureDB();// 从本地数据库获取血压数据
                            judgmentOxygenDB();
                        }/////////////////////////////////
                    }else {
                        curdatetv.setText(curtime_str);
                        sleep_curdatetv.setText(curtime_str);
                        heart_curdatetv.setText(curtime_str);
                        OXY_curdatetv.setText(curtime_str);
                        bloodpressure_curdatetv.setText(curtime_str);
                        if(stepNum.equals("1")){
                            Log.e("liuxiaodata", "收到1广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize1));
                            judgmentRunDB();  // 更新计步数据
                        }else  if(stepNum.equals("2")){
                            Log.e("liuxiaodata", "收到2广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize2));
                            judgmentSleepDB();   // 手表数据同步完成
                        }else if(stepNum.equals("3")){
                            Log.e("liuxiaodata", "收到3广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize3));
                            judgmentHealthDB(); // 更新心率数据
                        }else if(stepNum.equals("4")){
                            Log.e("liuxiaodata", "收到4广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize4));
                            judgmentBloodpressureDB();// 从本地数据库获取血压数据
                        }else if(stepNum.equals("5")){
                            Log.e("liuxiaodata", "收到5广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize5));
                            judgmentOxygenDB();
                        }else if(stepNum.equals("6")){
                            Log.e("liuxiaodata", "收到6广播");
                            loadingDialog.setText(getString(R.string.userdata_synchronize_success));
                            Message msg = new Message();
                            msg.what = 22;
                            handler.sendMessageDelayed(msg, 1000);
                            if(handler!=null){
                                handler.removeCallbacks(runnable);
                            }
                            judgmentRunDB();  // 更新计步数据
                            judgmentSleepDB();   // 手表数据同步完成
                            judgmentHealthDB(); // 更新心率数据
                            judgmentBloodpressureDB();// 从本地数据库获取血压数据
                            judgmentOxygenDB();
                        }/////////////////////////////////
                    }   */            ////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                }


                return;
            }

            if (intent.getAction().equals(MainService.ACTION_CHANGE_WATCH)) {   // TODO  切换设备   不同平台设备切换
                setCurDate();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                        judgmentSleepDB();
                        judgmentHealthDB();
                        judgmentBloodpressureDB();// 从本地数据库获取血压数据
                        judgmentOxygenDB();
                    }/////
                }).start();

                return;
            }
            if (intent.getAction().equals(MainService.ACTION_MACCHANGE)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        judgmentRunDB();
                        judgmentSleepDB();
                        judgmentHealthDB();
                        judgmentBloodpressureDB();// 从本地数据库获取血压数据
                        judgmentOxygenDB();
                    }/////
                }).start();

                return;
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //	 if(bluetoothDevice.getAddress().equals(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))){
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                // Log.e("dmm", bluetoothDevice.getName() + "====" + rssi);
                //	 }
                return;
            }
            if (intent.hasExtra("location")) {   // todo --- 收到同步数据完成的广播，有定位，重新显示天气    ？？？？？？？？？？？
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                } else {
                    weatherInit();
                }
            }
            if (intent.getAction().equals(MainService.ACTION_SYNNOTDATA)) {
                Toast.makeText(getActivity(), getString(R.string.now_is_null_syn), Toast.LENGTH_SHORT).show();
            }
            if (intent.getAction().equals(MainService.ACTION_SYNARTBO) && BTNotificationApplication.isSyncEnd) {   // todo  --- 实时血氧
              /*  String bo = intent.getStringExtra("bo");
                if (Integer.parseInt(bo) < 40) {
                   return;
               }
                tv_bo.setText(bo + " %");
                SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.LAST_BO,bo);*/  // todo  ---   实时血压 老页面的数据

                if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
                    String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                    //如果日期一样保存数据
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(curDate);
                    if (str.equals(getCurDate())) {
                        if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieyang")) {
                            mycount_xieyang = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieyang");
                            mycount_xieyang++;
                        } else {
                            mycount_xieyang++;
                        }

                    }
                    if (str.equals(getCurDate())) {
                        SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieyang", mycount_xieyang);
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentOxygenDB();  // 血氧
                        }/////
                    }).start();


                    //清空下次数 ，避免手环切换发生的次数问题
                    mycount_xieyang = 0;
                }

            }
            if (intent.getAction().equals(MainService.ACTION_SYNARTBP) && BTNotificationApplication.isSyncEnd) {
               /* String bp_min = intent.getStringExtra("bp_min");    // todo  --- 应该是无效代码  9999999999999999999999999999999999999999999
                String bp_max = intent.getStringExtra("bp_max");
                if (Integer.parseInt(bp_min)<=0){

                }else {
                    tv_bp_min.setText(bp_min+" mmHg");
                    SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.LAST_BP_MIN,bp_min);
                }
                if (Integer.parseInt(bp_max)<=0 || Integer.parseInt(bp_max)<=Integer.parseInt(bp_min)){

                }else {
                    tv_bp_max.setText(bp_max+" mmHg");
                    SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.LAST_BP_MAX,bp_max);
                }*/

                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(curDate);
                if (str.equals(getCurDate())) {
                    if (null != SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)) {
                        String myaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                        if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya")) {
                            mycount = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya");
                            mycount++;
                        } else {
                            mycount++;
                        }

                        if (null != SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya_err")) {
                            mycount_ERROR = (int) SharedPreferencesUtils.readObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya_err");
                            //90<收缩压<140  60<舒张压<90  当前日期对比下
                            if (!baohe_show.getText().toString().equals("0") && !baohe_showtwo.getText().toString().equals("0")) {
                                if (Integer.valueOf(baohe_show.getText().toString()) < 90 || Integer.valueOf(baohe_show.getText().toString()) > 140
                                        || Integer.valueOf(baohe_showtwo.getText().toString()) < 60 || Integer.valueOf(baohe_showtwo.getText().toString()) > 90) {
                                    mycount_ERROR++;
                                    SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya_err", mycount_ERROR);
                                }
                            }
                        } else {
                            if (!baohe_show.getText().toString().equals("0") && !baohe_showtwo.getText().toString().equals("0")) {
                                //90<收缩压<140  60<舒张压<90  当前日期对比下
                                if (Integer.valueOf(baohe_show.getText().toString()) < 90 || Integer.valueOf(baohe_show.getText().toString()) > 140
                                        || Integer.valueOf(baohe_showtwo.getText().toString()) < 60 || Integer.valueOf(baohe_showtwo.getText().toString()) > 90) {
                                    mycount_ERROR++;
                                    SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya_err", mycount_ERROR);
                                }
                            }
                        }
                        SharedPreferencesUtils.saveObject(BTNotificationApplication.getInstance(), getCurDate() + myaddress + "mycount_xieya", mycount);


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                judgmentBloodpressureDB();
                            }/////
                        }).start();


                        //清空下次数 ，避免手环切换发生的次数问题
                        mycount = 0;
                        mycount_ERROR = 0;
                    }
                }
            }

            if (intent.getAction().equals(MainService.ACTION_SYNARTHEART) && BTNotificationApplication.isSyncEnd) {       // todo ---- 实时心率
                String heart = intent.getStringExtra("heart");
//                String heartTime = intent.getStringExtra("time");
                if (!StringUtils.isEmpty(heart) && Integer.valueOf(heart) > 0) {
                    //todo  -- 当前页面不是当前日期 ，切换到当前日期页面
                    String mcurDate = getCurDate();  // 获取当前控件上的日期

                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                    String heartDay = getDateFormat.format(curDate);   // todo  --- 2017-06-14

                    if (!mcurDate.equals(heartDay)) { //不是当前日期，跳转到当前日期
                        Message msg = new Message();
                        msg.what = SETCURTIMEFORHEART;  // 设置当前的日期        SETCURTIME
                        msg.obj = heartDay;
                        handler.sendMessage(msg);

                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                judgmentHealthDB();
                            }/////
                        }).start();

                    }


                    /*if(heartDataList == null){
                        String strDate = curdatetv.getText().toString();
                        String choiceDate = arrangeDate(strDate);     // 整理日期数据
                        Query query = null;
                        if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC).equals("")) {  // 需要展示的设备的数据的mac地址
                            query = db.getHearDao().queryBuilder()
                                    .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))).where(HearDataDao.Properties.Date.eq(choiceDate)).build();
                        }
                        heartDataList = (ArrayList)query.list();
                    }

                    int max_heart = 0;
                    int min_heart = 100;
                    int avg_heart = 0;
                    for (int i = 0; i < heartDataList.size(); i++) {
                        max_heart = Math.max(Integer.parseInt(heartDataList.get(i).getHeartbeat()), max_heart);
                        min_heart = Math.min(Integer.parseInt(heartDataList.get(i).getHeartbeat()), min_heart);
                        avg_heart += Integer.parseInt(heartDataList.get(i).getHeartbeat());
                    }
                    if(heartDataList.size() == 0){
                        minimum_heart_text.setText("0");
                    }else {
                        minimum_heart_text.setText(min_heart + "");
                    }
                    highest_heart_text.setText(max_heart+"");
                    if(heartDataList.size() != 0) {
                        mean_heart_text.setText(avg_heart / heartDataList.size() + "");
                    }*/
                }
            }

            if (intent.getAction().equals(MainService.ACTION_WEATHER)) {  //MainService收到获取天气成功后，发出的广播
                weatherShow();
                if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                    if (MainService.getInstance().getState() == 3) {
                        String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE);
                        if( WEATHER_PUSH){ // !"601".equals(code) ||
                            L2Send.syncAppWeather();   // 同步天气
                        }
//                        L2Send.syncAppWeather();   // 同步天气给手环端
                    }
                }
                isInit = true;
            }
        }
    }


    private void weatherShow() {
//        if (UTIL.getLanguage().equals("zh")) {  //todo --- 中文才显示天气（现改为所有语言环境都显示天气）
        mTv_weather.setVisibility(View.VISIBLE);
        mWeather_icon.setVisibility(View.VISIBLE);
        sleep_tv_weather.setVisibility(View.VISIBLE);
        sleep_weather_icon.setVisibility(View.VISIBLE);
        heart_tv_weather.setVisibility(View.VISIBLE);
        heart_weather_icon.setVisibility(View.VISIBLE);

        String low = UTIL.readPre(mContext, "weather", "low");
        String high = UTIL.readPre(mContext, "weather", "high");
        String code = UTIL.readPre(mContext, "weather", "code");

        Log.d("weatherShow", "low == " + low + "high == " + high + "code == " + code);
        if (!StringUtils.isEmpty(low) && !StringUtils.isEmpty(high) && !StringUtils.isEmpty(code)) {
            if (SharedPreUtil.getParam(mContext, SharedPreUtil.USER
                    , SharedPreUtil.UNIT_TEMPERATURE, SharedPreUtil.YES).equals(SharedPreUtil.NO)) {
                low = (int) (32 + Integer.parseInt(low) * 1.8) + "";
                high = (int) (32 + Integer.parseInt(high) * 1.8) + "";
                mTv_weather.setText(low + "℉" + "-" + high + "℉");
                sleep_tv_weather.setText(low + "℉" + "-" + high + "℉");
                heart_tv_weather.setText(low + "℉" + "-" + high + "℉");
                Oxy_tv_weather.setText(low + "℉" + "-" + high + "℉");
                bloodpressure_tv_weather.setText(low + "℉" + "-" + high + "℉");
            } else {
                mTv_weather.setText(low + "℃" + "-" + high + "℃");
                sleep_tv_weather.setText(low + "℃" + "-" + high + "℃");
                heart_tv_weather.setText(low + "℃" + "-" + high + "℃");
                Oxy_tv_weather.setText(low + "℃" + "-" + high + "℃");
                bloodpressure_tv_weather.setText(low + "℃" + "-" + high + "℃");
            }
            //mTv_weather.setText(low + "℃" + "-" + high + "℃");

            int weathercode = Integer.parseInt(code);
            getPicIndex(weathercode);
            mWeather_icon.setBackgroundResource(dengjiimages[getSendCode(weathercode)]);  // dengjiimages[Integer.parseInt(code)]

            //sleep_tv_weather.setText(low + "℃" + "-" + high + "℃");
            sleep_weather_icon.setBackgroundResource(dengjiimages[getSendCode(weathercode)]);  // getPicIndex(weathercode)

            //heart_tv_weather.setText(low + "℃" + "-" + high + "℃");
            heart_weather_icon.setBackgroundResource(dengjiimages[getSendCode(weathercode)]);

            //Oxy_tv_weather.setText(low + "℃" + "-" + high + "℃");
            Oxy_weather_icon.setBackgroundResource(dengjiimages[getSendCode(weathercode)]);

            // bloodpressure_tv_weather.setText(low + "℃" + "-" + high + "℃");
            bloodpressure_weather_icon.setBackgroundResource(dengjiimages[getSendCode(weathercode)]);


        }
    }

    private static int getSendCode(int codeInt) {
        int code;
        if(codeInt >=100 && codeInt <104){
            code=0;   //  晴
        }else if(codeInt >= 104 && codeInt < 300){
            code = 1; // 阴
        }else if(codeInt >= 300 && codeInt <400){
            code=2;  //  雨
        }else if(codeInt >= 400 && codeInt <500){
            code=3; // 雪
        }else code=1;  // 阴
        return code;
    }

    private int getPicIndex(int code) {
        int dengjiIndex = 0;
        switch (code) {
            case 100:
                dengjiIndex = 0;
                break;
            case 101:
                dengjiIndex = 1;
                break;
            case 102:
                dengjiIndex = 2;
                break;
            case 103:
                dengjiIndex = 3;
                break;
            case 104:
                dengjiIndex = 4;
                break;
            case 200:
                dengjiIndex = 5;
                break;
            case 201:
                dengjiIndex = 6;
                break;
            case 202:
                dengjiIndex = 7;
                break;
            case 203:
                dengjiIndex = 8;
                break;
            case 204:
                dengjiIndex = 9;
                break;
            case 205:
                dengjiIndex = 10;
                break;
            case 206:
                dengjiIndex = 11;
                break;
            case 207:
                dengjiIndex = 12;
                break;
            case 208:
                dengjiIndex = 13;
                break;
            case 209:
                dengjiIndex = 14;
                break;
            case 210:
                dengjiIndex = 15;
                break;
            case 211:
                dengjiIndex = 16;
                break;
            case 212:
                dengjiIndex = 17;
                break;
            case 213:
                dengjiIndex = 18;
                break;
            case 300:
                dengjiIndex = 19;
                break;
            case 301:
                dengjiIndex = 20;
                break;
            case 302:
                dengjiIndex = 21;
                break;
            case 303:
                dengjiIndex = 22;
                break;
            case 304:
                dengjiIndex = 23;
                break;
            case 305:
                dengjiIndex = 24;
                break;
            case 306:
                dengjiIndex = 25;
                break;
            case 307:
                dengjiIndex = 26;
                break;
            case 308:
                dengjiIndex = 27;
                break;
            case 309:
                dengjiIndex = 28;
                break;
            case 310:
                dengjiIndex = 29;
                break;
            case 311:
                dengjiIndex = 30;
                break;
            case 312:
                dengjiIndex = 31;
                break;
            case 313:
                dengjiIndex = 32;
                break;
            case 400:
                dengjiIndex = 33;
                break;
            case 401:
                dengjiIndex = 34;
                break;
            case 402:
                dengjiIndex = 35;
                break;
            case 403:
                dengjiIndex = 36;
                break;
            case 404:
                dengjiIndex = 37;
                break;
            case 405:
                dengjiIndex = 38;
                break;
            case 406:
                dengjiIndex = 39;
                break;
            case 407:
                dengjiIndex = 40;
                break;
            case 500:
                dengjiIndex = 41;
                break;
            case 501:
                dengjiIndex = 42;
                break;
            case 502:
                dengjiIndex = 43;
                break;
            case 503:
                dengjiIndex = 44;
                break;
            case 504:
                dengjiIndex = 45;
                break;
            case 507:
                dengjiIndex = 46;
                break;
            case 508:
                dengjiIndex = 47;
                break;
            case 900:
                dengjiIndex = 48;
                break;
            case 901:
                dengjiIndex = 49;
                break;
            case 999:
                dengjiIndex = 50;
                break;
            default:
                dengjiIndex = 50;
        }
        return dengjiIndex;
    }

    private WheelView wheelView;
    private String[] pickers = new String[17];

    private void initTarget() {
        for (int i = 4; i < 21; i++) {   // 3000 --- 10000 0  ---------- 3 ---100      4000至20000   --- 4 20
            pickers[i - 4] = Integer.toString(i * 1000);
        }
    }

    private void initAlertDialog(String[] pickers) {  // todo --- 设置计步的运动目标的 选择框
        final String[] p = pickers;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu, null);
        wheelView = (WheelView) view.findViewById(R.id.targetWheel);
        wheelView.setAdapter(new StrericWheelAdapter(p));
        // wheelView.setCurrentItem(2);

        wheelView.setCurrentItem(goalstepcount / 1000 - 4);
        wheelView.setCyclic(true);
        wheelView.setInterpolator(new AnticipateOvershootInterpolator());

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
/*					alertDialog.dismiss();*/
                    mPopupWindow.dismiss();
                }
            }
        });
        view.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() { // 设置运动目标确定按钮
            @Override
            public void onClick(View v) {
                if (Utils.isFastClick()) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("goalstepfiles", Context.MODE_PRIVATE).edit();
//                    int sss = Integer.valueOf(wheelView.getCurrentItem());
//                    int ddd = Integer.parseInt(p[Integer.valueOf(wheelView.getCurrentItem())]);
                        editor.putInt("setgoalstepcount", Integer.parseInt(p[Integer.valueOf(wheelView.getCurrentItem())]));
                        editor.commit();
                        goalstepcount = Integer.parseInt(p[Integer.valueOf(wheelView.getCurrentItem())]);
                        setgoalstep();
                        //alertDialog.dismiss();
                        mPopupWindow.dismiss();
                        // todo  ---- 同时发送运动目标值给 ble平台设备
                        if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                            if (MainService.getInstance().getState() == 3) {
                                L2Send.syncSportTarget();   // 同步运动目标给手环端
                            }
                        } else if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {

//                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
//                        if(("G5").equals(device.getName())) {  // todo ---
                            if (MainService.getInstance().getState() == MainService.STATE_CONNECTED) {
                                int height = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT, "170"));
                                int weight = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.WEIGHT, "60"));
                                int sex = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.SEX, "0"));
                                String value = goalstepcount + "|" + sex + "|" + height + "|" + weight;
                                BluetoothMtkChat.getInstance().synUserInfo(value);
                            }
//                        }

//                        if(MainService.getInstance().getState() == MainService.STATE_CONNECTED){
//                            int height = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT));
//                            int weight = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.WEIGHT));
//                            int sex = Integer.parseInt(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.SEX));
//                            String value = goalstepcount + "|" + sex + "|" + height + "|" + weight;
//                            BluetoothMtkChat.getInstance().synUserInfo(value);
//                        }
                        }
                    }
                }
            }
        });

        mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.showAtLocation(homeView, Gravity.BOTTOM, 0, 0);
		/*alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.show();
		Point size = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getSize(size);
		int width = size.x;
		WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
		params.width = width;
		params.height = LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.BOTTOM;

		Window window = alertDialog.getWindow();
		window.setAttributes(params);
		window.setContentView(view);
		alertDialog.getWindow().setContentView(view);*/
    }

    /**
     * 定位改变回调接口
     *
     * @param location
     */
    @Override
    public void onLocationChanged(AMapLocation location) {

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (!isStartBaidu) {
            isStartBaidu = true;
            if (isValidCorrect(location.getLongitude(), location.getLatitude()) && Utils.getLanguage().equals("zh")) {  // 定位在国内且手机系统语言为中文   zh
                StatService.setAppKey("094802936a");  //todo 国内----  094802936a
            } else {   // 定位在国外
                StatService.setAppKey("f3491a12e1");  //todo 国外----  f3491a12e1
            }

            // 测试时，可以使用1秒钟session过期，这样不断的间隔1S启动退出会产生大量日志。
            StatService.setSessionTimeOut(30);

            // setOn也可以在AndroidManifest.xml文件中填写，BaiduMobAd_EXCEPTION_LOG，打开崩溃错误收集，默认是关闭的
            StatService.setOn(BTNotificationApplication.getInstance(), StatService.EXCEPTION_LOG);
            StatService.setLogSenderDelayed(0);


//        调用方式：StatService.setSendLogStrategy(this, SendStrategyEnum. SET_TIME_INTERVAL, 1, false); 第二个参数可选：
//        SendStrategyEnum.APP_START SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL 第三个参数：
//        这个参数在第二个参数选择SendStrategyEnum.SET_TIME_INTERVAL时生效、 取值。为1-24之间的整数,即1<=rtime_interval<=24，以小时为单位 第四个参数：
//        表示是否仅支持wifi下日志发送，若为true，表示仅在wifi环境下发送日志；若为false，表示可以在任何联网环境下发送日志
            StatService.setSendLogStrategy(BTNotificationApplication.getInstance(), SendStrategyEnum.APP_START, 1, true);

            // 调试百度统计SDK的Log开关，可以在Eclipse中看到sdk打印的日志，发布时去除调用，或者设置为false
            StatService.setDebugOn(false);

            StatService.start(BTNotificationApplication.getInstance());
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //todo  ---  天气请求机制：
       // ①仅在打开app时请求天气数据
//        ②app每次打开以后，查询本地缓存是否有天气及时效性，服务器返回的字段“updateTimes”是服务器数据的更新时间，app根据这个时间再和当前时间对比，是否在4*60分钟内，如果在，直接请求本地缓存天气（这时缓存数据和服务器数据一样，服务器也是4*60分钟，才更新一次，
//        注意时区，服务器时间和北京时间一样）。

        String updateTimes = (String) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.WEATHER, SharedPreUtil.WEATHER_UPDATE_TIMES, "");  // 2018-08-29 11:39:43
        if(!TextUtils.isEmpty(updateTimes)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
//                long updateTime = simpleDateFormat.parse(updateTimes).getTime();  // ---- 1535513983000
//                long thisTime = System.currentTimeMillis() / 1000;   // 当前的时间   ----   1535525873

                long updateTime = simpleDateFormat.parse(updateTimes).getTime() / 1000;  // ----                               1535513983 ---- 2018/8/29 11:39:43
                long thisTime = System.currentTimeMillis() / 1000;   // 当前的时间   ----      // todo --- 新的判断逻辑    1535526357 ---- 2018/8/29 15:5:57  = 12374 /60  = 206 /60 = 3.437 小时
                // 3.5 * 60 * 60 = 12600
                if (thisTime - updateTime < 3.5 * 60 * 60 ) {   //TODO-- < 3.5 * 60 * 60   ----   10 (测试用 )    比较秒数即可 ，当小于1个小时,不请求天气数据   4 * 60 * 60 * 1000      < 3.5 * 60 * 60 * 1000
                    Log.d(TAG, "refresh local weather data");
                    return;
                }else {
                    if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                        UTIL.savePre(mContext, "weather", "Latitude", "" + location.getLatitude());
                        UTIL.savePre(mContext, "weather", "Longitude", "" + location.getLongitude());
                        sendReceiver();
                    } else {
                        try {
                            String la = UTIL.readPre(mContext, "weather", "Latitude");
                            String lo = UTIL.readPre(mContext, "weather", "Longitude");
                            if (!StringUtils.isEmpty(la) && !StringUtils.isEmpty(lo)) {  // && !"".equals(city)
                                sendReceiver();  // todo --- 发送广播给给MainService ---- 获取天气
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                UTIL.savePre(mContext, "weather", "Latitude", "" + location.getLatitude());
                UTIL.savePre(mContext, "weather", "Longitude", "" + location.getLongitude());
                sendReceiver();
            } else {
                try {
                    String la = UTIL.readPre(mContext, "weather", "Latitude");
                    String lo = UTIL.readPre(mContext, "weather", "Longitude");
//                    String city = UTIL.readPre(mContext, "weather", "City");
                    if (!StringUtils.isEmpty(la) && !StringUtils.isEmpty(lo)) {  // && !"".equals(city)
                        sendReceiver();  // todo --- 发送广播给给MainService ---- 获取天气
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 更新运动数据
     */
    private void updateSportDate(String sportMode) {   // todo --- 在主页面 HomeFragment 中也 要显示运动数据
        Query query1 = db.getGpsPointDetailDao().queryBuilder()
                .where(GpsPointDetailDao.Properties.SportType.eq(sportMode))
                .orderDesc(GpsPointDetailDao.Properties.Mile)//距离排序
                .build();

//        Query query = null;
        List<GpsPointDetailData> gpsList = query1.list();
//        List<GpsPointDetailData> gpsList = db.getGpsPointDetailDao().queryBuilder().orderDesc(GpsPointDetailDao.Properties.Mile).list();
        if (gpsList != null && gpsList.size() > 0) {
            gpsPoint = gpsList.get(0);
        } else {
            gpsPoint = null;
        }

        if (gpsPoint != null && gpsList.size() > 0) {

            String avgSpeed = gpsPoint.getmCurrentSpeed();//获取当前配速（总用时/总距离）    setArrTotalSpeed
            int fen = 0;
            int miao = 0;
            if (!StringUtils.isEmpty(avgSpeed)&& Utils.isNumeric(avgSpeed)) {
                int avgPeisu = (int) Math.round(Double.parseDouble(avgSpeed));
                if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {  // SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))     isMetric
                    avgPeisu = Utils.getUnit_pace(avgPeisu);
                }
                fen = avgPeisu / 60;
                miao = avgPeisu % 60;
            }

            if (SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {  //公制
                home_distance_number.setText(Utils.decimalTo2(Double.valueOf(gpsPoint.getMile()) / 1000, 2) + "");
                home_kal_tv.setText(gpsPoint.getCalorie() + "");
                Integer ele = Utils.toint(gpsPoint.getAltitude());
                String eleString;
                if (ele >= 0) {
                    eleString = "+" + String.valueOf(ele);
                } else {
                    ele = 0;
                    eleString = String.valueOf(ele) + "";
                }
                home_altitude_tv.setText(eleString + "");
                home_time_tv.setText(gpsPoint.getSportTime() + "");   // 运动时长

      //          String arrPs[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组
      //          String m = arrPs[0];//分
       //         String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
       //         double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数

                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){  // todo --- 智能机配速

                    double countTimeMiao =  Math.round(Utils.getPaceForWatch1(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile())));//得到配速 数组   1：gpsData.getsTime()：运动时间  2： gpsData.getMile() 运动距离    配速 = 运动时间/距离
                    if(Integer.valueOf((int)(countTimeMiao/60.0)) > 1000){
                        home_pace_tv.setTextSize(10);   //  --- todo  ---
                    }
                    home_pace_tv.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf((int)(countTimeMiao/60.0)), Integer.valueOf((int)(countTimeMiao%60))));    // 设置配速的值

//                    home_pace_tv.setText(String.format(Locale.ENGLISH, "%1$02d'%2$02d''", Integer.valueOf(m),Math.round(sec)));
                }else{
                    home_pace_tv.setText(String.format(Locale.ENGLISH, "%1$02d'%2$02d''", Integer.valueOf(fen), miao));
                }
                home_distance_number_up.setText(getActivity().getString(R.string.kilometer));
                home_pace_tv_up.setText(getActivity().getString(R.string.realtime_minutes_km));
                home_kal_tv_up.setText(getActivity().getString(R.string.everyday_calorie));
                home_altitude_tv_up.setText(getActivity().getString(R.string.everyday_rice));
            } else {
                home_distance_number_up.setText(getActivity().getString(R.string.unit_mi));
                home_pace_tv_up.setText(getActivity().getString(R.string.unit_min_mi));
                home_kal_tv_up.setText(getActivity().getString(R.string.unit_kj));
                home_altitude_tv_up.setText(getActivity().getString(R.string.unit_ft));
                home_distance_number.setText(Utils.decimalTo2(Utils.getUnit_km(Double.valueOf(gpsPoint.getMile()) / 1000), 2) + "");
                home_kal_tv.setText(Utils.decimalTo2(Utils.getUnit_kal(Double.parseDouble(gpsPoint.getCalorie())), 1) + "");
                Integer ele = Utils.toint(gpsPoint.getAltitude());
                String eleString;
                if (ele >= 0) {
                    ele = (int) Utils.getUnit_mile(ele);
                    eleString = "+" + String.valueOf(ele);
                } else {
                    ele = 0;
                    eleString = String.valueOf(ele) + "";
                }
                home_altitude_tv.setText(eleString + "");
                home_time_tv.setText(gpsPoint.getSportTime() + "");   // 运动时长

             //   String arrPs[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组
             //   String m = arrPs[0];//分
             //   String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
             //   double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数

                home_pace_tv.setText(String.format(Locale.ENGLISH, "%1$02d'%2$02d''", Integer.valueOf(fen),  miao));
            }
           /* home_distance_number.setText(Utils.decimalTo2(Double.valueOf(gpsPoint.getMile()) / 1000, 2) + "");
            home_kal_tv.setText(gpsPoint.getCalorie() + "");
            Integer ele = Utils.toint(gpsPoint.getAltitude());
            String eleString;
            if (ele >= 0) {
                eleString = "+" + String.valueOf(ele);
            } else {
                ele = 0;
                eleString = String.valueOf(ele) + "";
            }
            home_altitude_tv.setText(eleString + "");
            home_time_tv.setText(gpsPoint.getSportTime() + "");   // 运动时长
            String arrPs[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//得到配速 数组
            String m = arrPs[0];//分
            String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
            double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
            home_pace_tv.setText(String.format("%1$02d'%2$02d''", Integer.valueOf(m), (int) sec));*/
        } else {//默认值
            if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                home_distance_number_up.setText(getActivity().getString(R.string.unit_mi));
                home_pace_tv_up.setText(getActivity().getString(R.string.unit_min_mi));
                home_kal_tv_up.setText(getActivity().getString(R.string.unit_kj));
                home_altitude_tv_up.setText(getActivity().getString(R.string.unit_ft));
            } else {
                home_distance_number_up.setText(getActivity().getString(R.string.kilometer));
                home_pace_tv_up.setText(getActivity().getString(R.string.realtime_minutes_km));
                home_kal_tv_up.setText(getActivity().getString(R.string.everyday_calorie));
                home_altitude_tv_up.setText(getActivity().getString(R.string.everyday_rice));
            }
            home_distance_number.setText(00.00 + "");
            home_kal_tv.setText(00 + "");
            home_altitude_tv.setText("+" + "0");
            home_time_tv.setText("00:00:00" + "");
            home_pace_tv.setText(String.format(Locale.ENGLISH, "%1$02d'%2$02d''", 0, 0));
        }
    }

    /**
     * 更新运动图标
     */
    private void updateSportView(String sportMode) {
        int sportType = Integer.valueOf(sportMode);
        switch (sportType) {
            case 1://健走
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.jianzou_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.jianzou));
                }
                home_run_text.setText(getResources().getString(R.string.sporttext_jianzou));
                break;
            case 2://
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.huwaipaoyundong_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.huwaipaoyundong));
                }
                home_run_text.setText(getResources().getString(R.string.sporttext_huwaipao));
                break;

            case 3://
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.shineipao_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.shineipao));
                }
                home_run_text.setText(getResources().getString(R.string.sporttext_shineipao));
                break;

            case 4://
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.dengshan_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.dengshan));
                }

                home_run_text.setText(getResources().getString(R.string.sporttext_dengshan));
                break;
            case 5://
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.yuyepao_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.yuyepao));
                }
                home_run_text.setText(getResources().getString(R.string.sporttext_yueyepao));
                break;

            case 6://
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.banma_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.banma));
                }
                home_run_text.setText(getResources().getString(R.string.sporttext_banma));
                break;
            case 7://
                if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.quanma_white));
                } else {//黑色主题
                    tv_sport_mode.setBackground(mContext.getResources().getDrawable(R.drawable.quanma));
                }
                home_run_text.setText(getResources().getString(R.string.sporttext_quanma));
                break;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
        if (mbroadcast != null) {
            getActivity().unregisterReceiver(mbroadcast);
        }

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void ShowMessage(String text) {
        if (null == toast) {
            toast = Toast.makeText(BTNotificationApplication.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void sendLast2DaysData(int index) {

    }

    private void sendLast3DaysData(int index) {
        if (index == 3) {
            BTNotificationApplication.needGetSportDayNum = 3;
        } else if (index == 1) {
            BTNotificationApplication.needGetSleepDayNum = 3;
        } else if (index == 2) {
            BTNotificationApplication.needGetHeartDayNum = 3;
        }

//        BTNotificationApplication.needGetSportDayNum = 3;
        byte[] key = new byte[7];
        key[0] = (byte) index;
        key[1] = (byte) (DateUtil.getYear() - 2000);
        key[2] = (byte) (DateUtil.getMonth());
        key[3] = (byte) (DateUtil.getCurrentMonthDay());
        key[4] = (byte) (DateUtil.getHour());
        key[5] = (byte) (DateUtil.getMinute());
        key[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        MainService.getInstance().writeToDevice(l2, true);

        byte[] key2 = new byte[7];
        key2[0] = (byte) index;
        key2[1] = (byte) (DateUtil.getLastDateYear(1) - 2000);
        key2[2] = (byte) (DateUtil.getLastDateMonth(1));
        key2[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte) (DateUtil.getHour());
        key2[5] = (byte) (DateUtil.getMinute());
        key2[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte) index;
        key3[1] = (byte) (DateUtil.getLastDateYear(2) - 2000);
        key3[2] = (byte) (DateUtil.getLastDateMonth(2));
        key3[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte) (DateUtil.getHour());
        key3[5] = (byte) (DateUtil.getMinute());
        key3[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

    }

    private void sendLast4DaysData(int index) {
        if (index == 3) {
            BTNotificationApplication.needGetSportDayNum = 4;
        } else if (index == 1) {
            BTNotificationApplication.needGetSleepDayNum = 4;
        } else if (index == 2) {
            BTNotificationApplication.needGetHeartDayNum = 4;
        }

//        BTNotificationApplication.needGetSportDayNum = 4;
        byte[] key = new byte[7];
        key[0] = (byte) index;
        key[1] = (byte) (DateUtil.getYear() - 2000);
        key[2] = (byte) (DateUtil.getMonth());
        key[3] = (byte) (DateUtil.getCurrentMonthDay());
        key[4] = (byte) (DateUtil.getHour());
        key[5] = (byte) (DateUtil.getMinute());
        key[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        MainService.getInstance().writeToDevice(l2, true);

        byte[] key2 = new byte[7];
        key2[0] = (byte) index;
        key2[1] = (byte) (DateUtil.getLastDateYear(1) - 2000);
        key2[2] = (byte) (DateUtil.getLastDateMonth(1));
        key2[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte) (DateUtil.getHour());
        key2[5] = (byte) (DateUtil.getMinute());
        key2[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte) index;
        key3[1] = (byte) (DateUtil.getLastDateYear(2) - 2000);
        key3[2] = (byte) (DateUtil.getLastDateMonth(2));
        key3[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte) (DateUtil.getHour());
        key3[5] = (byte) (DateUtil.getMinute());
        key3[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte) index;
        key4[1] = (byte) (DateUtil.getLastDateYear(3) - 2000);
        key4[2] = (byte) (DateUtil.getLastDateMonth(3));
        key4[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte) (DateUtil.getHour());
        key4[5] = (byte) (DateUtil.getMinute());
        key4[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        MainService.getInstance().writeToDevice(l24, true);
    }

    private void sendLast5DaysData(int index) {
        if (index == 3) {
            BTNotificationApplication.needGetSportDayNum = 5;
        } else if (index == 1) {
            BTNotificationApplication.needGetSleepDayNum = 5;
        } else if (index == 2) {
            BTNotificationApplication.needGetHeartDayNum = 5;
        }

//        BTNotificationApplication.needGetSportDayNum = 5;
        byte[] key = new byte[7];
        key[0] = (byte) index;
        key[1] = (byte) (DateUtil.getYear() - 2000);
        key[2] = (byte) (DateUtil.getMonth());
        key[3] = (byte) (DateUtil.getCurrentMonthDay());
        key[4] = (byte) (DateUtil.getHour());
        key[5] = (byte) (DateUtil.getMinute());
        key[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        MainService.getInstance().writeToDevice(l2, true);

        byte[] key2 = new byte[7];
        key2[0] = (byte) index;
        key2[1] = (byte) (DateUtil.getLastDateYear(1) - 2000);
        key2[2] = (byte) (DateUtil.getLastDateMonth(1));
        key2[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte) (DateUtil.getHour());
        key2[5] = (byte) (DateUtil.getMinute());
        key2[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte) index;
        key3[1] = (byte) (DateUtil.getLastDateYear(2) - 2000);
        key3[2] = (byte) (DateUtil.getLastDateMonth(2));
        key3[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte) (DateUtil.getHour());
        key3[5] = (byte) (DateUtil.getMinute());
        key3[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte) index;
        key4[1] = (byte) (DateUtil.getLastDateYear(3) - 2000);
        key4[2] = (byte) (DateUtil.getLastDateMonth(3));
        key4[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte) (DateUtil.getHour());
        key4[5] = (byte) (DateUtil.getMinute());
        key4[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        MainService.getInstance().writeToDevice(l24, true);

        byte[] key5 = new byte[7];
        key5[0] = (byte) index;
        key5[1] = (byte) (DateUtil.getLastDateYear(4) - 2000);
        key5[2] = (byte) (DateUtil.getLastDateMonth(4));
        key5[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(4));
        key5[4] = (byte) (DateUtil.getHour());
        key5[5] = (byte) (DateUtil.getMinute());
        key5[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l25 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key5);
        MainService.getInstance().writeToDevice(l25, true);
    }

    private void sendLast6DaysData(int index) {
        if (index == 3) {
            BTNotificationApplication.needGetSportDayNum = 6;
        } else if (index == 1) {
            BTNotificationApplication.needGetSleepDayNum = 6;
        } else if (index == 2) {
            BTNotificationApplication.needGetHeartDayNum = 6;
        }

//        BTNotificationApplication.needGetSportDayNum = 6;
        byte[] key = new byte[7];
        key[0] = (byte) index;
        key[1] = (byte) (DateUtil.getYear() - 2000);
        key[2] = (byte) (DateUtil.getMonth());
        key[3] = (byte) (DateUtil.getCurrentMonthDay());
        key[4] = (byte) (DateUtil.getHour());
        key[5] = (byte) (DateUtil.getMinute());
        key[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        MainService.getInstance().writeToDevice(l2, true);

        byte[] key2 = new byte[7];
        key2[0] = (byte) index;
        key2[1] = (byte) (DateUtil.getLastDateYear(1) - 2000);
        key2[2] = (byte) (DateUtil.getLastDateMonth(1));
        key2[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte) (DateUtil.getHour());
        key2[5] = (byte) (DateUtil.getMinute());
        key2[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte) index;
        key3[1] = (byte) (DateUtil.getLastDateYear(2) - 2000);
        key3[2] = (byte) (DateUtil.getLastDateMonth(2));
        key3[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte) (DateUtil.getHour());
        key3[5] = (byte) (DateUtil.getMinute());
        key3[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte) index;
        key4[1] = (byte) (DateUtil.getLastDateYear(3) - 2000);
        key4[2] = (byte) (DateUtil.getLastDateMonth(3));
        key4[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte) (DateUtil.getHour());
        key4[5] = (byte) (DateUtil.getMinute());
        key4[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        MainService.getInstance().writeToDevice(l24, true);

        byte[] key5 = new byte[7];
        key5[0] = (byte) index;
        key5[1] = (byte) (DateUtil.getLastDateYear(4) - 2000);
        key5[2] = (byte) (DateUtil.getLastDateMonth(4));
        key5[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(4));
        key5[4] = (byte) (DateUtil.getHour());
        key5[5] = (byte) (DateUtil.getMinute());
        key5[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l25 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key5);
        MainService.getInstance().writeToDevice(l25, true);

        byte[] key6 = new byte[7];
        key6[0] = (byte) index;
        key6[1] = (byte) (DateUtil.getLastDateYear(5) - 2000);
        key6[2] = (byte) (DateUtil.getLastDateMonth(5));
        key6[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(5));
        key6[4] = (byte) (DateUtil.getHour());
        key6[5] = (byte) (DateUtil.getMinute());
        key6[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l26 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key6);
        MainService.getInstance().writeToDevice(l26, true);
    }

    private void sendLast7DaysData(int index) {
        if (index == 3) {
            BTNotificationApplication.needGetSportDayNum = 7;
        } else if (index == 1) {
            BTNotificationApplication.needGetSleepDayNum = 7;
        } else if (index == 2) {
            BTNotificationApplication.needGetHeartDayNum = 7;
        }

        byte[] key = new byte[7];
        key[0] = (byte) index;
        key[1] = (byte) (DateUtil.getYear() - 2000);
        key[2] = (byte) (DateUtil.getMonth());
        key[3] = (byte) (DateUtil.getCurrentMonthDay());
        key[4] = (byte) (DateUtil.getHour());
        key[5] = (byte) (DateUtil.getMinute());
        key[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        Log.e(TAG, "第1天--" + UtilsLX.bytesToHexString(l2));
//                String resModebyteslx = UtilsLX.bytesToHexString(bytes);
        MainService.getInstance().writeToDevice(l2, true);


        byte[] key2 = new byte[7];
        key2[0] = (byte) index;
        key2[1] = (byte) (DateUtil.getLastDateYear(1) - 2000);
        key2[2] = (byte) (DateUtil.getLastDateMonth(1));
        key2[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte) (DateUtil.getHour());
        key2[5] = (byte) (DateUtil.getMinute());
        key2[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);

//                String t1 = UtilsLX.bytesToHexString(key2);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        String t2 = UtilsLX.bytesToHexString(l22);    // 0A00A000070311061E0F2706
        Log.e(TAG, "第2天--" + UtilsLX.bytesToHexString(l22));
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte) index;
        key3[1] = (byte) (DateUtil.getLastDateYear(2) - 2000);  // 17
        key3[2] = (byte) (DateUtil.getLastDateMonth(2));  // 6
        key3[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(2));   // 29
        key3[4] = (byte) (DateUtil.getHour());
        key3[5] = (byte) (DateUtil.getMinute());
        key3[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);

//                String t41 = UtilsLX.bytesToHexString(key3);

        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        String t3 = UtilsLX.bytesToHexString(l23);   // 0A00A00007030000000F2729
        Log.e(TAG, "第3天--" + UtilsLX.bytesToHexString(l23));
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte) index;
        key4[1] = (byte) (DateUtil.getLastDateYear(3) - 2000);
        key4[2] = (byte) (DateUtil.getLastDateMonth(3));
        key4[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte) (DateUtil.getHour());
        key4[5] = (byte) (DateUtil.getMinute());
        key4[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);

//                String t44 = UtilsLX.bytesToHexString(key4);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        String t4 = UtilsLX.bytesToHexString(l24);
        Log.e(TAG, "第4天--" + UtilsLX.bytesToHexString(l24));
        MainService.getInstance().writeToDevice(l24, true);

        byte[] key5 = new byte[7];
        key5[0] = (byte) index;
        key5[1] = (byte) (DateUtil.getLastDateYear(4) - 2000);
        key5[2] = (byte) (DateUtil.getLastDateMonth(4));
        key5[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(4));
        key5[4] = (byte) (DateUtil.getHour());
        key5[5] = (byte) (DateUtil.getMinute());
        key5[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l25 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key5);
        Log.e(TAG, "第5天--" + UtilsLX.bytesToHexString(l25));
        MainService.getInstance().writeToDevice(l25, true);

        byte[] key6 = new byte[7];
        key6[0] = (byte) index;
        key6[1] = (byte) (DateUtil.getLastDateYear(5) - 2000);
        key6[2] = (byte) (DateUtil.getLastDateMonth(5));
        key6[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(5));
        key6[4] = (byte) (DateUtil.getHour());
        key6[5] = (byte) (DateUtil.getMinute());
        key6[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l26 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key6);
        Log.e(TAG, "第6天--" + UtilsLX.bytesToHexString(l26));
        MainService.getInstance().writeToDevice(l26, true);

        byte[] key7 = new byte[7];
        key7[0] = (byte) index;
        key7[1] = (byte) (DateUtil.getLastDateYear(6) - 2000);
        key7[2] = (byte) (DateUtil.getLastDateMonth(6));
        key7[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(6));
        key7[4] = (byte) (DateUtil.getHour());
        key7[5] = (byte) (DateUtil.getMinute());
        key7[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l27 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key7);
        Log.e(TAG, "第7天--" + UtilsLX.bytesToHexString(l27));
        MainService.getInstance().writeToDevice(l27, true);
    }

    private void getLast6DaysData(Boolean isHasLast5DayData, int index) {
        if (!isHasLast5DayData) {  //TODO ---  前5天没有数据 --- 取前5天的数据  （包括今天）
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if (!StringUtils.isEmpty(isSync7DaysData)) {
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 6);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast6DaysData(3);
                } else if (index == 1) {
                    sendLast6DaysData(1);
                } else if (index == 2) {
                    sendLast6DaysData(2);
                }


            } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 6);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast6DaysData(3);
                } else if (index == 1) {
                    sendLast6DaysData(1);
                } else if (index == 2) {
                    sendLast6DaysData(2);
                }
            } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 6);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast6DaysData(3);
                } else if (index == 1) {
                    sendLast6DaysData(1);
                } else if (index == 2) {
                    sendLast6DaysData(2);
                }
            } else {
                if (index == 3) {
                    getLast5DaysData(isHasLast4DaySportData, index);
                } else if (index == 1) {
                    getLast5DaysData(isHasLast4DaySleepData, index);
                } else if (index == 2) {
                    getLast5DaysData(isHasLast4DayHeartData, index);
                }
            }
        } else {
            if (index == 3) {
                getLast5DaysData(isHasLast4DaySportData, index);
            } else if (index == 1) {
                getLast5DaysData(isHasLast4DaySleepData, index);
            } else if (index == 2) {
                getLast5DaysData(isHasLast4DayHeartData, index);
            }
        }
    }

    private void getLast5DaysData(Boolean isHasLast4DayData, int index) {
        if (!isHasLast4DayData) {  // TODO -- 前4天没有数据 --- 取前4天的数据
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if (!StringUtils.isEmpty(isSync7DaysData)) {
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 5);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast5DaysData(3);
                } else if (index == 1) {
                    sendLast5DaysData(1);
                } else if (index == 2) {
                    sendLast5DaysData(2);
                }
            } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据
                calendar.add(Calendar.DAY_OF_MONTH, 5);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast5DaysData(3);
                } else if (index == 1) {
                    sendLast5DaysData(1);
                } else if (index == 2) {
                    sendLast5DaysData(2);
                }
            } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据
                calendar.add(Calendar.DAY_OF_MONTH, 5);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                if (index == 3) {
                    sendLast5DaysData(3);
                } else if (index == 1) {
                    sendLast5DaysData(1);
                } else if (index == 2) {
                    sendLast5DaysData(2);
                }
            } else {
                if (index == 3) {
                    getLast4DaysData(isHasLast3DaySportData, index);
                } else if (index == 1) {
                    getLast4DaysData(isHasLast3DaySleepData, index);
                } else if (index == 2) {
                    getLast4DaysData(isHasLast3DayHeartData, index);
                }
            }
        } else {
            if (index == 3) {
                getLast4DaysData(isHasLast3DaySportData, index);
            } else if (index == 1) {
                getLast4DaysData(isHasLast3DaySleepData, index);
            } else if (index == 2) {
                getLast4DaysData(isHasLast3DayHeartData, index);
            }
        }
    }

    private void getLast4DaysData(Boolean isHasLast3DayData, int index) {
        if (!isHasLast3DayData) {  //TODO --  前3天没有数据 --- 取前3天的数据
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if (!StringUtils.isEmpty(isSync7DaysData)) {
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 4);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast4DaysData(3);
                } else if (index == 1) {
                    sendLast4DaysData(1);
                } else if (index == 2) {
                    sendLast4DaysData(2);
                }

            } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 4);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast4DaysData(3);
                } else if (index == 1) {
                    sendLast4DaysData(1);
                } else if (index == 2) {
                    sendLast4DaysData(2);
                }

            } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 4);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                if (index == 3) {
                    sendLast4DaysData(3);
                } else if (index == 1) {
                    sendLast4DaysData(1);
                } else if (index == 2) {
                    sendLast4DaysData(2);
                }

            } else {
                if (index == 3) {
                    getLast3DaysData(isHasLast2DaySportData, index);
                } else if (index == 1) {
                    getLast3DaysData(isHasLast2DaySleepData, index);
                } else if (index == 2) {
                    getLast3DaysData(isHasLast2DayHeartData, index);
                }
            }
        } else {
            if (index == 3) {
                getLast3DaysData(isHasLast2DaySportData, index);
            } else if (index == 1) {
                getLast3DaysData(isHasLast2DaySleepData, index);
            } else if (index == 2) {
                getLast3DaysData(isHasLast2DayHeartData, index);
            }
        }
    }

    private void getLast3DaysData(Boolean isHasLast2DayData, int index) {
        if (!isHasLast2DayData) {  //TODO 前2天没有数据 --- 取前2天的数据
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if (!StringUtils.isEmpty(isSync7DaysData)) {
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 3);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                if (index == 3) {
                    sendLast3DaysData(3);
                } else if (index == 1) {
                    sendLast3DaysData(1);
                } else if (index == 2) {
                    sendLast3DaysData(2);
                }
            } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 3);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast3DaysData(3);
                } else if (index == 1) {
                    sendLast3DaysData(1);
                } else if (index == 2) {
                    sendLast3DaysData(2);
                }
            } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 3);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if (index == 3) {
                    sendLast3DaysData(3);
                } else if (index == 1) {
                    sendLast3DaysData(1);
                } else if (index == 2) {
                    sendLast3DaysData(2);
                }
            } else {
                if (index == 3) {
                    getLast2DaysData(isHasLast1DaySportData, index);
                } else if (index == 1) {
                    getLast2DaysData(isHasLast1DaySleepData, index);
                } else if (index == 2) {
                    getLast2DaysData(isHasLast1DayHeartData, index);
                }
            }
        } else {
            if (index == 3) {
                getLast2DaysData(isHasLast1DaySportData, index);
            } else if (index == 1) {
                getLast2DaysData(isHasLast1DaySleepData, index);
            } else if (index == 2) {
                getLast2DaysData(isHasLast1DayHeartData, index);
            }
        }
    }

    private void getLast2DaysData(Boolean isHasLast2DayData, int index) {

        if (index == 3) {
            BTNotificationApplication.needGetSportDayNum = 2;
        } else if (index == 1) {
            BTNotificationApplication.needGetSleepDayNum = 2;
        } else if (index == 2) {
            BTNotificationApplication.needGetHeartDayNum = 2;
        }

        // TODO 前1天没有数据 --- 取前1天的数据(默认取两天的数据)
        byte[] key = new byte[7];
        key[0] = (byte) index;
        key[1] = (byte) (DateUtil.getYear() - 2000);
        key[2] = (byte) (DateUtil.getMonth());
        key[3] = (byte) (DateUtil.getCurrentMonthDay());
        key[4] = (byte) (DateUtil.getHour());
        key[5] = (byte) (DateUtil.getMinute());
        key[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        MainService.getInstance().writeToDevice(l2, true);

        byte[] key2 = new byte[7];
        key2[0] = (byte) index;
        key2[1] = (byte) (DateUtil.getLastDateYear(1) - 2000);
        key2[2] = (byte) (DateUtil.getLastDateMonth(1));
        key2[3] = (byte) (DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte) (DateUtil.getHour());
        key2[5] = (byte) (DateUtil.getMinute());
        key2[6] = (byte) ((System.currentTimeMillis() / 1000) % 60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

    }

    //    private  int sportIndex = 3;
//private  int sleepIndex = 1;
//    private  int heartIndex = 2;
    private boolean isHasLast1DaySportData = false;
    private boolean isHasLast2DaySportData = false;
    private boolean isHasLast3DaySportData = false;
    private boolean isHasLast4DaySportData = false;
    private boolean isHasLast5DaySportData = false;
    private boolean isHasLast6DaySportData = false;


    private boolean isHasLast1DaySleepData = false;
    private boolean isHasLast2DaySleepData = false;
    private boolean isHasLast3DaySleepData = false;
    private boolean isHasLast4DaySleepData = false;
    private boolean isHasLast5DaySleepData = false;
    private boolean isHasLast6DaySleepData = false;


    private boolean isHasLast1DayHeartData = false;
    private boolean isHasLast2DayHeartData = false;
    private boolean isHasLast3DayHeartData = false;
    private boolean isHasLast4DayHeartData = false;
    private boolean isHasLast5DayHeartData = false;
    private boolean isHasLast6DayHeartData = false;

    public void sendSyncData(int index) {
        //todo ---- 1：第一次同步时取 7 天的数据，后面都取两天的数据  （）
        if (db == null) {
            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
        }
        if (index == 3) {  // 计步  --- X2只有分段步数
            Query query = null;
            query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(RunDataDao.Properties.Step.notEq("0"))
                    .build();  // 1489824000     2017-03-16 19:00:00    .where(RunDataDao.Properties.Date.eq(arr.get(1).getBinTime().substring(0, 10)))   ---   .where(RunDataDao.Properties.Step.notEq("0")).build();
            List<RunData> slist = query.list();  // TODO ---获取到本地运动所有的计步数据

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
            String mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 前1天的数据     ---- 2017-06-30
            isHasLast1DaySportData = false;

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) - 2);
            String mcurDate2 = getDateFormat.format(calendar2.getTime());   // 2017-06-26       前2天的数据    ----- 2017-06-29
            isHasLast2DaySportData = false;

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.DAY_OF_MONTH, calendar3.get(Calendar.DAY_OF_MONTH) - 3);
            String mcurDate3 = getDateFormat.format(calendar3.getTime());   // 2017-06-26       前3天的数据
            isHasLast3DaySportData = false;

            Calendar calendar4 = Calendar.getInstance();
            calendar4.set(Calendar.DAY_OF_MONTH, calendar4.get(Calendar.DAY_OF_MONTH) - 4);
            String mcurDate4 = getDateFormat.format(calendar4.getTime());  // 2017-06-25        前4天的数据
            isHasLast4DaySportData = false;

            Calendar calendar5 = Calendar.getInstance();
            calendar5.set(Calendar.DAY_OF_MONTH, calendar5.get(Calendar.DAY_OF_MONTH) - 5);
            String mcurDate5 = getDateFormat.format(calendar5.getTime());  // 2017-06-24        前5天的数据
            isHasLast5DaySportData = false;

            Calendar calendar6 = Calendar.getInstance();
            calendar6.set(Calendar.DAY_OF_MONTH, calendar6.get(Calendar.DAY_OF_MONTH) - 6);
            String mcurDate6 = getDateFormat.format(calendar6.getTime()); // 2017-06-23         前6天的数据
            isHasLast6DaySportData = false;
            if (slist.size() > 0) {
                for (int i = 0; i < slist.size(); i++) {  // TODO --- 遍历所有的计步数据，获取该条数据对应的日期
                    String mItemData = slist.get(i).getDate(); // 对应条目的日期
                    if (mItemData.equals(mcurDate1)) {  // 前1天有数据
                        isHasLast1DaySportData = true;
                    }
                    if (mItemData.equals(mcurDate2)) {  // 前2天有数据
                        isHasLast2DaySportData = true;
                    }
                    if (mItemData.equals(mcurDate3)) {  // 前3天有数据
                        isHasLast3DaySportData = true;
                    }
                    if (mItemData.equals(mcurDate4)) {  // 前4天有数据
                        isHasLast4DaySportData = true;
                    }
                    if (mItemData.equals(mcurDate5)) {  // 前5天有数据
                        isHasLast5DaySportData = true;
                    }
                    if (mItemData.equals(mcurDate6)) {  // 前6天有数据
                        isHasLast6DaySportData = true;
                    }
                }
            }
//            }

            if (!isHasLast6DaySportData) { // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
                String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
                String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                String[] oldRecords = new String[2];
                if (!StringUtils.isEmpty(isSync7DaysData)) {
                    oldRecords = isSync7DaysData.split("#");
                }

                if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天 2017-09-25    2017-10-02    ----- 将此日期 保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(3);
                } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(3);
                } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(3);
                } else {
                    getLast6DaysData(isHasLast5DaySportData, 3);
                }
            } else {
                getLast6DaysData(isHasLast5DaySportData, 3);
            }
        } else if (index == 1) {  // todo ---- 睡眠                                9999999999999999999999999999999999999999999999999999999999999999999999999
            Query query = null;
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
                query = db.getSleepDao().queryBuilder()
                        .where(SleepDataDao.Properties.Mac
                                .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            } else {
                query = db.getSleepDao().queryBuilder()
                        .where(SleepDataDao.Properties.Mac
                                .eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).build();
            }
            List<SleepData> slist = query.list();  // TODO ---获取到本地睡眠所有的数据

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  // 2017-06-28     ----- 2017-07-01

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
            String mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 前1天的数据     ---- 2017-06-30
            isHasLast1DaySleepData = false;

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) - 2);
            String mcurDate2 = getDateFormat.format(calendar2.getTime());   // 2017-06-26       前2天的数据    ----- 2017-06-29
            isHasLast2DaySleepData = false;

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.DAY_OF_MONTH, calendar3.get(Calendar.DAY_OF_MONTH) - 3);
            String mcurDate3 = getDateFormat.format(calendar3.getTime());   // 2017-06-26       前3天的数据
            isHasLast3DaySleepData = false;

            Calendar calendar4 = Calendar.getInstance();
            calendar4.set(Calendar.DAY_OF_MONTH, calendar4.get(Calendar.DAY_OF_MONTH) - 4);
            String mcurDate4 = getDateFormat.format(calendar4.getTime());  // 2017-06-25        前4天的数据
            isHasLast4DaySleepData = false;

            Calendar calendar5 = Calendar.getInstance();
            calendar5.set(Calendar.DAY_OF_MONTH, calendar5.get(Calendar.DAY_OF_MONTH) - 5);
            String mcurDate5 = getDateFormat.format(calendar5.getTime());  // 2017-06-24        前5天的数据
            isHasLast5DaySleepData = false;

            Calendar calendar6 = Calendar.getInstance();
            calendar6.set(Calendar.DAY_OF_MONTH, calendar6.get(Calendar.DAY_OF_MONTH) - 6);
            String mcurDate6 = getDateFormat.format(calendar6.getTime()); // 2017-06-23         前6天的数据
            isHasLast6DaySleepData = false;

            if (slist.size() > 0) {
                for (int i = 0; i < slist.size(); i++) {  // TODO --- 遍历所有的计步数据，获取该条数据对应的日期
                    String mItemData = slist.get(i).getDate(); // 对应条目的日期
                    if (mItemData.equals(mcurDate1)) {  // 前1天有数据
                        isHasLast1DaySleepData = true;
                    }
                    if (mItemData.equals(mcurDate2)) {  // 前2天有数据
                        isHasLast2DaySleepData = true;
                    }
                    if (mItemData.equals(mcurDate3)) {  // 前3天有数据
                        isHasLast3DaySleepData = true;
                    }
                    if (mItemData.equals(mcurDate4)) {  // 前4天有数据
                        isHasLast4DaySleepData = true;
                    }
                    if (mItemData.equals(mcurDate5)) {  // 前5天有数据
                        isHasLast5DaySleepData = true;
                    }
                    if (mItemData.equals(mcurDate6)) {  // 前6天有数据
                        isHasLast6DaySleepData = true;
                    }
                }
            }
            if (!isHasLast6DaySleepData) { // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
                String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
                String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                String[] oldRecords = new String[2];
                if (!StringUtils.isEmpty(isSync7DaysData)) {
                    oldRecords = isSync7DaysData.split("#");
                }
                if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                    sendLast7DaysData(1);
                } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                    sendLast7DaysData(1);
                } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                    sendLast7DaysData(1);
                } else {
                    getLast6DaysData(isHasLast5DaySleepData, 1);
                }
            } else {
                getLast6DaysData(isHasLast5DaySleepData, 1);
            }
        } else if (index == 2) {  // todo ---- 心率     999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999
            Query query = null;
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
                query = db.getHearDao().queryBuilder()
                        .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            } else {  //  不需要展示的设备的数据的mac地址
                query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).build();  // 根据日期 查询 运动数据
            }
//            query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Date.eq(strDate)).orderAsc(HearDataDao.Properties.Times).build();    // todo ---- 需添加按mac 地址查询
            List<HearData> slist = query.list();  // TODO ---获取到本地心率所有的数据

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String mcurDate = getDateFormat.format(calendar.getTime());  // 2017-06-28     ----- 2017-07-01

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
            String mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 前1天的数据     ---- 2017-06-30
            isHasLast1DayHeartData = false;

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) - 2);
            String mcurDate2 = getDateFormat.format(calendar2.getTime());   // 2017-06-26       前2天的数据    ----- 2017-06-29
            isHasLast2DayHeartData = false;

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.DAY_OF_MONTH, calendar3.get(Calendar.DAY_OF_MONTH) - 3);
            String mcurDate3 = getDateFormat.format(calendar3.getTime());   // 2017-06-26       前3天的数据
            isHasLast3DayHeartData = false;

            Calendar calendar4 = Calendar.getInstance();
            calendar4.set(Calendar.DAY_OF_MONTH, calendar4.get(Calendar.DAY_OF_MONTH) - 4);
            String mcurDate4 = getDateFormat.format(calendar4.getTime());  // 2017-06-25        前4天的数据
            isHasLast4DayHeartData = false;

            Calendar calendar5 = Calendar.getInstance();
            calendar5.set(Calendar.DAY_OF_MONTH, calendar5.get(Calendar.DAY_OF_MONTH) - 5);
            String mcurDate5 = getDateFormat.format(calendar5.getTime());  // 2017-06-24        前5天的数据
            isHasLast5DayHeartData = false;

            Calendar calendar6 = Calendar.getInstance();
            calendar6.set(Calendar.DAY_OF_MONTH, calendar6.get(Calendar.DAY_OF_MONTH) - 6);
            String mcurDate6 = getDateFormat.format(calendar6.getTime()); // 2017-06-23         前6天的数据
            isHasLast6DayHeartData = false;

            if (slist.size() > 0) {
                for (int i = 0; i < slist.size(); i++) {  // TODO --- 遍历所有的计步数据，获取该条数据对应的日期
                    String mItemData = slist.get(i).getDate(); // 对应条目的日期
                    if (mItemData.equals(mcurDate1)) {  // 前1天有数据
                        isHasLast1DayHeartData = true;
                    }

                    if (mItemData.equals(mcurDate2)) {  // 前2天有数据
                        isHasLast2DayHeartData = true;
                    }

                    if (mItemData.equals(mcurDate3)) {  // 前3天有数据
                        isHasLast3DayHeartData = true;
                    }

                    if (mItemData.equals(mcurDate4)) {  // 前4天有数据
                        isHasLast4DayHeartData = true;
                    }

                    if (mItemData.equals(mcurDate5)) {  // 前5天有数据
                        isHasLast5DayHeartData = true;
                    }

                    if (mItemData.equals(mcurDate6)) {  // 前6天有数据
                        isHasLast6DayHeartData = true;
                    }
                }
            }

            if (!isHasLast6DayHeartData) { // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
                String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
                String curMacaddress = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                String[] oldRecords = new String[2];
                if (!StringUtils.isEmpty(isSync7DaysData)) {
                    oldRecords = isSync7DaysData.split("#");
                }

                if (StringUtils.isEmpty(isSync7DaysData)) {      //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(2);
                } else if (oldRecords[0].equals("0")) {        //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(2);
                } else if (oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)) {  // todo -- 取过7天的数据，但不是当前设备
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#" + curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                    sendLast7DaysData(2);
                } else {
                    getLast6DaysData(isHasLast5DayHeartData, 2);
                }
            } else {
                getLast6DaysData(isHasLast5DayHeartData, 2);
            }
        }
        else if(index == 6)
        {
            byte[] key = new byte[1];
            key[0] = (byte) 6;
            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
            Log.e(TAG, "第1天--" + UtilsLX.bytesToHexString(l2));
//                String resModebyteslx = UtilsLX.bytesToHexString(bytes);
            MainService.getInstance().writeToDevice(l2, true);
        }
    }///

    /**
     * 跳转到报告界面
     * 2017.10.23
     *
     * @param index 0计步 1睡眠 2心率 3血压 4血氧
     */
    public void startPresentationActivity(int index) {
        Intent intent = new Intent(getActivity(), PresentationActivity.class);
        intent.putExtra("index", index);//第几个页面
        intent.putExtra("time", getCurDate());//日期   2017-10-23
        startActivity(intent);
    }

    private class synHeartDataRunnable implements Runnable {

        @Override
        public void run() {
            judgmentHealthDB(); // 更新心率数据
        }
    }

    private class synXueyaDataRunnable implements Runnable {

        @Override
        public void run() {
            judgmentBloodpressureDB();// 从本地数据库获取血压数据
        }
    }

    private class synXueyangDataRunnable implements Runnable {

        @Override
        public void run() {
            judgmentOxygenDB();  // 血氧
        }
    }

    private class synDataRunnable implements Runnable {
        @Override
        public void run() {
            judgmentRunDB();
            judgmentSleepDB();
            judgmentHealthDB();
            judgmentBloodpressureDB();// 从本地数据库获取血压数据
            judgmentOxygenDB();
        }
    }

    private class synStepDataRunnable implements Runnable {
        @Override
        public void run() {
            judgmentRunDB();
        }
    }
    public void showLoadingDialogNew(String content) {
        loadingDialog = new LoadingDialog(getActivity(), R.style.Custom_Progress, content);
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (loadingDialog == null)
            return;
        loadingDialog.cancel();
        loadingDialog = null;
    }
}
