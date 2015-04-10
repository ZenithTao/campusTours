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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        ResultCallback<Status> {    
    
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
                .center(DebugResource.HOUSE)
                .radius(DebugResource.HOUSE_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(DebugResource.TEST_ONE_LATLNG)
                .radius(DebugResource.TEST_ONE_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(DebugResource.TEST_TWO_LATLNG)
                .radius(DebugResource.TEST_TWO_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(DebugResource.TEST_THREE_LATLNG)
                .radius(DebugResource.TEST_THREE_RADIUS)
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
                .setRequestId(DebugResource.HOUSE_TEXT)
                .setCircularRegion(
                        DebugResource.HOUSE.latitude,
                        DebugResource.HOUSE.longitude,
                        DebugResource.HOUSE_RADIUS)
                .setExpirationDuration(DebugResource.HOUSE_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(DebugResource.TEST_ONE)
                .setCircularRegion(
                        DebugResource.TEST_ONE_LATLNG.latitude,
                        DebugResource.TEST_ONE_LATLNG.longitude,
                        DebugResource.TEST_ONE_RADIUS)
                .setExpirationDuration(DebugResource.TEST_ONE_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(DebugResource.TEST_TWO)
                .setCircularRegion(
                        DebugResource.TEST_TWO_LATLNG.latitude,
                        DebugResource.TEST_TWO_LATLNG.longitude,
                        DebugResource.TEST_TWO_RADIUS)
                .setExpirationDuration(DebugResource.TEST_TWO_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(DebugResource.TEST_THREE)
                .setCircularRegion(
                        DebugResource.TEST_THREE_LATLNG.latitude,
                        DebugResource.TEST_THREE_LATLNG.longitude,
                        DebugResource.TEST_THREE_RADIUS)
                .setExpirationDuration(DebugResource.TEST_THREE_LIFETIME)
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