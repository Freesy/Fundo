package com.szkct.weloopbtsmartdevice.view;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

public class ColumnChartView extends View {
	public final static int COLUMN_CHAR_VIEW_TYPE_SPORTS = 0;
	public final static int COLUMN_CHAR_VIEW_TYPE_SLEEP = 1;
	
	private static final int[] SECTION_COLORS = {Color.parseColor("#37eeea"), Color.parseColor("#eeec37"),Color.parseColor("#30d24a")};
	private static final int[] SECTION_COLORS_SLEEP = {Color.parseColor("#37eeea"), Color.parseColor("#3791ee"),Color.parseColor("#4330d2")};
	private  final String[] SHOW_STRING = {"k","h"};
	private  final float[] SHOW_INTEND = {1000f,60f};
	private  final int[] SHOW_INT = {5,10,15,20,25,30,35,40,45,50};
	private  final int[] SHOW_MINLANE = {2,3};
	private String[] weeks = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
	private int linesNum = 2;
	private int xCoordinate = 5;
	
	private List<Integer> barValues = new ArrayList<Integer>();

	private Context mContext;
	private Paint linePaint, barPaint,brokenlinePaint;
	private TextPaint textPaint, stepTextPiant, xCoorPaint;
	private Typeface tfCondensed;
	private Rect rect;
	private Rect textRect = null;
	private String valueString = null;
	private Rect valueRect = null;
	private int startX = 0;
	private int startY = 0;
	private int barWidth = 0;
	private int barHeight = 0;
	private int uintWidth = 0;
	private int offset;
	private int showItem = -1;
	private int startDownX = 0;
	private int startDownY = 0;
	private int type = COLUMN_CHAR_VIEW_TYPE_SPORTS;
	private LinearGradient shader = null;
	private int barValue = 0;
	private int sportMaxType = 1;
	private int sportMinType = 0;
	
	public ColumnChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ColumnChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ColumnChartView(Context context) {
		super(context);

	}
	public ColumnChartView(Context context,int type) {
		super(context);
		this.type=type;
	}
	public void setviewtype(int type) {
	
		this.type=type;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		offset = w / 20;
		uintWidth = w / (weeks.length + 1);
		startX = (w - weeks.length * uintWidth)/2;
		startY = h;
		init();

	}

	@SuppressWarnings("ResourceType")
	private void init() {
		tfCondensed = BTNotificationApplication.getInstance().akzidenzGroteskMediumCondAlt;
		rect = new Rect();
		valueRect = new Rect();
		
		TypedArray a = mContext.obtainStyledAttributes(new int[] {  
                R.attr.analysis_fragment_y_text_color, R.attr.analysis_fragment_line_color, R.attr.analysis_fragment_x_text_color}); 
		
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(a.getColor(1,Color.parseColor("#4c5157")));
		linePaint.setStyle(Paint.Style.STROKE);
		
		brokenlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		brokenlinePaint.setColor(a.getColor(1,Color.parseColor("#4c5157")));
		brokenlinePaint.setStyle(Paint.Style.STROKE);
		  
		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5}, 1);  
		brokenlinePaint.setPathEffect(effects);  
		
		xCoorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		xCoorPaint.setColor(a.getColor(0, Color.parseColor("#ffffff")));
		xCoorPaint.setTextAlign(Align.RIGHT);
		xCoorPaint.setTextSize(uintWidth*2/5);
		xCoorPaint.setTypeface(BTNotificationApplication.getInstance().akzidenzGroteskLightCond);

		String language = Utils.getLanguage();
		textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

		if (language.equals("ru") || language.equals("fr")) {  // en
			textPaint.setTextSize(uintWidth/4);
		}else {
			textPaint.setTextSize(uintWidth/3);
		}

