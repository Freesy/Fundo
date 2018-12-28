package com.szkct.weloopbtsmartdevice.main;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.map.utils.Util;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.StrericWheelAdapter;
import com.szkct.weloopbtsmartdevice.view.WheelView;

public class MotionGoalActivity extends AppCompatActivity implements
		OnClickListener {

	TextView tv_freerun,tv_distancegoal,tv_distancegoal_settingvalue,tv_timegoal,tv_timegoal_settingvalue,tv_kalgoal,tv_kalgoal_settingvalue;
	View  line_distancegoal,line_timegoal,line_kalgoal;
	RelativeLayout rl_freerunning,rl_distancegoal,rl_timegoal,rl_kalgoal;
	ImageView img_distancegoal,img_timegoal,img_kalgoal;
	private String[] distancelist, timelist, kallist,distancelist_unit,kallist_unit;
	
	private String[] distancecustoml,distancecustomr,timecustoml,timecustomr,kalcustom,distancecustoml_unit,distancecustomr_unit,kalcustom_unit;
	private String[] mSexArr = new String[2];
	private WheelView wheelView,wheell,wheelr;
	private PopupWindow mPopupWindow,mtowPopupWindow;
	private boolean isMetric;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.rl_freerunning:

			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
					SharedPreUtil.MotionGoal, "0");

			Intent intent = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
			BTNotificationApplication.getInstance().sendBroadcast(intent);
			setViewGone();

			break;
		case R.id.rl_distancegoal:

			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
					SharedPreUtil.MotionGoal, "1");

			Intent intent2 = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
			BTNotificationApplication.getInstance().sendBroadcast(intent2);

			setViewGone();

			break;
		case R.id.rl_timegoal:

			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
					SharedPreUtil.MotionGoal, "2");

			Intent intent3 = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
			BTNotificationApplication.getInstance().sendBroadcast(intent3);
			setViewGone();

			break;
		case R.id.rl_kalgoal:

			SharedPreUtil.savePre(getApplicationContext(), SharedPreUtil.USER,
					SharedPreUtil.MotionGoal, "3");

			Intent intent4 = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
			BTNotificationApplication.getInstance().sendBroadcast(intent4);
			setViewGone();

			break;
		case R.id.tv_distancegoal_settingvalue:

			showpop(arg0,0);

			break;
		case R.id.tv_timegoal_settingvalue:

			showpop(arg0,1);

			break;
		case R.id.tv_kalgoal_settingvalue:

			showpop(arg0,2);

			break;
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (SharedPreUtil.readPre(this, SharedPreUtil.USER,
				SharedPreUtil.THEME_WHITE).equals("0")) {
			setTheme(R.style.KCTStyleWhite);
		} else {
			setTheme(R.style.KCTStyleBlack);
		}
		setContentView(R.layout.activity_motiongoal);

		/**屏幕常亮**/
		if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
				SharedPreUtil.CB_RUNSETTING_SCREEN, SharedPreUtil.YES).equals(
				SharedPreUtil.YES)) {
			getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		} else {
			getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
		initarr();
		initview();
		initviewdata();
	}

	

	private void initarr() {
		// TODO Auto-generated method stub    // +getString(R.string.distence)
		distancelist = new String[] {getString(R.string.motionsettting_custom),0.5+getString(R.string.kilometer),1+getString(R.string.kilometer),
				2+getString(R.string.kilometer),3+getString(R.string.kilometer),
						5+getString(R.string.kilometer),10+getString(R.string.kilometer)};
		// +getString(R.string.sleeptime)
		timelist=new String[] {getString(R.string.motionsettting_custom),10+getString(R.string.everyday_show_unit),20+getString(R.string.everyday_show_unit),
				30+getString(R.string.everyday_show_unit),60+getString(R.string.everyday_show_unit),
				120+getString(R.string.everyday_show_unit),180+getString(R.string.everyday_show_unit)};
		// +getString(R.string.kaluli)
		kallist=new String[] {getString(R.string.motionsettting_custom),50+getString(R.string.calories),100+getString(R.string.calories),200+getString(R.string.calories),
				300+getString(R.string.calories),600+getString(R.string.calories),1000+getString(R.string.calories)};

		distancelist_unit = new String[]{getString(R.string.motionsettting_custom),Utils.decimalTo2(Utils.getUnit_km(0.5),1)+getString(R.string.unit_mi),Utils.decimalTo2(Utils.getUnit_km(1),1)+getString(R.string.unit_mi),
				Utils.decimalTo2(Utils.getUnit_km(2),1)+getString(R.string.unit_mi),Utils.decimalTo2(Utils.getUnit_km(3),1)+getString(R.string.unit_mi),
				Utils.decimalTo2(Utils.getUnit_km(5),1)+getString(R.string.unit_mi),Utils.decimalTo2(Utils.getUnit_km(10),1)+getString(R.string.unit_mi)
		};

		kallist_unit = new String[]{
				getString(R.string.motionsettting_custom),Math.round(Utils.getUnit_kal(50))+getString(R.string.unit_kj),Math.round(Utils.getUnit_kal(100))+getString(R.string.unit_kj),Math.round(Utils.getUnit_kal(200))+getString(R.string.unit_kj),
				Math.round(Utils.getUnit_kal(300))+getString(R.string.unit_kj),Math.round(Utils.getUnit_kal(600))+getString(R.string.unit_kj),Math.round(Utils.getUnit_kal(1000))+getString(R.string.unit_kj)
		};

		distancecustoml=new String[50] ;
		for (int i = 0; i < distancecustoml.length; i++) {
			distancecustoml[i]=i+1+"";
		}

		distancecustoml_unit = new String[31];
		for (int i = 0; i < distancecustoml_unit.length; i++) {
			distancecustoml_unit[i]=i+1+"";
		}


		distancecustomr=new String[10] ;
		for (int i = 0; i < distancecustomr.length; i++) {
			distancecustomr[i]="."+i+getString(R.string.kilometer);
		}
		
		timecustoml=new String[13] ;
		for (int i = 0; i < timecustoml.length; i++) {
			timecustoml[i]=i+getString(R.string.everyday_show_h);
		}
		timecustomr=new String[12] ;
		for (int i = 0; i < timecustomr.length; i++) {
			timecustomr[i]=i*5+getString(R.string.everyday_show_unit);
		}
		kalcustom=new String[20] ;
		for (int i = 0; i < kalcustom.length; i++) {
			kalcustom[i]=(i+1)*50+getString(R.string.calories);
		}

		distancecustomr_unit=new String[7] ;
		for (int i = 0; i < distancecustomr_unit.length; i++) {
			distancecustomr_unit[i]="."+i+getString(R.string.unit_mi);
		}

		kalcustom_unit=new String[20] ;
		for (int i = 0; i < kalcustom_unit.length; i++) {
			kalcustom_unit[i]=Math.round(Utils.getUnit_kal((i+1)*50))+getString(R.string.unit_kj);
		}
	}

	private void initview() {
		findViewById(R.id.back).setOnClickListener(this);
		
		tv_freerun=(TextView)findViewById(R.id.tv_freerun);
		rl_freerunning=(RelativeLayout)findViewById(R.id.rl_freerunning);
		
		tv_distancegoal=(TextView)findViewById(R.id.tv_distancegoal);
		tv_distancegoal_settingvalue=(TextView)findViewById(R.id.tv_distancegoal_settingvalue);
		
		tv_timegoal=(TextView)findViewById(R.id.tv_timegoal);
		tv_timegoal_settingvalue=(TextView)findViewById(R.id.tv_timegoal_settingvalue);
		
		tv_kalgoal=(TextView)findViewById(R.id.tv_kalgoal);
		tv_kalgoal_settingvalue=(TextView)findViewById(R.id.tv_kalgoal_settingvalue);
		
		line_distancegoal=(View)findViewById(R.id.line_distancegoal);
		line_timegoal=(View)findViewById(R.id.line_timegoal);
		line_kalgoal=(View)findViewById(R.id.line_kalgoal);

		rl_distancegoal = findViewById(R.id.rl_distancegoal);
		rl_timegoal = findViewById(R.id.rl_timegoal);
		rl_kalgoal = findViewById(R.id.rl_kalgoal);
		img_distancegoal = findViewById(R.id.img_distancegoal);
		img_timegoal = findViewById(R.id.img_timegoal);
		img_kalgoal = findViewById(R.id.img_kalgoal);

		rl_distancegoal.setOnClickListener(this);
		rl_timegoal.setOnClickListener(this);
		rl_kalgoal.setOnClickListener(this);

		rl_freerunning.setOnClickListener(this);
	//	tv_distancegoal.setOnClickListener(this);
	//	tv_timegoal.setOnClickListener(this);
	//	tv_kalgoal.setOnClickListener(this);
		
		tv_distancegoal_settingvalue.setOnClickListener(this);
		tv_timegoal_settingvalue.setOnClickListener(this);
		tv_kalgoal_settingvalue.setOnClickListener(this);



	}
	
	
	private void initviewdata() {
		// TODO Auto-generated method stub
		
//		String distancegoal =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.DISTANCEGOAL,"0.5"+getString(R.string.kilometer));  // 默认0.5公里
//		String timegoal =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.TIMEGOAL,"10"+getString(R.string.everyday_show_unit));   // 默认10分钟
//		String kalgoal =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.KALGOAL,"50"+getString(R.string.calories));     // 默认50大卡

		String distancegoal =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.DISTANCEGOAL,"0.5");  // 默认0.5公里
		String timegoal =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.TIMEGOAL,"10");   // 默认10分钟
		String kalgoal =SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.KALGOAL,"50");     // 默认50大卡

		if(!isMetric){
			distancegoal = Utils.decimalTo2(Utils.getUnit_km(Float.parseFloat(distancegoal)),1) + getString(R.string.unit_mi);
			kalgoal = Math.round(Utils.getUnit_kal(Float.parseFloat(kalgoal))) + getString(R.string.unit_kj);
		}else {
			distancegoal = distancegoal + getString(R.string.kilometer);
			kalgoal = Math.round(Double.parseDouble(kalgoal)) + getString(R.string.calories);
		}
		tv_distancegoal_settingvalue.setText(distancegoal);
		tv_timegoal_settingvalue.setText(timegoal + getString(R.string.everyday_show_unit) );
		tv_kalgoal_settingvalue.setText(kalgoal);
		
		setViewGone();
	}

	private void setViewGone() {
		// TODO Auto-generated method stub

		int select =Utils.toint(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MotionGoal,"0"));
		
		tv_freerun.setVisibility(View.GONE);
		tv_distancegoal_settingvalue.setVisibility(View.GONE);
		tv_timegoal_settingvalue.setVisibility(View.GONE);
		tv_kalgoal_settingvalue.setVisibility(View.GONE);
		line_distancegoal.setVisibility(View.GONE);
		line_timegoal.setVisibility(View.GONE);
		line_kalgoal.setVisibility(View.GONE);

	//	tv_distancegoal.setCompoundDrawables(null, null, null, null);
	//	tv_timegoal.setCompoundDrawables(null, null, null, null);
	//	tv_kalgoal.setCompoundDrawables(null, null, null, null);
	//	Drawable drawable = getResources().getDrawable(
	//			R.drawable.im_motiongoal_check);

	//	drawable.setBounds(0, 0, drawable.getMinimumWidth(),
	//			drawable.getMinimumHeight());// 必须设置图片大小，否则不显示

		img_distancegoal.setVisibility(View.GONE);
		img_timegoal.setVisibility(View.GONE);
		img_kalgoal.setVisibility(View.GONE);


		switch (select) {
		case 0:
			tv_freerun.setVisibility(View.VISIBLE);
			break;
		case 1:
			tv_distancegoal_settingvalue.setVisibility(View.VISIBLE);
			line_distancegoal.setVisibility(View.VISIBLE);
		//	tv_distancegoal.setCompoundDrawables(null, null, drawable, null);
			img_distancegoal.setVisibility(View.VISIBLE);

			break;
		case 2:
			tv_timegoal_settingvalue.setVisibility(View.VISIBLE);
			line_timegoal.setVisibility(View.VISIBLE);
		//	tv_timegoal.setCompoundDrawables(null, null, drawable, null);
			img_timegoal.setVisibility(View.VISIBLE);

			break;
		case 3:
			tv_kalgoal_settingvalue.setVisibility(View.VISIBLE);
			line_kalgoal.setVisibility(View.VISIBLE);
		//	tv_kalgoal.setCompoundDrawables(null, null, drawable, null);
			img_kalgoal.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	private void showpop( final View popv, final int s) {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_menu, null);
		wheelView = (WheelView) view.findViewById(R.id.targetWheel);	
		
		switch (s) {
		case 0:
			if(isMetric) {
				wheelView.setAdapter(new StrericWheelAdapter(distancelist));
			}else{
				wheelView.setAdapter(new StrericWheelAdapter(distancelist_unit));
			}
			
				wheelView.setCurrentItem(Utils.toint(SharedPreUtil.readPre(getApplicationContext(),
					SharedPreUtil.USER,
					SharedPreUtil.DISTANCEGOAL_NUMBER, "1")));
			
			break;
		case 1:
			wheelView.setAdapter(new StrericWheelAdapter(timelist));
			
				wheelView.setCurrentItem(Utils.toint(SharedPreUtil.readPre(getApplicationContext(),
					SharedPreUtil.USER,
					SharedPreUtil.TIMEGOAL_NUMBER, "1")));
			
			break;
		case 2:
			if(isMetric) {
				wheelView.setAdapter(new StrericWheelAdapter(kallist));
			}else {
				wheelView.setAdapter(new StrericWheelAdapter(kallist_unit));
			}
			
				wheelView.setCurrentItem(Utils.toint(SharedPreUtil.readPre(getApplicationContext(),
					SharedPreUtil.USER,
					SharedPreUtil.KALGOAL_NUMBER, "1")));
			
			break;
		}
		wheelView.setCyclic(false);
		wheelView.setInterpolator(new AnticipateOvershootInterpolator());
		view.findViewById(R.id.btnCancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mPopupWindow != null && mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
					}
				});
		view.findViewById(R.id.btnConfirm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mPopupWindow != null && mPopupWindow.isShowing()) {
							switch (s) {
							case 0:
								if (wheelView.getCurrentItem() == 0) {
									// 打开另一个pop
									showcustompop(popv, s);

									mPopupWindow.dismiss();

								} else {

									SharedPreUtil.savePre(
											getApplicationContext(),
											SharedPreUtil.USER,
											SharedPreUtil.DISTANCEGOAL_NUMBER,
											wheelView.getCurrentItem() + "");
									SharedPreUtil.savePre(
											getApplicationContext(),
											SharedPreUtil.USER,
											SharedPreUtil.DISTANCEGOAL,
											distancelist[wheelView.getCurrentItem()].replace(getString(R.string.kilometer),"")); //将单位：公里替换掉
									if(isMetric) {
										tv_distancegoal_settingvalue.setText(distancelist[wheelView.getCurrentItem()]);
									}else{
										tv_distancegoal_settingvalue.setText(distancelist_unit[wheelView.getCurrentItem()]);
									}

									Intent intent = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
									BTNotificationApplication.getInstance().sendBroadcast(intent);

								}

								break;
							case 1:
								if (wheelView.getCurrentItem() == 0) {
									// 打开另一个pop
									showcustompop(popv, s);

									mPopupWindow.dismiss();
								} else {

									SharedPreUtil.savePre(
											getApplicationContext(),
											SharedPreUtil.USER,
											SharedPreUtil.TIMEGOAL_NUMBER,
											wheelView.getCurrentItem() + "");
									SharedPreUtil
											.savePre(getApplicationContext(),
													SharedPreUtil.USER,
													SharedPreUtil.TIMEGOAL,
													timelist[wheelView.getCurrentItem()].replace(getString(R.string.everyday_show_unit), "")); //替换掉时间的单位
									tv_timegoal_settingvalue.setText(timelist[wheelView.getCurrentItem()]);
									Intent intent = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
									BTNotificationApplication.getInstance().sendBroadcast(intent);
								}

								break;
							case 2:
								if (wheelView.getCurrentItem() == 0) {
									// 打开另一个pop
									showcustompop(popv, s);

									mPopupWindow.dismiss();

								} else {

									SharedPreUtil.savePre(
											getApplicationContext(),
											SharedPreUtil.USER,
											SharedPreUtil.KALGOAL_NUMBER,
											wheelView.getCurrentItem() + "");
									SharedPreUtil
											.savePre(getApplicationContext(),
													SharedPreUtil.USER,
													SharedPreUtil.KALGOAL,
													kallist[wheelView.getCurrentItem()].replace(getString(R.string.calories), ""));
									if(isMetric) {
										tv_kalgoal_settingvalue.setText(kallist[wheelView.getCurrentItem()]);
									}else {
										tv_kalgoal_settingvalue.setText(kallist_unit[wheelView.getCurrentItem()]);
									}
									Intent intent = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
									BTNotificationApplication.getInstance().sendBroadcast(intent);
								}

								break;
							default:
								break;
							}
					//		setbroadcast();
							mPopupWindow.dismiss();
						}
					}
				});

		mPopupWindow = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
		mPopupWindow.showAtLocation(popv, Gravity.BOTTOM, 0, 0);

		
	}
	
	
	private void showcustompop(View popv, final int s) {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_towmenu, null);
		wheell = (WheelView) view.findViewById(R.id.wheell);
		wheelr = (WheelView) view.findViewById(R.id.wheelr);
		switch (s) {
		case 0:
			wheelr.setVisibility(View.VISIBLE);
			if(isMetric) {
				wheelr.setAdapter(new StrericWheelAdapter(distancecustomr));
				wheell.setAdapter(new StrericWheelAdapter(distancecustoml));
			}else{
				wheelr.setAdapter(new StrericWheelAdapter(distancecustomr_unit));
				wheell.setAdapter(new StrericWheelAdapter(distancecustoml_unit));
			}
			break;
		case 1:
			wheell.setAdapter(new StrericWheelAdapter(timecustoml));
			wheelr.setVisibility(View.VISIBLE);
			wheelr.setAdapter(new StrericWheelAdapter(timecustomr));

			break;
		case 2:
			if(isMetric) {
				wheell.setAdapter(new StrericWheelAdapter(kalcustom));
			}else{
				wheell.setAdapter(new StrericWheelAdapter(kalcustom_unit));
			}
			wheelr.setVisibility(View.GONE);
			if(isMetric) {
				wheelr.setAdapter(new StrericWheelAdapter(kalcustom));
			}else{
				wheelr.setAdapter(new StrericWheelAdapter(kalcustom_unit));
			}
			break;

		}
		wheell.setCyclic(false);
		wheell.setInterpolator(new AnticipateOvershootInterpolator());
		wheelr.setCyclic(false);
		wheelr.setInterpolator(new AnticipateOvershootInterpolator());
		view.findViewById(R.id.btnCancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mtowPopupWindow != null && mtowPopupWindow.isShowing()) {
							mtowPopupWindow.dismiss();
						}
					}
				});
		view.findViewById(R.id.btnConfirm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mtowPopupWindow != null && mtowPopupWindow.isShowing()) {
							switch (s) {
							case 0:
								SharedPreUtil.savePre(getApplicationContext(),
										SharedPreUtil.USER,
										SharedPreUtil.DISTANCEGOAL_NUMBER, "0");
								if(isMetric) {
									SharedPreUtil
											.savePre(
													getApplicationContext(),
													SharedPreUtil.USER,
													SharedPreUtil.DISTANCEGOAL,
													distancecustoml[wheell.getCurrentItem()] + distancecustomr[wheelr.getCurrentItem()].replace(getString(R.string.kilometer), ""));    // .replace(getString(R.string.kilometer),"")); //将单位：公里替换掉
									tv_distancegoal_settingvalue.setText(distancecustoml[wheell.getCurrentItem()] + distancecustomr[wheelr.getCurrentItem()]);
								}else{
									double mi = Double.parseDouble(distancecustoml_unit[wheell.getCurrentItem()] + distancecustomr_unit[wheelr.getCurrentItem()].replace(getString(R.string.unit_mi), ""));
									String km = Utils.decimalTo2(Utils.getUnit_km_mi(mi),1) + "";
									SharedPreUtil
											.savePre(
													getApplicationContext(),
													SharedPreUtil.USER,
													SharedPreUtil.DISTANCEGOAL,
													km);
									tv_distancegoal_settingvalue.setText(distancecustoml_unit[wheell.getCurrentItem()] + distancecustomr_unit[wheelr.getCurrentItem()]);
								}

								Intent intent = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
								BTNotificationApplication.getInstance().sendBroadcast(intent);
								break;
							case 1:
								if (wheell.getCurrentItem() + wheelr.getCurrentItem() == 0) {
									Toast.makeText(getApplicationContext(), getString(R.string.motionsettting_customtimeofzero), Toast.LENGTH_SHORT).show();
									return;
								}
								SharedPreUtil.savePre(getApplicationContext(),
										SharedPreUtil.USER,
										SharedPreUtil.TIMEGOAL_NUMBER, "0");
								SharedPreUtil.savePre(
										getApplicationContext(),
										SharedPreUtil.USER,
										SharedPreUtil.TIMEGOAL,
										(wheell.getCurrentItem()) * 60 + wheelr.getCurrentItem() * 5 + "");  //  + getString(R.string.everyday_show_unit)       .replace(getString(R.string.everyday_show_unit),"")
								tv_timegoal_settingvalue.setText(wheell.getCurrentItem() * 60 + wheelr.getCurrentItem() * 5 + getString(R.string.everyday_show_unit));

								Intent intent2 = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
								BTNotificationApplication.getInstance().sendBroadcast(intent2);
								break;
							case 2:

								SharedPreUtil.savePre(getApplicationContext(),
										SharedPreUtil.USER,
										SharedPreUtil.KALGOAL_NUMBER, "0");
								if(isMetric) {
									SharedPreUtil.savePre(getApplicationContext(),
											SharedPreUtil.USER,
											SharedPreUtil.KALGOAL,
											kalcustom[wheell.getCurrentItem()].replace(getString(R.string.calories), ""));
									tv_kalgoal_settingvalue.setText(kalcustom[wheell.getCurrentItem()]);
								}else {
									double kj = Double.parseDouble(kalcustom_unit[wheell.getCurrentItem()].replace(getString(R.string.unit_kj), ""));
									String kcal = Utils.decimalTo2(Utils.getUnit_kal_kj(kj),2) + "";
									SharedPreUtil.savePre(getApplicationContext(),
											SharedPreUtil.USER,
											SharedPreUtil.KALGOAL,
											kcal);
									tv_kalgoal_settingvalue.setText(kalcustom_unit[wheell.getCurrentItem()]);
								}

								Intent intent3 = new Intent(MainService.ACTION_SPORTMODE_HINT); //todo --- 发广播更新 弹框的标志位
								BTNotificationApplication.getInstance().sendBroadcast(intent3);
								break;
							default:
								break;
							}
						//	setbroadcast();
							mtowPopupWindow.dismiss();
						}
					}
				});

		mtowPopupWindow = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		mtowPopupWindow.setFocusable(true);
		mtowPopupWindow.setOutsideTouchable(true);
		mtowPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
		mtowPopupWindow.showAtLocation(popv, Gravity.BOTTOM, 0, 0);


	}
	
	/*
	public void setbroadcast() {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(MainService.ACTION_MOTION_GOAL_CHANGE);
		sendBroadcast(broadcastIntent);
	}*/
	
	
}
