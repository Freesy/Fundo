package com.szkct.weloopbtsmartdevice.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.adapter.ViewPagerAdapter;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.bluetoothgyl.UtilsLX;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.main.AllDataActivity;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.LoadingDialog;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.SqliteControl;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.dao.query.Query;

import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.CONNECT_FAIL;
import static com.szkct.weloopbtsmartdevice.main.MainService.HEART;
import static com.szkct.weloopbtsmartdevice.main.MainService.ISSYNWATCHINFO;
import static com.szkct.weloopbtsmartdevice.main.MainService.SLEEP;

//import cn.sharesdk.demo.wxapi.onekeyshare.OnekeyShare;

public class AnalysisFragment extends Fragment implements OnPageChangeListener,
		OnClickListener {
	private static final String TAG = "AnalysisFragment";
	ViewPager vp;
	private List<View> views;
	private ViewPagerAdapter vpAdapter;
	private View homeView, analysis,sleep_analysis, sleeppage;
	// private ColumnChartView chartView;
	private SqliteControl sc;
	private ArrayList<RunData> arrRunDataWeek = null;
	private ArrayList<SleepData> arrSleepWeek = null;

	private List<Integer> barValues;
	private String[] weeks = new String[7];
	private String[] dayOfWeek;   // dayOfWeekForX2
	private String[] dayOfWeekForX2;
	private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat getDateFormatGjd = Utils.setSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar calendar;
	private String select_date;
	private int showLines = 0;
	private TextView average_tv, total_tv;
	private int totalStep = 0;
	private int totalsleep = 0;
	private ImageButton synchronization;
	private ImageButton analysis_change_ib,sleep_analysis_change_ib;
	// private RadioButton sleepRadioBtn;
	private ImageButton ib_navigation_share;
	private Bluttoothbroadcast vb;

	private DBHelper db = null;
	private TextView cb_bigdata, cb_healthy, bigdata_null;

	private ColumnChartView sportsAnalysisView = null;
	private ColumnChartView sleepAnalysisView = null;
	
	private TextView sleep_average_tv, sleep_total_tv;
	private Toast toast = null;
	private SimpleDateFormat format = Utils.setSimpleDateFormat("yyyy-MM-dd HH");

	private long syncStartTime = 0;
//	public Dialog dialog;

	private  LoadingDialog loadingDialog = null;

	private boolean isRunning = false;

	// 注册广播的方法
	private void registerBroad() {
		if(null == vb){
			vb = new Bluttoothbroadcast();
		}

		IntentFilter viewFilter = new IntentFilter();
		viewFilter.addAction(MainService.ACTION_SYNFINSH);   //大数据页面 注册 手表数据同步 成功的 广播
		viewFilter.addAction(MainService.ACTION_MACCHANGE);
		viewFilter.addAction(MainService.ACTION_USERDATACHANGE);
		viewFilter.addAction(MainService.ACTION_SYNNOTDATA);

		viewFilter.addAction(MainService.ACTION_CHANGE_WATCH);

		viewFilter.addAction("android.intent.action.DATE_CHANGED");

		viewFilter.addAction(MainService.ACTION_SYNFINSH_SUCCESS);

		getActivity().registerReceiver(vb, viewFilter);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onServiceEventMainThread(MessageEvent event) {
		if(null!=event.getMessage()){
			if(CONNECT_FAIL.equals(event.getMessage())){
				if(null != loadingDialog && loadingDialog.isShowing()){
					loadingDialog.dismiss();
					loadingDialog = null;
				}
			} else if("update_view".equals(event.getMessage())){
				initView();
			} else if("unBond".equals(event.getMessage())){
				initView();
			}
		}
	}

	private void initView(){
		if(views == null){
			views = new ArrayList<>();
		}else{
			views.clear();
		}
		views.add(analysis);
		if(ISSYNWATCHINFO){
			if(SLEEP){
				bigdata_null.setVisibility(View.VISIBLE);
				cb_healthy.setVisibility(View.VISIBLE);
				views.add(sleeppage);
			}else{
				bigdata_null.setVisibility(View.INVISIBLE);
				cb_healthy.setVisibility(View.INVISIBLE);
			}
		}else {
			views.add(sleeppage);
		}
		settitileText(vp.getCurrentItem() * 2);
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
	}

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

				case 7:
					if(null != loadingDialog && loadingDialog.isShowing()){
						loadingDialog.dismiss();
						loadingDialog =  null;
					}
					break;

			case 0:
				initData();
				break;
		
			case 5:
				if (null != loadingDialog) {
					if(loadingDialog.isShowing()){
						loadingDialog.setCancelable(true);
						loadingDialog.dismiss();
						loadingDialog =  null;
					}
					try {
						if(mHandler!=null){
							mHandler.removeCallbacks(runnable);  
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 同步成功

					String mac = SharedPreUtil.readPre(getActivity(),
							SharedPreUtil.USER, SharedPreUtil.MAC);
					if (!SharedPreUtil.readPre(getActivity(),
							SharedPreUtil.USER, SharedPreUtil.ALLMAC).contains(
							mac)) {
						if (SharedPreUtil.readPre(getActivity(),
								SharedPreUtil.USER, SharedPreUtil.ALLMAC)
								.equals("")) {
							SharedPreUtil.savePre(getActivity(),
									SharedPreUtil.USER, SharedPreUtil.ALLMAC,
									mac);
						} else {
							SharedPreUtil.savePre(
									getActivity(),
									SharedPreUtil.USER,
									SharedPreUtil.ALLMAC,
									SharedPreUtil.readPre(getActivity(),
											SharedPreUtil.USER,
											SharedPreUtil.ALLMAC)
											+ "nozuomi" + mac);
						}
					}
				}
				break;
			case 6:
				if (null != loadingDialog) {
					if(loadingDialog.isShowing()){
						loadingDialog.setCancelable(true);
						loadingDialog.dismiss();
						loadingDialog =  null;
					}
//					if(mHandler!=null){
//						mHandler.removeCallbacks(runnable);
//					}
				}
				Toast.makeText(getActivity(), getString(R.string.userdata_synerror), Toast.LENGTH_SHORT).show();
				MainService.getSyncDataNumInService = 0;
				BTNotificationApplication.isSyncEnd = true;   
				break;
				
			default:
				break;
			}
		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (null != loadingDialog) {
				if (System.currentTimeMillis() - syncStartTime > 60 * 1000) {  // 90 * 1000
					Message msg = mHandler.obtainMessage(6);
					mHandler.sendMessage(msg);
					return ;
				}
			}else{
				return ;
			}
		}
			
	};

	public static AnalysisFragment newInstance(String title) {
		AnalysisFragment fragment = new AnalysisFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		homeView = inflater.inflate(R.layout.fragment_analysis, container,
				false);
		//当前不是处于固件升级模式可同步数据
//		if(!(boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {  // 防止S8等三星机器，固件升级过程中无法显示UI
//			dialog= CustomProgress.show(getActivity(), getString(R.string.userdata_synchronize), null);
		EventBus.getDefault().register(this);
			init();
			initsleepData();   // 初始化睡眠数据
			initData();			// 初始化运动数据
//		}
		registerBroad();

		OnekeyShare.isShowShare = true;   // todo --- 页面初始化时
		
		return homeView;
	}
	
	private void initData() {  // 初始化运动数据
		showLines = 0;
		totalStep = 0;
		calendar = Calendar.getInstance();
		// TODO
		barValues = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			select_date = getDateFormat.format(calendar.getTime());
			if (i == 0) {
				weeks[i] = getString(R.string.today);  // 今天
			} else {
				weeks[i] = dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
			}
			int dayStepNum = 0;
			Random random = new Random();
			String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);
			if (watch.equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {  // i == 0 && watch.equals("2")
				arrRunDataWeek = judgmentRunDB(select_date);   // 一周的步数统计   （从本地数据库获取到一周的运动数据）   RunData

				////todo --- 添加数据是否重复的判断   RunData
				if (arrRunDataWeek.size() != 0) {

					if(arrRunDataWeek.size() == 1 && StringUtils.isEmpty(arrRunDataWeek.get(0).getBinTime())){
						dayStepNum += Utils.toint(arrRunDataWeek.get(0).getStep());
					}else {
						String mrunBinTime = "";
						List<RunData> listok = new ArrayList<>();
						for (int j = 0; j < arrRunDataWeek.size(); j++) {
							if(!arrRunDataWeek.get(j).getBinTime().equals(mrunBinTime)) {
								mrunBinTime = arrRunDataWeek.get(j).getBinTime();
								if(listok.size() > 0){
									boolean isExist = false;
									for(RunData data:listok){
										if(data.getBinTime().equals(arrRunDataWeek.get(j).getBinTime())){
											isExist = true;
											break;
										}
									}
									if(!isExist){
										listok.add(arrRunDataWeek.get(j));
									}
								}else {
									listok.add(arrRunDataWeek.get(j));
								}
							}
//						dayStepNum += Utils.toint(arrRunDataWeek.get(j).getStep());
						}

						if(listok.size() > 0 ){
							for (int j = 0; j < listok.size(); j++) {
								dayStepNum += Utils.toint(listok.get(j).getStep());
							}
						}
					}

					if(dayStepNum > 200000){
						dayStepNum = 52007;
					}else if(dayStepNum < 0){
						dayStepNum = 0;
					}
					barValues.add(dayStepNum);  // 对应的每一天的步数  ----- 柱状图条目的值
					totalStep += dayStepNum;   // 一周的总步数
					if (showLines < dayStepNum) {
						showLines = dayStepNum;
					}
				} else {
					barValues.add(0);
					totalStep += dayStepNum;
					if (showLines < dayStepNum) {
						showLines = dayStepNum;
					}
				}
				calendar.add(Calendar.DATE, -1);

			}else{
				if (watch.equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
					arrRunDataWeek = jumpRunData(select_date);
					if (arrRunDataWeek.size() != 0) {
						for (int j = 0; j < arrRunDataWeek.size(); j++) {
							if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {
								if (i == 0) {
									dayStepNum += Utils.toint(arrRunDataWeek.get(j).getDayStep());
								} else {
									dayStepNum += Utils.toint(arrRunDataWeek.get(j).getStep());
								}
							} else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")  ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){
								dayStepNum += Utils.toint(arrRunDataWeek.get(j).getDayStep());
							} else{
								dayStepNum += Utils.toint(arrRunDataWeek.get(j).getStep());
							}
						}

						if(dayStepNum > 200000){
							dayStepNum = 52007;
						}else if(dayStepNum < 0){
							dayStepNum = 0;
						}

						barValues.add(dayStepNum);  // 对应的每一天的步数  ----- 柱状图条目的值
						totalStep += dayStepNum;   // 一周的总步数
						if (showLines < dayStepNum) {
							showLines = dayStepNum;
						}
					} else {
						barValues.add(0);
						totalStep += dayStepNum;
						if (showLines < dayStepNum) {
							showLines = dayStepNum;
						}
					}
				} else {
					arrRunDataWeek = judgmentRunDB(select_date);   // 一周的步数统计   （从本地数据库获取到一周的运动数据）
					if (arrRunDataWeek.size() != 0) {
						for (int j = 0; j < arrRunDataWeek.size(); j++) {
							dayStepNum += Utils.toint(arrRunDataWeek.get(j).getStep());

//							if(j == 0){
//								dayStepNum += Utils.toint("10");   // 测试用
//							}
						}

						if(dayStepNum > 200000){
							dayStepNum = 52007;
						}else if(dayStepNum < 0){
							dayStepNum = 0;
						}

						barValues.add(dayStepNum);  // 对应的每一天的步数  ----- 柱状图条目的值
						totalStep += dayStepNum;   // 一周的总步数
						if (showLines < dayStepNum) {
							showLines = dayStepNum;
						}
					} else {
						barValues.add(0);
						totalStep += dayStepNum;
						if (showLines < dayStepNum) {
							showLines = dayStepNum;
						}
					}
				}
				calendar.add(Calendar.DATE, -1);
			}
//			showLines = showLines / 5000 + 2;   // 2 --- 5k
			showLines = showLines / 5000 + 3;  // 3 --- 10k
//			showLines = showLines / 5000 + 4; // 4 --- 15k
//			showLines = showLines / 5000 + 5; // 5 --- 20k
			average_tv.setText(totalStep / 7 + "");  // 平均步数赋值
			total_tv.setText(totalStep + "");          // 总步数赋值
			sportsAnalysisView.setDataToShow(showLines, barValues, weeks);
		}
	}

	private void initsleepData() {
		showLines = 0;
		totalsleep = 0;
		calendar = Calendar.getInstance();

		calendar.add(Calendar.DATE, -1);

		barValues = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			select_date = getDateFormat.format(calendar.getTime());  // 2017-04-06
			if (i == 0) {
				weeks[i] = getString(R.string.today);
			} else {
				weeks[i] = dayOfWeekForX2[calendar.get(Calendar.DAY_OF_WEEK) - 1];
			}
			arrSleepWeek = judgmentSleepDB(select_date);  // 根据近一周日期 查询数据库
			//todo --- arrSleepWeek 两天的睡眠数据
				int dayStepNum = 0;
				Random random = new Random();
				if (arrSleepWeek.size() != 0) {
					// 当前天的 21点 的时间戳  前一天 9 点的时间戳
//					String strDate = curdatetv.getText().toString();  // 2017-04-09   2017-04-10    // TODO 开始日期   2017-04-09
					String choiceDate = arrangeDate(select_date); // 当前天日期
					String  strDate = choiceDate + " 21";
					Date endTimeDate;
					long endTime = 0;    // todo  当前日期的睡眠的结束时间
					Calendar calendar3 = Calendar.getInstance();
					Date startTimeDate;
					long startTime = 0;   // todo  当前日期的睡眠开始时间
					try {
						startTimeDate = format.parse(strDate);   // Fri Apr 07 08:00:00 GMT+08:00 2017     ---- 20170407 --- 08:00
						// 睡眠的开始时间   1491523200000     1491523200  -- 2017/4/7 8:0:0   //TODO --- 当前日期 的 21 点的时间戳    当前日期的开始时间 1491742800000
						startTime = startTimeDate.getTime()/1000;     // todo --- 当前日期的睡眠开始时间  1492606800 -- 2017/4/19 21:0:0
						calendar3.setTime(startTimeDate);
						calendar3.add(Calendar.DATE, +1);
						String start = getDateFormat.format(calendar3.getTime()).toString();  //TODO 这里通过getDateFormat 将日期转成了 年月日格式，下面 + 09 还是第2天的 9点 2017-04-06
						start = start + " 09";
						endTimeDate = format.parse(start);    // Thu Apr 06 21:00:00 GMT+08:00 2017   20170406 21:00:00
						endTime = endTimeDate.getTime()/1000;   // //TODO ---当前日期的睡眠结束时间   前一天日期的 9 点的时间戳    13位转为10位  1492650000 -- 2017/4/20 9:0:0
					} catch (ParseException e) {
						e.printStackTrace();
					}
					int lights = 0;
					int deepS = 0;

					///////////////////////////////////////////////////////////////////////////////////////////////////
					String msleepBinTime = "";
					List<SleepData> listok = new ArrayList<>();
					for (int j = 0; j < arrSleepWeek.size(); j++) {
						if(!arrSleepWeek.get(j).getStarttimes().equals(msleepBinTime)) {
							msleepBinTime = arrSleepWeek.get(j).getStarttimes();
							if(listok.size() > 0){
								boolean isExist = false;
								for(SleepData data:listok){
									if(data.getStarttimes().equals(arrSleepWeek.get(j).getStarttimes())){
										isExist = true;
										break;
									}
								}
								if(!isExist){
									listok.add(arrSleepWeek.get(j));
								}
							}else {
								listok.add(arrSleepWeek.get(j));
							}
						}
					}

					if(listok.size() > 0 ){
						for (int j = 0; j < listok.size(); j++) {
							String endTimeStr = listok.get(j).getEndTime(); // s数据库中结束时间  日期格式   2017-04-09 21:00:00
							Date date = StringUtils.parseStrToDate(endTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							long sleepDataEndTime = calendar.getTimeInMillis() / 1000;  //将日期格式转为时间戳 1489816800   TODO --- 每一条睡眠数据的 结束时间  10位时间戳    1491742800     ---- 2017/4/9 21:0:0    1491742800     1491656400--2017/4/8 21:0:0

							String startTimeStr = listok.get(j).getStarttimes(); // s数据库中结束时间  日期格式   2017-04-09 21:00:00
							Date date2 = StringUtils.parseStrToDate(startTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
							Calendar calendar2 = Calendar.getInstance();
							calendar2.setTime(date2);
							long sleepDataStartTime = calendar2.getTimeInMillis() / 1000;  //TODO  s数据库睡眠数据的 开始时间   1491707220   2017/4/9 11:7:0
							//todo  1： 结束时间点必须大于 21 点     这里取的是一天的有效数据
							if(sleepDataEndTime > startTime){  // TODO 当天睡眠数据的有效数据 应该 结束时间 >= 当天的21点 <= 后一天的 9点         1492606800
								// 结束时间 >= 21 点 但可以 超过 9 点 （超过9点时 --- 以9点分割点， 9点之前的为当天的有效睡眠数据 ， 9点之后的为无效的睡眠数据）   用结束时间 -   20170409 00:08:50     20170409 00:10:50   120
								if(sleepDataEndTime <= endTime){  // 全为有效数据   结束时间在 21点到9点之间
									if(sleepDataStartTime >= startTime){ // 开始时间 >= 21 点   即睡眠数据在 21:00 到 09:00 之间  ---- 全为有效数据
										deepS += Utils.toint(listok.get(j).getDeepsleep());   // 深睡时长
										lights += Utils.toint(listok.get(j).getLightsleep()); // 浅睡时长
									}else {  // 开始时间 在 21 点之前    ----统计一天的 睡眠
										long okSleeptime= sleepDataEndTime - startTime;  //21点之后的有效 秒数值     1491753600 -- 2017/4/10 0:0:0      1491753601  ---- 2017/4/10 0:0:1
										int okFenTime = (int)okSleeptime/60; // 有效的睡眠 分钟数   //TODO ????? okSleeptime 是否要加判断
										if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //72
											if(listok.get(j).getSleeptype().equals("0")){  // 深睡
												deepS += okFenTime;
											}else if(listok.get(j).getSleeptype().equals("1")){ // 浅睡    else if(arrSleep.get(i).getSleeptype().equals("1"))
												lights += okFenTime;
											}
										}else if(SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
												|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
											if(listok.get(j).getSleeptype().equals("1")){  // 深睡
												deepS += okFenTime;
											}else if(listok.get(j).getSleeptype().equals("2")){ // 浅睡    else if(arrSleep.get(i).getSleeptype().equals("1"))
												lights += okFenTime;
											}
										}
									}
								}else {    // 结束时间 > 9点    9点之后的为无效的睡眠数据）   用结束时间 -   20170409 00:08:50     20170409 00:10:50   120
									listok.get(j).getStarttimes();
									if(sleepDataStartTime >= endTime){   // 数据库的开始时间 比 当前睡眠的截止 还大  --- 当天无效睡眠数据
										continue;
									}else {
										long okSleeptime= endTime - sleepDataStartTime ;
										int okFenTime = (int)okSleeptime/60; // 有效的睡眠 分钟数
										if(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {  //72
											if(listok.get(j).getSleeptype().equals("0")){  // 深睡
												deepS += okFenTime;
											}else if(listok.get(j).getSleeptype().equals("1")){ // 浅睡    else if(arrSleep.get(i).getSleeptype().equals("1"))
												lights += okFenTime;
											}
										}else if(SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
											|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
											if(listok.get(j).getSleeptype().equals("1")){  // 深睡
												deepS += okFenTime;
											}else if(listok.get(j).getSleeptype().equals("2")){ // 浅睡    else if(arrSleep.get(i).getSleeptype().equals("1"))
												lights += okFenTime;
											}
										}
									}
								}
							}
						}

					dayStepNum = dayStepNum + deepS + lights;  // 一天的睡眠数据

						if(dayStepNum < 0){  //todo --- 防止睡眠数据出现负数
							dayStepNum = 0;
						}else if(dayStepNum > 720){
							dayStepNum = 720;
						}

						barValues.add(dayStepNum);
						if (showLines < dayStepNum) {
							showLines = dayStepNum;
						}  ////////////////////////////
					}
				} else {
					barValues.add(0);
					if (showLines < dayStepNum) {
						showLines = dayStepNum;
					}
				}

				totalsleep += dayStepNum;
				calendar.add(Calendar.DATE, -1);
//			}

			showLines = showLines /360 + 2;
			if (totalsleep % 60 / 6 == 0) {
				sleep_total_tv.setText(totalsleep / 60 + "h");
			} else {
				double d=totalsleep/60d;
				Log.e("d=======", d+"");
				sleep_total_tv.setText(Utils.setformat(2,d+"")+ "h");
			}
			int average=totalsleep / 7;
			if (average % 60 / 6 == 0) {
				sleep_average_tv.setText(average / 60 + "h");
			} else {
				double d=totalsleep/7d/60d;
				//average_tv.setText(average / 60 + "." + average % 60 / 6 + "h");
				sleep_average_tv.setText(Utils.setformat(2,d+"")+ "h");
			}
			sleepAnalysisView.setDataToShow(showLines, barValues, weeks);  // 填充 一周睡眠数据
			}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	private void init() {
		dayOfWeek = getActivity().getResources().getStringArray(R.array.day_of_week);

		dayOfWeekForX2 = getActivity().getResources().getStringArray(R.array.day_of_week_forx2);

		cb_bigdata = (TextView) homeView.findViewById(R.id.cb_bigdata);
		cb_healthy = (TextView) homeView.findViewById(R.id.cb_healthy);
		bigdata_null = (TextView) homeView.findViewById(R.id.bigdata_null);
		
		cb_bigdata.setOnClickListener(this);
		cb_healthy.setOnClickListener(this);
		bigdata_null.setOnClickListener(this);

		synchronization = (ImageButton) homeView.findViewById(R.id.tv_navigation_synchronization);
		synchronization.setOnClickListener(new OnClickListener() {  // 点击同步按钮 --- 同步 手表 计步，睡眠数据
			@Override
			public void onClick(View v) {
				if (isFastDoubleClick()) {
					return;
				}

				if((boolean) SharedPreUtil.getParam(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.ISFIRMEWARING, false)) {  // todo ---- 固件升级模式下，不同步数据
					return;
				}

				MainService service = MainService.getInstance();
				// 同步数据
				if (service.getState() != 3) {   // getInstance       service.getState() == 0 || service.getState() == 1
					Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
					return;
				}

				MainService.isShowToast = true;

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

				if(SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1")){    //TODO  72
					BTNotificationApplication.needSendDataType = 0;
					BTNotificationApplication.needReceDataNumber = 0;

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
						if(SLEEP){
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
						if(HEART){
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
				}else if(SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2")){  //TODO  手环  请求手环一周的计步历史数据
					syncStartTime = System.currentTimeMillis();
					sendSyncData(3);  // 计步
					sendSyncData(1);  // 睡眠    todo ----  分析页面同步数据时，也取心率和血压血氧的值
					sendSyncData(2);  // 心率
					sendSyncData(5);  // 血氧血压
//					sendSyncData(6);

					BTNotificationApplication.needReceiveNum = BTNotificationApplication.needGetSportDayNum + BTNotificationApplication.needGetSleepDayNum + BTNotificationApplication.needGetHeartDayNum; //todo ---- 需要获取的数据条数
					Log.e("liuxiaodata", "需要收到的数据条数为--" +  BTNotificationApplication.needReceiveNum);
					Log.e("liuxiaodata", "需要收到的计步数据条数为--" + BTNotificationApplication.needGetSportDayNum);
					Log.e("liuxiaodata", "需要收到的睡眠数据条数为--" + BTNotificationApplication.needGetSleepDayNum);
					Log.e("liuxiaodata", "需要收到的心率数据条数为--" + BTNotificationApplication.needGetHeartDayNum);

					BTNotificationApplication.isSyncEnd = false;   //TODO ---  开始同步数据将标志位 置为 false
				}else if(SharedPreUtil.readPre(getActivity(),SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")){
//					BluetoothMtkChat.getInstance().syncRun();
//					BluetoothMtkChat.getInstance().syncSleepDetail();

					BluetoothMtkChat.getInstance().getWathchData();    //获取手表数据
					BluetoothMtkChat.getInstance().syncRun();        //每天计步数据
					BluetoothMtkChat.getInstance().sendApkState();  //前台运行
//					BluetoothMtkChat.getInstance().syncEcg();
					BTNotificationApplication.isSyncEnd = false;   //TODO ---  开始同步数据将标志位 置为 false
				}
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(null == loadingDialog ){ //
					loadingDialog = new LoadingDialog(getActivity(),R.style.Custom_Progress, getString(R.string.userdata_synchronize));
					loadingDialog.show();
					mHandler.postDelayed(runnable, 1000 * 61);// 打开定时器，执行操作   1000 * 91
				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
		});

		ib_navigation_share = (ImageButton) homeView.findViewById(R.id.ib_navigation_share);  // 分享
		ib_navigation_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
//							if(Utils.isFastClick()){
								if(OnekeyShare.isShowShare){ // todo ---- 弹出分享框了
									OnekeyShare.isShowShare = false;
									showShare(MainService.PAGE_INDEX_ANALYSIS);
								}
//							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						isRunning = false;
					}
				}, 1400);
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
		});

		analysis = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_analysis_sport, null);  // 计步页面
		sleeppage = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_analysis_sleep, null);// 睡眠页面
		vp = (ViewPager) homeView.findViewById(R.id.test_vpager);  // 运动数据分析的页面装载

		views = new ArrayList<View>();

		views.add(analysis);
		views.add(sleeppage);
		 sportsAnalysisView=(ColumnChartView)analysis.findViewById(R.id.vp_sports_columnchartview);
		 sportsAnalysisView.setviewtype(ColumnChartView.COLUMN_CHAR_VIEW_TYPE_SPORTS);
		sleepAnalysisView = (ColumnChartView)sleeppage.findViewById(R.id.vp_sleep_columnchartview);
		sleepAnalysisView.setviewtype(ColumnChartView.COLUMN_CHAR_VIEW_TYPE_SLEEP);

		analysis.findViewById(R.id.clickrl).setOnClickListener(this);
		average_tv = (TextView) analysis.findViewById(R.id.analysis_average_step);    // 平均步数、平均时间   ----- 此页面只是一周的数据统计
		total_tv = (TextView) analysis.findViewById(R.id.analysis_total_step);     // 总步数、总时间
		average_tv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
		total_tv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
		
		analysis_change_ib = (ImageButton) analysis
				.findViewById(R.id.analysis_change_ib);
		analysis_change_ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(getActivity(), AllDataActivity.class);  // 计步一周页面到 一年页面
				mIntent.putExtra("sportorsleep", 0);  
				startActivity(mIntent);
			}
		});

		
		sleeppage.findViewById(R.id.clickrl).setOnClickListener(this);
		sleep_average_tv = (TextView) sleeppage.findViewById(R.id.analysis_average_step);
		sleep_total_tv = (TextView) sleeppage.findViewById(R.id.analysis_total_step);
		sleep_average_tv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
		sleep_total_tv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
		
		sleep_analysis_change_ib = (ImageButton) sleeppage.findViewById(R.id.analysis_change_ib);
		sleep_analysis_change_ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(getActivity(), AllDataActivity.class);   // 从睡眠一周数据页面跳转到一年的统计页面
				mIntent.putExtra("sportorsleep", 1);  
				startActivity(mIntent);
			}
		});
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);   // 将运动和睡眠的页面装载进来
		vp.setOnPageChangeListener(this);
	
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
	
	private ArrayList<SleepData> judgmentSleepDB(String choiceDate) {
		if (db == null) {
			db = DBHelper.getInstance(getActivity());
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////
//		String choiceDate = arrangeDate(choiceDate);  // 当前日期   // 获取当天的睡眠数据 ，必须往前 再取一天的睡眠数据     2017-04-10
		String endStrDate = "";
//        strDate = strDate + " 00";  //   2017-04-07 08
		Date startTimeDate;
		Calendar calendar = Calendar.getInstance();
		try {
			startTimeDate = getDateFormat.parse(choiceDate);  // 当前天的 日期格式
			calendar.setTime(startTimeDate);
			calendar.add(Calendar.DATE, +1);
			endStrDate = getDateFormat.format(calendar.getTime()).toString();  // 2017-04-06     2017-04-11   // TODO 结束日期
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String choiceDateEnd = arrangeDate(endStrDate); // 后一天的日期
		///////////////////////////////////////////////////////////////////////////////////////////////////////////

		Query query = null;
		if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
				SharedPreUtil.SHOWMAC).equals("")) {
			query = db
					.getSleepDao()
					.queryBuilder()
					// .where(SleepDataDao.Properties.Mid.eq(mid))
					.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
							getActivity(), SharedPreUtil.USER,
							SharedPreUtil.MAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
		} else {
			query = db
					.getSleepDao()
					.queryBuilder()
					// .where(SleepDataDao.Properties.Mid.eq(mid))
					.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
							getActivity(), SharedPreUtil.USER,
							SharedPreUtil.SHOWMAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
		}
		List listCur = query.list();  //获取当前天的睡眠数据

		Query queryEnd = null;
		if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
			queryEnd = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDateEnd)).build();
		} else {
			queryEnd = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDateEnd)).build();
		}
		List listNext = queryEnd.list();  // 获取后一天的 睡眠 数据

		List list = new ArrayList();
		list.addAll(listCur);
		list.addAll(listNext); // 将当前天和上一天的睡眠数据都添加


		// Log.e("judgmentSleepDB", list.size()+"==");
		ArrayList<SleepData> arrSleep = new ArrayList<SleepData>();
		if (list != null && list.size() >= 1) {
			
			for (int j = 0; j < list.size(); j++) {
				SleepData runDB = (SleepData) list.get(j);
				arrSleep.add(runDB);
			}
			
		}
		return arrSleep;
		
	}


	private ArrayList<RunData> jumpRunData(String choiceDate){   //TODO  ---  MTK取计步数据
		if (db == null) {
			db = DBHelper.getInstance(getActivity());
		}
		Query query = null;
		if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
				SharedPreUtil.SHOWMAC).equals("")) {    //不展示的设备的数据的mac地址。
			query = db
					.getRunDao()
					.queryBuilder()
							// .where(RunDataDao.Properties.Mid.eq(mid))
					.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
							getActivity(), SharedPreUtil.USER,
							SharedPreUtil.MAC)))
					.where(RunDataDao.Properties.Date.eq(choiceDate)).build();
