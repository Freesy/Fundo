package com.szkct.weloopbtsmartdevice.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.kct.fundo.btnotification.R;

public class SetGoalStepProgressView extends View {

	private int color_start;
	private int color_end;
	public static int flag;
	private float circleDimensionRate = 0.1f;// 圆环比例
	private float triangleDimensionRate = 0.1f;// 三角形比例
	private float s;// 三角形边长
	private float strokeWidth;// 圆环宽度
	private float angle = getContext().getSharedPreferences("goalstepfiles",
			Context.MODE_PRIVATE).getFloat("setgoalangle", (float) 135.0);// 角度0-360
																				// 第一次设置默认为72度
	private RectF oval = new RectF();
	private Paint paint = new Paint();
	private Path path = new Path();// 绘制三角形的路径
	private Path path_matrix = new Path();// 经过matrix变换的三角形路径
	private Matrix matrix = new Matrix();// 三角形旋转矩阵
	Rect rect = new Rect();// 测量文字所占的高度宽度
	private String TAG = "SetGoalStepProgressView";
	private int goalstepcount;
	private String text;
	private SharedPreferences goalPreferences;
	int stepcount;

	private final float sqrt3 = (float) Math.sqrt(3);

	public SetGoalStepProgressView(Context context) {
		super(context);
		init(null, 0);
	}

	public SetGoalStepProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public SetGoalStepProgressView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
		
	}

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.SetGoalStepProgressView, defStyle, 0);

		color_start = a.getColor(
				R.styleable.SetGoalStepProgressView_color_start,
				Color.parseColor("#79CDCD"));// 默认绿色
		color_end = a.getColor(R.styleable.SetGoalStepProgressView_color_end,
				Color.LTGRAY);// 默认灰色
		circleDimensionRate = a.getFloat(
				R.styleable.SetGoalStepProgressView_circleDimensionRate, 0.1f);
		triangleDimensionRate = a
				.getFloat(
						R.styleable.SetGoalStepProgressView_triangleDimensionRate,
						0.1f);

		a.recycle();


	}

	private void initDrawVar(float width) {
		if (s != triangleDimensionRate * width) {
			s = triangleDimensionRate * width;// 三角形边长
			strokeWidth = circleDimensionRate * width;// 圆环宽度
			// 绘制圆环的矩形区域
			oval.set(s + strokeWidth / 2f, s + strokeWidth / 2f, width - s
					- strokeWidth / 2f, width - s - strokeWidth / 2f);
			// 绘制三角形的路径
			path.reset();
			path.moveTo(0, -s / sqrt3);
			path.lineTo(s / 2f, sqrt3 / 6 * s);
			path.lineTo(-s / 2f, sqrt3 / 6 * s);
			path.close();
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		//Log.e(TAG, "run here!  onDraw() angle ="+angle);
		super.onDraw(canvas);
		int width = this.getWidth();
		initDrawVar(width);
		// 画绿色环形
		paint.setAntiAlias(true);
		paint.setColor(color_start);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		// 正右方为0度，顺时针旋转，正上方为270度

		canvas.drawArc(oval, 270 - angle, angle, false, paint);

		// 画灰色环形
		paint.setColor(color_end);
		canvas.drawArc(oval, 270, 360 - angle, false, paint);

		// 画三角形
		paint.setStrokeWidth(1);
		paint.setColor(color_start);
		paint.setStyle(Paint.Style.FILL);

		matrix.reset();
		matrix.setRotate(180 - angle);// 旋转
		float angle_rad = (float) (angle / 180f * Math.PI);// 化成弧度制
		matrix.postTranslate(// 平移
				(float) (width / 2f - (width / 2f - s + s / sqrt3)
						* Math.sin(angle_rad)), (float) (width / 2f - (width
						/ 2f - s + s / sqrt3)
						* Math.cos(angle_rad)));
		path_matrix.set(path);
		path_matrix.transform(matrix);

		canvas.drawPath(path_matrix, paint);

		// 显示百分比
		paint.setTextSize(width * 0.1f);

		if (angle == 360.0) {
			goalPreferences = getContext().getSharedPreferences(
					"goalstepfiles", Context.MODE_PRIVATE);
			goalstepcount = goalPreferences.getInt("setgoalstepcount", 10000); // 默认步数为10000步

			text = goalstepcount + "";

			// paint.getFontMetrics()方法测量不准确，无法保证居中显示；paint.getTextBounds方法测量较准确
			paint.getTextBounds(text, 0, text.length(), rect);// 测量text所占宽度和高度
			canvas.drawText(text, width / 2f - rect.width() / 2, width / 2f
					+ rect.height() / 2, paint);
		} else {
			//角度小于72度（目标为4000）时自动设置为最小4000
			
			//text = String.valueOf((Math.round((angle / 360 * 24000)+4000)));
			int goaltext_int = (int) ((400*angle/9) +4000);
			text = String.valueOf(goaltext_int);
			
			//Log.e("SetGoalStepView", "text_4000 ="+text);
			int text_int = Integer.parseInt(text);
			if (text_int >= 100) {

				int ittext_int = (text_int / 100) * 100;

				text = ittext_int + "";
				if (text_int > 19950) {
					text = 20000 + "";
				}
			}

			// 通过广播的形式给activity发送消息
			Intent intent = new Intent("broadcast_action"); // action是broadcast_action;
			intent.putExtra("stepcount", text);
			intent.putExtra("stepangle", angle);
			getContext().sendBroadcast(intent);

			paint.getTextBounds(text, 0, text.length(), rect);// 测量text所占宽度和高度
			canvas.drawText(text, width / 2f - rect.width() / 2, width / 2f
					+ rect.height() / 2, paint);
		}

	}	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			
			float x = event.getX();
			float y = event.getY();
			float r = this.getWidth() / 2 - s - strokeWidth;// 圆环半径
			// 判断在圆环外 则处理触摸事件
			if (Math.pow(x - this.getWidth() / 2, 2)
					+ Math.pow(y - this.getWidth() / 2, 2) > Math.pow(r, 2)) {
				double angle = Math.atan((this.getWidth() / 2 - x)
						/ (this.getWidth() / 2 - y));
				angle = angle / Math.PI * 180;
				if (x > this.getWidth() / 2 && y <= this.getWidth() / 2) {// 第一象限
					angle += 360;
				} else if (y > this.getWidth() / 2) {// 第三四象限
					angle += 180;
				}
				if (Math.abs(this.angle - angle) > 1) {
					this.angle = (float) angle;
					this.invalidate();
				}
				//Log.e(TAG, "run here! onTouchEvent()--angle ="+angle);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	// 获取当前选择的比例
	public float getRate() {
		return angle / 360f;
	}

	public int getColor_start() {
		return color_start;
	}

	public void setColor_start(int color_start) {
		this.color_start = color_start;
	}

	public int getColor_end() {
		return color_end;
	}

	public void setColor_end(int color_end) {
		this.color_end = color_end;
	}

	public float getCircleDimensionRate() {
		return circleDimensionRate;
	}

	public void setCircleDimensionRate(float circleDimensionRate) {
		this.circleDimensionRate = circleDimensionRate;
	}

	public float getTriangleDimensionRate() {
		return triangleDimensionRate;
	}

	public void setTriangleDimensionRate(float triangleDimensionRate) {
		this.triangleDimensionRate = triangleDimensionRate;
	}

}
