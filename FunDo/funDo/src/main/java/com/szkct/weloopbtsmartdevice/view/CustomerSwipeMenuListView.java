package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.baoyz.swipemenulistview.SwipeMenuListView;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/9/13
 * 描述: ${VERSION}
 * 修订历史：
 */

public class CustomerSwipeMenuListView extends SwipeMenuListView{


    private GestureDetector mGestureDetector;

    public CustomerSwipeMenuListView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, onGestureListener);
    }

    public CustomerSwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, onGestureListener);
    }

    public CustomerSwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(context, onGestureListener);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                return true;
            }
            setParentScrollAble(false);
            return false;
        }
    };

    /**
     * 是否把滚动事件交给父ScrollView
     *
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {
        getParent().requestDisallowInterceptTouchEvent(!flag);
    }
}
