package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.adapter.NewTimelineAdapter;
import com.szkct.adapter.NewTimelineWhiteAdapter;
import com.szkct.adapter.ViewPagerAdapter;
import com.szkct.map.utils.Util;
import com.szkct.weloopbtsmartdevice.data.ChartViewCoordinateData;
import com.szkct.weloopbtsmartdevice.data.HealthReportContantsData;
import com.szkct.weloopbtsmartdevice.data.ReportSleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.Bloodpressure;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Oxygen;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.BloodpressureDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.OxyDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDao;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.ScreenshotsShare;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.ThreadPoolManager;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.ChartView;
import com.szkct.weloopbtsmartdevice.view.DataReportSleepColumchartView;
import com.szkct.weloopbtsmartdevice.view.DataReportStepColumchartView;
import com.szkct.weloopbtsmartdevice.view.HealthFragment;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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

import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_OXYGEN;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.ISSYNWATCHINFO;
import static com.szkct.weloopbtsmartdevice.util.Utils.dateInversion;


public class PresentationActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断主题。
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        dataReportView = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.activity_presentation, null);   // 数据报告页面
        setContentView(dataReportView);

        pageindex=getIntent().getExtras().getInt("index",0);//设置第几个页面
        mCurrentTimeStr=getIntent().getExtras().getString("time");//日期是几号
//        registerBroad();
//        EventBus.getDefault().register(this);
        init();
        dateInit();   // 这里才开始初始化数据

        registerBroad();
        EventBus.getDefault().register(this);
    }


    private static final String TAG = "HealthFragment";
    private View HealthView;
//    private RelativeLayout finish_re, finish_re2;
    private LinearLayout mPersonalInfo;  // , mPartOne, mPartTow, mPartThree, mPartFour
//    private ImageView mHealthBody, mScanLine, mScanBox;
    private TextView mSure; //  , mSexTv, mAgeTv, mHealthScordNumTv, mBodyAgeNumTv,  mStatureTv, mWeightTv, mCardiopulmonaryTv, mBeginTestTv, mBMITv, mBodyFatNumTv, mBodyFatTextTv, mVitalCapacityTv, mHeartRateTv
    private int mHeartRate;
    private String[] mSexArr = new String[2];
    private int  mAgeValue;   // mSexValue  , mStatureValue, mWeightValue
    private int mHeartRateValue, mVitalCapacityValue, mBMIValue, mBodyAgeValue,mHealthLevel;
//    private double mBaseBMI;
//    private int mBaseHeartRate;
//    private int mBaseVitalCapacity;
    private SimpleDateFormat mSimpleDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
    private ArrayList<RunData> arrRunDataLastTowWeeks = null;
    private DBHelper db = null;
    private View mUserInfo;
    private NumberPicker mAgePicker, mSexPicker, mStaturePicker, mWeightPicker;
    private NumberPicker stature_picker_imperial, stature_inch_picker_imperial, weight_picker_imperial;
//    private LinearLayout personal_info_sex_ll, personal_info_age_ll, personal_info_stature_ll, personal_info_weight_ll;
//    private int mStatureFootValue, mStatureInchValue, mWeightPoundValue;
//    private LinearLayout healthLinearLayout;
    private LinearLayout health_report_weight;
    private LinearLayout health_report_stature;
    private LinearLayout health_report_stature_ll;
    private LinearLayout health_report_weight_ll;
    private TranslateAnimation mScanAnimation;
    private PopupWindow mPopupWindow = null;
    private Toast mToast;
    private String mBodyFatValue;
    thbroadcast vb;
    TextView tv_nodata1,tv_nodata2,tv_nodata3,tv_nodata4,tv_nodata5;
    private boolean isDateUpDown = true;
    private String mCurrentTimeStr = "";
    private final int SYNCTIME = 6;
    private final int UPUERRUNINFO = 8;
    private final int UPUERSLEEPINFO = 9;
    private final int UPUERHEARTINFO = 11;
    private final int UPUERTIMELINEINFO = 12;
    private final int CLEARSPORT = 13;
    private final int CLEARSLEEP = 14;
    private final int BBBSNYBTDATAFAIL = 15;
    private final int UPOxygen_INFO = 16;//血氧状态
    private final int CLEAR_Oxygen = 17;//血氧无用数据

    private final int UPOxyBloodpressure_INFO = 18;//血压状态
    private final int CLEAR_Bloodpressure = 19;//血压无用数据
    private final int CLEAR_UERHEARTINFO = 20;//血压无用数据

    private final int REFRESHHEARTINFO = 23;   // refresh心率
    private final int REFRESHXUEYANGINFO = 24;   // refresh血氧    REFRESHXUEYANGINFO

    private final int REFRESHXUEYAINFO = 25;


    private SharedPreferences datePreferences;
    private String select_monthstr, select_daystr;
    private TextView cb_navigation_sport, cb_navigation_sleep, cb_navigation_heart;
    LineChartData dataxin = new LineChartData();
    // 时间轴与数据报告部分
    private View dataReportView,pagerView;  //   timeLineView
    private List<View> views = null;//
    private TextView synchronizationTv;
    private ImageButton btn_share;

    private DataReportStepColumchartView stepColumchartView;  // 活动图表
    private DataReportSleepColumchartView sleepColumchartView; // 睡眠图表
    private ChartView heartRateChartView;       // 心率图表

    private TextView mReportStepTv, mReportSleepHourTv, mReportSleepMinTv;
    private ImageView reportDateDownturning, reportDateUpturning;
    private TextView mReportCurdatetv;
    private TextView mReportSleepNotSupport ;  // mReportStepNotSupport  mReportHeartNotSupport

//    private TextView xueya_nodata_text,report_oxy_support;

    private LinearLayout mDataReportSleepChartLl;
//    private TextView mTimelineCurdatetv;
    private String change_date, uptime_str;
//    private ListView timeLineListView;
//    private View listViewHead, listViewFoot;
//    private ImageView timelineDateDownturning, timelineDateUpturning;
//    private NewTimelineAdapter timelineAdapter;
//    private NewTimelineWhiteAdapter whiteTimelineAdapter;
//    private List<Map<String, Object>> timelineList = new ArrayList<Map<String,Object>>();

    private List<ChartViewCoordinateData> stepData = new ArrayList<ChartViewCoordinateData>();  // 计步的图表数据
    List<ChartViewCoordinateData> stepData_effectives = new ArrayList<ChartViewCoordinateData>();  // 计步图表的有效数据
    private int maxStepValue = 0;    // 计步图表数据的最大 步数值
    ArrayList<ReportSleepData> arrSleepDatas = new ArrayList<>();
    private int mean_heart = 0;
    private LinearLayout ll_sport,ll_sleep_rate,ll_heart_rate,ll_oxy_rate,ll_xieya;
    ListView listview_detail_step,listview_detail_heart,listview_detail_bloodpressure,listview_detail_oxygen;//listview详情
    MyListviewDetailAdapter mMyListviewDetailAdapter0,mMyListviewDetailAdapter2,mMyListviewDetailAdapter3,mMyListviewDetailAdapter4;
    View listview_detail_sleep;//睡眠报告下方
    TextView tv_sleep_time1,tv_sleep_time2,tv_sleep_time3,tv_sleep_time4,tv_sleep_time5,tv_sleep_time6;//睡眠的时间
    private MyLoadingDialog myLoadingDialog;
    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");

    public View datareport_sc;

    private TextView mReportHeartRateTv,heighthata,lowhata;
    private TextView systolic_pressure,diastolic_pressure,pressure;//收缩压，舒张压

    /***************************心率***************************************/
    private ArrayList<ChartViewCoordinateData> heart= new ArrayList<>();//图表心率

//    ArrayList<ChartViewCoordinateData> listHeart = null;  //数据列表心率
    private List<HearData> listHeart = new ArrayList<>();

    String[] labelsX ;//X轴的标注
    String[] labelsXxin ;//X轴的标注
    int[] valuesY ;//图表的数据点
    int[] valuesYxin ;//图表的数据点
    int[] valuesY_avg ;//图表的数据点
    int[] valuesY_min ;//图表的数据点
    int[] valuesd ;//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<PointValue> mPointValuesb = new ArrayList<PointValue>();
    private List<PointValue> mPointValuesc = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();
    // private List<AxisValue> mAxisYValues2 = new ArrayList<AxisValue>();
    private LineChartView lineChart;
    private boolean hasAxesY = true; //是否需要Y坐标
    private boolean hasLines = true;//是否要折线连接
    private boolean hasPoints = true;//数据点是否要标注
    private ValueShape shape = ValueShape.CIRCLE;//数据标注点形状,这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
    private boolean isFilled = false;//是否需要填充和X轴之间的空间
    private boolean isCubic = false;//曲线是否平滑，即是曲线还是折线
    private boolean hasLabels = false;//数据点是否显示数据值
    private boolean hasLabelForSelected = true;//点击数据坐标提示数据（设置了这个hasLabels(true);就无效）
    private boolean hasTiltedLabels = false;  //X坐标轴字体是斜的显示还是直的，true是斜的显示
    private String lineColor = "#FD730E";//折现颜色(#FF0000红色)
    private String lineColorhuang = "#Fefb28";//很黄很
    private String lineColorlan = "#24bfff";//蓝色
    Line line;//线条
    Line  linexin;//线条
    Line  lineOXY;//线条
    List<Line> lines;//线集合
    List<Line> linesxin;//线集合
    List<Line> linesOXY;//线集合
    private int textColor = Color.WHITE;//设置字体颜色
    ArrayList<Bloodpressure> bloodpressure = new ArrayList<>();   //血压详情数据 list
    ArrayList<Bloodpressure> Newbloodpressure = new ArrayList<>();   //血压详情数据 血压图表

    /***************************血氧***************************************/
    ArrayList<ChartViewCoordinateData> Oxyvalue=null;//血氧
    String[] OxlabelsX ;//X轴的标注
    int[] OxvaluesY ;//图表的数据点
    int[] Oxvaluesd ;//图表的数据点
    private List<PointValue> OXmPointValues;
    private List<PointValue> OXmPointValuesb;
    private ArrayList<Oxygen> BloodpressureList = new ArrayList<>();   //血氧详情数据  listView 用到的数据
    private ArrayList<Oxygen> NewBloodpressureList = new ArrayList<>();  //血氧详情数据  报告图标 用到的数据
    private List<AxisValue> OXmAxisXValues ;
    private List<AxisValue> OXmAxisYValues;

    private LineChartView OxylineChart;//血氧

    private LineChartView xiayalineChart;//血压
    HearData runDB;
    boolean isDataAdd = false;

    LinkedList AA=new LinkedList ();
    LinkedList BB=new LinkedList();
    LinkedList cc=new LinkedList();//血氧值取最大最小值
    LinkedList dd=new LinkedList();
    LinkedList ee=new LinkedList();//心率值取最大最小值
    LinkedList ff=new LinkedList();

    private int minHearttbY = 0 ;
    private int maxHearttbY = 0;
    private int sizeHearttb = 0;

    private int minXueyangY = 0 ;
    private int maxXueyangY = 0;
    private int sizeXueyangtb = 0;

    private int minXueyaY = 0 ;
    private int maxXueyaY = 0;
    private int sizeXueyatb = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if(null!=event.getMessage()){
            if(event.getMessage().equals("updata_Bloodpressureone")){ //实时血压  updata_Bloodpressure    
//                judgmentBloodpressureDaoDB();//更新血压
            } else if(event.getMessage().equals("updata_XIEYANGpressuretwo")){
//                judgmentOxygenDB();//更新血氧
            }else if(event.getMessage().equals("updata_xinlv")){
                //judgmentHeartDB();
            } else if("update_view".equals(event.getMessage())){
                initView();
            } else if("unBond".equals(event.getMessage())){
                initView();
            }
        }
    }

    private void registerBroad() {
        if(null == vb){
            vb = new thbroadcast();
        }
        IntentFilter viewFilter = new IntentFilter();
        viewFilter.addAction(MainService.ACTION_SYNFINSH);   //报告页面 注册 手表数据同步 成功的 广播
        viewFilter.addAction(MainService.ACTION_MACCHANGE);
        viewFilter.addAction(MainService.ACTION_USERDATACHANGE);

        viewFilter.addAction(MainService.ACTION_CHANGE_WATCH);

        viewFilter.addAction(MainService.ACTION_SYNFINSH_SUCCESS);

//        viewFilter.addAction(MainService.ACTION_SSHEARTFINSH);  // 实时心率
        viewFilter.addAction(MainService.ACTION_SYNARTHEART);

        viewFilter.addAction(MainService.ACTION_SYNARTBP);     //TODO --   实时血压

        viewFilter.addAction(MainService.ACTION_SYNARTBO);   //TODO --   实时血氧

        viewFilter.addAction("android.intent.action.DATE_CHANGED");

        registerReceiver(vb, viewFilter);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /*case 1:
                    mPartOne.setVisibility(View.VISIBLE);
                    setResultAnimation(mPartOne);
                    break;

                case 2:
                    mPartTow.setVisibility(View.VISIBLE);
                    setResultAnimation(mPartTow);
                    break;

                case 3:
                    mPartThree.setVisibility(View.VISIBLE);
                    setResultAnimation(mPartThree);
                    break;

                case 4:
                    mPartFour.setVisibility(View.VISIBLE);
                    setResultAnimation(mPartFour);
                    personal_info_sex_ll.setClickable(true);
                    personal_info_age_ll.setClickable(true);
                    personal_info_stature_ll.setClickable(true);
                    personal_info_weight_ll.setClickable(true);
                    mHealthBody.setClickable(true);
                    break;

                case 5:
                    finish_re.setVisibility(View.VISIBLE);
                    finish_re2.setVisibility(View.VISIBLE);
                    break;*/

                case SYNCTIME:    // 根据当前日期，初始化各相关的数据
                    //clearABCD();//切换成功才清除
                    mCurrentTimeStr = (String) msg.obj;
                    setCurDate(mCurrentTimeStr);
                    if(ISSYNWATCHINFO) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                judgmentRunDB();
                                judgmentSleepDB();
                            }/////
                        }).start();

                        if (BLOOD_OXYGEN) {
                            judgmentOxygenDB();//血氧// TODO  ----血氧界面？
                        }
                        if (BLOOD_PRESSURE) {
                            judgmentBloodpressureDaoDB();//更新血压// TODO  ----血压界面？
                        }
                        judgmentHeartDB();

                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                judgmentRunDB();
                                judgmentSleepDB();
                            }/////
                        }).start();

                        judgmentOxygenDB();//血氧
                        judgmentBloodpressureDaoDB();//更新血压
                        judgmentHeartDB();
                    }
                    break;

                /*case UPDATEDATE:
                    mCurrentTimeStr = (String) msg.obj;
                    mReportCurdatetv.setText(mCurrentTimeStr);
                    mTimelineCurdatetv.setText(mCurrentTimeStr);

                    break;*/

                case UPUERRUNINFO:     //  TODO  ---- 对应于 活动报告的图表
                    Bundle bundle = (Bundle) msg.obj;
                    //int step = (int) msg.obj;  // 总步数值
                    int step = bundle.getInt("step",0);
                    ArrayList<ChartViewCoordinateData> stepList = (ArrayList<ChartViewCoordinateData>) bundle.getSerializable("stepList");
                    if(step > 200000){
                        step = 52007;
                    }else if(step < 0){
                        step = 0;
                    }
                    stepData_effectives.clear();
                    stepData_effectives.addAll(stepList);
                    stepColumchartView.updateView(stepData, maxStepValue);    //  1：stepData  分段步数的集合 计步的图表数据 2： 最大步数值
                    mReportStepTv.setText(step + " ");
                    setMyAdpater();
                    break;

                case UPUERSLEEPINFO:    // 睡眠数据     TODO  ---- 对应于 睡眠报告的图表
                    arrSleepDatas.clear();
                    arrSleepDatas.addAll((ArrayList<ReportSleepData>) msg.obj);     // 睡眠报告 集合数据
                    int sleephour = 0,sleepfen = 0;
                    int qianshui=0,shengshui=0,starttime=0,endtime=0;
                    if(arrSleepDatas != null && arrSleepDatas.size() > 0){
                        int allSleepData = 0;
                        for(ReportSleepData data :arrSleepDatas){
                            allSleepData += data.getSleepTime();  //分钟数
                            if(!data.isDeepSleep()) qianshui+=data.getSleepTime();//浅度睡眠
                            else    shengshui+=data.getSleepTime();//深睡
                        }
                        starttime=arrSleepDatas.get(0).getStartTime()+21*60;//9点开始计算睡眠的
                        endtime=starttime+allSleepData;
                        if(starttime>=24*60)    starttime-=24*60;//显示正确24小时
                        if(endtime>=24*60)      endtime-=24*60;
                        sleephour = allSleepData/60;
                        sleepfen = allSleepData%60;

                        if(sleephour > 12){
                            sleephour = 11;
                        }else if(sleephour == 12 && sleepfen > 0){
                            sleepfen = 0;
                        }
                        
                        mReportSleepHourTv.setText(sleephour + "");    // 睡眠的总小时数
                        mReportSleepMinTv.setText(sleepfen + "");      // 睡眠的分钟数
                    }else {
                        mReportSleepHourTv.setText("0");    // 睡眠的总小时数
                        mReportSleepMinTv.setText("0");      // 睡眠的分钟数
                    }
                    sleepColumchartView.updateView(arrSleepDatas);    // TODO ---- 睡眠图表 数据的更新
                    setSleepText(tv_sleep_time1, sleephour + "", sleepfen + "", true);   // 总睡眠时间
                    setSleepText(tv_sleep_time2,qianshui/60+"",qianshui%60+"",true);    // 浅睡时长
                    setSleepText(tv_sleep_time3,shengshui/60+"",shengshui%60+"",true);  // 深睡时长
                    setSleepText(tv_sleep_time4, starttime / 60 + "", starttime % 60 + "", false);   // 入睡时间

                    if(arrSleepDatas.size() <= 0){
                        setSleepText(tv_sleep_time5,endtime/60+"",endtime%60+"",false);
                        setSleepText(tv_sleep_time6, "0","0",false);
                    }else {
                        String endtimeXL = arrSleepDatas.get(arrSleepDatas.size() - 1).getEndTimeStr();
                        if(!StringUtils.isEmpty(endtimeXL)){
                            setSleepText(tv_sleep_time5, endtimeXL.substring(0, 2), endtimeXL.substring(3,5),false);       // 醒来时间

                            //todo 入睡时间
                            int rsShi = starttime / 60;
                            int rsFen = starttime % 60;
                            //todo  总有效睡眠时间
                            int sleepOkData = sleephour*60 + sleepfen;
                            //todo 醒来时间
                            int xlShi = Integer.valueOf(endtimeXL.substring(0, 2));
                            int xlFen = Integer.valueOf(endtimeXL.substring(3, 5));
                            int allSleepTime = 0;   // 总睡眠时间
                            if(xlShi > rsShi){
                                allSleepTime = (xlShi - rsShi)*60 + (xlFen - rsFen);
                            }else if(xlShi == rsShi){
                                if(xlFen >= rsFen){
                                    allSleepTime = (xlShi - rsShi)*60 + (xlFen - rsFen);  // 所有睡眠
                                }  // 不能小于
                            }else {// 醒来时 < 入睡时
                                if(rsShi >= 21 && xlShi <= 10){
                                    allSleepTime = (24 - rsShi)*60 - rsFen + xlShi*60 + xlFen;
                                }
                            }
                            if(allSleepTime > sleepOkData){
                                int okData = allSleepTime - sleepOkData;
                                setSleepText(tv_sleep_time6, okData/60+"",okData%60+"",true);
                            }else {
                                setSleepText(tv_sleep_time6, "0","0",false);
                            }
                        }else {
                            setSleepText(tv_sleep_time5,endtime/60+"",endtime%60+"",false);
                            setSleepText(tv_sleep_time6, "0","0",false);
                        }
//                    setSleepText(tv_sleep_time6, "0","0",false);    // todo ---  设置清醒时间
                    }

                    setMyAdpater();
                    break;

                case UPUERHEARTINFO:    // 心率数据
