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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yiweigao on 2/26/15.
 */
public class ControlPanelFragment extends Fragment {

    public static Map<String, Integer> library = new HashMap<>();
    public static MediaPlayer MEDIA_PLAYER;

    private AudioPlayer mAudioPlayer = new AudioPlayer();
    
    private static int POSITION_OFFSET = 10000;

    private static float BUTTON_ALPHA = 0.80f;
    private View mInflatedView;
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
        MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);

//        try {
//            MEDIA_PLAYER.setDataSource(assetFileDescriptor.getFileDescriptor());
//            MEDIA_PLAYER.prepare();
//            duration = MEDIA_PLAYER.getDuration();
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

        library.put(MainActivity.TEST_ONE, R.raw.emory_university_overview);
        library.put(MainActivity.TEST_TWO, R.raw.undergrad_02);
        library.put(MainActivity.TEST_THREE, R.raw.undergrad_04);

        mRwndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // skips backwards 
                if (MEDIA_PLAYER.isPlaying()) {

                    int newPosition = MEDIA_PLAYER.getCurrentPosition() - POSITION_OFFSET;

                    if (newPosition > 0) {
                        MEDIA_PLAYER.seekTo(newPosition);
                    } else if (newPosition <= 0) {
                        MEDIA_PLAYER.seekTo(0);
                    }
                }

            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!MEDIA_PLAYER.isPlaying()) {
                    MEDIA_PLAYER.start();
                    mPlayButton.setImageResource(R.mipmap.pause_icon);
                } else if (MEDIA_PLAYER.isPlaying()) {
                    MEDIA_PLAYER.pause();
                    mPlayButton.setImageResource(R.mipmap.play_icon);
                }

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newPosition = MEDIA_PLAYER.getCurrentPosition() + POSITION_OFFSET;

                if (newPosition > duration) {
                    trackNumber++;
                    nextTrack(trackNumber);
                }

                // skips forwards
                if (MEDIA_PLAYER.isPlaying()) {
                    MEDIA_PLAYER.seekTo(MEDIA_PLAYER.getCurrentPosition() + POSITION_OFFSET);
                }

            }
        });

        return mInflatedView;

    }

//    public static void playTrack(int trackNumber) {
//        MEDIA_PLAYER.release();
//        switch (trackNumber) {
//            case 0:
//                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);
//                break;
//            case 1:
//                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.undergrad_02);
//                break;
//            case 2:
//                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.undergrad_04);
//                break;
//        }
//    }

    private void nextTrack(int nextTrackNumber) {
        MEDIA_PLAYER.release();
        if (nextTrackNumber > 4) {
            nextTrackNumber = 0;
            trackNumber = 0;
        }
        switch (nextTrackNumber) {
            case 0:
                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.emory_university_overview);
                break;
            case 1:
                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.undergrad_01);
                break;
            case 2:
                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.undergrad_02);
                break;
            case 3:
                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.undergrad_03);
                break;
            case 4:
                MEDIA_PLAYER = MediaPlayer.create(getActivity(), R.raw.undergrad_04);
                break;
        }

        MEDIA_PLAYER.seekTo(0);
        MEDIA_PLAYER.start();

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
//            MEDIA_PLAYER.reset();
            MEDIA_PLAYER.stop();
            MEDIA_PLAYER.setDataSource(assetFileDescriptor.getFileDescriptor());
            MEDIA_PLAYER.prepare();
            MEDIA_PLAYER.start();
            assetFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    The getters below are for testing
     */
    public int getCurrentTrackNumber() {
        return trackNumber;
    }

    public int getCurrentTime() {
        return MEDIA_PLAYER.getCurrentPosition();
    }
}
