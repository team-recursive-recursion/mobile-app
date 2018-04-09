package recrec.golfcourseviewer;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiRequest {

    enum RequestType {
        REQ_GET_COURSES,
        REQ_GET_POLYGONS,
        REQ_CREATE_COURSE,
        REQ_ADD_POLYGON
    }

    enum RequestResult {
        RES_EXCEPTION,
        RES_FAILURE,
        RES_SUCCESS
    }

    private String url;
    private Activity caller;

    public ApiRequest(Activity caller, String url) {
        setUrl(url);
        this.caller = caller;
    }

    public void setUrl(String url) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        if (!url.endsWith(":5001")) {
            url = url + ":5001";
        }
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * getCoursesAsync(ApiCallback)
     *
     *     Returns the results of the GetCourses call to the callback.
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void getCoursesAsync(ApiCallback cb) {
        // create the request
        String target = url + "/GolfCourse/GetCourses";
        Request req = new Request.Builder()
                .url(target)
                .build();
        makeRequest(req, RequestType.REQ_GET_COURSES, cb);
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * getPolygonsAsync(ApiCallback, String)
     *
     *     Returns the results of the GetPolygons for the specified course
     *     ID to the callback.
     *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public void getPolygonsAsync(ApiCallback cb, String courseId) {
        // create the request
        String target = url + "/GolfCourse/GetPolygons?courseId=" + courseId;
        Request req = new Request.Builder()
                .url(target)
                .build();
        makeRequest(req, RequestType.REQ_GET_POLYGONS, cb);
    }

    private void makeRequest(Request req, final RequestType type, final ApiCallback cb) {
        OkHttpClient client = new OkHttpClient();
        // make asynchronous request and send the result to the callback
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                cb.receiveResponse(e.getMessage(), type, RequestResult.RES_EXCEPTION);
            }
            @Override
            public void onResponse(Call call, final Response response) {
                if (!response.isSuccessful()) {
                    cb.receiveResponse(response.toString(), type,
                            RequestResult.RES_FAILURE);
                } else {
                    try {
                        cb.receiveResponse(response.body().string(), type,
                                RequestResult.RES_SUCCESS);
                    } catch (IOException e) {
                        cb.receiveResponse(e.getMessage(), type,
                                RequestResult.RES_EXCEPTION);
                    }
                }
            }
        });
    }

}
