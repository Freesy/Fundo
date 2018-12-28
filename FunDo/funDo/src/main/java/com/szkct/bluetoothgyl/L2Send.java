package com.szkct.bluetoothgyl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/3/15
 * 描述: ${VERSION}
 * 修订历史：
 */
public class L2Send {
    private static final String TAG = MainService.class.getName();

    private static  final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;


    /**联系人名称**/
    private static ArrayList<String> mContactsName = new ArrayList<String>();

    /**联系人号码**/
    private static ArrayList<String> mContactsNumber = new ArrayList<String>();

    /** 时间同步设置 */    // 手环
    public static void  sendSynTime(Context context){
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);//获取当前系统时间格式

        Calendar c = Calendar.getInstance();//取得当前系统时间
        int year, month, day, hour, minute, second;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);

        if (StringUtils.isEmpty(strTimeFormat)){
            strTimeFormat = "24";
        }

        hour = c.get(Calendar.HOUR_OF_DAY);    // todo ---- ble平台设备端要求无论手机端为12 or 24 小时制，都传24小时制的时间

        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);

        byte[] time = new byte[7];
        time[0] = (byte)(year % 100);
        time[1] = (byte) month;
        time[2] = (byte) day;
        time[3] = (byte) hour;
        time[4] = (byte) minute;
        time[5] = (byte) second;

        if(strTimeFormat.equals("24")){
            time[6] = (byte)0;  // todo -- 24小时 ： 0
        }else{
            time[6] = (byte)1;  // todo -- 12小时 ： 1
        }


        byte[] l2 = new L2Bean().L2Pack(BleContants.INSTALL_COMMAND, BleContants.INSTALL_TIME,time);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void sendAlarmClock(int sequenctId){

    }


    /** 同步通讯录 */
    public static byte[] sendPhoneContacts(Context context){
        getPhoneContacts(context);
        getSIMContacts(context);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < mContactsNumber.size(); i++) {
            if(mContactsNumber.size()-1 != i) {
                stringBuffer.append(mContactsName.get(i) + "|" + mContactsNumber.get(i) + "^");  // 名字|号码^
            }else{
                stringBuffer.append(mContactsName.get(i) + "|" + mContactsNumber.get(i));
            }
        }
        Log.e("PhoneContacts", "Contacts =" + stringBuffer.toString());
        Log.e("PhoneContacts", "Contacts =" + Utils.bytesToHexString(stringBuffer.toString().getBytes()));
        mContactsName.clear();
        mContactsNumber.clear();
        return  stringBuffer.toString().getBytes();
    }

    /**得到手机通讯录联系人信息**/
    private  static void getPhoneContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                mContactsNumber.add(phoneNumber.replace(" ", ""));
                mContactsName.add(contactName);
            }
            phoneCursor.close();
        }

    }

    /**得到手机SIM卡联系人人信息**/
    private static void getSIMContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);

                //Sim卡中没有联系人头像

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber.replace(" ", ""));
            }

            phoneCursor.close();
        }
    }

    /**
     * 固件信息请求
     */
    public static void getFirmwareData() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.FIRMWARE_UPGRADE_COMMAND, BleContants.FIRMWARE_UPGRADE_REQUST, null);
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 将天气代码转成 0~3
     * @author xujianbo
     * @time 2017/3/4 14:31
     */
    private static int getSendCode(int codeInt) {
        int code;
        if(codeInt >=100 && codeInt <104){
            code=0;   //  晴
        }else if(codeInt >= 104 && codeInt < 300){
            code = 1; // 阴
        }else if(codeInt >= 300 && codeInt <400){
            code=2;  //  雨
        }else if(codeInt >= 400 && codeInt <500){
            code=3; // 雪
        }else code=1;  // 阴
        return code;
    }

    /**
     * 同步天气
     */
    public static void syncAppWeather() {
        String low = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "low");
        String high = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "high");
        String code = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "code");

        String nextlow = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextlow");
        String nexthigh = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nexthigh");
        String nextcode = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextcode");

        if(!StringUtils.isEmpty(low) && !StringUtils.isEmpty(high) && !StringUtils.isEmpty(nextlow) && !StringUtils.isEmpty(nexthigh)  && !StringUtils.isEmpty(code)  && !StringUtils.isEmpty(nextcode) ){
            int lowTem = Integer.valueOf(low);
            int highTem = Integer.valueOf(high);
            int nextlowTem = Integer.valueOf(nextlow);
            int nexthighTem = Integer.valueOf(nexthigh);
            byte[] value = new byte[6];
            value[0] = (byte)lowTem;
            value[1] = (byte)highTem;
            value[2] = (byte)getSendCode(Integer.valueOf(code));
//            value[2] = (byte) 0;
            value[3] = (byte)nextlowTem;
            value[4] = (byte)nexthighTem;
            value[5] = (byte)getSendCode(Integer.valueOf(nextcode));
            byte[] l2 = new L2Bean().L2Pack(BleContants.WEATHER_PROPELLING, BleContants.PROPELLING_WEATHER, value);
            MainService.getInstance().writeToDevice(l2, true);
        }
    }

    /**
     * 系统设置
     */
    public static void getSystrmUserData(Context context) {
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);//获取当前系统时间格式

        int timeFormat = 0;



        String language = Utils.getLanguage();  // zh ---中文(繁体)  en --- 英文  fr -- 法语   es --西班牙  ru --俄语   it---意大利   pt ----葡萄牙   pl---波兰   de ----德语    日语--- ja         zh--繁体
        int languageMode = 0;
        if (StringUtils.isEmpty(strTimeFormat)) {
            strTimeFormat = "24";
        }

        if(strTimeFormat.equals("12")){
            timeFormat = 1;
        }

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC));
//        String dd = device.getName();   // M Watch
        if("M Watch".equalsIgnoreCase(device.getName())){
            String country = Utils.getCountry();      // zh_HK  --- zh_TW
//        String dd = locale.getCountry();   // TW(繁体台湾) -- HK(繁体香港) --  CN（中文简体） --  IT（意大利） --  JP（日语） --  TR（土耳其） US（英语美国） --   RU（俄语） --   ES（西班牙） --  PT(葡萄牙) --  FR（法语） -- PL（波兰）  DE（德语）  NL--（荷兰）
            // GR --()希腊 ？？？  el_GR    RO--（罗马尼亚）

 /*  0:中文   1:英文        2.俄罗斯语        3.西班牙语        4.德国，        5意大利        6法国        7.葡萄牙        8波兰语        9荷兰        10希腊        11.其它*/

            if(country.equalsIgnoreCase("TW") || country.equalsIgnoreCase("HK") || country.equalsIgnoreCase("MO")){   // MO
                languageMode = 14;
            }else {
                if (language.equals("zh")) {  // en
                    languageMode = 0;
                }else if(language.equals("en")){
                    languageMode = 1;
                }else if(language.equals("ru")){
                    languageMode = 2;
                }else if(language.equals("es")){
                    languageMode = 3;
                }else if(language.equals("de")){
                    languageMode = 4;
                }else if(language.equals("it")){
                    languageMode = 5;
                }else if(language.equals("fr")){
                    languageMode = 6;
                }else if(language.equals("pt")){
                    languageMode = 7;
                }else if(language.equals("pl")){
                    languageMode = 8;
                }else if(language.equals("nl")){ //   Nederlands 荷兰语     nl
                    languageMode = 9;
                }else if(language.equals("el")){//    ελληνικά	    Greek	希腊语    el
                    languageMode = 10;
                }else if(language.equals("tr")){  //  11.土耳其
                    languageMode = 11;
                }else if(language.equals("ro")){  //罗马尼亚
                    languageMode = 12;
                }else if(language.equals("ja")){  //日语
                    languageMode = 13;
                }else{
                    languageMode = 15;
                }
            }
        }else {
//            String language = Utils.getLanguage();  // zh ---中文(繁体)  en --- 英文  fr -- 法语   es --西班牙  ru --俄语   it---意大利   pt ----葡萄牙   pl---波兰   de ----德语    日语--- ja         zh--繁体
             /*  0:中文   1:英文        2.俄罗斯语        3.西班牙语        4.德国，        5意大利        6法国        7.葡萄牙        8波兰语        9荷兰        10希腊        11.其它*/
            if (language.equals("zh")) {  // en
                languageMode = 0;
            }else if(language.equals("en")){
                languageMode = 1;
            }else if(language.equals("ru")){
                languageMode = 2;
            }else if(language.equals("es")){
                languageMode = 3;
            }else if(language.equals("de")){
                languageMode = 4;
            }else if(language.equals("it")){
                languageMode = 5;
            }else if(language.equals("fr")){
                languageMode = 6;
            }else if(language.equals("pt")){
                languageMode = 7;
            }else if(language.equals("pl")){
                languageMode = 8;
            }else if(language.equals("nl")){ //   Nederlands 荷兰语     nl
                languageMode = 9;
            }else if(language.equals("el")){//    ελληνικά	    Greek	希腊语    el
                languageMode = 10;
            }else if(language.equals("tr")){  //  11.土耳其
                languageMode = 11;
            }else if(language.equals("ro")){  //罗马尼亚
                languageMode = 12;
            }else if(language.equals("ja")){  //日语
                languageMode = 13;
            }else if(language.contains("iw")){ //希伯来语
                languageMode = 15;
            }else if(language.contains("da")){ //丹麦语
                languageMode = 16;
            }else if(language.contains("sr")){ //塞尔维亚
                languageMode = 17;
            }else if(language.contains("sv")){ //瑞典
                languageMode = 18;
            }else if(language.contains("cs")){ //捷克
                languageMode = 19;
            }else if(language.contains("sk")){ //斯洛伐克
                languageMode = 20;
            }else if(language.contains("hu")){ //匈牙利
                languageMode = 21;
            }else if(language.contains("ar")){ // 阿拉伯
                languageMode = 22;
            } else{
                languageMode = 23;    //其他
            }
        }

        byte[] value = new byte[4];
        value[0] = (byte) languageMode;
