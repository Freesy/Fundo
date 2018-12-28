package com.szkct.weloopbtsmartdevice.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.szkct.weloopbtsmartdevice.util.Log;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kct on 2016/12/16.
 */

public class MotionChartView extends View {


    private static final int[] SECTION_COLORS1 = {Color.parseColor("#ff34dfdb"), Color.parseColor("#7f34dfdb"), Color.parseColor("#0034dfdb")};
    private static final int[] SECTION_COLORS2 = {Color.parseColor("#ff3499df"), Color.parseColor("#7f3499df"), Color.parseColor("#003499df")};
    private static final int[] SECTION_COLORS3 = {Color.parseColor("#ffe30a20"), Color.parseColor("#7fe30a20"), Color.parseColor("#00e30a20")};
    private static final int[] SECTION_COLORS4 = {Color.parseColor("#ff56c52a"), Color.parseColor("#7f56c52a"), Color.parseColor("#0056c52a")};


    private List<Float> barValues = new ArrayList<Float>();


    private static final int[] SET_COLORS = {Color.parseColor("#34dfdb"),
            Color.parseColor("#3499df"), Color.parseColor("#e30a20"), Color.parseColor("#56c52a"),
    };

    String[] danweisting = {"m/s", "m", "bpm", ""};
    String[] danweistingMetric = {"ft/s", "ft", "bpm", ""};
    String[] leftstring ;
    String[] xstring ;//分钟数组
    float startx;

    Paint yPaint, cpaint, tPaint;
    TextPaint textPaint;
    float dywidth;//y间隔
    float dxwidth;//x间隔

    float pointxwidth;//x间隔
    float maxs;//最大配速
    float mins;//最小配速
    float fontHeight, fontwidth;//

    private LinearGradient shader = null;
    private Bitmap srcBitmap;
    private int type = 0;
    int screenW, screenH;
    Paint rpaint;
    private Context mContext;
    private boolean isMetric;

    int Drawwidth=9;

