/*
Filename: ServiceGenerator.java
 * Author: Team Recursive Recursion
 * Class: ServiceGenerator
 *       This class provides any client access to a Retrofit object from which
 *       API calls can be made in a java style.
 */
package recrec.golfcourseviewer.Requests;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit.Builder builder = null;

    private static Retrofit retrofit = null;

    private static String baseUrl;
    public static void setBaseUrl (String url){
        baseUrl = url;
    }

/*----------------------------------------------------------------------------
 * getService
 *
 *      Provides the user with an initialized retrofit client from which
 *      API calls can be made.
 *      The setBaseUrl function has to be called at least once before this
 *      function may be called.
 ----------------------------------------------------------------------------*/
    public static ApiClientRF getService(){
        if(baseUrl == null){
//            throw new Exception("Base Url not set");
        }
        if( builder == null){
            builder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create());
            retrofit = builder.build();
        }

        return retrofit.create(ApiClientRF.class);
    }
}
