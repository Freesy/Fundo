package com.szkct.weloopbtsmartdevice.data.greendao;

import android.support.annotation.NonNull;

import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ${wyl}
 */

public class Bloodpressure implements Serializable,Comparable<Bloodpressure>{
    String   Mac;
    String HeightBlood;//高压
    String MinBlood;//低压
    String Hour;//测试时间
     Long id;
    String Data;//日期
    String Conunt;//次数

    public String getConunt() {
        return Conunt;
    }

    public void setConunt(String conunt) {
        Conunt = conunt;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        this.Mac = mac;
    }

    public String getHeightBlood() {
        return HeightBlood;
    }

    public void setHeightBlood(String heightBlood) {
        HeightBlood = heightBlood;
    }

    public String getMinBlood() {
        return MinBlood;
    }

    public void setMinBlood(String minBlood) {
        MinBlood = minBlood;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public Bloodpressure() {
        super();
    }

    public Bloodpressure(Long id, String heightBlood, String minBlood, String hour, String data, String mac,String conunt) {
        this.Mac = mac;
        this.HeightBlood = heightBlood;
        this. MinBlood = minBlood;
        this. Hour = hour;
        this.id = id;
        this.Data = data;
        this.Conunt = conunt;
    }

    @Override
    public int compareTo(Bloodpressure o) {
        SimpleDateFormat simpleDateFormat = Utils.setSimpleDateFormat("yyyy-MM-dd HH:mm");
        long time = 0;
        long times = 0;
        try {
            time = simpleDateFormat.parse(o.getData() + " " + o.getHour()).getTime();
            times = simpleDateFormat.parse(this.Data + " " + this.Hour).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(time > times){
            return -1;
        }else if(time == times){
            return 0;
        }else{
            return 1;
        }
    }
}
