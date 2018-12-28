package com.szkct.weloopbtsmartdevice.trajectory;

import java.io.Serializable;

public class GpsPoint implements Serializable{
	private static final long serialVersionUID = 4598910917081132372L;
	public String lat;//经度
	public String lon;//纬度
	public String mile;//距离
	public String ele;//海拔
	public String date;//时间
	public String speed;//速度
	public GpsPoint(){}
	public GpsPoint(String lat, String lon, String mile, String ele,String date,String speed) {
		super();
		this.lat = lat;//3
		this.lon = lon;//4
		this.mile = mile;//6
		this.ele = ele;//5
		this.date = date;//7
		this.speed = speed;//8
	}
	
}
