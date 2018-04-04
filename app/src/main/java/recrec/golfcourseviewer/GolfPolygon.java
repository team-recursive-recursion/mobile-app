package recrec.golfcourseviewer;

import java.util.ArrayList;

public class GolfPolygon {

    public enum PolyType {
        TYPE_ROUGH,
        TYPE_GREEN,
        TYPE_FAIRWAY,
        TYPE_BUNKER,
        TYPE_WATER
    }

    private ArrayList<GolfPoint> points;
    private PolyType type;

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * <<constructor>> GolfPolygon(PolyType)
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public GolfPolygon(PolyType type) {
        points = new ArrayList<>();
        this.type = type;
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * addPoint(double, double)
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void addPoint(double lat, double lon) {
        points.add(new GolfPoint(lat, lon));
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * getPoints(): Iterable<GolfPoint>
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public Iterable<GolfPoint> getPoints() {
        return points;
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * getType(): PolyType
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public PolyType getType() {
        return type;
    }

}
