package com.szkct.weloopbtsmartdevice.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.map.shared.StatusShared;
import com.szkct.map.utils.Util;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.StrericWheelAdapter;
import com.szkct.weloopbtsmartdevice.view.WheelView;

import static com.kct.fundo.btnotification.R.id.ll_motionsetting_voice_setting;

/**
 *    运动设置页面
 */
public class MotionSettingActivity extends AppCompatActivity implements
        OnClickListener {

    private TextView tv_runsetting_voice, tv_runsetting_screen,
            tv_runsetting_autopause, tv_runsetting_autostop;

    private TextView tv_motionsetting_goal, tv_motionsetting_voice_setting,
            tv_motionsetting_reciprocal, tv_motionsetting_mapsetting;

    private TextView sportshistory_vibrator_setting, sportshistory_screen_always_on,
            sportshistory_automatic_pause, sportshistory_automatic_stop;

    private CheckBox cb_runsetting_voice, cb_runsetting_screen,
            cb_runsetting_autopause, cb_runsetting_autostop;

    private WheelView wheelView, wheelr;
    private PopupWindow mPopupWindow;

    String[] voicelist, reciprocallist, maplist, reciprocallist2, maptowlist, mapnamelist;
    private StatusShared shared;
    private MotionSettingActivity mContext;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_motionsetting_goal:  //运动目标设置
                startActivity(new Intent(getApplicationContext(),
                        MotionGoalActivity.class));
                break;
            case ll_motionsetting_voice_setting:  // 语音设置   ---- 现为振动设置
//                Toast.makeText(mContext,R.string.developed,Toast.LENGTH_SHORT).show();
//                showpop(v, 0);

                break;
            case R.id.ll_motionsetting_reciprocal:  // 倒数设置
                showpop(v, 1);
                break;
            case R.id.ll_motionsetting_mapsetting:
                if (Util.SPORT_STATUS == 1) {
                    Toast.makeText(mContext, getString(R.string.sport_status), Toast.LENGTH_SHORT).show();
                } else {
                    showpop(v, 2);
                }

                break;

            case R.id.back:
                finish();

                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER,
                SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_motionsetting);
        shared = new StatusShared(getApplicationContext());
        voicelist = new String[]{
                getString(R.string.motionsettting_standard_girl),
                getString(R.string.motionsettting_standard_boy)};
        reciprocallist = new String[]{
                3 + getString(R.string.motionsettting_reciprocal_second),
                5 + getString(R.string.motionsettting_reciprocal_second),
                10 + getString(R.string.motionsettting_reciprocal_second)};
        reciprocallist2 = new String[]{
                3 + "",
                5 + "",
                10 + ""};
        maplist = new String[]{
                getString(R.string.motionsettting_general_map),
                getString(R.string.motionsettting_satellite_map)};
        maptowlist = new String[]{
                getString(R.string.motionsettting_high_german_map),
                getString(R.string.motionsettting_google_map)};
        mapnamelist = new String[]{
                getString(R.string.motionsettting_high_german),
                getString(R.string.motionsettting_google)};

        initview();
        // initviewdata();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        initviewdata();
    }

    private void initview() {
        // TODO Auto-generated method stub

        tv_motionsetting_goal = (TextView) findViewById(R.id.tv_motionsetting_goal);
        tv_motionsetting_voice_setting = (TextView) findViewById(R.id.tv_motionsetting_voice_setting);
        tv_motionsetting_reciprocal = (TextView) findViewById(R.id.tv_motionsetting_reciprocal);
        tv_motionsetting_mapsetting = (TextView) findViewById(R.id.tv_motionsetting_mapsetting);

        tv_runsetting_voice = (TextView) findViewById(R.id.tv_runsetting_voice);
        tv_runsetting_screen = (TextView) findViewById(R.id.tv_runsetting_screen);
        tv_runsetting_autopause = (TextView) findViewById(R.id.tv_runsetting_autopause);
        tv_runsetting_autostop = (TextView) findViewById(R.id.tv_runsetting_autostop);
        RelativeLayout ll_yuyin = (RelativeLayout) findViewById(R.id.ll_yuyin);
       /* ll_yuyin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,R.string.developed,Toast.LENGTH_SHORT).show();
            }
        });*/

        sportshistory_vibrator_setting = (TextView) findViewById(R.id.sportshistory_vibrator_setting);
        sportshistory_screen_always_on = (TextView) findViewById(R.id.sportshistory_screen_always_on);
        sportshistory_automatic_pause = (TextView) findViewById(R.id.sportshistory_automatic_pause);
        sportshistory_automatic_stop = (TextView) findViewById(R.id.sportshistory_automatic_stop);

        String languageLx = Utils.getLanguage();
        if (languageLx.equals("de")) {  // 德语
            sportshistory_vibrator_setting.setTextSize(10); // R.dimen.home_title_text_n
            sportshistory_screen_always_on.setTextSize(10);
            sportshistory_automatic_pause.setTextSize(10);
            sportshistory_automatic_stop.setTextSize(10);
        }

        cb_runsetting_voice = (CheckBox) findViewById(R.id.cb_runsetting_voice);  // 语音播报
        cb_runsetting_screen = (CheckBox) findViewById(R.id.cb_runsetting_screen); // 屏幕常亮
        cb_runsetting_autopause = (CheckBox) findViewById(R.id.cb_runsetting_autopause); // 自动暂停
        cb_runsetting_autostop = (CheckBox) findViewById(R.id.cb_runsetting_autostop);  // 自动停止

//        cb_runsetting_voice.setClickable(false);
        cb_runsetting_voice.setOnCheckedChangeListener(oncheckedchangelistener);
        cb_runsetting_screen
                .setOnCheckedChangeListener(oncheckedchangelistener);
        cb_runsetting_autopause
                .setOnCheckedChangeListener(oncheckedchangelistener);
        cb_runsetting_autostop
                .setOnCheckedChangeListener(oncheckedchangelistener);

        findViewById(R.id.ll_motionsetting_goal).setOnClickListener(this);
        findViewById(R.id.ll_motionsetting_mapsetting).setOnClickListener(this);
        findViewById(R.id.ll_motionsetting_reciprocal).setOnClickListener(this);
        findViewById(ll_motionsetting_voice_setting).setOnClickListener(
                this);
        findViewById(R.id.back).setOnClickListener(this);

    }

    OnCheckedChangeListener oncheckedchangelistener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.cb_runsetting_voice:       // 关闭振动
