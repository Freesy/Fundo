package com.szkct.weloopbtsmartdevice.view;



import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/5/16 0016.
 */
public class TestProgressView extends View {
    //TODO 分段颜色
  /*  private static final int[] SECTION_COLORS = {Color.GREEN, Color.YELLOW,
            Color.RED};*/
	private static final int[] SECTION_COLORS = {Color.parseColor("#34b5da"), Color.parseColor("#8eda34"),Color.parseColor("#dab534"),Color.parseColor("#da3434")};
   // final int mBarInterval = 7;//TODO 每段间隔
    final float mBarWidth = (float) 0.4;//TODO 小条的粗细
     float nPaintWidth = (float) 6;//TODO 
    private float maxCount = 100;
    private float currentCount = 100;

    private Paint mPaint,m2Paint,nPaint,n2Paint,aPaint;
    RectF rectBlackBg,nrectBlackBg;

    private int mWidth, mHeight;
    private Context mContext;
    public TestProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init(context);
    }

    public TestProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestProgressView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
    	
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(Utils.dip2pxfloat(context, 11));//TODO 改小条的长短
        mPaint.setStyle(Paint.Style.STROKE);
        
        m2Paint = new Paint();
        m2Paint.setAntiAlias(true);
        m2Paint.setStrokeWidth(Utils.dip2pxfloat(context, 11));//TODO 改小条的长短
        m2Paint.setStyle(Paint.Style.STROKE);
        m2Paint.setColor(Color.parseColor("#6A7179")); 
        
        
        TypedArray a = mContext.obtainStyledAttributes(new int[] {  
                R.attr.home_cent_text_color}); 
       
        nPaint = new Paint();
        nPaint.setAntiAlias(true);
        BlurMaskFilter bm=new BlurMaskFilter(4, BlurMaskFilter.Blur.SOLID);
        nPaint.setMaskFilter(bm);
        nPaint.setColor( a.getColor(0,Color.parseColor("#37EEEA")));
        nPaint.setStrokeWidth(nPaintWidth);
        nPaint.setStyle(Paint.Style.STROKE);
        
        n2Paint = new Paint();
        n2Paint.setAntiAlias(true);
     //   BlurMaskFilter n2m=new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID);
      //  n2Paint.setMaskFilter(n2m);
        n2Paint.setColor(Color.parseColor("#6A7179"));
        n2Paint.setStrokeWidth(nPaintWidth);
        n2Paint.setStyle(Paint.Style.STROKE);
        //圆环和小原点
        aPaint = new Paint();
        aPaint.setAntiAlias(true);
        BlurMaskFilter am=new BlurMaskFilter(4, BlurMaskFilter.Blur.SOLID);
        aPaint.setMaskFilter(am);
        aPaint.setColor( a.getColor(0,Color.parseColor("#37EEEA")));
        aPaint.setStrokeWidth(nPaintWidth*3);
        aPaint.setStyle(Paint.Style.STROKE);
        aPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float section = currentCount / maxCount;
        LinearGradient shader = new LinearGradient(0, mHeight, 0, 0 , SECTION_COLORS, null,
                Shader.TileMode.MIRROR);
        mPaint.setShader(shader);

        for (int i = 0; i < 100; ++i) {
        	if(i<currentCount){
        		canvas.drawArc(rectBlackBg, (float) (i * 3.6-90), mBarWidth, false, m2Paint);
        	}else{
        		canvas.drawArc(rectBlackBg, (float) (i * 3.6-90), mBarWidth, false, mPaint);
        	}
            
        }
        
      //  mPaint.reset();
       // Log.e("currentCount", currentCount+"du");
        canvas.drawArc(nrectBlackBg, -90,(float) (currentCount*3.6), false, n2Paint);
        canvas.drawArc(nrectBlackBg, (float) (currentCount*3.6-90),(float) ((100-currentCount)*3.6), false, nPaint);
        canvas.drawArc(nrectBlackBg, (float) (currentCount*3.6-90),(float) 0.1, false, aPaint);
      //  nPaint.reset();
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }

    /**
     * 设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 设置当前的进度值
     *
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = maxCount-(currentCount > maxCount ? maxCount : currentCount);
     //   this.currentCount=50;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//FIXME 不知道是什么鬼,好像设置了宽高
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY
                || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST
                || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = 15;
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);

        rectBlackBg = new RectF(20, 20, mWidth - 20, mHeight - 20);//TODO 绘制图像的大小和位置
        nrectBlackBg = new RectF(60, 60, mWidth-60 , mHeight-60 );//TODO 绘制图像的大小和位置
    }
}
