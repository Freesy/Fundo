/**
 * 
 */
package com.szkct;

import com.amap.api.maps.model.LatLng;

/**
 * @author wuzhiyi
 * @date 2016-4-20 10:29:19
 */
public class GPSCorrect {
	// WGS-84 国际标准,GPS坐标(Google Earth,GPS使用)
	// GCJ-02 中国坐标偏移标准(Google Map,高德，腾讯使用)
	// BD-09 百度坐标偏移标准(Baidu Map 使用)
	private static double pi = 3.1415926535897932384626;
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;
	private static double x_pi = pi * 3000.0 / 180.0;

	
	public static final LatLng wgs2bg(LatLng lp){
		LatLng locationPoint = wgs2gcj(lp);
		return gcj2bg(locationPoint);
	}

	public static final LatLng wgs2gcj(LatLng lp){
		return wgs2gcj(lp, true);
	}

	public static final LatLng wgs2gcj(LatLng lp,boolean isGPSLocation) {    //标准到高德
		if(!isGPSLocation){
			return lp;
		}

		double dLat = transformLat(lp.longitude - 105.0, lp.latitude - 35.0);
		double dLon = transformLon(lp.longitude - 105.0, lp.latitude - 35.0);
		double radLat = lp.latitude / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lp.latitude + dLat;
		double mgLon = lp.longitude + dLon;

		return new LatLng(mgLat, mgLon);
	}
	
	public static final LatLng gcj2bg(LatLng lp) {
		double x = lp.longitude, y = lp.latitude;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new LatLng(bd_lat, bd_lon);
	}

	public static final LatLng bg2wgs(LatLng lp){
		LatLng locationPoint = bg2gcj(lp);
		return gcj2wgs(locationPoint);
	}
	
	public static final LatLng bg2gcj(LatLng lp) {
		double x = lp.longitude - 0.0065, y = lp.latitude - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double gg_lon = z * Math.cos(theta);
		double gg_lat = z * Math.sin(theta);
		return new LatLng(gg_lat, gg_lon);
	}

	public static final LatLng gcj2wgs(LatLng lp){
		LatLng gps = transform(lp.latitude, lp.longitude);
        double lontitude = lp.longitude * 2 - gps.longitude;
        double latitude = lp.latitude * 2 - gps.latitude;
        return new LatLng(latitude, lontitude);
	}
	public static LatLng transform(double lat, double lon) {

        double dLat = transformLat(lon - 105.0, lat - 35.0);  
        double dLon = transformLon(lon - 105.0, lat - 35.0);  
        double radLat = lat / 180.0 * pi;  
        double magic = Math.sin(radLat);  
        magic = 1 - ee * magic * magic;  
        double sqrtMagic = Math.sqrt(magic);  
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);  
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);  
        double mgLat = lat + dLat;  
        double mgLon = lon + dLon;  
        return new LatLng(mgLat, mgLon);
    }  
	private static final double transformLat(double x, double y) {
		double transLat = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x
				* y + 0.2 * Math.sqrt(Math.abs(x));
		transLat += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x
				* pi)) * 2.0 / 3.0;
		transLat += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		transLat += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi
				/ 30.0)) * 2.0 / 3.0;

		return transLat;
	}

	private static final double transformLon(double x, double y) {
		double transLon = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
				* Math.sqrt(Math.abs(x));
		transLon += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x
				* pi)) * 2.0 / 3.0;
		transLon += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		transLon += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x
				/ 30.0 * pi)) * 2.0 / 3.0;

		return transLon;
	}

}
