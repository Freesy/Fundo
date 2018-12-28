package com.szkct.weloopbtsmartdevice.data.greendao;

import android.support.annotation.NonNull;

import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by ${wyl}
 * 血氧
 */

public class Oxygen implements Comparable<Oxygen>{
    String   Mac;
    String Oxygen;//高压
    String HeightOxygen;//高压
    String MinOxygen;//高压
    String Hour;//测试时间
     Long id;
    String Data;//日期

    public Oxygen(Long id,String mac, String data, String hour, String minOxygen, String heightOxygen, String oxygen) {
        this.Mac = mac;
        this. Data = data;
        this. Hour = hour;
        this.id = id;
        this. MinOxygen = minOxygen;
       this. HeightOxygen = heightOxygen;
        this.  Oxygen = oxygen;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public String getOxygen() {
        return Oxygen;
    }

    public void setOxygen(String oxygen) {
        Oxygen = oxygen;
    }

    public String getMinOxygen() {
        return MinOxygen;
    }

    public void setMinOxygen(String minOxygen) {
        MinOxygen = minOxygen;
    }

    public String getHeightOxygen() {
        return HeightOxygen;
    }

    public void setHeightOxygen(String heightOxygen) {
        HeightOxygen = heightOxygen;
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

    public Oxygen() {
        super();
    }


    @Override
    public int compareTo(Oxygen o) {
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