//					.where(RunDataDao.Properties.Step.eq("0")).build();
		} else {
			query = db
					.getRunDao()
					.queryBuilder()
							// .where(RunDataDao.Properties.Mid.eq(mid))
					.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
							getActivity(), SharedPreUtil.USER,
							SharedPreUtil.SHOWMAC)))      //需要展示的设备的数据的mac地址。//后面被产品去掉
					.where(RunDataDao.Properties.Date.eq(choiceDate)).build();
//					.where(RunDataDao.Properties.Step.eq("0")).build();
		}
		List list = query.list();   //从本地数据库获取到一周的运动数据

		ArrayList<RunData> runData = new ArrayList<RunData>();
		if (list != null && list.size() >= 1) {
			for (int j = 0; j < list.size(); j++) {
				RunData runDB = (RunData) list.get(j);
				runData.add(runDB);
			}
		}
		return runData;
	}


	private ArrayList<RunData> judgmentRunDB(String choiceDate) { //TODO H872取计步数据
		if (db == null) {
			db = DBHelper.getInstance(getActivity());
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		ArrayList<RunData> runData = new ArrayList<RunData>();
		String realTime =  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME);  // 实时的日期

		if(choiceDate.equals(simpleDateFormat.format(new Date())) && !StringUtils.isEmpty(realTime)  && realTime.equals(choiceDate)) {    // todo ---- 为当前天日期,且有当前的实时步数
				int synStep = 0;
				int realStep = 0;
				RunData runDB = new RunData();
				if (!StringUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {  //同步的步数
					synStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
				}
				if (!StringUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {    // 实时的步数
					realStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
				}
				if (synStep <= realStep) {
					runDB.setStep(realStep+"");
				}else{
					runDB.setStep(synStep+"");
				}
				runData.add(runDB);
		}else {
			Query query = null;
			if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
					SharedPreUtil.SHOWMAC).equals("")) {    //不展示的设备的数据的mac地址。
				query = db
						.getRunDao()
						.queryBuilder()
						// .where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
								getActivity(), SharedPreUtil.USER,
								SharedPreUtil.MAC)))
						.where(RunDataDao.Properties.Date.eq(choiceDate)).build();
			} else {
				query = db
						.getRunDao()
						.queryBuilder()
						// .where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
								getActivity(), SharedPreUtil.USER,
								SharedPreUtil.SHOWMAC)))      //需要展示的设备的数据的mac地址。//后面被产品去掉
						.where(RunDataDao.Properties.Date.eq(choiceDate)).build();
			}
			List list = query.list();   //从本地数据库获取到一周的运动数据

			if (list != null && list.size() >= 1) {
				for (int j = 0; j < list.size(); j++) {
					RunData runDB = (RunData) list.get(j);
					runData.add(runDB);
				}
			}
		}
		return runData;
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


	private String filePath = Environment.getExternalStorageDirectory()
			+ "/appmanager/fundoShare/";
	private String fileName = "screenshot_analysis.png";
	private String detailPath = filePath + File.separator + fileName;
	private void showShare(int pageIndex) {  //分享
		ScreenshotsShare.savePicture(ScreenshotsShare.takeScreenShot(getActivity(),pageIndex), filePath, fileName);
		//ShareSDK.initSDK(getActivity());
		mapPackageName=	setImage(getActivity());
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.app_name));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl("http://fundoshouhu.szkct.cn/funfit.html");
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
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
		} else {oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_qq)), "QQ", mobileqqclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_facebook)), "Facebook", facebookclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_instagram)), "Instagram", Instagramclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_twitter)), "Twitter", twitterclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_whatsapp)), "Whatsapp", whatsappclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_linkedin)), getString(R.string.linkedin), Linkedinclick);
			oks.setCustomerLogo(drawableToBitmap(getActivity().getDrawable(R.drawable.ssdk_oks_classic_strava)), "strava", stravaclick);
		}
		
		// 启动分享GUI
		oks.show(getActivity());
	}
	OnClickListener facebookclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			shareToFacebook();
		}
	};
	OnClickListener Instagramclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			shareToInstagram();
		}
	};
