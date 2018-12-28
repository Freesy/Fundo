package com.szkct.weloopbtsmartdevice.util;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 网络请求类
 */
public class MyHttpUtils {
	/**
	 * 回调接口
	 * @author Administrator
	 *
	 */
	public interface JsonCallBack{
		public void callback(String jsonStr);
	}
	/**
	 * post提交数据返回结果
	 * @param url
	 * @param params
	 * @param callBack
	 */
	public static void parseShareJsonFromNet(String url,RequestParams params, final JsonCallBack callBack){
		Log.i("接口", url);
		HttpUtils httpUtils = new HttpUtils(10000);
		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i("main", "解析错误");
				Log.i("data", arg0.toString());
				Log.i("data", arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				callBack.callback(arg0.result);
				Log.i("main", "解析成功");
				Log.i("data", arg0.result);
			}
		});
	}

}