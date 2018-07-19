package recrec.golfcourseviewer.Entity;

import android.content.res.Resources;
import java.util.LinkedList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import recrec.golfcourseviewer.R;

public class GolfHole {

    private LinkedList<GolfPolygon> coursePolygons;
    private LinkedList<GolfPolygon> holePolygons;
    private LinkedList<Polygon> allPolygons;

    public void resetHolePolygons() {
        this.holePolygons = new LinkedList<>();
    }

    public GolfHole() {
        coursePolygons = new LinkedList<>();
        holePolygons = new LinkedList<>();
        allPolygons = new LinkedList<>();
    }

    public void addCoursePolygon(GolfPolygon poly) {
        coursePolygons.add(poly);
    }
    public void addHolePolygon(GolfPolygon poly) {
        holePolygons.add(poly);
    }
    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * drawHole(GoogleMap)
     *
     *     Renders the coursePolygons to the provided map. Different colours are used
     *     for different types of coursePolygons.
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void drawHole(Resources res, GoogleMap map) {
        // Delete previously drawn Holes
        for(Polygon p : allPolygons){
            p.remove();
        }
        // Draw the course polygons
        for (GolfPolygon poly : coursePolygons) {
            // draw the polygon
            PolygonOptions opt = new PolygonOptions();
            for (GolfPoint point : poly.getPoints()) {
                opt.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            float zIndex;
            switch (poly.getType()) {
                case TYPE_ROUGH:
                    zIndex = 0.0f;
                    opt.fillColor(res.getColor(R.color.colorRough));
                    opt.strokeColor(res.getColor(R.color.colorRough));
                    break;
                case TYPE_FAIRWAY:
                    zIndex = 1.0f;
                    opt.fillColor(res.getColor(R.color.colorFairway));
                    opt.strokeColor(res.getColor(R.color.colorFairway));
                    break;
                case TYPE_GREEN:
                    zIndex = 2.0f;
                    opt.fillColor(res.getColor(R.color.colorGreen));
                    opt.strokeColor(res.getColor(R.color.colorGreen));
                    break;
                case TYPE_BUNKER:
                    zIndex = 3.0f;
                    opt.fillColor(res.getColor(R.color.colorBunker));
                    opt.strokeColor(res.getColor(R.color.colorBunker));
                    break;
                case TYPE_WATER:
                    zIndex = 4.0f;
                    opt.fillColor(res.getColor(R.color.colorWater));
                    opt.strokeColor(res.getColor(R.color.colorWater));
                    break;
                default:
                    zIndex = 0.0f;
                    opt.fillColor(res.getColor(R.color.colorRough));
                    opt.strokeColor(res.getColor(R.color.colorRough));
            }
            opt.zIndex(zIndex);
            map.addPolygon(opt);
        }
        // Draw The hole polygons
        for (GolfPolygon poly : holePolygons) {
            // draw the polygon
            PolygonOptions opt = new PolygonOptions();
            for (GolfPoint point : poly.getPoints()) {
                opt.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            float zIndex;
            switch (poly.getType()) {
                case TYPE_ROUGH:
                    zIndex = 0.0f;
                    opt.fillColor(res.getColor(R.color.colorRough));
                    opt.strokeColor(res.getColor(R.color.colorRough));
                    break;
                case TYPE_FAIRWAY:
                    zIndex = 1.0f;
                    opt.fillColor(res.getColor(R.color.colorFairway));
                    opt.strokeColor(res.getColor(R.color.colorFairway));
                    break;
                case TYPE_GREEN:
                    zIndex = 2.0f;
                    opt.fillColor(res.getColor(R.color.colorGreen));
                    opt.strokeColor(res.getColor(R.color.colorGreen));
                    break;
                case TYPE_BUNKER:
                    zIndex = 3.0f;
                    opt.fillColor(res.getColor(R.color.colorBunker));
                    opt.strokeColor(res.getColor(R.color.colorBunker));
                    break;
                case TYPE_WATER:
                    zIndex = 4.0f;
                    opt.fillColor(res.getColor(R.color.colorWater));
                    opt.strokeColor(res.getColor(R.color.colorWater));
                    break;
                default:
                    zIndex = 0.0f;
                    opt.fillColor(res.getColor(R.color.colorRough));
                    opt.strokeColor(res.getColor(R.color.colorRough));
            }
            opt.zIndex(zIndex);
            allPolygons.add(map.addPolygon(opt));
        }
    }

}

