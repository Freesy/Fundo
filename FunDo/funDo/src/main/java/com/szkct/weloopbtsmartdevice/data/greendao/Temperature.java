package com.szkct.weloopbtsmartdevice.data.greendao;

import com.szkct.weloopbtsmartdevice.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ${wyl}
 * 血氧
 */

public class Temperature implements Comparable<Temperature>{
    String   Mac;
    String TemperatureValue;//高压
    Long id;
    String  binTime;
    String date;//日期

    public Temperature(Long id, String mac, String date,String  binTime,String TemperatureValue) {
        this.Mac = mac;
        this. date = date;
        this.id = id;
        this.binTime = binTime;
        this.TemperatureValue = TemperatureValue;
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

    public Temperature() {
        super();
    }

    public void setBinTime(String binTime) {
        this.binTime = binTime;
    }

    public String getBinTime() {
        return binTime;
    }

    public void setTemperatureValue(String temperatureValue) {
        TemperatureValue = temperatureValue;
    }

    public String getTemperatureValue() {
        return TemperatureValue;
    }

    @Override
    public int compareTo(Temperature o) {
//        SimpleDateFormat simpleDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd HH:mm");
//        long time = 0;
//        long times = 0;
//        try {
//            time = simpleDateFormat.parse(o.getData() + " " + o.getHour()).getTime();
//            times = simpleDateFormat.parse(this.Data + " " + this.Hour).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if(time > times){
//            return -1;
//        }else if(time == times){
            return 0;
//        }else{
//            return 1;
//        }
    }
}
