package com.yiweigao.campustours;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by yiweigao on 4/6/15.
 */

// uses Singleton pattern
public class AudioPlayer {  

    private static final AudioPlayer AUDIO_PLAYER = new AudioPlayer();
    private MediaPlayer mMediaPlayer;

    private AudioPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    public static AudioPlayer getInstance() {
        return AUDIO_PLAYER;
    }

    public void create(Context context) {
        mMediaPlayer = MediaPlayer.create(context, R.raw.emory_university_overview);
//        mMediaPlayer.start();
    }

    public void stop() {
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void togglePlayback() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    public void next() {

    }

    // go back 10000ms = 10s
    public void rewind() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void reconfigure(String geofenceRequestId, int geofenceTransition) {
        // TODO change data source based on geofence and transition
    }
}