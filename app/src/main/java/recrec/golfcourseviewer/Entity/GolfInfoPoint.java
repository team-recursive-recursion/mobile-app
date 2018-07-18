package recrec.golfcourseviewer.Entity;

import android.content.res.Resources;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

import recrec.golfcourseviewer.Entity.GolfPoint;


public class GolfInfoPoint {


    private String info;
    private double lat;
    private double lon;

    public void resetMultiPoints() {
        this.multiPoints = new LinkedList<>();
    }

    private LinkedList<GolfPoint> multiPoints;
    private LinkedList<Marker> markerList;

    public GolfInfoPoint(){
        multiPoints = new LinkedList<>();
        markerList = new LinkedList<>();
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

    public void addPoint(GolfPoint point) {
        multiPoints.add(point);
    }

    public void drawInfoPoint(Resources res, GoogleMap map) {
        for(Marker marker : markerList){
            marker.remove();
        }
        for (GolfPoint point : multiPoints) {
            LatLng latlonObj = new LatLng(point.getLongitude(), point.getLatitude());
            MarkerOptions opt = new MarkerOptions();

            opt.position(latlonObj);
            opt.title(point.getType());
            opt.snippet(point.getInfo());

            markerList.add(map.addMarker(opt));
        }
    }
}
