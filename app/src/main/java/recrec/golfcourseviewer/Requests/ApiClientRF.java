package recrec.golfcourseviewer.Requests;

import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Course;
import recrec.golfcourseviewer.Requests.Response.Holes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClientRF {

    @GET("api/courses")
    Call<List<Course>> getCourses();

    @GET("api/courses/{courseId}/holes")
    Call<List<Holes>> getHolesByCourseId(@Path("courseId") String courseId);

//    @GET("api/courses/{courseId}/polygon")

}
