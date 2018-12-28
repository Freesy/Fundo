package com.szkct.map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kct.fundo.btnotification.R;
import com.szkct.GPSCorrect;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.map.bean.GetPoint;
import com.szkct.map.shared.StatusShared;
import com.szkct.map.utils.PositionUtil;
import com.szkct.map.utils.ScreenShotHelper;
import com.szkct.map.utils.Util;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.amap.api.maps.AMap.LOCATION_TYPE_LOCATE;
import static com.szkct.weloopbtsmartdevice.util.Utils.dateInversion;


public class TrajectoryMapFragment extends Fragment implements View.OnClickListener, AMap.OnMapScreenShotListener, GoogleMap.SnapshotReadyCallback {
    private MapView mGoogleMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleApiClient client;
    private GoogleMap mGoogleMap;
    private Boolean isFirst = true;//是否第一次
    public static final String SEND_RECEIVER_SCREEN = "com.szkct.map.SCREEN";// 截图广播
    List<LatLng> latlngList = new ArrayList<LatLng>();   //todo --- Google经度，纬度的集合
    List<LatLng> latlngKmList = new ArrayList<LatLng>(); // todo --- google 每千米的轨迹
    List<com.amap.api.maps.model.LatLng> gdLatlngList = new ArrayList<com.amap.api.maps.model.LatLng>();  //todo --- 高德经度，纬度的集合
    List<com.amap.api.maps.model.LatLng> gdLatlngKmList = new ArrayList<com.amap.api.maps.model.LatLng>();     //todo --- 高德经度，纬度每千米的集合


    List<Integer> mKmList = new ArrayList<Integer>();     //todo ---每千米刻度值的集合

    com.amap.api.maps.model.Marker mGDMarker = null;
    private List<com.amap.api.maps.model.Marker> gdMarkerList = new ArrayList<com.amap.api.maps.model.Marker>();
    private List<Marker> mGoogleMarkerList = new ArrayList<Marker>();
    private TextView traject_date;
    private TextView traject_mile,tv_ydgjtitle;
    private TextView traject_peisu;
    private TextView traject_pingjun;
    private TextView traject_time;
    private TextView traject_zuijia;
    private TextView traject_mile_up;  //距离单位
    private TextView traject_qianka_up;  //卡路里单位
    private TextView traject_peisu_up;  //配速单位
    private TextView shichang_danwei; // 时长单位
    private LinearLayout mScreemShotView;
    private TextView traject_zuikuai, traject_zuiman;
    //    private List<GpsPoint> gpsList = new ArrayList<GpsPoint>();
    private List<Double> gpsLat = new ArrayList<Double>();//维度集合
    private List<Double> gpsLon = new ArrayList<Double>();//经度集合
    private List<Double> gpsKmLat = new ArrayList<Double>();//Km维度集合
    private List<Double> gpsKmLon = new ArrayList<Double>();//Km经度集合
    private List<Double> psList = new ArrayList<Double>();//配速集合
    private TextView traject_qianka;
    private ImageView traject_kejian;
    private ImageView traject_dtms;
    private ImageView traject_qianmi;
    private ImageView sportmode_logo;
    private int mapMode = 0;//TODO  ---- 常规模式  卫星模式
    private boolean isKm = true;
    private boolean isVisible = true;
    private Marker mOriginStartMarker;
    private com.google.android.gms.maps.model.Polyline mpolyline;
    private com.amap.api.maps.model.Polyline mGDpolyline;
    private Marker mOriginEndMarker;
    private int mapType = 0;//TODO  ----- 1为google 0为高德
    private com.amap.api.maps.MapView mGdMapView;
    private AMap mGdMap = null;
    private Marker mGoogleMarker;

    private GpsPointDetailData gpsPoint;  // 关键数据

    private String maxPs = "";       // 手机最快
    private String minPs = "";      // 手机最慢
    private String phonePjPs;   //手机平均配速
    private View mView;
    private int[] colorList = {0xFFFBE01C, 0xFFE1E618, 0xFF7DFF00, 0xffDE2C00};// 颜色值
    private FragmentActivity mContext;
    private Circle mGDcircle;
    private com.google.android.gms.maps.model.Circle mGoogleCircle;
    private ViewGroup mViewGroupContainer;
    private RelativeLayout mViewRl;
    private List<Integer> watchPsList = new ArrayList<Integer>();//手表配速集合
    private Integer maxPsInt = 0;  //手表 最快配速
    private Integer minPsInt = 0;  //手表 最慢配速
    private Integer watchPjPs = 0;  //手表 平均配速
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private MyGoogleLocationListener myGooleLocationListener;
    // http://www.it1352.com/110843.html //google地图截屏

    private com.amap.api.maps.LocationSource.OnLocationChangedListener mGDListener;

    private boolean isMetric;

   // private TextView shichang_danwei; // 时长单位

    ///////////////////TODO -----    ADD 10271109  //////////////////////////////////////////////////////////////////////////////////////
//    Map<String, com.amap.api.maps.model.LatLng> kmGaoDemap = new HashMap<String, com.amap.api.maps.model.LatLng>();  //todo   ---- 高德每KM   配速的集合
//    Map<String, LatLng> kmGooglemap = new HashMap<String, LatLng>();    // todo --- google 每千米的轨迹

    Map<String, com.amap.api.maps.model.LatLng> kmGaoDemap = new LinkedHashMap<String, com.amap.api.maps.model.LatLng>();
    Map<String, LatLng> kmGooglemap = new LinkedHashMap<String, LatLng>();

    private double GPS_PI = 3.14159265358979323846;

    ///////////////////TODO -----    ADD 10271109  //////////////////////////////////////////////////////////////////////////////////////


