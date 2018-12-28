package com.szkct.weloopbtsmartdevice.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.szkct.weloopbtsmartdevice.main.MainService.ISSYNWATCHINFO;
import static com.szkct.weloopbtsmartdevice.main.MainService.UNIT;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/9
 * 描述: ${VERSION}
 * 修订历史：
 */

public class UnitSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = UnitSettingActivity.class.getSimpleName();
    private Context mContext;
    private ImageView metric_iv,imperial_iv,celsius_iv,fahrenheit_iv;
    private TextView metric_tv,imperial_tv,celsius_tv,fahrenheit_tv;
    private String unit_measure,unit_temperature;

    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_unitset);
        mContext = this;

        EventBus.getDefault().register(this);

        metric_tv = (TextView) findViewById(R.id.metric_tv);         //公制
        imperial_tv = (TextView) findViewById(R.id.imperial_tv);     //英制
        celsius_tv = (TextView) findViewById(R.id.celsius_tv);       //摄氏度
        fahrenheit_tv = (TextView) findViewById(R.id.fahrenheit_tv); //华氏度


        metric_iv = (ImageView) findViewById(R.id.metric_iv);
        imperial_iv = (ImageView) findViewById(R.id.imperial_iv);
        celsius_iv = (ImageView) findViewById(R.id.celsius_iv);
        fahrenheit_iv = (ImageView) findViewById(R.id.fahrenheit_iv);


        metric_tv.setOnClickListener(this);
        imperial_tv.setOnClickListener(this);
        celsius_tv.setOnClickListener(this);
        fahrenheit_tv.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_save).setOnClickListener(this);

        initView();
 	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.metric_tv:
                metric_iv.setVisibility(View.VISIBLE);
                imperial_iv.setVisibility(View.GONE);
                unit_measure = SharedPreUtil.YES;
                break;
            case R.id.imperial_tv:
                metric_iv.setVisibility(View.GONE);
                imperial_iv.setVisibility(View.VISIBLE);
                unit_measure = SharedPreUtil.NO;
                break;
            case R.id.celsius_tv:
                celsius_iv.setVisibility(View.VISIBLE);
                fahrenheit_iv.setVisibility(View.GONE);
                unit_temperature = SharedPreUtil.YES;
                break;
            case R.id.fahrenheit_tv:
                celsius_iv.setVisibility(View.GONE);
                fahrenheit_iv.setVisibility(View.VISIBLE);
                unit_temperature = SharedPreUtil.NO;
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_save:
                SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,unit_measure);
                SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.UNIT_TEMPERATURE,unit_temperature);
                // todo ----  保存时发送单位设置的命令

                if(MainService.getInstance().getState() == MainService.STATE_CONNECTED) {
                    if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
						  if(ISSYNWATCHINFO) {
	                        if (UNIT) {
	                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
	                            if(!("V6").equals(device.getName())){
	                                L2Send.unitSetting();
	                            }
	                        }
                          }
                       
                    }else if(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")){
                        if(ISSYNWATCHINFO) {
                            if (UNIT) {
                                String unit = unit_measure.equals(SharedPreUtil.YES) ? "0" : "1";
                                String temp = unit_temperature.equals(SharedPreUtil.YES) ? "0" : "1";
                                BluetoothMtkChat.getInstance().synUnit(unit + "," + temp);
                            }
                        }else{
                            if (UNIT) {
                                String unit = unit_measure.equals(SharedPreUtil.YES) ? "0" : "1";
                                String temp = unit_temperature.equals(SharedPreUtil.YES) ? "0" : "1";
                                BluetoothMtkChat.getInstance().synUnit(unit + "," + temp);
                            }
                        }

                    }
                }
                EventBus.getDefault().post(new MessageEvent("update_unit"));
                // todo --- 添加提示
                if(ISSYNWATCHINFO) {
                    if(!UNIT) {      //TODO  不支持单位设置
                        Toast.makeText(BTNotificationApplication.getInstance(), getString(R.string.device_not_support), Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
                break;
        }
    }


    private void initView(){
        unit_measure = (String) SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES);
        unit_temperature = (String) SharedPreUtil.getParam(mContext,SharedPreUtil.USER,SharedPreUtil.UNIT_TEMPERATURE,SharedPreUtil.YES);

        if(unit_measure.equals(SharedPreUtil.YES)){
            metric_iv.setVisibility(View.VISIBLE);
            imperial_iv.setVisibility(View.GONE);
        }else{
            metric_iv.setVisibility(View.GONE);
            imperial_iv.setVisibility(View.VISIBLE);
        }

        if(unit_temperature.equals(SharedPreUtil.YES)){
            celsius_iv.setVisibility(View.VISIBLE);
            fahrenheit_iv.setVisibility(View.GONE);
        }else{
            celsius_iv.setVisibility(View.GONE);
            fahrenheit_iv.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if(event.getMessage().equals("update_unit")){
            initView();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
