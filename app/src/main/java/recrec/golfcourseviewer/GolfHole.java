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
    }

}

