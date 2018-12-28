package com.szkct.weloopbtsmartdevice.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * 日期时间选择控件 使用方法： private TextView curdate_tv;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * 
 * @author laiqinglin
 * @since 2015/3/25
 */
public class DateTimePickDialogUtil {
	
	private static String downtime_str,uptime_str;

	private static String lastDay;
	//private static String TAG = "DateTimePickDialogutil";

	private static SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");


	
	
	public static String dealDateDown(String change_date){
		
		
		String year_str = change_date.substring(0, 4);
		int year = Integer.parseInt(year_str); 
		String month_str = change_date.substring(5, 7);
		int month = Integer.parseInt(month_str);		
		String day_str = change_date.substring(8, 10);
		int change_day = Integer.parseInt(day_str);				
		if (change_day <= 10) {
			if (change_day != 1) {
				int int_down_day = change_day - 1;
				String str_down_day = "0" + int_down_day;
				downtime_str = change_date.substring(0, 8)
						+ str_down_day + change_date.substring(10, 10);
				
			}
			if (change_day == 1) {
				if (month == 1) {
					int theyear = year-1;
					downtime_str = theyear +"-12-31";
				}else {
					int themonth = month-1;
					int onemonthday = DateUtil.getMonthDays(year, themonth);	//获取这个month的天数。
					if(themonth<=9){
						downtime_str = year+"-0"+themonth+"-"+onemonthday;
					}else {
						downtime_str = year+"-"+themonth+"-"+onemonthday;
					}
				}
				
			}
		} else {
			int int_down_day = change_day - 1;
			downtime_str = change_date.substring(0, 8) + int_down_day
					+ change_date.substring(10, 10);
			
		}
		
		return downtime_str;
		
	}
	public static String dealDateUp(String change_date){
		String day_str = change_date.substring(8, 10);
		int change_day = Integer.parseInt(day_str);
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);// 选中日期为当前月的上个月的最后一天
		lastDay = getDateFormat.format(cale.getTime());
		if (change_date.equals(lastDay)) {

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, 0);
			c.set(Calendar.DAY_OF_MONTH, 1);// 设置当前日期为本月第一天
			uptime_str = getDateFormat.format(c.getTime());					

	
		}
		else {
			String year_str = change_date.substring(0, 4);
			int year = Integer.parseInt(year_str); 
			String month_str = change_date.substring(5, 7);
			int month = Integer.parseInt(month_str);
			int onemonthday = DateUtil.getMonthDays(year, month); 

			if (change_day < 9) {
				int int_up_day = change_day + 1;
				String str_up_day = "0" + int_up_day;
				System.out.println("str_up_day=" + str_up_day);
				uptime_str = change_date.substring(0, 8) + str_up_day
				+ change_date.substring(10, 10);
		

				//curdatetv.setText(uptime_str);
			} else if ((change_day >= 9) && (change_day <= onemonthday)) {
				if(change_day == onemonthday){	//当前显示的日期是选择的月份的最后一天
					
				
					
					if (month == 12) {
						int setyear = year+1;
						uptime_str = setyear+"-01-"+"01";
					}else{
						int themonth = month+1;
						uptime_str = year+"-"+"0"+themonth+"-01";
					}					
				}else{
					int int_up_day = change_day + 1;

					uptime_str = change_date.substring(0, 8) + int_up_day
					+ change_date.substring(10, 10);
				}
				
		
				
			}
		}
		
			return uptime_str;
		
		}

}