package com.szkct.bluetoothservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.MediaManager;


public class RingService extends Service {

    boolean flag = true;

    public static RingService ring = null;

    public RingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub      
        super.onCreate();
        ring = this;
    }

    public static MediaPlayer mediaPlayer ;
    public boolean ring() {
        mediaPlayer=new MediaPlayer();
        Uri ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        try
        {
            mediaPlayer.setDataSource(this, ringToneUri);
            final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING)!= 0)
            {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mediaPlayer.setLooping(false);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ringdisconnect() {
        mediaPlayer = new MediaPlayer();
        Uri ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            mediaPlayer.setDataSource(this, ringToneUri);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mediaPlayer.setLooping(false);
                mediaPlayer.prepare();
                mediaPlayer.start();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public void stopRing() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void vibrate(int ms) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(ms);
    }


    public static RingService getRing() {
        return ring;
    }
}
