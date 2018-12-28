package com.szkct.bluetoothtool;

public class AppValueCheckbox {
	public boolean findPhondEnabled = true;
	public boolean lockScreenEnabled = true;
    public boolean lostPhoneEnabled = true;
	public boolean messagePushEnbled = true;
	

	public static AppValueCheckbox checkbox = new AppValueCheckbox();

	private AppValueCheckbox() {

	}

	public static AppValueCheckbox getInance() {
		return checkbox;
	}

	// 得到当前寻找手机功能的能力
	public boolean isFindPhondEnabled() {
		return findPhondEnabled;
	}

	// 设置当前寻找手机功能的能力
	public void setFindPhondEnabled(boolean findPhondEnabled) {
		this.findPhondEnabled = findPhondEnabled;
	}

	//得到当前远程锁屏功能的能力
	public boolean isLockScreenEnabled() {
		return lockScreenEnabled;
	}
	
	//设置当前远程锁屏功能的能力
	public void setLockScreenEnabled(boolean lockScreenEnabled) {
		this.lockScreenEnabled = lockScreenEnabled;
	}
	public boolean isLostPhoneEnabled() {
		return lostPhoneEnabled;
	}

	public void setLostPhoneEnabled(boolean lostPhoneEnabled) {
		this.lostPhoneEnabled = lostPhoneEnabled;
	}
	public boolean isMessagePushEnbled() {
		return messagePushEnbled;
	}

	public void setMessagePushEnbled(boolean messagePushEnbled) {
		this.messagePushEnbled = messagePushEnbled;
	}
}
