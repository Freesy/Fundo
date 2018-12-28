package com.szkct.weloopbtsmartdevice.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.LanTingBoldBlackTextView;

import java.util.ArrayList;
import java.util.List;

import static com.szkct.weloopbtsmartdevice.util.Utils.getTimeList;

/**
 * ================================================
 * Created by Tody Chou
 * Created on 2017/6/15 20:40
 * Version：1.0
 * Description：
 * Revision History：
 * ================================================
 */
public class NoFazeModeActivity extends AppCompatActivity {
    private LinearLayout ll_time_pick, ll_other_pick;
    private TextView tv_start_time, tv_stop_time, tv_sit_time, tv_sit_threshold, tv_repeat_setting_week, tv_tips, tv_notify_switch;
    private ToggleButton tb_notify_switch;
    private PickerView pv_start_time, pv_stop_time, pv_sit_time, pv_sit_threshold;
    private RelativeLayout rl_need_hide, rl_repeat_setting, rl_notify_switch;
    private TextView title_three;
    private ImageView iv_back, iv_ok;
    private List<String> dataWeek;
    private String[] ARRAY_FRUIT = new String[7];
    private boolean[] arrayFruitSelectedBooleans = new boolean[7];
    private String weeksave = "";
    private int[] pv_selector = new int[4];

    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;

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

        //TODO ---- 添加根据语言，调整字体大小
        String languageLx = Utils.getLanguage();
        if(languageLx.equals("ru")) {  // en   tv_stop_time
            tv_start_time.setTextSize(12);
            tv_stop_time.setTextSize(12);
            tv_sit_time.setTextSize(12);
            tv_sit_threshold.setTextSize(12);
        }

        tv_repeat_setting_week = (TextView) findViewById(R.id.tv_repeat_setting_week);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        tv_notify_switch = (TextView) findViewById(R.id.tv_notify_switch);

        tb_notify_switch = (ToggleButton) findViewById(R.id.tb_notify_switch);

        pv_start_time = (PickerView) findViewById(R.id.pv_start_time);
        pv_stop_time = (PickerView) findViewById(R.id.pv_stop_time);
        pv_sit_time = (PickerView) findViewById(R.id.pv_sit_time);
        pv_sit_threshold = (PickerView) findViewById(R.id.pv_sit_threshold);

        rl_need_hide = (RelativeLayout) findViewById(R.id.rl_need_hide);
        rl_repeat_setting = (RelativeLayout) findViewById(R.id.rl_repeat_setting);
        rl_notify_switch = (RelativeLayout) findViewById(R.id.rl_notify_switch);

        tv_notify_switch.setText(R.string.not_disturb);
        tb_notify_switch.setChecked((Boolean) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.NO_SWITCH,false));


        title_three.setText(R.string.not_disturb);
        tv_tips.setText(R.string.no_tips);
        ll_other_pick.setVisibility(View.GONE);
        rl_repeat_setting.setVisibility(View.GONE);
        ARRAY_FRUIT = getResources().getStringArray(R.array.day_of_week);
        getBooleans();
        iv_back.setOnClickListener(new MyOnClickListener());
        iv_ok.setOnClickListener(new MyOnClickListener());
        rl_repeat_setting.setOnClickListener(new MyOnClickListener());
        rl_notify_switch.setOnClickListener(new MyOnClickListener());

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
        pv_start_time.setData(getTimeList(true), pv_selector[0]);
        pv_stop_time.setData(getTimeList(true), pv_selector[1]);
        pv_sit_time.setData(Utils.getSitList(), pv_selector[2]);
        pv_sit_threshold.setData(Utils.getStepList(), pv_selector[3]);

        pv_start_time.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                startHour = Integer.parseInt(text.split(":")[0]);
                startMin = Integer.parseInt(text.split(":")[1]);
                pv_selector[0] = Utils.getTimeList(true).indexOf(text);
            }
        });
        pv_stop_time.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                endHour = Integer.parseInt(text.split(":")[0]);
                endMin = Integer.parseInt(text.split(":")[1]);
                pv_selector[1] = Utils.getTimeList(true).indexOf(text);
            }
        });

    }

    public void getBooleans() {
        //0 代表未选择 1代表选择
//        String s = (String) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.SIT_REPEAT_WEEK, "1111111");
//        char[] c = s.toCharArray();
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < c.length; i++) {
//            arrayFruitSelectedBooleans[i] = (c[i] == '1');
//        }
//        if (s.equals("0000000")) {
//            sb.append(getString(R.string.one_time_alert));
//        } else {
//            for (int i = 0; i < arrayFruitSelectedBooleans.length; i++) {
//                if (i == 0) {
//                    sb.append(ARRAY_FRUIT[0]);
//                } else {
//                    if (arrayFruitSelectedBooleans[i]) {
//                        sb.append("、" + ARRAY_FRUIT[i]);
//                    }
//                }
//            }
//        }
//        tv_repeat_setting_week.setText(sb.toString());
        startHour = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.NO_START_TIME, 9);
        startMin = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.NO_START_TIME_MIN, 0);
        endHour = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.NO_STOP_TIME, 11);
        endMin = (int) SharedPreUtil.getParam(this, SharedPreUtil.USER, SharedPreUtil.NO_STOP_TIME_MIN, 0);

        pv_selector[0] = (startMin == 0 ? startHour * 2 : startHour * 2 + 1);
        pv_selector[1] = (endMin == 0 ? endHour * 2 : endHour * 2 + 1);

    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_ok:
                    if (MainService.getInstance().getState()==3) {
                        saveAndSend();
                    }else {
                        Toast.makeText(NoFazeModeActivity.this,R.string.not_connected,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.rl_repeat_setting:
//                    dialog();
                    break;
                case R.id.rl_notify_switch:
                    tb_notify_switch.setChecked(!tb_notify_switch.isChecked());
                    break;
            }
        }
    }

    /**
     * 保存结果并发送到手环
     */
    private void saveAndSend() {
        if(startHour + startMin == endHour + endMin){
            Toast.makeText(this,R.string.endtime_notreal,Toast.LENGTH_SHORT).show();
            return;
        }

//        if(pv_selector[0] > pv_selector[1]){
//            Toast.makeText(this,R.string.end_time_nosmll,Toast.LENGTH_SHORT).show();
//            return;
//        }

        SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.NO_SWITCH, tb_notify_switch.isChecked());
        SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.NO_START_TIME, startHour);
        SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.NO_START_TIME_MIN, startMin);
        SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.NO_STOP_TIME, endHour);
        SharedPreUtil.setParam(this, SharedPreUtil.USER, SharedPreUtil.NO_STOP_TIME_MIN, endMin);
        byte[] bytes = new byte[5];
        bytes[0] = (byte) (tb_notify_switch.isChecked() == true ? 1 : 0);
        bytes[1] = (byte) startHour;
        bytes[2] = (byte) startMin;
        bytes[3] = (byte) endHour;
        bytes[4] = (byte) endMin;

        L2Send.sendNotify(BleContants.REMIND_COMMAND, BleContants.REMIND_DISRURB,bytes);

        finish();
    }

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
                        // TODO Auto-generated method stub

                        dataWeek = new ArrayList<>();
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
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }
}
