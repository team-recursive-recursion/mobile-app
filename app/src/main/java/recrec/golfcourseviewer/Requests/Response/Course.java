package recrec.golfcourseviewer.Requests.Response;

public class Course {
    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getUserId() {
        return userId;
    }

    public String getInfo() {
        return info;
    }

    private String info;
    private String courseId;
    private String courseName;
    private String userId;
}
