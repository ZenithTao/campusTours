package com.yiweigao.campustours;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        ResultCallback<Status> {

    // b/w b.jones and msc
    public static final String TEST_ONE = "One";
    public static final LatLng TEST_ONE_LATLNG = new LatLng(33.789933, -84.326457);
    public static final float TEST_ONE_RADIUS = 25f;
    public static final int TEST_ONE_LIFETIME = 100000;
    // b/w white and admin
    public static final String TEST_TWO = "Two";
    public static final LatLng TEST_TWO_LATLNG = new LatLng(33.790457, -84.325608);
    public static final float TEST_TWO_RADIUS = 25f;
    public static final int TEST_TWO_LIFETIME = 100000;
    // in front of carlos
    public static final String TEST_THREE = "Three";
    public static final LatLng TEST_THREE_LATLNG = new LatLng(33.790587, -84.324259);
    public static final float TEST_THREE_RADIUS = 25f;
    public static final int TEST_THREE_LIFETIME = 100000;
    private static final LatLng YIWEI_POS = new LatLng(40, -79);
    private static final String ASBURY_CIRCLE_NAME = "Asbury Circle";
    private static final LatLng ASBURY_CIRCLE = new LatLng(33.792731, -84.324075);
    private static final float ASBURY_CIRCLE_RADIUS = 20.0f;
    private static final int ASBURY_CIRCLE_LIFETIME = 100000;
    private static final String HOUSE_TEXT = "Fraternity house";
    private static final LatLng HOUSE = new LatLng(33.793766, -84.327198);
    private static final float HOUSE_RADIUS = 15.0f;     // current minimum = 15 (9), reliable (unreliable)
    private static final int HOUSE_LIFETIME = 100000;
    private static final String TOUR_START_NAME = "Tour Start";
    private static final LatLng TOUR_START = new LatLng(33.789591, -84.326506);
    // ORB
    private static final String TEST_NAME = "Test";
    private static final LatLng TEST = new LatLng(33.789933, -84.326457);
    private static final float TEST_RADIUS = 15.0f;
    private static final int TEST_LIFETIME = 100000;
    private GoogleMap mGoogleMap;
    private MapManager mapManager;
    private MapFragment mMapFragment;
    private ControlPanelFragment mControlPanelFragment;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private List<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;

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


        mGeofencePendingIntent = null;

        try {
            if (mMapFragment == null) {
                mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                mMapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        populateGeofenceList();

        buildGoogleApiClient();

        createLocationRequest();

    }

    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapManager = new MapManager(getApplicationContext(), googleMap);

        googleMap.addCircle(new CircleOptions()
                .center(HOUSE)
                .radius(HOUSE_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(TEST_ONE_LATLNG)
                .radius(TEST_ONE_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(TEST_TWO_LATLNG)
                .radius(TEST_TWO_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(TEST_THREE_LATLNG)
                .radius(TEST_THREE_RADIUS)
                .visible(true));

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
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) this);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mapManager.updateCamera(location);
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        mGeofenceList = new ArrayList<Geofence>();
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(HOUSE_TEXT)
                .setCircularRegion(
                        HOUSE.latitude,
                        HOUSE.longitude,
                        HOUSE_RADIUS)
                .setExpirationDuration(HOUSE_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(TEST_ONE)
                .setCircularRegion(
                        TEST_ONE_LATLNG.latitude,
                        TEST_ONE_LATLNG.longitude,
                        TEST_ONE_RADIUS)
                .setExpirationDuration(TEST_ONE_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(TEST_TWO)
                .setCircularRegion(
                        TEST_TWO_LATLNG.latitude,
                        TEST_TWO_LATLNG.longitude,
                        TEST_TWO_RADIUS)
                .setExpirationDuration(TEST_ONE_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(TEST_THREE)
                .setCircularRegion(
                        TEST_THREE_LATLNG.latitude,
                        TEST_THREE_LATLNG.longitude,
                        TEST_THREE_RADIUS)
                .setExpirationDuration(TEST_ONE_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;

            Toast.makeText(
                    this,
                    mGeofencesAdded ? "Geofence added" :
                            "Geofence removed",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
        }
    }


}