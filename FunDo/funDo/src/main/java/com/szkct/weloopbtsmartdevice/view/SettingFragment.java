package com.szkct.weloopbtsmartdevice.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mtk.app.applist.FileUtils;
import com.szkct.adapter.ViewPagerAdapter;
import com.szkct.map.utils.CircleImageView;
import com.szkct.weloopbtsmartdevice.activity.NewLoginPhoneActivity;
import com.szkct.weloopbtsmartdevice.data.greendao.RunData;
import com.szkct.weloopbtsmartdevice.data.greendao.UserInfo;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.RunDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.runshowdata;
import com.szkct.weloopbtsmartdevice.login.Gdata;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.main.MyDataActivity;
import com.szkct.weloopbtsmartdevice.main.SettingActivity;
import com.szkct.weloopbtsmartdevice.util.BitmapTools;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.ImageCacheUtil;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.MyLoadingDialog;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.Query;

import static com.kct.fundo.btnotification.R.id.tv_distancestring;
import static com.kct.fundo.btnotification.R.id.tv_kalstring;

@SuppressWarnings("ResourceType")
public class SettingFragment extends Fragment implements OnClickListener {
    private static final String TAG = "SettingFragment";

    private LinearLayout mRelativeSetting;
    TextView my_step_tv, my_distance_tv, my_kal_tv, my_name_tv,not_login,
            tv_step_percent, tv_distance_percent, tv_kal_percent,
            tv_my_viewpager_title, my_distance_tv_up, my_kal_tv_up;
    ImageView iv_step, iv_distance, iv_kal;
    CircleImageView my_touxian;
    Button my_qiandao;
    private View settingView, v_vp, v_vp2, v_vp3;

    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");

    private String todayString, yesterdayString;
    private DBHelper db = null;

    TextView vp1_step_tv, vp1_step_time, vp1_distance_tv, vp1_distance_time,
            vp1_kal_tv, vp1_kal_time;

    TextView vp2_step_tv, vp2_step_daysize, vp2_distance_tv,
            vp2_distance_daysize, vp2_kal_tv, vp2_kal_daysize;

    TextView vp3_step_tv, vp3_step_daysize, vp3_distance_tv,
            vp3_distance_daysize, vp3_kal_tv, vp3_kal_daysize;
    TextView vp1_tv_distancestring, vp1_tv_kalstring, vp2_tv_distancestring, vp2_tv_kalstring, vp3_tv_distancestring, vp3_tv_kalstring; //距离单位，卡路里单位
    int today_step, yesterday_step;
    float today_distance, today_kal, yesterday_distance, yesterday_kal;

    String mindateString;
    Bluttoothbroadcast vb = null;
    List<View> views;
    String[] viewpagertitle = new String[3];
    // 底部导航的小点
    private ImageView[] guideDots;
    // 记录当前选中的图片
    private int currentIndex;
    private UserInfo userInfo;
    public BitmapTools bitmapTools;

    private MyLoadingDialog myLoadingDialog;
    private String userName = "";
    private String userSex;
    private String userHeight;
    private String userWeight;
    private String userBirth;
    private String userEmail;
    private String userExperience;
    private String userFace = "";
    private String userFacePath = "";
    private Toast toast = null;
    private String nickName;