OnClickListener twitterclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			shareTotwitter();
		}
	};
OnClickListener whatsappclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			shareTowhatsapp();
		}
	};
OnClickListener Linkedinclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			shareToLinkedin();
		}
	};
OnClickListener mobileqqclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			shareTomobileqq();
			Utils.onClickShareToQQ(getActivity(),detailPath);
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
				actionShare_sms_email_facebook(packageName, getActivity(),  "");
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
				actionShare_sms_email_facebook(packageName, getActivity(),  "");
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
				actionShare_sms_email_facebook(packageName, getActivity(),  "");
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
				actionShare_sms_email_facebook(packageName, getActivity(),  "");
			} else {
				Toast.makeText(getActivity(), getString(R.string.no_whatsapp_in_your_phone), Toast.LENGTH_SHORT).show();
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
				actionShare_sms_email_facebook(packageName, getActivity(),  "");
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
				actionShare_sms_email_facebook(packageName, getActivity(),  "");
			} else {
				Toast.makeText(getActivity(), getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getActivity(), getString(R.string.no_mobileqq_in_your_phone), Toast.LENGTH_SHORT).show();
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
				boolean isAdd = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE",packageName));
				if(!isAdd){
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
	public  void actionShare_sms_email_facebook(String packageName, Activity activity, String shareText ){
//		String fileName = "rideSummary_" + datetime_start + ".png";
//		File file = new File("/storage/emulated/0/DCIM/Camera/1411099620786.jpg");
		Intent intent=new Intent(Intent.ACTION_SEND);   
		intent.setType("image/jpg");   
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

//		intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
//		intent.putExtra(Intent.EXTRA_STREAM, DatabaseProvider.queryScreenshot(activity, datetime.getTime()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		intent.setPackage(packageName);
		activity.startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
		System.out.println("****3");
	}
	public Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
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
		settitileText(arg0*2);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bigdata_null:
			
			if (bigdata_null.getText().toString().equals("")) {
				return;
			}
			 vp.setCurrentItem(0);
			break;
		case R.id.cb_bigdata:
			// settitileText(1);
			// vp.setCurrentItem(0);
			break;

		case R.id.cb_healthy:
			if (cb_healthy.getText().toString().equals("")) {
				return;
			}
			 vp.setCurrentItem(1);
			
			break;
	
		default:
			break;
		}
	}




	

	private void settitileText(int x) {
		switch (x) {
		case 0:
			
				bigdata_null.setText("");
				cb_bigdata.setText(getActivity().getString(R.string.steps_text));
				cb_healthy.setText(getActivity().getString(R.string.data_sleep));
				
			
			break; 
		case 1:
			break;
		case 2:
			
				bigdata_null.setText(getActivity().getString(R.string.steps_text));
				cb_bigdata.setText(getActivity().getString(R.string.data_sleep));
				cb_healthy.setText("");
				
			
			break;
		default:
			break;
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public class Bluttoothbroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.e("onReceive", action);
			if (MainService.ACTION_SYNFINSH.equals(action)) {  //大数据页面 注册 手表数据同步 成功的 广播
				String stepNum = intent.getStringExtra("step");

				if(null == loadingDialog ){   //todo ---  蓝牙连上的时候，同步数据（TMK平台）    && SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")
					if(stepNum.equals("6")){
						Log.e("liuxiaodata", "收到6广播");
						initData();
						initsleepData();
					}//////////////////////////////
				}else if(null != loadingDialog && !StringUtils.isEmpty(stepNum)){
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
						msg.what = 7;
						mHandler.sendMessageDelayed(msg, 1000);
//						Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.userdata_synchronize_success), Toast.LENGTH_SHORT).show();
						if(mHandler!=null){
							mHandler.removeCallbacks(runnable);
						}

						initData();
						initsleepData();
					}
				}

			}

			if (MainService.ACTION_CHANGE_WATCH.equals(action) || "android.intent.action.DATE_CHANGED".equals(action)) {    //TODO --- 实时的广播不销毁加载框
				initsleepData();
				initData();
			}

			if (MainService.ACTION_SYNFINSH_SUCCESS.equals(action)) {  // 实时计步数据
				initData();
			}



			if (MainService.ACTION_MACCHANGE.equals(action)) {
					initsleepData();
					initData();
			}
			if(MainService.ACTION_SYNNOTDATA.equals(action)){
				if (null != loadingDialog && loadingDialog.isShowing()) {
					loadingDialog.setCancelable(true);
					loadingDialog.dismiss();
					loadingDialog =  null;
					if(mHandler!=null){
						mHandler.removeCallbacks(runnable);
					}
				}
				Toast.makeText(getActivity(),getString(R.string.now_is_null_syn),Toast.LENGTH_SHORT).show();
			}
		}
	}
	public  void showdata(int position) {
		
		if(vp!=null){
			vp.setCurrentItem(position,false);
		}
		
	}
	public void onDestroyView() {
		super.onDestroyView();

		EventBus.getDefault().unregister(this);
		if(vb != null){
			getActivity().unregisterReceiver(vb);
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

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("AnalysisFragment");

		String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);  //mac 应该不会空，会一直保存最近连接的设备的mac
		if(!StringUtils.isEmpty(curMacaddress)){
			Calendar calendar = Calendar.getInstance();   // 当前第一天的日期  2017-06-28
			calendar.setTime(new Date());
			String  mcurDate = getDateFormat.format(calendar.getTime());  //当前系统的日期
			String Last7DayDate = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE);
			if(!StringUtils.isEmpty(Last7DayDate) && Last7DayDate.equals(mcurDate) && !StringUtils.isEmpty(curMacaddress)){
				SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0:"+curMacaddress);//TODO ---将取7取过7天数据的标志重置为0--- 没有取过 7 天的数据
			}
		}

