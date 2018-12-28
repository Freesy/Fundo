package com.szkct.map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kct.fundo.btnotification.R;
import com.szkct.map.bean.GetPoint;
import com.szkct.map.service.SportService;
import com.szkct.map.shared.StatusShared;
import com.szkct.map.utils.AnimationUtil;
import com.szkct.map.utils.PositionUtil;
import com.szkct.weloopbtsmartdevice.main.OutdoorRunActitivy;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.amap.api.maps.AMap.LOCATION_TYPE_MAP_FOLLOW;
//import static com.google.android.gms.internal.zzs.TAG;
import static com.szkct.map.service.SportService.CMD_RECEIVER;

/**
 * 实时路径
 */
public class RealTimeMapActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleMap mGoogleMap;
    private int mapMode = 0;//地图模式
    private Boolean isFull = false;//地图是否全屏显示
    private double mLatitude;//当前维度
    private double mLongitude;//当前经度
    private Boolean isFirst = true;//是否第一次定位当前位置

    private List<LatLng> goolePoints = new ArrayList<LatLng>();// google定位点画线
    private List<com.amap.api.maps.model.LatLng> gdPoints = new ArrayList<com.amap.api.maps.model.LatLng>();// 高德定位点画线

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private com.google.android.gms.maps.model.PolylineOptions mLineOptions;
    private com.amap.api.maps.model.PolylineOptions mGDLineOptions;
    private RealTimeMapActivity mContext;
    private ImageView realtime_switch;
    private ImageView realtime_quit;
    private ImageView realtime_mode;
    private LinearLayout realtime_ll;
    private TextView realtime_peisu,realtime_peisu_up;
//    float speed;// 速度
    double altitude;// 海拔
    double mile;// 总路程 KM
    int satenum = 0;// 当前GPS数量
    private ImageView realtime_gps;
    private TextView realtime_time;
    private TextView realtime_mile,realtime_mile_up;
    TranslateAnimation animation;
    private TextView realtime_qianka,realtime_qianka_up;
    private com.google.android.gms.maps.model.Polyline mpolyline;
    private com.amap.api.maps.model.Polyline mGDpolyline;
    private int mapType = 0;//1为google 0为高德
    private MyGoogleLocationListener myGooleLocationListener;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private com.google.android.gms.maps.MapView mGoogleMapView;
    private com.amap.api.maps.MapView mGdMapView;
    private com.amap.api.maps.AMap mGdMap = null;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mGooglistener;
    private com.amap.api.maps.LocationSource.OnLocationChangedListener mGDListener;
    private Marker mGooglelocationMarker;

    private ArrayList<com.amap.api.maps.model.LatLng> mRealTimepoints = new ArrayList<com.amap.api.maps.model.LatLng>();// 实时定位点的集合

    private ArrayList<AMapLocation> pointLocation = new ArrayList<AMapLocation>();  // 定位位置的集合

    private float speedw;

    private Long Count = 0l;
    private  TextView  xianshione,xiantwo,xinshithere,xinshifour,xianshifir,xianshisix;

    private boolean isMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        mContext = this;
        setContentView(R.layout.activity_realtime);
        xianshione=( TextView) findViewById(R.id.reatime_pace);
        xiantwo=( TextView) findViewById(R.id.reatime_minutes_km);
        xinshithere=( TextView) findViewById(R.id.reatime_minutes_time);
        xinshifour=( TextView) findViewById(R.id.reatime_minutes_hms);
        xianshifir=( TextView) findViewById(R.id.reatime_minutes_consume);
        xianshisix=( TextView) findViewById(R.id.reatime_minutes_calorie);

        if(!Utils.isZh(RealTimeMapActivity.this)){
            List<View> AAA=new ArrayList<View>();
            AAA.add(xianshione);
            AAA.add(xinshithere);
            AAA.add(xianshifir);
            Utils.settingAllFontsize(AAA,12);//
            List<View> bbb=new ArrayList<View>();
            bbb.add(xiantwo);
            bbb.add(xinshifour);
            bbb.add(xianshisix);
            Utils.settingAllFontsize(bbb,8);//
        }
        StatusShared shared = new StatusShared(mContext);
        mapMode = Utils.toint(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.TV_MOTIONSETTING_MAPSETTING));//地图模式
        mapType= Utils.toint(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.TV_MOTIONSETTING_MAPTOWSETTING));//地图模式

        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));

        initData(savedInstanceState);//初始化数据


