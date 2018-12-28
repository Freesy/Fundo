package com.szkct.weloopbtsmartdevice.view;

import android.app.Activity;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.kct.fundo.btnotification.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.mediatek.leprofiles.BlePxpFmpConstants;
import com.mediatek.leprofiles.PxpFmStatusRegister;
import com.mediatek.wearable.WearableManager;
import com.mtk.app.bluetoothle.LocalPxpFmpController;
import com.mtk.app.notification.NeNotificationService;
import com.mtk.app.notification.NotificationAppListActivity;
import com.mtk.app.remotecamera.RemoteCamera;
import com.szkct.adapter.BleConnectAdapter;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.bluetoothgyl.UtilsLX;
import com.szkct.weloopbtsmartdevice.activity.AlarmClockActivity;
import com.szkct.weloopbtsmartdevice.activity.AlarmModeActivity;
import com.szkct.weloopbtsmartdevice.activity.CalibrationActivity;
import com.szkct.weloopbtsmartdevice.activity.HeartAutoCheckActivity;
import com.szkct.weloopbtsmartdevice.activity.HeartEnterCheckActivity;
import com.szkct.weloopbtsmartdevice.activity.LinkBleActivity;
import com.szkct.weloopbtsmartdevice.activity.NewElectronicInvoiceActivity;
import com.szkct.weloopbtsmartdevice.activity.NewLoginPhoneActivity;
import com.szkct.weloopbtsmartdevice.activity.NoFazeModeActivity;
import com.szkct.weloopbtsmartdevice.activity.ReceivingCodeActivity;
import com.szkct.weloopbtsmartdevice.activity.SedentaryReminderActivity;
import com.szkct.weloopbtsmartdevice.activity.TemperatureActivity;
import com.szkct.weloopbtsmartdevice.activity.UnitSettingActivity;
import com.szkct.weloopbtsmartdevice.activity.WXMovementActivity;
import com.szkct.weloopbtsmartdevice.activity.WatchPushActivity;
import com.szkct.weloopbtsmartdevice.activity.WatchPushActivityNew;
import com.szkct.weloopbtsmartdevice.activity.WaterClockActivity;
import com.szkct.weloopbtsmartdevice.data.PushPicContent;
import com.szkct.weloopbtsmartdevice.data.advertisingBean;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.main.AboutActivity;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.BTcallActivity;
import com.szkct.weloopbtsmartdevice.main.BtInputActivity;
import com.szkct.weloopbtsmartdevice.main.FirmWareUpdateActivity;
import com.szkct.weloopbtsmartdevice.main.FirmWareUpdateActivityForBK;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.main.SoscallActivity;
import com.szkct.weloopbtsmartdevice.main.UserHelpActivity;
import com.szkct.weloopbtsmartdevice.main.Wifiactivity;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.Constants;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.HidConncetUtil;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.thefinestartist.finestwebview.FinestWebView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.Query;

import static com.kct.fundo.btnotification.R.id.unit_setting_rel;
import static com.szkct.weloopbtsmartdevice.main.MainService.ALARM_CLOCK;
import static com.szkct.weloopbtsmartdevice.main.MainService.ASSISTANT_INPUT;
import static com.szkct.weloopbtsmartdevice.main.MainService.AUTO_HEART;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLEMUSIC;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.BODYTEMPERATURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.BT_CALL;
import static com.szkct.weloopbtsmartdevice.main.MainService.CAMEAR;
import static com.szkct.weloopbtsmartdevice.main.MainService.CONNECT_FAIL;
import static com.szkct.weloopbtsmartdevice.main.MainService.CONSTANTS;
import static com.szkct.weloopbtsmartdevice.main.MainService.DIAL_PUSH;
import static com.szkct.weloopbtsmartdevice.main.MainService.ECG;
import static com.szkct.weloopbtsmartdevice.main.MainService.FAPIAO;
import static com.szkct.weloopbtsmartdevice.main.MainService.FAZE_MODE;
import static com.szkct.weloopbtsmartdevice.main.MainService.FIRMWARE_SUPPORT;
import static com.szkct.weloopbtsmartdevice.main.MainService.GESTURE_CONTROL;
import static com.szkct.weloopbtsmartdevice.main.MainService.ISSYNWATCHINFO;
import static com.szkct.weloopbtsmartdevice.main.MainService.MESSAGE_PUSH;
import static com.szkct.weloopbtsmartdevice.main.MainService.POINTER_CALIBRATION;
import static com.szkct.weloopbtsmartdevice.main.MainService.PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.REMIND_MODE;
import static com.szkct.weloopbtsmartdevice.main.MainService.SEDENTARY_CLOCK;
import static com.szkct.weloopbtsmartdevice.main.MainService.SHOUKUANEWM;
import static com.szkct.weloopbtsmartdevice.main.MainService.SMS_NOTIFICATION;
import static com.szkct.weloopbtsmartdevice.main.MainService.SOS_CALL;
import static com.szkct.weloopbtsmartdevice.main.MainService.STATE_DISCONNECTED;
import static com.szkct.weloopbtsmartdevice.main.MainService.STATE_DISCONNECTEDANDUNBIND;
import static com.szkct.weloopbtsmartdevice.main.MainService.STATE_NOCONNECT;
import static com.szkct.weloopbtsmartdevice.main.MainService.UNIT;
import static com.szkct.weloopbtsmartdevice.main.MainService.WATER_CLOCK;
import static com.szkct.weloopbtsmartdevice.main.MainService.WEATHER_PUSH;
import static com.szkct.weloopbtsmartdevice.main.MainService.WECHAT_SPORT;

//import static com.szkct.weloopbtsmartdevice.main.MainService.STATE_NOCONNECT;


public class HelperFragment extends Fragment {
    private static final String TAG = "[HelperFragment]";
    private LinearLayout linearsetting;
    private RelativeLayout Join_us_rl;
    // private Toolbar toolbar;
    private TextView find_telephone_txt, ble_selecttext ,tv_firme_number;
    private ToggleButton find_telephone_switchone, camera_switchone;
    private RelativeLayout find_telephone, notifications_layout, camera_layout;
    boolean first_find_telephone, first_notifications, first_camera;
    private PopupWindow mPopupWindow;
    private ToggleButton tb_call_notify, tb_sms_notify,tb_raise_bright;
    RelativeLayout switchSkinll;
    private WheelView wheelView;
    // private ImageView bleimage;
    private TextView link_blename_txt;

    private TextView Connection_device_description,RecyclerView_link_blename_txt;
    private TextView Notification_description;
    private TextView Telephone_description;
    private TextView Information_description;
    private TextView Get_watches_txt;
    private TextView User_guide_description,item_gujianshengji_text;
    private TextView devceAbou;

    private ImageView img_watch_sesarch, instructions_img;
    String[] names, addrrs;
    private View helperView;
    Bluttoothbroadcast blutbroadcast;
    MainService service;
    Animation operatingAnim;
    private boolean geting = false;
    private int squenId = 0;
    private TimeCount timeCount;
    private static final long TIME_OUT = 1000L;//倒计时5s

    private int type;
    private String firmware_version;
    boolean  gotomessage=false;//是否有权

    private helpHintDialog helpHintDialog;

    private com.szkct.map.dialog.AlertDialog firmDialog;
    public static HelperFragment newInstance(String title) {
        HelperFragment fragment = new HelperFragment();
        return fragment;
    }

    private CustomerSwipeMenuListView swipeMenuListView;
    private SwipeMenuCreator creator;
    private int localWigth = 0;
    private int localHeigth = 0;
    private ScrollView scrollView;

    private long syncStartTime = 0;
    private final int SNYBTDATAFAIL = 16;
   // public Dialog dialog;

    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");

    private DBHelper db = null;

    private BluetoothAdapter mBluetoothAdapter;

    private  LoadingDialog loadingDialog = null;

    private HidConncetUtil mHidConncetUtil;

