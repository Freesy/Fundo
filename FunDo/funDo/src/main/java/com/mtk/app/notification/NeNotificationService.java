package com.mtk.app.notification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.notification.NotificationData;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * wyl 2017/8/3
 *
 * 辅助服务监听消息通知
 */
public class NeNotificationService extends AccessibilityService {
	private static final String TAG = "liuxiaoService";
	private static String qqpimsecure = "com.tencent.qqpimsecure";
	private NotificationDataManager notificationDataManager = null;
	private Timer timerNeNoti = null;//
	//private MyTask mTimerTask = null;

	public NeNotificationService() {
		notificationDataManager = new NotificationDataManager(this);
	}

	private Context mContext = BTNotificationApplication.getInstance().getApplicationContext();
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if(null==event||null==event.getPackageName()){
		return;
		}

		if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)
		{
			if(event.getPackageName().equals(qqpimsecure))
			{
			}else{
				try{

					Parcelable data = event.getParcelableData();

					if (data instanceof Notification) {
						Notification notification = (Notification) data;
//						Log.e("AppManager/Noti/Manager", "收到的消息-- " + event.getPackageName());

						if (null == notification) {
							return;
						}

//						if(event.getPackageName().toString().equals("com.linkedin.android") || event.getPackageName().toString().equals("com.tencent.mobileqq") ||  event.getPackageName().toString().equals("com.xiaomi.xmsf") ){
//							String sss = event.getPackageName().toString();
//							int ddd= 999;
//						}
//						System.out.println( "other" + event.getEventType() + " .package:"  + event.getPackageName() + " .text:" + event.getText().toString());

						if(event.getPackageName().toString().contains("com.kct.fundo")){   // todo --- add 20180202    com.kct.fundo.btnotification
							return;
						}

						if(event.getPackageName().toString().contains(".music")){
							return;
						}
						if (!(boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_CALL_NOTIFY, true)) {
							if (event.getPackageName().toString().contains(".incallui")) {
								String dsd = "56546";
								return;
							}
						}
						if (!(boolean) SharedPreUtil.getParam(mContext, SharedPreUtil.USER, SharedPreUtil.TB_SMS_NOTIFY, true)) {
							//最好不要这样写，这是屏蔽了特殊手机的信息啊mms
							if (event.getPackageName().toString().contains(".mms") || event.getPackageName().toString().contains(".contacts")) {
								return;
							}
						}
						if (event.getPackageName().equals("com.android.mms")) {
							return;
						}
						NotificationData notificationData = notificationDataManager.getNotificationData(
								notification, event.getPackageName().toString(),"", NotificationController.genMessageId());
						notificationDataManager.sendNotificationData(notificationData,2);   // todo --- 发送消息 2
					//	Log.e("AppManager/Noti/Manager", "发送2类型的消息-- NeNotificationService");
						/*if(notificationDataManager.isSendmsg()==false){
							Log.e("SENFG","ONE");

						}*/
					}
				}catch (Exception E){E.printStackTrace();}
}}
	}

	@Override
	protected void onServiceConnected() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED |
				AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
				AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
		info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
		info.notificationTimeout = 100;
		setServiceInfo(info);

	}
	@Override
	public void onInterrupt() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	/*	Log.e(TAG, "NeNotificationService ------ onStartCommand");

		if (timerNeNoti == null) {
			timerNeNoti = new Timer();
		} else {
			timerNeNoti.cancel();
			timerNeNoti = null;
			timerNeNoti = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new MyTask();
		} else {
			mTimerTask.cancel();
			mTimerTask = null;
			mTimerTask = new MyTask();
		}

		timerNeNoti.schedule(mTimerTask, 0, 10000);  // 1000
		return START_REDELIVER_INTENT;*/
		return super.onStartCommand(intent, flags, startId);
	}

/*	class MyTask extends TimerTask {
		@Override
		public void run() {
			boolean b = Utils.isServiceRunning(NeNotificationService.this, "com.mtk.app.notification.NotificationReceiver18");
			if(!b) {
				Log.e(TAG, "NotificationReceiver18 stop: " + System.currentTimeMillis());
				Intent service = new Intent(NeNotificationService.this, NotificationReceiver18.class);
				startService(service);
				Log.e(TAG, "Start NotificationReceiver18");
			}else {
				Log.e(TAG, "NotificationReceiver18 Run: " + System.currentTimeMillis());
			}
		}
	}*/

	@Override
	public void onDestroy() {
		super.onDestroy();
	/*	if (timerNeNoti != null) {
			timerNeNoti.cancel();
			timerNeNoti = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}*/
	}
}
