package com.szkct.map.common;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;

public class GpsSatelliteListener implements Listener {
	public void onGpsStatusChanged(int event) {
		switch (event) {
		// 第一次定位
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			break;
		// 卫星状态改变
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			break;
		// 定位启动
		case GpsStatus.GPS_EVENT_STARTED: 
			break;
		// 定位结束
		case GpsStatus.GPS_EVENT_STOPPED:
			break;
		}
	}


}
