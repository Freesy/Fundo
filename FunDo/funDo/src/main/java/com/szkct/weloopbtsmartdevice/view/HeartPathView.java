package com.szkct.weloopbtsmartdevice.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.szkct.weloopbtsmartdevice.data.HeartBean;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.view.LineChartView;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class HeartPathView<T> extends View {


    //View的宽高
    private int mWidth, mHeight=200;
    //路径色
    private int mPathColor = Color.parseColor("#76eae8");
    //路径paint
    private Paint mPaint;
    private Path mPath;
    //滑动N屏幕
    private int i = 0;
    private int position;

    private float mProgress;
    private  int DivideCount = 77;

    private ArrayList<String> mList = new ArrayList<>();
    private static Handler mHandler = new Handler();
    private int mMinY;
    private int mMaxY;
    private float mScaleY;
    private float mScaleX;

    public HeartPathView(Context context) {
        this(context, null);
    }

    public HeartPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDivideCount(int divideCount) {
        DivideCount = divideCount;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mPathColor);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setPathEffect(new CornerPathEffect(10));
        mPath = new Path();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        computeAxisX();
        converXOffset();

    }

    private void computeAxisX() {
        mScaleX = StringUtils.divideToFloat(mWidth, DivideCount, 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawHeart(canvas);
    }

    private void drawHeart(Canvas canvas) {
        int size= mList.size();
        for (int i = 0; i < size; i++) {
            String t = mList.get(i);
            if (i == 0) {
                mPath.moveTo(i*mScaleX, ecgConver(t));
            } else {
                mPath.lineTo(i*mScaleX, ecgConver(t));
            }
        }
        canvas.drawPath(mPath, mPaint);
    }

    //滑动屏幕距离次数
    public void setData(ArrayList<String> t) {
        if(t!=null&&t.size()>0)
        {
            mList.clear();
            mList.addAll(t);
            invalidate();
        }

    }

    private void computeAxisY(ArrayList<T> t) {
        mMinY = 0;
        mMaxY = 0;
        for (int i = 0; i < t.size(); i++) {
            HeartBean heartBean = (HeartBean) t.get(i);
            if (heartBean.getY() < mMinY) {
                mMinY = heartBean.getY();
            }
            if (heartBean.getY() >= mMaxY) {
                mMaxY = heartBean.getY();
            }
        }
        mScaleY = StringUtils.divideToFloat(mHeight, mMaxY - mMinY, 2);

    }
    private double mmPx = 6;
    int gain = 10;
    int dimension =350;
    private int ecgConver(String data){
        int value = 0;
        if(!Utils.isNumeric(data))
        {
            value = 0;
        }
        else
        {
            value = Integer.parseInt(data);
        }
        if(mmPx>0)
        {
            value = (int) (value*gain*mmPx/dimension/2+mHeight/2);
        }
        else
        {
            value = value*gain*6/dimension/2+mHeight/2;
        }
        Log.e("ecgConver", "ecgConver value="+value+"--data="+data);
        return value;
    }


    private void converXOffset(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //获取屏幕对角线的长度，单位:px
        int dd = dm.densityDpi;
        double diagonalMm = Math.sqrt(width * width + height * height) / dm.densityDpi;//单位：英寸
        diagonalMm = diagonalMm * 2.54 * 10;//转换单位为：毫米
        double diagonalPx = width * width + height * height;
        diagonalPx = Math.sqrt(diagonalPx);
        //每毫米有多少px
        double px1mm = diagonalPx / diagonalMm;
        mmPx = px1mm;
    }
}
