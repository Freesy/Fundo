
package com.mtk.app.thirdparty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.mediatek.wearable.Controller;
import com.mediatek.wearable.WearableManager;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothtool.StepData;
import com.szkct.weloopbtsmartdevice.data.greendao.Bloodpressure;
import com.szkct.weloopbtsmartdevice.data.greendao.Ecg;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.HearData;
import com.szkct.weloopbtsmartdevice.data.greendao.Oxygen;
import com.szkct.weloopbtsmartdevice.data.greendao.SleepData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.BloodpressureDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.HearDataDao;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.OxyDao;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.FirmWareUpdateActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.net.HTTPController;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.MusicManager;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.StringUtils;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.Query;


public class EXCDController extends Controller {
    private static final String sControllerTag = "EXCDController";

    private static final String TAG = "AppManager/EXCDController";

    private static EXCDController mInstance;

    private Context mContext = BTNotificationApplication.getInstance().getApplicationContext();

    public static final String EXTRA_DATA = "EXTRA_DATA";

    public List<StepData> runList = new ArrayList<>();

    private StringBuffer runSB = new StringBuffer();

    private StringBuffer sleepSB = new StringBuffer();

    private StringBuffer sportSB = new StringBuffer();

    private StringBuffer latitude = new StringBuffer();
    private StringBuffer longitude = new StringBuffer();

    private ArrayList<String> sportDataList;     //运动模式index数据
    private int index;

    private SimpleDateFormat format = Utils.setSimpleDateFormat("yyyy-MM-dd HH");

    public static boolean isReceiveSyncData = true;  // 是否收到了运动模式的数据

    private SimpleDateFormat getDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd");

    private List<HearData> hlist = new ArrayList<HearData>();        // 心率数据

    private DBHelper db = null;

    private EXCDController() {
        super(sControllerTag, CMD_9);
    }

    private String stepNum;
    private String sleepNum;
    private String heartNum;
    private String blood_pressureNum;
    private String blood_oxygenNum;


