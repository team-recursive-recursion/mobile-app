package recrec.golfcourseviewer.Requests.Response;

public class Point {


    private String geoJson;
    private String holeId;
    private String courseId;
    private String info;

    public String getGeoJson() {
        return geoJson;
    }

    public String getHoleId() {
        return holeId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getInfo() {return info;}
}
