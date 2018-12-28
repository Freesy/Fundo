package com.szkct.weloopbtsmartdevice.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.login.Gdata;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;
import com.szkct.weloopbtsmartdevice.util.ShareUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.HeartPathView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.szkct.weloopbtsmartdevice.util.Utils.getScreenHeight;

public class HeartCheckDetailActivity extends AppCompatActivity implements View.OnClickListener {

//    private NestedScrollView sc_heart_detail;
    private ScrollView sc_heart_detail;
    private FrameLayout frag_heart_content;
    private FrameLayout fram_heart_detail;
    private ImageView mIvBack;
    private ImageView mIvAdd;
    private TextView mTvName;
    private TextView tv_2;
    private TextView tv_3;
    private TextView mTvHeight;
    private TextView mTvWeight;
    private TextView mTvHeart;
    private LinearLayout liner_title;
    private LinearLayout liner_heart_detail;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ShareUtil mShareUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            setTheme(R.style.KCTStyleWhite);
        } else {
            setTheme(R.style.KCTStyleBlack);
        }
        setContentView(R.layout.activity_heart_check_detail);
        mShareUtil = new ShareUtil(this);
        initView();
        initData();
        initListener();
        setUserInfor();
    }
    int mScreenHeight = 0;
    private void initView() {
        mScreenHeight = getScreenHeight(this);
        frag_heart_content = (FrameLayout) findViewById(R.id.frag_heart_content);
        fram_heart_detail = (FrameLayout) findViewById(R.id.frag_heart_detail);
        sc_heart_detail = (ScrollView) findViewById(R.id.sc_heart_detail);
//        sc_heart_detail = (NestedScrollView) findViewById(R.id.sc_heart_detail);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mTvName = (TextView) findViewById(R.id.tv_name);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        mTvHeight = (TextView) findViewById(R.id.tv_height);
        mTvWeight = (TextView) findViewById(R.id.tv_weight);
        mTvHeart = (TextView) findViewById(R.id.tv_heart);
        liner_heart_detail = (LinearLayout) findViewById(R.id.liner_heart_detail);
        liner_title = (LinearLayout) findViewById(R.id.liner_title);
    }

    private void setUserInfor()
    {
        if (Gdata.getMid() != Gdata.NOT_LOGIN) {
            String userName = Gdata.getPersonData().getUsername();
            if (!TextUtils.isEmpty(userName)) {
                mTvName.setText(userName);
            }
            initheightAndWeight();
        }
    }

    private void initheightAndWeight() {
        // TODO Auto-generated method stub
        if (SharedPreUtil.readPre(this, SharedPreUtil.USER,
                SharedPreUtil.METRIC).equals(SharedPreUtil.NO)) { // TODO --- 英制
//            tv_Metric.setText(getString(R.string.imperial_units));
            String ft, in, libs;
            if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT).equals("")) {
                if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT).equals("")) {
                    ft = Utils.metricToInchForft(170);
                } else {
                    ft = Utils.metricToInchForft(Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT)));
                }
                SharedPreUtil.savePre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT, ft);
            } else {
                ft = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT);
            }

            if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN).equals("")) {
                if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT).equals("")) {
                    in = Utils.metricToInchForin(170);
                } else {
                    in = Utils.metricToInchForin(Float.parseFloat(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT)));
                }
                SharedPreUtil.savePre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN, in);
            } else {
                in = SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN);
            }

            mTvHeight.setText(ft + " " + getString(R.string.imperial_foot) + in + " " + getString(R.string.imperial_inch));   // TODO --- 英制