    public static EXCDController getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new EXCDController();
        }
        return mInstance;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Override
    public void onConnectionStateChange(int state) {
        super.onConnectionStateChange(state);
    }

    @Override
    public void send(String cmd, byte[] dataBuffer, boolean response, boolean progress, int priority) {
        try {
            super.send(cmd, dataBuffer, response, progress, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(byte[] dataBuffer) {
        super.onReceive(dataBuffer);
        String command = new String(dataBuffer);
        String[] commands = command.split(" ");
        for (Controller c : (HashSet<Controller>) WearableManager.getInstance()
                .getControllers()) {
            if (c.getCmdType() == 9) {
                HashSet<String> receivers = c.getReceiverTags();
                if (receivers != null && receivers.size() > 0 && receivers.contains(commands[1])) {
                    return;
                }
            }
        }
        Log.e(TAG, "EXCDController onReceive(), command :" + command);


//        HashSet<String> receivers = getReceiverTags();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(commands[1]);
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // Fill extra data, it is optional
        if (dataBuffer != null) {
            broadcastIntent.putExtra(EXTRA_DATA, dataBuffer);
        }
        mContext.sendBroadcast(broadcastIntent);

        getCommond(command, commands);
    }


    public synchronized void getCommond(String command, String[] commands) {
        MainService mainService = MainService.getInstance();

        List<StepData> runBeans = new ArrayList<StepData>();
        Intent intent = null;
        if (commands.length < 5) {
            Log.e(TAG, "异常数据");

            intent = new Intent();
            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
            intent.putExtra("step", "6");
            mContext.sendBroadcast(intent);

            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
            return;
        }
        String data = "";
        for (int i = 4; i < commands.length; i++) {
            if (i == commands.length - 1) {
                data += commands[i];
            } else {
                data += commands[i] + " ";
            }
        }
        //data = "GET,10,2017-5-2|28000,2017-5-3|3000,2017-5-4|45000";
        //data = "GET,10,2017-5-4|45000";
        //data = "GET,11,1,1,2017-1-1|12:05|0|15|90|0|8";
//        data = "GET,13,1,1,2017-1-1|21:00|1,2017-5-17|21:25|1,2017-5-17|21:49|2,2017-5-17|22:36|1,2017-5-17|22:46|2,2017-5-18|08:50|1,2017-5-18|08:56|2,2017-5-18|09:00|2,2017-5-18|21:25|1,2017-5-18|21:49|2,2017-5-18|22:36|1,2017-5-18|22:46|2,2017-5-19|08:50|1,2017-5-19|08:56|2,2017-5-19|09:00|2";
        String[] datalist = data.split(",");
        if (datalist[0].equals("GET")) {    //接收
            try {
                switch (datalist[1]) {
                    case "20":    // 手表相关的基本信息
                        parseEcg(data);
                        break;
                    case "0":    // 手表相关的基本信息
                        //EventBus.getDefault().post(new MessageEvent("mkt_pauseData"));
                        //GET,0,name,version,display,pedometer,sleep, heartrate, sit,personal, alarm,alert_type,battery,bt_address,software_version
                        //GET,0,2502,2.00,1|240|240,100|10,2|0|20:00|10:00,0,1,10000|1|170|60|4000|1000|10800|1988-9-8,0,1,100,6f:6a:94:c7:62:61, SW02_V1_A_160708
                        //GET,0,2502,2.02,0|48|64,1|0,2|0|22:00|08:00,0,0,10000|1|170|60|0|0|0|0-0-0,0,0,48,69:a3:91:b8:62:61,K3_B_V1.1_1705091759
                        //GET,0,2502,2.021,0|48|64,2|0,0|0|22:00|08:00,0,0,10000|1|170|60|0|0|0|0-0-0,0,0,45,68:e0:47:b4:62:61,PH_F3_B_V1.1_1709060845,0,0
                        String version = datalist[3];                      //协议版本号
                        stepNum = datalist[5].split("\\|")[1];      //分段步数条数
                        sleepNum = datalist[6].split("\\|")[1];     //分段睡眠条数
                        heartNum = datalist[7];                     //心率条数
                        String personal = datalist[9];                     //个人信息设置
                        blood_pressureNum = "0";                    //血压条数
                        blood_oxygenNum = "0";                      //血氧条数
                        String platformCode = "0";                         //型号适配
                        String autoHeart = "0";                            //心率检测
                        String hr_ecg = "0";                            //心率检测
                        if(datalist.length >= 16){
                            blood_pressureNum = datalist[15];
                        }
                        if(datalist.length >= 17){
                            blood_oxygenNum = datalist[16];
                        }
                        if(datalist.length >= 18){
                            platformCode = datalist[17];   //todo  ---  212
//                            String ada = "ad";
                        }
                        if(datalist.length >= 20){
                            autoHeart = datalist[19];
                        }

                        if(datalist.length >= 23){
                            hr_ecg = datalist[22];
                        }
                        if (!stepNum.equals("0")) {
                            BluetoothMtkChat.getInstance().syncRunIncrement();//同步时间段增量数据
                        }
                        if (!sleepNum.equals("0")) {
                            BluetoothMtkChat.getInstance().syncSleepDetail();//同步睡眠数据详情
                        }
                        if (!heartNum.equals("0")) {
                            BluetoothMtkChat.getInstance().syncHeartRate();//同步心率数据详情
                        }
                        if (!blood_pressureNum.equals("0")){
                            BluetoothMtkChat.getInstance().syncBlood_pressure();//同步血压数据详情
                        }
                        if (!blood_oxygenNum.equals("0")){
                            BluetoothMtkChat.getInstance().syncBlood_oxygen();//同步血氧数据详情
                        }

                        if(datalist.length == 16){
                            if(stepNum.equals("0") && sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")){
                                intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "6");
                                mContext.sendBroadcast(intent);

                                BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                            }
                        }else if(datalist.length >= 17){
                            if(stepNum.equals("0") && sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){
                                intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                                intent.putExtra("step", "6");
                                mContext.sendBroadcast(intent);

                                BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                            }
                        }

						if (!platformCode.equals("0")){
                            if(db == null){
                                db = DBHelper.getInstance(mContext);
                            }
                            HTTPController.SynWatchInfo(mContext,db,Integer.parseInt(platformCode));
                        }
                        if (!autoHeart.equals("0")){
                            if(autoHeart.contains("|")){
                                String[] autoHearts = autoHeart.split("\\|");
                                if(autoHearts.length >= 6){
                                    if(!"0".equals(autoHearts[5])){
                                        SharedPreUtil.setParam(mContext, SharedPreUtil.USER, SharedPreUtil.HEART_SWITCH, autoHearts[0].equals("1") ? true : false);
                                        SharedPreUtil.setParam(mContext, SharedPreUtil.USER, SharedPreUtil.HEART_START_TIME, Integer.parseInt(autoHearts[1]));
                                        SharedPreUtil.setParam(mContext, SharedPreUtil.USER, SharedPreUtil.HEART_STOP_TIME, Integer.parseInt(autoHearts[3]));
                                        SharedPreUtil.setParam(mContext, SharedPreUtil.USER, SharedPreUtil.HEART_FREQUENCY, Integer.parseInt(autoHearts[5]));
                                    }
                                }
                            }
                        }

                        if(!hr_ecg.equals("0"))
                        {
                            BluetoothMtkChat.getInstance().syncEcg();
                        }
                        /*if (!personal.equals("")){
                            if(personal.contains("|")){
                                String[] personals = personal.split("\\|");
                                String goal = personals[0];
                                String sex =personals[1];
                                String height = personals[2];
                                String weight = personals[3];

                                SharedPreferences goalPreferences = mContext.getSharedPreferences("goalstepfiles",
                                        Context.MODE_WORLD_READABLE);
                                SharedPreferences.Editor editor = goalPreferences.edit();
                                editor.putInt("setgoalstepcount", Integer.parseInt(goal));
                                editor.commit();

                                SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT, height);
                                SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.WEIGHT, weight);
                                SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.SEX, sex);
                                SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT, Utils.metricToInchForft(Float.parseFloat(height)));
                                SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN, Utils.metricToInchForin(Float.parseFloat(height)));
                                SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.WEIGHT_US, Utils.kgTolb(Float.parseFloat(height)));
                                EventBus.getDefault().post(new MessageEvent("update_userInfo"));
                            }
                        }*/

                        break;
                    case "10":           // 计步    ----- 同步每天计步数据  （总步数）
                        //GET,10,date|step|distance|calorie|time
                        //GET,10,2016-3-28|1034|6721|3212|3600,2016-3-27|2000|9721|5212|4800

                        BluetoothMtkChat.getInstance().retSyncRun();   // 回复 手表端 注释 调试用

                        for (int i = 2; i < datalist.length; i++) {
                            if (datalist[i].contains("|")) {  // 计步一天的总步数的数据长度只能为 4或5
                                String[] dataDetail = datalist[i].split("\\|");  // 用| 切割每一条 计步数据
                                String date = dataDetail[0];         //日期   一天的总步数
                                String step = dataDetail[1];        //步数
                                String distance = dataDetail[2];   //距离
                                String calorie = dataDetail[3];  //卡路里
                                //String activityTime = dataDetail[4];  //活跃时间
                                StepData runData = new StepData();
                                runData.setTime(date);// 只保存日期   2017-6-14
                                runData.setCounts(step);  // 步数值
                                runData.setCalorie(Utils.setformat(1,Float.parseFloat(calorie) / 10 + ""));  //  ------ GET,10,2017-6-14|80|453|25|0
                                runData.setDistance(Utils.setformat(1, Double.parseDouble(distance) / 10000 + ""));
                                runBeans.add(runData);
                                runList.add(runData);
                            }
                        }
                        mainService.updateRunDataArr(runList);  //todo --- MTK 历史计步数据 一天总步数的保存
                        if (runList.size() > 0) {
                            runList.clear();
                        }
                        break;
                    case "11":   // 2、同步时间段计步增量数据     ---- 每10分钟发送一次
                        //K3协议
                        //GET,11,packet_sum,packet_index,date|time|mode|delta_step|delta_distance|delta_calorie|delta_time
                        //GET,11,1,1,2016-3-28|10:30|0|10|521|212|60, 2016-3-28|10:35|0|12|541|232|60
                        String heardSplitsA = commands[4];
                        heardSplitsA.split(",");
                        String[] splitA = heardSplitsA.split(",");
                        String packet_sumA = splitA[2];
                        String packet_indexA = splitA[3];
                    /*if(Integer.parseInt(packet_sumA) != Integer.parseInt(packet_indexA)){
                        runSB.append(commands);
                        return;
                    }else{
                        runSB.append(commands);
                    }*/
                        for (int i = 4; i < datalist.length; i++) {
                            //K3 协议
                            String[] dataDetail = datalist[i].split("\\|");
                            if (dataDetail.length == 7) {  // TODO  --- 手表端数据容错判断
                                String date = dataDetail[0];         //时期    ----
                                String time = dataDetail[1];        //时间
                                String mode = dataDetail[2];        //模式
                                String step = dataDetail[3];        //步数   ---- 都为增量值
                                String distance = dataDetail[4];   //距离
                                String calorie = dataDetail[5];  //卡路里
                                //String activityTime = dataDetail[6];  //活跃时间
                                SimpleDateFormat dateForMater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);  // 2016-3-28|10:30
                                try {
                                    Date parse = dateForMater.parse(date + " " + time + ":00");
                                    StepData runData = new StepData();
                                    long sss = parse.getTime();
                                    runData.setTime(String.valueOf(parse.getTime()));
                                    runData.setCounts(step);
                                    runData.setCalorie(Utils.setformat(1,Float.parseFloat(calorie) / 10 + ""));
                                    runData.setDistance(Utils.setformat(1, Double.parseDouble(distance) / 10000 + ""));  // ----- GET,11,1,1,2017-6-14|01:40|0|31|175|9|0,2017-6-14|09:50|0|49|278|16|0
                                    runBeans.add(runData);
                                    if (i == 0) {
                                        SharedPreUtil.savePre(mContext, SharedPreUtil.SPORT, SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC), runData.getTime());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if(sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){ //只有计步数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        } else {  // 全有  !stepNum.equals("0") && sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "1");
                            mContext.sendBroadcast(intent);
                        }

                        mainService.BTdataWrite(runBeans);  // todo  保存分段步数值
                        BluetoothMtkChat.getInstance().retSyncRunIncrement(packet_sumA, packet_indexA);  //注释用 调试用
                        break;
                    case "12":             //睡眠
                        Log.e(TAG, "onReceive---睡眠");

                    case "13":              //分段睡眠数据
                        //"GET,13,1,1,2017-5-16|21:25|1,2017-4-10|21:49|2,2017-4-10|22:36|1,2017-4-10|22:46|2,2017-4-11|08:50|1,2017-4-11|08:56|2,2017-4-11|09:00|2";
                        if (datalist.length < 4) {
                            Log.e(TAG, "sleep is null");
                            return;
                        }
                        String packet_sum = datalist[2];
                        String packet_index = datalist[3];
                        if (!packet_sum.equals(packet_index)) {
                            sleepSB.append(data.replace(datalist[0] + "," + datalist[1] + "," + datalist[2] + "," + datalist[3] + ",", ""));
                        } else {
                            sleepSB.append(data.replace(datalist[0] + "," + datalist[1] + "," + datalist[2] + "," + datalist[3] + ",", ""));
                            String[] commandList = sleepSB.toString().split(",");
                            for (int i = 0; i < commandList.length; i++) {
                                long deepSleepTime = 0;   //深睡
                                long lightSleepTime = 0;   //浅睡
                                long notSleepTime = 0; //未睡眠
                                int sleepBeginHour = 0;   //开始小时
                                int sleepEndHour = 0;     //结束小时
                                long beginTime = 0;      //开始秒数
                                long endTime = 0;         //结束秒数
                                long normalBeginTime = 0;   //标准21点秒数
                                long normalEndTime = 0;   //标准9点秒数
                                if ((i + 1) == commandList.length) {
                                    break;
                                } else {
                                    if (commandList[i].split("\\|").length == 3 && commandList[i + 1].split("\\|").length == 3) {
                                        SleepData sleepData = new SleepData();
                                        String[] sleepBegin = commandList[i].replace("|", ",").split(","); // 睡眠开始时间
                                        String[] sleepEnd = commandList[i + 1].replace("|", ",").split(",");  //睡眠结束时间 todo   --- 睡眠的数据容错处理是否需要（待跟进）
                                        String sleepBeginDay = (sleepBegin[0].split("-")[0] + "-" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(sleepBegin[0].split("-")[1])) + "-" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(sleepBegin[0].split("-")[2])));     //开始日期  2017-4-10
                                        String sleepBeginTime = sleepBegin[1];                                //开始时间  21:49
                                        sleepBeginHour = Integer.parseInt(sleepBegin[1].split(":")[0]);      //开始小时  21
                                        String sleepBeginMode = sleepBegin[2];                           //睡眠模式   mode=0: 未进入睡眠 1：轻度睡眠 2：深度睡眠
                                        String sleepEndDay = (sleepEnd[0].split("-")[0] + "-" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(sleepEnd[0].split("-")[1])) + "-" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(sleepEnd[0].split("-")[2])));        //结束日期  2017-4-10
                                        String sleepEndTime = sleepEnd[1];                              //结束时间   22:36
                                        sleepEndHour = Integer.parseInt(sleepEndTime.split(":")[0]);    //结束小时  22
                                        try {
                                            beginTime = format.parse(sleepBeginDay + " " + sleepBeginHour).getTime() / 1000;   //开始时间 ---- 精确到 小时  ---  2017-4-10 21
                                            normalBeginTime = format.parse(sleepBeginDay + " " + "00").getTime() / 1000;
                                            endTime = format.parse(sleepEndDay + " " + sleepEndHour).getTime() / 1000;     //结束时间 ---- 精确到 小时  ---  2017-4-10 22
                                            normalEndTime = format.parse(sleepBeginDay + " " + "09").getTime() / 1000;
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if ((endTime - beginTime) > 3 * 24 * 60 * 60) {  //两条数据相差3天默认不要
                                            continue;
                                        }
                                /*if(endTime == beginTime){
                                    if(sleepBeginHour < 21 || sleepBeginHour > 9){     //当开始小时小于21点或者大于9点时
                                        sleepBeginTime = sleepBeginDay + " " + "21:00";
                                    }
                                    if(sleepBeginHour < 9 && sleepEndHour > 9){        //开始小时小于9点和结束小时大于9点
                                        continue;
                                    }
                                }*/
                                        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                                        try {
                                            Date begin = dfs.parse(sleepBeginDay + " " + sleepBeginTime);   // 2017-4-10 21:49
                                            Date end = dfs.parse(sleepEndDay + " " + sleepEndTime);         // 2017-4-10 22:36
                                            long between = (end.getTime() - begin.getTime()) / 1000;//除以1000是为了转换成秒    todo   --- 睡眠的有效时间(总秒数)
                                            if (Integer.parseInt(sleepBeginMode) == 0) {   //  mode=0: 未进入睡眠 1：轻度睡眠 2：深度睡眠
                                                notSleepTime = between / 60;
                                                continue;
                                            } else if (Integer.parseInt(sleepBeginMode) == 1) {  //mtk --- 1: 浅睡
                                                lightSleepTime = between / 60;  // 分钟数
                                            } else {              // mtk 2: 深睡
                                                deepSleepTime = between / 60;
                                            }
                                            sleepData.setMac(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC));
                                            sleepData.setMid(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MID));
                                            sleepData.setDeepsleep(deepSleepTime + "");
                                            sleepData.setLightsleep(lightSleepTime + "");
                                            sleepData.setDate(sleepBeginDay);
                                            sleepData.setSleepmillisecond((deepSleepTime + lightSleepTime) * 60 * 1000 + "");  // 毫秒
                                            sleepData.setStarttimes(sleepBeginDay + " " + sleepBeginTime + ":00");    // 开始时间   2017-4-10 21:22:10
                                            sleepData.setEndTime(sleepEndDay + " " + sleepEndTime + ":00");           // 结束时间   2017-4-10 22:22:10
                                            sleepData.setAutosleep(deepSleepTime + lightSleepTime + ":00");
                                            sleepData.setSleeptype(sleepBeginMode);
                                            DBHelper db = DBHelper.getInstance(mContext);
                                            db.saveSleepData(sleepData);
                                            Log.e(TAG, "sleeplist----" + i + "------" + deepSleepTime + "---" + lightSleepTime + "---" + sleepBeginDay + " " + sleepBeginTime + ":00" + "---" + sleepEndDay + " " + sleepEndTime + ":00");
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }
                            sleepSB.delete(0, sleepSB.length());
                        }
                        Log.e(TAG, "sleepSB = " + sleepSB.toString());

                        if(stepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){ //只有睡眠数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        }else if( heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){  // 有计步和睡眠数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        } else {  // 全有  !stepNum.equals("0") && sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "2");
                            mContext.sendBroadcast(intent);
                        }

                        if (mainService.isShowToast) {
                            mainService.callbackSynchronousDialog();  // 关闭同步的提示框
                            mainService.isShowToast = false;
                        }
                        BluetoothMtkChat.getInstance().retSyncSleepDetail(packet_sum, packet_index);   //todo --- 调式时，注释掉
                        break;
                    case "14":                //TODO ---- （历史数据） 手表端心率数据
                        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.DEFAULT_HEART_RATE, "1");  // 显示心率页面的 标识
                        List<HearData> heartList = new ArrayList<>();
                        for (int i = 2; i < datalist.length; i++) {
                            String[] heartData = datalist[i].split("\\|");
                            String heartDay = heartData[0].split(" ")[0].split("-")[0] + "-" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(heartData[0].split(" ")[0].split("-")[1]))
                                    + "-" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(heartData[0].split(" ")[0].split("-")[2]));
                            Log.e(TAG, "heartDay = " + heartDay);
                            String heartTime = heartData[0].split(" ")[1].split(":")[0] + ":" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(heartData[0].split(" ")[1].split(":")[1]))
                                    + ":" + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(heartData[0].split(" ")[1].split(":")[2]));
                            Log.e(TAG, "heartTime = " + heartTime);
                            String hearts = heartData[1];
                            Log.e(TAG, "hearts = " + hearts);

                            if (StringUtils.isEmpty(hearts) || Integer.valueOf(hearts) <= 0) {
                                continue;
                            }

                            HearData hearData = new HearData();
                            Date date = StringUtils.parseStrToDate(heartDay + " " + heartTime, StringUtils.SIMPLE_DATE_FORMAT);
                            if (date != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                long beginTime = calendar.getTimeInMillis() / 1000;  // 1489816800
                                hearData.setBinTime(beginTime + "");    //TODO --- 设置为时间戳格式
                            }
                            hearData.setHeartbeat(hearts);

                            hearData.setHigt_hata(hearts + "");
                            hearData.setLow_hata(hearts + "");
                            hearData.setAvg_hata(hearts + "");//平均的心率


                            if (db == null) {
                                db = DBHelper.getInstance(mContext);
                            }
                            Query query = null;
                            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {  // 需要展示的设备的数据的mac地址
                                query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC))).build();
                            } else {  //  不需要展示的设备的数据的mac地址
                                query = db.getHearDao().queryBuilder().where(HearDataDao.Properties.Mac.eq(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC))).build();  // 根据日期 查询 运动数据
                            }

                            List list = query.list();
                            if (list != null && list.size() >= 1) {
                                ArrayList<HearData> arrHear = new ArrayList<HearData>();
                                boolean flag = false;
                                for (int j = 0; j < list.size(); j++) {
                                    HearData hearDB = (HearData) list.get(j);
                                    if (hearDB.getHeartbeat().equals(hearData.getHeartbeat()) && hearDB.getBinTime().equals(hearData.getBinTime())) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    heartList.add(hearData);
                                }
                            } else if (list.size() == 0) {
                                heartList.add(hearData);
                            }
                        }
                        mainService.heartdataWrite(heartList,false);
                        BluetoothMtkChat.getInstance().retHeartRate();

                        if(stepNum.equals("0") && sleepNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){ //只有心率数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        }else if( sleepNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){  // 有计步和心率数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        }else if( stepNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){  // 有睡眠和心率数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        } else if(blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")){  // 有计步和睡眠，心率数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功

                        }else if(blood_oxygenNum.equals("0")){  // 有计步和睡眠，心率，血压数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "3");
                            mContext.sendBroadcast(intent);

                        }else {  // 全有  !stepNum.equals("0") && sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "3");
                            mContext.sendBroadcast(intent);
                        }

                        if (mainService.isShowToast) {
                            mainService.callbackSynchronousDialog();  // 关闭同步的提示框
                            mainService.isShowToast = false;
                        }
                        break;
                    case "15":
                        Log.e(TAG, "onReceive---闹钟");
                        BluetoothMtkChat.getInstance().retSyncAlarm();
                        break;

                    case "17":
                        Log.e(TAG, TAG + "手表端返回运动历史数据");
                        if(null != datalist && datalist.length >= 3){   // GET,17,1,2,3
                            sportDataList = new ArrayList<>();
                            index = 0;
                            for (int i = 2; i < datalist.length; i++) {
                                sportDataList.add(datalist[i]);
                            }
                            Log.e(TAG,"sportDataList.size() = " + sportDataList.size());
                            BluetoothMtkChat.getInstance().syncSport("," + datalist[2]);
                        }else{
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // 发广播，关闭同步的加载框
                            BTNotificationApplication.getInstance().sendBroadcast(broadcastIntent);
                            Log.e(TAG, "手表无运动历史数据");
                        }
                        /*if (datalist != null && datalist.length >= 3) {   // GET,17,1,2,3
                            if (datalist.length >= 4) {   // GET,17,8,9
                                index = 2;
                                sportDataList = datalist;
                            } else {
                                sportDataList = new String[1];
                            }
                            BluetoothMtkChat.getInstance().syncSport("," + datalist[2]);
                        } else {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // 发广播，关闭同步的加载框
                            BTNotificationApplication.getInstance().sendBroadcast(broadcastIntent);
                            Log.e(TAG, "手表无运动历史数据");
                        }*/

                        isReceiveSyncData = true;

                        break;
                    case "18":

                     /*   发送的数据：GET,18,1,1,1,18,2018|8|9|17|44|26,            184,       25,    1,       5,        3,0,3,0,2,0,0,0,0,0,0,0,0,0,0,end
                        说明：          游泳运动模式18,开始时间2018|8|9|17|44|26,用时184秒,距离25m,总趟数1, 总划水数5,泳姿3,自由泳划水数0,蛙泳划水数3,蝶泳划水数0,仰泳划水数2,其它划水数0，0,0,0,0,0,0,0,0,0,end
*/

                        // runSplits  --- GET,18,1,1,1,2,2017|5|12|17|1|58,185,203,9,911,1,104,63,0,0,0,0,0,0,0,0,0,0,0,22.557743|113.946195,end
                        String runSplits = commands[4];
                        runSplits.split(",");
                        if (runSplits.length() < 11) {
                            Log.e(TAG, "运动数据为空");
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainService.ACTION_SYNFINSH_SPORTS); // 发广播，关闭同步的加载框
                            BTNotificationApplication.getInstance().sendBroadcast(broadcastIntent);
                            return;
                        }
                        String[] sportLit = runSplits.split(",");
                        StringBuffer sb = new StringBuffer();
                        for (int j = 5; j <sportLit.length ; j++) {
                            if(j == sportLit.length -1){
                                sb.append(sportLit[j]);
                            }else {
                                sb.append(sportLit[j] + ",");
                            }
                        }
                        String substring = sb.toString();
                        String run_packet_sum = sportLit[3];
                        String run_packet_index = sportLit[4];
                        String[] runs = substring.split(",");
                        String mark = sportLit[sportLit.length - 1];
                        if (mark.equals("end")) {
                            sportSB.append(substring);
                            runData(sportSB.toString(), mContext);
                            sportSB.delete(0, sportSB.length());
                        } else {
                            sportSB.append(substring);
                        }
                        BluetoothMtkChat.getInstance().retSyncSport("," + datalist[2] + "," + run_packet_sum + "," + run_packet_index);//todo ---  调试时可以注释掉
                        if(null != sportDataList) {
                            if (sportDataList.size() < 0) {
                                if (mainService.isShowToast) {
                                    mainService.callbackSynchronousDialog();  // 关闭同步的提示框
                                    mainService.isShowToast = false;
                                }
                            } else {
                                if (run_packet_sum.equals(run_packet_index)) {
                                    Log.i(TAG,"index = " + index  + " ;   sportDataList.size() = " + sportDataList.size());
                                    index++;
                                    if (index < (sportDataList.size() - 1)) {
                                        BluetoothMtkChat.getInstance().syncSport("," + sportDataList.get(index));
                                    } else if (index == (sportDataList.size() - 1)) {
                                        BluetoothMtkChat.getInstance().syncSport("," + sportDataList.get(index));
                                        if (mainService.isShowToast) {
                                            mainService.callbackSynchronousDialog();  // 关闭同步的提示框
                                            mainService.isShowToast = false;
                                        }
                                    }
                                }

                            }
                        }else{
                            if (mainService.isShowToast) {
                                mainService.callbackSynchronousDialog();  // 关闭同步的提示框
                                mainService.isShowToast = false;
                            }
                        }
                        break;
                    case "51":   //TODO  ---  历史血压
                        String mac = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                        if(db == null){
                            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
                        }
                        Query query = db.getBloodpressureDao().queryBuilder()
                                .where(BloodpressureDao.Properties.Mac.eq(mac))
                                .build();
                        List<Bloodpressure> list = query.list();
                        int index = list.size();
                        List<Bloodpressure> bpList = new ArrayList<>();
                        for (int i = 2; i < datalist.length; i++) {
                            String[] bpData = datalist[i].split("\\|");
                            String day = bpData[0].split(" ")[0].split("-")[0] + "-"
                                    + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[1])) + "-"
                                    + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[2]));
                            String hour = bpData[0].split(" ")[1];
                            String highBP = bpData[1];
                            String lowBP= bpData[2];
                            boolean isFlag = false;
                            for (int j = 0; j < list.size(); j++) {
                                if(list.get(j).getData().equals(day)
                                        && list.get(j).getHour().equals(hour)
                                        && highBP.equals(list.get(j).getHeightBlood())
                                        && lowBP.equals(list.get(j).getMinBlood())){
                                    isFlag = true;
                                    break;
                                }
                            }
                            if(isFlag){
                                continue;
                            }
                            Bloodpressure bloodpressure = new Bloodpressure();
                            bloodpressure.setData(day);
                            bloodpressure.setHour(hour);
                            bloodpressure.setHeightBlood(highBP);
                            bloodpressure.setMinBlood(lowBP);
                            bloodpressure.setMac(mac);
                            index++;
                            bloodpressure.setConunt(index+"");
                            bpList.add(bloodpressure);
                        }
                        MainService.getInstance().saveBloodpressure(bpList);
                        BluetoothMtkChat.getInstance().retBlood_pressure();

                        if(stepNum.equals("0") && sleepNum.equals("0")  && heartNum.equals("0")  && blood_oxygenNum.equals("0")){ //只有血压数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        }else if(blood_oxygenNum.equals("0")){  // 有计步和睡眠，心率，血压数据
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "6");
                            mContext.sendBroadcast(intent);

                            BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功
                        }else {  // 全有  !stepNum.equals("0") && sleepNum.equals("0") && heartNum.equals("0")  && blood_pressureNum.equals("0")  && blood_oxygenNum.equals("0")
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                            intent.putExtra("step", "5");
                            mContext.sendBroadcast(intent);
                        }

                        break;
                    case "52":   //TODO  历史血氧
                        String address = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                        if(db == null){
                            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
                        }
                        Query queryOXY = db.getOxygenDao().queryBuilder()
                                .where(OxyDao.Properties.Mac.eq(address))
                                .build();
                        List<Oxygen> listoxy = queryOXY.list();
                        List<Oxygen> oxyList = new ArrayList<>();
                        List listHigh = new ArrayList();  //最高血氧值
                        for (int i = 0; i < listoxy.size(); i++) {
                            listHigh.add(listoxy.get(i).getOxygen());
                        }
                        for (int i = 2; i < datalist.length; i++) {
                            String[] bpData = datalist[i].split("\\|");
                            String day = bpData[0].split(" ")[0].split("-")[0] + "-"
                                    + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[1])) + "-"
                                    + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[2]));
                            String hour = bpData[0].split(" ")[1];
                            String oxy = bpData[1];
                            boolean isFlag = false;
                            for (int j = 0; j < listoxy.size(); j++) {
                                if(listoxy.get(j).getData().equals(day)
                                        && listoxy.get(j).getHour().equals(hour)
                                        && oxy.equals(listoxy.get(j).getOxygen())){
                                    isFlag = true;
                                    break;
                                }
                            }
                            if(isFlag){
                                continue;
                            }
                            Oxygen oxygen = new Oxygen();
                            oxygen.setData(day);
                            oxygen.setHour(hour);
                            oxygen.setOxygen(oxy);
                            listHigh.add(oxy);
                            if(listHigh.size() == 1){
                                oxygen.setHeightOxygen(oxy);
                                oxygen.setMinOxygen(oxy);
                            }else {
                                oxygen.setHeightOxygen(Integer.parseInt(Collections.max(listHigh)+"")+"");
                                oxygen.setMinOxygen(Integer.parseInt(Collections.min(listHigh)+"")+"");
                            }
                            oxygen.setMac(address);
                            oxyList.add(oxygen);
                        }
                        MainService.getInstance().saveOxygen(oxyList);
                        BluetoothMtkChat.getInstance().retBlood_oxygen();

                        intent = new Intent();
                        intent.setAction(MainService.ACTION_SYNFINSH);    // 发数据同步成功的广播
                        intent.putExtra("step", "6");
                        mContext.sendBroadcast(intent);

                        BTNotificationApplication.isSyncEnd = true;  //todo --- 同步数据成功

                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (datalist[0].equals("SEND")) {    //实时  （被动接收数据）
            try {
                if(datalist.length < 3){
                    Log.e(TAG,"实时数据异常");
                    return;
                }
                switch (datalist[1]) {
                    case "17":
                        EventBus.getDefault().post(data);
                        break;
                    case "13":
                        EventBus.getDefault().post(data);
                        break;
                    case "10":           // 计步
                        Log.e(TAG, "onReceive---实时计步" + datalist[2]);
                        String step = datalist[2];
                        String date = step.split("\\|")[0];
                        Calendar cal = Calendar.getInstance();
                        String time = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE)
                                + ":" + cal.get(Calendar.SECOND);
                        int value = Integer.parseInt(step.split("\\|")[1]);
                        String distance = Utils.setformat(1, Double.parseDouble(step.split("\\|")[2]) / 10000 + "");
                        String calorie = Utils.setformat(1, Double.parseDouble(step.split("\\|")[3]) / 10 + "");
                        StepData stepData = new StepData();
                        stepData.setTime(date + " " + time);
                        stepData.setCounts(value + "");
                        stepData.setCalorie(calorie + "");
                        stepData.setDistance(String.valueOf(distance));
                        runList.add(stepData);
                        mainService.updateRunDataArr(runList);  //todo ---MTK 实时计步数据 保存为一天的总步数
                        runList.clear();
//                        BluetoothMtkChat.getInstance().retSyncRun();  // todo --- ????  应该是不需要 回复
                        break;
                    case "12":             //TODO ----  mtk  实时心率
                        String heart = datalist[2].split("\\|")[1];  // 98
                        String heartTime = datalist[2].split("\\|")[0];  // 16:20:05
                        if (!StringUtils.isEmpty(heart) && Integer.valueOf(heart) > 0) {
                            if (hlist.size() > 0) {
                                hlist.clear();
                            }

                            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                            String heartDay = getDateFormat.format(curDate);   // todo  --- 2017-06-14

                            HearData hearData = new HearData();
                            Date dateH = StringUtils.parseStrToDate(heartDay + " " + heartTime, StringUtils.SIMPLE_DATE_FORMAT);
                            if (dateH != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(dateH);
                                long beginTime = calendar.getTimeInMillis() / 1000;  // 1489816800
                                hearData.setBinTime(beginTime + "");    //TODO --- 设置为时间戳格式
                            }
                            hearData.setHeartbeat(heart);

                            hearData.setHigt_hata(heart + "");
                            hearData.setLow_hata(heart + "");
                            hearData.setAvg_hata(heart + "");//平均的心率

                            hlist.add(hearData);

                            mainService.heartdataWrite(hlist,true);  // 保存心率数据


                            if(BTNotificationApplication.isSyncEnd){     //todo  --- 参考BLE 同步数据成功 才发实时心率数据
                                intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNARTHEART);    //发送实时心率
                                intent.putExtra("heart", heart);
//                                intent.putExtra("time", heartTime);
                                mContext.sendBroadcast(intent);
                            }

//                            intent = new Intent();
//                            intent.setAction(MainService.ACTION_SYNARTHEART);    //发送实时心率
//                            intent.putExtra("heart", heart);
//                            intent.putExtra("time", heartTime);
//                            mContext.sendBroadcast(intent);
                        }
                        break;
                    case "51":
                        String mac = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                        if(db == null){
                            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
                        }
                        Query query = db.getBloodpressureDao().queryBuilder()
                                .where(BloodpressureDao.Properties.Mac.eq(mac))
                                .build();
                        int index = query.list().size();
                        List<Bloodpressure> bpList = new ArrayList<>();
                        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                        String bloodDay = getDateFormat.format(curDate);   // todo  --- 2017-06-14
                        for (int i = 2; i < datalist.length; i++) {
                            index++;
                            String[] bpData = datalist[i].split("\\|");
                            String hour = "";
                            String day = "";
                            if(bpData[0].contains(" ")){
                                hour = bpData[0].split(" ")[1];
                                day = bpData[0].split(" ")[0].split("-")[0] + "-"
                                        + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[1])) + "-"
                                        + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[2]));
                            }else{
                                hour = bpData[0];
                                day = bloodDay;
                            }
                            String highBP = bpData[1];
                            String lowBP= bpData[2];
                            Bloodpressure bloodpressure = new Bloodpressure();
                            bloodpressure.setData(day);
                            bloodpressure.setHour(hour);
                            bloodpressure.setHeightBlood(highBP);
                            bloodpressure.setMinBlood(lowBP);
                            bloodpressure.setMac(mac);
                            bloodpressure.setConunt(index+"");
                            bpList.add(bloodpressure);

                            if(BTNotificationApplication.isSyncEnd) {
                                intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNARTBP);    //TODO  ----  发送 实时  血压     updata_Bloodpressureone
                                BTNotificationApplication.getInstance().sendBroadcast(intent);
                            }

                        }
                        MainService.getInstance().saveBloodpressure(bpList);
                        break;
                    case "52":
                        String address = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC).toString();
                        if(db == null){
                            db = DBHelper.getInstance(BTNotificationApplication.getInstance());
                        }
                        Query queryOXY = db.getOxygenDao().queryBuilder()
                                .where(OxyDao.Properties.Mac.eq(address))
                                .build();
                        List<Oxygen> listoxy = queryOXY.list();
                        List listHigh = new ArrayList();
                        for (int i = 0; i < listoxy.size(); i++) {
                            listHigh.add(listoxy.get(i).getOxygen());
                        }
                        String oxyDay = getDateFormat.format(new Date(System.currentTimeMillis()));   // todo  --- 2017-06-14
                        List<Oxygen> oxyList = new ArrayList<>();
                        for (int i = 2; i < datalist.length; i++) {
                            String[] bpData = datalist[i].split("\\|");
                            String hour = "";
                            String day = "";
                            if(bpData[0].contains(" ")){
                                hour = bpData[0].split(" ")[1];
                                day = bpData[0].split(" ")[0].split("-")[0] + "-"
                                        + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[1])) + "-"
                                        + String.format(Locale.ENGLISH,"%02d",Integer.parseInt(bpData[0].split(" ")[0].split("-")[2]));
                            }else{
                                hour = bpData[0];
                                day = oxyDay;
                            }
                            String oxy = bpData[1];
                            Oxygen oxygen = new Oxygen();
                            oxygen.setData(day);
                            oxygen.setHour(hour);
                            oxygen.setOxygen(oxy);
                            listHigh.add(oxy);
                            if(listHigh.size() == 1) {
                                oxygen.setHeightOxygen(oxy);
                                oxygen.setMinOxygen(oxy);
                            }else{
                                oxygen.setHeightOxygen(Integer.parseInt(Collections.max(listHigh)+"")+"");
                                oxygen.setMinOxygen(Integer.parseInt(Collections.min(listHigh)+"")+"");
                            }
                            oxygen.setMac(address);
                            oxyList.add(oxygen);

                            if(BTNotificationApplication.isSyncEnd) {
                                intent = new Intent();
                                intent.setAction(MainService.ACTION_SYNARTBO);    //TODO  v-----  发送实时血氧
//                                intent.putExtra("bo", oxy);
                                BTNotificationApplication.getInstance().sendBroadcast(intent);
                            }
                        }
                        MainService.getInstance().saveOxygen(oxyList);
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (datalist[0].equals("SET")) {       //设置命令
            try {
                switch (datalist[1]) {
                    case "18":         //个人信息
                        EventBus.getDefault().post(data);
                        break;
                    case "17":         //个人信息
                        if (!TextUtils.isEmpty(data)) {
                            if (datalist[1].equals("17")) {
                                if (datalist.length >= 6) {
                                    int  speed = Integer.parseInt(datalist[2]);
                                    int gain = Integer.parseInt(datalist[3]);
                                    int rate = Integer.parseInt(datalist[4]);
                                    int dimension = Integer.parseInt(datalist[5]);
                                    SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_SPEED,speed);
                                    SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_GAIN,gain);
                                    SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_RATE,rate);
                                    SharedPreUtil.setParam(BTNotificationApplication.getInstance(),SharedPreUtil.USER,SharedPreUtil.ECG_DIMENSION,dimension);
                                }
                            }
                        }
                        break;
                    case "10":         //个人信息
                        if(datalist.length == 3) {
                            if(datalist[2].contains("|")){
                                String[] user = datalist[2].split("\\|");
                                if(user.length >= 4){
                                    String goal = user[0];
                                    String sex = user[1];
                                    String height = user[2];
                                    String weight = user[3];
                                    SharedPreferences goalPreferences = mContext.getSharedPreferences("goalstepfiles",
                                            Context.MODE_PRIVATE);   // todo --- ??????????     MODE_PRIVATE
                                    SharedPreferences.Editor editor = goalPreferences.edit();
                                    editor.putInt("setgoalstepcount", Integer.parseInt(goal));
                                    editor.commit();

                                    SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT, height);
                                    SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.WEIGHT, weight);
                                    if(sex.equals("1")){
                                        SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.SEX, "0");
                                    }else{
                                        SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.SEX, "1");
                                    }
                                    SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT_FT, Utils.metricToInchForft(Float.parseFloat(height)));
                                    SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.HEIGHT_IN, Utils.metricToInchForin(Float.parseFloat(height)));
                                    SharedPreUtil.savePre(mContext, SharedPreUtil.USER, SharedPreUtil.WEIGHT_US, Utils.kgTolb(Float.parseFloat(weight)));
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(MainService.ACTION_MYINFO_CHANGE);   //更新昵称（没有更新头像）
                                    mContext. sendBroadcast(broadcastIntent);
                                    EventBus.getDefault().post(new MessageEvent("update_userInfo"));
                                }
                            }
                        }
                        break;
                    case "13":         //设置闹钟
                        // SET,13,num,time|days|ring| alert_type|flag|type
                        // SET,13,2,08:00|0111111|1|0|1|0, 09:00|0111111|2|2|1|5
                        int number = Integer.parseInt(datalist[2]);    //闹钟数量
                        if (number == 0) {
                            return;
                        }
                        for (int i = 3; i < datalist.length; i++) {
                            String[] alarmDetail = datalist[i].split("\\|");
                            String time = alarmDetail[0];   //时间
                            String days = alarmDetail[1];   //每天的响铃
                            String ring = alarmDetail[2];   //铃声 1-5
                            String alert_type = alarmDetail[3];   //报警提示  0：响铃；1：震动；2：震动响铃
                            String flag = alarmDetail[4];   //是否开启
                            String type = alarmDetail[5];   //提醒类型 1：吃药，2：会议，3：睡眠，4：运动，5：起床
                        }
                        break;
                    case "14":
                        //SET,14,ON/OFF
                        //SET,14,1/0     0:关闭相机；1：打开相机；2：拍照
                        switch (datalist[2]) {
                            case "1":
                                if ((Boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true)) {
                                    intent = new Intent("0x46");
                                    mContext.sendBroadcast(intent);
                                    BluetoothMtkChat.getInstance().retSynCamera();
                                    isEnableOpenCammer = true;
                                }
                                break;
                            case "0":
                                if ((Boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true)) {
                                    intent = new Intent("0x48");
                                    mContext.sendBroadcast(intent);
                                    BluetoothMtkChat.getInstance().retSynCamera();
                                    isEnableOpenCammer = false;
                                }
                                break;
                            case "2":
                                if ((Boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CAMERA_NOTIFY, true)&& isEnableOpenCammer) {
                                    intent = new Intent("0x47");
                                    mContext.sendBroadcast(intent);
                                    BluetoothMtkChat.getInstance().retSynCamera();
                                }
                                break;
                        }
