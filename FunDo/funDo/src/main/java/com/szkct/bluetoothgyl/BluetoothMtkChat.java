package com.szkct.bluetoothgyl;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mtk.app.thirdparty.EXCDController;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.PinyinUtils;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.UTIL;
import com.szkct.weloopbtsmartdevice.util.Utils;
import com.szkct.weloopbtsmartdevice.util.WeatherCodeDesc;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.mtk.app.remotecamera.RemoteCamera.sContext;


/**
 * @author zhaixiang$.
 * @explain
 * @time 2017/3/21$ 10:21$.
 */
public class BluetoothMtkChat {

    private static final String TAG = BluetoothMtkChat.class.getName();

    public static final String BASE = "KCT_PEDOMETER kct_pedometer 0 0 "; //MTK通信头部

    // X02D 同步每天计步数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_RUN = "GET,10";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_RUN = "RET,GET,10";
    // X02D 同步时间段增量数据(步数)
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_RUN_INCREMENT = "GET,11";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_RUN_INCREMENT = "RET,GET,11";
    // X02D 发送当前时间段增量计步数据
    public static final String NEW_EXTRA_COMMAND_RECEIVE_SEND_RUN_INCREMENT = "RET,SEND,11";

    // X02D 同步每天睡眠数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_SLEEP = "GET,12";
    public static final String COMMAND_SYCN_ECG = "GET,20";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_SLEEP = "RET,GET,12";
    // X02D 同步时间段睡眠数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_SLEEP_INCREMENT = "GET,13";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_SLEEP_INCREMENT = "RET,GET,13";

    public static final String COMMAND_RECEIVE_ECG_INCREMENT = "RET,GET,20";

    // X02D 获取单次心率测试数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_HEART = "GET,14";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_HEART = "RET,GET,14";


    //获取单次血压测试数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_BLOOD_PRESSURE = "GET,51";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_BLOOD_PRESSURE = "RET,GET,51";

    //获取单次血氧测试数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_BLOOD_OXYGEN = "GET,52";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_BLOOD_OXYGEN = "RET,GET,52";

    //设置闹钟
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_ALARM = "SET,13";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_ALARM_SUCCESS = "RET,SET,13,1";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_ALARM_FAIL = "RET,SET,13,0";

    //相机命令
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_CRAMER_CLOSE = "SET,14,0";
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_CRAMER_OPEN = "SET,14,1";
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_CRAMER = "SET,14,2";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_CRAMER_SUCCESS = "RET,SET,14,1";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_CRAMER_FAIL = "RET,SET,14,0";

    //APK运行状态
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_APK_STATE_ON = "SET,15,1";
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_APK_STATE_OFF = "SET,15,0";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_APK_STATE = "RET,SET,15";


    //获取运动索引
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_SPORT_INDEX = "GET,17";
    //获取运动数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_SPORT = "GET,18";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_SPORT = "RET,GET,18";

    //获取表盘信息
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_WATCH_PAD = "GET,30";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_WATCH_PAD = "RET,GET,30";

    //查找手表
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_FIND_WATCH_ON = "SET,40,1";
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_FIND_WATCH_OFF = "SET,40,0";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_FIND_WATCH_SUCCESS = "RET,SET,40,1";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_FIND_WATCH_FAIL = "RET,SET,40,0";

    //推送表盘数据
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_SEND_WATCH_PAD = "SET,31";

    //删除表盘
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_DELETE_WATCH_PAD = "SET,32";

    //音乐协议返回
    public static final String NEW_EXTRA_COMMAND_MUSIC_RECEIVE = "RET,SET,43,1";

    //设置个人信息
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_USERINFO = "SET,10,";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_USERINFO = "RET,SET,10,1";

    //自动检测心率
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_AUTOHEART = "SET,19,";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_AUTOHEART = "RET,SET,19,1";

    //推送天气
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_WEATHER = "WEATHER;";

    //久坐提醒
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_LONGSIT= "SET,12,";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_LONGSIT= "RET,SET,12,1";

    //喝水提醒
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_DRINK = "SET,21,";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_DRINK = "RET,SET,21,1";

