package com.szkct.weloopbtsmartdevice.util;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.kct.fundo.btnotification.R;

import java.io.File;

import lecho.lib.hellocharts.model.UpdateBean;

/**
 * Created by kct on 2018/7/10.
 */
public class DownloadService extends Service {

    /**
     * mobileSystem : 1
     * appVersion : 1.3.2
     * appMarketForeignUrl : https://play.google.com/store/apps/details?id=com.kct.fundo.btnotification&rdid=com.kct.fundo.btnotification
     * appName : 0
     * description : 分动安卓端版本
     * appMarketUrl : http://imtt.dd.qq.com/16891/6551F8D1828AFAD7C2DE3020B70F8D5D.apk?fsname=com.kct.fundo.btnotification_V1.3.2_1277.apk&amp;csr=1bbd
     * isUpgrade : true
     * status : 2
     */

    private DownloadManager mDownloadManager;
    private long enqueue;
    private BroadcastReceiver receiver;
    private static  String APK_URL = "";
//    private  static final String APK_URL= "http://imtt.dd.qq.com/16891/4DD57D3D212EE23D7069F3D93610D40F.apk?fsname=com.kct.fundo.btnotification_V1.3.3_1278.apk&amp;csr=1bbd";
    //    private  static final String APK_NAME="youni.apk";
//    private  static final String APK_NAME="youni_"+ System.currentTimeMillis()+"_.apk";  //
//    private  static final String APK_NAME = "com.kct.fundo.btnotification_V1.3.3_1278.apk";
    private  static  String APK_NAME = "com.kct.fundo.btnotification_V1.3.2_1277.apk";

    private UpdateBean bean;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        DebugLog.v("onBind");
        return null;
    }
    @Override
    public void onCreate() {
//        DebugLog.v("onCreate");
        super.onCreate();

//        if(getIntent().hasExtra(UPDATE_BEAN)){
//            bean = getIntent().getParcelableExtra(UPDATE_BEAN);
//            if(bean.getDescription() != null){
//                tvDes.setText(bean.getDescription());
//            }
//        }

        /*删除以前下载的安装包*/
        RecursionDeleteFile(new File(Environment.getExternalStorageDirectory() + "/download/fundoapk/"));
    }

    @Override
    public void onStart(Intent intent, int startId) {
//        DebugLog.v("onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        bean = intent.getParcelableExtra("update_bean");
        APK_URL = bean.getAppMarketUrl();    // //    private  static final String APK_URL= "http://imtt.dd.qq.com/16891/4DD57D3D212EE23D7069F3D93610D40F.apk?fsname= com.kct.fundo.btnotification_V1.3.3_1278.apk&amp;csr =1bbd";
        String[] denghao = APK_URL.split("=");
        String[] yuhao = denghao[1].split("&");
        APK_NAME = yuhao[0];

//        DebugLog.v("onStartCommand");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/fundoapk/"+APK_NAME)),
                        "application/vnd.android.package-archive");
                startActivity(intent);
                stopSelf();
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownload();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
//        DebugLog.v("onDestroy");
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void startDownload() {
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(APK_URL));
        request.setDescription(getString(R.string.notification_tip_forupdate));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+"/fundoapk", APK_NAME);
        enqueue = mDownloadManager.enqueue(request);
    }
    /**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
    public void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
