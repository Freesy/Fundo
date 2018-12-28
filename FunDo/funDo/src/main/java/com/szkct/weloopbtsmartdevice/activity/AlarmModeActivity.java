package com.szkct.weloopbtsmartdevice.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.mediatek.wearable.WearableManager;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.LanTingBoldBlackTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * Created by Tody Chou
 * Created on 2017/6/15 20:40
 * Version：1.0
 * Description：
 * Revision History：
 * ================================================
 */
public class AlarmModeActivity extends AppCompatActivity {
    private LinearLayout ll_time_pick, ll_other_pick;
    private TextView tv_start_time, tv_stop_time, tv_sit_time, tv_sit_threshold, tv_repeat_setting_week, tv_tips;
    private PickerView pv_start_time, pv_stop_time, pv_sit_time, pv_sit_threshold;
    private RelativeLayout rl_need_hide, rl_repeat_setting, rl_notify_switch;
    private TextView title_three;
    private ImageView iv_back, iv_ok;
    private List<String> dataWeek;
    private String[] ARRAY_FRUIT = new String[7];
    private boolean[] arrayFruitSelectedBooleans = new boolean[7];
    private String weeksave = "";
    private int[] pv_selector = new int[]{1, 1, 1, 1};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_for_three_use);
        initView();
    }

    private void initView() {
        ll_time_pick = (LinearLayout) findViewById(R.id.ll_time_pick);
        ll_other_pick = (LinearLayout) findViewById(R.id.ll_other_pick);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_ok = (ImageView) findViewById(R.id.iv_ok);
        title_three = (TextView) findViewById(R.id.title_three);

        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_stop_time = (TextView) findViewById(R.id.tv_stop_time);
        tv_sit_time = (TextView) findViewById(R.id.tv_sit_time);
        tv_sit_threshold = (TextView) findViewById(R.id.tv_sit_threshold);
        tv_repeat_setting_week = (TextView) findViewById(R.id.tv_repeat_setting_week);
        tv_tips = (TextView) findViewById(R.id.tv_tips);

        pv_start_time = (PickerView) findViewById(R.id.pv_start_time);
        pv_stop_time = (PickerView) findViewById(R.id.pv_stop_time);
        pv_sit_time = (PickerView) findViewById(R.id.pv_sit_time);
        pv_sit_threshold = (PickerView) findViewById(R.id.pv_sit_threshold);

        rl_need_hide = (RelativeLayout) findViewById(R.id.rl_need_hide);
        rl_repeat_setting = (RelativeLayout) findViewById(R.id.rl_repeat_setting);
        rl_notify_switch = (RelativeLayout) findViewById(R.id.rl_notify_switch);

        title_three.setText(R.string.notify_mode);
        tv_tips.setText(R.string.no_tips);

        ll_time_pick.setVisibility(View.GONE);
        rl_need_hide.setVisibility(View.GONE);
        rl_notify_switch.setVisibility(View.GONE);
        tv_sit_time.setVisibility(View.GONE);
        rl_repeat_setting.setVisibility(View.GONE);
        tv_tips.setVisibility(View.GONE);

        ARRAY_FRUIT = getResources().getStringArray(R.array.day_of_week_forx2);
        getBooleans();
        iv_back.setOnClickListener(new MyOnClickListener());
        iv_ok.setOnClickListener(new MyOnClickListener());
        rl_repeat_setting.setOnClickListener(new MyOnClickListener());

        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            pv_start_time.setColor(0x3A90D6);
            pv_stop_time.setColor(0x3A90D6);
            pv_sit_time.setColor(0x3A90D6);
            pv_sit_threshold.setColor(0x3A90D6);
        } else {
            pv_start_time.setColor(0x37EEEA);
            pv_stop_time.setColor(0x37EEEA);
            pv_sit_time.setColor(0x37EEEA);
            pv_sit_threshold.setColor(0x37EEEA);

        }

        final List<String> list = new ArrayList();
        list.add(getResources().getString(R.string.bright));
        list.add(getResources().getString(R.string.vibrate));
        list.add(getResources().getString(R.string.vibrate_bright));


        final List<String> indexList = new ArrayList();
        indexList.addAll(list);

        pv_start_time.setData(Utils.getTimeList(false), pv_selector[0]);
        pv_stop_time.setData(Utils.getTimeList(false), pv_selector[1]);
        pv_sit_time.setData(list, pv_selector[2]);
        pv_sit_threshold.setData(Utils.getStepList(), pv_selector[3]);

        pv_sit_time.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                pv_selector[2] = indexList.indexOf(text);
            }
        });

    }

    public void getBooleans() {
        pv_selector[2] = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_MODE, 3) - 1;
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_ok:
                    saveAndSend();
                    break;
            }
        }
    }

    /**
     * 保存结果并发送到手环
     */
    private void saveAndSend() {
        SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.ALARM_MODE, pv_selector[2] + 1);
        if(MainService.getInstance().getState() == 3) {
            L2Send.sendNotify(BleContants.REMIND_COMMAND, BleContants.REMIND_MODE, new byte[]{(byte) (pv_selector[2] + 1)});
        }
        finish();
    }


}
