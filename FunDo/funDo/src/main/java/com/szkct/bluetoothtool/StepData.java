package com.szkct.bluetoothtool;

import java.io.Serializable;

public class StepData implements Serializable{
	
	private String time;
	private String counts;
	private String calorie;
	private String distance;
	
	public StepData() {
		// TODO Auto-generated constructor stub
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCounts() {
		return counts;
	}

	public void setCounts(String counts) {
		this.counts = counts;
	}

	public String getCalorie() {
		return calorie;
	}

	public void setCalorie(String calorie) {
		this.calorie = calorie;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "StepData [time=" + time + ", counts=" + counts + ", calorie="
				+ calorie + ", distance=" + distance + "]";
	}

	
	
	
}
