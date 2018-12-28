package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.view.View;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/1/4
 * 描述: ${VERSION}
 * 修订历史：
 */

public class PointerCalibrationView extends View{

    //圆，指针，刻度画笔
    private  Paint mPaint;

    //半径
    public float mRadius;


    // 外圆的宽度
    public float mCircleWidth;

    //控件宽度
    public int mWidth;

    //控件高度
    public int mHeght;

    private int hours;

    private int minutes;

    private int seconds;

    public PointerCalibrationView(Context context) {
        this(context,null);
    }

    public PointerCalibrationView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PointerCalibrationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCircleWidth= 5 ;

        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec),measureSize(heightMeasureSpec));
    }

    private int measureSize(int mMeasureSpec) {
        int result;
        int mode=MeasureSpec.getMode(mMeasureSpec);
        int size=MeasureSpec.getSize(mMeasureSpec);
        if(mode==MeasureSpec.EXACTLY){
            result=size;
        }else{
            result=600;
            if(mode==MeasureSpec.AT_MOST){
                result=Math.min(result,size);
            }
        }
        return  result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置宽高、半径
        mWidth= (getMeasuredWidth()-getPaddingLeft()-getPaddingRight()) / 2;
        mHeght= (getMeasuredHeight()-getPaddingBottom()-getPaddingTop()) / 2;
        mRadius= mWidth / 2;
        //首先绘制圆
        drawCircle(canvas);
        //绘制刻度
        drawScale(canvas);
        //绘制指针
        drawPointer(canvas);
    }

    /**
     * 画圆
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#9F9FA0"));
        canvas.drawCircle(mWidth,mHeght,mRadius,mPaint);
    }

    /**
     * 刻度和文字
     * @param canvas
     */
    private void drawScale(Canvas canvas) {

        for (int i=0;i<60;i++){
            if (i % 5 == 0) {
                mPaint.setStrokeWidth(4);
                mPaint.setColor(Color.parseColor("#9F9FA0"));
                canvas.drawLine(mWidth,mHeght-mWidth/2+mCircleWidth/2,
                        mWidth,mHeght-mWidth/2+mCircleWidth/2+40,mPaint);
            }else{
                mPaint.setColor(Color.parseColor("#9F9FA0"));
                mPaint.setStrokeWidth(2);
                canvas.drawLine(mWidth,mHeght-mWidth/2+mCircleWidth/2+10,
                        mWidth,mHeght-mWidth/2+mCircleWidth/2+30,mPaint);
            }
            canvas.rotate(6,mWidth,mHeght);
        }
    }


    /**
     * 绘制指针
     * @param canvas
     */
    private void drawPointer(Canvas canvas) {

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //绘制时针
        canvas.save();
        mPaint.setColor(Color.parseColor("#9F9FA0"));
        mPaint.setStrokeWidth(7);
        //这里计算时针需要旋转的角度 实现原理是计算出一共多少分钟除以60计算出真实的小时数（带有小数，为了更加准确计算度数），已知12小时是360度，现在求出了实际小时数比例求出角度
        //Float hoursAngle = (hours * 60 + minutes) / 60f / 12f  * 360;
        Float hoursAngle = hours * 30 + minutes / 60f * 30;
        canvas.rotate(hoursAngle, mWidth, mHeght);
        canvas.drawLine(mWidth, mHeght - mWidth/2f*0.5f, mWidth, mHeght - 25, mPaint);
        canvas.restore();


        //绘制分针
        canvas.save();
        mPaint.setColor(Color.parseColor("#9F9FA0"));
        mPaint.setStrokeWidth(5);
        //这里计算分针需要旋转的角度  60分钟360度，求出实际分钟数所占的度数
        //Float minutesAngle = (minutes*60 + seconds) / 60f/ 60f * 360;
        Float minutesAngle = minutes * 6f;
        canvas.rotate(minutesAngle, mWidth, mHeght );
        canvas.drawLine(mWidth, mHeght -  mWidth/2f*0.7f, mWidth, mHeght - 25, mPaint);
        canvas.restore();

        //绘制中间的圆圈
        canvas.save();
        mPaint.setColor(Color.parseColor("#9F9FA0"));
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mWidth,mHeght,14,mPaint);
        canvas.restore();


        //绘制秒针
        /*canvas.save();
        mPaint.setColor(mSecondHandColor);
        mPaint.setStrokeWidth(mSecondHandWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        //这里计算秒针需要旋转的角度  60秒360度，求出实际秒数所占的度数
        Float secondAngle = seconds/60f*360;
        canvas.rotate(secondAngle, mWidth, mHeght);
        canvas.drawLine(mWidth, mHeght -  mWidth/2f*0.8f, mWidth, mHeght - 25 , mPaint);
        canvas.restore();*/


    }


    public void setTime(int hour,int minute,int second){
        this.hours = hour;
        this.minutes = minute;
        this.seconds = second;
        invalidate();
    }
}
