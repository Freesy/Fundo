package com.szkct.weloopbtsmartdevice.main;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.RequestCallBackEx;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.SqliteControl;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.HorizontalChartView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.Query;

public class AllDataActivity extends Activity implements OnCheckedChangeListener {

	private TextView mAverageTv, mTotalStepTv, mGoalAchievementTv, mTotalDisTv,mAverageText, mTotalStepText, mGoalAchievementText, mTotalDisText;
	private HorizontalChartView mHorizontalChartView;
	private RadioGroup mStepGroup;
	private ImageButton mFullscreenIb;
	private Calendar calendar;

	private Calendar calendarXiabiao;
	private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat getMonthFormat =Utils.setSimpleDateFormat("yyyy-M");
	private SimpleDateFormat getYYMMFormat = Utils.setSimpleDateFormat("yyyy-MM");
	private SimpleDateFormat dateFormat = Utils.setSimpleDateFormat("M.dd");
	private SimpleDateFormat monthFormat = Utils.setSimpleDateFormat("MM");
	private SqliteControl sc;
	
	private int sportorsleep = 0;
	private int change = 0;
	private String select_date;
	private int linesNum, totalStep, goalstepcount, achievementCount;
	private List<Integer> barValues;
	private String[] coordinates;
	private ArrayList<RunData> arrRunData;
	private ArrayList<RunData> arrRunDataWeek;
	private ArrayList<RunData> arrRunDataOne;
	private ArrayList<RunData> arrRunDataMonth;
	private ArrayList<SleepData> arrSleepData;
	private ArrayList<SleepData> arrSleepDataWeek;
	private ArrayList<SleepData> arrSleepDataOne;
	private ArrayList<SleepData> arrSleepDataMonth;
	private DBHelper db = null;

	private Toast toast = null;
	private int sleepmin;
	private int deepsleepmin;
	private int deepsleepminall;
	private SimpleDateFormat format = Utils.setSimpleDateFormat("yyyy-MM-dd HH");

