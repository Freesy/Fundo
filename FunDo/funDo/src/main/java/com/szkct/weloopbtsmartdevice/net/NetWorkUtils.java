package com.szkct.weloopbtsmartdevice.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWorkUtils {
	
	
    /**
     * 检测网络是否可用
     * @param context
     * @return
     */
    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
    private static String result = "";
    public final static String FAILURE = "FAILURE";
    /*
     * Get网络请求
     */
    public static String sendGet(String uri) {
        /* 建立HTTP Get对象 */
    //    final HttpGet hGet = new HttpGet(uri);
        /* 发送请求并等待响应 */
        try {
            /*HttpClient client = new DefaultHttpClient();
            // 请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
            HttpResponse hResponse = client.execute(hGet);
             若状态码为200 ok 
            if (200 == hResponse.getStatusLine().getStatusCode()) {
                 读取服务器返回的数据 
                result = EntityUtils.toString(hResponse.getEntity(), "utf-8");
              
            } else {
                result = FAILURE;

            }*/
        	URL url = new URL(uri);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(30000);
			urlConnection.setReadTimeout(30000);
			urlConnection.connect();
			if (urlConnection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					result += line;
				}
			} else {
                result = FAILURE;

            }
        } catch (IOException e) {
            e.printStackTrace();
            result = FAILURE;
        }

//        catch (ClientProtocolException e) {
//            e.printStackTrace();
//            result = FAILURE;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            result = FAILURE;
//        }

        return result;
    }

    /**
     * 判断当前Wifi是否有效连接
     *
     * @param context
     * @return true 有效连接 false 无效连接
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                Log.i("liuxiao", "isNetworkAvailable: info.isAvailable() ==" + info.isAvailable()
                        + "   networkInfo.isConnected()==" + info.isConnected());
                if (info.isAvailable()) {
                    return true;
                }

            }
        }
        return false;
    }
}
