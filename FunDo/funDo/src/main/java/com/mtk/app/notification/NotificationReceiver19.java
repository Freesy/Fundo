/**
 *
 */

package com.mtk.app.notification;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.kct.fundo.btnotification.R;
import com.mediatek.ctrl.notification.NotificationData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainActivity;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

/**
 * This class will receive and process all notifications.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationReceiver19 extends NotificationListenerService {
    private NotificationDataManager notificationDataManager = null;
    private Context mContext = BTNotificationApplication.getInstance().getApplicationContext();
    //֪ͨ����Ϣ
    private int messageNotificationID = 1235;
    private NotificationManager messageNotificationManager = null;
    private Notification notification = null;

    @Override
    public void onCreate() {
        notificationDataManager = new NotificationDataManager(BTNotificationApplication.getInstance());
        messageNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setting();
        MessageThread thread = new MessageThread();
        thread.isRunning = true;
        thread.start();
        super.onCreate();

    }

    private void setting(){
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle(getString(R.string.app_name));
        builder.setSmallIcon(R.drawable.speed_logo);
        if(null!=MainService.getInstance()&&MainService.getInstance().getState()==3){
            builder.setContentText(getString(R.string.connected));
            builder.setTicker(getString(R.string.connected));
        }else{
            builder.setContentText(getString(R.string.bluetooth_connecting));
            builder.setTicker(getString(R.string.bluetooth_connecting));
        }
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setWhen(System.currentTimeMillis());
        Intent i = new Intent(BTNotificationApplication.getInstance(),MainActivity.class);
        i.putExtra("Notification","Notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_HIGH);
        notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(messageNotificationID, notification);
    }
    /***
     * �ӷ���˻�ȡ��Ϣ
     * @author zhanglei
     * ��Ϣ�»���֪ͨ�������ⱻɱ��
     *
     */
    class MessageThread extends Thread{
        //����״̬
        public boolean isRunning = true;
        @Override
        public void run() {
            while(isRunning){
                try {
                    //��Ϣ20��
                    Thread.sleep(20000);
                   // if(getServerMessage().equals("have")){
                        setting();
                        messageNotificationManager.notify(messageNotificationID, notification);
                        //���⸲����Ϣ����ȡID����
                      /*  messageNotificationID++;*/
                   // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /***
     * ģ���˷���˵���Ϣ��ʵ��Ӧ����Ӧ��ȥ�������õ�message
     * @return
     */
    public String getServerMessage(){
        return "have";
    }




    //
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(null==sbn.getPackageName()){return;}
        if (android.os.Build.VERSION.SDK_INT < 18) {
            return;
        }
        Notification notification =sbn.getNotification();

        if (null == notification) {
            return;
        }
        if(sbn.getPackageName().contains(".music")){
            return;
        }
        if (!(boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, true)) {
            if (sbn.getPackageName().contains(".incallui")) {
                String dsd = "56546";
                return;
            }
        }
        if (!(boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, true)) {
            if (sbn.getPackageName().contains(".mms") || sbn.getPackageName().contains(".contacts")) {
                return;
            }
        }
        if (sbn.getPackageName().equals("com.miui.securitycenter")||sbn.getPackageName().equals("com.kct.fundo.btnotification")||sbn.getPackageName().equals("com.android.mms")) {
            return;
        }
//        if( TextUtils.isEmpty(notification.tickerText) && (!sbn.getPackageName().contains("linkedin") || !sbn.getPackageName().contains("xiaomi.xmsf"))){return;}   //todo --- 这句话 会让领英进不来
        if(TextUtils.isEmpty(notification.tickerText)){
            if((!sbn.getPackageName().contains("linkedin") && !sbn.getPackageName().contains("xiaomi.xmsf"))){
                return;
            }
        }

        NotificationData notificationData = notificationDataManager.getNotificationData(
                notification, sbn.getPackageName(), sbn.getTag(), sbn.getId());
        notificationDataManager.setSendmsg(true);
        if(null!=notificationData){
            notificationDataManager.sendNotificationData(notificationData,1);
           // Log.e("AppManager/Noti/Manager", "发送1类型的消息-- NotificationReceiver19" );
        }     // todo --- 发送消息 1
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, 1, startId);
    }



    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent i=new Intent(this,NotificationReceiver19.class);
        startService(i);
    }
}