	private final static String TAG = AllDataActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
			setTheme(R.style.KCTStyleWhite);
		} else {
			setTheme(R.style.KCTStyleBlack);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_alldata);
		Intent intent = getIntent();
		//从Intent当中根据key取得value
		sportorsleep = intent.getIntExtra("sportorsleep", 0);
		init();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.alldata_one_rb:    // 一个月的数据
			change = 0;
			if(sportorsleep==0){
				showData();   // 计步的数据
			}else{
				showDatasleep();  // 睡眠的数据
			}
			
			break;
			
		case R.id.alldata_six_rb:   // 6个月的数据
			change = 1;
			if(sportorsleep==0){
				showData();
			}else{
				showDatasleep();
			}
			break;
			
		case R.id.alldata_all_rb:   // 一年的数据
			change = 2;
			if(sportorsleep==0){
				showData();
			}else{
				showDatasleep();
			}
			break;
			
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(sportorsleep==0){
			showData();
		}else{
			showDatasleep();
		}
	}
	
	private void init() {
		mAverageTv = (TextView) findViewById(R.id.alldata_average_tv);
		mTotalStepTv = (TextView) findViewById(R.id.alldata_totalstep_tv);
		mGoalAchievementTv = (TextView) findViewById(R.id.alldata_goal_tv);
		mTotalDisTv = (TextView) findViewById(R.id.alldata_totaldis_tv);
		mAverageText = (TextView) findViewById(R.id.alldata_average_text);
		mTotalStepText = (TextView) findViewById(R.id.alldata_totalstep_text);
		mGoalAchievementText = (TextView) findViewById(R.id.alldata_goal_text);
		mTotalDisText = (TextView) findViewById(R.id.alldata_totaldis_text);
		mStepGroup = (RadioGroup) findViewById(R.id.alldata_headstep_rg);
		mHorizontalChartView = (HorizontalChartView) findViewById(R.id.alldata_step_chartview);
		mFullscreenIb = (ImageButton) findViewById(R.id.alldata_fullscreen_ib);
		mFullscreenIb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mStepGroup.setOnCheckedChangeListener(this);
		if(sportorsleep==1){
			mAverageText.setText(getString(R.string.total_duration));
			mTotalStepText.setText(getString(R.string.long_shallow_sleep));
			mGoalAchievementText.setText(getString(R.string.deep_sleep_length));
			mTotalDisText.setText(getString(R.string.average_sleep));
		}
	}

	private void showData() {
		linesNum = 0;
		totalStep = 0;
		int totalStep=0;
		float distance= 0;
		achievementCount=0;
		calendar = Calendar.getInstance();
		barValues = new ArrayList<Integer>();
		arrRunDataWeek = new ArrayList<RunData>();
		arrRunDataMonth = new ArrayList<RunData>();
		SharedPreferences goalPreferences = getSharedPreferences("goalstepfiles", Context.MODE_APPEND);
		goalstepcount = goalPreferences.getInt("setgoalstepcount", 5000);
	
		if (change == 0) { // todo --- 1个月
			coordinates = new String[5];
			for (int i = 0; i < 30; i++) {
				select_date = getDateFormat.format(calendar.getTime());  // 2017-04-06
				arrRunDataOne = judgmentRunDB( select_date);
				int dayStepNum = 0;
				if (arrRunDataOne.size() != 0) {
					if(!SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3") &&  !SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
						for (int j = 0; j < arrRunDataOne.size(); j++) {
//							com.szkct.weloopbtsmartdevice.util.Log.e("lxstep", "一个的步数---- " + arrRunDataOne.get(j).getStep());
							dayStepNum += Utils.toint(arrRunDataOne.get(j).getStep());
							distance += Float.parseFloat(arrRunDataOne.get(j).getDistance());
						}
					}else{
						for (int j = 0; j < arrRunDataOne.size(); j++) {
							dayStepNum += Utils.toint(arrRunDataOne.get(j).getDayStep());
							distance += Utils.tofloat(arrRunDataOne.get(j).getDayDistance());
						}
					}
					if (dayStepNum >= goalstepcount) {
						achievementCount += 1;
					}

					if(dayStepNum > 200000){
						dayStepNum = 52007;
					}else if(dayStepNum < 0){
						dayStepNum = 0;
					}

					barValues.add(dayStepNum);
					totalStep += dayStepNum;
					if (linesNum < dayStepNum) {
						linesNum = dayStepNum;
					}
				} else {
					barValues.add(0);
				}
				if (i % 7 == 0) {
					coordinates[i / 7] = dateFormat.format(calendar.getTime());
				}
				calendar.add(Calendar.DATE, -1);
			}
			mAverageTv.setText(totalStep / 30 + "");
			mGoalAchievementTv.setText(String.valueOf(Utils.setformat(1, achievementCount / 0.3f + "")) + "%");
		} else if (change == 1) { //TODO ---  6个月
			coordinates = new String[6];
			int weekStepNum = 0;
			int coo = 0;
			for (int i = 0; i < 6; i++) {
				select_date = getYYMMFormat.format(calendar.getTime());  // 2017-04
				arrRunDataMonth = judgmentRunDB(select_date+"-01",select_date+"-31");
				int monthStepNum = 0;
				//Log.e("arrRunDataMonth", arrRunDataMonth.size()+"=="+select_date);
				if (arrRunDataMonth.size() != 0) {
					if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {   // H872
						for (int j = 0; j < arrRunDataMonth.size(); j++) {
							com.szkct.weloopbtsmartdevice.util.Log.e("lxstep", "6个月的步数---- " + arrRunDataMonth.get(j).getStep());
							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getStep());
							distance += Float.parseFloat(arrRunDataMonth.get(j).getDistance());
						}
					}else if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){ //X2
						/*String tempDate = "";
						int oneDaySteps = 0;
						boolean isFirstData = false;
						for (int j = 0; j < arrRunDataMonth.size(); j++) {  //
							String mDate = arrRunDataMonth.get(j).getDate();//
//							if(StringUtils.isEmpty(tempDate)){
//								isFirstData = true;
//							}else {
//								isFirstData = false;
//							}
							if(!tempDate.equals(mDate)){ // 日期切换了
								if (oneDaySteps >= goalstepcount) {
									achievementCount += 1;
								}
								tempDate = mDate;
								oneDaySteps = 0;
								oneDaySteps += Integer.valueOf(arrRunDataMonth.get(j).getStep());
							}else {
//								if(isFirstData){
//
//								}
								oneDaySteps += Integer.valueOf(arrRunDataMonth.get(j).getStep());
							}*/

						for (int j = 0; j < arrRunDataMonth.size(); j++) {
							com.szkct.weloopbtsmartdevice.util.Log.e("lxstep", "6个月的步数---- " + arrRunDataMonth.get(j).getStep());
							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getStep());
							distance += Float.parseFloat(arrRunDataMonth.get(j).getDistance());

							if (Utils.toint(arrRunDataMonth.get(j).getDayStep()) >= goalstepcount) {  // Utils.toint(arrRunDataMonth.get(j).getDayStep()) >= goalstepcount  --- Utils.toint(arrRunDataMonth.get(j).getStep()) >= goalstepcount
								achievementCount += 1;
							}
						}
					}else {  // MTK
						for (int j = 0; j < arrRunDataMonth.size(); j++) {
							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getDayStep());
//							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getStep());
							distance += Utils.tofloat(arrRunDataMonth.get(j).getDayDistance());
							if (Utils.toint(arrRunDataMonth.get(j).getDayStep()) >= goalstepcount) {
								achievementCount += 1;
							}
						}
					}
					barValues.add(monthStepNum);
					totalStep += monthStepNum;
					if (linesNum < monthStepNum) {
						linesNum = monthStepNum;
					}
				} else {
					barValues.add(0);
				}
				coordinates[i] = monthFormat.format(calendar.getTime());
				calendar.add(Calendar.MONTH, -1);  // 月份减1
			}
			mAverageTv.setText(totalStep / 180 + "");
			mGoalAchievementTv.setText(String.valueOf(Utils.setformat(1, achievementCount / 1.80f + "")) + "%");
		} else if (change == 2) {  // 一年的 计步数据
			coordinates = new String[12];
			for (int i = 0; i < 12; i++) {
				select_date = getYYMMFormat.format(calendar.getTime());  // 2017-04 (4月)
				arrRunDataMonth = judgmentRunDB(select_date+"-01",select_date+"-31");
				int monthStepNum = 0;
				if (arrRunDataMonth.size() != 0) {
					if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("1") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {
						for (int j = 0; j < arrRunDataMonth.size(); j++) {
							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getStep());
							distance += Float.parseFloat(arrRunDataMonth.get(j).getDistance());
						}
					}else if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){ //X2
						for (int j = 0; j < arrRunDataMonth.size(); j++) {
                            Log.i(TAG,"time = " + arrRunDataMonth.get(j).getDate() + " ; " + arrRunDataMonth.get(j).getStep());
							com.szkct.weloopbtsmartdevice.util.Log.e("lxstep", "6个月的步数---- " + arrRunDataMonth.get(j).getStep());
							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getStep());
							distance += Float.parseFloat(arrRunDataMonth.get(j).getDistance());

							if (Utils.toint(arrRunDataMonth.get(j).getDayStep()) >= goalstepcount) {  // Utils.toint(arrRunDataMonth.get(j).getDayStep()) >= goalstepcount  --- Utils.toint(arrRunDataMonth.get(j).getStep()) >= goalstepcount
								achievementCount += 1;
							}
						}
					}else{ // MTK
						for (int j = 0; j < arrRunDataMonth.size(); j++) {
							monthStepNum += Utils.toint(arrRunDataMonth.get(j).getDayStep());
							distance += Utils.tofloat(arrRunDataMonth.get(j).getDayDistance());

							if (Utils.toint(arrRunDataMonth.get(j).getDayStep()) >= goalstepcount) {
								achievementCount += 1;
							}
						}
					}
					barValues.add(monthStepNum);
					totalStep += monthStepNum;
					if (linesNum < monthStepNum) {
						linesNum = monthStepNum;
					}
				} else {
					barValues.add(0);
				}
				coordinates[i] = monthFormat.format(calendar.getTime());
				calendar.add(Calendar.MONTH, -1);
			}
			mAverageTv.setText(totalStep / 365 + "");
			mGoalAchievementTv.setText(String.valueOf(Utils.setformat(1, achievementCount / 3.65f + "")) + "%");
		}
		linesNum = linesNum / 1000 ;
		//Log.e("linesNum", linesNum+"+++++++++++++++");
		int	linehight=(linesNum/10+1)*5;
		mTotalStepTv.setText(totalStep + "");
		if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))){
			mTotalDisTv.setText(String.valueOf(Utils.setformat(1, distance + "")) + "km");
		}else{
			mTotalDisTv.setText(String.valueOf(Utils.setformat(1, Utils.getUnit_km(distance) + "")) + "mile");
		}

		mHorizontalChartView.setDataShow(sportorsleep, change, linehight, barValues, coordinates);
	}

	public  void parseDownLoadSleepData(String content) {
		/*try {
			String result = content;
			JSONObject jsonObj = new JSONObject(content);
			int nRetCode = jsonObj.optInt("msg");
			if(nRetCode == 0){
				////////////////////////////////////////////////////////////////////////////////////
//				JSONObject obj = jsonObj.optJSONObject("data");
				arrSleepDataOne.clear();    //   arrSleepDataMonth  ---- 从后台获取数据，解析时 保存数据的结合应该可以共用
				JSONArray objArr = jsonObj.optJSONArray("data");
				for(int i=0 ; i<objArr.length() ; i++){
					JSONObject jsonObject = objArr.getJSONObject(i);
					SleepData mData = new SleepData();
					mData.setId(Long.valueOf(jsonObject.getString("id")));   // user_id  ---用不到
					mData.setDeepsleep(jsonObject.getString("deep_sleep"));  // deep_sleep  深睡时间
					mData.setLightsleep(jsonObject.getString("shallow_sleep")); // shallow_sleep  浅睡时间
					mData.setAutosleep(jsonObject.getString("cout"));   		  // 总睡眠时间
					mData.setDate(jsonObject.getString("date"));   // 睡眠对应的日期
					arrSleepDataOne.add(mData);
				}
//				String deep_sleep = obj.optString("deep_sleep");  // 深睡
//				String shallow_sleep = obj.optString("shallow_sleep");  // 浅睡
				// TODO ------ 给睡眠数据控件赋值
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				for (int j = 0; j < arrSleepDataOne.size(); j++) {
					deepsleepmin += Utils.toint(arrSleepDataOne.get(j).getDeepsleep());  // 深睡时间
					sleepmin = sleepmin+Utils.toint(arrSleepDataOne.get(j).getDeepsleep())+Utils.toint(arrSleepDataOne.get(j).getLightsleep());  // 总睡眠时间
				}
				barValues.add(sleepmin/60);
				totalStep += sleepmin;
				deepsleepminall += deepsleepmin;
				if (linesNum < sleepmin/60) {
					linesNum = sleepmin/60;
				}
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}else if(nRetCode == 1){
//                dismissLoadingDialog();
				Toast.makeText(AllDataActivity.this, "参数填写不完整", Toast.LENGTH_SHORT).show();
			}else if(nRetCode == 2){
				Toast.makeText(AllDataActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
			}else if(nRetCode == 504){
				Toast.makeText(AllDataActivity.this, "上传失败，未知错误", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	}

	private void toDownloadSleepData(String userId,int type,RequestCallBackEx<String> respon){ // 根据用户id获取用户信息    睡眠数据下载
		/*try {
//            reParams.addQueryStringParameter("mid", 1063024 + "");
			com.lidroid.xutils.http.RequestParams reParams = new RequestParams();
			reParams.addBodyParameter("user_id", userId);
			reParams.addBodyParameter("type", type +"");        // 类型 1:按周 2：按月(1个月)3：按月（6个月）4:按年5:按天
//			reParams.addBodyParameter("date",  1 + "");    // 当天的日期
			String url = ServerConfig.DOWNLOAD_SLEEP_DATA;
			if (url.contains(" ")){
				if(url.substring(url.length()-1)==" "){
					url= url.substring(0,url.length()-1);
				}else{
					url= url.replace(" ","%20");
				}
			}
			if (url.contains("\"")){
				url= url.replace("\"","%22");
			}
			if (url.contains("{")){
				url= url.replace("{","%7B");
			}
			if (url.contains("}")){
				url= url.replace("{","%7D");
			}
			XHttpUtils.getInstance().send(HttpRequest.HttpMethod.POST, url, reParams, respon);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	private void showDatasleep() {   // 睡眠的大数据
		linesNum = 0;
		totalStep = 0;
		deepsleepminall = 0;   // ???
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);//TODO---- 将睡眠数据 提前一天

		calendarXiabiao = Calendar.getInstance();

		barValues = new ArrayList<Integer>();
		arrSleepDataWeek = new ArrayList<SleepData>();
		arrSleepDataMonth = new ArrayList<SleepData>();
		if (change == 0) {  // 一个月
			coordinates = new String[5];
			for (int i = 0; i < 30; i++) {
				select_date = getDateFormat.format(calendar.getTime()); 
				arrSleepDataOne = judgmentSleepDB(select_date);
				sleepmin = 0;   // 总睡眠时间
				deepsleepmin = 0;  // 深睡时间
				///////////////////////////////////////////////////////////////////////////////////////////////////
					if (arrSleepDataOne.size() != 0) {
//					String strDate = curdatetv.getText().toString();  // 2017-04-09   2017-04-10
						String choiceDate = arrangeDate(select_date);
						String  strDate = choiceDate + " 21";
						Date endTimeDate;
						long endTime = 0;
						Calendar calendar3 = Calendar.getInstance();
						Date startTimeDate;
						long startTime = 0;
						try {
							startTimeDate = format.parse(strDate);
							startTime = startTimeDate.getTime()/1000;
							calendar3.setTime(startTimeDate);
							calendar3.add(Calendar.DATE, +1);
							String start = getDateFormat.format(calendar3.getTime()).toString();
							start = start + " 09";
							endTimeDate = format.parse(start);    // Thu Apr 06 21:00:00 GMT+08:00 2017   20170406 21:00:00
							endTime = endTimeDate.getTime()/1000;
						} catch (ParseException e) {
							e.printStackTrace();
						}

						int lights = 0;
						int deepS = 0;
						for (int j = 0; j < arrSleepDataOne.size(); j++) {
							String endTimeStr = arrSleepDataOne.get(j).getEndTime(); //   2017-04-09 21:00:00
							Date date = StringUtils.parseStrToDate(endTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							long sleepDataEndTime = calendar.getTimeInMillis() / 1000;  //

							String startTimeStr = arrSleepDataOne.get(j).getStarttimes(); //    2017-04-09 21:00:00
							Date date2 = StringUtils.parseStrToDate(startTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
							Calendar calendar2 = Calendar.getInstance();
							calendar2.setTime(date2);
							long sleepDataStartTime = calendar2.getTimeInMillis() / 1000;
							if(sleepDataEndTime > startTime){
								if(sleepDataEndTime <= endTime){
									if(sleepDataStartTime >= startTime){
										deepS += Utils.toint(arrSleepDataOne.get(j).getDeepsleep());
										lights += Utils.toint(arrSleepDataOne.get(j).getLightsleep());
									}else {
										long okSleeptime= sleepDataEndTime - startTime;
										int okFenTime = (int)okSleeptime/60;
										if(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
											if(arrSleepDataOne.get(j).getSleeptype().equals("0")){
												deepS += okFenTime;
											}else if(arrSleepDataOne.get(j).getSleeptype().equals("1")){
												lights += okFenTime;
											}
										}else if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
												|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
											if(arrSleepDataOne.get(j).getSleeptype().equals("2")){
												deepS += okFenTime;
											}else if(arrSleepDataOne.get(j).getSleeptype().equals("1")){
												lights += okFenTime;
											}
										}
									}
								}else {
									arrSleepDataOne.get(j).getStarttimes();
									if(sleepDataStartTime >= endTime){
										continue;
									}else {
										long okSleeptime= endTime - sleepDataStartTime ;
										int okFenTime = (int)okSleeptime/60;
										if(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
											if(arrSleepDataOne.get(j).getSleeptype().equals("0")){
												deepS += okFenTime;
											}else if(arrSleepDataOne.get(j).getSleeptype().equals("1")){
												lights += okFenTime;
											}
										}else if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
												|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
											if(arrSleepDataOne.get(j).getSleeptype().equals("2")){
												deepS += okFenTime;
											}else if(arrSleepDataOne.get(j).getSleeptype().equals("1")){
												lights += okFenTime;
											}
										}
									}
								}
							}
						}
						deepsleepmin = deepS;
						sleepmin = sleepmin + deepS + lights;

						if(sleepmin < 0){  //todo --- 防止睡眠数据出现负数
							sleepmin = 0;
						}else if(sleepmin > 720){
							sleepmin = 720;
						}

						barValues.add(sleepmin / 60);
						totalStep += sleepmin;
						deepsleepminall += deepsleepmin;
						if (linesNum < sleepmin/60) {
							linesNum = sleepmin/60;
						}
					} else {
						barValues.add(0);
					}
//				}
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if (i % 7 == 0) {
//					coordinates[i / 7] = dateFormat.format(calendar.getTime());
					coordinates[i / 7] = dateFormat.format(calendarXiabiao.getTime());
				}
				calendarXiabiao.add(Calendar.DATE, -1);
				calendar.add(Calendar.DATE, -1);
			}
			mTotalDisTv.setText(Utils.setformat(1,totalStep/1800d+"")+"h");
		} else if (change == 1) {  // 6个月
			coordinates = new String[6];
			//int monday = getDayWeek(calendar); 
			int weekStepNum = 0;
			int coo = 0;
			for (int i = 0; i < 6; i++) {
				select_date = getYYMMFormat.format(calendar.getTime());  // 得到 年月 （月份值）

				Calendar calendar6Month = Calendar.getInstance();

				//arrRunDataMonth = getRunDataMonth(MovementDatas.TABLE_NAME, select_date, 0);
			//	arrRunDataWeek = judgmentRunDB(select_date);
				arrSleepDataMonth = judgmentSleepDB(select_date+"-01",select_date+"-31");
				int monthsleepmin = 0;
				int monthdeepsleepmin= 0;
				Log.e("arrRunDataMonth", arrSleepDataMonth.size()+"=="+select_date);
					if (arrSleepDataMonth.size() != 0) {
						for (int s = 0; s < 30; s++) {
//							String select_date_riqi = getDateFormat.format(calendar.getTime());
							String select_date_riqi = getDateFormat.format(calendar6Month.getTime());
							String choiceDate = arrangeDate(select_date_riqi); // 当前天日期
							String strDate = choiceDate + " 21";
							Date endTimeDate;
							long endTime = 0;
							Calendar calendar3 = Calendar.getInstance();
							Date startTimeDate;
							long startTime = 0;
							try {
								startTimeDate = format.parse(strDate);   // Fri Apr 07 08:00:00 GMT+08:00 2017     ---- 20170407 --- 08:00
								startTime = startTimeDate.getTime() / 1000;
								calendar3.setTime(startTimeDate);
								calendar3.add(Calendar.DATE, +1);
								String start = getDateFormat.format(calendar3.getTime()).toString();
								start = start + " 09";
								endTimeDate = format.parse(start);    // Thu Apr 06 21:00:00 GMT+08:00 2017   20170406 21:00:00
								endTime = endTimeDate.getTime() / 1000;
							} catch (ParseException e) {
								e.printStackTrace();
							}

							int lights = 0;
							int deepS = 0;

							for (int j = 0; j < arrSleepDataMonth.size(); j++) {
								String endTimeStr = arrSleepDataMonth.get(j).getEndTime();
								Date date = StringUtils.parseStrToDate(endTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								long sleepDataEndTime = calendar.getTimeInMillis() / 1000;

								String startTimeStr = arrSleepDataMonth.get(j).getStarttimes();
								Date date2 = StringUtils.parseStrToDate(startTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
								Calendar calendar2 = Calendar.getInstance();
								calendar2.setTime(date2);
								long sleepDataStartTime = calendar2.getTimeInMillis() / 1000;
								if(sleepDataEndTime > startTime){
									if(sleepDataEndTime <= endTime){
										if(sleepDataStartTime >= startTime){
											deepS += Utils.toint(arrSleepDataMonth.get(j).getDeepsleep());
											lights += Utils.toint(arrSleepDataMonth.get(j).getLightsleep());
										}else {
											long okSleeptime= sleepDataEndTime - startTime;
											int okFenTime = (int)okSleeptime/60;
											if(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
												if(arrSleepDataMonth.get(j).getSleeptype().equals("0")){  //
													deepS += okFenTime;
												}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //     else if(arrSleep.get(i).getSleeptype().equals("1"))
													lights += okFenTime;
												}
											}else if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
													|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
												if(arrSleepDataMonth.get(j).getSleeptype().equals("2")){  //
													deepS += okFenTime;
												}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //     else if(arrSleep.get(i).getSleeptype().equals("1"))
													lights += okFenTime;
												}
											}
										}
									}else {
										arrSleepDataMonth.get(j).getStarttimes();
										if(sleepDataStartTime >= endTime){
											continue;
										}else {
											long okSleeptime= endTime - sleepDataStartTime ;
											int okFenTime = (int)okSleeptime/60; //
											if(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
												if(arrSleepDataMonth.get(j).getSleeptype().equals("0")){  //
													deepS += okFenTime;
												}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //     else if(arrSleep.get(i).getSleeptype().equals("1"))
													lights += okFenTime;
												}
											}else if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
													|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
												if(arrSleepDataMonth.get(j).getSleeptype().equals("2")){  //
													deepS += okFenTime;
												}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //     else if(arrSleep.get(i).getSleeptype().equals("1"))
													lights += okFenTime;
												}
											}
										}
									}
								}
							//	monthsleepmin += deepS;
							//	monthdeepsleepmin = monthdeepsleepmin + deepS + lights;
							}
							
								monthsleepmin += deepS;  //
								monthdeepsleepmin = monthdeepsleepmin + deepS + lights;
							calendar6Month.add(Calendar.DATE, -1);
						}
						barValues.add(monthdeepsleepmin/60);
						totalStep += monthdeepsleepmin;
						deepsleepminall += monthsleepmin;
						if (linesNum < monthdeepsleepmin/60) {
							linesNum = monthdeepsleepmin/60;
						}
					} else {
						barValues.add(0);
					}
//				}
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				coordinates[i] = monthFormat.format(calendar.getTime());
				calendar.add(Calendar.MONTH, -1);
			}
			mTotalDisTv.setText(Utils.setformat(1,totalStep/60d/180+"")+"h");
		} else if (change == 2) {  // 1年
			coordinates = new String[12];
			for (int i = 0; i < 12; i++) {
				select_date = getYYMMFormat.format(calendar.getTime());

				Calendar calendarOneYear = Calendar.getInstance();

				arrSleepDataMonth = judgmentSleepDB(select_date+"-01",select_date+"-31");
				int monthsleepmin = 0;
				int monthdeepsleepmin= 0;

					if (arrSleepDataMonth.size() != 0) {

						for (int s = 0; s < 30; s++) {
//							String select_date_riqi = getDateFormat.format(calendar.getTime());
							String select_date_riqi = getDateFormat.format(calendarOneYear.getTime());
							String choiceDate = arrangeDate(select_date_riqi); // 当前天日期
							String strDate = choiceDate + " 21";
							Date endTimeDate;
							long endTime = 0;
							Calendar calendar3 = Calendar.getInstance();
							Date startTimeDate;
							long startTime = 0;   //
							try {
								startTimeDate = format.parse(strDate);   // Fri Apr 07 08:00:00 GMT+08:00 2017     ---- 20170407 --- 08:00
								startTime = startTimeDate.getTime() / 1000;
								calendar3.setTime(startTimeDate);
								calendar3.add(Calendar.DATE, +1);
								String start = getDateFormat.format(calendar3.getTime()).toString();
								start = start + " 09";
								endTimeDate = format.parse(start);    // Thu Apr 06 21:00:00 GMT+08:00 2017   20170406 21:00:00
								endTime = endTimeDate.getTime() / 1000;
							} catch (ParseException e) {
								e.printStackTrace();
							}

							int lights = 0;
							int deepS = 0;

							for (int j = 0; j < arrSleepDataMonth.size(); j++) {
								String endTimeStr = arrSleepDataMonth.get(j).getEndTime();
								Date date = StringUtils.parseStrToDate(endTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								long sleepDataEndTime = calendar.getTimeInMillis() / 1000;  // 1489816800   TODO ---        1491742800     ---- 2017/4/9 21:0:0    1491742800     1491656400--2017/4/8 21:0:0

							String startTimeStr = arrSleepDataMonth.get(j).getStarttimes(); //      2017-04-09 21:00:00
							Date date2 = StringUtils.parseStrToDate(startTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
							Calendar calendar2 = Calendar.getInstance();
							calendar2.setTime(date2);
							long sleepDataStartTime = calendar2.getTimeInMillis() / 1000;  //TODO      1491707220   2017/4/9 11:7:0
							if(sleepDataEndTime > startTime){  //        1492606800
								if(sleepDataEndTime <= endTime){
									if(sleepDataStartTime >= startTime){
										deepS += Utils.toint(arrSleepDataMonth.get(j).getDeepsleep());   //
										lights += Utils.toint(arrSleepDataMonth.get(j).getLightsleep()); //
									}else {
										long okSleeptime= sleepDataEndTime - startTime;
										int okFenTime = (int)okSleeptime/60;
										if(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
											if(arrSleepDataMonth.get(j).getSleeptype().equals("0")){  //
												deepS += okFenTime;
											}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //
												lights += okFenTime;
											}
										}else if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
												|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
											if(arrSleepDataMonth.get(j).getSleeptype().equals("2")){  //
												deepS += okFenTime;
											}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //
												lights += okFenTime;
											}
										}
									}
								}else {
									arrSleepDataMonth.get(j).getStarttimes();
									if(sleepDataStartTime >= endTime){
										continue;
									}else {
										long okSleeptime= endTime - sleepDataStartTime ;
										int okFenTime = (int)okSleeptime/60; //
										if(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
											if(arrSleepDataMonth.get(j).getSleeptype().equals("0")){  //
												deepS += okFenTime;
											}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //
												lights += okFenTime;
											}
										}else if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
												|| SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
											if(arrSleepDataMonth.get(j).getSleeptype().equals("2")){  //
												deepS += okFenTime;
											}else if(arrSleepDataMonth.get(j).getSleeptype().equals("1")){ //
												lights += okFenTime;
											}
										}
									}
								}
							}
							}
							monthsleepmin += deepS;  //
							monthdeepsleepmin = monthdeepsleepmin + deepS + lights; //
							calendarOneYear.add(Calendar.DATE, -1);
						}
						barValues.add(monthdeepsleepmin/60);
						totalStep += monthdeepsleepmin;
						deepsleepminall += monthsleepmin;
						if (linesNum < monthdeepsleepmin/60) {
							linesNum = monthdeepsleepmin/60;
						}
					} else {
						barValues.add(0);
					}
//				}
				///////////////////////////////////////////////////////////////////////////////////////////////////////
				coordinates[i] = monthFormat.format(calendar.getTime());
				calendar.add(Calendar.MONTH, -1);
			}
			mTotalDisTv.setText(Utils.setformat(1,totalStep/60d/365d+"")+"h");
		}
	/*	mAverageTv.setText(totalStep/60+ "h");
		mTotalStepTv.setText(totalStep/60-deepsleepminall/60+"h");
		mGoalAchievementTv.setText(deepsleepminall/60+"h");*/
		mAverageTv.setText(Utils.setformat(1,totalStep/60d+"")+ "h");     // 平均睡眠时间
		mTotalStepTv.setText(Utils.setformat(1,totalStep/60d-deepsleepminall/60d+"")+"h");   // 总睡眠时间
		mGoalAchievementTv.setText(Utils.setformat(1,deepsleepminall/60d+"")+"h");			//
		int linehight=(linesNum/4+1)*2;
		if(linehight<4){
			linehight=4;
		}
	//	linesNum = linesNum / 4 + 2;
	//	mTotalStepTv.setText(totalStep + "");
		//mTotalDisTv.setText(String.valueOf(df.format(totalStep * 0.6 / 1000)) + "km");
		mHorizontalChartView.setDataShow(sportorsleep,change, linehight, barValues, coordinates);
	}
	
	private ArrayList<RunData> judgmentRunDB(String choiceDate){
		if (db == null) {
			db = DBHelper.getInstance(AllDataActivity.this);
		}
		Query query=null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		List list = null;
		String today = simpleDateFormat.format(new Date());
		if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){
			if(SharedPreUtil.readPre(AllDataActivity.this,SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")){
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))
						.where(RunDataDao.Properties.Date.eq(choiceDate))
						.where(RunDataDao.Properties.Step.eq("0"))
						.build();
			}else{
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
						.where(RunDataDao.Properties.Date.eq(choiceDate))
						.where(RunDataDao.Properties.Step.eq("0"))
						.build();
			}
			list = query.list();
		}else if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){
			String realTime =  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME);    // todo ---- 为当前天日期,且有当前的实时步数
			if(choiceDate.equals(today) && !StringUtils.isEmpty(realTime)  && realTime.equals(today)) {
				int synStep = 0;
				int realStep = 0;
				float calorie = 0;
				float distance = 0;
				RunData runData = new RunData();
				if (!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {
					synStep = Integer.parseInt(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
				}
				if (!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {
					realStep = Integer.parseInt(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
				}
				if (synStep <= realStep) {
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))){
						calorie = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
					}
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))){
						distance = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
					}
					runData.setStep(realStep+"");
				}else{
//					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE))){
//						calorie = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE));
//					}
//					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE))){
//						distance = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE));
//					}
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))){    //todo ---- ble平台都是用的 实时的 卡路里和 距离
						calorie = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
					}
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))){
						distance = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
					}
					runData.setStep(synStep+"");
				}
				runData.setCalorie(calorie+"");
				runData.setDistance(distance+"");
				list = new ArrayList();
				list.add(runData);
			}else {
				if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
					query = db.getRunDao().queryBuilder()
							//	.where(RunDataDao.Properties.Mid.eq(mid))
							.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))
							.where(RunDataDao.Properties.Date.eq(choiceDate))
							.build();
				} else {
					query = db.getRunDao().queryBuilder()
							//	.where(RunDataDao.Properties.Mid.eq(mid))
							.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
							.where(RunDataDao.Properties.Date.eq(choiceDate))

							.build();
				}
				list = query.list();
			}

		}else{
			if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER,SharedPreUtil.MAC)))
						.where(RunDataDao.Properties.Date.eq(choiceDate))
						.build();
			}else{
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
						.where(RunDataDao.Properties.Date.eq(choiceDate))

						.build();
			}
			list = query.list();
		}
		ArrayList<RunData> runData =  new ArrayList<RunData>();
		if(list!=null&&list.size()>=1){
		
			for(int j = 0;j<list.size();j++){
				RunData runDB = (RunData)list.get(j);
				runData.add(runDB);
			}
		}
		return runData;
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

	private ArrayList<SleepData> judgmentSleepDB(String choiceDate) {  //  // 根据日期获取一个月的 睡眠数据
		if (db == null) {
			db = DBHelper.getInstance(AllDataActivity.this);
		}

		String endStrDate = "";
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

		Query query = null;  // 当前日期
		if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER,
				SharedPreUtil.SHOWMAC).equals("")) {
			query = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
		} else {
			query = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
		}
		List listCur = query.list();  //获取当前天的睡眠数据

		Query queryEnd = null;
		if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
			queryEnd = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDateEnd)).build();
		} else {
			queryEnd = db.getSleepDao().queryBuilder().where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
					.where(SleepDataDao.Properties.Date.eq(choiceDateEnd)).build();
		}
		List listNext = queryEnd.list();  // 获取后一天的 睡眠 数据

