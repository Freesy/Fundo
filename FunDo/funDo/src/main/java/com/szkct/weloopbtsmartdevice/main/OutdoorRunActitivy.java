package com.szkct.weloopbtsmartdevice.main;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.kct.fundo.btnotification.R;
import com.szkct.map.RealTimeMapActivity;
import com.szkct.map.service.SportService;
import com.szkct.map.shared.StatusShared;
import com.szkct.map.utils.Util;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.activity.NewWaterMakActivity;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.SlideView;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class OutdoorRunActitivy extends AppCompatActivity implements OnClickListener {  //  户外跑

    private final int OUTDOORRUNNING = 0;
    private final int OUTDOORRUN_SUSPEND = 1;
    private final int OUTDOORRUN_STOP = 2;

    public static final int CMD_START_SPORTS = 1;// 开始运动
    public static final int CMD_PAUSE_SPORTS = 2;// 暂停运功
    public static final int CMD_STOP_SERVICE = 3;// 停止服务 保存运动数据
    public static final int CMD_FINISH_SERVICE = 4;// 停止服务 单不保存数据

    public  static  int runstate = 0;
    private TextView run_stop_tv;
    private TextView home_time_tv;
    private LinearLayout activity_outdoorrun_core_ll;
    private ImageButton outdoorrun_start_ib, outdoorrun_suspend_ib, outdoorrun_finish_ib, outdoorrun_lockscreen_ib, outdoorrun_motionsettting_ib;
    private RelativeLayout outdoorrun_bottom_rl, slider_rl;
    SlideView slider;
    private ImageButton outdoorrun_map;
    private OutdoorRunActitivy mContext;

    private ArrayList<LatLng> goolePoints = new ArrayList<LatLng>();// google定位点画线
    private ArrayList<com.amap.api.maps.model.LatLng> gdPoints = new ArrayList<com.amap.api.maps.model.LatLng>();// 高德定位点画线


    private TextView realtime_peisu,realtime_peisu_up;
    float speed;// 速度
    double altitude;// TODO --- 海拔值
    double mile;// 总路程 KM
    int satenum = 0;// 当前GPS数量
    double calorie = 0;// 卡里路
    private static ImageView realtime_gps;
    private TextView realtime_qianka,realtime_qianka_up;
    private TextView home_altitude_tv,home_altitude_tv_up;//海拔
    private TextView realtime_mile,realtime_mile_up;
    private Intent intentSportService;
    private SportService sportService;
    private TextView sport_mode;
    private StatusShared shared;
    private ImageView ib_sporthistory_photo;
    private GpsPointDetailData gpsPoint;

    private double distancegoal = 0;  // 运动模式的运动距离目标
    private int timegoal = 0;          // 运动模式的运动时间目标
    private double kalgoal = 0;           // 运动模式的运动卡路里目标

    private int goalNumSelect = 0;           //选择的运动模式的选项

    private  int countSportTimeNum = 0 ;// 统计运动目标时间的次数
    private  int countDisAndKalNum = 0 ;// 统计运动目标距离和目标卡路里的次数
//    private  int countSportTimeNum = 0 ;// 统计运动目标时间的次数

    private boolean isShowAlertDialog = false;

    // 记录文件保存位置
    private String mFilePath;

    private boolean isMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        shared = new StatusShared(mContext);

        mFilePath = Environment.getExternalStorageDirectory().getPath(); // 获取SD卡路径
        mFilePath = mFilePath + "/" + "photo.png";  // 文件名

        startService();  // todo --- 一进入 Ourdoor 页面 即 启动 SportService
        runstate = 0;

        setContentView(R.layout.activity_outdoorrun);      //  户外跑UI

        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        Intent intent = this.getIntent();
        gpsPoint=(GpsPointDetailData)intent.getSerializableExtra("gpsPoint");  // todo --- 从 HomeFragment 传递过来的   (没用到)

        updateSportmodeSetDatas();

        init();
        registerBoradcastReceiver();
    }

    private void updateSportmodeSetDatas(){
        //todo --- 获取运动模式设置的目标值
        String distancegoalStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.DISTANCEGOAL,"0.5");  // 默认0.5公里
        String timegoalStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.TIMEGOAL,"10");   // 默认10分钟
        String kalgoalStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.KALGOAL,"50");     // 默认50大卡

        String goalNumStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MotionGoal);     //
        if(!StringUtils.isEmpty(goalNumStr)){
            goalNumSelect  = Integer.valueOf(goalNumStr);
        }

        if(!StringUtils.isEmpty(distancegoalStr)){   // TODO --- 这里还需要加判断，如果选择的自由跑，则不提示
            distancegoal = Double.valueOf(distancegoalStr);
        }
        if(!StringUtils.isEmpty(timegoalStr)){
            timegoal = Integer.valueOf(timegoalStr);//5分钟   .substring(0,1)
        }
        if(!StringUtils.isEmpty(kalgoalStr)){
            kalgoal = Double.parseDouble(kalgoalStr);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("OutdoorRunActitivy");
    }

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("OutdoorRunActitivy");

        /**屏幕常亮**/
        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
                SharedPreUtil.CB_RUNSETTING_SCREEN, SharedPreUtil.YES).equals(
                SharedPreUtil.YES)) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        String distancegoalStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.DISTANCEGOAL,"0.5");  // 默认0.5公里
        String timegoalStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.TIMEGOAL,"10");   // 默认10分钟
        String kalgoalStr =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.KALGOAL,"50");     // 默认50大卡

          // 运动距离目标
        if(!StringUtils.isEmpty(distancegoalStr)){
            distancegoal = Double.valueOf(distancegoalStr);
        }

         // 运动时间目标
        if(!StringUtils.isEmpty(timegoalStr)){
            timegoal = Integer.valueOf(timegoalStr);  // .substring(0,1)
        }

         // 运动卡路里目标
        if(!StringUtils.isEmpty(kalgoalStr)){
            kalgoal = Double.parseDouble(kalgoalStr);
        }

    }

    /**
     * 开启服务(获取位置)
     */
    private void startService() {
        intentSportService = new Intent(mContext, SportService.class);
        mContext.startService(intentSportService);

        Util.SPORT_STATUS = 1;//表示运动启动标识
//        SharedPreUtil.savePre(mContext, SharedPreUtil.SPORTSTART, SharedPreUtil.SPORTSTART,1+"");
    }


    /**
     * 暂停服
     */
    private void stopSportService() {
        stopService(intentSportService);
//		Intent s = new Intent(SportService.CMD_RECEIVER);
//		s.putExtra("cmd", CMD_STOP_SERVICE);
//		getActivity().sendBroadcast(s);
//		if (dataReceiver != null) {
//			getActivity().getApplicationContext().unregisterReceiver(dataReceiver);// 取消注册BroadcastReceiver
//			dataReceiver = null;
//		}
    }

    /**
     * 注册广播 ------ 运动相关数据的
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(SportService.SEND_RECEIVER_DATA);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_GPS);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_TIME);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_NETWORK);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_AUTO_PAUSE);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_AUTO_START);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_AUTO_PFINISH);

        myIntentFilter.addAction(MainService.ACTION_SPORTMODE_HINT);  // 更新运动模式 弹框标志的action
        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {    //
            if (intent == null) return;
            String action = intent.getAction();

            if(action.equals(MainService.ACTION_SPORTMODE_HINT)){ //
                isShowAlertDialog = false;
                updateSportmodeSetDatas();
            } else if (action.equals(SportService.SEND_RECEIVER_DATA)) {//接收实时更新经纬度数据
                Toast.makeText(mContext, "进入GPS定位----------", Toast.LENGTH_SHORT).show();
            } else if (action.equals(SportService.SEND_RECEIVER_GPS)) {     //TODO  --- 接收运动服务 发送过来的运动数据
                countDisAndKalNum++;
                Bundle bundledata = intent.getExtras();
                if (bundledata != null) {
                    speed = bundledata.getFloat("speed");//速度
                    altitude = bundledata.getDouble("altitude");//海拔
                    mile = bundledata.getDouble("mile");//距离 米
                    calorie = bundledata.getDouble("calorie");//卡路里
                    String peisu = bundledata.getString("peisu");//配速   ----- 实时配速
                    double latitude_gps = bundledata.getDouble("latitude_gps");//维度
                    double longitude_gps = bundledata.getDouble("longitude_gps");//经度

                    if(mile == 0){
                        realtime_peisu.setText(String.format(Locale.ENGLISH,"%1$01d'%2$01d''", Integer.valueOf(0), (int) 0));
                    }else {
                        if(StringUtils.isEmpty(peisu)){
                            realtime_peisu.setText(String.format(Locale.ENGLISH,"%1$01d'%2$01d''", Integer.valueOf(0), (int) 0));// 配速  ----- 赋值  （配速赋值是根据GPS来的）
                        }else {
                            realtime_peisu.setText(peisu);
                        }
                    }

                    if(isMetric) {
                        realtime_mile.setText(Utils.decimalTo2(mile / 1000, 2) + "");// 里程/千米  todo ---  设置运动距离
                        realtime_mile_up.setText(getString(R.string.kilometer));
                        realtime_peisu_up.setText(getString(R.string.realtime_minutes_km));
                    }else{
                        realtime_mile.setText(Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");// 里程/千米  todo ---  设置运动距离
                        realtime_mile_up.setText(getString(R.string.unit_mi));
                        realtime_peisu_up.setText(getString(R.string.unit_min_mi));
                    }

                    double curSportDistance = Utils.decimalTo2(mile / 1000, 2);
                    if(distancegoal > 0){
                        if(curSportDistance >= distancegoal){
                            if(countDisAndKalNum%5 == 0){
                                if(goalNumSelect == 1){
                                    if(SharedPreUtil.readPre(OutdoorRunActitivy.this, SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_VOICE).equals(SharedPreUtil.YES)) {
                                        showDialog(getString(R.string.goalTarget));
                                    }
                                }
                            }
                        }
                    }

                    if(isMetric) {
                        realtime_qianka.setText((int) calorie + "");// 卡路里    ---- 赋值  （配速赋值是根据GPS来的）  todo --- 设置卡路里
                        realtime_qianka_up.setText(getString(R.string.realtime_calorie));
                    }else{
                        realtime_qianka.setText((int) (calorie * 4.18675) + "");// 卡路里    ---- 赋值  （配速赋值是根据GPS来的）  todo --- 设置卡路里
                        realtime_qianka_up.setText(getString(R.string.unit_kj));
                    }
                    if(kalgoal > 0){
                        if(calorie >= kalgoal){
                            if(countDisAndKalNum%5 == 0){
                                if(goalNumSelect == 3){
//                                    Toast.makeText(mContext, getString(R.string.calorieTarget), Toast.LENGTH_SHORT).show();
                                    if(SharedPreUtil.readPre(OutdoorRunActitivy.this, SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_VOICE).equals(SharedPreUtil.YES)) {
                                        showDialog(getString(R.string.calorieTarget));
                                    }
                                }
                            }
                        }
                    }

                    String alt;
                    if (altitude >= 0) {
//                        alt = "+" + String.valueOf((int) altitude);    // 0.0     +0
                        alt = String.valueOf((int) altitude);
                    } else {
                        alt = String.valueOf((int) altitude) + "";
                    }
                    if(isMetric) {
                        home_altitude_tv.setText(alt + "");// 海拔     ---- 赋值  （配速赋值是根据GPS来的）
                        home_altitude_tv_up.setText(getString(R.string.everyday_rice));
                    }else {
                        home_altitude_tv.setText((int)(Utils.getUnit_mile(Integer.parseInt(alt))) + "");
                        home_altitude_tv_up.setText(getString(R.string.unit_ft));
                    }

                    gdPoints.add(new com.amap.api.maps.model.LatLng(latitude_gps, longitude_gps));//TODO ---- 高德经纬度集合
                    goolePoints.add(new LatLng(latitude_gps, longitude_gps));//高德经纬度集合  ;//TODO ----   接收运动服务发送的广播数据

                }
            } else if (action.equals(SportService.SEND_RECEIVER_TIME)) {// 时间   ------ TODO 通过SportService发送的广播更新运动时间值
                countSportTimeNum++;

                Bundle bundledata = intent.getExtras();
                if (bundledata != null) {
                    Long Count = bundledata.getLong("count");    // 时间变量
                    String FILE_NAME = bundledata.getString("file_name");
                    String startTime = bundledata.getString("startTime");   // 运动开始时间
                    satenum = bundledata.getInt("Satenum");//gps强度

                    if(Count%30 == 0 && !SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("3")){   // if(SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("3")){
                        upedateGps_signal(satenum);//更改gps强度
                    }

                    int totalSec = 0;
                    int yunshu = 0;
                    totalSec = (int) (Count / 60);  //根据总秒数--- 得到分钟的整数
                    yunshu = (int) (Count % 60);    //根据总秒数--- 除开分钟后剩下的秒数
                    int mai = totalSec / 60;   // 小时值
                    int sec = totalSec % 60;   // 分钟值
                    try {                                             //  mai, sec, yunshu  ----  对应 时，分，秒
                        home_time_tv.setText(String.format(Locale.ENGLISH,"%1$02d:%2$02d:%3$02d", mai, sec, yunshu));// TODO  ---- 设置运动时间赋值   Count
                        if(timegoal > 0){
                            if(Count >= timegoal*60){  // 运动时间目标为分钟 * 60 = 秒数
                                if(countSportTimeNum % 5 ==0){  // 5次提示一次
                                    if(goalNumSelect == 2){
                                        if(SharedPreUtil.readPre(OutdoorRunActitivy.this, SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_VOICE).equals(SharedPreUtil.YES)) {  // 振动开关打开了
                                            showDialog(getString(R.string.runTimeTarget));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (action.equals(SportService.SEND_RECEIVER_NETWORK)) {//网络判断
                Toast.makeText(mContext, "请打开GPS，否则无法定位...", Toast.LENGTH_SHORT).show();
            } else if (action.equals(SportService.SEND_RECEIVER_AUTO_PAUSE)) {//运动自动暂停
//                Toast.makeText(mContext, "运动已停止", Toast.LENGTH_SHORT).show();
                runstate = OUTDOORRUN_SUSPEND;
                Log.e(TAG, "runstate = " + runstate);
                run_stop_tv.setVisibility(View.VISIBLE);
                activity_outdoorrun_core_ll.setVisibility(View.VISIBLE);
                outdoorrun_suspend_ib.setVisibility(View.GONE);

            } else if (action.equals(SportService.SEND_RECEIVER_AUTO_START)) {//运动自动启动
//                Toast.makeText(mContext, "运动已启动", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                runstate = OUTDOORRUNNING;
                Log.e(TAG, "runstate = " + runstate);
                run_stop_tv.setVisibility(View.GONE);
                activity_outdoorrun_core_ll.setVisibility(View.GONE);
                outdoorrun_suspend_ib.setVisibility(View.VISIBLE);
            } else if (action.equals(SportService.SEND_RECEIVER_AUTO_PFINISH)) {  // 自动停止
                if(SharedPreUtil.readPre(OutdoorRunActitivy.this, SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_VOICE).equals(SharedPreUtil.YES)) {  // 振动开关打开了
                    //自动停止时， 振动500ms一次，间隔1s后，再振动500ms一次
                    long [] pattern = {0,500,1000,500};   // 停止 开启 停止 开启
                    Vibrator vib = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(pattern, -1);
                }
                finishSprot();
            }
        }
    };


    private void init() {
        run_stop_tv = (TextView) findViewById(R.id.run_stop_tv);
        activity_outdoorrun_core_ll = (LinearLayout) findViewById(R.id.activity_outdoorrun_core_ll);
        outdoorrun_start_ib = (ImageButton) findViewById(R.id.outdoorrun_start_ib);  // 开始运动按钮
        outdoorrun_suspend_ib = (ImageButton) findViewById(R.id.outdoorrun_suspend_ib);
        outdoorrun_finish_ib = (ImageButton) findViewById(R.id.outdoorrun_finish_ib);  // 结束运动按钮
        outdoorrun_lockscreen_ib = (ImageButton) findViewById(R.id.outdoorrun_lockscreen_ib);
        outdoorrun_motionsettting_ib = (ImageButton) findViewById(R.id.outdoorrun_motionsettting_ib);
        outdoorrun_map = (ImageButton) findViewById(R.id.outdoorrun_map);
        ib_sporthistory_photo = (ImageView) findViewById(R.id.ib_sporthistory_photo);

        home_time_tv = (TextView) findViewById(R.id.home_time_tv);//时间
        realtime_peisu = (TextView) findViewById(R.id.home_pace_tv);//配速
        realtime_gps = (ImageView) findViewById(R.id.realtime_gps);//gps信号强度
        home_altitude_tv = (TextView) findViewById(R.id.home_altitude_tv);//海拔
        realtime_mile = (TextView) findViewById(R.id.home_distance_number);//距离
        realtime_qianka = (TextView) findViewById(R.id.home_kal_tv);//卡里路

        realtime_peisu_up = (TextView) findViewById(R.id.home_pace_text_tv_up);//配速单位
        home_altitude_tv_up = (TextView) findViewById(R.id.home_altitude_text_tv_up);//海拔单位
        realtime_mile_up = (TextView) findViewById(R.id.home_distance_number_up);//距离单位
        realtime_qianka_up = (TextView) findViewById(R.id.home_kal_text_tv_up);//卡里路单位

        outdoorrun_bottom_rl = (RelativeLayout) findViewById(R.id.outdoorrun_bottom_rl);
        slider_rl = (RelativeLayout) findViewById(R.id.slider_rl);

        slider = (SlideView) findViewById(R.id.slider);
        slider.setSlideListener(new SlideView.SlideListener() {
            @Override
            public void onDone() {
                Unlock();
            }
        });

        if(SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("3")){  // SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("5")
            outdoorrun_map.setVisibility(View.GONE);
        }

        sport_mode = (TextView) findViewById(R.id.sport_mode);
        String sport = SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE);
        int sportMode = 1;
        if (sport.equals("")) {
            sportMode = 1;
        } else {
            sportMode = Integer.valueOf(sport);
        }

        switch (sportMode) {
            case 1:
                sport_mode.setText(getResources().getString(R.string.sportshistory_jianzou));
                break;
            case 2:
                sport_mode.setText(getResources().getString(R.string.sportshistory_huwaipao));
                break;
            case 3:
                sport_mode.setText(getResources().getString(R.string.sportshistory_shineipao));
                break;
            case 4:
                sport_mode.setText(getResources().getString(R.string.sportshistory_dengshan));
                break;
            case 5:
                sport_mode.setText(getResources().getString(R.string.sportshistory_yueyepao));
                break;
            case 6:
                sport_mode.setText(getResources().getString(R.string.sportshistory_banma));
                break;
            case 7:
                sport_mode.setText(getResources().getString(R.string.sportshistory_quanma));
                break;
        }

        outdoorrun_start_ib.setOnClickListener(this);
        outdoorrun_suspend_ib.setOnClickListener(this);
        outdoorrun_finish_ib.setOnClickListener(this);
        outdoorrun_lockscreen_ib.setOnClickListener(this);
        outdoorrun_motionsettting_ib.setOnClickListener(this);
        outdoorrun_map.setOnClickListener(this);
        ib_sporthistory_photo.setOnClickListener(this);

        if(isMetric){
            realtime_mile_up.setText(getString(R.string.kilometer));
            realtime_peisu_up.setText(getString(R.string.realtime_minutes_km));
            realtime_qianka_up.setText(getString(R.string.realtime_calorie));
            home_altitude_tv_up.setText(getString(R.string.everyday_rice));
        }else{
            realtime_mile_up.setText(getString(R.string.unit_mi));
            realtime_peisu_up.setText(getString(R.string.unit_min_mi));
            realtime_qianka_up.setText(getString(R.string.unit_kj));
            home_altitude_tv_up.setText(getString(R.string.unit_ft));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode ==-1){
            Intent intent = new Intent();
            intent.setClass(OutdoorRunActitivy.this, NewWaterMakActivity.class);   // WaterMakActivity  99999999999999999999

//            Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
//            Double mile = Double.valueOf(Utils.decimalTo2(mile / 1000, 2) + "");    9999999999999

//            String distance = Utils.decimalTo2(mile / 1000, 2) + "";// 里程/千米

            String distance = realtime_mile.getText().toString();

            intent.putExtra("distance",distance);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.outdoorrun_suspend_ib:// ----- 暂停运动
                runsuspend();
                break;
            case R.id.outdoorrun_start_ib://重新启动
                runstart();   // 开始运动
                break;
            case R.id.outdoorrun_finish_ib://结束当前运动  ---- 结束运动
                runstop();
                break;
            case R.id.ib_sporthistory_photo://拍照分享
                Intent intentphoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                Uri uri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    uri = Uri.fromFile(new File(mFilePath));
                }else{
                    uri = TUriParse.getUriForFile(OutdoorRunActitivy.this, new File(mFilePath));
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                Uri uri = Uri.fromFile(new File(mFilePath));
                // 指定存储路径，这样就可以保存原图了
                intentphoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intentphoto, 1);
//                Toast.makeText(mContext,R.string.developed,Toast.LENGTH_SHORT).show();
                break;
            case R.id.outdoorrun_lockscreen_ib:  // 锁屏按钮
                lockscreen();
                break;
            case R.id.outdoorrun_motionsettting_ib:   // 设置按钮
                startActivity(new Intent(this, MotionSettingActivity.class));
                break;
            case R.id.outdoorrun_map://地图界面
                /**获取运动类型  1.健走 2.户外跑 3.登山跑 4.越野跑 5.室内跑 6.半马 7.全马**/
                if(NetWorkUtils.isNetConnected(BTNotificationApplication.getInstance())){
                    if(!SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("3")) {  //TODO --- 现在3位室内跑 !SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("5")
                        Intent intent = new Intent(this, RealTimeMapActivity.class);
                        intent.putExtra("googleLat", goolePoints);//  运动通过服务发送的广播数据
                        intent.putExtra("gdLat", gdPoints);   // todo ---- 传递 高德的经纬度到 实时地图页面
                        intent.putExtra("time", home_time_tv.getText().toString());//时间
                        if(isMetric) {
                            intent.putExtra("mile", Utils.decimalTo2(mile / 1000, 2) + "");//里程
                            if(runstate == 1){
                                intent.putExtra("peisu", realtime_peisu.getText().toString());//配速
                            }else{
                                intent.putExtra("peisu", Utils.decimalTo2(speed * 60 / 1000, 3) + "");//配速
                            }
                            intent.putExtra("calorie", (int)Math.round(calorie) + "");//卡里路
                        }else{
                            intent.putExtra("mile", Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");//里程
                            if(runstate == 1){
                                intent.putExtra("peisu", realtime_peisu.getText().toString());//配速
                            }else{
                                intent.putExtra("peisu", Utils.decimalTo2(Utils.getUnit_pace(speed * 60 / 1000), 3) + "");//配速
                            }
                            intent.putExtra("calorie", (int)Math.round(Utils.getUnit_kal(calorie)) + "");//卡里路
                        }
                      /*  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                        startActivity(intent);
                    }else{
                        Toast.makeText(mContext,getString(R.string.indoor_run),Toast.LENGTH_SHORT).show();  // 室内跑没有地图哦
                    }
                }else{
                    Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void lockscreen() {
        if (runstate == OUTDOORRUNNING) {
            outdoorrun_bottom_rl.setVisibility(View.GONE);
            slider_rl.setVisibility(View.VISIBLE);
        }
    }

    private void Unlock() {
        outdoorrun_bottom_rl.setVisibility(View.VISIBLE);
        slider_rl.setVisibility(View.GONE);
        slider.unlock();
    }

    private void runstart() {
//        startService(intentSportService);//重新启动服务
//        sportService.startSport();
        Intent intent = new Intent(SportService.CMD_RECEIVER);
        intent.putExtra("cmd", CMD_START_SPORTS);
        mContext.sendBroadcast(intent);     // 发送开始运动的广播

        runstate = OUTDOORRUNNING;

        Log.e(TAG, "runstate = " + runstate);
        run_stop_tv.setVisibility(View.GONE);
        activity_outdoorrun_core_ll.setVisibility(View.GONE);
        outdoorrun_suspend_ib.setVisibility(View.VISIBLE);  // 点击开始运动按钮---开始运动按钮变成暂停按钮
    }

    /**
     * 暂停运动
     */
    private void runsuspend() {
//        stopSportService();
//        sportService.psuseSport();
        Intent intent = new Intent(SportService.CMD_RECEIVER);
        intent.putExtra("cmd", CMD_PAUSE_SPORTS);
        mContext.sendBroadcast(intent);    // 发送暂停的广播

        runstate = OUTDOORRUN_SUSPEND;   //TODO----  暂停的状态
        Log.e(TAG, "runstate = " + runstate);
        run_stop_tv.setVisibility(View.VISIBLE);  // 停止运动的按钮
        activity_outdoorrun_core_ll.setVisibility(View.VISIBLE);  // 显示整个开始运动按钮 和 停止运动的按钮
        outdoorrun_suspend_ib.setVisibility(View.GONE);
    }

    /**
     * 结束运动
     */
    private void runstop() {
        if (mile < 20) {//如果总距离小于20M则不存入数据库
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt);
            builder.setMessage(R.string.endSaveRunData);
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
                            Intent intent = new Intent(SportService.CMD_RECEIVER);
                            intent.putExtra("cmd", CMD_FINISH_SERVICE);
                            mContext.sendBroadcast(intent);
                            runstate = OUTDOORRUN_STOP;
                            Log.e(TAG, "runstate = " + runstate);
//                            unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
                            finish();
                        }
                    });
            builder.create().show();
        } else {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt);
            builder.setMessage(R.string.saveRunData);
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
                            Intent intent = new Intent(SportService.CMD_RECEIVER);
                            intent.putExtra("cmd", CMD_STOP_SERVICE);
                            mContext.sendBroadcast(intent);
                            runstate = OUTDOORRUN_STOP;
                            Log.e(TAG, "runstate = " + runstate);
