package com.szkct.weloopbtsmartdevice.util;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageCacheUtil {
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
//			roundPx = width ;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;
//			right = width*2;
//			bottom = width*2;
//
//			height = width*2;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
//			dst_right = width*2;
//			dst_bottom = width*2;
		} else {
			roundPx = height / 2;
//			roundPx = height;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
//			bottom = height*2;
//			width = height*2;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
//			dst_right = height*2;
//			dst_bottom = height*2;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		Bitmap output = Bitmap.createBitmap(width/2, height/2, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}
	
	/**
	 * 对图片内存溢出的处理
	 */
	public static Bitmap getResizedBitmap(String path, byte[] data,
			Context context,Uri uri, int target, boolean width) {
		Options options = null;

		if (target > 0) {

			Options info = new Options();
			info.inJustDecodeBounds = false;
			
			decode(path, data, context,uri, info);
			
			int dim = info.outWidth;
			if (!width)
				dim = Math.max(dim, info.outHeight);
			int ssize = sampleSize(dim, target);

			options = new Options();
			options.inSampleSize = ssize;

		}

		Bitmap bm = null;
		try {
			bm = decode(path, data, context,uri, options);
		} catch(Exception e){
			e.printStackTrace();
		}
		return bm;

	}
	
	public static Bitmap decode(String path, byte[] data, Context context,
			Uri uri, BitmapFactory.Options options) {

		Bitmap result = null;

		if (path != null) {

			result = BitmapFactory.decodeFile(path, options);

		} else if (data != null) {

			result = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);

		} else if (uri != null) {
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = null;

			try {
				inputStream = cr.openInputStream(uri);
				result = BitmapFactory.decodeStream(inputStream, null, options);
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}
	
	
	private static int sampleSize(int width, int target){	    	
	    	int result = 1;	    	
	    	for(int i = 0; i < 10; i++){	    		
	    		if(width < target * 2){
	    			break;
	    		}	    		
	    		width = width / 2;
	    		result = result * 2;	    		
	    	}	    	
	    	return result;
	    }
	
	/**
	 * 压缩图片
	 * 
	 * 
	 */
		public static int max = 0;
		public static boolean act_bool = true;
		public static List<Bitmap> bmp = new ArrayList<Bitmap>();

		// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
		public static List<String> drr = new ArrayList<String>();

		// TelephonyManager tm = (TelephonyManager) this
		// .getSystemService(Context.TELEPHONY_SERVICE);

		public static Bitmap revitionImageSize(String path) throws IOException {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(
					new File(path)));

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// Bitmap btBitmap=BitmapFactory.decodeFile(path);
			// System.out.println("原尺寸高度："+btBitmap.getHeight());
			// System.out.println("原尺寸宽度："+btBitmap.getWidth());
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int i = 0;
			Bitmap bitmap = null;
			while (true) {
				if ((options.outWidth >> i <= 800)
						&& (options.outHeight >> i <= 800)) {
					in = new BufferedInputStream(
							new FileInputStream(new File(path)));
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, options);
					break;
				}
				i += 1;
			}
			// 当机型为三星时图片翻转
//			bitmap = Photo.photoAdapter(path, bitmap);
//			System.out.println("-----压缩后尺寸高度：" + bitmap.getHeight());
//			System.out.println("-----压缩后尺寸宽度度：" + bitmap.getWidth());
			return bitmap;
		}

		public static Bitmap getLoacalBitmap(String url) {
			try {
				FileInputStream fis = new FileInputStream(url);
				return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * 
		 * @param x
		 *            图像的宽度
		 * @param y
		 *            图像的高度
		 * @param image
		 *            源图片
		 * @param outerRadiusRat
		 *            圆角的大小
		 * @return 圆角图片
		 */
		public static Bitmap createFramedPhoto(int x, int y, Bitmap image, float outerRadiusRat) {
			// 根据源文件新建一个darwable对象
			Drawable imageDrawable = new BitmapDrawable(image);

			// 新建一个新的输出图片
			Bitmap output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			// 新建一个矩形
			RectF outerRect = new RectF(0, 0, x, y);

			// 产生一个红色的圆角矩形
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.RED);
			canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

			// 将源图片绘制到这个圆角矩形上
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			imageDrawable.setBounds(0, 0, x, y);
			canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
			imageDrawable.draw(canvas);
			canvas.restore();

			return output;
		}
}
