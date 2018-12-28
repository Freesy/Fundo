package com.szkct.weloopbtsmartdevice.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.AlarmClockData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/6/28
 * 描述: ${VERSION}
 * 修订历史：
 */
public class NewAlarmActivity extends AppCompatActivity {
    private static final String TAG = NewAlarmActivity.class.getSimpleName();
    private TextView tv_ring_type, tv_repeat_setting_week;
    private PickerView pv_hour, pv_min;
    private ImageView iv_back, iv_ok;
    private RelativeLayout rl_repeat_setting, rl_ring_type;

    private List<String> dataWeek, hourList, minList;
    private String[] ARRAY_FRUIT = new String[7];
    private String[] type = new String[3];
    private boolean[] arrayFruitSelectedBooleans = new boolean[7];
    private boolean[] arrayFruitSelectedBooleans_example = new boolean[7];
    private String weeksave = "00000000";  //11111110   默认模式为仅一次
//    private int[] pv_selector = new int[]{7,30};
    private int[] pv_selector = new int[2];
    private int mode = 0;     //
    private DBHelper dbHelper;
    private AlarmClockData alarmClockData;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_newalarm);

        initView();

        intent = getIntent();
        alarmClockData = (AlarmClockData) intent.getSerializableExtra("AlarmClock");
        if(alarmClockData != null){
            String[] mTime = alarmClockData.getTime().split(":");
            pv_selector[0] = Integer.valueOf(mTime[0]);
            pv_selector[1] = Integer.valueOf(mTime[1]);

            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_HOUR, pv_selector[0]);
            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_MIN, pv_selector[1]);
        }


        if(alarmClockData != null){
            tv_ring_type.setText(type[Integer.parseInt(alarmClockData.getRemind())]);
            weeksave = alarmClockData.getCycle();   // 1111 1110
            char[] c = weeksave.toCharArray();    // 1111 1110        

            if(c.length == 8){
                for (int i = 0; i < c.length; i++) {
                    if(i == c.length - 1){
                        break;
                    }
                    arrayFruitSelectedBooleans[i] = (c[i] == '1');
                    arrayFruitSelectedBooleans_example[i] = (c[i] == '1');
                }
            }else {
                for (int i = 0; i < c.length; i++) {
                    arrayFruitSelectedBooleans[i] = (c[i] == '1');
                    arrayFruitSelectedBooleans_example[i] = (c[i] == '1');
                }
            }
            tv_repeat_setting_week.setText(Utils.getFrequency(this,alarmClockData.getCycle()));
            pv_hour.setData(hourList,Integer.parseInt(alarmClockData.getTime().split(":")[0]));
            pv_min.setData(minList,Integer.parseInt(alarmClockData.getTime().split(":")[1]));
        }else{
            getBooleans();
        }
    }

    private void initView() {

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_ok = (ImageView) findViewById(R.id.iv_ok);

        tv_ring_type = (TextView) findViewById(R.id.tv_ring_type);   //提醒方式
        tv_repeat_setting_week = (TextView) findViewById(R.id.tv_repeat_setting_week);  //重复设置

        pv_hour = (PickerView) findViewById(R.id.pv_hour);
        pv_min = (PickerView) findViewById(R.id.pv_min);

        rl_repeat_setting = (RelativeLayout) findViewById(R.id.rl_repeat_setting);
        rl_ring_type = (RelativeLayout) findViewById(R.id.rl_ring_type);
        type = getResources().getStringArray(R.array.type_mode);
        ARRAY_FRUIT = getResources().getStringArray(R.array.day_of_week_forx2);
        iv_back.setOnClickListener(new MyOnClickListener());
        iv_ok.setOnClickListener(new MyOnClickListener());
        rl_repeat_setting.setOnClickListener(new MyOnClickListener());
        rl_ring_type.setOnClickListener(new MyOnClickListener());

        hourList = new ArrayList<>();
        minList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hourList.add(String.format(Locale.ENGLISH,"%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minList.add(String.format(Locale.ENGLISH,"%02d", i));
        }

        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            pv_hour.setColor(0x3A90D6);
            pv_min.setColor(0x3A90D6);
        } else {
            pv_hour.setColor(0x37EEEA);
            pv_min.setColor(0x37EEEA);

        }

        pv_hour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                pv_selector[0] = Integer.parseInt(text);
            }
        });
        pv_min.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                pv_selector[1] = Integer.parseInt(text);
            }
        });

        if(dbHelper == null){
            dbHelper = DBHelper.getInstance(BTNotificationApplication.getInstance().getApplicationContext());
        }
    }

    /**
     * 创建新的闹钟
     */
    public void getBooleans() {

        tv_ring_type.setText(type[mode]);
        char[] c = weeksave.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if(i == c.length - 1){  // c.length - 1   i == 0   todo --- 取前面 7位
                break;
            }
            arrayFruitSelectedBooleans[i] = (c[i] == '1');
            arrayFruitSelectedBooleans_example[i] = (c[i] == '1');
//            arrayFruitSelectedBooleans[i-1] = (c[i] == '1');
        }
        tv_repeat_setting_week.setText(Utils.getFrequency(this, weeksave));  // 0111111

        pv_selector[0] = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_HOUR, 7);
        pv_selector[1] = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_MIN, 30);

        pv_hour.setData(hourList, pv_selector[0]);
        pv_min.setData(minList, pv_selector[1]);

    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_ok:
                    //当前蓝牙状态是连接的时候才保存数据
                    if(MainService.getInstance().getState() == 3){
                     if(isFastDoubleClick()) {
                         return;
                     }
                        saveAndSend();
                        Toast.makeText(BTNotificationApplication.getInstance().getApplicationContext(),R.string.save_success,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(BTNotificationApplication.getInstance().getApplicationContext(),R.string.ble_not_connected,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.rl_repeat_setting:
                    dialog();
                    break;
                case R.id.rl_ring_type:
                    mode();
                    break;
            }
        }
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

    private void mode() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.ring_type)
                .setIcon(R.drawable.ic_launcher)
                .setSingleChoiceItems(type, mode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mode = which;
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        tv_ring_type.setText(type[mode]);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    /**
     * 保存结果
     */
    private void saveAndSend() {
        if(alarmClockData == null) {
            AlarmClockData alarmClockData = new AlarmClockData();
            alarmClockData.setMac(SharedPreUtil.readPre(BTNotificationApplication.getInstance().getApplicationContext()
                    , SharedPreUtil.USER, SharedPreUtil.MAC));
            alarmClockData.setMid(SharedPreUtil.readPre(BTNotificationApplication.getInstance().getApplicationContext()
                    , SharedPreUtil.USER, SharedPreUtil.MID));
            alarmClockData.setType("1");
            alarmClockData.setRemind(mode + "");

            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_HOUR, pv_selector[0]);
            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_MIN, pv_selector[1]);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            String day = simpleDateFormatDay.format(new Date());     //当前时间
            String time = simpleDateFormat.format(new Date()) + " " + String.format(Locale.ENGLISH,"%02d", pv_selector[0]) + ":" + String.format(Locale.ENGLISH,"%02d", pv_selector[1]) + ":00";    //选择时间
            try {
                if(weeksave.equals("00000000")) {
                    if (simpleDateFormatDay.parse(day).getTime() > simpleDateFormatDay.parse(time).getTime()) {
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        time = simpleDateFormat.format(calendar.getTime()) + " " + String.format(Locale.ENGLISH,"%02d", pv_selector[0]) + ":" + String.format(Locale.ENGLISH,"%02d", pv_selector[1]) + ":00";
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            alarmClockData.setAlarm_time(time);
            alarmClockData.setTime(String.format(Locale.ENGLISH,"%02d", pv_selector[0]) + ":" + String.format(Locale.ENGLISH,"%02d", pv_selector[1]));
            alarmClockData.setCycle(weeksave);
            alarmClockData.setUpload("0");
            dbHelper.saveAlarmClockData(alarmClockData);
            //setResult(RESULT_FIRST_USER,intent);
            EventBus.getDefault().post(new MessageEvent("result_ok"));
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            String day = simpleDateFormatDay.format(new Date());     //当前时间
            String time = simpleDateFormat.format(new Date()) + " " + String.format(Locale.ENGLISH,"%02d", pv_selector[0]) + ":" + String.format(Locale.ENGLISH,"%02d", pv_selector[1])+ ":00";    //选择时间

            try {
                if(weeksave.equals("00000000")) {
                    if (simpleDateFormatDay.parse(day).getTime() > simpleDateFormatDay.parse(time).getTime()) {
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        time = simpleDateFormat.format(calendar.getTime()) + " " + String.format(Locale.ENGLISH,"%02d", pv_selector[0]) + ":" + String.format(Locale.ENGLISH,"%02d", pv_selector[1])+ ":00";
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            alarmClockData.setCycle(weeksave);    // 0111 1110   --- 取从左到右 1到7位

            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_HOUR, pv_selector[0]);
            SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_MIN, pv_selector[1]);

            alarmClockData.setAlarm_time(time);  // 7 ---  30
            alarmClockData.setTime(String.format(Locale.ENGLISH,"%02d", pv_selector[0]) + ":" + String.format(Locale.ENGLISH,"%02d", pv_selector[1]));
            alarmClockData.setRemind(mode+"");
            dbHelper.updataAlarmClockData(alarmClockData);
            //setResult(RESULT_OK,intent);
            EventBus.getDefault().post(new MessageEvent("result_ok"));
        }
        finish();
    }

    /**
     * 选择重复设置
     */
    public void dialog() {

        //默认选中
        Dialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.repeat_setting))
                .setIcon(R.drawable.ic_launcher)
                .setMultiChoiceItems(ARRAY_FRUIT, arrayFruitSelectedBooleans, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        arrayFruitSelectedBooleans[which] = isChecked;
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dataWeek = new ArrayList<>();
                        weeksave = "";   // 00000100
                        System.arraycopy(arrayFruitSelectedBooleans,0,arrayFruitSelectedBooleans_example,0,arrayFruitSelectedBooleans.length);
                        for (int i = 0; i < arrayFruitSelectedBooleans.length; i++) {
                            if (arrayFruitSelectedBooleans[i] == true) {
                                dataWeek.add(ARRAY_FRUIT[i]);
                                weeksave = weeksave + "1";
                            } else {
                                weeksave = weeksave + "0";
                            }
                        }
                        StringBuffer sb = new StringBuffer();
                        //只提醒一次
                        if (0 == dataWeek.size()) {
                            sb.append(getString(R.string.one_time_alert));
                        } else {
                            for (int i = 0; i < dataWeek.size(); i++) {
                                if (i == 0) {
                                    sb.append(dataWeek.get(0));
                                } else {
                                    sb.append("、" + dataWeek.get(i));
                                }
                            }
                        }
                        tv_repeat_setting_week.setText(sb.toString());
//                        weeksave = weeksave + "0";
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        System.arraycopy(arrayFruitSelectedBooleans_example,0,arrayFruitSelectedBooleans,0,arrayFruitSelectedBooleans.length);
                    }
                }).create();
        dialog.show();
    }
}
