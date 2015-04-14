package com.yiweigao.campustours;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yiweigao on 2/26/15.
 */
public class ControlPanelFragment extends Fragment {

    public static Map<String, Integer> library = new HashMap<>();
    public static MediaPlayer MEDIA_PLAYER;

    private AudioPlayer mAudioPlayer = AudioPlayer.getInstance();
    
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

//        library.put(MainActivity.TEST_ONE, R.raw.emory_university_overview);
//        library.put(MainActivity.TEST_TWO, R.raw.undergrad_02);
//        library.put(MainActivity.TEST_THREE, R.raw.undergrad_04);
        
        mAudioPlayer.create(getActivity());
        
        mRwndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioPlayer.rewind();  
            }
        });
        
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioPlayer.togglePlayback();
                if (mAudioPlayer.isPlaying()) {
                    mPlayButton.setImageResource(R.mipmap.pause_icon);
                } else {
                    mPlayButton.setImageResource(R.mipmap.play_icon);
                }   
            }
        });
        
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioPlayer.next();
            }
        });

        return mInflatedView;

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
