package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.net.NetWorkUtils;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpConfig;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.szkct.weloopbtsmartdevice.data.greendao.RunDataFromServer;
//import com.szkct.weloopbtsmartdevice.data.greendao.SleepDataDownLoad;
//import com.szkct.weloopbtsmartdevice.data.greendao.dao.ServerRunDataDao;
//import com.szkct.weloopbtsmartdevice.data.greendao.dao.SleepDataDownLoadDao;

//import com.kct.fundobeta.btnotification.R;

/**
 * 上传数据
 * Created by HRJ on 2017/12/8.
 */

public class NewUploadDataUtil {

    public final static String UPLOADSTEPDATALASTTIME = "uploadStepDatalasttime";//上次上传的数据时间
    public final static String DOWNLOADDATALASTTIME = "DownloadDatalasttime";//上次下载的数据时间
    private static final String TAG = "hrj";
    private static final String START_TIME = "2017-12-20";//临时这样  等移动正式版从安装应用的第一天开始

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private static  DBHelper db = null;

    /**
     * 上传全部数据
     * @param context
     */
    public static void uploadAll(final Context context){
        new Thread(){
            @Override
            public void run() {//在后台上传
                super.run();

            }
        }.start();
    }

    /**
     * 下载用户健康数据
     * @param context
     */
    public static void updatedDownloadAll(final Context context){
        new Thread(){
            @Override
            public void run() {//在后台下载
                super.run();

            }
        }.start();
    }

    static Timer mTimer;
    /**
     * 定时30分钟上传一次数据
     * @param context
     */
    public static void start30MinUploadAll(final Context context){
        if(!NetWorkUtils.isNetworkAvailable(context))//网络不可用就不要进行下去了
            return;
        if(mTimer!=null)
            return;
        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                uploadAll(context);
            }
        },0,30*60*1000);
    }


    static Drawable[] seximg=new Drawable[2];
    public static KJBitmap mKJBitmap;//全局的图片加载   貌似会错位
    public static KJHttp mKJHttp;//网络数据请求
    static {
        HttpConfig config=new HttpConfig();
        config.cacheTime=0;
        mKJHttp=new KJHttp(config);
        mKJBitmap=new KJBitmap();
    }
    /**
     * 显示用户头像
     * @param context
     * @param iv
     */
    public static void showUserImg(Context context, ImageView iv,String imgpath){
//        String photopath = Gdata.getPersonData().getIconPath();
        Drawable mDrawable = null;
        if (SharedPreUtil.readPre(context, SharedPreUtil.USER, SharedPreUtil.SEX).equals("female")) {
            if(seximg[0]==null) {
                TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.im_head_women, R.attr.im_head_men});
                seximg[0] = a.getDrawable(0);
            }
            mDrawable=seximg[0];
        } else{
            if(seximg[1]==null) {
                TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.im_head_women, R.attr.im_head_men});
                seximg[1] = a.getDrawable(1);
            }
            mDrawable=seximg[1];
        }

        if(imgpath==null || imgpath.trim().length()==0)
            iv.setImageDrawable(mDrawable);
         else
            mKJBitmap.display(iv, imgpath);
    }

    /**
     * 上传用户头像到服务器
     */
   /* public static void uploadUserImg(final File file, final HttpCallBack mHttpCallBack){
        HttpParams params=new HttpParams();
        params.putHeaders("mid",Gdata.getPersonData().getMid());
        params.put("pic",file);
        mKJHttp.post(ServerConfig.UPLOAD_PHOTO, params, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e("hrj", "头像onSuccess: "+t);
                try {
                    JSONObject jsonObj = new JSONObject(t);
                    if(jsonObj.getInt("code")==0) {//上传成功 保存新头像路径
//                        Gdata.getPersonData().setIconPath(jsonObj.getString("face"));  //服务器有缓存
                        Gdata.getPersonData().setIconPath(file.getAbsolutePath());
                        Gdata.savePersonToFile();
                        if(mHttpCallBack!=null)
                            mHttpCallBack.onSuccess(t);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Log.e(TAG, "头像onFailure: "+strMsg);
            }
        });
    }*/


    // 整理日期数据
    private static String arrangeDate(String dateStr) {
        String[] dates = dateStr.split("-");
        String year = dates[0];
        String month = dates[1];
        String day = dates[2];
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        return year + "-" + month + "-" + day;
    }

    static long clicktime;
    /**
     * 防止无聊多次快速点击
     * @return
     */
    public static boolean isClickQuickly(){
        boolean isok=true;
        if(System.currentTimeMillis()-clicktime>=1000) {
            isok=true;
            clicktime=System.currentTimeMillis();
        }else{
            isok=false;
        }
        return isok;
    }

    /**
     * 同步post
     * @param uri
     * @param charset
     * @return
     */
    public static String sendPost(String uri, String charset) {
        try {
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),charset);
            Request request = new Request.Builder().url(uri)
                    .post(requestBody).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String str=response.body().string();
            response.close();
            return  str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步get
     * @param uri
     * @return
     */
    public static String sendGet(String uri) {
        try {
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(uri)
                    .get().build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String str=response.body().string();
            response.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
