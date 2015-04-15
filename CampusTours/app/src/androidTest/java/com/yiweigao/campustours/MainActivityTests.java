package com.yiweigao.campustours;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import java.io.Console;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.test.ViewAsserts.assertOnScreen;

/*
    Make sure you turn off all animations in your emulator (under dev controls) otherwise they
    may cause the tests to be finicky
 */

public class MainActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mMainActivity;

    public MainActivityTests() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
    }

    public void testControlPanelIsVisible() {
        View controlPanelView = mMainActivity.findViewById(R.id.control_panel_fragment);
        assertOnScreen(mMainActivity.getWindow().getDecorView(), controlPanelView);
    }

    public void testPlayPauseButtonWorks() {
        AudioManager audioManager = (AudioManager) mMainActivity.getSystemService(Context.AUDIO_SERVICE);
        View playButton = mMainActivity.findViewById(R.id.control_panel_play_button);
        assertTrue(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE).matches(playButton));
        onView(withId(R.id.control_panel_play_button)).perform(click());
        assertTrue(audioManager.isMusicActive());
        onView(withId(R.id.control_panel_play_button)).perform(click());
        assertFalse(audioManager.isMusicActive());
    }

    public void testRewindButtonWorks() {
        FragmentManager fragmentManager = mMainActivity.getFragmentManager();
        ControlPanelFragment controlPanelFragment = (ControlPanelFragment) fragmentManager.findFragmentById(R.id.control_panel_fragment);
        AudioManager audioManager = (AudioManager) mMainActivity.getSystemService(Context.AUDIO_SERVICE);
        View rewindButton = mMainActivity.findViewById(R.id.control_panel_rewind_button);
        assertTrue(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE).matches(rewindButton));
        onView(withId(R.id.control_panel_play_button)).perform(click());
        int initialTime = controlPanelFragment.getCurrentTime();
        assertTrue(audioManager.isMusicActive());
        onView(withId(R.id.control_panel_rewind_button)).perform(click());
        int finalTime = controlPanelFragment.getCurrentTime();
        assertTrue(finalTime <= initialTime);
        assertTrue(audioManager.isMusicActive());
        onView(withId(R.id.control_panel_play_button)).perform(click());
    }

    public void testSkipButtonWorks() {
        FragmentManager fragmentManager = mMainActivity.getFragmentManager();
        ControlPanelFragment controlPanelFragment = (ControlPanelFragment) fragmentManager.findFragmentById(R.id.control_panel_fragment);
        AudioManager audioManager = (AudioManager) mMainActivity.getSystemService(Context.AUDIO_SERVICE);
        View skipButton = mMainActivity.findViewById(R.id.control_panel_next_button);
        assertTrue(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE).matches(skipButton));
        onView(withId(R.id.control_panel_play_button)).perform(click());
        int initialTrack = controlPanelFragment.getCurrentTrackNumber();
        assertTrue(audioManager.isMusicActive());
        onView(withId(R.id.control_panel_next_button)).perform(click());
        int finalTrack = controlPanelFragment.getCurrentTrackNumber();
        assertFalse(initialTrack == finalTrack);
        assertTrue(audioManager.isMusicActive());
        onView(withId(R.id.control_panel_play_button)).perform(click());
    }

}