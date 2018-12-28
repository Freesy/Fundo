package com.szkct.weloopbtsmartdevice.util;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.main.WelcomeActivity;



/**JobService，支持5.0以上forcestop依然有效
 */
@TargetApi(21)
public class AliveJobService extends JobService {
    private final static String TAG = "KeepAppAlive";   // KeepAppAlive    KeepAliveService
    // 告知编译器，这个变量不能被优化
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive(){
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            if(SystemUtils.isAPPALive(getApplicationContext(), "com.kct.fundo.btnotification")){   //  Contants.PACKAGE_NAME
//                Toast.makeText(getApplicationContext(), "APP运行着", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"KeepAliveService----->APP运行着...");

                //todo  ---- 添加 MainService 是否运行的逻辑
                ////////////////////////////////////////////////////////////////
                if (!MainService.isMainServiceActive() || !Utils.isServiceWork(BTNotificationApplication.getInstance(), MainService.class.getName())) {
                    Log.i(TAG, "start MainService!");
                    getApplicationContext().startService(new Intent(BTNotificationApplication.getInstance(), MainService.class));
                }
//                if(false== Utils.isServiceWork(BTNotificationApplication.getInstance(), MainService.class.getName())){
//                    getApplicationContext().startService(new Intent(getApplicationContext(), MainService.class));
//                }
                ///////////////////////////////////////////////////////////////////////

            }else{
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);    // todo ----- 重启APP
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "APP被杀死，重启...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "KeepAliveService----->APP被杀死，重启...");
            }
            // 通知系统任务执行结束
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {   // todo ---- add 20171204
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
//        if(Contants.DEBUG)
            Log.d(TAG,"KeepAliveService----->JobService服务被启动...");
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
//        if(Contants.DEBUG)
        Log.d(TAG, "KeepAliveService----->JobService服务被关闭");
        return false;
    }
}
