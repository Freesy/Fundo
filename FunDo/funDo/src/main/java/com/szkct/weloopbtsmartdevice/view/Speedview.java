package com.szkct.weloopbtsmartdevice.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Speedview extends View {
	private List<Float> barValues = new ArrayList<Float>();  // Long
	float startx;

	Paint  yPaint,cpaint,tPaint;
	TextPaint textPaint;
	float  dywidth;//y间隔
	float  dxwidth;//x间隔
	float  maxs;//最大配速
	float  mins;//最小配速
	float  fontHeight,fontwidth;//
	String [] leftstring;
	private LinearGradient shader = null;
	private Context mContext;
	private boolean isMetric;
	public Speedview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
	}

	public Speedview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
	}

	public Speedview(Context context) {
		super(context);
		mContext = context;
		isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
	}
	public Speedview(Context context,int type) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = Utils.getScreenWidth(mContext) - Utils.dip2px(mContext, 32);
		dxwidth = w / 11;
		if (barValues.size() > 10) {
			w = w * (barValues.size()+1) / 11;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(w, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		init();
	}

	private void init() {
		cpaint = new Paint();
		cpaint.setAntiAlias(true); //消除锯齿
		cpaint.setStyle(Paint.Style.STROKE); //绘制空心圆
		cpaint.setColor(Color.parseColor("#25b4b1"));
		cpaint.setStrokeWidth(2);

		//空心圆内部
		tPaint = new Paint();
		tPaint.setAntiAlias(true);
		tPaint.setStrokeWidth(1);
		tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		tPaint.setColor(Color.parseColor("#292C30"));
		if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			tPaint.setColor(Color.parseColor("#ffffff"));
		}else{
			tPaint.setColor(Color.parseColor("#292C30"));
		}


		yPaint = new Paint();
		yPaint.setAntiAlias(true);
		yPaint.setStrokeWidth(2);
		yPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		yPaint.setColor(Color.parseColor("#ef0000"));


		textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(dxwidth / 3);
		if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
			textPaint.setColor(Color.parseColor("#717171"));
		}else{
			textPaint.setColor(Color.parseColor("#ffffff"));
		}

		fontHeight =(textPaint.getFontMetrics().descent-textPaint.getFontMetrics().ascent);

		dywidth=(getHeight()-fontHeight)/4;

		float keduOk = (maxs-mins)/2+mins;
		String averageStr = Utils.setformat(2, String.valueOf(keduOk));
		String[] avg = averageStr.split("\\.");
		int sun0 = Integer.valueOf(avg[0]);
		int sun1 = Integer.valueOf(avg[1].substring(0,2));
		if(sun1 >= 60){
			sun0++;
			sun1 = sun1 - 60;
		}
		String averageOk = sun0 + "." + sun1;
		keduOk = Float.valueOf(averageOk);


		leftstring=new String []{Utils.setformat(2,maxs),Utils.setformat(2,keduOk +""),Utils.setformat(2,mins),"0.00"}; // 左边的刻度值
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int s = 0; s < 4; s++) {
			if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
				if (s == 0) {
					yPaint.setColor(Color.parseColor("#d63737"));
					yPaint.setAlpha(255 / 2);
				} else if (s == 2) {
					yPaint.setColor(Color.parseColor("#37d671"));
					yPaint.setAlpha((int) (255 * 0.3));
				} else {
					yPaint.setColor(Color.parseColor("#3a90d6"));
					yPaint.setAlpha((int) (255 * 0.3));
				}
			}else{
				if (s == 0) {
					yPaint.setColor(Color.parseColor("#c81b2d"));
					yPaint.setAlpha(255/2);
				} else if (s == 2) {
					yPaint.setColor(Color.parseColor("#00ff00"));
					yPaint.setAlpha((int) (255 * 0.3));
				} else {
					yPaint.setColor(Color.parseColor("#25b4b1"));
					yPaint.setAlpha((int) (255 * 0.3));
				}
			}
			canvas.drawLine(0, dywidth * s + dywidth / 2, getWidth(), dywidth * s + dywidth / 2, yPaint);
			if(maxs - mins==0){
				if(s==1||s==2){

				}else{
					String[] keduStr = leftstring[s].split("\\.");  // 0 --- 43925235
					canvas.drawText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",Integer.valueOf(keduStr[0]) , Integer.valueOf(keduStr[1].substring(0,2))), 0, dywidth * s + dywidth / (float) 2 - 10, textPaint);
				}
			}else{
				String[] keduStr = leftstring[s].split("\\.");
				canvas.drawText(String.format(Locale.ENGLISH,"%1$02d'%2$02d''",Integer.valueOf(keduStr[0]) , Integer.valueOf(keduStr[1].substring(0,2))), 0, dywidth * s + dywidth / (float) 2 - 10, textPaint);
			}
		}
		int canvassize=barValues.size();
		if(canvassize<10){
			canvassize=10;   // todo --- 1-10 公里
		}

		for (int s = 0; s < canvassize; s++) {
			if(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")){
				if(barValues.size()>s){
					if(barValues.get(s)==maxs){
						cpaint.setColor(Color.parseColor("#d63737"));
						yPaint.setColor(Color.parseColor("#d63737"));
						yPaint.setAlpha((int) (255 * 0.3));
					}else if(barValues.get(s)==mins){
						cpaint.setColor(Color.parseColor("#37d671"));
						yPaint.setColor(Color.parseColor("#37d671"));
						yPaint.setAlpha((int) (255 * 0.3));
					}else{
						cpaint.setColor(Color.parseColor("#3a90d6"));
						yPaint.setColor(Color.parseColor("#3a90d6"));
						yPaint.setAlpha((int) (255 * 0.2));
					}
				}else{
					cpaint.setColor(Color.parseColor("#3a90d6"));
					yPaint.setColor(Color.parseColor("#3a90d6"));
					yPaint.setAlpha((int) (255 * 0.2));
				}
			}else{
				if(barValues.size()>s){
					if(barValues.get(s)==maxs){
						cpaint.setColor(Color.parseColor("#c81b2d"));
						yPaint.setColor(Color.parseColor("#c81b2d"));
						yPaint.setAlpha((int) (255 * 0.3));
					}else if(barValues.get(s)==mins){
						cpaint.setColor(Color.parseColor("#00ff00"));
						yPaint.setColor(Color.parseColor("#00ff00"));
						yPaint.setAlpha((int) (255 * 0.3));
					}else{
						cpaint.setColor(Color.parseColor("#25b4b1"));
						yPaint.setColor(Color.parseColor("#25b4b1"));
						yPaint.setAlpha((int) (255 * 0.2));
					}
				}else{
					cpaint.setColor(Color.parseColor("#25b4b1"));
					yPaint.setColor(Color.parseColor("#25b4b1"));
					yPaint.setAlpha((int) (255 * 0.2));
				}
			}
			if(isMetric) {
				canvas.drawText((s + 1) + "KM", dxwidth * s + dxwidth * 1, getHeight() - fontHeight, textPaint);
			}else{
				canvas.drawText((s + 1) + "MI", dxwidth * s + dxwidth * 1, getHeight() - fontHeight, textPaint);
			}
			canvas.drawLine(dxwidth * s + dxwidth * 3 / 2, 0, dxwidth * s + dxwidth * 3 / 2, getHeight() - fontHeight * 2, yPaint);

			if(barValues.size()>s){
				if(maxs - mins==0){
					canvas.drawCircle(dxwidth * s + dxwidth * 3 / 2, dywidth / 2, 15, tPaint);
					canvas.drawCircle(dxwidth * s + dxwidth * 3 / 2,   dywidth / 2, 16, cpaint);
				} else {
					canvas.drawCircle(dxwidth * s + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs- mins)) * dywidth * 2 + dywidth / 2, 15, tPaint);
					canvas.drawCircle(dxwidth * s + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs- mins )) * dywidth * 2 + dywidth / 2, 16, cpaint);
				}
			}
		}
	}

	public void setDataToShow( List<Float> barValues,float mins,float maxs) {  //   List<Long> barValues,float mins,float maxs
			this.barValues=barValues;    // 点的集合
			this.mins=mins;  // 最大点
			this.maxs=maxs;  // 最小点
			initdata();
			postInvalidate();
	}

	private void initdata() {

	}
	
}