//		textPaint.setTextSize(uintWidth/3);
		textPaint.setColor(a.getColor(2,Color.parseColor("#37eeea")));
		textPaint.setTypeface(tfCondensed);
		
		
		stepTextPiant = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		stepTextPiant.setTextSize(uintWidth*2/5); 
		stepTextPiant.setColor(Color.parseColor("#b1b1b1"));
		stepTextPiant.setTextAlign(Align.CENTER);
		stepTextPiant.setTypeface(tfCondensed);

		textRect = new Rect(0, 0, 0, 0);
		if(weeks!=null && weeks.length>0){
			textPaint.getTextBounds(weeks[0], 0, weeks[0].length(), textRect);
		}else{
			textPaint.getTextBounds("一", 0, "一".length(), textRect);
		}
		startY = getHeight() - textRect.height()*3;
		barWidth = textRect.width();
		barHeight = startY*2/3;		
  
		   
		barPaint = new Paint();
		barPaint.setAntiAlias(true);
		barPaint.setStrokeWidth(1);//TODO 改小条的长短
		barPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		barPaint.setColor(Color.parseColor("#6A7179")); 
		if(shader==null){
			if(type==0){
				shader = new LinearGradient(0,startY, 0, startY-barHeight-textRect.height() , SECTION_COLORS, new float[]{0 , 0.5f, 1.0f},
		                Shader.TileMode.MIRROR);
			}else{
				shader = new LinearGradient(0,startY, 0, startY-barHeight-textRect.height() , SECTION_COLORS_SLEEP, new float[]{0 , 0.5f, 1.0f},
		                Shader.TileMode.MIRROR);
			}
		}
		barPaint.setShader(shader);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = startX + barWidth / 2;
		int y = startY;
		linePaint.setStrokeWidth(textRect.height()/4);
		
		int uintY = barHeight / (linesNum - 1);
		int offsetLine = textRect.height() / 3;

		for (int i = 0; i < linesNum; i++) {
			canvas.drawText(i * SHOW_INT[sportMinType] + SHOW_STRING[type], x - barWidth / 2, y + offsetLine + textRect.height() / 2 - i * uintY, xCoorPaint);  // 8 h

			canvas.drawLine(x - textRect.width() / 4, y + offsetLine - i * uintY, x + uintWidth * weeks.length - offsetLine * 2, y + offsetLine - i * uintY, linePaint);

			if (i == 1 && type==1) {  // i == 1 && type==1
				canvas.drawText(8 + SHOW_STRING[type], x - barWidth / 2, y + offsetLine + textRect.height() / 2 - 5/3f * uintY, xCoorPaint);
				
				Path path = new Path();
				path.moveTo(x-textRect.width() / 4, y + offsetLine - 5/3f*uintY);
				path.lineTo(x+uintWidth*weeks.length-offsetLine*2,y + offsetLine- 5/3f*uintY);

		        canvas.drawPath(path, brokenlinePaint);
			}
			if(i==0){
				linePaint.setStrokeWidth(2);
				offsetLine = 0;
			}
		}
		xCoordinate = (linesNum - 1) * SHOW_INT[sportMinType];
		for (int i = 0; i < barValues.size(); i++) {
			float positionOffset = barValues.get(barValues.size()-1-i)/ (xCoordinate * SHOW_INTEND[type]);
			int top = (int) (y - (float) (positionOffset*barHeight));
			rect.set(x, top, (int) (x + barWidth),y);
			canvas.drawRect(rect, barPaint);
		//	canvas.drawLine((offset * 3.1f + offset * i * 2),(getHeight() - offset * 1.7f), (offset * 3.1f + offset * i * 2),top+30, barPaint);
			/*if(i==showItem){
				drawHighItem(canvas,x + barWidth/2 ,top,barWidth/2,positionOffset);
				//canvas.drawText(barValues.get(barValues.size()-1-i) + "", x + (barWidth-valueRect.width()/2), top - valueRect.height(), stepTextPiant);
			}	1025 箭头全部显示*/
			drawHighItem(canvas,x + barWidth/2 ,top,barWidth/2,positionOffset,i);
			//俄语下的适配问题
			String languageLx = Utils.getLanguage();
			if("ru".equals(languageLx) || "tr".equals(languageLx)) {
				canvas.drawText(weeks[weeks.length - 1 - i], x + 20, y + textRect.height() * 2, textPaint);
			}else{
				canvas.drawText(weeks[weeks.length - 1 - i], x , y + textRect.height() * 2, textPaint);
			}
			x += uintWidth;
		}
	}
	
	private void drawHighItem(Canvas canvas,int x,int y,int width,float positionOffset,int showno){
		//String.format("%03d", position + 1)
		Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(stepTextPiant.getTextSize());
        paint.setTextAlign(Align.CENTER);
        paint.setTypeface(tfCondensed);
		
		ArgbEvaluator argbEvaluator = new ArgbEvaluator();
		int color = 0;
		if(type==COLUMN_CHAR_VIEW_TYPE_SPORTS){
			valueString = String.valueOf(barValues.get(barValues.size()-1-showno));
			if(positionOffset>=0.5f){
				color = (int)argbEvaluator.evaluate((1.0f-positionOffset)/0.5f,Color.parseColor("#30d24a"), Color.parseColor("#eeec37"));
			}else{
				color = (int)argbEvaluator.evaluate(positionOffset/0.5f, Color.parseColor("#37eeea"),Color.parseColor("#eeec37"));
			}
		}else{
			int min = barValues.get(barValues.size() - 1 - showno);

			if (min % 60 / 6 == 0) {
				valueString = min / 60 + "h";
			} else {
				//valueString = min / 60 + "." + min % 60 / 6 + "h";
			
				double d=min/60d;
				//average_tv.setText(average / 60 + "." + average % 60 / 6 + "h");
//				valueString=Utils.setformat(1,d+"")+ "h";
				valueString= Utils.setformat(2, d + "")+ "h";
			}
			
			//valueString = String.format("%01f",((float) (barValues.get(barValues.size()-1-showItem)))/60);
			if(positionOffset>=0.5f){
				color = (int)argbEvaluator.evaluate((1.0f-positionOffset)/0.5f,Color.parseColor("#4330d2"), Color.parseColor("#3791ee"));
			}else{
				color = (int)argbEvaluator.evaluate(positionOffset/0.5f, Color.parseColor("#37eeea"),Color.parseColor("#3791ee"));
			}
		}
		if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			color = (Integer) Color.parseColor("#3791db");
		}
		paint.getTextBounds(valueString, 0, valueString.length(), valueRect);
		y -= valueRect.height()/2;
		
		paint.setColor(color);
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x+width/2, y-width);
        path.lineTo(x-width/2, y-width);
        path.lineTo(x, y);
        path.close(); 
        canvas.drawPath(path, paint); 
        y -= valueRect.height();

		//俄语下的适配问题
		String languageLx = Utils.getLanguage();
		if("ru".equals(languageLx)){
			canvas.drawText(valueString, x, y-valueRect.height()/2-20, paint);
		}else{
			canvas.drawText(valueString, x, y-valueRect.height()/2, paint);
		}

	}
	public void setDataToShow(int linesNum, List<Integer> barValues, String[] weeks) {
		this.linesNum = Math.max(SHOW_MINLANE[type],linesNum);
		this.barValues = barValues;
		this.weeks = weeks;
		if(weeks!=null && weeks.length>0){
			showItem = this.weeks.length-1;
			postInvalidate();
		}
		barValue = Collections.max(barValues);
		for (int i = 0; i < SHOW_INT.length; i++) {
			if(SHOW_INT[i]*1000 >= barValue){
				if(i+1 == SHOW_INT.length) {
					sportMaxType = i;
				}else{
					sportMaxType = i+1;
				}
				if(i == 0){
					sportMinType = i;
				}else{
					sportMinType = i-1;
				}
				break;
			}
		}
	}
	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startDownX = (int) event.getX();
			startDownY = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			startDownX = (int) event.getX();
			startDownY = (int) event.getY();
			
			rect.left = startX+ barWidth/2;
			rect.right = rect.left + uintWidth * weeks.length;
			rect.top = startY - barHeight;
			rect.bottom = getHeight();
			if (rect.contains(startDownX, startDownY)) {
				showItem = (startDownX - startX - barWidth/2) / uintWidth;
				showItem = Math.min(Math.max(showItem, 0),weeks.length - 1);
				invalidate();
			}
			break;
		}
		return true;
	}
*/
}