    private List<String> constantList = new ArrayList<>();
    private int index;
    private int constantSum;
    private int constantIndex;
    public final int CODE = 10086;
    public String murl = "https://mp.weixin.qq.com/s/KgcZYWvDIYa6N0rA-NsJQQ";
    private HTTPController hc;
    Runnable runnable = new Runnable() {    
        @Override
        public void run() {
            if (null != loadingDialog) {
                if (System.currentTimeMillis() - syncStartTime > 60 * 1000) {  //  90 * 1000
                    Message msg = handler.obtainMessage(SNYBTDATAFAIL);  //todo -----  数据同步失败,稍后重试
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    };

    private Handler handler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ble_selecttext.setText(names[(int) msg.obj]);
                    if ((int) msg.obj == 0) {
                        SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC, "");
                    } else {
                        SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC, addrrs[(int) msg.obj - 1]);
                    }
                    Intent intent = new Intent();
                    intent.setAction(MainService.ACTION_MACCHANGE);
                    getActivity().sendBroadcast(intent);
                    break;

                case SNYBTDATAFAIL:  // 同步失败
                    try {
                        if (null != loadingDialog) {
                            if(loadingDialog.isShowing()){
                                loadingDialog.setCancelable(true);
                                loadingDialog.dismiss();
                                loadingDialog = null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(isAdded()) {
                        Toast.makeText(BTNotificationApplication.getInstance(), getResources().getString(R.string.userdata_synerror), Toast.LENGTH_SHORT).show();
                    }
                    MainService.getSyncDataNumInService = 0;
                    BTNotificationApplication.isSyncEnd = true;   
                    break;

                case 6:
                    if(null != loadingDialog && loadingDialog.isShowing()){
                          loadingDialog.dismiss();
                        loadingDialog = null;
                    }

//                    BluetoothDevice device =  BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
//                    if (Build.VERSION.SDK_INT >= 17) {
//                        if (null == mHidConncetUtil) {
//                            mHidConncetUtil = new HidConncetUtil(BTNotificationApplication.getInstance());
//                        }
//
//                        if (null != device && ("QW11").equals(device.getName()) && !mHidConncetUtil.isBonded(device)) {// todo ---    || ("MTS036").equals(device.getName())
//                            Intent intentHid = new Intent();
//                            intentHid.setAction(MainService.ACTION_BLECONNECTHID);
//                            getActivity().sendBroadcast(intentHid);
//                        }
//                    } else {
//                        Toast.makeText(BTNotificationApplication.getInstance(), "您的手机不支持HID连接", Toast.LENGTH_SHORT).show();
//                    }
                    String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //
                    BluetoothDevice device =  BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));     // TODO ---- 同步数据完成
                    if(("QW11").equals(device.getName())  || "156".equals(code)  ||  "415".equals(code)   || "542".equals(code)  ||  "546".equals(code)  || "573".equals(code)  ||  "435".equals(code)   || "199".equals(code)  ||  "459".equals(code)
                            || "505".equals(code)  ||  "496".equals(code)   || "569".equals(code)  ||  "548".equals(code)  || "540".equals(code) || "530".equals(code)|| BLEMUSIC) {// todo ---    || ("MTS036").equals(device.getName())
                        Intent intentHid = new Intent();
                        intentHid.setAction(MainService.ACTION_BLECONNECTHID);    // TODO ---- 同步数据完成  发送HID 连接的广播
                        getActivity().sendBroadcast(intentHid);
                    }

                    if(WEATHER_PUSH){ // StringUtils.isEmpty()!"601".equals(code) ||
                        L2Send.syncAppWeather();   // 同步天气
                    }

                    break;

                case 18:
                    String result = msg.obj.toString();
                    Log.e("a", "----------" + result + "-----------");

                   /*
                   返回结果（json）：
                   {"code":0,
                   "data":{"country":"中国","city":"龙岗","weatherCode":"101","aqi":"","temperature":"30","cityid":"CN101280606","pressure":"1006","updateTimes":"2018-05-18 17:41","createTimes":"2018-05-18 17:41",
                   "dailyForecast":[{"weatherDate":"2018-05-18","weatherCode":"101","temperatureMax":"32","temperatureMin":"27","pressure":"1008","uvIndex":"10"},
                   {"weatherDate":"2018-05-19","weatherCode":"101","temperatureMax":"32","temperatureMin":"26","pressure":"1009","uvIndex":"9"},
                   {"weatherDate":"2018-05-20","weatherCode":"101","temperatureMax":"32","temperatureMin":"26","pressure":"1009","uvIndex":"11"}]},
                   "message":"请求成功"}

                   返回结果（json）：
                   {"code":0,
                   "data":[{"dialId":84,"adaptiveNumber":301,"dialPictureUrl":"http://wx.funos.cn:8080/fundo-dialPic/Chrysanthemum.jpg","dialFileUrl":"http://wx.funos.cn:8080/fundo-dialFile/test.zip","dialName":"测试1"},
                           {"dialId":85,"adaptiveNumber":301,"dialPictureUrl":"http://wx.funos.cn:8080/fundo-dialPic/Chrysanthemum.jpg","dialFileUrl":"http://wx.funos.cn:8080/fundo-dialFile/test.zip","dialName":"测试2"}],
                           "message":"请求成功"}
                    */
                    try {
                        JSONObject jo = new JSONObject(result);
                        int resultcode = jo.getInt("code");
                        if (resultcode == 0 && jo.has("data")) {
                            String data = jo.getString("data");
                            List<PushPicContent> mlist = Utils.getObjectList(data,PushPicContent.class);
                            int d = 999;
                            ///////////////////////////////////////////////////////////////////////////////////////////////////

                            /////////////////////////////////////////////////////////////////////////////////////////////////
                        }
                    } catch (Exception e) {
                        Log.e("a", "----------" + e.toString() + "-----------");
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                toMain();
//                            }
//                        }, 1000);

                    } finally {

                    }
                    break;

                case CODE:  //加入我们,跳转网址
                    if (!StringUtils.isEmpty(msg.obj.toString())){
                        Log.e("msg:", ""+msg.obj.toString());
                        if (!("-1").equals(msg.obj.toString())){

                            advertisingBean advertisingBean = new Gson().fromJson(msg.obj.toString(), advertisingBean.class);
                            Log.e("advertisingBean:", ""+advertisingBean.getData().getGuideUrl());

                            String urls = advertisingBean.getData().getGuideUrl();
                            if (!StringUtils.isEmpty(urls)){
                                new FinestWebView.Builder(getActivity()).showIconMenu(false).show(urls);
                            }else{
                                new FinestWebView.Builder(getActivity()).showIconMenu(false).show(Constants.CHECK_moren);
                            }
                        }else{
                            new FinestWebView.Builder(getActivity()).showIconMenu(false).show(Constants.CHECK_moren);
                        }

                    }else{
                        new FinestWebView.Builder(getActivity()).showIconMenu(false).show(Constants.CHECK_moren);
                    //    Toast.makeText(getActivity(),getString(R.string.check_network_message), Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helperView = inflater.inflate(R.layout.watch_assistant_setting_preference, null);
        EventBus.getDefault().register(this);
        linearsetting = (LinearLayout) helperView.findViewById(R.id.re_alertset);
        init();

      //  dialog= CustomProgress.show(getActivity(), getString(R.string.userdata_synchronize), null);

//        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.KENGDIEDEXIAOMI).equals("")) {
//            SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.KENGDIEDEXIAOMI, SharedPreUtil.YES);
//            if (isMIUIRom() && !isMiuiFloatWindowOpAllowed(getActivity())) {
//                setfindwatch(getActivity());
//            }
//        }

       /* /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(getActivity())) {
//                                    Toast.makeText(context, R.string.xuanfukuang, Toast.LENGTH_SHORT).show();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setMessage(R.string.xuanfukuang)
                        .setNegativeButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                        startActivity(intent);
                                    }
                                });

                builder.show();
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/

        return helperView;
    }

    private void init() {
        switchSkinll = (RelativeLayout) helperView.findViewById(R.id.switchSkinll);
        switchSkinll.setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.contacts_tv).setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.contacts_tv).setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.contacts_tv).setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.rl_call_notify).setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.rl_sms_notify).setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.soscall_tv).setOnClickListener(new myOnChangeListener());  // 紧急拨号
        helperView.findViewById(R.id.function_about_tv).setOnClickListener(new myOnChangeListener());  // 关于
        helperView.findViewById(R.id.function_userhelp_tv).setOnClickListener(new myOnChangeListener()); // 用户帮助
        helperView.findViewById(R.id.camera_layout).setOnClickListener(new myOnChangeListener());//拍照
        helperView.findViewById(R.id.get_watches).setOnClickListener(new myOnChangeListener());//查找设备
        helperView.findViewById(R.id.rl_sit_notify).setOnClickListener(new myOnChangeListener());//久坐提醒
        helperView.findViewById(R.id.rl_drink_notify).setOnClickListener(new myOnChangeListener());//喝水提醒
        helperView.findViewById(R.id.rl_notify_mode).setOnClickListener(new myOnChangeListener());//提醒模式
        helperView.findViewById(R.id.rl_notify_alarm).setOnClickListener(new myOnChangeListener());//闹钟提醒
        helperView.findViewById(R.id.rl_gesture_control).setOnClickListener(new myOnChangeListener());//手势智控
        helperView.findViewById(R.id.rl_heart_check).setOnClickListener(new myOnChangeListener());//心率检测
        helperView.findViewById(R.id.rl_ecg_check).setOnClickListener(new myOnChangeListener());//心率检测
        helperView.findViewById(R.id.rl_temp_check).setOnClickListener(new myOnChangeListener());//心率检测
        helperView.findViewById(R.id.rl_not_disturb).setOnClickListener(new myOnChangeListener()); //勿扰模式
        helperView.findViewById(R.id.firmeware_rel).setOnClickListener(new myOnChangeListener()); //固件升级
        helperView.findViewById(R.id.rl_elektronik_fatura).setOnClickListener(new myOnChangeListener()); //电子发票
        helperView.findViewById(R.id.rl_receiving_code).setOnClickListener(new myOnChangeListener()); //收款二维码
        helperView.findViewById(R.id.rl_pointer_calibration).setOnClickListener(new myOnChangeListener()); //指针校准
        helperView.findViewById(R.id.watch_push_rel).setOnClickListener(new myOnChangeListener()); //表盘推送
        helperView.findViewById(R.id.assist_input_rel).setOnClickListener(new myOnChangeListener()); //协助输入
        Join_us_rl =  helperView.findViewById(R.id.Join_us_rl);
        Join_us_rl.setOnClickListener(new myOnChangeListener());//加入我们

        instructions_img = helperView.findViewById(R.id.instructions_img);
        instructions_img.setVisibility(View.VISIBLE);
        instructions_img.setOnClickListener(new myOnChangeListener());

        item_gujianshengji_text= (TextView) helperView.findViewById(R.id.item_gujianshengji_text);
        String languageLx = Utils.getLanguage();
        if(languageLx.equals("ja")){
            item_gujianshengji_text.setTextSize(12);
        }


        helperView.findViewById(R.id.rl_wxsport_notify).setOnClickListener(new myOnChangeListener()); //微信运动
        helperView.findViewById(unit_setting_rel).setOnClickListener(new myOnChangeListener()); //单位设置

        tb_call_notify = (ToggleButton) helperView.findViewById(R.id.tb_call_notify);
        tb_call_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, isChecked);
            }
        });
        tb_sms_notify = (ToggleButton) helperView.findViewById(R.id.tb_sms_notify);
        tb_sms_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, isChecked);
            }
        });

        tb_raise_bright = (ToggleButton) helperView.findViewById(R.id.tb_raise_bright);
        tb_raise_bright.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.RAISE_BRIGHT, isChecked);
            }
        });

      /*  tb_call_notify.setChecked((boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, true));
        tb_sms_notify.setChecked((boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, true));*/

        //link_blename_txt = (TextView) helperView.findViewById(R.id.link_blename_txt);   // 手表连接状态描述文本
        img_watch_sesarch = (ImageView) helperView.findViewById(R.id.img_watch_sesarch);
        //Connection_device_description = (TextView) helperView.findViewById(R.id.link_ble_txt);
        Notification_description = (TextView) helperView.findViewById(R.id.notifications_switchone_txt);
        Telephone_description= (TextView) helperView.findViewById(R.id.telephone_description);
        Information_description= (TextView) helperView.findViewById(R.id.information_description);
        Get_watches_txt= (TextView) helperView.findViewById(R.id.get_watches_txt);
        User_guide_description= (TextView) helperView.findViewById(R.id.user_guide_description);
      devceAbou  = (TextView) helperView.findViewById(R.id.mydevice_about);
        tv_firme_number = (TextView) helperView.findViewById(R.id.firmeware_number);

        RecyclerView_link_blename_txt= (TextView) helperView.findViewById(R.id.RecyclerView_link_blename_txt);

//        if(Locale.getDefault().getLanguage().equalsIgnoreCase("ar")){ //todo ---  阿拉伯语
//            RecyclerView_link_blename_txt.setla
//        }else {
//
//        }

        if(!Utils.isZh(getActivity())){
            Utils.settingFontsize (link_blename_txt,10);
        }
        if(!Utils.isZh(getActivity())){
            List<View>view=new ArrayList<View>();
           // view.add(Connection_device_description);
            view.add(Notification_description);
            view.add(Telephone_description);
            view.add(Information_description);
            view.add(Get_watches_txt);
            view.add(User_guide_description);
            view.add(devceAbou);
            Utils.  settingAllFontsize (view,16);
        }

        scrollView = (ScrollView) helperView.findViewById(R.id.helper_scrollView);
        swipeMenuListView = (CustomerSwipeMenuListView) helperView.findViewById(R.id.listView_ble_connect);


        if((boolean) SharedPreUtil.getParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.BLE_CLICK_STOP,true)) {
            if(BTNotificationApplication.getMainService().getState() == STATE_DISCONNECTED) {
                BTNotificationApplication.getMainService().setState(STATE_DISCONNECTEDANDUNBIND);
            }
        }

        BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /*if(BTNotificationApplication.getMainService().getState() == STATE_DISCONNECTED && !mBluetoothAdapter.isEnabled()){
            BTNotificationApplication.getMainService().setState(STATE_CONNECTING);
        }*/

        if(BTNotificationApplication.getMainService().getState() == STATE_DISCONNECTED && !mBluetoothAdapter.isEnabled()){
            BTNotificationApplication.getMainService().setState(STATE_NOCONNECT);
        }else if(!(boolean) SharedPreUtil.getParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.BLE_CLICK_STOP,true) && !mBluetoothAdapter.isEnabled()){
            BTNotificationApplication.getMainService().setState(STATE_NOCONNECT);
        }
        BleConnectAdapter bleConnectAdapter = new BleConnectAdapter(getActivity(), BTNotificationApplication.getMainService().getState()
                , SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME));
        swipeMenuListView.setAdapter(bleConnectAdapter);

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!btAdapter.isEnabled()) {
                    Toast.makeText(getActivity(), R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();  // 提示将手机的蓝牙打开
                    return;
                }

                if(STATE_DISCONNECTEDANDUNBIND==BTNotificationApplication.getMainService().getState()){
                    startActivity(new Intent(getActivity(), LinkBleActivity.class));   // TODO --- 进入手表 蓝牙页面
                }

                // BTNotificationApplication.getMainService().getState() != MainService.STATE_DISCONNECTEDANDUNBIND   // --- MainService.getInstance().getState() == 3 || MainService.getInstance().getState() == 2 || MainService.getInstance().getState() == 1
                if (BTNotificationApplication.getMainService().getState() != MainService.STATE_DISCONNECTEDANDUNBIND) { //todo ---- 设备连接中和连接成功时，都可点击解绑    ||  WearableManager.STATE_CONNECTING ||  != WearableManager.STATE_CONNECTED
                     // TODO --- 蓝牙连上或连接中 点击解绑设备   ---- add1120 ,z只要显示了设备的名字就可以点击解绑设备
                    showUnbindDialog();
                }

            }
        });





        if(BTNotificationApplication.getMainService().getState() != MainService.STATE_DISCONNECTEDANDUNBIND) {
            // 添加删除按钮
            creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                    deleteItem.setWidth(dp2px(70));
                    deleteItem.setTitle(getString(R.string.unbind_device));
                    deleteItem.setTitleColor(Color.WHITE);

                    String languageLx = Utils.getLanguage();
                    if (!languageLx.equals("zh")) {  // en
                        deleteItem.setTitleSize(12);
                    }else {
                        deleteItem.setTitleSize(18);
                    }

//                    deleteItem.setTitleSize(18);
                    menu.addMenuItem(deleteItem);// add to menu

                }
            };
            swipeMenuListView.setMenuCreator(creator);
        }else{
            swipeMenuListView.setMenuCreator(null);
        }

        // 删除按钮单击监听
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                showUnbindDialog();
            }
        });


        /*swipeMenuListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        localWigth = (int)motionEvent.getX();
                        localHeigth = (int)motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int sx = (int)motionEvent.getX();
                        int sy = (int)motionEvent.getY();
                        if (localWigth - sx > 10 || localWigth - sx < 10) {  //重点就是来判断横向或者纵向滑动的距离来分配焦点
                            scrollView.requestDisallowInterceptTouchEvent(true);  //拦截ScrollView的事件，让listView能滑动
                        }else {
                            scrollView.requestDisallowInterceptTouchEvent(false);   //取消ScrollView的拦截事件，让listView不能滑动
                        }
                        if (localHeigth - sy >=50 || localHeigth - sy < -50){
                            scrollView.requestDisallowInterceptTouchEvent(false);
                        }else {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });*/


        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate359);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        /*helperView.findViewById(R.id.link_ble).setOnClickListener(   // TODO ---- 连接手表条目点击
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFastDoubleClick()) {
                            Log.d(TAG, "isFastDoubleClick TYPE_SCAN return");
                            return;
                        }
                        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (!btAdapter.isEnabled()) {
                            Toast.makeText(getActivity(), R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();  // 提示将手机的蓝牙打开
                            return;
                        }
                        *//*if (MainService.getInstance().getState() == 2){
                            Toast.makeText(getActivity(), getString(R.string.bluetooth_connecting), Toast.LENGTH_SHORT).show();
                            return;
                        }*//*
                        startActivity(new Intent(getActivity(), LinkBleAcitivity.class));   // TODO --- 进入手表 蓝牙页面
                    }
                });*/
        helperView.findViewById(R.id.set_wifi).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFastDoubleClick()) {
                            Log.d(TAG, "isFastDoubleClick TYPE_SCAN return");
                            return;
                        }
                        if (MainService.getInstance().getState() != 3) {
                            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(getActivity(), Wifiactivity.class));  // TODO ---- 手表连接成功时，进入手表WiFi页面
                    }
                });
        helperView.findViewById(R.id.assist_input).setOnClickListener(  // TODO -- 协助输入
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFastDoubleClick()) {
                            Log.d(TAG, "isFastDoubleClick TYPE_SCAN return");
                            return;
                        }