//        value[1] = (byte) Integer.parseInt(strTimeFormat);
        value[1] = (byte)timeFormat;
        value[2] = (byte) 60;
        value[3] = (byte) 0;
        byte[] l2 = new L2Bean().L2Pack(BleContants.INSTALL_COMMAND, BleContants.INSTALL_SYSTEM_USER, value);
        MainService.getInstance().writeToDevice(l2, true);
    }


    /**
     * 个人信息设置
     */
    public static void getUserInfoData(Context context) {
        int weight = 0;
        int height = 0;
        int sex = 1;
        int birth = 0;
        int step = 0;

        if (SharedPreUtil.readPre(context, SharedPreUtil.USER,
                SharedPreUtil.WEIGHT).equals("")) {
            weight = 60;
        } else {
            weight = Integer.parseInt(SharedPreUtil.readPre(context, SharedPreUtil.USER,
                    SharedPreUtil.WEIGHT));
        }

        if (SharedPreUtil.readPre(context, SharedPreUtil.USER,
                SharedPreUtil.HEIGHT).equals("")) {
            height = 170;
        } else {
            height = Integer.parseInt(SharedPreUtil.readPre(context, SharedPreUtil.USER,
                    SharedPreUtil.HEIGHT));
        }

        if (SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SEX).equals("")) {
            sex = 1;//默认为男
        } else {
            int mspSex = Integer.parseInt(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SEX));
            if(mspSex == 0){
                sex = 1;
            }else {
                sex = 0;
            }

