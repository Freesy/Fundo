package com.szkct.weloopbtsmartdevice.view;



import java.util.ArrayList;
import java.util.List;




import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalChartView extends View {
	public final static int COLUMN_CHAR_VIEW_TYPE_SPORTS = 0;
	public final static int COLUMN_CHAR_VIEW_TYPE_SLEEP = 1;
	
	private static final int[] SECTION_COLORS = {Color.parseColor("#30d24a"), Color.parseColor("#eeec37"),Color.parseColor("#37eeea")};
	private int linesNum = 3;
	private int[] xCoordinate = {5,4};
	private List<Integer> barValues = new ArrayList<Integer>();
	private String[] coordinates;
	private String[] korh={"k","h"};
	private int lineshigth = 4;
	private static final float[] linelengt = {1000f,1f};
	private Paint linePaint, barPaint, barLinePaint;
	private TextPaint textPiant, xCoorPaint;
	private Typeface tfCondensed;
	private Rect rect;
	private int offset;
	private int startY = 0;
	private int change = 0;
	private int sportorsleep = 0;
	 
	public HorizontalChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
	}

	public HorizontalChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalChartView(Context context) {
		super(context);
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		offset = w / 40;
		startY = getHeight() - offset * 3;
		init();
	}
	
	private void init() {
		tfCondensed = BTNotificationApplication.getInstance().lanTingThinBlackTypeface;

		rect = new Rect();
		
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(Color.parseColor("#6A7179"));

		xCoorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		xCoorPaint.setColor(Color.parseColor("#b1b1b1"));
		xCoorPaint.setTextAlign(Align.RIGHT);
		xCoorPaint.setTextSize(offset * 1.0f);
		xCoorPaint.setTypeface(tfCondensed);

		barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setColor(Color.parseColor("#6A7179"));
		barPaint.setStyle(Style.FILL);
		
		barLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);  // HINTING_OFF  ANTI_ALIAS_FLAG
//		barLinePaint.setStrokeWidth(offset * 0.7f);//TODO 改小条的长短
		barLinePaint.setStyle(Style.STROKE);  // STROKE
		barLinePaint.setStrokeCap(Paint.Cap.ROUND);  // ROUND
		barLinePaint.setColor(Color.parseColor("#6A7179")); 
		
		textPiant = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPiant.setTextSize(offset * 1.3f);
		textPiant.setColor(Color.parseColor("#b1b1b1"));
		textPiant.setTextAlign(Align.CENTER);
		
		LinearGradient shader = new LinearGradient(0,startY/4, 0, startY, SECTION_COLORS, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.MIRROR);  
		barLinePaint.setShader(shader);
		barPaint.setShader(shader);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawLine(offset * 3, startY + offset, getWidth() - offset * 3, startY + offset, linePaint);
		/*if(sportorsleep==0){
			canvas.drawLine(offset * 3, (getHeight() - offset * 1.5f) / 4, getWidth() - offset * 3, (getHeight() - offset * 1.5f) / 4, linePaint);	
		}*/
		
		canvas.drawText("0", offset * 2.9f, getHeight() - offset * 1.4f, xCoorPaint);
		xCoordinate[sportorsleep] = (linesNum - 1) * lineshigth;
		
		if (xCoordinate[sportorsleep] > lineshigth) {
			for (int i = 0; i < linesNum - 1; i++) {
				canvas.drawLine(offset * 3,startY/ 4 + ((startY * 3 / 4) / (linesNum - 1)) * i-offset * 0.35f, getWidth() - offset * 3, startY / 4 + (startY * 3 / 4) / (linesNum - 1) * i-offset * 0.35f, linePaint);
			}
			for (int i = 0; i < linesNum - 1; i++) {
				canvas.drawText((xCoordinate[sportorsleep] - i * lineshigth) + korh[sportorsleep], offset * 2.7f, startY / 4 + ((startY * 3 / 4) / (linesNum - 1)) * i+0.5f*offset, xCoorPaint);
			}
		} else {
			canvas.drawText(xCoordinate[sportorsleep] + korh[sportorsleep], offset * 2.7f, startY / 3.8f, xCoorPaint);
		}
		
		if (change == 0) {
			oneMonth(canvas);
		} else if (change == 1) {
			sixMonth(canvas);
		} else if (change == 2) {
			allData(canvas);
		}
		
	}

	private void oneMonth(Canvas canvas) {		
		barLinePaint.setStrokeWidth(offset * 0.7f);
		for (int i = 0; i < barValues.size(); i++) {
			int top = (int) (startY - (float) (barValues.get(barValues.size() - 1 - i) / (xCoordinate[sportorsleep] * linelengt[sportorsleep])) * (startY * 3 / 4));

			if (barValues.get(barValues.size() - 1 - i) > 0) {
				canvas.drawLine((int) (offset * 3.80f + offset * i * 1.15f), top, (int) (offset * 3.80f + offset * i * 1.15f), startY, barLinePaint);
			}

			if ((i - 1) % 7 == 0) {
				canvas.drawCircle(offset * 3.8f + offset * i * 1.15f, startY + offset, offset / 8, linePaint);
				canvas.drawText(coordinates[4 - (i - 1) / 7], offset * 3.8f + offset * i * 1.15f, getHeight() - offset / 2, textPiant);
			}
			
		}
	}
	
	private void sixMonth(Canvas canvas) {
		barLinePaint.setStrokeWidth(offset * 1.4f);
		for (int i = 0; i < barValues.size(); i++) {
			int top = (int) (startY - (float) (barValues.get(barValues.size() - 1 - i) / (xCoordinate[sportorsleep] * linelengt[sportorsleep])) * (startY * 3 / 4));

			if (barValues.get(barValues.size() - 1 - i) > 0) {
				canvas.drawLine((int) (offset * 5.2f + offset * i * 6), top, (int) (offset * 5.2f + offset * i * 6), startY, barLinePaint);
			}

			canvas.drawCircle(offset * 5.2f + offset * i * 6, startY + offset, offset / 8, linePaint);
			canvas.drawText(coordinates[5 - i], offset * 5.2f + offset * i * 6, getHeight() - offset / 2, textPiant);
		}
	}
	
	private void allData(Canvas canvas) {
		barLinePaint.setStrokeWidth(offset * 0.7f);
		for (int i = 0; i < barValues.size(); i++) {
			int top = (int) (startY - (float) (barValues.get(barValues.size() - 1 - i) / (xCoordinate[sportorsleep] * linelengt[sportorsleep])) * (startY * 3 / 4));

			if (barValues.get(barValues.size() - 1 - i) > 0) {
				canvas.drawLine((int) (offset * 4 + offset * i * 2.9f), top, (int) (offset * 4 + offset * i * 2.9f), startY, barLinePaint);
			}

			canvas.drawCircle(offset * 4 + offset * i * 2.9f, startY + offset, offset / 8, linePaint);
			canvas.drawText(coordinates[11 - i], offset * 4.3f + offset * i * 2.9f, getHeight() - offset / 2, textPiant);
	    }
	}
	
	public void setDataShow(int sportorsleep,int change, int lineshigth, List<Integer> barValues, String[] coordinates) {
		this.sportorsleep = sportorsleep;
		this.change = change;
		this.lineshigth=lineshigth;
		this.barValues = barValues;
		this.coordinates = coordinates;
		postInvalidate();
	}
}