//                        if (MainService.getInstance().getState() != 3) {   // 蓝牙未连接时
//                            Toast.makeText(getActivity(), getString(R.string.watch_not_connected), Toast.LENGTH_SHORT).show();
//                            return;   // 蓝牙未连接时 ---- 直接返回
//                        }
                        /*if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")){
                            *//*com.szkct.map.dialog.AlertDialog alertDialog = new com.szkct.map.dialog.AlertDialog(getActivity()).builder();
                            alertDialog.setMsg(getString(R.string.bluetooth_call_notify));
                            alertDialog.setPositiveButton(getString(R.string.go_to), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                    startActivity(intent);
                                }
                            });
                            alertDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            alertDialog.show();*//*

                            //todo - 现改为 一个页面
                            Intent intent = new Intent(getActivity(), BTcallActivity.class);
                            startActivity(intent);

                        }else {
                            startActivity(new Intent(getActivity(), BtInputActivity.class));
                        }*/
                        Intent intent = new Intent(getActivity(), BTcallActivity.class);   //现不区分平台跳转不同界面。
                        startActivity(intent);
                    }
                });
        helperView.findViewById(R.id.pushdial_ti).setOnClickListener(  // TODO --- 表盘推送   ---- // 通过发消息显示表盘推送页面
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message message = new Message();
                        message.what = 4;
                        MainActivity.mMainActivity.myHandler.sendMessage(message);
                    }
                });
        helperView.findViewById(R.id.get_watches).setOnClickListener(  // 点击找手表
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        int j = 1/0;   // for test

                        if (geting) {  //没有找到手表标志位
                           /* if (MainService.getInstance().getState() == 3 && !SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {   //STATE_CONNECTED = 3;  // now connected to a remote device     已经连接上了
                                byte[] l2 = new L2Bean().L2Pack(BleContants.FIND_COMMAND, BleContants.FIND_DEVICE, null);  // 查找设备
                                MainService.getInstance().writeToDevice(l2, true);  // 发了几次，根据回调 销毁 加载框
                            }*/
                            img_watch_sesarch.clearAnimation();
                            img_watch_sesarch.setVisibility(View.GONE);     // TODO ---- 销毁搜索框
                            geting = false;
                            return;
                        }
                        if (MainService.getInstance().getState() == 3) {    //已经连接上了
                            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) { // MTK 查找设备
                                int status = PxpFmStatusRegister.getInstance().getFindMeStatus();
                                if (status == PxpFmStatusRegister.FIND_ME_STATUS_USING) {  //  FIND_ME_STATUS_USING = 2
                                    LocalPxpFmpController.findTargetDevice(BlePxpFmpConstants.FMP_LEVEL_NO);  // FMP_LEVEL_NO = 0
                                } else {
                                    LocalPxpFmpController.findTargetDevice(BlePxpFmpConstants.FMP_LEVEL_HIGH);  // FMP_LEVEL_HIGH = 2
                                }
                                BluetoothMtkChat.getInstance().sendFindWatchOn();
                                img_watch_sesarch.setVisibility(View.VISIBLE);
                                if (operatingAnim != null) {
                                    img_watch_sesarch.startAnimation(operatingAnim);
                                }
                                geting = true;
                                if (timeCount == null) {
                                    timeCount = new TimeCount(TIME_OUT, 1000L);
                                }
                                timeCount.start();
                            } else {
                                byte[] l2 = new L2Bean().L2Pack(BleContants.FIND_COMMAND, BleContants.FIND_DEVICE, null);  //72 & ble 查找设备
                                MainService.getInstance().writeToDevice(l2, true);
                                img_watch_sesarch.setVisibility(View.VISIBLE);
                                if (operatingAnim != null) {
                                    img_watch_sesarch.startAnimation(operatingAnim);
                                }
                                geting = true;
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        find_telephone = (RelativeLayout) helperView.findViewById(R.id.find_telephone);  // 找手机开关
        notifications_layout = (RelativeLayout) helperView.findViewById(R.id.notifications_layout);  // 通知应用
        camera_layout = (RelativeLayout) helperView.findViewById(R.id.camera_layout);
		/*
		 * ringLayout = (RelativeLayout) findViewById(R.id.re_ring_layout);
		 * shockLayout = (RelativeLayout) findViewById(R.id.re_shock_layout);
		 */

        find_telephone_txt = (TextView) helperView.findViewById(R.id.find_telephone_txt);
        ble_selecttext = (TextView) helperView.findViewById(R.id.ble_selecttext);
        /*if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
            ble_selecttext.setText(getActivity().getString(R.string.the_last_connected_device));
        } else {
            ble_selecttext.setText(SharedPreUtil.readPre(getActivity(), SharedPreUtil.NAME, SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)));
        }*/
        find_telephone_switchone = (ToggleButton) helperView.findViewById(R.id.find_telephone_switchone);
        camera_switchone = (ToggleButton) helperView.findViewById(R.id.camera_switchone);
        initswitch();
        initPreferences();
        blutbroadcast = new Bluttoothbroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainService.ACTION_BLEDISCONNECTED);
        intentFilter.addAction(MainService.ACTION_UNABLECONNECT);
        intentFilter.addAction(MainService.ACTION_BLECONNECTED);
        intentFilter.addAction(MainService.ACTION_GETWATCH);    // 注册找手表的action ---  ACTION_GETWATC
        intentFilter.addAction(MainService.ACTION_GESTURE_ON);
        intentFilter.addAction(MainService.ACTION_GESTURE_OFF);

        intentFilter.addAction(MainService.ACTION_SYNC_BLECONNECT);
        intentFilter.addAction(MainService.ACTION_SYNFINSH);


        getActivity().registerReceiver(blutbroadcast, intentFilter);
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (blutbroadcast != null) {
            getActivity().unregisterReceiver(blutbroadcast);
        }
    }

    private void initswitch() {
        if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_FIND_PHONE).equals("0")) {
            first_find_telephone = true;
        } else {
            first_find_telephone = false;
        }
        find_telephone_switchone.setChecked(first_find_telephone);
        find_telephone_switchone.setEnabled(false);

        if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_CAMERA).equals("0")) {
            first_camera = true;
        } else {
            first_camera = false;
        }

        camera_switchone.setChecked((Boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY,true));
        camera_switchone.setEnabled(false);//
    }

    /**
     * do preference initialization about click listeners.
     */
    private void initPreferences() {
        find_telephone.setOnClickListener(new myOnChangeListener());
        notifications_layout.setOnClickListener(new myOnChangeListener());
        camera_layout.setOnClickListener(new myOnChangeListener());
        helperView.findViewById(R.id.ble_select).setOnClickListener(
                new myOnChangeListener());
		/*
		 * ringLayout.setOnClickListener(new myOnChangeListener());
		 * shockLayout.setOnClickListener(new myOnChangeListener());
		 */
    }

    private class myOnChangeListener implements OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 2000;   //两秒内防止多次点击事件
        private long lastClickTime = 0;           //记录多次点击的时间

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.function_userhelp_tv: {
//                    TemperatureUtil.judgmentTemperatureDB(getActivity());
//                    try {
//                        MainService.getInstance().saveTemperature();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                    Intent mIntent = new Intent(getActivity().getApplication(), UserHelpActivity.class); // 用户帮助页面
                    startActivity(mIntent);
                }
                break;

                case R.id.function_about_tv: {
                    Intent mIntent = new Intent(getActivity().getApplication(), AboutActivity.class);  // 关于页面
                    startActivity(mIntent);
                }
                break;

                case R.id.soscall_tv:  // TODO ----- 紧急拨号
                    Intent intentAlert = new Intent(getActivity().getApplication(), SoscallActivity.class);
                    startActivity(intentAlert);
                    break;

                case R.id.contacts_tv:  //TODO --- 同步联系人
                    if (isFastDoubleClick()) {
                        return;
                    }
                    SynchronizeContacts(getActivity());
                    break;

                case R.id.switchSkinll: {
                    //Log.e("HYC", "switchSkinll -------------------------------------------");
                    final String[] p = new String[]{getString(R.string.theme_white), getString(R.string.theme_black)};
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu, null);
                    wheelView = (WheelView) view.findViewById(R.id.targetWheel);
                    wheelView.setAdapter(new StrericWheelAdapter(p));

                    if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
                        wheelView.setCurrentItem(0);
                    } else {
                        wheelView.setCurrentItem(1);
                    }
                    wheelView.setCyclic(false);
                    wheelView.setInterpolator(new AnticipateOvershootInterpolator());

                    view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                    view.findViewById(R.id.btnConfirm).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                                if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals(wheelView.getCurrentItem() + "")) {
                                    mPopupWindow.dismiss();
                                } else {
                                    SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE, wheelView.getCurrentItem() + "");
                                    sendSwitchStyleBrocast(wheelView.getCurrentItem());
                                    mPopupWindow.dismiss();
                                }
                            }
                        }
                    });

                    mPopupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                    mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
                    mPopupWindow.showAtLocation(switchSkinll, Gravity.BOTTOM, 0, 0);
                }
                break;

                case R.id.find_telephone: // 提醒设置   ----- TODO  --- 找手机 ，app中只是一个控制开关，找手机的指令是由手表主动发送
                    first_find_telephone = !first_find_telephone;
                    find_telephone_switchone.setChecked(first_find_telephone);
                    if (first_find_telephone) {
                        SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_FIND_PHONE, "1");
                        BluetoothMtkChat.getInstance().sendFindWatchOn();  //TODO  ---  查找手机的开关打开
                    } else {
                        SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_FIND_PHONE, "0");
                        BluetoothMtkChat.getInstance().sendFindWatchOff(); //TODO  ---  查找手机的开关关闭
                    }
                    break;
                case R.id.notifications_layout: // 通知
                    /**
                     * 是否开启辅助服务
                     */
                    Utils.getPhoneInfo();
                    try {
                        if (null != Utils.mtype && Utils.mtype.contains("mates") || "Lenovo".equals(Utils.mtyb) || "HUAWEI".equals(Utils.mtyb) || null != Utils.mtype && "HUAWEI MT7-TL00".equals(Utils.mtype) || "Meizu".equals(Utils.mtyb) || null != Build.VERSION.BASE_OS && Build.VERSION.BASE_OS.toString().contains("7.1.1")) {
                            if (!Utils.isAccessibilitySettingsOn(getActivity(), NeNotificationService.class)) {
                                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
                                normalDialog.setTitle(getActivity().getResources().getString(R.string.sweet_warn));
                                normalDialog.setMessage(getActivity().getResources().getString(R.string.necessary_to_open));
                                normalDialog.setPositiveButton(getActivity().getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //...To-do
                                                Intent intentr = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                                startActivityForResult(intentr, 1);
                                            }
                                        });
                                normalDialog.setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                normalDialog.show();
                            } else {
                                startActivity(new Intent(getActivity(), NotificationAppListActivity.class));  // 通知应用页面
                            }
                        } else {
                            String string = Settings.Secure.getString(getActivity().getContentResolver(), "enabled_notification_listeners");
                            if (null != string && !string.contains(getActivity().getPackageName())) {
                                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                            } else {
                                startActivity(new Intent(getActivity(), NotificationAppListActivity.class));  // 通知应用页面
                            }

                       }
                   }catch (NoSuchFieldError e){
                       String string = Settings.Secure.getString(getActivity().getContentResolver(), "enabled_notification_listeners");
                       if (null!=string&&!string.contains(getActivity().getPackageName())) {
                           startActivity( new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                       }else{
                           startActivity(new Intent(getActivity(), NotificationAppListActivity.class));  // 通知应用页面
                       }
                      }
                    break;

                case R.id.rl_call_notify: // 来电通知
                    setToggleButton(tb_call_notify, SharedPreUtil.TB_CALL_NOTIFY);
                    break;
                case R.id.rl_sms_notify: // 短信通知
                    setToggleButton(tb_sms_notify, SharedPreUtil.TB_SMS_NOTIFY);
                    break;
               /* case R.id.camera_layout:// 远程拍照设置
                    first_camera = !first_camera;
                    camera_switchone.setChecked(first_camera);
                    if (first_camera) {
                        SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_CAMERA, "1");  // 保存 开关的状态
                    } else {
                        SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH_ASSISTANT_CAMERA, "0");
                    }
                    break;*/

                case R.id.ble_select:
                    addrrs = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC).split("nozuomi");
                    names = new String[addrrs.length + 1];
                    names[0] = getString(R.string.the_last_connected_device);
                    for (int z = 0; z < addrrs.length; z++) {
                        names[z + 1] = SharedPreUtil.readPre(getActivity(), SharedPreUtil.NAME, addrrs[z]);
                    }
                    Log.e("addrrssize", addrrs.length + "=" + SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.ALLMAC));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle(getString(R.string.select_a_device));
                    // 指定下拉列表的显示数据
                    // 设置一个下拉的列表选择项
                    builder.setItems(names, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = which;
                            handler.sendMessage(msg);
                            // Toast.makeText(getActivity(), "选择的城市为：" +
                            // cities[which], Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                    break;
                /////TODO --- X2添加//////////////////////////////////////////////////////////////////////////////////
                case R.id.camera_layout:
//                    Intent intent = new Intent("0x46");   // 发送远程拍照 --- 对应的广播
//                    getActivity().sendBroadcast(intent);

                    if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")
                            || SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                        setToggleButton(camera_switchone, SharedPreUtil.TB_CAMERA_NOTIFY);
                    }else if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")){
                        long currentTime = Calendar.getInstance().getTimeInMillis();
                        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                            lastClickTime = currentTime;
                            if(MainService.getInstance().getState() == 3) {
                                RemoteCamera.isSendExitTakephoto = false;
                                L2Send.sendTakephoto();
                            }else {
                                Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    break;
                case R.id.get_watches:
                    findDevice();
                    break;
                case R.id.rl_sit_notify:
                    startActivity(new Intent(getActivity(), SedentaryReminderActivity.class));   // 久坐提醒
                    break;
                case R.id.rl_drink_notify:
                    startActivity(new Intent(getActivity(), WaterClockActivity.class));    //喝水提醒
                    break;
                case R.id.rl_notify_mode:
                    startActivity(new Intent(getActivity(), AlarmModeActivity.class)); // 提醒模式
                    break;
                case R.id.rl_notify_alarm:
                    startActivity(new Intent(getActivity(), AlarmClockActivity.class));  // 闹钟提醒
                    break;
                case R.id.rl_gesture_control:

                    setToggleButton(tb_raise_bright, SharedPreUtil.TB_RAISE_BRIGHT);   // raise_bright

//                    startActivity(new Intent(getActivity(), GestureControlActivity.class));  // 手势智控
                    break;
                case R.id.rl_heart_check://todo 改动界面
                    startActivity(new Intent(getActivity(), HeartAutoCheckActivity.class)); // 心率检测
                    break;
                case R.id.rl_ecg_check://todo 改动界面
                    startActivity(new Intent(getActivity(), HeartEnterCheckActivity.class)); // 心率检测
                    break;
                case R.id.rl_temp_check://todo 改动界面(新增)
//                    startActivity(new Intent(getActivity(), HeartAutoCheckActivity.class)); // 心率检测
                    startActivity(new Intent(getActivity(), TemperatureActivity.class)); // 心率检测
                    break;
                case R.id.rl_not_disturb:
                    startActivity(new Intent(getActivity(), NoFazeModeActivity.class));  // 勿扰模式
                    break;
                case R.id.firmeware_rel:     //固件升级
                    //                      //升级平台  0:nordic  1:dialog  2：MTK  3：智能表，4：BK
                    String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //
                    if ("4".equals(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE))) {// todo ---    AB312Q ---- 190      "190".equals(code)
                        if (NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())) {
                            getBleFirmwareInfoForBk();   // todo --- 从服务器获取 固件
                        }else{
                            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
                            getBleFirmwareInfo();   // todo --- 从服务器获取 固件
                        }else{
                            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.rl_wxsport_notify: //微信运动
                    startActivity(new Intent(getActivity(), WXMovementActivity.class));
                    break;
                case unit_setting_rel: //单位设置
                    startActivity(new Intent(getActivity(), UnitSettingActivity.class));
                    break;
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                case R.id.rl_receiving_code://TODO  ---- 收款二维码
                    startActivity(new Intent(getActivity(), ReceivingCodeActivity.class));
                    break;
                case R.id.rl_elektronik_fatura://TODO  --- 电子发票
                    startActivity(new Intent(getActivity(), NewElectronicInvoiceActivity.class));
                    break;
                case R.id.rl_pointer_calibration: //指针校准
                    if (isFastDoubleClick()) {
                        return;
                    }
                    if(BTNotificationApplication.getMainService().getState() == MainService.STATE_CONNECTED) {
                        L2Send.getCalibration();  //获取手环指针校准状态
                        loadingDialog = new LoadingDialog(getActivity(),R.style.Custom_Progress, "");
                        loadingDialog.setCancelable(true);
                        loadingDialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = handler.obtainMessage(6);  //todo -----  数据同步失败,稍后重试
                                handler.sendMessage(msg);
                            }
                        }, 1000 * 10);
                    }else{
                        Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.watch_push_rel: //表盘推送
                    int type = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE));   // TODO -- 升级的平台类型    //升级平台  0:nordic  1:dialog  2：MTK  3：智能表
                    if(type != 3){      //SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE).equals("427")
                        if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
                            startActivity(new Intent(getActivity(), WatchPushActivityNew.class));     // todo 表盘推送
                        }else{
                            Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        startActivity(new Intent(getActivity(), WatchPushActivity.class));     // todo 智能机表盘推送
                    }

                    break;
                case R.id.assist_input_rel: //协助输入
                    startActivity(new Intent(getActivity(), BtInputActivity.class));
                    break;

                case R.id.instructions_img: //提示框
                    helpHintDialog = new helpHintDialog(getActivity());
                    helpHintDialog.setMessage(getResources().getString(R.string.helphint_str));
                    helpHintDialog.setYesOnclickListener(new helpHintDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            helpHintDialog.dismiss();
                        }
                    });

                    helpHintDialog.show();

                    break;


                case R.id.Join_us_rl:
                    String url = Constants.CHECK_Login_next ;
                    hc = HTTPController.getInstance();
                    hc.open(getActivity());
                    hc.getNetworkStringData(url, handler, CODE);

                    break;
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                default:
                    break;
            }
        }
    }

    /**
     * 设置按钮
     *
     * @param tb
     * @param key
     */
    private void setToggleButton(ToggleButton tb, String key) {
        boolean b = !(boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, key, true);
        if(key.equals(SharedPreUtil.TB_CALL_NOTIFY)){
            if(b){
                MainService.getInstance().startCallService();
            }else{
                MainService.getInstance().stopCallService();
            }
        }
        if(key.equals(SharedPreUtil.TB_SMS_NOTIFY)){
            if(b){
                MainService.getInstance().startSmsService();
            }else{
                MainService.getInstance().stopSmsService();
            }
        }

        if(key.equals(SharedPreUtil.TB_RAISE_BRIGHT)){ // 抬手亮屏
            if(b){  // 开关打开时发送命令
                byte[] bytes = new byte[3];
                bytes[0] = (byte) 1;
                bytes[1] = (byte) 1;
                bytes[2] = (byte) 1;
                L2Send.sendNotify(BleContants.DEVICE_COMMADN, BleContants.GESTURE, bytes);
            }else{ // 开关关闭
                byte[] bytes = new byte[3];
                bytes[0] = (byte) 0;
                bytes[1] = (byte) 0;
                bytes[2] = (byte) 0;
                L2Send.sendNotify(BleContants.DEVICE_COMMADN, BleContants.GESTURE, bytes);
            }
        }

        tb.setChecked(b);
        SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, key, b);
    }

    public class Bluttoothbroadcast extends BroadcastReceiver {   // 通过广播监听蓝牙的连接状态
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainService.ACTION_BLECONNECTED.equals(action)) {   // 蓝牙连接成功
                // Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
                /*if (link_blename_txt != null) {
                    link_blename_txt.setText(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME));     //通过保存的蓝牙mac地址，获取到蓝牙的名字


                }*/
                if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                    //todo --- 添加判断mac地址
                    //                        myaddress = SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                    String deviceNAME = SharedPreUtil.readPre(context, SharedPreUtil.NAME, SharedPreUtil.MAC).toString();
                    if (!StringUtils.isEmpty(deviceNAME) && deviceNAME.contains("DfuTarg")) {  //  if(!StringUtils.isEmpty(deviceNAME) && deviceNAME.equals("DfuTarg")){   && FirmWareUpdateActivity.isHasDfuDevice
                        if ((boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {  //是否处于固件升级模式
                            if (firmDialog != null) {
                                firmDialog.dismiss();
                                firmDialog = null;
                            }
                            firmDialog = new com.szkct.map.dialog.AlertDialog(context).builder();
                            firmDialog.setMsg(getString(R.string.firmware_isnot_update_complete));   // 有未升级完成的固件，请继续升级！
                            firmDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())) {
                                        getBleFirmwareInfo();
                                    } else {
                                        Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            firmDialog.show();
                        }
                    }
                }
            }
            if (MainService.ACTION_BLEDISCONNECTED.equals(action)) { // 蓝牙断开
                // Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
               /* if (link_blename_txt != null) {
                    link_blename_txt.setText(getString(R.string.watch_not_connected));
                    if (img_watch_sesarch != null) {
                        img_watch_sesarch.clearAnimation();
                        img_watch_sesarch.setVisibility(View.GONE);
                    }
                    geting = false;
                }*/
            }
            if (MainService.ACTION_UNABLECONNECT.equals(action)) {   // 手机蓝牙未打开
                // Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
                /*if (link_blename_txt != null) {
                    link_blename_txt.setText(getString(R.string.watch_not_connected));
                    if (img_watch_sesarch != null) {
                        img_watch_sesarch.clearAnimation();
                        img_watch_sesarch.setVisibility(View.GONE);
                    }
                    geting = false;
                }*/
            }
            if (MainService.ACTION_GETWATCH.equals(action)) {    // TODO ---- 找到手表的广播
                geting = false;
                img_watch_sesarch.clearAnimation();
                img_watch_sesarch.setVisibility(View.GONE);   // 隐藏找手表的状态
            }
			
			if (MainService.ACTION_GESTURE_ON.equals(action)) {    // TODO ---- 找到手表的广播
                // Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
                tb_raise_bright.setChecked(true);
                SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.RAISE_BRIGHT, true);
            }

            if (MainService.ACTION_GESTURE_OFF.equals(action)) {    // TODO ---- 找到手表的广播
                // Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
                tb_raise_bright.setChecked(false);
                SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.RAISE_BRIGHT, false);
            }

            if (MainService.ACTION_SYNFINSH.equals(action)) {    // TODO ---- 同步数据完成
                // Toast.makeText(context, "蓝牙状态改变", Toast.LENGTH_SHORT).show();
               /* if(null != dialog){
                    //todo ---- 弹加载框
                    dialog.setCancelable(true);
                    dialog.dismiss();
                    if(handler!=null){
                        handler.removeCallbacks(runnable);
                    }
                }*/

                String stepNum = intent.getStringExtra("step");
                if(null != loadingDialog && !StringUtils.isEmpty(stepNum)){
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
//                        loadingDialog.setText(getString(R.string.userdata_synchronize3));

                        loadingDialog.setText(getString(R.string.userdata_synchronize_success));


                        Message msg = new Message();
                        msg.what = 6;
                        handler.sendMessageDelayed(msg,1000);
//                        Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.userdata_synchronize_success), Toast.LENGTH_SHORT).show();
                        if(handler!=null){
                            handler.removeCallbacks(runnable);
                        }
                    }
                }
            }

            if (MainService.ACTION_SYNC_BLECONNECT.equals(action)) {    // TODO ---- 自动同步
                if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {    //手环实时同步运动数据

                    if((boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {
                        return;
                    }
                    syncStartTime = System.currentTimeMillis();
                    sendSyncData(3);  // 计步
                    sendSyncData(1);  // 睡眠
                    sendSyncData(2);  // 心率
                    sendSyncData(5);  // 血氧血压
                    BTNotificationApplication.needReceiveNum = BTNotificationApplication.needGetSportDayNum + BTNotificationApplication.needGetSleepDayNum + BTNotificationApplication.needGetHeartDayNum; //todo ---- 需要获取的数据条数
//                    Log.e("liuxiaodata", "需要收到的数据条数为--" +  BTNotificationApplication.needReceiveNum);
//                    Log.e("liuxiaodata", "需要收到的计步数据条数为--" +  BTNotificationApplication.needGetSportDayNum);
//                    Log.e("liuxiaodata", "需要收到的睡眠数据条数为--" +  BTNotificationApplication.needGetSleepDayNum);
//                    Log.e("liuxiaodata", "需要收到的心率数据条数为--" +  BTNotificationApplication.needGetHeartDayNum);

                    BTNotificationApplication.isSyncEnd = false;   //TODO ---  是否同步数据完成了

                    //当前不是处于固件升级模式可同步数据
                    if(!(boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {
                        if(null == loadingDialog){  // isShowing

                            loadingDialog = new LoadingDialog(getActivity(),R.style.Custom_Progress, getString(R.string.userdata_synchronize));
                            loadingDialog.show();
                            handler.postDelayed(runnable, 1000 * 61);// 打开定时器，执行操作   1000 * 91
                        }
                    }
                } else if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {  //TODO  ---  mtk  同步数据   (现去掉,MainService已同步一次，避免重复)
                    /*BluetoothMtkChat.getInstance().getWathchData();    //获取手表数据
                    BluetoothMtkChat.getInstance().syncRun();        //每天计步数据*/
                    BluetoothMtkChat.getInstance().sendApkState();  //前台运行
                } else if (SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1")){
                    BTNotificationApplication.needSendDataType = 0;
                    BTNotificationApplication.needReceDataNumber = 0;

                    String bluetoothAdress = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC);  // 蓝牙地址  72:D9:46:65:72:3A
                    String sporttime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.SPORT, bluetoothAdress);   //1488441600000   ----- 上次保存的运动时间   时间戳   1491994800
                    String sleeptime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.SLEEP, bluetoothAdress);   // 睡眠时间        ---- 上次保存的睡眠时间   日期     2017-04-13 09:00:00
                    String hearttime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.HEART, bluetoothAdress);   // 心率时间        ---- 上次保存的心率时间   时间戳    1490095000
                    String pressuretime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLOOD_PRESSURE, bluetoothAdress);   // 血压时间        ---- 上次保存的血压时间  日期     2018-03-26 09:00:00

                    byte[] sportBytes = new byte[7];
                    byte[] sleepBytes = new byte[7];
                    byte[] heartBytes = new byte[7];
                    byte[] pressureBytes = new byte[7];
                    if(!StringUtils.isEmpty(sporttime)){
                        String lastGetSportData = StringUtils.timestamp2Date(sporttime);   //由时间戳格式转为日期格式  2017-03-16 19:00:00     2017-04-12 19:00:00

                        int sportYear = Integer.valueOf(lastGetSportData.substring(2, 4));  // 11
                        int sportMonth =  Integer.valueOf(lastGetSportData.substring(5, 7));  // 3
                        int sportRi = Integer.valueOf(lastGetSportData.substring(8, 10));  // 10
                        int sportShi = Integer.valueOf(lastGetSportData.substring(11, 13));

                        sportBytes[0] = (byte)3;
                        sportBytes[1] = (byte)sportYear;
                        sportBytes[2] = (byte)sportMonth;
                        sportBytes[3] = (byte)sportRi;
                        sportBytes[4] = (byte)sportShi;
                        sportBytes[5] = (byte)0;
                        sportBytes[6] = (byte)0;
                    }

                    if(!StringUtils.isEmpty(sleeptime)){
                        String lastGetSleepData = sleeptime;

                        int sleepYear = Integer.valueOf(lastGetSleepData.substring(2, 4));  // 11
                        int sleepMonth =  Integer.valueOf(lastGetSleepData.substring(5, 7));  // 3
                        int sleepRi =  Integer.valueOf(lastGetSleepData.substring(8, 10));  // 10
                        int sleepShi =  Integer.valueOf(lastGetSleepData.substring(11, 13));
                        int sleepFen =  Integer.valueOf(lastGetSleepData.substring(14, 16));

                        sleepBytes[0] = (byte)1;
                        sleepBytes[1] = (byte)sleepYear;
                        sleepBytes[2] = (byte)sleepMonth;
                        sleepBytes[3] = (byte)sleepRi;
                        sleepBytes[4] = (byte)sleepShi;
                        sleepBytes[5] = (byte)sleepFen;
                        sportBytes[6] = (byte)0;
                    }


                    if(!StringUtils.isEmpty(hearttime)){
                        String lastGetHeartData = StringUtils.timestamp2Date(hearttime);

                        int heartYear = Integer.valueOf(lastGetHeartData.substring(2, 4));  // 11
                        int heartMonth =  Integer.valueOf(lastGetHeartData.substring(5, 7));  // 3
                        int heartRi =  Integer.valueOf(lastGetHeartData.substring(8, 10));  // 10
                        int heartShi = Integer.valueOf(lastGetHeartData.substring(11, 13));
                        int heartFen =  Integer.valueOf(lastGetHeartData.substring(14, 16));
                        int heartMiao =  Integer.valueOf(lastGetHeartData.substring(17, 19));

                        heartBytes[0] = (byte)2;
                        heartBytes[1] = (byte)heartYear;
                        heartBytes[2] = (byte)heartMonth;
                        heartBytes[3] = (byte)heartRi;
                        heartBytes[4] = (byte)heartShi;
                        heartBytes[5] = (byte)heartFen;
                        heartBytes[6] = (byte)heartMiao;

                    }

                    if(!StringUtils.isEmpty(pressuretime)){
                        String lastGetPressureData = pressuretime;

                        int pressureYear = Integer.valueOf(lastGetPressureData.substring(2, 4));
                        int pressureMonth =  Integer.valueOf(lastGetPressureData.substring(5, 7));
                        int pressureRi =  Integer.valueOf(lastGetPressureData.substring(8, 10));
                        int pressureShi =  Integer.valueOf(lastGetPressureData.substring(11, 13));
                        int pressureFen =  Integer.valueOf(lastGetPressureData.substring(14, 16));
                        int pressureSecond =  Integer.valueOf(lastGetPressureData.substring(17, 18));

                        pressureBytes[0] = (byte)5;
                        pressureBytes[1] = (byte)pressureYear;
                        pressureBytes[2] = (byte)pressureMonth;
                        pressureBytes[3] = (byte)pressureRi;
                        pressureBytes[4] = (byte)pressureShi;
                        pressureBytes[5] = (byte)pressureFen;
                        pressureBytes[6] = (byte)pressureSecond;
                    }

                    if(!(boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {
                        if(null == loadingDialog){  // isShowing

                            loadingDialog = new LoadingDialog(getActivity(),R.style.Custom_Progress, getString(R.string.userdata_synchronize));
                            loadingDialog.show();
                            handler.postDelayed(runnable, 1000 * 61);// 打开定时器，执行操作   1000 * 91
                        }
                    }

                    //同步计步
                    if(StringUtils.isEmpty(sporttime)){
                        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{3,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                        MainService.getInstance().writeToDevice(l2, true);
                        BTNotificationApplication.needSendDataType += 1;

                    }else{
                        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sportBytes);  // 传最后个日期的时间
                        MainService.getInstance().writeToDevice(l2, true);
                        BTNotificationApplication.needSendDataType += 1;
                    }

                    //同步睡眠
                    if(ISSYNWATCHINFO){
                        if(MainService.SLEEP){
                            if(StringUtils.isEmpty(sleeptime)){
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{1,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }else{
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sleepBytes);  // 传最后个日期的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }
                        }
                    }else{
                        if(StringUtils.isEmpty(sleeptime)){
                            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{1,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                            MainService.getInstance().writeToDevice(l2, true);
                            BTNotificationApplication.needSendDataType += 1;
                        }else{
                            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, sleepBytes);  // 传最后个日期的时间
                            MainService.getInstance().writeToDevice(l2, true);
                            BTNotificationApplication.needSendDataType += 1;
                        }
                    }


                    //同步心率
                    if(ISSYNWATCHINFO){
                        if(MainService.HEART){
                            if(StringUtils.isEmpty(hearttime)){
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{2,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }else{
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, heartBytes);  // 传最后个日期的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }
                        }
                    }else{
                        if(StringUtils.isEmpty(hearttime)){
                            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{2,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                            MainService.getInstance().writeToDevice(l2, true);
                            BTNotificationApplication.needSendDataType += 1;
                        }else{
                            byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, heartBytes);  // 传最后个日期的时间
                            MainService.getInstance().writeToDevice(l2, true);
                            BTNotificationApplication.needSendDataType += 1;
                        }
                    }

                        if(ISSYNWATCHINFO){
                            if(BLOOD_PRESSURE){
                                if(StringUtils.isEmpty(pressuretime)){
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{5,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                }else{
                                    byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, pressureBytes);  // 传最后个日期的时间
                                    MainService.getInstance().writeToDevice(l2, true);
                                    BTNotificationApplication.needSendDataType += 1;
                                }
                            }
                        }else{
                            if(StringUtils.isEmpty(pressuretime)){
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{5,0,0,0,0,0,0});  // keyValue --- 可以传上次获取同步数据的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }else{
                                byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, pressureBytes);  // 传最后个日期的时间
                                MainService.getInstance().writeToDevice(l2, true);
                                BTNotificationApplication.needSendDataType += 1;
                            }
                        }

                    BTNotificationApplication.isSyncEnd = false;
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        }
    }

    private long mLastClickTime = 0L;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;
    }

    /**
     * 判断MIUI的悬浮窗权限
     *
     * @param context
     * @return
     */

    public static boolean isMiuiFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {

                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e("log", AppOpsManager.MODE_ALLOWED + " invoke " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {  //这儿反射就自己写吧
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e("log", e.getMessage());
            }
        } else {
            Log.e("log", "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 经测试V5版本是有区别的
     *
     * @param context
     */
    public void openMiuiPermissionActivity(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");

        if ("V5".equals(getProperty())) {
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("canking", "error");
            }
            intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
            intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
        } else {
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
        }

        if (isActivityAvailable(context, intent)) {
            if (context instanceof Activity) {
                Activity a = (Activity) context;
                a.startActivityForResult(intent, 2);
            }
        } else {
            Toast.makeText(BTNotificationApplication.getInstance(),R.string.displaysuspendedwindow_permissions,Toast.LENGTH_LONG).show();
            Log.e("canking", "Intent is not available!");
        }
    }

    public static boolean isActivityAvailable(Context cxt, Intent intent) {
        PackageManager pm = cxt.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<ResolveInfo> list = pm.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    public static String getProperty() {
        String property = "null";
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            return property;
        }
        try {
            Class<?> spClazz = Class.forName("android.os.SystemProperties");
            Method method = spClazz.getDeclaredMethod("get", String.class, String.class);
            property = (String) method.invoke(spClazz, "ro.miui.ui.version.name", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return property;
    }

    public void setfindwatch(Context context) {
//        StringBuffer sb = new StringBuffer();
//        sb.append(R.string.about_findwatch);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.sweet_warn);
        builder.setMessage(R.string.fingwatch_text);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.gotosettings, new DialogInterface.OnClickListener() {    // todo ---- 小米手机需要开启悬浮框权限。否则可能影响找手机
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openMiuiPermissionActivity(getActivity());
            }
        })
//        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        })
        .create();
        builder.show();
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static boolean isMIUIRom() {
        String property = getSystemProperty("ro.miui.ui.version.name");
        Log.e("property", property);
        if (property == null || property.equals("")) {
            return false;
        }
        return true;
    }

    private void sendSwitchStyleBrocast(int index) {
        Intent intent = new Intent();
        intent.setAction(MainService.ACTION_THEME_CHANGE);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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

    //接收Service发过来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if(event.getMessage().equals("firmeware_version")){
            Log.e(TAG,"firmeware_version");
            tv_firme_number.setText(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION));
        }else if(event.getMessage().equals("connect_success")){
            BluetoothDevice device = (BluetoothDevice) event.getObject();
            SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC, device.getAddress());// 存储当前连接的蓝牙地址。
            SharedPreUtil.savePre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME, device.getName());// 存储当前连接的蓝牙名称。
        }else if(event.getMessage().equals("deviceState")){
            if((boolean) SharedPreUtil.getParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.BLE_CLICK_STOP,true)) {
                if(BTNotificationApplication.getMainService().getState() == STATE_DISCONNECTED) {
                    BTNotificationApplication.getMainService().setState(STATE_DISCONNECTEDANDUNBIND);
                }
            }

            if(BTNotificationApplication.getMainService().getState() == STATE_DISCONNECTED && !mBluetoothAdapter.isEnabled()){
                BTNotificationApplication.getMainService().setState(STATE_NOCONNECT);
            }

            BleConnectAdapter bleConnectAdapter = new BleConnectAdapter(getActivity(), BTNotificationApplication.getMainService().getState()
                    , SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME));
            swipeMenuListView.setAdapter(bleConnectAdapter);
            bleConnectAdapter.notifyDataSetChanged();

            if(BTNotificationApplication.getMainService().getState() == MainService.STATE_DISCONNECTEDANDUNBIND) { // todo --- 解绑断开连接   add0923
                SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH,"");
                refreshItems();
            }

            if(BTNotificationApplication.getMainService().getState() != MainService.STATE_DISCONNECTEDANDUNBIND) {
                // 添加删除按钮
                creator = new SwipeMenuCreator() {
                    @Override
                    public void create(SwipeMenu menu) {
                        SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                        deleteItem.setWidth(dp2px(70));
                        deleteItem.setTitle(getString(R.string.unbind_device));
                        deleteItem.setTitleColor(Color.WHITE);
                        deleteItem.setTitleSize(18);
                        menu.addMenuItem(deleteItem);// add to menu

                    }
                };
                swipeMenuListView.setMenuCreator(creator);
            }else{
                swipeMenuListView.setMenuCreator(null);
            }
        }else if("update_view".equals(event.getMessage())){   //更新列表
            if(MainService.getInstance().getState() == MainService.STATE_CONNECTED) {
                String name = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME);
                if (ISSYNWATCHINFO) {
                    if (UNIT) {
                        if (!name.equals("V6")) {
                            L2Send.unitSetting(); //todo --- add 1011
                        }
                    }
                } else {
                    /*if (!devices.getName().equals("V6")){
                        L2Send.unitSetting(); //todo --- add 1011
                    }*/
                }


                Log.e(TAG, "ISSYNWATCHINFO = " + ISSYNWATCHINFO + "  ;   PRESSURE = " + PRESSURE);
                if (ISSYNWATCHINFO) {
                    if (PRESSURE) {
                        L2Send.syncWeatherIndex();  // 同步紫外线，气压,海拔
                    }
                } else {
                    if (name.equals("F4") || name.equals("Smare Band")
                            || name.equals("Smart band") || name.equals("sh321")) {  //
                        L2Send.syncWeatherIndex();  // 同步紫外线，气压，海拔
                    }

                }
            }
            refreshItems();
        }else if(CONNECT_FAIL.equals(event.getMessage())){
            if(null != loadingDialog && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }else if(CalibrationActivity.SEND_CALIBRATION.equals(event.getMessage())){
            if(loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            startActivity(new Intent(getActivity(), CalibrationActivity.class));
        }else if(CalibrationActivity.CANCEL_CALIBRATION.equals(event.getMessage())){
            if(loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            if(isAdded()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.pointing), Toast.LENGTH_SHORT).show();
            }
        }else if("constants".equals(event.getMessage())){
            if(constantList.size() > index && constantList.size() > 0){
                byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.SYN_ADDREST_LIST,constantList.get(index).toString().getBytes());  // 手表WiFi  04 4B  TODO --- 同步联系人
                MainService.getInstance().writeToDevice(l2, false);
                index++;
                int precent = (int)(index / (double)constantList.size() * 100);
                loadingDialog.setText(getString(R.string.constants_load) + precent + "%");

            }else if(constantList.size() > 0 && constantList.size() == index){
                if(loadingDialog != null) {
                    loadingDialog.setText(getString(R.string.userdata_synchronize_success));
                    Message msg = new Message();
                    msg.what = 6;
                    handler.sendMessageDelayed(msg,1000);
                }
            }
        }else if("constants_first".equals(event.getMessage())){
            if(loadingDialog == null) {
                loadingDialog = new LoadingDialog(getActivity(), R.style.Custom_Progress, getString(R.string.userdata_synchronize));
                loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.e(TAG,"loading cancel");
                        constantList.clear();
                        index = 0;
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                });
                loadingDialog.show();
            }

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HelperFragment");
    }

    private void refreshItems(){
        String watch = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        if(ISSYNWATCHINFO) {
            if (FIRMWARE_SUPPORT) {   //固件升级
                helperView.findViewById(R.id.firmeware_rel).setVisibility(View.VISIBLE);
                if (MainService.getInstance().getState() == 3) {
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION))) {
                        tv_firme_number.setText(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION));
                    }
                } else {
                    tv_firme_number.setText("");
                }
            } else {
                helperView.findViewById(R.id.firmeware_rel).setVisibility(View.GONE);
            }

            if (AUTO_HEART) {     //心率自动检测
                helperView.findViewById(R.id.rl_heart_check).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_heart_check).setVisibility(View.GONE);
            }
            if (ECG) {     //心率自动检测
                helperView.findViewById(R.id.rl_ecg_check).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_ecg_check).setVisibility(View.GONE);
            }
            if (BODYTEMPERATURE) {     //心率自动检测
                helperView.findViewById(R.id.rl_temp_check).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_temp_check).setVisibility(View.GONE);
            }

            if (MESSAGE_PUSH) {   //来电提醒
                helperView.findViewById(R.id.rl_call_notify).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_call_notify).setVisibility(View.GONE);
            }

            if (SMS_NOTIFICATION) {   //短信提醒
                helperView.findViewById(R.id.rl_sms_notify).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_sms_notify).setVisibility(View.GONE);
            }

            if (SEDENTARY_CLOCK) {   //久坐提醒
                helperView.findViewById(R.id.rl_sit_notify).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_sit_notify).setVisibility(View.GONE);
            }

            if (WATER_CLOCK) {   //喝水提醒
                helperView.findViewById(R.id.rl_drink_notify).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_drink_notify).setVisibility(View.GONE);
            }


            if (ALARM_CLOCK) {   //闹钟提醒
                helperView.findViewById(R.id.rl_notify_alarm).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_notify_alarm).setVisibility(View.GONE);
            }


            if (FAZE_MODE) {   //勿扰模式
                helperView.findViewById(R.id.rl_not_disturb).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_not_disturb).setVisibility(View.GONE);
            }


            if (BT_CALL) {   //蓝牙通话
                helperView.findViewById(R.id.assist_input).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.assist_input).setVisibility(View.GONE);
            }


            if (CAMEAR) {   //智能拍照
                if ("3".equals(watch) || "1".equals(watch)) {
                    helperView.findViewById(R.id.camera_switchone).setVisibility(View.VISIBLE);
                }
                helperView.findViewById(R.id.camera_layout).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.camera_layout).setVisibility(View.GONE);
            }


            if (GESTURE_CONTROL) {   //抬手亮屏
                helperView.findViewById(R.id.rl_gesture_control).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_gesture_control).setVisibility(View.GONE);
            }


            if (WECHAT_SPORT) {   //微信运动
                helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.VISIBLE);
                Log.e(TAG, "Utils.getLanguage() = " + Utils.getLanguage());
                if (Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
                    helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.VISIBLE); //微信运动
                } else {
                    helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.GONE); //微信运动
                }
            } else {
                helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.GONE);
            }

            if (REMIND_MODE) {   //提醒模式
                helperView.findViewById(R.id.rl_notify_mode).setVisibility(View.VISIBLE);
            } else {
                helperView.findViewById(R.id.rl_notify_mode).setVisibility(View.GONE);
            }

            helperView.findViewById(R.id.unit_setting_rel).setVisibility(View.VISIBLE);
            if(!UNIT){      //TODO  不支持单位设置
//                SharedPreUtil.setParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.UNIT_TEMPERATURE, SharedPreUtil.YES); // TODO 将温度单位设置为默认的摄氏度
//                Intent intent2 = new Intent();
//                intent2.setAction(MainService.ACTION_NO_UNITS);    // 不支持单位设置
//                getActivity().sendBroadcast(intent2);
//                L2Send.unitSetting(); //todo --- add 1017
            }
            if(POINTER_CALIBRATION){
                helperView.findViewById(R.id.rl_pointer_calibration).setVisibility(View.VISIBLE);//指针校准
            }else{
                helperView.findViewById(R.id.rl_pointer_calibration).setVisibility(View.GONE);//指针校准
            }

            if(DIAL_PUSH){
                if(watch.equals("1") || watch.equals("2")) {
                    helperView.findViewById(R.id.watch_push_rel).setVisibility(View.VISIBLE);  //表盘推送
                }else{
                    helperView.findViewById(R.id.watch_push_rel).setVisibility(View.GONE);  //表盘推送
                }
            }else{
                helperView.findViewById(R.id.watch_push_rel).setVisibility(View.GONE);  //表盘推送
            }
            if(SOS_CALL){
                helperView.findViewById(R.id.soscall_tv).setVisibility(View.VISIBLE);   //SOS
            }else{
                helperView.findViewById(R.id.soscall_tv).setVisibility(View.GONE);      //SOS
            }
            if(ASSISTANT_INPUT){
                helperView.findViewById(R.id.assist_input_rel).setVisibility(View.VISIBLE);   //SOS
            }else{
                helperView.findViewById(R.id.assist_input_rel).setVisibility(View.GONE);      //SOS
            }

            if(FAPIAO &&   Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")){ // todo --- 中文下才显示发票
                helperView.findViewById(R.id.rl_elektronik_fatura).setVisibility(View.VISIBLE);//todo 发票
            }else {
                helperView.findViewById(R.id.rl_elektronik_fatura).setVisibility(View.GONE);//发票
            }

            if(SHOUKUANEWM &&   Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")){
                helperView.findViewById(R.id.rl_receiving_code).setVisibility(View.VISIBLE);//todo 收款二维码
            }else {
                helperView.findViewById(R.id.rl_receiving_code).setVisibility(View.GONE);//收款二维码
            }

            if(CONSTANTS){
                helperView.findViewById(R.id.contacts_tv).setVisibility(View.VISIBLE);
            }else{
                helperView.findViewById(R.id.contacts_tv).setVisibility(View.GONE);
            }

        }else {  // todo -- 没有适配时
        if (watch.equals("")) {      //初始化原始控件
            helperView.findViewById(R.id.pushdial_ti).setVisibility(View.GONE);
            helperView.findViewById(R.id.camera_layout).setVisibility(View.GONE);
            helperView.findViewById(R.id.set_wifi).setVisibility(View.GONE);
            helperView.findViewById(R.id.assist_input).setVisibility(View.GONE);
            helperView.findViewById(R.id.soscall_tv).setVisibility(View.GONE);
            helperView.findViewById(R.id.contacts_tv).setVisibility(View.GONE);
            helperView.findViewById(R.id.find_telephone).setVisibility(View.GONE);  // 默认隐藏找手机的开关

            helperView.findViewById(R.id.camera_layout).setVisibility(View.GONE);//拍照
//            helperView.findViewById(R.id.get_watches).setVisibility(View.GONE);//查找设备  ---- 所有平台都有
            helperView.findViewById(R.id.rl_sit_notify).setVisibility(View.GONE);//久坐提醒
            helperView.findViewById(R.id.rl_drink_notify).setVisibility(View.GONE);//喝水提醒
            helperView.findViewById(R.id.rl_notify_mode).setVisibility(View.GONE);//提醒模式
            helperView.findViewById(R.id.rl_notify_alarm).setVisibility(View.GONE);//闹钟提醒
            helperView.findViewById(R.id.rl_gesture_control).setVisibility(View.GONE);//手势智控
            helperView.findViewById(R.id.rl_heart_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_temp_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_not_disturb).setVisibility(View.GONE); //勿扰模式
            helperView.findViewById(R.id.firmeware_rel).setVisibility(View.GONE); //固件升级
            helperView.findViewById(R.id.camera_switchone).setVisibility(View.GONE); //拍照开关
            helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.GONE); //微信运动
            helperView.findViewById(R.id.unit_setting_rel).setVisibility(View.VISIBLE);//单位设置
            helperView.findViewById(R.id.rl_pointer_calibration).setVisibility(View.GONE);//指针校准
            helperView.findViewById(R.id.watch_push_rel).setVisibility(View.GONE);   //表盘推送
            helperView.findViewById(R.id.assist_input_rel).setVisibility(View.GONE); //协助输入
            helperView.findViewById(R.id.rl_elektronik_fatura).setVisibility(View.GONE);//发票
            helperView.findViewById(R.id.rl_receiving_code).setVisibility(View.GONE);//收款二维码
        } else if (watch.equals("1")) {   //72
            helperView.findViewById(R.id.pushdial_ti).setVisibility(View.GONE);
            helperView.findViewById(R.id.set_wifi).setVisibility(View.GONE);
            helperView.findViewById(R.id.assist_input).setVisibility(View.GONE);
            helperView.findViewById(R.id.soscall_tv).setVisibility(View.VISIBLE);
            helperView.findViewById(R.id.contacts_tv).setVisibility(View.GONE);
            helperView.findViewById(R.id.find_telephone).setVisibility(View.GONE);

            helperView.findViewById(R.id.camera_layout).setVisibility(View.GONE);//拍照
//            helperView.findViewById(R.id.get_watches).setVisibility(View.GONE);//查找设备
            helperView.findViewById(R.id.rl_sit_notify).setVisibility(View.GONE);//久坐提醒
            helperView.findViewById(R.id.rl_drink_notify).setVisibility(View.GONE);//喝水提醒
            helperView.findViewById(R.id.rl_notify_mode).setVisibility(View.GONE);//提醒模式
            helperView.findViewById(R.id.rl_notify_alarm).setVisibility(View.GONE);//闹钟提醒
            helperView.findViewById(R.id.rl_gesture_control).setVisibility(View.GONE);//手势智控
            helperView.findViewById(R.id.rl_heart_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_temp_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_not_disturb).setVisibility(View.GONE); //勿扰模式
            helperView.findViewById(R.id.firmeware_rel).setVisibility(View.GONE); //固件升级
            helperView.findViewById(R.id.camera_switchone).setVisibility(View.VISIBLE); //拍照开关
            helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.GONE); //微信运动
            helperView.findViewById(R.id.unit_setting_rel).setVisibility(View.VISIBLE);//单位设置
            helperView.findViewById(R.id.rl_pointer_calibration).setVisibility(View.GONE);//指针校准
            helperView.findViewById(R.id.watch_push_rel).setVisibility(View.VISIBLE);  //表盘推送
            helperView.findViewById(R.id.assist_input_rel).setVisibility(View.VISIBLE); //协助输入
            helperView.findViewById(R.id.rl_elektronik_fatura).setVisibility(View.GONE);//发票
            helperView.findViewById(R.id.rl_receiving_code).setVisibility(View.GONE);//收款二维码
        } else if (watch.equals("2")) { //ble  --- x2
            helperView.findViewById(R.id.camera_layout).setVisibility(View.VISIBLE);//拍照
//            helperView.findViewById(R.id.get_watches).setVisibility(View.VISIBLE);//查找设备
            helperView.findViewById(R.id.rl_sit_notify).setVisibility(View.VISIBLE);//久坐提醒
            helperView.findViewById(R.id.rl_drink_notify).setVisibility(View.VISIBLE);//喝水提醒
            helperView.findViewById(R.id.rl_notify_mode).setVisibility(View.VISIBLE);//提醒模式
            helperView.findViewById(R.id.rl_notify_alarm).setVisibility(View.VISIBLE);//闹钟提醒    GONE
            helperView.findViewById(R.id.rl_gesture_control).setVisibility(View.VISIBLE);//手势智控
            helperView.findViewById(R.id.rl_heart_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_temp_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_not_disturb).setVisibility(View.VISIBLE); //勿扰模式
            helperView.findViewById(R.id.firmeware_rel).setVisibility(View.VISIBLE); //固件升级

            helperView.findViewById(R.id.pushdial_ti).setVisibility(View.GONE);
//            helperView.findViewById(R.id.camera_layout).setVisibility(View.GONE);
            helperView.findViewById(R.id.set_wifi).setVisibility(View.GONE);
            helperView.findViewById(R.id.assist_input).setVisibility(View.GONE);
            helperView.findViewById(R.id.soscall_tv).setVisibility(View.GONE);
            helperView.findViewById(R.id.contacts_tv).setVisibility(View.GONE);
            helperView.findViewById(R.id.find_telephone).setVisibility(View.GONE);
            helperView.findViewById(R.id.camera_switchone).setVisibility(View.GONE); //拍照开关
            Log.e(TAG,"Utils.getLanguage() = " + Utils.getLanguage());
            if(Utils.getLanguage().equals("zh") || Utils.getLanguage().equals("hk") || Utils.getLanguage().equals("tw")) {
                helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.VISIBLE); //微信运动
            }else {
                helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.GONE); //微信运动
            }
            if(MainService.getInstance().getState() == 3) {
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION))) {
                    tv_firme_number.setText(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION));
                }
            }else{
                tv_firme_number.setText("");
            }
            helperView.findViewById(R.id.unit_setting_rel).setVisibility(View.VISIBLE);//单位设置
            helperView.findViewById(R.id.rl_pointer_calibration).setVisibility(View.GONE);//指针校准
            helperView.findViewById(R.id.watch_push_rel).setVisibility(View.GONE); //表盘推送
            helperView.findViewById(R.id.assist_input_rel).setVisibility(View.GONE); //协助输入
            helperView.findViewById(R.id.rl_elektronik_fatura).setVisibility(View.GONE);//发票
            helperView.findViewById(R.id.rl_receiving_code).setVisibility(View.GONE);//收款二维码
        } else if (watch.equals("3")) {  // mtk
            helperView.findViewById(R.id.find_telephone).setVisibility(View.GONE);  // 找手机开关
//            helperView.findViewById(R.id.find_telephone).setVisibility(View.VISIBLE);
            helperView.findViewById(R.id.assist_input).setVisibility(View.VISIBLE);
            helperView.findViewById(R.id.camera_layout).setVisibility(View.VISIBLE);//拍照
//            helperView.findViewById(R.id.get_watches).setVisibility(View.GONE);//查找设备
            helperView.findViewById(R.id.rl_sit_notify).setVisibility(View.GONE);//久坐提醒
            helperView.findViewById(R.id.rl_drink_notify).setVisibility(View.GONE);//喝水提醒
            helperView.findViewById(R.id.rl_notify_mode).setVisibility(View.GONE);//提醒模式
            helperView.findViewById(R.id.rl_notify_alarm).setVisibility(View.GONE);//闹钟提醒
            helperView.findViewById(R.id.rl_gesture_control).setVisibility(View.GONE);//手势智控
            helperView.findViewById(R.id.rl_heart_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_temp_check).setVisibility(View.GONE);//心率检测
//            helperView.findViewById(R.id.rl_heart_check).setVisibility(View.GONE);//心率检测
//            helperView.findViewById(R.id.rl_temp_check).setVisibility(View.GONE);//心率检测
            helperView.findViewById(R.id.rl_not_disturb).setVisibility(View.GONE); //勿扰模式
            helperView.findViewById(R.id.firmeware_rel).setVisibility(View.GONE); //固件升级
            helperView.findViewById(R.id.camera_switchone).setVisibility(View.VISIBLE); //拍照开关
            helperView.findViewById(R.id.rl_wxsport_notify).setVisibility(View.GONE); //微信运动
            helperView.findViewById(R.id.unit_setting_rel).setVisibility(View.VISIBLE);//单位设置
            helperView.findViewById(R.id.rl_pointer_calibration).setVisibility(View.GONE);//指针校准
            helperView.findViewById(R.id.watch_push_rel).setVisibility(View.GONE); //表盘推送
            helperView.findViewById(R.id.assist_input_rel).setVisibility(View.GONE); //协助输入
            helperView.findViewById(R.id.rl_elektronik_fatura).setVisibility(View.GONE);//发票
            helperView.findViewById(R.id.rl_receiving_code).setVisibility(View.GONE);//收款二维码
        }
        }