    public static SettingFragment newInstance(String title) {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    runshowdata sRunshowdata = (runshowdata) msg.obj;
                    if (null != sRunshowdata) {
                        setvpdata(sRunshowdata);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if ("update_unit".equals(event.getMessage())) {
            initTodayData();
            setv_vp1data();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO
        settingView = inflater.inflate(R.layout.fragment_my_main, null);
        EventBus.getDefault().register(this);
        viewpagertitle[0] = getString(R.string.best_me);  // 最佳的我
        viewpagertitle[1] = getString(R.string.average_me);  // 平均的我
        viewpagertitle[2] = getString(R.string.week_me);     // 上周的我
        inittext();
        initContorl();
        initnamehead();
        initTodayData();  // 初始化今天的数据
        setv_vp1data();
        registerBroad();
        setUserName();
        return settingView;

    }

    private void setUserName() {
        if (Gdata.getMid() != Gdata.NOT_LOGIN) {  
            String photoName = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.FACE);
            String path = FileUtils.SDPATH + photoName;
            Log.e("MyDataActivity ", " 显示的图片路径：" + path);
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null){
                    my_touxian.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));  // todo --- 设置头像
                }else {
                    if (SharedPreUtil.readPre(getContext(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("1")) {
                    my_touxian.setImageResource(R.drawable.head_women);
//                        my_touxian.setImageResource(R.drawable.head_men);
                    } else {
                    my_touxian.setImageResource(R.drawable.head_men);
//                        my_touxian.setImageResource(R.drawable.head_women);
                    }
                }
//                    my_touxian.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));  // todo --- 设置头像
                Log.e("MyDataActivity ", " 显示本地图片");
            } else {
                if (SharedPreUtil.readPre(getContext(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("1")) {
                    my_touxian.setImageResource(R.drawable.head_women);
                } else {
                    my_touxian.setImageResource(R.drawable.head_men);
                }
            }
            not_login.setVisibility(View.INVISIBLE);
            String userName = Gdata.getPersonData().getUsername();
            if (TextUtils.isEmpty(userName)) {
                my_name_tv.setText(getString(R.string.not_set_info));
            } else {
                my_name_tv.setText(userName);
            }
        } else {
            not_login.setVisibility(View.VISIBLE);
            my_name_tv.setText(getString(R.string.not_set_info));
            if (SharedPreUtil.readPre(getContext(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("1")) {
                my_touxian.setImageResource(R.drawable.head_women);
            } else {
                my_touxian.setImageResource(R.drawable.head_men);
            }
        }
    }

    // 注册广播的方法
    private void registerBroad() {
        if (null == vb) {
            vb = new Bluttoothbroadcast();
        }
        IntentFilter viewFilter = new IntentFilter();
        viewFilter.addAction(MainService.ACTION_SYNFINSH); //我的 页面 注册 手表数据同步 成功的 广播
        viewFilter.addAction(MainService.ACTION_MACCHANGE);
        viewFilter.addAction(MainService.ACTION_USERDATACHANGE);
        viewFilter.addAction(MainService.ACTION_MYINFO_CHANGE);

        viewFilter.addAction(MainService.ACTION_CHANGE_WATCH);

        viewFilter.addAction(MainService.ACTION_SYNFINSH_SUCCESS);

        getActivity().registerReceiver(vb, viewFilter);

    }

    protected void setvpdata(runshowdata sRunshowdata) {
        // TODO Auto-generated method stub

        int meBestStep = sRunshowdata.getBest_step();
        if (meBestStep > 200000) {
            meBestStep = 52007;

            String distance = String.format(Locale.ENGLISH, "%.2f", (meBestStep * 0.7) / 1000.0);     // 运动距离
            float bestDistance = Float.valueOf(distance);

            int userWeightI = 0;
            String userWeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WEIGHT);
            if (StringUtils.isEmpty(userWeight)) {
                userWeightI = 60;
            } else {
                userWeightI = Integer.valueOf(userWeight);
            }
            float bestCalorie = Float.valueOf(String.format(Locale.ENGLISH, "%1$.2f", (float) (userWeightI) * (float) ((meBestStep * 0.7) / 1000.0) * 1.036));  // 卡路里

            if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                bestDistance = (float) Utils.getUnit_km(bestDistance);
                bestCalorie = (float) Utils.getUnit_kal(bestCalorie);
                sRunshowdata.setAverage_kal((float) Utils.getUnit_kal(sRunshowdata.getAverage_kal()));
                sRunshowdata.setAverage_dis((float) Utils.getUnit_km(sRunshowdata.getAverage_dis()));
                sRunshowdata.setWeek_kal((float) Utils.getUnit_kal(sRunshowdata.getWeek_kal()));
                sRunshowdata.setWeek_dis((float) Utils.getUnit_km(sRunshowdata.getWeek_dis()));
                vp1_tv_kalstring.setText(getActivity().getString(R.string.unit_kj));
                vp1_tv_distancestring.setText(getActivity().getString(R.string.unit_mi));
                vp2_tv_kalstring.setText(getActivity().getString(R.string.unit_kj));
                vp2_tv_distancestring.setText(getActivity().getString(R.string.unit_mi));
                vp3_tv_kalstring.setText(getActivity().getString(R.string.unit_kj));
                vp3_tv_distancestring.setText(getActivity().getString(R.string.unit_mi));
            } else {
                vp1_tv_kalstring.setText(getActivity().getString(R.string.everyday_calorie));
                vp1_tv_distancestring.setText(getActivity().getString(R.string.kilometer));
                vp2_tv_kalstring.setText(getActivity().getString(R.string.everyday_calorie));
                vp2_tv_distancestring.setText(getActivity().getString(R.string.kilometer));
                vp3_tv_kalstring.setText(getActivity().getString(R.string.everyday_calorie));
                vp3_tv_distancestring.setText(getActivity().getString(R.string.kilometer));
            }

            vp1_step_tv.setText(Utils.setformat(0, meBestStep));  // 最佳的我 的 步数 赋值    	valueString=Utils.setformat(1,d+"")+ "h";
            vp1_step_time.setText(Utils.date2De2(sRunshowdata.getBest_step_time()));     // Utils.setformat(1, )
            vp1_distance_tv.setText(Utils.setformat(1, bestDistance));
            vp1_distance_time.setText(Utils.date2De2(sRunshowdata.getBest_dis_time()));
            vp1_kal_tv.setText(Utils.setformat(1, bestCalorie));
            vp1_kal_time.setText(Utils.date2De2(sRunshowdata.getBest_kal_time()));

        } else {

            if (!SharedPreUtil.YES.equals(SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
                sRunshowdata.setBest_kal((float) Utils.getUnit_kal(sRunshowdata.getBest_kal()));
                sRunshowdata.setBest_dis((float) Utils.getUnit_km(sRunshowdata.getBest_dis()));
                sRunshowdata.setAverage_kal((float) Utils.getUnit_kal(sRunshowdata.getAverage_kal()));
                sRunshowdata.setAverage_dis((float) Utils.getUnit_km(sRunshowdata.getAverage_dis()));
                sRunshowdata.setWeek_kal((float) Utils.getUnit_kal(sRunshowdata.getWeek_kal()));
                sRunshowdata.setWeek_dis((float) Utils.getUnit_km(sRunshowdata.getWeek_dis()));
                vp1_tv_kalstring.setText(getActivity().getString(R.string.unit_kj));
                vp1_tv_distancestring.setText(getActivity().getString(R.string.unit_mi));
                vp2_tv_kalstring.setText(getActivity().getString(R.string.unit_kj));
                vp2_tv_distancestring.setText(getActivity().getString(R.string.unit_mi));
                vp3_tv_kalstring.setText(getActivity().getString(R.string.unit_kj));
                vp3_tv_distancestring.setText(getActivity().getString(R.string.unit_mi));
            } else {
                vp1_tv_kalstring.setText(getActivity().getString(R.string.everyday_calorie));
                vp1_tv_distancestring.setText(getActivity().getString(R.string.kilometer));
                vp2_tv_kalstring.setText(getActivity().getString(R.string.everyday_calorie));
                vp2_tv_distancestring.setText(getActivity().getString(R.string.kilometer));
                vp3_tv_kalstring.setText(getActivity().getString(R.string.everyday_calorie));
                vp3_tv_distancestring.setText(getActivity().getString(R.string.kilometer));
            }

            vp1_step_tv.setText(Utils.setformat(0, sRunshowdata.getBest_step()));  // 最佳的我 的 步数 赋值    	valueString=Utils.setformat(1,d+"")+ "h";
            vp1_step_time.setText(Utils.date2De2(sRunshowdata.getBest_step_time()));     // Utils.setformat(1, )
            vp1_distance_tv.setText(Utils.setformat(1, sRunshowdata.getBest_dis()));
            vp1_distance_time.setText(Utils.date2De2(sRunshowdata.getBest_dis_time()));
            vp1_kal_tv.setText(Utils.setformat(1, sRunshowdata.getBest_kal()));
            vp1_kal_time.setText(Utils.date2De2(sRunshowdata.getBest_kal_time()));
        }


        vp2_step_tv.setText(Utils.setformat(0, sRunshowdata.getAverage_step()));
        //	vp2_step_daysize.setText(sRunshowdata.getSize());

        vp2_distance_tv.setText(Utils.setformat(1, sRunshowdata.getAverage_dis()));
        //	vp2_distance_daysize.setText(sRunshowdata.getSize());

        vp2_kal_tv.setText(Utils.setformat(1, sRunshowdata.getAverage_kal()));
        //	vp2_kal_daysize.setText(sRunshowdata.getSize());

        vp3_step_tv.setText(Utils.setformat(0, sRunshowdata.getWeek_step()));
        //vp3_step_daysize.setText(getString(R.string.last_seven_days));

        vp3_distance_tv.setText(Utils.setformat(1, sRunshowdata.getWeek_dis()));
        //	vp3_distance_daysize.setText(getString(R.string.last_seven_days));

        vp3_kal_tv.setText(Utils.setformat(1, sRunshowdata.getWeek_kal()));
        //	vp3_kal_daysize.setText(getString(R.string.last_seven_days));

    }

    /**
     * 初始化控件
     */
    private void initContorl() {
        // TODO Auto-generated method stub

        settingView.findViewById(R.id.ib_setting).setOnClickListener(this);
        LinearLayout ll = (LinearLayout) settingView.findViewById(R.id.textl);
        android.view.ViewGroup.LayoutParams lp = ll.getLayoutParams();

        lp.height = Utils.getScreenWidth(settingView.getContext()) * 1 / 3;
        views = new ArrayList<View>();
        v_vp = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_my_viewpagerview, null);
        vp1_step_tv = (TextView) v_vp.findViewById(R.id.tv_step); // 最佳的我 的步数
        vp1_step_time = (TextView) v_vp.findViewById(R.id.tv_step_time);   // 最佳的我 的时间
        vp1_distance_tv = (TextView) v_vp.findViewById(R.id.tv_distance);
        vp1_distance_time = (TextView) v_vp.findViewById(R.id.tv_distance_time);
        vp1_kal_tv = (TextView) v_vp.findViewById(R.id.tv_kal);
        vp1_kal_time = (TextView) v_vp.findViewById(R.id.tv_kal_time);

        v_vp2 = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_viewpagerview, null);

        vp2_step_tv = (TextView) v_vp2.findViewById(R.id.tv_step);
        vp2_step_daysize = (TextView) v_vp2.findViewById(R.id.tv_step_time);
        vp2_distance_tv = (TextView) v_vp2.findViewById(R.id.tv_distance);
        vp2_distance_daysize = (TextView) v_vp2.findViewById(R.id.tv_distance_time);
        vp2_kal_tv = (TextView) v_vp2.findViewById(R.id.tv_kal);
        vp2_kal_daysize = (TextView) v_vp2.findViewById(R.id.tv_kal_time);

        v_vp3 = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_viewpagerview, null);