//        ()intent.putExtra("googleLat",goolePoints);
//        (ArrayList<com.amap.api.maps.model.LatLng>)intent.putExtra("gdLat",gdPoints);

//        Bundle mapViewBundle = null;
//        if (savedInstanceState != null) {
//            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
//        }
//        mGoogleMapView = (MapView) findViewById(R.id.google_map);
//        mGoogleMapView.onCreate(mapViewBundle);
//        mGoogleMapView.getMapAsync(this);
//        mGoogleMapView.setVisibility(View.GONE);


//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
//        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
//
//        mLineOptions = new PolylineOptions();//添加轨迹
//        mLineOptions.width(9);
//        mLineOptions.color(0xff00c31c);

//        mGdMapView = (com.amap.api.maps.MapView) findViewById(R.id.gd_map);
//        mGdMapView.onCreate(savedInstanceState);// 此方法必须重写


    }

    private void initData(Bundle savedInstanceState) {
        /** 初始化UI**/
        initView();
        /** 注册广播 **/
        registerBoradcastReceiver();
        /** 初始化地图**/
        initMap(savedInstanceState);

    }

    private void initMap(Bundle savedInstanceState) {
        if (mapType == 1) {//谷歌地图
            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            }
            mGoogleMapView = (com.google.android.gms.maps.MapView) findViewById(R.id.google_map);
            mGoogleMapView.onCreate(mapViewBundle);
            mGoogleMapView.getMapAsync(new gooleOnMapReadyCallback());
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            mGoogleMapView.setVisibility(View.VISIBLE);

            mLineOptions = new PolylineOptions();//添加轨迹
            mLineOptions.width(15);
            mLineOptions.color(0xff00c31c);
            if(null!=mGoogleMapView){mGoogleMapView.setVisibility(View.VISIBLE);}
            if(null!=mGdMapView){ mGdMapView.setVisibility(View.INVISIBLE);}
        } else if (mapType == 0) {//高德地图
            mGdMapView = (com.amap.api.maps.MapView) findViewById(R.id.gd_map);
            mGdMapView.onCreate(savedInstanceState);// 此方法必须重写
            mGdMapView.setVisibility(View.VISIBLE);
            if(null!=mGoogleMapView){mGoogleMapView.setVisibility(View.INVISIBLE);}
            if(null!=mGdMapView){mGdMapView.setVisibility(View.VISIBLE);}
            mGDLineOptions = new com.amap.api.maps.model.PolylineOptions();//添加轨迹
            mGDLineOptions.width(15);
            mGDLineOptions.color(0xff00c31c);

            if (mGdMap == null) {
                mGdMap = mGdMapView.getMap();
                setGDUpMap();
            }
        }
    }

    private void initView() {

        realtime_ll = (LinearLayout) findViewById(R.id.ll_top);
        realtime_switch = (ImageView) findViewById(R.id.realtime_switch);
        realtime_quit = (ImageView) findViewById(R.id.realtime_quit);
        realtime_mode = (ImageView) findViewById(R.id.realtime_mode);
        realtime_peisu = (TextView) findViewById(R.id.realtime_peisu);
        realtime_gps = (ImageView) findViewById(R.id.realtime_gps);
        realtime_time = (TextView) findViewById(R.id.realtime_time);
        realtime_mile = (TextView) findViewById(R.id.realtime_mile);
        realtime_qianka = (TextView) findViewById(R.id.realtime_qianka);
        realtime_mile_up = (TextView) findViewById(R.id.realtime_mile_up);
        realtime_peisu_up = (TextView) findViewById(R.id.reatime_minutes_km);
        realtime_qianka_up = (TextView) findViewById(R.id.reatime_minutes_calorie);

        if(isMetric){
            realtime_mile_up.setText(getString(R.string.kilometer));
            realtime_peisu_up.setText(getString(R.string.realtime_minutes_km));
            realtime_qianka_up.setText(getString(R.string.realtime_calorie));
        }else{
            realtime_mile_up.setText(getString(R.string.unit_mi));
            realtime_peisu_up.setText(getString(R.string.unit_min_mi));
            realtime_qianka_up.setText(getString(R.string.unit_kj));
        }
        Intent intent = getIntent();
        if (intent != null) {
            goolePoints = (List<LatLng>) intent.getSerializableExtra("googleLat");
            gdPoints = (List<com.amap.api.maps.model.LatLng>) intent.getSerializableExtra("gdLat");   // 上一个页面传递过来的定位点
            realtime_time.setText(intent.getStringExtra("time")+"");
            realtime_mile.setText(intent.getStringExtra("mile")+"");
            if(null!=intent.getStringExtra("peisu")+""){
                realtime_peisu.setText(intent.getStringExtra("peisu")+"");
            }else{
                realtime_peisu.setText("0’0”");
            }

            realtime_qianka.setText(intent.getStringExtra("calorie")+"");
        }

        realtime_switch.setOnClickListener(this);
        realtime_quit.setOnClickListener(this);
        realtime_mode.setOnClickListener(this);
    }


    /**
     * 设置一些amap的属性
     */
    private void setGDUpMap() {
        if (mapMode == 0) {
            mapMode=1;
            mGdMap.setMapType(AMap.MAP_TYPE_NORMAL);//正常模式
        } else {
            mapMode=0;
            mGdMap.setMapType(AMap.MAP_TYPE_SATELLITE);//卫星模式
        }

        mGdMap.addPolyline(mGDLineOptions.addAll(gdPoints));
        mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.zoomTo(14)); // todo  ---- 地图缩放级别 14
        mGdMap.setLocationSource(new com.amap.api.maps.LocationSource() {
            @Override
            public void activate(OnLocationChangedListener listener) {
                mGDListener = listener;
                startlocation();
            }

            @Override
            public void deactivate () {}
         });// 设置定位监听

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.icon_cursor));
//        myLocationStyle.anchor(0, 0);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        mGdMap.setMyLocationStyle(myLocationStyle);

        UiSettings mUiSettings = mGdMap.getUiSettings();//实例化UiSettings类
        mUiSettings.setZoomControlsEnabled(false);//隐藏放大缩小按钮
        mUiSettings.setMyLocationButtonEnabled(false);
        mGdMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mGdMap.setMyLocationType(LOCATION_TYPE_MAP_FOLLOW);  // LOCATION_TYPE_LOCATE   // LOCATION_TYPE_MAP_FOLLOW
        /** 高德地图目前的定位类型有 3 种：
         LOCATION_TYPE_LOCATE ：只在第一次定位移动到地图中心点；
         LOCATION_TYPE_MAP_FOLLOW ：定位，移动到地图中心点并跟随；
         LOCATION_TYPE_MAP_ROTATE ：定位，移动到地图中心点，跟踪并根据面向方向旋转地图。**/
    }

    /**
     * 高德地图开始定位。
     */
    private void startlocation() {

//        mLocationOption.getLocationMode();
//        mLocationOption.setLocationMode(Am)
//        mLocationOption.setGpsFirst(false); // 必须设为false
//        mLocationOption.isGpsFirst();
       // 获取高精度模式下单次定位是否优先返回GPS定位信息 默认值：false 只有在单次定位高精度定位模式下有效 为true时，会等待GPS定位结果返回，最多等待30秒，若30秒后仍无GPS结果返回，返回网络定位结果
        if (mlocationClient == null) {

            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(new MyGDLocationListener());
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);

            mLocationOption.setGpsFirst(true); // todo  --- 0606测试用的false

            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(10000);    // 2000 todo ----   实时地图页面，2秒太耗性能
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //启动定位

            if(mlocationClient != null && !mlocationClient.isStarted()){  // todo --- add 0606
                mlocationClient.startLocation();
            }
//            mlocationClient.startLocation();
        }
    }

    /**
     * 注册广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(SportService.SEND_RECEIVER_DATA);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_GPS);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_TIME);
        myIntentFilter.addAction(SportService.SEND_RECEIVER_NETWORK);
        myIntentFilter.addAction(CMD_RECEIVER);
        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (action.equals(SportService.SEND_RECEIVER_DATA)) {//接收实时更新经纬度数据
                Toast.makeText(mContext, "进入GPS定位----------", Toast.LENGTH_SHORT).show();
            } else if (action.equals(SportService.SEND_RECEIVER_GPS)) {  //TODO  --- 接收运动服务 发送过来的运动数据
                if(OutdoorRunActitivy.runstate == 0) {  // todo ---为运动状态
                    Bundle bundledata = intent.getExtras();
                    if (bundledata != null) {
//                    speed = bundledata.getFloat("speed");//速度
                        satenum = bundledata.getInt("Satenum");//gps强度
                        String peisu = bundledata.getString("peisu");//配速
//                    upedateGps_signal(satenum);//更改gps强度
                        altitude = bundledata.getDouble("altitude");//海拔
                        mile = bundledata.getDouble("mile");//距离 米
                        double calorie = bundledata.getDouble("calorie");//距离 米

                        double latitude_gps = bundledata.getDouble("latitude_gps");//TODO   ---维度
                        double longitude_gps = bundledata.getDouble("longitude_gps");//TODO   ---- 经度

//                    ArrayList<LatLng> gdPoints = (LatLng)bundledata.getSerializable("points");//经度
                        if(null!=peisu){
                            realtime_peisu.setText(peisu + "");// 配速
                        }else{
                            realtime_peisu.setText("0’0”");
                        }

//                    realtime_mile.setText(String.valueOf(mile) + "");// 里程/千米
                        if(isMetric) {
                            realtime_mile.setText(Utils.decimalTo2(mile / 1000, 2) + "");// 里程/千米
                            realtime_qianka.setText((int) calorie + "");// 卡路里
                        }else{
                            realtime_mile.setText(Utils.decimalTo2(Utils.getUnit_km(mile / 1000), 2) + "");// 里程/千米
                            realtime_qianka.setText((int)(Utils.getUnit_kal(calorie)) + "");// 卡路里
                        }
                        if (mapType == 1) {
                            LatLng latlng = new LatLng(latitude_gps, longitude_gps);
                            addCustomElementsDemo(null, latlng);  //todo --- 谷歌地图画线
                        } else if (mapType == 0) {
                            com.amap.api.maps.model.LatLng latlng = new com.amap.api.maps.model.LatLng(latitude_gps, longitude_gps);

//                            if(mRealTimepoints.size() >= 2){  //todo --- 是否也要添加 长时间无GPS后再有GPS的数据处理 ??????
//                                double distance = 0.0;
//                                distance = AMapUtils.calculateLineDistance(mRealTimepoints.get(mRealTimepoints.size() - 2), mRealTimepoints.get(mRealTimepoints.size() - 1));      //获取两点之间的距离 （相邻两个点）
//                                if(distance < 200){  // 500
//                                    boolean isFlag = dealDataO(pointLocation.get(pointLocation.size() - 2).getTime(), pointLocation.get(pointLocation.size() - 1).getTime(), distance, speedw);
//                                    if(isFlag){  // 正常的数据
                                        addGDCustomElementsDemo(gdPoints, latlng);  // todo --- 高德地图画线    gdPoints只会在从Outdoor页面 进入 此页面时，传递数据
//                                    }
//                                }
//                            }

//                            addGDCustomElementsDemo(gdPoints, latlng);  // todo --- 高德地图画线    gdPoints只会在从Outdoor页面 进入 此页面时，传递数据
                        }
                    }
                }
            } else if (action.equals(SportService.SEND_RECEIVER_TIME)){   // 时间
                if(OutdoorRunActitivy.runstate == 0){  // todo ---为运动状态
                    Bundle bundledata = intent.getExtras();
                    if (bundledata != null) {
                        Count = bundledata.getLong("count");
                        satenum = bundledata.getInt("Satenum");//gps强度

                        if(Count%30 == 0 && !SharedPreUtil.readPre(mContext, SharedPreUtil.STATUSFLAG, SharedPreUtil.SPORTMODE).equals("3")){  // 每5秒检测一次GPS 信号
                            upedateGps_signal(satenum);//更改gps强度
                        }

//                    upedateGps_signal(satenum);//更改gps强度
                        String FILE_NAME = bundledata.getString("file_name");
                        String startTime = bundledata.getString("startTime");
                        int totalSec = 0;
                        int yunshu = 0;
                        totalSec = (int) (Count / 60);
                        yunshu = (int) (Count % 60);
                        int mai = totalSec / 60;
                        int sec = totalSec % 60;
                        Log.i("时间：", "------" + String.format(Locale.ENGLISH,"%1$02d:%2$02d:%3$02d", mai, sec, yunshu));
                        try {
                            realtime_time.setText(String.format(Locale.ENGLISH,"%1$02d:%2$02d:%3$02d", mai, sec, yunshu));//实时轨迹页面 运动用时 时间 todo --- 收到时间后若运动状态才更新时间
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (action.equals(SportService.SEND_RECEIVER_NETWORK)) {//gps判断
                Toast.makeText(mContext, "请打开GPS，否则无法定位...", Toast.LENGTH_SHORT).show();
            } else if (CMD_RECEIVER.equals(action)) {
                int cmd = intent.getIntExtra("cmd", -1);// 获取Extra信息
                switch (cmd) {
                    case OutdoorRunActitivy.CMD_STOP_SERVICE:// 停止服务 表示上传成功
                        endSport();
                        break;
                    case OutdoorRunActitivy.CMD_FINISH_SERVICE:
                        endSport();
                        break;
                }
            }
        }
    };

    /**
     * google map 获取地图实列
     */
    public class gooleOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
