package com.yiweigao.campustours;

import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final LatLng YIWEI_POS = new LatLng(40, -79);

    private static final String ASBURY_CIRCLE_NAME = "Asbury Circle";
    private static final LatLng ASBURY_CIRCLE = new LatLng(33.792731, -84.324075);
    private static final float ASBURY_CIRCLE_RADIUS = 20.0f;
    private static final int ASBURY_CIRCLE_LIFETIME = 100000;

    private static final String HOUSE_TEXT = "Fraternity house";
    private static final LatLng HOUSE = new LatLng(33.793766, -84.327198);
    private static final float HOUSE_RADIUS = 20.0f;
    private static final int HOUSE_LIFETIME = 100000;

    private GoogleMap mGoogleMap;

    private GoogleApiClient mGoogleApiClient;
    private List<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;

        try {
            if (mGoogleMap == null) {
                mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.setBuildingsEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);


        } catch (Exception e) {
            e.printStackTrace();
        }

        buildGoogleApiClient();

        mGoogleApiClient.connect();
        Log.d("onCreate", "just finished called connect()");

    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    @Override
    public void onConnected(Bundle bundle) {
//        Log.d("onConnected", "api is now connected");
//        Geofence asburyFence = new Geofence.Builder()
//                .setRequestId(ASBURY_CIRCLE_NAME)
//                .setCircularRegion(ASBURY_CIRCLE.latitude, ASBURY_CIRCLE.longitude, ASBURY_CIRCLE_RADIUS)
//                .setExpirationDuration(ASBURY_CIRCLE_LIFETIME)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
//                        | Geofence.GEOFENCE_TRANSITION_EXIT
//                        | Geofence.GEOFENCE_TRANSITION_DWELL)
//                .setLoiteringDelay(5000)
//                .build();
//
//        Geofence houseFence = new Geofence.Builder()
//                .setRequestId(HOUSE_TEXT)
//                .setCircularRegion(HOUSE.latitude, HOUSE.longitude, HOUSE_RADIUS)
//                .setExpirationDuration(HOUSE_LIFETIME)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
//                        | Geofence.GEOFENCE_TRANSITION_EXIT
//                        | Geofence.GEOFENCE_TRANSITION_DWELL)
//                .setLoiteringDelay(5000)
//                .build();
//
//        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
//                .addGeofence(asburyFence)
//                .addGeofence(houseFence)
//                .build();
//
//        Log.d("onResume", "about to call api");
//
//        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofencingRequest, getGeofencePendingIntent());
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
