package recrec.golfcourseviewer.Entity;

import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

import recrec.golfcourseviewer.Activity.MainActivity;


public class GolfInfoPoint {


    private String info;
    private double lat;
    private double lon;

    public void resetMultiPoints() {
        this.holePoints = new LinkedList<>();
    }

    private LinkedList<GolfPoint> holePoints;
    private LinkedList<Marker> markerList;
    private LinkedList<GolfPoint> courseMarker;

    public GolfInfoPoint(){
        holePoints = new LinkedList<>();
        markerList = new LinkedList<>();
        courseMarker = new LinkedList<>();
    }


    public void setInfo(String iinfo){
        info = iinfo;
    }

    public void addHolePoint(GolfPoint point) {
        holePoints.add(point);
    }

    public void addCoursePoint(GolfPoint point) {
        courseMarker.add(point);
    }


    public void drawInfoPoint(Resources res, GoogleMap map) {
        for (GolfPoint point : holePoints) {
            LatLng latlonObj = new LatLng(point.getLongitude(), point.getLatitude());
            MarkerOptions opt = new MarkerOptions();
            opt.position(latlonObj);
            opt.title(point.getType());
            opt.snippet(point.getInfo());
            markerList.add(map.addMarker(opt));
            createClickPointListener(map);
        }
        for (GolfPoint point : courseMarker){
            LatLng latlonObj = new LatLng(point.getLongitude(), point.getLatitude());
            MarkerOptions opt = new MarkerOptions();

            opt.position(latlonObj);
            opt.title(point.getType());
            opt.snippet(point.getInfo());

            map.addMarker(opt);
            createClickPointListener(map);
        }
    }


    private void createClickPointListener(GoogleMap map){
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Location loc1 = new Location("");
                loc1.setLatitude(marker.getPosition().latitude);
                loc1.setLongitude(marker.getPosition().longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(MainActivity.playerLat);
                loc2.setLongitude(MainActivity.playerLon);
                String title = marker.getTitle();
                int pos = title.indexOf(' ');
                if(pos != -1){
                    title = title.substring(0, title.indexOf(' '));
                }
                marker.setTitle(title+" ("
                        +Integer.toString((int)loc1.distanceTo(loc2))+"m)");

                marker.showInfoWindow();
                return true;
            }
        });
    }
}
