package com.szkct.weloopbtsmartdevice.view;


import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.Utils;

public class WheelIndicatorView extends View {

	private final static int ANGLE_INIT_OFFSET = -90;
	private final static int DEFAULT_FILLED_PERCENT = 100;
	private final static int DEFAULT_ITEM_LINE_WIDTH = 25;
	public static final int ANIMATION_DURATION = 1200;
	// public static final int INNER_BACKGROUND_CIRCLE_COLOR = Color.argb(255,
	// 220, 220, 220); // Color for
	public static final int INNER_BACKGROUND_CIRCLE_COLOR = Color
			.parseColor("#EDEDED"); // 圆环灰色背景
	public final static String TAG = "WheelIndicatorView";

	public static WheelIndicatorItem firstBarItem, secondBarItem, thirdBarItem;

	/*
	 * public static WheelIndicatorItem firstBarItem = new
	 * WheelIndicatorItem(1.8f, Color.parseColor("#ff9000")); public static
	 * WheelIndicatorItem secondBarItem = new WheelIndicatorItem(1.8f,
	 * Color.argb(255, 194, 30, 92)); public static WheelIndicatorItem
	 * thirdBarItem = new WheelIndicatorItem(1.8f, Color.parseColor("#63B8FF"));
	 */

	private Paint itemArcPaint;
	private Paint itemEndPointsPaint;
	private Paint innerBackgroundCirclePaint;
	private List<WheelIndicatorItem> wheelIndicatorItems;
	private int viewHeight;
	private int viewWidth;
	private int minDistViewSize;
	private int maxDistViewSize;
	private int traslationX;
	private int traslationY;
	private RectF wheelBoundsRectF;
	private Paint circleBackgroundPaint;
	private ArrayList<Float> wheelItemsAngles; // calculated angle for each
												// @WheelIndicatorItem
	private int filledPercent = 80;
	private int itemsLineWidth = 25;

	public WheelIndicatorView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	public WheelIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public WheelIndicatorView(Context context) {
		super(context);
		init(null);
	}

	public void setWheelIndicatorItems(
			List<WheelIndicatorItem> wheelIndicatorItems) {
		if (wheelIndicatorItems == null)
			throw new IllegalArgumentException(
					"wheelIndicatorItems cannot be null");
		this.wheelIndicatorItems = wheelIndicatorItems;
		recalculateItemsAngles();
		invalidate();
	}

	public void setFilledPercent(int filledPercent) {
		if (filledPercent < 0)
			this.filledPercent = 0;
		else if (filledPercent > 100)
			this.filledPercent = 100;
		else
			this.filledPercent = filledPercent;
		invalidate();
	}

	public int getFilledPercent() {
		return filledPercent;
	}

	public void setItemsLineWidth(int itemLineWidth) {
		if (itemLineWidth <= 0)
			throw new IllegalArgumentException(
					"itemLineWidth must be greater than 0");
		this.itemsLineWidth = itemLineWidth;
		invalidate();
	}

	public void addWheelIndicatorItem(WheelIndicatorItem indicatorItem) {
		if (indicatorItem == null)
			throw new IllegalArgumentException(
					"wheelIndicatorItems cannot be null");

		this.wheelIndicatorItems.add(indicatorItem);
		recalculateItemsAngles();
		invalidate();

	}

	public void notifyDataSetChanged() {
		recalculateItemsAngles();
		invalidate();
	}

	public void setBackgroundColor(int color) {
		circleBackgroundPaint = new Paint();
		circleBackgroundPaint.setColor(color);
		invalidate();
	}

