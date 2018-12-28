package com.szkct.weloopbtsmartdevice.util;

/**
 * This class is used for recording some useful constants, and will collect
 * these info from remote device later.
 */
public final class Constants {
	// Null app name
	public static final String NULL_TEXT_NAME = "(unknown)";
	// 通用URL // http://183.57.41.90:8080/
	public static String BASE_URL = "http://www.fundo.cc/";
	public static final String URLLOGPREFIX = "http://www.fundo.cc/export/login1.php?"; // 登陆
	public static final String URLPHTOTPREFIX = "http://www.fundo.cc/member/faces/big/"; // 头像下载

	public static final String URLINQUIRYRUNPREFIX = "http://www.fundo.cc/export/run.php?"; // 运动数据查询
	public static final String URLLINQUIRYSLEEPPREFIX = "http://www.fundo.cc/export/sleep.php?"; // 睡眠数据查询
	public static final String URLINUQIRYHEARTPREFIX = "http://www.fundo.cc/export/heart.php?"; // 心率数据查询

	public static final String URLUPLOADSLEEPPREFIX = "http://www.fundo.cc/export/sleep_upload.php?"; // 睡眠数据上传
	public static final String URLMANYUPLOADRUNPREFIX = "http://www.fundo.cc/export/run_upload.php?"; // 运动多条数据上传
	public static final String URLUPLOADRUNPREFIX = "http://www.fundo.cc/export/heart.php?"; // 运动数据上传
	public static final String URLUPLOADHEARTPREFIX = "http://www.fundo.cc/export/heart_upload.php?"; // 心率数据上传

	public static final String URLREVISEINFOPREFIX = "http://www.fundo.cc/export/user_modify.php?"; // 修改用户信息
	public static final String URLREVISEPHTOTPREFIX = "http://www.fundo.cc/export/user_face_upload.php?"; // 修改用户头像

	public static final String URLGETUSERINFO = "http://www.fundo.cc/export/user_info.php?"; // 获取用户资料

	public static final String SUGGESTIONFEEDBACK = "http://www.fundo.cc/export/sys_suggest.php?";
	public static final String FINDBACKPASSWORD = "http://www.fundo.cc/export/rpwd_email.php?";

	//public static final String CHECK_Advertising = "http://121.201.66.218:8080/fundogroup/content/activity/protal/guideActivitys.do?type=";//测试
	public static final String CHECK_Advertising = "http://39.108.209.154:8080/fundogroup/content/activity/protal/guideActivitys.do?type="; // 线上

	public static final String CHECK_Login_next = "http://121.201.66.218:8080/fundogroup/content/activity/protal/guideType.do";

	public static final String CHECK_moren = "https://mp.weixin.qq.com/s/KgcZYWvDIYa6N0rA-NsJQQ";//H5跳转的默认网址

	// 附近的人
	public static String NEARBY_FRIENDS = BASE_URL
			+ "export/friend_map_search.php?";
	// 用户头像
	public static String FRIENDIMAGEURI = BASE_URL + "member/faces/big/";
	// 添加好友
	public static String ADD_FRIEDN = BASE_URL + "export/friend_add.php?";
	// 获取添加的好友请求
	public static String ALREADYFRIEND = BASE_URL
			+ "export/friend_request.php?";
	// 用户好友列表
	public static String USERFRIENDLIST = BASE_URL + "export/friend.php?";
	// 附近的人的详情
	public static String NEARBYDETAILS = BASE_URL + "export/friend_info.php?";
	// 发送位置给好友
	public static String REPORT_POSITION = BASE_URL
			+ "export/friend_position_send.php?";
	
	// 邀请好友参加活动
		public static String ACTIVITY_INVITE = BASE_URL
				+ "export/activity_invite.php?";
		
		// 活动详情
		public static String ACTIVITY_DETAIL = BASE_URL
				+ "export/activity_dtl.php?";
		// 解散活动
		public static String ACTIVITYS_DISSO = BASE_URL
				+ "export/activity_disso.php?";
	
	//搜索好友
	public static String SEARCHFRIEND = BASE_URL+"export/friend_search.php?";
	//删除好友
	public static String DELETEFRIEND =BASE_URL+"export/friend_del.php?";
	// 发送消息
	public static String SEND_MESSAGE = BASE_URL + "export/user_msg_send.php?";
	//好友排行
	public static String FRIENDRANKURL = BASE_URL+"export/ranking_friend.php?";
	//全部排行
	public static String ALLPLAYERRANK = BASE_URL+"export/ranking.php?";
	//获取活动
	public static String GETACTIVITYURL = BASE_URL +"export/activity.php?";
	//查找活动
	public static String SEARCHACTIVITYURL = BASE_URL +"export/activity_search.php?";
	// 创建活动
		public static String ACTIVITY_CREATE = BASE_URL
				+ "export/activity_add.php?";

