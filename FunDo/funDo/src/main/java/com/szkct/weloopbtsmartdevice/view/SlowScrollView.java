package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.szkct.weloopbtsmartdevice.util.StringUtils;

/**
 * Created by kct on 2017/11/29.
 */
public class SlowScrollView extends ScrollView {

    private Scroller mScroller;

    public SlowScrollView(Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    public SlowScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public SlowScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
    }

    //调用此方法滚动到目标位置  duration滚动时间
    public void smoothScrollToSlow(int fx, int fy, int duration) {
        int dx = fx - getScrollX();//mScroller.getFinalX();  普通view使用这种方法
        int dy = fy - getScrollY();  //mScroller.getFinalY();
        smoothScrollBySlow(dx, dy, duration);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBySlow(int dx, int dy,int duration) {

        //设置mScroller的滚动偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy,duration);//scrollView使用的方法（因为可以触摸拖动）
//        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, duration);  //普通view使用的方法
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 滑动事件，这是控制手指滑动的惯性速度
     */
    @Override
    public void fling(int velocityY) {
//        super.fling(velocityY / 4);
//        super.fling(velocityY / 2);
//        super.fling(velocityY);
//        TelephonyManager mTm = (TelephonyManager) BTNotificationApplication.getInstance().getSystemService(TELEPHONY_SERVICE);
        String mtype = android.os.Build.MODEL;    // PIC-AL00   ----    VTR-AL00
        String mtyb = android.os.Build.BRAND;//手机品牌    HUAWEI   HUAWEI
//        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        Log.d("lq3", "手机品牌：" + mtyb + " mtype:" + mtype);
        if(!StringUtils.isEmpty(mtype) && !StringUtils.isEmpty(mtyb)){
            if((mtype.equalsIgnoreCase("PIC-AL00") || mtype.equalsIgnoreCase("VTR-AL00")) && mtyb.equalsIgnoreCase("HUAWEI")){
                super.fling(velocityY*6);
            }else {
                super.fling(velocityY/4);
            }
        }else {
            super.fling(velocityY/4);
        }

    }

}