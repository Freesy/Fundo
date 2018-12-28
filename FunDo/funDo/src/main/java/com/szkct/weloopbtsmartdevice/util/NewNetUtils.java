/*
package com.szkct.weloopbtsmartdevice.util;

import android.os.Looper;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointData;
import com.szkct.weloopbtsmartdevice.data.greendao.GpsPointDetailData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.GpsPointDetailDao;
import com.szkct.weloopbtsmartdevice.data.netdata.ChangeTypeUtil;
import com.szkct.weloopbtsmartdevice.data.netdata.SyncSportData;
import com.szkct.weloopbtsmartdevice.dialog.DataError;
import com.szkct.weloopbtsmartdevice.dialog.Gdata;
import com.szkct.weloopbtsmartdevice.dialog.Logg;
import com.szkct.weloopbtsmartdevice.dialog.ToastUtil;
import com.szkct.weloopbtsmartdevice.dialog.TxHttpUtil;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

*/
/**抄原来的XHttpUtils*//*

//T_FLAG 2018/1/11 上传的方法写复杂了,有时间再改

*/
/**
 * 原来需要做的上传功能是不需要提示的,所以当时startUploadThread方法的包含了主动上传(传入代参的方法).和自动上传功能,但是两种回调提示都统一在这个类中处理
 * 现在要示主动上传失败提示:所以另外提供一个方法upGpsDetailData,可以传入MyCallBack接口.成功和失败都提示.
 *
 *      !-!如果upGpsDetailData没有执行完,startUploadThread会直接return.在upGpsDetailData执行成功的回调方法再调用,startUploadThread方法.
 *      !-!:自动上传的方法只查询当前时间的前7天的数据,这是与ios暂时这么定的
 *//*

public class NewNetUtils extends HttpUtils {
    private static final String TAG = NewNetUtils.class.getSimpleName();
    private static NewNetUtils mXHTTPUtils = null;

    private NewNetUtils() {
        super();
    }

    public static NewNetUtils getInstance() {
        if (mXHTTPUtils == null) {
            mXHTTPUtils = new NewNetUtils();
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

    */
/**
     * 新协议get请求
     *
     * @param url
     * @param map
     * @return
     *//*

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

    */
/**
     * 新协议get请求
     *
     * @param url
     * @param dataurl
     * @return
     *//*

    public static String newGet(String url, String dataurl) {
        StringBuffer sb = new StringBuffer(url + "?params=");
        sb.append(Base64.encodeToString(dataurl.getBytes(), Base64.DEFAULT).trim());
        return sb.toString().replace("\n", "");
    }

    */
/**
     * ---访另原来的-----------------------end---------------------------------------------
     **//*


    public static final String MY_GET = "GET";
    public static final String MY_POST = "POST";

    public void sendGet(String method_type, String url, MyCallback callBack) {
        Logg.i(TAG, "loadNetData: url==" + url);
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.GET;
        if (method_type.equals(MY_GET)) {
            method = HttpRequest.HttpMethod.GET;
        } else if (method_type.equals(MY_POST)) {
            method = HttpRequest.HttpMethod.POST;
        }
        send(method, url, callBack);
    }


    public <T> HttpHandler<T> sendPost(String method_type, String url, String json, MyCallback callBack) {
        HttpRequest.HttpMethod method = HttpRequest.HttpMethod.GET;
        if (method_type.equals(MY_GET)) {
            method = HttpRequest.HttpMethod.GET;
        } else if (method_type.equals(MY_POST)) {
            method = HttpRequest.HttpMethod.POST;
        }

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


    */
/**
     * 上传过程标识,防止多个线程同时进入
     *//*

    private final int isUploading = 0;
    private final int isUploadSeccess = 1;
    private final int isUploadError = 2;
    private int uploadState = isUploadError;
    */
/**
     * 当前上传的数据,每次只能有一条
     *//*

    private GpsPointDetailData curData;
    */
