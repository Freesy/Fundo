package com.szkct.weloopbtsmartdevice.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.kct.fundo.btnotification.R;

import java.io.IOException;
//import com.kct.fundobeta.btnotification.R;

public class NewCropperActivity extends AppCompatActivity {

    public static Bitmap bitmap=null;//直接静态，从内存拿出来
    CropImageView mCropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cropper);

        mCropImageView= (CropImageView) findViewById(R.id.CropImageView);

        init();
    }

    private void init() {
        String path=getIntent().getStringExtra("path");
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        int imageHeight = opt.outHeight;
        int imageWidth = opt.outWidth;

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        // 该方法已过时，使用getRealSize()方法替代。也可以使用getSize()，但是不能准确的获取到分辨率
        // int screenHeight = display.getHeight();
        // int screenWidth = display.getWidth();

        display.getRealSize(point);
        int screenHeight = point.y;
        int screenWidth = point.x;

        int scale = 1;
        int scaleWidth = imageWidth / screenWidth;
        int scaleHeigh = imageHeight / screenHeight;
        if (scaleWidth >= scaleHeigh && scaleWidth > 1) {
            scale = scaleWidth;
        } else if (scaleWidth < scaleHeigh && scaleHeigh > 1) {
            scale = scaleHeigh;
        }

        opt.inSampleSize = scale;
        opt.inJustDecodeBounds = false;


        Bitmap bmp = BitmapFactory.decodeFile(path, opt);

        // 取得图片旋转角度
        int angle = readPictureDegree(path);

        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);

        mCropImageView.setImageBitmap(bitmap);
        mCropImageView.setAspectRatio(1,1);//裁剪区域为正方形
        mCropImageView.setFixedAspectRatio(true);
    }

    public void doClick(View view) {
        switch (view.getId()){
            case R.id.bt_cancel:
                finish();
                break;
            case R.id.bt_ok:
                bitmap=mCropImageView.getCroppedImage();
                setResult(1);
                finish();
                break;
        }
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
}
