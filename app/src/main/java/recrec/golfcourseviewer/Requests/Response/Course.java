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

    private String courseId;
    private String courseName;
    private String userId;
}
