package com.yiweigao.campustours;

import android.content.Context;
import android.media.MediaPlayer;

import com.google.android.gms.location.Geofence;

/**
 * Created by yiweigao on 4/6/15.
 */

// uses Singleton pattern
public class AudioPlayer {  

    private static final AudioPlayer INSTANCE = new AudioPlayer();
    private MediaPlayer mMediaPlayer;
    private Context mContext;

    private AudioPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    public static AudioPlayer getInstance() {
        return INSTANCE;
    }

    public void create(Context context) {
        mContext = context;
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio00);
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

    public void changeAudioSource(String geofenceRequestId, int geofenceTransition) {
        // TODO change data source based on geofence and transition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            mMediaPlayer.release();
            switch (geofenceRequestId) {
                case "1":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio01);
                    break;
                case "2":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio02);
                    break;
                case "3":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio03);
                    break;
                case "4":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio04);
                    break;
                case "5":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio05);
                    break;
                case "6":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio06);
                    break;
                case "7":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio07);
                    break;
                case "8":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio08);
                    break;
                case "9":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio09);
                    break;
                case "10":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio10);
                    break;
                case "11":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio11);
                    break;
            }
        }
        togglePlayback();
    }
}