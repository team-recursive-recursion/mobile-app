package recrec.golfcourseviewer.Entity;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import recrec.golfcourseviewer.Requests.Response.Element;

import java.util.LinkedList;
import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Zone;

public class ElementDrawer {

    private List<Zone> zonesCollection;
    private Zone courseZone;

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

    public void drawElements() throws Exception{
        for (Zone zone: zonesCollection){
            for (Element elem: zone.getElements()){
                if (elem.getElementType()==0){
                    String geoJson = elem.getGeoJson();
                    JSONObject geoJsonObject = new JSONObject(geoJson);
                    JSONArray coords = geoJsonObject.getJSONArray("coordinates")
                            .getJSONArray(0);
                    for (int j = 0; j < coords.length(); ++j) {
                        JSONArray pair = coords.getJSONArray(j);
                        double lat = pair.getDouble(1);
                        double lon = pair.getDouble(0);

                    }
                }
            }

        }
    }
}
