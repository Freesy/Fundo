package com.szkct.weloopbtsmartdevice.view;



import com.kct.fundo.btnotification.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HomepageCircleView extends View {

	private Paint percentPaint;


	private int percent;
	private int allLineColor;
	private int percentLineColorLow;
	private int percentLineColorHight;

	private int allLineWidth = 2;
	private int percentLineWidth = 7;
	private int lineHeight = 25;
	
	
	public HomepageCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	private void init(AttributeSet attrs, int defStyle) {
		
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShowPercentView, defStyle, 0);
		
		percent = a.getInt(R.styleable.ShowPercentView_percent, 0);
		allLineColor = a.getColor(R.styleable.ShowPercentView_allLineColor, Color.parseColor("#eaeded"));
		percentLineColorLow = a.getColor(R.styleable.ShowPercentView_percentLineColorLow, Color.parseColor("#5e5d63"));
		percentLineColorHight = a.getColor(R.styleable.ShowPercentView_percentLineColorHight, Color.parseColor("#0077fe"));

		a.recycle();

		percentPaint = new Paint();
		percentPaint.setAntiAlias(true);

	}
	private static final int[] SECTION_COLORS = {Color.parseColor("#34b5da"), Color.parseColor("#8eda34"),Color.parseColor("#dab534"),Color.parseColor("#da3434"),};
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int pointX = width / 2;
		int pointY = height / 2;

		percentPaint.setColor(allLineColor);
		percentPaint.setStrokeWidth(allLineWidth);

		float degrees = (float) (360.0 / 100);

		canvas.save();
		canvas.translate(0, pointY);
		canvas.rotate(-90, pointX, 0);
		for (int i = 0; i < 100; i++) {
			canvas.drawLine(0, 0, lineHeight, 0, percentPaint);
			canvas.rotate(degrees, pointX, 0);
		}
		canvas.restore();
		
		percentPaint.setColor(percentLineColorLow);
		canvas.save();
		canvas.translate(0, pointY);
		canvas.rotate(90, pointX, 0);
		/* LinearGradient gradient = new LinearGradient(0, 0, 100, 100, Color.parseColor("#ffffff"), Color.parseColor("#000000"), Shader.TileMode.MIRROR);  
		  percentPaint.setShader(gradient);  */
		//  LinearGradient shader = new LinearGradient(0, 0, 100, 100,
		//		  new int[]{Color.parseColor("#66ccff"), Color.parseColor("#ef0000"), Color.parseColor("#000000")},new float[]{0 , 0.5f, 1.0f}, TileMode.MIRROR);
		//  percentPaint.setShader(shader);
		
		  for (int i = 0; i <= percent; i++) {
			if (i < percent) {
				canvas.drawLine(0, 0, lineHeight, 0, percentPaint);
				canvas.rotate(degrees, pointX, 0);
			} else if (i == percent && percent != 0) {
				percentPaint.setColor(percentLineColorHight);
	
				  percentPaint.setStrokeWidth(percentLineWidth);
				canvas.drawLine(0, 0, lineHeight, 0, percentPaint);
				canvas.rotate(degrees, pointX, 0);
			}
			
		}
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int d = (width >= height) ? height : width;
		setMeasuredDimension(d, d);
	}

	public void setPercent(int percent) {
		this.percent = 80;
		postInvalidate();
	}
	
}






