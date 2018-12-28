package com.szkct.weloopbtsmartdevice.main;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.kct.fundo.btnotification.R;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 运动开始倒计时
 * 
 * @author Administrator
 * 
 */
public class SportsTheCountdownActivity extends Activity
{
	TextView text;
	int seconds = 4;
	Animation rotateAnimation,translateAnimation;// 动画
	AnimationSet set;
	//private PowerManager powerManager;
	private boolean isStop = true;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_thecountdow);

		if (SharedPreUtil.readPre(getApplicationContext(),
			SharedPreUtil.USER,
			SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL).equals("")||SharedPreUtil.readPre(getApplicationContext(),
				SharedPreUtil.USER,
				SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL).equals("0")){
			seconds = 4;
		}else if (SharedPreUtil.readPre(getApplicationContext(),
				SharedPreUtil.USER,
				SharedPreUtil.TV_MOTIONSETTING_RECIPROCAL).equals("1")){

			seconds = 6;
		}else {
			seconds = 11;
		}

		//powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		StatusShared shared = new StatusShared(getApplicationContext());
//		seconds=Integer.valueOf(shared.getCount())+1;

		//初始化  
		rotateAnimation = new AlphaAnimation(0.1f, 1.0f); 
		//初始化 Translate动画  
		translateAnimation = new ScaleAnimation(0.1f, 1.0f,0.1f,1.0f);  
		//动画集  
		set = new AnimationSet(true);  
		set.addAnimation(translateAnimation);  
		set.addAnimation(rotateAnimation);

		//设置动画时间  
		set.setDuration(500);   
		this.text = (TextView) findViewById(R.id.ac);
		new Thread()
		{
			public void run()
			{
				while (seconds != -1)
				{
					seconds--;
					handler.sendEmptyMessage(1);
					try
					{
						sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
				case 1:
					if (seconds == 0) {
						text.startAnimation(set);
						text.setText("GO");
					} else if (seconds > 0) {
						text.startAnimation(set);
						text.setText(seconds + "");
					} else {
						//						setResult(55);
						if (SharedPreUtil.readPre(SportsTheCountdownActivity.this, SharedPreUtil.USER, SharedPreUtil.CB_RUNSETTING_VOICE).equals
								(SharedPreUtil.YES)) {  //振动开关打开了
							Vibrator vib = (Vibrator) SportsTheCountdownActivity.this.getSystemService(Service.VIBRATOR_SERVICE);  //开始运动时，振动500ms
							vib.vibrate(500);
						}
						Intent intent = new Intent(SportsTheCountdownActivity.this, OutdoorRunActitivy.class);   //  TODO ---- 倒计时完成后，进入OutdoorRunActitivy 页面
						startActivity(intent);
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								finish();
							}
						}, 300);    /** 延时finish,不然会点到主界面的开始 */
					}
					break;
				
				default:
					break;
			}
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
