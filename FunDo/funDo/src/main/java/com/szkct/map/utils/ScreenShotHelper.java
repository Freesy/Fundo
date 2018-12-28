package com.szkct.map.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.MapView;
import com.szkct.weloopbtsmartdevice.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yiyi.qi on 2016/10/31.
 */

public class ScreenShotHelper {
	
	
	  private static String filePath = Environment.getExternalStorageDirectory()
	            + "/appmanager/fundoShare/";
	    private static String fileName = "screenshot_analysis.png";
	    private String detailPath = filePath + File.separator + fileName;
	
    /**
    * 组装地图截图和其他View截图，并且将截图存储在本地sdcard，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
    *@param    bitmap             地图截图回调返回的结果
     *@param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
     *@param   mapView            MapView控件
     *@param   views              其他想要在截图中显示的控件
    * */
public static void saveScreenShot(final Bitmap bitmap, final ViewGroup viewContainer, final MapView mapView, final View...views){
	new Thread(){
		public void run(){
			Bitmap mCompressPic=getMapAndViewScreenShot(bitmap,viewContainer,mapView,views);
			Log.e("picSize ====", "截屏时图片原始大小为---- " + mCompressPic.getByteCount());

			// TODO --- 压缩图片

			Bitmap screenShotBitmap = compressScale(mCompressPic);  // mCompressPic
			Log.e("picSize ====", "截屏后压缩的图片大小为---- " + screenShotBitmap.getByteCount());

			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File f = new File(filePath);
				if (!f.exists()) {
				  f.mkdir();
	//    	      f.createNewFile();
				}
				FileOutputStream fos = null;
				try {
				  fos = new FileOutputStream(filePath + File.separator + fileName);
				  if (null != fos) {
					  screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
				  }
				} catch (FileNotFoundException e) {
				  e.printStackTrace();
				} catch (IOException e) {
				  e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}.start();
}

	/**
	 * 图片按比例大小压缩方法
	 *
	 * @param image （根据Bitmap图片压缩）
	 * @return
	 */
	public static Bitmap compressScale(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);

		// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出

		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;  // 1080
		int h = newOpts.outHeight;   // 1776
//		Log.i(TAG, w + "---------------" + h);
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//		 float hh = 800f;// 这里设置高度为800f
//		 float ww = 480f;// 这里设置宽度为480f

		float hh = 200f;
		float ww = 200f;  // ---- be==7

		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;

		Log.e("picSize ====", "be大小为---- " + be);

		newOpts.inSampleSize = 2; // 设置缩放比例   8  3   // ---- be -- 设为2 ok
		newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//降低图片从ARGB888到RGB565       2--1516320   3---673920 (有点模糊)
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 质量压缩方法
	 *
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		int sss = image.getByteCount();   //原始大小 852480
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 90, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 20;

//		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
//			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, 90, baos);// 这里压缩options%，把压缩后的数据存放到baos中
//			options -= 10;// 每次都减少10
//		}

		int dd = baos.toByteArray().length / 1024;
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

    /**
     * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
     *@param    bitmap             地图截图回调返回的结果
     *@param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
     *@param   mapView            MapView控件
     *@param   views              其他想要在截图中显示的控件
     * */
public static Bitmap getMapAndViewScreenShot(Bitmap bitmap, ViewGroup viewContainer,MapView mapView, View...views){
    int width = viewContainer.getWidth();
    int height = viewContainer.getHeight();
//	int width = viewContainer.getWidth()/5;
//	int height = viewContainer.getHeight()/9;
    final Bitmap screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  // ARGB_8888
    Canvas canvas = new Canvas(screenBitmap);
    canvas.drawBitmap(bitmap, mapView.getLeft(), mapView.getTop(), null);
    for (View view:views){
        view.setDrawingCacheEnabled(true);
        canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
    }

    return screenBitmap;
}





	/**
	 * 组装地图截图和其他View截图，并且将截图存储在本地sdcard，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
	 *@param    bitmap             地图截图回调返回的结果
	 *@param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
	 *@param   mapView            MapView控件
	 *@param   views              其他想要在截图中显示的控件
	 * */
	public static void savegoogleScreenShot(final Bitmap bitmap, final ViewGroup viewContainer, final com.google.android.gms.maps.MapView mapView, final View...views){
		new Thread(){
			public void run(){
				Bitmap screenShotBitmap=getgooleMapAndViewScreenShot(bitmap,viewContainer,mapView,views);
				if(Environment.getExternalStorageState().
						equals(Environment.MEDIA_MOUNTED)) {
					File f = new File(filePath);
					if (!f.exists()) {
						f.mkdir();
//    	      f.createNewFile();
					}
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(filePath + File.separator + fileName);
						if (null != fos) {
							screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
							fos.flush();
							fos.close();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
//    	    }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
	 *@param    bitmap             地图截图回调返回的结果
	 *@param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
	 *@param   mapView            MapView控件
	 *@param   views              其他想要在截图中显示的控件
	 * */
	public static Bitmap getgooleMapAndViewScreenShot(Bitmap bitmap, ViewGroup viewContainer, com.google.android.gms.maps.MapView mapView, View...views){
		int width = viewContainer.getWidth();
		int height = viewContainer.getHeight();
		final Bitmap screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(screenBitmap);
		canvas.drawBitmap(bitmap, mapView.getLeft(), mapView.getTop(), null);
		for (View view:views){
			view.setDrawingCacheEnabled(true);
			canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
		}

		return screenBitmap;
	}



}
