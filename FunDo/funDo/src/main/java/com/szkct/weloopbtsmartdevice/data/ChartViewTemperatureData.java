package com.szkct.weloopbtsmartdevice.data;

public class ChartViewTemperatureData {

	public int x;
	public float value;
	public String Hour;
	public  float avghata;
	public float calorie;
	public float distance;

	public float getAvghata() {
		return avghata;
	}

	public void setAvghata(float avghata) {
		this.avghata = avghata;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public float getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getHour() {
		return Hour;
	}

	public void setHour(String hour) {
		Hour = hour;
	}

	public float getCalorie() {
		return calorie;
	}

	public void setCalorie(float calorie) {
		this.calorie = calorie;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "ChartViewCoordinateData{" +
				"x=" + x +
				", value=" + value +
				", Hour='" + Hour + '\'' +
				", avghata=" + avghata +
				", calorie=" + calorie +
				", distance=" + distance +
				'}';
	}
}