//            tvHeight.setText(pv_ft.getValue() + " " + getString(R.string.imperial_foot) + pv_in.getValue() + " " + getString(R.string.imperial_inch));

            if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.WEIGHT_US).equals("")) {
                libs = Utils.kgTolb(60);
                mTvWeight.setText(libs + getString(R.string.imperial_pound));
                //tvWeight.setText("120 lbs");
                SharedPreUtil.savePre(this, SharedPreUtil.USER, SharedPreUtil.WEIGHT_US, libs);
            } else {
                mTvWeight.setText(SharedPreUtil.readPre(this,
                        SharedPreUtil.USER, SharedPreUtil.WEIGHT_US)
                        + " "
                        + getString(R.string.imperial_pound));
            }

            tv_2.setText(getResources().getString(R.string.my_height_in));
            tv_3.setText(getResources().getString(R.string.weight_in));
        } else {
//            tv_Metric.setText(getString(R.string.metric_units));
            if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT).equals("")) {
                mTvHeight.setText("170 " + getString(R.string.centimeter));
            } else {
                mTvHeight.setText(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.HEIGHT) + " " + getString(R.string.centimeter));
            }
            if (SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.WEIGHT).equals("")) {
                mTvWeight.setText("60 " + getString(R.string.kilogram));
            } else {
                mTvWeight.setText(SharedPreUtil.readPre(this, SharedPreUtil.USER, SharedPreUtil.WEIGHT) + " " + getString(R.string.kilogram));
            }


        }

    }

    int  ROWCOUNT = 77;
    private void initData() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        Intent intent = getIntent();
        Ecg ecg = (Ecg) intent.getSerializableExtra("ecgdetail");
        if(ecg != null&&!TextUtils.isEmpty(ecg.getEcgs()))
        {
            String[] ecgs = ecg.getEcgs().split(";");
            List<String> list = Arrays.asList(ecgs);
            String currentTime = simpleDateFormat1.format(Long.parseLong(ecg.getBinTime()));
            boolean isZear = list.size()%ROWCOUNT==0;
            int size = 0;
            if(isZear)
            {
                size = list.size()/ROWCOUNT;
            }
            else
            {
                size = list.size()/ROWCOUNT+1;
            }
            int itemMeasuredHeight =0;
            for(int i=0;i<size ;i++)
            {
                ArrayList<String> list_ = new ArrayList<>();
                if(i==size-1)
                {
                    if(isZear)
                    {
                        list_.addAll(list.subList(i*ROWCOUNT,i*ROWCOUNT+ROWCOUNT));
                    }
                    else
                    {
                        list_.addAll(list.subList(i*ROWCOUNT,list.size()));
                    }
                }
                else{
                    list_.addAll(list.subList(i*ROWCOUNT,i*ROWCOUNT+ROWCOUNT));
                }
                View view = layoutInflater.inflate(R.layout.heart_detail_item,null);
                int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(widthSpec, heightSpec);
                itemMeasuredHeight = view.getMeasuredHeight();//测量得到的textview的高
                liner_heart_detail.addView(view);
                HeartPathView heartPathView = view.findViewById(R.id.hpv_1);
                heartPathView.setDivideCount(ROWCOUNT);
                heartPathView.setData(list_);
                Log.e("initData", "initData measuredHeight="+itemMeasuredHeight);
            }
            mTvHeart.setText(Utils.getHeart(ecg)+"");
            int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            liner_title.measure(widthSpec, heightSpec);
            int titleHeight = liner_title.getMeasuredHeight();//测量得到的textview的高

            widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            liner_heart_detail.measure(widthSpec, heightSpec);
            int detail_height = liner_heart_detail.getMeasuredHeight();//测量得到的textview的高
            Log.e("initData", "initData mScreenHeight="+mScreenHeight);
            View view = layoutInflater.inflate(R.layout.ecg_params,null);
            widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(widthSpec, heightSpec);
            int configHeight = view.getMeasuredHeight();//测量得到的textview的高
            TextView tvSpeed = (TextView) view.findViewById(R.id.tv_speed);
            TextView tvIncrease = (TextView)  view.findViewById(R.id.tv_increase);
            TextView tv_time = (TextView)  view.findViewById(R.id.tv_time);
            int speed = (int) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.ECG_SPEED,25);
            int gain = (int) SharedPreUtil.getParam(this,SharedPreUtil.USER,SharedPreUtil.ECG_GAIN,10);
            tvSpeed.setText(speed+"");
            tvIncrease.setText(gain+"");
            if(!TextUtils.isEmpty(currentTime))
            {
                String[] times = currentTime.split(" ");
                tv_time.setText(times[0]);
            }
            int sum = detail_height+titleHeight+configHeight+50;

            if(mScreenHeight-sum>0&&itemMeasuredHeight>0)
            {
                size = (mScreenHeight-sum)/itemMeasuredHeight;
                for(int i=0;i<size;i++)
                {
                    View view_ = layoutInflater.inflate(R.layout.heart_detail_item,null);
                    liner_heart_detail.addView(view_);
                }
            }
            liner_heart_detail.addView(view);
        }
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
    }
    private boolean isRunning = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add:
                if (isRunning) {
                    return;
                }
                isRunning = true;
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
                        //execute the task
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        if (!NetWorkUtils.isConnect(HeartCheckDetailActivity.this)) {
                            Toast.makeText(HeartCheckDetailActivity.this, R.string.my_network_disconnected, Toast.LENGTH_SHORT).show();
                        } else {
							if(Utils.isFastClick()){
                            if(OnekeyShare.isShowShare){ // todo ---- 弹出分享框了
                                OnekeyShare.isShowShare = false;
//                                mShareUtil.showShare(MainService.PAGE_INDEX_ECG);
//                            mShareUtil.drawBitmapAndView(sc_heart_detail);
                            mShareUtil.showShare(sc_heart_detail);
//                            mShareUtil.showShare(null, this, false);
                            }
							}
                        }
                        isRunning = false;
//                    }
//                }, 1400);
                break;
        }
    }


}
