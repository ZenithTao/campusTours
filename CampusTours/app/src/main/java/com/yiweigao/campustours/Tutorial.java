package com.yiweigao.campustours;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by yiweigao on 4/16/15.
 */

public class Tutorial {

    private Context mContext;

    public Tutorial(Context context) {
        mContext = context;
        launchMapShowCaseView();
    }

    /**
     * Tests if this device needs the tutorial
     * @param context The context of the current application
     * @return True if tutorial needs to be shown, otherwise false
     */
    public static boolean isNeeded(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.yiweigao.CampusTours", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("first_run", true)) {
            sharedPreferences.edit().putBoolean("first_run", false).apply();
            return true;
        }
        return false;
    }

    private void launchMapShowCaseView() {
        Point screenSize = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(screenSize);
        new ShowcaseView.Builder((Activity) mContext, true)
                .setTarget(new PointTarget(new Point(screenSize.x, 0)))
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Using the map")
                .setContentText("Make sure that your GPS is turned on, and " +
                        "click on this button to show your current " +
                        "location on the map.\n\nTap anywhere to continue.")
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        launchControlPanelShowCaseView();
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        showcaseView.hideButton();
                    }
                })
                .hideOnTouchOutside()
                .build();
    }

    private void launchControlPanelShowCaseView() {
        ViewTarget viewTarget = new ViewTarget(R.id.control_panel_play_button, (Activity) mContext);
        new ShowcaseView.Builder((Activity) mContext, true)
                .setTarget(viewTarget)
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("Using the audio controls")
                .setContentText("Audio clips will play automatically along the " +
                        "tour, but you can use these buttons to " +
                        "rewind, play/pause, and fast forward." +
                        "\n\nEnjoy your tour!" +
                        "\n\nTap anywhere to dismiss this message.")
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        showcaseView.hideButton();
                    }
                })
                .hideOnTouchOutside()
                .build();
    }
}