//            sex = Integer.parseInt(SharedPreUtil.readPre(context, SharedPreUtil.USER,
//                    SharedPreUtil.SEX));
        }

        if (SharedPreUtil.readPre(context, SharedPreUtil.USER,
                SharedPreUtil.BIRTH).equals("")) {
            birth = 18;
        } else {
            SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy");
            birth = Utils.toint(getDateFormat.format(new Date(System.currentTimeMillis()))) -
                    Utils.toint(SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.BIRTH).substring(0, 4));
            if (birth < 18) {
                birth = 18;
            }
        }


        SharedPreferences goalPreferences = context.getSharedPreferences("goalstepfiles", Context.MODE_PRIVATE); // todo --- ???  MODE_PRIVATE
        step = goalPreferences.getInt("setgoalstepcount", 5000); // 默认步数为5000步
        byte[] value = new byte[8];
        value[0] = (byte)sex;
        value[1] = (byte)birth;
        value[2] = (byte)height;
        value[3] = (byte)weight;
        value[4] = (byte)(step & 0xff);
        value[5] = (byte)(step >> 8);
        value[6] = (byte)(step >> 16);
        value[7] = (byte)(step >> 24);
        byte[] l2 = new L2Bean().L2Pack(BleContants.INSTALL_COMMAND, BleContants.INSTALL_USER_INFO, value);
        MainService.getInstance().writeToDevice(l2, true);
    }


    /**
     * 请求血氧血压
     */
    public static void getOxyData() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, new byte[]{5});
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 固件升级启动
     */
    /*public static void sendFirmUpdate() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.FIRMWARE_UPGRADE_COMMAND, BleContants.FIRMWARE_UPGRADE_START, null);
        MainService.getInstance().writeToDevice(l2, true);
    }*/

    /**
     * 固件升级启动
     */
    public static void sendFirmUpdate(byte[] bytes) {
        byte[] l2 = new L2Bean().L2Pack(BleContants.FIRMWARE_UPGRADE_COMMAND, BleContants.FIRMWARE_UPGRADE_START, bytes);
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 发送拍照命令
     */
    public static void sendTakephoto() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.CRAMER_OPEN, new byte[]{1});
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 发送退出拍照命令
     */
    public static void sendExitTakephoto() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.CRAMER_OPEN, new byte[]{0});
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 发送退出拍照命令(新协议)
     */
    public static void sendNewExitTakephoto() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.CRAMER_CLOSE, new byte[]{1});
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 发送同步实时步数命令 --- (蓝牙连上后)
     */
    public static void sendSyncShishiStep() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.ACTUAL_SYN_DATA_REQUEST, new byte[]{3});
        MainService.getInstance().writeToDevice(l2, true);
    }


    /**
     * 发送久坐提醒
     */
    public static void sendNotify(byte key, byte[] b) {
        sendNotify(BleContants.INSTALL_COMMAND,key,b);
    }

    /**
     * 发送久坐提醒
     */
    public static void sendNotify(byte cmd,byte key, byte[] b) {
        byte[] l2 = new L2Bean().L2Pack(cmd, key, b);
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 发送提醒消息
     */
    public static void sendNotifyMsg(byte[] b) {
        byte[] l2 = new L2Bean().L2Pack(BleContants.REMIND_COMMAND, BleContants.REMIND, b);
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 读取手环设置请求
     */
    public static void sendBraceletSet() {
        byte[] l2 = new L2Bean().L2Pack(BleContants.INSTALL_COMMAND, BleContants.INSTALL_SETTING, null);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void sendSycnEcg() {
        byte[] key = new byte[1];
        key[0] = (byte) 6;
        byte[] l2 = new L2Bean().L2Pack(BleContants.SYN_COMMAND, BleContants.SYN_DATA_REQUEST, key);
        Log.e(TAG, "第1天--" + UtilsLX.bytesToHexString(l2));
//                String resModebyteslx = UtilsLX.bytesToHexString(bytes);
        MainService.getInstance().writeToDevice(l2, true);
    }

    /**
     * 同步运动目标
     */
    public static void syncSportTarget() {
        SharedPreferences goalPreferences = BTNotificationApplication.getInstance().getSharedPreferences("goalstepfiles", Context.MODE_PRIVATE);  // todo --- ???  MODE_PRIVATE
        int step = goalPreferences.getInt("setgoalstepcount", 5000); // 默认步数为5000步
        byte[] value = new byte[4];
        value[0] = (byte)(step & 0xff);
        value[1] = (byte)(step >> 8);
        value[2] = (byte)(step >> 16);
        value[3] = (byte)0;
        byte[] l2 = new L2Bean().L2Pack(BleContants.INSTALL_COMMAND, BleContants.INSTALL_RUN_GOAL, value);
        MainService.getInstance().writeToDevice(l2, true);
    }


    /**
     * 同步气压指数 ---- F4
     */
    public static void syncWeatherIndex() {
        SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();   // 当前天的日期  2017-06-28
        calendar.setTime(new Date());
        String mcurDate = getDateFormat.format(calendar.getTime());
        String date = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "date");
        String nextdate = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextdate");
        String thirddate = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirddate");
        String ziwaixian = "0";
        String qiya = "0";
        if(date.equals(mcurDate)){
            ziwaixian = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "ziwaixian");
            qiya = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "qiya");
        }else if(nextdate.equals(mcurDate)){
            ziwaixian = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextziwaixian");
            qiya = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextqiya");
        }else if(thirddate.equals(mcurDate)){
            ziwaixian = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirdziwaixian");
            qiya = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirdqiya");
        }

        //String ziwaixian = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "ziwaixian");
        //String qiya = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "qiya");
        String altitude = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "altitude");   // ------ 297
        if(!StringUtils.isEmpty(ziwaixian) && !StringUtils.isEmpty(qiya)){  //  && !StringUtils.isEmpty(altitude)
            int zwx = Integer.valueOf(ziwaixian);
            int qy = Integer.valueOf(qiya);   // 1014
//            int altitudeInt = (pressureInt - 970) * 9;
            int hb = (qy - 970)*9;    // 396
//            int hb = Integer.valueOf(altitude);
//            int hb = 769;    //TODO ---- 测试用假数据
            byte[] value = new byte[5];
            value[0] = (byte)zwx;  // 紫外线
            value[1] = (byte)(qy&0xff);    // 气压
            value[2] = (byte)(qy>>8);
            value[3] = (byte)(hb&0xff);              // 海拔
            value[4] = (byte)(hb>>8);
            byte[] l2 = new L2Bean().L2Pack(BleContants.COMMAND_WEATHER_INDEX, BleContants.WEATHER_INDEX, value);
            MainService.getInstance().writeToDevice(l2, true);
        }
    }

    /**
     * 单位设置
     */
    public static void unitSetting() {  //  private String unit_measure,unit_temperature;
        String unit_measure = (String) SharedPreUtil.getParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES);  //公英制
        String unit_temperature = (String) SharedPreUtil.getParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.UNIT_TEMPERATURE,SharedPreUtil.YES);// 温度

        int measure = 0;
        int temperature = 0;
        if(!StringUtils.isEmpty(unit_measure) && !StringUtils.isEmpty(unit_temperature)){  // YES-- 摄氏度     NO
            if(!unit_measure.equals("YES")){ // 英制
                measure = 1;
            }
            if(!unit_temperature.equals("YES")){ // 华氏度
                temperature = 1;
            }

            byte[] value = new byte[2];
            value[0] = (byte)measure;  // 公英制
            value[1] = (byte)temperature;    // 温度
            byte[] l2 = new L2Bean().L2Pack(BleContants.INSTALL_COMMAND, BleContants.UNITSETTING_COMMAND, value);
            MainService.getInstance().writeToDevice(l2, true);
        }
    }

    public static void setCalibration(int code){
        byte[] l2 = new L2Bean().L2Pack((byte)0x10, (byte)0x01, new byte[]{(byte) code});
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void sendCalibration(byte[] value){
        byte[] l2 = new L2Bean().L2Pack((byte)0x10, (byte)0x02, value);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void getCalibration(){
        byte[] l2 = new L2Bean().L2Pack((byte)0x10, (byte)0x05, null);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void getWatchPushData(){
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.DIAL_RETURN, null);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void sendWatchPushData(byte[] value){
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.DIAL_REQUEST, value);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void sendPushDialPicData(byte[] value){  // todo --- 表盘推送     9999999999999999        new L2Bean().L2Pack(BleContants.COMMAND_WEATHER_INDEX, BleContants.WEATHER_INDEX, value);
        byte[] l2 = new L2Bean().L2Pack(BleContants.COMMAND_WEATHER_INDEX, BleContants.DIAL_PUSH, value);
        MainService.getInstance().writeToDevice(l2, true);
    }

    public static void sendFindPhone(){
        byte[] l2 = new L2Bean().L2Pack(BleContants.DEVICE_COMMADN, BleContants.CLOSE_FIND_PHONE, null);
        MainService.getInstance().writeToDevice(l2, true);
    }
}