//                    if(null==heart){
                    heart.clear();
                    heart.addAll((ArrayList<ChartViewCoordinateData>) msg.obj);
                    // mReportHeartRateTv.setText(mean_heart + " ");    // 设置平均心率 值
                    if(null!=heart&&heart.size()>0){
                        lineChart.setVisibility(View.VISIBLE);
                        if(null !=heart && heart.size() >= 2){
                            for (int b=0;b<heart.size();b++){
                                ee .add(heart.get(b).getMaxhata());   // 最大
                                ff .add(heart.get(b).getManhata());   // 最小
                            }
                        }else{
                            //第一次画心率数据应该画点
                            if(null!=heart){
                                ee .add(heart.get(0).getMaxhata());   // 最大
                                ff .add(heart.get(0).getManhata());   // 最小
                            }
                        }
                        Log.e("mydta", Collections.max(ee) + "");
                        //  y最小    y最大    heart.size() ----所有心率的组数   heart---心率图表数据的集合  3---Linecount 线总数  1--- type(类型)    10 ----      lineChart
                        final int min = (((Integer.valueOf(Collections.min(ff) + "")) / 10) * 10);
                        final int max = Integer.valueOf(Integer.valueOf(Collections.max(ee) + ""));
//                         int min = (((Integer.valueOf(Collections.min(ff) + "")) / 10) * 10);
//                         int max = Integer.valueOf(Integer.valueOf(Collections.max(ee) + ""));
                        int proportion = (max - min)/7;
                        if(proportion == 0) {
                            proportion = 1;
                        }else if(proportion >= 2.5) {
                            proportion = proportion * 2;
                            Log.e(TAG, "proportion = " + proportion);
                        }else{
                            proportion = proportion * 4;
                            Log.e(TAG, "proportion = " + proportion);
                        }
                        Log.e(TAG,"proportion = " + proportion);

//                        final int proportionOk = proportion;
//                        minHearttbY = min - proportionOk ;
//                        maxHearttbY = max + proportionOk;

                        minHearttbY = min - proportion ;
                        maxHearttbY = max + proportion;
                        sizeHearttb = heart.size();
                        if(proportion == 1){
                            maxHearttbY = maxHearttbY + 1;
                        }
//                        ThreadPoolManager.getInstance().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                getAxisXYLables(min - proportionOk, max + proportionOk, heart.size(), heart, 3, proportionOk, lineChart);     // int minY,int maxY,int size,
//                            }
//                        });

                        getAxisXYLables(min - proportion, max + proportion, heart.size(), heart, 3, proportion, lineChart);//获取x轴的标注
//                        Log.e("mydta", heart + "");
                    }else {
                        lineChart.setVisibility(View.INVISIBLE);
                    }
                    //todo ---- 无心率数据需要刷新
//                    }
                    break;

                case REFRESHHEARTINFO:    // 刷新心率图表
                    LineChartData okdata = (LineChartData) msg.obj;

                    // 刷新需要用到的数据  ----  data ， minY ， maxY
                    // 以下为UI 刷新逻辑
                    lineChart.setInteractive(true);
                    lineChart.setZoomType(ZoomType.HORIZONTAL);
                    lineChart.setMaxZoom((float) 4);//最大方法比例
                    lineChart.setZoomEnabled(false);
                    lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
                    okdata.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
                    okdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                    okdata.setValueLabelTextSize(8);
                    okdata.isValueLabelBackgroundAuto();
                    lineChart.setLineChartData(okdata);
                    lineChart.setVisibility(View.VISIBLE);
                    Viewport v = new Viewport(lineChart.getMaximumViewport());
                    v.bottom = minHearttbY;
                    v.top = maxHearttbY ;
                    //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
                    lineChart.setMaximumViewport(v); // todo   ------

                    //这2个属性的设置一定要在lineChart.setMaximumViewport(v);这个方法之后,不然显示的坐标数据是不能左右滑动查看更多数据的
                    if(heart.size()>7){
                        v.left = sizeHearttb  - 7;
                        v.right = sizeHearttb  - 1;
                    }
                    lineChart.setCurrentViewport(v);
                    lineChart.invalidate();

                    break;

                case REFRESHXUEYANGINFO:   // 刷新血氧图表

                   /* LineChartData dataXueyang = (LineChartData) msg.obj;
                    //设置行为属性，支持缩放、滑动以及平移
                    OxylineChart.setInteractive(true);
                    OxylineChart.setZoomType(ZoomType.HORIZONTAL);
                    OxylineChart.setMaxZoom((float) 4);//最大方法比例
                    OxylineChart.setZoomEnabled(false);
                    OxylineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
                    dataXueyang.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
                    dataXueyang.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                    dataXueyang.setValueLabelTextSize(8);
                    dataXueyang.isValueLabelBackgroundAuto();
                    OxylineChart.setLineChartData(dataXueyang);
                    OxylineChart.setVisibility(View.VISIBLE);
                    Viewport vXueyang = new Viewport(OxylineChart.getMaximumViewport());
                    vXueyang.bottom = minXueyangY;
                    vXueyang.top = maxXueyangY;
                    OxylineChart.setMaximumViewport(vXueyang);
                    if(sizeXueyangtb>7){    // sizeXueyangtb     Oxyvalue.size()
                        vXueyang.left = sizeXueyangtb - 7;    // Oxyvalue.size()
                        vXueyang.right = sizeXueyangtb - 1;
                    }
                    OxylineChart.setCurrentViewport(vXueyang);
                    OxylineChart.invalidate();*/

//                    while(Oxyvalue.size()>Oxyvalue.size()){//移除无效数据      ???????????????????
//                        Oxyvalue.remove(Oxyvalue.size()-1);
//                    }

                    //////////////////////////////////////////////////////////////////////////////////////////////
//                    LineChartData okdata = (LineChartData) msg.obj;
                    // 刷新需要用到的数据  ----  data ， minY ， maxY
                    // 以下为UI 刷新逻辑
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    break;

                //血氧的图表数据
                case UPOxygen_INFO:
//                    BloodpressureList.clear();
//                    BloodpressureList.addAll((ArrayList<Oxygen>) msg.obj);
//                    Collections.sort(BloodpressureList);   //排序
                    if(NewBloodpressureList.size()>2||NewBloodpressureList.size()==2){   // NewBloodpressureList    BloodpressureList
                        cc.clear();
                        dd.clear();
                        for (int b=0;b<NewBloodpressureList.size();b++){
                            cc.add(Integer.valueOf(NewBloodpressureList.get(b).getOxygen()));
                            dd.add(Integer.valueOf(NewBloodpressureList.get(b).getOxygen()));
                        }
                        int man=(((Integer.valueOf(Collections.min(dd)+""))/10)*10);
                        int mind=Integer.valueOf(Integer.valueOf(Collections.max(cc)+""));
                        if((man-mind)<10){
                            //更新血氧的值
                            settingOxy(man - 5,mind+15,NewBloodpressureList,1,OxylineChart);
                        }else{
                            //更新血氧的值
                            if(null!=SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)){
                                if(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC).toString().contains("X2")){
                                    settingOxy(man-10,mind+20,NewBloodpressureList,5,OxylineChart);
                                }else{
                                    settingOxy(man-20,mind+40,NewBloodpressureList,5,OxylineChart);
                                }
                            }
                        }
                        //BloodpressureList.clear();
                    }else{
//                        minXueyangY = Integer.valueOf(NewBloodpressureList.get(0).getOxygen())-20 ;
//                        maxXueyangY = Integer.valueOf(NewBloodpressureList.get(0).getOxygen())+20;
//                        sizeXueyangtb = NewBloodpressureList.size();

//                        sizeHearttb = heart.size();
//                        if(proportion == 1){
//                            maxHearttbY = maxHearttbY + 1;
//                        }


                        //更新血氧的值
                        settingOxy(Integer.valueOf(NewBloodpressureList.get(0).getOxygen())-20,Integer.valueOf(NewBloodpressureList.get(0).getOxygen())+20,NewBloodpressureList,1,OxylineChart);
                    }
                    break;
                //血氧的图表数据不显示
                case CLEAR_Oxygen:
                    setMyAdpater();
                    if(BloodpressureList != null) {
                        BloodpressureList.clear();
                    }
                    OxylineChart.setVisibility(View.INVISIBLE);
                    tv_nodata5.setVisibility(View.VISIBLE);
                    break;

                case REFRESHXUEYAINFO:   //todo --- 血压

                    /*LineChartData dataXueya = (LineChartData) msg.obj;
                    xiayalineChart.setInteractive(true);
                    xiayalineChart.setZoomType(ZoomType.HORIZONTAL);
                    xiayalineChart.setMaxZoom((float) 4);//最大方法比例
                    xiayalineChart.setZoomEnabled(false);
                    xiayalineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
                    dataXueya.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
                    dataXueya.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                    dataXueya.setValueLabelTextSize(8);
                    dataXueya.isValueLabelBackgroundAuto();
                    xiayalineChart.setLineChartData(dataXueya);
                    xiayalineChart.setVisibility(View.VISIBLE);
                    Viewport vXueya = new Viewport(xiayalineChart.getMaximumViewport());
                    vXueya.bottom = minXueyaY;
                    vXueya.top = maxXueyaY;
                    (xiayalineChart).setMaximumViewport(vXueya);
                    if(sizeXueyatb>7){       // Oxyvalue.size()
                        vXueya.left = sizeXueyatb - 7;
                        vXueya.right = sizeXueyatb - 1;
                    }
                    xiayalineChart.setCurrentViewport(vXueya);
                    xiayalineChart.invalidate();*/
                    break;

                //血压的图图表数据
                case UPOxyBloodpressure_INFO:
//                    bloodpressure.clear();
//                    bloodpressure.addAll((ArrayList<Bloodpressure>) msg.obj);
//                    Collections.sort(bloodpressure);   //排序
                    AA.clear();
                    BB.clear();
                    if(Newbloodpressure.size()>2||Newbloodpressure.size()==2){   // bloodpressure   Newbloodpressure     Newbloodpressure
                        for (int b=0;b<Newbloodpressure.size();b++){
                            AA.add(Integer.valueOf(Newbloodpressure.get(b).getHeightBlood()));
                            BB.add(Integer.valueOf(Newbloodpressure.get(b).getMinBlood()));
                        }
                        //更新血压的值
                        Log.e("Collections",Collections.max(AA)+"");
                        Log.e("Collections",Collections.min(BB)+"");

                        if(null!=SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)){
                            if(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC).toString().contains("X2")){
                                settingOxyxieya((((Integer.valueOf(Collections.min(BB)+""))/10)*10)-20,Integer.valueOf(Integer.valueOf(Collections.max(AA)+""))+20,Newbloodpressure,10,xiayalineChart);
                            }else{
                                settingOxyxieya((((Integer.valueOf(Collections.min(BB)+""))/10)*10)-20,Integer.valueOf(Integer.valueOf(Collections.max(AA)+""))+40,Newbloodpressure,5,xiayalineChart);
                            }
                        }
                    }else{
                        AA.add(Integer.valueOf(Newbloodpressure.get(0).getHeightBlood()));
                        BB.add(Integer.valueOf(Newbloodpressure.get(0).getMinBlood()));

//                        minXueyaY = (Integer.valueOf(Newbloodpressure.get(0).getMinBlood()))-20;
//                        maxXueyaY = (Integer.valueOf(Newbloodpressure.get(0).getHeightBlood()))+20;
//                        sizeXueyatb = Newbloodpressure.size();
                        //第一次发送血压要画点
                        settingOxyxieya((Integer.valueOf(Newbloodpressure.get(0).getMinBlood()))-20,(Integer.valueOf(Newbloodpressure.get(0).getHeightBlood()))+20,Newbloodpressure,10,xiayalineChart);
                    }
                    break;
                //血压的图表数据不显示
                case CLEAR_Bloodpressure:
                    setMyAdpater();
                    if(bloodpressure != null) {
                        bloodpressure.clear();
                    }
                    xiayalineChart.setVisibility(View.INVISIBLE);
                    tv_nodata4.setVisibility(View.VISIBLE);
                    break;

                case CLEAR_UERHEARTINFO:
                    lineChart.setVisibility(View.INVISIBLE);
                    break;

                case CLEARSLEEP:
                    lineChart.setVisibility(View.INVISIBLE);
                    break;


                case UPUERTIMELINEINFO:   // 时间轴数据的更新
//                    if (timelineAdapter != null) {
//                        timelineAdapter.notifyDataSetChanged();
//                        timeLineListView.setAdapter(timelineAdapter);
//                    } else if (whiteTimelineAdapter != null) {
//                        whiteTimelineAdapter.notifyDataSetChanged();
//                        timeLineListView.setAdapter(whiteTimelineAdapter);
//                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void init() {
        inithealthView();
//        inithealthData();  // 初始化健康数据 （身高，体重。。）
//        setUphealthView(); // 设置健康页面的数据
        inithealthListener();
        setPageindex(pageindex);//设置数据报告页面的显示情况
    }

    /**
     *
     * @param minY Y轴坐标最小值
     * @param maxY Y轴坐标最大值
     *  size  总共有几个点
     *    ArrayList<ChartViewCoordinateData> heart  数据源
     *   Linecount  有几条线
     *  proportion  x 从哪里开始
     *    type  1心率
     *
     */
    private synchronized void getAxisXYLables(int minY, int maxY, int size, ArrayList<ChartViewCoordinateData> heart, int Linecount, int proportion, final View view) {
        try {
//            int youxiao_size=heart.size();//有效的数据长度
//            while(heart.size()<7){//补充到7,保持距离好看
//                ChartViewCoordinateData cd=new ChartViewCoordinateData();
//                cd.setHour("");
//                cd.setAvghata(heart.get(heart.size()-1).getAvghata());
//                cd.setValue(heart.get(heart.size()-1).getValue());
//                cd.setManhata(heart.get(heart.size()-1).getManhata());
//                cd.setMaxhata(heart.get(heart.size()-1).getMaxhata());
//                heart.add(cd);
//            }

            mAxisXValues.clear();
            mAxisYValues.clear();
            mPointValues.clear();
            mPointValuesb.clear();
            mPointValuesc.clear();
            //按类设置X的比例
            if(heart.size()==1){    // youxiao_size==1
                labelsX = new String[2];//X轴的标注
                labelsX[0]=heart.get(0).getHour();
                labelsX[1]="0";
                valuesY = new int[2];//图表的数据点
                valuesY[0]=heart.get(0).getMaxhata();
                valuesY[1]=0;
                valuesY_avg = new int[2];//图表的数据点
                valuesY_avg[0] = heart.get(0).getAvghata();
                valuesY_avg[1] = 0;
                valuesY_min = new int[2];//图表的数据点
                valuesY_min[0] = heart.get(0).getManhata();
                valuesY_min[1] = 0;
                for (int i = 0; i < 2; i++) {
                    mAxisXValues.add(new AxisValue(i).setLabel(labelsX[i]));    // X轴的值
                }
                //每一个点的值
                for (int i = 0; i < 2; i++) {
                    mPointValues.add(new PointValue(i, valuesY[i]));
                    mPointValuesb.add(new PointValue(i, valuesY_avg[i]));
                    mPointValuesc.add(new PointValue(i, valuesY_min[i]));
                }
            }else{
                labelsX = new String[heart.size()];//X轴的标注
                valuesY = new int[heart.size()];//图表的数据点
                valuesY_avg = new int[heart.size()];//图表的数据点
                valuesY_min = new int[heart.size()];//图表的数据点
                //取x轴的值及点的位置
                if(null!=SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)){
                    if(!SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC).toString().contains("X2")){
                        for (int i=0; i< heart.size(); i++) {
                            labelsX[i] =heart.get(i).getHour();
                            if((heart.get(i).getMaxhata()-heart.get(i).getManhata())>6){
                                valuesY[i] = heart.get(i).getMaxhata();
                                valuesY_avg[i] = heart.get(i).getAvghata();
                                valuesY_min[i] = heart.get(i).getManhata();
                            }else{
                                valuesY[i] = heart.get(i).getMaxhata()+1;
                                valuesY_avg[i] = heart.get(i).getAvghata();
                                valuesY_min[i] = heart.get(i).getManhata()-1;
                            }


                        }
                    }else{
                        for (int i=0; i< heart.size(); i++) {
                            labelsX[i] =heart.get(i).getHour();
                            valuesY[i] = heart.get(i).getMaxhata();
                            valuesY_avg[i] = heart.get(i).getAvghata();
                            valuesY_min[i] = heart.get(i).getManhata();
                        }
                    }
                }


                for (int i = 0; i < heart.size(); i++) {
                    mAxisXValues.add(new AxisValue(i).setLabel(labelsX[i]));    // X轴的值
                }
                //每一个点的值
                for (int i = 0; i < heart.size(); i++) {
                    mPointValues.add(new PointValue(i, valuesY[i]));
                    mPointValuesb.add(new PointValue(i, valuesY_avg[i]));
                    mPointValuesc.add(new PointValue(i, valuesY_min[i]));
                }
            }
      /*  for (int i = minY; i <= maxY; i+=proportion) {
            mAxisYValues.add(new AxisValue(i).setLabel(i+""));
        }*/
            for (int i = minY; i <= maxY; i+=proportion) {
                mAxisYValues.add(new AxisValue(i).setLabel(i+""));
            }
            lines = new ArrayList<Line>();
            for (int i=0;i<Linecount;i++){   // ------ 3
                if(0==i){
                    line = new Line(mPointValues).setColor(Color.parseColor(lineColor));  //折线的颜色   ---- 最高
                    line.setStrokeWidth(3);//设置节点大小
                /*line.setLineShader(Color.parseColor("#fa7832"),Color.parseColor("#fd2f31"));//设置从左到右渐变
                continue;*/
                }
                if(1==i){
                    line = new Line(mPointValuesc).setColor(Color.parseColor(lineColorlan));  //折线的颜色     ---- 最低
                    line.setStrokeWidth(3);//设置节点大小
                /*line.setLineShader(Color.parseColor("#24e8ff"),Color.parseColor("#066ad2"));//设置从左到右渐变
                continue;*/
                }
                if(2==i){
                    line = new Line(mPointValuesb).setColor(Color.parseColor(lineColorhuang));  //折线的颜色   ---- 平均
                    line.setStrokeWidth(3);//设置节点大小
                    //line.setLineShader(Color.parseColor("#ffff02"),Color.parseColor("#ffa900"));//设置从左到右渐变
                    //line.setLineShader(Color.parseColor("#fa7832"),Color.parseColor("#fd2f31"));//设置从左到右渐变
                }
                line.setShape(shape);//折线图上每个数据点的形状
                line.setCubic(true);//曲线是否平滑，即是曲线还是折线


                line.setFilled(isFilled);//是否填充曲线的面积
                if(heart.size()==1){
                    line.setPointRadius(3);// 设置节点半径
                    line.setHasLines(false);//是否用线显示。如果为false 则没有曲线只有点显示
                    line.setHasLabels(false);//曲线的数据坐标是否加上备注
                    line.setHasPoints(hasPoints);//是否显示圆点
                }else{
                    line.setHasPoints(false);
                    line.setPointRadius(1);// 设置节点半径
                    line.setHasLines(hasLines);//是否用线显示。如果为false 则没有曲线只有点显示
                    line.setHasLabels(false);//曲线的数据坐标是否加上备注
                }
                line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数
                line.setAreaTransparency(20);//透明度
//                line.setEffectiveLength(youxiao_size);
                lines.add(line);

            }


            LineChartData data = new LineChartData();
            data.setLines(lines);
            //坐标轴X
            final Axis axisX = new Axis(); //X轴
            axisX.setHasTiltedLabels(hasTiltedLabels);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
            axisX.setTextColor(0xff4c5157);  //设置字体颜色
            axisX.setTextSize(12);

            axisX.setHasLines(true);
            axisX.setLineColor(0xff4c5157);
            //axisX.setMaxLabelChars(0);
            axisX.setValues(mAxisXValues);
            data.setAxisXBottom(axisX);//x 轴在底部

            //坐标轴Y
            if (hasAxesY) {
                Axis axisY = new Axis();
                axisY.setHasLines(true);
                axisY.setTextSize(12);
                axisY.setTextColor(0xff4c5157);
                axisY.setLineColor(0xff4c5157);
                axisY.setValues(mAxisYValues);
                data.setAxisYLeft(axisY);
            }

            Message msg = mHandler.obtainMessage();
            msg.what = REFRESHHEARTINFO;    // TODO  ---- 刷新 心率 图表
            msg.obj = data;    // TODO ----- 更新心率数据  ---- 数据报告 心率报告 图表
            mHandler.sendMessage(msg);

            //设置行为属性，支持缩放、滑动以及平移
       /* ((LineChartView) view).setInteractive(true);
        ((LineChartView) view).setZoomType(ZoomType.HORIZONTAL);
        ((LineChartView) view).setMaxZoom((float) 4);//最大方法比例
        ((LineChartView) view).setZoomEnabled(false);
        ((LineChartView) view).setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        data.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
        data.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
        data.setValueLabelTextSize(8);
        data.isValueLabelBackgroundAuto();
        ((LineChartView) view).setLineChartData(data);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                ((LineChartView) view).setVisibility(View.VISIBLE);  //主线程更新UI
//            }
//        });
        Viewport v = new Viewport(((LineChartView) view).getMaximumViewport());
        v.bottom = minY;
        v.top = maxY;
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
        ((LineChartView) view).setMaximumViewport(v);

        //这2个属性的设置一定要在lineChart.setMaximumViewport(v);这个方法之后,不然显示的坐标数据是不能左右滑动查看更多数据的
        if(heart.size()>7){
            v.left = size - 7;
            v.right = size - 1;
        }
        ((LineChartView) view).setCurrentViewport(v);
//        ((LineChartView) view).postInvalidate();   //子线程刷新
            ((LineChartView) view).invalidate();   //子线程刷新*/
