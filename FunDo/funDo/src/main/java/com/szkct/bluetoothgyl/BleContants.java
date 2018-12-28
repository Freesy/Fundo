package com.szkct.bluetoothgyl;

import java.util.UUID;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/2/17
 * 描述: ${VERSION}
 * 修订历史：
 */
public class BleContants {

    public final static byte PROTOCOL_MARK = (byte) 0xBA;
    /**
     * 唯一标识
     */

    //体温
    public final static byte TEMPERATURE_COMMAND = (byte) 0x12;
    //固件升级
    public final static byte ECG_COMMAND = (byte) 0x11;

    //固件升级
    public final static byte FIRMWARE_UPGRADE_COMMAND = (byte) 0x01;
    /**
     * 固件升级命令
     */
    public final static byte FIRMWARE_UPGRADE_START = (byte) 0x10;
    /**
     * 固件升级启动
     */
    public final static byte FIRMWARE_UPGRADE_ECHO = (byte) 0x11;
    /**
     * 固件升级回应
     */
    public final static byte FIRMWARE_UPGRADE_REQUST = (byte) 0x12;
    /**
     * 固件信息查询
     */
    public final static byte FIRMWARE_UPGRADE_REQURN = (byte) 0x13;
    /**
     * 固件信息返回
     */

    //设置命令
    public final static byte INSTALL_COMMAND = (byte) 0x02;
    /**
     * 设置命令
     */

    public final static byte UNITSETTING_COMMAND = (byte) 0x01;


    public final static byte INSTALL_TIME = (byte) 0x20;
    /**
     * 时间设置
     */
    public final static byte INSTALL_ALARM_CLOCK = (byte) 0x21;
    /**
     * 闹钟设置
     */
    public final static byte INSTALL_RUN_GOAL = (byte) 0x22;
    /**
     * 运动目标设置
     */
    public final static byte INSTALL_USER_INFO = (byte) 0x23;
    /**
     * 用户信息设置
     */
    public final static byte INSTALL_ANTI_LOST = (byte) 0x24;
    /**
     * 防丢设置
     */
    public final static byte INSTALL_SEDENTARINESS = (byte) 0x25;
    /**
     * 久坐设置
     */
    public final static byte INSTALL_AUTOMATIC_SLEEP = (byte) 0x26;
    /**
     * 自动睡眠设置
     */
    public final static byte INSTALL_SYSTEM_USER = (byte) 0x27;
    /**
     * 系统用户设置
     */
    public final static byte INSTALL_DRINK_NOTIFICATION = (byte) 0x28;
    /**
     * 喝水提醒设置
     */


    /**
     * 读取手环设置请求
     */
    public final static byte INSTALL_SETTING = (byte) 0x2E;

    /**
     * 读取手环设置响应
     */
    public final static byte INSTALL_SETTING_RETURN = (byte) 0x2F;



    //天气推送
    public final static byte WEATHER_PROPELLING = (byte) 0x03;
    /**
     * 天气推送命令
     */
    public final static byte PROPELLING_WEATHER = (byte) 0x30;
    /**
     * 天气推送
     */
    public final static byte WEATHER_ULTRAVIOLET_REQUEST = (byte) 0x31;
    /**
     * 紫外线数据请求
     */
    public final static byte WEATHER_ULTRAVIOLET_RETURN = (byte) 0x32;
    /**
     * 紫外线数据返回
     */
    public final static byte WEATHER_PRESSURE_REQUEST = (byte) 0x33;
    /**
     * 气压数据请求
     */
    public final static byte WEATHER_PRESSURE_RETURN = (byte) 0x34;
    /**
     * 气压数据返回
     */
    public final static byte WEATHER_ALTITUDE_REQUEST = (byte) 0x35;
    /**
     * 海拔数据请求
     */
    public final static byte WEATHER_ALTITUDE_RETURN = (byte) 0x36;
    /**
     * 海拔数据返回
     */

    //设备命令
    public final static byte DEVICE_COMMADN = (byte) 0x04;


    //体温返回
    public final static byte TEMPERATURE_RETURN = (byte) 0x02;

    //心电开始
    public final static byte ECG_START = (byte) 0x01;
    //心电结束
    public final static byte ECG_FINISH = (byte) 0x02;

    //心电配置
    public final static byte ECG_CONFIGURATION = (byte) 0x03;
    //心电内容
    public final static byte ECG_CONTENT = (byte) 0x04;

