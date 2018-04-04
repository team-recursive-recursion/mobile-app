package recrec.golfcourseviewer;

import java.util.LinkedList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

public class GolfHole {

    private LinkedList<GolfPolygon> polygons;

    public GolfHole() {
        polygons = new LinkedList<>();
        // TODO add and then remove hardcoded polygons
    }

    public void addPolygon(GolfPolygon poly) {
        polygons.add(poly);
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * drawHole(GoogleMap)
     *
     *     Renders the polygons to the provided map. Different colours are used
     *     for different types of polygons.
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void drawHole(GoogleMap map) {
        for (GolfPolygon poly : polygons) {
            // draw the polygon
            PolygonOptions opt = new PolygonOptions();
            for (GolfPoint point : poly.getPoints()) {
                opt.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            map.addPolygon(opt);
        }
    }

}
