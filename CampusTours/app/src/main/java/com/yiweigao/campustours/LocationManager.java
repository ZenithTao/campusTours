package com.yiweigao.campustours;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiweigao on 4/17/15.
 */

public class LocationManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>,
        LocationListener {

    // constants
    private static final int LOCATION_REQUEST_INTERVAL = 10000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 5000;
    private static final int GEOFENCE_LIFETIME = 100000;

    // variables
    private MapManager mMapManager;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent = null;
    private List<Geofence> listOfGeofences = new ArrayList<>();

    public LocationManager(Context context, MapManager mapManager) {
        mContext = context;
        mMapManager = mapManager;

        new DownloadGeofencesTask().execute();
    }

    /**
     * Initializes GoogleApiClient and attempts to connect
     */
    protected void initializeGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Once the GoogleAPIClient is connected, we add geofences, prepare the pending intent,
     * and start updating our location
     *
     * @param bundle No clue what this is actually...not using it here,
     *               but I would like to know what this is
     */
    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);

        startLocationUpdates();
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
        builder.addGeofences(listOfGeofences);

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
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, getLocationRequest(), this);
    }

    /**
     * Creates and returns a location request
     * @return LocationRequest
     */
    private LocationRequest getLocationRequest() {
        return new LocationRequest()
                .setInterval(LOCATION_REQUEST_INTERVAL)
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        mMapManager.updateView(newLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }

    /**
     * AsyncTask that fetches latitudes, longitudes, and radii from our REST API.
     * Then GoogleAPIClient.connect() is called
     */
    private class DownloadGeofencesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            return new DownloadManager(DownloadManager.Type.GEOFENCES).getJSONObject();
        }

        /**
         * Converts jsonObject to Geofence, adds it to listOfGeofences,
         * cancels the "loading" toast, and then tells the Google API client to connect
         *
         * @param jsonObject The jsonObject that is returned from the REST API
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONArray resources = null;
            try {
                resources = jsonObject.getJSONArray("resources");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < resources.length(); i++) {
                try {
                    JSONObject point = resources.getJSONObject(i);
                    String id = point.getString("id");
                    String lat = point.getString("lat");
                    String lng = point.getString("lng");
                    String rad = point.getString("rad");

                    listOfGeofences.add(new Geofence.Builder()
                            .setRequestId(id)
                            .setCircularRegion(
                                    Double.parseDouble(lat),
                                    Double.parseDouble(lng),
                                    Float.parseFloat(rad))
                            .setExpirationDuration(GEOFENCE_LIFETIME)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            .build());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            initializeGoogleApiClient();
        }
    }
}
