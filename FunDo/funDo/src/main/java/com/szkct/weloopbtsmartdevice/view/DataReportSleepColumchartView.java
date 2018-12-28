package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.ReportSleepData;
import com.szkct.weloopbtsmartdevice.util.UTIL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataReportSleepColumchartView extends View {

	private static final int[] SECTION_COLORS = {Color.parseColor("#fdde51"), Color.parseColor("#E68208")};
	
	private int spacing = 0;
	private int pointSpacing = 0;
	private int mViewHeight;
	private int mViewWidth;
	private int startY;
	private int sleepTime;
	private int mPointRadius;
	
	private Context mContext;
	private Paint pointPaint, deepSleepPaint, lightSleepPaint;
	private TextPaint xCoorPaint;
	private RectF mRectF;

	private List<ReportSleepData> sleepValues = new ArrayList<ReportSleepData>();
	
	public DataReportSleepColumchartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	public DataReportSleepColumchartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public DataReportSleepColumchartView(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewHeight = h;
		mViewWidth = w;
		spacing = (w - UTIL.dip2px(getContext(), 43)) / 23;
		mPointRadius = spacing / 5;
		pointSpacing = (w - UTIL.dip2px(getContext(), 43)) / 12;
		startY = (int) (h - spacing * 2.5);

//		mViewHeight = h;
//		mViewWidth = w;
//		spacing = (w - UTIL.dip2px(getContext(), 43)) / 23;
//		mPointRadius = spacing / 5;
//		pointSpacing = (w - UTIL.dip2px(getContext(), 43)) / 23;    // 将睡眠改为 24小时
//		startY = (int) (h - spacing * 2.5);

		//////////////////////////////////// 计步
//		mViewHeight = h;
//		mViewWidth = w;
//		mPointRadius = (w - UTIL.dip2px(getContext(), 43)) / 23 / 5;
//		pointSpacing = (w - UTIL.dip2px(getContext(), 43)) / 23;
//		startY = (int) (h - pointSpacing * 2.5);
		//////////////////////////////////// 心率
//		mViewHeight = h;
//		mViewWidth = w;
//		mPointRadius = (w - UTIL.dip2px(getContext(), 43)) / 23 / 5;
//		pointSpacing = (w - UTIL.dip2px(getContext(), 53)) / 23;
//		startY = (int) (h - pointSpacing * 2.5);
		///////////////////////////////////////////////////////
		init();
	}
	
	private void init() {
		TypedArray array = mContext.obtainStyledAttributes(new int[]{R.attr.global_text_color});
		
		pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pointPaint.setColor(array.getColor(0, Color.parseColor("#ffffff")));
		
		xCoorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		xCoorPaint.setColor(array.getColor(0, Color.parseColor("#ffffff")));
		xCoorPaint.setTextAlign(Align.CENTER);
		xCoorPaint.setTextSize(UTIL.dip2px(mContext, 10));
		
		deepSleepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		deepSleepPaint.setColor(Color.parseColor("#4E42C8"));
		
		lightSleepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		lightSleepPaint.setColor(Color.parseColor("#645BB8"));
		
		mRectF = new RectF();
		
		array.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		double pointAlpha = 0.1;
		for (int i = 0; i < 13; i++) {
			if (i < 10) {
				pointAlpha += 0.1;
				pointPaint.setAlpha((int) (255 * 0.3 * pointAlpha));
			} else {
				pointPaint.setAlpha((int) (255 * 0.3));
			}
			if (i % 6 == 0) {
				xCoorPaint.setAlpha((int) (255 * 0.3));
				canvas.drawText(String.format(Locale.ENGLISH, "%02d:00", (21 + i < 24) ? (i + 21) : (i - 3)), UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - spacing, xCoorPaint);
			}
			canvas.drawCircle(UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - spacing * 2, mPointRadius, pointPaint);
		}

		/*for (int i = 0; i < 24; i++) {   // 将睡眠改为24小时
			if (i < 10) {
				pointAlpha += 0.1;
				pointPaint.setAlpha((int) (255 * 0.3 * pointAlpha));
			} else {
				pointPaint.setAlpha((int) (255 * 0.3));
			}
//			if (i % 6 == 0) {
//				xCoorPaint.setAlpha((int) (255 * 0.3));
//				canvas.drawText(String.format(Locale.ENGLISH, "%02d:00", (21 + i < 24) ? (i + 21) : (i - 3)), UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - spacing, xCoorPaint);
//			}

			if (i % 12 == 0) {
				xCoorPaint.setAlpha((int) (255 * 0.3));
				canvas.drawText(String.format(Locale.ENGLISH, "%02d:00", i), UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - pointSpacing, xCoorPaint);
			}
			if (i == 23) {
				canvas.drawText("23:59", UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - pointSpacing, xCoorPaint);
			}

			canvas.drawCircle(UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - spacing * 2, mPointRadius, pointPaint);
		}*/
		
		int startX = 0;
		int stopX = 0;
		for (int i = 0; i < sleepValues.size(); i++) {
			startX = (int) ((sleepValues.get(i).getStartTime() / 720f * (mViewWidth - UTIL.dip2px(getContext(), 43))) + UTIL.dip2px(getContext(), 21));
			stopX = (int) (sleepValues.get(i).getSleepTime() / 720f * (mViewWidth - UTIL.dip2px(getContext(), 43)) + startX);
			if (sleepValues.get(i).isDeepSleep()) {   // 深睡
				mRectF.set(startX, spacing * 2.5f, stopX, startY);
				canvas.drawRect(mRectF, deepSleepPaint);
			} else {         // 浅睡
				mRectF.set(startX, (startY / 2) + spacing, stopX, startY);
				canvas.drawRect(mRectF, lightSleepPaint);
			}
		}
		
		/*if (sleepTime > 720) {
			sleepTime = 720;
		}
		
		if (sleepTime != 0 && sleepValues.size() == 0) {
			int max = 720 - sleepTime;
			int start = (int) (Math.random() * max);
			startX = (int) ((start / 720f * (mViewWidth - UTIL.dip2px(getContext(), 43))) + UTIL.dip2px(getContext(), 21));
			stopX = (int) (sleepTime / 720f * (mViewWidth - UTIL.dip2px(getContext(), 43)) + startX);
			mRectF.set(startX, spacing * 2.5f, stopX, startY);
			canvas.drawRect(mRectF, lightSleepPaint);
		}*/
		
	}
	
	public void updateView(List<ReportSleepData> sleepValues) {
		this.sleepValues = sleepValues;
		postInvalidate();
	}

}
