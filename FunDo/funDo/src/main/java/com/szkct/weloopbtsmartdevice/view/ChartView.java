package com.szkct.weloopbtsmartdevice.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.ChartViewCoordinateData;
import com.szkct.weloopbtsmartdevice.util.UTIL;

/**
 * Created by mengmeng on 2016/9/22.
 */
public class ChartView extends View {
    //画横纵轴
    private Paint mBorderPaint;

    //画折线图
    private Paint mPathPaint;

    private Path mPath;

    private float fontHeight;

    //画虚线
    private Path mDottedLinePath;

    //纵轴最大值
    private int maxValue;

    //纵轴分割数量
    private int dividerCount;

    //纵轴每个单位值
    private int perValue;

    //底部显示String(24小时的数据历史，每小时一个间隔。0：当前时间，4：4小时前·····以此类推)
    private String[] bottomStr = {"0","4","8","12","16","20","24"};

    //具体的值
    private float[] values = {50.0f, 30.5f, 60.2f, 25.5f};

    //底部横轴单位间距
    private float bottomGap;

    //左边纵轴间距
    private float leftGap;

    //画Y轴文字
    private TextPaint textPaint;

    private float padding = 20;

    private Shader mShader = null;

    //曲线渐变开始颜色
    private int mStartColor;

    //曲线渐变中间颜色
    private int mMidColor;

    //曲线渐变结束颜色
    private int mEndColor;
    
