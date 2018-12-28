package com.szkct.weloopbtsmartdevice.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.ChartViewTemperatureData;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Temperature;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.TemperatureDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.CalendarAcitity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.DailogUtils;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.ThreadPoolManager;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.TemperatureAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


public class TemperatureActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private ImageView mIvAdd;
    private TextView mTvTest;
    private TextView mTvInfo;
    private TextView mTvSelectTime;
    private ImageView mIvDown;
    private ImageView mIvUp;
    private SimpleDateFormat mDateFormat;
    private String mCurrentTimeStr = "";
    private RecyclerView mRv;
    private TemperatureAdapter mAdapter;

    private LineChartView mChart;
    private TextView mTvNoData;

    private ArrayList<Temperature> mData = new ArrayList<>();
    private ArrayList<ChartViewTemperatureData> mChartData;

    private thbroadcast vb;
    private DBHelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_temperature);
        EventBus.getDefault().register(this);
        initView();
        initTime();
        initRV();

        initData();

        registBroadcastReceiver();

        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //日历界面返回结果显示
        calendarResult();
        judgmentTemperatureDB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vb != null) {
            unregisterReceiver(vb);
        }
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mTvTest = (TextView) findViewById(R.id.tv_test);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mTvSelectTime = (TextView) findViewById(R.id.curdate_tv);
        mIvDown = (ImageView) findViewById(R.id.data_bt_downturning);
        mIvUp = (ImageView) findViewById(R.id.data_bt_upturning);
        mChart = (LineChartView) findViewById(R.id.chart);
        mTvNoData = (TextView) findViewById(R.id.tv_nodata);
        mRv = (RecyclerView) findViewById(R.id.rv);
    }

    private void initTime() {
        mTvSelectTime.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskLightCond);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        mDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
        mCurrentTimeStr = mDateFormat.format(curDate);
        setCurDate(mCurrentTimeStr);
    }

    private void initRV() {
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TemperatureAdapter(R.layout.item_temperature_check, mData);
        mRv.setAdapter(mAdapter);

    }

    private void initData() {
        judgmentTemperatureDB();
    }

    private void registBroadcastReceiver() {
        if (null == vb) {
            vb = new thbroadcast();
        }
        IntentFilter viewFilter = new IntentFilter();
        viewFilter.addAction(MainService.ACTION_SYNFINSH);   //报告页面 注册 手表数据同步 成功的 广播
        viewFilter.addAction(MainService.ACTION_MACCHANGE);
        viewFilter.addAction(MainService.ACTION_USERDATACHANGE);

        viewFilter.addAction(MainService.ACTION_CHANGE_WATCH);

        viewFilter.addAction(MainService.ACTION_SYNFINSH_SUCCESS);

        viewFilter.addAction(MainService.ACTION_SYNARTHEART);

        viewFilter.addAction("android.intent.action.DATE_CHANGED");

        registerReceiver(vb, viewFilter);
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mTvTest.setOnClickListener(this);
        mTvSelectTime.setOnClickListener(this);
        mIvDown.setOnClickListener(this);
        mIvUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add:
                DailogUtils.showTip(this, new DailogUtils.DialogListener() {
                    @Override
                    public void close() {
                    }
                });
                break;
            case R.id.tv_test:
                DailogUtils.showTip(this, new DailogUtils.DialogListener() {
                    @Override
                    public void close() {
                        mTvInfo.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.curdate_tv:
                Intent mIntent = new Intent(this, CalendarAcitity.class);
                mIntent.putExtra("from", "HealthFragment");
                startActivity(mIntent);
                break;
            case R.id.data_bt_downturning:
                if (isFastDoubleClick()) {
                    return;
                }
                dateDown();
                judgmentTemperatureDB();
                break;
            case R.id.data_bt_upturning:
                if (isFastDoubleClick()) {
                    return;
                }
                dateUp();
                judgmentTemperatureDB();
                break;
        }
    }

    private void dateUp() {
        String time = mTvSelectTime.getText().toString();
        if (time.equals(mCurrentTimeStr)) {
            Toast.makeText(this, R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
        } else {
            setCurDate(UTIL.getAddDay(time));
        }
    }

    private void dateDown() {
        String time = mTvSelectTime.getText().toString();
        setCurDate(UTIL.getSubtractDay(time));
    }

    private void calendarResult() {
        SharedPreferences datepreferences = getSharedPreferences("datepreferences", Context.MODE_PRIVATE);
        final int select_day = datepreferences.getInt("1_select_day", 0);
        final int select_month = datepreferences.getInt("1_select_month", 0);
        final int select_year = datepreferences.getInt("1_select_year", 0);

        String select_monthstr;
        String select_daystr;
        if ((select_day != 0) && (select_month != 0) && (select_year != 0)) {
            if (select_month < 10) {
                select_monthstr = "0" + select_month;
            } else {
                select_monthstr = String.valueOf(select_month);
            }
            if (select_day < 10) {
                select_daystr = "0" + select_day;
            } else {
                select_daystr = String.valueOf(select_day);
            }
            String select_date = select_year + "-" + select_monthstr + "-" + select_daystr;
            mTvSelectTime.setText(select_date);
            // 清除缓存。
            SharedPreferences.Editor editor = datepreferences.edit();
            editor.remove("1_select_day");
            editor.remove("1_select_month");
            editor.remove("1_select_year");
            editor.commit();
        }
    }

    private long mLastClickTime = 0L;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 500) {   // 800
            return true;
        }
        return false;
    }

    private void setCurDate(String time) {
        if (Utils.isDe()) {
            time = Utils.dateInversion(time);
        }
        mTvSelectTime.setText(time);
    }

    private void judgmentTemperatureDB() {   // K3实时心率 数据量 大 ，且实时测量时，测量频率大
        if (db == null) {
            db = DBHelper.getInstance(this);
        }

        try {
//            mean_heart = 0;
            String strDate = mTvSelectTime.getText().toString();  // 获取当前时间
            int times = 0;
            Query query = null;
            // todo ---- 现改为查询时按时间戳排序(加上设备MAC查询)
            query = db.getTemperatureDao().queryBuilder()
                    .where(TemperatureDao.Properties.Date.eq(strDate)).orderDesc(TemperatureDao.Properties.BinTime)    // orderAsc：升序     orderDesc ：降序
                    .where(TemperatureDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();  //TODO 降序
            final List<Temperature> list = query.list();    //TODO 降序
            mData.clear();
            if(list!=null)
            {
                int size = list.size();
                if (list != null && size > 0) {
                    mData.addAll(list);
                    String temperatureValue = mData.get(0).getTemperatureValue();
                    if(!TextUtils.isEmpty(temperatureValue)&&Utils.isNumeric(temperatureValue))
                    {
                        mTvTest.setText(temperatureValue+getResources().getString(R.string.temperature_unit));
                    }
                    else
                    {
                        mTvTest.setText(getResources().getString(R.string.temp_test));
                    }
                }
                else
                {
                    mTvTest.setText(getResources().getString(R.string.temp_test));
                }
            }
            else
            {
                mTvTest.setText(getResources().getString(R.string.temp_test));
            }

            mAdapter.notifyDataSetChanged();
//            Query query2 = db.getHearDao().queryBuilder()
//                    .where(HearDataDao.Properties.Date.eq(strDate)).orderAsc(HearDataDao.Properties.Times)    // orderAsc：升序     orderDesc ：降序
//                    .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();  //TODO 升序
//            final List<HearData> listAsc = query2.list();      //TODO 升序
//            Collections.reverse(list);

            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Temperature> data = (ArrayList<Temperature>) mData.clone();
                    Collections.reverse(data);
                    getTemperatureFrData(data);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private synchronized void getTemperatureFrData(List<Temperature> list) {
        //数据图表显示
        ArrayList<ChartViewTemperatureData> arrHeart = new ArrayList<>();
        if (list != null && list.size() >= 1) {  // 有本地数据
            ChartViewTemperatureData heartData;
            String mheartBinTime = "";
            List<Temperature> listok = new ArrayList<>();
            listok.addAll(list);
            String tempheartTime = "";
            Calendar calendar = Calendar.getInstance();
            for (int j = 0; j < listok.size(); j++) {     // todo ---- 当前时间点的所有数据
                Temperature temperature = listok.get(j);   //前一个心率数据
                //todo --- 遍历完成，得到相同分钟数的心率数据集合
                    heartData = new ChartViewTemperatureData();
                    if(!TextUtils.isEmpty(temperature.getBinTime()))
                    {
                        if(Utils.isNumeric(temperature.getBinTime()))
                        {
                            calendar.setTimeInMillis(Long.parseLong(temperature.getBinTime()));
                            String date = mSimpleDateFormat.format(calendar.getTime());
                            heartData.Hour = date.split(" ")[1].substring(0, 5);   // // 2017-08-16 10:57:14 ------    取到时分值
                            heartData.x = Integer.parseInt(date.split(" ")[1].split(":")[0]);   // 心率时间值    // 10点
                            heartData.value = Float.parseFloat(temperature.getTemperatureValue());// 心率值
                            heartData.avghata = Float.parseFloat(temperature.getTemperatureValue());    // 平均         -----  getHeartbeat
                            String time = date.substring(11, 16);
                            arrHeart.add(heartData);
                        }

                    }
            }
        }
        String mheartHour = "";
        mChartData = new ArrayList<>();
        for (int j = 0; j < arrHeart.size(); j++) {
                        mChartData.add(arrHeart.get(j));
        }

        // todo 显示lineChartView(当前线程在子线程)
        if (null != mChartData && mChartData.size() > 0) {
            chartVisibility(true);
            float min = 0;
            float max = 0;
            if (null != mChartData && mChartData.size() >= 2) {
                ArrayList<Float> tempData = new ArrayList<>();
                for (int b = 0; b < mChartData.size(); b++) {
                    tempData.add(mChartData.get(b).getAvghata());
                }
                min = Float.valueOf(Collections.min(tempData) + "");
                max = Float.valueOf(Collections.max(tempData) + "");
            } else {
                //第一次画心率数据应该画点
                if (null != mChartData) {
                    min = mChartData.get(0).getAvghata();
                    max = mChartData.get(0).getAvghata();
                }
            }
            max = (float) Math.rint(max);
            min = (float) Math.rint(min);
            int proportion = (int) ((max - min) / 7);
            if (proportion == 0) {
                proportion = 1;
            } else if (proportion >= 2.5) {
                proportion = proportion * 2;
            } else {
                proportion = proportion * 4;
            }
            Log.e("panda", "proportion = " + proportion);
            int minHearttbY = (int) (min - proportion);
            int maxHearttbY = (int) (max + proportion);
            if (proportion == 1) {
                maxHearttbY = maxHearttbY + 1;
            }

            getAxisXYLables(minHearttbY, maxHearttbY, mChartData.size(), mChartData, proportion);//获取x轴的标注
        } else {
            chartVisibility(false);
        }
    }

//    private synchronized void getTemperatureFrData(List<Temperature> list) {
//        //数据图表显示
//        ArrayList<ChartViewTemperatureData> arrHeart = new ArrayList<>();
//        if (list != null && list.size() >= 1) {  // 有本地数据
//            ChartViewTemperatureData heartData;
//            String mheartBinTime = "";
//            List<Temperature> listok = new ArrayList<>();
//
//            for (int j = 0; j < list.size(); j++) {
//                if (!list.get(j).getBinTime().equals(mheartBinTime)) {
//                    mheartBinTime = list.get(j).getBinTime();
//                    if (listok.size() > 0) {
//                        boolean isExist = false;
//                        for (Temperature data : listok) {
//                            if (data.getBinTime().equals(list.get(j).getBinTime())) {
//                                isExist = true;
//                                break;
//                            }
//                        }
//                        if (!isExist) {
//                            listok.add(list.get(j));
//                        }
//                    } else {
//                        listok.add(list.get(j));
//                    }
//                }
//            }
////            listok.addAll(list);
//            String tempheartTime = "";
//            for (int j = 0; j < listok.size(); j++) {     // todo ---- 当前时间点的所有数据
//                Temperature runDB = listok.get(j);   //前一个心率数据
//                List<Temperature> mlist = new ArrayList<>();   // 每一个for循环取一组相同开始时间的心率数据
//                String bgTime = runDB.getBinTime();
//                if (!bgTime.equals(tempheartTime)) { // bgTime.equals(tempheartTime)   ---- StringUtils.isEmpty(tempheartTime)
//                    tempheartTime = bgTime;
//                    mlist.add(runDB);
//                    for (Temperature data : listok) {
//                        if (!runDB.getBinTime().equals(data.getBinTime())) {
//                            String oneTime = runDB.getBinTime().substring(0, 16); //相同开始时间 心率集合数据的第一条数据
//                            String listDataTime = data.getBinTime().substring(0, 16);
//                            if (oneTime.equals(listDataTime)) {  // 是同一分钟的心率数据
//                                mlist.add(data);
//                            }
//                        }
//                    }
//                }
//
//                //todo --- 遍历完成，得到相同分钟数的心率数据集合
//                if (mlist.size() < 2) {   // 同一分钟只有一条数据
//                    heartData = new ChartViewTemperatureData();
//                    heartData.x = Integer.parseInt(mlist.get(0).getBinTime().split(" ")[1].split(":")[0]);   // 心率时间值    // 10点
//                    heartData.value = Integer.parseInt(mlist.get(0).getTemperatureValue());// 心率值
//                    heartData.Hour = mlist.get(0).getBinTime().substring(11, 16);   // // 2017-08-16 10:57:14 ------    取到时分值
//                    heartData.maxhata = Integer.valueOf(mlist.get(0).getTemperatureValue());   // 最大   -----  getHeartbeat
//                    heartData.manhata = Integer.valueOf(mlist.get(0).getTemperatureValue());    // 最小          ------ getHeartbeat
//                    heartData.avghata = Integer.valueOf(mlist.get(0).getTemperatureValue());    // 平均         -----  getHeartbeat
//                    String time = mlist.get(0).getBinTime().substring(11, 16);
//                    Log.e("TIME", time + "********" + runDB.toString());
//                    arrHeart.add(heartData);
//                } else {   // 同一分钟有多条数据
//                    heartData = new ChartViewTemperatureData();
//                    heartData.x = Integer.parseInt(mlist.get(0).getBinTime().split(" ")[1].split(":")[0]);   // 心率时间值    // 10点
//                    heartData.value = Integer.parseInt(mlist.get(0).getTemperatureValue());// 心率值
//                    heartData.Hour = mlist.get(0).getBinTime().substring(11, 16);   // // 2017-08-16 10:57:14 ------    取到时分值
//                    int mean_heart = 0;
//                    int highest_heart = 0;
//                    int minimum_heart = 11110;
//                    for (int v = 0; v < mlist.size(); v++) {
//                        highest_heart = Math.max(Integer.parseInt(mlist.get(v).getTemperatureValue()), highest_heart);   // 最大
//                        minimum_heart = Math.min(Integer.parseInt(mlist.get(v).getTemperatureValue()), minimum_heart);   // 最小
//                        mean_heart += Integer.parseInt(mlist.get(v).getTemperatureValue());       // 平均
//                    }
//                    mean_heart = mean_heart / mlist.size();
//                    heartData.maxhata = highest_heart;   // 最大   -----  getHeartbeat
//                    heartData.manhata = minimum_heart;    // 最小          ------ getHeartbeat
//                    heartData.avghata = mean_heart;    // 平均         -----  getHeartbeat
//                    arrHeart.add(heartData);
//                }
//            }
//        }
//        String mheartHour = "";
//        mChartData = new ArrayList<>();
//        for (int j = 0; j < arrHeart.size(); j++) {
//            if (!arrHeart.get(j).getHour().equals(mheartHour)) {
//                mheartHour = arrHeart.get(j).getHour();
//                if (mChartData.size() > 0) {
//                    boolean isExist = false;
//                    for (ChartViewTemperatureData data : mChartData) {
//                        if (data.getHour().equals(arrHeart.get(j).getHour())) {
//                            isExist = true;
//                            break;
//                        }
//                    }
//                    if (!isExist) {
//                        mChartData.add(arrHeart.get(j));
//                    }
//                } else {
//                    mChartData.add(arrHeart.get(j));
//                }
//            }
//        }
//
//        // todo 显示lineChartView(当前线程在子线程)
//        if (null != mChartData && mChartData.size() > 0) {
//            chartVisibility(true);
//            int min = 0;
//            int max = 0;
//            if (null != mChartData && mChartData.size() >= 2) {
//                ArrayList<Integer> tempData = new ArrayList<>();
//                for (int b = 0; b < mChartData.size(); b++) {
//                    tempData.add(mChartData.get(b).getAvghata());
//                }
//                min = (((Integer.valueOf(Collections.min(tempData) + "")) / 10) * 10);
//                max = Integer.valueOf(Collections.max(tempData) + "");
//            } else {
//                //第一次画心率数据应该画点
//                if (null != mChartData) {
//                    min = mChartData.get(0).getAvghata();
//                    max = mChartData.get(0).getAvghata();
//                }
//            }
//            int proportion = (max - min) / 7;
//            if (proportion == 0) {
//                proportion = 1;
//            } else if (proportion >= 2.5) {
//                proportion = proportion * 2;
//            } else {
//                proportion = proportion * 4;
//            }
//            Log.e("panda", "proportion = " + proportion);
//            int minHearttbY = min - proportion;
//            int maxHearttbY = max + proportion;
//            if (proportion == 1) {
//                maxHearttbY = maxHearttbY + 1;
//            }
//            getAxisXYLables(minHearttbY, maxHearttbY, mChartData.size(), mChartData, proportion);//获取x轴的标注
//        } else {
//            chartVisibility(false);
//        }
//    }

    private synchronized void getAxisXYLables(int minY, int maxY, int size, ArrayList<ChartViewTemperatureData> heart, int proportion) {
        try {
            List<AxisValue> mAxisXValues = new ArrayList<>();
            List<AxisValue> mAxisYValues = new ArrayList<>();
            ArrayList<PointValue> mPointValues = new ArrayList<>();
            //按类设置X的比例
            if (heart.size() == 1) {    // youxiao_size==1
                String[] labelsX = new String[2];//X轴的标注
                labelsX[0] = heart.get(0).getHour();
                labelsX[1] = "0";
                float[] valuesY = new float[2];//图表的数据点
                valuesY[0] = heart.get(0).getAvghata();
                valuesY[1] = 0;
                for (int i = 0; i < 2; i++) {
                    mAxisXValues.add(new AxisValue(i).setLabel(labelsX[i]));    // X轴的值
                }
                //每一个点的值
                for (int i = 0; i < 2; i++) {
                    mPointValues.add(new PointValue(i, valuesY[i]));
                }
            } else {
                String[] labelsX = new String[heart.size()];//X轴的标注
                float[] valuesY = new float[heart.size()];//图表的数据点
                //取x轴的值及点的位置
                if (null != SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC)) {
                    if (!SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC).toString().contains("X2")) { //todo X2设备数据有误差???
                        for (int i = 0; i < heart.size(); i++) {
                            labelsX[i] = heart.get(i).getHour();
                            valuesY[i] = heart.get(i).getAvghata();
                        }
                    } else {
                        for (int i = 0; i < heart.size(); i++) {
                            labelsX[i] = heart.get(i).getHour();
                            valuesY[i] = heart.get(i).getAvghata();
                        }
                    }
                }

                for (int i = 0; i < heart.size(); i++) {
                    mAxisXValues.add(new AxisValue(i).setLabel(labelsX[i]));    // X轴的值
                }
                //每一个点的值
                for (int i = 0; i < heart.size(); i++) {
                    mPointValues.add(new PointValue(i, valuesY[i]));
                }
            }
            for (int i = (int) minY; i <= maxY; i += proportion) {
                mAxisYValues.add(new AxisValue(i).setLabel(i + ""));//Y轴的值
            }

            LineChartData data = new LineChartData();//用于绑定数据给mChart
            initBindData(data);//设置相关属性

            ArrayList<Line> lines = new ArrayList<>();
            initLine(heart, mPointValues, lines);//设置line的属性
            //线
            data.setLines(lines);
            //坐标轴X
            Axis axisX = new Axis(); //X轴
            initAxisX(axisX, mAxisXValues);//设置X轴相关属性
            data.setAxisXBottom(axisX);//x 轴在底部
            //坐标轴Y
            Axis axisY = new Axis();
            initAxisY(mAxisYValues, axisY);//设置Y轴相关属性
            data.setAxisYLeft(axisY);

            initLineChartView();
            mChart.setLineChartData(data);
//            mChart.setVisibility(View.VISIBLE);
            Viewport v = new Viewport(mChart.getMaximumViewport());
            v.bottom = minY;
            v.top = (float) (maxY+0.5);
            //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
            mChart.setMaximumViewport(v); // todo   ------
            if (heart.size() > 7) {
                v.left = heart.size() - 7;
                v.right = heart.size() - 1;
            }
            mChart.setCurrentViewport(v);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChart.invalidate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBindData(LineChartData data) {
        data.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
        data.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
        data.setValueLabelTextSize(8);
        data.isValueLabelBackgroundAuto();
    }

    private void initLineChartView() {
        mChart.setInteractive(true);
        mChart.setZoomType(ZoomType.HORIZONTAL);
        mChart.setMaxZoom((float) 4);//最大方法比例
        mChart.setZoomEnabled(false);
        mChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }

    private void initAxisY(List<AxisValue> mAxisYValues, Axis axisY) {
        axisY.setHasLines(true);
        axisY.setTextSize(12);
        axisY.setTextColor(0xff4c5157);
        axisY.setLineColor(0xff4c5157);
        axisY.setValues(mAxisYValues);
    }

    private void initAxisX(Axis axisX, List<AxisValue> mAxisXValues) {
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(0xff4c5157);  //设置字体颜色
        axisX.setTextSize(12);
        axisX.setHasLines(true);
        axisX.setLineColor(0xff4c5157);
        //axisX.setMaxLabelChars(0);
        axisX.setValues(mAxisXValues);
    }

    private void initLine(ArrayList<ChartViewTemperatureData> heart, ArrayList<PointValue> mPointValues, ArrayList<Line> lines) {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#Fefb28"));  //折线的颜色   ---- 最高
        line.setStrokeWidth(3);//设置节点大小
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线


        line.setFilled(false);//是否填充曲线的面积
        if (heart.size() == 1) {
            line.setPointRadius(3);// 设置节点半径
            line.setHasLines(false);//是否用线显示。如果为false 则没有曲线只有点显示
            line.setHasLabels(false);//曲线的数据坐标是否加上备注
            line.setHasPoints(true);//是否显示圆点
        } else {
            line.setHasPoints(false);
            line.setPointRadius(1);// 设置节点半径
            line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line.setHasLabels(false);//曲线的数据坐标是否加上备注
        }
        line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数
        line.setAreaTransparency(20);//透明度
//                line.setEffectiveLength(youxiao_size);
        lines.add(line);
    }

    private void chartVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    mChart.setVisibility(View.VISIBLE);
                    mTvNoData.setVisibility(View.INVISIBLE);
                } else {
                    mChart.setVisibility(View.INVISIBLE);
                    mTvNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class thbroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // todo ---0922  各实时数据更新  ---- 发送的广播 需要区分
            if (intent.getAction().equals(MainService.ACTION_SYNFINSH) || intent.getAction().equals(MainService.ACTION_CHANGE_WATCH)
                    || intent.getAction().equals("android.intent.action.DATE_CHANGED")) {  //TODO -----  报告页面收到数据同步成功的广播了
                judgmentTemperatureDB();
            }
            if (intent.getAction().equals(MainService.ACTION_SYNARTHEART) && BTNotificationApplication.isSyncEnd) {    // todo  --- 同步数据完成 if(BTNotificationApplication.isSyncEnd){
                judgmentTemperatureDB();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(final String key) {
        if(key.equals("updateTemperature"))
        {
            judgmentTemperatureDB();
        }
    }

}
