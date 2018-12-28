package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;

import com.kct.fundo.btnotification.R;


/**
 * Toast显示管理。避免多点长时间显示
 * 
 * @author Administrator
 * 
 */
public class ToastManage
{

	public static TipsToast mTipsToast = null;

	public static void showToast(Context context, CharSequence text , int iconId)
	{
		if (null == mTipsToast)
		{
			mTipsToast = TipsToast.makeText(context, text, TipsToast.LENGTH_SHORT);
			mTipsToast.show();
		}
		else
		{
			mTipsToast.cancel();
			mTipsToast = null;
			mTipsToast = TipsToast.makeText(context, text,
					TipsToast.LENGTH_SHORT);
			if(iconId == 1){
				mTipsToast.setIcon(R.drawable.tips_error);
			}else{
				mTipsToast.setIcon(R.drawable.tips_smile);
			}
			mTipsToast.show();
		}
	}

	public static void showToast(Context context, int resId)
	{
		showToast(context, context.getResources().getString(resId) , 0);
	}

	public static void showToast(Context context, int resId,int iconId)
	{
		showToast(context, context.getResources().getString(resId) , iconId);
	}

	// 直接取消显示
	public static void cancelShow()
	{
		if (null != mTipsToast)
		{
			mTipsToast.cancel();
			mTipsToast=null;
		}
	}
}
