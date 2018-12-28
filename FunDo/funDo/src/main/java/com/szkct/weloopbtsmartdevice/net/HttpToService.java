package com.szkct.weloopbtsmartdevice.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.AboutActivity;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.util.Constants;


public class HttpToService extends Thread {
    private Context context;
    public String jsonArrayData="";
    private int verCode;
    private int newVerCode = 0;
    private boolean flag = false;
    public static int UPDATE = 1;
    public static int NO_UPDATE = 2;
    public static String updateUrl = "";

    public HttpToService(Context context) {

        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        try {
/*            @SuppressWarnings("deprecation")
			HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(Constants.APP_UPDATE);
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
            HttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                jsonArrayData = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                Log.e("TAg", "请求位置5。。。。。。。" + jsonArrayData);
            }*/
        	URL url = new URL(Constants.APP_UPDATE);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(3000);
			urlConnection.setReadTimeout(3000);
			urlConnection.connect();
			if (urlConnection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line;
				jsonArrayData="";
				while ((line = reader.readLine()) != null) {  // {"versionCode":"123","versionName":"1.1.7","file":"http:\/\/imtt.dd.qq.com\/16891\/97BDD85FF66FDD9E47A9ECF21904BD15.apk?fsname=com.kct.fundo.btnotification_V1.1.7_123.apk&amp;csr=1bbd","file_size":"30064","context":"\u6682\u65e0\u63cf\u8ff0"}
					jsonArrayData += line;
				}
			}
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;   // 102
            if (jsonArrayData.equals("")) {
            } else {
                Log.e("TAg", ".........");
                JSONObject ja = new JSONObject(jsonArrayData);
                String version = ja.getString("versionCode");  // 123          // http://imtt.dd.qq.com/16891/97BDD85FF66FDD9E47A9ECF21904BD15.apk?fsname=com.kct.fundo.btnotification_V1.1.7_123.apk&csr=1bbd
                updateUrl = ja.getString("file");                              // http://imtt.dd.qq.com/16891/97BDD85FF66FDD9E47A9ECF21904BD15.apk?fsname=com.kct.fundo.btnotification_V1.1.7_123.apk&amp;csr=1bbd
                Log.e("tag", updateUrl);
                newVerCode = Integer.parseInt(version);  // 123
            }
            if (verCode < newVerCode) {
                flag = true;
            } else {
                flag = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (flag) {
                if (context instanceof MainActivity) {
                    //MainActivity销毁后会报错
                    try{
                        Thread.sleep(3000);
                        ((MainActivity) context).myHandler.sendEmptyMessage(UPDATE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (context instanceof AboutActivity) {
                    //MainActivity销毁后会报错给MainActivity1秒时间关闭
                    try{
                        Thread.sleep(3000);
                        ((AboutActivity) context).myHandler.sendEmptyMessage(UPDATE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }else {
                if (context instanceof AboutActivity) {
                    try{
                        Thread.sleep(3000);
                        ((AboutActivity) context).myHandler.sendEmptyMessage(NO_UPDATE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}