//       heart.clear();  //todo  ---- 没有clear 方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置血氧线条
     * String[] x   x轴的坐标数据
     *proportion  比例e
     * minY y轴最小开始点  maxY最大结束点
     * int lines 多少条线
     */

    public synchronized void  settingOxy(int minY, int maxY, ArrayList<Oxygen> Oxyvalue, int proportion, View view){
        try {

//            int youxiao_size=Oxyvalue.size();//有效的数据长度
//            while(Oxyvalue.size()<7){//补充到7,保持距离好看
//                Oxygen mOxygen=new Oxygen();
//                mOxygen.setOxygen(Oxyvalue.get(Oxyvalue.size()-1).getOxygen());
//                mOxygen.setHour("");
//                Oxyvalue.add(mOxygen);
//            }

            Oxvaluesd = new int[Oxyvalue.size()];//图表的数据点

            if(Oxyvalue.size()==1){   // youxiao_size
                OxlabelsX = new String[2];//X轴的标注
                OxvaluesY = new int[2];//图表的数据点
                if(Oxyvalue.get(0).getHour().contains(":")) {
                    String[] hours = Oxyvalue.get(0).getHour().split(":");
                    OxlabelsX[0] = hours[0] + ":" + hours[1];//设置x坐标值
                }else{
                    OxlabelsX[0] = Oxyvalue.get(0).getHour();
                }
                OxlabelsX[1] = " ";//设置x坐标值
                OxvaluesY[0] =Integer.valueOf(Oxyvalue.get(0).getOxygen());//设置x坐标对应的点
                OxvaluesY[1] =  0;//设置x坐标对应的点
                OXmAxisXValues = new ArrayList<AxisValue>();
                for (int i = 0; i < Oxyvalue.size(); i++) {
                    OXmAxisXValues.add(new AxisValue(i).setLabel(OxlabelsX[i]));
                }
            }else{
                OxlabelsX = new String[Oxyvalue.size()];//X轴的标注
                OxvaluesY = new int[Oxyvalue.size()];//图表的数据点
                for (int i=0; i< Oxyvalue.size(); i++) {
                    if(Oxyvalue.get(i).getHour().contains(":")) {
                        String[] hours = Oxyvalue.get(i).getHour().split(":");
                        OxlabelsX[i] = hours[0] + ":" + hours[1];//设置x坐标值
                    }else{
                        OxlabelsX[i] = Oxyvalue.get(i).getHour();
                    }
                    OxvaluesY[i] = Integer.valueOf(Oxyvalue.get(i).getOxygen());//设置x坐标对应的点
                }
                OXmAxisXValues = new ArrayList<AxisValue>();
                for (int i = 0; i < Oxyvalue.size(); i++) {
                    OXmAxisXValues.add(new AxisValue(i).setLabel(OxlabelsX[i]));
                }
            }

            //设置Y轴的可见范围
            OXmAxisYValues = new ArrayList<AxisValue>();
            for (int i = minY; i <= maxY; i+=proportion) {
                OXmAxisYValues.add(new AxisValue(i).setLabel(i+""));
            }

            if(Oxyvalue.size()==1){
                OXmPointValues = new ArrayList<PointValue>();
                for (int i = 0; i <2; i++) {
                    OXmPointValues.add(new PointValue(i, OxvaluesY[i]));
                }
            }else{
                OXmPointValues = new ArrayList<PointValue>();
                for (int i = 0; i < Oxyvalue.size(); i++) {
                    OXmPointValues.add(new PointValue(i, OxvaluesY[i]));
                }
            }
            linesOXY = new ArrayList<Line>();
            lineOXY = new Line(OXmPointValues).setColor(Color.parseColor("#ff00fc"));  //折线的颜色
            lineOXY.setShape(shape);//折线图上每个数据点的形状
            lineOXY.setCubic(true);//曲线是否平滑，即是曲线还是折线
            lineOXY.setStrokeWidth(4);//设置节点大小

            lineOXY.setFilled(isFilled);//是否填充曲线的面积
            lineOXY.setHasLabels(false);//曲线的数据坐标是否加上备注
            lineOXY.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据
            if(Oxyvalue.size()==1){
                lineOXY.setPointRadius(4);// 设置节点半径
                lineOXY.setHasLines(false);//是否用线显示。如果为false 则没有曲线只有点显示
            }else{
                lineOXY.setPointRadius(0);// 设置节点半径
                lineOXY.setHasLines(hasLines);//是否用线显示。如果为false 则没有曲线只有点显示
            }
            lineOXY.setHasPoints(hasPoints);//是否显示圆点
            lineOXY.setAreaTransparency(20);//透明度
//            lineOXY.setLineShader(Color.parseColor("#ff00fc"),Color.parseColor("#fa0087"));
//            lineOXY.setEffectiveLength(youxiao_size);
            linesOXY.add(lineOXY);
            LineChartData data = new LineChartData();
            data.setLines(linesOXY);
            //坐标轴X
            final Axis axisX = new Axis(); //X轴
            axisX.setHasTiltedLabels(hasTiltedLabels);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
            axisX.setTextColor(0xff4c5157);  //设置字体颜色
            axisX.setTextSize(12);
            if(Oxyvalue.size()==1){
                axisX.setHasLines(false);
            }else{
                axisX.setHasLines(true);
            }
            axisX.setLineColor(0xff4c5157);
            //axisX.setMaxLabelChars(0);
            axisX.setValues(OXmAxisXValues);
            data.setAxisXBottom(axisX);//x 轴在底部
            //坐标轴Y
            Axis axisY = new Axis();
            axisY.setHasLines(true);
            axisY.setTextSize(12);
            axisY.setTextColor(0xff4c5157);
            axisY.setLineColor(0xff4c5157);
            axisY.setValues(OXmAxisYValues);
            data.setAxisYLeft(axisY);

            ////////////////////////////////////////////////
//            Message msg = mHandler.obtainMessage();
//            msg.what = REFRESHXUEYANGINFO;    // TODO  ---- 刷新 血氧 图表
//            msg.obj = data;    // TODO ----- 更新心率数据  ---- 数据报告 血氧报告 图表
//            mHandler.sendMessage(msg);
            //////////////////////////////////////////////////////////////////

            //设置行为属性，支持缩放、滑动以及平移
            ((LineChartView) view).setInteractive(true);
            ((LineChartView) view).setZoomType(ZoomType.HORIZONTAL);
            ((LineChartView) view).setMaxZoom((float) 4);//最大方法比例
            ((LineChartView) view).setZoomEnabled(false);
            ((LineChartView) view).setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            data.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
            data.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
            data.setValueLabelTextSize(8);
            data.isValueLabelBackgroundAuto();
            ((LineChartView) view).setLineChartData(data);
            ((LineChartView) view).setVisibility(View.VISIBLE);
            Viewport v = new Viewport(((LineChartView) view).getMaximumViewport());
            v.bottom = minY;
            v.top = maxY;
            ((LineChartView) view).setMaximumViewport(v);
            if(Oxyvalue.size()>7){
                v.left = Oxyvalue.size() - 7;
                v.right = Oxyvalue.size() - 1;
            }
            ((LineChartView) view).setCurrentViewport(v);
            ((LineChartView) view).invalidate();

            while(Oxyvalue.size()>Oxyvalue.size()){//移除无效数据
                Oxyvalue.remove(Oxyvalue.size()-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置血压线条
     * String[] x   x轴的坐标数据
     *proportion  比例e
     * minY y轴最小开始点  maxY最大结束点
     * int lines 多少条线
     */

    public synchronized void  settingOxyxieya(int minY,int maxY, ArrayList<Bloodpressure> Oxyvalue,int proportion,View view){
        try {
//            int youxiao_size=Oxyvalue.size();//有效的数据长度
//            while(Oxyvalue.size()<7){//补充到7,保持距离好看
//                Bloodpressure mBloodpressure=new Bloodpressure();
//                mBloodpressure.setHour("");
//                mBloodpressure.setHeightBlood(Oxyvalue.get(Oxyvalue.size()-1).getHeightBlood());
//                mBloodpressure.setMinBlood(Oxyvalue.get(Oxyvalue.size()-1).getMinBlood());
//                Oxyvalue.add(mBloodpressure);
//            }

            if(Oxyvalue.size()>1){  //  youxiao_size
                OxlabelsX = new String[Oxyvalue.size()];//X轴的标注
                OxvaluesY = new int[Oxyvalue.size()];//图表的数据点
                Oxvaluesd = new int[Oxyvalue.size()];//图表的数据点
                for (int i=0; i< Oxyvalue.size(); i++) {
                    if(Oxyvalue.get(i).getHour().contains(":")) {
                        String[] hours = Oxyvalue.get(i).getHour().split(":");
                        OxlabelsX[i] = hours[0] + ":" + hours[1];//设置x坐标值
                    }else{
                        OxlabelsX[i] = Oxyvalue.get(i).getHour();
                    }
                    OxvaluesY[i] = Integer.valueOf(Oxyvalue.get(i).getHeightBlood());//设置x坐标对应的点
                    Oxvaluesd[i] = Integer.valueOf(Oxyvalue.get(i).getMinBlood());//设置x坐标对应的点
                }
                OXmAxisXValues = new ArrayList<AxisValue>();
                for (int i = 0; i < Oxyvalue.size(); i++) {
                    OXmAxisXValues.add(new AxisValue(i).setLabel(OxlabelsX[i]));
                }
            }else{
                OxlabelsX = new String[2];//X轴的标注
                OxvaluesY = new int[2];//图表的数据点
                Oxvaluesd = new int[2];//图表的数据点
                if(Oxyvalue.get(0).getHour().contains(":")) {
                    String[] hours = Oxyvalue.get(0).getHour().split(":");
                    OxlabelsX[0] = hours[0] + ":" + hours[1];//设置x坐标值
                }else{
                    OxlabelsX[0] = Oxyvalue.get(0).getHour();
                }
                OxlabelsX[1] =" ";//设置x坐标值
                OxvaluesY[0] = Integer.valueOf(Oxyvalue.get(0).getHeightBlood());//设置x坐标对应的点
                OxvaluesY[1] = 0;//设置x坐标对应的点
                Oxvaluesd[0] = Integer.valueOf(Oxyvalue.get(0).getMinBlood());//设置x坐标对应的点
                Oxvaluesd[1] = 0;//设置x坐标对应的点
                OXmAxisXValues = new ArrayList<AxisValue>();
                for (int i = 0; i <2; i++) {
                    OXmAxisXValues.add(new AxisValue(i).setLabel(OxlabelsX[i]));
                }
            }


            //设置Y轴的可见范围
            OXmAxisYValues = new ArrayList<AxisValue>();
            for (int i = minY; i <= maxY; i+=proportion) {
                OXmAxisYValues.add(new AxisValue(i).setLabel(i+""));
            }

            OXmPointValues = new ArrayList<PointValue>();
            OXmPointValuesb = new ArrayList<PointValue>();

            if(Oxyvalue.size()==1){
                for (int i = 0; i <2; i++) {
                    OXmPointValues.add(new PointValue(i, OxvaluesY[i]));
                    OXmPointValuesb.add(new PointValue(i, Oxvaluesd[i]));
                }
                lines = new ArrayList<Line>();
                for(int i=0;i<2;i++){
                    if(0==i){ line = new Line(OXmPointValues).setColor(Color.parseColor("#01fad1"));  //折线的颜色
//                        line.setLineShader(Color.parseColor("#01fad1"),Color.parseColor("#2793fb"));
                    }
                    if(1==i){
                        line = new Line(OXmPointValuesb).setColor(Color.parseColor("#dcfe00"));  //折线的颜色
//                        line.setLineShader(Color.parseColor("#dcfe00"),Color.parseColor("#00c631"));
                    }
//                    line.setEffectiveLength(youxiao_size);//有效长度拦截
                    line.setShape(shape);//折线图上每个数据点的形状
                    line.setCubic(true);//曲线是否平滑，即是曲线还是折线
                    line.setStrokeWidth(4);//设置节点大小
                    line.setFilled(isFilled);//是否填充曲线的面积
                    line.setHasLabels(false);//曲线的数据坐标是否加上备注
                    line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据
                    line.setHasLines(false);
                    line.setPointRadius(4);// 设置节点半径
                    line.setHasPoints(hasPoints);//是否显示圆点
                    line.setAreaTransparency(20);//透明度
                    lines.add(line);
                }
            }else{
                for (int i = 0; i <Oxyvalue.size(); i++) {
                    OXmPointValues.add(new PointValue(i, OxvaluesY[i]));
                    OXmPointValuesb.add(new PointValue(i, Oxvaluesd[i]));
                }
                lines = new ArrayList<Line>();
                for(int i=0;i<Oxyvalue.size();i++){
                    if(0==i){ line = new Line(OXmPointValues).setColor(getResources().getColor(R.color.lineColorlan));  //折线的颜色
//                        line.setLineShader(Color.parseColor("#01fad1"),Color.parseColor("#2793fb"));
                    }
                    if(1==i){
                        line = new Line(OXmPointValuesb).setColor(getResources().getColor(R.color.lineColorhuang));  //折线的颜色
//                        line.setLineShader(Color.parseColor("#dcfe00"),Color.parseColor("#00c631"));
                    }
//                    line.setEffectiveLength(youxiao_size);//有效长度拦截
                    line.setShape(shape);//折线图上每个数据点的形状
                    line.setCubic(true);//曲线是否平滑，即是曲线还是折线
                    line.setPointRadius(0);// 设置节点半径
                    line.setStrokeWidth(4);//设置节点大小
                    line.setFilled(isFilled);//是否填充曲线的面积
                    line.setHasLabels(false);//曲线的数据坐标是否加上备注
                    line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据
                    line.setHasLines(true);
//                line.setHasPoints(hasPoints);//是否显示圆点
                    line.setAreaTransparency(20);//透明度
                    lines.add(line);
                }
            }





            LineChartData data = new LineChartData();
            data.setLines(lines);
            //坐标轴X
            final Axis axisX = new Axis(); //X轴
            axisX.setHasTiltedLabels(hasTiltedLabels);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
            axisX.setTextColor(0xff4c5157);  //设置字体颜色
            axisX.setTextSize(12);
            if(Oxyvalue.size()==1){
                axisX.setHasLines(true);
            }else{
                axisX.setHasLines(true);
            }
            axisX.setLineColor(0xff4c5157);
            //axisX.setMaxLabelChars(0);
            axisX.setValues(OXmAxisXValues);
            data.setAxisXBottom(axisX);//x 轴在底部
            //坐标轴Y
            if (hasAxesY) {
                Axis axisY = new Axis();
                axisY.setHasLines(true);
                axisY.setTextSize(12);
                axisY.setTextColor(0xff4c5157);
                axisY.setLineColor(0xff4c5157);
                axisY.setValues(OXmAxisYValues);
                data.setAxisYLeft(axisY);
            }

            ////////////////////////////////////////////////
//            Message msg = mHandler.obtainMessage();
//            msg.what = REFRESHXUEYAINFO;    // TODO  ---- 刷新 血压 图表
//            msg.obj = data;    // TODO ----- 更新心率数据  ---- 数据报告 血压报告 图表
//            mHandler.sendMessage(msg);
            //////////////////////////////////////////////////////////////////

            //设置行为属性，支持缩放、滑动以及平移
            ((LineChartView) view).setInteractive(true);
            ((LineChartView) view).setZoomType(ZoomType.HORIZONTAL);
            ((LineChartView) view).setMaxZoom((float) 4);//最大方法比例
            ((LineChartView) view).setZoomEnabled(false);
            ((LineChartView) view).setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            data.setValueLabelBackgroundColor(R.color.transparent);// 设置数据背景颜色
            data.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
            data.setValueLabelTextSize(8);
            data.isValueLabelBackgroundAuto();
            ((LineChartView) view).setLineChartData(data);
            ((LineChartView) view).setVisibility(View.VISIBLE);
            Viewport v = new Viewport(((LineChartView) view).getMaximumViewport());
            v.bottom = minY;
            v.top = maxY;
            ((LineChartView) view).setMaximumViewport(v);
            if(Oxyvalue.size()>7){
                v.left = Oxyvalue.size() - 7;
                v.right = Oxyvalue.size() - 1;
            }
            ((LineChartView) view).setCurrentViewport(v);
            ((LineChartView) view).invalidate();

            while(Oxyvalue.size()>Oxyvalue.size()){//移除无效数据
                Oxyvalue.remove(Oxyvalue.size()-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }///////////////////////////////////////////////////////////


    /**
     * 获取健康控件
     */
    private void inithealthView() {
        dataReportView.findViewById(R.id.tv_sport_mode).setVisibility(View.GONE);

        cb_navigation_sport = (TextView) dataReportView.findViewById(R.id.cb_navigation_sport);  // 计步
        cb_navigation_sport.setBackground(null);
        cb_navigation_sleep = (TextView) dataReportView.findViewById(R.id.cb_navigation_sleep);  // 睡眠
        cb_navigation_heart = (TextView) dataReportView.findViewById(R.id.cb_navigation_heart_rate); // 心率

        cb_navigation_heart.setOnClickListener(this);
        cb_navigation_sleep.setOnClickListener(this);
        cb_navigation_heart.setOnClickListener(this);

        int widowHeight = PresentationActivity.this.getWindow().getWindowManager().getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UTIL.dip2px(getApplicationContext(),200));

        btn_share = (ImageButton) dataReportView.findViewById(R.id.ib_navigation_share);
        synchronizationTv = (TextView) dataReportView.findViewById(R.id.tv_navigation_synchronization);
        btn_share.setOnClickListener(this);
        btn_share.setVisibility(View.GONE);//隐藏分享
        //synchronizationTv.setBackgroundResource(R.drawable.icon_return_back);
        synchronizationTv.setVisibility(View.GONE);
        //synchronizationTv.setClickable(true);
//        synchronizationTv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity)PresentationActivity.this).toBackhomefragment();
//            }
//        });
        View iv_back =dataReportView.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        HealthView = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.fragment_health_report, null);   //TODO  健康页面
//        finish_re = (RelativeLayout) HealthView.findViewById(R.id.finish_re);
//        finish_re2 = (RelativeLayout) HealthView.findViewById(R.id.finish_re2);

        mPersonalInfo = (LinearLayout) HealthView.findViewById(R.id.part_personal_info);

      /*  mPartOne = (LinearLayout) HealthView.findViewById(R.id.part_one);
        mPartTow = (LinearLayout) HealthView.findViewById(R.id.part_tow);
        mPartThree = (LinearLayout) HealthView.findViewById(R.id.part_three);
        mPartFour = (LinearLayout) HealthView.findViewById(R.id.part_four);
        mHealthBody = (ImageView) HealthView.findViewById(R.id.health_body);
        mScanLine = (ImageView) HealthView.findViewById(R.id.health_body_scan_line);
        mScanBox = (ImageView) HealthView.findViewById(R.id.health_body_scan_box);
        mSexTv = (TextView) HealthView.findViewById(R.id.health_sex_tv);
        mAgeTv = (TextView) HealthView.findViewById(R.id.health_age_tv);
        mStatureTv = (TextView) HealthView.findViewById(R.id.health_stature_tv); // 身高
        mWeightTv = (TextView) HealthView.findViewById(R.id.health_weight_tv);
        mBeginTestTv = (TextView) HealthView.findViewById(R.id.begin_test_text);
        mBMITv = (TextView) HealthView.findViewById(R.id.bmi_num_tv);
        mBodyFatNumTv = (TextView) HealthView.findViewById(R.id.body_fat_num_tv);
        mBodyFatTextTv = (TextView) HealthView.findViewById(R.id.body_fat);
        mHeartRateTv = (TextView) HealthView.findViewById(R.id.heart_rate_num_tv);
        mVitalCapacityTv = (TextView) HealthView.findViewById(R.id.vital_capacity_num_tv);
        mCardiopulmonaryTv = (TextView) HealthView.findViewById(R.id.body_cardiopulmonary);
        mHealthScordNumTv = (TextView) HealthView.findViewById(R.id.health_num);
        mBodyAgeNumTv = (TextView) HealthView.findViewById(R.id.body_age_tv);
        mBMITv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
        mBodyFatNumTv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
        mHeartRateTv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
        mVitalCapacityTv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
        mHealthScordNumTv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
        mBodyAgeNumTv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt);
        personal_info_sex_ll = (LinearLayout) HealthView.findViewById(R.id.personal_info_sex_ll);
        personal_info_age_ll = (LinearLayout) HealthView.findViewById(R.id.personal_info_age_ll);
        personal_info_stature_ll = (LinearLayout) HealthView.findViewById(R.id.personal_info_stature_ll);
        personal_info_weight_ll = (LinearLayout) HealthView.findViewById(R.id.personal_info_weight_ll);*/

        // TODO 时间轴
       /* timeLineView = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.continuousdata_view, null);  // 时间轴页面
        timeLineListView = (ListView) timeLineView.findViewById(R.id.timeline_listview);
        listViewHead = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.listview_data_head, null);
        listViewFoot = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.listview_data_head, null);

        mTimelineCurdatetv = (TextView) timeLineView.findViewById(R.id.curdate_tv);
        timelineDateDownturning = (ImageView) timeLineView.findViewById(R.id.data_bt_downturning);
        timelineDateUpturning = (ImageView) timeLineView.findViewById(R.id.data_bt_upturning);

        mTimelineCurdatetv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskLightCond);
        mTimelineCurdatetv.setOnClickListener(this);
        timelineDateUpturning.setOnClickListener(this);
        timelineDateDownturning.setOnClickListener(this);*/

        // TODO  ---- 这里给时间轴数据，初始化数据 无效
       /* int currentStyle = Integer.parseInt(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE, "1"));   // 页面初始化时，默认显示时间轴页面
        if(currentStyle == 1){   // 黑色主题
            timeLineListView.addHeaderView(listViewHead);
            timeLineListView.addFooterView(listViewFoot);
            timelineAdapter = new NewTimelineAdapter(PresentationActivity.this, timelineList);  // TODO -- 这里设置时timelineList还没有数据   时间轴页面的数据适配     ---- 时间轴相关的数据
            timeLineListView.setAdapter(timelineAdapter);
        } else if (currentStyle == 0) {  // 白色 主题
            whiteTimelineAdapter = new NewTimelineWhiteAdapter(PresentationActivity.this, timelineList);
            timeLineListView.setAdapter(whiteTimelineAdapter);
        }*/

        //TODO ----  数据报告页面
        //dataReportView = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.report_datareport_view, null);   // 数据报告页面
        datareport_sc = dataReportView.findViewById(R.id.datareport_sc);

        stepColumchartView = (DataReportStepColumchartView) dataReportView.findViewById(R.id.report_stepcolumchart);  // 活动报告的图表
        sleepColumchartView = (DataReportSleepColumchartView) dataReportView.findViewById(R.id.report_sleepcolumchart);  // 睡眠报告的图表
        //heartRateChartView = (ChartView) dataReportView.findViewById(R.id.report_heart_rate_chartview);                 // 心率报告的图表
        lineChart = (LineChartView)dataReportView. findViewById(R.id.chart);//心率
        OxylineChart = (LineChartView)dataReportView. findViewById(R.id.chart_oxy);//血氧
        xiayalineChart= (LineChartView)dataReportView. findViewById(R.id.chart_xiaya);//血压

        mReportStepTv = (TextView) dataReportView.findViewById(R.id.report_data_step);   // 活动报告顶部 步数值

        mReportSleepHourTv = (TextView) dataReportView.findViewById(R.id.report_sleep_data_hour);  // 睡眠报告 -- 顶部小时值
        mReportSleepMinTv = (TextView) dataReportView.findViewById(R.id.report_sleep_data_min);     // 睡眠报告 -- 顶部分钟值

        mReportHeartRateTv  = (TextView) dataReportView.findViewById(R.id.report_heart_rate_data_times); // 心率报告AVG
        heighthata = (TextView) dataReportView.findViewById(R.id.report_heart_hieht); // 最高心率
        lowhata = (TextView) dataReportView.findViewById(R.id.report_heart_rate_string); // 最低心率


        systolic_pressure= (TextView) dataReportView.findViewById(R.id.datareport_xiaya_lessone);//收缩压
        diastolic_pressure= (TextView) dataReportView.findViewById(R.id.datareport_xiaya_lesstwo);//舒张压
        pressure= (TextView) dataReportView.findViewById(R.id.datareport_xiaya_less);


        reportDateDownturning = (ImageView) dataReportView.findViewById(R.id.data_bt_downturning);
        reportDateUpturning = (ImageView) dataReportView.findViewById(R.id.data_bt_upturning);
        mReportCurdatetv = (TextView) dataReportView.findViewById(R.id.curdate_tv);


        mDataReportSleepChartLl = (LinearLayout) dataReportView.findViewById(R.id.data_report_sleep_chart_ll);
        listview_detail_step= (ListView) dataReportView.findViewById(R.id.listview_detail_step);
        listview_detail_heart= (ListView) dataReportView.findViewById(R.id.listview_detail_heart);
        listview_detail_bloodpressure= (ListView) dataReportView.findViewById(R.id.listview_detail_bloodpressure);
        listview_detail_oxygen= (ListView) dataReportView.findViewById(R.id.listview_detail_oxygen);
        listview_detail_sleep=dataReportView.findViewById(R.id.listview_detail_sleep);
        tv_sleep_time1= (TextView) dataReportView.findViewById(R.id.tv_time1);
        tv_sleep_time2= (TextView) dataReportView.findViewById(R.id.tv_time2);
        tv_sleep_time3= (TextView) dataReportView.findViewById(R.id.tv_time3);
        tv_sleep_time4= (TextView) dataReportView.findViewById(R.id.tv_time4);
        tv_sleep_time5= (TextView) dataReportView.findViewById(R.id.tv_time5);
        tv_sleep_time6= (TextView) dataReportView.findViewById(R.id.tv_time6);

        ll_sport=(LinearLayout)dataReportView.findViewById(R.id.ll_sport);
        ll_oxy_rate=(LinearLayout)dataReportView.findViewById(R.id.ll_oxy_rate);
        ll_xieya=(LinearLayout)dataReportView.findViewById(R.id.ll_xieya);
        ll_heart_rate=(LinearLayout)dataReportView.findViewById(R.id.ll_heart_rate);
        ll_sleep_rate=(LinearLayout)dataReportView.findViewById(R.id.ll_sleep_rate);

        tv_nodata1= (TextView) dataReportView.findViewById(R.id.report_data_step_not_support);
        tv_nodata2= (TextView) dataReportView.findViewById(R.id.report_data_sleep_not_support);
        tv_nodata3= (TextView) dataReportView.findViewById(R.id.report_data_heart_not_support);
        tv_nodata4= (TextView) dataReportView.findViewById(R.id.xueya_nodata_text);
        tv_nodata5= (TextView) dataReportView.findViewById(R.id.report_oxy_support);

        //设置其他语言下字体大小
        if(!Utils.isZh(PresentationActivity.this)){
            List<View>view=new ArrayList<View>();
            view.add(mReportHeartRateTv);
            view.add(heighthata);
            view.add(lowhata);
            view.add(systolic_pressure);
            view.add(diastolic_pressure);
            Utils.  settingAllFontsize (view,7);
        }

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("it") || languageLx.equals("ru") || languageLx.equals("pt") || languageLx.equals("fr")) {  // 俄语
            pressure.setTextSize(12);
        }

        if(languageLx.equals("ja")){
            tv_nodata1.setTextSize(16);
            tv_nodata2.setTextSize(16);
            tv_nodata3.setTextSize(16);
            tv_nodata4.setTextSize(16);
            tv_nodata5.setTextSize(16);
        }

        if(languageLx.contains("tr")){
            LinearLayout linearLayout = (LinearLayout) dataReportView.findViewById(R.id.report_sleep_fall_ll);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 40;
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            linearLayout.setLayoutParams(layoutParams);
        }
//        mReportStepNotSupport = (TextView) dataReportView.findViewById(R.id.report_data_step_not_support);  // 计步无数据
        mReportSleepNotSupport = (TextView) dataReportView.findViewById(R.id.report_data_sleep_not_support);
//        mReportHeartNotSupport = (TextView) dataReportView.findViewById(R.id.report_data_heart_not_support);

//        report_oxy_support = (TextView) dataReportView.findViewById(R.id.report_oxy_support);
        //xueya_nodata_text = (TextView) dataReportView.findViewById(R.id.xueya_nodata_text);  // 血压无数据

        mReportCurdatetv.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskLightCond);

//        stepColumchartView.setLayoutParams(mParams);
//        sleepColumchartView.setLayoutParams(mParams);
        //   heartRateChartView.setLayoutParams(mParams);

        reportDateDownturning.setOnClickListener(this);
        reportDateUpturning.setOnClickListener(this);
        mReportCurdatetv.setOnClickListener(this);

        //默认 有睡眠报告
        sleepColumchartView.setVisibility(View.VISIBLE);
        mDataReportSleepChartLl.setVisibility(View.VISIBLE);
        mReportSleepNotSupport.setVisibility(View.GONE);

      /*  ImageButton ib_navigation_share = (ImageButton) HealthView.findViewById(R.id.ib_navigation_share);
        ib_navigation_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtils.isConnect(PresentationActivity.this)) {
                    Toast.makeText(PresentationActivity.this, R.string.my_network_disconnected, Toast.LENGTH_SHORT).show();
                } else {
//                    if(vPager.getCurrentItem() == 0){ //数据报告页面
                        if(Utils.isFastClick()){
                            //datareport_sc.fullScroll(ScrollView.FOCUS_UP);//滚动到顶部
                            showShareNotmap(MainService.PAGE_INDEX_HEALTH);
                        }
//                    }else {// 健康页面
//                        if(Utils.isFastClick()){
//                            showShare(MainService.PAGE_INDEX_HEALTH);
//                        }
//                    }
                }
            }
        });*/

        pagerView = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.fragment_home_viewpager, null);
        views = new ArrayList<>();

//        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.DEFAULT_HEART_RATE).equals("1")){
        //views.add(timeLineView);  // 添加时间轴页面   --- 去掉注释可以显示
        cb_navigation_heart.setText("");
        cb_navigation_sport.setText(PresentationActivity.this.getString(R.string.data_reporting));
        cb_navigation_sleep.setText(PresentationActivity.this.getString(R.string.health));
        cb_navigation_sleep.setVisibility(View.INVISIBLE);
        cb_navigation_sleep.setText("");
//        }

        views.add(dataReportView);  // 添加数据报告页面
        //views.add(HealthView);      // 添加健康报告页面

//        vpAdapter = new ViewPagerAdapter(views);
//        vPager = (ViewPager) pagerView.findViewById(R.id.test_vp);
//        vPager.setAdapter(vpAdapter);
//        vPager.setOnPageChangeListener(this);
//        reportLayout.addView(pagerView);
    }

    private void inithealthData() {
      /*  mSexArr = new String[]{getString(R.string.my_man), getString(R.string.my_woman)};
        String sexString = SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SEX);
        if (sexString.equals("1")) {
            mSexValue = 1;
        } else {
            mSexValue = 0;
        }
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy");
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.BIRTH).equals("")) {
            mAgeValue = 18;
        } else {
            mAgeValue = Utils.toint(getDateFormat.format(curDate)) - Utils.toint(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.BIRTH).substring(0, 4));
        }
        if (mAgeValue < 18) {
            mAgeValue = 18;
        }
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.HEIGHT).equals("")) {
            mStatureValue = 170;
        } else {
            mStatureValue = Utils.toint(SharedPreUtil.readPre(PresentationActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.HEIGHT));
        }

        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.WEIGHT).equals("")) {
            mWeightValue = 60;
        } else {
            mWeightValue = Utils.toint(SharedPreUtil.readPre(PresentationActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.WEIGHT));
        }
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.HEIGHT_FT).equals("")) {
            mStatureFootValue = 6;
        } else {
            mStatureFootValue = Utils.toint(SharedPreUtil.readPre(PresentationActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT));
        }
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.HEIGHT_IN).equals("")) {
            mStatureInchValue = 6;
        } else {
            mStatureInchValue = Utils.toint(SharedPreUtil.readPre(PresentationActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN));
        }
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.WEIGHT_US).equals("")) {
            mWeightPoundValue = 120;
        } else {
            mWeightPoundValue = Utils.toint(SharedPreUtil.readPre(PresentationActivity.this,
                    SharedPreUtil.USER, SharedPreUtil.WEIGHT_US));
        }

        // 先判断男女 0为男
        if (mSexValue == 0) {
            // 再判断年龄
            if (mAgeValue >= 60) {
                mBaseBMI = HealthReportContantsData.MALE_BMI_BASE_VALUE_5;
                mBaseHeartRate = HealthReportContantsData.MALE_HEART_RATE_BASE_VALUE_5;
                mBaseVitalCapacity = HealthReportContantsData.MALE_VITAL_CAPACITY_BASE_VALUE_5;
            } else if (mAgeValue >= 50) {
                mBaseBMI = HealthReportContantsData.MALE_BMI_BASE_VALUE_4;
                mBaseHeartRate = HealthReportContantsData.MALE_HEART_RATE_BASE_VALUE_4;
                mBaseVitalCapacity = HealthReportContantsData.MALE_VITAL_CAPACITY_BASE_VALUE_4;
            } else if (mAgeValue >= 40) {
                mBaseBMI = HealthReportContantsData.MALE_BMI_BASE_VALUE_3;
                mBaseHeartRate = HealthReportContantsData.MALE_HEART_RATE_BASE_VALUE_3;
                mBaseVitalCapacity = HealthReportContantsData.MALE_VITAL_CAPACITY_BASE_VALUE_3;
            } else if (mAgeValue >= 30) {
                mBaseBMI = HealthReportContantsData.MALE_BMI_BASE_VALUE_2;
                mBaseHeartRate = HealthReportContantsData.MALE_HEART_RATE_BASE_VALUE_2;
                mBaseVitalCapacity = HealthReportContantsData.MALE_VITAL_CAPACITY_BASE_VALUE_2;
            } else {
                mBaseBMI = HealthReportContantsData.MALE_BMI_BASE_VALUE_1;
                mBaseHeartRate = HealthReportContantsData.MALE_HEART_RATE_BASE_VALUE_1;
                mBaseVitalCapacity = HealthReportContantsData.MALE_VITAL_CAPACITY_BASE_VALUE_1;
            }
        } else {
            if (mAgeValue >= 60) {
                mBaseBMI = HealthReportContantsData.FEMALE_BMI_BASE_VALUE_5;
                mBaseHeartRate = HealthReportContantsData.FEMALE_HEART_RATE_BASE_VALUE_5;
                mBaseVitalCapacity = HealthReportContantsData.FEMALE_VITAL_CAPACITY_BASE_VALUE_5;
            } else if (mAgeValue >= 50) {
                mBaseBMI = HealthReportContantsData.FEMALE_BMI_BASE_VALUE_4;
                mBaseHeartRate = HealthReportContantsData.FEMALE_HEART_RATE_BASE_VALUE_4;
                mBaseVitalCapacity = HealthReportContantsData.FEMALE_VITAL_CAPACITY_BASE_VALUE_4;
            } else if (mAgeValue >= 40) {
                mBaseBMI = HealthReportContantsData.FEMALE_BMI_BASE_VALUE_3;
                mBaseHeartRate = HealthReportContantsData.FEMALE_HEART_RATE_BASE_VALUE_3;
                mBaseVitalCapacity = HealthReportContantsData.FEMALE_VITAL_CAPACITY_BASE_VALUE_3;
            } else if (mAgeValue >= 30) {
                mBaseBMI = HealthReportContantsData.FEMALE_BMI_BASE_VALUE_2;
                mBaseHeartRate = HealthReportContantsData.FEMALE_HEART_RATE_BASE_VALUE_2;
                mBaseVitalCapacity = HealthReportContantsData.FEMALE_VITAL_CAPACITY_BASE_VALUE_2;
            } else {
                mBaseBMI = HealthReportContantsData.FEMALE_BMI_BASE_VALUE_1;
                mBaseHeartRate = HealthReportContantsData.FEMALE_HEART_RATE_BASE_VALUE_1;
                mBaseVitalCapacity = HealthReportContantsData.FEMALE_VITAL_CAPACITY_BASE_VALUE_1;
            }
        }*/
    }

    private void setUphealthView() {
        // 初始化页面
       /* mSexTv.setText(mSexArr[mSexValue]);
        if (mAgeValue > 70) {
            mAgeValue = 70;
        }
        mAgeTv.setText(String.valueOf(mAgeValue));
        mStatureTv.setText(String.valueOf(mStatureValue));
        mWeightTv.setText(String.valueOf(mWeightValue));
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
            String mRealStatureInchValue = Utils.setformat(2,(float) mStatureInchValue/12);
            mStatureTv.setText(mStatureFootValue + Float.parseFloat(mRealStatureInchValue) + "");
            mWeightTv.setText(String.valueOf(mWeightPoundValue)
            );
        } else {
            mStatureTv.setText(String.valueOf(mStatureValue));
            mWeightTv.setText(String.valueOf(mWeightValue));
        }
        if (mSexValue == 0) {
            mHealthBody.setImageResource(R.drawable.health_body_male);
        } else {
            mHealthBody.setImageResource(R.drawable.health_body_female);
        }*/
    }

    /**
     * 事件监听
     */

    private void inithealthListener() {
/*		personal_info_sex_ll.setOnClickListener(this);
        personal_info_age_ll.setOnClickListener(this);
		personal_info_stature_ll.setOnClickListener(this);
		personal_info_weight_ll.setOnClickListener(this);*/
        mPersonalInfo.setOnClickListener(this);
//        mHealthBody.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("HealthFragment");

//        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.DEFAULT_HEART_RATE, "0").equals("1")) {
//            ll_heart_rate.setVisibility(View.VISIBLE);
//        }
        //ll_heart_rate.setVisibility(View.VISIBLE);  //todo --- 默认显示 心率报告
        //ll_heart_rate.setVisibility(View.VISIBLE);  //todo --- 默认显示 心率报告
        //initView();
        dealDateShow();
    }


    private void initView(){
//        if(ISSYNWATCHINFO){
//            if(HEART){
//                ll_heart_rate.setVisibility(View.VISIBLE);
//            }else {
//                ll_heart_rate.setVisibility(View.GONE);
//            }
//            if(BLOOD_PRESSURE){
//                ll_xieya.setVisibility(View.VISIBLE);
//            }else{
//                ll_xieya.setVisibility(View.GONE);
//            }
//            if(BLOOD_OXYGEN){
//                ll_oxy_rate.setVisibility(View.VISIBLE);
//            }else{
//                ll_oxy_rate.setVisibility(View.GONE);
//            }
//        }else{
//            ll_heart_rate.setVisibility(View.VISIBLE);
//            ll_xieya.setVisibility(View.VISIBLE);
//            ll_oxy_rate.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HealthFragment");
    }

    private void dealDateShow() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        datePreferences = PresentationActivity.this.getSharedPreferences("datepreferences", Context.MODE_PRIVATE);
        final int select_day = datePreferences.getInt("1_select_day", 0);
        final int select_month = datePreferences.getInt("1_select_month", 0);
        final int select_year = datePreferences.getInt("1_select_year", 0);

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
            Message msg = new Message();
            msg.what = SYNCTIME;
            msg.obj = select_date;
            mHandler.sendMessage(msg);
            // 清除缓存。
            SharedPreferences.Editor editor = datePreferences.edit();
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

    private String filePath = Environment.getExternalStorageDirectory() + "/appmanager/fundoShare/";  // funfit
    private String fileName = "screenshot_analysis.png";
    private String detailPath = filePath + File.separator + fileName;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_navigation_heart_rate:
//                vPager.setCurrentItem(vPager.getCurrentItem() - 1);
                break;
            case R.id.cb_navigation_sport:
                break;
            case R.id.cb_navigation_sleep:
//                if (vPager.getCurrentItem() < 2) {
//                    vPager.setCurrentItem(vPager.getCurrentItem() + 1);
//                }
                break;
            case R.id.data_bt_downturning:   // 切换前一天的日期
                if (isFastDoubleClick()) {
                    return;
                }
                //clearABCD();
                date_downturning();
                break;
            case R.id.data_bt_upturning:     // 切换后一天的日期
                if (isFastDoubleClick()) {
                    return;
                }
                date_upturning();
                break;
            case R.id.curdate_tv:
                Intent mIntent = new Intent(PresentationActivity.this, CalendarAcitity.class);
                mIntent.putExtra("from", "HealthFragment");
                startActivity(mIntent);
                break;
            case R.id.ib_navigation_share:
                if (!NetWorkUtils.isConnect(PresentationActivity.this)) {
                    Toast.makeText(PresentationActivity.this, R.string.my_network_disconnected, Toast.LENGTH_SHORT).show();
                } else {
                    if (!isFastDoubleClick()) {
//                        if(vPager.getCurrentItem() == 0){ //数据报告页面
                        if (Utils.isFastClick()) {
//                                showShareNotmap(MainService.PAGE_INDEX_HEALTH);}
//                        }else {// 健康页面
//                            if(Utils.isFastClick()){
//                                showShare(MainService.PAGE_INDEX_HEALTH);}
//                        }
                        }
                    }
                    break;

         /*   case R.id.personal_info_sex_ll:
                showPopupWindow(0);
                break;
            case R.id.personal_info_age_ll:
                showPopupWindow(1);
                break;
            case R.id.personal_info_stature_ll:
                showPopupWindow(2);
                break;
            case R.id.personal_info_weight_ll:
                showPopupWindow(3);
                break;
            case R.id.health_body:
                if (mAgeValue == 0 || mStatureValue == 0 || mWeightValue == 0) {
                    if (mToast == null) {
                        mToast = Toast.makeText(PresentationActivity.this, PresentationActivity.this
                                        .getText(R.string.personal_info_error),
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(R.string.personal_info_error);
                    }
                    mToast.show();
                } else if (mAgeValue < 18) {
                    if (mToast == null) {
                        mToast = Toast.makeText(PresentationActivity.this, PresentationActivity.this
                                        .getText(R.string.personal_age_error),
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(R.string.personal_age_error);
                    }
                    mToast.show();
                } else {
                    mScanLine.setVisibility(View.VISIBLE);
                    personal_info_sex_ll.setClickable(false);
                    personal_info_age_ll.setClickable(false);
                    personal_info_stature_ll.setClickable(false);
                    personal_info_weight_ll.setClickable(false);
//				mPersonalInfo.setClickable(false);
                    mHealthBody.setClickable(false);
                    mPartOne.setVisibility(View.GONE);
                    mPartTow.setVisibility(View.GONE);
                    mPartThree.setVisibility(View.GONE);
                    mPartFour.setVisibility(View.GONE);
                    initScanAnimaiton();
                }
                break;
            case R.id.tv_ok:
                // 获取设置好的用户信息参数
                int sexValue = mSexPicker.getValue();
                int ageValue = mAgePicker.getValue();
                int statureValue = mStaturePicker.getValue();
                int statureFootValue = stature_picker_imperial.getValue();
                int statureInchValue = stature_inch_picker_imperial.getValue();
                int weightValue = mWeightPicker.getValue();
                int weightPoundValue = weight_picker_imperial.getValue();

                // 性别
                if (sexValue != mSexValue) {
                    mSexValue = sexValue;
                    mSexTv.setText(mSexArr[mSexValue]);
                    if (mSexValue == 0) {
                        mHealthBody.setImageResource(R.drawable.health_body_male);
                    } else {
                        mHealthBody.setImageResource(R.drawable.health_body_female);
                    }
                }
                // 年龄
                if (ageValue != mAgeValue) {
                    mAgeValue = ageValue;
                    mAgeTv.setText(String.valueOf(mAgeValue));
                }
                if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
                    mStatureFootValue = statureFootValue;
                    mStatureInchValue = statureInchValue;
                    mWeightPoundValue = weightPoundValue;

                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    double realFootValue  = mStatureFootValue*(1.2);    // 6.0
                    String footVal = Utils.setformat(1, String.valueOf(realFootValue));
                    int mRealStatureFootValue = 0;
                    int mRealStatureInchValue = 0;
                    int zhengshu = 0;
                    int xiaoshu = 0;
                    if(footVal.contains(".")){
                        String[] foot = footVal.split("\\.");
                        zhengshu = Integer.valueOf(foot[0]);
                        xiaoshu = Integer.valueOf(foot[1]);
                    }

                    if(mStatureInchValue + xiaoshu >= 10){
                        int mod = (mStatureInchValue + xiaoshu)%10;
                        mRealStatureFootValue = zhengshu + 1;
                        mRealStatureInchValue = mod;
                    }else {
                        mRealStatureFootValue = zhengshu;
                        mRealStatureInchValue = mStatureInchValue + xiaoshu;
                    }
                    mStatureTv.setText(String.valueOf(mRealStatureFootValue) + "." + String.valueOf(mRealStatureInchValue));
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
//                    mStatureTv.setText(String.valueOf(mStatureFootValue) + "." + String.valueOf(mStatureInchValue));
                    mWeightTv.setText(String.valueOf(mWeightPoundValue));
                    SharedPreUtil.savePre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT, mStatureFootValue + "");
                    SharedPreUtil.savePre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN, mStatureInchValue + "");
                    SharedPreUtil.savePre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT_US, mWeightPoundValue + "");

                } else {
                    if (statureValue != mStatureValue) {
                        mStatureValue = statureValue;
                        mStatureTv.setText(String.valueOf(mStatureValue));
                        //	SharedPreUtil.savePre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.HEIGHT, mStatureValue+"");
                    }
                    if (weightValue != mWeightValue) {
                        mWeightValue = weightValue;
                        mWeightTv.setText(String.valueOf(mWeightValue));
                        //	SharedPreUtil.savePre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.WEIGHT, mWeightValue+"");
                    }

                }
                // 身高
                if (statureValue != mStatureValue) {
                    mStatureValue = statureValue;
                    mStatureTv.setText(String.valueOf(mStatureValue));
                    // SharedPreferencesUtils.setParam(PresentationActivity.this,
                    // HealthReportContantsData.USER_STATURE_KEY,mStatureValue);
                }
                // 体重
                if (weightValue != mWeightValue) {
                    mWeightValue = weightValue;
                    mWeightTv.setText(String.valueOf(mWeightValue));
                    // SharedPreferencesUtils.setParam(PresentationActivity.this,
                    // HealthReportContantsData.USER_WEIGHT_KEY, mWeightValue);
                }
                // 关闭弹窗
                mPopupWindow.dismiss();
                mPartOne.setVisibility(View.GONE);
                mPartTow.setVisibility(View.GONE);
                mPartThree.setVisibility(View.GONE);
                mPartFour.setVisibility(View.GONE);
                mBeginTestTv.setText(getString(R.string.body_health_test_text));
                mBeginTestTv.setVisibility(View.VISIBLE);
                break;*/


//                    default:
//                        break;
                }
        }
    }

    void initshow() {
//        finish_re.setVisibility(View.GONE);
//        finish_re2.setVisibility(View.GONE);
//        mPartOne.setVisibility(View.GONE);
//        mPartTow.setVisibility(View.GONE);
//        mPartThree.setVisibility(View.GONE);
//        mPartFour.setVisibility(View.GONE);
    }

    private void showPopupWindow(int i) {
        if (mUserInfo == null) {
            mUserInfo = LayoutInflater.from(PresentationActivity.this).inflate(R.layout.popup_user_info, null);
            mAgePicker = (NumberPicker) mUserInfo.findViewById(R.id.age_picker);
            mSexPicker = (NumberPicker) mUserInfo.findViewById(R.id.sex_picker);
            mStaturePicker = (NumberPicker) mUserInfo.findViewById(R.id.stature_picker);
            mWeightPicker = (NumberPicker) mUserInfo.findViewById(R.id.weight_picker);
            stature_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.stature_picker_imperial);
            stature_inch_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.stature_inch_picker_imperial);
            weight_picker_imperial = (NumberPicker) mUserInfo.findViewById(R.id.weight_picker_imperial);

            health_report_stature = (LinearLayout) mUserInfo.findViewById(R.id.health_report_stature);
            health_report_weight = (LinearLayout) mUserInfo.findViewById(R.id.health_report_weight);
            health_report_stature_ll = (LinearLayout) mUserInfo.findViewById(R.id.health_report_stature_);
            health_report_weight_ll = (LinearLayout) mUserInfo.findViewById(R.id.health_report_weight_);
            mSure = (TextView) mUserInfo.findViewById(R.id.tv_ok);
            mSure.setOnClickListener(this);
            mSexPicker.setDisplayedValues(mSexArr);
            mSexPicker.setMaxValue(mSexArr.length - 1);
            mAgePicker.setMinValue(18);
            mAgePicker.setMaxValue(70);
            mStaturePicker.setMinValue(100);
            mStaturePicker.setMaxValue(210);
            mWeightPicker.setMinValue(20);
            mWeightPicker.setMaxValue(150);
            stature_picker_imperial.setMinValue(4);
            stature_picker_imperial.setMaxValue(8);
            stature_inch_picker_imperial.setMinValue(0);
            stature_inch_picker_imperial.setMaxValue(11);
            weight_picker_imperial.setMinValue(40);
            weight_picker_imperial.setMaxValue(500);
            TypedArray a = PresentationActivity.this.obtainStyledAttributes(new int[]{R.attr.global_text_color});
            setNumberPickerTextColor(mSexPicker, a.getColor(0, Color.parseColor("#4c5157")));
            setNumberPickerTextColor(mAgePicker, a.getColor(0, Color.parseColor("#4c5157")));
            setNumberPickerTextColor(mStaturePicker, a.getColor(0, Color.parseColor("#4c5157")));
            setNumberPickerTextColor(mWeightPicker, a.getColor(0, Color.parseColor("#4c5157")));
            setNumberPickerTextColor(stature_picker_imperial, a.getColor(0, Color.parseColor("#4c5157")));
            setNumberPickerTextColor(stature_inch_picker_imperial, a.getColor(0, Color.parseColor("#4c5157")));
            setNumberPickerTextColor(weight_picker_imperial, a.getColor(0, Color.parseColor("#4c5157")));

        }
