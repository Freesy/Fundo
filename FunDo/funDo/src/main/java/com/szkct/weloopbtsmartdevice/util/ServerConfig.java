package com.szkct.weloopbtsmartdevice.util;

/**
 * 
 * @author Donsen
 * @version 1.0
 * @date 2015-12-17
 * @Description 服务器的一些配置参数
 * 
 */
public class ServerConfig {    

	/**与服务器的时间差值*/
	public static long SERVER_TIME_DIFF = 0;
	
	/**应用服务器IP */
//	public static String mDefaultIpAddr = SharedUtils.readServerIp();
	/**应用服务器IP */
	public static String mDefaultIpAddr = "192.168";


	 /**应用服务器IP */
	public static int mTomcatPort = 45231; //45231 8080

	/**推送服务器IP */
//	public static String DDPUSH_IP = mDefaultIpAddr;

	/**推送服务器端口 */
	public static int DDPUSH_PORT = 9966; //9966  9961   
	
	public static String mWebAppContext = "/webSer/";
	public static String mProtocol = "http://";
	
	/** 连接超时 */
	public static final int HTTP_TIME_OUT = 30000;  ///连接超时30秒
	 
	//后台服务器地址
	//   mProtocol: http://    mDefaultIpAddr : www.fundo.cc/ api/reg_mobile.php      mTomcatPort : 45231
	public static String SERVER_URL = mProtocol + mDefaultIpAddr + ":" + mTomcatPort;
//	public static String SERVER_URL = mProtocol + SharedUtils.readServerIp() + ":" + mTomcatPort;
	
	//1登录
//	public static final String USER_LOGIN = SERVER_URL + "/webSer/app/user/Login";

//	public static final String USER_LOGIN = mProtocol + SharedUtils.readServerIp() + ":" + mTomcatPort + "/webSer/app/user/Login";  ////
//	public static final String USER_LOGIN = SERVER_URL + "/webSer/app/user/Login";

//	public static final String USER_LOGIN =mProtocol + "www.fundo.cc/api/reg_mobile.php";  //必须加mProtocol--- http://    www.fundo.cc/api/note.php

	//手机号注册   www.fundo.cc/ api/note-verify.php
public static final String USER_LOGIN =mProtocol + "www.fundo.cc/api/note-verify.php";

//	// 邮箱注册--获取验证码     www.fundo.cc/api/newrpwd_email.php
	public static final String EMAIL_REGISTER_GETCODE =mProtocol + "www.fundo.cc/api/newrpwd_email.php";
//
//	// 获取验证码---- 邮箱和手机号      www.fundo.cc/api/newrpwd_email.php
//	public static final String REGISTER_GETCODE =mProtocol + "www.fundo.cc/api/newrpwd_email.php";

	// 手机注册--获取验证码     www.fundo.cc/api/newrpwd_email.php    www.fundo.cc/api/newrpwd_email.php
//	public static final String EMAIL_REGISTER_GETCODE =mProtocol + "www.fundo.cc/api/newrpwd_email.php";

	// 邮箱注册     www.fundo.cc/api/verify.php
	public static final String EMAIL_REGISTER =mProtocol + "www.fundo.cc/api/verify.php";

	// 手机号登录
	public static final String EDIT_USERDATA =mProtocol + "wx.funos.cn:8080/fundo/user/editUser.do";

	//修改用户信息
	public static final String TO_LOGIN =mProtocol + "wx.funos.cn:8080/fundo/login/phone.do";

	//游客登陆
	public static final String TO_LOGIN_TOURIST =mProtocol + "wx.funos.cn:8080/fundo/login/tourist.do";


	// 修改密码     www.fundo.cc/api/modify_pwd.php
	public static final String CHANGE_PASSWORD =mProtocol + "www.fundo.cc/api/modify_pwd.php";


//	 邮箱登录和手机号登录     www.fundo.cc/api/login.php
//	public static final String TO_LOGIN =mProtocol + "www.fundo.cc/api/login.php";
//
//	// 修改密码     www.fundo.cc/api/modify_pwd.php
//	public static final String CHANGE_PASSWORD =mProtocol + "www.fundo.cc/api/modify_pwd.php";

	// 上传图片        www.fundo.cc/api/user_face_upload.php
	public static final String UPLOAD_PHOTO =mProtocol + "www.fundo.cc/api/user_face_upload.php";

