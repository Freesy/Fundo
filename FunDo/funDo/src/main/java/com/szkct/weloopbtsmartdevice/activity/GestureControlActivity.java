package com.szkct.weloopbtsmartdevice.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

/**
 * ================================================
 * Created by Tody Chou
 * Created on 2017/6/15 20:40
 * Version：1.0
 * Description：
 * Revision History：
 * ================================================
 */
public class GestureControlActivity extends AppCompatActivity {

    private RelativeLayout rl_raise_bright,rl_fanwan_bright;
    private ToggleButton tb_raise_bright, tb_fanwan_bright;
    private ImageView iv_back, iv_ok;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
            setTheme(R.style.KCTStyleWhite);
        }else{
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_gesture_control);

        initView();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_ok = (ImageView) findViewById(R.id.iv_ok);

        tb_raise_bright = (ToggleButton) findViewById(R.id.tb_raise_bright);
        tb_fanwan_bright = (ToggleButton) findViewById(R.id.tb_fanwan_bright);
        rl_raise_bright = (RelativeLayout) findViewById(R.id.rl_raise_bright);
        rl_fanwan_bright = (RelativeLayout) findViewById(R.id.rl_fanwan_bright);

        iv_back.setOnClickListener(new MyOnClickListener());
        iv_ok.setOnClickListener(new MyOnClickListener());

        rl_raise_bright.setOnClickListener(new MyOnClickListener());
        rl_fanwan_bright.setOnClickListener(new MyOnClickListener());

        tb_raise_bright.setChecked((Boolean) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.RAISE_BRIGHT,false));
        tb_fanwan_bright.setChecked((Boolean) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.FANWAN_BRIGHT,false));
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
                case R.id.rl_raise_bright:
                    setTb(tb_raise_bright);
                    break;
                case R.id.rl_fanwan_bright:
                    setTb(tb_fanwan_bright);
                    break;
            }
        }
    }

    private void setTb(ToggleButton tb) {
        tb.setChecked(!tb.isChecked());
    }

    private void saveAndSend() {
        SharedPreUtil.setParam(this,SharedPreUtil.USER,SharedPreUtil.RAISE_BRIGHT,tb_raise_bright.isChecked());
        SharedPreUtil.setParam(this,SharedPreUtil.USER,SharedPreUtil.FANWAN_BRIGHT,tb_fanwan_bright.isChecked());
        byte[] bytes = new byte[3];
        bytes[0] = (byte) 0;
        bytes[1] = (byte) (tb_raise_bright.isChecked()?1:0);
        bytes[2] = (byte) (tb_fanwan_bright.isChecked()?1:0);

        L2Send.sendNotify(BleContants.DEVICE_COMMADN, BleContants.GESTURE, bytes);
        finish();
    }



}
