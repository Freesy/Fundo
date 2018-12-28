package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class HeartView extends View {

    //背景色
    private int mBackgroundColor = Color.parseColor("#000000");
    //大网格色
    private int mGridLineColor = Color.parseColor("#969696");
    //小网格色
    private int mSmallGridLineColor = Color.parseColor("#3f3f3f");
    //View的宽高
    private int mWidth, mHeight;
    //大网格大小
    private int mBigSize = 100;
    //小网格大小
    private int mSmallSize = 20;
    //大网格paint
    private Paint mGridPaint;
    //小网格paint
    private Paint mSmallGridPaint;

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setColor(mGridLineColor);
        mGridPaint.setStrokeWidth(2);

        mSmallGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallGridPaint.setColor(mSmallGridLineColor);
        mSmallGridPaint.setStrokeWidth(1);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
    }


    private void drawBackground(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);
        //竖线数
        int vNum = mWidth / mBigSize;
        for (int i = 0; i <= vNum; i++) {
            canvas.drawLine(i * mBigSize, 0, i * mBigSize, mHeight, mGridPaint);
        }
        //横线数
        int hNum = mHeight / mBigSize;
        for (int i = 0; i <= hNum; i++) {
            canvas.drawLine(0, i * mBigSize, mWidth, i * mBigSize, mGridPaint);
        }


        //小格竖线数
        int svNum = mWidth / mSmallSize;
        for (int i = 0; i <= svNum; i++) {
            canvas.drawLine(i * mSmallSize, 0, i * mSmallSize, mHeight, mSmallGridPaint);
        }
        //小格横线数
        int shNum = mHeight / mSmallSize;
        for (int i = 0; i <= shNum; i++) {
            canvas.drawLine(0, i * mSmallSize, mWidth, i * mSmallSize, mSmallGridPaint);
        }
    }
}
