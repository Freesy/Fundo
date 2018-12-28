package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.CustomDate;
import com.szkct.weloopbtsmartdevice.util.ActionBarSystemBarTint;
import com.szkct.weloopbtsmartdevice.util.CalendarViewAdapter;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.view.CalendarCard;
import com.szkct.weloopbtsmartdevice.view.CalendarCard.OnCellClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chendalin
 *         日历类activity
 *         说明：ActionBarActivit对于最新的sdk 20x版本以上已经被官方抛弃，不建议使用，先推荐使用AppCompatActivity
 */
public class CalendarAcitity extends Activity implements OnClickListener, OnCellClickListener {
    private static final String TAG = "CalendarAcitity";
    private ViewPager mViewPager;
    private int mCurrentIndex = 498;
    private CalendarCard[] mShowViews;
    private CalendarViewAdapter<CalendarCard> adapter;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
    private LinearLayout linearLayout;
    private int from = 0;

    //   private Toolbar mToolbar = null;
    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    private ImageView preImgBtn, nextImgBtn;
    private TextView monthText;
    Animation animation;
    LinearLayout calen_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        linearLayout = (LinearLayout) findViewById(R.id.calender_li);

        LinearLayout ll = (LinearLayout) this.findViewById(R.id.textl);
        android.view.ViewGroup.LayoutParams lp = ll.getLayoutParams();

        String mIntent = getIntent().getStringExtra("from");
        if ("" != mIntent && null != mIntent) {
            if (mIntent.equals("HealthFragment")) {
                from = 1;
            }
        }

        lp.height = Utils.getScreenWidth(this) * 6 / 7;
        mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
        preImgBtn = (ImageView) this.findViewById(R.id.btnPreMonth);
        nextImgBtn = (ImageView) this.findViewById(R.id.btnNextMonth);
        monthText = (TextView) this.findViewById(R.id.tvCurrentMonth);
        preImgBtn.setOnClickListener(this);
        nextImgBtn.setOnClickListener(this);
        //状态栏与标题栏一体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ActionBarSystemBarTint.ActionBarSystemBarTintTransparent(this, linearLayout, R.color.trajectory_bg);
        }
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, this);
        }
        adapter = new CalendarViewAdapter<CalendarCard>(views);
        setViewPager();
        calen_ll = (LinearLayout) this.findViewById(R.id.calen_ll);
        animation = AnimationUtils.loadAnimation(CalendarAcitity.this, R.anim.in_from_top);

        calen_ll.startAnimation(animation);
    }


    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(498);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPreMonth:   // 切换月份
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.btnNextMonth:  // 切换月份
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void clickDate(CustomDate date) {
        Log.e(TAG, "点击当日号数：" + date.getDay());
        Log.e(TAG, "点击当日的月份：" + date.getMonth());

        Date curDate = new Date(System.currentTimeMillis());// 获取系统当前时间年、月、日
        String curtime_str = getDateFormat.format(curDate);
        int day_int = Integer.valueOf(curtime_str.substring(8, 10)).intValue();
        int month_int = Integer.valueOf(curtime_str.substring(5, 7)).intValue();
        int year_int = Integer.valueOf(curtime_str.substring(0, 4)).intValue();

        SharedPreferences datePreferences = CalendarAcitity.this.getSharedPreferences("datepreferences", Context.MODE_PRIVATE);  // todo --- ???  MODE_PRIVATE
        Editor editor = datePreferences.edit();
        if (date.getYear() == year_int) {  // 传进来的年 = 系统当前的年
            if (date.getMonth() == month_int) {// 传进来的月 = 系统当前的月
                if (date.getDay() > day_int) {// 传进来的日期 > 系统当前的日期
                    Toast.makeText(CalendarAcitity.this, R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
                } else {
                    //跳转并显示这天
                    if (from == 0) {  // 传参的页面标识
                        editor.putInt("select_day", date.getDay());   //保存传递进来的时间
                        editor.putInt("select_month", date.getMonth());
                        editor.putInt("select_year", date.getYear());
                        editor.commit();
                    } else if (from == 1) {
                        editor.putInt("1_select_day", date.getDay());
                        editor.putInt("1_select_month", date.getMonth());
                        editor.putInt("1_select_year", date.getYear());
                        editor.commit();
                    }
                    finish();   // 将传递进来的日期保存 本地后 ，关闭 日历页面
                }
            } else if (date.getMonth() > month_int) {
                Toast.makeText(CalendarAcitity.this, R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
            } else {  // 传进来的月 < 系统当前的月
                //跳转并显示这天
                if (from == 0) {
                    editor.putInt("select_day", date.getDay());
                    editor.putInt("select_month", date.getMonth());
                    editor.putInt("select_year", date.getYear());
                    editor.commit();
                } else if (from == 1) {
                    editor.putInt("1_select_day", date.getDay());
                    editor.putInt("1_select_month", date.getMonth());
                    editor.putInt("1_select_year", date.getYear());
                    editor.commit();
                }
                finish();
            }
        } else if (date.getYear() < year_int) {// 传进来的年 < 系统当前年
            if (from == 0) {
                editor.putInt("select_day", date.getDay());
                editor.putInt("select_month", date.getMonth());
                editor.putInt("select_year", date.getYear());
                editor.commit();
            } else if (from == 1) {
                editor.putInt("1_select_day", date.getDay());
                editor.putInt("1_select_month", date.getMonth());
                editor.putInt("1_select_year", date.getYear());
                editor.commit();
            }
            finish();
        } else {
            Toast.makeText(CalendarAcitity.this, R.string.tomorrow_no, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void changeDate(CustomDate date) {
        if(Utils.isDe()){
            monthText.setText(date.month + "-" + date.year);
        }else{
            monthText.setText(date.year + "-" + date.month);
        }
    }

    /**
     * 计算方向
     *
     * @param arg0
     */
    private void measureDirection(int arg0) {
        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;

        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图  
    private void updateCalendarView(int arg0) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    public boolean onTouchEvent(MotionEvent event) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        linearLayout.setBackground(null);
        overridePendingTransition(0, R.anim.out_to_top);
    }
}