	public static String GPX_UPLOAD = BASE_URL + "export/run_upload_gpx1.php?";
	// 查询运动数据
	public static String SEACH_RUN = BASE_URL + "export/run_count.php?";
	// 用户运动数据
	public static String USERSPORT_DATAS = BASE_URL + "export/run.php?";
	// Broadcast Action
	public static final String RUNBROADCAST = "sendBroadRunIdentification";
	public static final String SLEEPBROADCAST = "sendBroadSleepIdentification";
	public static final String RECEIVERACITON_UPDATEDATA = "com.fendong.sports.update";// 数据更新
	public static final String SETTITLE = "title";

	// App icon size
	public static final int APP_ICON_WIDTH = 40;
	public static final int APP_ICON_HEIGHT = 40;
	public static final int NOTIFYMINIHEADERLENTH = 8;
	public static final int NOTIFYSYNCLENTH = 4;
	// Message content size
	public static final int TEXT_MAX_LENGH = 256;
	public static final int TICKER_TEXT_MAX_LENGH = 64;
	public static final int TITLE_TEXT_MAX_LENGH = 128;

	public static final int TICKER_TEXT_MAX_LENGH_OTHERDEVICE = 1024; // TODO --- 序列号 ： 548

	public static final String TEXT_POSTFIX = "...";
	// 天气接口     todo --- 雅虎天气
	public final static String WEATHER_URL = "http://query.yahooapis.com/v1/public/yql";
	// app 更新
	//public final static String APP_UPDATE = "http://www.kctsv.com/update/fundowear/";

	public final static String APP_UPDATE = "http://app.fundo.xyz:8001/version/api/version.php?type=apk&flag=6";   // http://app.fundo.xyz:8001/version/api/version.php?type=apk&flag=1&model=
	// TODO ---- 微信APPID和appKEY
	public static final String WXAPPID = "wx20af627b5fa97ced";
	public static final String WXAPPKEY = "824bc2767676e03606b2f3395c7b9a77";
	// QQ APPID appkey
	public static final String APPID = "1105311365";
	public static final String APPKEY = "iKR5VGJyL1FxISHj";
	
	public static final String FACEBOOKAPPID ="514424712061025";
	public static final String FACEBOOKKEY ="288a3b8d7603c24db31c850e5e1a551e";

	// 轨迹规划，数据库相关
	public static final String DATABASE_NAME = "sports";// 数据库名
	public static final int DBTABASE_VERSION = 19;// 数据库版本
	public static final String DATABASE_TABLE_SPORT = "sport";// 表名
	public static final String DATABASE_TABLE_SLEEP = "sleep";// 睡眠
	public static final String DATABASE_TABLE_SIT = "sit";// 久坐
	public static final String DATABASE_TABLE_HEART = "heart";// 心率
	public static final String DATABASE_TABLE_SHEBEI = "device";// 设备
	public static final String DATABASE_TABLE_GPS_POINTS = "gps_points";// 记录GPS运动经纬度
	public static final String DATABASE_TABLE_GPS_RUN = "gps_run";// 记录GPS运动数据
	// 建表语句356872
	public static final String DATABASE_TABLE_USER_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ DATABASE_TABLE_SPORT
			+ "("
			+ "id  INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "mid  varchar(30) NOT NULL DEFAULT '1',"
			+ "dateTime timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',"
			+ "step  INTEGER NOT NULL DEFAULT '0',"
			+ "time_upload timestamp,"
			+ "calorie  INTEGER NOT NULL DEFAULT '0',"
			+ "mile  INTEGER NOT NULL DEFAULT '0',"
			+ "times  INTEGER NOT NULL DEFAULT '0',"
			+ "type  INTEGER NOT NULL DEFAULT '0',"
			+ "upload_ok INTEGER NOT NULL DEFAULT '0',"
			+ "upload_id INTEGER NOT NULL DEFAULT '0',"// 运动上传后的ID
			+ "gps_fileName varchar(30),"// GPS文件名称
			+ "app_w INTEGER NOT NULL DEFAULT '0'" + ")";

