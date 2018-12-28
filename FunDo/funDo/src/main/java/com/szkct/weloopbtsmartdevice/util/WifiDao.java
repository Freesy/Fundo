package com.szkct.weloopbtsmartdevice.util;

import java.io.Serializable;

public class WifiDao implements Serializable {

	private String SSID;// name
	private String BSSID;// ip
	private Boolean LOCK;// 类型
	

	private int level;// 信号强度
	private boolean linking;// 是否连接
	private int networkId;// 是否连接

	public WifiDao() {
		// TODO Auto-generated constructor stub
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}

	public Boolean getLOCK() {
		return LOCK;
	}

	public void setLOCK(Boolean lOCK) {
		LOCK = lOCK;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isLinking() {
		return linking;
	}

	public void setLinking(boolean linking) {
		this.linking = linking;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	@Override
	public String toString() {
		return "WifiDao [SSID=" + SSID + ", BSSID=" + BSSID + ", level="
				+ level + ", linking=" + linking + ", networkId=" + networkId
				+ "]";

	}

}
