package recrec.golfcourseviewer.Entity;

import android.content.Context;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

import recrec.golfcourseviewer.Activity.MainActivity;
import recrec.golfcourseviewer.Entity.GolfPoint;
import recrec.golfcourseviewer.db.Dao.GolfCourseDao;


public class GolfInfoPoint {


    private String info;
    private double lat;
    private double lon;

    public void resetMultiPoints() {
        this.multiPoints = new LinkedList<>();
    }

    private LinkedList<GolfPoint> multiPoints;
    private LinkedList<Marker> markerList;
    private LinkedList<GolfPoint> courseMarker;

    public GolfInfoPoint(){
        multiPoints = new LinkedList<>();
        markerList = new LinkedList<>();
        courseMarker = new LinkedList<>();
    }

    public void setLat(double llat){
        lat = llat;
    }

    public void setLon(double llon){
        lon = llon;
    }

    public void setInfo(String iinfo){
        info = iinfo;
    }

    public void addHolePoint(GolfPoint point) {
        multiPoints.add(point);
    }

    public void addCoursePoint(GolfPoint point) {
        courseMarker.add(point);
    }


    public void drawInfoPoint(Resources res, GoogleMap map) {
        for(Marker marker : markerList){
            marker.remove();
        }
        for (GolfPoint point : multiPoints) {
            LatLng latlonObj = new LatLng(point.getLongitude(), point.getLatitude());
            MarkerOptions opt = new MarkerOptions();
            double lat,lon;
            opt.position(latlonObj);
            opt.title(point.getType());
            opt.snippet(point.getInfo());
            markerList.add(map.addMarker(opt));
            createClickPointListener(map,point.getType());
        }
        for (GolfPoint point : courseMarker){
            LatLng latlonObj = new LatLng(point.getLongitude(), point.getLatitude());
            MarkerOptions opt = new MarkerOptions();

            opt.position(latlonObj);
            opt.title(point.getType());
            opt.snippet(point.getInfo());

            map.addMarker(opt);
            createClickPointListener(map,point.getType());
        }
    }


    private void createClickPointListener(GoogleMap map,final String typePoint){
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Location loc1 = new Location("");
                loc1.setLatitude(marker.getPosition().latitude);
                loc1.setLongitude(marker.getPosition().longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(MainActivity.playerLat);
                loc2.setLongitude(MainActivity.playerLon);

                //Log.w("Click", Float.toString(loc1.distanceTo(loc2)));
                marker.setTitle(typePoint+" ("+Integer.toString((int)loc1.distanceTo(loc2))+"m)");

                marker.showInfoWindow();
                return true;
            }

        });
    }
}
