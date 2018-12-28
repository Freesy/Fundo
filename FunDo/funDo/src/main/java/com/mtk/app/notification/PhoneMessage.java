package com.mtk.app.notification;

/**
 * 接收手机推送过来的内容
 * Created by kct on 2016/3/30.
 */
public class PhoneMessage {
    private String packageName;
    private String tickerText;
    private String id;
    private String time;
    private String appName;


    public PhoneMessage(String packageName, String tickerText, String id, String time, String appName) {
        this.packageName = packageName;
        this.tickerText = tickerText;
        this.id = id;
        this.time = time;
        this.appName = appName;
    }


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public String getTickerText() {
		return tickerText;
	}


	public void setTickerText(String tickerText) {
		this.tickerText = tickerText;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getAppName() {
		return appName;
	}


	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
    
}
