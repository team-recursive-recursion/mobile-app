package recrec.golfcourseviewer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

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
        }

        // draw the hole
        if (hole != null) {
            hole.drawHole(getResources(), map);
        }

        // move the camera
        // TODO center on the position of something useful
        LatLng position = new LatLng(0.0, 0.0);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 1.0f));
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

    @Override
    public void receiveResponse(String resp, ApiRequest.RequestType type,
                                ApiRequest.RequestResult res) {
        if (res == ApiRequest.RequestResult.RES_SUCCESS) {
            if (type == ApiRequest.RequestType.REQ_GET_COURSES) {
                // TODO show a nice list to choose the course ID
                final String COURSE_ID = "ef6fcf01-351f-45bd-881e-6251f718960d";
                api.getPolygonsAsync(this, COURSE_ID);
            } else if (type == ApiRequest.RequestType.REQ_GET_POLYGONS) {
                // create the hole
                hole = holeFromResponse(resp);

                // load the map
                SupportMapFragment mapFragment = (SupportMapFragment)
                        getSupportFragmentManager().
                        findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } else {
                showError("Unknown type");
            }
        } else {
            showError("Response unsuccessful: " + resp);
        }
    }

    private void onServerSet(String url) {
        // attempt to fetch courses from the server
        api = new ApiRequest(this, url);
        api.getCoursesAsync(this);
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
        input.setText("192.168.8.103");
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

    private GolfHole holeFromResponse(String resp) {
        GolfHole hole = new GolfHole();
        // load polygons from the response
        for (int i = 0; i < 1; ++i) {
            // TODO don't hardcode here
            GolfPolygon poly = new GolfPolygon(
                    GolfPolygon.PolyType.TYPE_FAIRWAY);
            poly.addPoint(-10.0, -10.0);
            poly.addPoint(10.0, -10.0);
            poly.addPoint(10.0, 10.0);
            poly.addPoint(-10.0, 10.0);
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
