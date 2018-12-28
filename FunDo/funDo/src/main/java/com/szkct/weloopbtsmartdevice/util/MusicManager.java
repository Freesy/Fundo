package com.szkct.weloopbtsmartdevice.util;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;

import java.util.Iterator;

import static android.content.Context.AUDIO_SERVICE;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2017/6/14
 * 描述: ${VERSION}
 * 修订历史：
 */

public class MusicManager {

    private static MusicManager musicManager;
    private Context context = BTNotificationApplication.getInstance().getApplicationContext();
    private MusicBroadcastReceiver musicBroadcastReceiver;
    private AudioManager audioManager;
    private boolean isPlaying = false;
    private static final String TAG = MusicManager.class.getName();


    private MusicManager(){
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.htc.music.metachanged");
        iF.addAction("fm.last.android.metachanged");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        iF.addAction("com.amazon.mp3.metachanged");
        iF.addAction("com.miui.player.metachanged");
        iF.addAction("com.real.IMP.metachanged");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.andrew.apollo.metachanged");
        iF.addAction("com.kugou.android.music.metachanged");
        iF.addAction("com.ting.mp3.playinfo_changed");
        iF.addAction("com.oppo.music.service.meta_changed");
        iF.addAction("com.oppo.music.service.playstate_changed");
        iF.addAction("com.lge.music.metachanged");
        musicBroadcastReceiver = new MusicBroadcastReceiver();
        context.registerReceiver(musicBroadcastReceiver,iF);

        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public static MusicManager getInstance(){
        if(musicManager == null){
            musicManager = new MusicManager();
            return musicManager;
        }else{
            return musicManager;
        }
    }

    private class MusicBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.e(TAG,"intent.getAction() = "+intent.getAction());
            if(intent.hasExtra("playing")) {
                isPlaying = intent.getBooleanExtra("playing", false);
            }
            if(intent.hasExtra("track")) {
                String track = intent.getStringExtra("track");
                Log.e(TAG,"track = " + track);
            }
        }
    }

    public void open(){
        KeyEvent keyDown = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, 126, 0);
        audioManager.dispatchMediaKeyEvent(keyDown);
        KeyEvent keyUp = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, 126, 0);
        audioManager.dispatchMediaKeyEvent(keyUp);
        /*Intent var7 = new Intent("android.intent.action.MEDIA_BUTTON");
        var7.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 126, 0));
        Intent var8 = new Intent("android.intent.action.MEDIA_BUTTON");
        var8.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 126, 0));
        context.sendOrderedBroadcast(var7, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);
        context.sendOrderedBroadcast(var8, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);*/
    }



    public void pause(){
        KeyEvent keyDown = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, 127, 0);
        audioManager.dispatchMediaKeyEvent(keyDown);
        KeyEvent keyUp = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, 127, 0);
        audioManager.dispatchMediaKeyEvent(keyUp);
        /*Intent var7 = new Intent("android.intent.action.MEDIA_BUTTON");
        var7.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 127, 0));
        Intent var8 = new Intent("android.intent.action.MEDIA_BUTTON");
        var8.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 127, 0));
        context.sendOrderedBroadcast(var7, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);
        context.sendOrderedBroadcast(var8, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);*/
    }


    public void next(){
        KeyEvent keyDown = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, 88, 0);
        audioManager.dispatchMediaKeyEvent(keyDown);
        KeyEvent keyUp = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, 88, 0);
        audioManager.dispatchMediaKeyEvent(keyUp);
        /*Intent var7 = new Intent("android.intent.action.MEDIA_BUTTON");
        var7.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 87, 0));
        Intent var8 = new Intent("android.intent.action.MEDIA_BUTTON");
        var8.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 87, 0));
        context.sendOrderedBroadcast(var7, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);
        context.sendOrderedBroadcast(var8, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);*/
    }

    public void last(){
        KeyEvent keyDown = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, 87, 0);
        audioManager.dispatchMediaKeyEvent(keyDown);
        KeyEvent keyUp = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, 87, 0);
        audioManager.dispatchMediaKeyEvent(keyUp);
        /*Intent var7 = new Intent("android.intent.action.MEDIA_BUTTON");
        var7.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 88, 0));
        Intent var8 = new Intent("android.intent.action.MEDIA_BUTTON");
        var8.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 88, 0));
        context.sendOrderedBroadcast(var7, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);
        context.sendOrderedBroadcast(var8, (String)null, (BroadcastReceiver)null, (Handler)null, -1, (String)null, (Bundle)null);*/
    }

    public void up_music(){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        //audioManager.adjustStreamVolume(3, 1, 9);
    }

    public void down_music(){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        //audioManager.adjustStreamVolume(3, -1, 9);
    }

}
