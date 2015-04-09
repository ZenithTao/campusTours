package com.yiweigao.campustours;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by yiweigao on 4/6/15.
 */

public class AudioPlayer {

    private static MediaPlayer sMediaPlayer;

    private AudioPlayer() {}
    
    public static MediaPlayer getInstance() {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
        }
        return sMediaPlayer;
    }
    
    

    public void create(Context context) {
        sMediaPlayer = MediaPlayer.create(context, R.raw.emory_university_overview);
//        sMediaPlayer.start();
    }
    
    public void stop() {
        if (sMediaPlayer != null) {
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    }
    
    public void togglePlayback() {
        if (sMediaPlayer != null) {
            if (sMediaPlayer.isPlaying()) {
                sMediaPlayer.pause();
            } else {
                sMediaPlayer.start();
            }
        }
    }
    
    public void next() {
        
    }
    
    // go back 10000ms = 10s
    public void rewind() {
        if (sMediaPlayer != null) {
            sMediaPlayer.seekTo(sMediaPlayer.getCurrentPosition() - 10000);
        }
    }
    
    public boolean isPlaying() {
        return sMediaPlayer.isPlaying();
    }

}