    public static TrajectoryMapFragment newInstance() {
        TrajectoryMapFragment fragment = new TrajectoryMapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        mView = inflater.inflate(R.layout.activity_trajectory, null);

        StatusShared shared = new StatusShared(mContext);
        mapMode = Utils.toint(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.TV_MOTIONSETTING_MAPSETTING));//地图模式
        mapType = Utils.toint(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.TV_MOTIONSETTING_MAPTOWSETTING));//地图类型 ？？
        gpsPoint = (GpsPointDetailData) getActivity().getIntent().getSerializableExtra("Vo");   // 点击条目 进 运动历史记录 详情页面时 传   GpsPointDetailData

        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        initData(savedInstanceState);
        registerBoradcastReceiver();
        return mView;
    }
    /**
     * 注册广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(TrajectoryMapFragment.SEND_RECEIVER_SCREEN);   // 截图广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (action.equals(TrajectoryMapFragment.SEND_RECEIVER_SCREEN)) {//TODO 收到广播 ----- 截图
                if (mapType == 1) {
                    mGoogleMap.snapshot(TrajectoryMapFragment.this);
                } else {
                    mGdMap.getMapScreenShot(TrajectoryMapFragment.this);  // 地图截屏
//                    mGdMap.getMapScreenShot(new A);
//                    mGdMap.getMapPrintScreen(TrajectoryMapFragment.this);  // 打印地图
                }
            }
        }
    };


    private void initData(Bundle savedInstanceState) {
        /**得到总时间 总距离配速**/     // ----    0
        String totalPs = gpsPoint.getArrTotalSpeed();   //todo -- 普通配速数据，用显示在详情页面中的最大，最小，平均配速值   02'30''&00'5 3''&10'06''&09'46''&'&05
        Double psSum = 0.00;
        if (!totalPs.equals("") && !totalPs.equals("0")) {//TODO   手机配速
            if(totalPs.indexOf("&") !=-1) {
                String[] arrPs = totalPs.split("&");  // todo -- 手机配速的数组
                int psSize = arrPs.length;
                for (int i = 0; i < psSize; i++) {
                    if(arrPs[i].contains("'")){
                        String[] ss = arrPs[i].split("'");
                        String psok = ss[0] + "." + ss[1];
                        Double psValue = Double.valueOf(psok);  //  Double psValue = Double.valueOf(arrPs[i]);
                        if (psValue > 0) {
                            psList.add(psValue);
                            psSum += psValue;
                        }
                    }else {  // 为了容错之前的数据
                        Double psValue = Double.valueOf(arrPs[i]);  //  Double psValue = Double.valueOf(arrPs[i]);
                        if (psValue > 0) {
                            psList.add(psValue);
                            psSum += psValue;
                        }
                    }
                }
                double max = Collections.max(psList);//得到集合中最大值   TODO--- psList 手机配速的集合
                double min = Collections.min(psList);//得到集合中最小值
                maxPs = getPeisu(String.valueOf(min));  // 最大配速 --- 给最小值
                minPs = getPeisu(String.valueOf(max));   // 最小配速 --- 给最大值
                phonePjPs = getPeisu(String.valueOf(psSum/psList.size())); // 过滤掉配速为 0 的数据，否则当有配速为 0 的数据时，平均配速比 最慢配速还小
            }
        } else {        //TODO  --- 手表配速
            /**得到手表配速 手表只有每公里的配速**/
            String watchPs = gpsPoint.getSpeed();  // TODO ---- 手表和手机取配速的方式 不一样
            if (!watchPs.equals("")) {
                String[] arrWatchPs = watchPs.split("&");
                for (int i = 0; i < arrWatchPs.length; i++) {
                    if (!arrWatchPs[i].equals("")) {
                        watchPsList.add((int)Math.round(Double.parseDouble(arrWatchPs[i])));
                    }
                }
                if (watchPsList != null && watchPsList.size() > 0) {
                    maxPsInt = Collections.min(watchPsList);//得到集合中最小值  最快配速   手表
                    minPsInt = Collections.max(watchPsList);//得到集合中最大值  最慢配速
                    int totalWatchPs = 0;
                    for (int j = 0; j < watchPsList.size(); j++) {
                        totalWatchPs += watchPsList.get(j);
                    }
                    watchPjPs = totalWatchPs / watchPsList.size();//平均配速
                }
            }
        }

        /**得到所有的维度**/
        String mLat = gpsPoint.getArrLat().trim();

        gpsLat.clear();

        if (!mLat.equals("")) {
            String[] arrLat = mLat.split("&");
            int latSize = arrLat.length;
            mKmList.clear();
            for (int i = 0; i < latSize; i++) {
                if (arrLat[i].contains("KM")) {   // KM122.554409      KM1#22.554409

                    String[] mKMStr = arrLat[i].split("KM");   // KM122.560166015625       ----  122.560166015625   ---- KM122.560166 015625    ##########    KM1    "" --- "1"
                    mKmList.add(Integer.valueOf(mKMStr[1])); // TODO ，添加每个KM刻度值
                    if(!mKMStr[0].equals("")){
                        gpsKmLat.add(Utils.toDouble(mKMStr[0]));
                        gpsLat.add(Utils.toDouble(mKMStr[0]));
                    }

//                    String lat = arrLat[i].replace("KM", "");
//                    if (!lat.equals("")) { // 有定位点
//                        gpsKmLat.add(Utils.toDouble(lat));        9999999999999999999999999999
//                        gpsLat.add(Utils.toDouble(lat));
//                    }
                } else {
                    gpsLat.add(Double.valueOf(arrLat[i]));    //todo --- 纬度集合
                }
            }
        }
        /**得到所有的经度**/
        String mLon = gpsPoint.getArrLng().trim();

        gpsLon.clear();

        if (!mLon.equals("")) {
            String[] arrLon = mLon.split("&");
            int lonSize = arrLon.length;
            for (int i = 0; i < lonSize; i++) {
                if (arrLon[i].contains("KM")) {

                    String[] mKMStr = arrLon[i].split("KM");
                    if (!mKMStr[0].equals("")) {
                        gpsKmLon.add(Utils.toDouble(mKMStr[0]));
                        gpsLon.add(Utils.toDouble(mKMStr[0]));
                    }

//                    String lon = arrLon[i].replace("KM", "");
//                    if (!lon.equals("")) {
//                        gpsKmLon.add(Utils.toDouble(lon));
//                        gpsLon.add(Utils.toDouble(lon));
//                    }
                } else {
                    gpsLon.add(Utils.toDouble(arrLon[i]));   //todo --- 经度集合
                }
            }
        }

        if(gpsLat.size() > 0 && gpsLon.size() > 0){  //todo ---- add 082
            latlngList.clear();
            gdLatlngList.clear();
            for (int i = 0; i < gpsLat.size(); i++) {  // 纬度
                if (mapType == 1) {   // todo --- google
                    Double mLatitude = gpsLat.get(i);
                    Double mLongitude = gpsLon.get(i);
                    GetPoint getPoint = PositionUtil.gps84_To_Gcj02(mLatitude, mLongitude);
                    getPoint.  setWgLat(mLatitude);
                    getPoint. setWgLon(mLongitude);
                    LatLng latLng = new LatLng(getPoint.getWgLat(), getPoint.getWgLon());
                    latlngList.add(latLng);
//                    latlngList.add(new LatLng(gpsLat.get(i), gpsLon.get(i)));    9999999999999999999999999
                } else {
                    if(SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
                            || SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2")
                            || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {  // TODO --  后面还需要处理（根据手表型号）
                        if(gpsPoint.getDeviceType().equals("2") && !((String) SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.UUID, "")).equals(BleContants.RX_SERVICE_872_UUID.toString())){ //TODO  --- G703 手表数据(872不用转换)
                            // TODO --- 坐标转换，如果是G703的坐标数据，需要转换
                            com.amap.api.maps.model.LatLng latLng = new com.amap.api.maps.model.LatLng(gpsLat.get(i), gpsLon.get(i));   //经纬度
                            gdLatlngList.add(GPSCorrect.wgs2gcj(latLng));    //todo ----  标准到高德
                        }else { // todo --- 手机端数据
                            gdLatlngList.add(new com.amap.api.maps.model.LatLng(gpsLat.get(i), gpsLon.get(i)));
                        }
                    }else {  // 手机端数据，和H872 数据不用转换
                        gdLatlngList.add(new com.amap.api.maps.model.LatLng(gpsLat.get(i), gpsLon.get(i)));     //todo --- 经度，纬度的集合
                    }
                }
            }

            if (gpsPoint.getDeviceType().equals("2")){//手表端的每KM标记
                getKmLatLng();
            }else {//手机端的
                /**计算每Km的经纬度集合**/
                for (int i = 0; i < gpsKmLat.size(); i++) {
                    if (mapType == 1) {
                        latlngKmList.add(new LatLng(gpsKmLat.get(i), gpsKmLon.get(i)));
                    } else {
                        gdLatlngKmList.add(new com.amap.api.maps.model.LatLng(gpsKmLat.get(i), gpsKmLon.get(i)));
                    }
                }
            }

        }
        /** 初始化UI**/
        initView();
        /** 初始化地图**/
        initMap(savedInstanceState);
    }

    private String getPeisu(String ps) {
        String arrPs[] = ps.split("\\.");
        String m = arrPs[0];//分
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数
        if(!isMetric){
            int second = Utils.getUnit_pace((int)Math.round(Double.parseDouble(m)) * 60 + (int)sec);
            m = second / 60 + "";
            sec = second % 60;
        }
        String peisu = String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf(m), (int) sec);
        return peisu;
    }

    private void initMap(Bundle savedInstanceState) {
        if (mapType == 1) {//谷歌地图
            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mGoogleMapView = (com.google.android.gms.maps.MapView) mView.findViewById(R.id.google_map);
            mGoogleMapView.onCreate(mapViewBundle);
            mGoogleMapView.getMapAsync(new gooleOnMapReadyCallback());
            client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
            mGoogleMapView.setVisibility(View.VISIBLE);
        } else if (mapType == 0) {//高德地图
            mGdMapView = (com.amap.api.maps.MapView) mView.findViewById(R.id.gd_map);
            mGdMapView.onCreate(savedInstanceState);// 此方法必须重写
            mGdMapView.setVisibility(View.VISIBLE);
            if (mGdMap == null) {
                mGdMap = mGdMapView.getMap();
                isShowMap();//是否显示地图
                if (mapMode == 0) {
                    mapMode = 1;
                    mGdMap.setMapType(AMap.MAP_TYPE_NORMAL);//正常模式
                } else {
                    mapMode = 0;
                    mGdMap.setMapType(AMap.MAP_TYPE_SATELLITE);//卫星模式
                }
                UiSettings mUiSettings = mGdMap.getUiSettings();//实例化UiSettings类
                mUiSettings.setZoomControlsEnabled(false);//隐藏放大缩小按钮
                mUiSettings.setMyLocationButtonEnabled(false);
//                mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.zoomTo(17));//设置地图缩放级别
                /**获取运动类型  1.健走 2.户外跑 3.登山跑 4.越野跑 5.室内跑 6.半马 7.全马**/
                String aa = gpsPoint.getSportType();
                if (!gpsPoint.getSportType().equals("3")) {  // 5
                    initGDLocation();
                } else {
                    //初始化定位
                    mLocationClient = new AMapLocationClient(mContext);
                    //设置定位回调监听
                    mLocationClient.setLocationListener(new MyGDLocationListener());
                    setGDUpMap();
                }
            }
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setGDUpMap() {
        mGdMap.setLocationSource(new com.amap.api.maps.LocationSource() {
            @Override
            public void activate(OnLocationChangedListener listener) {
                mGDListener = listener;
                setUpMap();
            }
            @Override
            public void deactivate() {

            }
        });// 设置定位监听

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.icon_cursor));
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        mGdMap.setMyLocationStyle(myLocationStyle);

        UiSettings mUiSettings = mGdMap.getUiSettings();//实例化UiSettings类
        mUiSettings.setZoomControlsEnabled(false);//隐藏放大缩小按钮
        mUiSettings.setMyLocationButtonEnabled(false);
        mGdMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mGdMap.setMyLocationType(LOCATION_TYPE_LOCATE);
        /** 高德地图目前的定位类型有 3 种：
         LOCATION_TYPE_LOCATE ：只在第一次定位移动到地图中心点；
         LOCATION_TYPE_MAP_FOLLOW ：定位，移动到地图中心点并跟随；
         LOCATION_TYPE_MAP_ROTATE ：定位，移动到地图中心点，跟踪并根据面向方向旋转地图。**/
    }

    private void setUpMap() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);  // Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);

        mLocationOption.setGpsFirst(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 高德地图当前位置改变监听方法
     */
    public class MyGDLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (mGDListener != null && amapLocation != null) {
                if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                    mGDListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    com.amap.api.maps.model.LatLng mylocation = new com.amap.api.maps.model.LatLng(amapLocation.getLatitude(),
                            amapLocation.getLongitude());
                    if (isFirst) {
                        mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngZoom(mylocation, 14));
                        isFirst = false;
                    }
                } else {
                }
            }
        }
    }

    /**
     * google当前位置改变监听方法
     */
    public class MyGoogleLocationListener implements GoogleMap.OnMyLocationChangeListener {

        @Override
        public void onMyLocationChange(Location location) {
            if (/*mGooglistener != null && */location != null) {
                Double mLatitude = location.getLatitude();
                Double mLongitude = location.getLongitude();
                GetPoint getPoint = PositionUtil.gps84_To_Gcj02(mLatitude, mLongitude);   //todo --- 坐标转换
                location.setLatitude(getPoint.getWgLat() * 1E6);
                location.setLongitude(getPoint.getWgLon());
//                myGooleLocationListener.onMyLocationChange(location);
                LatLng latLng = new LatLng(getPoint.getWgLat(), getPoint.getWgLon());

                if (isFirst) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)      // Sets the center of the map to Mountain View
                            .zoom(17)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    isFirst = false;
                }
            } else {
                String errText = "定位失败," + "请检查网络！";
                Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 控制地图是否显示
     */
    private void isShowMap() {
        if (mapType == 1) {
            LatLng latLng1 = new LatLng(39.984059, 116.307771);
            mGoogleCircle = mGoogleMap.addCircle(new CircleOptions().
                    center(latLng1).
                    radius(100000000).
                    fillColor(Color.GRAY).
                    strokeColor(Color.GRAY).
                    visible(false).
                    strokeWidth(15));

        } else if (mapType == 0) {
            com.amap.api.maps.model.LatLng latLng = new com.amap.api.maps.model.LatLng(39.984059, 116.307771);
            mGDcircle = mGdMap.addCircle(new com.amap.api.maps.model.CircleOptions().
                    center(latLng).
                    radius(100000000).
                    fillColor(Color.GRAY).
                    strokeColor(Color.GRAY).
                    visible(false).
                    strokeWidth(15));
        }

    }


    /**
     * 高德地图截屏
     *
     * @param bitmap
     */
    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        ScreenShotHelper.saveScreenShot(bitmap, mViewGroupContainer, mGdMapView, mScreemShotView);
//        Toast.makeText(mContext, "SD卡下查看截图后的文件", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int i) {

    }

    /**
     * google地图截图
     * @param bitmap
     */
    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        ScreenShotHelper.savegoogleScreenShot(bitmap, mViewGroupContainer, mGoogleMapView, mScreemShotView);
    }

    /**
     * google map 获取地图实列
     */
    public class gooleOnMapReadyCallback implements OnMapReadyCallback {
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            isShowMap();
            if (mapMode == 0) {
                mapMode = 1;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//正常模式
            } else {
                mapMode = 0;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//卫星模式
            }
            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);//手势放大缩小
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

            if (!gpsPoint.getSportType().equals("3")) { //   5
                if(latlngList.size() > 0){
                    initLocation();
                }                
            } else {
                mGoogleMap.setMyLocationEnabled(true);//激活定位
                myGooleLocationListener = new MyGoogleLocationListener();
                mGoogleMap.setOnMyLocationChangeListener(myGooleLocationListener);

            }
        }
    }

    private void initView() {
        mViewGroupContainer = (ViewGroup) mView.findViewById(R.id.container);
        mViewRl = (RelativeLayout) mView.findViewById(R.id.container);
        mScreemShotView = (LinearLayout) mView.findViewById(R.id.traject_top);
        traject_date = (TextView) mView.findViewById(R.id.traject_date);   // 运动日期
        traject_mile = (TextView) mView.findViewById(R.id.traject_mile);  // 运动距离
        traject_peisu = (TextView) mView.findViewById(R.id.traject_peisu);   // 配速

        traject_pingjun = (TextView) mView.findViewById(R.id.traject_pingjun);   // 平均配速

        traject_qianka = (TextView) mView.findViewById(R.id.traject_qianka);    // 消耗
        traject_time = (TextView) mView.findViewById(R.id.traject_time);           // 时长

        traject_zuikuai = (TextView) mView.findViewById(R.id.traject_zuikuai);  // 最快
        traject_zuiman = (TextView) mView.findViewById(R.id.traject_zuiman);    // 最慢

        traject_kejian = (ImageView) mView.findViewById(R.id.traject_kejian);   // 是否可见 --- 对应 3个点击 图标
        traject_dtms = (ImageView) mView.findViewById(R.id.traject_dtms);
        traject_qianmi = (ImageView) mView.findViewById(R.id.traject_qianmi);  // 运动距离 每千米的 标识

        tv_ydgjtitle = (TextView) mView.findViewById(R.id.tv_ydgjtitle);    // tv_ydgjtitle


        String languageLx  = Utils.getLanguage();
        if (languageLx.equals("ja")  ) {     // || languageLx.equals("it") || languageLx.contains("fr")
            tv_ydgjtitle.setTextSize(10);
//            tv_psbtitle.setTextSize(11);
        }else if(languageLx.equals("ru")){
            tv_ydgjtitle.setTextSize(8);
//            tv_psbtitle.setTextSize(6);
//            speed_time.setTextSize(6);
        }

        sportmode_logo = (ImageView) mView.findViewById(R.id.sportmode_logo);
        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){  // 白色背景
            sportmode_logo.setImageResource(R.drawable.sportmode_logo_w);
        }else{
            sportmode_logo.setImageResource(R.drawable.sportmode_logo_b);
        }


        traject_mile_up = (TextView) mView.findViewById(R.id.traject_mile_up);
        traject_qianka_up = (TextView) mView.findViewById(R.id.traject_qianka_up);
        traject_peisu_up = (TextView) mView.findViewById(R.id.peisu_up);

        shichang_danwei = (TextView) mView.findViewById(R.id.shichang_danwei);
        String ss = gpsPoint.getsTime();   // 2.5638769E-9     8.81E-43