    //气象指数
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_METEOROLOGY = "SET,18,";

    //单位设置
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_UNIT = "SET,35,";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_UNIT = "RET,SET,35,1";

    //同步语言
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_LANGUAGE = "SET,44,";

    //同步时间参数
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_TIME = "SET,45,";

    //抬手亮屏
    public static final String NEW_EXTRA_COMMAND_ENQUIRE_RAISINGBRIGHT = "SET,46,";
    public static final String NEW_EXTRA_COMMAND_RECEIVE_RAISINGBRIGHT = "RET,SET,46,1";

    public static final String HEART_STATUS_ON_COMMAND = "SET,16,1";
    public static final String HEART_STATUS_OFF_COMMAND ="SET,16,0";

    //电子发票
    public static final String SEND_INVOICE_DATA = "SET,20,";
    //收款二维码
    public static final String SEND_PAYMENT_CODE = "SET,22,";

    // X02D 是否发送SEND数据
    public static final String EXTRA_COMMAND_ENQUIRE = "GET,1";

    public static final String EXTRA_COMMAND_REQUEST = "GET,0";

    public static BluetoothMtkChat mInstance;

    private BluetoothMtkChat(){}

    public static BluetoothMtkChat getInstance(){
        if (mInstance == null){
            synchronized (BluetoothMtkChat.class){
                if (mInstance ==null){
                    mInstance = new BluetoothMtkChat();
                }
            }
        }
        return mInstance;
    }

    //  //获取手表数据 (手机相关的基本信息)
    public void getWathchData(){                                                                                             //     命令号     命令内容               是否有响应     进度               优先级
        EXCDController.getInstance().send("KCT_PEDOMETER kct_pedometer 0 0 5 ","GET,0".getBytes(),true,false,0);  // send(String cmd, byte[] dataBuffer, boolean response, boolean progress, int priority)
        //EventBus.getDefault().post(new MessageEvent("mtk_sendData"));
    }



    //心率数据接收成功
    public void sendHeard(){
        EXCDController.getInstance().send("GET","14".getBytes(),true,false,0);
    }
    //同步每天计步数据
    public void syncRun(){    //每天计步数据
        send(NEW_EXTRA_COMMAND_ENQUIRE_RUN);   // GET,10
    }
    //反馈每天计步 (恢复手表端)
    public void retSyncRun(){
        send(NEW_EXTRA_COMMAND_RECEIVE_RUN);  // RET,GET,10
    }