	// 获取用户信息        www.fundo.cc/api/user_info.php
	public static final String USER_INFO =mProtocol + "www.fundo.cc/api/user_info.php";

	// 获取用户头像        http://www.fundo.cc/member/faces/big/
	public static final String USER_PHOTO =mProtocol + "www.fundo.cc/member/faces/big/";

	// 修改用户信息       www.fundo.cc/api/user_modify.php   www.fundo.cc/api/user_modify.php
	public static final String MODIFY_USER_PHOTO =mProtocol + "www.fundo.cc/api/user_modify.php";

	// 运动数据同步       www.fundo.cc/api/fit_pattern_add.php
	public static final String SYNC_SPORTS_DATA =mProtocol + "www.fundo.cc/api/fit_pattern_add.php";

	// 运动历史数据       www.fundo.cc/ api/fit_pattern_gain.php
	public static final String SPORTS_HISTORY_DATA =mProtocol + "www.fundo.cc/api/fit_pattern_gain.php";

	// 获取SOS号码        www.fundo.cc/ api/fit_emergency.php
	public static final String SOS_DATA =mProtocol + "www.fundo.cc/api/fit_emergency.php";

	// 添加SOS号码        www.fundo.cc/ api/fit_emergency_up.php
	public static final String ADD_SOS_DATA =mProtocol + "www.fundo.cc/api/fit_emergency_up.php";

	// 睡眠数据上传        www.fundo.cc/ api/fit_sellp_upload.php
	public static final String UPLOAD_SLEEP_DATA =mProtocol + "www.fundo.cc/api/fit_sellp_upload.php";

	// 获取睡眠数据            www.fundo.cc/ api/fit_sellp.php
	public static final String DOWNLOAD_SLEEP_DATA =mProtocol + "www.fundo.cc/api/fit_sellp.php";



	// 手机号注册      www.fundo.cc/api/newrpwd_email.php
	public static final String REGISTER_GETCODE =mProtocol + "wx.funos.cn:8080/fundo/register/phone.do";


	//手机忘记密码
	public static final String REGISTER_GETCODE_FORGET =mProtocol + "wx.funos.cn:8080/fundo/user/editPasswordByPhone.do";
	//手机号码效验
	public static final String VERIFY_GETCODE_FORGET =mProtocol + "wx.funos.cn:8080/fundo/register/verifyPhone.do";




/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//2注销
	public static final String USER_LOGOUT = SERVER_URL + "/webSer/app/user/Logout";   ////

	//3	拉取桌子区域列表
	public static final String QUERY_AREAS = SERVER_URL + "/webSer/app/table/QueryAreas";   
	
	//4 拉取桌子列表
	public static final String QUERY_TABLES = SERVER_URL + "/webSer/app/table/QueryTables";   
	
	//5查询预订信息(QueryBookingInfo)
	public static final String QUERY_BOOKING_INFO = SERVER_URL + "/webSer/app/table/QueryBookingInfo";   

	//6 预订桌子(BookingTable)
	public static final String BOOKING_TABLE = SERVER_URL + "/webSer/app/table/BookingTable";   

	//7取消预订和预订到达(ReleaseBooking)
	public static final String RELEASE_BOOKING = SERVER_URL + "/webSer/app/table/ReleaseBooking";
	
	//8 更新上传酒瓶信息
	public static final String UPDATE_BOTTLES = SERVER_URL + "/webSer/app/table/UpdateBottles";   
	
	//9	上传Rx连接/断开信息(UpdateRxOnOffline)
	public static final String UPDATE_RX_ONOFFLINE = SERVER_URL + "/webSer/app/table/UpdateRxOnOffline";
	
	//10	拉取rx的连接状态（QueryRxStatus）
		public static final String PULL_RX_STATE = SERVER_URL + "/webSer/app/rx/rxStatus";
	
	//10 获取最新酒瓶信息(QueryBottles)
	public static final String QUERY_BOTTLES = SERVER_URL + "/webSer/app/table/QueryBottles";
	
	
	//11.	拉取菜单分组（类别）(QueryMenuTypes)
	public static final String QUERY_MENU_TYPES = SERVER_URL + "/webSer/app/menu/QueryMenuTypes";
	
	//12.	拉取菜单列表(QueryMenus)
	public static final String QUERY_MENU = SERVER_URL + "/webSer/app/menu/QueryMenus";  /////
	