//        helperView.findViewById(R.id.rl_ecg_check).setVisibility(View.VISIBLE);
//        helperView.findViewById(R.id.rl_temp_check).setVisibility(View.VISIBLE);
//        helperView.findViewById(R.id.rl_heart_check).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {   // todo ----- 断开设备时，将更多也的条目设置成默认的
        super.onResume();
        tb_call_notify.setChecked((boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, true));
        tb_sms_notify.setChecked((boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, true));

        tb_raise_bright.setChecked((boolean) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.RAISE_BRIGHT, true));

        MobclickAgent.onPageStart("HelperFragment");

        refreshItems();

        if(BTNotificationApplication.getMainService().getState() == MainService.STATE_CONNECTED) {
            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                //todo --- 添加判断mac地址
                String deviceNAME = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME).toString();
                if (!StringUtils.isEmpty(deviceNAME) && deviceNAME.contains("DfuTarg")) {  //  if(!StringUtils.isEmpty(deviceNAME) && deviceNAME.equals("DfuTarg")){   && FirmWareUpdateActivity.isHasDfuDevice
                    if ((boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {  //是否处于固件升级模式
                        if (null != firmDialog) {
                            firmDialog.dismiss();
                            firmDialog = null;
                        }
                        firmDialog = new com.szkct.map.dialog.AlertDialog(getActivity()).builder();
                        firmDialog.setMsg(getString(R.string.firmware_isnot_update_complete));   // 有未升级完成的固件，请继续升级！
                        firmDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())) {
                                    getBleFirmwareInfo();
                                } else {
                                    Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        firmDialog.show();
                    }
                }
            }
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (img_watch_sesarch != null) {
                img_watch_sesarch.clearAnimation();
                img_watch_sesarch.setVisibility(View.GONE);
            }
            geting = false;
            if (timeCount != null) {
                timeCount.cancel();
                timeCount = null;
            }
        }
    }

    private void findDevice() {
        if (geting) {  //没有找到手表标志位
            if (MainService.getInstance().getState() == 3 && !SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {   //STATE_CONNECTED = 3;  // now connected to a remote device     已经连接上了
                byte[] l2 = new L2Bean().L2Pack(BleContants.FIND_COMMAND, BleContants.FIND_DEVICE, null);  // 查找设备
                MainService.getInstance().writeToDevice(l2, true);  // 发了几次，根据回调 销毁 加载框
            }
            img_watch_sesarch.clearAnimation();
            img_watch_sesarch.setVisibility(View.GONE);     // TODO ---- 销毁搜索框
            geting = false;
            return;
        }
        if (MainService.getInstance().getState() == 3 && !SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {    //已经连接上了
            byte[] l2 = new L2Bean().L2Pack(BleContants.FIND_COMMAND, BleContants.FIND_DEVICE, null);  //72 & ble 查找设备
            MainService.getInstance().writeToDevice(l2, true);
            img_watch_sesarch.setVisibility(View.VISIBLE);
            if (operatingAnim != null) {
                img_watch_sesarch.startAnimation(operatingAnim);
            }
            geting = true;
        } else {
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void getBleFirmwareInfoForBk() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE).equals("")) {   //设备code
                String code = SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE);  // TODO -- 手环序列号    固件信息版本
                String updateUrl = Constants.URL_FIRMWARE_UPDATE + code;
                Log.i(TAG, "请求固件url = " + updateUrl);
                if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE).equals("")) {
                    type = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE));   // TODO -- 升级的平台类型
                }
                if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION).equals("")) {
                    firmware_version = SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION);    // TODO --   固件信息版本
                }
                Log.i(TAG, "手环平台 = " + type + "   手环版本 = " + firmware_version);
                helperView.findViewById(R.id.firmeware_rel).setClickable(false);   //设置不可点击
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configTimeout(2 * 1000);// 连接超时
                httpUtils.configSoTimeout(2 * 1000);// 获取数据超时
                httpUtils.send(HttpRequest.HttpMethod.GET, updateUrl, new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        if (responseInfo.result.toString().equals("-1")) {   //网络获取失败
//                            Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
//                            intent.putExtra("url_path", "netError");
//                            startActivity(intent);
                            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.net_error_tip),Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result.toString());
                                Log.i(TAG, "固件版本更新: " + jsonObject.toString());
                                String url_path = jsonObject.getString("file");
                                String file_size = jsonObject.getString("file_size");
                                String server_firmware_version = jsonObject.getString("application_version");
                                if (!DateUtil.versionCompare(firmware_version, server_firmware_version)) {
                                    Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
                                    intent.putExtra("url_path", "");
                                    startActivity(intent);
                                } else {
                                    ////////////////////// todo -- 有新版本才 解绑断开 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                    String code = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE); //
                                    if("4".equals(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE))) {// todo ---    AB312Q ---- 190          "190".equals(code)
                                        if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")
                                                || SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {     //BLE手动断开
                                            SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);
                                            BTNotificationApplication.getMainService().disConnect();
                                        }

                                        BTNotificationApplication.getMainService().setState(STATE_DISCONNECTEDANDUNBIND);
                                        SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.UNBOND, true);
                                        EventBus.getDefault().post(new MessageEvent("unBond"));
                                        BleConnectAdapter bleConnectAdapter = new BleConnectAdapter(getActivity(), BTNotificationApplication.getMainService().getState()
                                                , SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,SharedPreUtil.MACNAME));
                                        swipeMenuListView.setAdapter(bleConnectAdapter);
                                        swipeMenuListView.setMenuCreator(null);
                                        MainService.clearWatchInfo();
                                        refreshItems();
                                        EventBus.getDefault().post(new MessageEvent("unBond"));
                                        Toast.makeText(getActivity(), R.string.unbind_success, Toast.LENGTH_SHORT).show();
                                        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(getActivity(), FirmWareUpdateActivityForBK.class);   // todo -- 这里只请求了 基本的固件信息
                                        intent.putExtra("url_path", url_path);
                                        intent.putExtra("file_size", file_size);
                                        intent.putExtra("server_firmware_version", server_firmware_version);
                                        intent.putExtra("file_type", type);
                                        intent.putExtra("firmware_version", firmware_version);
