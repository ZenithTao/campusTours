package com.yiweigao.campustours;

import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Created by yiweigao on 2/26/15.
 */
public class ControlPanelFragment extends Fragment {

    private static int POSITION_OFFSET = 10000;
    private static float BUTTON_ALPHA = 0.80f;
    private View mInflatedView;
    private MediaPlayer mMediaPlayer;
    private ImageButton mRwndButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private int trackNumber = 0;
    private int duration = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
//        return super.onCreateView(inflater, container, savedInstanceState);

        mInflatedView = inflater.inflate(R.layout.control_panel_fragment, container, false);

//        AssetFileDescriptor assetFileDescriptor = 
//                getActivity().getResources().openRawResourceFd(R.raw.emory_university_overview);
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);
        
//        try {
//            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
//            mMediaPlayer.prepare();
//            duration = mMediaPlayer.getDuration();
//            assetFileDescriptor.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mRwndButton = (ImageButton) mInflatedView.findViewById(R.id.control_panel_rewind_button);
        mPlayButton = (ImageButton) mInflatedView.findViewById(R.id.control_panel_play_button);
        mNextButton = (ImageButton) mInflatedView.findViewById(R.id.control_panel_next_button);
        
        mRwndButton.setAlpha(BUTTON_ALPHA);
        mPlayButton.setAlpha(BUTTON_ALPHA);
        mNextButton.setAlpha(BUTTON_ALPHA);


        mRwndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // skips backwards 
                if (mMediaPlayer.isPlaying()) {
                    
                    int newPosition = mMediaPlayer.getCurrentPosition() - POSITION_OFFSET;
                    
                    if (newPosition > 0) {
                        mMediaPlayer.seekTo(newPosition);
                    }
                }

            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mPlayButton.setImageResource(R.mipmap.pause_icon);
                } else if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayButton.setImageResource(R.mipmap.play_icon);
                }

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newPosition = mMediaPlayer.getCurrentPosition() + POSITION_OFFSET;

                if (newPosition > duration) {
                    trackNumber++;
                    nextTrack(trackNumber);
                }

                // skips forwards
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + POSITION_OFFSET);
                }

            }
        });

        return mInflatedView;

    }

    private void nextTrack(int nextTrackNumber) {
        mMediaPlayer.release();
        if (nextTrackNumber > 4) {
            nextTrackNumber = 0;
            trackNumber = 0;
        }
        switch (nextTrackNumber) {
            case 0:
                mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);
                break;
            case 1:
                mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.undergrad_01);
                break;
            case 2:
                mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.undergrad_02);
                break;
            case 3:
                mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.undergrad_03);
                break;
            case 4:
                mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.undergrad_04);
                break;
        }

        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
    }
    
    private void changeTrack(int newTrackNumber) {

        if (newTrackNumber > 4) {
            newTrackNumber = 0;
            trackNumber = 0;
        }

        AssetFileDescriptor assetFileDescriptor = null;
//                getActivity().getResources().openRawResourceFd(R.raw.emory_university_overview);

        switch (newTrackNumber) {
            case 0:
                assetFileDescriptor = getActivity().getResources().openRawResourceFd(R.raw.emory_university_overview);
                break;
            case 1:
                assetFileDescriptor = getActivity().getResources().openRawResourceFd(R.raw.undergrad_01);
                break;
            case 2:
                assetFileDescriptor = getActivity().getResources().openRawResourceFd(R.raw.undergrad_02);
                break;
            case 3:
                assetFileDescriptor = getActivity().getResources().openRawResourceFd(R.raw.undergrad_03);
                break;
            case 4:
                assetFileDescriptor = getActivity().getResources().openRawResourceFd(R.raw.undergrad_04);
                break;
        }

        try {
            assert assetFileDescriptor != null;
//            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            assetFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
