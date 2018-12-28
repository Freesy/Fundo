package com.szkct.weloopbtsmartdevice.data.greendao;

import java.io.Serializable;

/**
 * Created by ${wyl}
 * 血氧
 */

public class Ecg implements Serializable {//implements Comparable<Ecg>{
    String   Mac;
    String hearts = "";//高压
    String ecgs = "";//高压
    private Long id;
    String  binTime;
    String date;//日期

    public Ecg(Long id, String mac, String date, String  binTime, String hearts, String ecgs) {
        this.Mac = mac;
        this. date = date;
        this.id = id;
        this.binTime = binTime;
        this.hearts = hearts;
        this.ecgs = ecgs;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public Ecg() {
        super();
    }

    public void setBinTime(String binTime) {
        this.binTime = binTime;
    }

    public String getBinTime() {
        return binTime;
    }

    public void setEcgs(String ecgs) {
        this.ecgs = ecgs;
    }

    public void setHearts(String hearts) {
        this.hearts = hearts;
    }

    public String getEcgs() {
        return ecgs;
    }

    public String getHearts() {
        return hearts;
    }

//    @Override
//    public int compareTo(Ecg o) {
////        SimpleDateFormat simpleDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd HH:mm");
////        long time = 0;
////        long times = 0;
////        try {
////            time = simpleDateFormat.parse(o.getData() + " " + o.getHour()).getTime();
////            times = simpleDateFormat.parse(this.Data + " " + this.Hour).getTime();
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
////        if(time > times){
////            return -1;
////        }else if(time == times){
//            return 0;
////        }else{
////            return 1;
////        }
//    }
}
