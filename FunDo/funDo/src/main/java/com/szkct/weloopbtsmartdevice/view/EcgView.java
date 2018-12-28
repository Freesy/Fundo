package com.szkct.weloopbtsmartdevice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * Created by Frankie on 2016/5/26.
 */
public class EcgView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder surfaceHolder;
    public static boolean isRunning;
    int gain = 10;
    int dimension =350;
    private Canvas mCanvas;
    private String bgColor = "#292C30";
    private int wave_speed = 25;//波速: 25mm/s
    private int sleepTime = 30; //每次锁屏的时间间距，单位:ms
    private float lockWidth;//每次锁屏需要画的
    private int ecgPerCount = 1;//每次画心电数据的个数，心电每秒有500个数据包

    private Queue<Integer> ecg0Datas = new LinkedList<Integer>();
    private Paint mPaint;//画波形图的画笔
    private int mWidth;//控件宽度
    private int mHeight;//控件高度
    private int startY0;
    private Rect rect;

    private int startX;//每次画线的X坐标起点
    private double ecgXOffset;//每次X坐标偏移的像素
    private int blankLineWidth = 36;//右侧空白点的宽度

    private static SoundPool soundPool;
    private static int soundId;//心跳提示音
    private double mmPx = 6;

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public Queue<Integer> getEcg0Datas() {
        return ecg0Datas;
    }

    public EcgView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.mContext = context;