	//13.	拉取缺菜列表(QueryLackMenus)
	public static final String QUERY_LACK_MENUS = SERVER_URL + "/webSer/app/menu/QureryLackMenus";   
	
	//14.	设置菜品销售状态(SetMenuSaleStatus)
	public static final String SET_MENUS_SALE_STATUS = SERVER_URL + "/webSer/app/menu/SetMenuSaleStatus";   
	
	//15.	拉取账单(QueryBill)
	public static final String QUERY_BILL = SERVER_URL + "/webSer/app/order/QueryBill";   
	
	//16.	拉取订单列表(QueryOrders)
	public static final String QUERY_ORDERS = SERVER_URL + "/webSer/app/order/QueryOrders";   
	
	//17.	增加订单(AddOrder)
	public static final String ADD_ORDER = SERVER_URL + "/webSer/app/order/AddOrder";   
	
	//18.	修改订单(UpdateOrder)
	public static final String UPDATE_ORDER = SERVER_URL + "/webSer/app/order/UpdateOrder";   

	//19.	买单(PayBill)
	public static final String PAY_BILL = SERVER_URL + "/webSer/app/order/PayBill";   

	//20.	查询待制作的订单(QueryCookOrders)
	public static final String QUERY_COOK_ORDERS = SERVER_URL + "/webSer/app/order/QueryCookOrders";
	
	//21.	拉取营业信息(QuerySaleInfo)
	public static final String QUERY_SALE_INFO = SERVER_URL + "/app/table/QueryBookingInfo";   

	//22.催单(QuickBill)
	public static final String QUICK_BILL = SERVER_URL + "/webSer/app/order/QuickBill";   


	//23.关闭呼叫(CloseCall)
	public static final String CLOSE_CALL = SERVER_URL + "/webSer/app/table/CloseCall"; 
	
	//24. 用户下单后，取消订单走了的情况 (Recovery)
	public static final String RECOVERY = SERVER_URL + "/webSer/app/table/Recovery"; 
	
	//25. 获取桌子的开台预定状态 (QueryTableStatus)
	public static final String QUERY_TABLE_STATUS = SERVER_URL + "/webSer/app/table/QueryTableStatus"; 
	
	
	//26. 获取boss端营业额数据(Turnover)
	public static final String QUERY_TABLE_TURNOVER = SERVER_URL + "/webSer/app/chart/Turnover"; 
	
	
	//27. 获取boss端的营业额对比报表(TurnoverConstrast)
	public static final String QUERY_TABLE_TURNOVER_CONSTRAST = SERVER_URL + "/webSer/app/chart/TurnoverConstrast"; 
		//28. 获取boss端的菜品销量报表
	public static final String QUERY_TABLE_PRODUCT = SERVER_URL + "/webSer/app/chart/Product"; 
	//29. 提交RX清除对码的状态请求
	public static final String FACTORY_CLEAR = SERVER_URL + "/webSer/app/rx/appRxClearInit"; 
	
	//30厨房撤销操作接口
		public static final String KITCHEN_CANCEL = SERVER_URL + "/webSer/app/order/UpdateRecoveryOrder";


	/**
	 * 发票添加
	 */
	public static final String INVOICE_ADD = mProtocol + "wx.funos.cn:8080/fundo/receipt/insert.do";

	/**
	 * 发票修改
	 */
	public static final String INVOICE_CHANGE = mProtocol + "wx.funos.cn:8080/fundo/receipt/edit.do";

	/**
	 * 发票查询全部
	 */
	public static final String INVOICE_SELECTALL = mProtocol + "wx.funos.cn:8080/fundo/receipt/request.do";

	/**
	 * 发票删除
	 */
	public static final String INVOICE_DELECT = mProtocol + "wx.funos.cn:8080/fundo/receipt/delete.do";

	/**
	 * 默认发票
	 */
	public static final String INVOICE_DEF = mProtocol + "wx.funos.cn:8080/fundo/receipt/updateDefault.do";

	//绑定登陆
	public static final String BINDLOGIN =mProtocol + "wx.funos.cn:8080/fundo/user/bindLoginCommit.do";
	//解除绑定登陆
	public static final String UNBINDLOGIN =mProtocol + "wx.funos.cn:8080/fundo/user/unbindLoginCommit.do";
}
