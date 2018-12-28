package com.szkct.weloopbtsmartdevice.data;


import com.google.gson.annotations.SerializedName;

/**
 * 型号适配
 */
public class WatchInfoData {

    private Long id;
    @SerializedName("ecg")
    private String ecg;     //心电
    @SerializedName("bodyTemperature")
    private String bodyTemperature;     //体温
    @SerializedName("qrcodenotice")
    private String qrcodenotice;     //二维码推送
    @SerializedName("wechatSport")
    private String wechatSport;      //微信运动
    @SerializedName("autoheart")
    private String autoheart;        //心率自动检测
    @SerializedName("appnotice")
    private String appnotice;        //消息推送
    @SerializedName("callnotice")
    private String callnotice;       //来电提醒
    @SerializedName("platform")
    private String platform;         //平台
    @SerializedName("smartphoto")
    private String smartphoto;       //智能拍照
    @SerializedName("number")
    private String number;           //序号
    @SerializedName("weathernotice")
    private String weathernotice;    //天气推送
    @SerializedName("remindMode")
    private String remindMode;       //提醒模式（亮屏，震动，亮屏+震动）
    @SerializedName("model")
    private String model;            //设备型号
    @SerializedName("oxygen")
    private String oxygen;           //血氧
    @SerializedName("smartalarm")
    private String smartalarm;       //智能闹钟
    @SerializedName("smsnotice")
    private String smsnotice;        //短信提醒
    @SerializedName("sports")
    private String sports;           //运动
    @SerializedName("meteorology")
    private String meteorology;      //气象指数
    @SerializedName("firware")
    private String firware;          //固件升级
    @SerializedName("longsit")
    private String longsit;          //久坐提醒
    @SerializedName("blood")
    private String blood;            //血压
    @SerializedName("heart")
    private String heart;            //心率
    @SerializedName("watchnotice")
    private String watchnotice;      //表盘推送
    @SerializedName("drinknotice")
    private String drinknotice;      //喝水提醒
    @SerializedName("nodisturb")
    private String nodisturb;        //勿扰模式
    @SerializedName("raisingbright")
    private String raisingbright;    //抬手亮屏
    @SerializedName("btcall")
    private String btcall;           //蓝牙通话
    @SerializedName("board")
    private String board;            //主板名称
    @SerializedName("updateTimes")
    private String update_time;      //型号更新时间
    @SerializedName("createTimes")
    private String times;            //服务器更新时间
    @SerializedName("unitSetup")
    private String unitSetup;        //单位设置
    @SerializedName("pointerCalibration")
    private String pointerCalibration;    //指针校准
    @SerializedName("sleep")
    private String sleeps;            //睡眠
    @SerializedName("sos")
    private String sos;          //SOS紧急拨号
    @SerializedName("assistInput")
    private String assistInput;   //协助输入
	 @SerializedName("invoiceTitle")
    private String faPiao;    //TODO -- 发票
    @SerializedName("receiptCode")
    private String shouKuanewm;    //TODO -- 收款二维码
    @SerializedName("bluetoothMusic")
    private String bluetoothMusic;    //TODO -- 蓝牙音乐

    public String getBluetoothMusic() {
        return bluetoothMusic;
    }