//        double sTime = Double.parseDouble(gpsPoint.getsTime());

        Double mile = Double.valueOf(gpsPoint.getMile());//  总路程
//        double pjSpeed = Utils.decimalTo2(mile / sTime, 2);//平均速度

       

        String arrPs[] = Utils.getPace(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile()));//TODO   ---- 得到配速 数组（上部配速值）
        String m = arrPs[0];//分    40
        String s = "0." + arrPs[1];// 将小数点后面的数转换成时间进制（60）      0.30769230769231
        double sec = Utils.decimalTo2(Double.valueOf(Double.valueOf(s) * 60), 2);//秒数   18.46

//        String languageLx = Utils.getLanguage();
        if(languageLx.equals("it")) {  // en
            traject_peisu_up.setTextSize(9);
            shichang_danwei.setTextSize(9);
            traject_qianka_up.setTextSize(9);
        }

        if (Utils.isDe()) {
            traject_date.setText(dateInversion(gpsPoint.getDate()));
//            viewHolder.item_date.setText(dateInversion(gpsData.getDate()));
        } else {
            traject_date.setText(gpsPoint.getDate() + "");
//            viewHolder.item_date.setText(gpsData.getDate());
        }

//        traject_date.setText(gpsPoint.getDate() + "");  // 运动日期
        if(isMetric){
            traject_mile.setText(Utils.decimalTo2(mile / 1000, 2) + "");// 里程/千米
            traject_mile_up.setText(getResources().getString(R.string.kilometer));
            traject_peisu_up.setText(getResources().getString(R.string.realtime_minutes_km));
        }else {
            traject_mile.setText(Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");// 里程/千米
            traject_mile_up.setText(getString(R.string.unit_mi));
            traject_peisu_up.setText(getResources().getString(R.string.unit_min_mi));
        }
        //traject_mile.setText(Utils.decimalTo2(mile / 1000, 2) + "");// 里程/千米

        if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||
                SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //mtk
            if(gpsPoint.getDeviceType().equals("2")){   // 设备类型    2：手表   1： 手机   TODO---- 手表端
                String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取当前配速（总用时/总距离）    setArrTotalSpeed
                if(!StringUtils.isEmpty(avgSpeed)){
                    int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                    if(!isMetric) {
                        avgPeisu = Utils.getUnit_pace(avgPeisu);
                    }
                    int fen = avgPeisu/60;
                    int miao  = avgPeisu%60 ;
                    traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");  // todo    999999999999999
                }else {
                    traject_peisu.setText("--");
                }
            }else {   // TODO---- 手机端
                traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");// 配速（分钟/公里）    40'18''    ----TODO    由分钟数40 和 秒数 18  拼装成 配速 （上部配速值）
            }
        }else {  // H872等
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") ||
                    SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")){  // todo --- 智能机配速
                double countTime =  Math.round(Utils.getPaceForWatch1(gpsPoint.getsTime(), String.valueOf(gpsPoint.getMile())));//得到配速 数组   1：gpsData.getsTime()：运动时间  2： gpsData.getMile() 运动距离    配速 = 运动时间/距离
                if(Integer.valueOf((int)(countTime/60.0)) > 1000){
                    traject_peisu.setTextSize(10);   //  --- todo  ---
                }
                traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", Integer.valueOf((int)(countTime/60.0)), Integer.valueOf((int)(countTime%60))));    // 设置配速的值

//                traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), Math.round(sec)));
            }else{

                if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.FIRMEWARECODE).equals("415")){
                    String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取当前配速（总用时/总距离）    setArrTotalSpeed
                    if(!StringUtils.isEmpty(avgSpeed)){
                        int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                        if(!isMetric) {
                            avgPeisu = Utils.getUnit_pace(avgPeisu);
                        }
                        int fen = avgPeisu/60;
                        int miao  = avgPeisu%60 ;
                        traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");  // todo    999999999999999
                    }else {
                        traject_peisu.setText("--");
                    }
                }else{
                    traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");
                }

