package recrec.golfcourseviewer.Requests;

import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Zone;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClientRF {

    @GET("api/Zones")
    Call<List<Zone>> getZones();

    @GET("api/Zones/{ZoneID}")
    Call<Zone> getZones(@Path("ZoneID") String zoneID);

    @GET("api/courses")
    Call<List<Zone>> getCoursesWithLocation(@Query("latVal") double latVal,
                                              @Query("lonVal") double lonVal);

}