//            mGoogleMap.setLocationSource(this);
            if (mapMode == 0) {
                mapMode=1;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//正常模式
            } else {
                mapMode=0;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//卫星模式
            }

            mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);//手势放大缩小
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
//          mGoogleMap.setLocationSource(new com.google.android.gms.maps.LocationSource() {
//                @Override
//                public void activate(OnLocationChangedListener onLocationChangedListener) {
//                    mGooglistener = onLocationChangedListener;
//                    mGoogleMap.setMyLocationEnabled(true);//激活定位
//                    myGooleLocationListener = new MyGoogleLocationListener();
//                    mGoogleMap.setOnMyLocationChangeListener(myGooleLocationListener);
//                }
//
//                @Override
//                public void deactivate() {
//
//                }
//            });

            //mGoogleMap.setMyLocationEnabled(true);//激活定位
            myGooleLocationListener = new MyGoogleLocationListener();
            mGoogleMap.setOnMyLocationChangeListener(myGooleLocationListener);
            if(null!=goolePoints&&goolePoints.size()>0){
                mGoogleMap.addPolyline(mLineOptions.addAll(goolePoints));//谷歌地图第一次加载轨迹
            }
        }
//        @Override
//        public void activate(OnLocationChangedListener onLocationChangedListener) {

//            mGooglistener = onLocationChangedListener;

