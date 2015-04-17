package com.yiweigao.campustours;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeScreenActivity extends ActionBarActivity {

    private List<String> listOfSchools;
    private String mSelectedCampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        listOfSchools = new ArrayList<>();
        mSelectedCampus = null;
        populateCampuses();
        activateButton();
    }

    private void activateButton() {
        final Button startTourButton = (Button) findViewById(R.id.StartButton);
        startTourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTourButton.setText("Please wait...");
                if (mSelectedCampus != null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a campus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateCampuses() {
        new DownloadSchoolsTask(this).execute();
    }

    private void createSpinner() {
        Spinner campusDropdown = (Spinner) findViewById(R.id.school_dropdown);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listOfSchools);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusDropdown.setAdapter(arrayAdapter);
        campusDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedCampus = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    /**
     * Download list of schools from REST API, and then populate spinner
     */
    private class DownloadSchoolsTask extends AsyncTask<String, Void, JSONObject> {

        Toast loadingToast;
        Context mContext;

        public DownloadSchoolsTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            loadingToast = Toast.makeText(mContext, "Loading campuses...", Toast.LENGTH_LONG);
            loadingToast.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return new DownloadManager(DownloadManager.Type.SCHOOLS).getJSONObject();
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
                    listOfSchools.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            loadingToast.cancel();
            createSpinner();
        }
    }
}