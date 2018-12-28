package com.szkct.weloopbtsmartdevice.data.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table HEART.
 */
public class GpsPointData {

    private Long id;
    /** Not-null value. */
    private String mac;  // 蓝牙mac
    /** Not-null value. */
    private String mid;
    /** Not-null value. */
    private String arrLat;  // 经度值数组
    /** Not-null value. */
    private String arrLng; // 纬度值数组
    /** 实时速度 */
    private String arrSpeed;  // 实时速度数组
    /** 步频 */
    private String arrBuPing;  // 步频数组
    /** 海拔 */
    private String arrAltitude;  // 海拔数组
    /** 时间戳. */
    private String timeMillis;

    public String getBufuList() {
        return bufuList;
    }

    public void setBufuList(String bufuList) {
        this.bufuList = bufuList;
    }

    /** 步幅 */
    private String bufuList;

    public String getArrAltitude() {
        return arrAltitude;
    }

    public void setArrAltitude(String arrAltitude) {
        this.arrAltitude = arrAltitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(String timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getArrLat() {
        return arrLat;
    }

    public void setArrLat(String arrLat) {
        this.arrLat = arrLat;
    }

    public String getArrLon() {
        return arrLng;
    }

    public void setArrLon(String arrLng) {
        this.arrLng = arrLng;
    }

    public String getArrSpeed() {
        return arrSpeed;
    }

    public void setArrSpeed(String arrSpeed) {
        this.arrSpeed = arrSpeed;
    }

    public String getArrBuPing() {
        return arrBuPing;
    }

    public void setArrBuPing(String arrBuPing) {
        this.arrBuPing = arrBuPing;
    }

    public GpsPointData() {
    }

    public GpsPointData(Long id) {
        this.id = id;
    }


    public GpsPointData(String mac, String mid, String arrLat, String arrLng, String arrSpeed, String arrBuPing,String arrAltitude,String timeMillis,String bufuList) {
        this.mac = mac;
        this.mid = mid;
        this.arrLat = arrLat;  //纬度
        this.arrLng = arrLng; // 经度
        this.arrSpeed = arrSpeed;
        this.arrBuPing = arrBuPing;
        this.arrAltitude = arrAltitude; // 海拔
        this.timeMillis = timeMillis;
        this.bufuList = bufuList;
    }


    public GpsPointData(Long id,String mac, String mid, String arrLat, String arrLng, String arrSpeed, String arrBuPing,String arrAltitude,String timeMillis,String bufuList) {
        this.id=id;
        this.mac = mac;
        this.mid = mid;
        this.arrLat = arrLat;
        this.arrLng = arrLng;
        this.arrSpeed = arrSpeed;
        this.arrBuPing = arrBuPing;
        this.arrAltitude = arrAltitude;
        this.timeMillis = timeMillis;
        this.bufuList = bufuList;
    }



    @Override
    public String toString() {
        String dbstring="id="+id+"mac="+mac+"mid="+mid+"arrLat="+arrLat+
                "arrLng="+arrLng+"arrSpeed="+arrSpeed+"arrBuPing="+arrBuPing+"arrAltitude="+arrAltitude+"timeMillis="+timeMillis+"bufuList"+bufuList;
        return dbstring;
    }
}