//                                        startActivity(intent);
                                        startActivityForResult(intent,6);
                                    }else {
                                        Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);   // todo -- 这里只请求了 基本的固件信息
                                        intent.putExtra("url_path", url_path);
                                        intent.putExtra("file_size", file_size);
                                        intent.putExtra("server_firmware_version", server_firmware_version);
                                        intent.putExtra("file_type", type);
                                        intent.putExtra("firmware_version", firmware_version);
                                        startActivity(intent);
                                    }
                                }
                            } catch (JSONException e) {   // is_the_latest_version
                                Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.is_the_latest_version),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        helperView.findViewById(R.id.firmeware_rel).setClickable(true);   //设置可点击
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {
                        Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
                        intent.putExtra("url_path", "");
                        startActivity(intent);
                        helperView.findViewById(R.id.firmeware_rel).setClickable(true);   //设置可点击
                    }
                });
            }
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 6) {
//            if(resultCode == 8) {     //todo --- 发广播给 MainService 通知 自动重连
//                SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
            SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, false);
                Intent intent = new Intent();
                intent.setAction(MainService.ACTION_BKOTASUCCESS_RECON);   // 发广播，销毁加载框    ACTION_BKOTASUCCESS_RECON = "com.kct.ACTION_BKOTASUCCESS_RECON";  // BK平台升级成功后发广播自动重连
                getActivity().sendBroadcast(intent);
                Log.e("liuxiaodebug", "发送ACTION_BKOTASUCCESS_RECON了");
