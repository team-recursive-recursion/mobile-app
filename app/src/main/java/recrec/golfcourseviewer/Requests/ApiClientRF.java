/*----------------------------------------------------------------------------
 *   Filename : ApiClientRF.java
 *   Author : Team Recursive Recursion
 *   Class : ApiClientRF
 *
 *      The ApiClientRF is an interface of the Android architecture component,
 *      Retrofit. It defines end points to the API that can be called like
 *      standard Java functions.
 *----------------------------------------------------------------------------*/
package recrec.golfcourseviewer.Requests;

import java.util.List;

import recrec.golfcourseviewer.Requests.Response.Zone;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiClientRF {

    @GET("api/Zones")
    Call<List<Zone>> getZones();

    @GET("api/Zones/{ZoneID}")
    Call<Zone> getZones(@Path("ZoneID") String zoneID);

}
