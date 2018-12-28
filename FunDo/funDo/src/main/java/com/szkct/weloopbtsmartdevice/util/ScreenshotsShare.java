package com.szkct.weloopbtsmartdevice.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.szkct.map.utils.ScreenShotHelper;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  截屏
 * @author chendalin
 *
 */
public class ScreenshotsShare {
	/**
	 * 获取指定Activity的截屏，保存到png文件
	 * @param activity
	 * @return
	 */
	static View view;

//	private static File f;

	public  static Bitmap takeScreenShot(Activity activity,int pageIndex) {

	/*	// View是你需要截图的View     
	    View view = activity.getWindow().getDecorView();     
	    view.setDrawingCacheEnabled(true);     
	    view.buildDrawingCache();     
	    Bitmap b1 = view.getDrawingCache();     
	    // 获取状态栏高度     
	    Rect frame = new Rect();    
	    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);     
	    int statusBarHeight = frame.top;     
	    System.out.println(statusBarHeight);     
	    // 获取屏幕长和高     
	    int width = activity.getWindowManager().getDefaultDisplay().getWidth();     
	    int height = activity.getWindowManager().getDefaultDisplay().getHeight();     
	    // 去掉标题栏     
	    // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);    
	    System.out.println("ScreenshotsShare b1 = "+b1);
	    Log.e("ScreenshotsShare", "ScreenshotsShare statusBarHeight = "+statusBarHeight);
	    Log.e("ScreenshotsShare", "ScreenshotsShare width = "+width);
	    Log.e("ScreenshotsShare", "ScreenshotsShare height = "+height);	
	    Log.e("ScreenshotsShare", "ScreenshotsShare height-statusBarHeight = "+(height-statusBarHeight));
	    Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height     
	        - statusBarHeight);     
	    view.destroyDrawingCache();     
	    return b;  */

		view = activity.getWindow().getDecorView();
	    view.setDrawingCacheEnabled(true);     
	    view.buildDrawingCache();     
	    Bitmap b1 = view.getDrawingCache();

		if(pageIndex == 1 || pageIndex == 2 || pageIndex == 3 || pageIndex == 5 || pageIndex == 6 || pageIndex == 7|| pageIndex == 8){ // 主页面分享，不压缩
			return b1;   // b1
		}else {
			Log.e("picSize ====", "图片原始大小为---- "+b1.getByteCount());
			// TODO --- 压缩图片
			Bitmap mCompressPic = compressScale(b1);  // b1
//			int ddd = mCompressPic.getByteCount();   // 852480/1024 = 832.5
			Log.e("picSize ====", "图片压缩后的大小为---- " + mCompressPic.getByteCount());
			return mCompressPic;
		}
	}

	public  static Bitmap takeScreenShot(View view) {

	/*	// View是你需要截图的View
	    View view = activity.getWindow().getDecorView();
	    view.setDrawingCacheEnabled(true);
	    view.buildDrawingCache();
	    Bitmap b1 = view.getDrawingCache();
	    // 获取状态栏高度
	    Rect frame = new Rect();
	    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	    int statusBarHeight = frame.top;
	    System.out.println(statusBarHeight);
	    // 获取屏幕长和高
	    int width = activity.getWindowManager().getDefaultDisplay().getWidth();
	    int height = activity.getWindowManager().getDefaultDisplay().getHeight();
	    // 去掉标题栏
	    // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
	    System.out.println("ScreenshotsShare b1 = "+b1);
	    Log.e("ScreenshotsShare", "ScreenshotsShare statusBarHeight = "+statusBarHeight);
	    Log.e("ScreenshotsShare", "ScreenshotsShare width = "+width);
	    Log.e("ScreenshotsShare", "ScreenshotsShare height = "+height);
	    Log.e("ScreenshotsShare", "ScreenshotsShare height-statusBarHeight = "+(height-statusBarHeight));
	    Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
	        - statusBarHeight);
	    view.destroyDrawingCache();
	    return b;  */

		int width = view.getWidth();
		int height = view.getBottom();     //底部留多30个像素应该会好看些
//		Bitmap b1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Bitmap fullBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(fullBitmap);
		/**设置背景色*/
//        canvas.drawColor(Color.parseColor("#ffffff"));
		canvas.drawColor(Color.parseColor("#00292C30"));
		view.setDrawingCacheEnabled(true);
        Bitmap bt = view.getDrawingCache();
			canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);