//		registerBroad();


	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("AnalysisFragment");

//		if (vb != null && ((MainActivity)getActivity()).isScreenOn) {
//			getActivity().unregisterReceiver(vb);
//		}
	}

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


	public void sendSyncData(int index){
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

			if(!isHasLast6DaySportData){ // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
				String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
				String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
				String[] oldRecords = new String[2];
				if(!StringUtils.isEmpty(isSync7DaysData)){
					oldRecords = isSync7DaysData.split("#");
				}

				if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天 2017-09-25    2017-10-02    ----- 将此日期 保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					Log.e(TAG, "当前日期的后7天日期为 ---- " + last7Day);
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

					sendLast7DaysData(3);

				}else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

					sendLast7DaysData(3);
				}else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
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
					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
					sendLast7DaysData(1);
				}else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期
					sendLast7DaysData(1);
				}else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
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
//            }

			if(!isHasLast6DayHeartData){ // TODO --- 前6天没有数据 --- 取前6天的数据 (包括当前天)
				String isSync7DaysData = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED);  //是否同步过7天的设备--- 根据mac地址
				String curMacaddress=SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
				String[] oldRecords = new String[2];
				if(!StringUtils.isEmpty(isSync7DaysData)){
					oldRecords = isSync7DaysData.split("#");
				}

				if(StringUtils.isEmpty(isSync7DaysData)){      //todo   0--- 没有取过 7 天的数据
					//TODO ---- 切换设备时，比较mac 地址        // todo -- 还添加 mac 地址来 保存 区分
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

					sendLast7DaysData(2);

				}else if(oldRecords[0].equals("0")){        //todo   0--- 没有取过 7 天的数据
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
					calendar.add(Calendar.DAY_OF_MONTH, 7);  //设置为后7天
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					String last7Day = sdf2.format(calendar.getTime());//获得后7天     2017-09-29    ----- 将此日期保存本地，当前主页的当前日期为  2017-09-29 时，将 同步7天的标志位 置为  0
					Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.LAST7DAY, SharedPreUtil.LAST7DAY_DATE, last7Day);// 保存后7天开始日期

					sendLast7DaysData(2);
				}else if(oldRecords[0].equals("1") && !oldRecords[1].equals(curMacaddress)){  // todo -- 取过7天的数据，但不是当前设备
					SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.ISFIRSTSYNCDATA, SharedPreUtil.SYNCED, "0#"+curMacaddress);// 0--- 没有取过 7 天的数据

					//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
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

				//将后7天的日期保存到本地，当app端系统日期为 前一次保存的后7天的日期时，再次需要同步7天的数据
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
				Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
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
				Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
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
				Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
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
				Log.e(TAG, "当前日期的后6天日期为 ---- " + last7Day);
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
	}
}
