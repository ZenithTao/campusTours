package com.yiweigao.campustours;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.ImageButton;

import com.google.android.gms.location.Geofence;

/**
 * Created by yiweigao on 4/6/15.
 */

// uses Singleton pattern
public class AudioPlayer {

    private static final AudioPlayer INSTANCE = new AudioPlayer();
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private ImageButton mPlayButton;
    private int currentTrack = 0;

    private AudioPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    public static AudioPlayer getInstance() {
        return INSTANCE;
    }

    public void setPlayButton(ImageButton playButton) {
        mPlayButton = playButton;
    }

    public void create(Context context) {
        mContext = context;
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio00);
    }

    /**
     * Toggles audio playback, and changes the image button to reflect current state
     */
    public void togglePlayback() {
        if (mMediaPlayer.isPlaying()) {
            pausePlayback();
        } else {
            mMediaPlayer.start();
            mPlayButton.setImageResource(R.mipmap.pause_icon);
        }
    }

    /**
     * Only pauses the player and updates the button
     */
    public void pausePlayback() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mPlayButton.setImageResource(R.mipmap.play_icon);
        }
    }

    /**
     * Loops through all of the audio source files
     */
    public void next() {
        if (currentTrack > 11) {
            currentTrack = 0;
        }
        // hacky way to change audio tracks based on button
        changeAudioSource(String.valueOf(++currentTrack), Geofence.GEOFENCE_TRANSITION_ENTER);
    }

    /**
     * Seek back 10000 ms, or 10s.
     * Good thing is the seekTo() method already checks bounds, so if the resulting position
     * is < 0, it will just play from the beginning
     */
    public void rewind() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000);
    }

    /**
     * Changes the audio source file based on the triggering geofence and
     * the type of geofence event. Audio will then automatically play
     *
     * @param geofenceRequestId  The ID of the triggering geofence
     * @param geofenceTransition The type of geofence transition
     */
    public void changeAudioSource(String geofenceRequestId, int geofenceTransition) {
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
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
                case "Fraternity house":
                    mMediaPlayer = MediaPlayer.create(mContext, R.raw.audio02);
                    break;
            }
        }
        togglePlayback();
    }

    /**
     * Releases the media player and nullifies the INSTANCE of the Singleton
     */
    public void releaseMediaPlayer() {
        mMediaPlayer.release();
    }
}