	// 建表语句
	public static final String DATABASE_TABLE_GPS_POINTS_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ DATABASE_TABLE_GPS_POINTS
			+ "("
			+ "id  INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "mid  varchar(30) NOT NULL DEFAULT '1',"
			+ "gps_runID  INTEGER NOT NULL DEFAULT '1',"
			+ "bd_lat varchar(30)," // 百度地图纬度
			+ "bd_lon varchar(30)," // 百度地图经度
			+ "ele varchar(30),"// 海拔
			+ "mile INTEGER NOT NULL DEFAULT '0',"// 距离
			+ "dateTime varchar(80),"// 时间
			+ "speed varchar(30)," + "gps_lat varchar(30)," // GPS纬度
			+ "gps_lon varchar(30)" // GPS经度
			+ ")";
	// 建表语句
	public static final String DATABASE_TABLE_GPS_RUN_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ DATABASE_TABLE_GPS_RUN
			+ "("
			+ "id  INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "mid  varchar(30) NOT NULL DEFAULT '1',"
			+ "dateTime timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',"
			+ "step  INTEGER NOT NULL DEFAULT '0',"
			+ "time_upload timestamp,"
			+ "calorie  INTEGER NOT NULL DEFAULT '0',"
			+ "mile  INTEGER NOT NULL DEFAULT '0',"
			+ "times  INTEGER NOT NULL DEFAULT '0',"
			+ "type  INTEGER NOT NULL DEFAULT '0',"
			+ "upload_ok INTEGER NOT NULL DEFAULT '0',"
			+ "upload_id INTEGER NOT NULL DEFAULT '0',"// 运动上传后的ID
			+ "gps_fileName varchar(100),"// GPS文件名称
			+ "app_w INTEGER NOT NULL DEFAULT '0'" + ")";





	public static final String APP_PACKAGE_NAME_QQ = "com.tencent.mobileqq";
	public static final String APP_PACKAGE_NAME_WECHAT = "com.tencent.mm";

	// com.android.incallui 努比亚 魅蓝note2 荣耀畅玩四 荣耀六 华为G9 小米四 三星(玉莲、S4) 红米 OPPO
	public static final String APP_PACKAGE_NAME_TEL = "com.android.incallui";
	public static final String APP_PACKAGE_NAME_TEL_OTHER = "com.android.dialer";//乐视


	// com.android.mms 乐视、魅蓝note2 华为G9 小米四 三星(玉莲、S4) vivo 红米 OPPO
	// cn.nubia.mms          努比亚
	// com.android.contacts  荣耀畅玩四 荣耀六
	public static final String APP_PACKAGE_NAME_SMS_COMMON = "com.android.mms";
	public static final String APP_PACKAGE_NAME_SMS_NUBIA = "cn.nubia.mms";
	public static final String APP_PACKAGE_NAME_SMS_HUAWEI = "com.android.contacts";

	public static final String APP_PACKAGE_NAME_WHATSAPP = "com.whatsapp";
	public static final String APP_PACKAGE_NAME_MESSENGER = "com.facebook.orca";
	public static final String APP_PACKAGE_NAME_GOOGLE_MESSENGER = "com.google.android.apps.messaging";
	public static final String APP_PACKAGE_NAME_TWITTER = "com.twitter.android";
	public static final String APP_PACKAGE_NAME_LINKEDIN = "com.linkedin.android";
	public static final String APP_PACKAGE_NAME_INSTAGRAM = "com.instagram.android";
	public static final String APP_PACKAGE_NAME_FACEBOOK = "com.facebook.katana";


	public static final String FUNDO_UNIFIED_DOMAIN = "http://app.fundo.xyz:8001";
	public static final String URL_FIRMWARE_UPDATE = FUNDO_UNIFIED_DOMAIN + "/version/api/version.php?type=fota&flag=2&model="; //固件升级接口http://183.57.41.90

	public static String FUNDO_UNIFIED_DOMAIN_test = "http://wx.funos.cn:8080/";//暂时测试
	public static String LAUCH_PAGE = "fundo/page/selectPageList.do?";//加载广告或活动类接口
	public static String TJ = "fundo/page/insertStatistics.do?";//广告统计
	// appName  0:分动；1：分动手环，2：分动穿戴,3:funfit,4:funrun
	// mobileSystem 安卓：1；ios：2
	public static String APP_CHECK_UPDATE = "fundo/apkFromApp/selectUpgradeStatus.do?appName=%s&mobileSystem=1&uuid=%s";//填写两个参数：appname 和 uuid

	public static String BIAOPAN_PUSH = "fundo/dial/requestDialList.do?adaptiveNumber=";//表盘推送     fundo/dial/getDialList.do?adaptiveNumber=301      http://wx.funos.cn:8080/fundo/dial/requestDialList.do?adaptiveNumber=427

	public static final String TJ_AD_YQ_CLICK = "ad_yq_click";//云晴-国外广告-点击
	public static final String TJ_AD_GDT_CLICK = "ad_gdt_click";//广点通-国内广告-点击
	public static final String TJ_AD_GDT_PAGE_SHOW = "ad_gdt_page_show";//广点通-国内广告展示
	public static final String TJ_AD_YQ_PAGE_SHOW = "ad_yq_page_show";//云晴-国外广告展示
	public static final String TJ_AD_GDT_LOAD_FALURE = "ad_gdt_load_falure";//广点通--加载失败
	public static final String TJ_AD_YQ_LOAD_FALURE = "ad_yq_load_falure";//云晴--加载失败
}
