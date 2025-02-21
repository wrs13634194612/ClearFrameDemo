package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private final IBinder binder = new MusicBinder();

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_game); // 替换为您的音乐资源
        mediaPlayer.setLooping(true); // 设置音乐循环播放
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // 服务可被重建
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop(); // 停止播放
        mediaPlayer.release(); // 释放资源
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder; // 返回绑定的 Binder
    }

    public void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // 播放音乐
        }
    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // 停止音乐
        }
    }
}