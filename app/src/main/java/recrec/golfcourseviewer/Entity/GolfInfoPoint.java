package recrec.golfcourseviewer.Entity;

import android.content.res.Resources;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

import recrec.golfcourseviewer.Entity.GolfPoint;


public class GolfInfoPoint {


    private String info;
    private double lat;
    private double lon;
    private LinkedList<GolfPoint> multiPoints;

    public GolfInfoPoint(){
        multiPoints = new LinkedList<>();
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
        for (GolfPoint point : multiPoints) {
            LatLng latlonObj = new LatLng(point.getLongitude(), point.getLatitude());
            MarkerOptions opt = new MarkerOptions();

            opt.position(latlonObj);
            opt.title(point.getType());
            opt.snippet(point.getInfo());

            map.addMarker(opt);
        }
    }
}
