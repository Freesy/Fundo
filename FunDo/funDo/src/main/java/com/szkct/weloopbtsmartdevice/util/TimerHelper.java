package com.szkct.weloopbtsmartdevice.util;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper {
	private TimerProcessor mProcessor;
	private int mDelayMs;
	private Timer mTimer;
	private TimerTask mTimerTask;
	
	public TimerHelper(int mDelayMs, TimerProcessor mProcessor){
		this.mDelayMs = mDelayMs;
		this.mProcessor = mProcessor;
	}

	public TimerHelper(TimerProcessor mProcessor){
		this.mProcessor = mProcessor;
	}
	
	public void startTimer(){
		stopTimer();
		mTimer = new Timer(true);
		mTimerTask = new TimerTask() {
			public void run() {
				if(mProcessor != null){
					mProcessor.process();
				}
			}
		};
		mTimer.schedule(mTimerTask, mDelayMs);
	}

	public void startPeriodTimer(int delay,int mDelayMs){
		stopTimer();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			public void run() {
				if(mProcessor != null){
					mProcessor.process();
				}
			}
		};
		mTimer.schedule(mTimerTask, delay, mDelayMs);
	}

	public void startPeriodTimer(){
		stopTimer();
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			public void run() {
				if(mProcessor != null){
					mProcessor.process();
				}
			}
		};
		mTimer.schedule(mTimerTask, 0, mDelayMs);
	}
	
	public void stopTimer(){
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
		if(mTimerTask != null){
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
}