    //同步时间段增量数据
    public void syncRunIncrement(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_RUN_INCREMENT);   // GET,11
    }

    public void retSyncRunIncrement(String packet_sum,String packet_index){
        send(NEW_EXTRA_COMMAND_RECEIVE_RUN_INCREMENT+","+packet_sum+","+packet_index);
    }

    //同步每天睡眠
    public void syncSleep(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_SLEEP);
    }

    //同步每天睡眠
    public void syncEcg(){
        send(COMMAND_SYCN_ECG);
    }

    //反馈每天睡眠数据
    public void retSyncSleep(){
        send(NEW_EXTRA_COMMAND_RECEIVE_SLEEP);
    }  // todo --- 没有用到 ？？？

    //同步时间段睡眠数据
    public void syncSleepDetail(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_SLEEP_INCREMENT);
    }

    //反馈时间段睡眠
    public void retSyncSleepDetail(String packet_sum,String packet_index){
        send(NEW_EXTRA_COMMAND_RECEIVE_SLEEP_INCREMENT+","+packet_sum+","+packet_index);
    }

    //反馈时间段睡眠
    public void retSyncEcgDetail(String packet_sum,String packet_index){
        send(COMMAND_RECEIVE_ECG_INCREMENT+","+packet_sum+","+packet_index);
    }


    //获取心率数据
    public void syncHeartRate(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_HEART);
    }

    //获取血压数据
    public void syncBlood_pressure(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_BLOOD_PRESSURE);
    }

    //获取血氧数据
    public void syncBlood_oxygen(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_BLOOD_OXYGEN);
    }

    //反馈血压数据
    public void retBlood_pressure(){
        send(NEW_EXTRA_COMMAND_RECEIVE_BLOOD_PRESSURE);
    }

    //反馈血氧数据
    public void retBlood_oxygen(){
        send(NEW_EXTRA_COMMAND_RECEIVE_BLOOD_OXYGEN);
    }

    //反馈心率数据
    public void retHeartRate(){
        send(NEW_EXTRA_COMMAND_RECEIVE_HEART);
    }

    //设置闹钟
    public void syncAlarm(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_ALARM);
    }

    //反馈获取闹钟结果
    public void retSyncAlarm(){
        send(NEW_EXTRA_COMMAND_RECEIVE_ALARM_SUCCESS);
    }

    public void sendApkState(){   //前台运行
        send(NEW_EXTRA_COMMAND_ENQUIRE_APK_STATE_ON);  // SET,15,1     1:在前台；0：不在前台
    }

    public void retApkState(){
        send(NEW_EXTRA_COMMAND_RECEIVE_APK_STATE);
    }

    //获取运动索引
    public void syncSportIndex(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_SPORT_INDEX);
    }

    //获取运动数据
    public void syncSport(String index){
        send(NEW_EXTRA_COMMAND_ENQUIRE_SPORT, index);
    }

    //反馈运动数据
    public void retSyncSport(String index){
        send(NEW_EXTRA_COMMAND_RECEIVE_SPORT, index);
    }

    //获取表盘数据
    public void syncWatchPadData(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_WATCH_PAD);
    }

    //反馈相机
    public void retSynCamera(){
        send(NEW_EXTRA_COMMAND_RECEIVE_CRAMER_SUCCESS);
    }

    //反馈获取表盘数据
    public void retSyncWatchPadData(){
        send(NEW_EXTRA_COMMAND_RECEIVE_WATCH_PAD);
    }

    //反馈音乐协议
    public void retSynMusicData(){
        send(NEW_EXTRA_COMMAND_MUSIC_RECEIVE);
    }

    //设置个人信息
    public void synUserInfo(String value){
        send(NEW_EXTRA_COMMAND_ENQUIRE_USERINFO,value);
    }

    //自动检测心率
    public void synAutoHeart(String value){
        send(NEW_EXTRA_COMMAND_ENQUIRE_AUTOHEART,value);
    }

    //反馈个人信息
    public void retUserInfo(){
        send(NEW_EXTRA_COMMAND_RECEIVE_USERINFO);
    }

    //推送天气
    public void synWeather(){
        String city = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "city");

        String date = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "date");
        String low = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "low");
        String high = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "high");
        String code = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "code");

        String nextdate = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextdate");
        String nextlow = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextlow");
        String nexthigh = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nexthigh");
        String nextcode = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextcode");

        String thirddate = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirddate");
        String thirdlow = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirdlow");
        String thirdhigh = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirdhigh");
        String thirdcode = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirdcode");

        int weeks = 0;
        int nextweeks = 0;
        int thirdweeks = 0;
        try {
            weeks = Utils.dayForWeek(date);
            nextweeks = Utils.dayForWeek(nextdate);
            thirdweeks = Utils.dayForWeek(thirddate);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String value = "";
        if(!TextUtils.isEmpty(low) && !TextUtils.isEmpty(high) && !TextUtils.isEmpty(code)){
            try {
                city = PinyinUtils.chineseToPinYinF(BTNotificationApplication.getInstance(),city);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            value  += (city + ";" + weeks + "," + date + "," + low + "," + high + "," + WeatherCodeDesc.weatherCodeTransform(Integer.parseInt(code)));
        }

        if(!TextUtils.isEmpty(nextlow) && !TextUtils.isEmpty(nexthigh) && !TextUtils.isEmpty(nextcode)){
            if(!TextUtils.isEmpty(value)){
                value += "|";
            }
            value  += (nextweeks + "," + nextdate + "," + nextlow + "," + nexthigh + "," + WeatherCodeDesc.weatherCodeTransform(Integer.parseInt(nextcode)));
        }

        if(!TextUtils.isEmpty(thirdlow) && !TextUtils.isEmpty(thirdhigh) && !TextUtils.isEmpty(thirdcode)){
            if(!TextUtils.isEmpty(value)){
                value += "|";
            }
            value  += (thirdweeks + "," + thirddate + "," + thirdlow + "," + thirdhigh + "," + WeatherCodeDesc.weatherCodeTransform(Integer.parseInt(thirdcode)));
        }

        send(NEW_EXTRA_COMMAND_ENQUIRE_WEATHER,value);
    }

    //久坐提醒
    public void synLongSit(String value) {
        send(NEW_EXTRA_COMMAND_ENQUIRE_LONGSIT,value);
    }

    //喝水提醒
    public void retUserInfo(String value){
        send(NEW_EXTRA_COMMAND_ENQUIRE_DRINK,value);
    }

    //气象指数
    public void synMeteorology(){
        SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();   // 当前天的日期  2017-06-28
        calendar.setTime(new Date());
        String mcurDate = getDateFormat.format(calendar.getTime());
        String date = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "date");
        String nextdate = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "nextdate");
        String thirddate = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "thirddate");
        String ziwaixian = "";
        String qiya = "";
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
        /*String ziwaixian = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "ziwaixian");
        String qiya = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "qiya");*/
        String wendu = UTIL.readPre(BTNotificationApplication.getInstance(), "weather", "wendu");
        if(!StringUtils.isEmpty(ziwaixian) && !StringUtils.isEmpty(qiya) &&
                !StringUtils.isEmpty(wendu)){
            int qy = Integer.valueOf(qiya);
            int hb = (qy - 970)*9;
            String value = qiya + "|" + hb + "|" +ziwaixian + "|" + wendu + "|" + "0|0|0";
            send(NEW_EXTRA_COMMAND_ENQUIRE_METEOROLOGY,value);
        }

    }


    //单位设置
    public void synUnit(String value){
        send(NEW_EXTRA_COMMAND_ENQUIRE_UNIT,value);
    }

    //同步语言
    public void synLanguage(String value){
        send(NEW_EXTRA_COMMAND_ENQUIRE_LANGUAGE,value);
    }

    //同步时间参数
    public void synTime(Context context){
        int format = 1; //时制
        int zone = 0;  //时区
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);//获取当前系统时间格式
        if (StringUtils.isEmpty(strTimeFormat)){
            format = 1;
        }
        if ("24".equals(strTimeFormat)){
            format = 1;
        }else{
            format = 0;
        }
        String strTz = DateUtil.getCurrentTimeZone();  // +02.0   // +09.50   +12.75     +09.50
        String fuhao = strTz.substring(0,1); // +
        String[] shiqu = strTz.split("\\.");
        int xiaoshu =  Integer.valueOf(shiqu[1]);
        long time = System.currentTimeMillis() / 1000;   // 1532315293 (05:08)   --- 2018/7/23 11:8:13   (8-2 = 6)

        if(xiaoshu > 0){
            int ewaimiao = xiaoshu*1*6*6;  // xiaoshu*0.01*60*60
            if(fuhao.equals("+")){
                time += ewaimiao;
            }else {
                time -= ewaimiao;
            }
        }

        Log.e("时区", "strTz = " + strTz + " ; format = " + format + " ; time = " + time);
        String value = format + "|" + strTz + "|" + time;  // 1|+02.0|1532315293
        send(NEW_EXTRA_COMMAND_ENQUIRE_TIME,value);
    }

    //抬手亮屏
    public void synRaisingbright(String value){
        send(NEW_EXTRA_COMMAND_ENQUIRE_RAISINGBRIGHT,value);
    }

    //反馈单位设置
    public void retAutoHeart(){
        send(NEW_EXTRA_COMMAND_RECEIVE_AUTOHEART);
    }

    //反馈久坐提醒
    public void retSynLongSit(){
        send(NEW_EXTRA_COMMAND_RECEIVE_LONGSIT);
    }

    //反馈喝水提醒
    public void retSynDrink(){
        send(NEW_EXTRA_COMMAND_RECEIVE_DRINK);
    }

    //反馈单位设置
    public void retSynUnit(){
        send(NEW_EXTRA_COMMAND_RECEIVE_UNIT);
    }

    //反馈抬手亮屏
    public void retSynRaisingbright(){
        send(NEW_EXTRA_COMMAND_RECEIVE_RAISINGBRIGHT);
    }


    /***
     * 推送表盘到手表
     * @param data :1,1,1,1,2,137,80  时钟索引，图片类型，packet_sum,packet_index,len,data
     */
    public void sendWatchPad(String data){
        send(NEW_EXTRA_COMMAND_ENQUIRE_SEND_WATCH_PAD, data);

    }


    /***
     * 删除表盘
     * @param index ",10"  以，开头，后续有数据用，隔开
     */
    public void deleteWatchPad(String index){
        send(NEW_EXTRA_COMMAND_ENQUIRE_DELETE_WATCH_PAD,index);
    }


    /**
     * 通过MTK接口发送数据到手表
     * @param cmd 命令
     */
    private void send(String cmd){
        int len = cmd.length();
        //Log.e(TAG, "MTK send without data：" + BASE + len + " " + cmd);
        EXCDController.getInstance().send(BASE + len + " ", cmd.getBytes(), true, false, 0);
        Log.e(TAG,"send = " + BASE + len + " " + cmd);
    }

    /**
     * 同上
     * @param cmd
     * @param data 数据
     */
    private void send(String cmd,String data){
        String total = cmd+data;
        int len = total.length();
        //Log.e(TAG, "MTK send with data：" + BASE + len + " " + total);
        EXCDController.getInstance().send(BASE + len + " ", total.getBytes(), true, false, 0);
        Log.e(TAG,"send = " + BASE + len + " " + total);
    }