//                traject_peisu.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");// 配速（分钟/公里）    40'18''    ----TODO    由分钟数40 和 秒数 18  拼装成 配速 （上部配速值）
            }
        }

        if(isMetric){
            traject_qianka.setText(gpsPoint.getCalorie() + "");//消耗千卡
            traject_qianka_up.setText(getResources().getString(R.string.realtime_calorie));
        }else {
            traject_qianka.setText(Utils.decimalTo2(Utils.getUnit_kal(Double.parseDouble(gpsPoint.getCalorie())),2) + "");//消耗千卡
            traject_qianka_up.setText(getString(R.string.unit_kj));
        }
        //traject_qianka.setText(gpsPoint.getCalorie() + "");//消耗千卡
        traject_time.setText(gpsPoint.getSportTime() + "");  // 时长

        if (gpsPoint.getDeviceType().equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {   // 手机      2：手表   1： 手机
            if(TextUtils.isEmpty(maxPs) && TextUtils.isEmpty(minPs) && TextUtils.isEmpty(phonePjPs)){
                traject_zuikuai.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");  // 最快
                traject_zuiman.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");   // 最慢
                traject_pingjun.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");   // 平均
            }else {
                traject_zuikuai.setText(maxPs + "");  // 最快
                traject_zuiman.setText(minPs + "");   // 最慢
                traject_pingjun.setText(phonePjPs); // 平均
            }
        } else {   // 手表
            if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) { //mtk
                traject_zuikuai.setText("--");
                traject_zuiman.setText("--");
                String avgSpeed =  gpsPoint.getmCurrentSpeed();//获取平均配速    setArrTotalSpeed
                if(!StringUtils.isEmpty(avgSpeed)){
                    int avgPeisu = (int)Math.round(Double.parseDouble(avgSpeed));
                    if(!isMetric) {
                        avgPeisu = Utils.getUnit_pace(avgPeisu);
                    }
                    int fen = avgPeisu/60;
                    int miao  = avgPeisu%60 ;
                    traject_pingjun.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", fen , miao) + "");
                }else {
                    traject_pingjun.setText("--");
                }
            }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")){
                if(TextUtils.isEmpty(maxPs) && TextUtils.isEmpty(minPs) && TextUtils.isEmpty(phonePjPs)){
                    traject_zuikuai.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");  // 最快
                    traject_zuiman.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");   // 最慢
                    traject_pingjun.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", (int)Math.round(Double.parseDouble(m)), (int) sec) + "");   // 平均
                }else {
                    traject_pingjun.setText(minPs);
                    traject_zuikuai.setText(maxPs);  // min，miao
                    traject_zuiman.setText(phonePjPs);
                }
            }else {
                traject_zuikuai.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", maxPsInt / 60, maxPsInt % 60) + "");
                traject_zuiman.setText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", minPsInt / 60, minPsInt % 60) + "");
                traject_pingjun.setText(String.format(String.format(Locale.ENGLISH,"%1$02d'%2$02d''", watchPjPs / 60, watchPjPs % 60) + "")); // 平均
            }
        }
        traject_kejian.setOnClickListener(this);
        traject_dtms.setOnClickListener(this);
        traject_qianmi.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapType == 1) {
            mGoogleMapView.onResume();
        } else if (mapType == 0) {
            mGdMapView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        if (mapType == 1) {
            client.connect();
            mGoogleMapView.onStart();
            AppIndex.AppIndexApi.start(client, getIndexApiAction());
        } else if (mapType == 0) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
        if (mapType == 1) {
            AppIndex.AppIndexApi.end(client, getIndexApiAction());
            mGoogleMapView.onStop();
            client.disconnect();
        } else if (mapType == 0) {

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mapType == 1) {
            mGoogleMapView.onPause();
        } else if (mapType == 0) {
            mGdMapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapType == 1) {
            mGoogleMapView.onDestroy();
        } else if (mapType == 0) {
            mGdMapView.onDestroy();
        }
        mContext.unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapType == 1) {
            mGoogleMapView.onLowMemory();
        } else if (mapType == 0) {
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    /**
     * 谷歌地图画线
     */
    void initLocation() {
        mpolyline = mGoogleMap.addPolyline(new PolylineOptions().color(
                0xffE8901E).width(16).addAll(latlngList));
        mOriginStartMarker = mGoogleMap.addMarker(new MarkerOptions().position(
                latlngList.get(0)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.kaishi)).anchor(0.5f, 0.5f));
        mOriginEndMarker = mGoogleMap.addMarker(new MarkerOptions().position(
                latlngList.get(latlngList.size() - 1)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.jieshu)).anchor(0.5f, 0.5f));
        try {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngList.get(0), 17));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (gpsPoint.getDeviceType().equals("2")) {//手表端的每KM标记
            Set<String> keysH = kmGooglemap.keySet();// 得到全部的key
            Iterator<String> iterH = keysH.iterator();
//            int kmAll = 0;
            while (iterH.hasNext()) {
                String itemKm = iterH.next();     // todo ---- 每千米的值
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                LatLng latLng = kmGooglemap.get(itemKm);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_main_marker, null);
                TextView tv_num_price = (TextView) view.findViewById(R.id.tv_num_price);

//                kmAll += Integer.valueOf(itemKm);
//                tv_num_price.setText(kmAll + "");

                tv_num_price.setText(itemKm);
                
                Bitmap bitmap = Util.convertViewToBitmap(view);
                mGoogleMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)// 锚点
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .draggable(true));
                mGoogleMarkerList.add(mGoogleMarker);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        }else {
             if(latlngKmList.size() > 0){     //    latlngKmList
                //            *每千米的标注*
                for (int i = 0; i < latlngKmList.size(); i++) {
                    LatLng latLng = latlngKmList.get(i);
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_main_marker, null);
                    TextView tv_num_price = (TextView) view.findViewById(R.id.tv_num_price);
                    tv_num_price.setText(i + 1 + "");
                    Bitmap bitmap = Util.convertViewToBitmap(view);
                    mGoogleMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .anchor(0.5f, 0.5f)// 锚点
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .draggable(true));
                    mGoogleMarkerList.add(mGoogleMarker);
                }
            }
        }




    }


    /**
     * 高德地图画线
     */
    void initGDLocation() {  // todo ---  高德地图画线
        List<Integer> colorList = new ArrayList<Integer>();
        colorList.add(0xFFFBE01C);
        colorList.add(0xFFE1E618);
        colorList.add(0xFF7DFF00);
        colorList.add(0xff8CD64D);
//        colorList.add(0xffE8901E);
        if (gdLatlngList.size() < 1)
            return;

        mGDpolyline = mGdMap.addPolyline(new com.amap.api.maps.model.PolylineOptions()/*.color(
                0xffE8901E)*/.width(16).useGradient(true).colorValues(colorList).addAll(gdLatlngList));
        com.amap.api.maps.model.Marker mOriginStartMarker = mGdMap.addMarker(new com.amap.api.maps.model.MarkerOptions().position(
                gdLatlngList.get(0)).icon(
                com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.kaishi)).anchor(0.5f, 0.5f));
        com.amap.api.maps.model.Marker mOriginEndMarker = mGdMap.addMarker(new com.amap.api.maps.model.MarkerOptions().position(
                gdLatlngList.get(gdLatlngList.size() - 1)).icon(
                com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.jieshu)).anchor(0.5f, 0.5f));
        try {
            mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngZoom(gdLatlngList.get(0), 19));
