package recrec.golfcourseviewer.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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

import java.util.List;

import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.Entity.GolfInfoPoint;
import recrec.golfcourseviewer.Entity.GolfPoint;
import recrec.golfcourseviewer.Fragments.GolfCourseListFragment;
import recrec.golfcourseviewer.Fragments.HolesListFragment;
import recrec.golfcourseviewer.Fragments.Map;
import recrec.golfcourseviewer.Entity.GolfHole;
import recrec.golfcourseviewer.Entity.GolfPolygon;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.ApiClientRF;
import recrec.golfcourseviewer.Requests.Response.Point;
import recrec.golfcourseviewer.Requests.Response.PolygonElement;
import recrec.golfcourseviewer.Requests.ServiceGenerator;
import recrec.golfcourseviewer.db.AppDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private final int PERMISSION_REQUEST = 0;

    private GoogleMap map;
    private GolfHole hole;
    private GolfInfoPoint point;


    private AppDatabase db;

    public CourseViewModel golfCourseListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(recrec.golfcourseviewer.R.layout.activity_main);

        golfCourseListViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        subscribe();
        // ask the user for a URL
        showInputDialog();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(recrec.golfcourseviewer.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setFragment("Map");


        hole = new GolfHole();
        point = new GolfInfoPoint();
    }

    private void subscribe()
    {
        //When Course is chosen
        golfCourseListViewModel.courseID.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                setFragment("HolesList");
            }
        });

        //When hole is chosen

        final OnMapReadyCallback mcb = this;
        golfCourseListViewModel.holeID.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                buildHoles();
                createCourse(hole);
                setFragment("Map");
                // Get Map ready
                Map activeMapFragment = (Map) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
                SupportMapFragment mapFragment = (SupportMapFragment)
                        activeMapFragment.getChildFragmentManager().
                                findFragmentById(R.id.map);
                mapFragment.getMapAsync(mcb);

            }
        });

        //When Hole call is finished
        golfCourseListViewModel.holeCallResponded.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean courseState = golfCourseListViewModel.courseCallResponded.getValue();
                if( courseState != null){
                    if( aBoolean && courseState){
                        if(map != null){
                            hole.drawHole(getResources(), map);
                        }
                    }
                }
            }
        });

        //When course call is finished
        golfCourseListViewModel.courseCallResponded.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean holeState = golfCourseListViewModel.holeCallResponded.getValue();
                if(holeState != null){
                    if( aBoolean && holeState){
                        if(map != null){
                            hole.drawHole(getResources(), map);
                        }
                    }
                }
            }
        });

        //point call is finished
        golfCourseListViewModel.pointCallResponded.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean holeState = golfCourseListViewModel.pointCallResponded.getValue();
                if(holeState != null){
                    if( aBoolean && holeState){
                        if(map != null){
                            point.drawInfoPoint(getResources(), map);
                        }
                    }
                }
            }
        });


        //When hole polygons are retrieved it is collected in the ArrayList
        golfCourseListViewModel.holesPolygons.observe(this, new Observer<List<PolygonElement>>() {
            @Override
            public void onChanged(@Nullable List<PolygonElement> polygonElements) {
//                holeElements.addAll(polygonElements);
            }
        });
    }
