package com.yiweigao.campustours;

import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final LatLng YIWEI_POS = new LatLng(40, -79);

    private static final String ASBURY_CIRCLE_NAME = "Asbury Circle";
    private static final LatLng ASBURY_CIRCLE = new LatLng(33.792731, -84.324075);
    private static final float ASBURY_CIRCLE_RADIUS = 20.0f;
    private static final int ASBURY_CIRCLE_LIFETIME = 100000;

    private static final String HOUSE_TEXT = "Fraternity house";
    private static final LatLng HOUSE = new LatLng(33.793766, -84.327198);
    private static final float HOUSE_RADIUS = 20.0f;
    private static final int HOUSE_LIFETIME = 100000;

    private static final String TOUR_START_NAME = "Tour Start";
    private static final LatLng TOUR_START = new LatLng(33.789591, -84.326506);

    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private ControlPanelFragment mControlPanelFragment;

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
            if (mMapFragment == null) {
                mMapFragment= (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                mMapFragment.getMapAsync(this);
            }
            if (mControlPanelFragment == null) {
                mControlPanelFragment = ((ControlPanelFragment) getFragmentManager().findFragmentById(R.id.control_panel));
            }



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
