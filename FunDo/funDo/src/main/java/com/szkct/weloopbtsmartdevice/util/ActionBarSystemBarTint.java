package com.szkct.weloopbtsmartdevice.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.SystemBarTintManager.SystemBarConfig;

/**
 * 
 * @author chendalin
 *  状态栏和标题栏一体化封装类
 */
public class ActionBarSystemBarTint {
	 
	@TargetApi(19)
    public static void ActionBarSystemBarTintTransparent(Activity activity,View mView) {
		//状态栏和标题栏一体化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(activity,true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.app_theme_colors);
		SystemBarConfig mBarConfig = tintManager.getConfig();
		mView.setPadding(0, mBarConfig.getPixelInsetTop(true), 0, mBarConfig.getPixelInsetBottom());
	}
	
	@TargetApi(19)
    public static void ActionBarSystemBarTintTransparent(Activity activity,View mView,int color) {
		//状态栏和标题栏一体化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(activity,true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(activity);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(color);
		SystemBarConfig mBarConfig = tintManager.getConfig();
		mView.setPadding(0, mBarConfig.getPixelInsetTop(true), 0, mBarConfig.getPixelInsetBottom());
	}
	/**
     * 
     * @param on
     * 状态栏和标题栏一体化
     */
    @TargetApi(19) 
	private static void setTranslucentStatus(Activity activity,boolean on) {
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

}
