package com.szkct.weloopbtsmartdevice.trajectory;

import java.io.Serializable;

public class LatLngBean implements Serializable {
	private static final long serialVersionUID = 7645726287184179460L;
	public double lat;
	public double lon;
	public LatLngBean(){}
	public LatLngBean(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
}
