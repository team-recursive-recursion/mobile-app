/*----------------------------------------------------------------------------
 *   Filename : ElementDrawer.java
 *   Author : Team Recursive Recursion
 *   Class : ElementDrawer
 *
 *       The ElementDrawer is used to store Zone information and draw it onto
 *       the map.
 *----------------------------------------------------------------------------*/
package recrec.golfcourseviewer.Entity;

import android.content.res.Resources;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import recrec.golfcourseviewer.Activity.MainActivity;
import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Element;
import java.util.ArrayList;
import java.util.List;
import recrec.golfcourseviewer.Requests.Response.Zone;

public class ElementDrawer {

    private List<Zone> zonesCollection;
    private List<Polygon> polysOnMap;
    private List<Marker> markerOnMap;

    private Zone courseZone;
    private CourseViewModel viewModel;

    public ElementDrawer(CourseViewModel vm){
        viewModel = vm;
        polysOnMap = new ArrayList<>();
        markerOnMap = new ArrayList<>();
    }
/*-----------------------------------------------------------------------------
    addZoneCollection(List<Zone>) : Void
        This function adds the list of Zones from parameter 1 to the list of
        Zones used to draw the map.
-----------------------------------------------------------------------------*/
    public void addZoneCollection(List<Zone> list){
        zonesCollection = list;
        if(courseZone != null){
            zonesCollection.add(courseZone);
        }
    }
/*-----------------------------------------------------------------------------
    addZone(Zone) : Void
        This function adds a single Zone from parameter 1 to the list of
        Zones used to draw the map.
-----------------------------------------------------------------------------*/
    public void addZone(Zone zoneList){
        courseZone = zoneList;
    }
/*-----------------------------------------------------------------------------
    drawElements(Resources, GoogleMap) : Void
        This function draws the Zones onto the GoogleMap (Parameter 2).
        It uses the Resources Object (Parameter 1) to get access to the
        color that each Zone should be shaded as.
-----------------------------------------------------------------------------*/
    public void drawElements(Resources res, GoogleMap map) throws Exception{
        String holeID = viewModel.holeID.getValue();
        String courseID = viewModel.courseID.getValue();
        for(Polygon p : polysOnMap){
            p.remove();
        }
        for(Marker m : markerOnMap){
            m.remove();
        }
        for (Zone zone: zonesCollection){
            for (Element elem: zone.getElements()){
                if (elem.getElementType()==0){ //Only draw type polygon
                    String geoJson = elem.getGeoJson();
                    JSONObject geoJsonObject = new JSONObject(geoJson);
                    JSONArray coords = geoJsonObject
                            .getJSONArray("coordinates")
                            .getJSONArray(0);
                    PolygonOptions opt = new PolygonOptions();
                    for (int j = 0; j < coords.length(); ++j) {
                        JSONArray pair = coords.getJSONArray(j);
                        double lat = pair.getDouble(1);
                        double lon = pair.getDouble(0);
                        opt.add(new LatLng(lat,lon));
                    }

                    float zIndex;
                    if(elem.getZoneID().equals(holeID) ||
                            elem.getZoneID().equals(courseID)){

                        switch(elem.getClassType()){
                            case 0: //TYPE_ROUGH
                                zIndex = 0.0f;
                                opt.fillColor(res.getColor(R.color.colorRough));
                                opt.strokeColor(res.getColor(R.color.colorRough));
                                break;
                            case 1: //TYPE_FAIRWAY
                                zIndex = 1.0f;
                                opt.fillColor(res.getColor(R.color.colorFairway));
                                opt.strokeColor(res.getColor(R.color.colorFairway));
                                break;
                            case 2: //TYPE_GREEN
                                zIndex = 2.0f;
                                opt.fillColor(res.getColor(R.color.colorGreen));
                                opt.strokeColor(res.getColor(R.color.colorGreen));
                                break;
                            case 3: //TYPE_BUNKER
                                zIndex = 3.0f;
                                opt.fillColor(res.getColor(R.color.colorBunker));
                                opt.strokeColor(res.getColor(R.color.colorBunker));
                                break;
                            case 4: //TYPE_WATER
                                zIndex = 4.0f;
                                opt.fillColor(res.getColor(R.color.colorWater));
                                opt.strokeColor(res.getColor(R.color.colorWater));
                                break;
                            default:
                                zIndex = 0.0f;
                                opt.fillColor(res.getColor(R.color.colorRough));
                                opt.strokeColor(res.getColor(R.color.colorRough));
                        }
                    }else{
                        zIndex = 1.0f;
                        opt.fillColor(res.getColor(R.color.notFocus));
                        opt.strokeColor(res.getColor(R.color.notFocus));
                    }
                    opt.zIndex(zIndex);
                    polysOnMap.add(map.addPolygon(opt));
                }else {
                    if(elem.getZoneID().equals(holeID) ||
                            elem.getZoneID().equals(courseID)) {
                        String geoJson = elem.getGeoJson();
                        JSONObject geoJsonObject = new JSONObject(geoJson);
                        JSONArray coords = geoJsonObject
                                .getJSONArray("coordinates");
                        MarkerOptions opt = new MarkerOptions();
                        LatLng latlonObj = new LatLng(coords.getDouble(1),
                                coords.getDouble(0));
                        opt.position(latlonObj);
                        switch (elem.getClassType()) {
                            case 0:
                                opt.title("Pin");
                                break;
                            case 1:
                                opt.title("Hole");
                                opt.icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.flag));

                                break;
                            case 2:
                                opt.title("Tee");
                                opt.icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.tee));

                                break;
                        }
                        opt.snippet(elem.getInfo());
                        markerOnMap.add(map.addMarker(opt));
                        createClickPointListener(map);
                    }
                }
            }
        }
    }
/*-----------------------------------------------------------------------------
    createClickPointListener(GoogleMap) : Void
        This function creates the onClickPointListener on the GoogleMap Object
        (Parameter 1)
-----------------------------------------------------------------------------*/
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

