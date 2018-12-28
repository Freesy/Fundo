package com.szkct.weloopbtsmartdevice.view;




import com.kct.fundo.btnotification.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/5/17 0017.
 */
public class TestXfermodeView extends View {

    Bitmap mBitmapBackground = null;
    Bitmap mBitmapProgress = null;
    Bitmap mBitmapShadow = null;
    Paint mPaint = null;
    int mProgressCurrent = -1;
    int mProgressMax = 100;

    public TestXfermodeView(Context context) {
        super(context);
        init(context);

        //FIXME 默认进度
        setProgress(30);
    }

    protected void init(Context context) {
        mPaint = new Paint();
       /* try {
            mBitmapBackground = BitmapFactory.decodeStream(context.getAssets().open("img_background.png"));
            mBitmapProgress = BitmapFactory.decodeStream(context.getAssets().open("img_progress.png"));
        }catch (Exception e){
            e.printStackTrace();*/
            mBitmapBackground = BitmapFactory.decodeResource(context.getResources(),R.drawable.img_background);
            mBitmapBackground = BitmapFactory.decodeResource(context.getResources(),R.drawable.img_progress);
     //   }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmapBackground, 0, 0, mPaint);

        if (mBitmapShadow != null) {
            int _sc = canvas.saveLayer(0, 0, mBitmapShadow.getWidth(), mBitmapShadow.getHeight(), null, Canvas.ALL_SAVE_FLAG);
            canvas.drawBitmap(mBitmapProgress, 0, 0, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawBitmap(mBitmapShadow, 0, 0, mPaint);
            mPaint.setXfermode(null);
            canvas.restoreToCount(_sc);
        }
    }

    public int getProgress(){
        return mProgressCurrent;
    }
    public void setProgress(int progress) {
        if (progress == mProgressCurrent) {
            return;
        }
        progress = Math.min(mProgressMax, progress);
        progress = Math.max(0, progress);
        mProgressCurrent = progress;

        if (mBitmapShadow != null) {
            mBitmapShadow.recycle();
            mBitmapShadow = null;
        }
        mBitmapShadow = Bitmap.createBitmap(mBitmapProgress.getWidth(), mBitmapProgress.getHeight(), mBitmapProgress.getConfig());
        Canvas _canvas = new Canvas(mBitmapShadow);
        RectF rectBlackBg = new RectF(0, 0, mBitmapShadow.getWidth(), mBitmapShadow.getHeight());
        _canvas.drawArc(rectBlackBg, 0, 360 * (mProgressMax - mProgressCurrent) / mProgressMax, true, mPaint);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //FIXME 摸一下涨10点进度
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            int _progress = getProgress();
            if(_progress<100){
                _progress+=10;
            }else{
                _progress = 0;
            }
            setProgress(_progress);
        }
        return super.onTouchEvent(event);
    }
}