//            mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.changeLatLng(latLng));
//            mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngBounds(getGDBounds(),50));
//            mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.zoomBy(11));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOriginEndMarker.setToTop();

        if (gpsPoint.getDeviceType().equals("2")) {//手表端的每KM标记
            Set<String> keysH = kmGaoDemap.keySet();// 得到全部的key
            Iterator<String> iterH = keysH.iterator();
            int kmAll = 0;
            while (iterH.hasNext()) {
                String itemKm = iterH.next();     // todo ---- 每千米的值
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                com.amap.api.maps.model.LatLng latLng = kmGaoDemap.get(itemKm);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_main_marker, null);
                TextView tv_num_price = (TextView) view.findViewById(R.id.tv_num_price);

//                kmAll += Integer.valueOf(itemKm);
//                tv_num_price.setText(kmAll + "");

                tv_num_price.setText(itemKm);

                Bitmap bitmap = Util.convertViewToBitmap(view);
                mGDMarker = mGdMap.addMarker(new com.amap.api.maps.model.MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)// 锚点
//                            .icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(getBitMap(String.valueOf(i),R.drawable.black_dian)))
                        .icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap))
                        .draggable(true));
                gdMarkerList.add(mGDMarker);
            }
        }else { // todo --- 手机端的每KM 标志
            int j = 0;
            for (int i = 0; i < gdLatlngKmList.size(); i++) {   //   //todo ---- 每千米值，每千米对应的轨迹点
                com.amap.api.maps.model.LatLng latLng = gdLatlngKmList.get(i);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_main_marker, null);
                TextView tv_num_price = (TextView) view.findViewById(R.id.tv_num_price);

                tv_num_price.setText(mKmList.get(i) + "");
//                tv_num_price.setText(i + 1 + "");     // mKmList
                Bitmap bitmap = Util.convertViewToBitmap(view);
                mGDMarker = mGdMap.addMarker(new com.amap.api.maps.model.MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)// 锚点
//                            .icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(getBitMap(String.valueOf(i),R.drawable.black_dian)))
                        .icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap))
                        .draggable(true));
                gdMarkerList.add(mGDMarker);
            }
        }
    }

    /**
     * google
     * 设置轨迹可视范围
     * @return
     */
    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (latlngList == null) {
            return b.build();
        }
        for (int i = 0; i < latlngList.size(); i++) {
            b.include(latlngList.get(i));
        }
        return b.build();
    }


    /**
     * 高德
     * 设置轨迹可视范围
     * @return
     */
    private com.amap.api.maps.model.LatLngBounds getGDBounds() {
        com.amap.api.maps.model.LatLngBounds.Builder b = com.amap.api.maps.model.LatLngBounds.builder();
        if (gdLatlngList == null) {
            return b.build();
        }
        for (int i = 0; i < gdLatlngList.size(); i++) {
            b.include(gdLatlngList.get(i));
        }
        return b.build();
    }

    public void clearMap() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.traject_kejian:
                if (isVisible) {//不可见
                    if (mapType == 1) {
                        mGoogleCircle.setVisible(true);//显示地图/**/
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);//正常模式);//隐藏地图上所有文字
                        isVisible = false;
                        traject_kejian.setBackground(getResources().getDrawable(R.drawable.bukejian));
                    } else if (mapType == 0) {
                        isVisible = false;
//                        mGdMapView.setVisibility(View.GONE);
                        traject_kejian.setBackground(getResources().getDrawable(R.drawable.bukejian));
                        mGdMap.showMapText(false);//隐藏地图上所有文字
                        mGDcircle.setVisible(true);//隐藏地图
                    }
                } else {//可见
                    if (mapType == 1) {
                        mGoogleCircle.setVisible(false);//显示地图/**/
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//正常模式);//隐藏地图上所有文字
                        isVisible = true;
                        traject_kejian.setBackground(getResources().getDrawable(R.drawable.kejian));
                    } else {
                        if (mapType == 0) {
                            mGDcircle.setVisible(false);//显示地图/**/
                            mGdMap.showMapText(true);//隐藏地图上所有文字
                            isVisible = true;
//                            mGdMapView.setVisibility(View.VISIBLE);
                            traject_kejian.setBackground(getResources().getDrawable(R.drawable.kejian));
                        }
                    }
                }
                break;
            case R.id.traject_dtms:  // 地图模式选择
                switch (mapType) {
                    case 1://谷歌地图
                        if (mapMode == 0) {
                            mapMode = 1;
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//正常模式
                            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_NORMAL);
                        } else {
                            mapMode = 0;
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//卫星模式
                            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_SATELLITE);
                        }
                        break;
                    case 0://高德地图
                        if (mapMode == 0) {
                            mapMode = 1;
                            mGdMap.setMapType(AMap.MAP_TYPE_NORMAL);//正常模式
                            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_NORMAL);
                        } else {
                            mapMode = 0;
                            mGdMap.setMapType(AMap.MAP_TYPE_SATELLITE);//卫星模式
                            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_SATELLITE);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.traject_qianmi:
                if (isKm) {//无千米
                    isKm = false;
                    traject_qianmi.setBackground(getResources().getDrawable(R.drawable.wuqianmi));
                    if (mapType == 1) {
                        for (int i = 0; i < mGoogleMarkerList.size(); i++) {
                            Marker marker = mGoogleMarkerList.get(i);
                            marker.setVisible(false);
                        }
                    } else {
                        for (int i = 0; i < gdMarkerList.size(); i++) {
                            com.amap.api.maps.model.Marker marker = gdMarkerList.get(i);
                            marker.setVisible(false);
                        }
                    }
                } else {//有千米
                    isKm = true;
                    traject_qianmi.setBackground(getResources().getDrawable(R.drawable.qianmi));
                    if (mapType == 1) {
                        for (int i = 0; i < mGoogleMarkerList.size(); i++) {
                            Marker marker = mGoogleMarkerList.get(i);
                            marker.setVisible(true);
                        }
                    } else {
                        if (mGDMarker != null) {
                            for (int i = 0; i < gdMarkerList.size(); i++) {
                                com.amap.api.maps.model.Marker marker = gdMarkerList.get(i);
                                marker.setVisible(true);
                            }
                        }
                    }
                }
                break;

