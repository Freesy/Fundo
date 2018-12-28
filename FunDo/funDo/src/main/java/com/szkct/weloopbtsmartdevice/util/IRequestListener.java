package com.szkct.weloopbtsmartdevice.util;

public interface IRequestListener {
	void onPrepare();
	void onComplete();
	void onSuccess(String content);
	void onError(String error);
	void onException(Exception e);

}
