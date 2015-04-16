package com.yiweigao.campustours;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.MapFragment;

public class MainActivity extends ActionBarActivity implements
        ResultCallback<Status> {

    private MapFragment mMapFragment;
    private MapManager mMapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.control_panel_fragment);

        if (fragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.control_panel_fragment, new ControlPanelFragment());
            fragmentTransaction.commit();
        }

        try {
            if (mMapFragment == null) {
                mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                mMapManager = new MapManager(this, mMapFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
//        if (status.isSuccess()) {
//            // Update state and save in shared preferences.
//            mGeofencesAdded = !mGeofencesAdded;
//
//            Toast.makeText(
//                    this,
//                    mGeofencesAdded ? "Geofence added" :
//                            "Geofence removed",
//                    Toast.LENGTH_SHORT
//            ).show();
//        }
    }
}