//        setZOrderOnTop(true);
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        rect = new Rect();
        converXOffset();
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.color_76eae8));
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        soundId = soundPool.load(mContext, R.raw.heartbeat, 1);

    ecgXOffset = lockWidth / ecgPerCount;
    startY0 = mHeight / 2;//波1初始Y坐标是控件高度的1/4
}

    /**
     * 根据波速计算每次X坐标增加的像素
     * 计算出每次锁屏应该画的px值
     */
    private void converXOffset(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //获取屏幕对角线的长度，单位:px
        int dd = dm.densityDpi;
        double diagonalMm = Math.sqrt(width * width + height * height) / dm.densityDpi;//单位：英寸
        diagonalMm = diagonalMm * 2.54 * 10;//转换单位为：毫米
        double diagonalPx = width * width + height * height;
        diagonalPx = Math.sqrt(diagonalPx);
        //每毫米有多少px
        double px1mm = diagonalPx / diagonalMm;
        mmPx = px1mm;
        //每秒画多少px
        double px1s = wave_speed * px1mm;
        //每次锁屏所需画的宽度
        lockWidth = (float) (px1s * (sleepTime / 1000f));

//        lockWidth = Math.round(lockWidth);
    }

    private List<Integer> list = new ArrayList<>();
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startX = 0;
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.parseColor(bgColor));
//        //释放掉Canvas锁
//        defaultDrawWave(canvas);
        holder.unlockCanvasAndPost(canvas);
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    private float mScaleY;
    private float mScaleX;
    int RowCount = 0;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h ;
        isRunning = true;
        init();
        computeAxisX();
        RowCount = lockWidth>0? (int) (mWidth / lockWidth) :0;
    }

    public int getRowCount() {
        return RowCount;
    }

    private void computeAxisX() {
        mScaleX = StringUtils.divideToFloat(mWidth, 77, 2);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        stopThread();
    }

    private void startThread() {
        isRunning = true;
        new Thread(drawRunnable).start();
    }

    private void stopThread(){
        isRunning = false;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {


            while(isRunning){
                long startTime = System.currentTimeMillis();
                if(!isStop)
                {
                    startDrawWave();
                }
                long endTime = System.currentTimeMillis();
                if(endTime - startTime < sleepTime){
                    try {
                        Thread.sleep(sleepTime - (endTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Thread.currentThread().interrupt();
        }
    };
    int mRowCount =0;
    private void startDrawWave(){
        if(startX ==0)
        {
            mRowCount =0;
        }
        synchronized (surfaceHolder)
        {
            if(ecg0Datas.size() < ecgPerCount){
//                mCanvas = surfaceHolder.lockCanvas(rect);
                Thread.yield();
                return;
            }
            rect.set(startX, 0, (int) (startX + lockWidth + blankLineWidth), mHeight);
            mCanvas = surfaceHolder.lockCanvas(rect);
            if(mCanvas == null) return;
            mCanvas.drawColor(Color.parseColor(bgColor));
            drawWave0();
            surfaceHolder.unlockCanvasAndPost(mCanvas);
            startX = (int) (startX + lockWidth);
            mRowCount++;
            if(startX >= mWidth){
                Log.e("startDrawWave", "startDrawWave mRowCount=" + mRowCount );
                startX = 0;
                RowCount = mRowCount;
            }
            Log.e("startDrawWave", "startDrawWave startX=" + startX );
        }
    }

//    private void startDrawWave(){
//        if(startX ==0)
//        {
//            mRowCount =0;
//        }
//        synchronized (surfaceHolder)
//        {
//            if(ecg0Datas.size() < ecgPerCount){
////                mCanvas = surfaceHolder.lockCanvas(rect);
//                Thread.yield();
//                return;
//            }
//            rect.set(startX, 0, (int) (startX + lockWidth + blankLineWidth), mHeight);
//            mCanvas = surfaceHolder.lockCanvas(rect);
//            if(mCanvas == null) return;
//            mCanvas.drawColor(Color.parseColor(bgColor));
//            drawWave0();
//            surfaceHolder.unlockCanvasAndPost(mCanvas);
//            startX = (int) (startX + lockWidth);
//            mRowCount++;
//            if(startX >= mWidth){
//                Log.e("startDrawWave", "startDrawWave mRowCount=" + mRowCount );
//                startX = 0;
//                RowCount = mRowCount;
//            }
//            Log.e("startDrawWave", "startDrawWave startX=" + startX );
//        }
//    }

    private void defaultDrawWave(Canvas canvas){
            startX =0;
            float mStartX = startX;
            for(int i=0;i<RowCount;i++)
            {
                if(canvas == null) return;
                canvas.drawColor(Color.parseColor(bgColor));
                float newX = (float) (mStartX + ecgXOffset);
                if(list.size()<i)
                {
                    break;
                }
                int y = list.get(i);
                int newY = ecgConver(y);
                Log.e("drawWave0", "drawWave0 newX="+newX+"--newY="+newY+"--mStartX="+mStartX+"--startY0="+startY0+"--y="+y);
                canvas.drawLine(mStartX, startY0, newX, newY, mPaint);
                mStartX = newX;
                startY0 = newY;
                startX = (int) (startX + lockWidth);
            }
//            holder.unlockCanvasAndPost(canvas);
    }
    Path mPath     = new Path();

//    private synchronized void drawWave0(){
//        try{
//            float mStartX = startX;
//            if(ecg0Datas.size() >= ecgPerCount){
//                for(int i=0;i<ecgPerCount;i++){
//                    float newX = (float) (mStartX + ecgXOffset);
//                    int y = ecg0Datas.poll();
//                    int newY = ecgConver(y);
//                    Log.e("drawWave0", "drawWave0 newX="+newX+"--newY="+newY+"--mStartX="+mStartX+"--startY0="+startY0+"--y="+y);
//                    mCanvas.drawLine(mStartX, startY0, newX, newY, mPaint);
//                    mStartX = newX;
//                    startY0 = newY;
//                }
//            }
//            else{
//                /**
//                 * 如果没有数据
//                 * 因为有数据一次画ecgPerCount个数，那么无数据时候就应该画ecgPercount倍数长度的中线
//                 */
//                int newX = (int) (mStartX + ecgXOffset * ecgPerCount);
//                int newY = ecgConver(0);
//                mCanvas.drawLine(mStartX, startY0, newX, newY, mPaint);
//                startY0 = newY;
//            }
//        }catch (NoSuchElementException e){
//            e.printStackTrace();
//        }
//    }

    private synchronized void drawWave0(){
        try{
            float mStartX = startX;
            if(ecg0Datas.size() >= ecgPerCount){
                for(int i=0;i<ecgPerCount;i++){
                    float newX = (float) (mStartX + ecgXOffset);
                    int y = ecg0Datas.poll();
                    int newY = ecgConver(y);
                    Log.e("drawWave0", "drawWave0 newX="+newX+"--newY="+newY+"--mStartX="+mStartX+"--startY0="+startY0+"--y="+y);
                    mCanvas.drawLine(mStartX, startY0, newX, newY, mPaint);
                    mStartX = newX;
                    startY0 = newY;
                }
            }
            else{
                /**
                 * 如果没有数据
                 * 因为有数据一次画ecgPerCount个数，那么无数据时候就应该画ecgPercount倍数长度的中线
                 */
                int newX = (int) (mStartX + ecgXOffset * ecgPerCount);
                int newY = ecgConver(0);
                mCanvas.drawLine(mStartX, startY0, newX, newY, mPaint);
                startY0 = newY;
            }
        }catch (NoSuchElementException e){
            e.printStackTrace();
        }
    }

    /**
     * 将心电数据转换成用于显示的Y坐标
     * @param data
     * @return
     */
    private int ecgConver(int data){
        if(mmPx>0)
        {
            data = (int) (data*gain*mmPx/dimension+mHeight/2);
        }
        else
        {
            data = data*gain*6/dimension+mHeight/2;
        }
        Log.e("ecgConver", "ecgConver data="+data);
        return data;
    }

        public  void addEcgData0(int data){
            ecg0Datas.add(data);
    }

        public  void addEcgData0(ArrayList data){
            ecg0Datas.addAll(data);
    }

    public static void setIsRunning(boolean isRunning) {
        EcgView.isRunning = isRunning;
    }

    static boolean isStop = false;

    public static  void setStop(boolean stop) {
        isStop = stop;
    }
    Bitmap bitmap;
    public void drawBitmap()
    {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
    }

    public void clearData()
    {
        ecg0Datas.clear();
    }

}
