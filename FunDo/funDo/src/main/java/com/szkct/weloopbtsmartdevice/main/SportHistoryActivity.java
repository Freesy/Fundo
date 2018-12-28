package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kct.fundo.btnotification.R;
import com.mtk.app.applist.FileUtils;
import com.szkct.map.TrajectoryMapFragment;
import com.szkct.takephoto.app.TakePhoto;
import com.szkct.takephoto.app.TakePhotoImpl;
import com.szkct.takephoto.model.CropOptions;
import com.szkct.takephoto.model.InvokeParam;
import com.szkct.takephoto.model.TContextWrap;
import com.szkct.takephoto.model.TResult;
import com.szkct.takephoto.permission.InvokeListener;
import com.szkct.takephoto.permission.PermissionManager;
import com.szkct.takephoto.permission.TakePhotoInvocationHandler;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.activity.NewWaterMakActivity;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.DetailedFragment;
import com.szkct.weloopbtsmartdevice.view.MotionChartFragment;
import com.szkct.weloopbtsmartdevice.view.SpeedFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;

//import cn.sharesdk.demo.wxapi.onekeyshare.OnekeyShare;

/**
 * Created by Kct on 2016/12/10.
 */
public class SportHistoryActivity extends FragmentActivity implements View.OnClickListener ,TakePhoto.TakeResultListener, InvokeListener {  // 运动模式 历史记录 详情页面    implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener

    private static final String LOG_TAG = "SportHistoryActivity";
    private FragmentManager fragmentManager;

    private TrajectoryMapFragment sport_trajectoryFragment;  // 运动轨迹页面
    private DetailedFragment detailed_dataFragment;   // 详细数据页面
    private SpeedFragment speed_matching_detailsFragment;  // 配速详情页面

    private MotionChartFragment motion_chartFragment;  // 运动图表页面
    // 记录文件保存位置
    private String mFilePath;
    private RadioButton rb_sport_trajectory, rb_detailed_data, rb_speed_matching_details, rb_motion_chart;
    TextView tv_sporthistory_title;
    private ImageView iv_sprothis_bottom1,iv_sprothis_bottom2,iv_sprothis_bottom3,iv_sprothis_bottom4,back;

    private ImageButton ib_sporthistory_share;

    private String watch;  // 设备型号
    private String tempWatchType;

    private static final int[] SECTION_STRINGS = {R.string.sportshistory_jianzou,
            R.string.sportshistory_huwaipao,
            R.string.sportshistory_shineipao,
            R.string.sportshistory_dengshan,
            R.string.sportshistory_yueyepao,
            R.string.sportshistory_banma,
            R.string.sportshistory_quanma,  // 之前7
            R.string.sportshistory_tiaosheng,
            R.string.sportshistory_yumaoqiu,
            R.string.sportshistory_lanqiu,
            R.string.sportshistory_qixing,
            R.string.sportshistory_huabing,
            R.string.sportshistory_jianshen,
            R.string.sportshistory_yujia,
            R.string.sportshistory_wangqiu,
            R.string.sportshistory_pingpang,
            R.string.sportshistory_zuqiu,
            R.string.sportshistory_youyong,
            R.string.sportshistory_xingai,
            R.string.sportshistory_fanhang,
    };
    private DBHelper db;
    private GpsPointDetailData gpsPoint;
    private List<GpsPointDetailData> gpsList = new ArrayList<GpsPointDetailData>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private boolean isRunning = false;

    private boolean isMetricInActivity;
    private List<Float> speedListInAc = new ArrayList<Float>();//
    private List<Float> altitudeListInAc = new ArrayList<Float>();//
    private List<Float> cadencelistInAc = new ArrayList<Float>();//
    private List<Float> heartlistInAc = new ArrayList<Float>();//

