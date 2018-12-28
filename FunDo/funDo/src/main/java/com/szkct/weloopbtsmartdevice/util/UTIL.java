package com.szkct.weloopbtsmartdevice.util;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UTIL {
	public static void savePre(Context context,String name,String key,String value){
		//创建共享参数对象
		SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
		//获取编辑器
		Editor editor = preference.edit();
		editor.putString(key, value);
		//提交数据 
		editor.commit();
	}
	
	public static String readPre(Context context,String name,String key){
		//创建共享参数对象
		SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
		return preference.getString(key, "");
	}
	
	public static void delPre(Context context,String name,String key){
		SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
		Editor editor = preference.edit();
		if(key==null||"".equals(key)){
			editor.clear();
		}else{
			editor.remove(key);
		}
		editor.commit();
	}
	
	public static byte[] read(InputStream fis){
		try {
			System.out.println(2);
			int len = 0;
			byte[]data = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while((len = fis.read(data))!=-1){
				bos.write(data, 0, len);
			}
			fis.close();
			byte arrData[] = bos.toByteArray();
			return arrData;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getSubtractDay(String s){
		String date[] = s.split("-");
		int year = Integer.parseInt(date[0]);
		int month= Integer.parseInt(date[1]);
		int day= Integer.parseInt(date[2]);
		String s1 = getPreDay(year, month, day);
		String date1[] = s1.split("-");
		String monthStr = date1[1];
		String dayStr = date1[2];
		if(monthStr.length()==1){
			monthStr = "0"+monthStr;
		}
		if(dayStr.length()==1){
			dayStr = "0"+dayStr;
		}
		return date1[0] + "-" + monthStr + "-" + dayStr;
	}
	
	public static String getAddDay(String s){
		String date[] = s.split("-");
		int year = Integer.parseInt(date[0]);
		int month= Integer.parseInt(date[1]);
		int day= Integer.parseInt(date[2]);
		String s1 = getNextDay(year, month, day);
		String date1[] = s1.split("-");
		String monthStr = date1[1];
		String dayStr = date1[2];
		if(monthStr.length()==1){
			monthStr = "0"+monthStr;
		}
		if(dayStr.length()==1){
			dayStr = "0"+dayStr;
		}
		return date1[0] + "-" + monthStr + "-" + dayStr;
	}
	
	/**
	 * 获得某年某月某日的前一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getPreDay(int year, int month, int day)
	{

		if (day != 1)
		{ // 判断是否为某月月初
			day--;
		}
		else
		{
			if (month != 1)
			{ // 如果不是1月的话，那么就是上月月末
				month--;
				day = getMaxDay(year, month);
			}
			else
			{ // 如果是1月的话，那么就是上年的12月31日
				year--;
				month = 12;
				day = 31;
			}
		}
		return year + "-" + month + "-" + day;
	}
	
	/**
	 * 获得某年某月某日的后一天
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getNextDay(int year, int month, int day)
	{
		if (day != getMaxDay(year, month))
		{ // 判断是否为某月月末
			day++;
		}
		else
		{
			if (month != 12)
			{ // 如果不是12月的话，那么就是次月月初
				month++;
				day = 1;
			}
			else
			{ // 如果是12月的话，那么就是次年的1月1日
				year++;
				month = day = 1;
			}
		}
		return year + "-" + month + "-" + day;
	}
	
	/**
	 * 获得某年、某月的最大天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMaxDay(int year, int month)
	{
		switch (month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				return 31;
			case 4:
			case 6:
			case 9:
			case 11:
				return 30;
			case 2:
				return (IsLeapYear(year) ? 29 : 28);
			default:
				return -1;
		}
	}
	/**
	 * 获取系统默认语言   chendalin add
	 * @return
	 */
    public static String getLanguage() {
		//获取系统当前使用的语言  
		Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
		return language; 
	}
    /**
	 * 获取系统默认语言 chendalin add
	 * @return
	 */
	public static String getCountry() {
		//获取系统当前使用的语言  
		Locale locale = Locale.getDefault();
        String country = locale.getCountry();
		return country; 
	}
	/**
	 * 判断一年是否为闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean IsLeapYear(int year)
	{
		// 能被400整出，或者能被4整出但不能被100整数的数(年份)，才是闰年
		return ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0)));
	}
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