	private void init(AttributeSet attrs) {
		TypedArray attributesArray = getContext().getTheme()
				.obtainStyledAttributes(attrs, R.styleable.WheelIndicatorView,
						0, 0);

		int itemsLineWidth = attributesArray.getDimensionPixelSize(
				R.styleable.WheelIndicatorView_itemsLineWidth,
				DEFAULT_ITEM_LINE_WIDTH);
		setItemsLineWidth(itemsLineWidth);

		int filledPercent = attributesArray.getInt(
				R.styleable.WheelIndicatorView_filledPercent,
				DEFAULT_FILLED_PERCENT);
		setFilledPercent(filledPercent);

		int bgColor = attributesArray.getColor(
				R.styleable.WheelIndicatorView_backgroundColor, -1);
		if (bgColor != -1)
			setBackgroundColor(bgColor);

		this.wheelIndicatorItems = new ArrayList<>();
		this.wheelItemsAngles = new ArrayList<>();

		itemArcPaint = new Paint();
		itemArcPaint.setStyle(Paint.Style.STROKE);
		itemArcPaint.setStrokeWidth(itemsLineWidth * 2);
		itemArcPaint.setAntiAlias(true);

		innerBackgroundCirclePaint = new Paint();
		innerBackgroundCirclePaint.setColor(INNER_BACKGROUND_CIRCLE_COLOR);
		innerBackgroundCirclePaint.setStyle(Paint.Style.STROKE);
		innerBackgroundCirclePaint.setStrokeWidth(itemsLineWidth * 2);
		innerBackgroundCirclePaint.setAntiAlias(true);

		itemEndPointsPaint = new Paint();
		itemEndPointsPaint.setAntiAlias(true);
	}

	private void recalculateItemsAngles() {
		wheelItemsAngles.clear();
		float total = 0;
		float angleAccumulated = 0;

		/*
		 * for (WheelIndicatorItem item : wheelIndicatorItems){ total +=
		 * item.getWeight(); Log.e(TAG +"item total =", total +""); Log.e(TAG
		 * +"item.getWeight  =", item.getWeight() +""); }
		 */

		for (int i = 0; i < wheelIndicatorItems.size(); i++) {
			total += wheelIndicatorItems.get(i).getWeight();
		}
		for (int i = 0; i < wheelIndicatorItems.size(); ++i) {
			float normalizedValue = wheelIndicatorItems.get(i).getWeight()
					/ total;
			// Log.e(TAG+"  normalizedValue= ", normalizedValue+"");
			float angle = 360 * normalizedValue * filledPercent / 100;

			wheelItemsAngles.add(angle + angleAccumulated);
			angleAccumulated += angle;

		}
	}