/**
     * 上传失败次数
     *//*

    private int errorCount = 0;

    */
/**
     * 一条一条的上传
     *//*

    private synchronized void uploadDetailData(final GpsPointDetailData gpsDeatils) {
        if (gpsDeatils == null || uploadState == isUploading) {
            Logg.e(TAG, "uploadDetailData: 传入的参数为null");
            return;
        }
        uploadState = isUploading;

        MyCallback callback = new MyCallback() {
            @Override
            public void mySuccess(Object responseInfo) {
                Logg.e(TAG, "本地数据上传成功= "+gpsDeatils.getTimeMillis());
                */
/**1更新数据库*//*

                updateDBData(gpsDeatils);

                uploadState = isUploadSeccess;
                errorCount = 0;

                */
/**2.从新查询看是否需要再次下载*//*

                startUploadThread(null);
            }

            @Override
            public void myFailure(DataError error) {
                Logg.e(TAG, "本地数据上传失败=" + error.toString() + gpsDeatils.getTimeMillis());
                */
/**数据库返回为3的情况是指数据已经上传了,服务端是通过sport_times字段来判断的,如出现同一时间的数据会走这里*//*

                if(error.getMsgCode() == 3){
                    Logg.e(TAG, "myFailure: 数据已经上传="+gpsDeatils.getTimeMillis());
                    updateDBData(gpsDeatils);
                }
                
                uploadState = isUploadError;
                errorCount++;
                */
/**失败的情况尝试3次*//*

                if (errorCount <= 3) {
                    startUploadThread(null);
                }
            }
        };
        uploadGpsPointDetailData(gpsDeatils, callback);
    }

    private void uploadGpsPointDetailData(GpsPointDetailData gpsDeatils, MyCallback callback) {
        gpsDeatils.setMid(Gdata.getMid()+"");
        SyncSportData data = ChangeTypeUtil.fromGpsPointDetailData(gpsDeatils);
        String json = TxHttpUtil.getPostJson(data);
        Logg.w(TAG, "uploadDetailData: json=" + json);
        sendPost(NewNetUtils.MY_POST, ServerConfig.SYNC_SPORT_DATA, json, callback);
    }

    */
/**主线程调这方法的次数*//*

    private int mainUploadCount = 0;
    */
/**如当自动上传的方法上传数据完成,主线程也调用这个方法上传.这时自动上传可能会读到主线程在上传的字段,就有可能上传两条数据,所以用个字段标识*//*

    */
/**把所有的状态存用一个int属性标识,因为调用这个方法与回调都在主线程,所以不存在同步的问题.唯一的问题是,如果两个方法都没有回调,那么自动下载判断就会出错,这是框架的问题,暂时没解*//*

    */
/**如果要弹窗提示,把MyCallback移到具体类里面*//*

    public void upGpsDetailData(final GpsPointDetailData gpsDeatils){
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            */
/**不是主线程调用直接退出*//*

            return;
        }
        mainUploadCount++;

        MyCallback callback = new MyCallback() {
            @Override
            public void mySuccess(Object responseInfo) {
                updateDBData(gpsDeatils);
                mainUploadCount--;
                */
/**2.从新查询看是否需要再次下载*//*

                startUploadThread(null);
            }

            @Override
            public void myFailure(DataError error) {
                Logg.e(TAG, "本地数据上传失败=" + error.toString() + gpsDeatils.getTimeMillis());
                */
/**数据库返回为3的情况是指数据已经上传了,服务端是通过sport_times字段来判断的,如出现同一时间的数据会走这里*//*

                if(error.getMsgCode() == 3){
                    Logg.e(TAG, "myFailure: 数据已经上传="+gpsDeatils.getTimeMillis());
                    updateDBData(gpsDeatils);
                }
                mainUploadCount--;
                ToastUtil.mayShow("数据上传失败请重试");
            }
        };
        uploadGpsPointDetailData(gpsDeatils,callback);
    }

    */
