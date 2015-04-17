package com.yiweigao.campustours;

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yiweigao on 4/17/15.
 */

public class DownloadManager {

    private static final String BASE_URL = "http://dutch.mathcs.emory.edu:8009/";
    private String finalURL;

    public DownloadManager(Type type) {
        switch (type) {
            case POINTS:
                finalURL = BASE_URL + "points";
                break;
            case GEOFENCES:
                finalURL = BASE_URL + "geofences";
                break;
            case SCHOOLS:
                finalURL = BASE_URL + "schools";
                break;
        }
    }

    /**
     * Retrieves and returns the JSON from the API
     * @return JSONObject
     */
    public JSONObject getJSONObject() {
        InputStream is = null;
        String result = "";
        JSONObject jsonObject = null;

        // Download JSON data from URL
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(finalURL);
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

    /**
     * Type enum used to distinguish between API calls
     */
    public enum Type {POINTS, GEOFENCES, SCHOOLS}


}
