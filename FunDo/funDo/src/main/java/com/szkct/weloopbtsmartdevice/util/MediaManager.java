package com.szkct.weloopbtsmartdevice.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.CountDownTimer;
import android.os.Vibrator;

/**
 * 用于Mtk查收设备播放声音
 */
public class MediaManager {

    private static MediaPlayer mPlayer;
    private static Vibrator mVibrator = null;//震动
    private static final long[] VIBRATE_PATTERN = new long[]{
            500, 500
    };

    private static TimeCount timeCount = null;//倒计时器
    private static final int DURATION_TIME = 3 * 1000;//持续时间

    public interface PlaySoundListener {
        //播放前 该方法中不要对mPlayer再做操作
        public abstract void playBefore();

        //播完后
        public abstract void playCompletion();
    }

    public static MediaPlayer getMediaPlayerInstance() {
        if (mPlayer == null) {
            synchronized (MediaManager.class) {
                if (mPlayer == null) {
                    mPlayer = new MediaPlayer();
                }
            }
        }
        return mPlayer;
    }

    //播放Assets下音乐资源
    public static void playSound(AssetFileDescriptor assetFileDescriptor) {

        try {

            if (!isPlaying()) {//如果没有播放 那就播放
//                timeCount = new TimeCount(DURATION_TIME, 1000L);
//                timeCount.start();//启动倒计时器

                if (mPlayer == null) {
                    mPlayer = getMediaPlayerInstance();
                }
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mPlayer.setLooping(true);//重复播放

                mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer player, int what, int extra) {
                        stop();
                        release();
                        return false;
                    }
                });

                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void playSound(String filePathString,
                                 final PlaySoundListener playSoundListener) {

        try {

            playSoundListener.playBefore();//播放之前

            if (mPlayer == null) {
                mPlayer = getMediaPlayerInstance();
            }

            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(filePathString);

            mPlayer.setOnCompletionListener(new OnCompletionListener() {//播放完的监听函数
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playSoundListener.playCompletion();//播完后
                    release();//播完后就停止
                }
            });

            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //暂停
    public static void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    //停止
    public static void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }

        stopVib();
    }

    //正在播放
    public static boolean isPlaying() {
        if (mPlayer == null) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    public static void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }

        stopVib();
    }

    //开始震动
    public static void replayVib(Context context) {
        try {
            mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(VIBRATE_PATTERN, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //停止震动
    public static void stopVib() {
        try {
            if (mVibrator != null) {
                mVibrator.cancel();
                mVibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭倒计时器
    private void closeTimeCount() {
        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
    }

    /**
     * 倒计时器
     */
    static class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
//            MediaManager.stopVib();//停止震动
//            MediaManager.stop();//停止播放
//            MediaManager.release();//释放资源
//            if (timeCount != null) {
//                timeCount.cancel();
//                timeCount = null;
//            }
        }
    }

}