//            }
        }
    }

    private void getBleFirmwareInfo() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getActivity(), R.string.pls_switch_bt_on, Toast.LENGTH_SHORT).show();
            return;
        }
        if (MainService.getInstance().getState() != 3) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if(null!=SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME)&&
                (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MACNAME).toString().contains("DfuTarg"))){  //  "DfuTarg".equals(SharedPreUtil.readPre(getActivity(), SharedPreUtil.NAME, SharedPreUtil.MAC).toString())
            Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
            intent.putExtra("url_path", "");
            startActivity(intent);
        }else{
            if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE).equals("")) {   //设备code
                String code = SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE);
                String updateUrl = Constants.URL_FIRMWARE_UPDATE + code;
                Log.i(TAG, "请求固件url = " + updateUrl);
                if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE).equals("")) {
                    type = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARETYPE));
                }
                if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION).equals("")) {
                    firmware_version = SharedPreUtil.readPre(getActivity(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWAREVERSION);
                }
                Log.i(TAG, "手环平台 = " + type + "   手环版本 = " + firmware_version);
                helperView.findViewById(R.id.firmeware_rel).setClickable(false);   //设置不可点击
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.configTimeout(2 * 1000);// 连接超时
                httpUtils.configSoTimeout(2 * 1000);// 获取数据超时
                httpUtils.send(HttpRequest.HttpMethod.GET, updateUrl, new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        if (responseInfo.result.toString().equals("-1")) {   //网络获取失败

//                            Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
//                            intent.putExtra("url_path", "netError");
//                            startActivity(intent);
                            Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.net_error_tip),Toast.LENGTH_SHORT).show();

                        } else {
                            try {

                                JSONObject jsonObject = new JSONObject(responseInfo.result.toString());
                                Log.i(TAG, "固件版本更新: " + jsonObject.toString());
                                String url_path = jsonObject.getString("file");
                                String file_size = jsonObject.getString("file_size");
                                String server_firmware_version = jsonObject.getString("application_version");
                                if (!DateUtil.versionCompare(firmware_version, server_firmware_version)) {

                                    Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
                                    intent.putExtra("url_path", "");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
                                    intent.putExtra("url_path", url_path);
                                    intent.putExtra("file_size", file_size);
                                    intent.putExtra("server_firmware_version", server_firmware_version);
                                    intent.putExtra("file_type", type);
                                    intent.putExtra("firmware_version", firmware_version);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {   // is_the_latest_version
                                Toast.makeText(BTNotificationApplication.getInstance(),getString(R.string.is_the_latest_version),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        helperView.findViewById(R.id.firmeware_rel).setClickable(true);   //设置可点击
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                        Intent intent = new Intent(getActivity(), FirmWareUpdateActivity.class);
                        intent.putExtra("url_path", "");
                        startActivity(intent);
                        helperView.findViewById(R.id.firmeware_rel).setClickable(true);   //设置可点击

                    }
                });
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private  boolean isHasLast1DaySportData = false;
    private  boolean isHasLast2DaySportData = false;
    private  boolean isHasLast3DaySportData = false;
    private  boolean isHasLast4DaySportData = false;
    private  boolean isHasLast5DaySportData = false;
    private  boolean isHasLast6DaySportData = false;


    private  boolean isHasLast1DaySleepData = false;
    private  boolean isHasLast2DaySleepData = false;
    private  boolean isHasLast3DaySleepData = false;
    private  boolean isHasLast4DaySleepData = false;
    private  boolean isHasLast5DaySleepData = false;
    private  boolean isHasLast6DaySleepData = false;


    private  boolean isHasLast1DayHeartData = false;
    private  boolean isHasLast2DayHeartData = false;
    private  boolean isHasLast3DayHeartData = false;
    private  boolean isHasLast4DayHeartData = false;
    private  boolean isHasLast5DayHeartData = false;
    private  boolean isHasLast6DayHeartData = false;

    private void sendLast3DaysData(int index){
        if(index == 3){
            BTNotificationApplication.needGetSportDayNum = 3;
        }else if(index == 1){
            BTNotificationApplication.needGetSleepDayNum = 3;
        }else if(index == 2){
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
        key2[0] = (byte)index;
        key2[1] = (byte)(DateUtil.getLastDateYear(1)-2000);
        key2[2] = (byte)(DateUtil.getLastDateMonth(1));
        key2[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte)(DateUtil.getHour());
        key2[5] = (byte)(DateUtil.getMinute());
        key2[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte)index;
        key3[1] = (byte)(DateUtil.getLastDateYear(2)-2000);
        key3[2] = (byte)(DateUtil.getLastDateMonth(2));
        key3[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte)(DateUtil.getHour());
        key3[5] = (byte)(DateUtil.getMinute());
        key3[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

    }

    private void sendLast4DaysData(int index){
        if(index == 3){
            BTNotificationApplication.needGetSportDayNum = 4;
        }else if(index == 1){
            BTNotificationApplication.needGetSleepDayNum = 4;
        }else if(index == 2){
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
        key2[0] = (byte)index;
        key2[1] = (byte)(DateUtil.getLastDateYear(1)-2000);
        key2[2] = (byte)(DateUtil.getLastDateMonth(1));
        key2[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte)(DateUtil.getHour());
        key2[5] = (byte)(DateUtil.getMinute());
        key2[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte)index;
        key3[1] = (byte)(DateUtil.getLastDateYear(2)-2000);
        key3[2] = (byte)(DateUtil.getLastDateMonth(2));
        key3[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte)(DateUtil.getHour());
        key3[5] = (byte)(DateUtil.getMinute());
        key3[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte)index;
        key4[1] = (byte)(DateUtil.getLastDateYear(3)-2000);
        key4[2] = (byte)(DateUtil.getLastDateMonth(3));
        key4[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte)(DateUtil.getHour());
        key4[5] = (byte)(DateUtil.getMinute());
        key4[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        MainService.getInstance().writeToDevice(l24, true);
    }

    private void sendLast5DaysData(int index){
        if(index == 3){
            BTNotificationApplication.needGetSportDayNum = 5;
        }else if(index == 1){
            BTNotificationApplication.needGetSleepDayNum = 5;
        }else if(index == 2){
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
        key2[0] = (byte)index;
        key2[1] = (byte)(DateUtil.getLastDateYear(1)-2000);
        key2[2] = (byte)(DateUtil.getLastDateMonth(1));
        key2[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte)(DateUtil.getHour());
        key2[5] = (byte)(DateUtil.getMinute());
        key2[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte)index;
        key3[1] = (byte)(DateUtil.getLastDateYear(2)-2000);
        key3[2] = (byte)(DateUtil.getLastDateMonth(2));
        key3[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte)(DateUtil.getHour());
        key3[5] = (byte)(DateUtil.getMinute());
        key3[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte)index;
        key4[1] = (byte)(DateUtil.getLastDateYear(3)-2000);
        key4[2] = (byte)(DateUtil.getLastDateMonth(3));
        key4[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte)(DateUtil.getHour());
        key4[5] = (byte)(DateUtil.getMinute());
        key4[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        MainService.getInstance().writeToDevice(l24, true);

        byte[] key5 = new byte[7];
        key5[0] = (byte)index;
        key5[1] = (byte)(DateUtil.getLastDateYear(4)-2000);
        key5[2] = (byte)(DateUtil.getLastDateMonth(4));
        key5[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(4));
        key5[4] = (byte)(DateUtil.getHour());
        key5[5] = (byte)(DateUtil.getMinute());
        key5[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l25 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key5);
        MainService.getInstance().writeToDevice(l25, true);
    }

    private void sendLast6DaysData(int index){
        if(index == 3){
            BTNotificationApplication.needGetSportDayNum = 6;
        }else if(index == 1){
            BTNotificationApplication.needGetSleepDayNum = 6;
        }else if(index == 2){
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
        key2[0] = (byte)index;
        key2[1] = (byte)(DateUtil.getLastDateYear(1)-2000);
        key2[2] = (byte)(DateUtil.getLastDateMonth(1));
        key2[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte)(DateUtil.getHour());
        key2[5] = (byte)(DateUtil.getMinute());
        key2[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte)index;
        key3[1] = (byte)(DateUtil.getLastDateYear(2)-2000);
        key3[2] = (byte)(DateUtil.getLastDateMonth(2));
        key3[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(2));
        key3[4] = (byte)(DateUtil.getHour());
        key3[5] = (byte)(DateUtil.getMinute());
        key3[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte)index;
        key4[1] = (byte)(DateUtil.getLastDateYear(3)-2000);
        key4[2] = (byte)(DateUtil.getLastDateMonth(3));
        key4[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte)(DateUtil.getHour());
        key4[5] = (byte)(DateUtil.getMinute());
        key4[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        MainService.getInstance().writeToDevice(l24, true);

        byte[] key5 = new byte[7];
        key5[0] = (byte)index;
        key5[1] = (byte)(DateUtil.getLastDateYear(4)-2000);
        key5[2] = (byte)(DateUtil.getLastDateMonth(4));
        key5[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(4));
        key5[4] = (byte)(DateUtil.getHour());
        key5[5] = (byte)(DateUtil.getMinute());
        key5[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l25 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key5);
        MainService.getInstance().writeToDevice(l25, true);

        byte[] key6 = new byte[7];
        key6[0] = (byte)index;
        key6[1] = (byte)(DateUtil.getLastDateYear(5)-2000);
        key6[2] = (byte)(DateUtil.getLastDateMonth(5));
        key6[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(5));
        key6[4] = (byte)(DateUtil.getHour());
        key6[5] = (byte)(DateUtil.getMinute());
        key6[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l26 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key6);
        MainService.getInstance().writeToDevice(l26, true);
    }

    private void sendLast7DaysData(int index){
        if(index == 3){
            BTNotificationApplication.needGetSportDayNum = 7;
        }else if(index == 1){
            BTNotificationApplication.needGetSleepDayNum = 7;
        }else if(index == 2){
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
        key2[0] = (byte)index;
        key2[1] = (byte)(DateUtil.getLastDateYear(1)-2000);
        key2[2] = (byte)(DateUtil.getLastDateMonth(1));
        key2[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte)(DateUtil.getHour());
        key2[5] = (byte)(DateUtil.getMinute());
        key2[6] = (byte)((System.currentTimeMillis()/1000)%60);

//                String t1 = UtilsLX.bytesToHexString(key2);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        String t2 = UtilsLX.bytesToHexString(l22);    // 0A00A000070311061E0F2706
        Log.e(TAG, "第2天--" + UtilsLX.bytesToHexString(l22));
        MainService.getInstance().writeToDevice(l22, true);

        byte[] key3 = new byte[7];
        key3[0] = (byte)index;
        key3[1] = (byte)(DateUtil.getLastDateYear(2)-2000);  // 17
        key3[2] = (byte)(DateUtil.getLastDateMonth(2));  // 6
        key3[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(2));   // 29
        key3[4] = (byte)(DateUtil.getHour());
        key3[5] = (byte)(DateUtil.getMinute());
        key3[6] = (byte)((System.currentTimeMillis()/1000)%60);

//                String t41 = UtilsLX.bytesToHexString(key3);

        byte[] l23 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key3);
        String t3 = UtilsLX.bytesToHexString(l23);   // 0A00A00007030000000F2729
        Log.e(TAG, "第3天--" + UtilsLX.bytesToHexString(l23));
        MainService.getInstance().writeToDevice(l23, true);

        byte[] key4 = new byte[7];
        key4[0] = (byte)index;
        key4[1] = (byte)(DateUtil.getLastDateYear(3)-2000);
        key4[2] = (byte)(DateUtil.getLastDateMonth(3));
        key4[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(3));
        key4[4] = (byte)(DateUtil.getHour());
        key4[5] = (byte)(DateUtil.getMinute());
        key4[6] = (byte)((System.currentTimeMillis()/1000)%60);

//                String t44 = UtilsLX.bytesToHexString(key4);
        byte[] l24 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key4);
        String t4 = UtilsLX.bytesToHexString(l24);
        Log.e(TAG, "第4天--" + UtilsLX.bytesToHexString(l24));
        MainService.getInstance().writeToDevice(l24, true);

        byte[] key5 = new byte[7];
        key5[0] = (byte)index;
        key5[1] = (byte)(DateUtil.getLastDateYear(4)-2000);
        key5[2] = (byte)(DateUtil.getLastDateMonth(4));
        key5[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(4));
        key5[4] = (byte)(DateUtil.getHour());
        key5[5] = (byte)(DateUtil.getMinute());
        key5[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l25 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key5);
        Log.e(TAG, "第5天--" + UtilsLX.bytesToHexString(l25));
        MainService.getInstance().writeToDevice(l25, true);

        byte[] key6 = new byte[7];
        key6[0] = (byte)index;
        key6[1] = (byte)(DateUtil.getLastDateYear(5)-2000);
        key6[2] = (byte)(DateUtil.getLastDateMonth(5));
        key6[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(5));
        key6[4] = (byte)(DateUtil.getHour());
        key6[5] = (byte)(DateUtil.getMinute());
        key6[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l26 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key6);
        Log.e(TAG, "第6天--" + UtilsLX.bytesToHexString(l26));
        MainService.getInstance().writeToDevice(l26, true);

        byte[] key7 = new byte[7];
        key7[0] = (byte)index;
        key7[1] = (byte)(DateUtil.getLastDateYear(6)-2000);
        key7[2] = (byte)(DateUtil.getLastDateMonth(6));
        key7[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(6));
        key7[4] = (byte)(DateUtil.getHour());
        key7[5] = (byte)(DateUtil.getMinute());
        key7[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l27 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key7);
        Log.e(TAG, "第7天--" + UtilsLX.bytesToHexString(l27));
        MainService.getInstance().writeToDevice(l27, true);
    }

    private void getLast6DaysData(Boolean isHasLast5DayData,int index){
        if(!isHasLast5DayData){  //TODO ---  前5天没有数据 --- 取前5天的数据  （包括今天）
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if(!StringUtils.isEmpty(isSync7DaysData)){
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 6);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast6DaysData(3);
                }else if(index == 1){
                    sendLast6DaysData(1);
                }else if(index == 2){
                    sendLast6DaysData(2);
                }


            }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 6);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast6DaysData(3);
                }else if(index == 1){
                    sendLast6DaysData(1);
                }else if(index == 2){
                    sendLast6DaysData(2);
                }
            }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 6);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast6DaysData(3);
                }else if(index == 1){
                    sendLast6DaysData(1);
                }else if(index == 2){
                    sendLast6DaysData(2);
                }
            }else {
                if(index == 3){
                    getLast5DaysData(isHasLast4DaySportData, index);
                }else if(index == 1){
                    getLast5DaysData(isHasLast4DaySleepData, index);
                }else if(index == 2){
                    getLast5DaysData(isHasLast4DayHeartData, index);
                }
            }
        }else {
            if(index == 3){
                getLast5DaysData(isHasLast4DaySportData, index);
            }else if(index == 1){
                getLast5DaysData(isHasLast4DaySleepData, index);
            }else if(index == 2){
                getLast5DaysData(isHasLast4DayHeartData, index);
            }
        }
    }

    private void getLast5DaysData(Boolean isHasLast4DayData,int index){
        if(!isHasLast4DayData){  // TODO -- 前4天没有数据 --- 取前4天的数据
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if(!StringUtils.isEmpty(isSync7DaysData)){
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 5);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast5DaysData(3);
                }else if(index == 1){
                    sendLast5DaysData(1);
                }else if(index == 2){
                    sendLast5DaysData(2);
                }
            }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据
                calendar.add(Calendar.DAY_OF_MONTH, 5);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast5DaysData(3);
                }else if(index == 1){
                    sendLast5DaysData(1);
                }else if(index == 2){
                    sendLast5DaysData(2);
                }
            }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据
                calendar.add(Calendar.DAY_OF_MONTH, 5);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                if(index == 3){
                    sendLast5DaysData(3);
                }else if(index == 1){
                    sendLast5DaysData(1);
                }else if(index == 2){
                    sendLast5DaysData(2);
                }
            }else {
                if(index == 3){
                    getLast4DaysData(isHasLast3DaySportData, index);
                }else if(index == 1){
                    getLast4DaysData(isHasLast3DaySleepData, index);
                }else if(index == 2){
                    getLast4DaysData(isHasLast3DayHeartData, index);
                }
            }
        }else {
            if(index == 3){
                getLast4DaysData(isHasLast3DaySportData, index);
            }else if(index == 1){
                getLast4DaysData(isHasLast3DaySleepData, index);
            }else if(index == 2){
                getLast4DaysData(isHasLast3DayHeartData, index);
            }
        }
    }

    private void getLast4DaysData(Boolean isHasLast3DayData,int index){
        if(!isHasLast3DayData){  //TODO --  前3天没有数据 --- 取前3天的数据
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if(!StringUtils.isEmpty(isSync7DaysData)){
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 4);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast4DaysData(3);
                }else if(index == 1){
                    sendLast4DaysData(1);
                }else if(index == 2){
                    sendLast4DaysData(2);
                }

            }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 4);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast4DaysData(3);
                }else if(index == 1){
                    sendLast4DaysData(1);
                }else if(index == 2){
                    sendLast4DaysData(2);
                }

            }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 4);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                if(index == 3){
                    sendLast4DaysData(3);
                }else if(index == 1){
                    sendLast4DaysData(1);
                }else if(index == 2){
                    sendLast4DaysData(2);
                }

            }else {
                if(index == 3){
                    getLast3DaysData(isHasLast2DaySportData, index);
                }else if(index == 1){
                    getLast3DaysData(isHasLast2DaySleepData, index);
                }else if(index == 2){
                    getLast3DaysData(isHasLast2DayHeartData, index);
                }
            }
        }else {
            if(index == 3){
                getLast3DaysData(isHasLast2DaySportData, index);
            }else if(index == 1){
                getLast3DaysData(isHasLast2DaySleepData, index);
            }else if(index == 2){
                getLast3DaysData(isHasLast2DayHeartData, index);
            }
        }
    }

    private void getLast3DaysData(Boolean isHasLast2DayData,int index){
        if(!isHasLast2DayData){  //TODO 前2天没有数据 --- 取前2天的数据
            String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
            String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            String[] oldRecords = new String[2];
            if(!StringUtils.isEmpty(isSync7DaysData)){
                oldRecords = isSync7DaysData.split("#");
            }

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 3);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                if(index == 3){
                    sendLast3DaysData(3);
                }else if(index == 1){
                    sendLast3DaysData(1);
                }else if(index == 2){
                    sendLast3DaysData(2);
                }
            }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 3);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast3DaysData(3);
                }else if(index == 1){
                    sendLast3DaysData(1);
                }else if(index == 2){
                    sendLast3DaysData(2);
                }
            }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                calendar.add(Calendar.DAY_OF_MONTH, 3);  //设置为后7天
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                if(index == 3){
                    sendLast3DaysData(3);
                }else if(index == 1){
                    sendLast3DaysData(1);
                }else if(index == 2){
                    sendLast3DaysData(2);
                }
            }else {
                if(index == 3){
                    getLast2DaysData(isHasLast1DaySportData, index);
                }else if(index == 1){
                    getLast2DaysData(isHasLast1DaySleepData, index);
                }else if(index == 2){
                    getLast2DaysData(isHasLast1DayHeartData, index);
                }
            }
        }else {
            if(index == 3){
                getLast2DaysData(isHasLast1DaySportData, index);
            }else if(index == 1){
                getLast2DaysData(isHasLast1DaySleepData, index);
            }else if(index == 2){
                getLast2DaysData(isHasLast1DayHeartData, index);
            }
        }
    }

    private void getLast2DaysData(Boolean isHasLast2DayData,int index){

        if(index == 3){
            BTNotificationApplication.needGetSportDayNum = 2;
        }else if(index == 1){
            BTNotificationApplication.needGetSleepDayNum = 2;
        }else if(index == 2){
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
        key2[0] = (byte)index;
        key2[1] = (byte)(DateUtil.getLastDateYear(1)-2000);
        key2[2] = (byte)(DateUtil.getLastDateMonth(1));
        key2[3] = (byte)(DateUtil.getCurrentMonthLastOneDay(1));
        key2[4] = (byte)(DateUtil.getHour());
        key2[5] = (byte)(DateUtil.getMinute());
        key2[6] = (byte)((System.currentTimeMillis()/1000)%60);
        byte[] l22 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key2);
        MainService.getInstance().writeToDevice(l22, true);

    }      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendSyncData(int index){
        //todo ---- 1：第一次同步时取 7 天的数据，后面都取两天的数据  （）
        if (db == null) {
            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
        }
        if(index == 3){  // 计步  --- X2只有分段步数
            Query query = null;
            query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(RunDataDao.Properties.Step.notEq("0"))
                    .build();  // 1489824000     2017-03-16 19:00:00    .where(RunDataDao.Properties.Date.eq(arr.get(1).getBinTime().substring(0, 10)))   ---   .where(RunDataDao.Properties.Step.notEq("0")).build();
            List<RunData> slist = query.list();  // TODO ---获取到本地运动所有的计步数据

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  //  TODO---- 当前天的日期   --- 2017-09-22

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
            String  mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 前1天的数据     ---- 2017-06-30
            isHasLast1DaySportData = false;

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) - 2);
            String  mcurDate2 = getDateFormat.format(calendar2.getTime());   // 2017-06-26       前2天的数据    ----- 2017-06-29
            isHasLast2DaySportData = false;

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.DAY_OF_MONTH, calendar3.get(Calendar.DAY_OF_MONTH) - 3);
            String  mcurDate3 = getDateFormat.format(calendar3.getTime());   // 2017-06-26       前3天的数据
            isHasLast3DaySportData = false;

            Calendar calendar4 = Calendar.getInstance();
            calendar4.set(Calendar.DAY_OF_MONTH, calendar4.get(Calendar.DAY_OF_MONTH) - 4);
            String  mcurDate4 = getDateFormat.format(calendar4.getTime());  // 2017-06-25        前4天的数据
            isHasLast4DaySportData = false;

            Calendar calendar5 = Calendar.getInstance();
            calendar5.set(Calendar.DAY_OF_MONTH, calendar5.get(Calendar.DAY_OF_MONTH) - 5);
            String  mcurDate5 = getDateFormat.format(calendar5.getTime());  // 2017-06-24        前5天的数据
            isHasLast5DaySportData = false;

            Calendar calendar6 = Calendar.getInstance();
            calendar6.set(Calendar.DAY_OF_MONTH, calendar6.get(Calendar.DAY_OF_MONTH) - 6);
            String  mcurDate6 = getDateFormat.format(calendar6.getTime()); // 2017-06-23         前6天的数据
            isHasLast6DaySportData = false;
            if(slist.size() > 0){
                for (int i = 0; i < slist.size(); i++) {  // TODO --- 遍历所有的计步数据，获取该条数据对应的日期
                    String mItemData = slist.get(i).getDate(); // 对应条目的日期
                    if(mItemData.equals(mcurDate1)){  // 前1天有数据
                        isHasLast1DaySportData = true;
                    }
                    if(mItemData.equals(mcurDate2)){  // 前2天有数据
                        isHasLast2DaySportData = true;
                    }
                    if(mItemData.equals(mcurDate3)){  // 前3天有数据
                        isHasLast3DaySportData = true;
                    }
                    if(mItemData.equals(mcurDate4)){  // 前4天有数据
                        isHasLast4DaySportData = true;
                    }
                    if(mItemData.equals(mcurDate5)){  // 前5天有数据
                        isHasLast5DaySportData = true;
                    }
                    if(mItemData.equals(mcurDate6)){  // 前6天有数据
                        isHasLast6DaySportData = true;
                    }
                }
            }
