package recrec.golfcourseviewer.Requests.Response;

public class Hole {
    public String getHoleId() {
        return holeId;
    }

    public String getName() {
        return name;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getInfo() {
        return info;
    }

    private String info;
    private String holeId;
    private String name;
    private String courseId;
}
