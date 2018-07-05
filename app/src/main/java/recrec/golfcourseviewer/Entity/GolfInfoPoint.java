package recrec.golfcourseviewer.Entity;

import android.content.res.Resources;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import recrec.golfcourseviewer.Entity.GolfPoint;


public class GolfInfoPoint {


    private String info;
    private double lat;
    private double lon;

    public void setLat(double llat){
        lat = llat;
    }

    public void setLon(double llon){
        lon = llon;
    }

    public void setInfo(String iinfo){
        info = iinfo;
    }

    public void drawInfoPoint(Resources res, GoogleMap map){
        LatLng latlonObj = new LatLng(lon,lat);
        MarkerOptions opt = new MarkerOptions();

        opt.position(latlonObj);
        opt.title("Point");
        opt.snippet(info);

        map.addMarker(opt);
    }

}