    public final static byte ECG_HISTORY_CONTENT = (byte) 0x05;
    /**
     * 设备命令
     */
    public final static byte ELECTRIC_REQUEST = (byte) 0x40;
    /**
     * 设备电量请求
     */
    public final static byte ELECTRIC_RETURN = (byte) 0x41;
    /**
     * 设备电量返回
     */
    public final static byte UNBOND_REQUEST = (byte) 0x42;
    /**
     * 解除绑定请求
     */
    public final static byte UNBOND_RETURN = (byte) 0x43;
    /**
     * 解除绑定返回
     */
    public final static byte BOND_REQUEST = (byte) 0x44;
    /**
     * 设备绑定请求
     */
    public final static byte BOND_RETURN = (byte) 0x45;
    /**
     * 设备绑定返回
     */
    public final static byte CRAMER_OPEN = (byte) 0x46;
    /**
     * 打开拍照
     */
    public final static byte CRAMER_TAKE = (byte) 0x47;
    /**
     * 拍照
     */
    public final static byte CRAMER_CLOSE = (byte) 0x48;
    /**
     * 退出拍照
     */
    public final static byte LANDSCAPE = (byte) 0x49;
    /**
     * 横竖显示
     */
    public final static byte GESTURE = (byte) 0x4A;
    /**
     * 手势指控
     */
    public final static byte SYN_ADDREST_LIST = (byte) 0x4B;
    /**
     * 同步通讯录
     */
    public final static byte SYN_WIFI = (byte) 0x4C;
    /**
     * 手表WIFI--  (byte) 0x4C
     */   // TODO --- 手表返回错误 （19）

    public final static byte INPUTASSIT_START = (byte) 0x4D;
    /**
     * 协助输入开始
     */

    public final static byte DIAL_REQUEST = (byte) 0x4E;
    /**
     * 表盘推送
     */

    public final static byte DIAL_RETURN = (byte) 0x4F;
    /**
     * 表盘数据返回
     */

    public final static byte SYNC_USER_WEIGHT = (byte) 0x14;
    /**
     * 同步用户体重
     */

    public final static byte SOS = (byte) 0x15;
    /**
     * 紧急拨号
     */

    public static final byte KEY_INPUTASSIT_SEND = (byte) 0X16;
    /**
     * 发送协助输入数据
     */
    public static final byte KEY_SHEAR_PLATE = (byte) 0X17;
    /**
     * 发送剪切板数据
     */
    public static final byte KEY_INPUTASSIT_END = (byte) 0X18;
    /**
     * 协助输入结束
     */


    public static final int KEY_WIFI_LIST = (byte) 0X19;
    /**
     * 获取WIFI列表
     */
    public static final int KEY_WIFI_PASSWORD = (byte) 0X1A;
    /**
     * WIFI需要密码
     */
    public static final int KEY_WIFI_LINK = (byte) 0X1B;
    /**
     * 已保存的WIFI
     */
    public static final int KEY_WIFI_NOPASSWORD = (byte) 0X1C;
    /**
     * WIFI无需密码
     */

    public final static byte APP_BLUETOOTH_DISCONNECT = (byte) 0x60;
    /**
     * 来电提醒
     */     // 暂用为app端，发送蓝牙断开连接命令
    public final static byte WATCH_BLUETOOTH_DISCONNECT = (byte) 0x61;
    /**
     * 短信提醒
     */        // 暂用为手表端，发送蓝牙断开连接命令
    public final static byte KEY_RELIEVE_BLUETOOTH = (byte) 0X62;  /** 解除安卓蓝牙配对 */

    //android begin
    /**
     * 手机锁屏功能
     * 获取锁屏权限value=1
     * 手机未获得锁屏权限value =2;
     * 手机解锁提醒 value = 3;
     */
    public static final byte KEY_PHONE_LOCK_SCREEN = 0X1D;
    /**
     * 手表发送锁屏功能
     */
    public static final byte KEY_WATCH_LOCK_SCREEN = 0X1E;  // 0X1E --- 正常应该为 0X1E

    /**
     * 手机通知推送 0X1F
     */
    public static final byte KEY_NOTIFICATION_PUSH = 0X1F;


    //查找命令
    public final static byte FIND_COMMAND = (byte) 0x05;
    /**
     * 查找命令
     */
    public final static byte FIND_DEVICE = (byte) 0x50;
    /**
     * 查找设备
     */
    public final static byte FIND_PHONE = (byte) 0x51;
    /**
     * 查找手机
     */
    /**
     * 确认查找手机
     */
    public final static byte CLOSE_FIND_PHONE = (byte) 0x52;
    //提醒命令
//0：来电 1：短信 2：QQ 3：微信 4：FB 5：Messenger 6：twitter 7：whatsapp 8：instagram 9：linkedin 10：other apps 15：挂断电话（hungup）
//11：alarm clock
//12：sendentary
//13：find band
//14：sport target done


    public final static byte REMIND_COMMAND = (byte) 0x06;
    /**
     * 提醒命令
     */
    public final static byte REMIND = (byte) 0x60;
    /**
     * 提醒
     */

