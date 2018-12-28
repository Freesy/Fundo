package com.szkct.weloopbtsmartdevice.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;



public class WXShearedUtil {
	
	private static Activity mActivity = null;
	Context context;
	public final static int CONSULT_DOC_CAMERA = 1001; 
	
	public WXShearedUtil(Activity activity) {
		mActivity = activity;
		
		
	}
	
	
	
	
	/** 是否安装微信 */
	public boolean isInstallWx(String packageName) {
		try {

			PackageManager manager = mActivity.getPackageManager();

			PackageInfo info = manager.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);

			if (info != null) {

				return true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 是否存在SDCard
	 * 
	 */
	public static final boolean hasSdCard() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		}
		return false;
	}
	/** 打开本地图片 */
	public void openLocalPic() {

		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		mActivity.startActivityForResult(i, 0);

	}


	
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
        
        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        
        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }
	

	
	
	
	/**
	 * 将Bitmap保存在本地
	 * 
	 * @param mBitmap
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public String saveBitmap(Bitmap mBitmap) {

		try {
			String sdCardPath = "";
			if (hasSdCard()) {
				sdCardPath = Environment.getExternalStorageDirectory()
						.getPath();
			} else {

			}

			String filePath = sdCardPath + "/" + "myImg/";

			Date date = new Date(System.currentTimeMillis());

			SimpleDateFormat sdf = Utils.setSimpleDateFormat("yyyyMMddHHmmss");// 时间格式-显示方式

			String imgPath = filePath + sdf.format(date) + ".png";

			File file = new File(filePath);

			if (!file.exists()) {
				file.mkdirs();
			}
			File imgFile = new File(imgPath);

			if (!imgFile.exists()) {
				imgFile.createNewFile();
			}

			FileOutputStream fOut = new FileOutputStream(imgFile);

			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();

			if (fOut != null) {

				fOut.close();
			}
			return imgPath;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