//            }

            if(!isHasLast6DaySportData){ // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
                String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
                String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                String[] oldRecords = new String[2];
                if(!StringUtils.isEmpty(isSync7DaysData)){
                    oldRecords = isSync7DaysData.split("#");
                }

                if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天 2017-09-25    2017-10-02    ----- 将此日期 保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(3);
                }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(3);
                }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(3);
                }else {
                    getLast6DaysData(isHasLast5DaySportData,3);
                }
            }else {
                getLast6DaysData(isHasLast5DaySportData,3);
            }
        }else if(index == 1){  // todo ---- 睡眠                                9999999999999999999999999999999999999999999999999999999999999999999999999
            Query query = null;
            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
                query = db.getSleepDao().queryBuilder()
                        .where(SleepDataDao.Properties.Mac
                                .eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            } else {
                query = db.getSleepDao().queryBuilder()
                        .where(SleepDataDao.Properties.Mac
                                .eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).build();
            }
            List<SleepData> slist = query.list();  // TODO ---获取到本地睡眠所有的数据

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  // 2017-06-28     ----- 2017-07-01

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
            String  mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 前1天的数据     ---- 2017-06-30
            isHasLast1DaySleepData = false;

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) - 2);
            String  mcurDate2 = getDateFormat.format(calendar2.getTime());   // 2017-06-26       前2天的数据    ----- 2017-06-29
            isHasLast2DaySleepData = false;

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.DAY_OF_MONTH, calendar3.get(Calendar.DAY_OF_MONTH) - 3);
            String  mcurDate3 = getDateFormat.format(calendar3.getTime());   // 2017-06-26       前3天的数据
            isHasLast3DaySleepData = false;

            Calendar calendar4 = Calendar.getInstance();
            calendar4.set(Calendar.DAY_OF_MONTH, calendar4.get(Calendar.DAY_OF_MONTH) - 4);
            String  mcurDate4 = getDateFormat.format(calendar4.getTime());  // 2017-06-25        前4天的数据
            isHasLast4DaySleepData = false;

            Calendar calendar5 = Calendar.getInstance();
            calendar5.set(Calendar.DAY_OF_MONTH, calendar5.get(Calendar.DAY_OF_MONTH) - 5);
            String  mcurDate5 = getDateFormat.format(calendar5.getTime());  // 2017-06-24        前5天的数据
            isHasLast5DaySleepData = false;

            Calendar calendar6 = Calendar.getInstance();
            calendar6.set(Calendar.DAY_OF_MONTH, calendar6.get(Calendar.DAY_OF_MONTH) - 6);
            String  mcurDate6 = getDateFormat.format(calendar6.getTime()); // 2017-06-23         前6天的数据
            isHasLast6DaySleepData = false;

            if(slist.size() > 0){
                for (int i = 0; i < slist.size(); i++) {  // TODO --- 遍历所有的计步数据，获取该条数据对应的日期
                    String mItemData = slist.get(i).getDate(); // 对应条目的日期
                    if(mItemData.equals(mcurDate1)){  // 前1天有数据
                        isHasLast1DaySleepData = true;
                    }
                    if(mItemData.equals(mcurDate2)){  // 前2天有数据
                        isHasLast2DaySleepData = true;
                    }
                    if(mItemData.equals(mcurDate3)){  // 前3天有数据
                        isHasLast3DaySleepData = true;
                    }
                    if(mItemData.equals(mcurDate4)){  // 前4天有数据
                        isHasLast4DaySleepData = true;
                    }
                    if(mItemData.equals(mcurDate5)){  // 前5天有数据
                        isHasLast5DaySleepData = true;
                    }
                    if(mItemData.equals(mcurDate6)){  // 前6天有数据
                        isHasLast6DaySleepData = true;
                    }
                }
            }
            if(!isHasLast6DaySleepData){ // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
                String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
                String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                String[] oldRecords = new String[2];
                if(!StringUtils.isEmpty(isSync7DaysData)){
                    oldRecords = isSync7DaysData.split("#");
                }
                if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                    sendLast7DaysData(1);
                }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
                    sendLast7DaysData(1);
                }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