    public final static byte REMIND_INCALL = (byte) 0x00;  /*来电提醒*/
    public final static byte REMIND_SMS = (byte) 0x01;  /*短信提醒*/
    public final static byte REMIND_QQ = (byte) 0x02;  /*QQ提醒*/
    public final static byte REMIND_WX = (byte) 0x03;  /*微信提醒*/
    public final static byte REMIND_FACEBOOK = (byte) 0x04;  /*FB提醒*/
    public final static byte REMIND_MESSENGER = (byte) 0x05;  /*Messenger提醒*/
    public final static byte REMIND_TWITTER = (byte) 0x06;  /*twitter提醒*/
    public final static byte REMIND_WHATSAPP = (byte) 0x07;  /*whatsapp提醒*/
    public final static byte REMIND_INSTAGRAM = (byte) 0x08;  /*instagram提醒*/
    public final static byte REMIND_LINKEDIN = (byte) 0x09;  /*linkedin提醒*/
    public final static byte REMIND_OTHER = (byte) 0x0A;  /*other提醒*/
    public final static byte REMIND_LINE = (byte) 0x0B;  /*LINE提醒*/

    public final static byte REMIND_HUNGUP = (byte) 0x0F;  /*挂断电话提醒*/
    public final static byte REMIND_RING = (byte) 0x10;  /*接电话提醒*/


    public final static byte REMIND_DISRURB = (byte) 0x64;
    /**
     * 勿扰模式
     */
    public final static byte REMIND_MODE = (byte) 0x65;
    /**
     * 提醒模式
     */
    public final static byte SYN_CONFIGURE = (byte) 0x66;
    /**
     * 配置信息同步
     */


    //运动模式命令
    public final static byte RUN_MODE_COMMADN = (byte) 0x07;
    /**
     * 运动模式命令
     */
    public final static byte RUN_REQUEST = (byte) 0x70;
    /**
     * 计步数据请求
     */
    public final static byte RUN_BASE_RETURN = (byte) 0x71;  /*基础数据返回**/       //计步数据返回    ---- 72 无计步数据返回
    public final static byte RUN_HEART_RETURN = (byte) 0x72;
    /**
     * 心率、速度，步频数据返回
     */  //心率、速度、步频数据返回
    public final static byte PACE_RETURN = (byte) 0x73;
    /**
     * 配速数据返回
     */
    public final static byte TRAJECTORY_RETURN = (byte) 0x74;
    /**
     * 轨迹数据返回
     */
    public final static byte RUN_INCREMENT_REQUEST = (byte) 0x75;
    /**
     * 时间段增量运动数据请求
     */
    public final static byte RUN_INCREMENT_RETURN = (byte) 0x76;
    /**
     * 时间段增量运动数据返回
     */


    //睡眠命令
    public final static byte SLEEP_COMMAND = (byte) 0x08;
    /**
     * 睡眠命令
     */
    public final static byte SLEEP_REQUEST = (byte) 0x80;
    /**
     * 睡眠数据请求
     */
    public final static byte SLEEP_RETURN = (byte) 0x81;
    /**
     * 睡眠数据返回
     */


    //心率命令
    public final static byte HEART_COMMAND = (byte) 0x09;
    /**
     * 心率命令
     */
    public final static byte HEART_REQUEST = (byte) 0x90;
    /**
     * 心率数据请求
     */
    public final static byte HEART_RETURN = (byte) 0x91;
    /**
     * 心率数据返回
     */
    public final static byte ACTUAL_HEART_REQUEST = (byte) 0x92;
    /**
     * 实时测试心率
     */


    //同步命令
    public final static byte SYN_COMMAND = (byte) 0x0A;
    /**
     * 同步命令
     */
    public final static byte SYN_DATA_REQUEST = (byte) 0xA0;

    /**
     * 同步命令
     */
    public final static byte ANSWER_ECG_SYCN_REQUEST = (byte) 0xB6;
    /**
     * 同步数据指令
     */   // 请求历史数据
    public final static byte SYN_DATA_RETURN = (byte) 0xA1;
    /**
     * 同步数据返回
     */

    public final static byte ACTUAL_SYN_DATA_REQUEST = (byte) 0xA6;
   

    /**
     * 手环睡眠数据返回
     */
    public final static byte BRACELET_SLEEP_DATA_RETURN = (byte) 0xA2;

    /**
     * 手环计步数据返回
     */
    public final static byte BRACELET_RUN_DATA_RETURN = (byte) 0xA3;

    //心率数据返回
    public final static byte BRACELET_HEART_DATA_RETURN = (byte) 0xA4;

    /**
     * 手环运动模式返回
     */
    public final static byte BRACELREALSPORT = (byte) 0xA5;


