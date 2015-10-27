package com.yiweigao.campustours;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiweigao on 3/31/15.
 */

// manages all map activity (location, gps connection, map drawings, etc)
public class MapManager implements
        OnMapReadyCallback {

    // some constants
    private static final LatLng TOUR_START = new LatLng(33.789591, -84.326506);

    // context for making toasts and generating intents
    private Context mContext;

    // map related
    private MapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    // add the building info
    //private List<Building> mBuildingList;

    public MapManager(Context context, MapFragment mapFragment) {
        mContext = context;
        mMapFragment = mapFragment;
        getMap();

        new LocationManager(mContext, this);
    }

    public void getMap() {
        mMapFragment.getMapAsync(this);
    }

    /**
     * When map is ready, set initial view, then load route and geofences
     *
     * @param googleMap Reference to the map on screen
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setInitialMapView();
        //getRoute();

    }

    /*
     * function to add the info to each building
     */
    private void addBuildingInfo(List<LatLng> listOfBuildingCoordinates, List<String> listOfBuildingDetails) {
        for (int i = 0; i < listOfBuildingCoordinates.size(); i++) {
            LatLng current = listOfBuildingCoordinates.get(i);
            String info = listOfBuildingDetails.get(i);

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(current))
                    .setSnippet(info);
        }
    }

    private void setInitialMapView() {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setBuildingsEnabled(true);
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
        new DownloadRouteTask().execute();
    }

    private void drawRoute(List<LatLng> listOfRouteCoordinates) {
        mGoogleMap.addPolyline(new PolylineOptions()
                .color(Color.argb(255, 0, 40, 120))     // emory blue, 100% opacity
//                .color(Color.argb(255, 210, 176, 0))    // emory "web light gold", 100% opacity
//                .color(Color.argb(255, 210, 142, 0))    // emory "web dark gold", 100% opacity
                .geodesic(true)
                .addAll(listOfRouteCoordinates));
    }

    public void updateMapView(Location newLocation) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(newLocation.getLatitude(), newLocation.getLongitude())));
    }

    /**
     * AsyncTask that fetches latitude and longitude from our REST API,
     * converts them into LatLng objects, stores them in a list, and
     * passes this list to drawRoute() for drawing.
     */
    private class DownloadRouteTask extends AsyncTask<String, Void, JSONObject> {

        private Toast loadingToast;

        /**
         * shows a "loading" toast immediately prior to fetching data
         */
        @Override
        protected void onPreExecute() {
            loadingToast = Toast.makeText(mContext, "Loading", Toast.LENGTH_LONG);
            loadingToast.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return new DownloadManager(DownloadManager.Type.POINTS).getJSONObject();
        }

        /**
         * Converts jsonObject to LatLnt object, adds it to listOfRouteCoordinates,
         * then draws the route
         *
         * @param jsonObject The jsonObject that is returned from the REST API
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
//            JSONArray resources = null;
//            try {
//                resources = jsonObject.getJSONArray("resources");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            List<LatLng> listOfRouteCoordinates = new ArrayList<>();
//            for (int i = 0; i < resources.length(); i++) {
//                try {
//                    JSONObject point = resources.getJSONObject(i);
//                    String lat = point.getString("lat");
//                    String lng = point.getString("lng");
//
//                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//                    listOfRouteCoordinates.add(latLng);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            //drawRoute(listOfRouteCoordinates);

            // add the buildings' info
//            JSONArray buildings_coordinates = null;
//            JSONArray buildings_details = null;
//            try {
//                buildings_coordinates = jsonObject.getJSONArray("buildings_coordinates");
//                buildings_details = jsonObject.getJSONArray("buildings_details");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            List<LatLng> listOfBuildingCoordinates = new ArrayList<>();
//            List<String> listOfBuildingDetails = new ArrayList<>();
//            for (int i = 0; i < buildings_coordinates.length(); i++) {
//                try {
//                    JSONObject point = buildings_coordinates.getJSONObject(i);
//                    String lat = point.getString("lat");
//                    String lng = point.getString("lng");
//                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//                    listOfBuildingCoordinates.add(latLng);
//
//                    JSONObject detail = buildings_details.getJSONObject(i);
//                    String detail_info = detail.getString("info");
//                    listOfBuildingDetails.add(detail_info);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            addBuildingInfo(listOfBuildingCoordinates, listOfBuildingDetails);



            loadingToast.cancel();
        }
    }
}