package com.yiweigao.campustours;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiweigao on 3/31/15.
 */

// manages all map activity (location, gps connection, map drawings, etc)
public class MapManager implements OnMapReadyCallback {

    // some constants
    private static final LatLng TOUR_START = new LatLng(33.789591, -84.326506);
    private static final String BASE_URL = "http://dutch.mathcs.emory.edu:8009/";

    // context for making toasts
    private Context mContext;
    
    // 
    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    
    private Toast loadingToast;

    private List<LatLng> mRouteCoordinates = new ArrayList<>();
//    private List<GeofenceObject> mGeofenceCoordinates = new ArrayList<>();

    public MapManager(Context context, MapFragment mapFragment) {
        this.mContext = context;
        this.mMapFragment = mapFragment;
    }

    public MapManager(Context context, GoogleMap googleMap) {
        mContext = context;
        mGoogleMap = googleMap;
        setInitialView();
        getRoute();
        getGeofences();
    }

    private void setInitialView() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TOUR_START, 18.0f));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(TOUR_START)
                .zoom(18.0f)    // 18.0f seems to show buildings...anything higher will not
                .bearing(55)    // 55 degrees makes us face the b jones center directly
                .tilt(15)       // 15 degrees seems ideal
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getRoute() {
        // executes a new AsyncTask to fetch coordinates from API
        new DownloadRouteTask().execute(BASE_URL + "points");
    }

    private void getGeofences() {
        new DownloadGeofencesTask().execute(BASE_URL + "geofences");
    }

    // draws route using stored coordinates
    private void drawRoute() {
        mGoogleMap.addPolyline(new PolylineOptions()
                .color(Color.argb(255, 0, 40, 120))     // emory blue, 100% opacity
//                .color(Color.argb(255, 210, 176, 0))    // emory "web light gold", 100% opacity
//                .color(Color.argb(255, 210, 142, 0))    // emory "web dark gold", 100% opacity
                .geodesic(true)
                .addAll(mRouteCoordinates));
    }

//    private void drawGeofences() {
//
//        for (GeofenceObject geofenceObject : mGeofenceCoordinates) {
//            mGoogleMap.addCircle(new CircleOptions()
//                    .center(geofenceObject.getCoordinates())
//                    .radius(geofenceObject.getRadius())
//                    .strokeWidth(5.0f));      // width in pixels, default = 10
//        }
//    }

    private void drawGeofences(List<GeofenceObject> listOfGeofenceObjects) {
        for (GeofenceObject geofenceObject : listOfGeofenceObjects) {
            mGoogleMap.addCircle(new CircleOptions()
                    .center(geofenceObject.getCoordinates())
                    .radius(geofenceObject.getRadius())
                    .strokeWidth(5.0f));      // width in pixels, default = 10
        }
    }

    public void updateCamera(Location newLocation) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(newLocation.getLatitude(), newLocation.getLongitude())));
    }

    public void getMap() {
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setInitialView();
        getRoute();
        getGeofences();
    }

    private class DownloadRouteTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            loadingToast = Toast.makeText(mContext, "Loading", Toast.LENGTH_LONG);
            loadingToast.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            InputStream is = null;
            String result = "";
            JSONObject jsonObject = null;

            // Download JSON data from URL
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urls[0]);
                String user = "";
                String pwd = "secret";
                httpGet.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pwd).getBytes(), Base64.NO_WRAP));

                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            // Convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            try {

                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            return jsonObject;
        }

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
                    String lat = point.getString("lat");
                    String lng = point.getString("lng");

                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    mRouteCoordinates.add(latLng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            loadingToast.cancel();
            drawRoute();
        }
    }

    /**
     * AsyncTask that fetches latitudes, longitudes, and radii from our REST API,
     * then converts them into GeofenceObjects, which contain LatLng and radius properties.
     * These GeofenceObjects are then stored into a member variable to be used for drawing.
     */
    private class DownloadGeofencesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            InputStream is = null;
            String result = "";
            JSONObject jsonObject = null;

            // Download JSON data from URL
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urls[0]);
                String user = "";
                String pwd = "secret";
                httpGet.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pwd).getBytes(), Base64.NO_WRAP));

                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            // Convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            try {

                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONArray resources = null;
            try {
                resources = jsonObject.getJSONArray("resources");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<GeofenceObject> listOfGeofenceObjects = new ArrayList<>();
            for (int i = 0; i < resources.length(); i++) {
                try {
                    JSONObject point = resources.getJSONObject(i);
                    String lat = point.getString("lat");
                    String lng = point.getString("lng");
                    String rad = point.getString("rad");

                    listOfGeofenceObjects.add(new GeofenceObject(lat, lng, rad));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            loadingToast.cancel();
            drawGeofences(listOfGeofenceObjects);
        }
    }
}
