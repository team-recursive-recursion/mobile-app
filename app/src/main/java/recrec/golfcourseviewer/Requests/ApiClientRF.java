package recrec.golfcourseviewer.Requests;

import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Course;
import recrec.golfcourseviewer.Requests.Response.Point;
import recrec.golfcourseviewer.Requests.Response.PolygonElement;
import recrec.golfcourseviewer.Requests.Response.Holes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClientRF {

    @GET("api/courses")
    Call<List<Course>> getCourses();

    @GET("api/courses/{courseId}/holes")
    Call<List<Holes>> getHolesByCourseId(@Path("courseId") String courseId);

    @GET("api/courses/{courseId}/polygons")
    Call<List<PolygonElement>> getCourseElementsById(@Path("courseId") String courseId);

    @GET("api/holes/{holeId}/polygons")
    Call<List<PolygonElement>> getHoleElementsById(@Path("holeId") String holeId);

    @GET("api/holes/{holeId}/points")
    Call<List<Point>> getPointElementsById(@Path("holeId") String holeId);
}