//		List list = query.list();
		List list = new ArrayList();
		list.addAll(listCur);
		list.addAll(listNext); // 将当前天和上一天的睡眠数据都添加

		ArrayList<SleepData> arrSleep = new ArrayList<SleepData>();
		if (list != null && list.size() >= 1) {
			
			for (int j = 0; j < list.size(); j++) {
				SleepData runDB = (SleepData) list.get(j);
				arrSleep.add(runDB);
			}
			
		}
		return arrSleep;
		
	}
	private ArrayList<RunData> judgmentRunDB(String startDate,String endDate){
		if (db == null) {
			db = DBHelper.getInstance(AllDataActivity.this);
		}
		Query query=null;
		if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
			if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))     
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))
						.where(RunDataDao.Properties.Date.between(startDate, endDate))
						.where(RunDataDao.Properties.Step.eq("0"))
						.build();
			} else {
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))     
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
								//	.where(RunDataDao.Properties.Date.eq(choiceDate))
						.where(RunDataDao.Properties.Date.between(startDate, endDate))
						.where(RunDataDao.Properties.Step.eq("0"))
						.build();
			}
		}else{
			if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)))
						.where(RunDataDao.Properties.Date.between(startDate, endDate))
						.build();
			} else {
				query = db.getRunDao().queryBuilder()
						//	.where(RunDataDao.Properties.Mid.eq(mid))
						.where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
								//	.where(RunDataDao.Properties.Date.eq(choiceDate))
						.where(RunDataDao.Properties.Date.between(startDate, endDate))
						.build();
			}
		}


		List list = query.list();
		ArrayList<RunData> runData =  new ArrayList<RunData>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		String today = simpleDateFormat.format(new Date());
		if(list!=null&&list.size()>=1){
			for(int j = 0;j<list.size();j++){
				RunData runDB = (RunData)list.get(j);
				if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){
					if(runDB.getDate().equals(today)){
						continue;
					}
				}
				runData.add(runDB);
			}
			if(SharedPreUtil.readPre(this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")){
				int synStep = 0;
				int realStep = 0;
				float calorie = 0;
				float distance = 0;
				RunData runDB = new RunData();
				if (!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {
					synStep = Integer.parseInt(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
				}
				if (!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {
					realStep = Integer.parseInt(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
				}
				if (synStep <= realStep) {
					runDB.setStep(realStep+"");
					runDB.setDayStep(realStep + ""); // 设置一天的步数
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))){
						calorie = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
					}
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))){
						distance = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
					}
				}else{
					runDB.setStep(synStep+"");
					runDB.setDayStep(synStep + ""); // 设置一天的步数
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE))){
						calorie = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE));
					}
					if(!TextUtils.isEmpty(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE))){
						distance = Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE));
					}
				}
				runDB.setCalorie(calorie+"");
				runDB.setDistance(distance+"");
				runDB.setDate(today);
				runData.add(runDB);
			}
		}
		return runData;
	}
	private ArrayList<SleepData> judgmentSleepDB(String startDate,String endDate) {
		if (db == null) {
			db = DBHelper.getInstance(AllDataActivity.this);
		}
		Query query = null;
		if (SharedPreUtil.readPre(AllDataActivity.this, SharedPreUtil.USER,
				SharedPreUtil.SHOWMAC).equals("")) {
			query = db
					.getSleepDao()
					.queryBuilder()
					// .where(SleepDataDao.Properties.Mid.eq(mid))
					.where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
							AllDataActivity.this, SharedPreUtil.USER,
							SharedPreUtil.MAC)))
					.where(SleepDataDao.Properties.Date.between(startDate, endDate))
					.build();
		} else {
			query = db
					.getSleepDao()
					.queryBuilder()
					// .where(SleepDataDao.Properties.Mid.eq(mid))
					.where(SleepDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
							AllDataActivity.this, SharedPreUtil.USER,
							SharedPreUtil.SHOWMAC)))
					.where(SleepDataDao.Properties.Date.between(startDate, endDate))
					.build();
		}



		List list = query.list();
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

	public void ShowMessage(String text) {
		if (null == toast) {
			toast = Toast.makeText(BTNotificationApplication.getInstance(), text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
