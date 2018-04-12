package recrec.golfcourseviewer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, ApiCallback {

    private final int PERMISSION_REQUEST = 0;

    private GoogleMap map;
    private ApiRequest api;
    private GolfHole hole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ask the user for a URL
        showInputDialog();
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
            centerOnPlayer();
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
                    centerOnPlayer();
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

                        // TODO show a nice list to choose the course ID
                        final String COURSE_ID = "ef6fcf01-351f-45bd-881e-6251f718960d";
                        api.getPolygonsAsync(cb, COURSE_ID);

                    } else if (type == ApiRequest.RequestType.REQ_GET_POLYGONS) {
                        // create the hole
                        try {
                            hole = holeFromResponse(resp);
                        } catch (Exception e) {
                            // TODO improve errors and ask for another course
                            hole = null;
                            showError(e.getMessage());
                        }
                        // load the map
                        SupportMapFragment mapFragment = (SupportMapFragment)
                                getSupportFragmentManager().
                                        findFragmentById(R.id.map);
                        mapFragment.getMapAsync(mcb);
                    }
                } else {
                    showError("Response unsuccessful: " + resp);
                }
            }
        });
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
        input.setText("192.168.8.100");
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

}