//                            unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
                            finish();
                        }
                    });
            builder.create().show();
        }
//        sportService.finshSport();
//        stopSportService();
    }


    /**
     * 自动结束运动
     */
    private void finishSprot() {
        if (mile < 20) {
            Intent intent = new Intent(SportService.CMD_RECEIVER);
            intent.putExtra("cmd", CMD_FINISH_SERVICE);   // todo ---- 结束运动，不保存数据
            mContext.sendBroadcast(intent);
            runstate = OUTDOORRUN_STOP;
            Log.e(TAG, "runstate = " + runstate);
//            unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
            finish();
            Toast.makeText(mContext, getString(R.string.autosave_sportdata_fail),Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(SportService.CMD_RECEIVER);
            intent.putExtra("cmd", CMD_STOP_SERVICE);   // todo --- 结束运动，保存数据
            mContext.sendBroadcast(intent);
            runstate = OUTDOORRUN_STOP;
            Log.e(TAG, "runstate = " + runstate);
//            unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
            finish();
            Toast.makeText(mContext, getString(R.string.autosave_sportdata_success), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("Map", "运动控制界面销毁......");
        super.onDestroy();

        unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void upedateGps_signal(int satenum) {  //  更改GPS信号
        // 提示打开GPS
        if (!Utils.isGpsEnabled((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE))) {
            satenum = 0;
        }
        switch (satenum) {
            case 0:
                realtime_gps.setBackgroundResource(R.drawable.gps1);
                Toast.makeText(this,getString(R.string.gps_sign_warn),Toast.LENGTH_SHORT).show();  // 为了数据记录的准确性,需要更好的GPS信号,否则数据可能不准确 \n\n小贴士:\n\n请在空旷的室外运动;\n设置里开启移动数据连接;\n还是不行?请重启手机。
                break;
            case 1:
            case 2:
            case 3:
                realtime_gps.setBackgroundResource(R.drawable.gps1);
                Toast.makeText(this,getString(R.string.gps_sign_warn),Toast.LENGTH_SHORT).show();
                break;

            case 4:
            case 5:
                realtime_gps.setBackgroundResource(R.drawable.gps2);
                break;
            case 6:
            case 7:
            case 8:
                realtime_gps.setBackgroundResource(R.drawable.gps3);
                break;
            default:
                realtime_gps.setBackgroundResource(R.drawable.gps3);
                break;
        }
    }

    //弹出警报框
    private void showDialog(final String content) {  //todo ---- 还需要考虑 重新设置 目标后，应该将  isShowAlertDialog 置为 false
        final android.app.AlertDialog myDialog;
        if(!isShowAlertDialog) {//是否显示过
            myDialog = new android.app.AlertDialog.Builder(OutdoorRunActitivy.this).create();
            myDialog.show();
            isShowAlertDialog = true;
            myDialog.getWindow().setContentView(R.layout.alert_fence_dialog);
            TextView tv_alert_content = (TextView) myDialog.getWindow().findViewById(R.id.tv_alert_content);
            tv_alert_content.setText(content);
            myDialog.setView(tv_alert_content);
            myDialog.setCancelable(false);
            ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
            myDialog.getWindow().setBackgroundDrawable(dw);
            myDialog.getWindow()
                    .findViewById(R.id.btn_fence_pop_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
            // 振动500ms一次，间隔1s后，再振动500ms一次
            long [] pattern = {0,500,1000,500};   // 停止 开启 停止 开启
            Vibrator vib = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(pattern, -1);
        }
    }
}
