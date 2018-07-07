package recrec.golfcourseviewer.Requests.Response;

public class PolygonElement {
    private int polygonType;
    private String geoJson;
    private String holeId;
    private String courseId;

    public int getPolygonType() {
        return polygonType;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public String getHoleId() {
        return holeId;
    }

    public String getCourseId() {
        return courseId;
    }


}
