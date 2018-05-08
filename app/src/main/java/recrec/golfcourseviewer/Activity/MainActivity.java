package recrec.golfcourseviewer.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import recrec.golfcourseviewer.Fragments.GolfCourseListFragment;
import recrec.golfcourseviewer.Fragments.Map;
import recrec.golfcourseviewer.Entity.GolfHole;
import recrec.golfcourseviewer.Entity.GolfPolygon;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.ApiCallback;
import recrec.golfcourseviewer.Requests.ApiRequest;
import recrec.golfcourseviewer.db.AppDatabase;
import recrec.golfcourseviewer.db.Models.GolfCourseModel;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, ApiCallback {

    private final int PERMISSION_REQUEST = 0;

    private GoogleMap map;
    private ApiRequest api;
    private GolfHole hole;

    private GolfCourseListFragment courseListFrag;
    private Map mapFrag;
    private AppDatabase db;

    private String responce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(recrec.golfcourseviewer.R.layout.activity_main);

        // ask the user for a URL
        showInputDialog();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(recrec.golfcourseviewer.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setFragment("Map");

        createDb();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // attempt to request the player location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // request the permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        } else {
            map.setMyLocationEnabled(true);
//            centerOnPlayer();
        }
        try {
            centerOnCourse(responce);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // draw the hole
        if (hole != null) {
            hole.drawHole(getResources(), map);
        }
    }

    @Override
    public void onRequestPermissionsResult(int request, String perm[],
                                           int[] grant) {
        if (request == PERMISSION_REQUEST) {
            if (grant.length > 0 && grant[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                try {
                    map.setMyLocationEnabled(true);
//                    centerOnPlayer();
                } catch (SecurityException e) {
                    // this exception shouldn't be able to happen here
                }
            }
        }
    }

    @Override
    public void receiveResponse(final String resp, final ApiRequest.RequestType type,
                                final ApiRequest.RequestResult res) {
        // run the response on the main thread
        final OnMapReadyCallback mcb = this;
        final ApiCallback cb = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (res == ApiRequest.RequestResult.RES_SUCCESS) {

                    if (type == ApiRequest.RequestType.REQ_GET_COURSES) {
                        try {
                            populateDbFromResponse(resp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (type == ApiRequest.RequestType.REQ_GET_POLYGONS) {
                        // create the hole
                        try {

                            hole = holeFromResponse(resp);
                            responce = resp;
                        } catch (Exception e) {
                            // TODO improve errors and ask for another course
                            hole = null;
                            showError(e.getMessage());
                        }
                        // load the map
                        Map activeMapFragment = (Map) getSupportFragmentManager()
                                .findFragmentById(recrec.golfcourseviewer.R.id.fragment_container);
                        SupportMapFragment mapFragment = (SupportMapFragment)
                                activeMapFragment.getChildFragmentManager().
                                        findFragmentById(recrec.golfcourseviewer.R.id.map);
                        mapFragment.getMapAsync(mcb);

                    }
                } else {
                    showError("Response unsuccessful: " + resp);
                }
            }
        });

    }

    public void getPolygonsFromAdapter(final String courseId, final MainActivity mainActivity){
        Log.d("DO NOT DO THIS IN R L", courseId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                api.getPolygonsAsync(mainActivity, courseId);

            }
        });

        setFragment("Map");
    }

    private void onServerSet(String url) {
        // attempt to fetch courses from the server
        api = new ApiRequest(this, url);
        api.getCoursesAsync(this);
    }

    private void centerOnPlayer() throws SecurityException {
        if (map != null) {
            LocationManager locationManager = (LocationManager)getSystemService
                    (Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            if (location != null)
            {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(17)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    private void centerOnCourse(String resp) throws JSONException {
        if (map != null) {
            JSONArray jObjects = new JSONArray(resp);
            JSONObject jObj = jObjects.getJSONObject(0);
            JSONArray coords = jObj.getJSONObject("polygon")
                    .getJSONArray("coordinates")
                    .getJSONArray(0);
            LatLng coordsLL = new LatLng(coords.getJSONArray(0).getDouble(1), coords.getJSONArray(0).getDouble(0));

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordsLL, 15f));

        }
    }
    /*--------------------------------------------------------------------------
     * showInputDialog()
     *
     *     Displays an alert dialog with an input field that prompts the user
     *     for the URL of the dev server.
     *------------------------------------------------------------------------*/
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter server URL:");

        final EditText input = new EditText(this);
        input.setText("10.0.2.2");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);

        // set up the ok button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onServerSet(input.getText().toString());
            }
        });

        builder.show();
    }

    private GolfHole holeFromResponse(String resp) throws Exception {
        GolfHole hole = new GolfHole();
        JSONArray jObjects = new JSONArray(resp);
        // load polygons and points from the response
        for (int i = 0; i < jObjects.length(); ++i) {
            // TODO extend to show points (such as flag, tee, etc.)
            JSONObject jObj = jObjects.getJSONObject(i);

            // determine the type
            GolfPolygon.PolyType type;
            switch (jObj.getInt("type")) {
                case 0:
                    type = GolfPolygon.PolyType.TYPE_ROUGH;
                    break;
                case 1:
                    type = GolfPolygon.PolyType.TYPE_FAIRWAY;
                    break;
                case 2:
                    type = GolfPolygon.PolyType.TYPE_GREEN;
                    break;
                case 3:
                    type = GolfPolygon.PolyType.TYPE_BUNKER;
                    break;
                case 4:
                    type = GolfPolygon.PolyType.TYPE_WATER;
                    break;
                default:
                    throw new Exception("The hole contains an invalid polygon type");
            }

            // create the polygon
            GolfPolygon poly = new GolfPolygon(type);
            JSONArray coords = jObj.getJSONObject("polygon")
                    .getJSONArray("coordinates")
                    .getJSONArray(0);
            for (int j = 0; j < coords.length(); ++j) {
                JSONArray pair = coords.getJSONArray(j);
                double lat = pair.getDouble(1);
                double lon = pair.getDouble(0);
                poly.addPoint(lat, lon);
            }

            hole.addPolygon(poly);
        }
        return hole;
    }

    private void showError(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showError(String msg) {
        showError("Error", msg);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case recrec.golfcourseviewer.R.id.navigation_list:
                    Log.d("ITEM", "List");
                    setFragment("GolfCourseListFragment");
                    return true;
                case recrec.golfcourseviewer.R.id.navigation_map:
                    Log.d("ITEM","MAP");
                    setFragment("Map");
                    return true;

            }
            return false;
        }
    };


    public void setFragment(String fragName){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragName == "Map"){
            if(mapFrag == null) {
                mapFrag = new Map();
                ft.add(recrec.golfcourseviewer.R.id.fragment_container, mapFrag,
                        "Map");
            }
            if(courseListFrag != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("List"));
            }
            ft.attach(mapFrag);
            ft.commit();

        }
        if(fragName == "GolfCourseListFragment"){
            if(courseListFrag == null){
                courseListFrag = new GolfCourseListFragment();
                //fixme: Fix this
                courseListFrag.mainActivity = this;
                ft.add(R.id.fragment_container,courseListFrag,"List");
            }
            if(mapFrag != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("Map"));
            }
            ft.attach(courseListFrag);
            ft.commit();
        }
    }
    private void createDb() {
        db = AppDatabase.getInMemoryDatabase(getApplication());
    }

/*+++++++++++++++++++++++++++++++++++++++++++++
*   populateDbFromResponse(String resp)
*
*       Takes a Jason string from response and populate a Room Database
* ++++++++++++++++++++++++++++++++++++++++++++++*/
    private void populateDbFromResponse(String resp) throws Exception{
        JSONArray courses = new JSONArray(resp);
        for(int i = 0; i< courses.length(); i++){
            JSONObject jcourse = courses.getJSONObject(i);
            GolfCourseModel gcm = new GolfCourseModel();
            gcm.CourseName = jcourse.getString("courseName");
            gcm.CourseId = jcourse.getString("courseId");
            db.golfCourseModel().insertGolfCourse(gcm);
        }
    }

}