//                        Toast.makeText(mContext,R.string.developed,Toast.LENGTH_SHORT).show();  // 该功能正在开发中
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_VOICE,
                                SharedPreUtil.YES);
                        tv_runsetting_voice
                                .setText(getString(R.string.motionsettting_open));
                        break;
                    case R.id.cb_runsetting_screen:      // 关闭常亮
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_SCREEN,
                                SharedPreUtil.YES);
                        tv_runsetting_screen
                                .setText(getString(R.string.motionsettting_open));
                        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        break;
                    case R.id.cb_runsetting_autopause:  // 关闭自动暂停
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_AUTOPAUSE,
                                SharedPreUtil.YES);
                        tv_runsetting_autopause
                                .setText(getString(R.string.motionsettting_open));

                        Intent intent = new Intent(MainService.ACTION_SPORTMODE_AUTOPAUSE); //todo --- 发广播更新 自动暂停的的标志位
                        BTNotificationApplication.getInstance().sendBroadcast(intent);
                        break;
                    case R.id.cb_runsetting_autostop:   // 关闭自动停止
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_AUTOSTOP,
                                SharedPreUtil.YES);
                        tv_runsetting_autostop
                                .setText(getString(R.string.motionsettting_open));

                        Intent intent2 = new Intent(MainService.ACTION_SPORTMODE_AUTOSTOP); //todo --- 发广播更新 自动停止的的标志位
                        BTNotificationApplication.getInstance().sendBroadcast(intent2);
                        break;
                    default:
                        break;
                }
            } else {
                switch (buttonView.getId()) {
                    case R.id.cb_runsetting_voice:    // 打开振动开关
                        SharedPreUtil
                                .savePre(getApplicationContext(),
                                        SharedPreUtil.USER,
                                        SharedPreUtil.CB_RUNSETTING_VOICE,
                                        SharedPreUtil.NO);
                        tv_runsetting_voice
                                .setText(getString(R.string.motionsettting_close));

                        break;
                    case R.id.cb_runsetting_screen:   // 打开常亮
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_SCREEN,
                                SharedPreUtil.NO);
                        tv_runsetting_screen
                                .setText(getString(R.string.motionsettting_close));
                        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        break;
                    case R.id.cb_runsetting_autopause:    // 打开自动暂停
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_AUTOPAUSE,
                                SharedPreUtil.NO);
                        tv_runsetting_autopause
                                .setText(getString(R.string.motionsettting_close));

                        Intent intent = new Intent(MainService.ACTION_SPORTMODE_AUTOPAUSE); //todo --- 发广播更新 自动暂停的的标志位
                        BTNotificationApplication.getInstance().sendBroadcast(intent);
                        break;
                    case R.id.cb_runsetting_autostop:   // 打开自动停止
                        SharedPreUtil.savePre(getApplicationContext(),
                                SharedPreUtil.USER,
                                SharedPreUtil.CB_RUNSETTING_AUTOSTOP,
                                SharedPreUtil.NO);
                        tv_runsetting_autostop
                                .setText(getString(R.string.motionsettting_close));

                        Intent intent2 = new Intent(MainService.ACTION_SPORTMODE_AUTOSTOP); //todo --- 发广播更新 自动停止的的标志位
                        BTNotificationApplication.getInstance().sendBroadcast(intent2);

                        break;
                    default:
                        break;
                }

            }
        }
    };

    private void initviewdata() {
        // TODO Auto-generated method stub
        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
                SharedPreUtil.CB_RUNSETTING_VOICE).equals(
                SharedPreUtil.YES)) {
            cb_runsetting_voice.setChecked(true);
            tv_runsetting_voice
                    .setText(getString(R.string.motionsettting_open));
        } else {
            cb_runsetting_voice.setChecked(false);
            tv_runsetting_voice
                    .setText(getString(R.string.motionsettting_close));
        }
        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
                SharedPreUtil.CB_RUNSETTING_SCREEN).equals(
                SharedPreUtil.YES)) {

            cb_runsetting_screen.setChecked(true);
            tv_runsetting_screen
                    .setText(getString(R.string.motionsettting_open));
        } else {
            cb_runsetting_screen.setChecked(false);
            tv_runsetting_screen
                    .setText(getString(R.string.motionsettting_close));
        }

        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
                SharedPreUtil.CB_RUNSETTING_AUTOPAUSE)
                .equals(SharedPreUtil.YES)) {

            cb_runsetting_autopause.setChecked(true);
            tv_runsetting_autopause
                    .setText(getString(R.string.motionsettting_open));
        } else {

            cb_runsetting_autopause.setChecked(false);
            tv_runsetting_autopause
                    .setText(getString(R.string.motionsettting_close));
        }

        if (SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER,
                SharedPreUtil.CB_RUNSETTING_AUTOSTOP)
                .equals(SharedPreUtil.YES)) {

            cb_runsetting_autostop.setChecked(true);
            tv_runsetting_autostop
                    .setText(getString(R.string.motionsettting_open));
        } else {

            cb_runsetting_autostop.setChecked(false);
            tv_runsetting_autostop
                    .setText(getString(R.string.motionsettting_close));
        }
        int select = Utils.toint(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MotionGoal, "0"));
        switch (select) {
            case 0:
                tv_motionsetting_goal.setText(SharedPreUtil.readPre(
                        getApplicationContext(), SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_GOAL,
                        getString(R.string.free_running)));   // motionsettting_goal_freerun  9999999999999999
                break;
            case 1:
                if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))) {
                    tv_motionsetting_goal.setText(SharedPreUtil.readPre(getApplicationContext(),
                            SharedPreUtil.USER, SharedPreUtil.DISTANCEGOAL, "0.5") + getString(R.string.kilometer));  // "0.5" + getString(R.string.kilometer)
                }else{
                    tv_motionsetting_goal.setText(Utils.decimalTo2(Float.parseFloat(SharedPreUtil.readPre(getApplicationContext(),
                            SharedPreUtil.USER, SharedPreUtil.DISTANCEGOAL, "0.5")) * 0.621,1) + getString(R.string.unit_mi));
                }
                break;
            case 2:
                tv_motionsetting_goal.setText(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.TIMEGOAL, "10") +   getString(R.string.everyday_show_unit));
                break;
            case 3:
                if(SharedPreUtil.YES.equals(SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES))) {
                    tv_motionsetting_goal.setText(Math.round(Double.parseDouble(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.KALGOAL, "50"))) + getString(R.string.calories));
                }else{
                    tv_motionsetting_goal.setText(Math.round(Double.parseDouble(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.KALGOAL, "50")) * 4.18675) + getString(R.string.unit_kj));
                }
                break;
            default:
                break;
        }


        tv_motionsetting_voice_setting.setText(voicelist[Utils
                .toint(SharedPreUtil.readPre(getApplicationContext(),
                        SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_VOICE_SETTING, "0"))]);  // 语音设置

        tv_motionsetting_reciprocal.setText(reciprocallist[Utils
                .toint(SharedPreUtil.readPre(getApplicationContext(),
                        SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL, "0"))]);  // 倒数设置

        if (Utils.getisgooglemap(mContext)) {
            tv_motionsetting_mapsetting.setText(mapnamelist[Utils.toint(SharedPreUtil
                    .readPre(getApplicationContext(), SharedPreUtil.USER,
                            SharedPreUtil.TV_MOTIONSETTING_MAPTOWSETTING, "0"))] + maplist[Utils.toint(SharedPreUtil
                    .readPre(getApplicationContext(), SharedPreUtil.USER,
                            SharedPreUtil.TV_MOTIONSETTING_MAPSETTING, "0"))]);
        } else {
            tv_motionsetting_mapsetting.setText(maplist[Utils.toint(SharedPreUtil
                    .readPre(getApplicationContext(), SharedPreUtil.USER,
                            SharedPreUtil.TV_MOTIONSETTING_MAPSETTING, "0"))]);

        }


    }

    private void showpop(View v, final int s) {
        // TODO Auto-generated method stub

        View view = LayoutInflater.from(this).inflate(R.layout.pop_towmenu, null);
        wheelView = (WheelView) view.findViewById(R.id.wheell);
        wheelr = (WheelView) view.findViewById(R.id.wheelr);
        switch (s) {
            case 0:
                wheelView.setAdapter(new StrericWheelAdapter(voicelist));
                if (SharedPreUtil.readPre(getApplicationContext(),
                        SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_VOICE_SETTING, "0").equals(
                        "0")) {
                    wheelView.setCurrentItem(0);
                } else {
                    wheelView.setCurrentItem(1);
                }
                wheelr.setVisibility(View.GONE);
                break;
            case 1:
                wheelView.setAdapter(new StrericWheelAdapter(reciprocallist));
                if (SharedPreUtil.readPre(getApplicationContext(),
                        SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL, "0").equals("0")) {
                    wheelView.setCurrentItem(0);
                } else if (SharedPreUtil.readPre(getApplicationContext(),
                        SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL, "0").equals("1")) {
                    wheelView.setCurrentItem(1);
                } else {
                    wheelView.setCurrentItem(2);
                }
                wheelr.setVisibility(View.GONE);
                break;
            case 2:
                wheelView.setAdapter(new StrericWheelAdapter(maplist));
                if (SharedPreUtil.readPre(getApplicationContext(),
                        SharedPreUtil.USER,
                        SharedPreUtil.TV_MOTIONSETTING_MAPSETTING, "0").equals("0")) {
                    wheelView.setCurrentItem(0);
                } else {
                    wheelView.setCurrentItem(1);
                }
                if (Utils.getisgooglemap(mContext)) {
                    wheelr.setVisibility(View.VISIBLE);
                    wheelr.setAdapter(new StrericWheelAdapter(maptowlist));
                    if (SharedPreUtil.readPre(getApplicationContext(),
                            SharedPreUtil.USER,
                            SharedPreUtil.TV_MOTIONSETTING_MAPTOWSETTING, "0").equals("0")) {
                        wheelr.setCurrentItem(0);
                    } else {
                        wheelr.setCurrentItem(1);
                    }
                    wheelr.setCyclic(false);
                    wheelr.setInterpolator(new AnticipateOvershootInterpolator());
                } else {
                    wheelr.setVisibility(View.GONE);
                }

                break;
            default:
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
                                    SharedPreUtil
                                            .savePre(
                                                    getApplicationContext(),
                                                    SharedPreUtil.USER,
                                                    SharedPreUtil.TV_MOTIONSETTING_VOICE_SETTING,
                                                    wheelView.getCurrentItem() + "");
                                    tv_motionsetting_voice_setting
                                            .setText(voicelist[wheelView
                                                    .getCurrentItem()]);

                                    break;
                                case 1:
                                    SharedPreUtil
                                            .savePre(
                                                    getApplicationContext(),
                                                    SharedPreUtil.USER,
                                                    SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL,
                                                    wheelView.getCurrentItem() + "");
                                    tv_motionsetting_reciprocal
                                            .setText(reciprocallist[wheelView
                                                    .getCurrentItem()]);
                                    break;
                                case 2:
                                    SharedPreUtil
                                            .savePre(
                                                    getApplicationContext(),
                                                    SharedPreUtil.USER,
                                                    SharedPreUtil.TV_MOTIONSETTING_MAPSETTING,
                                                    wheelView.getCurrentItem() + "");
                                    if (Utils.getisgooglemap(mContext)) {
                                        SharedPreUtil
                                                .savePre(
                                                        getApplicationContext(),
                                                        SharedPreUtil.USER,
                                                        SharedPreUtil.TV_MOTIONSETTING_MAPTOWSETTING,
                                                        wheelr.getCurrentItem() + "");

                                        tv_motionsetting_mapsetting
                                                .setText(mapnamelist[wheelr
                                                        .getCurrentItem()] + maplist[wheelView
                                                        .getCurrentItem()]);
                                    } else {
                                        tv_motionsetting_mapsetting
                                                .setText(maplist[wheelView
                                                        .getCurrentItem()]);

                                    }
                                    break;
                                default:
                                    break;
                            }
                            mPopupWindow.dismiss();
                        }
                    }
                }
        );

        mPopupWindow = new
                PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.infopopwindow_anim_style);
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }
}
