package recrec.golfcourseviewer.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
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
import recrec.golfcourseviewer.Requests.EchoWebSocketListener;
import recrec.golfcourseviewer.Requests.Response.Point;
import recrec.golfcourseviewer.Requests.Response.PolygonElement;
import recrec.golfcourseviewer.Requests.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private final int PERMISSION_REQUEST = 0;

    private GoogleMap map;
    private GolfHole hole;
    private GolfInfoPoint point;

    private OkHttpClient client;
    private String hostAddress;

    public CourseViewModel golfCourseListViewModel;
    SharedPreferences sharedPreferences;

    public static double playerLat;
    public static double playerLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(recrec.golfcourseviewer.R.layout.activity_main);

        golfCourseListViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        subscribe();
        // ask the user for a URL
        showInputDialog();
        BottomNavigationView navigation = findViewById(recrec.golfcourseviewer.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setFragment("Map");

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        client = new OkHttpClient();

        hole = new GolfHole();
        point = new GolfInfoPoint();

    }


    private void subscribe() {
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
                createCourse(hole);
                setFragment("Map");
                // Get Map ready
                if (map == null) {
                    Map activeMapFragment = (Map) getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
                    SupportMapFragment mapFragment = (SupportMapFragment)
                            activeMapFragment.getChildFragmentManager().
                                    findFragmentById(R.id.map);
                    mapFragment.getMapAsync(mcb);
                }
                if (map != null) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions
                                ((Activity) getApplicationContext(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST);
                    }else{
                        mFusedLocationClient.requestLocationUpdates
                                (mLocationRequest, locationCallback, t.getLooper());
                    }
                }

            }
        });

        //When Hole call is finished
        golfCourseListViewModel.holeCallResponded.observe(this,
                new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean courseState = golfCourseListViewModel
                        .courseCallResponded.getValue();
                if(aBoolean != null && courseState != null){
                    if( aBoolean && courseState){
                        if(map != null){
                            hole.drawHole(getResources(), map);
                        }
                    }
                }
            }
        });

        //When course call is finished
        golfCourseListViewModel.courseCallResponded.observe(this,
                new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean holeState = golfCourseListViewModel
                        .holeCallResponded.getValue();
                if(aBoolean != null && holeState != null){
                    if( aBoolean && holeState){
                        if(map != null){
                            hole.drawHole(getResources(), map);
                        }
                    }
                }
            }
        });

        //Course Marker call
        golfCourseListViewModel.coursePointCallResponded.observe(this,
                new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean holeState = golfCourseListViewModel
                        .coursePointCallResponded.getValue();
                if(aBoolean != null && holeState != null){
                    if( aBoolean && holeState){
                        if(map != null){
                            point.drawInfoPoint(getResources(), map);
                        }
                    }
                }
            }
        });


        //point call is finished
        golfCourseListViewModel.pointCallResponded.observe(this,
                new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Boolean holeState = golfCourseListViewModel
                        .pointCallResponded.getValue();
                if(aBoolean != null && holeState != null){
                    if( aBoolean && holeState){
                        if(map != null){
                            point.drawInfoPoint(getResources(), map);
                        }
                    }
                }
            }
        });

    }

    private void createCourse(final GolfHole course)
    {
        ApiClientRF client = ServiceGenerator.getService();
        Call<List<PolygonElement>> callCourse = client
                .getCourseElementsById(golfCourseListViewModel.courseID.getValue());

        callCourse.enqueue(new Callback<List<PolygonElement>>() {
            @Override
            public void onResponse(@NonNull Call<List<PolygonElement>> call,
                                   @NonNull Response<List<PolygonElement>> response) {
                if(response.isSuccessful()){
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
            public void onFailure(@NonNull Call<List<PolygonElement>> call,
                                  @NonNull Throwable t) {
                Log.d("Course Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.courseCallResponded.setValue(false);
            }
        });

        Call<List<Point>> callCoursePoint = client.getCoursePointElementById(
                golfCourseListViewModel.courseID.getValue());
        callCoursePoint.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(@NonNull Call<List<Point>> call,
                                   @NonNull Response<List<Point>> response) {
                for(Point poly : response.body()){
                    try {
                        pointFromResponse(poly);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                golfCourseListViewModel.coursePointCallResponded.setValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<Point>> call,
                                  @NonNull Throwable t) {
                Log.d("Point Course Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.coursePointCallResponded.setValue(false);
            }
        });


        Call<List<PolygonElement>> callHole = client.getHoleElementsById(
                golfCourseListViewModel.holeID.getValue());
        callHole.enqueue(new Callback<List<PolygonElement>>() {
            @Override
            public void onResponse(@NonNull Call<List<PolygonElement>> call, @NonNull Response<List<PolygonElement>> response) {
                if(response.isSuccessful()){
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
            public void onFailure(@NonNull Call<List<PolygonElement>> call, @NonNull Throwable t) {
                Log.d("Hole Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.holeCallResponded.setValue(false);
            }
        });

        Call<List<Point>> callPoint = client.getPointElementsById(
                golfCourseListViewModel.holeID.getValue());
        callPoint.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(@NonNull Call<List<Point>> call,
                                   @NonNull Response<List<Point>> response) {
                for(Point poly : response.body()){
                    try {
                        pointFromResponse(poly);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                golfCourseListViewModel.pointsPolygons.setValue(response.body());
                golfCourseListViewModel.pointCallResponded.setValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<Point>> call, @NonNull Throwable t) {
                Log.d("Point Call", "Fail: " + t.getMessage());
                golfCourseListViewModel.pointCallResponded.setValue(false);
            }
        });


    }

    public enum POINTTYPE {
        PIN(0),
        HOLE(1),
        TEE(2);

        private final int type;
        POINTTYPE(int type){
            this.type = type;
        }
        int getType(){
            return type;
        }
    }

    private void pointFromResponse(Point resp) throws Exception {
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

        String id = resp.getHoleId();
        if (id == null){
            point.addCoursePoint(newPoint);
        }else{
            point.addHolePoint(newPoint);
        }
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

        JSONArray coords = geoJsonObject.getJSONArray("coordinates")
                .getJSONArray(0);
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

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    HandlerThread t;
    /*------------------------------------------------------------------------
    *   onMapReady(GoogleMap)
    *       This is the function that is called once the map is initialized.
    *       This wil only be called once the hole id is chosen since the
    *       getMapAsync(mcb) is only called on hole id change. See the subscribe
    *       function above.
    * -----------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // attempt to request the player location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // request the permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST);
        } else {
            map.setMyLocationEnabled(true);
            centerOnPlayer();
        }

//         Location handling and sending to web socket.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        t = new HandlerThread("myHandlerThread");
        t.start();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback, t.getLooper());

    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            playerLat = locationResult.getLastLocation().getLatitude();
            playerLon = locationResult.getLastLocation().getLongitude();

            try {
                JSONObject obj = new JSONObject().put("Location",
                        new JSONObject().put("type","Point")
                                .put("coordinates", new JSONArray
                                        ("["+Double.toString(playerLon)+"," +
                                                ""+Double.toString(playerLat)
                                                +"]")).toString());
                String id = sharedPreferences.getString("userId", null);
                if(id != null){
                    obj = obj.put("UserID", id);
                }
                String toSend = obj.toString();
                Log.d("web", "Sending: " +toSend);
                ws.send(toSend);

                Location playerLoc = new Location("");
                playerLoc.setLatitude(playerLat);
                playerLoc.setLongitude(playerLon);
                setDistanceToHole(playerLoc);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Location holeLoc = null;
    public void setDistanceToHole(Location player){
        List<Point> pointList = golfCourseListViewModel.pointsPolygons
                .getValue();
        String holeId = golfCourseListViewModel.holeID.getValue();
        String geoJSON = "";
        if(pointList != null && holeId != null){
            for(Point poly : pointList){
                if(poly.getPointType() == POINTTYPE.HOLE.getType() && poly
                        .getHoleId().equals(holeId)){
                    geoJSON = poly.getGeoJson();
                    break;
                }
            }
            try {
                JSONArray coordinates = new JSONObject(geoJSON)
                        .getJSONArray("coordinates");
                holeLoc = new Location("");
                holeLoc.setLongitude(coordinates.getDouble(0));
                holeLoc.setLatitude(coordinates.getDouble(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final double distance = (holeLoc == null )? 0: player.distanceTo(holeLoc);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                golfCourseListViewModel.distanceToHole.setValue(distance);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int request, @NonNull String perm[],
                                           @NonNull int[] grant) {
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
            Location location = null;
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
            }

            playerLat = (location == null) ? 0: location.getLatitude();
            playerLon = (location == null) ? 0 : location.getLongitude();


            if (location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        location.getLatitude(), location.getLongitude()), 13));
            }

            CameraPosition cameraPosition = null;
            if (location != null) {
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(17)
                        .build();
            }
            if(cameraPosition != null){
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        }
    }

    private void centerOnHole(String holeId){
        if (map != null) {
            try{
                List<PolygonElement> holes = golfCourseListViewModel.holesPolygons.getValue();
                String holeGeoJson = "";
                if(holes.isEmpty()) return;
                for(PolygonElement h : holes){
                    if(h.getHoleId().equals(holeId)){
                        holeGeoJson = h.getGeoJson();
                        break;
                    }
                }
                if(!holeGeoJson.equals("")){
                    JSONObject geoJsonObject = new JSONObject(holeGeoJson);
                    JSONArray coordinates = geoJsonObject
                            .getJSONArray("coordinates").getJSONArray(0)
                            .getJSONArray(0);
                    double lat = coordinates.getDouble(1);
                    double lon = coordinates.getDouble(0);
                    LatLng coordinatesLL = new LatLng(lat, lon);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinatesLL, 17f));
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
                hostAddress = input.getText().toString();
                String baseUrl = "http://"+ hostAddress + ":5001/";
                ServiceGenerator.setBaseUrl(baseUrl );
                wsStart();
            }
        });
        builder.show();
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
        if(fragName.equals("Map")){
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
        if(fragName.equals("CourseList")){
            if(courseListFrag == null){
                courseListFrag = new GolfCourseListFragment();
                ft.add(R.id.fragment_container,courseListFrag,"List");
            }
            if(mapFrag != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("Map"));
            }
            if(holesListFragment != null){
                ft.detach(getSupportFragmentManager().findFragmentByTag("Hole"));
            }
            ft.attach(courseListFrag);
            ft.commit();
        }
        if(fragName.equals("HolesList")){
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

/*  WebSocket things    */
    WebSocket ws;
    private void wsStart() {
        Request request = new Request.Builder().url("ws://"+hostAddress+":5001/ws").build();
        EchoWebSocketListener listener = new EchoWebSocketListener
                (this, golfCourseListViewModel);
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }


}
