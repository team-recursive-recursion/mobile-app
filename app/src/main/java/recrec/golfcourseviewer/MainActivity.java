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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int PERMISSION_REQUEST = 0;

    private GolfHole hole;
    private GoogleMap map;

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
        // add the player marker
        LatLng position = new LatLng(0.0, 0.0);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //map.addMarker(new MarkerOptions().position(position).title("Player"));
            // request the permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        } else {
            map.setMyLocationEnabled(true);
        }
        // draw the hole
        hole.drawHole(getResources(), map);
        // move the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 1.0f));
    }

    @Override
    public void onRequestPermissionsResult(int request, String perm[], int[] grant) {
        if (request == PERMISSION_REQUEST) {
            if (grant.length > 0 && grant[0] == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        }
    }

    private void onServerSet(String url) {
        // TODO attempt to fetch from the server
        // create the example hole
        hole = new GolfHole();

        // display the map
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*----------------------------------------------------------------------------------------------
     * showInputDialog
     *
     *     Displays an alert dialog with an input field that prompts the user for the URL of the
     *     dev server.
     */
    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter server URL:");

        final EditText input = new EditText(this);
        input.setText("localhost");
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

}