//            Location location = new Location(LocationManager.GPS_PROVIDER);
//            location.setLatitude(location.getLatitude());
//            location.setLongitude(location.getLongitude());
//            location.setAccuracy(100);
//            mGooglistener.onLocationChanged(location);
//            mGoogleMap.setMyLocationEnabled(true);//激活定位
//            myGooleLocationListener = new MyGoogleLocationListener();
//            mGoogleMap.setOnMyLocationChangeListener(myGooleLocationListener);
//        }
//
//        @Override
//        public void deactivate() {
//
//        }
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RealTime Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mapType == 1) {
            client.connect();
            mGoogleMapView.onStart();
            AppIndex.AppIndexApi.start(client, getIndexApiAction());
        } else if (mapType == 0) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (mapType == 1) {
            AppIndex.AppIndexApi.end(client, getIndexApiAction());
            client.disconnect();
        } else if (mapType == 0) {

        }
    }


    /**
     * 谷歌地图画线
     *
     * @param points
     * @param latLng
     */
    public void addCustomElementsDemo(ArrayList<LatLng> points, LatLng latLng) {

        if (mGooglelocationMarker == null) {
            MarkerOptions option = new MarkerOptions();
//                    option.anchor(0.5f, 0.5f).title("当前位置");
            option.position(latLng);
            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cursor));
            option.draggable(true);
            mGooglelocationMarker = mGoogleMap.addMarker(option);
        } else {
            mGooglelocationMarker.setPosition(latLng);
        }

        mLineOptions.add(latLng);
