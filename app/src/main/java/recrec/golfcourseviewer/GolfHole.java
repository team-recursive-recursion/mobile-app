package recrec.golfcourseviewer;

import android.content.res.Resources;
import java.util.LinkedList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

public class GolfHole {

    private LinkedList<GolfPolygon> polygons;

    public GolfHole() {
        polygons = new LinkedList<>();
        // TODO remove hardcoded polygons and fetch from API
        GolfPolygon p = new GolfPolygon(GolfPolygon.PolyType.TYPE_ROUGH);
        p.addPoint(-20.0, -20.0);
        p.addPoint(20.0, -20.0);
        p.addPoint(0.0, 20.0);
        polygons.add(p);
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
    public void drawHole(Resources res, GoogleMap map) {
        for (GolfPolygon poly : polygons) {
            // draw the polygon
            PolygonOptions opt = new PolygonOptions();
            for (GolfPoint point : poly.getPoints()) {
                opt.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            switch (poly.getType()) {
                case TYPE_ROUGH:
                    opt.fillColor(res.getColor(R.color.colorRough));
                    opt.strokeColor(res.getColor(R.color.colorRough));
                    break;
                case TYPE_FAIRWAY:
                    opt.fillColor(res.getColor(R.color.colorFairway));
                    opt.strokeColor(res.getColor(R.color.colorFairway));
                    break;
                case TYPE_GREEN:
                    opt.fillColor(res.getColor(R.color.colorGreen));
                    opt.strokeColor(res.getColor(R.color.colorGreen));
                    break;
                case TYPE_BUNKER:
                    opt.fillColor(res.getColor(R.color.colorBunker));
                    opt.strokeColor(res.getColor(R.color.colorBunker));
                    break;
                case TYPE_WATER:
                    opt.fillColor(res.getColor(R.color.colorWater));
                    opt.strokeColor(res.getColor(R.color.colorWater));
                    break;
                default:
                    opt.fillColor(res.getColor(R.color.colorRough));
                    opt.strokeColor(res.getColor(R.color.colorRough));
            }
            map.addPolygon(opt);
        }
    }

}

