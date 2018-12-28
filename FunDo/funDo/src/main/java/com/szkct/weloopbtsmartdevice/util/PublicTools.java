package com.szkct.weloopbtsmartdevice.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.kct.funcare.application.KctFuncareApplication;

/**
 * 
 * 
 * 类名称：PublicTools 类描述：公共方法
 * wangshuyu
 * 修改备注：
 * 
 * @version 1.0.0
 * 
 */
public class PublicTools {

	/**
	 * 
	 * dip2px：根据手机的分辨率从 dp 的单位 转成为 px(像素) (这里描述这个方法适用条件 – 可选)
	 *
	 * wangshuyu
	 * 
	 * @param context
	 * @param dpValue
	 * @return int
	 * @exception
	 * @since 1.0.0
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 
	 * px2dip：根据手机的分辨率从 px(像素) 的单位 转成为 dp (这里描述这个方法适用条件 – 可选)
	 * wangshuyu
	 * 
	 * @param context
	 * @param pxValue
	 * @return int
	 * @exception
	 * @since 1.0.0
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 
	 * isGetNULL：空值判断 (这里描述这个方法适用条件 – 可选)
	 *
	 * wangshuyu
	 * 
	 * @return true 是空；false 非空
	 * @exception
	 * @since 1.0.0
	 */
	public static boolean isGetNULL(Object obj) {
		if ("".equals(obj) || obj == null || "null".equals(obj)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * isIdCardCode：判断身份证格式是否正确(18位)--正则表达式
	 * wangshuyu
	 * 
	 * @param idCardCode
	 * @return boolean
	 * @exception
	 * @since 1.0.0
	 */
	public static boolean isIdCardCode(String idCardCode) {
		String regPattern = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
		return idCardCode.matches(regPattern);
	}

	/**
	 * 
	 * isBankCode：判断银行卡号位数是否正确
	 *
	 * wangshuyu
	 * 
	 * @param bankCode
	 * @return boolean
	 * @exception
	 * @since 1.0.0
	 */
	public static boolean isBankCode(String bankCode) {
		String regPattern = "[0-9]{19}";
		return bankCode.matches(regPattern);
	}

	/**
	 * 
	 * IsValidMobileNo：手机号码判断-正则表达式
	 *
	 * wangshuyu
	 * 
	 * @param MobileNo
	 * @return boolean
	 * @exception
	 * @since 1.0.0
	 */
	public static boolean IsValidMobileNo(String MobileNo) {
		String regPattern = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
		return MobileNo.matches(regPattern);
	}

	/**
	 * 
	 * getLocalVersionName：获取当前apk版本 (这里描述这个方法适用条件 – 可选)
	 *
	 * wangshuyu
	 * 
	 * @param context
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public static String getLocalVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			// 包名
			String packageName = context.getPackageName();

			PackageInfo packageInfo = packageManager.getPackageInfo(
					packageName, 0);
			versionName = packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}

	/**
	 * 
	 * GetNetBoolean：判断用户联网状态
	 * (这里描述这个方法适用条件 – 可选)
	 * wangshuyu
	 * @return true:网络连接正常 false:网络未连接
	 * @exception
	 * @since  1.0.0
	 */

	/*public static boolean GetNetBoolean() {
		ConnectivityManager connManager = (ConnectivityManager) KctFuncareApplication.getInstance()
				.getSystemService(KctFuncareApplication.getInstance().CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo == null ? false : networkInfo.isAvailable();
	}*/


//	/**
//	 * 判断用户名的格式是否合法(2~10个字符以内 含中英文下划线数字)
//	 * @param username
//	 * @return
//	 */
//	public static boolean isUserName(String username){
//		String regPattern = "^[0-9A-Za-z_\\u4e00-\\u9fa5]{2,10}$";
//		return username.matches(regPattern);
//	}

	/**
	 * 判断呢称的格式是否合法(1~20个字符以内 含中英文数字)
	 * @param username
	 * @return
	 */
	public static boolean isNickName(String username){  //lx
		String regPattern = "^[0-9A-Za-z\\u4e00-\\u9fa5]{1,20}$";
//		String regPattern = "^[0-9a-zA-Z\\u4e00-\\u9fa5]{1,10}$";
		return username.matches(regPattern);
	}

	//lx
//	public static boolean isMyChineseName(String ChineseNo) {
//		String regPattern = "^[0-9A-Za-z\\u4e00-\\u9fa5]{2,10}$";   //"^[\u4e00-\u9fa5 ]{2,10}$";
//		return ChineseNo.matches(regPattern);
//	}

	
	/***
	 * 
	 * 是否是中文(2~10个汉字)
	 *
	 * wangshuyu
	 *
	 * @param ChineseNo
	 * @return
	 *boolean
	 * @exception
	 * @since  1.0.0
	 */
	public static boolean isChineseName(String ChineseNo) {
		String regPattern = "^[\u4e00-\u9fa5 ]{2,10}$";
		return ChineseNo.matches(regPattern);
	}

	/**
	 * 英文数字都可以（2~20个字符）
	 * @param username
	 * @return
	 */
	public static boolean isEnglishOrNumber(String username){
		String regPattern = "^[a-zA-Z0-9]{2,20}$";
		return username.matches(regPattern);
	}

	/**
	 * 判断是否是英文（20个以内字符包含空格 以英文字母开头）
	 *
	 * @param EnglishNo
	 * @return
	 */
	public static boolean isEnglishName(String EnglishNo){
		String regPattern = "^[a-zA-Z]{1}[a-zA-Z\\s]{0,19}$";
		return EnglishNo.matches(regPattern);
	}

	/**
	 * 判断密码格式是否正确(6~15个字符)
	 *
	 * @param Password
	 * @return
	 */
	public static boolean isPassword(String Password){
		String regPattern = "^[0-9a-zA-Z]\\w{5,14}$";
		return Password.matches(regPattern);
	}

	/**
	 * 是否是纯数字
	 * @param UserName
	 * @return
	 */
	public static boolean isNumber(String UserName){
		String regPattern = "^[0-9]*$";
		return UserName.matches(regPattern);
	}

	/**
	 * 判断IMEI号码格式是否正确(15个数字)
	 * @param DeviceId
	 * @return
	 */
	public static boolean isIMEINumber(String DeviceId){
		String regPattern = "^\\d{15}$";
		return DeviceId.matches(regPattern);
	}

	/**
	 * 检测是否有emoji表情
	 *
	 * @param source
	 * @return
	 */
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是Emoji
	 *
	 * @param codePoint 比较的单个字符
	 * @return
	 */
	public static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
				(codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
				&& (codePoint <= 0x10FFFF));
	}


	/**
	 * bitmap转换成字节数组
	 * @param bmp
	 * @param needRecycle
	 * @return
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	//"重复"对应的字符串
	/*public static void setWeekCircleTypeText(int[] weekCircleType, TextView textview) {
		String circleType = circleTypeToStr(weekCircleType);  //解析第一级模式值
		if(circleType!=null){
			textview.setText(circleType);
			return;
		}
		StringBuilder sb = new StringBuilder();
		*//*for (int i = 0; i < weekCircleType.length; i++) {
			if (weekCircleType[i] == 1 || weekCircleType[i] == 2) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append(KctFuncareApplication.getApplicationInstance().getResources().getStringArray(R.array.circletype_short)[i]); //解析第二级模式值
			}
		}*//*
		for (int i = 0; i < weekCircleType.length; i++) {
			if (weekCircleType[i] == 1 ) { //|| weekCircleType[i] == 2
				if (i > 0) {
					sb.append(" ");//增加空格
				}
				sb.append(KctFuncareApplication.getApplicationInstance().getResources().getStringArray(R.array.circletype_short)[i]); //解析第二级模式值
			}
		}
		textview.setText(sb.toString());
	}*/

	/*public static String setWeekCircleTypeTextMy(int[] weekCircleType) {
		String circleType = circleTypeToStr(weekCircleType);
		if(circleType!=null){

			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < weekCircleType.length; i++) {
			if (weekCircleType[i] == 1) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append(KctFuncareApplication.getApplicationInstance().getResources().getStringArray(R.array.circletype_short)[i]);
			}
		}
//		textview.setText(sb.toString());
		return sb.toString();
	}*/

	//0,1,2,3 工作日,周末,每天,一次
	/*public static int[] typeToCircleTypeArray(int type){
		int[] circleType = null;
		switch (type){
			case 0:
				circleType = new int[]{1, 1, 1, 1, 1, 0, 0};//工作日
				break;
			case 1:
				circleType = new int[]{0, 0, 0, 0, 0, 1, 1};//周末
				break;
			case 2:
				circleType = new int[]{1, 1, 1, 1, 1, 1, 1};//每天
				break;
			case 3:
				circleType = new int[]{0, 0, 0, 0, 0, 0, 0};//一次
				break;
		}
		return circleType;
	}*/

	public static int[] typeToCircleTypeArray(int type){
		int[] circleType = null;
		switch (type){
			case 0:
				circleType = new int[]{0, 0, 0, 0, 0, 0, 0};//一次
				break;
			case 1:
				circleType = new int[]{1, 1, 1, 1, 1, 1, 1};//每天
				break;
			case 2:
				circleType = new int[]{1, 1, 1, 1, 1, 0, 0};//工作日
				break;
			case 3:
				circleType = new int[]{0, 0, 0, 0, 0, 1, 1};//周末
				break;
		}
		return circleType;
	}

	//特殊"重复"对应数字
	public static int circleTypeToInt(int[] weekCircleType){
		int[] circleType1 = new int[]{1, 1, 1, 1, 1, 0, 0};//工作日
		int[] circleType2 = new int[]{0, 0, 0, 0, 0, 1, 1};//周末
		int[] circleType3 = new int[]{1, 1, 1, 1, 1, 1, 1};//每天
		int[] circleType4 = new int[]{0, 0, 0, 0, 0, 0, 0};//一次
		if(judgeArray(weekCircleType,circleType1)){
			return 2;//工作日  0
		}
		if(judgeArray(weekCircleType,circleType2)){
			return 3;//周末  1
		}
		if(judgeArray(weekCircleType,circleType3)){
			return 1;//每天  2
		}
		if(judgeArray(weekCircleType,circleType4)){
			return 0;//一次  3
		}
		return -1;
	}

	//特殊"重复"对应的字符串
	/*public static String circleTypeToStr(int[] weekCircleType){
		int[] circleType1 = new int[]{1, 1, 1, 1, 1, 0, 0};//工作日
		int[] circleType2 = new int[]{0, 0, 0, 0, 0, 1, 1};//周末
		int[] circleType3 = new int[]{1, 1, 1, 1, 1, 1, 1};//每天
		int[] circleType4 = new int[]{0, 0, 0, 0, 0, 0, 0};//一次
		if(judgeArray(weekCircleType,circleType1)){
			return KctFuncareApplication.getApplicationInstance().getString(R.string.setting_alarm_clock_model_workday);//工作日
		}
		if(judgeArray(weekCircleType,circleType2)){
			return KctFuncareApplication.getApplicationInstance().getString(R.string.setting_alarm_clock_model_weekend);//周末
		}
		if(judgeArray(weekCircleType,circleType3)){
			return KctFuncareApplication.getApplicationInstance().getString(R.string.setting_alarm_clock_model_all);//每天
		}
		if(judgeArray(weekCircleType,circleType4)){
			return KctFuncareApplication.getApplicationInstance().getString(R.string.setting_alarm_clock_model_once);//一次
		}
		return null;
	}*/

	//判断两个数组是否相同
	public static boolean judgeArray(int[] arrayA, int[] arrayB) {
		for (int i = 0; i < arrayA.length; i++) {
			if (arrayA[i] != arrayB[i]) {
				return false;
			}
		}
		return true;
	}

	//将"重复" 数组变成0、1字符串
	public static String getWeekCircleType(int[] weekCircleType) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < weekCircleType.length; i++) {
			sb.append(weekCircleType[i]);
		}
		return sb.toString();
	}

	//判断包含中文的字符串的长度
	public static int getContainChineseLength(String s) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";

		if(isContainChinese(s)){//先判断是否包含中文
			// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < s.length(); i++) {
				// 获取一个字符
				String temp = s.substring(i, i + 1);
				// 判断是否为中文字符
				if (temp.matches(chinese)) {
					// 中文字符长度为2
					valueLength += 2;
				} else {
					// 其他字符长度为1(包含中文以后，字母也以2来计算)
//					valueLength += 1;
					valueLength += 2;
				}
			}
		}

		return  valueLength;
	}

	public static boolean isContainChinese(String str) {

		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	public static boolean isContainChineseW(String s){
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < s.length(); i++) {
			// 获取一个字符
			String temp = s.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				return true;
			}
		}
		return false;
	}

	/** android启动更新程序 */
	public static void installApk(String savePath,String appAndVerCode,Context context) {
		File apkfile = new File(savePath,appAndVerCode);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setAction(android.content.Intent.ACTION_VIEW);
		// intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
		// "application/vnd.android.package-archive");
		// mContext.startActivity(intent);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * get App versionCode
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context){
		PackageManager packageManager=context.getPackageManager();
		PackageInfo packageInfo;
		String versionCode="";
		try {
			packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
			versionCode=packageInfo.versionCode+"";
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * get App versionName
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context){
		PackageManager packageManager=context.getPackageManager();
		PackageInfo packageInfo;
		String versionName="";
		try {
			packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
			versionName=packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 判断某个界面是否在前台
	 *
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	public static boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}
}