    /**
     * 手环实时运动数据返回
     */
    public final static byte BRACELREALRUN = (byte) 0xAC;


    /**
     * 手环实时心率数据返回
     */
    public final static byte BRACELREALHEART = (byte) 0xAB;




    public final static byte BLOOD_PRESSURE_HIS = (byte) 0xAD;//!< 血压数据返回 0xAD
    public final static byte BLOOD_OXYGEN_HIS = (byte) 0xAE; //!< 血氧数据返回 0xAE   
    public final static byte BLOOD_PRESSURE = (byte) 0xB1; //!< 实时血压数据返回 0xB1
    public final static byte BLOOD_OXYGEN = (byte) 0xB2;  //!< 实时血氧数据返回 0xB2

    //校准命令
    public final static byte CALIBRATION_COMMAND = (byte) 0x0B;
    /**
     * 校准命令
     */
    public final static byte CALIBRATION_DAY_SPORT = (byte) 0xB0;
    /**
     * 当天运动校准
     */

    //工厂命令
    public final static byte FACTORY_COMMAND = (byte) 0x0C;
    /**
     * 工厂命令
     */

    public final static byte PUSH_DATA_TO_PHONE_COMMAND = (byte) 0x0D;  // 	推送数据到手机

    public final static byte GESTURE_PUSH_COMMAND = (byte) 0x01;   // 手势智控推送

    public final static byte REJECT_DIAL_COMMAND = (byte) 0x02;   // 拒接电话推送

    public final static byte ANSWER_DIAL_COMMAND = (byte) 0x03;   // 接收电话推送

    public final static byte COMMAND_WEATHER_INDEX = (byte) 0x0E;  //气象指数  & 表盘推送

    public final static byte WEATHER_INDEX = (byte) 0xE1;  //气象指数

    public final static byte DIAL_PUSH = (byte) 0xE2;  //表盘推送

    public final static byte PLAY_MUSIC_COMMAND = (byte) 0x04;   // 播放音乐推送
    public final static byte PAUSE_MUSIC_COMMAND = (byte) 0x05;   // 暂停音乐推送
    public final static byte LAST_MUSIC_COMMAND = (byte) 0x06;   // 上一首推送
    public final static byte NEXT_MUSIC_COMMAND = (byte) 0x07;   // 下一首推送


    public final static byte FACTORY_SCREEN_TEST = (byte) 0xF0;
    /**
     * 屏幕测试
     */
    public final static byte FACTORY_MOTOR_TEST = (byte) 0xF1;
    /**
     * 马达测试
     */
    public final static byte FACTORY_PEDOMETER_TEST = (byte) 0xF2;
    /**
     * 计步器测试
     */
    public final static byte FACTORY_HEART_TEST = (byte) 0xF3;
    /**
     * 心率测试
     */
    public final static byte FACTORY_FLASH_TEST = (byte) 0xF4;
    /**
     * Flash测试
     */
    public final static byte FACTORY_SHUT_DOWN = (byte) 0xF5;
    /**
     * 关机命令
     */
    public final static byte FACTORY_UNBOND = (byte) 0xF6;
    /**
     * 解绑命令
     */
    public final static byte FACTORY_RESTART = (byte) 0xF7;  /**重启命令*/

    public final static UUID BLE_YDS_UUID = UUID.fromString("0000fea0-0000-1000-8000-00805f9b34fb");//手环
    public final static UUID BLE_YDS_UUID_HUAJING = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");//手环(华晶心率芯片)
    public final static UUID MTK_YDS_2502_UUID = UUID.fromString("00002502-0000-1000-8000-00805f9b34fb");//2502mtk
    public final static UUID MTK_YDS_2503_UUID = UUID.fromString("00002503-0000-1000-8000-00805f9b34fb");//2503mtk
    public final static UUID MTK_YDS_3802_UUID = UUID.fromString("00003802-0000-1000-8000-00805f9b34fb");//3802mtk
    public static final UUID RX_SERVICE_872_UUID = UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CB7");
    public static final UUID RX_CHAR_872_UUID = UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CBA");
    public static final UUID TX_CHAR_872_UUID = UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CB8");

    /**
     * ble
     */
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //Service UUID （数据通信服务）
    public static final UUID RX_SERVICE_UUID = UUID.fromString("C3E6FEA0-E966-1000-8000-BE99C223DF6A");
    //Rx UUID (BLE设备-->手机APP Notification)
    public static final UUID RX_CHAR_UUID = UUID.fromString("C3E6FEA1-E966-1000-8000-BE99C223DF6A");
    //Tx UUID (手机APP-->BLE设备 Write)
    public static final UUID TX_CHAR_UUID = UUID.fromString("C3E6FEA2-E966-1000-8000-BE99C223DF6A");


}