//        mSexPicker.setValue(mSexValue);
//        mAgePicker.setValue(mAgeValue);
//        mStaturePicker.setValue(mStatureValue);
//        mWeightPicker.setValue(mWeightValue);
//        stature_picker_imperial.setValue(mStatureFootValue);
//        stature_inch_picker_imperial.setValue(mStatureInchValue);
//        weight_picker_imperial.setValue(mWeightPoundValue);
        if (i == 0) {
            mSexPicker.setVisibility(View.VISIBLE);
            mAgePicker.setVisibility(View.GONE);
            health_report_stature.setVisibility(View.GONE);
            health_report_weight.setVisibility(View.GONE);
            health_report_stature_ll.setVisibility(View.GONE);
            health_report_weight_ll.setVisibility(View.GONE);
        } else if (i == 1) {
            mSexPicker.setVisibility(View.GONE);
            mAgePicker.setVisibility(View.VISIBLE);
            health_report_stature.setVisibility(View.GONE);
            health_report_weight.setVisibility(View.GONE);
            health_report_stature_ll.setVisibility(View.GONE);
            health_report_weight_ll.setVisibility(View.GONE);
        } else if (i == 2) {
            if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.VISIBLE);
                health_report_weight.setVisibility(View.GONE);
                health_report_stature_ll.setVisibility(View.GONE);
                health_report_weight_ll.setVisibility(View.GONE);
            } else {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.GONE);
                health_report_weight.setVisibility(View.GONE);
                health_report_stature_ll.setVisibility(View.VISIBLE);
                health_report_weight_ll.setVisibility(View.GONE);
            }
        } else if (i == 3) {

            if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.GONE);
                health_report_weight.setVisibility(View.VISIBLE);
                health_report_stature_ll.setVisibility(View.GONE);
                health_report_weight_ll.setVisibility(View.GONE);
            } else {
                mSexPicker.setVisibility(View.GONE);
                mAgePicker.setVisibility(View.GONE);
                health_report_stature.setVisibility(View.GONE);
                health_report_weight.setVisibility(View.GONE);
                health_report_stature_ll.setVisibility(View.GONE);
                health_report_weight_ll.setVisibility(View.VISIBLE);
            }
        }
        mPopupWindow = new PopupWindow(mUserInfo, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.showAtLocation(HealthView, Gravity.BOTTOM, 0, 0);
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initScanAnimaiton() {
       /* if (mScanAnimation == null) {
            float height = mHealthBody.getHeight() - mHealthBody.getHeight() / 20;
            mScanAnimation = new TranslateAnimation(0, 0, 0, height);
            mScanAnimation.setDuration(3000);
            mScanAnimation.setRepeatCount(1);
            mScanAnimation.setRepeatMode(Animation.REVERSE);
            mScanAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    mScanLine.setImageResource(R.drawable.health_body_scan_line);
                    mBeginTestTv.setText(PresentationActivity.this.getResources().getText(
                            R.string.health_is_test_text));
                    mBeginTestTv.setVisibility(View.VISIBLE);
                    mScanBox.setVisibility(View.VISIBLE);
                    finish_re.setVisibility(View.GONE);
                    finish_re2.setVisibility(View.GONE);
                    onCalculate();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //Bitmap pic = rotatePic(180);
                    //	mScanLine.setImageBitmap(pic);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mScanLine.setVisibility(View.GONE);
                    mScanBox.setVisibility(View.GONE);
                    mBeginTestTv.setText("");
                    mBeginTestTv.setVisibility(View.GONE);
                    mHandler.sendEmptyMessageDelayed(1, 500);
                    mHandler.sendEmptyMessageDelayed(2, 1500);
                    mHandler.sendEmptyMessageDelayed(3, 2500);
                    mHandler.sendEmptyMessageDelayed(4, 3500);
                    mHandler.sendEmptyMessageDelayed(5, 4500);
                    setResult();
                }
            });
        }
        mScanLine.setAnimation(mScanAnimation);
        mScanAnimation.startNow();*/
    }

    // 计算健康情况
    private void onCalculate() {
        // 计算BMI
       /* double stature = (double) mStatureValue / 100;
        mBMIValue = (int) (mWeightValue / (stature * stature));
        // 计算体脂率

        mBodyFatValue = Utils.setformat(1, 1.2 * mBMIValue + 0.23 * mAgeValue - 5.4 - 10.8 * mSexValue + "");
        // 获取心率,没有心率功能得话，就显示"暂无心率功能"
        mHeartRateValue = 65;
        // 计算肺活量
        double averageDistance = 0.0;
        averageDistance = getLastTowWeeksAverageDistance();
        if (averageDistance == 0.0) {
            averageDistance = 4.0;
        }
        mVitalCapacityValue = (int) (mBaseVitalCapacity * (averageDistance * 0.04 + 1));
        // 计算心率
        if (mSexValue == 0) {
            mHeartRate = (int) (((60 * 0.7 * 60 / 1000) * 0.3 + (60 - mAgeValue) * 0.5 + mWeightValue * 0.2) * 4.5);
        } else {
            mHeartRate = (int) (((60 * 0.5 * 60 / 1000) * 0.3 + (60 - mAgeValue) * 0.5 + mWeightValue * 0.2) * 4.5);
        }
        if (mHeartRate < 60) {
            mHeartRate = 60;
        }
        if (mHeartRate > 180) {
            mHeartRate = 180;
        }
        Random random = new Random();
        mHeartRate = random.nextInt(10) + 70;

        // 计算健康指数
        double relativeBMI = HealthReportContantsData.BMI_WEIGHT - Math.abs(mBaseBMI - mBMIValue) * 1.5;
        double relativeHeartRate = HealthReportContantsData.HEART_RATE_WEIGHT
                - Math.abs(mBaseHeartRate - mHeartRateValue) * 0.3;
        double relativeVitalCapacity = HealthReportContantsData.VITAL_CAPACITY_WEIGHT
                + (mVitalCapacityValue - mBaseVitalCapacity) / 100;
        mHealthLevel = (int) (relativeBMI + relativeHeartRate + relativeVitalCapacity);
        if (mHealthLevel < 10) {
            mHealthLevel = 10;
        }
        // 计算身体年龄
        mBodyAgeValue = (int) ((80 - mHealthLevel) / 1.5 + mAgeValue);
        if (mBodyAgeValue < 10) {
            mBodyAgeValue = 10;
        }*/
    }

    private double getLastTowWeeksAverageDistance() {
        double averageDistance = 0.0;
        double dayDistance = 0.0;
        double totalDistance = 0.0;
        int totalDays = 14;
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            Date date = calendar.getTime();
            String strDate = mSimpleDateFormat.format(date);
            arrRunDataLastTowWeeks = judgmentRunDB(strDate);
            if (arrRunDataLastTowWeeks.size() != 0) {
                for (int j = 0; j < arrRunDataLastTowWeeks.size(); j++) {
                    dayDistance += Double.valueOf(arrRunDataLastTowWeeks.get(j)
                            .getDistance());
                }
                totalDistance += dayDistance;
            } else {
                totalDays -= 1;
            }
            calendar.add(Calendar.DATE, -1);
        }
        if (totalDays != 0) {
            averageDistance = totalDistance / totalDays;
        }
        averageDistance = averageDistance / 1000;
        return averageDistance;
    }

    private ArrayList<RunData> judgmentRunDB(String choiceDate) {
        if (db == null) {
            db = DBHelper.getInstance(PresentationActivity.this);
        }

        /*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(SharedPreUtil.readPre(PresentationActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2")
                && df.format(new Date()).toString().equals(choiceDate)){
            int step = Integer.parseInt(SharedPreUtil.readPre(PresentationActivity.this,SharedPreUtil.BLEWATCHDATA,SharedPreUtil.RUN));
            float calorie = Float.parseFloat(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
            float distance = Float.parseFloat(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
            String time = SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME);
            RunData runData = new RunData();
            runData.setDate(time.split(" ")[0]);
            runData.setStep(step + "");
            runData.setCalorie(calorie + "");
            runData.setDistance(distance + "");
            runData.setBinTime(time);
            ArrayList<RunData> list = new ArrayList<>();
            list.add(runData);
            return list;
        }*/

        Query query = null;
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER,
                SharedPreUtil.SHOWMAC).equals("")) {
            query = db
                    .getRunDao()
                    .queryBuilder()
                    // .where(RunDataDao.Properties.Mid.eq(mid))
                    .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                            PresentationActivity.this, SharedPreUtil.USER,
                            SharedPreUtil.MAC)))
                    .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
        } else {
            query = db
                    .getRunDao()
                    .queryBuilder()
                    // .where(RunDataDao.Properties.Mid.eq(mid))
                    .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                            PresentationActivity.this, SharedPreUtil.USER,
                            SharedPreUtil.SHOWMAC)))
                    .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
        }

        List list = query.list();

        ArrayList<RunData> runData = new ArrayList<RunData>();
        if (list != null && list.size() >= 1) {

            for (int j = 0; j < list.size(); j++) {
                RunData runDB = (RunData) list.get(j);
                runData.add(runDB);
            }

        }
        return runData;
    }

    // 图片翻转显示
    private Bitmap rotatePic(int degree) {
        Resources res = PresentationActivity.this.getResources();
        Bitmap img = BitmapFactory.decodeResource(res,
                R.drawable.health_body_scan_line);
        Matrix matrix = new Matrix();
        matrix.postRotate(degree); /* 翻转180度 */
        int width = img.getWidth();
        int height = img.getHeight();
        Bitmap img_a = Bitmap.createBitmap(img, 0, 0, width, height, matrix,
                true);
        return img_a;
    }

    private void setResultAnimation(LinearLayout view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1500);
        view.setAnimation(alphaAnimation);
        alphaAnimation.startNow();

    }

    private void setResult() {
      /*  mBMITv.setText(String.valueOf(mBMIValue));
        mBodyFatNumTv.setText(String.valueOf(mBodyFatValue));
        if (mSexValue == 0) {
            if (mBMIValue >= 40) {
                mBodyFatTextTv.setText(R.string.health_fat_super_fat);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 35) {
                mBodyFatTextTv.setText(R.string.health_fat_very_fat);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 30) {
                mBodyFatTextTv.setText(R.string.health_fat_fat);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 25) {
                mBodyFatTextTv.setText(R.string.health_fat_overweight);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 18.5) {
                mBodyFatTextTv.setText(R.string.health_fat_normal);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_green));
            } else {
                mBodyFatTextTv.setText(R.string.health_fat_thin);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            }
        } else {
            if (mBMIValue >= 40) {
                mBodyFatTextTv.setText(R.string.health_fat_super_fat);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 35) {
                mBodyFatTextTv.setText(R.string.health_fat_very_fat);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 30) {
                mBodyFatTextTv.setText(R.string.health_fat_fat);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 25) {
                mBodyFatTextTv.setText(R.string.health_fat_overweight);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else if (mBMIValue >= 18.5) {
                mBodyFatTextTv.setText(R.string.health_fat_normal);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_green));
            } else {
                mBodyFatTextTv.setText(R.string.health_fat_thin);
                mBodyFatTextTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            }
        }

        mVitalCapacityTv.setText(String.valueOf(mVitalCapacityValue));
        mHealthScordNumTv.setText(String.valueOf(mHealthLevel));
        mBodyAgeNumTv.setText(String.valueOf(mBodyAgeValue));
        mHeartRateTv.setText(mHeartRate + "");
        if (mSexValue == 0) {
            if (mHeartRate > (210 - mAgeValue / 2 - 0.11 * mWeightValue - 4)) {
                mCardiopulmonaryTv.setText(R.string.health_score_bad);
                mCardiopulmonaryTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else {
                mCardiopulmonaryTv.setText(R.string.health_score_good);
                mCardiopulmonaryTv.setTextColor(getResources().getColor(R.color.body_green));
            }
        } else {
            if (mHeartRate > (210 - mAgeValue / 2 - 0.11 * mWeightValue)) {
                mCardiopulmonaryTv.setText(R.string.health_score_bad);
                mCardiopulmonaryTv.setTextColor(getResources().getColor(R.color.body_overweight_text));
            } else {
                mCardiopulmonaryTv.setText(R.string.health_score_good);
                mCardiopulmonaryTv.setTextColor(getResources().getColor(R.color.body_green));
            }
        }*/
    }//////////////

    private void setSportData() {
        // TODO

    }

    private void date_downturning() {
        isDateUpDown = false;
        change_date = getCurDate();
        // 日期处理方法返回的日期
        String downtime_str = UTIL.getSubtractDay(change_date);
        setCurDate(downtime_str);
//        mTimelineCurdatetv.setText(downtime_str);

        if(pageindex == 0){
                    judgmentRunDB();
        }else if(pageindex == 1){
                    judgmentSleepDB();
        }else if(pageindex == 2){
                    judgmentHeartDB();
        }else if(pageindex == 3){
                    judgmentBloodpressureDaoDB();//更新血压
        }else if(pageindex == 4){
                    judgmentOxygenDB();//血氧
        }
    }

    private void setCurDate(String downtime_str) {
        if (Utils.isDe()) {
            downtime_str = Utils.dateInversion(downtime_str);
        }
        mReportCurdatetv.setText(downtime_str);
    }

    private void dateInit() {
        // *************************获取系统时间************************************
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                try {
//                    Thread.sleep(10);
                    if(StringUtils.isEmpty(mCurrentTimeStr) ||mCurrentTimeStr.length()==0) {
                        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                        mCurrentTimeStr = mSimpleDateFormat.format(curDate);
                    }
                    Message msg = new Message();
                    msg.what = SYNCTIME;
                    msg.obj = mCurrentTimeStr;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
//        }).start();
    }

    private void date_upturning() {
        isDateUpDown = true;
        change_date = getCurDate();
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        mCurrentTimeStr = mSimpleDateFormat.format(curDate);
        if (change_date.equals(mCurrentTimeStr)) {
            Toast.makeText(PresentationActivity.this, R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
        } else {
            //clearABCD();//切换成功才清除
            uptime_str = UTIL.getAddDay(change_date);
            setCurDate(uptime_str);
//            mTimelineCurdatetv.setText(uptime_str);

            if(pageindex == 0){
                        judgmentRunDB();
            }else if(pageindex == 1){
                        judgmentSleepDB();
            }else if(pageindex == 2){
                        judgmentHeartDB();
            }else if(pageindex == 3){
                        judgmentBloodpressureDaoDB();//更新血压
            }else if(pageindex == 4){
                        judgmentOxygenDB();//血氧
            }
        }
    }

    @NonNull
    private String getCurDate() {
        if (Utils.isDe()) {
            return dateInversion(mReportCurdatetv.getText().toString());
        } else {
            return mReportCurdatetv.getText().toString();
        }
    }

    private void sethearttitileText(int x) {
        switch (x) {
            case 0:
                if (views.size() == 3) {
                    cb_navigation_heart.setText("");
                    cb_navigation_sport.setText(PresentationActivity.this.getString(R.string.timeline));
                    cb_navigation_sleep.setText(PresentationActivity.this.getString(R.string.data_reporting));
                } else {
                    cb_navigation_heart.setText("");
                    cb_navigation_sport.setText(PresentationActivity.this.getString(R.string.data_reporting));
                    //cb_navigation_sleep.setText(PresentationActivity.this.getString(R.string.health));
                }
                break;
            case 1:
                if (views.size() == 3) {
                    cb_navigation_heart.setText(PresentationActivity.this.getString(R.string.timeline));
                    cb_navigation_sport.setText(PresentationActivity.this.getString(R.string.data_reporting));
                    cb_navigation_sleep.setText(PresentationActivity.this.getString(R.string.health));
                } else {
                    cb_navigation_heart.setText(PresentationActivity.this.getString(R.string.data_reporting));
                    cb_navigation_sport.setText(PresentationActivity.this.getString(R.string.health));
                    cb_navigation_sleep.setText("");
                }
                break;
            case 2:
                cb_navigation_heart.setText(PresentationActivity.this.getString(R.string.data_reporting));
                cb_navigation_sport.setText(PresentationActivity.this.getString(R.string.health));
                cb_navigation_sleep.setText("");
                break;
            default:
                break;
        }
    }

    private void judgmentRunDB() {   // 获取运动数据  ---- TODO 填充活动报告 图表
        stepData = new ArrayList<>();
        ArrayList<ChartViewCoordinateData> stepData_effective = new ArrayList<>();
        maxStepValue = 0;  // 最大的步数值
        int step = 0;       // 总步数值
        if (db == null) {
            db = DBHelper.getInstance(PresentationActivity.this);
        }
        String strDate = getCurDate();
        String choiceDate = arrangeDate(strDate);    // 当前选择 的 日期


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Query query = null;

        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){  //todo --- MTK   Query querySum
            query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(RunDataDao.Properties.Date.eq(choiceDate)).build();   // .build();
//                            .where(RunDataDao.Properties.Step.notEq("0")).build();
            List<RunData> list = query.list();  // TODO----  根据日期查询当前的计步     获取到本地运动数据    ---- 根据当天的日期
            Log.e(TAG, "list.size == " + list.size());
            if (list != null && list.size() >= 1) {  // 本地有运动数据

//////////////////////////////////////////////////////////////////////////////////////////////////////////
                if(list.size() == 1 && list.get(0).getStep().equals("0") && !list.get(0).getDayStep().equals("0")){  //只有一条数据，分段步数为0，总步数不为0
                    ChartViewCoordinateData coordinateData;   // 填充图标数据的类
                    RunData runDB = (RunData) list.get(0);
                    coordinateData = new ChartViewCoordinateData();
                    coordinateData.x = Integer.parseInt(runDB.getHour());  //运动数据的小时值
//                            Log.e("MTK--fenduanStep-----", runDB.getStep() + "----" + runDB.getHour());
                    coordinateData.value = Integer.parseInt(runDB.getDayStep()); //TODO ---- MTK 取  step(一天的分段步数)      dayStep（一天的总步数）
                    coordinateData.calorie = Float.parseFloat(runDB.getDayCalorie());
                    coordinateData.distance = Float.parseFloat(runDB.getDayDistance());
                    if (maxStepValue < coordinateData.value)
                        maxStepValue = coordinateData.value;  //重置最大的步数值  TODO   计步图表数据的最大 步数值


                    if( Integer.parseInt(runDB.getDayStep()) > 0){ // MTK 一天的步数
                        step = Integer.parseInt(runDB.getDayStep());  // 运动的总步数值
                    }
                    stepData.add(coordinateData);  //所有分段步数运动数据的集合    ------------- //TODO 计步的图表数据
                    if(coordinateData.getValue()>0)
                        stepData_effective.add(coordinateData);
                }else {
                    ChartViewCoordinateData coordinateData;   // 填充图标数据的类
                    boolean hasFenduanData = false;
                    int stepNum = 0;
                    for (int j = 0; j < list.size(); j++) {
                        RunData runDB = (RunData) list.get(j);
                        coordinateData = new ChartViewCoordinateData();
                        coordinateData.x = Integer.parseInt(runDB.getHour());  //运动数据的小时值
                        Log.e("MTK--fenduanStep-----", runDB.getStep() + "----" + runDB.getHour());


                        int value = Integer.parseInt(runDB.getStep()); //TODO ---- MTK 取  step(一天的分段步数)      dayStep（一天的总步数）;
                        float calorie = Float.parseFloat(runDB.getCalorie());
                        float distance = Float.parseFloat(runDB.getDistance());


                        boolean isFlag = false;
                        if(stepData.size() > 0){
                            for (int i = 0; i < stepData.size(); i++) {
                                if(stepData.get(i).getX() == coordinateData.x){
                                    isFlag = true;
                                    break;
                                }
                            }
                        }
                        if(!isFlag) {
                            for (int i = (j + 1); i < list.size(); i++) {
                                if (Integer.parseInt(list.get(i).getHour()) == coordinateData.x) {
                                    value += Integer.parseInt(list.get(i).getStep());
                                    calorie += Float.parseFloat(list.get(i).getCalorie());
                                    distance += Float.parseFloat(list.get(i).getDistance());
                                }
                            }

                            coordinateData.value = value;
                            coordinateData.calorie = calorie;
                            coordinateData.distance = distance;


                            stepNum += value;
                            if (maxStepValue < coordinateData.value)
                                maxStepValue = coordinateData.value;  //重置最大的步数值  TODO   计步图表数据的最大 步数值

                            if (Integer.parseInt(runDB.getDayStep()) > 0) {
                                hasFenduanData = true;
                            }

                            if (Integer.parseInt(runDB.getDayStep()) > 0) { // MTK 一天的步数
                                step = Integer.parseInt(runDB.getDayStep());  // 运动的总步数值
                            }


                            stepData.add(coordinateData);  //所有分段步数运动数据的集合    ------------- //TODO 计步的图表数据
                            if (coordinateData.getValue() > 0)
                                stepData_effective.add(coordinateData);
                        }
                    }
                    if(!hasFenduanData){
                        step = stepNum;
                    }
                }
            } else if (!SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {

            } else {  // 没有本地数据
                Message msg = mHandler.obtainMessage();
                msg.what = CLEARSPORT;      // 没有本地数据  ，发清空 数据的消息  ---- 无效demo
                mHandler.sendMessage(msg);
            }

        }else {   // todo --- H872,BLE
            if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {      // 不需要展示的设备的数据的mac地址
                query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
            } else {                                                                                    // 需要展示的设备的数据的mac地址。//后面被产品去掉，
                query = db.getRunDao().queryBuilder().where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
            }

            List<RunData> list = query.list();  // TODO----  根据日期查询当前的计步     获取到本地运动数据    ---- 根据当天的日期
            Log.e("HYC", "list.size == " + list.size());
            if (list != null && list.size() >= 1) {  // 本地有运动数据
                ChartViewCoordinateData coordinateData;   // 填充图标数据的类

                String mrunBinTime = "";
                List<RunData> listok = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    if(!list.get(j).getBinTime().equals(mrunBinTime)) {
                        mrunBinTime = list.get(j).getBinTime();
                        if(listok.size() > 0){
                            boolean isExist = false;
                            for(RunData data:listok){
                                if(data.getBinTime().equals(list.get(j).getBinTime())){
                                    isExist = true;
                                    break;
                                }
                            }
                            if(!isExist){
                                listok.add(list.get(j));
                            }
                        }else {
                            listok.add(list.get(j));
                        }
                    }
                }

                if(listok.size() > 0 ){
                    for (int j = 0; j < listok.size(); j++) {
                        RunData runDB = (RunData) listok.get(j);
                        coordinateData = new ChartViewCoordinateData();
                        coordinateData.x = Integer.parseInt(runDB.getHour());  //运动数据的小时值
                        coordinateData.value = Integer.parseInt(runDB.getStep()); ////TODO ---- H872 取  step 字段获取步数 （step字段 --- 分段步数）
                        coordinateData.calorie = Float.parseFloat(runDB.getCalorie());
                        coordinateData.distance = Float.parseFloat(runDB.getDistance());
                        if (maxStepValue < coordinateData.value)
                            maxStepValue = coordinateData.value;  //重置最大的步数值  TODO   计步图表数据的最大 步数值
                        step += coordinateData.value;  // 运动的总步数值
                        stepData.add(coordinateData);  //所有运动数据的集合    ------------- //TODO 计步的图表数据
                        if(coordinateData.getValue()>0)
                            stepData_effective.add(coordinateData);
                    }
                }
            } else if (!SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {

            } else {  // 没有本地数据
                Message msg = mHandler.obtainMessage();
                msg.what = CLEARSPORT;      // 没有本地数据  ，发清空 数据的消息  ---- 无效demo
                mHandler.sendMessage(msg);
            }
        }

        if(stepData_effective != null && stepData_effective.size() > 0) {
            Collections.sort(stepData_effective, new Comparator<ChartViewCoordinateData>() {
                @Override
                public int compare(ChartViewCoordinateData lhs, ChartViewCoordinateData rhs) {
                    int hour1 = lhs.x;
                    int hour2 = rhs.x;
                    if (hour1 > hour2) {
                        return -1;
                    } else if (hour1 == hour2) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }

        Message msg = mHandler.obtainMessage();
        msg.what = UPUERRUNINFO;   //发送更新运动数据的消息    ---- 更新数据报告页面 活动报告 图表
        Bundle bundle = new Bundle();
        bundle.putInt("step",step);
        bundle.putSerializable("stepList",stepData_effective);
        //msg.obj = step;   // ---- 1501     --- 0 (2763)
        msg.obj = bundle;
        mHandler.sendMessage(msg);
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

    private SimpleDateFormat format = Utils.setSimpleDateFormat("yyyy-MM-dd HH");
    private SimpleDateFormat hourMinFormat = Utils.setSimpleDateFormat("HH:mm");
    private SimpleDateFormat hourFormat = Utils.setSimpleDateFormat("HH");
    private SimpleDateFormat minFormat = Utils.setSimpleDateFormat("mm");

    private void judgmentSleepDB() {   // 获取睡眠数据

        ArrayList<ReportSleepData> arrSleep = new ArrayList<>();  // 睡眠报告 集合    按日期查询 睡眠数据
        String strDate = getCurDate();
        String choiceDate = arrangeDate(strDate);    // 当前选择 的 日期     2017-04-09
        String beginDate = "";
        Date startTimeDate;
        Calendar calendar = Calendar.getInstance();
        try {
            startTimeDate = getDateFormat.parse(strDate);  // 当前天的 日期格式
            calendar.setTime(startTimeDate);
            calendar.add(Calendar.DATE, -1);
            beginDate = getDateFormat.format(calendar.getTime()).toString();  // 2017-04-06     2017-04-11   // TODO 后一天日期
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String choiceDateBegin = arrangeDate(beginDate); // 后一天的日期
        int nextDateriqi = Integer.valueOf(choiceDate.substring(8, 10));  //TODO --- 后一天的 日期值
        int curDateriqi = Integer.valueOf(choiceDateBegin.substring(8,10));  //TODO --- 当前天的 日期值
        Query query = null;
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
            query = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDateBegin)).build();
        } else {
            query = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDateBegin)).build();
        }

        Query queryEnd = null;
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 根据当前日期 查询 睡眠数据
            queryEnd = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
        } else {
            queryEnd = db.getSleepDao().queryBuilder()
                    // .where(SleepDataDao.Properties.Mid.eq(mid))
                    .where(SleepDataDao.Properties.Mac
                            .eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC)))
                    .where(SleepDataDao.Properties.Date.eq(choiceDate)).build();
        }

        List listCur = query.list();    //按日期查询到当前天的 睡眠 数据
        List listPre = queryEnd.list();  // 查询前一天的 睡眠 数据
        List<SleepData> list = new ArrayList();
        list.addAll(listCur);
        list.addAll(listPre); // 将当前天和上一天的睡眠数据都添加

        if (list != null && list.size() >= 1) {  // 本地有睡眠的数据
            ArrayList<SleepData> arrSleepSql = new ArrayList<SleepData>();
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String msleepBinTime = "";
            for (int j = 0; j < list.size(); j++) {
                if(!list.get(j).getStarttimes().equals(msleepBinTime)) {
                    msleepBinTime = list.get(j).getStarttimes();
                    if(arrSleepSql.size() > 0){
                        boolean isExist = false;
                        for(SleepData data:arrSleepSql){
                            if(data.getStarttimes().equals(list.get(j).getStarttimes())){
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            arrSleepSql.add(list.get(j));
                        }
                    }else {
                        arrSleepSql.add(list.get(j));
                    }
                }
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (arrSleepSql.size()>0) {
                // 当前天的 21点 的时间戳  前一天 9 点的时间戳
                String strDateTv = getCurDate();  // 2017-04-09   2017-04-10    // TODO 当前控件上的  开始日期   2017-04-09
                String choiceDateTv = arrangeDate(strDateTv);

                Date startTimeDates;
                Calendar calendar1 = Calendar.getInstance();
                try {
                    startTimeDates = getDateFormat.parse(strDate);  // 当前天的 日期格式
                    calendar1.setTime(startTimeDates);
                    calendar1.add(Calendar.DATE, -1);
                    strDateTv = getDateFormat.format(calendar1.getTime()).toString();  // 2017-04-06     2017-04-11   // TODO 结束日期
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                strDateTv = strDateTv + " 21";  //   2017-04-07 08      2017-04-08 21
                Date endTimeDate;
                long endTime = 0;    //   当前日期的睡眠的结束时间
                Calendar calendar4 = Calendar.getInstance();
                Date startTimeDateTv;
                long startTime = 0;   //   当前日期的睡眠开始时间
                try {
                    startTimeDateTv = format.parse(strDateTv);   // Fri Apr 07 08:00:00 GMT+08:00 2017     ---- 20170407 --- 08:00
                    //TODO --- 当前日期 的 21 点的时间戳    当前日期的开始时间 1491742800000
                    startTime = startTimeDateTv.getTime()/1000;     //当天日期的21 点 的开始时间   1491742800   ---- 2017/4/9 21:0:0      1491656400  --- 2017/4/8 21:0:0
                    calendar4.setTime(startTimeDateTv);
                    calendar4.add(Calendar.DATE, +1);
                    String end = getDateFormat.format(calendar4.getTime()).toString();  //
                    end = end + " 09";     // 晚上 9 点到第2天的 8 点
                    endTimeDate = format.parse(end);    //   20170406 21:00:00
                    //TODO --- 前一天日期的 9 点的时间戳    当前日期的结束时间 1491786000000
                    endTime = endTimeDate.getTime()/1000;   //后一天天日期的 9 点 的结束时间 1491786000   --- 2017/4/10 9:0:0     1491699600 ----  2017/4/9 9:0:0
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < arrSleepSql.size(); i++) {
                    String endTimeStr = arrSleepSql.get(i).getEndTime(); //TODO 数据库中结束时间  日期格式   2017-04-09 21:00:00
                    Date date = StringUtils.parseStrToDate(endTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(date);
                    long sleepDataEndTime = calendar2.getTimeInMillis() / 1000;  //将日期格式转为时间戳 1489816800   TODO --- 每一条睡眠数据的 结束时间  10位时间戳     1491656400--2017/4/8 21:0:0

                    String startTimeStr = arrSleepSql.get(i).getStarttimes(); //TODO 数据库中开始时间  日期格式   2017-04-09 21:00:00
                    Date date2 = StringUtils.parseStrToDate(startTimeStr, StringUtils.SIMPLE_DATE_FORMAT);
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(date2);
                    long sleepDataStartTime = calendar3.getTimeInMillis() / 1000;  //TODO  数据库睡眠数据的 开始时间   1491707220   2017/4/9 11:7:0

                    //todo  1： 结束时间点必须大于 21 点
                    if(sleepDataEndTime > startTime){  // TODO 当天睡眠数据的有效数据 应该 结束时间 >= 当天的21点 <= 后一天的 9点         sleepDataEndTime >= startTime && sleepDataEndTime <= endTime
                        SleepData sleepDB =  arrSleepSql.get(i);   // 睡眠数据 bean 类
                        String ee = sleepDB.getStarttimes().substring(11, 13);  // 01   开始时间 小时
                        int hour = Integer.valueOf(ee);
                        String ee2 = sleepDB.getStarttimes().substring(14, 16);  // 15  开始时间 分钟
                        int fen = Integer.valueOf(ee2);
                        String ee3 = sleepDB.getStarttimes().substring(8, 10);  // 数据库睡眠数据 开始时间 的日期值
                        int sqlDateValue = Integer.valueOf(ee3);  //数据库睡眠数据 的日期值

                        if(sleepDataEndTime <= endTime){  //TODO 结束时间 在 第2天 的 9点之前
                            ReportSleepData sleepData = new ReportSleepData();   // 睡眠报告 的 bean 类
                            if(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")  || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
                                if (Integer.parseInt(sleepDB.getSleeptype()) == 2)  //TODO 为1 深睡    // runDB.getDeepsleep() ----- 为深睡时间
                                    sleepData.setDeepSleep(true);
                                else if (Integer.parseInt(sleepDB.getSleeptype()) == 1)  // TODO 为2 浅睡
                                    sleepData.setDeepSleep(false);  // || StringUtils.isEmpty(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH))
                            }else if(SharedPreUtil.readPre(PresentationActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(PresentationActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
                                     || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")  || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
                                if (Integer.parseInt(sleepDB.getSleeptype()) == 1)  // TODO 为1 浅睡    // runDB.getDeepsleep() ----- 为深睡时间
                                    sleepData.setDeepSleep(false);
                                else if (Integer.parseInt(sleepDB.getSleeptype()) == 2)  //TODO 为2 深睡
                                    sleepData.setDeepSleep(true);
                            }

                            if(sqlDateValue == curDateriqi){  // 是当前天 取21 点 以后的数据         ------ 通过上面两个 if 语句后，当前时间 肯定时当天的日期
                                if(hour >= 21){  //TODO 开始时间 当天的21点 以后的睡眠数据      21:00 ---- 09:00     12*60 = 720
                                    sleepData.setStartTime(hour * 60 + fen - 21 * 60);
                                    int bb = (int) (Long.parseLong(sleepDB.getSleepmillisecond()) / 1000 / 60);  // 睡眠的分钟数   17
                                    sleepData.setSleepTime(bb);   // 设置睡眠报告的 总睡眠时间   ---- 分钟

                                    if(i == arrSleepSql.size()-1){ // todo --- 最后一条睡眠数据 保存睡眠结束时间
                                        String  endTimeLast = arrSleepSql.get(i).getEndTime();    // 2017-11-03 08:03:00
                                        String endTimeLastOk = endTimeLast.substring(11,16);
                                        sleepData.setEndTimeStr(endTimeLastOk);      // arrSleepSql.get(i).getEndTime();
                                    }

                                    arrSleep.add(sleepData);    //添加 睡眠报告 集合
                                }else {  // TODO --- 开始时间 是当天 21 点 之前 的时间
                                    int bb = (int) (Long.parseLong(sleepDB.getSleepmillisecond()) / 1000 / 60);  //当天21点之前 睡眠的分钟数
                                    int before21Time = 0;
                                    if(fen == 0){ // 开始时间为整点
                                        before21Time = (21 - hour)*60; // 21点之前的睡眠时间
                                    }else {
                                        before21Time = (21 - hour -1 )*60 + 60 - fen;
                                    }

                                    int after21Time = 0;
                                    if(bb >= before21Time ){  // 正常时 bb 是 必须大于 before21Time  --- 防止手表端数据传错
                                        after21Time = bb - before21Time;  // 21 点后的睡眠 时间
                                    }
                                    sleepData.setStartTime(0); // 开始时间为 21点
                                    sleepData.setSleepTime(after21Time);// 设置21点之后的睡眠时长

                                    arrSleep.add(sleepData);  // todo  --- add 20171220
                                }
                            }else if(sqlDateValue == nextDateriqi){  // 开始时间的日期 是后一天的 日期 睡眠 数据 ，取 9 点 之前的 睡眠数据
                                if(hour < 9){  //后一天 9点之前的    后一天的睡眠开始 时间 在 9 点之前
                                    int bb = (int) (Long.parseLong(sleepDB.getSleepmillisecond()) / 1000 / 60);  //当天21点之前 睡眠的分钟数
                                    sleepData.setStartTime(hour * 60 + fen + 180);// 设置9点之前的 开始时间    在睡眠报告图上 就算 总睡眠 时间超过了 9点，应该也无影响
                                    int sleepStime = hour * 60 + fen;
                                    int sleepokTime = 540 - sleepStime; // 睡眠的有效时间
                                    if(bb > sleepokTime){  // 是否需要 计算 9点之前的 睡眠时长
                                        sleepData.setSleepTime(sleepokTime);   // 设置睡眠报告的 总睡眠时间   ---- 分钟
                                    }else {
                                        sleepData.setSleepTime(bb);
                                    }

                                    if(i == arrSleepSql.size()-1){ // todo --- 最后一条睡眠数据 保存睡眠结束时间
                                        String  endTimeLast = arrSleepSql.get(i).getEndTime();    // 2017-11-03 08:03:00    ---------------    2017-11-03 08:03:00
                                        String endTimeLastOk = endTimeLast.substring(11,16);   // 08:03
                                        sleepData.setEndTimeStr(endTimeLastOk);      // arrSleepSql.get(i).getEndTime();
                                    }

                                    arrSleep.add(sleepData);    //添加 睡眠报告 集合
                                }
                            }
                        }else {    //9点之后的为无效的睡眠数据）   用结束时间 -   20170409 00:08:50           //TODO 结束时间 在 第2天 的 9点之后
                            arrSleepSql.get(i).getStarttimes();
                            ReportSleepData sleepData = new ReportSleepData();   // 睡眠报告 的 bean 类
                            if(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("1")) {  //72
                                if (Integer.parseInt(sleepDB.getSleeptype()) == 2)  // 为0 深睡
                                    sleepData.setDeepSleep(true);
                                else if (Integer.parseInt(sleepDB.getSleeptype()) == 1)  // 为1 浅睡
                                    sleepData.setDeepSleep(false);
                            }else if(SharedPreUtil.readPre(PresentationActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("2") || SharedPreUtil.readPre(PresentationActivity.this,SharedPreUtil.USER,SharedPreUtil.WATCH).equals("3")
                                    || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")){ // 2：BLE 3：MTK
                                if (Integer.parseInt(sleepDB.getSleeptype()) == 1)  // 为0 深睡
                                    sleepData.setDeepSleep(false);
                                else if (Integer.parseInt(sleepDB.getSleeptype()) == 2)  // 为1 浅睡
                                    sleepData.setDeepSleep(true);
                            }

                            if(sleepDataStartTime >= endTime){   // 数据库的开始时间 比 当前睡眠的截止 还大  --- 当天无效睡眠数据
                                continue;
                            }else {   //TODO 开始时间在 在 第2天 的 9点之前 结束时间 在 第2天 的 9点之后
                                if(sqlDateValue == curDateriqi){  // 睡眠开始时间 的 日期是当前天的日期
                                    if(hour >= 21){  //TODO 开始时间 当天的21点 以后的睡眠数据
                                        sleepData.setStartTime(hour * 60 + fen - 21 * 60);
                                        long sleepOktime = (endTime - sleepDataStartTime)/60;  // 睡眠的有效时间 --- 分钟
                                        sleepData.setSleepTime(sleepOktime);   // 设置睡眠报告的 总睡眠时间   ---- 分钟

                                        if(i == arrSleepSql.size()-1){ // todo --- 最后一条睡眠数据 保存睡眠结束时间
                                            String  endTimeLast = arrSleepSql.get(i).getEndTime();    // 2017-11-03 08:03:00
                                            String endTimeLastOk = endTimeLast.substring(11,16);
                                            sleepData.setEndTimeStr(endTimeLastOk);      // arrSleepSql.get(i).getEndTime();
                                        }

                                        arrSleep.add(sleepData);    //添加 睡眠报告 集合
                                    }else {  // TODO --- 开始时间 是当天 21 点 之前 的时间
                                        // 开始时间在当前天的 21点之前 ，结束时间在第2天的 9点之后  721---0---2017-04-22 21:00:00---2017-04-23 09:01:00
                                        int bb = (int) (Long.parseLong(sleepDB.getSleepmillisecond()) / 1000 / 60);  //当天21点之前 睡眠的分钟数
                                        long tt1 =(startTime - sleepDataStartTime)/60 ; // 开始时间在当前天的 21点之前 ,有效时间为 startTime（有效的开始时间的时间戳） - sleepDataStartTime（数据库中睡眠的开始时间）= 无效的睡眠时间
                                        long tt2 = (sleepDataEndTime - endTime)/60; // 结束时间在第2天的 9点之后 , 无效的睡眠时间
                                        long sleepOktime = bb - tt1 - tt2;  // 睡眠的有效时间（分钟数）
                                        if(sleepOktime > 0){
                                            sleepData.setStartTime(0); // 开始时间为 21点
                                            sleepData.setSleepTime(sleepOktime);// 设置21点之后的睡眠时长

                                            arrSleep.add(sleepData);  // todo  --- add 20171220
                                        }
                                    }
                                }else if(sqlDateValue == nextDateriqi){  // 睡眠开始时间的日期 是后一天的 日期      睡眠 数据 ，取 9 点 之前的 睡眠数据
                                    if(hour < 9){  //后一天 9点之前的
                                        int bb = (int) (Long.parseLong(sleepDB.getSleepmillisecond()) / 1000 / 60);  //当天21点之前 睡眠的分钟数
                                        sleepData.setStartTime(hour * 60 + fen + 180);
                                        int sleepStime = hour * 60 + fen;
                                        int sleepokTime = 540 - sleepStime; // 睡眠的有效时间
                                        if(bb > sleepokTime){  // 是否需要 计算 9点之前的 睡眠时长
                                            sleepData.setSleepTime(sleepokTime);   // 设置睡眠报告的 总睡眠时间   ---- 分钟
                                        }else {
                                            sleepData.setSleepTime(bb);
                                        }

                                        if(i == arrSleepSql.size()-1){ // todo --- 最后一条睡眠数据 保存睡眠结束时间
                                            String  endTimeLast = arrSleepSql.get(i).getEndTime();    // 2017-11-03 08:03:00
                                            String endTimeLastOk = endTimeLast.substring(11,16);
                                            sleepData.setEndTimeStr(endTimeLastOk);      // arrSleepSql.get(i).getEndTime();
                                        }

                                        arrSleep.add(sleepData);    //添加 睡眠报告 集合
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (!SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MID).equals("")) {

        } else {    // 本地没有睡眠的数据
            Message msg = mHandler.obtainMessage();
            msg.what = CLEARSLEEP;
            mHandler.sendMessage(msg);
        }
        if(arrSleep != null && arrSleep.size() > 0){
            for(int i=0;i<arrSleep.size();i++){
                Log.e(TAG, "睡眠数组数据--" + i + "---" + arrSleep.get(i).getStartTime() + "###"+ (arrSleep.get(i).getSleepTime()) + "###"  + arrSleep.get(i).isDeepSleep());
            }
        }

        Message msg = mHandler.obtainMessage();
        msg.what = UPUERSLEEPINFO;     // TODO   --- 更新数据报告 睡眠报告的图表
        msg.obj = arrSleep;
        mHandler.sendMessage(msg);
    }

    /**
     * 查询血压值
     */

    private void judgmentBloodpressureDaoDB() {
        String strDate = getCurDate();
        final String choiceDate = arrangeDate(strDate);     // 整理日期数据

        if(db == null){
            db = DBHelper.getInstance(PresentationActivity.this);
        }

        Query query =null;
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getBloodpressureDao().queryBuilder().where(BloodpressureDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))).where(BloodpressureDao.Properties.Date.eq(choiceDate)).orderDesc(BloodpressureDao.Properties.Hour).build();
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getBloodpressureDao().queryBuilder().where(BloodpressureDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).where(BloodpressureDao.Properties.Date.eq(choiceDate)).orderDesc(BloodpressureDao.Properties.Hour).build();  // 根据日期
        }     // 999999999999999999999999999999
        List list = query.list();
        bloodpressure.clear();
        if (list != null && list.size() >= 1) {
            bloodpressure.addAll(list);
        }
        runOnUiThread(new Runnable() {     // 99999999999999999999999999999
            @Override
            public void run() {
                setMyAdpater();  // setMyAdpater
            }
        });   // todo --- 刷新listview的数据

        new Thread(new Runnable() {
            @Override
            public void run() {
                Query query2  = db.getBloodpressureDao().queryBuilder().where(BloodpressureDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))).where(BloodpressureDao.Properties.Date.eq(choiceDate)).orderAsc(BloodpressureDao.Properties.Hour).build(); //添加升序排列
                final List<Bloodpressure> listAsc = query2.list();

                /////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String mheartBinTime = "";
                List<Bloodpressure> listok = new ArrayList<>();
                if(null != listAsc && listAsc.size() >= 1){
                    for (int j = 0; j < listAsc.size(); j++) {
                        String HF = listAsc.get(j).getHour().substring(0,5);     // 时：分：秒    15:47:17
                        if(!HF.equals(mheartBinTime)){
                            mheartBinTime = HF;
                            if(listok.size() > 0){
                                boolean isExist = false;
                                for(Bloodpressure data:listok){
                                    String data1 = data.getHour().substring(0,5); // 时：分
                                    String data2 = data.getMinBlood();   // 小值
                                    String data3 = data.getHeightBlood(); //大值
                                    if(data1.equals(HF) && data2.equals(listAsc.get(j).getMinBlood()) && data3.equals(listAsc.get(j).getHeightBlood())){
                                        isExist = true;
                                        break;
                                    }
                                }
                                if(!isExist){
                                    listok.add(listAsc.get(j));
                                }
                            }else {
                                listok.add(listAsc.get(j));
                            }
                        }
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String lastminXueya = "";
                String lastmaxXueya = "";
                List<Bloodpressure> listok2 = new ArrayList<>();
                if(null != listok && listok.size() >= 1){  // todo --- 过滤掉连续相同的值（图表数据）
                    for (int j = 0; j < listok.size(); j++) {
                        String minXueya = listok.get(j).getMinBlood();
                        String maxXueya = listok.get(j).getHeightBlood();
                        if(!minXueya.equals(lastminXueya) || !maxXueya.equals(lastmaxXueya)){
                            lastminXueya = minXueya;
                            lastmaxXueya = maxXueya;
                            listok2.add(listok.get(j));
                        }
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////////

                if (listok2 != null && listok2.size() >= 1) {    //  99999999999999     listok
                    ArrayList<Bloodpressure> arrHear = new ArrayList<Bloodpressure>();
                    for (int j = 0; j < listok2.size(); j++) {
                        Bloodpressure hearDB = listok2.get(j);
                        arrHear.add(hearDB);
                    }

                    Newbloodpressure = arrHear;

                    Message msg = mHandler.obtainMessage();
                    msg.what = UPOxyBloodpressure_INFO;   //   9999999999999999999999999
//            msg.obj = arrHear;      9999999999
                    mHandler.sendMessage(msg);
                }else {
                    Message msg = mHandler.obtainMessage();
                    msg.what = CLEAR_Bloodpressure;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 查询血氧值
     */

    private void judgmentOxygenDB() {
        String strDate = getCurDate();
        final String choiceDate = arrangeDate(strDate);     // 整理日期数据

        if(db == null){
            db = DBHelper.getInstance(PresentationActivity.this);
        }


        Query query =null;
        if (SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
            query = db.getOxygenDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))).where(OxyDao.Properties.Date.eq(choiceDate)).orderDesc(OxyDao.Properties.Hour).build(); //添加降序排列
        } else {  //  不需要展示的设备的数据的mac地址
            query = db.getOxygenDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).where(OxyDao.Properties.Date.eq(choiceDate)).orderDesc(OxyDao.Properties.Hour).build();  // 根据日期 添加降序排列
        }
        List list = query.list();
        BloodpressureList.clear();
        if(list != null && list.size() > 0){
            BloodpressureList.addAll(list);
        }
        runOnUiThread(new Runnable() {     // 99999999999999999999999999999
            @Override
            public void run() {
                setMyAdpater();
            }
        });   // todo ---- listView 用到 BloodpressureList

        new Thread(new Runnable() {
            @Override
            public void run() {
                Query query2  = db.getOxygenDao().queryBuilder().where(OxyDao.Properties.Mac.eq(SharedPreUtil.readPre(PresentationActivity.this, SharedPreUtil.USER, SharedPreUtil.MAC))).where(OxyDao.Properties.Date.eq(choiceDate)).orderAsc(OxyDao.Properties.Hour).build(); //添加升序排列
                final List<Oxygen> listAsc = query2.list();      //TODO 升序
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String mheartBinTime = "";
                List<Oxygen> listok = new ArrayList<>();
                if(null != listAsc && listAsc.size() >= 1){
                    for (int j = 0; j < listAsc.size(); j++) {
                        String HF = listAsc.get(j).getHour().substring(0,5);     // 时：分：秒    15:47:17
                        if(!HF.equals(mheartBinTime)){
                            mheartBinTime = HF;
                            if(listok.size() > 0){
                                boolean isExist = false;
                                for(Oxygen data:listok){
                                    String data1 = data.getHour().substring(0,5);  // 时：分
                                    String data2 = data.getOxygen();      // 血氧值
                                    if(data1.equals(HF) && listAsc.get(j).getOxygen().equals(data2)){
                                        isExist = true;
                                        break;
                                    }
                                }
                                if(!isExist){
                                    listok.add(listAsc.get(j));
                                }
                            }else {
                                listok.add(listAsc.get(j));
                            }
                        }
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                String lastXueyang = "";
                List<Oxygen> listok2 = new ArrayList<>();
                if(null != listok && listok.size() >= 1){  // todo --- 过滤掉连续相同的值（图表数据）
                    for (int j = 0; j < listok.size(); j++) {
                        String mXueyang = listok.get(j).getOxygen();
                        if(!mXueyang.equals(lastXueyang)){
                            lastXueyang = mXueyang;
                            listok2.add(listok.get(j));
                        }
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                if (listok2 != null && listok2.size() >= 1) {  //listok 999999  listok2
                    ArrayList<Oxygen> arrHear = new ArrayList<Oxygen>();
                    for (int j = 0; j < listok2.size(); j++) {
                        Oxygen hearDB =  listok2.get(j);
                        arrHear.add(hearDB);
                    }

                    NewBloodpressureList = arrHear;

                    Message msg = mHandler.obtainMessage();
                    msg.what = UPOxygen_INFO;
//            msg.obj = arrHear;         //这里传送的值没有用到 todo ---- 心率报告图标也 用到 BloodpressureList
                    mHandler.sendMessage(msg);
                }else {
                    Message msg = mHandler.obtainMessage();
                    msg.what = CLEAR_Oxygen;
                    mHandler.sendMessage(msg);
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        }).start();
    }


    private synchronized void getHealthFrData(List<HearData> list){    //  private  List<ChartViewCoordinateData> getHealthFrData(List<HearData> list){
        //数据列表显示
//        ArrayList<ChartViewCoordinateData> heartList = new ArrayList<>();

        //数据图表显示
        ArrayList<ChartViewCoordinateData> arrHeart = new ArrayList<>();
        if (list != null && list.size() >= 1) {  // 有本地数据
            ChartViewCoordinateData heartData;
            String mheartBinTime = "";
            List<HearData> listok = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                if(!list.get(j).getBinTime().equals(mheartBinTime)){
                    mheartBinTime = list.get(j).getBinTime();
                    if(listok.size() > 0){
                        boolean isExist = false;
                        for(HearData data:listok){
                            if(data.getBinTime().equals(list.get(j).getBinTime())){
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            listok.add(list.get(j));
                        }
                    }else {
                        listok.add(list.get(j));
                    }
                }
            }
            String tempheartTime = "";
            for (int j = 0; j < listok.size(); j++) {     // todo ---- 当前时间点的所有数据
                HearData runDB = (HearData) listok.get(j);   //前一个心率数据
                List<HearData> mlist = new ArrayList<HearData>();   // 每一个for循环取一组相同开始时间的心率数据
                String bgTime = runDB.getBinTime();
                if(!bgTime.equals(tempheartTime)){ // bgTime.equals(tempheartTime)   ---- StringUtils.isEmpty(tempheartTime)
                    tempheartTime = bgTime;
                    mlist.add(runDB);
                    for(HearData data:listok){
                        if(!runDB.getBinTime().equals(data.getBinTime())){
                            String oneTime = runDB.getBinTime().substring(0,16); //相同开始时间 心率集合数据的第一条数据
                            String listDataTime = data.getBinTime().substring(0,16);
                            if(oneTime.equals(listDataTime)){  // 是同一分钟的心率数据
                                mlist.add(data);
                            }
                        }
                    }
                }

                //todo --- 遍历完成，得到相同分钟数的心率数据集合
                if(mlist.size() < 2){   // 同一分钟只有一条数据
                    heartData = new ChartViewCoordinateData();
                    heartData.x = Integer.parseInt(mlist.get(0).getBinTime().split(" ")[1].split(":")[0]);   // 心率时间值    // 10点
                    heartData.value = Integer.parseInt(mlist.get(0).getHeartbeat());// 心率值
                    heartData.Hour=mlist.get(0).getBinTime().substring(11,16);   // // 2017-08-16 10:57:14 ------    取到时分值
                    heartData.maxhata=Integer.valueOf(mlist.get(0).getHigt_hata());   // 最大   -----  getHeartbeat
                    heartData.manhata=Integer.valueOf(mlist.get(0).getLow_hata());    // 最小          ------ getHeartbeat
                    heartData.avghata=Integer.valueOf(mlist.get(0).getAvg_hata());    // 平均         -----  getHeartbeat
                    String time=mlist.get(0).getBinTime().substring(11,16);
                    Log.e("TIME",time+"********"+runDB.toString());
                    arrHeart.add(heartData);
                }else {   // 同一分钟有多条数据
                    heartData = new ChartViewCoordinateData();
                    heartData.x = Integer.parseInt(mlist.get(0).getBinTime().split(" ")[1].split(":")[0]);   // 心率时间值    // 10点
                    heartData.value = Integer.parseInt(mlist.get(0).getHeartbeat());// 心率值
                    heartData.Hour=mlist.get(0).getBinTime().substring(11,16);   // // 2017-08-16 10:57:14 ------    取到时分值
                    int mean_heart = 0;
                    int highest_heart = 0;
                    int minimum_heart = 11110;
                    for (int v = 0; v < mlist.size(); v++) {
                        highest_heart = Math.max(Integer.parseInt(mlist.get(v).getHeartbeat()), highest_heart);   // 最大
                        minimum_heart = Math.min(Integer.parseInt(mlist.get(v).getHeartbeat()), minimum_heart);   // 最小
                        mean_heart += Integer.parseInt(mlist.get(v).getHeartbeat());       // 平均
                    }
                    mean_heart = mean_heart / mlist.size();
                    heartData.maxhata = highest_heart;   // 最大   -----  getHeartbeat
                    heartData.manhata = minimum_heart;    // 最小          ------ getHeartbeat
                    heartData.avghata = mean_heart;    // 平均         -----  getHeartbeat
                    arrHeart.add(heartData);
                }
            }
        }
        String mheartHour = "";
        ArrayList<ChartViewCoordinateData> arrHeartOk = new ArrayList<>();
        for (int j = 0; j < arrHeart.size(); j++) {
            if(!arrHeart.get(j).getHour().equals(mheartHour)){
                mheartHour = arrHeart.get(j).getHour();
                if(arrHeartOk.size() > 0){
                    boolean isExist = false;
                    for(ChartViewCoordinateData data:arrHeartOk){
                        if(data.getHour().equals(arrHeart.get(j).getHour())){
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist){
                        arrHeartOk.add(arrHeart.get(j));
                    }
                }else {
                    arrHeartOk.add(arrHeart.get(j));
                }
            }
        }

        Message msg = mHandler.obtainMessage();
        msg.what = UPUERHEARTINFO;
        msg.obj = arrHeartOk;    // TODO ----- 更新心率数据  ---- 数据报告 心率报告 图表
        mHandler.sendMessage(msg);
    }


    private void judgmentHeartDB() {   // K3实时心率 数据量 大 ，且实时测量时，测量频率大
        if (db == null) {
            db = DBHelper.getInstance(PresentationActivity.this);
        }

        try {
                    ArrayList<ChartViewCoordinateData> arrHeart = new ArrayList<>();
                    mean_heart = 0;
                    String strDate = getCurDate();  // 获取当前时间
                    int times = 0;
                    Query query = null;
                    // todo ---- 现改为查询时按时间戳排序(加上设备MAC查询)
                    query = db.getHearDao().queryBuilder()
                            .where(HearDataDao.Properties.Date.eq(strDate)).orderDesc(HearDataDao.Properties.Times)    // orderAsc：升序     orderDesc ：降序
                            .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();  //TODO 降序
                    final List<HearData> list = query.list();    //TODO 降序
            listHeart.clear();
            if(list != null && list.size() > 0){
                listHeart.addAll(list);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setMyAdpater();
                }
            });

            Query query2 = db.getHearDao().queryBuilder()
                    .where(HearDataDao.Properties.Date.eq(strDate)).orderAsc(HearDataDao.Properties.Times)    // orderAsc：升序     orderDesc ：降序
                    .where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();  //TODO 升序
            final List<HearData> listAsc = query2.list();      //TODO 升序

            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    getHealthFrData(listAsc);
                }
            });

//            getHealthFrData(listAsc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class thbroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainService.ACTION_USERDATACHANGE.equals(action)) {
//                inithealthData();
//                setUphealthView();
//                initshow();
            }

            // todo ---0922  各实时数据更新  ---- 发送的广播 需要区分
            if (intent.getAction().equals(MainService.ACTION_SYNFINSH) || intent.getAction().equals(MainService.ACTION_CHANGE_WATCH)
                      || intent.getAction().equals("android.intent.action.DATE_CHANGED") ) {  //TODO -----  报告页面收到数据同步成功的广播了

                if(ISSYNWATCHINFO) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentRunDB();
                            judgmentSleepDB();
                        }/////
                    }).start();
                    if (BLOOD_OXYGEN) {
                        judgmentOxygenDB();//血氧// TODO  ----血氧界面？
                    }
                    if (BLOOD_PRESSURE) {
                        judgmentBloodpressureDaoDB();//更新血压// TODO  ----血压界面？
                    }

                    judgmentHeartDB();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            judgmentRunDB();
                            judgmentSleepDB();
                        }/////
                    }).start();

                    judgmentOxygenDB();//血氧
                    judgmentBloodpressureDaoDB();//更新血压
                    judgmentHeartDB();
                }
            }

            if(intent.getAction().equals(MainService.ACTION_SYNFINSH_SUCCESS)){  // TODO  ---- 实时计步的广播  add 20171211
                        judgmentRunDB();
            }

            if(intent.getAction().equals(MainService.ACTION_SYNARTHEART) && BTNotificationApplication.isSyncEnd){    // todo  --- 同步数据完成 if(BTNotificationApplication.isSyncEnd){
                        judgmentHeartDB();
            }

            if(intent.getAction().equals(MainService.ACTION_SYNARTBP) && BTNotificationApplication.isSyncEnd){    // todo  --- 同步数据完成 if(BTNotificationApplication.isSyncEnd){
//                ThreadPoolManager.getInstance().execute(new Runnable() {
//                    @Override
//                    public void run() {
                        judgmentBloodpressureDaoDB();//更新血压
//                    }
//                });
            }

            if(intent.getAction().equals(MainService.ACTION_SYNARTBO) && BTNotificationApplication.isSyncEnd){    // todo  --- 同步数据完成 if(BTNotificationApplication.isSyncEnd){
//                ThreadPoolManager.getInstance().execute(new Runnable() {
//                    @Override
//                    public void run() {
                        judgmentOxygenDB();//血氧
//                    }
//                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (vb != null) {
            unregisterReceiver(vb);
        }
        try {
            EventBus.getDefault().unregister(this);
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);


        } catch (Exception e) {

        }
    }

    private void showLoadingDialogNew(final String content) {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(PresentationActivity.this);
            myLoadingDialog.setWaitingTitle(content);
        }
        myLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
        }
        myLoadingDialog = null;
    }

    private int pageindex=0;

    /**
     * 记录第几个page跳转过来的   0 计步    1睡眠     其他
     * @param pageindex
     */
    public void setPageindex(int pageindex){
        this.pageindex=pageindex;
//        if(tv_more==null)
//            return;
//        tv_more.setClickable(true);
//        tv_more.setVisibility(View.INVISIBLE);
        ll_sport.setVisibility(View.GONE);
        ll_sleep_rate.setVisibility(View.GONE);
        ll_heart_rate.setVisibility(View.GONE);
        ll_oxy_rate.setVisibility(View.GONE);
        ll_xieya.setVisibility(View.GONE);
        listview_detail_sleep.setVisibility(View.GONE);
        switch (pageindex){
            case 0://计步
//                cb_navigation_sport.setText(getString(R.string.activity_report));
//                tv_more.setVisibility(View.VISIBLE);
//                tv_more.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ((MainActivity)PresentationActivity.this).toBackanalysisFragment();
//                    }
//                });
                ll_sport.setVisibility(View.VISIBLE);
                break;
            case 1://睡眠
//                cb_navigation_sport.setText(getString(R.string.sleep_report));
//                tv_more.setVisibility(View.VISIBLE);
//                tv_more.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ((MainActivity)PresentationActivity.this).toBackanalysis_SleepFragment();
//                    }
//                });
                ll_sleep_rate.setVisibility(View.VISIBLE);
                listview_detail_sleep.setVisibility(View.VISIBLE);
                break;
            case 2://心率
//                cb_navigation_sport.setText(getString(R.string.heart_rate_report));
             
                ll_heart_rate.setVisibility(View.VISIBLE);
                break;
            case 3://血压
//                cb_navigation_sport.setText(getString(R.string.Blood_pressure_Report));
          
                ll_xieya.setVisibility(View.VISIBLE);
                break;
            case 4://血氧
//                cb_navigation_sport.setText(getString(R.string.Blood_oxygen_Report));
           
                ll_oxy_rate.setVisibility(View.VISIBLE);
                break;
        }

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("zh")) {
            cb_navigation_sport.setTextSize(16);
        }else {
            cb_navigation_sport.setTextSize(10);
        }
        if(languageLx.contains("fr") || languageLx.contains("pt") || languageLx.contains("pl")){
            cb_navigation_sport.setLines(2);
            cb_navigation_sport.setSingleLine(false);
        }

//        setMyAdpater();
    }

    class MyListviewDetailAdapter extends BaseAdapter {

        int pageindex;

        public MyListviewDetailAdapter(int pageindex){
            this.pageindex=pageindex;
        }

        @Override
        public int getCount() {
            int size=0;
            try {
                if (pageindex == 0)
                    size=stepData_effectives.size();
                else if (pageindex == 2)
                    size=listHeart.size();
                else if (pageindex ==3)
                    size=bloodpressure.size();
                else if(pageindex==4)
                    size=BloodpressureList.size();
            }catch (Exception e){}
            return size;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(pageindex==0) {//计步
                if(convertView==null)
                    convertView = PresentationActivity.this.getLayoutInflater().inflate(R.layout.report_datareport_view_listitem_sport, parent, false);
                if(stepData_effectives != null && stepData_effectives.size() > 0) {
                    ChartViewCoordinateData cd = stepData_effectives.get(position);
                    TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    tv_time.setText(cd.getX() < 10 ? "0" + cd.getX() + ":00" : cd.getX() + ":00");
                    TextView tv_step = (TextView) convertView.findViewById(R.id.tv_step);
                    tv_step.setText(cd.getValue() + "");

//                int userWeightI = 0;
//                String userWeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WEIGHT);
//                if (StringUtils.isEmpty(userWeight)) {
//                    userWeightI = 60;
//                } else {
//                    userWeightI = Integer.valueOf(userWeight);
//                }


                    String calorie = Utils.setformat(2, cd.getCalorie() + "");  // 卡路里
                    String distance = Utils.setformat(2, cd.getDistance() + "");     // 运动距离
                    TextView tv_calorie = (TextView) convertView.findViewById(R.id.tv_calorie);
                    tv_calorie.setText(calorie + "");
                    TextView tv_km = (TextView) convertView.findViewById(R.id.tv_km);
                    tv_km.setText(distance);
                    TextView tv_km_unit = (TextView) convertView.findViewById(R.id.tv_km_unit);
                    tv_km_unit.setText(R.string.kilometer);
                    TextView tv_calorie_unit = (TextView) convertView.findViewById(R.id.tv_calorie_unit);
                    if (Utils.getLanguage().contains("ja")) {
                        tv_calorie_unit.setTextSize(6.5f);
                    }
                    tv_calorie_unit.setText(R.string.everyday_calorie);
                    if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                        //如果是英制
                        tv_calorie.setText(Utils.setformat(2, String.format(Locale.ENGLISH, "%.2f", Utils.getUnit_kal(Double.parseDouble(calorie)))));
                        tv_km.setText(Utils.setformat(2, String.format(Locale.ENGLISH, "%.2f", Utils.getUnit_km(Float.parseFloat(distance)))));
                        tv_km_unit.setText(R.string.unit_mi);
                        tv_calorie_unit.setText(R.string.unit_kj);
                    }
                }
            }else if(pageindex==2){//心率
                if(convertView==null)
                    convertView = PresentationActivity.this.getLayoutInflater().inflate(R.layout.report_datareport_view_listitem_heart, parent, false);

//  ChartViewCoordinateData mChartViewCoordinateData=listHeart.get(position);    //
                if(listHeart != null && listHeart.size() > 0) {
                    HearData mChartViewCoordinateData = listHeart.get(position);



              /*  ChartViewCoordinateData mChartViewCoordinateData;
                try {
                    mChartViewCoordinateData = listHeart.get(position);
                }catch (Exception e){//java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
                    // listHeart 数据是在子线程被修改了 导致运行到这里的瞬间又被修改了size数量 重新通知listview刷新
                    mMyListviewDetailAdapter2.notifyDataSetChanged();
                    return convertView;
                }*/


                    TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    tv_time.setText(mChartViewCoordinateData.getHour());
                    TextView tv_heart = (TextView) convertView.findViewById(R.id.tv_heart);
//                tv_heart.setText(mChartViewCoordinateData.getAvghata()+"");    9999999999999999
                    tv_heart.setText(mChartViewCoordinateData.getHeartbeat() + "");
                    TextView tv_km = (TextView) convertView.findViewById(R.id.tv_km);

                String languageLx  = Utils.getLanguage();
                if (!languageLx.equals("zh")) {
                    tv_km.setTextSize(8);
                }
//                tv_km.setText(getDescribeHeart(mChartViewCoordinateData.getAvghata()));
                    tv_km.setText(getDescribeHeart(Integer.valueOf(mChartViewCoordinateData.getHeartbeat())));
                }
            }else if(pageindex==3){//血压
                if(convertView==null)
                    convertView = PresentationActivity.this.getLayoutInflater().inflate(R.layout.report_datareport_view_listitem_bloodpressure, parent, false);
                if(bloodpressure != null && bloodpressure.size() > 0) {
                    Bloodpressure mBloodpressure = bloodpressure.get(position);
                    TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    tv_time.setText(mBloodpressure.getHour());
                    TextView tv_aa = (TextView) convertView.findViewById(R.id.tv_aa);
                    tv_aa.setText(mBloodpressure.getHeightBlood());
                    TextView tv_bb = (TextView) convertView.findViewById(R.id.tv_bb);
                    tv_bb.setText(mBloodpressure.getMinBlood());
                    TextView tv_km = (TextView) convertView.findViewById(R.id.tv_km);

                    String languageLx = Utils.getLanguage();
                    if (!languageLx.equals("zh")) {
                        tv_km.setTextSize(10);
                    }

                    tv_km.setText(getDescribeBloodpressure(mBloodpressure));
                }
            }else if(pageindex==4){//血氧
                if(convertView==null)
                    convertView = PresentationActivity.this.getLayoutInflater().inflate(R.layout.report_datareport_view_listitem_oxygen, parent, false);
                if(BloodpressureList != null && BloodpressureList.size() > 0) {
                    Oxygen mOxygen = BloodpressureList.get(position);
                    TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    tv_time.setText(mOxygen.getHour());
                    TextView tv_oxy = (TextView) convertView.findViewById(R.id.tv_oxy);
                    tv_oxy.setText(mOxygen.getOxygen());
                }
            }
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {//移动到ui线程
                    MyListviewDetailAdapter.super.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * 获得血压描述
     * @param mBloodpressure
     * @return
     */
    private String getDescribeBloodpressure(Bloodpressure mBloodpressure) {
        String dss[]={"LBp","Bp","HBp-1","HBp-2","HBp-3"};
        String s="";
        if(Integer.parseInt(mBloodpressure.getHeightBlood())<90||Integer.parseInt(mBloodpressure.getMinBlood())<60)
            s=dss[0];
        else if(Integer.parseInt(mBloodpressure.getHeightBlood())<140||Integer.parseInt(mBloodpressure.getMinBlood())<90)
            s=dss[1];
        else if(Integer.parseInt(mBloodpressure.getHeightBlood())<159||Integer.parseInt(mBloodpressure.getMinBlood())<99)
            s=dss[2];
        else if(Integer.parseInt(mBloodpressure.getHeightBlood())<179||Integer.parseInt(mBloodpressure.getMinBlood())<109)
            s=dss[3];
        else
            s=dss[4];
        return s;
    }

    /**
     * 获得心率描述
     * @param avghata
     * @return
     */
    private String getDescribeHeart(int avghata) {
        String dss[]={getString(R.string.resting),getString(R.string.warm_up),getString(R.string.aerobic),
                getString(R.string.heart_lung),getString(R.string.anaerobic),getString(R.string.limit)};
        String s="";
        if(avghata<93)
            s=dss[0];          //静息
        else if(avghata<139)
            s=dss[1];     //热身
        else if(avghata<152)
            s=dss[2];     //有氧
        else if(avghata<164)
            s=dss[3];     //心肺
        else if(avghata<176)
            s=dss[4];   // 无氧
        else
            s=dss[5];    //极限
        return s;
    }

    /**
     * 设置睡眠显示的文本
     * @param flag hm  还是:
     */
    public void setSleepText(TextView tv,String h,String m,boolean flag){
        Spanned sp = null;
        //补充两位
        if (h.length() < 2) h = "0" + h;
        if (m.length() < 2) m = "0" + m;
        if(flag) {
            sp = Html.fromHtml("<font color='#6852ff'>" + h + "</font> h "
                    + "<font color='#6852ff'>"+ m +"</font> m ");
        }else{
            sp = Html.fromHtml("<font color='#6852ff'>" + h + "</font> : "
                    + "<font color='#6852ff'>" + m + "</font>");
        }
        /*if(Integer.parseInt(h)==0&&Integer.parseInt(m)==0)
            sp = Html.fromHtml("<font color='#6852ff'>-</font>");*/
        tv.setText(sp);

    }

    /**
     * 显示数据有bug，切换日期先清理之前的
     */
    private void clearABCD(){
        if(stepData_effectives!=null && stepData_effectives.size() > 0){
            stepData_effectives.clear();
            setMyAdpater();
        }
        if(heart!=null && heart.size() > 0 ){
            heart.clear();
            setMyAdpater();
        }

        if(listHeart!=null && listHeart.size() > 0){
            listHeart.clear();
            setMyAdpater();
        }

        if(bloodpressure!=null && bloodpressure.size() > 0){
            bloodpressure.clear();
            setMyAdpater();
        }

        if(BloodpressureList!=null && BloodpressureList.size() > 0){
            BloodpressureList.clear();
            setMyAdpater();
        }

        if(arrSleepDatas!=null && arrSleepDatas.size() > 0){
            arrSleepDatas.clear();
            setMyAdpater();
        }

//        setMyAdpater();

       /* if(stepData_effective!=null)
            stepData_effective.clear();
        if(heart!=null)
            heart.clear();
        if(listHeart!=null)
            listHeart.clear();
        if(bloodpressure!=null)
            bloodpressure.clear();
        if(BloodpressureList!=null)
            BloodpressureList.clear();
        if(arrSleepDatas!=null)
            arrSleepDatas.clear();
//        setMyAdpater();*/
    }


    private void setMyAdpater() {
        listview_detail_step.setVisibility(View.GONE);
        listview_detail_heart.setVisibility(View.GONE);
        listview_detail_bloodpressure.setVisibility(View.GONE);
        listview_detail_oxygen.setVisibility(View.GONE);
        if(pageindex==0) {
            if(mMyListviewDetailAdapter0==null) {
                mMyListviewDetailAdapter0 = new MyListviewDetailAdapter(0);
                listview_detail_step.setAdapter(mMyListviewDetailAdapter0);
            }
            mMyListviewDetailAdapter0.notifyDataSetChanged();
            listview_detail_step.setVisibility(View.VISIBLE);
            if(mMyListviewDetailAdapter0.getCount()==0){
                tv_nodata1.setVisibility(View.VISIBLE);
            }else{
                tv_nodata1.setVisibility(View.INVISIBLE);
            }
        }else if(pageindex==1) {
            if(arrSleepDatas==null||arrSleepDatas.size()==0){
                tv_nodata2.setVisibility(View.VISIBLE);
            }else{
                tv_nodata2.setVisibility(View.INVISIBLE);
            }
        }else if(pageindex==2) {
            if(mMyListviewDetailAdapter2==null) {
                mMyListviewDetailAdapter2 = new MyListviewDetailAdapter(2);
                listview_detail_heart.setAdapter(mMyListviewDetailAdapter2);
            }
            mMyListviewDetailAdapter2.notifyDataSetChanged();
            listview_detail_heart.setVisibility(View.VISIBLE);
            if(mMyListviewDetailAdapter2.getCount()==0){
                tv_nodata3.setVisibility(View.VISIBLE);
            }else{
                tv_nodata3.setVisibility(View.INVISIBLE);
            }
        }else if(pageindex==3) {
            if(mMyListviewDetailAdapter3==null) {
                mMyListviewDetailAdapter3 = new MyListviewDetailAdapter(3);
                listview_detail_bloodpressure.setAdapter(mMyListviewDetailAdapter3);
            }
            mMyListviewDetailAdapter3.notifyDataSetChanged();
            listview_detail_bloodpressure.setVisibility(View.VISIBLE);
            if(bloodpressure==null||bloodpressure.size()==0){
                tv_nodata4.setVisibility(View.VISIBLE);
            }else{
                tv_nodata4.setVisibility(View.GONE);
            }
        }else if(pageindex==4) {
            if(mMyListviewDetailAdapter4==null) {
                mMyListviewDetailAdapter4 = new MyListviewDetailAdapter(4);
                listview_detail_oxygen.setAdapter(mMyListviewDetailAdapter4);
            }
            mMyListviewDetailAdapter4.notifyDataSetChanged();
            listview_detail_oxygen.setVisibility(View.VISIBLE);
            if(BloodpressureList==null||BloodpressureList.size()==0){
                tv_nodata5.setVisibility(View.VISIBLE);
            }else{
                tv_nodata5.setVisibility(View.INVISIBLE);
            }
        }
    }
}
