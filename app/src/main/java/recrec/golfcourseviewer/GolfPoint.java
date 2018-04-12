package recrec.golfcourseviewer;

public class GolfPoint {

    public double lat;
    public double lon;

    public GolfPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

}
