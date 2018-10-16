package recrec.golfcourseviewer.Activity;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import recrec.golfcourseviewer.Entity.CourseViewModel;
import recrec.golfcourseviewer.Entity.ElementDrawer;
import recrec.golfcourseviewer.Fragments.GolfCourseListFragment;
import recrec.golfcourseviewer.Fragments.HolesListFragment;
import recrec.golfcourseviewer.Fragments.Map;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.ApiClientRF;
import recrec.golfcourseviewer.Requests.EchoWebSocketListener;
import recrec.golfcourseviewer.Requests.Response.Element;
import recrec.golfcourseviewer.Requests.Response.Zone;
import recrec.golfcourseviewer.Requests.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private final int PERMISSION_REQUEST = 0;

    private GoogleMap map;

    private String currHoleId;

    private OkHttpClient client;
    private String hostAddress;

    public CourseViewModel golfCourseListViewModel;
    SharedPreferences sharedPreferences;

    public static double playerLat;
    public static double playerLon;
    ElementDrawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(recrec.golfcourseviewer.R.layout.activity_main);
        client = new OkHttpClient();
        golfCourseListViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        drawer = new ElementDrawer(golfCourseListViewModel);
        subscribe();
        // ask the user for a URL

        BottomNavigationView navigation = findViewById(recrec.golfcourseviewer.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setFragment("Map");

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        showInputDialog();


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
        golfCourseListViewModel.
                holeID.observe(this, new Observer<String>() {

            @Override
            public void onChanged(@Nullable String s) {
                centerOnHole(golfCourseListViewModel.holeID.getValue());
                createCourse();
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
                    } else {
                        mFusedLocationClient.requestLocationUpdates
                                (mLocationRequest, locationCallback, t.getLooper());

                    }
                }
                centerOnHole(golfCourseListViewModel.holeID.getValue());
            }
        });


        golfCourseListViewModel.zoneList.observe(this, new Observer<List<Zone>>() {
            @Override
            public void onChanged(@Nullable List<Zone> zones) {
                Log.d("Zone", Integer.toString(zones.size()));

                drawer.addZoneCollection(zones);
                try {
                    drawer.drawElements(getResources(), map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        golfCourseListViewModel.courseZone.observe(this, new Observer<Zone>() {
            @Override
            public void onChanged(@Nullable Zone zone) {
                drawer.addZone(zone);
            }
        });

    }

    private void createCourse() {
        final ArrayList<Zone> fullHoleList = new ArrayList<>();
        final ApiClientRF client = ServiceGenerator.getService();

        List<Zone> innerZoneList = golfCourseListViewModel.holes.getValue();
        final int numHoles = innerZoneList.size();
        final int count[] = new int[1];
        count[0] = 0;
        centerOnHole(golfCourseListViewModel.holeID.getValue());
        for (final Zone hole : innerZoneList) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Call<Zone> call = client.getZones(hole.getZoneID());
                    call.enqueue(new Callback<Zone>() {
                        @Override
                        public void onResponse(Call<Zone> call, Response<Zone> response) {
                            fullHoleList.add(response.body());
                            count[0]++;
                            if (count[0] == numHoles) {
                                golfCourseListViewModel.zoneList.setValue(fullHoleList);
                            }
                        }
                        @Override
                        public void onFailure(Call<Zone> call, Throwable t) {
                            count[0]++;
                        }
                    });
                }
            }, 1500);
            centerOnHole(golfCourseListViewModel.holeID.getValue());
        }


    }

    public enum POINTTYPE {
        PIN(0),
        HOLE(1),
        TEE(2);

        private final int type;

        POINTTYPE(int type) {
            this.type = type;
        }

        int getType() {
            return type;
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
        //  centerOnHole(currHoleId);
         //   centerOnPlayer();
        }

//         Location handling and sending to web socket.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        t = new HandlerThread("myHandlerThread");
        t.start();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback, t.getLooper());

        golfCourseListViewModel.zoneList.observe(this, new Observer<List<Zone>>() {
            @Override
            public void onChanged(@Nullable List<Zone> zones) {
                centerOnHole(golfCourseListViewModel.holeID.getValue());
            }
        });
        //centerOnHole(golfCourseListViewModel.holeID.getValue());

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            playerLat = locationResult.getLastLocation().getLatitude();
            playerLon = locationResult.getLastLocation().getLongitude();

            try {
                //Send player location over web socket
                JSONObject obj = new JSONObject().put("Location",
                        new JSONObject().put("type", "Point")
                                .put("coordinates", new JSONArray
                                        ("[" + Double.toString(playerLon) + "," +
                                                "" + Double.toString(playerLat)
                                                + "]")).toString());
                String id = sharedPreferences.getString("userId", null);
                if (id != null) {
                    obj = obj.put("UserID", id);
                }
                String toSend = obj.toString();
                Log.d("web", "Sending: " + toSend);
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

    public void setDistanceToHole(final Location player) {
        List<Zone> holeList = golfCourseListViewModel.zoneList.getValue();
        String holeId = golfCourseListViewModel.holeID.getValue();
        Zone currentHole = null;
        if (holeList != null) {
            for (Zone z : holeList) {
                if (z.getZoneID().equals(holeId)) {
                    currentHole = z;
                    break;
                }
            }
        }
        if (currentHole != null) {
            String geoJSON = "";
            for (Element el : currentHole.getElements()) {
                //The element type of 1 means it is a point
                if (el.getElementType() == 1 && el.getClassType() == POINTTYPE
                        .HOLE.getType()) {
                    geoJSON = el.getGeoJson();
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
        final double distance = (holeLoc == null || player == null) ?
                0 : player.distanceTo(holeLoc);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                golfCourseListViewModel.distanceToHole.setValue(distance);
                golfCourseListViewModel.playerCurLoc.setValue(player);
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
            LocationManager locationManager = (LocationManager) getSystemService
                    (Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = null;
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
            }

            playerLat = (location == null) ? 0 : location.getLatitude();
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
            if (cameraPosition != null) {
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        }
    }

    private void centerOnHole(String holeId) {
        if (map != null) {
            try {
                List<Zone> holes = golfCourseListViewModel.zoneList.getValue();
                String holeGeoJson = "";
                if (holes.isEmpty()) return;
                for (Zone h : holes) {
                    if (h.getZoneID().equals(holeId) && h.getElements() != null) {
                        holeGeoJson = h.getElements().get(0).getGeoJson();
                        break;
                    }
                }
                if (!holeGeoJson.equals("")) {
                    JSONObject geoJsonObject = new JSONObject(holeGeoJson);
                    JSONArray coordinates = geoJsonObject
                            .getJSONArray("coordinates").getJSONArray(0)
                            .getJSONArray(0);
                    double lat = coordinates.getDouble(1);
                    double lon = coordinates.getDouble(0);
                    LatLng coordinatesLL = new LatLng(lat, lon);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinatesLL, 17f));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Location pl = golfCourseListViewModel.playerCurLoc.getValue();
            setDistanceToHole(pl);
        }
    }

    /*--------------------------------------------------------------------------
     * showInputDialog()
     *
     *     Displays an alert dialog with an input field that prompts the user
     *     for the URL of the dev server.
     *------------------------------------------------------------------------*/
    private void showInputDialog() {


        hostAddress = "ec2-18-191-152-232.us-east-2.compute.amazonaws.com";
        String baseUrl = "http://" + hostAddress;
        ServiceGenerator.setBaseUrl(baseUrl);
        wsStart();

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
                    Log.d("ITEM", "MAP");
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

    public void setFragment(String fragName) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragName.equals("Map")) {
            if (mapFrag == null) {
                mapFrag = new Map();
                ft.add(recrec.golfcourseviewer.R.id.fragment_container, mapFrag,
                        "Map");
            }
            if (prevFrag.equals("HolesList")) {
                ft.detach(getSupportFragmentManager().findFragmentByTag("Hole"));
//                ft.addToBackStack(null);
            }
            if (courseListFrag != null) {
                ft.detach(getSupportFragmentManager().findFragmentByTag("List"));
            }
            ft.attach(mapFrag);
            ft.commitNow();

        }
        if (fragName.equals("CourseList")) {
            if (courseListFrag == null) {
                courseListFrag = new GolfCourseListFragment();
                ft.add(R.id.fragment_container, courseListFrag, "List");
            }
            if (mapFrag != null) {
                ft.detach(getSupportFragmentManager().findFragmentByTag("Map"));
            }
            if (holesListFragment != null) {
                ft.detach(getSupportFragmentManager().findFragmentByTag("Hole"));
            }
            ft.attach(courseListFrag);
            ft.commit();
        }
        if (fragName.equals("HolesList")) {
            if (holesListFragment == null) {
                holesListFragment = new HolesListFragment();
                ft.add(R.id.fragment_container, holesListFragment, "Hole");
            }
            if (courseListFrag != null) {
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

        Request request = new Request.Builder().url("ws://" + hostAddress + "/ws").build();
        EchoWebSocketListener listener = new EchoWebSocketListener (this, golfCourseListViewModel);
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }


}