////////////////////////////////////////////////////////////////////////////////////////////// add 0531
    //发送心电状态
    public void sendHeartStatus(boolean isOn){
        send(isOn?HEART_STATUS_ON_COMMAND:HEART_STATUS_OFF_COMMAND);
    }

    //打开查找设备
    public void sendFindWatchOn(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_FIND_WATCH_ON);
    }

    //关闭查找设备
    public void sendFindWatchOff(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_FIND_WATCH_OFF);
    }

    //反馈查找设备数据
    public void retSyncFindWatchData(){
        send(NEW_EXTRA_COMMAND_RECEIVE_FIND_WATCH_SUCCESS);  // todo --- 回复 该命令后，手表端 会响铃（相当于查找设备命令）
    }

    //关闭相机
    public void sendCloseCramer(){
        send(NEW_EXTRA_COMMAND_ENQUIRE_CRAMER_CLOSE);
    }

    /**
     * 发送发票抬头
     * @param s_default     默认显示第几个
     * @param lists_Abs     备注
     * @param lists_data    加密后的发票字符串
     */
    public void sendInvoiceDataS(int s_default, ArrayList<String> lists_Abs, ArrayList<String> lists_data){
        int packet_sum=lists_Abs.size();
        for(int i=0;i<lists_Abs.size();i++){
            if(i!=lists_Abs.size()-1)
                send(SEND_INVOICE_DATA,packet_sum+","+(i+1)+","+s_default+","+lists_Abs.get(i)+"|"+lists_data.get(i)+",");
            else
                send(SEND_INVOICE_DATA,packet_sum+","+(i+1)+","+s_default+","+lists_Abs.get(i)+"|"+lists_data.get(i));
        }
    }

    /**
     * 清空发票抬头
     */
    public void sendClearInvoiceDataS(){
        send(SEND_INVOICE_DATA,"1,1,0,0");
    }

    /**
     * 发送二维码收款码
     * @param type 0支付宝 1微信
     * @param msg
     */
    public void sendPayment(int type,String msg){
        send(SEND_PAYMENT_CODE,type+","+msg);
    }

    /**
     * 清除二维码收款码
     * @param type
     */
    public void sendClearPayment(int type){
        send(SEND_PAYMENT_CODE,type+",0");
    }

}
