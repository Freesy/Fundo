package com.szkct.weloopbtsmartdevice.data;

public class ReportSleepData {

	private boolean isDeepSleep;   // 是否深睡
	private int startTime;
	private long sleepTime;

	private String endTimeStr;  // todo --- 添加 醒来时间 （只取小时，分钟 08:03）

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public boolean isDeepSleep() {
		return isDeepSleep;
	}
	public void setDeepSleep(boolean isDeepSleep) {
		this.isDeepSleep = isDeepSleep;
	}

	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public long getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
}
