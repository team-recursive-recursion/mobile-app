package recrec.golfcourseviewer;

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

    public ApiRequest(String url) {
        setUrl(url);
    }

    public void setUrl(String url) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        this.url = url;
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
        makeRequest(req, cb);
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
        makeRequest(req, cb);
    }

    private void makeRequest(Request req, final ApiCallback cb) {
        OkHttpClient client = new OkHttpClient();
        // make asynchronous request and send the result to the callback
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cb.receiveResponse(e.getMessage(), RequestType.REQ_GET_COURSES,
                        RequestResult.RES_EXCEPTION);
            }
            @Override
            public void onResponse(Call call, final Response response) {
                if (!response.isSuccessful()) {
                    cb.receiveResponse(response.toString(),
                            RequestType.REQ_GET_COURSES,
                            RequestResult.RES_FAILURE);
                } else {
                    try {
                        cb.receiveResponse(response.body().string(),
                                RequestType.REQ_GET_COURSES,
                                RequestResult.RES_SUCCESS);
                    } catch (IOException e) {
                        cb.receiveResponse(e.getMessage(),
                                RequestType.REQ_GET_COURSES,
                                RequestResult.RES_EXCEPTION);
                    }
                }
            }
        });
    }

}
