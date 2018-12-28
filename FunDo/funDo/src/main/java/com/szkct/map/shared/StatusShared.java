package com.szkct.map.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 记录状态信息保存
 * 
 * @author xiaodai
 * 
 */
public class StatusShared {
	private Context mContext;

	public StatusShared(Context mContext) {
		super();
		this.mContext = mContext;
	}

	
	/**
	 * 保存地图类型（1为google,2为高德地图）
	 */
	public void setSavaMapType(int type) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"STATUSFLAG", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("mapType",type);
		editor.commit();// 提交修改
	}
	
	/**
	 * 获取地图类型
	 */
	public int getMapType() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"STATUSFLAG", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		int mapType = sharedPreferences.getInt("mapType",1);
		return mapType;
		
	}



	/**
	 * 保存开始运动倒计时
	 */
	public void savaCount(String count) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"STATUSFLAG", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("count",count);
		editor.commit();// 提交修改
	}


	/**
	 * 获取倒计时
	 */
	public String getCount() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"STATUSFLAG", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		String count = sharedPreferences.getString("count","3");
		return count;

	}




//	/**
//	 * 保存运动模式（1.健走模式2.户外跑模式）
//	 */
//	public void savaSportMode(int sportMode) {
//		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
//				"STATUSFLAG", Context.MODE_PRIVATE);
//		Editor editor = sharedPreferences.edit();
//		editor.putInt("sportMode",sportMode);
//		editor.commit();// 提交修改
//	}
//
//
//	/**
//	 * 获取运动模式 1到7
//	 */
//	public int getSportMode() {
//		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
//				"STATUSFLAG", Context.MODE_PRIVATE);
//		Editor editor = sharedPreferences.edit();
//		int count = sharedPreferences.getInt("sportMode",1);
//		return count;
//
//	}


	/**
	 * 注销所有记录
	 * 
	 */
	public void deleteCarInfo() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"STATUSFLAG", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

}
