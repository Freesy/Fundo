package com.szkct.weloopbtsmartdevice.util;

import android.annotation.SuppressLint;

import com.szkct.weloopbtsmartdevice.data.CustomDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	private static SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");

	public static int getMonthDays(int year, int month) {
		if (month > 12) {
			month = 1;
			year += 1;
		} else if (month < 1) {
			month = 12;
			year -= 1;
		}
		int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int days = 0;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			arr[1] = 29; // 闰年2月29天
		}

		try {
			days = arr[month - 1];
		} catch (Exception e) {
			e.getStackTrace();
		}

		return days;
	}

	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static int getLastDateYear(int index) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - index);
		String  mcurDate = getDateFormat.format(calendar.getTime());   // 2017-06-26
		int mYear = Integer.valueOf(mcurDate.substring(0,4));
		return mYear;
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static int getLastDateMonth(int index) {  // int index
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - index);
		String  mcurDate = getDateFormat.format(calendar.getTime());   // 2017-06-26
		int mMonth = Integer.valueOf(mcurDate.substring(5,7));
		return mMonth;
	}


	public static int getCurrentMonthDay() {  //
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static int getCurrentMonthLastOneDay(int index) {  //
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - index);
		String  mcurDate = getDateFormat.format(calendar.getTime());   // 2017-06-26
		int mRiqi = Integer.valueOf(mcurDate.substring(8,10));
		return mRiqi;
	}

	public static int getWeekDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	public static CustomDate getNextSunday() {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7 - getWeekDay() + 1);
		CustomDate date = new CustomDate(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		return date;
	}

	public static int[] getWeekSunday(int year, int month, int day, int pervious) {
		int[] time = new int[3];
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.add(Calendar.DAY_OF_MONTH, pervious);
		time[0] = c.get(Calendar.YEAR);
		time[1] = c.get(Calendar.MONTH) + 1;
		time[2] = c.get(Calendar.DAY_OF_MONTH);
		return time;

	}

	public static int getWeekDayFromDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return week_index;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDateFromString(int year, int month) {
		String dateString = year + "-" + (month > 9 ? month : ("0" + month))
				+ "-01";
		Date date = null;
		try {
			SimpleDateFormat sdf = Utils.setSimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}

	public static boolean isToday(CustomDate date) {
		return (date.year == DateUtil.getYear()
				&& date.month == DateUtil.getMonth() && date.day == DateUtil
					.getCurrentMonthDay());
	}

	public static boolean isCurrentMonth(CustomDate date) {
		return (date.year == DateUtil.getYear() && date.month == DateUtil
				.getMonth());
	}


	/**
	 * @param usingVer  正在使用的，保存在本地的版本号
	 * @param serverVer     获取服务器上的版本号
	 * @return           返回true 则为需要更新版本
	 */
	public static boolean versionCompare(String usingVer,String serverVer){
		boolean result = false;
		if(usingVer != null && serverVer != null && !usingVer.equals("") && !serverVer.equals("")
				&& (usingVer.substring(0,1).equals("V") || usingVer.substring(0,1).equals("v"))
				&& (serverVer.substring(0,1).equals("V") || serverVer.substring(0,1).equals("v"))){   //判断两个版本号是否为标准版本号

			String beforeStr = usingVer.substring(1, usingVer.length());
			String nowStr = serverVer.substring(1, serverVer.length());

			String beforeStrs[] = beforeStr.split("\\.");
			String nowStrs[] = nowStr.split("\\.");

			StringBuffer sbBefore = new StringBuffer();
			StringBuffer sbNow = new StringBuffer();
			for (int i = 0; i < beforeStrs.length; i++) {
				sbBefore.append(beforeStrs[i]);
			}
			for (int i = 0; i < nowStrs.length; i++) {
				sbNow.append(nowStrs[i]);
			}
			result = Integer.parseInt(String.valueOf(sbBefore)) < Integer.parseInt(String.valueOf(sbNow));
		}

		Log.e(DateUtil.class.getSimpleName(), "# versionCompare # 在用版本：usingVer = " + usingVer + "  服务器版本：serverVer = " + serverVer);
		return result;
	}


	public static String getCurrentTimeZone()
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ZZ", Locale.ENGLISH);
		String timeZone = simpleDateFormat.format(new Date());
		int times = (int) (Double.parseDouble(timeZone.substring(3,5)) / 60 * 100);
		if(times == 100){
			times = 0;
		}
		return timeZone.substring(0,3) + "." + times;
		//return createGmtOffsetString(false,false,tz.getRawOffset());
	}
	private static String createGmtOffsetString(boolean includeGmt,
											   boolean includeMinuteSeparator, int offsetMillis) {
		int offsetMinutes = offsetMillis / 60000;
		String sign = "";
		if (offsetMinutes < 0) {
			sign = "-";
			offsetMinutes = -offsetMinutes;
		}
		StringBuilder builder = new StringBuilder(9);
		if (includeGmt) {
			builder.append("GMT");
		}
		builder.append(sign);
		appendNumber(builder, 0, offsetMinutes / 60);
		if (includeMinuteSeparator) {
			builder.append(':');
			appendNumber(builder, 2, offsetMinutes % 60);
		}
		return builder.toString();
	}

	private static void appendNumber(StringBuilder builder, int count, int value) {
		String string = Integer.toString(value);
		for (int i = 0; i < count - string.length(); i++) {
			builder.append('0');
		}
		builder.append(string);
	}
}