/**
     * 为空的情况自动查询数据库去上传,需要提示的调用upGpsDetailData;
     *//*

    public void startUploadThread(final GpsPointDetailData gpsDeatils) {
        Logg.i(TAG, "startUploadThread: uploadState ="+uploadState );
        if (uploadState == isUploading) return;
        if(!NetWorkUtils.isConnect()){
            Logg.e(TAG, "startUploadThread: 网络没有连接");
            return;
        }
        */
/**主线程正在上传数据*//*

        if(mainUploadCount > 0){
            Logg.e(TAG, "startUploadThread: 主线程正在上传数据");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (gpsDeatils == null) {
                    List list = queryNotUpload();
                    if (list != null && list.size() != 0) {
                        Logg.i(TAG, "run: 开始上传数据= "+((GpsPointDetailData) list.get(0)).getTimeMillis());
                        uploadDetailData((GpsPointDetailData) list.get(0));
                    }else{
                        Logg.e(TAG, "run: 本地数据库没有数据="+Gdata.getMid());
                    }
                } else {
                    uploadDetailData(gpsDeatils);
                }
            }
        }).start();
    }

    private void updateDBData(GpsPointDetailData data) {
        data.setIsUploadState(GpsPointDetailData.YES_UPLOAD);
        DBHelper.getInstance(BTNotificationApplication.getInstance()).getGpsPointDetailDao().update(data);
    }

    */
/**
     * 每次只拿一条,而且只拿当前时间的前7天的数据
     *//*

    private static List queryNotUpload() {

        Calendar calendar = Calendar.getInstance();
        long dateFir ;
        long dateEnd ;
        dateFir = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        dateEnd = calendar.getTimeInMillis();

        QueryBuilder queryBuilder = DBHelper.getInstance(BTNotificationApplication.getInstance()).getGpsPointDetailDao() .queryBuilder();

        queryBuilder.where(
                GpsPointDetailDao.Properties.Mid.eq(Gdata.getMid())
                ,GpsPointDetailDao.Properties.TimeMillis.between(dateEnd/ 1000, dateFir / 1000) //数据库没存毫秒,时间必须小的在前面,大的在后面
                ,queryBuilder.or(
                        GpsPointDetailDao.Properties.isUploadState.isNull()
                        , GpsPointDetailDao.Properties.isUploadState.eq(GpsPointDetailData.NO_UPLOAD)));
        queryBuilder.orderAsc(GpsPointDetailDao.Properties.TimeMillis);
        queryBuilder.limit(1);

        return queryBuilder.build().list();
    }

    public void __uploadDetailData(final GpsPointDetailData gpsDeatils, ArrayList<GpsPointData> gpsPointDataList) {

        SyncSportData data = new SyncSportData();
        data.setMid(472);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            sb.append(100.23);
            sb.append(":");
            sb.append(300.52);
            sb.append("-");
        }
        data.setSporttrail_details(sb.toString());


        String json = TxHttpUtil.getPostJson(data);
        MyCallback callback = new MyCallback() {
            @Override
            public void mySuccess(Object responseInfo) {
                ToastUtil.tShow("上传成功");
                Logg.e(TAG, "testPost: json=" + "成功");
//				Gdata.removeNoUploadDataId(gpsDeatils.getId());
            }

            @Override
            public void myFailure(DataError error) {
                ToastUtil.tShow("上传失败");
                Logg.e(TAG, "testPost: json=" + "失败");
//				Gdata.addNoUploadDataId(gpsDeatils.getId());
            }
        };
        sendPost(NewNetUtils.MY_POST, ServerConfig.SYNC_SPORT_DATA, json, callback);
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


    private void __testVollyRequestData(String url) {
        Request stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                ToastUtil.tShow("发送成功=" + s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.tShow("发送失败=" + volleyError.getMessage());
            }
        });
        Volley.newRequestQueue(BTNotificationApplication.getInstance()).add(stringRequest);
    }


}
*/
