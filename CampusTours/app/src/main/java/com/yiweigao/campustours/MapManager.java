package com.yiweigao.campustours;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by yiweigao on 3/31/15.
 */

public class MapManager {

    private static final LatLng TOUR_START = new LatLng(33.789591, -84.326506);

    Context mContext;
    GoogleMap mGoogleMap;

    List<LatLng> mRouteCoordinates = new ArrayList<>();

    public MapManager(Context context, GoogleMap googleMap) {
        mContext = context;
        mGoogleMap = googleMap;
        this.setInitialView();
        this.getRouteCoordinates();
    }

    public void setInitialView() {
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

    public void getRouteCoordinates() {
//        new JSONfunctions().execute("http://dutch.mathcs.emory.edu:8009/points");

        JSONObject jsonObject;

        try {
            jsonObject = new JSONfunctions().execute("http://dutch.mathcs.emory.edu:8009/points").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void drawRoute() {
        mGoogleMap.addPolyline(new PolylineOptions()
                        .color(Color.argb(255, 0, 40, 120))     // emory blue, 100% opacity
//                        .color(Color.argb(255, 210, 176, 0))    // emory "web light gold", 100% opacity
//                        .color(Color.argb(255, 210, 142, 0))    // emory "web dark gold", 100% opacity
                        .geodesic(true)
                        .addAll(mRouteCoordinates)
        );
        
        Log.d("---drawRoute()---", "just drew map");
    }

    class JSONfunctions extends AsyncTask<String, Void, JSONObject> {

        Toast loadingToast;

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
//                HttpPost httppost = new HttpPost(urls[0]);
                HttpGet httpGet = new HttpGet(urls[0]);
//                HttpResponse response = httpclient.execute(httppost);
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
                    Log.d("---asynctask---", "added to mRouteCoordinates");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            loadingToast.cancel();
            drawRoute();
        }
    }
}
