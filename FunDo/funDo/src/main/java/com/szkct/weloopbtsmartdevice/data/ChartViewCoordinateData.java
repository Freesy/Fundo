package com.szkct.weloopbtsmartdevice.data;

public class ChartViewCoordinateData {

	public int x;
	public int value;
	public String Hour;
	public  int manhata;
	public  int maxhata;
	public  int avghata;
	public float calorie;
	public float distance;

	public int getManhata() {
		return manhata;
	}

	public void setManhata(int manhata) {
		this.manhata = manhata;
	}

	public int getMaxhata() {
		return maxhata;
	}

	public void setMaxhata(int maxhata) {
		this.maxhata = maxhata;
	}

	public int getAvghata() {
		return avghata;
	}

	public void setAvghata(int avghata) {
		this.avghata = avghata;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getValue() {
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
				", manhata=" + manhata +
				", maxhata=" + maxhata +
				", avghata=" + avghata +
				", calorie=" + calorie +
				", distance=" + distance +
				'}';
	}
}
