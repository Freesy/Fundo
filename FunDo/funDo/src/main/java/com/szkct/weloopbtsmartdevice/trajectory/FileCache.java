package com.szkct.weloopbtsmartdevice.trajectory;

import java.io.File;

import android.content.Context;

public class FileCache
{

	private File cacheDir;

	public FileCache(Context context, String path)
	{
		/**
		 * 如果有SD卡则在SD卡中建一个face的目录存放缓存的图片 没有SD卡就放在系统的缓存目录中
		 */
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
		{
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(), path);

		}
		else
		{
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists())
		{
			cacheDir.mkdirs();
			// boolean s = cacheDir.mkdirs();
			// TipsToast.makeText(context, "创建文件夹：" + s,
			// TipsToast.LENGTH_LONG).show();
		}

	}

	public File getFile(String url)
	{
		// 将url的hashCode作为缓存的文件名
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.

		String filename = url.substring(url.lastIndexOf("/") + 1);
		// String filename=String.valueOf(url.hashCode());
		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear()
	{
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}