// activity displayed to the user that will allow them to select a school
public class SelectionScreen extends Activity {
    
    // intent that will be passed to the MapActivity to start it
    private Intent intent;

    // access database and load data for specified school
    protected void loadAssetsForSchool(int schoolID);

    // start map activity
    protected void startMapActivity();

}

// the activity that is responsible for displaying the map and controls
public class MapActivity extends Activity {
    // load map, find current location, draw assets on map, load controls
    @Override
    protected void onCreate();

    // build google api client
    protect buildAPIClient();
}

// the control panel 
public class ControlPanelFragment {
    // buttons
    private Button prevButton;
    private Button playButton;
    private Button nextButton;

    // media player
    MediaPlayer mMediaPlayer;

    // set up button actions and media player
    protected View onCreateView();
}

// intent service that will be monitoring the current user location
public class GeofenceTransitionsIntentService {
    // handle the intent based on what kind of message was passed
    protected void onHandleIntent(Intent intent);
}