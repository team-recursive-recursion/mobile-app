package recrec.golfcourseviewer.Entity;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import recrec.golfcourseviewer.Requests.Response.Element;

import java.util.LinkedList;
import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Zone;

public class ElementDrawer {
    public CourseViewModel golfCourseListViewModel;

    private List<Zone> zonesCollection;

    ElementDrawer(CourseViewModel model,LifecycleOwner own){
        golfCourseListViewModel = model;
        golfCourseListViewModel.zoneList.observe(own, new Observer<List<Zone>>() {
            @Override
            public void onChanged(@Nullable List<Zone> zones) {
               zonesCollection =  golfCourseListViewModel.zoneList.getValue();
            }
        });

    }

    public void addZones(List<Zone> zoneList){
        zonesCollection.addAll(zoneList);
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