//                        BluetoothMtkChat.getInstance().retSynCamera();
                        break;
                    case "35":     //单位设置
                        if(datalist.length >= 3){
                            String unit = datalist[2];   //公英制
                            if(unit.equals("0")){    //公制
                                SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.YES);
                            }else if(unit.equals("1")){  //英制
                                SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.METRIC,SharedPreUtil.NO);
                            }
                        }
                        if(datalist.length >= 4){
                            String temp = datalist[3];   //温度
                            if(temp.equals("0")){     //摄氏度
                                SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.UNIT_TEMPERATURE,SharedPreUtil.YES);
                            }else if(temp.equals("1")){   //华氏度
                                SharedPreUtil.setParam(mContext,SharedPreUtil.USER,SharedPreUtil.UNIT_TEMPERATURE,SharedPreUtil.NO);
                            }
                        }
                        EventBus.getDefault().post(new MessageEvent("update_unit"));
                        BluetoothMtkChat.getInstance().retSynUnit();
                        break;
                    case "40":
                        /*if (datalist[2].equals("1")) {
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_FINDWATCHON);    //打开查找手机
                            mContext.sendBroadcast(intent);
//                        BluetoothMtkChat.getInstance().sendFindWatchOn();
                        } else if (datalist[2].equals("0")) {

                            if(!isRepeatSend()){
                                intent = new Intent();
                                intent.setAction(MainService.ACTION_FINDWATCHOFF);    //关闭查找手机
                                mContext.sendBroadcast(intent);
                            }
//                        BluetoothMtkChat.getInstance().sendFindWatchOff();
                        }*/
                        if (datalist[2].equals("1")){
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_FINDWATCHON);    //打开查找手机
                            mContext.sendBroadcast(intent);
                        }else if(datalist[2].equals("0")){
                            intent = new Intent();
                            intent.setAction(MainService.ACTION_FINDWATCHOFF);    //关闭查找手机
                            mContext.sendBroadcast(intent);
                        }
                        //BluetoothMtkChat.getInstance().retSyncFindWatchData();
                        break;
                    case "43":
                        if (datalist[2].equals("1")) {
                            BluetoothMtkChat.getInstance().retSynMusicData();
                        } else if (datalist[2].equals("3")) {
                            MusicManager.getInstance().open();
                        } else if (datalist[2].equals("4")) {
                            MusicManager.getInstance().pause();
                        } else if (datalist[2].equals("5")) {
                            MusicManager.getInstance().up_music();
                        } else if (datalist[2].equals("6")) {
                            MusicManager.getInstance().down_music();
                        } else if (datalist[2].equals("7")) {
                            MusicManager.getInstance().next();
                        } else if (datalist[2].equals("8")) {
                            MusicManager.getInstance().last();
                        }
                        break;
                }
            }catch (Exception e){
               e.printStackTrace();
            }
        }
    }

    boolean isEnableOpenCammer = false;

    private long mLastClickTime = 0L;
    private boolean isRepeatSend() {
        long time = System.currentTimeMillis();
        long slotT = 0;
        slotT = time - mLastClickTime;
        mLastClickTime = time;
        if (0 < slotT && slotT < 800) {
            return true;
        }
        return false;
    }

    public void runData(String dataString, Context context) {
        List<GpsPointDetailData> gpspointdetailList = new ArrayList<GpsPointDetailData>();//
        GpsPointDetailData gpspointdetaildata = new GpsPointDetailData();

         /*   发送的数据：GET,18,1,1,1,18,2018|8|9|17|44|26,            184,       25,    1,       5,        3,0,3,0,2,0,0,0,0,0,0,0,0,0,0,end
                        说明：          游泳运动模式18,开始时间2018|8|9|17|44|26,用时184秒,距离25m,总趟数1, 总划水数5,泳姿3,自由泳划水数0,蛙泳划水数3,蝶泳划水数0,仰泳划水数2,其它划水数0，0,0,0,0,0,0,0,0,0,end
*/

        // runSplits  --- GET,18,1,1,1,2,2017|5|12|17|1|58,185,203,9,911,1,104,63,0,0,0,0,0,0,0,0,0,0,0,22.557743|113.946195,end

        //16,     //运动模式
        // 2017|11|4|14|53|11,   //开始时间
        // 688,                  //用时
        // 0,                    //距离
        // 0,                    //卡路里
        // 0,                    //平均配速
        // 0.000,                //平均速度
        // 0,                    //平均步频
        // 0,                    //平均步长
        // 0,                    //平均海拔
        // 74,                   //心率
        // 0,                    //暂停时长
        // 0,                    //暂停次数
        // 0,                    //
        // 60:71:82:74:101:81:85:82:60:82:94:60:70:77:70:76:79:61:60:59:76:82:76:73:61:84:78:72:63:78:72:78:66,
        // 0,
        // 0,
        // 0,
        // -308:135,
        // 0,
        // 0,
        // end

        String[] runs = dataString.split(",");
        String[] times = runs[1].split("\\|");  // 运动开始时间
        String hour = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(times[3]));
        String minute = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(times[4]));

        String second = String.format(Locale.ENGLISH,"%02d", Integer.parseInt(times[5]));    //只需要到分

        String date = times[0] + "-" + times[1] + "-" + times[2];
