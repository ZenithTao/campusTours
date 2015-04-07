package com.yiweigao.campustours;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by yiweigao on 4/6/15.
 */

public class AudioPlayer {

    private MediaPlayer mMediaPlayer;

    public AudioPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    public void start(Context context) {
        mMediaPlayer = MediaPlayer.create(context, R.raw.emory_university_overview);
        mMediaPlayer.start();
    }
    
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    public void togglePlayback() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                mMediaPlayer.start();
            }
        }
    }
    
    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }
    
    public void unPause() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }
    
    public void next() {
        
    }
    
    // go back 10000ms = 10s
    public void rewind() {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000);
        }
    }
    
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }
}
