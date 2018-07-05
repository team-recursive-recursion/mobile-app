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

    private String holeId;
    private String name;
    private String courseId;
}