//            case R.id.back:
//                finish();
//                break;
        }

    }

    private void gdShowKM() {
        for (int i = 1; i < gdLatlngList.size() - 1; i++) {
            com.amap.api.maps.model.LatLng latLng = gdLatlngList.get(i);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_main_marker, null);
            TextView tv_num_price = (TextView) view.findViewById(R.id.tv_num_price);
            tv_num_price.setText(i + "");
            Bitmap bitmap = Util.convertViewToBitmap(view);
            mGDMarker = mGdMap.addMarker(new com.amap.api.maps.model.MarkerOptions()
                    .position(latLng)
                    .icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap))
                    .draggable(true));
        }
    }


    /**
     * 计算每KM的经纬度值(用于手表每Km的标注)
     */
    private void getKmLatLng() {
        if (mapType == 1) {//谷歌
            int j = 0;
            float distance2 = 0;
            int distanceAll = 0;   // 每KM整数
            kmGooglemap.clear();

            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(gpsLat.size() > 0 && gpsLon.size() > 0) {  //todo ---- add 20171202
                for (int i = 0; i < gpsLat.size(); i++) {  // 纬度
                    if(i == gpsLat.size() -1){
                        return;
                    }
                    double radLat1 = gpsLat.get(i)*GPS_PI/180.0;  // 纬度
                    double radLat2 = gpsLat.get(i+1)*GPS_PI/180.0;  // 纬度
                    double radLng1=  gpsLon.get(i)*GPS_PI /180.0;    // 经度
                    double radLng2=  gpsLon.get(i+1)*GPS_PI /180.0;    // 经度
                    double a = Math.abs(radLat1 - radLat2);
                    double b = Math.abs(radLng1 - radLng2);
                    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
                    s=s*6378137.0;
                    distance2 += s; // 每km距离

                    if (distance2 >= 1000) {
                        latlngKmList.add(latlngList.get(i+1));   // todo --- 添加每千米的 轨迹    gdLatlngKmList.add(gdLatlngList.get(i));
                        distanceAll += (int)distance2/1000;      //              distanceAll += s;
                        kmGooglemap.put(distanceAll + "",latlngList.get(i+1));   // gdLatlngList.get(i)
                        distance2 = distance2 % 1000;   //todo ---- 每千米值，每千米对应的轨迹点     -------------------------------------------   高德每千米 打点 ok
                    }
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

           /* for (int i = 1; i < latlngList.size(); i++) {
                double distance = PositionUtil.getDistance(latlngList.get(j).longitude,latlngList.get(j).latitude,latlngList.get(i).longitude,latlngList.get(i).latitude);
                distance2 += distance;
                j++;
                if (distance2 >= 1000) {
                    latlngKmList.add(latlngList.get(i));
                    distanceAll += (int)distance2/1000;
                    kmGooglemap.put(distanceAll + "", latlngList.get(i));
                    distance2 = distance2 % 1000;  //todo ---- 每千米值，每千米对应的轨迹点     -------------------------------------------   Google每千米 打点 ok
                }
            }*/
        } else {   // todo ---- 高德每千米标记
            int j = 0;
            float distance2 = 0;
            int distanceAll = 0;   // 每KM整数
            kmGaoDemap.clear();
          /*  for (int i = 1; i < gdLatlngList.size(); i++) {
                float distance = AMapUtils.calculateLineDistance(gdLatlngList.get(j), gdLatlngList.get(i));   // 这里是高德的坐标
                distance2 += distance;
                j++;
                if (distance2 >= 1000) {
                    gdLatlngKmList.add(gdLatlngList.get(i));   // todo --- 添加每千米的 轨迹
                    distanceAll += (int)distance2/1000;
                    kmGaoDemap.put(distanceAll + "",gdLatlngList.get(i));
//                    kmGaoDemap.put((int)distance2/1000 + "",gdLatlngList.get(i));   //todo ---- 每千米值，每千米对应的轨迹点     -------------------------------------------   高德每千米 打点 ok
                    distance2 = distance2 % 1000;
                }
            }*/

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// todo --- 设备端原始定位
            if(gpsLat.size() > 0 && gpsLon.size() > 0) {  //todo ---- add 20171202
                for (int i = 0; i < gpsLat.size(); i++) {  // 纬度
                    if(i == gpsLat.size() -1){
                        return;
                    }
                    double radLat1 = gpsLat.get(i)*GPS_PI/180.0;  // 纬度
                    double radLat2 = gpsLat.get(i+1)*GPS_PI/180.0;  // 纬度
                    double radLng1=  gpsLon.get(i)*GPS_PI /180.0;    // 经度
                    double radLng2=  gpsLon.get(i+1)*GPS_PI /180.0;    // 经度
                    double a = Math.abs(radLat1 - radLat2);
                    double b = Math.abs(radLng1 - radLng2);
                    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
                    s=s*6378137.0;
                    distance2 += s; // 每km距离

                    if (distance2 >= 1000) {
                        gdLatlngKmList.add(gdLatlngList.get(i+1));   // todo --- 添加每千米的 轨迹    gdLatlngKmList.add(gdLatlngList.get(i));
                        distanceAll += (int)distance2/1000;      //              distanceAll += s;
                        kmGaoDemap.put(distanceAll + "",gdLatlngList.get(i+1));   // gdLatlngList.get(i)
                        distance2 = distance2 % 1000;   //todo ---- 每千米值，每千米对应的轨迹点     -------------------------------------------   高德每千米 打点 ok
                    }
                }
            }

//                double radLat1 = lat1*GPS_PI/180.0;
//                double radLat2 = lat2*GPS_PI/180.0;
//                double radLng1=  lng1*GPS_PI /180.0;    // 经度
//                double radLng2=  lng2*GPS_PI /180.0;    // 经度
//                double a =JF_ABS1(radLat1-radLat2);
//                double b =JF_ABS1(radLng1-radLng2);
//                double s = 2 * asin(sqrt(JF_pow(sin(a / 2), 2) +cos(radLat1) * cos(radLat2) * JF_pow(sin(b / 2), 2)));
//                s=s*6378137.0;
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
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
    // 调试 签名
}