    public void setBluetoothMusic(String bluetoothMusic) {
        this.bluetoothMusic = bluetoothMusic;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber(){
        return number;
    }

    public String getMeteorology() {
        return meteorology;
    }

    public void setMeteorology(String meteorology) {
        this.meteorology = meteorology;
    }

    public String getRemindMode() {
        return remindMode;
    }

    public void setRemindMode(String remindMode) {
        this.remindMode = remindMode;
    }

    public String getWechatSport() {
        return wechatSport;
    }

    public void setWechatSport(String wechatSport) {
        this.wechatSport = wechatSport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQrcodenotice() {
        return qrcodenotice;
    }

    public void setQrcodenotice(String qrcodenotice) {
        this.qrcodenotice = qrcodenotice;
    }

    public String getAutoheart() {
        return autoheart;
    }

    public void setAutoheart(String autoheart) {
        this.autoheart = autoheart;
    }

    public String getAppnotice() {
        return appnotice;
    }

    public void setAppnotice(String appnotice) {
        this.appnotice = appnotice;
    }

    public String getCallnotice() {
        return callnotice;
    }

    public void setCallnotice(String callnotice) {
        this.callnotice = callnotice;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSmartphoto() {
        return smartphoto;
    }

    public void setSmartphoto(String smartphoto) {
        this.smartphoto = smartphoto;
    }

    public String getWeathernotice() {
        return weathernotice;
    }

    public void setWeathernotice(String weathernotice) {
        this.weathernotice = weathernotice;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOxygen() {
        return oxygen;
    }

    public void setOxygen(String oxygen) {
        this.oxygen = oxygen;
    }

    public String getSmartalarm() {
        return smartalarm;
    }

    public void setSmartalarm(String smartalarm) {
        this.smartalarm = smartalarm;
    }

    public String getSmsnotice() {
        return smsnotice;
    }

    public void setSmsnotice(String smsnotice) {
        this.smsnotice = smsnotice;
    }

    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports = sports;
    }

    public String getFirware() {
        return firware;
    }

    public void setFirware(String firware) {
        this.firware = firware;
    }

    public String getLongsit() {
        return longsit;
    }

    public void setLongsit(String longsit) {
        this.longsit = longsit;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getWatchnotice() {
        return watchnotice;
    }

    public void setWatchnotice(String watchnotice) {
        this.watchnotice = watchnotice;
    }

    public String getDrinknotice() {
        return drinknotice;
    }

    public void setDrinknotice(String drinknotice) {
        this.drinknotice = drinknotice;
    }

    public String getNodisturb() {
        return nodisturb;
    }

    public void setNodisturb(String nodisturb) {
        this.nodisturb = nodisturb;
    }

    public String getRaisingbright() {
        return raisingbright;
    }

    public void setRaisingbright(String raisingbright) {
        this.raisingbright = raisingbright;
    }

    public String getBtcall() {
        return btcall;
    }

    public void setBtcall(String btcall) {
        this.btcall = btcall;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getUnitSetup() {
        return unitSetup;
    }

    public void setUnitSetup(String unitSetup) {
        this.unitSetup = unitSetup;
    }

    public String getPointerCalibration() {
        return pointerCalibration;
    }

    public void setPointerCalibration(String pointerCalibration) {
        this.pointerCalibration = pointerCalibration;
    }

    public String getSleep() {
        return sleeps;
    }

    public void setSleep(String sleep) {
        this.sleeps = sleep;
    }


    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public String getAssistInput() {
        return assistInput;
    }

    public void setAssistInput(String assistantInput) {
        this.assistInput = assistantInput;
    }
	
	 public String getFaPiao() {
        return faPiao;
    }

    public void setFaPiao(String faPiao) {
        this.faPiao = faPiao;
    }

    public String getShouKuanewm() {
        return shouKuanewm;
    }

    public String getSleeps() {
        return sleeps;
    }

    public void setSleeps(String sleeps) {
        this.sleeps = sleeps;
    }


    public void setBodyTemperature(String bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }


    public String getBodyTemperature() {
        return bodyTemperature;
    }

    public String getEcg() {
        return ecg;
    }

    @Override
    public String toString() {
        return "WatchInfoData{" +
                "id=" + id +
                ", qrcodenotice='" + qrcodenotice + '\'' +
                ", wechatSport='" + wechatSport + '\'' +
                ", autoheart='" + autoheart + '\'' +
                ", appnotice='" + appnotice + '\'' +
                ", callnotice='" + callnotice + '\'' +
                ", platform='" + platform + '\'' +
                ", smartphoto='" + smartphoto + '\'' +
                ", number='" + number + '\'' +
                ", weathernotice='" + weathernotice + '\'' +
                ", remindMode='" + remindMode + '\'' +
                ", model='" + model + '\'' +
                ", oxygen='" + oxygen + '\'' +
                ", smartalarm='" + smartalarm + '\'' +
                ", smsnotice='" + smsnotice + '\'' +
                ", sports='" + sports + '\'' +
                ", meteorology='" + meteorology + '\'' +
                ", firware='" + firware + '\'' +
                ", longsit='" + longsit + '\'' +
                ", blood='" + blood + '\'' +
                ", heart='" + heart + '\'' +
                ", watchnotice='" + watchnotice + '\'' +
                ", drinknotice='" + drinknotice + '\'' +
                ", nodisturb='" + nodisturb + '\'' +
                ", raisingbright='" + raisingbright + '\'' +
                ", btcall='" + btcall + '\'' +
                ", board='" + board + '\'' +
                ", update_time='" + update_time + '\'' +
                ", times='" + times + '\'' +
                ", unitSetup='" + unitSetup + '\'' +
                ", pointerCalibration='" + pointerCalibration + '\'' +
                ", sleeps='" + sleeps + '\'' +
                ", sos='" + sos + '\'' +
                ", assistInput='" + assistInput + '\'' +
                ", faPiao='" + faPiao + '\'' +
                ", shouKuanewm='" + ecg + '\'' +
                ", shouKuanewm='" + bodyTemperature + '\'' +
                ", shouKuanewm='" + shouKuanewm + '\'' +
                ", bluetoothMusic='" + bluetoothMusic + '\'' +
                ", ecg='" + ecg + '\'' +
                ", bodyTemperature='" + bodyTemperature + '\'' +
                '}';
    }

    public void setShouKuanewm(String shouKuanewm) {
        this.shouKuanewm = shouKuanewm;
    }

    public WatchInfoData(Long id, String qrcodenotice, String wechatSport, String autoheart, String appnotice, String callnotice, String platform, String smartphoto, String number, String weathernotice, String remindMode, String model, String oxygen, String smartalarm, String smsnotice, String sports, String meteorology, String firware, String longsit, String blood, String heart, String watchnotice, String drinknotice, String nodisturb, String raisingbright, String btcall, String board, String update_time, String times, String unitSetup, String pointerCalibration, String sleep
                       , String sos, String assistInput, String faPiao, String shouKuanewm, String bluetoothMusic,String ecg,String bodyTemperature) {
        this.id = id;
        this.qrcodenotice = qrcodenotice;
        this.wechatSport = wechatSport;
        this.autoheart = autoheart;
        this.appnotice = appnotice;
        this.callnotice = callnotice;
        this.platform = platform;
        this.smartphoto = smartphoto;
        this.number = number;
        this.weathernotice = weathernotice;
        this.remindMode = remindMode;
        this.model = model;
        this.oxygen = oxygen;
        this.smartalarm = smartalarm;
        this.smsnotice = smsnotice;
        this.sports = sports;
        this.meteorology = meteorology;
        this.firware = firware;
        this.longsit = longsit;
        this.blood = blood;
        this.heart = heart;
        this.watchnotice = watchnotice;
        this.drinknotice = drinknotice;
        this.nodisturb = nodisturb;
        this.raisingbright = raisingbright;
        this.btcall = btcall;
        this.board = board;
        this.update_time = update_time;
        this.unitSetup = unitSetup;
        this.pointerCalibration = pointerCalibration;
        this.sleeps = sleep;
        this.sos = sos;
        this.assistInput = assistInput;
		 this.faPiao = faPiao;
        this.shouKuanewm = shouKuanewm;
        this.bluetoothMusic = bluetoothMusic;
        this.ecg = ecg;
        this.bodyTemperature = bodyTemperature;
    }



    public WatchInfoData(){}

}
