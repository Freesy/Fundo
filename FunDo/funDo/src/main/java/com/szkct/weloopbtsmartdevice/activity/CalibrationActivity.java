package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.Log;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.view.PointerCalibrationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;

import static u.aly.cw.i;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/1/4
 * 描述: ${VERSION}
 * 修订历史：
 */

public class CalibrationActivity extends AppCompatActivity implements View.OnClickListener{

    private PickerView pv_calibration_hour,pv_calibration_minute,pv_calibration_second;
    private LinearLayout ll_calibration,ll_check_calibration;
    private PointerCalibrationView calibrationView;
    private ArrayList<String> hourList = new ArrayList<>();
    private ArrayList<String> minuteList = new ArrayList<>();
    private ArrayList<String> secondList = new ArrayList<>();
    public final static String SEND_CALIBRATION = "send_calibration";
    public final static String CONFIRM_CALIBRATION = "confirm_calibration";
    public final static String REFUSE_CALIBRATION = "refuse_calibration";
    public final static String CANCEL_CALIBRATION = "cancel_calibration";
    private int hour;
    private int minute;
    private int second;
    private int yearNew;
    private int monthNew;
    private int dayNew;
    private int hourNew;
    private int minuteNew;
    private int secondNew;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent event) {
        if(event.getMessage() != null){
            if(event.getMessage().equals(CONFIRM_CALIBRATION)){
                init();
            }else if(event.getMessage().equals(REFUSE_CALIBRATION)){
                finish();
            }else if(event.getMessage().equals(CANCEL_CALIBRATION)){
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_calibration);

        EventBus.getDefault().register(this);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_save).setOnClickListener(this);

        ll_calibration = (LinearLayout) findViewById(R.id.ll_calibration);
        ll_check_calibration = (LinearLayout) findViewById(R.id.ll_check_calibration);
        pv_calibration_hour = (PickerView) findViewById(R.id.pv_calibration_hour);
        pv_calibration_minute = (PickerView) findViewById(R.id.pv_calibration_minute);
        pv_calibration_second = (PickerView) findViewById(R.id.pv_calibration_second);
        calibrationView = (PointerCalibrationView) findViewById(R.id.calibration_view);

        if(BTNotificationApplication.getMainService().getState() == MainService.STATE_CONNECTED) {
            L2Send.setCalibration(1);  //进入指针校准
        }

        //初始化当前时间
        Calendar calendar = Calendar.getInstance();
        yearNew = calendar.get(Calendar.YEAR);
        monthNew = calendar.get(Calendar.MONTH);
        dayNew = calendar.get(Calendar.DAY_OF_MONTH);
        hourNew = calendar.get(Calendar.HOUR);
        minuteNew = calendar.get(Calendar.MINUTE);
        secondNew = calendar.get(Calendar.SECOND);

    }


    private void init(){
        findViewById(R.id.iv_save).setVisibility(View.VISIBLE);
        ll_calibration.setVisibility(View.VISIBLE);
        ll_check_calibration.setVisibility(View.GONE);

        for (int i = 1; i <= 12; i++) {
            hourList.add(i + "");
        }

        for (int i = 0; i < 60; i++) {
            minuteList.add(i + "");
        }

        for (int i = 0; i <= 40 ; i+= 20) {
            if(i == 0){
                secondList.add("00");
            }else{
                secondList.add(i + "");
            }

        }

        //初始化指针（当前时间）
        Calendar calendar = Calendar.getInstance();
        second = Integer.parseInt(secondList.get(0));
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR);
        calibrationView.setTime(hour,minute,second);



        pv_calibration_hour.setData(hourList,hourList.indexOf(hour + "") == - 1 ?  0 : hourList.indexOf(hour + ""));
        pv_calibration_minute.setData(minuteList,minuteList.indexOf(minute + "") == - 1 ?  0 : minuteList.indexOf(minute + ""));
        pv_calibration_second.setData(secondList,0);


        pv_calibration_hour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                hour = Integer.parseInt(text);
                calibrationView.setTime(hour,minute,second);
            }
        });
        pv_calibration_minute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                minute = Integer.parseInt(text);
                calibrationView.setTime(hour,minute,second);
            }
        });
        pv_calibration_second.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                second = Integer.parseInt(text);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L2Send.setCalibration(0);   //退出指针校准
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                if(BTNotificationApplication.getMainService().getState() == MainService.STATE_CONNECTED) {
                    L2Send.setCalibration(0);   //退出指针校准
                }
                break;
            case R.id.iv_save:
                byte[] value = new byte[]{(byte) yearNew , (byte) monthNew , (byte) dayNew , (byte) hourNew
                        , (byte) minuteNew , (byte) secondNew , (byte) hour , (byte) minute , (byte) second};
                if(BTNotificationApplication.getMainService().getState() == MainService.STATE_CONNECTED) {
                    L2Send.sendCalibration(value);
                }
                break;
        }
    }
}