//                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE,last7Day);// 保存后7天开始日期
                    sendLast7DaysData(1);
                }else {
                    getLast6DaysData(isHasLast5DaySleepData,1);
                }
            }else {
                getLast6DaysData(isHasLast5DaySleepData,1);
            }
        }else if(index == 2){  // todo ---- 心率     999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999
            Query query = null;
            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
                query = db.getHearDao().queryBuilder()
                        .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            } else {  //  不需要展示的设备的数据的mac地址
                query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).build();  // 根据日期 查询 运动数据
            }
//            query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Date.eq(strDate)).orderAsc(HearDataDao.Properties.Times).build();    // todo ---- 需添加按mac 地址查询
            List<HearData> slist = query.list();  // TODO ---获取到本地心率所有的数据

            Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
            calendar.setTime(new Date());
            String  mcurDate = getDateFormat.format(calendar.getTime());  // 2017-06-28     ----- 2017-07-01

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH) - 1);
            String  mcurDate1 = getDateFormat.format(calendar1.getTime());  // todo --- 2017-06-27 前1天的数据     ---- 2017-06-30
            isHasLast1DayHeartData = false;

            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH) - 2);
            String  mcurDate2 = getDateFormat.format(calendar2.getTime());   // 2017-06-26       前2天的数据    ----- 2017-06-29
            isHasLast2DayHeartData = false;

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.DAY_OF_MONTH, calendar3.get(Calendar.DAY_OF_MONTH) - 3);
            String  mcurDate3 = getDateFormat.format(calendar3.getTime());   // 2017-06-26       前3天的数据
            isHasLast3DayHeartData = false;

            Calendar calendar4 = Calendar.getInstance();
            calendar4.set(Calendar.DAY_OF_MONTH, calendar4.get(Calendar.DAY_OF_MONTH) - 4);
            String  mcurDate4 = getDateFormat.format(calendar4.getTime());  // 2017-06-25        前4天的数据
            isHasLast4DayHeartData = false;

            Calendar calendar5 = Calendar.getInstance();
            calendar5.set(Calendar.DAY_OF_MONTH, calendar5.get(Calendar.DAY_OF_MONTH) - 5);
            String  mcurDate5 = getDateFormat.format(calendar5.getTime());  // 2017-06-24        前5天的数据
            isHasLast5DayHeartData = false;

            Calendar calendar6 = Calendar.getInstance();
            calendar6.set(Calendar.DAY_OF_MONTH, calendar6.get(Calendar.DAY_OF_MONTH) - 6);
            String  mcurDate6 = getDateFormat.format(calendar6.getTime()); // 2017-06-23         前6天的数据
            isHasLast6DayHeartData = false;

            if(slist.size() > 0){
                for (int i = 0; i < slist.size(); i++) {  // TODO --- 遍历所有的计步数据，获取该条数据对应的日期
                    String mItemData = slist.get(i).getDate(); // 对应条目的日期
                    if(mItemData.equals(mcurDate1)){  // 前1天有数据
                        isHasLast1DayHeartData = true;
                    }

                    if(mItemData.equals(mcurDate2)){  // 前2天有数据
                        isHasLast2DayHeartData = true;
                    }

                    if(mItemData.equals(mcurDate3)){  // 前3天有数据
                        isHasLast3DayHeartData = true;
                    }

                    if(mItemData.equals(mcurDate4)){  // 前4天有数据
                        isHasLast4DayHeartData = true;
                    }

                    if(mItemData.equals(mcurDate5)){  // 前5天有数据
                        isHasLast5DayHeartData = true;
                    }

                    if(mItemData.equals(mcurDate6)){  // 前6天有数据
                        isHasLast6DayHeartData = true;
                    }
                }
            }

            if(!isHasLast6DayHeartData){ // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
                String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
                String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
                String[] oldRecords = new String[2];
                if(!StringUtils.isEmpty(isSync7DaysData)){
                    oldRecords = isSync7DaysData.split("#");
                }

                if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(2);
                }else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

                    sendLast7DaysData(2);
                }else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

                    calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
                    Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
                    SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE,last7Day);// 保存后7天开始日期
                    sendLast7DaysData(2);
                }else {
                    getLast6DaysData(isHasLast5DayHeartData,2);
                }
            }else {
                getLast6DaysData(isHasLast5DayHeartData,2);
            }
        }
    }///
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void showUnbindDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sweet_warn);
        builder.setMessage(R.string.issure_unbind_device);
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
                        //断开连接 or 解绑
                        if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")
                               ) {     //BLE手动断开    || SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")
                            SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);
                            BTNotificationApplication.getMainService().disConnect();
                        }

                        if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){
                            SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);

                            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
                            Method createBondMethod;
                            try {
                                createBondMethod = BluetoothDevice.class.getMethod("removeBond");    // todo --- 智能机手动断开时，主动解绑
                                createBondMethod.invoke(device);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            BTNotificationApplication.getMainService().disConnect();
                        }

                        if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {     //Mtk手动断开
                            SharedPreUtil.setParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.BLE_CLICK_STOP, true);
                            WearableManager.getInstance().disconnect();
                        }
                        BTNotificationApplication.getMainService().setState(STATE_DISCONNECTEDANDUNBIND);
                        SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.UNBOND,true);
                /*SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.MACNAME,"");
                SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.MAC,"");
                SharedPreUtil.setParam(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH,"");*/
                        EventBus.getDefault().post(new MessageEvent("unBond"));
                        BleConnectAdapter bleConnectAdapter = new BleConnectAdapter(getActivity(), BTNotificationApplication.getMainService().getState()
                                , SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,SharedPreUtil.MACNAME));
                        swipeMenuListView.setAdapter(bleConnectAdapter);
                        swipeMenuListView.setMenuCreator(null);
                        MainService.clearWatchInfo();
                        refreshItems();
                        EventBus.getDefault().post(new MessageEvent("unBond"));
                        Toast.makeText(getActivity(), R.string.unbind_success, Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();
    }


    //--------------------------同步联系人--------------------------//

    /**
     * 获取库Phon表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    /**
     * 联系人名称
     **/
    private static ArrayList<String> mContactsName = new ArrayList<String>();

    /**
     * 联系人头像
     **/
    private static ArrayList<String> mContactsNumber = new ArrayList<String>();

    public void SynchronizeContacts(final Context context) {
        final StringBuffer sb = new StringBuffer();
        sb.append(R.string.no_or_update);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.data_synchronization);
        builder.setMessage(R.string.sync_contacts);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MainService.getInstance().getState() != 3) {
                    Toast.makeText(context, context.getString(R.string.contacts_synchronization_failed), Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mContactsName.clear();
                        mContactsNumber.clear();
                        getPhoneContacts(getActivity());
                        getSIMContacts(getActivity());
                        StringBuffer stringBuffer = new StringBuffer();
                        constantSum = mContactsNumber.size();
                        constantIndex = mContactsNumber.size() / 10;
                        for (int i = 0; i < mContactsNumber.size(); i++) {
                            if (mContactsNumber.size() - 1 != i) {
                                stringBuffer.append(mContactsName.get(i) + "|" + mContactsNumber.get(i) + "^");  // 名字|号码^
                            } else {
                                stringBuffer.append(mContactsName.get(i) + "|" + mContactsNumber.get(i));
                            }
                            if (mContactsNumber.size() >= 10) {
                                if (i % 10 == 0 && i != 0) {
                                    constantList.add(stringBuffer.toString());
                                    stringBuffer.delete(0, stringBuffer.length());
                                } else {
                                    if (i == (mContactsNumber.size() - 1)) {
                                        constantList.add(stringBuffer.toString());
                                        stringBuffer.delete(0, stringBuffer.length());
                                    }
                                }
                            }else{
                                if (i == (mContactsNumber.size() - 1)) {
                                    constantList.add(stringBuffer.toString());
                                    stringBuffer.delete(0, stringBuffer.length());
                                }
                            }
                        }
                        Log.e(TAG,"constantList = " + constantList.size());
                        if (constantList.size() > 0) {
                            byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.SYN_ADDREST_LIST, constantList.get(index).toString().getBytes());  // 手表WiFi  04 4B  TODO --- 同步联系人
                            MainService.getInstance().writeToDevice(l2, false);
                            index++;
                            EventBus.getDefault().post(new MessageEvent("constants_first"));
                        }
                        mContactsName.clear();
                        mContactsNumber.clear();
                    }
                }.start();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        }).create();
        builder.show();

    }

    /**
     * 得到手机通讯录联系人信息
     **/
    public static void getPhoneContacts(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                //   Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                //    Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                Log.e("phoneNumber", contactName + "====" + phoneNumber);
                //得到联系人头像Bitamp
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber.replace(" ", ""));
                //   mContactsPhonto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    /**
     * 得到手机SIM卡联系人人信息
     **/
    public static void getSIMContacts(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
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
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //Sim卡中没有联系人头像
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber.replace(" ", ""));
            }
            phoneCursor.close();
        }
    }
}
