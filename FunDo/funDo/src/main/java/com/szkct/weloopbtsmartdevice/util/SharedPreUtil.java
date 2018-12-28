package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreUtil {

	private String  arrspeed;
	/**
	 * 每周运动天数
	 */
	public final static String WEEKSPORTDAY = "WEEKSPORTDAY";
	/**
	 * 每天运动时长
	 */
	public final static String DAYSPORTTIME = "DAYSPORTTIME";
	/**
	 *取睡眠数据最后一次睡眠的时间
	 */
	public final static String HEART="HEART";

	/**
	 *取睡眠数据最后一次睡眠的时间
	 */
	public final static String SLEEP="SLEEP";
	/**
	 *取运动数据最后一条数据的时间
	 */
	public final static String SPORT="SPORT";

	/**
	 *取运动数据最后一条数据的时间
	 */
	public final static String SPORT_BT="SPORT_BT";

	/**
	 *取血压数据最后一条数据的时间
	 */
	public final static String BLOOD_PRESSURE="BLOOD_PRESSURE";

	/**
	 *取SharedPreferences的key值。大部分数据都是存在这个里面
	 */
	public final static String USER="USER";

	public final static String LAST7DAY="LAST7DAY";     //后7天
	public final static String LAST7DAY_DATE = "LAST7DAY_DATE";  //后7天的日期

	public final static String ISFIRSTSYNCDATA="ISFIRSTSYNCDATA";     //是否第一次同步数据
	public final static String SYNCED = "SYNCED";              //是否第一次同步过

	public final static String BLEWATCHDATA="BLEWATCHDATA";     //存手环实时数据
	public final static String MTKWATCHDATA="MTKWATCHDATA";     //存MTK实时数据

    public final static String TIME = "TIME";              //存储数据的日期
    public final static String RUN = "RUN";              //存实时计步数据
    public final static String CALORIE = "CALORIE";     //存实时卡路里数据
    public final static String DISTANCE = "DISTANCE";     //存实时距离数据
    public final static String WATCHTIME = "WATCHTIME";     //存实时时间数据
public final static String WATCHSYNCTIME = "WATCHSYNCTIME";     //存同步时间数据
    public final static String SYNRUN = "SYNRUN";         //存同步后的计步数据
    public final static String SYNCALORIE = "SYNCALORIE";         //存同步后的卡路里数据
    public final static String SYNDISTANCE = "SYNDISTANCE";         //存同步后的距离数据
    public final static String SYNDATASIZE = "SYNDATASIZE";         //存同步后的数据个数

	public final static String GOALSET="GOALSET";     //达到计步目标提示   ""代表没有提示,"1"代表有提示

	public final static String USERNAME="USERNAME";
	public final static String PASSWORD="PASSWORD";

	public final static String IGNORELIST="IGNORELIST";   //判断推送应用消息黑名单，true：有选择推送应用消息

//	public final static String NICKNAME="NICKNAME";

	/**
	 *需要展示的设备的数据的mac地址。//后面被产品去掉，
	 */
	public final static String SHOWMAC="SHOWMAC";

	/**
	 *需要展示的设备的数据的mac地址的总和。//后面被产品去掉，
	 */
	public final  static String ALLMAC="ALLMAC";

	/**
	 * 蓝牙名称
	 */
	public final static String NAME="NAME";   // TODO ---- 蓝牙名称另取    ？？？？？、   NAME---- 已被用户名占用
	/**
	 *mac地址
	 */
	public final static String MAC="MAC";
	public final static String MACNAME = "MACNAME";
	public final static String TEMP_MAC="TEMP_MAC";
	public final static String TEMP_SECURE="TEMP_SECURE";

	public final static String PAGE_WELCOME_STARTNUM="PAGE_WELCOME_STARTNUM";  // 记录APP启动次数
	public final static String IS_USER_COMMENT="IS_USER_COMMENT";  // 用户是否评论标记   0：未评论 1：已评论
	/**
	 * 蓝牙UUID
	 */
	public final static String UUID="UUID";
	/**
	 *最后一个连接的蓝牙的地址
	 */
	public final static String MYMAC="MYMAC";


	public final static String WATCH = "WATCH";  //TODO ---- 手表手环平台 1：6572平台；2：ble手环平台；3：MTK平台
	public final static String ECG_SPEED = "ECG_SPEED";
	public final static String ECG_GAIN = "ECG_GAIN";
	public final static String ECG_RATE = "ECG_RATE";
	public final static String ECG_DIMENSION = "ECG_DIMENSION";

	public final static String TEMP_WATCH = "TEMP_WATCH";


    public final static String FIRMEWAREINFO = "FIRMEWAREINFO";  //TODO ---- 固件信息存储   key值
    public final static String FIRMEWAREVERSION = "FIRMEWAREVERSION";  //TODO ---- 固件信息版本   name值
    public final static String FIRMEWARETYPE = "FIRMEWARETYPE";  //TODO ---- 固件平台   name值
    public final static String FIRMEWARECODE = "FIRMEWARECODE";  //TODO ---- 固件序列号   name值
	public final static String ISFIRMEWARING = "ISFIRMEWARING";  //TODO ---- 固件序列号   name值
	public final static String PROTOCOLCODE = "PROTOCOLCODE";    //协议版本号  V1.3.37
    /**
     * 用户id。后面去掉了
     */
    public final static String MID = "MID";
    public final static String FACE = "FACE";  // 图片名称
    public final static String SEX = "SEX";
    public final static String FACEURL = "FACEURL";  // 图片对应的地址
    public final static String FACEPATH = "FACEPATH";  // 图片对应的本地地址

	public final static String WEIGHT="WEIGHT";
	public final static String WEIGHT_US="WEIGHT_US";

	public final static String HEIGHT="HEIGHT";    // TODO --- 目前只考虑了公制的单位保存
	public final static String HEIGHT_IN="HEIGHT_IN";  //// ?????
	public final static String HEIGHT_FT="HEIGHT_FT";  //// ?????

	public final static String BIRTH="BIRTH";
	public final static String EMAIL="EMAIL";
	public final static String EXPERIENCE="EXPERIENCE";

	public final static String ISEMAILLOGIN="ISEMAILLOGIN";   // 是否是邮箱登录
	public final static String CUREMAILNUM="CUREMAILNUM";   // 当前登录的邮箱账号
	public final static String CUREMAILPASSWORD="CUREMAILPASSWORD";   // 当前登录的邮箱账号的密码

	public final static String ISPHONELOGIN="ISPHONELOGIN";   // 是否是手机号登录
	public final static String CURPHONENUM="CURPHONENUM";   // 当前登录的手机号
	public final static String CURPHONEPASSWORD="CURPHONEPASSWORD";   // 当前登录的手机账号的密码

	public final static String ISSAMEUSER="ISSAMEUSER";

	public final static String PHONEVERIFICODE="PHONEVERIFICODE";  // 短信验证码
	public final static String PHONEVERIFICODECOUNTTIME="PHONEVERIFICODECOUNTTIME";  // 短信验证码倒计时时间


	/**
	 *个人信息修改的时间。每次同步回合手表传过来的时间对比。相互同步身高体重性别参数
	 */
	public final static String CHANGE_TIME="CHANGE_TIME";
	public final static String STATUSFLAG="STATUSFLAG";


	public final static String OPENID="OPENID";
	public final static String TOKEN="TOKEN";
	public final static String RUN_DAY="RUN_DAY";
	public final static String RUN_WEEK="RUN_WEEK";
	public final static String RUN_MONTH="RUN_MONTH";
	public final static String FEET_RUN="FEET_RUN";
	public final static String FEET_WALK="FEET_WALK";

	public final static String SCORE="SCORE";
	public final static String MSG="MSG";
	public final static String SOSNAME="SOSNAME";
	public final static String SOSNUMBER="SOSNUMBER";
//	public final static String ISHEART="ISHEART";
	public final static String YES="YES";
	public final static String NO="NO";
	public final static String METRIC="METRIC";
	public final static String SPORTMODE="SPORTMODE";
	public final static String SPORTSTART="SPORTSTART";//运动开始 结束标志

    public final static String MAP_TYPE_SATELLITE = "MAP_TYPE_SATELLITE"; // 地图显示为卫星模式
    public final static String MAP_TYPE_NORMAL = "MAP_TYPE_NORMAL"; // 地图显示为普通模式


    public final static String DATA = "DATA";
    public final static String BEST_STEP_NO = "BEST_STEP_NO";
    public final static String BEST_STEP_TIME = "BEST_STEP_TIME";

    public final static String BEST_DISTANCE_NO = "BEST_DISTANCE_NO";
    public final static String BEST_DISTANCE_TIME = "BEST_DISTANCE_TIME";

    public final static String BEST_KAL_NO = "BEST_KAL_NO";
    public final static String BEST_KAL_TIME = "BEST_KAL_TIME";

	public final static String ALL_STEP_NO="ALL_STEP_NO";
	public final static String ALL_STEP_TIME="ALL_STEP_TIME";
	public final static String ALL_STEP_SIZE="ALL_STEP_SIZE";
	
	public final static String ALL_DISTANCE_NO="ALL_DISTANCE_NO";
	public final static String ALL_DISTANCE_TIME="ALL_DISTANCE_TIME";
	public final static String ALL_DISTANCE_SIZE="ALL_DISTANCE_SIZE";
	
	public final static String ALL_KAL_NO="ALL_KAL_NO";
	public final static String ALL_KAL_TIME="ALL_KAL_TIME";
	public final static String ALL_KAL_SIZE="ALL_KAL_SIZE";
	
	/*public final static String THEME_BREAK="THEME_BREAK";*/
	
	public final static String THEME_WHITE="THEME_WHITE";
	
	public final static String WATCHCODE="WATCHCODE";
	public final static String WATCHTYPE="WATCHTYPE";
	
	public final static String WATCH_ASSISTANT_FIND_PHONE="WATCH_ASSISTANT_FIND_PHONE";
	
	public final static String WATCH_ASSISTANT_CAMERA="WATCH_ASSISTANT_CAMERA";

	public final static String KENGDIEDEXIAOMI="KENGDIEDEXIAOMI";
	
	public final static String CLOCK_SKIN_MODEL="CLOCK_SKIN_MODEL";

	public final static String CB_RUNSETTING_VOICE="CB_RUNSETTING_VOICE";
	public final static String CB_RUNSETTING_SCREEN="CB_RUNSETTING_SCREEN";
	public final static String CB_RUNSETTING_AUTOPAUSE="CB_RUNSETTING_AUTOPAUSE";
	public final static String CB_RUNSETTING_AUTOSTOP="CB_RUNSETTING_AUTOSTOP";
	
	public final static String TV_MOTIONSETTING_GOAL="TV_MOTIONSETTING_GOAL";
	public final static String TV_MOTIONSETTING_VOICE_SETTING="TV_MOTIONSETTING_VOICE_SETTING";
	public final static String TV_MOTIONSETTING_RECIPROCAL="TV_MOTIONSETTING_RECIPROCAL";
	public final static String TV_MOTIONSETTING_MAPSETTING="TV_MOTIONSETTING_MAPSETTING";
	public final static String TV_MOTIONSETTING_MAPTOWSETTING="TV_MOTIONSETTING_MAPTOWSETTING";
	
	public final static String MotionGoal="MotionGoal";
	
	public final static String KALGOAL="KALGOAL";
	public final static String TIMEGOAL="TIMEGOAL";
	public final static String DISTANCEGOAL="DISTANCEGOAL";
	
	public final static String DISTANCEGOAL_NUMBER="DISTANCEGOAL_NUMBER";
	public final static String TIMEGOAL_NUMBER="TIMEGOAL_NUMBER";
	
	public final static String KALGOAL_NUMBER="KALGOAL_NUMBER";

	public final static String DEFAULT_AUTOSLEEP="DEFAULT_AUTOSLEEP";
	public final static String DEFAULT_HEART_RATE="DEFAULT_HEART_RATE";
	public final static String DEFAULT_LOCATION="DEFAULT_LOCATION";

    public final static String MAP_TYPE = "MAP_TYPE";  // 地图显示时的模式

    public final static String CB_ISSELECT_ALLPERSONAPP = "CB_ISSELECT_ALLPERSONAPP";
    public final static String CB_ISSELECT_ALLSYSTEMAPP = "CB_ISSELECT_ALLSYSTEMAPP";

	public final static String CB_ISOPEN_APPKEEPALIVE = "CB_ISOPEN_APPKEEPALIVE";

	public final static String TB_RAISE_BRIGHT = "TB_RAISE_BRIGHT"; //抬手亮屏

    public final static String TB_CALL_NOTIFY = "TB_CALL_NOTIFY"; //来电通知
    public final static String TB_SMS_NOTIFY = "TB_SMS_NOTIFY"; //短信通知
	public final static String TB_CAMERA_NOTIFY = "TB_CAMERA_NOTIFY"; //拍照开关

	public final static String SIT_SWITCH = "SIT_SWITCH"; //久坐开关
    public final static String SIT_REPEAT_WEEK = "SIT_REPEAT_WEEK"; //久坐重复
    public final static String SIT_START_TIME= "SIT_START_TIME"; //久坐开始
    public final static String SIT_STOP_TIME = "SIT_STOP_TIME"; //久坐结束
    public final static String SIT_TIME = "SIT_TIME"; //久坐时间
    public final static String SIT_STEP = "SIT_STEP"; //久坐步数

	public final static String HEART_SWITCH = "HEART_SWITCH"; //心率开关
    public final static String HEART_REPEAT_WEEK = "HEART_REPEAT_WEEK"; //心率重复
    public final static String HEART_START_TIME = "HEART_START_TIME"; //心率开始小时
	public final static String HEART_START_TIME_MIN = "HEART_START_TIME_MIN"; //心率开始分钟
    public final static String HEART_STOP_TIME = "HEART_STOP_TIME"; //心率结束小时
	public final static String HEART_STOP_TIME_MIN = "HEART_STOP_TIME_MIN"; //心率结束分钟
    public final static String HEART_FREQUENCY = "HEART_FREQUENCY"; //心率频率

	public final static String NO_SWITCH = "NO_SWITCH"; //勿扰开关
    public final static String NO_START_TIME = "NO_START_TIME"; //勿扰开始小时
	public final static String NO_START_TIME_MIN = "NO_START_TIME_MIN"; //勿扰开始分钟
    public final static String NO_STOP_TIME = "NO_STOP_TIME"; //勿扰结束小时
	public final static String NO_STOP_TIME_MIN = "NO_STOP_TIME_MIN"; //勿扰结束分钟

	public final static String DRINK_SWITCH = "DRINK_SWITCH"; //喝水开关
    public final static String DRINK_REPEAT_WEEK = "DRINK_REPEAT_WEEK"; //喝水重复
    public final static String DRINK_START_TIME = "DRINK_START_TIME"; //喝水开始小时
	public final static String DRINK_START_TIME_MIN = "DRINK_START_TIME_MIN"; //喝水开始分钟
    public final static String DRINK_STOP_TIME = "DRINK_STOP_TIME"; //喝水结束小时
	public final static String DRINK_STOP_TIME_MIN = "DRINK_STOP_TIME_MIN"; //喝水结束分钟
    public final static String DRINK_FREQUENCY = "DRINK_FREQUENCY"; //喝水频率


	public final static String GESTURE_HAND = "GESTURE_HAND"; // 左右手
    public final static String RAISE_BRIGHT = "RAISE_BRIGHT"; //抬手亮屏
    public final static String FANWAN_BRIGHT = "FANWAN_BRIGHT"; //翻腕亮屏

    public final static String ALARM_MODE = "ALARM_MODE"; //提醒模式

    public final static String ALARM_SPREAD = "ALARM_SPREAD"; //闹钟分布
    public final static String ALARM_FREQUENCY = "ALARM_FREQUENCY"; //闹钟凭率 9位 ，第1-7位是周期 第8位是开关，第九位是模式
    public final static String ALARM_HOUR = "ALARM_HOUR"; //闹钟时
    public final static String ALARM_MIN = "ALARM_MIN"; //闹钟分
    public final static String ALARM_CHANGE = "ALARM_CHANGE"; //改变

	public final static String BLE_CLICK_STOP = "BLE_CLICK_STOP"; //BLE手动断开
	public final static String UNBOND = "UNBOND"; //解绑

	public final static String LAST_BO = "LAST_BO"; //改
	public final static String LAST_BP_MIN = "LAST_BP_MIN"; //改
	public final static String LAST_BP_MAX = "LAST_BP_MAX";


	public final static String UNIT_MEASURE = "UNIT_MEASURE"; //度量单位
	public final static String UNIT_DISTANCE = "UNIT_DISTANCE"; //距离单位
	public final static String UNIT_WEIGHT = "UNIT_WEIGHT"; //体重单位
	public final static String UNIT_HEIGHT = "UNIT_HEIGHT"; //身高单位
	public final static String UNIT_TEMPERATURE = "UNIT_TEMPERATURE"; //温度单位

	public final static String LOCAL_WATCHINFO = "LOCAL_WATCHINFO"; //本地存储的型号适配表

	public final static String WEATHER = "WEATHER";           //天气 -----key
	public final static String WEATHER_UPDATE_TIMES = "WEATHER_UPDATE_TIMES";           //天气更新时间

    public static void savePre(Context context, String name, String key, String value) {
        SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
        Editor editor = preference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readPre(Context context, String name, String key) {
        if (null == context) {
            Log.e("", "共享参数context为空");
            return "";
        }
        /*if(key.equals(MID)){
			return "test";
		}*/
		SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
		return preference.getString(key, "");
	}
	public static String readPre(Context context,String name,String key,String type){
		if(null == context){
			Log.e("s","共享参数context为空");
			return type;
		}
	
		SharedPreferences preference = context.getSharedPreferences(name, Context.MODE_APPEND);
		
		return preference.getString(key, type);
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
	
	public static int getwatchcode(Context context){   // todo ????
		int code=0;
		
			try {
				code=Utils.toint(readPre(context, USER, WATCHCODE));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		return code;
		
	}
	public static void setwatchcode(Context context,int code){  // todo ????
		
		
			try {
				savePre(context, USER, WATCHCODE,code+"");
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
		
	}
	public static String getwatchtype(Context context){
		String type="F";
		
			try {
				if(!readPre(context, USER, WATCHTYPE).equals("")){
					type=readPre(context, USER, WATCHTYPE);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		return type;
		
	}
	public static void setwatchtype(Context context,String type){


        try {
            savePre(context, USER, WATCHTYPE, type);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context, String name, String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(Context context, String name, String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

}
