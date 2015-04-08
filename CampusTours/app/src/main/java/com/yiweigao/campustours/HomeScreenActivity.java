package com.yiweigao.campustours;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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


public class HomeScreenActivity extends ActionBarActivity {

    ArrayList<String> mSchools;
    String mSelectedCampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        mSchools = new ArrayList<>();
        mSelectedCampus = null;
        populateCampuses();
        activateButton();
    }

    private void activateButton() {
        Button startTourButton = (Button) findViewById(R.id.StartButton);
        startTourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectedCampus != null){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please select a campus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateCampuses() {
        DataFetcher dataFetcher = new DataFetcher(this);
        dataFetcher.execute("http://dutch.mathcs.emory.edu:8009/schools");
    }

    private void createSpinner() {
        Spinner campusDropdown = (Spinner) findViewById(R.id.school_dropdown);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mSchools);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusDropdown.setAdapter(arrayAdapter);
        campusDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
                mSelectedCampus = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),mSelectedCampus,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected (AdapterView < ? > adapterView){

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
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
    private class DataFetcher extends AsyncTask<String, Void, JSONObject> {

        Toast loadingToast;
        Context mContext;

        public DataFetcher(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            loadingToast = Toast.makeText(mContext, "Loading campuses...", Toast.LENGTH_LONG);
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
                    JSONObject schools = resources.getJSONObject(i);
                    String name = schools.getString("name");
                    mSchools.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            loadingToast.cancel();
            createSpinner();
        }
    }
}