	    view.setDrawingCacheEnabled(true);
	    view.buildDrawingCache();
	    Bitmap b1 = view.getDrawingCache();
//			Log.e("picSize ====", "图片原始大小为---- "+b1.getByteCount());
			// TODO --- 压缩图片
//			Bitmap mCompressPic = compressScale(b1);  // b1
//			int ddd = mCompressPic.getByteCount();   // 852480/1024 = 832.5
//			Log.e("picSize ====", "图片压缩后的大小为---- " + mCompressPic.getByteCount());
//			return mCompressPic;
			return b1;
	}

//	public static  Bitmap compass(View view)
//	{
//		int width = view.getWidth();
//		int height = view.getBottom();     //底部留多30个像素应该会好看些
//
//		Bitmap fullBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(fullBitmap);
//		/**设置背景色*/
////        canvas.drawColor(Color.parseColor("#ffffff"));
//		canvas.drawColor(Color.parseColor("#00292C30"));
//		view.setDrawingCacheEnabled(true);
//		canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
//
//		Bitmap screenShotBitmap = ScreenShotHelper.compressScale(fullBitmap);  // mCompressPic
//
//	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Bitmap getViewBpWithoutBottom(View v) {
		if (null == v) {
			return null;
		}
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache();
		if (Build.VERSION.SDK_INT >= 11) {
			v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(),
					View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
					v.getHeight(), View.MeasureSpec.EXACTLY));

			v.layout((int) v.getX(), (int) v.getY(),
					(int) v.getX() + v.getMeasuredWidth(),
					(int) v.getY() + v.getMeasuredHeight());
		} else {
			v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		}

		Bitmap bp = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(),
				v.getMeasuredHeight() - v.getPaddingBottom());
		v.setDrawingCacheEnabled(false);
		v.destroyDrawingCache();
		return bp;
	}

	public static Bitmap getViewBitmap(Context ctx, ScrollView sv) {
		if (null == sv) {
			return null;
		}
		// enable something
		sv.setVerticalScrollBarEnabled(false);
		sv.setVerticalFadingEdgeEnabled(false);
		sv.scrollTo(0, 0);
		sv.setDrawingCacheEnabled(true);
		sv.buildDrawingCache(true);
		Bitmap b = getViewBpWithoutBottom(sv);

		/**
		 * vh : the height of the scrollView that is visible <BR>
		 * th : the total height of the scrollView <BR>
		 **/
		int vh = sv.getHeight();
		int th = sv.getChildAt(0).getHeight();

		/** the total height is more than one screen */
		Bitmap temp = null;
		if (th > vh) {
			int w = getScreenW(ctx);
			int absVh = vh - sv.getPaddingTop() - sv.getPaddingBottom();
			do {
				int restHeight = th - vh;
				if (restHeight <= absVh) {
					sv.scrollBy(0, restHeight);
					vh += restHeight;
					temp = getViewBp(sv);
				} else {
					sv.scrollBy(0, absVh);
					vh += absVh;
					temp = getViewBpWithoutBottom(sv);
				}
				b = mergeBitmap(vh, w, temp, 0, sv.getScrollY(), b, 0, 0);
			} while (vh < th);
		}

		// restore somthing
		sv.scrollTo(0, 0);
		sv.setVerticalScrollBarEnabled(true);
		sv.setVerticalFadingEdgeEnabled(true);
		sv.setDrawingCacheEnabled(false);
		sv.destroyDrawingCache();
		return b;
	}

	public static Bitmap getViewBp(View v) {
		if (null == v) {
			return null;
		}
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache();
		if (Build.VERSION.SDK_INT >= 11) {
			v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(),
					View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
					v.getHeight(), View.MeasureSpec.EXACTLY));
			v.layout((int) v.getX(), (int) v.getY(),
					(int) v.getX() + v.getMeasuredWidth(),
					(int) v.getY() + v.getMeasuredHeight());
		} else {
			v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		}
		Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

		v.setDrawingCacheEnabled(false);
		v.destroyDrawingCache();
		return b;
	}

	public static int getScreenW(Context ctx) {
		int w = 0;
		if (Build.VERSION.SDK_INT > 13) {
			Point p = new Point();
			((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getSize(p);
			w = p.x;
		} else {
			w = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getWidth();
		}
		return w;
	}

	public static Bitmap mergeBitmap(int newImageH, int newIamgeW,
									 Bitmap background, float backX, float backY, Bitmap foreground,
									 float foreX, float foreY) {
		if (null == background || null == foreground) {
			return null;
		}
		// create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
		Bitmap newbmp = Bitmap.createBitmap(newIamgeW, newImageH,
				Bitmap.Config.RGB_565);
		Canvas cv = new Canvas(newbmp);
		// draw bg into
		cv.drawBitmap(background, backX, backY, null);
		// draw fg into
		cv.drawBitmap(foreground, foreX, foreY, null);
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储

		return newbmp;
	}

	/**
	 * 图片按比例大小压缩方法
	 *
	 * @param image （根据Bitmap图片压缩）
	 * @return
	 */
	public static Bitmap compressScale(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 30, baos);

		// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, 30, baos);// 这里压缩50%，把压缩后的数据存放到baos中
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
		// float hh = 800f;// 这里设置高度为800f
		// float ww = 480f;// 这里设置宽度为480f

		float hh = 200f;
		float ww = 200f;

		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be; // 设置缩放比例   8  3
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565

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
		image.compress(Bitmap.CompressFormat.PNG, 30, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 20;

		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}

		int dd = baos.toByteArray().length / 1024;

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 把Bitmap转Byte
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public  static void savePicture(Bitmap bm, String filePath, String fileName) {
		Log.e("picSize ====", "保存到SD卡时图片的大小为---- " + bm.getByteCount());
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		if (!f.exists()) {
			f.mkdirs();
		}
		FileOutputStream fos = null;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		try {
			fos = new FileOutputStream(filePath + File.separator + fileName);
			if (null != fos) {
				String mapType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_NORMAL);
				if(mapType.equals(SharedPreUtil.MAP_TYPE_SATELLITE)){ //卫星模式
					bm.compress(Bitmap.CompressFormat.PNG, 60, fos);
				}else {
					bm.compress(Bitmap.CompressFormat.PNG, 60, fos);  // 	  b.compress(Bitmap.CompressFormat.PNG, 30, fos);
				}

				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if(view!=null){
				view.destroyDrawingCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  static void saveViewPicture(Bitmap bm, String filePath, String fileName) {
		Log.e("picSize ====", "保存到SD卡时图片的大小为---- " + bm.getByteCount());
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		if (!f.exists()) {
			f.mkdirs();
		}
		FileOutputStream fos = null;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		try {
			fos = new FileOutputStream(filePath + File.separator + fileName);
			if (null != fos) {
				String mapType = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAP_TYPE, SharedPreUtil.MAP_TYPE_NORMAL);
				if(mapType.equals(SharedPreUtil.MAP_TYPE_SATELLITE)){ //卫星模式
					bm.compress(Bitmap.CompressFormat.PNG, 60, fos);
				}else {
					bm.compress(Bitmap.CompressFormat.PNG, 60, fos);  // 	  b.compress(Bitmap.CompressFormat.PNG, 30, fos);
				}

				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if(view!=null){
				view.destroyDrawingCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分享界面固定View 图片保存
	 * 把View绘制到Bitmap上
	 * @param width 该View的宽度
	 * @param height 该View的高度
	 * @return 返回Bitmap对象
	 * add by csj 13-11-6
	 */
	public static Bitmap getShareViewBitmap(View comBitmap, int width, int height) {
		Bitmap bitmap = null;
		if (comBitmap != null) {
			comBitmap.clearFocus();
			comBitmap.setPressed(false);

			boolean willNotCache = comBitmap.willNotCacheDrawing();
			comBitmap.setWillNotCacheDrawing(false);

			// Reset the drawing cache background color to fully transparent
			// for the duration of this operation
			int color = comBitmap.getDrawingCacheBackgroundColor();
			comBitmap.setDrawingCacheBackgroundColor(0);
			float alpha = comBitmap.getAlpha();
			comBitmap.setAlpha(1.0f);

			if (color != 0) {
				comBitmap.destroyDrawingCache();
			}

			int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
			int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
			comBitmap.measure(widthSpec, heightSpec);
			//comBitmap.layout(0, 0, width, height);

			comBitmap.buildDrawingCache();
			Bitmap cacheBitmap = comBitmap.getDrawingCache();
			if (cacheBitmap == null) {
				Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
						new RuntimeException());
				return null;
			}
			bitmap = Bitmap.createBitmap(cacheBitmap);
			// Restore the view
			comBitmap.setAlpha(alpha);
			comBitmap.destroyDrawingCache();
			comBitmap.setWillNotCacheDrawing(willNotCache);
			comBitmap.setDrawingCacheBackgroundColor(color);
		}
		return bitmap;
	}


}
