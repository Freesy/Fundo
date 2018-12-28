package com.szkct.weloopbtsmartdevice.login;

import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**抄原来的XHttpUtils*/
//T_FLAG 2018/1/11 上传的方法写复杂了,有时间再改

/**
 * 原来需要做的上传功能是不需要提示的,所以当时startUploadThread方法的包含了主动上传(传入代参的方法).和自动上传功能,但是两种回调提示都统一在这个类中处理
 * 现在要示主动上传失败提示:所以另外提供一个方法upGpsDetailData,可以传入MyCallBack接口.成功和失败都提示.
 *
 *      !-!如果upGpsDetailData没有执行完,startUploadThread会直接return.在upGpsDetailData执行成功的回调方法再调用,startUploadThread方法.
 *      !-!:自动上传的方法只查询当前时间的前7天的数据,这是与ios暂时这么定的
 */
public class NewNetUtils extends HttpUtils {
    private static final String TAG = NewNetUtils.class.getSimpleName();
    private static NewNetUtils mXHTTPUtils = null;

    private NewNetUtils() {
        super();
    }

    public static NewNetUtils getInstance() {
        if (mXHTTPUtils == null) {
            mXHTTPUtils = new NewNetUtils();
            mXHTTPUtils.configTimeout(30000);
            mXHTTPUtils.configSoTimeout(30000);
            mXHTTPUtils.configCurrentHttpCacheExpiry(0);// 设置无缓存
        }

        return mXHTTPUtils;
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
        return super.send(method, url, params, callBack);
    }

    /**
     * 新协议get请求
     *
     * @param url
     * @param map
     * @return
     */
    public static String newGet(String url, HashMap<String, Object> map) {
        StringBuffer sb = new StringBuffer(url + "?params=");
        StringBuffer ssb = new StringBuffer();
        ssb.append("{");
        for (String key : map.keySet()) {
            if (map.get(key) instanceof String) {
                ssb.append("\"" + key + "\":\"" + map.get(key) + "\"");
            } else if (map.get(key) instanceof Integer) {
                ssb.append("\"" + key + "\":" + map.get(key));
            }
            ssb.append(",");
        }
        ssb.deleteCharAt(ssb.length() - 1);//去除多余的，号
        ssb.append("}");
        sb.append(Base64.encodeToString(ssb.toString().getBytes(), Base64.DEFAULT).trim());
        return sb.toString().replace("\n", "");
    }

    /**
     * 新协议get请求
     *
     * @param url
     * @param dataurl
     * @return
     */
    public static String newGet(String url, String dataurl) {
        StringBuffer sb = new StringBuffer(url + "?params=");
        sb.append(Base64.encodeToString(dataurl.getBytes(), Base64.DEFAULT).trim());
        return sb.toString().replace("\n", "");
    }

    /**
     * ---访另原来的-----------------------end---------------------------------------------
     **/
    public static final String MY_GET = "GET";
    public static final String MY_POST = "POST";

    public <T> HttpHandler<T> sendPost(String url, String json, MyCallback callBack) {
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.POST;

        RequestParams params = new RequestParams();
        HttpEntity bodyEntity = null;
        try {
            bodyEntity = new StringEntity(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Logg.e(TAG, "sendPost: " + e.getMessage());
        }
        params.setBodyEntity(bodyEntity);

        return this.send(method, url, params, callBack);
    }




    public void __sendPost(String method_type, String url, String json, final MyCallback callBack) {
        Logg.i(TAG, "testVolleyPost: json ==" + json);

        JSONObject jsonObjec = null;
        try {
            jsonObjec = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Logg.e(TAG, "testVolleyPost: " + e.getMessage());
        }
        JsonObjectRequest jsonRequst = new JsonObjectRequest(Request.Method.POST, url, jsonObjec, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                callBack.mySuccess(jsonObject.toString());
                Logg.e(TAG, "volley->onResponse: " + jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                DataError error = new DataError();
                error.setData(volleyError.getMessage());
                callBack.myFailure(error);
                Logg.e(TAG, "onErrorResponse: " + volleyError.getMessage());
            }
        });
        Volley.newRequestQueue(BTNotificationApplication.getInstance()).add(jsonRequst);
    }

    public static String getPostJson(HashMap<String, Object> map) {
        StringBuffer ssb = new StringBuffer();
        ssb.append("{");
        for (String key : map.keySet()) {
            if (map.get(key) instanceof String) {
                String gsh_str = (String) map.get(key);
                ssb.append("\"" + key + "\":\"" + gsh_str + "\"");
            } else if (map.get(key) instanceof Integer) {
                ssb.append("\"" + key + "\":" + map.get(key));
            }
            ssb.append(",");
        }
        ssb.deleteCharAt(ssb.length() - 1);//去除多余的，号
        ssb.append("}");
        return ssb.toString().replace("\n", "");
    }

}