        vp3_step_tv = (TextView) v_vp3.findViewById(R.id.tv_step);
        vp3_step_daysize = (TextView) v_vp3.findViewById(R.id.tv_step_time);
        vp3_distance_tv = (TextView) v_vp3.findViewById(R.id.tv_distance);
        vp3_distance_daysize = (TextView) v_vp3.findViewById(R.id.tv_distance_time);
        vp3_kal_tv = (TextView) v_vp3.findViewById(R.id.tv_kal);
        vp3_kal_daysize = (TextView) v_vp3.findViewById(R.id.tv_kal_time);

        vp1_tv_distancestring = (TextView) v_vp.findViewById(tv_distancestring);
        vp1_tv_kalstring = (TextView) v_vp.findViewById(tv_kalstring);

        vp2_tv_distancestring = (TextView) v_vp2.findViewById(tv_distancestring);
        vp2_tv_kalstring = (TextView) v_vp2.findViewById(tv_kalstring);

        vp3_tv_distancestring = (TextView) v_vp3.findViewById(tv_distancestring);
        vp3_tv_kalstring = (TextView) v_vp3.findViewById(tv_kalstring);

        ViewPager vp = (ViewPager) settingView.findViewById(R.id.test_vp);
        views.add(v_vp); // 最佳的我
        views.add(v_vp2);  // 平均的我
        views.add(v_vp3);  // 上周的我
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                setCurrentDot(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        initDot();
    }

    // 初始化导航小点
    private void initDot() {
        // 找到放置小点的布局
        LinearLayout layout = (LinearLayout) settingView.findViewById(R.id.my_dots);
        // 初始化小点数组
        guideDots = new ImageView[views.size()];
        // 循环取得小点图片，让每个小点都处于正常状态
        for (int i = 0; i < views.size(); i++) {
            guideDots[i] = (ImageView) layout.getChildAt(i);
            guideDots[i].setSelected(false);
        }
        // 初始化第一个小点为选中状态
        currentIndex = 0;
        tv_my_viewpager_title.setText(viewpagertitle[currentIndex]);  // 最佳的我 ViewPager 顶部标题
        guideDots[currentIndex].setSelected(true);
    }

