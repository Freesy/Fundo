package com.szkct.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Ronny on 2018/5/19.
 */

public class JPushReceiver extends BroadcastReceiver {

    int i = 100;

    public static final String TAG = "JPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action == null)return;
        //国外不支持推送
        if(!Utils.getLanguage().equals("zh")){
            return;
        } if(action.equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {//todo 通知消息

        }else if(action.equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {//todo 自定义消息
            Bundle bundle = intent.getExtras();
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Log.e(TAG, "msg="+message + "----json="+json + "------alert="+alert);
            Intent in = new Intent(JPushInterface.ACTION_NOTIFICATION_OPENED);
            in.addCategory(context.getPackageName());
            Bundle b = new Bundle();
            b.putString(JPushInterface.EXTRA_ALERT, message);
            b.putString(JPushInterface.EXTRA_EXTRA, json);
            in.putExtras(b);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            Notification build = builder.setAutoCancel(true)
                    .setContentTitle(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(++i, build);

        }else if(action.equals(JPushInterface.ACTION_NOTIFICATION_OPENED)){//todo 点击通知
            //获取通知数据
            Bundle bundle = intent.getExtras();
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Log.e(TAG, "msg="+message + "----json="+json + "------alert="+alert);
            //todo 先解析扩展消息
            String url = null;
            try {
                JSONObject jo = new JSONObject(json);
                if(jo.has("activity_url")){
                    url = jo.getString("activity_url");
//                    toUrl(context, activity_url);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(url == null){//扩展中没有就用消息的
                url = getUrl(alert);
                Log.e(TAG, url);
            }

            toUrl(context, url);

        }
    }

    private String getUrl(String s) {
        String url = "https://mp.weixin.qq.com/s/KgcZYWvDIYa6N0rA-NsJQQ";
        if(TextUtils.isEmpty(s))return url;
        try {
            if(s.contains("#")){
                url = s.substring(s.indexOf("#") + 1, s.lastIndexOf("#"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }


    private void toUrl(Context context, String activity_url) {
        String url = "https://mp.weixin.qq.com/s/KgcZYWvDIYa6N0rA-NsJQQ";
        if(!TextUtils.isEmpty(activity_url)){
            url = activity_url;
            if(!url.startsWith("http")){
                url = "http://"+url;
            }
        }
        Log.e(TAG, "--------url = "+url);
        try {
            Uri uri = Uri.parse(url);
            Intent in = new Intent(Intent.ACTION_VIEW, uri);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }catch (Exception e){

        }
    }
}