    private String picName = "";

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }



    @Override
    public void onClick(View view) {
//        iv_sprothis_bottom1.setVisibility(View.INVISIBLE);
//        iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
//        iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
//        iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
        switch (view.getId()) {
            case R.id.ib_sporthistory_photo:  // 拍照
//                Toast.makeText(getApplicationContext(),R.string.developed,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                    uri = Uri.fromFile(new File(mFilePath));
                }else{
                    uri = TUriParse.getUriForFile(SportHistoryActivity.this,new File(mFilePath));
                }

//                Uri uri = Uri.fromFile(new File(mFilePath));
                // 指定存储路径，这样就可以保存原图了
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
                break;
            case R.id.rb_sport_trajectory:    // 运动轨迹

                if(gpsPoint.getSportType().equals("3")) { // 室内跑
                    rb_sport_trajectory.setChecked(false);
                    rb_detailed_data.setChecked(false);
                    rb_speed_matching_details.setChecked(false);
                    rb_motion_chart.setChecked(false);
                    iv_sprothis_bottom1.setVisibility(View.GONE);
                    iv_sprothis_bottom2.setVisibility(View.VISIBLE);

                    iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
                    iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
                    return;
                }

                if (!sport_trajectoryFragment.isAdded() ) { // && null == fragmentManager.findFragmentByTag("sport_trajectoryFragment")
                    fragmentManager.beginTransaction().add(R.id.container, sport_trajectoryFragment, "sport_trajectoryFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions(); // todo --- add 20180813
                }
                fragmentManager.beginTransaction().show(sport_trajectoryFragment).hide(detailed_dataFragment)
                        .hide(speed_matching_detailsFragment).hide(motion_chartFragment).commitAllowingStateLoss();
                rb_sport_trajectory.setChecked(true);
                rb_detailed_data.setChecked(false);
                rb_speed_matching_details.setChecked(false);
                rb_motion_chart.setChecked(false);
                iv_sprothis_bottom1.setVisibility(View.VISIBLE);
                ib_sporthistory_share.setVisibility(View.VISIBLE);

                iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
                iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
                iv_sprothis_bottom4.setVisibility(View.INVISIBLE);

                if( (speedListInAc.size() <=0 || ((speedListInAc.size() ==1) && speedListInAc.get(0) == 0)) &&
                        (altitudeListInAc.size()<=0 || ((altitudeListInAc.size() ==1) && altitudeListInAc.get(0) == 0)) &&
                        (heartlistInAc.size() <= 0 || ((heartlistInAc.size() ==1) && heartlistInAc.get(0) == 0)) &&
                        (cadencelistInAc.size()<=0 || ((cadencelistInAc.size() ==1)))  // && cadencelistInAc.get(0) == 0
                        ){ // todo -- 都无数据  不显示 运动图表页面
                    rb_motion_chart.setVisibility(View.GONE);
                    iv_sprothis_bottom4.setVisibility(View.GONE);

                  /*  if(gpsPoint.getSportType().equals("3")){
                        rb_sport_trajectory.setVisibility(View.GONE);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.GONE);
                    }else {
                        rb_sport_trajectory.setChecked(false);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.INVISIBLE);
                    }*/
                }

                //todo  ---- 配速详情页面
//                String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
//                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
                if((watch.equals("3") && gpsPoint.getDeviceType().equals("2")) || (tempWatchType.equals("3") && gpsPoint.getDeviceType().equals("2"))) { //仅对于G703手表端数据 mtk    // 设备类型    2：手表   1： 手机
                    // todo --- 当为2503 设备时
                    rb_speed_matching_details.setVisibility(View.GONE);   // todo --- 隐藏配速详情 页面
                    iv_sprothis_bottom3.setVisibility(View.GONE);
                }

                break;
            case R.id.rb_detailed_data:   // 详细数据
                if (!detailed_dataFragment.isAdded()) {  //  && null == fragmentManager.findFragmentByTag("detailed_dataFragment")
                    fragmentManager.beginTransaction().add(R.id.container, detailed_dataFragment, "detailed_dataFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions(); // todo --- add 20180813
                }

                fragmentManager.beginTransaction().show(detailed_dataFragment).hide(sport_trajectoryFragment)
                        .hide(speed_matching_detailsFragment).hide(motion_chartFragment).commitAllowingStateLoss();

                if(gpsPoint.getSportType().equals("3")){
                    rb_sport_trajectory.setVisibility(View.GONE);
                    rb_detailed_data.setChecked(true);
                    rb_speed_matching_details.setChecked(false);
                    rb_motion_chart.setChecked(false);
                    iv_sprothis_bottom2.setVisibility(View.VISIBLE);
                    iv_sprothis_bottom1.setVisibility(View.GONE);

                    iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
                    iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
                }else {
                    rb_sport_trajectory.setChecked(false);
                    rb_detailed_data.setChecked(true);
                    rb_speed_matching_details.setChecked(false);
                    rb_motion_chart.setChecked(false);
                    iv_sprothis_bottom2.setVisibility(View.VISIBLE);
                    iv_sprothis_bottom1.setVisibility(View.INVISIBLE);

                    iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
                    iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
                }

                if( (speedListInAc.size() <=0 || ((speedListInAc.size() ==1) && speedListInAc.get(0) == 0)) &&
                        (altitudeListInAc.size()<=0 || ((altitudeListInAc.size() ==1) && altitudeListInAc.get(0) == 0)) &&
                        (heartlistInAc.size() <= 0 || ((heartlistInAc.size() ==1) && heartlistInAc.get(0) == 0)) &&
                        (cadencelistInAc.size()<=0 || ((cadencelistInAc.size() ==1)))  // && cadencelistInAc.get(0) == 0
                        ){ // todo -- 都无数据  不显示 运动图表页面
                    rb_motion_chart.setVisibility(View.GONE);
                    iv_sprothis_bottom4.setVisibility(View.GONE);

                  /*  if(gpsPoint.getSportType().equals("3")){
                        rb_sport_trajectory.setVisibility(View.GONE);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.GONE);
                    }else {
                        rb_sport_trajectory.setChecked(false);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.INVISIBLE);
                    }*/
                }

                //todo  ---- 配速详情页面
//                String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
//                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
                if((watch.equals("3") && gpsPoint.getDeviceType().equals("2")) || (tempWatchType.equals("3") && gpsPoint.getDeviceType().equals("2"))) { //仅对于G703手表端数据 mtk    // 设备类型    2：手表   1： 手机
                    // todo --- 当为2503 设备时
                    rb_speed_matching_details.setVisibility(View.GONE);   // todo --- 隐藏配速详情 页面
                    iv_sprothis_bottom3.setVisibility(View.GONE);
                }

                break;
            case R.id.rb_speed_matching_details:   // 配速详情
                if (!speed_matching_detailsFragment.isAdded()) { //  && null == fragmentManager.findFragmentByTag("speed_matching_detailsFragment")
                    fragmentManager.beginTransaction().add(R.id.container, speed_matching_detailsFragment, "speed_matching_detailsFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions(); // todo --- add 20180813
                }

                fragmentManager.beginTransaction().show(speed_matching_detailsFragment).hide(sport_trajectoryFragment)
                        .hide(detailed_dataFragment).hide(motion_chartFragment).commitAllowingStateLoss();

                if(gpsPoint.getSportType().equals("3")){
                    rb_sport_trajectory.setVisibility(View.GONE);
                    rb_detailed_data.setChecked(false);
                    rb_speed_matching_details.setChecked(true);
                    rb_motion_chart.setChecked(false);
                    iv_sprothis_bottom3.setVisibility(View.VISIBLE);
                    iv_sprothis_bottom1.setVisibility(View.GONE);

                    iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
                    iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
                }else {
                    rb_sport_trajectory.setChecked(false);
                    rb_detailed_data.setChecked(false);
                    rb_speed_matching_details.setChecked(true);
                    rb_motion_chart.setChecked(false);
                    iv_sprothis_bottom3.setVisibility(View.VISIBLE);
                    iv_sprothis_bottom1.setVisibility(View.INVISIBLE);

                    iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
                    iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
                }

                if( (speedListInAc.size() <=0 || ((speedListInAc.size() ==1) && speedListInAc.get(0) == 0)) &&
                        (altitudeListInAc.size()<=0 || ((altitudeListInAc.size() ==1) && altitudeListInAc.get(0) == 0)) &&
                        (heartlistInAc.size() <= 0 || ((heartlistInAc.size() ==1) && heartlistInAc.get(0) == 0)) &&
                        (cadencelistInAc.size()<=0 || ((cadencelistInAc.size() ==1)))  // && cadencelistInAc.get(0) == 0
                        ){ // todo -- 都无数据  不显示 运动图表页面
                    rb_motion_chart.setVisibility(View.GONE);
                    iv_sprothis_bottom4.setVisibility(View.GONE);

                  /*  if(gpsPoint.getSportType().equals("3")){
                        rb_sport_trajectory.setVisibility(View.GONE);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.GONE);
                    }else {
                        rb_sport_trajectory.setChecked(false);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.INVISIBLE);
                    }*/
                }

                //todo  ---- 配速详情页面
//                String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
//                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
                if((watch.equals("3") && gpsPoint.getDeviceType().equals("2")) || (tempWatchType.equals("3") && gpsPoint.getDeviceType().equals("2"))) { //仅对于G703手表端数据 mtk    // 设备类型    2：手表   1： 手机
                    // todo --- 当为2503 设备时
                    rb_speed_matching_details.setVisibility(View.GONE);   // todo --- 隐藏配速详情 页面
                    iv_sprothis_bottom3.setVisibility(View.GONE);
                }

                break;
            case R.id.rb_motion_chart:   //显示 运动图表

                if (!motion_chartFragment.isAdded() ) {  // && null == fragmentManager.findFragmentByTag("motion_chartFragment")
                    fragmentManager.beginTransaction().add(R.id.container, motion_chartFragment, "motion_chartFragment").commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions(); // todo --- add 20180813
                }

                fragmentManager.beginTransaction().show(motion_chartFragment).hide(sport_trajectoryFragment)
                        .hide(speed_matching_detailsFragment).hide(detailed_dataFragment).commitAllowingStateLoss();


                if( (speedListInAc.size() <=0 || ((speedListInAc.size() ==1) && speedListInAc.get(0) == 0)) &&
                        (altitudeListInAc.size()<=0 || ((altitudeListInAc.size() ==1) && altitudeListInAc.get(0) == 0)) &&
                        (heartlistInAc.size() <= 0 || ((heartlistInAc.size() ==1) && heartlistInAc.get(0) == 0)) &&
                        (cadencelistInAc.size()<=0 || ((cadencelistInAc.size() ==1)))  // && cadencelistInAc.get(0) == 0
                        ){ // todo -- 都无数据  不显示 运动图表页面
                    rb_motion_chart.setVisibility(View.GONE);
                    iv_sprothis_bottom4.setVisibility(View.GONE);

                  /*  if(gpsPoint.getSportType().equals("3")){
                        rb_sport_trajectory.setVisibility(View.GONE);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.GONE);
                    }else {
                        rb_sport_trajectory.setChecked(false);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);
//                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.INVISIBLE);
                    }*/
                }else {
                    if(gpsPoint.getSportType().equals("3")){
                        rb_sport_trajectory.setVisibility(View.GONE);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);

                        rb_motion_chart.setVisibility(View.VISIBLE);

                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.GONE);

                        iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
                        iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
                    }else {
                        rb_sport_trajectory.setChecked(false);
                        rb_detailed_data.setChecked(false);
                        rb_speed_matching_details.setChecked(false);
                        rb_motion_chart.setChecked(true);

                        rb_motion_chart.setVisibility(View.VISIBLE);

                        iv_sprothis_bottom4.setVisibility(View.VISIBLE);
                        iv_sprothis_bottom1.setVisibility(View.INVISIBLE);

                        iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
                        iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
                    }
                }

                //todo  ---- 配速详情页面
//                String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
//                String tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
                if((watch.equals("3") && gpsPoint.getDeviceType().equals("2")) || (tempWatchType.equals("3") && gpsPoint.getDeviceType().equals("2"))) { //仅对于G703手表端数据 mtk    // 设备类型    2：手表   1： 手机
                    // todo --- 当为2503 设备时
                    rb_speed_matching_details.setVisibility(View.GONE);   // todo --- 隐藏配速详情 页面
                    iv_sprothis_bottom3.setVisibility(View.GONE);
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.ib_sporthistory_share:     // 点击分享
                if (isRunning) {
                    return;
                }
                isRunning = true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
//                        showShare();
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        if (rb_sport_trajectory.isChecked()) {  //运动轨迹页面分享
                            if (!NetWorkUtils.isConnect(SportHistoryActivity.this)) {
                                Toast.makeText(SportHistoryActivity.this, getString(R.string.my_network_disconnected), Toast.LENGTH_SHORT).show();
                            } else {
//                                if (Utils.isFastClick()) {
//                                sendBroadcast(new Intent(TrajectoryMapFragment.SEND_RECEIVER_SCREEN));
                                    if (OnekeyShare.isShowShare) { // todo ---- 弹出分享框了
                                        OnekeyShare.isShowShare = false;
                                        showShare(MainService.PAGE_INDEX_SPORTMODE);
                                    }
//                                }
                            }
                        }else if(rb_detailed_data.isChecked()){ //详情数据页面分享
                            if (!NetWorkUtils.isConnect(SportHistoryActivity.this)) {
                                Toast.makeText(SportHistoryActivity.this, getString(R.string.my_network_disconnected), Toast.LENGTH_SHORT).show();
                            } else {
//                                if( Utils.isFastClick()) {
                                    if(OnekeyShare.isShowShare){ // todo ---- 弹出分享框了
                                        OnekeyShare.isShowShare = false;
                                        showShareNotmap(MainService.PAGE_INDEX_SPORT_DETAILED_DATA);// todo  --- 运动模式详细数据页面
                                    }
//                                }
                            }
                        }else if(rb_speed_matching_details.isChecked()){ //配速详情页面分享
                            if (!NetWorkUtils.isConnect(SportHistoryActivity.this)) {
                                Toast.makeText(SportHistoryActivity.this, getString(R.string.my_network_disconnected), Toast.LENGTH_SHORT).show();
                            } else {
//                                if( Utils.isFastClick()) {
                                    if(OnekeyShare.isShowShare){ // todo ---- 弹出分享框了
                                        OnekeyShare.isShowShare = false;
                                        showShareNotmap(MainService.PAGE_INDEX_SPORT_SPEED_DETAILS);// todo  --- 运动模式配速详情页面
                                    }
//                                }
                            }
                        }else if(rb_motion_chart.isChecked()){ //运动图表页面分享
                            if (!NetWorkUtils.isConnect(SportHistoryActivity.this)) {
                                Toast.makeText(SportHistoryActivity.this, getString(R.string.my_network_disconnected), Toast.LENGTH_SHORT).show();
                            } else {
//                                if( Utils.isFastClick()) {
                                    if(OnekeyShare.isShowShare){ // todo ---- 弹出分享框了
                                        OnekeyShare.isShowShare = false;
                                        showShareNotmap(MainService.PAGE_INDEX_SPORT_MOTION_CHART);// todo  --- 运动模式运动图表页面
                                    }
//                                }
                            }
                        }
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                        isRunning = false;
                    }

                }, 1400);

                if (rb_sport_trajectory.isChecked()) {  //运动轨迹页面分享
                    sendBroadcast(new Intent(TrajectoryMapFragment.SEND_RECEIVER_SCREEN));
                }

                break;
        }
    }

    private void showShareNotmap(int pageIndex) {
        if(pageIndex == MainService.PAGE_INDEX_SPORT_DETAILED_DATA){   // 运动模式详细数据页面
            ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, DetailedFragment.detailfragment_sc), filePath, fileName);   // 滚动分享OK
        }else if(pageIndex == MainService.PAGE_INDEX_SPORT_SPEED_DETAILS){   // 运动模式配速详情页面
            ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, SpeedFragment.speedfragment_sc), filePath, fileName);   // 滚动分享OK
        }else if(pageIndex == MainService.PAGE_INDEX_SPORT_MOTION_CHART){   // 运动模式运动图表页面
            ScreenshotsShare.savePicture(ScreenshotsShare.getViewBitmap(this, MotionChartFragment.motionchart_sc), filePath, fileName);   // 滚动分享OK
        }

        //ShareSDK.initSDK(this);
        mapPackageName = setImage(this);
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
        if (android.os.Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        int id =getIntent().getIntExtra("id",0);
//
//        if (db == null) {
//            db = DBHelper.getInstance(getApplicationContext());
//        }
//        Query query = null;
//        query = db.getGpsPointDetailDao().queryBuilder()
//                .orderAsc(GpsPointDetailDao.Properties.TimeMillis)
//                .build();
//        gpsList = (List<GpsPointDetailData>) query.list();
//        gpsPoint = gpsList.get(id);
        // 获取SD卡路径
        mFilePath = Environment.getExternalStorageDirectory().getPath();
        // 文件名
        mFilePath = mFilePath + "/" + "photo.png";
        gpsPoint = (GpsPointDetailData) getIntent().getSerializableExtra("Vo");
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        isMetricInActivity = SharedPreUtil.YES.equals(SharedPreUtil.getParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        String totalPs = gpsPoint.getArrspeed().replace("Infinity","");         // TODO ---- 速度值 GpsPointDetailData
        Log.e("rq", "totalPs==" + totalPs);
//        if(!StringUtils.isEmpty(totalPs) && totalPs.contains("&")){
        String[] arrPs = totalPs.split("&");
        if(arrPs != null && arrPs.length !=0){
            int psSize = arrPs.length;
            for (int i = 0; i < psSize; i++) {
                Float psValue = Utils.tofloat(arrPs[i]);
                if(!isMetricInActivity){
                    psValue = (float)Utils.getUnit_mile(psValue);
                }
                speedListInAc.add(psValue);
            }
        }
        //###########################
        String totalal= gpsPoint.getArraltitude();  // 海拔值   地图得到的海拔值 260.15902099774047&256.7814860623313&258.2029664825295&263.0911206371336&237.06402266995303&
        Log.e("rq","totalal=="+totalal);
        String[] arral = totalal.split("&");
        if(arral!=null&&arral.length!=0){
            int caSize = arral.length;
            for (int i = 0; i < caSize; i++) {
                Float psValue = Utils.tofloat(arral[i]);
                if(!isMetricInActivity){
                    psValue = (float)Utils.getUnit_mile(psValue);
                }
                altitudeListInAc.add(psValue);     // TODO --- 海拔数组
            }
        }
        //###########################
        String totalht= gpsPoint.getArrheartRate();  //todo  ---  心率值
        Log.e("rq","totalht=="+totalht);
        String[] arrht = totalht.split("&");
        Log.e("rq",arrht.toString()+"=="+arrht.length);
        if(arrht!=null && arrht.length!=0){
            int caSize = arrht.length;
            for (int i = 0; i < caSize; i++) {
                Float psValue = Utils.tofloat(arrht[i]);
                heartlistInAc.add(psValue);
            }
        }
        //###########################
        String totalca= gpsPoint.getArrcadence();   // TODO --- 步频值
        Log.e("rq","totalca=="+totalca);
        String[] arrca = totalca.split("&");
        if(arrca!=null&&arrca.length!=0){
            int caSize = arrca.length;
            for (int i = 0; i < caSize; i++) {
                Float psValue = Utils.tofloat(arrca[i]);
                cadencelistInAc.add(psValue);
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        initView();
        initRadioButton();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        OnekeyShare.isShowShare = true;   // todo --- 页面初始化时

        getTakePhoto().onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void initRadioButton() {
        rb_sport_trajectory = (RadioButton) findViewById(R.id.rb_sport_trajectory);
        rb_detailed_data = (RadioButton) findViewById(R.id.rb_detailed_data);
        rb_speed_matching_details = (RadioButton) findViewById(R.id.rb_speed_matching_details);
        rb_motion_chart = (RadioButton) findViewById(R.id.rb_motion_chart);
        rb_sport_trajectory.setOnClickListener(this);
        rb_detailed_data.setOnClickListener(this);
        rb_speed_matching_details.setOnClickListener(this);
        rb_motion_chart.setOnClickListener(this);  // 运动图表
        tv_sporthistory_title = (TextView) findViewById(R.id.tv_sporthistory_title);
        int type = Utils.toint(gpsPoint.getSportType()) - 1;   // TODO ----  运动类型

        if(type < 0){
            type = 0;
        }

        String languageLx  = Utils.getLanguage();
        if (!languageLx.equals("en") || !languageLx.equals("zh")) {
            if(languageLx.equals("th")){
                rb_sport_trajectory.setTextSize(8);
                rb_detailed_data.setTextSize(8);
                rb_speed_matching_details.setTextSize(8);
                rb_motion_chart.setTextSize(8);
            }
            tv_sporthistory_title.setTextSize(12);
        }
//        else if(languageLx.equals("th")){
//            rb_sport_trajectory.setTextSize(8);
//            rb_detailed_data.setTextSize(8);
//            rb_speed_matching_details.setTextSize(8);
//            rb_motion_chart.setTextSize(8);
//        }

        tv_sporthistory_title.setText(SECTION_STRINGS[type]);  // TODO  ---- 给标题设置运动类型

        iv_sprothis_bottom1= (ImageView) findViewById(R.id.iv_sprothis_bottom1);
        iv_sprothis_bottom2= (ImageView) findViewById(R.id.iv_sprothis_bottom2);
        iv_sprothis_bottom3= (ImageView) findViewById(R.id.iv_sprothis_bottom3);
        iv_sprothis_bottom4= (ImageView) findViewById(R.id.iv_sprothis_bottom4);

        if(gpsPoint.getSportType().equals("3")){ // 室内跑
            rb_sport_trajectory.setChecked(false);
            rb_detailed_data.setChecked(true);
            rb_sport_trajectory.setVisibility(View.GONE);
            iv_sprothis_bottom1.setVisibility(View.GONE);
            iv_sprothis_bottom2.setVisibility(View.VISIBLE);

            iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
            iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
        }else {
            rb_sport_trajectory.setChecked(true);
            rb_sport_trajectory.setVisibility(View.VISIBLE);
            iv_sprothis_bottom1.setVisibility(View.VISIBLE);

            iv_sprothis_bottom2.setVisibility(View.INVISIBLE);
            iv_sprothis_bottom3.setVisibility(View.INVISIBLE);
            iv_sprothis_bottom4.setVisibility(View.INVISIBLE);
        }

        //todo  ---- 运动图表 页面
        if( (speedListInAc.size() <=0 || ((speedListInAc.size() ==1) && speedListInAc.get(0) == 0)) &&
                (altitudeListInAc.size()<=0 || ((altitudeListInAc.size() ==1) && altitudeListInAc.get(0) == 0)) &&
                (heartlistInAc.size() <= 0 || ((heartlistInAc.size() ==1) && heartlistInAc.get(0) == 0)) &&
                (cadencelistInAc.size()<=0 || ((cadencelistInAc.size() ==1)))  // && cadencelistInAc.get(0) == 0
                ){ // todo -- 都无数据  不显示 运动图表页面
            rb_motion_chart.setVisibility(View.GONE);
            iv_sprothis_bottom4.setVisibility(View.GONE);
        }else{
//            rb_motion_chart.setVisibility(View.VISIBLE);
//            iv_sprothis_bottom4.setVisibility(View.VISIBLE);
        }

        //todo  ---- 配速详情页面
        watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);  // 设备型号
        tempWatchType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH);
        if((watch.equals("3") && gpsPoint.getDeviceType().equals("2")) || (tempWatchType.equals("3") && gpsPoint.getDeviceType().equals("2"))) { //仅对于G703手表端数据 mtk    // 设备类型    2：手表   1： 手机
            // todo --- 当为2503 设备时
//            barValues = new ArrayList<Float>();

            rb_speed_matching_details.setVisibility(View.GONE);   // todo --- 隐藏配速详情 页面
            iv_sprothis_bottom3.setVisibility(View.GONE);
        }

        back= (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

//        findViewById(R.id.ib_sporthistory_share).setOnClickListener(this);
        findViewById(R.id.ib_sporthistory_photo).setOnClickListener(this);  //TODO  拍照

        ib_sporthistory_share= (ImageButton) findViewById(R.id.ib_sporthistory_share);
        ib_sporthistory_share.setOnClickListener(this);
    }

    private void initView() {
        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_sport_history);
        fragmentManager=getSupportFragmentManager();   // 该Activity下面配置了4个Fragment
        sport_trajectoryFragment= TrajectoryMapFragment.newInstance();  // 运动轨迹
        detailed_dataFragment=DetailedFragment.newInstance();           //  详细数据
        speed_matching_detailsFragment=SpeedFragment.newInstance();   // 配速详情
        motion_chartFragment=MotionChartFragment.newInstance();         // 运动图表

        if(gpsPoint.getSportType().equals("3")) { // 室内跑
            fragmentManager.beginTransaction().add(R.id.container, detailed_dataFragment, "detailed_dataFragment").commitAllowingStateLoss();
        }else {
            fragmentManager.beginTransaction().add(R.id.container, sport_trajectoryFragment, "sport_trajectoryFragment").commitAllowingStateLoss();
        }
//        fragmentManager.beginTransaction().add(R.id.container, sport_trajectoryFragment, "sport_trajectoryFragment").commitAllowingStateLoss();
    }

    private String filePath = Environment.getExternalStorageDirectory()
            + "/appmanager/fundoShare/";
    private String fileName = "screenshot_analysis.png";  // screenshot_analysis.png
    private String detailPath = filePath + File.separator + fileName;

    private void showShare(int pageIndex) {
       // ShareSDK.initSDK(this);
        mapPackageName = setImage(this);
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
        oks.setImagePath(detailPath);// 确保SDcard下面存在此张图片   // /storage/emulated/0/appmanager/fundoShare//screenshot_analysis.png
        // url仅在微信（包括好友和朋友圈）中使用
        // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(getString(R.string.welcome_funrun));
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        if (Build.VERSION.SDK_INT < 21) {
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(this.getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        } else {
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
            oks.setCustomerLogo(drawableToBitmap(this.getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
        }

        // 启动分享GUI
        oks.show(this);
    }

    View.OnClickListener facebookclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToFacebook();
        }
    };
    View.OnClickListener Instagramclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareToInstagram();
        }
    };
    View.OnClickListener twitterclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTotwitter();
        }
    };
    View.OnClickListener whatsappclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareTowhatsapp();
        }
    };
    View.OnClickListener Linkedinclick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            shareToLinkedin();
        }
    };
    View.OnClickListener mobileqqclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            shareTomobileqq();
            Utils.onClickShareToQQ(SportHistoryActivity.this, detailPath);
        }
    };
    View.OnClickListener stravaclick = new View.OnClickListener() {
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
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_facebook_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 分享至Instagram
     */
    public void shareToInstagram() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_INSTAGRAM_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_instagram_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Twitter
     */
    public void shareTotwitter() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TWITTER_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_twitter_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至whatsapp
     */
    public void shareTowhatsapp() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_WHATSAPP);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至Linkedin
     */
    public void shareToLinkedin() {

        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_LINKEDIN_ANDROID);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_linkedin_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至strava
     */
    public void shareToStrava() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_STRAVA);
            if (packageName != null) {
                PackageManager pm = this.getPackageManager();
                boolean isAdd = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE",packageName));
                if(!isAdd){
                    Toast.makeText(this, getString(R.string.strava_need_open_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_strava_in_your_phone), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享至mobileqq
     */
    public void shareTomobileqq() {
        if (mapPackageName != null) {
            String packageName = mapPackageName.get(COM_TENCENT_MOBILEQQ);
            if (packageName != null) {
                actionShare_sms_email_facebook(packageName, this, "");
            } else {
                Toast.makeText(this, getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
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

    public static final String COM_TENCENT_MOBILEQQ = "com.tencent.mobileqq";
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
            if (packageName.startsWith(COM_STRAVA)){
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
            if (packageName.startsWith(COM_STRAVA)){
                WECHAT_FACEBOOK.add(resolveInfo);
            }
        }

        return WECHAT_FACEBOOK;
    }

    public void actionShare_sms_email_facebook(String packageName, Activity activity, String shareText) {

//		String fileName = "rideSummary_" + datetime_start + ".png";
//		File file = new File("/storage/emulated/0/DCIM/Camera/1411099620786.jpg");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png"); //   intent.setType("image/jpeg");

        intent.putExtra(Intent.EXTRA_SUBJECT, "SchwinnCycleNav Ride Share");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        File file = new File(detailPath);

//        Uri ddd =  Uri.fromFile(file);

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
        activity.startActivity(Intent.createChooser(intent, getString(R.string.app_name)));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode ==-1){
                Intent intent = new Intent();
                intent.setClass(SportHistoryActivity.this, NewWaterMakActivity.class);   // WaterMakActivity   99999999999999999999999999

                Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
                String distance = Utils.decimalTo2(mile / 1000, 2) + "";// 里程/千米

                intent.putExtra("distance",distance);
                SportHistoryActivity.this.startActivity(intent);
            }
    }
	
	  @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_NORMAL);  // 页面销毁时，将地图模式模式置为普通模式
    }

    private Uri getPhotoUri() {
        String sdcardState = Environment.getExternalStorageState();
        String sdcardPathDir = FileUtils.SDPATH;

        SimpleDateFormat sDateFormat = Utils.setSimpleDateFormat("yyyyMMddhhmmss");
        picName = sDateFormat.format(new java.util.Date());

        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            File fileDir = new File(sdcardPathDir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            file = new File(sdcardPathDir + picName + ".JPEG");
        }
        return Uri.fromFile(file);
    }

    @NonNull
    private CropOptions getBuilder() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(400).setOutputY(400);
        builder.setAspectX(400).setAspectY(400);
        builder.setWithOwnCrop(false);
        return builder.create() ;
    }

    @Override
    public void takeSuccess(TResult result) {
//        Logg.e(TAG, "takeSuccess: " + result + "  :" + result.getImages().size());
        Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(result.getImage().getOriginalPath());

//        iv_my_headphoto.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));

//        FileUtils.saveBitmap(bitmap, picName);
//        SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.FACE, picName + ".JPEG");
//        setheadnamebroadcast();
    }

    @Override
    public void takeFail(TResult result, String msg) {
//        Logg.e(TAG, "takeFail: " + result + "  msg=" + msg);
    }

    @Override
    public void takeCancel() {
//        Logg.e(TAG, "takeCancel: ");
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
//                Log.e("EverydayDataActivity", "点击了fanhui按钮");
                finish();
                break;

            default:
                break;
        }
        return false;
    }

}