    // 页面更换时，更新小点状态
    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1 || currentIndex == position) {
            return;
        }
        tv_my_viewpager_title.setText(viewpagertitle[position]);
        guideDots[position].setSelected(true);
        guideDots[currentIndex].setSelected(false);
        currentIndex = position;
    }

    private void setv_vp1data() {    // 设置滑动的ViewPager的数据
        new Thread() {
            @Override
            public void run() {
                super.run();
                dealCommand();
            }
        }.start();
    }

    private void dealCommand() {     //从数据库获得viewPager的数据显示

        String bluetoothAddress = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.MAC);
        String lastTime = SharedPreUtil.readPre(getActivity(), SharedPreUtil.DATA, bluetoothAddress + SharedPreUtil.ALL_STEP_TIME);  // 所有的计步时间
        int allStepNum = 0;    //全部总步数
        int weekStepNum = 0;   //上周总步数
        int maxStepNum = 0;    //最佳步数

        Float allDistanceNum = (float) 0;   //全部总距离
        Float weekDistanceNum = (float) 0;   //上周总距离
        Float maxDistanceNum = (float) 0;   //最佳距离

        Float allKalNum = (float) 0;     //全部总卡路里
        Float weekKalNum = (float) 0;    //上周总卡路里
        Float maxKalNum = (float) 0;     //最佳卡路里

        String maxDayTime = "";      //最佳步数的时间

        String watch = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        List<RunData> list = jumpRunData();

        for (int i = 0; i < list.size(); i++) {
            int step = Integer.parseInt(list.get(i).getStep());
            float calorie = Float.parseFloat(list.get(i).getCalorie());
            float distance = Float.parseFloat(list.get(i).getDistance());
            String date = list.get(i).getDate();
            allStepNum += step;
            allKalNum += calorie;
            allDistanceNum += distance;
            if (maxStepNum <= step) {
                maxStepNum = step;
                maxDistanceNum = distance;
                maxKalNum = calorie;
                maxDayTime = date;
            }
        }

        Calendar todaycld = Calendar.getInstance();
        String week_dayString;
        ArrayList<RunData> weekRunData;
        int s = 0 - todaycld.get(Calendar.DAY_OF_WEEK);
        todaycld.add(Calendar.DATE, s);
        for (int i = 0; i < 7; i++) {
            week_dayString = getDateFormat.format(todaycld.getTime());
            weekRunData = judgmentRunDB(week_dayString);
            for (int j = 0; j < weekRunData.size(); j++) {
                if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {
                    weekStepNum += Utils.toint(weekRunData.get(j).getStep());
                    weekDistanceNum += Float.parseFloat(weekRunData.get(j).getDistance());
                    weekKalNum += Float.parseFloat(weekRunData.get(j).getCalorie());
                } else {
                    weekStepNum += Utils.toint(weekRunData.get(j).getDayStep());
                    weekDistanceNum += Float.parseFloat(weekRunData.get(j).getDayDistance());
                    weekKalNum += Float.parseFloat(weekRunData.get(j).getDayCalorie());
                }
            }
            todaycld.add(Calendar.DATE, -1);
        }
        runshowdata rsd = null;
        Message msg = mHandler.obtainMessage();
        if (list.size() == 0) {
            rsd = new runshowdata(maxStepNum, maxDayTime, maxDistanceNum, maxDayTime, maxKalNum, maxDayTime,
                    allStepNum, allDistanceNum, allKalNum, weekStepNum, weekDistanceNum, weekKalNum, list.size());
        } else {
            int hasDataDayNum = 0;
            if (!SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")) {// H872等
                //hasDataDayNum = list.size();
                for (RunData data : list) {   // H872待处理  + BLE
                    if (Integer.valueOf(data.getStep()) > 0) { // 大于才有步数，为有效天数
                        hasDataDayNum++;
                    }
                }
            } else { // MTK
                for (RunData data : list) {
                  /*  if(todayString.equals(data.getDate())){  // 2017-06-12 (判断计步数据的日期是否是当天的日期)    2017-06-12
                        if(!StringUtils.isEmpty(data.getStep()) && Integer.valueOf(data.getStep()) > 0){ // 为当天的日期，取step 字段的 值大于才有步数，为有效天数
                            hasDataDayNum++;
                        }
                    }else {
                        if(!StringUtils.isEmpty(data.getDayStep()) && Integer.valueOf(data.getDayStep()) > 0){ // 不是当天的日期，取dayStep 字段 大于才有步数，为有效天数
                            hasDataDayNum++;
                        }
                    }*/

                    if (!StringUtils.isEmpty(data.getStep()) && Integer.valueOf(data.getStep()) > 0) { // 为当天的日期，取step 字段的 值大于才有步数，为有效天数  todo  --- 必须用step 字段
                        hasDataDayNum++;
                    }
                }
            }
            if (hasDataDayNum == 0) {
                rsd = new runshowdata(maxStepNum, maxDayTime, maxDistanceNum, maxDayTime, maxKalNum, maxDayTime,
                        0, 0, 0, weekStepNum, weekDistanceNum, weekKalNum, list.size());
            } else {
//            rsd = new runshowdata(maxStepNum, maxDayTime, maxDistanceNum, maxDayTime, maxKalNum, maxDayTime,
//                    allStepNum / list.size(), (float) (allDistanceNum / list.size()), (float) (allKalNum / list.size()), weekStepNum, weekDistanceNum, weekKalNum, list.size());  // 平均的我 ----  总步数/有计步数据的天数累加
                rsd = new runshowdata(maxStepNum, maxDayTime, maxDistanceNum, maxDayTime, maxKalNum, maxDayTime,
                        allStepNum / hasDataDayNum, (float) (allDistanceNum / hasDataDayNum), (float) (allKalNum / hasDataDayNum), weekStepNum, weekDistanceNum, weekKalNum, list.size());
            }
        }
        msg.what = 0;
        msg.obj = rsd;
        mHandler.sendMessage(msg);
    }


    private List<RunData> jumpRunData() {
        if (db == null) {
            db = DBHelper.getInstance(getActivity());
        }
        String watch = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Query query = null;
        List<RunData> list = null;
        List<RunData> listData = new ArrayList<>();
        if (watch.equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                    SharedPreUtil.SHOWMAC).equals("")) {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Step.eq("0")).build();
            } else {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.SHOWMAC)))
                        .where(RunDataDao.Properties.Step.eq("0")).build();
            }
            list = query.list();

        } else if (watch.equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {
            int synStep = 0;
            int realStep = 0;
            float calorie = 0;
            float distance = 0;
            String today = sdf.format(new Date());
            RunData runDB = new RunData();
            if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {
                synStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
            }
            if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {
                realStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
            }
            if (synStep <= realStep) {
                runDB.setStep(realStep + "");
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {
                    calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                }
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {
                    distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                }
            } else {
                runDB.setStep(synStep + "");
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE))) {
                    calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE));
                }
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE))) {
                    distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE));
                }
            }
            runDB.setCalorie(calorie + "");
            runDB.setDistance(distance + "");
            runDB.setDate(today);
            listData.add(runDB);
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                    SharedPreUtil.SHOWMAC).equals("")) {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Date.notEq(today))
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.MAC))).build();
            } else {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Date.notEq(today))
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.SHOWMAC))).build();
            }
            list = query.list();
        } else {
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                    SharedPreUtil.SHOWMAC).equals("")) {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.MAC))).build();
            } else {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.SHOWMAC))).build();
            }
            list = query.list();
        }
        List<String> listTime = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean isSame = false;
            for (int j = 0; j < listTime.size(); j++) {
                if (list.get(i).getDate().equals(listTime.get(j))) {
                    isSame = true;
                    break;
                }
            }
            if (!isSame) {
                listTime.add(list.get(i).getDate());
            }
        }

        for (int i = 0; i < listTime.size(); i++) {
            RunData rundata = new RunData();
            int step = 0;
            float kal = 0;
            float distance = 0;
            for (int j = 0; j < list.size(); j++) {
                if (listTime.get(i).equals(list.get(j).getDate())) {
                    if (watch.equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
                        step += Integer.parseInt(list.get(j).getDayStep());
                        kal += Float.parseFloat(list.get(j).getDayCalorie());
                        distance += Float.parseFloat(list.get(j).getDayDistance());
                    } else {
                        step += Integer.parseInt(list.get(j).getStep());
                        kal += Float.parseFloat(list.get(j).getCalorie());
                        distance += Float.parseFloat(list.get(j).getDistance());
                    }
                }
            }
            rundata.setStep(step + "");
            rundata.setCalorie(kal + "");
            rundata.setDistance(distance + "");
            rundata.setDate(listTime.get(i) + "");
            listData.add(rundata);
        }

        return listData;
    }


    private void inittext() {
        my_step_tv = (TextView) settingView.findViewById(R.id.my_step_tv);
        my_distance_tv = (TextView) settingView.findViewById(R.id.my_distance_tv);
        my_kal_tv = (TextView) settingView.findViewById(R.id.my_kal_tv);
        my_name_tv = (TextView) settingView.findViewById(R.id.my_name_tv);
        tv_step_percent = (TextView) settingView.findViewById(R.id.tv_step_percent);
        tv_distance_percent = (TextView) settingView.findViewById(R.id.tv_distance_percent);
        tv_kal_percent = (TextView) settingView.findViewById(R.id.tv_kal_percent);
        tv_my_viewpager_title = (TextView) settingView.findViewById(R.id.tv_my_viewpager_title);
        not_login = (TextView) settingView.findViewById(R.id.not_login);
        my_qiandao = (Button) settingView.findViewById(R.id.my_qiandao);
        my_touxian = (CircleImageView) settingView.findViewById(R.id.my_touxian);
        iv_step = (ImageView) settingView.findViewById(R.id.iv_step);
        iv_distance = (ImageView) settingView.findViewById(R.id.iv_distance);
        iv_kal = (ImageView) settingView.findViewById(R.id.iv_kal);
        settingView.findViewById(R.id.tv_mydata).setOnClickListener(this);  //设置个人资料条目

        my_distance_tv_up = (TextView) settingView.findViewById(R.id.my_distance_tv_up);
        my_kal_tv_up = (TextView) settingView.findViewById(R.id.my_kal_tv_up);
        //TODO ---- 先判断用户是否是第一次登陆（第一次设置本页面的数据），是第一次，从网络获取数据，如果不是，先从本地获取数据，本地没有数据再从网络获取数据
        //  refreshUserDatas();
    }

    void initnamehead() {
        String name = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.NAME);

        if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.NAME).equals("")) {
            String languageLx = Utils.getLanguage();
            if (languageLx.equals("es") || languageLx.equals("de")) {  // en

                my_name_tv.setTextSize(16);
            }
            my_name_tv.setText(getString(R.string.not_set_info));
        } else {
            if (name.length() > 10) {
                my_name_tv.setTextSize(15);
                my_name_tv.setText(name);
            } else {
                my_name_tv.setText(name);
            }
        }
        /** 头像显示只在手动修改的情况下改 */
