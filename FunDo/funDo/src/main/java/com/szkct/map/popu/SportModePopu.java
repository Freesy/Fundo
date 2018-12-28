package com.szkct.map.popu;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kct.fundo.btnotification.R;
import com.szkct.map.shared.StatusShared;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

/**
 * 运动模式
 */
public class SportModePopu extends PopupWindow implements OnClickListener {

	private Context mContext;
	private View view;
	private Handler mHandler;
	/**
	 * 布局加载器
	 */
	private RelativeLayout floatingMenu;
	private FloatingActionsMenu right_labels;
	private FloatingActionButton fab_location_quanma;
	private FloatingActionButton fab_location_banma;
	private FloatingActionButton fab_location_shineipao;
	private FloatingActionButton fab_location_yuyepao;
	private FloatingActionButton fab_location_dengshanpao;
	private FloatingActionButton fab_location_huwaipao;
	private FloatingActionButton fab_location_jianzou;
	private TextView tv_sport_mode;
	private StatusShared shared;

	public SportModePopu(final Context context,Handler handler, View parent) {
		this.mContext = context;
		this.mHandler = handler;
		if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			mContext.setTheme(R.style.KCTStyleWhite);
		}else{
			mContext.setTheme(R.style.KCTStyleBlack);
		}
		view = View.inflate(context, R.layout.popu_sportmode, null);
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_ins));
		view.setFocusableInTouchMode(true);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.CENTER, 0, 0);
		initView();
		update();
//		backgroundAlpha(0.5f,(Activity) mContext);
		/** 点击空白取消popuwind **/
		getContentView().setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					setFocusable(false);
					dismiss();
				}
				return true;
			}
		});

	}

	private void initView() {
		/** 注册广播 **/
		registerBoradcastReceiver();

		shared = new StatusShared(mContext);

		floatingMenu = (RelativeLayout) view.findViewById(R.id.floatingMenu);
		right_labels = (FloatingActionsMenu) view.findViewById(R.id.right_labels);
		right_labels.toggle();
		fab_location_quanma = (FloatingActionButton) view.findViewById(R.id.fab_location_quanma);
		fab_location_banma = (FloatingActionButton) view.findViewById(R.id.fab_location_banma);
		fab_location_shineipao = (FloatingActionButton) view.findViewById(R.id.fab_location_shineipao);
		fab_location_yuyepao = (FloatingActionButton) view.findViewById(R.id.fab_location_yuyepao);
		fab_location_dengshanpao = (FloatingActionButton) view.findViewById(R.id.fab_location_dengshanpao);
		fab_location_huwaipao = (FloatingActionButton) view.findViewById(R.id.fab_location_huwaipao);
		fab_location_jianzou = (FloatingActionButton) view.findViewById(R.id.fab_location_jianzou);
        boolean isSensor=hasStepSensor(mContext);
		if (isSensor){//判断手机是否支持计步传感器
			fab_location_shineipao.setVisibility(View.VISIBLE);

		}else {
			fab_location_shineipao.setVisibility(View.GONE);
		}
		
		/*	if (isSensor){//判断手机是否支持计步传感器        lx 注释 0214
			fab_location_shineipao.setVisibility(View.VISIBLE);

		}else {
			fab_location_shineipao.setVisibility(View.GONE);
		}*/

		//////////////////////////////////////////////////////////////////////////////////
		right_labels.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
			@Override
			public void onMenuExpanded() {   // 展开的

			}

			@Override
			public void onMenuCollapsed() { //收缩的
				setFocusable(false);
				dismiss();
			}
		});
		////////////////////////////////////////////////////////////////////////////
		fab_location_quanma.setOnClickListener(this);
		fab_location_banma.setOnClickListener(this);
		fab_location_shineipao.setOnClickListener(this);
		fab_location_yuyepao.setOnClickListener(this);
		fab_location_dengshanpao.setOnClickListener(this);
		fab_location_huwaipao.setOnClickListener(this);
		fab_location_jianzou.setOnClickListener(this);

	}

	/**
	 * 注册广播
	 */
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("funfit.SPORTMODE");
		// 注册广播监听
		mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) return;
			String action = intent.getAction();
			if (action.equals("funfit.SPORTMODE")) {
				dismiss();
			}
			}
	};

	/**
	 * 设置背景透明的方法
	 * 
	 * @param bgAlpha
	 * @param context
	 */
	public void backgroundAlpha(float bgAlpha, Activity context) {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		context.getWindow().setAttributes(lp);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			/**FloatingActiomButton事件**/
			case R.id.fab_location_quanma:   // 全马
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,7+"");
//				shared.savaSportMode(7);
//				right_labels.toggle();
				mHandler.sendEmptyMessage(1007);
				break;
			case R.id.fab_location_banma:  // 半马
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,6+"");
//				shared.savaSportMode(6);
//				right_labels.toggle();
				mHandler.sendEmptyMessage(1006);
				break;
			case R.id.fab_location_yuyepao:   // 越野跑
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,5+"");
//				shared.savaSportMode(5);
//				right_labels.toggle();
				mHandler.sendEmptyMessage(1005);
				break;
			case R.id.fab_location_dengshanpao:   // 登山
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,4+"");
//				shared.savaSportMode(4);
				mHandler.sendEmptyMessage(1004);
				break;

			case R.id.fab_location_shineipao:   // 室内跑
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,3+"");
//				shared.savaSportMode(3);
//				right_labels.toggle();
				mHandler.sendEmptyMessage(1003);
				break;

			case R.id.fab_location_huwaipao:   // 户外
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,2+"");
//				shared.savaSportMode(2);
				mHandler.sendEmptyMessage(1002);
				break;
			case R.id.fab_location_jianzou:   // 健走
				SharedPreUtil.savePre(mContext,SharedPreUtil.STATUSFLAG,SharedPreUtil.SPORTMODE,1+"");
//				shared.savaSportMode(1);
				mHandler.sendEmptyMessage(1001);
				break;

			default:
			break;
		}
		dismiss();

	}


//	/**
//	 * 代码检测Android4.4后更高并且有sensor支持
//	 * @return
//     */
//	private boolean isKitkatWithStepSensor() {
//		// BEGIN_INCLUDE(iskitkatsensor)
//		// Require at least Android KitKat
//		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
//		// Check that the device supports the step counter and detector sensors
//		PackageManager packageManager = mContext.getPackageManager();
//		return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
//				&& packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
//				&& packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
//		// END_INCLUDE(iskitkatsensor)
//	}


	/**
	 * 检测计步传感器是否可以使用
	 *
	 * @PARAM CONTEXT 上下文
	 * @RETURN 是否可用计步传感器
	 */
	public static boolean hasStepSensor(Context context) {  //todo ---  是否可用计步传感器
		if (context == null) {
			return false;
		}

		Context appContext = context.getApplicationContext();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return false;
		} else {
			boolean hasSensor = false;
			Sensor sensor = null;
			try {
				hasSensor = appContext.getPackageManager().hasSystemFeature("android.hardware.sensor.stepcounter");
				SensorManager sm = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
				sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return hasSensor && sensor != null;
		}
	}



}