//        String date2 = times[0] + "." + times[1] + "." +times[2];

        String date2 = times[0] + "." + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(times[1])) + "." + String.format(Locale.ENGLISH,"%02d", Integer.parseInt(times[2]));

        String time = hour + ":" + minute + ":" + second;  //
        String time2 = hour + ":" + minute;  // + ":" + second

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        gpspointdetaildata.setMac(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MAC));
        gpspointdetaildata.setMid(SharedPreUtil.readPre(mContext, SharedPreUtil.USER, SharedPreUtil.MID));
        gpspointdetaildata.setDeviceType("2");
        gpspointdetaildata.setSportType(runs[0]); // 运动模式的类型
        try {
            gpspointdetaildata.setTimeMillis((format.parse(date + " " + time)).getTime() / 1000 + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        gpspointdetaildata.setsTime(runs[2]);   // 运动用时时间
        gpspointdetaildata.setMile(Double.parseDouble(runs[3]));  // 运动距离    ----
        gpspointdetaildata.setCalorie(runs[4]);  // 卡路里        -----     总趟数      // TODO  ----    CALORIE     总趟数
        gpspointdetaildata.setmCurrentSpeed(runs[5]);     //TODO MTK  平均配速     // TODO  ----    MCURRENTSPEED  总划水数
        gpspointdetaildata.setSpeed(runs[6]);  // 平均速度    // TODO  ----    SPEED      泳姿  暂未用到
//        gpspointdetaildata.setMCurrentSpeed(runs[5]);
        gpspointdetaildata.setSportTime(String.format(Locale.ENGLISH, "%1$02d:%2$02d:%3$02d", Utils.tolong(runs[2]) / 60 / 60, Utils.tolong(runs[2]) / 60 % 60, Utils.tolong(runs[2]) % 60));
//        gpspointdetaildata.setDate(date + " " + time);  // 这种格式不对     setArrcadence
        gpspointdetaildata.setDate(date2 + " " + time2);

        String arrcadence = runs[7];   // TODO  ----    ARRCADENCE    自由泳划水数
        if(arrcadence.contains("|")){          //后面新加协议，步频|步数
            gpspointdetaildata.setArrcadence(arrcadence.split("\\|")[0]);
            gpspointdetaildata.setStep(arrcadence.split("\\|")[1]);
        }else{
            gpspointdetaildata.setStep("0");
            gpspointdetaildata.setArrcadence(runs[7]); // 设置步频率数组  TODO --- MTK 步频数组为 0  --- 将平均步频 设置 到 步频数组中
        }

        gpspointdetaildata.setAve_step_width(runs[8]);  // 平均步幅  // TODO --- AVE_STEP_WIDTH   蛙泳划水数

        if(runs[0].equals("18")){  // todo --- 游泳模式
            gpspointdetaildata.setArraltitude(runs[9]);  // TODO --- 放到海拔数组 字段
        }else{
            gpspointdetaildata.setAltitude(runs[9]); // todo  --- 非游泳模式放到 海拔 字段
        }

//        gpspointdetaildata.setAltitude(runs[9]);       // TODO --- ?????? 没有海拔只有海拔数组   蝶泳划水数
        gpspointdetaildata.setHeartRate(runs[10]);      // TODO --- HEARTRATE     仰泳划水数
        gpspointdetaildata.setPauseTime(runs[11]);       // TODO ---  PAUSETIME    其它划水数
        gpspointdetaildata.setPauseNumber(runs[12]);
        gpspointdetaildata.setMax_step_width(runs[13]);  // 最大步副
//        gpspointdetaildata.setMin_step_width(runs[14]);  // 最小步副
        gpspointdetaildata.setMin_step_width("");  // 最小步副
//        gpspointdetaildata.setAve_step_width((Double.parseDouble(runs[13]) + Double.parseDouble(runs[14]))/2+"");  // 平均步副
//        String arrHear = runs[15];
        String arrHear = runs[14];
        String splitArrheartRate = arrHear.replace(":", "&");
        gpspointdetaildata.setArrheartRate(splitArrheartRate);//心率数组

//        String arrSet =  runs[17];
//        String splitArrcadence = arrSet.replace(":", "&");
//        gpspointdetaildata.setArrcadence(splitArrcadence);//步频数组

        String arrSpeed = runs[17];
        String splitArrspeed = arrSpeed.replace(":", "&");
        gpspointdetaildata.setArrspeed(splitArrspeed);//速度数组
        String arrTotalSpeed = runs[18];
        String splitTotalSpeed = arrTotalSpeed.replace(":", "&");
        gpspointdetaildata.setArrTotalSpeed(splitTotalSpeed);//配速数组

        String arraltitude = runs[19];
        String splitltitude = arraltitude.replace(":", "&");

        if(runs[0].equals("18")) {  // todo --- 游泳模式
            gpspointdetaildata.setAltitude(splitltitude);//海拔数组
        }else{
            gpspointdetaildata.setArraltitude(splitltitude);//海拔数组
        }

//        gpspointdetaildata.setArraltitude(splitltitude);//海拔数组

        String arraTracks = runs[20];//   String arraTracks =  runs[20];

        if (StringUtils.isEmpty(arraTracks) || arraTracks.equals("0")) {
            latitude = latitude.append("&");
            longitude = longitude.append("&");
        } else {
            String[] splitTrack = arraTracks.split(":");
            for (int i = 0; i < splitTrack.length; i++) {
                String tracks = splitTrack[i];
                String[] track = tracks.split("\\|");
                if(track.length == 2){
                    latitude = latitude.append(track[0] + "&");      //
                    longitude = longitude.append(track[1] + "&");
                }
//                latitude = latitude.append(track[0] + "&");      //
//                longitude = longitude.append(track[1] + "&");
            }
        }

        gpspointdetaildata.setArrLat(latitude.toString());
        gpspointdetaildata.setArrLng(longitude.toString());
        gpspointdetailList.add(gpspointdetaildata);
        Utils.recordGpsPointForDataBase(gpspointdetailList, null, context);
        latitude.delete(0, latitude.length());
        longitude.delete(0, longitude.length());
    }

    ArrayList mHeartList = new ArrayList<Byte>();
    ArrayList mEcgList = new ArrayList<Byte>();
    int mEcgPackageIndex =0;
    String mEcgStr = "";

    private void parseEcg(String ecg)
    {
        String[] datalist = ecg.split(",");
        if(datalist.length<4)
        {
            return;
        }
        int packageCount = Integer.parseInt(datalist[2]);
        int packageIndex = Integer.parseInt(datalist[3]);
        if(packageCount <=0)
        {
            return;
        }
        if (packageIndex == 1) {
            mEcgList.clear();
            mHeartList.clear();
            mEcgPackageIndex = packageIndex;
            mEcgStr = "";
        } else {
            if (mEcgPackageIndex + 1 != packageIndex) {
                return;
            }
            mEcgPackageIndex = packageIndex;
        }
        int startIndex = datalist[0].length()+datalist[1].length()+datalist[2].length()+datalist[3].length()+4;
        mEcgStr = mEcgStr+ecg.substring(startIndex);
        if (packageIndex  == packageCount) {
            saveEcg();
        }
        else
        {
            BluetoothMtkChat.getInstance().retSyncEcgDetail(packageCount+"", packageIndex+"");
        }
    }

    private void saveEcg()
    {
        String[] items = mEcgStr.split("#");
        for(int k=0;k<items.length;k++)
        {
            Ecg ecg = new Ecg();
            String hearts = "";
            String ecgs = "";
            String  binTime = "";
            String hourStr = "";
            String date = "";//日期
            String   Mac = "";
            String itemStr = items[k];
            if(TextUtils.isEmpty(itemStr))
            {
                continue;
            }
            String[] ecgStrs =  itemStr.split(",");
            for (int i = 0; i < ecgStrs.length; i++) {
                String dateStr = ecgStrs[i];
                String[] datas = dateStr.split("\\|");
                for(int j=0;j<datas.length;j++)
                {
                    if(TextUtils.isEmpty(datas[j]))
                    {
                        continue;
                    }
                    if(i==0)
                    {
                        switch (j)
                        {
                            case 0:
                                date = datas[0];
                                String[] dates = date.split("-");
                                String year = String.format(Locale.ENGLISH, "%02d", Integer.parseInt(dates[0]));
                                String month = String.format(Locale.ENGLISH, "%02d", Integer.parseInt(dates[1]));
                                String day = String.format(Locale.ENGLISH, "%02d", Integer.parseInt(dates[2]));
                                date = year+"-"+month+"-"+day;
                                break;
                            case 1:
                                hourStr = datas[1];
                                break;
                            case 2:
                                hearts = hearts+Integer.parseInt(datas[j])+";";
                                break;
                            default:
                                ecgs = ecgs+Integer.parseInt(datas[j])+";";
                                break;
                        }
                    }
                    else
                    {
                        if(j==0)
                        {
                            hearts = hearts+Integer.parseInt(datas[j])+";";
                        }
                        else
                        {
                            ecgs = ecgs+Integer.parseInt(datas[j])+";";
                        }
                    }
                }
            }

            ecg.setEcgs(ecgs);
            ecg.setHearts(hearts);
            String mac = "";
            if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC).equals("")) {
                mac = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.MAC);
            } else {
                mac = SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SHOWMAC);
            }
            ecg.setMac(mac);
            try {
                binTime = mSimpleDateFormat.parse(date + " " + hourStr).getTime() + "";
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ecg.setBinTime(binTime);
            ecg.setDate(date);
            MainService.getInstance().saveEcgData(ecg);
        }

    }
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
