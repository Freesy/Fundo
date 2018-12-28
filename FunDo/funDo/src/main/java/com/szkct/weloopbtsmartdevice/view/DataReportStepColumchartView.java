package com.szkct.weloopbtsmartdevice.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.data.ChartViewCoordinateData;
import com.szkct.weloopbtsmartdevice.util.UTIL;

public class DataReportStepColumchartView extends View {

	private static final int[] SECTION_COLORS = {Color.parseColor("#fdde51"), Color.parseColor("#E68208")};
	
	private int pointSpacing = 0;
	private int mViewHeight;
	private int mViewWidth;
	private int startY;
	private int maxValue;
	private int mPointRadius;
	
	private Context mContext;
	private Paint pointPaint, barPaint;
	private TextPaint xCoorPaint;

	private List<ChartViewCoordinateData> stepBarValues = new ArrayList<ChartViewCoordinateData>();
	
	public DataReportStepColumchartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	public DataReportStepColumchartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public DataReportStepColumchartView(Context context) {
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
		mPointRadius = (w - UTIL.dip2px(getContext(), 43)) / 23 / 5;
		pointSpacing = (w - UTIL.dip2px(getContext(), 43)) / 23;
		startY = (int) (h - pointSpacing * 2.5);
		
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
		
		barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setStrokeCap(Cap.ROUND);
		barPaint.setStrokeWidth(pointSpacing / 6);
		
		LinearGradient shader = new LinearGradient(0, startY/4, 0, startY, SECTION_COLORS, new float[]{0, 1.0f}, Shader.TileMode.MIRROR);  
		barPaint.setShader(shader);
		
		array.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
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
				canvas.drawText(String.format(Locale.ENGLISH, "%02d:00", i), UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - pointSpacing, xCoorPaint);
			}
			if (i == 23) {
				canvas.drawText("23:59", UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - pointSpacing, xCoorPaint);
			}
			canvas.drawCircle(UTIL.dip2px(getContext(), 21) + i * pointSpacing, mViewHeight - pointSpacing * 2, mPointRadius, pointPaint);
		}
		
		for (int i = 0; i < stepBarValues.size(); i++) {
			if (maxValue != 0) {
				int barHeight = (int) ((stepBarValues.get(i).value / (float) maxValue) * (startY - pointSpacing));
				canvas.drawLine(UTIL.dip2px(getContext(), 21) + stepBarValues.get(i).x * pointSpacing, startY, UTIL.dip2px(getContext(), 21) + stepBarValues.get(i).x * pointSpacing, startY - barHeight, barPaint);
			}
		}
		
	}
	
	public void updateView(List<ChartViewCoordinateData> stepBarValues, int maxValue) {
		this.stepBarValues = stepBarValues;
		this.maxValue = maxValue;
		postInvalidate();
	}

}
