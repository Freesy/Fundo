package com.szkct.weloopbtsmartdevice.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.CalendarAcitity;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.HeartCheckHistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeartCheckHistoryActivity extends AppCompatActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private ImageView mIvBack;
    private TextView mTvSelectTime;
    private ImageView mIvDown;
    private ImageView mIvUp;
    private SimpleDateFormat mDateFormat= Utils.setSimpleDateFormat("yyyy-MM-dd");;
    private String mCurrentTimeStr = "";
    private RecyclerView mRv;
    private HeartCheckHistoryAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_heart_check_history);
        initView();
        initTime();
//        initRV();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //日历界面返回结果显示
        calendarResult();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvSelectTime = (TextView) findViewById(R.id.curdate_tv);
        mIvDown = (ImageView) findViewById(R.id.data_bt_downturning);
        mIvUp = (ImageView) findViewById(R.id.data_bt_upturning);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initTime() {
        mTvSelectTime.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskLightCond);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        mCurrentTimeStr = mDateFormat.format(curDate);
        setCurDate(mCurrentTimeStr);
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mTvSelectTime.setOnClickListener(this);
        mIvDown.setOnClickListener(this);
        mIvUp.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.curdate_tv:
                Intent mIntent = new Intent(this, CalendarAcitity.class);
                mIntent.putExtra("from", "HealthFragment");
                startActivity(mIntent);
                break;
            case R.id.data_bt_downturning:
                if (isFastDoubleClick()) {
                    return;
                }
                dateDown();
                break;
            case R.id.data_bt_upturning:
                if (isFastDoubleClick()) {
                    return;
                }
                dateUp();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<Ecg> list = adapter.getData();
        if(list.size()>position)
        {
            Intent intent = new Intent(this, HeartCheckDetailActivity.class);
            intent.putExtra("ecgdetail",list.get(position));
            startActivity(intent);
        }
    }

    private void dateUp() {
        String time = mTvSelectTime.getText().toString();
        if (time.equals(mCurrentTimeStr)) {
            Toast.makeText(this, R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
        } else {
            setCurDate(UTIL.getAddDay(time));
        }
    }

    private void dateDown() {
        String time = mTvSelectTime.getText().toString();
        setCurDate(UTIL.getSubtractDay(time));
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

    private void calendarResult() {
        SharedPreferences datepreferences = getSharedPreferences("datepreferences", Context.MODE_PRIVATE);
        final int select_day = datepreferences.getInt("1_select_day", 0);
        final int select_month = datepreferences.getInt("1_select_month", 0);
        final int select_year = datepreferences.getInt("1_select_year", 0);

        String select_monthstr;
        String select_daystr;
        if ((select_day != 0) && (select_month != 0) && (select_year != 0)) {
            if (select_month < 10) {
                select_monthstr = "0" + select_month;
            } else {
                select_monthstr = String.valueOf(select_month);
            }
            if (select_day < 10) {
                select_daystr = "0" + select_day;
            } else {
                select_daystr = String.valueOf(select_day);
            }
            String select_date = select_year + "-" + select_monthstr + "-" + select_daystr;
//            mTvSelectTime.setText(select_date);
            setCurDate(select_date);
            // 清除缓存。
            SharedPreferences.Editor editor = datepreferences.edit();
            editor.remove("1_select_day");
            editor.remove("1_select_month");
            editor.remove("1_select_year");
            editor.commit();
        }
    }

    private void setCurDate(String time) {
        if (Utils.isDe()) {
            time = Utils.dateInversion(time);
        }
        String date = mTvSelectTime.getText().toString();
        if(!date.equals(time))
        {
            List<Ecg> list = Utils.getEcgList(time);
            if(mAdapter!=null)
            {
                mAdapter.getData().clear();
                mAdapter.getData().addAll(list);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                mAdapter = new HeartCheckHistoryAdapter(R.layout.item_heart_history2, list);
                mRv.setAdapter(mAdapter);
                mAdapter.setEmptyView(R.layout.empty_history, mRv);
            }
        }
        mTvSelectTime.setText(time);
    }

}
