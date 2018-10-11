package recrec.golfcourseviewer.Entity;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONObject;


import recrec.golfcourseviewer.R;
import recrec.golfcourseviewer.Requests.Response.Element;

import java.util.LinkedList;
import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Zone;

public class ElementDrawer {

    private List<Zone> zonesCollection;
    private Zone courseZone;

    public enum PolyType {
        TYPE_ROUGH,
        TYPE_FAIRWAY,
        TYPE_GREEN,
        TYPE_BUNKER,
        TYPE_WATER
    }

    public ElementDrawer(){
    }

    public void addZoneCollection(List<Zone> list){
        zonesCollection = list;
        if(courseZone != null){
            zonesCollection.add(courseZone);
            courseZone = null;
        }
    }

    public void addZone(Zone zoneList){
        courseZone = zoneList;
    }

    public void drawElements(GoogleMap map) throws Exception{
        for (Zone zone: zonesCollection){
            for (Element elem: zone.getElements()){
                if (elem.getElementType()==0){
                    String geoJson = elem.getGeoJson();
                    JSONObject geoJsonObject = new JSONObject(geoJson);
                    JSONArray coords = geoJsonObject.getJSONArray("coordinates")
                            .getJSONArray(0);
                    PolygonOptions opt = new PolygonOptions();
                    for (int j = 0; j < coords.length(); ++j) {
                        JSONArray pair = coords.getJSONArray(j);
                        double lat = pair.getDouble(1);
                        double lon = pair.getDouble(0);

                        opt.add(new LatLng(lat,lon));
                    }

                        float zIndex;
                        switch(elem.getClassType()){
                            case 0: //TYPE_ROUGH
                                zIndex = 0.0f;
                                opt.fillColor(R.color.colorRough);
                                opt.strokeColor(R.color.colorRough);
                                break;
                            case 1: //TYPE_FAIRWAY
                                zIndex = 1.0f;
                                opt.fillColor(R.color.colorFairway);
                                opt.strokeColor(R.color.colorFairway);
                                break;
                            case 2: //TYPE_GREEN
                                zIndex = 2.0f;
                                opt.fillColor(R.color.colorGreen);
                                opt.strokeColor(R.color.colorGreen);
                                break;
                            case 3: //TYPE_BUNKER
                                zIndex = 3.0f;
                                opt.fillColor(R.color.colorBunker);
                                opt.strokeColor(R.color.colorBunker);
                                break;
                            case 4: //TYPE_WATER
                                zIndex = 4.0f;
                                opt.fillColor(R.color.colorWater);
                                opt.strokeColor(R.color.colorWater);
                                break;
                            default:
                                zIndex = 0.0f;
                                opt.fillColor(R.color.colorRough);
                                opt.strokeColor(R.color.colorRough);
                        }
                        opt.zIndex(zIndex);
                        map.addPolygon(opt);
                    }
                }
            }

        }
    }

