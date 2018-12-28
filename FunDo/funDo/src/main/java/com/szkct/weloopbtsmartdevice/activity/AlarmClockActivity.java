package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.kct.fundo.btnotification.R;
import com.szkct.adapter.SMListViewAdapter;
import com.szkct.bluetoothgyl.BleContants;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.weloopbtsmartdevice.data.greendao.AlarmClockData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.AlarmClockDataDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.Query;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/6/28
 * 描述: ${VERSION}
 * 修订历史：
 */
public class AlarmClockActivity extends AppCompatActivity {

    private static final String TAG = AlarmClockActivity.class.getName();
    private SwipeMenuListView lv;                  //闹钟listView
    private ImageView iv_back, iv_add;
    private DBHelper db;
    private List<AlarmClockData> clockList;
    private SMListViewAdapter mSMListViewAdapter;
    public static final int REQUEST = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_alarmclock);

        EventBus.getDefault().register(this);
        initView();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(MessageEvent messageEvent){
        if(messageEvent.getMessage() != null && messageEvent.getMessage().equals("result_ok")){
            clockList.clear();
            Log.i(TAG,"onActivityResult");
            Query query = db.getAlarmClockDataDao().queryBuilder()
                    .where(AlarmClockDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            clockList.addAll(query.list());
            mSMListViewAdapter.notifyDataSetChanged();
            sendAndSave();
        }
    }

    private void initView() {

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        lv = (SwipeMenuListView) findViewById(R.id.listView_alarm_clock);
        if(db == null){
            db = DBHelper.getInstance(BTNotificationApplication.getInstance().getApplicationContext());
        }
        Query query = db.getAlarmClockDataDao().queryBuilder()
                .where(AlarmClockDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
        clockList = query.list();

        if(clockList != null && clockList.size() > 0) {
            for (int i = 0; i < clockList.size(); i++) {
                if(clockList.get(i).getCycle().equals("00000000") && clockList.get(i).getType().equals("1")){  //仅一次，开启
                    String alarm_time = clockList.get(i).getAlarm_time().split(" ")[1];
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    String time = simpleDateFormat.format(new Date());
                    String times = clockList.get(i).getAlarm_time();
                    try {
                        if (simpleDateFormat.parse(times).getTime() <= simpleDateFormat.parse(time).getTime()){
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            calendar.add(Calendar.DAY_OF_MONTH,1);
                            clockList.get(i).setAlarm_time(simpleDateFormatDay.format(calendar.getTime()) + " " + alarm_time);
                            clockList.get(i).setType("0");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        mSMListViewAdapter = new SMListViewAdapter(BTNotificationApplication.getInstance().getApplicationContext(),
                clockList,mListener);

        lv.setAdapter(mSMListViewAdapter);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_add.setOnClickListener(new View.OnClickListener() { // TODO --- 添加新的闹钟
            @Override
            public void onClick(View v) {
                if(clockList.size()<5){  //
                    goToNewAlarmActivity();
                }else {
                    Toast.makeText(AlarmClockActivity.this,R.string.max_clock,Toast.LENGTH_SHORT).show();  // 最多只能设置5组闹钟
                }
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlarmClockActivity.this, NewAlarmActivity.class);
                intent.putExtra("AlarmClock", clockList.get(position));
                startActivityForResult(intent,REQUEST);
            }
        });


        // 添加删除按钮
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(AlarmClockActivity.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(70));
                deleteItem.setTitle(getString(R.string.delete_data));
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(18);
                menu.addMenuItem(deleteItem);// add to menu

            }
        };

        lv.setMenuCreator(creator);

        // 删除按钮单击监听
        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//                if (clockList.get(position).getType().equals("1")) {    //删除一个已开启的闹钟   todo --- type --- 1:开启 0：关闭
                    if(MainService.getInstance().getState() == 3){
                        db.getAlarmClockDataDao().deleteByKey(clockList.get(position).getId());
                        clockList.remove(position);
                        mSMListViewAdapter.notifyDataSetChanged();
                        // 删除一个开启状态的闹钟
                        sendAndSave(); //发送闹钟设置指令
                    } else {
                        Toast.makeText(BTNotificationApplication.getInstance().getApplicationContext()
                                ,R.string.ble_not_connected,Toast.LENGTH_SHORT).show();
                    }

//                } else {
//                    db.getAlarmClockDataDao().deleteByKey(clockList.get(position).getId());
//                    clockList.remove(position);
//                    mSMListViewAdapter.notifyDataSetChanged();
//                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST && resultCode == RESULT_OK){
            clockList.clear();
            Log.i(TAG,"onActivityResult");
            Query query = db.getAlarmClockDataDao().queryBuilder()
                    .where(AlarmClockDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            clockList.addAll(query.list());
            mSMListViewAdapter.notifyDataSetChanged();
            sendAndSave();
        }else if(resultCode == RESULT_FIRST_USER && requestCode == REQUEST){
            clockList.clear();
            Log.i(TAG,"onActivityResult");
            Query query = db.getAlarmClockDataDao().queryBuilder()
                    .where(AlarmClockDataDao.Properties.Mac.eq(SharedPreUtil.readPre(getApplicationContext(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
            clockList.addAll(query.list());
            mSMListViewAdapter.notifyDataSetChanged();

            //todo --- add 0710
            sendAndSave();
        }
    }

    /**
     *  响应开启 | 关闭闹钟
     *  发送手表闹钟设置指令
     */
    private SMListViewAdapter.MyClickListener mListener = new SMListViewAdapter.MyClickListener() {

        @Override
        public void myOnClick(int position, View v) {

            switch (v.getId()) {
                case R.id.tb_alarm_clock:
                    if(MainService.getInstance().getState() != 3){
                        Toast.makeText(BTNotificationApplication.getInstance().getApplicationContext()
                                ,R.string.ble_not_connected,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(clockList.get(position).getType().equals("1")){   //已打开
                        AlarmClockData alarmClock = clockList.get(position);
                        alarmClock.setType("0");
                        db.updataAlarmClockData(alarmClock);
                        mSMListViewAdapter.notifyDataSetChanged();
                        sendAndSave();
                    }else{
                        int clockIndex = 0;
                        for (int i = 0; i < clockList.size(); i++) {
                            if(clockList.get(i).getType().equals("1")){
                                clockIndex++;
                            }
                        }
                        if(clockIndex >= 5){
                            Toast.makeText(BTNotificationApplication.getInstance().getApplicationContext()
                                    ,R.string.max_clock,Toast.LENGTH_SHORT).show();
                        }else{
                            AlarmClockData alarmClock = clockList.get(position);
                            if(alarmClock.getCycle().equals("00000000")){
                                String alarm_time = alarmClock.getAlarm_time().split(" ")[1];
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                                String time = simpleDateFormat.format(new Date());
                                String times = time.split(" ")[0] + " " + alarm_time;
                                try {
                                    if (simpleDateFormat.parse(times).getTime() < simpleDateFormat.parse(time).getTime()){
                                        Calendar calendar = Calendar.getInstance();
                                        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                        calendar.add(Calendar.DAY_OF_MONTH,1);
                                        alarmClock.setAlarm_time(simpleDateFormatDay.format(calendar.getTime()) + " " + alarm_time);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            alarmClock.setType("1");
                            db.updataAlarmClockData(alarmClock);
                            mSMListViewAdapter.notifyDataSetChanged();
                            sendAndSave();
                            Toast.makeText(BTNotificationApplication.getInstance().getApplicationContext()
                                    ,R.string.open_alarm_clock_success,Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private long currentTime;
    /**
     * 添加闹钟
     */
    private void goToNewAlarmActivity() {
        if ((System.currentTimeMillis() - currentTime) < 1000) return;
        currentTime = System.currentTimeMillis();
        Intent intent = new Intent(AlarmClockActivity.this, NewAlarmActivity.class);
        AlarmClockData alarmClockData = null;
        intent.putExtra("AlarmClock",alarmClockData);
        startActivityForResult(intent,REQUEST);
    }


    /**
     * 发送闹钟数据到手环
     */
    private void sendAndSave() {
        byte[] bytes = new byte[25];
        for (int i = 0; i < clockList.size(); i++) {
                bytes[i * 5] = Byte.parseByte(clockList.get(i).getTime().split(":")[0]);  // 17
                bytes[i * 5 + 1] =Byte.parseByte(clockList.get(i).getTime().split(":")[1]); // 20
                bytes[i * 5 + 2] = Utils.getFbyte(clockList.get(i).getCycle());
                Log.i(TAG,"闹钟重复: " + Utils.getFbyte(clockList.get(i).getCycle()));
                bytes[i * 5 + 3] =(byte) 1;     // 标签位  设置时传1
                bytes[i * 5 + 4] =Byte.parseByte(clockList.get(i).getType());
//            }
        }
        L2Send.sendNotify(BleContants.INSTALL_COMMAND, BleContants.INSTALL_ALARM_CLOCK, bytes);
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
