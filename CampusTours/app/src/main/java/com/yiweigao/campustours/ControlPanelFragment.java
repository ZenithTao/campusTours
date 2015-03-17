package com.yiweigao.campustours;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by yiweigao on 2/26/15.
 */
public class ControlPanelFragment extends Fragment {

    private static int POSITION_OFFSET = 10000;
    boolean mIsPlaying = false;
    private View mInflatedView;
    private MediaPlayer mMediaPlayer = null;
    private ImageButton mRwndButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        mInflatedView = inflater.inflate(R.layout.control_panel_fragment, container, false);

        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);

        mRwndButton = (ImageButton) mInflatedView.findViewById(R.id.control_panel_rewind_button);
        mPlayButton = (ImageButton) mInflatedView.findViewById(R.id.control_panel_play_button);
        mNextButton = (ImageButton) mInflatedView.findViewById(R.id.control_panel_next_button);
        
        mRwndButton.setAlpha(0.75f);
        mPlayButton.setAlpha(0.75f);
        mNextButton.setAlpha(0.75f);


        mRwndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaPlayer.isPlaying()) {
                    // skips backwards 
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - POSITION_OFFSET);
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

                if (mMediaPlayer.isPlaying()) {
                    // skips forwards
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + POSITION_OFFSET);
                }

            }
        });

        return mInflatedView;

    }
}
