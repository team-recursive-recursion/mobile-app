package recrec.golfcourseviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GolfHole hole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // create the example hole
        hole = new GolfHole();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // add the player marker
        LatLng position = new LatLng(0.0, 0.0);
        map.addMarker(new MarkerOptions().position(position).title("Player"));
        // draw the hole
        hole.drawHole(map);
        // move the camera
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

}
