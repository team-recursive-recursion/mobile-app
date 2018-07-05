package recrec.golfcourseviewer.Requests;

import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Course;
import recrec.golfcourseviewer.Requests.Response.Hole;
import recrec.golfcourseviewer.Requests.Response.PolygonElement;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClientRF {

    @GET("api/courses")
    Call<List<Course>> getCourses();

    @GET("api/courses/{courseId}/holes")
    Call<List<Hole>> getHolesByCourseId(@Path("courseId") String courseId);

    @GET("api/courses/{courseId}/polygons")
    Call<List<PolygonElement>> getCourseElementsById(@Path("courseId") String courseId);

    @GET("api/holes/{holeId}/polygons")
    Call<List<PolygonElement>> getHoleElementsById(@Path("holeId") String holeId);
}
