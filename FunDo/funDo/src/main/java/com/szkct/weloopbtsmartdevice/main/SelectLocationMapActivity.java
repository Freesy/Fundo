package com.szkct.weloopbtsmartdevice.main;

import android.support.v7.app.AppCompatActivity;


/**
 * 选择地点
 * 
 * @author Administrator
 * 
 */
public class SelectLocationMapActivity extends AppCompatActivity { // implements OnClickListener, LocationSource, AMapLocationListener {
//
//	private ImageView mBack;
//	private TextView mTitle;
//	private Context mContext;
//
//	// 百度地图控件
//	private MapView mMapView = null;
//	// 百度地图
//	private AMap mAMap;
//	// 搜索模块，也可去掉地图模块独立使用
////	private GeoCoder mSearch = null;
//	private GeocodeSearch mSearch = null;
//	
//	// 百度地图定位
////	public LocationClient mlLocationClient = null;
////	private LocationMode mCurrentMode;// 定位模式
//	
//	private AMapLocationClient mapLocationClient = null;
//	private AMapLocationClientOption mClientOption = null;
//	private OnLocationChangedListener mChangedListener = null;
//	
//	private BitmapDescriptor mCurrentMarker;
//	// 当前经纬度
//	private double mLat;
//	private double mLon;
//	// 得到地理地址
//	private String mAddr;
//	// 第二次修改地理地址
//	private String mSecondLat;
//	private String mSecondLon;
//	/**
//	 * 覆盖物
//	 */
//	public Marker mMarkerA;
//	// 初始化全局 bitmap 信息，不用时及时 recycle
//	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
//
//	private int where;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.select_activitylocation_main);
//		mContext = this;
//		init();
//	}
//
//	private void init() {
//		mBack = (ImageView) findViewById(R.id.location_title_back);
//		mBack.setOnClickListener(this);
//
//		Intent intent = getIntent();
//		where = intent.getExtras().getInt("where");
//		// 修改活动地点
//		mSecondLat = intent.getStringExtra("reviseLat");
//		mSecondLon = intent.getStringExtra("reviseLon");
//		/*
//		 * if (2 == where) { mTitle.setText(R.string.activity_location); }
//		 */
//
//		mMapView = (MapView) findViewById(R.id.activitylocation_mapView);
//		mAMap = mMapView.getMap();
//		// 初始化搜索模块，注册事件监听
//		mSearch = new GeocodeSearch(this);
//		mSearch.setOnGeocodeSearchListener(this);
//		// 普通地图
//		// mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
////		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(14);// 缩放级别
////		mBaiduMap.setMapStatus(u);
////		deleteBaiSome();
//		// 判断是否是修改地点
//		if (null == mSecondLat || "".equals(mSecondLat)) {
//			location();
//		}
//		// 修改地点
//		else {
//			// Geo搜索
//			initOverlay(Double.valueOf(mSecondLat), Double.valueOf(mSecondLon));
//			LatLng llA = new LatLng(Double.valueOf(mSecondLat), Double.valueOf(mSecondLon));
//			// FIXME
//			mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//			mAMap.moveCamera(CameraUpdateFactory.changeLatLng(llA));
//			MarkerOptions markerOptions = new MarkerOptions().position(llA).icon(bdA).zIndex(9);
//			mMarkerA = mAMap.addMarker(markerOptions);
//			
//			
////			MapStatusUpdate mUpdate = MapStatusUpdateFactory.newLatLng(llA);
////			mBaiduMap.animateMapStatus(mUpdate);
////			// 获得地理位置
////			// mAddr = arg0.getAddress();
////			// TipsToast.makeText(mContext, mAddr,
////			// TipsToast.LENGTH_SHORT).show();
////			OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9);
////			mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
//			// 定位成功就设置地图点击事件
//			mMapView.setClickable(true);
//			initListener();
//		}
//	}
//
//	private float XPosition = 0;
//	private float YPosition = 0;
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			YPosition = event.getY();
//			XPosition = event.getX();
//			break;
//
//		case MotionEvent.ACTION_MOVE:
//			if (event.getY() - YPosition > 50 || YPosition - event.getY() > 50) {
//				break;
//			}
//			if (event.getX() - XPosition > 80) {
//				finish();
//				overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
//			}
//			break;
//
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}
//
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		return super.dispatchTouchEvent(ev);
//	}
//
//	private void initListener() {
//		// 单击
//		mAMap.setOnMapClickListener(new OnMapClickListener() {
//			public void onMapClick(LatLng point) {
//				initOverlay(point.latitude, point.longitude);
//			}
//		});
//		// 长按
//		mAMap.setOnMapLongClickListener(new OnMapLongClickListener() {
//			public void onMapLongClick(LatLng point) {
//				initOverlay(point.latitude, point.longitude);
//			}
//		});
//		// 双击
////		mAMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
////			public void onMapDoubleClick(LatLng point) {
////				initOverlay(point.latitude, point.longitude);
////			}
////		});
//	}
//
//	public void initOverlay(double lat, double lon) {
//		mMarkerA.remove();
//		// add marker overlay
//		mLat = lat;
//		mLon = lon;
//		LatLng llA = new LatLng(lat, lon);
//		MarkerOptions markerOptions = new MarkerOptions().position(llA).icon(bdA).zIndex(9);
//		mMarkerA = mAMap.addMarker(markerOptions);
//		LatLonPoint latLonPoint = new LatLonPoint(mLat, mLon);
//		// 反Geo搜索 得到地理位置或经纬度
//		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 50, GeocodeSearch.GPS);
//		mSearch.getFromLocationAsyn(query);
////		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(llA));
//	}
//
//	/**
//	 * 定位
//	 */
//	private void location() {
//		
//		mAMap.setMyLocationEnabled(true);
//		mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
//		UiSettings settings = mAMap.getUiSettings();
////		mAMap.setLocationSource(this);
//		settings.setMyLocationButtonEnabled(false);
//		settings.setZoomControlsEnabled(false);
//		mAMap.setMyLocationEnabled(true);
//		mAMap.setLocationSource(this);
//		
//		MyLocationStyle myLocationStyle = new MyLocationStyle();
//		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_point_blue));
//		myLocationStyle.radiusFillColor(android.R.color.transparent);
//		myLocationStyle.strokeColor(android.R.color.transparent);
//		mAMap.setMyLocationStyle(myLocationStyle);
//		
//		mapLocationClient = new AMapLocationClient(this);
//		mClientOption = new AMapLocationClientOption();
//		mClientOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
//		mClientOption.setGpsFirst(true);
//		mClientOption.setInterval(1000);
//		mClientOption.setNeedAddress(true);
//		mapLocationClient.setLocationOption(mClientOption);
//		mapLocationClient.startLocation();
//		
//		
////		// 开启定位图层
////		mBaiduMap.setMyLocationEnabled(true);
////		// 定位模式为跟随模式
////		mCurrentMode = LocationMode.FOLLOWING;
////		mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_point_blue);
////		// 自定义定位图标
////		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
////		// 定位初始化 // 声明LocationClient类
////		mlLocationClient = new LocationClient(getApplicationContext());
////		// 注册监听函数
////		mlLocationClient.registerLocationListener(this);
////		LocationClientOption option = new LocationClientOption();
////		option.setOpenGps(true);// 打开GPS
////		option.setCoorType("bd09ll"); // 设置坐标类型
////		option.setScanSpan(1000);
////		// 设置反地理
////		option.setAddrType("all");
////		mlLocationClient.setLocOption(option);
////		mlLocationClient.start();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.location_title_back:
//			returnData();
//			break;
//		}
//	}
//
//	boolean isFirstLoc = true;
//
//	// 定位监听
////	@Override
////	public void onReceiveLocation(BDLocation location) {
////		// map view 销毁后不在处理新接收的位置
////		if (null == location || null == mMapView)
////			return;
////		if (isFirstLoc) {
////			isFirstLoc = false;
////			LatLng firstPoint = new LatLng(location.getLatitude(), location.getLongitude());
////			mLon = location.getLongitude();
////			mLat = location.getLatitude();
////			initOverlay(mLat, mLon);
////			// 获得地理位置
////			mAddr = location.getAddrStr();
////			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(firstPoint);
////			mBaiduMap.animateMapStatus(u);
////			// 定位成功就设置地图点击事件
////			mMapView.setClickable(true);
////			initListener();
////		}
////	}
//
//	@Override
//	public void onLocationChanged(AMapLocation location) {
//		if (null == location || null == mMapView)
//			return;
//		if (location.getErrorCode() == 0) {
//			if (isFirstLoc) {
//				isFirstLoc = false;
//				mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//				mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//				mChangedListener.onLocationChanged(location);
//				mLon = location.getLongitude();
//				mLat = location.getLatitude();
//				initOverlay(mLat, mLon);
//				mAddr = location.getAddress();
//				mMapView.setClickable(true);
//				initListener();
//			}
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		if (null != mapLocationClient) {
//			mapLocationClient.stopLocation();
//			mapLocationClient = null;
//		}
//		mMapView.onDestroy();
//		if (null != mCurrentMarker) {
//			mCurrentMarker.recycle();
//		}
//		if (null != bdA) {
//			bdA.recycle();
//		}
//	}
//
////	// 根据地理城市得到经纬度
////	@Override
////	public void onGetGeoCodeResult(GeoCodeResult arg0) {
////	}
//
////	// 根据经纬度得到地理城市。
////	@Override
////	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
////		if (null == result || SearchResult.ERRORNO.NO_ERROR != result.error) {
////			Log.e("onGetReverseGeoCodeResult", "抱歉，未能找到结果");
////			return;
////		}
////		mAddr = result.getAddress();
////		Toast.makeText(mContext, mAddr, Toast.LENGTH_SHORT).show();
////	}
//
//	@Override
//	public void onGeocodeSearched(GeocodeResult result, int arg1) {
//	}
//
//	@Override
//	public void onRegeocodeSearched(RegeocodeResult result, int arg1) {
//		if (arg1 != 0 && result == null && result.getRegeocodeAddress() == null) {
//			Log.e("onRegeocodeSearched", "抱歉，未能找到结果");
//			return;
//		}
//		mAddr = result.getRegeocodeAddress().getFormatAddress();
//		Toast.makeText(mContext, mAddr, Toast.LENGTH_SHORT).show();
//	}
//	
//	// 把值返回给上一个Activity
//	private void returnData() {
//		Intent intent = new Intent();
//		intent.putExtra("lat", mLat + "");
//		intent.putExtra("lon", mLon + "");
//		intent.putExtra("addr", mAddr);
//		if (2 == where) {
//			setResult(8, intent);
//
//		}
//		/*
//		 * else { setResult(6, intent); }
//		 */
//		finish();
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// 监听返回键
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			returnData();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	public void activate(OnLocationChangedListener arg0) {
//		mChangedListener = arg0;
//	}
//
//	@Override
//	public void deactivate() {
//		mChangedListener = null;
//	}
//
}
