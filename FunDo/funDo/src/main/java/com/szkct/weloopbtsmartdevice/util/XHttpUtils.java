package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.util.Base64;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

public class XHttpUtils extends HttpUtils {
	private static final String TAG = XHttpUtils.class.getSimpleName();
	public static CookieStore cookieStore;
//	private static PreferencesCookieStore preferencesCookieStore = null;	
	private static XHttpUtils mXHTTPUtils = null;
	
	private XHttpUtils() {
		// TODO Auto-generated constructor stub
		super();
		HttpParams params = getHttpClient().getParams();
        params.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        HttpConnectionParams.setSocketBufferSize(params, 1024 * 20);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(30));
        ConnManagerParams.setMaxTotalConnections(params, 50);
		configRequestThreadPoolSize(15);
		ConnManagerParams.setTimeout(params, 5000);
//		configCookieStore(new XPreferencesCookieStore(BTNotificationApplication.mApp)); //BTNotificationApplication.mApp
	}
	
	public static HttpUtils getInstance(){
		if(mXHTTPUtils == null){
			mXHTTPUtils = new XHttpUtils();
			mXHTTPUtils.configCurrentHttpCacheExpiry(0);// 设置无缓存
		} 
	
		return mXHTTPUtils;
	}
	
	public static void destroyInstance(){
//		if(mXHTTPUtils != null){	
//			mXHTTPUtils.getHttpClient().getConnectionManager().shutdown();
//			
//		} 
//		mXHTTPUtils = null;
	}
	
    // ***************************************** send request *******************************************
	@Override
    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url,
                                   RequestCallBack<T> callBack) {
        return send(method, url, null, callBack);
    }

    @Override
    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestParams params,
                                   RequestCallBack<T> callBack) {
    	//增加sessionKey
//        if (params != null && StrUtil.isNotEmpty(AppConfig.user.sessionKey)) {
//        	params.addBodyParameter("sessionKey", AppConfig.user.sessionKey);
//        }
        return super.send(method, url, params, callBack);
    }

	/**
	 * 新协议get请求
	 * @param url
	 * @param map
	 * @return
	 */
    public static String newGet(String url,HashMap<String,Object> map){
		StringBuffer sb=new StringBuffer(url+"?params=");
		StringBuffer ssb=new StringBuffer();
		ssb.append("{");
		for(String key:map.keySet()){
			if(map.get(key) instanceof String){
				String gsh_str= (String) map.get(key);
//				try {//URLEncoder处理中文字段
//					gsh_str=URLEncoder.encode(gsh_str,"UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
				ssb.append("\""+key+"\":\""+gsh_str+"\"");

			}else if(map.get(key) instanceof Integer){
				ssb.append("\""+key+"\":"+map.get(key));
			}
			ssb.append(",");
		}
		ssb.deleteCharAt(ssb.length()-1);//去除多余的，号
		ssb.append("}");
//		sb.append(Base64.encodeToString(ssb.toString().getBytes(),Base64.DEFAULT).trim());
		return sb.toString().replace("\n","");
	}

	/**
	 * 新协议get请求
	 * @param url
	 * @param dataurl
	 * @return
	 */
	public static String newGet(String url,String dataurl){
		StringBuffer sb=new StringBuffer(url+"?params=");
		sb.append(Base64.encodeToString(dataurl.getBytes(),Base64.DEFAULT).trim());
		return sb.toString().replace("\n","");
	}

	/**
	 * 新协议get请求
	 * URLEncoder处理中文字段
	 * @param url
	 * @param map
	 * @return
	 */
	public static String newGetURLEncoder(String url,HashMap<String,Object> map){
		StringBuffer sb=new StringBuffer(url+"?params=");
		StringBuffer ssb=new StringBuffer();
		ssb.append("{");
		for(String key:map.keySet()){
			if(map.get(key) instanceof String){
				String gsh_str= (String) map.get(key);
//				try {
//					gsh_str=URLEncoder.encode(gsh_str,"UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
				ssb.append("\""+key+"\":\""+gsh_str+"\"");
			}else if(map.get(key) instanceof Integer){
				ssb.append("\""+key+"\":"+map.get(key));
			}
			ssb.append(",");
		}
		ssb.deleteCharAt(ssb.length()-1);//去除多余的，号
		ssb.append("}");
		try {//URLEncoder处理中文字段
			sb.append(URLEncoder.encode(Base64.encodeToString(ssb.toString().getBytes(),Base64.DEFAULT).trim().replace("\n",""),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
