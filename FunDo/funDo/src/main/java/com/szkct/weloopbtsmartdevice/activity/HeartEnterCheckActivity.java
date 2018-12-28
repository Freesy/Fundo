package com.szkct.weloopbtsmartdevice.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Bean;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.EcgView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
public class HeartEnterCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_ecgView;
    private ImageView mIvBack;
    private ImageView mIvAdd;
    private ImageView mImgState;
    private TextView mTvState;
    private TextView mTvFinish;
    private TextView tv_repeat_check;
    private EcgView mEcgviews;
    private RelativeLayout relay_enter_check;
    private LinearLayout liner_finish;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int DELAY_TIME = 0;
//    int DELAY_TIME = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_heart_enter_check);
        EventBus.getDefault().register(this);
        gain = (int) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.ECG_GAIN,10);
        dimension = (int) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.ECG_DIMENSION,350);
        initView();
        initListener();
    }


    private void initView() {
        relay_enter_check = (RelativeLayout) findViewById(R.id.relay_enter_check);
        liner_finish = (LinearLayout) findViewById(R.id.liner_finish);
        iv_ecgView = (ImageView) findViewById(R.id.iv_ecgview);
        mEcgviews = (EcgView) findViewById(R.id.ecg);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mImgState = (ImageView) findViewById(R.id.iv_state);
        mTvState = (TextView) findViewById(R.id.tv_state);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        tv_repeat_check = (TextView) findViewById(R.id.tv_repeat_check);
        if(dimension>0&&gain>0)
        {
            mEcgviews.setDimension(dimension);
            mEcgviews.setGain(gain);
        }
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mTvFinish.setOnClickListener(this);
        tv_repeat_check.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add: {
                if (isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(this, HeartCheckHistoryActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tv_finish: {
                if (isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(this, HeartCheckDetailActivity.class);
                intent.putExtra("ecgdetail", mEcg);
                startActivity(intent);
                break;
            }
            case R.id.tv_repeat_check: {
                if(SharedPreUtil.readPre(HeartEnterCheckActivity.this, SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")) {
                    mEcgviews.setVisibility(View.GONE);
                    mImgState.setVisibility(View.VISIBLE);
                    liner_finish.setVisibility(View.GONE);
                    mTvState.setText(getResources().getString(R.string.heart_check_state_one));
                    iv_ecgView.setVisibility(View.GONE);
                }
                else
                {
                    mEcgviews.setVisibility(View.GONE);
                    mImgState.setVisibility(View.VISIBLE);
                    liner_finish.setVisibility(View.GONE);
                    iv_ecgView.setVisibility(View.GONE);
                    mTvState.setText(getResources().getString(R.string.heart_check_state_one));
                }
                break;
            }
        }
    }

    private void sendHeartActivityStatus(boolean isOn) {
        BluetoothMtkChat.getInstance().sendHeartStatus(isOn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        if (watch.equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
            sendHeartActivityStatus(true);
        }
        mEcgviews.setStop(false);
        if(liner_finish.getVisibility() == View.VISIBLE)
        {
            if(mEcgList!=null&&mEcgList.size()>0)
            {
                int size = mEcgList.size();
                int rowCount = mEcgviews.getRowCount();
                if(rowCount>0)
                {
                    if(mEcgList.size()>=rowCount)
                    {
                        mEcgviews.getEcg0Datas().clear();
                        mEcgviews.setSleepTime(1);
                        for(int i=size-rowCount;i<size;i++)
                        {
                            mEcgviews.addEcgData0(mEcgList.get(i));
                        }
                    }
                };
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String watch = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH);
        if (watch.equals("3") ||  SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.TEMP_WATCH).equals("3")) {
            sendHeartActivityStatus(false);
        }
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(1);
    }

    int gain = 0;
    int dimension = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(final String data) {
         parseHeartData(data);
    }

     Ecg mEcg = new Ecg();
     ArrayList<Integer> mEcgList = new ArrayList<>();

    @SuppressLint("LongLogTag")
    private  synchronized void parseHeartData(String data) {
        if (!TextUtils.isEmpty(data)) {
            String[] datalist = data.split(",");
            if (datalist.length < 3) {
                return;
            }
            if (datalist[0].equals("SEND")) {
                if (datalist[1].equals("13")&&isValid) {
                    String step = datalist[2];
                    String[] date = step.split("\\|");
                    final List<String> list1 = Arrays.asList(date);
                    if (mEcg != null) {
                        String heartsStr = mEcg.getHearts();
                        String ecgStr = mEcg.getEcgs();
                        if (!TextUtils.isEmpty(heartsStr) && !heartsStr.endsWith(";")) {
                            heartsStr = heartsStr + ";";
                        }
                        if (!TextUtils.isEmpty(ecgStr) && !ecgStr.endsWith(";")) {
                            ecgStr = ecgStr + ";";
                        }
                        int size = list1.size();
                        if (size > 0) {
                            String value = list1.get(0);
                            heartsStr = heartsStr + value;
                            mEcg.setHearts(heartsStr);
                        }
                        for (int i = 1; i < size; i++) {
//                            Log.e("onServiceEventMainThread", "onServiceEventMainThread i=" + i + "--list1.get(i)=" + list1.get(i));
                            String value = list1.get(i);
                            ecgStr = ecgStr + value + ";";
                            if (mImgState.getVisibility() == View.VISIBLE) {
                                mImgState.setVisibility(View.GONE);
                            }
                            if (mEcgviews.getVisibility() == View.GONE) {
                                mEcgviews.setVisibility(View.VISIBLE);
                            }
                            mEcgList.add(Integer.parseInt(value));
                            mEcgviews.addEcgData0(Integer.parseInt(value));
                        }
                        mEcg.setEcgs(ecgStr);
                    }
                }
            } else if (datalist[0].equals("SET")) {
                 if (datalist[1].equals("18")) {
                    int answer = Integer.parseInt(datalist[2]);
                    if (answer == 0) {
                        if(isValid)
                        {
                            mHandler.removeMessages(1);
                            mHandler.sendEmptyMessageDelayed(1,DELAY_TIME);
                        }
                    } else if (answer == 1) {
                        liner_finish.setVisibility(View.GONE);
                        mTvState.setText(getResources().getString(R.string.heart_check_state_two));
                        mEcg = new Ecg();
                        isValid = true;
                        mEcgList.clear();
                        mEcgviews.setSleepTime(30);
                        mIvAdd.setEnabled(false);
                        mIvAdd.setImageDrawable(getResources().getDrawable(R.drawable.history_disable));
                        mHandler.removeMessages(1);
                    }

                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(byte[] bytes) {
        if(bytes!=null&&bytes.length>3)
        {
            switch (bytes[2])
            {
                case BleContants.ECG_FINISH: {
                    if (bytes.length >= 6) {
                        if (bytes[5] == 1) {
                            isValid = true;
                            byte[] l2 = new L2Bean().L2Pack(BleContants.ECG_COMMAND, BleContants.ECG_START, new byte[]{1});  //72 & ble 查找设备
                            MainService.getInstance().writeToDevice(l2, true);
                            mEcg = new Ecg();
                            liner_finish.setVisibility(View.GONE);
//                            mImgState.setVisibility(View.GONE);
                            mTvState.setText(getResources().getString(R.string.heart_check_state_two));
                            mIvAdd.setEnabled(false);
                            mIvAdd.setImageDrawable(getResources().getDrawable(R.drawable.history_disable));
                            mEcgList.clear();
                        } else if (bytes[5] == 0) {
                            if(isValid)
                            {
                                mHandler.removeMessages(1);
                                mHandler.sendEmptyMessageDelayed(1,DELAY_TIME);
                            }
                        }
                    }
                    break;
                }
                case BleContants.ECG_CONTENT: {
                    parseBleData(bytes);
                    break;
                }
            }

        }
    }


    private  synchronized  void parseBleData(final byte[] bytes)
    {
                if (mEcg != null&&isValid) {
                    int length= bytes.length;
                    final short heart = (short) ((bytes[5]&0xff)<<8|(bytes[6]&0xff));
                    String hearts = mEcg.getHearts();
                    if(!TextUtils.isEmpty(hearts)&&!hearts.endsWith(";"))
                    {
                        hearts = hearts+";";
                    }
                    hearts = hearts+heart;
                    mEcg.setHearts(hearts);
                    int startIndex = 7;
                    if(length>=startIndex)
                    {
                        String str = "";
                        String ecgStr = mEcg.getEcgs();
                        if(!TextUtils.isEmpty(ecgStr)&&!ecgStr.endsWith(";"))
                        {
                            ecgStr = ecgStr+";";
                        }
                        for (int i=startIndex;i<length;i++)
                        {
                            if(i+1<=length-1)
                            {
                                final short value = (short) ((bytes[i]&0xff)<<8|(bytes[i+1]&0xff));
                                        if(mImgState.getVisibility() == View.VISIBLE)
                                        {
                                            mImgState.setVisibility(View.GONE);
                                        }
                                        if(mEcgviews.getVisibility() == View.GONE)
                                        {
                                            mEcgviews.setVisibility(View.VISIBLE);
                                        }
                                        mEcgviews.addEcgData0(value);
                                mEcgList.add((int) value);
                                str =str+value+",";
                                ecgStr = ecgStr+value+";";
                                i++;
                            }
                        }
                        mEcg.setEcgs(ecgStr);
                    }
                }
    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    checkFinish();
                    break;

            }
        }
    };

    boolean isValid = false;
    private void checkFinish()
    {
        liner_finish.setVisibility(View.VISIBLE);
        mTvState.setText(getResources().getString(R.string.heart_check_state_finish));
        if(mEcg!=null&&isValid)
        {
            mEcgviews.clearData();
            Calendar calendar = Calendar.getInstance();
            String currentDate = simpleDateFormat.format(calendar.getTime());
            mEcg.setBinTime(calendar.getTimeInMillis()+"");
            mEcg.setDate(currentDate);
            String mac = "";
            if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
                mac = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.MAC);
            } else {
                mac = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.SHOWMAC);
            }
            mEcg.setMac(mac);
            mIvAdd.setEnabled(true);
            mIvAdd.setImageDrawable(getResources().getDrawable(R.drawable.title_history));
            if(!TextUtils.isEmpty(mEcg.getEcgs()))
            {
                MainService.getInstance().saveEcgData(mEcg);
            }
        }
        isValid = false;
    }

    boolean isPause = false;
    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    public static void main(String[] args) throws ParseException {

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
}