    public MotionChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenW = getScreenWidth((Activity) context);
        screenH = getScreenHeight((Activity) context);
        mContext = context;
        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
    }

    public MotionChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenW = getScreenWidth((Activity) context);
        screenH = getScreenHeight((Activity) context);
        mContext = context;
        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
    }

    public MotionChartView(Context context) {
        super(context);
        screenW = getScreenWidth((Activity) context);
        screenH = getScreenHeight((Activity) context);
        mContext = context;
        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
    }

    public MotionChartView(Context context, int type) {
        super(context);
        screenW = getScreenWidth((Activity) context);
        screenH = getScreenHeight((Activity) context);
        mContext = context;
        isMetric = SharedPreUtil.YES.equals(SharedPreUtil.getParam(context,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        dxwidth = w / 11;
        pointxwidth = Drawwidth* dxwidth / barValues.size();
        screenW = w;
        screenH = h;
        init();

    }

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private void init() {

        rpaint = new Paint();
        rpaint.setFilterBitmap(false);
        rpaint.setStyle(Paint.Style.FILL);

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


        yPaint = new Paint();
        yPaint.setAntiAlias(true);
        yPaint.setStrokeWidth(2);
        yPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        yPaint.setColor(Color.parseColor("#ef0000"));


        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(dxwidth /3);
        if (SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.THEME_WHITE).equals("0")) {
            textPaint.setColor(Color.parseColor("#292C30"));
        } else {

            textPaint.setColor(Color.parseColor("#ffffff"));
        }
        fontHeight = (textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent);

        dywidth = (getHeight() - fontHeight) / 4;
        if(isMetric) {
            if (mins == 0 && maxs == 1) {
                leftstring = new String[]{1.5 + danweisting[type], 1 + danweisting[type], 0.5 + danweisting[type], ""};
            } else {
                if (mins == 0) {
                    leftstring = new String[]{String.format(Locale.ENGLISH,"%.1f", (float) maxs) + danweisting[type], String.format(Locale.ENGLISH,"%.1f", (float) (maxs * (0.67))) + danweisting[type], String.format(Locale.ENGLISH,"%.1f", (float) (maxs * (0.34))) + danweisting[type], ""};
                } else {
                    leftstring = new String[]{String.format(Locale.ENGLISH,"%.1f", (float) maxs) + danweisting[type], String.format(Locale.ENGLISH,"%.1f", ((float) ((maxs - mins) / 2) + mins)) + danweisting[type], String.format(Locale.ENGLISH,"%.1f", (float) mins) + danweisting[type], ""};
                }
            }
        }else{
            if (mins == 0 && maxs == 1) {
                leftstring = new String[]{1.5 + danweistingMetric[type], 1 + danweistingMetric[type], 0.5 + danweistingMetric[type], ""};
            } else {
                if (mins == 0) {
                    leftstring = new String[]{String.format(Locale.ENGLISH,"%.1f", (float) maxs) + danweistingMetric[type], String.format(Locale.ENGLISH,"%.1f", (float) (maxs * (0.67))) + danweistingMetric[type], String.format(Locale.ENGLISH,"%.1f", (float) (maxs * (0.34))) + danweistingMetric[type], ""};
                } else {
                    leftstring = new String[]{String.format(Locale.ENGLISH,"%.1f", (float) maxs) + danweistingMetric[type], String.format(Locale.ENGLISH,"%.1f", ((float) ((maxs - mins) / 2) + mins)) + danweistingMetric[type], String.format(Locale.ENGLISH,"%.1f", (float) mins) + danweistingMetric[type], ""};
                }
            }
        }
       if(barValues.size()>2){
           srcBitmap = makesss(barValues);
       }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

      if(barValues==null||barValues.size()==0){

          for (int s = 0; s < 4; s++) {

              yPaint.setColor(SET_COLORS[type]);
              yPaint.setAlpha((int) (255 * 0.3));

              canvas.drawLine(0, dywidth * s + dywidth / 2, getWidth(), dywidth
                      * s + dywidth / 2, yPaint);
              canvas.drawText(leftstring[s], 0,
                      dywidth * s + dywidth / (float) 2 - 10, textPaint);
          }
          for (int s = 0; s < 10; s++) {

              yPaint.setColor(SET_COLORS[type]);

              yPaint.setAlpha((int) (255 * 0.2));
              if (s % 2 == 0) {
                  canvas.drawText(xstring[s/2], dxwidth * s + dxwidth * 1,
                          getHeight() - fontHeight, textPaint);
              }else if(s==9){

                  canvas.drawText(xstring[5], dxwidth * s + dxwidth * 1,
                          getHeight() - fontHeight, textPaint);
              }

              canvas.drawLine(dxwidth * s + dxwidth * 3 / 2, 0, dxwidth * s
                      + dxwidth * 3 / 2, getHeight() - fontHeight * 2, yPaint);
          }
          return ;
      }
        if(barValues.size()>2){
            canvas.drawBitmap(srcBitmap, 0, 0, rpaint);

        }


        for (int s = 0; s < 4; s++) {

            yPaint.setColor(SET_COLORS[type]);
            yPaint.setAlpha((int) (255 * 0.3));

            canvas.drawLine(0, dywidth * s + dywidth / 2, getWidth(), dywidth
                    * s + dywidth / 2, yPaint);
            canvas.drawText(leftstring[s], 0,
                    dywidth * s + dywidth / (float) 2 - 10, textPaint);
        }
        Path path = new Path();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        //  p.setShader(shader);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);

        p.setColor(SET_COLORS[type]);


        p.setPathEffect(new CornerPathEffect(50));
        for (int s = 0; s < barValues.size(); s++) {


            cpaint.setColor(Color.parseColor("#25b4b1"));

            //  canvas.drawCircle(pointxwidth * s + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2, 15, tPaint);
            //  canvas.drawCircle(pointxwidth * s + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2, 17, cpaint);
            if (s == 0) {
                path.moveTo(pointxwidth * 0 + dxwidth * 3 / 2, (1 - (barValues.get(0) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
            } else if (s == barValues.size() - 1) {
                path.quadTo(pointxwidth * (s - 1) + dxwidth * 3 / 2, (1 - (barValues.get(s - 1) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2,
                        dxwidth * Drawwidth + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
            } else {
                path.lineTo(pointxwidth * s + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
            }
        }
        for (int s = 0; s < 10; s++) {

            yPaint.setColor(SET_COLORS[type]);

            yPaint.setAlpha((int) (255 * 0.2));
            if (s % 2 == 0) {
                canvas.drawText(xstring[s/2], dxwidth * s + dxwidth * 1,
                        getHeight() - fontHeight, textPaint);
            }else if(s==9){

                canvas.drawText(xstring[5], dxwidth * s + dxwidth * 1,
                        getHeight() - fontHeight, textPaint);
            }

            canvas.drawLine(dxwidth * s + dxwidth * 3 / 2, 0, dxwidth * s
                    + dxwidth * 3 / 2, getHeight() - fontHeight * 2, yPaint);
        }
        canvas.drawPath(path, p);

    }

    public void setDataToShow(List<Float> barValues, float mins, float maxs, int type,int minute) {
        this.barValues = barValues;
        this.mins = mins;
        this.maxs = maxs;
        this.type = type;
        initdata(minute);
        postInvalidate();

    }

    private void initdata(int minute) {
        // TODO Auto-generated method stub
        Drawwidth = 8;
        float S1 =Float.parseFloat(Utils.setformat(1, (float) (minute) / 60)) ;
        if (S1*10%4!=0){
            S1=S1+(0.4f-S1*10%4/10);
        }

        xstring = new String[]{"0min",Utils.setformat(1,S1/4) + "min", Utils.setformat(1,S1/4*2)+ "min"
                , Utils.setformat(1,S1/4*3)+ "min", Utils.setformat(1,S1) + "min", ""};
       /* if(minute%4 == 0){
            xstring = new String[]{"0min",Utils.setformat(1,(float)(minute/4)/60)+"min",Utils.setformat(1,(float)(minute/4*2)/60)+"min"
                    ,Utils.setformat(1,(float)(minute/4*3)/60)+"min",Utils.setformat(1,(float)(minute/4*4)/60)+"min",""};
        }else{
            float  minutes = minute%4;
            xstring = new String[]{"0min", Utils.setformat(1,((float)(minute / 4) + minutes)/60)+"min",Utils.setformat(1,((float)(minute / 4*2) + minutes)/60)+"min"
                    ,Utils.setformat(1,((float)(minute / 4*3) + minutes)/60)+"min",Utils.setformat(1,((float)(minute / 4*4) + minutes)/60)+"min",""};
        }
*/

        /*if(minute>8){
            if(minute%4==0){
                Drawwidth=8;
                xstring = new String[]{"0min",minute/4+"min",minute/4*2+"min",minute/4*3+"min",minute/4*4+"min",""};
            }else{

                xstring = new String[]{"0min",minute/4+"min",minute/4*2+"min",minute/4*3+"min","",minute+"min"};
            }

        }else{
            Drawwidth=8;
            xstring = new String[]{"0min","2min","4min","6min","8min",""};
        }*/

    }

    // 创建一个bitmap，
    private Bitmap makesss(List<Float> barValues) {


        Log.e("barValues", barValues.size() + "==" + screenW + "==" + screenH);
        LinearGradient shade;
        switch (type) {
            case 0:
                shader = new LinearGradient(0, 0, 0, dywidth * 3, SECTION_COLORS1, null, Shader.TileMode.CLAMP);
                break;
            case 1:
                shader = new LinearGradient(0, 0, 0, dywidth * 3, SECTION_COLORS2, null, Shader.TileMode.CLAMP);
                break;
            case 2:
                shader = new LinearGradient(0, 0, 0, dywidth * 3, SECTION_COLORS3, null, Shader.TileMode.CLAMP);
                break;
            case 3:
                shader = new LinearGradient(0, 0, 0, dywidth * 3, SECTION_COLORS4, null, Shader.TileMode.CLAMP);
                break;


        }


        Bitmap bm = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Path path = new Path();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        //  p.setShader(shader);
        p.setColor(0x00000000);
        path.moveTo(dxwidth * Drawwidth + dxwidth * 3 / 2, (1 - (barValues.get(barValues.size() - 1) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
        path.lineTo(dxwidth * Drawwidth + dxwidth * 3 / 2, getHeight() - fontHeight * 2);
        path.lineTo(dxwidth * 3 / 2, getHeight() - fontHeight * 2);
        path.lineTo(dxwidth * 3 / 2, (1 - (barValues.get(0) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
        c.drawPath(path, p);
        p.setPathEffect(new CornerPathEffect(50));

        for (int s = 0; s < barValues.size(); s++) {
            if (s == barValues.size() - 1) {
                path.quadTo(pointxwidth * (s - 1) + dxwidth * 3 / 2, (1 - (barValues.get(s - 1) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2, dxwidth * Drawwidth + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
            } else {
                path.lineTo(pointxwidth * (s) + dxwidth * 3 / 2, (1 - (barValues.get(s) - mins) / (maxs - mins)) * dywidth * 2 + dywidth / 2);
            }


        }
        p.setColor(0xFF66ccff);
        p.setShader(shader);
        c.drawPath(path, p);

        //   p.setPathEffect(new CornerPathEffect(0));
/*
        path.lineTo(dxwidth * (barValues.size() - 1) + dxwidth * 3 / 2, getHeight() - fontHeight * 2);
        path.lineTo(dxwidth * 3 / 2, getHeight() - fontHeight * 2);*/


        return bm;
    }
}