//    ArrayList<PolygonElement> holeElements = new ArrayList<>();
//    private void buildHoles(){
//        holeHash = new HashMap<>();
//        ArrayList<Hole> holes = new ArrayList<>(golfCourseListViewModel.holes.getValue());
//        ApiClientRF client = ServiceGenerator.getService();
//
//        for(final Hole c : holes){
//
//        }
//    }


    private void createCourse(final GolfHole course)
    {
        ApiClientRF client = ServiceGenerator.getService();
        Call<List<PolygonElement>> callCourse = client
                .getCourseElementsById(golfCourseListViewModel.courseID.getValue());

        callCourse.enqueue(new Callback<List<PolygonElement>>() {
            @Override
            public void onResponse(Call<List<PolygonElement>> call, @NonNull Response<List<PolygonElement>> response) {
                if(response.body() != null){
                    for(PolygonElement poly : response.body()){
                        try {
                            holeFromResponse(poly, course);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                golfCourseListViewModel.courseCallResponded.setValue(true);
            }

            @Override
            public void onFailure(Call<List<PolygonElement>> call, Throwable t) {
                Log.d("Course Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.courseCallResponded.setValue(false);
            }
        });

        Call<List<PolygonElement>> callHole = client.getHoleElementsById(golfCourseListViewModel.holeID.getValue());
        callHole.enqueue(new Callback<List<PolygonElement>>() {
            @Override
            public void onResponse(Call<List<PolygonElement>> call, @NonNull Response<List<PolygonElement>> response) {
                if(response.body() != null){
                    course.resetHolePolygons();
                    for(PolygonElement poly : response.body()){
                        try {
                            golfCourseListViewModel.holesPolygons.setValue(response.body());
                            holeFromResponse(poly, course);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    golfCourseListViewModel.holeCallResponded.setValue(true);
                    centerOnHole(golfCourseListViewModel.holeID.getValue());
                }

            }

            @Override
            public void onFailure(Call<List<PolygonElement>> call, Throwable t) {
                Log.d("Hole Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.holeCallResponded.setValue(false);
            }
        });

        Call<List<Point>> callPoint = client.getPointElementsById(golfCourseListViewModel
                .holeID.getValue());
        callPoint.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(Call<List<Point>> call, @NonNull Response<List<Point>> response) {
                point.resetMultiPoints();
                for(Point poly : response.body()){
                    try {

                        pointFromResponse(poly, course);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                golfCourseListViewModel.pointCallResponded.setValue(true);
            }

            @Override
            public void onFailure(Call<List<Point>> call, Throwable t) {
                Log.d("Point Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.pointCallResponded.setValue(false);
            }
        });

    }

    private void pointFromResponse(Point resp, GolfHole hole) throws Exception {
        if (point == null){
            point = new GolfInfoPoint();
        }

        String geoJson = resp.getGeoJson();
        JSONObject geoJsonObject = new JSONObject(geoJson);

        JSONArray coords = geoJsonObject.getJSONArray("coordinates");


        double val1 = coords.getDouble(0);
        double val2 = coords.getDouble(1);

        GolfPoint newPoint = new GolfPoint(val1,val2);
        newPoint.setInfo(resp.getInfo());

        switch(resp.getPointType()){
            case 0: newPoint.setType("Pin");
            break;
            case 1: newPoint.setType("Hole");
            break;
            case 2: newPoint.setType("Tee");
            break;
        }

        point.addPoint(newPoint);


    }

    private void holeFromResponse(PolygonElement resp, GolfHole hole) throws Exception
    {
        if(hole == null){
            hole = new GolfHole();
        }
        //Determine type
        GolfPolygon.PolyType type;
        switch (resp.getPolygonType()) {
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
        String geoJson = resp.getGeoJson();
        JSONObject geoJsonObject = new JSONObject(geoJson);

        JSONArray coords = geoJsonObject.getJSONArray("coordinates").getJSONArray(0);
        for (int j = 0; j < coords.length(); ++j) {
            JSONArray pair = coords.getJSONArray(j);
            double lat = pair.getDouble(1);
            double lon = pair.getDouble(0);
            poly.addPoint(lat, lon);
        }
        String id = resp.getHoleId();
        if(id == null){
            hole.addCoursePolygon(poly);
        }else{
            hole.addHolePolygon(poly);
        }

    }

    boolean mapReady = false;

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
        // draw the hole
        mapReady = true;

        //map.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int request, String perm[],
                                           int[] grant) {
        if (request == PERMISSION_REQUEST) {
            if (grant.length > 0 && grant[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                try {
                    map.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    // this exception shouldn't be able to happen here
                }
            }
        }
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

    private void centerOnHole(String holeId){
        if (map != null) {
            try{
                List<PolygonElement> holes = golfCourseListViewModel.holesPolygons.getValue();
                String holeGeoJson = "";
                for(PolygonElement h : holes){
                    if(h.getHoleId().equals(holeId)){
                        holeGeoJson = h.getGeoJson();
                        break;
                    }
                }
                if(!holeGeoJson.equals("")){
                    JSONObject geoJsonObject = new JSONObject(holeGeoJson);
                    JSONArray coords = geoJsonObject.getJSONArray("coordinates").getJSONArray(0).getJSONArray(0);
                    double lat = coords.getDouble(1);
                    double lon = coords.getDouble(0);
                    LatLng coordsLL = new LatLng(lat, lon);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordsLL, 17f));
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
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
//                onServerSet(input.getText().toString());
                String baseUrl = "http://"+input.getText().toString() + ":5001/";
                ServiceGenerator.setBaseUrl(baseUrl );

            }
        });

        builder.show();
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
                    setFragment("CourseList");
                    return true;
                case recrec.golfcourseviewer.R.id.navigation_map:
                    Log.d("ITEM","MAP");
                    setFragment("Map");
                    return true;

            }
            return false;
        }
    };

    private GolfCourseListFragment courseListFrag;
    private Map mapFrag;
    private HolesListFragment holesListFragment;
    String prevFrag = "";

    public void setFragment(String fragName){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragName == "Map"){
            if(mapFrag == null) {
                mapFrag = new Map();
                ft.add(recrec.golfcourseviewer.R.id.fragment_container, mapFrag,
                        "Map");
            }
            if(prevFrag.equals("HolesList")){
                ft.detach(getSupportFragmentManager().findFragmentByTag("Hole"));
//                ft.addToBackStack(null);
            }
            if(courseListFrag != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("List"));
            }
            ft.attach(mapFrag);
            ft.commitNow();

        }
        if(fragName == "CourseList"){
            if(courseListFrag == null){
                courseListFrag = new GolfCourseListFragment();
                ft.add(R.id.fragment_container,courseListFrag,"List");
            }
            if(mapFrag != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("Map"));
            }
            ft.attach(courseListFrag);
            ft.commit();
        }
        if(fragName == "HolesList"){
            if(holesListFragment == null){
                holesListFragment = new HolesListFragment();
                ft.add(R.id.fragment_container, holesListFragment, "Hole");
            }
            if(courseListFrag != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("List"));
            }
            ft.attach(holesListFragment);
            ft.commit();
        }
        prevFrag = fragName;
    }
    private void createDb() {
        db = AppDatabase.getInMemoryDatabase(getApplication());
    }

/*+++++++++++++++++++++++++++++++++++++++++++++
*   populateDbFromResponse(String resp)
*
*       Takes a Jason string from response and populate a Room Database
* ++++++++++++++++++++++++++++++++++++++++++++++*/
//    private void populateDbFromResponse(String resp) throws Exception{
//        JSONArray courses = new JSONArray(resp);
//        for(int i = 0; i< courses.length(); i++){
//            JSONObject jcourse = courses.getJSONObject(i);
//            GolfCourseModel gcm = new GolfCourseModel();
//            gcm.CourseName = jcourse.getString("courseName");
//            gcm.CourseId = jcourse.getString("courseId");
//            db.golfCourseModel().insertGolfCourse(gcm);
//        }
//    }

}
