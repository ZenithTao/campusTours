package com.yiweigao.campustours;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by yiweigao on 2/26/15.
 */
public class ControlPanelFragment extends Fragment {

    View mInflatedView;
    MediaPlayer mMediaPlayer = null;
    Button mPrevButton;
    Button mPlayButton;
    Button mNextButton;
    boolean mIsPlaying = false;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        mInflatedView = inflater.inflate(R.layout.control_panel_fragment, container, false);

        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);

        mPrevButton = (Button) mInflatedView.findViewById(R.id.control_panel_prev_button);
        mPlayButton = (Button) mInflatedView.findViewById(R.id.control_panel_play_button);
        mNextButton = (Button) mInflatedView.findViewById(R.id.control_panel_next_button);
        
        
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaPlayer.isPlaying()) {
                    // skips backwards 10s, need to set this as a constant
                    mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 10000); 
                }

            }
        });
        
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mPlayButton.setText(R.string.control_panel_pause_button_text);
                } else if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayButton.setText(R.string.control_panel_play_button_text);
                }

            }
        });
        
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        
        return mInflatedView;
        
    }
}
