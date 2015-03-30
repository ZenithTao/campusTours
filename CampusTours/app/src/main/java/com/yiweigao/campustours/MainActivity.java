package com.yiweigao.campustours;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        ResultCallback<Status> {

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
    private MapFragment mMapFragment;
    private ControlPanelFragment mControlPanelFragment;

    private GoogleApiClient mGoogleApiClient;
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
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TOUR_START, 18.0f));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(TOUR_START)
                .zoom(18.0f)    // 18.0f seems to show buildings...anything higher will not
                .bearing(55)    // 55 degrees makes us face the b jones center directly
                .tilt(15)       // 15 degrees seems ideal
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.addMarker(new MarkerOptions()
                .title("Asbury Cicle")
                .snippet("Location of WW!")
                .position(ASBURY_CIRCLE));

        googleMap.addMarker(new MarkerOptions()
                .title("Tour Start")
                .snippet("Start Here!")
                .position(TOUR_START));

        googleMap.addMarker(new MarkerOptions()
                .title("Chi Phi")
                .snippet("22 Eagle Row")
                .position(HOUSE));

        // tour route, assuming usual tour route, current count = 38
        List<LatLng> routeCoordinates = new ArrayList<>();
        routeCoordinates.add(new LatLng(33.789689, -84.326368)); // start
        routeCoordinates.add(new LatLng(33.789803, -84.326454)); // curve before stairs
        routeCoordinates.add(new LatLng(33.789933, -84.326457)); // between career center and MSC
        routeCoordinates.add(new LatLng(33.790457, -84.325608)); // between admin building and white hall
        routeCoordinates.add(new LatLng(33.789986, -84.325209)); // south quad corner of admin building
        routeCoordinates.add(new LatLng(33.790587, -84.324259)); // front of carlos museum
        routeCoordinates.add(new LatLng(33.791010, -84.323635)); // between corner of bowden and candler lib
        routeCoordinates.add(new LatLng(33.790951, -84.323530)); // between candler and bowden
        routeCoordinates.add(new LatLng(33.790935, -84.323407)); // between woodruff and candler, before bridge
        routeCoordinates.add(new LatLng(33.791302, -84.322868)); // between woodruff and hospital, red brick road
        routeCoordinates.add(new LatLng(33.791522, -84.323152)); // between candler and hospital
        routeCoordinates.add(new LatLng(33.791949, -84.323396)); // corner of cox and hospital
        routeCoordinates.add(new LatLng(33.792073, -84.323198)); // right before cox hall entrance
        routeCoordinates.add(new LatLng(33.792515, -84.323552)); // right after cox hall exit
        routeCoordinates.add(new LatLng(33.792771, -84.323595)); // in front of alabama
        routeCoordinates.add(new LatLng(33.793140, -84.323383)); // right before duc entrance
        routeCoordinates.add(new LatLng(33.793790, -84.323319)); // right after duc exit, stairs bottom
        routeCoordinates.add(new LatLng(33.793788, -84.323130)); // right after duc exit, stairs top
        routeCoordinates.add(new LatLng(33.793923, -84.322985)); // turman, right outside rear stairs
        routeCoordinates.add(new LatLng(33.793931, -84.322473)); // southeast corner of turman, alongside means drive
        routeCoordinates.add(new LatLng(33.794166, -84.322484)); // northeast corner of turman, alongside means drive
        routeCoordinates.add(new LatLng(33.794169, -84.323546)); // outside hamilton holmes, before the hill slopes down
        routeCoordinates.add(new LatLng(33.793858, -84.323927)); // southeast corner of mctyeire
        routeCoordinates.add(new LatLng(33.793857, -84.324195)); // southwest corner of mctyeire
        routeCoordinates.add(new LatLng(33.793602, -84.324375)); // north road of 'wpec curve' @ asbury cir
        routeCoordinates.add(new LatLng(33.793585, -84.325032)); // north of wpec entrace
        routeCoordinates.add(new LatLng(33.793464, -84.325048)); // directly in front of wpec entrance
        routeCoordinates.add(new LatLng(33.793305, -84.325016)); // south of wpec entrance
        routeCoordinates.add(new LatLng(33.793293, -84.324353)); // south road of 'wpec curve' @ asbury circle
        routeCoordinates.add(new LatLng(33.792982, -84.324256)); // directly in front of dobbs
        routeCoordinates.add(new LatLng(33.792462, -84.324097)); // dooley!
        routeCoordinates.add(new LatLng(33.792251, -84.324132)); // start of bridge south of anthro building
        routeCoordinates.add(new LatLng(33.791998, -84.324545)); // tull plaza
        routeCoordinates.add(new LatLng(33.791752, -84.324338)); // directly in front of callaway
        routeCoordinates.add(new LatLng(33.791479, -84.324823)); // south corner of modern languages
        routeCoordinates.add(new LatLng(33.791433, -84.325156)); // under canon chapel
        routeCoordinates.add(new LatLng(33.791126, -84.325553)); // between new theology lib, white hall, and canon chapel
        routeCoordinates.add(new LatLng(33.790679, -84.325521)); // east corner of white hall
//        routeCoordinates.add(new LatLng())

        googleMap.addPolyline(new PolylineOptions()
                        .color(Color.argb(255, 0, 40, 120))     // emory blue, 100% opacity
//                        .color(Color.argb(255, 210, 176, 0))    // emory "web light gold", 100% opacity
//                        .color(Color.argb(255, 210, 142, 0))    // emory "web dark gold", 100% opacity
                        .geodesic(true)
                        .addAll(routeCoordinates)
        );

        googleMap.addCircle(new CircleOptions()
                .center(HOUSE)
                .radius(HOUSE_RADIUS)
                .visible(true));

        googleMap.addCircle(new CircleOptions()
                .center(TEST)
                .radius(TEST_RADIUS)
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
                getGeofencePendingIntent()).setResultCallback(this);
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

    public void populateGeofenceList() {
        mGeofenceList = new ArrayList<>();
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
                .setRequestId(TEST_NAME)
                .setCircularRegion(
                        TEST.latitude,
                        TEST.longitude,
                        TEST_RADIUS)
                .setExpirationDuration(TEST_LIFETIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
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