	public void startItemsAnimation() {
		ObjectAnimator animation = ObjectAnimator.ofInt(
				WheelIndicatorView.this, "filledPercent", 0, filledPercent);
		animation.setDuration(ANIMATION_DURATION);
		animation.setInterpolator(PathInterpolatorCompat.create(0.4F, 0.0F,
				0.2F, 1.0F));
		animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				recalculateItemsAngles();
				invalidate();
			}
		});
		animation.start();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		this.viewHeight = getMeasuredHeight();
		this.viewWidth = getMeasuredWidth();
		this.minDistViewSize = Math
				.min(getMeasuredWidth(), getMeasuredHeight());
		this.maxDistViewSize = Math
				.max(getMeasuredWidth(), getMeasuredHeight());

		if (viewWidth <= viewHeight) {
			this.traslationX = 0;
			this.traslationY = (maxDistViewSize - minDistViewSize) / 2;
		} else {
			this.traslationX = (maxDistViewSize - minDistViewSize) / 2;
			this.traslationY = 0;
		}
		// Adding artificial padding, depending on line width
		wheelBoundsRectF = new RectF(0 + itemsLineWidth, 0 + itemsLineWidth,
				minDistViewSize - itemsLineWidth, minDistViewSize
						- itemsLineWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.translate(traslationX, traslationY);
		if (circleBackgroundPaint != null)
			canvas.drawCircle(wheelBoundsRectF.centerX(),
					wheelBoundsRectF.centerY(), wheelBoundsRectF.width() / 2
							- itemsLineWidth, circleBackgroundPaint);
		canvas.drawArc(wheelBoundsRectF, ANGLE_INIT_OFFSET, 360, false,
				innerBackgroundCirclePaint);
		drawIndicatorItems(canvas);
	}

	private void drawIndicatorItems(Canvas canvas) {
		if (wheelIndicatorItems.size() > 0) {
			for (int i = wheelIndicatorItems.size() - 1; i >= 0; i--) { // Iterate
																		// backward
																		// to
																		// overlap
																		// larger
																		// items
				draw(wheelIndicatorItems.get(i), wheelBoundsRectF,
						wheelItemsAngles.get(i), canvas);
			}
		}
	}

	private void draw(WheelIndicatorItem indicatorItem, RectF surfaceRectF,
			float angle, Canvas canvas) {
		itemArcPaint.setColor(indicatorItem.getColor());
		itemEndPointsPaint.setColor(indicatorItem.getColor());
		// Draw arc
		canvas.drawArc(surfaceRectF, ANGLE_INIT_OFFSET, angle, false,
				itemArcPaint);
		// Draw top circle
		canvas.drawCircle(minDistViewSize / 2, 0 + itemsLineWidth,
				itemsLineWidth, itemEndPointsPaint);
		int topPosition = minDistViewSize / 2 - itemsLineWidth;
		// Draw end circle
		canvas.drawCircle(
				(float) (Math.cos(Math.toRadians(angle + ANGLE_INIT_OFFSET))
						* topPosition + topPosition + itemsLineWidth),
				(float) (Math.sin(Math.toRadians((angle + ANGLE_INIT_OFFSET)))
						* topPosition + topPosition + itemsLineWidth),
				itemsLineWidth, itemEndPointsPaint);
	}
	
	public static  String ifHasDot(String in_s) {
		char dot_c = ',';
		String dot_str = dot_c + "";
		String replace = in_s;
		// Log.e("......", "in_s ="+in_s);
		if (in_s.indexOf(dot_str) >= 0) {
			// 找到特殊字符
			replace = in_s.replace(",", ".");
			// Log.e("提换setHeightStr中的, ", "replace ="+replace);
		} else {
			replace = in_s;
		}

		return replace;
	}

	public static void setsportProgressBar(WheelIndicatorView wheelSportView,
			int allstep, int goalstepcount) {
		// TODO Auto-generated method stub
		float totalstep = goalstepcount;
		float stepdone = (float) allstep;
		int finishstep = (int) ((stepdone / totalstep) * 100);

		wheelSportView.setFilledPercent(finishstep); // 色条环百分比的设置

		secondBarItem = new WheelIndicatorItem(1.0f, Color.parseColor("#00C5CD"));
		wheelSportView.addWheelIndicatorItem(secondBarItem); // 黄色填充
		wheelSportView.startItemsAnimation(); // Animate!`

	}

	public static void setsleepProgressBar(WheelIndicatorView wheelSleepView,Double deepsleeptime,
			Double allsleeptime) {
		// TODO Auto-generated method stub
		//allsleeptime = 6.0; deepsleeptime = 4.0;
		Log.e(TAG, "总睡眠allsleeptime ="+allsleeptime +"深睡 ="+deepsleeptime);
	
		String percentStr = Utils.setformat(2,deepsleeptime/allsleeptime+"");
		String allpercentStr = Utils.setformat(2,allsleeptime/8*100+"");
		//Log.e(TAG, "percentStr ="+percentStr);
		percentStr = ifHasDot(percentStr);
		allpercentStr = ifHasDot(allpercentStr);
		float percentfloat = Float.valueOf(percentStr);
		float allpercent = Float.valueOf(allpercentStr);
		wheelSleepView.setFilledPercent((int) allpercent); // 色条环百分比的设置
		//Log.e(TAG, "percent ="+percent);
		firstBarItem =null;
		thirdBarItem = null;
		
		Log.e(TAG, "wheelSleepView.wheelIndicatorItems.size() ="+wheelSleepView.wheelIndicatorItems.size());
		if (wheelSleepView.wheelIndicatorItems.size() >0) {
			wheelSleepView.wheelIndicatorItems.clear();
			
		}
		firstBarItem = new WheelIndicatorItem((percentfloat), Color.parseColor("#8357ac"));  //#8B2252 暗色
		
		//thirdBarItem = new WheelIndicatorItem(1.8f, Color.parseColor("#00C5CD"));// 蓝色填充
		thirdBarItem = new WheelIndicatorItem((1f-percentfloat), Color.parseColor("#cc7bf2"));//#D15FEE 粉色
		wheelSleepView.addWheelIndicatorItem(firstBarItem);  //总睡眠时间
		wheelSleepView.addWheelIndicatorItem(thirdBarItem); // 深睡填充
		wheelSleepView.startItemsAnimation(); // Animate!

	}

}
