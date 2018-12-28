package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


public class HttpUtils {
	private static final String TAG="HttpUtils";
	private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟  
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟 
	private static CookieStore cookieStore1;
	private static final int TRY_TIMES = 1;
//	static HttpContext localContext = new BasicHttpContext();
	
	private static void login(){
		/*String phone = MyApplication.getInstance().getLoginName();
		String password = MyApplication.getInstance().getLoginPassword();

		List<NameValuePair> param = ParamsBuilder.create()
				.addParam("phone", phone)
				.addParam("pwd", password);
		String content = getContent(MyApplication.getInstance(), MyApplication.getInstance().getUrl("Passenger/login"), param);
		Map<String, Object> result = new JsonParser<Map>().parser(content);
		if(result.get("token")!=null){
			MyApplication.getInstance().setToken(result.get("token").toString());
		}else{
			MyLog.e(TAG, "token is null");
		}*/
	}
	
	public static DefaultHttpClient  getHttpClient(){  
	    BasicHttpParams httpParams = new BasicHttpParams();  
	    HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);  
	    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);  
	    DefaultHttpClient  client = new DefaultHttpClient(httpParams);  
	    if(cookieStore1!=null){
	    	client.setCookieStore(cookieStore1);
	    }
	    return client;  
	}  
	
	
	public static void getContentAsync(Context context,String url,IRequestListener listener) {
		getContentAsync(context, url, null, listener);
	}

	public static void getContentAsync(final Context context,final String url,final List<NameValuePair> params,final IRequestListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(listener!=null){
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							
							@Override
							public void run() {
								listener.onPrepare();
							}
						});
						
					}
					final String content = getContent(context,url,params);
//					MyLog.i(TAG, "url: " + url);
					if(listener!=null){
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							
							@Override
							public void run() {
								listener.onComplete();
								if (content.contains("error")) {
									listener.onError(content);
								}else {
									listener.onSuccess(content);
								}
							}
						});
					}
					
				} catch (final Exception e) {
					e.printStackTrace();
					if(listener!=null){
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							
							@Override
							public void run() {
								listener.onComplete();
								listener.onException(e);
							}
						});
					}
					
				}
			}
		}).start();
	}
	
	public static String getContent(Context mContext,String url, List<NameValuePair> params)  {
		if(params==null){
			params = new ArrayList<NameValuePair>();
		}
//		params.add(new BasicNameValuePair("token", BTNotificationApplication.getInstance().getToken()));
		String url1 = url+"?";
		if(params!=null){
			for (NameValuePair nameValuePair : params) {
				url1+=nameValuePair.getName()+"="+nameValuePair.getValue()+"&";
			}
		}  // http://www.fundo.cc/api/user_face_upload.php?mid=1063024&

//		MyLog.i(TAG, url1);
		String result = null;
		int times = 0;
		while(times<TRY_TIMES){
			try {
				HttpEntityEnclosingRequestBase httpRequest =new HttpPost(url);
				if(params!=null){
					
					httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
				}
				
				DefaultHttpClient  httpclient = getHttpClient();
				HttpResponse response = httpclient.execute(httpRequest);
				if (response.getStatusLine().getStatusCode() == 200) {
					result = EntityUtils.toString(response.getEntity(),"UTF-8");
//					MyLog.d(TAG, result);
//					Map<String, Object> map = new JsonParser<Map>().parser(result);
//					MyLog.i(TAG, "result map: " + map);
//					{"ret":0,"apply_number":"A2014122000046"}
					
					// map解析出来的有问题，两个完全一样的result，解析出来的结果不同
					//重新登录
//					if(map.get("ret")!=null && map.get("ret").toString().equals("-97")){
					/*if(result.contains("\"ret\":-97")) {
						login();
						//移除token
						NameValuePair tokenPair = null;
						for (NameValuePair nameValuePair : params) {
							if("token".equals(nameValuePair.getName())){
								tokenPair = nameValuePair;
								break;
							}
						}
						if(tokenPair!=null) params.remove(tokenPair);
//						params.add(new BasicNameValuePair("token",MyApplication.getInstance().getToken()));
						continue;
					}*/
					
					if(url.contains("login")){
						cookieStore1 = ((AbstractHttpClient) httpclient).getCookieStore();  
					}
					
					break;
				}else{
					result = "{\"ret\":\"1\",\"error\":\""+response.getStatusLine().getStatusCode()+"\"}";
				}
				httpclient.getConnectionManager().shutdown();
//				MyLog.i(TAG, result);
			} catch (Exception e) {
				e.printStackTrace();
				result = "{\"ret\":\"1\",\"error\":\"net exception\"}";
			}
			times++;
//			MyLog.i(TAG, "try againt");
		}
		return result;
	}

}