//        try {
//            setheadp();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    private void initTodayData() {
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例

        todayString = getDateFormat.format(ca.getTime());   // 今天

        ca.add(Calendar.DATE, -1); // 减1 MONTH,DATE
        yesterdayString = getDateFormat.format(ca.getTime()); // 昨天

        today_step = 0;
        today_distance = 0;
        today_kal = 0;
        yesterday_step = 0;
        yesterday_distance = 0;
        yesterday_kal = 0;
        judgmentRunDB(todayString, 0);   // 根据今天的日期，查询数据库
        judgmentRunDB(yesterdayString, 1); // 根据昨天的日期，查询数据库

        if (today_step > 200000) {
            today_step = 52007;

            String distance = String.format(Locale.ENGLISH, "%.2f", (today_step * 0.7) / 1000.0);     // 运动距离
            today_distance = Float.valueOf(distance);

            int userWeightI = 0;
            String userWeight = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WEIGHT);
            if (StringUtils.isEmpty(userWeight)) {
                userWeightI = 60;
            } else {
                userWeightI = Integer.valueOf(userWeight);
            }
            today_kal = Float.valueOf(String.format(Locale.ENGLISH, "%1$.2f", (float) (userWeightI) * (float) ((today_step * 0.7) / 1000.0) * 1.036));  // 卡路里

        } else if (today_step < 0) {
            today_step = 0;
            today_distance = 0;
            today_kal = 0;
        }

        if (SharedPreUtil.YES.equals(SharedPreUtil.getParam(getActivity(), SharedPreUtil.USER, SharedPreUtil.METRIC, SharedPreUtil.YES))) {
            my_distance_tv_up.setText(getActivity().getString(R.string.kilometer));
            my_kal_tv_up.setText(getActivity().getString(R.string.everyday_calorie));
        } else {
            today_distance = (float) Utils.getUnit_km(Float.valueOf(today_distance));
            today_kal = (float) Utils.getUnit_kal(today_kal);  // 卡路里
            yesterday_distance = (float) Utils.getUnit_km(Float.valueOf(yesterday_distance)); //昨天的距离
            yesterday_kal = (float) Utils.getUnit_kal(yesterday_kal);  // 昨天的卡路里
            my_distance_tv_up.setText(getActivity().getString(R.string.unit_mi));
            my_kal_tv_up.setText(getActivity().getString(R.string.unit_kj));
        }

        my_step_tv.setText(today_step + "");   // 设置今天的步数
        my_distance_tv.setText(Utils.setformat(1, today_distance)); // 设置今天的距离
        my_kal_tv.setText(Utils.setformat(1, today_kal));           // 设置今天的卡路里
        setpercent();  // 设置对比昨天的 % 值
    }

    private void setheadp() {
        String photoName = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.FACE);
        if (photoName == "") {

            TypedArray a = getActivity().obtainStyledAttributes(new int[]{R.attr.im_head_men, R.attr.im_head_women});  // R.attr.im_head_women, R.attr.im_head_men

            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.SEX).equals("1")) {

                my_touxian.setImageDrawable(a.getDrawable(0));
            } else {
                my_touxian.setImageDrawable(a.getDrawable(1));
            }
            a.recycle();
            return;
        }
        String path = FileUtils.SDPATH + photoName;
        Log.e("MyDataActivity ", " 显示的图片路径：" + path);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            my_touxian.setImageBitmap(ImageCacheUtil.toRoundBitmap(bitmap));
            Log.e("MyDataActivity ", " 显示本地图片");
        }
    }

    private void setpercent() {
        // TODO 相对昨天的步数
        if (today_step == 0) {
            if (yesterday_step == 0) {
                tv_step_percent.setTextColor(Color
                        .parseColor(getString(R.color.data_up)));
                iv_step.setImageResource(R.drawable.datahold);
                tv_step_percent.setText("0%");
            } else {
                tv_step_percent.setText("100%");
                tv_step_percent.setTextColor(Color
                        .parseColor(getString(R.color.beatme_kal_tv)));
                iv_step.setImageResource(R.drawable.datadown);
            }

        } else {
            if (yesterday_step == 0) {
                tv_step_percent.setText("100%");
                tv_step_percent.setTextColor(Color.parseColor(getString(R.color.data_up)));
                iv_step.setImageResource(R.drawable.dataup);
            } else {
                if (today_step == yesterday_step) {
                    tv_step_percent.setTextColor(Color.parseColor(getString(R.color.data_up)));
                    tv_step_percent.setText("0%");
                    iv_step.setImageResource(R.drawable.datahold);
                } else if (today_step > yesterday_step) {
                    float percent = ((today_step - yesterday_step) * 100) / yesterday_step;
                    tv_step_percent.setText(Math.round(percent) + "%");

                    tv_step_percent.setTextColor(Color.parseColor(getString(R.color.data_up)));
                    iv_step.setImageResource(R.drawable.dataup);
                } else {
                    float percent = ((yesterday_step - today_step) * 100) / yesterday_step;
                    tv_step_percent.setText(Math.round(percent) + "%");
                    tv_step_percent.setTextColor(Color.parseColor(getString(R.color.beatme_kal_tv)));
                    iv_step.setImageResource(R.drawable.datadown);
                }

            }

        }

        // todo --- 相对昨天的 距离
        if (today_distance == 0) {
            if (yesterday_distance == 0) {
                tv_distance_percent.setTextColor(Color
                        .parseColor(getString(R.color.data_up)));
                iv_distance.setImageResource(R.drawable.datahold);
                tv_distance_percent.setText("0%");
            } else {
                tv_distance_percent.setText("100%");
                tv_distance_percent.setTextColor(Color
                        .parseColor(getString(R.color.beatme_kal_tv)));
                iv_distance.setImageResource(R.drawable.datadown);
            }

        } else {
            if (yesterday_distance == 0) {
                tv_distance_percent.setText("100%");
                tv_distance_percent.setTextColor(Color
                        .parseColor(getString(R.color.data_up)));
                iv_distance.setImageResource(R.drawable.dataup);
            } else {
                if (today_distance == yesterday_distance) {
                    tv_distance_percent.setTextColor(Color
                            .parseColor(getString(R.color.data_up)));
                    tv_distance_percent.setText("0%");
                    iv_distance.setImageResource(R.drawable.datahold);
                } else if (today_distance > yesterday_distance) {
                    float percent = (today_distance - yesterday_distance)
                            * 10000 / (yesterday_distance * 100);
                    tv_distance_percent.setText(Math.round(percent) + "%");
                    tv_distance_percent.setTextColor(Color
                            .parseColor(getString(R.color.data_up)));
                    iv_distance.setImageResource(R.drawable.dataup);
                } else {

                    float percent = (yesterday_distance - today_distance)
                            * 10000 / (yesterday_distance * 100);
                    tv_distance_percent.setText(Math.round(percent) + "%");
                    tv_distance_percent.setTextColor(Color
                            .parseColor(getString(R.color.beatme_kal_tv)));
                    iv_distance.setImageResource(R.drawable.datadown);
                }

            }

        }

        // todo --- 相对昨天的卡路里
        if (today_kal == 0) {
            if (yesterday_kal == 0) {
                tv_kal_percent.setTextColor(Color
                        .parseColor(getString(R.color.data_up)));
                iv_kal.setImageResource(R.drawable.datahold);
                tv_kal_percent.setText("0%");
            } else {
                tv_kal_percent.setText("100%");
                tv_kal_percent.setTextColor(Color
                        .parseColor(getString(R.color.beatme_kal_tv)));
                iv_kal.setImageResource(R.drawable.datadown);
            }

        } else {
            if (yesterday_kal == 0) {
                tv_kal_percent.setText("100%");
                tv_kal_percent.setTextColor(Color
                        .parseColor(getString(R.color.data_up)));
                iv_kal.setImageResource(R.drawable.dataup);
            } else {
                if (today_kal == yesterday_kal) {
                    tv_kal_percent.setTextColor(Color
                            .parseColor(getString(R.color.data_up)));
                    tv_kal_percent.setText("0%");
                    iv_kal.setImageResource(R.drawable.datahold);
                } else if (today_kal > yesterday_kal) {
                    float percent = (today_kal - yesterday_kal)
                            * 10000 / (yesterday_kal * 100);
                    tv_kal_percent.setText(Math.round(percent) + "%");
                    tv_kal_percent.setTextColor(Color
                            .parseColor(getString(R.color.data_up)));
                    iv_kal.setImageResource(R.drawable.dataup);
                } else {
                    float percent = (yesterday_kal - today_kal)
                            * 10000 / (yesterday_kal * 100);
                    tv_kal_percent.setText(Math.round(percent) + "%");
                    tv_kal_percent.setTextColor(Color
                            .parseColor(getString(R.color.beatme_kal_tv)));
                    iv_kal.setImageResource(R.drawable.datadown);
                }
            }
        }
    }

    /**
     * mtk add
     */
    private long mLastClickTime = 0L;

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ib_setting: {
                Intent mIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(mIntent);

            }
            break;

            case R.id.tv_mydata: {  // 设置个人资料条目点击
                if (Gdata.getMid() == Gdata.NOT_LOGIN) {
                    Intent mIntent = new Intent(getActivity(), NewLoginPhoneActivity.class);  // 进入个人资料页面
                    startActivity(mIntent);
                } else {
                    Intent mIntent = new Intent(getActivity(), MyDataActivity.class);  // 进入个人资料页面
                    startActivity(mIntent);
                }
            }
            break;

            default:
                break;
        }
    }

    private void judgmentRunDB(String choiceDate, int today) {
        if (db == null) {
            db = DBHelper.getInstance(getActivity());
        }
        Query query = null;
        List list = null;
        String watch = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        if (!watch.equals("3") && !SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
            if (watch.equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {
                if (today == 0) {  // 今天的数据
                    list = new ArrayList();
                    int synStep = 0;
                    int realStep = 0;
                    float calorie = 0;
                    float distance = 0;
                    RunData runDB = new RunData();
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    String realTime = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME);  // 实时的日期
                    if (choiceDate.equals(todayString) && !StringUtils.isEmpty(realTime) && realTime.equals(todayString)) {    // todo ---- 为当前天日期,且有当前的实时步数
                        if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {
                            synStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
                        }
                        if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {
                            realStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
                        }
                        if (synStep <= realStep) {
                            runDB.setStep(realStep + "");
                            if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {
                                calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                            }
                            if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {
                                distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                            }
                        } else {
                            runDB.setStep(synStep + "");
//                            if(!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE))){    //todo ---- ble平台都是用的 实时的 卡路里和 距离
//                                calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE));
//                            }
//                            if(!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE))){
//                                distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE));
//                            }

                            if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {
                                calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                            }
                            if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {
                                distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                            }
                        }
                    } else {
                        runDB.setStep(0 + "");
                        calorie = 0;
                        distance = 0;
                    }
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                    runDB.setCalorie(calorie + "");
                    runDB.setDistance(distance + "");
                    list.add(runDB);
                } else if (today == 1) {   //昨天的步数
                    if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                            SharedPreUtil.SHOWMAC).equals("")) {
                        query = db
                                .getRunDao()
                                .queryBuilder()
                                .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                        getActivity(), SharedPreUtil.USER,
                                        SharedPreUtil.MAC)))
                                .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
                    } else {
                        query = db
                                .getRunDao()
                                .queryBuilder()
                                .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                        getActivity(), SharedPreUtil.USER,
                                        SharedPreUtil.SHOWMAC)))
                                .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
                    }
                    list = query.list();
                }
            } else {
                if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                        SharedPreUtil.SHOWMAC).equals("")) {
                    query = db
                            .getRunDao()
                            .queryBuilder()
                            .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                    getActivity(), SharedPreUtil.USER,
                                    SharedPreUtil.MAC)))
                            .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
                } else {
                    query = db
                            .getRunDao()
                            .queryBuilder()
                            .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                    getActivity(), SharedPreUtil.USER,
                                    SharedPreUtil.SHOWMAC)))
                            .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
                }
                list = query.list();
            }
        } else {
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                    SharedPreUtil.SHOWMAC).equals("")) {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate))
                        .where(RunDataDao.Properties.Step.eq("0")).build();
            } else {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.SHOWMAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate))
                        .where(RunDataDao.Properties.Step.eq("0")).build();
            }
            list = query.list();
        }
        ArrayList<RunData> runData = new ArrayList<RunData>();
        if (list != null && list.size() >= 1) {
            switch (today) {
                case 0:   // 今天
                    today_step = 0;
                    today_distance = 0;
                    today_kal = 0;
                    if (!watch.equals("3") && !SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
                        for (int j = 0; j < list.size(); j++) {
                            RunData runDB = (RunData) list.get(j);
                            today_step += Utils.toint(runDB.getStep());
                            today_distance += Utils.toDouble(runDB.getDistance());
                            today_kal += Utils.toDouble(runDB.getCalorie());
                        }
                    } else {
                        for (int j = 0; j < list.size(); j++) {
                            RunData runDB = (RunData) list.get(j);
                            // runData.add(runDB);
                            today_step += Utils.toint(runDB.getDayStep());
                            today_distance += Utils.toDouble(runDB.getDayDistance());
                            today_kal += Utils.toDouble(runDB.getDayCalorie());
                        }
                    }

                    break;
                case 1:  // 昨天
                    yesterday_step = 0;
                    yesterday_distance = 0;
                    yesterday_kal = 0;
                    if (!watch.equals("3") && !SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
                        for (int j = 0; j < list.size(); j++) {
                            RunData runDB = (RunData) list.get(j);
                            // runData.add(runDB);
                            yesterday_step += Utils.toint(runDB.getStep());
                            yesterday_distance += Utils.toDouble(runDB.getDistance());
                            yesterday_kal += Utils.toDouble(runDB.getCalorie());
                        }
                    } else {
                        for (int j = 0; j < list.size(); j++) {
                            RunData runDB = (RunData) list.get(j);
                            yesterday_step += Utils.toint(runDB.getDayStep());
                            yesterday_distance += Utils.toDouble(runDB.getDayDistance());
                            yesterday_kal += Utils.toDouble(runDB.getDayCalorie());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private ArrayList<RunData> judgmentRunDB(String choiceDate) {
        if (db == null) {
            db = DBHelper.getInstance(getActivity());
        }
        Query query = null;
        List list = null;
        String watch = SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        ArrayList<RunData> runData = new ArrayList<RunData>();
        if (watch.equals("3") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                    SharedPreUtil.SHOWMAC).equals("")) {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate))
                        .where(RunDataDao.Properties.Step.eq("0")).build();
            } else {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        // .where(RunDataDao.Properties.Mid.eq(mid))
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.SHOWMAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate))
                        .where(RunDataDao.Properties.Step.eq("0"))
                        .build();
            }
            list = query.list();
        } else if (watch.equals("2") || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("2")) {
            String realTime = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.WATCHTIME);  // 实时的日期
            if (sdf.format(new Date()).equals(choiceDate) && !StringUtils.isEmpty(realTime) && realTime.equals(sdf.format(new Date()))) {
                int synStep = 0;
                int realStep = 0;
                float calorie = 0;
                float distance = 0;
                RunData runDB = new RunData();
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN))) {
                    synStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNRUN));
                }
                if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN))) {
                    realStep = Integer.parseInt(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.RUN));
                }
                if (synStep <= realStep) {
                    runDB.setStep(realStep + "");
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {
                        calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                    }
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {
                        distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                    }
                } else {
                    runDB.setStep(synStep + "");
//                    if(!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE))){
//                        calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNCALORIE));
//                    }
//                    if(!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE))){
//                        distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.SYNDISTANCE));
//                    }
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE))) {
                        calorie = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.CALORIE));
                    }
                    if (!TextUtils.isEmpty(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE))) {
                        distance = Float.parseFloat(SharedPreUtil.readPre(getActivity(), SharedPreUtil.BLEWATCHDATA, SharedPreUtil.DISTANCE));
                    }
                }
                runDB.setCalorie(calorie + "");
                runDB.setDistance(distance + "");
                runDB.setDate(choiceDate);
                runData.add(runDB);
            } else {
                if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                        SharedPreUtil.SHOWMAC).equals("")) {
                    query = db
                            .getRunDao()
                            .queryBuilder()
                            .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                    getActivity(), SharedPreUtil.USER,
                                    SharedPreUtil.MAC)))
                            .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
                } else {
                    query = db
                            .getRunDao()
                            .queryBuilder()
                            .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                    getActivity(), SharedPreUtil.USER,
                                    SharedPreUtil.SHOWMAC)))
                            .where(RunDataDao.Properties.Date.eq(choiceDate))

                            .build();
                }
                list = query.list();
            }
        } else {
            if (SharedPreUtil.readPre(getActivity(), SharedPreUtil.USER,
                    SharedPreUtil.SHOWMAC).equals("")) {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.MAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate)).build();
            } else {
                query = db
                        .getRunDao()
                        .queryBuilder()
                        .where(RunDataDao.Properties.Mac.eq(SharedPreUtil.readPre(
                                getActivity(), SharedPreUtil.USER,
                                SharedPreUtil.SHOWMAC)))
                        .where(RunDataDao.Properties.Date.eq(choiceDate))

                        .build();
            }
            list = query.list();
        }
        if (list != null && list.size() >= 1) {
            for (int j = 0; j < list.size(); j++) {
                RunData runDB = (RunData) list.get(j);
                runData.add(runDB);
            }
        }
        return runData;
    }


    public class Bluttoothbroadcast extends BroadcastReceiver {
        /*
         * State wifiState = null; State mobileState = null;
		 */

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.e("onReceive", action);
            if (intent.getAction().equals(MainService.ACTION_CHANGE_WATCH) || MainService.ACTION_SYNFINSH_SUCCESS.equals(action)) { // TODO 我的页面收到手表数据同步成功的广播了
                initTodayData();
                setv_vp1data();
            }

            if (MainService.ACTION_SYNFINSH.equals(action)) {
                String stepNum = intent.getStringExtra("step");
                if (!StringUtils.isEmpty(stepNum) && stepNum.equals("6")) {
                    initTodayData();
                    setv_vp1data();
                }
            }

            if (MainService.ACTION_MYINFO_CHANGE.equals(action)) {
                initnamehead();
                setUserName();
            }

            if (MainService.ACTION_USERDATACHANGE.equals(action)) {
//                inithealthData();
//                setUphealthView();
//                initshow();

                setUserName();
//                setHeadPhoto();
            }

        }
    }


    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("SettingFragment");

        setv_vp1data();

        setUserName();
//        registerBroad();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingFragment");

//        if (vb != null && ((MainActivity)getActivity()).isScreenOn) {
//            getActivity().unregisterReceiver(vb);
//        }

//        EventBus.getDefault().unregister(this);
    }

    public void onDestroyView() {
        super.onDestroyView();

        if (vb != null) {
            getActivity().unregisterReceiver(vb);
        }


        EventBus.getDefault().unregister(this);
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
}
