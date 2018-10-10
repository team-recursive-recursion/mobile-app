package recrec.golfcourseviewer.Entity;

public class GolfPoint {

    public double lat;
    public double lon;
    String info;
    String type;

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

    public String getInfo(){return info;}

    public String getType(){return type;}

    public void setInfo(String s){this.info = s;}

    public void setType(String s){this.type = s;}

}
