package com.yiweigao.campustours;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by yiweigao on 4/6/15.
 */

public class AudioPlayer {

    private MediaPlayer mPlayer;
    
    public void stop() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    public void play(Context context) {
        mPlayer = MediaPlayer.create(context, R.raw.emory_university_overview);
        mPlayer.start();
    }
    
}