//        mLineOptions.addAll(points);
        if (mLineOptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mLineOptions.getPoints());
            } else {
                mpolyline = mGoogleMap.addPolyline(mLineOptions);
            }
        }
    }

    private boolean dealDataO(long oneTime,long nextTime,double distance,float speed){   //手机运动数据偏移处理
        long between = (nextTime - oneTime)/1000;
        double mile = (double)(between * speed);
        Log.e(ContentValues.TAG, "mile =" + mile + "  distance =" + distance + "  speed = " + speed + "   between =" + between);
        if(mile > 200){  //  500  200
            return false;
        }else{
            return true;
        }
    }

    /**
     * 高德地图画线
     *
     * @param points
     * @param latLng
     */
    public void addGDCustomElementsDemo(List<com.amap.api.maps.model.LatLng> points, com.amap.api.maps.model.LatLng latLng) {  // 每次接受进来一个点
       /* double distance = 0.0;
        distance = AMapUtils.calculateLineDistance(points.get(points.size() - 1), latLng);      //获取两点之间的距离 （相邻两个点）*///rw-友盟报数组为空越界异常
//            if(distance > 500){  //  100  todo --- 过滤掉 两点 间距离大于 30 的点
////                isCorrectData = false ;// TODO --- 注释 0518
//            }else { //todo --- 距离小于15m 才是正确的数据
//                boolean isFlag = dealDataO(pointLocation.get(pointLocation.size() - 1).getTime(), location.getTime(), distance, speedw);  // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx         float speedw = location.getSpeed();             //TODO --- 速度
//                if (!isFlag) {
//                    isCorrectData = false;
//                } else {
//                    isCorrectData = true;
//                }
//            }

        if(points.size() > 0){    // 定位位置的集合    pointLocation   mRealTimepoints
//            distance = AMapUtils.calculateLineDistance(mRealTimepoints.get(mRealTimepoints.size() - 1), latLng);      //获取两点之间的距离 （相邻两个点）
                mGDLineOptions.add(latLng);
        }else {
//            pointLocation.add(latLng);
            mRealTimepoints.add(latLng); // 添加实时运动点
        }

        if (mGDLineOptions.getPoints().size() > 1) {
            if (mGDpolyline != null) {
                mGDpolyline.setPoints(mGDLineOptions.getPoints());
            } else {
                mGDpolyline = mGdMap.addPolyline(mGDLineOptions);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.realtime_switch:
                switch (mapType) {
                    case 1://谷歌地图
                        if (mapMode == 0) {
                            mapMode = 1;
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//正常模式
                        } else {
                            mapMode = 0;
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//卫星模式
                        }
                        break;
                    case 0://高德地图
                        if (mapMode == 0) {
                            mapMode = 1;
                            mGdMap.setMapType(AMap.MAP_TYPE_NORMAL);//正常模式
                        } else {
                            mapMode = 0;
                            mGdMap.setMapType(AMap.MAP_TYPE_SATELLITE);//卫星模式
                        }
                        break;
                    default:
                        break;

                }
                break;
            case R.id.realtime_quit:
                Intent intent = new Intent(mContext, OutdoorRunActitivy.class);
                startActivity(intent);

                break;
            case R.id.realtime_mode:
                if (isFull) {
                    realtime_ll.setVisibility(View.VISIBLE);
                    realtime_quit.setVisibility(View.VISIBLE);
                    realtime_switch.setVisibility(View.VISIBLE);
                    realtime_ll.setAnimation(AnimationUtil.moveToViewLocation());
                    realtime_quit.setAnimation(AnimationUtil.moveToViewLeft());
                    // 向右边移入
                    realtime_switch.setAnimation(AnimationUtil.moveToRight());
                    isFull = false;
                } else {
                    // 向右边移出
                    realtime_quit.setAnimation(AnimationUtil.moveToViewRight());
                    // 向右边移入
                    realtime_switch.setAnimation(AnimationUtil.moveToLeft());
                    realtime_ll.setAnimation(AnimationUtil.moveToViewBottom());
                    realtime_quit.setVisibility(View.GONE);
                    realtime_switch.setVisibility(View.GONE);
                    realtime_ll.setVisibility(View.GONE);
                    isFull = true;
                }
                break;
        }
    }


    public class MyLocation implements LocationSource.OnLocationChangedListener {
        @Override
        public void onLocationChanged(Location location) {    //9999999999999999999999    onLocationChanged

        }
    }

    /**
     * google当前位置改变监听方法
     */
    public class MyGoogleLocationListener implements GoogleMap.OnMyLocationChangeListener {

        @Override
        public void onMyLocationChange(Location location) {
            if (/*mGooglistener != null && */location != null) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                GetPoint getPoint = PositionUtil.gps84_To_Gcj02(mLatitude, mLongitude);
                if(null!=getPoint&&!(getPoint.getWgLat()+"").equals("null")){
                    location.setLatitude(getPoint.getWgLat() * 1E6);
                    location.setLongitude(getPoint.getWgLon());
                }
//                myGooleLocationListener.onMyLocationChange(location);
                LatLng latLng = new LatLng(getPoint.getWgLat(), getPoint.getWgLon());
//                LatLng latLng = new LatLng(mLatitude*1E6, mLongitude*1E6);
                goolePoints.add(latLng);
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
//                addCustomElementsDemo(points, latLng);
            }else {
                String errText = "定位失败," + "请检查网络！";
                Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 高德地图当前位置改变监听方法
     */
    public class MyGDLocationListener implements AMapLocationListener {  // todo --- 实时画线 修改点
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (mGDListener != null && amapLocation != null) {
                if (amapLocation != null && amapLocation.getErrorCode() == 0) {   //amapLocation.getErrorCode() == 0 ----  success   todo --- 可以去掉 此条件 amapLocation.getErrorCode() == 0
                    mGDListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    com.amap.api.maps.model.LatLng mylocation = new com.amap.api.maps.model.LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                    if (isFirst) {
                        mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.newLatLngZoom(mylocation, 14));
                        isFirst = false;
                    }else {
//                        mGdMap.moveCamera(com.amap.api.maps.CameraUpdateFactory.changeLatLng(mylocation));
                    }

//                    addGDCustomElementsDemo(gdPoints, mylocation);   // 05271440 注释掉
                } else {
                    if(Count%30 == 0){
                        String errText = "定位失败," + amapLocation.getErrorInfo();  // 定位失败,网络连接异常
                        Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
                    }
                }
            }


            if(amapLocation != null && amapLocation.getErrorCode() == 0) {
                double mLatitude = amapLocation.getLatitude();      //纬度
                double mLongitude = amapLocation.getLongitude();    //经度

                com.amap.api.maps.model.LatLng latLng = new com.amap.api.maps.model.LatLng(mLatitude, mLongitude);   //经纬度

                speedw = amapLocation.getSpeed();             //TODO --- 实时的速度

                long locationTime = amapLocation.getTime();                 //定位时间

             //   pointLocation.add(amapLocation);    // 定位位置的集合

              //  mRealTimepoints.add(latLng);
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    /**
     * 更改GPS信号
     *
     * @param satenum
     */
    private void upedateGps_signal(int satenum) {
        // 提示打开GPS
        if (!Utils.isGpsEnabled((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE))) {
            satenum=0;
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
                realtime_gps.setBackgroundResource(R.drawable.gps3); // gps2
                break;
            default:
                realtime_gps.setBackgroundResource(R.drawable.gps3);  // gps3   gps1
//                Toast.makeText(this,getString(R.string.gps_sign_warn),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 结束运动
     */
    private void endSport() {
        if(mapType == 1) {
            mGoogleMap.clear();
        }else {
            if(mlocationClient != null  && mlocationClient.isStarted() ){     // todo --- add 0606
                mlocationClient.stopLocation();
            }
        }
//        myLocationListener.stopLocation();// 结束地图定位
        this.unregisterReceiver(mBroadcastReceiver);// 取消注册的CommandReceiver
        finish();

//        if (locationManager != null && locationListener != null && gpsSatelliteListener != null) {
//            locationManager.removeUpdates(locationListener);
//            locationManager.removeGpsStatusListener(gpsSatelliteListener);
//            Log.i(TAG, "locationManager.removeGpsStatusListener.");
//        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mapType == 1) {
            mGoogleMapView.onResume();
        } else if (mapType == 0) {
            mGdMapView.onResume();
        }
        /**屏幕常亮**/
        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
                SharedPreUtil.CB_RUNSETTING_SCREEN, SharedPreUtil.YES).equals(
                SharedPreUtil.YES)) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     *
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
     /*   if (mapType == 1) {

        } else if (mapType == 0) {
            mGdMapView.onPause();
        }*/
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapType == 1) {

        } else if (mapType == 0) {
            mGdMapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        Log.i("Map","规划路径界面销毁......");
        super.onDestroy();
        if (mapType == 1) {
            mGoogleMapView.onDestroy();
        } else if (mapType == 0) {
            mGdMapView.onDestroy();
            if (null != mlocationClient) {
                mlocationClient.onDestroy();
            }
        }
        mRealTimepoints.clear(); // 清空实时轨迹点的集合

        pointLocation.clear();

        if(null != mBroadcastReceiver){
            unregisterReceiver(mBroadcastReceiver);
        }
    }
}