    private int pointSpacing = 0;
    private int mPointRadius;
    private int mViewHeight;
	private int mViewWidth;
	private Paint pointPaint;
	private Paint oneValuePaint;
	private TextPaint xCoorPaint;
	private int startY;
	private List<ChartViewCoordinateData> heartRateValues = new ArrayList<ChartViewCoordinateData>();

    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	mViewHeight = h;
		mViewWidth = w;
    	mPointRadius = (w - UTIL.dip2px(getContext(), 43)) / 23 / 5;
		pointSpacing = (w - UTIL.dip2px(getContext(), 53)) / 23;
		startY = (int) (h - pointSpacing * 2.5);
    }
    
    private void init(Context context, AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ChartView);

        maxValue = array.getInt(R.styleable.ChartView_maxValue,100);
        dividerCount = array.getInt(R.styleable.ChartView_dividerCount,10);

        mStartColor = array.getColor(R.styleable.ChartView_pathStartColor,Color.RED);  // WHITE
        mMidColor = array.getColor(R.styleable.ChartView_pathMidColor,Color.RED); // Color.RED   -1
        mStartColor = array.getColor(R.styleable.ChartView_pathEndColor,Color.RED);

        int lineColor = array.getColor(R.styleable.ChartView_lineColor, Color.RED);  // GRAY
        int textColor = array.getColor(R.styleable.ChartView_textColor, Color.RED);  // WHITE

        mBorderPaint = new Paint();
        mPathPaint = new Paint();
        textPaint = new TextPaint();
        mDottedLinePath = new Path();
        mPath = new Path();

        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(lineColor);
        mBorderPaint.setStrokeWidth(1.0f);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{1.0f, 1.0f}, 1));

        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(5);
        mPathPaint.setPathEffect(new CornerPathEffect(50));
        mPathPaint.setColor(Color.RED);  // WHITE


        textPaint.setColor(textColor);
        textPaint.setTextSize(UTIL.dip2px(getContext(), 12));
        textPaint.setAlpha((int) (255 * 0.3));

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pointPaint.setColor(textColor);
		
		oneValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		oneValuePaint.setColor(Color.parseColor("#FF0000"));  // todo  ----  EC3214  FC3530
		
		xCoorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		xCoorPaint.setColor(textColor);
		xCoorPaint.setTextAlign(Align.CENTER);
		xCoorPaint.setTextSize(UTIL.dip2px(getContext(), 10));
        
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode==MeasureSpec.EXACTLY&&heightMode==MeasureSpec.EXACTLY){
            setMeasuredDimension(widthSize,heightSize);
        }else if (widthMeasureSpec==MeasureSpec.EXACTLY){
            setMeasuredDimension(widthSize,widthSize);
        }else if (heightMeasureSpec==MeasureSpec.EXACTLY){
            setMeasuredDimension(heightSize,heightSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        fontHeight =(textPaint.getFontMetrics().descent-textPaint.getFontMetrics().ascent);
        bottomGap = (getWidth() - padding * 2)/(bottomStr.length + 1);
        leftGap = (getHeight() - fontHeight) / dividerCount;
        if(mMidColor != -1){
            mShader = new LinearGradient(0,0,0,getHeight(),new int[]{mStartColor,mMidColor,mEndColor},null,Shader.TileMode.CLAMP);
        } else {
            mShader = new LinearGradient(0,0,0,getHeight(),new int[]{mStartColor,mEndColor},null,Shader.TileMode.CLAMP);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bottomStr == null || bottomStr.length == 0){
            return;
        }

        perValue = 220 / 7;
        
        mPathPaint.setShader(mShader);
        for (int i = 1;i <= 7; i++){
        	canvas.drawText(30 * (i - 1) + 40 + "", UTIL.dip2px(getContext(), 10), startY - (i - 1) * (1 / 6f) * (startY - pointSpacing * 1.2f), textPaint);
        }
        
        double pointAlpha = 0.1;
		for (int i = 0; i < 24; i++) {
			if (i < 10) {
				pointAlpha += 0.1;
				pointPaint.setAlpha((int) (255 * 0.3 * pointAlpha));
			} else if (i > 14) {
				pointAlpha -= 0.1;
				pointPaint.setAlpha((int) (255 * 0.3 * pointAlpha));
			} else {
				pointPaint.setAlpha((int) (255 * 0.3));
			}
			if (i % 12 == 0) {
				xCoorPaint.setAlpha((int) (255 * 0.3));
				canvas.drawText(String.format(Locale.ENGLISH, "%02d:00", i), UTIL.dip2px(getContext(), 34) + i * pointSpacing, mViewHeight - pointSpacing, xCoorPaint);
			}
			if (i == 23) {
				canvas.drawText("23:59", UTIL.dip2px(getContext(), 34) + i * pointSpacing, mViewHeight - pointSpacing, xCoorPaint);
			}
			canvas.drawCircle(UTIL.dip2px(getContext(), 34) + i * pointSpacing, mViewHeight - pointSpacing * 2, mPointRadius, pointPaint);
		}
        
        /**
         * 画轨迹
         * y的坐标点根据 y/leftGap = values[i]/perValue 计算
         *
         */
		boolean isFirst = false;
		if (heartRateValues.size() == 1) {
			int pointHeight = (int) (((heartRateValues.get(0).value - 40) / 180f) * ((startY - pointSpacing)));
			canvas.drawCircle(UTIL.dip2px(getContext(), 34) + heartRateValues.get(0).x * pointSpacing, startY - pointHeight, mPointRadius, oneValuePaint);
		} else {
			for (int i = 0; i < heartRateValues.size(); i++) {
				if (heartRateValues.get(i).value != 0) {
					int pointHeight = (int) (((heartRateValues.get(i).value - 40) / 180f) * ((startY - pointSpacing)));
					if (i == 0 || isFirst) {
						mPath.moveTo(UTIL.dip2px(getContext(), 34) + heartRateValues.get(i).x * pointSpacing, startY - pointHeight);
						isFirst = false;
					} else {
						mPath.lineTo(UTIL.dip2px(getContext(), 34) + heartRateValues.get(i).x * pointSpacing, startY - pointHeight);
					}
				} else {
					if (i != 0)
						isFirst = true;
				}
			}
	        canvas.drawPath(mPath, mPathPaint);
		}
		
    }

    public void setValues(List<ChartViewCoordinateData> heartRateValues) {
        this.heartRateValues = heartRateValues;
		mPath = new Path();
        postInvalidate();
    }

}
