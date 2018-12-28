package com.szkct.map.bean;

import java.io.Serializable;

public class GpsPoint implements Serializable {
    private static final long serialVersionUID = 4598910917081132372L;
    public String lat;//经度
    public String lon;//纬度
    public String mile;//距离
    public String ele;//海拔
    public String date;//系统时间
    public String speed;//配速
    public String sportTime; //运动时间  时分秒
    public String calorie;//消耗 千卡
    public String sTime; //运动时间 秒数
    public String totalPs; //总时间 总距离 配速

    public String getTotalPs() {
        return totalPs;
    }

    public void setTotalPs(String totalPs) {
        this.totalPs = totalPs;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEle() {
        return ele;
    }

    public void setEle(String ele) {
        this.ele = ele;
    }

    public String getMile() {
        return mile;
    }

    public void setMile(String mile) {
        this.mile = mile;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSportTime() {
        return sportTime;
    }

    public void setSportTime(String sportTime) {
        this.sportTime = sportTime;
    }

    public GpsPoint() {
    }

    public GpsPoint(String lat, String lon, String mile, String ele, String date, String speed, String sportTime,String calorie ,String sTime,String totalPs) {
        super();
        this.lat = lat;//1
        this.lon = lon;//2
        this.mile = mile;//3
        this.ele = ele;//4
        this.date = date;//5
        this.speed = speed;//6
        this.sportTime = sportTime;//7
        this.calorie = calorie;//8
        this.sTime = sTime;//9
        this.totalPs = totalPs;//10
    }

}
