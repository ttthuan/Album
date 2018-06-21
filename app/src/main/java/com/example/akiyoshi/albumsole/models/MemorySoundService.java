package com.example.akiyoshi.albumsole.models;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.activities.MemoryActivity;

import java.io.FileInputStream;
import java.io.IOException;

public class MemorySoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            player = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd(MemoryActivity.listSound.get(MemoryActivity.now).getPath());
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.setLooping(false);
            player.setVolume(100, 100);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mPlayer) {
                    Log.i("NEXT", "Sound next");
                    MemoryActivity.now++;
                    mPlayer.reset();
                    if (MemoryActivity.now >= MemoryActivity.listSound.size()) {
                        MemoryActivity.now = 0;
                    }
                    AssetFileDescriptor afd;
                    try {
                        afd = getAssets().openFd(MemoryActivity.listSound.get(MemoryActivity.now).getPath());
                        mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mPlayer.prepare();
                        player.setLooping(false);
                        player.setVolume(100, 100);
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return Service.START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
